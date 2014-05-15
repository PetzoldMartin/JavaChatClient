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

package de.fh_zwickau.informatik.stompj.internal;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

import de.fh_zwickau.informatik.stompj.Connection;
import de.fh_zwickau.informatik.stompj.ErrorMessage;
import de.fh_zwickau.informatik.stompj.StompMessage;
import de.fh_zwickau.informatik.stompj.MessageHandler;
import de.fh_zwickau.informatik.stompj.StompJException;
import de.fh_zwickau.informatik.stompj.StompJRuntimeException;

/**
 * 
 * main implementation class that handles the connection to message brokers
 * using the stomp protocol.
 * specially adapted and tested for use on android systems as interface to
 * apache ActiveMQ.
 * 
 * @see <a href="http://stomp.codehaus.org/Protocol"><br>
 * @see <a href="http://activemq.apache.org/">
 * 
 * @author others
 * @author georg beier
 * 
 */
public class StompJSession {

	public static final String TEMP_Q = "/temp-queue/";
	public static final String JMS_TEMP_Q = "/remote-temp-queue/";
	public static final String RESOLVE_TEMP_NAME = "resolve-temp-name";

	private String host;
	private int port;
	private String userid;
	private String password;
	private Socket socket;
	private BufferedInputStream input;
	private BufferedOutputStream output;
	private Connection connection;
	private HashMap<String, Boolean> autoAckMap;
	private HashMap<String, String> tempDestinationMap;
	private FrameReceiver frameReceiver;
	private ConcurrentHashMap<String, CopyOnWriteArraySet<MessageHandler>> messageHandlers;

	/**
	 * 
	 */
	public StompJSession(String host, int port, String userid, String password,
		Connection con,
		ConcurrentHashMap<String, CopyOnWriteArraySet<MessageHandler>> handlers) {
		this.host = host;
		this.port = port;
		this.userid = userid;
		this.password = password;
		connection = con;
		messageHandlers = handlers;
		autoAckMap = new HashMap<String, Boolean>();
		tempDestinationMap = new HashMap<String, String>();
	}

	/**
	 * try a few seconds to connect to a message broker using host, port, login
	 * and passcode. if connection fails, throw exception.
	 * 
	 * @throws StompJException
	 *             id host is unknown or connection does not succed within 2
	 *             seconds.
	 */
	public ErrorMessage connect() throws StompJException {
		try {
			socket = new Socket();
			InetSocketAddress address = new InetSocketAddress(host, port);
			int tout = 2000;
			socket.connect(address, tout);
			// socket = new Socket(host, port);
			input = new BufferedInputStream(socket.getInputStream());
			output = new BufferedOutputStream(socket.getOutputStream());
			output.write(createCONNECTFrame(userid, password));
			output.flush();
			frameReceiver = new FrameReceiver(this, input, messageHandlers);
			ErrorMessage errorMsg = frameReceiver.processFirstResponse();
			if (errorMsg == null) {
				frameReceiver.start();
			} else {
				disconnect();
				return errorMsg;
			}
		} catch (UnknownHostException e) {
			disconnect();
			connection.setErrorFlag();
			if (!sendExceptionAsErrorMessage(e) && !connection.isPollingFlagSet())
				throw new StompJException(e.getMessage(), e);
		} catch (IOException e) {
			disconnect();
			connection.setErrorFlag();
			if (!sendExceptionAsErrorMessage(e) && !connection.isPollingFlagSet())
				throw new StompJException(e.getMessage(), e);
		}
		return null;
	}

	/**
	 * send a disconnect message and close socket and io streams.
	 */
	public void disconnect() {
		sendFrame(createDISCONNECTFrame());
		// interrupt pending read operation on input
		frameReceiver.interrupt();
		// wait for io operation to finish before closing streams
		synchronized (frameReceiver) {
			try {
				frameReceiver.wait(2000);
			} catch (InterruptedException e) {
			}
		}
		closeAll();
	}

	/**
	 * 
	 * close everything, ignore exceptions
	 * 
	 * @throws IOException
	 */
	public void closeAll() {
		try {
			input.close();
			output.close();
			socket.close();
		} catch (IOException e) {
			// ignore exceptions
		}
	}

	/**
	 * find out connection state
	 * 
	 * @return true if connected
	 */
	public boolean isConnected() {
		if (socket == null)
			return false;
		else
			return !socket.isClosed() && socket.isConnected();
	}

	/**
	 * subscribe to receiving messages from a stomp destination. @see <a
	 * href="http://stomp.codehaus.org/Protocol">
	 * special care has to be taken with temporary destinations, because the
	 * message broker "invents" a new name for them. So we will have to find out
	 * their real name first, before we can subscribe.<br>
	 * Here ist the implementation!
	 * 
	 * @param destination
	 *            when used as connector to ActiveMQ, legal values are /queue/*,
	 *            /topic/*, /temp-queue/*, /temp-topic/*
	 * @param autoAck
	 *            if false, client acknowledges messages
	 */
	public void subscribe(String destination, boolean autoAck) {
		sendFrame(createSUBSCRIBEFrame(destination, autoAck));
		autoAckMap.put(destination, Boolean.valueOf(autoAck));
		if (isTempQueue(destination)) {
			queryTempQueueName(destination);
		}
	}

	/**
	 * stop subscription<br>
	 * Here ist the implementation
	 * 
	 * @param destination
	 *            identifies an existing subscription
	 */
	public void unsubscribe(String destination) {
		sendFrame(createUNSUBSCRIBEFrame(destination));
	}

	/**
	 * find the real name of a temporary destination, if already resolved
	 * 
	 * @param destination
	 *            stomp temporary destination
	 * @return the name used by the message broker, if known. else echo the
	 *         input.
	 */
	public String resolveTempName(String destination) {
		if (tempDestinationMap.containsKey(destination)) {
			return tempDestinationMap.get(destination);
		} else {
			return destination;
		}
	}

	/**
	 * send message to given destination. the destination field in ther message
	 * is not considered, so destination has to be given explicitely!
	 * 
	 * @param msg
	 *            message to be sent
	 * @param destination
	 *            valid stomp destination
	 */
	public void send(StompMessage msg, String destination) {
		sendFrame(createSENDFrame(msg, destination));
	}

	/**
	 * send exception as Error Message, if an ErrorHandler is set
	 * 
	 * @param exception
	 *            Exception to be sent
	 * @return true, if ErrorHandler is set
	 */
	boolean sendExceptionAsErrorMessage(Exception exception) {
		ErrorImpl error = new ErrorImpl();
		error.setMessage(Connection.FATAL_CONNECTION_ERROR);
		if (exception != null) {
			error.setContent(exception.toString().getBytes());
		} else {
			error.setContent(Connection.FATAL_CONNECTION_ERROR.getBytes());
		}
		if (getConnection().getErrorHandler() != null) {
			getConnection().getErrorHandler().onError(error);
			return true;
		} else {
			return false;
		}

	}

	/**
	 * check if destination of received message requires explicit acknowledge,
	 * or if autoAcknowledge was set for it. send ack frame if needed.
	 * 
	 * @param msg
	 *            incoming message
	 */
	void sendAckIfNeeded(StompMessage msg) {
		String dest = msg.getDestination();
		Boolean val = autoAckMap.get(dest);
		if (val != null && !val)
			sendFrame(createACKFrame(msg.getMessageId()));
	}

	void receivedTempQueueName(StompMessage reply) {
		String jmsDestination = reply.getDestination();
		String stompDestination = reply.getProperty(RESOLVE_TEMP_NAME);
		tempDestinationMap.put(stompDestination, jmsDestination);
		Boolean ack = autoAckMap.remove(stompDestination);
		if (ack != null) {
			autoAckMap.put(jmsDestination, ack);
		} else {
			autoAckMap.put(jmsDestination, true);
		}
		CopyOnWriteArraySet<MessageHandler> handlers = messageHandlers
			.remove(stompDestination);
		if (handlers != null) {
			messageHandlers.put(jmsDestination, handlers);
		}
	}

	/**
	 * determine if stomp destination is a temporary queue, i.e. destination
	 * starts with /temp-queue/
	 * 
	 * @param destination
	 *            stomp destination name
	 * @return true if temp
	 */
	private boolean isTempQueue(String destination) {
		return destination.startsWith(TEMP_Q);
	}

	/**
	 * send a message to the temporary queue to find out which name is given
	 * by the JMS broker. As this message is returned to the sender, its
	 * destination
	 * attribute contains the JMS name of the temp queue.
	 * 
	 * @param destination
	 *            temporary destination name as seen from stomp
	 */
	private void queryTempQueueName(String destination) {
		MessageImpl selfMessage = new MessageImpl();
		HashMap<String, String> props = new HashMap<String, String>();
		props.put(RESOLVE_TEMP_NAME, destination);
		selfMessage.setProperties(props);
		selfMessage.setContent(destination.getBytes());
		send(selfMessage, destination);
	}

	/**
	 * actually write out the message frame to the socket
	 * 
	 * @param frame
	 *            message codes as byte[]
	 * @throws StompJRuntimeException
	 *             wrapper for various io exceptions
	 */
	private synchronized void sendFrame(byte frame[])
		throws StompJRuntimeException {
		if (!isConnected())
			throw new StompJRuntimeException("Not connected to the server");
		try {
			output.write(frame);
			output.flush();
		} catch (IOException e) {
			throw new StompJRuntimeException(e.getMessage(), e);
		}
	}

	/**
	 * creates a CONNECT message frame according to the stomp protocol
	 * specification
	 * 
	 * @param userid
	 * @param password
	 * @return nicely coded as byte[]
	 * @throws IOException
	 */
	private byte[] createCONNECTFrame(String userid, String password)
		throws IOException {
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		os.write(prepareBytes("CONNECT"));
		os.write(10);
		os.write(prepareBytes((new StringBuilder("login: ")).append(userid)
			.toString()));
		os.write(10);
		os.write(prepareBytes((new StringBuilder("passcode:")).append(password)
			.toString()));
		os.write(10);
		os.write(10);
		os.write(0);
		return os.toByteArray();
	}

	/**
	 * 
	 * creates a SEND message frame according to the stomp protocol
	 * specification
	 * 
	 * @param msg
	 *            what to send
	 * @param destination
	 *            where to send
	 * @return nicely coded as byte[]
	 */
	private byte[] createSENDFrame(StompMessage msg, String destination) {
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		try {
			os.write(prepareBytes("SEND"));
			os.write(10);
			os.write(prepareProperty("destination", destination));
			os.write(10);
			String propNames[] = msg.getPropertyNames();
			String as[];
			int j = (as = propNames).length;
			for (int i = 0; i < j; i++) {
				String p = as[i];
				os.write(prepareProperty(p, msg.getProperty(p)));
				os.write(10);
			}

			os.write(10);
			os.write(msg.getContentAsBytes());
			os.write(0);

			// System.out.println(os.toString("UTF-8"));
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		return os.toByteArray();
	}

	/**
	 * creates a SUBSCRIBE message frame according to the stomp protocol
	 * specification
	 * 
	 * @param destination
	 *            where to send
	 * @param autoAck
	 *            shall broker autoack messages or wait for ack from client
	 * @return nicely coded as byte[]
	 */
	private byte[] createSUBSCRIBEFrame(String destination, boolean autoAck) {
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		try {
			os.write(prepareBytes("SUBSCRIBE"));
			os.write(10);
			os.write(prepareProperty("destination", destination));
			os.write(10);
			os.write(prepareProperty("ack", autoAck ? "auto" : "client"));
			os.write(10);
			os.write(10);
			os.write(0);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		return os.toByteArray();
	}

	/**
	 * creates a UNSUBSCRIBE message frame according to the stomp protocol
	 * specification
	 * 
	 * @param destination
	 *            where to send
	 * @return nicely coded as byte[]
	 */
	private byte[] createUNSUBSCRIBEFrame(String destination) {
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		try {
			os.write(prepareBytes("UNSUBSCRIBE"));
			os.write(10);
			os.write(prepareProperty("destination", destination));
			os.write(10);
			os.write(10);
			os.write(0);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		return os.toByteArray();
	}

	/**
	 * creates an ACK (acknowledge) message frame according to the stomp
	 * protocol specification
	 * 
	 * @param msgId
	 *            reference to the message being acknowledged
	 * @return nicely coded as byte[]
	 */
	private byte[] createACKFrame(String msgId) {
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		try {
			os.write(prepareBytes("ACK"));
			os.write(10);
			os.write(prepareProperty("message-id", msgId));
			os.write(10);
			os.write(10);
			os.write(0);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		return os.toByteArray();
	}

	/**
	 * creates a DISCONNECT message frame according to the stomp protocol
	 * specification
	 * 
	 * @return nicely coded as byte[]
	 */
	private byte[] createDISCONNECTFrame() {
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		try {
			os.write(prepareBytes("DISCONNECT"));
			os.write(10);
			os.write(10);
			os.write(0);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		return os.toByteArray();
	}

	/**
	 * code string to byte[] using UTF-8 coding and mask Exception to
	 * RuntimeException
	 * 
	 * @param s
	 * @return
	 */
	private byte[] prepareBytes(String s) {
		try {
			return s.getBytes("UTF-8");
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * convert a property, as from the properties map, to the stomp notation
	 * 
	 * @param propName
	 * @param prop
	 *            property value as String
	 * @return "propName: prop"
	 */
	private byte[] prepareProperty(String propName, String prop) {
		return prepareBytes((new StringBuilder(String.valueOf(propName)))
			.append(":").append(prop).toString());
	}

	/**
	 * accessor method. really needed?
	 * 
	 * @return
	 */
	Connection getConnection() {
		return connection;
	}
}
