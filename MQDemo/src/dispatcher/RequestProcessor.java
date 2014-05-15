/**
 * 
 */
package dispatcher;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;

import org.grlea.log.SimpleLogger;

/**
 * Basisklasse zur Verarbeitung von Request Messages. Definiert den
 * fundamentalen Workflow für die Bearbeitung von Request Objekten.
 * 
 * @author georg beier
 * 
 */
public abstract class RequestProcessor implements Callable<Boolean> {

	public enum State {
		Requested, Delegating, WaitingForResults, Finalizing, Replying, Done
	}

	/**
	 * in diese Destination werden die ServiceReply messages gesendet
	 */
	protected static Destination serviceReplyQ;

	public static void setServiceReplyQ(Destination serviceReplyDestination) {
		serviceReplyQ = serviceReplyDestination;
	}

	private static final int THREADCOUNT = 40;

	/**
	 * verwalte requests
	 */
	private static ConcurrentMap<String, RequestProcessor> requests =
		new ConcurrentHashMap<String, RequestProcessor>();

	/**
	 * stellt pool von threads mit spezifizierter größe zur verfügung, über die
	 * asynchron die call() methode aufgerufen werden kann
	 */
	private static ExecutorService threadPool =
		Executors.newFixedThreadPool(THREADCOUNT);

	/**
	 * queue von messages, die von services an diesen request zurück gesendet
	 * wurden
	 */
	private Deque<Message> messages = new ArrayDeque<Message>();

	/**
	 * bearbeitungszustand des objekts
	 */
	private State state;

	/**
	 * eindeutiger identifier für diesen message prozessor
	 */
	protected String key;

	/**
	 * verhindert in der methode call(), dass zwei instanzen des objekts
	 * gleichzeitig ausgeführt werden
	 */
	private Lock executionLock = new ReentrantLock();

	/**
	 * einfaches logging vorbereiten, um den Ablauf verfolgen zu können
	 */
	protected SimpleLogger log;

	protected Message requestMessage;

	/**
	 * ruft asynchron unter verwendung des threadPool die verarbeitungsmethode
	 * call() des übergebenen RequestProcessor objekts auf. wird direkt nach
	 * empfang einer request message aufgerufen.
	 * 
	 * @param processor
	 *            aufzurufende instanz, muss die aktuell zu bearbeitende message
	 *            schon zugewiesen haben
	 */
	public static void process(RequestProcessor processor) {
		threadPool.submit(processor);
	}

	/**
	 * bereitet den asynchronen aufruf des request prozessors vor, zu dem die
	 * serviceReply message gehört
	 * 
	 * @param serviceReply
	 *            neu eingetroffene reply message von einem service
	 */
	public static void process(Message serviceReply) {
		try {
			String key;
			key = serviceReply.getStringProperty("refId");
			RequestProcessor processor = requests.get(key);
			processor.addMessage(serviceReply);
			process(processor);
		} catch (JMSException e) {
			throw new RuntimeException(e);
		}
	}

	protected RequestProcessor(Message message) throws JMSException {
		requestMessage = message;
		key = message.getJMSCorrelationID();
		log = new SimpleLogger(getClass(), key, false);
		log.entry("constructed");
		requests.put(key, this);
		onRequested();

	}

	/**
	 * action methode, die ausgeführt wird, wenn ein neuer Request angelegt
	 * wurde
	 */
	protected void onRequested() {
		setState(State.Requested);
		log.entry("requested");
	}

	/**
	 * action methode, die ausgeführt wird, wenn der Request seine aufgaben an
	 * die services delegiert
	 */
	protected abstract void onDelegating();

	/**
	 * action methode, die ausgeführt wird, wenn eine neue antwort-message eines
	 * service eintrifft. <br>
	 * bearbeite alle messages, die in der message queue vorliegen.<br>
	 * der request ist dafür verantwortlich, zu erkennen,
	 * wenn alle services ihre antworten geschickt haben. dann kann er in den
	 * nächsten zustand übergehen. andernfalls muss das executionLock wieder
	 * freigegeben werden.
	 */
	protected boolean onWaitingForResults() {
		boolean done = false;
		Message message;
		log.entry("waiting for results");
		// überprüfe, ob schon etwas in der result queue ist, gib lock sofort
		// frei, wenn noch nicht. damit wird sichergestellt, dass messages nicht
		// hängen bleiben, wenn das delegating lange dauert
		synchronized (this) {
			if (messages.isEmpty()) {
				executionLock.unlock();
				log.exit("waiting for reply without processing");
				return done;
			}
		}
		while ((message = removeMessage()) != null) {
			done = processResultMessage(message);
			// beende task, wenn momentan keine weiteren messages vorliegen,
			// aber die bearbeitung noch nicht abgeschlossen ist.
			// verhindere, dass sich noch eine message "dazwischenmogelt"
			synchronized (this) {
				if (!done && messages.isEmpty()) {
					executionLock.unlock();
					break;
				}
			}
		}
		log.exit("waiting for reply after processing");
		return done;
	}

	/**
	 * bearbeite eine eingetroffene result message von einem service. überprüfe,
	 * ob alle angeforderten ergebnisse eingetroffen sind.
	 * 
	 * @param message
	 *            neu eingetroffene message
	 * @return true, wenn alle ergebnisse eingetroffen sind
	 */
	protected abstract boolean processResultMessage(Message message);

	/**
	 * action methode, die ausgeführt wird, wenn alle angeforderten ergebnisse
	 * eingetroffen sind und die ergebnisse zusammengeführt werden.
	 */
	protected void onFinalizing() {

	}

	/**
	 * action methode, die ausgeführt wird, wenn der request seine ergebnisse
	 * zurücksendet.
	 */
	protected abstract void onReplying();

	/**
	 * action methode, die ausgeführt wird, wenn der request fertig bearbeitet
	 * ist.
	 */
	protected void onDone() {
		messages.clear();
		messages = null;
		requests.remove(key);
	}

	/**
	 * methode, die asynchron über einen Executor aufgerufen wird und den
	 * lifecycle anstößt. Kann sinnvollerweise nur aufgerufen werden, wenn
	 * objekt im Zustand Requested oder WaitingForResults ist, alle anderen
	 * statusübergänge sind interne transitionen. <br>
	 * diese methode darf nicht gleichzeitig mehrfach ausgeführt werden.
	 */
	@Override
	public Boolean call() throws Exception {
		Boolean canDo = true;
		log.entry("call");
		if (executionLock.tryLock()) {
			switch (state) {
			case Requested:
				setState(State.Delegating);
				onDelegating();
				state = State.WaitingForResults;
				// executionLock.unlock();
				// break;
				// schau sofort nach, ob results da sind
			case WaitingForResults:
				if (onWaitingForResults()) {
					onFinalizing();
					onReplying();
					onDone();
					executionLock.unlock();
				}
				break;
			default:
				canDo = false;
			}
		} else {
			log.debug("not executing");
		}
		log.debugObject("executionLock is ", executionLock.toString());
		return canDo;
	}

	public synchronized void addMessage(Message message) {
		messages.add(message);
	}

	protected synchronized Message removeMessage() {
		return messages.poll();
	}

	public synchronized State getState() {
		return state;
	}

	protected synchronized void setState(State state) {
		this.state = state;
	}

	public synchronized void setRequestMessage(Message request) {
		requestMessage = request;
	}

}
