/**
 *    Copyright 2011 prashant, Michał Janiszewski, Georg Beier and others

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.


 * This source code is based on work of prashant ??? at google code, published under 
 * an Apache License 2.0. The original package path was pk.aamir.stompj.* . Code was
 * decompiled from class files using Jad v1.5.8g by Michał Janiszewski and published on
 * http://www.gitorious.org/pai-market/stompj.
 *  To keep the work available, the base package path was moved to an existing url.
 * 
 * Adapted to work with Apache ActiveMQ temporary queues, added comments and use 
 * template classes
 * 
 */

package de.fh_zwickau.informatik.stompj;

import java.io.UnsupportedEncodingException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

import de.fh_zwickau.informatik.stompj.internal.MessageImpl;
import de.fh_zwickau.informatik.stompj.internal.StompJSession;

// Referenced classes of package de.fh_zwickau.informatik.stompj:
//            StompJException, MessageHandler, ErrorHandler, ErrorMessage, 
//            StompMessage

/**
 * main class to maintain a connection to the messaging systen
 * 
 * @author georg beier based on work of others
 * 
 */
public class Connection {

	/**
	 * fatal error string for messages
	 */
	public static String FATAL_CONNECTION_ERROR = "StompJFatalConnectionError";

	/**
	 * prepare default connection to localhost
	 */
	public Connection() {
		this("localhost", 61613);
	}

	/**
	 * prepare to connect to arbitrary host/port, ignoring authorisation
	 * 
	 * @param host
	 * @param port
	 */
	public Connection(String host, int port) {
		this(host, port, "", "");
	}

	/**
	 * prepare to connect to arbitrary host/port, authorize with login and
	 * passcode
	 * 
	 * @param host
	 * @param port
	 * @param userid
	 * @param password
	 */
	public Connection(String host, int port, String userid, String password) {
		messageHandlers = new ConcurrentHashMap<String, CopyOnWriteArraySet<MessageHandler>>();
		session = new StompJSession(host, port, userid, password, this,
				messageHandlers);
	}

	/**
	 * actually open the connection
	 * 
	 * @return null if connected successfully, otherwise returns the error
	 *         message received from the server
	 * @throws StompJException
	 */
	public ErrorMessage connect() throws StompJException {
		return session.connect();
	}

	/**
	 * close connection
	 */
	public void disconnect() {
		session.disconnect();
	}

	/**
	 * query connection state
	 * 
	 * @return trfue if connected
	 */
	public boolean isConnected() {
		return session.isConnected();
	}

	/**
	 * subscribe to receiving messages from a stomp destination. @see <a
	 * href="http://stomp.codehaus.org/Protocol">
	 * 
	 * @param destination
	 *            when used as connector to ActiveMQ, legal values are /queue/*,
	 *            /topic/*, /temp-queue/*, /temp-topic/*
	 * @param autoAck
	 *            if false, client acknowledges messages
	 */
	public void subscribe(String destination, boolean autoAck) {
		session.subscribe(destination, autoAck);
	}

	/**
	 * stop subscription
	 * 
	 * @param destination
	 *            identifies an existing subscription
	 */
	public void unsubscribe(String destination) {
		session.unsubscribe(destination);
	}

	/**
	 * send a message to stomp destination @see <a
	 * href="http://stomp.codehaus.org/Protocol">
	 * 
	 * @param msg
	 *            message to send
	 * @param destination
	 *            (optional) legal stomp destination
	 */
	public void send(StompMessage msg, String... destination) {
		String dest;
		if (destination.length == 1)
			dest = destination[0];
		else
			dest = msg.getDestination();
		session.send(msg, dest);
	}

	/**
	 * send a string message to stomp destination @see <a
	 * href="http://stomp.codehaus.org/Protocol">
	 * 
	 * @param msg
	 * @param destination
	 */
	public void send(String msg, String destination) {
		MessageImpl m = new MessageImpl();
		try {
			m.setContent(msg.getBytes("UTF-8"));
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}
		send(((StompMessage) (m)), destination);
	}

	/**
	 * add handler object that processes incoming messages to a destination
	 * 
	 * @param destination
	 *            where the message is delivered
	 * @param handler
	 *            handler object
	 */
	public void addMessageHandler(String destination, MessageHandler handler) {
		destination = session.resolveTempName(destination);
		CopyOnWriteArraySet<MessageHandler> set = new CopyOnWriteArraySet<MessageHandler>();
		messageHandlers.putIfAbsent(destination, set);
		messageHandlers.get(destination).add(handler);
	}

	/**
	 * get all handlesrs for a destination
	 * 
	 * @param destination
	 *            where the message is delivered
	 * @return array of handlers
	 */
	public MessageHandler[] getMessageHandlers(String destination) {
		return (MessageHandler[]) messageHandlers.get(destination).toArray(
				new MessageHandler[0]);
	}

	public void removeMessageHandlers(String destination) {
		messageHandlers.remove("destination");
	}

	/**
	 * access error handler object
	 * 
	 * @return the error handler
	 */
	public ErrorHandler getErrorHandler() {
		return errorHandler;
	}

	/**
	 * set object that handles all communication errors for all destinations
	 * 
	 * @param handler
	 *            the handler object
	 */
	public void setErrorHandler(ErrorHandler handler) {
		errorHandler = handler;
	}

	/**
	 * test, if an error occurred and reset error flag
	 * 
	 * @return error flag value
	 */
	public synchronized boolean testAndClearErrorFlag() {
		boolean result = errorFlag;
		errorFlag = false;
		return result;
	}

	/**
	 * mark an error by setting the error flag
	 */
	public synchronized void setErrorFlag() {
		errorFlag = true;
	}

	/**
	 * is error polling enabled?
	 * 
	 * @return true
	 */
	public boolean isPollingFlagSet() {
		return pollingFlag;
	}

	/**
	 * mark if error state is detected by polling (default = true)
	 * 
	 * @param pollingFlag
	 */
	public void setPollingFlag(boolean pollingFlag) {
		this.pollingFlag = pollingFlag;
	}

	private boolean pollingFlag = true;
	private boolean errorFlag = false;
	private StompJSession session;
	private ConcurrentHashMap<String, CopyOnWriteArraySet<MessageHandler>> messageHandlers;
	private ErrorHandler errorHandler;
}
