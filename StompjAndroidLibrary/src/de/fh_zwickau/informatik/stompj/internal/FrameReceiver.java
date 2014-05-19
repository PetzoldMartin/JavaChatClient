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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

import de.fh_zwickau.informatik.stompj.ErrorMessage;
import de.fh_zwickau.informatik.stompj.MessageHandler;
import de.fh_zwickau.informatik.stompj.StompJRuntimeException;

// Referenced classes of package de.fh_zwickau.informatik.stompj.internal:
//            MessageImpl, StompJSession, ErrorImpl

/**
 * this class handles the incoming stomp frames and decodes them into
 * StompMessage objects. it runs as separate thread.
 * 
 * @author georg beier based on work of others
 * 
 */
public class FrameReceiver extends Thread {
	private volatile boolean mRun = true;

	/**
	 * initialize object
	 * 
	 * @param session
	 *            session that manages the connection for which the
	 *            FrameReceiver is active
	 * @param input
	 *            input stream from socket
	 * @param handlers
	 *            map of handler objects that are invoked for incoming messages
	 *            on specific stomp destinations
	 */
	public FrameReceiver(StompJSession session, InputStream input,
		ConcurrentHashMap<String, CopyOnWriteArraySet<MessageHandler>> handlers) {
		this.session = session;
		this.input = input;
		messageHandlers = handlers;
		mRun = true;
	}

	/**
	 * first response to the connected message is handled differently, because
	 * it is a protocol internal message that is not handed to any handler
	 * 
	 * @return null on success, else error message
	 * @throws IOException
	 */
	public ErrorMessage processFirstResponse() throws IOException {
		String command = getLine();
		if (command.equals("CONNECTED")) {
			processCONNECTEDFrame();
			return null;
		}
		if (command.equals("ERROR")) {
			ErrorMessage em = processERRORFrame();
			return em;
		} else {
			return null;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Thread#run()
	 */
	@Override
	public void run() {
		mRun = true;
		do
			processInComingFrame();
		while (mRun);
	}

	/**
	 * classify frame and pass it to handling method
	 */
	private void processInComingFrame() {
		String command = getLine();
		if (command.equals("MESSAGE"))
			processMESSAGEFrame();
		else if (command.equals("ERROR"))
			processERRORFrame();
		else if (command.equals("RECEIPT"))
			getFrameBody(-1);
	}

	/**
	 * read bytes from input stream and organize them in lines. as stomp
	 * protocol is line oriented, each line should contain one message element.
	 * First line identifies kind of message, successive lines hold message
	 * properties. The message content has multiple lines and is read by method
	 * 
	 * @see #getFrameBody(int).
	 * 
	 * @return one line of an incoming message
	 */
	private String getLine() {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		String line = "";
		try {
			do {
				byte b = (byte) input.read();
				if (b == 10) {
					break;
				} else if (b == -1) {
					mRun = false;
					System.out.println("end of input stream");
					break;
				}
				bos.write(b);
			} while (true);
			line = new String(bos.toByteArray(), "UTF-8");
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		} catch (IOException e) {
			throw new StompJRuntimeException(e.getMessage(), e);
		}
		return line;
	}

	/**
	 * Process a normal stomp MESSAGE:<br>
	 * read all parts of the message and organize message content into a
	 * StompMessage object. Then send ACK, if necessary. If message is not only
	 * to resolve a temporary destination name, pass message object to
	 * MessageHandler objects registered with the message's destination.
	 */
	private void processMESSAGEFrame() {
		boolean isTempRes = false;
		HashMap<String, String> properties = getProperties();
		final MessageImpl msg = new MessageImpl();
		String cl = (String) properties.get("content-length");
		int contentLength = -1;
		if (cl != null) {
			contentLength = Integer.parseInt(cl);
//			properties.remove("content-length");
		}
		String msgId = (String) properties.remove("message-id");
		msg.setMessageId(msgId);
		String destination = (String) properties.remove("destination");
		msg.setDestination(destination);
		msg.setProperties(properties);
		msg.setContent(getFrameBody(contentLength));
		if (properties.get(StompJSession.RESOLVE_TEMP_NAME) != null) {
			session.receivedTempQueueName(msg);
			isTempRes = true;
		}
		session.sendAckIfNeeded(msg);
		if (isTempRes)
			return; // don't process tempq name resolve message
		for (Iterator<MessageHandler> iter = messageHandlers.get(
			msg.getDestination()).iterator(); iter.hasNext();) {
			mh = iter.next();
			new Thread() {
				private final MessageHandler msgHdlr = mh;
				private final MessageImpl message = msg;

				public void run() {
					msgHdlr.onMessage(message);
				}

			}.start();
		}

	}

	/**
	 * Process a stomp ERROR message
	 * 
	 * @return ErrorMessage object
	 */
	private ErrorMessage processERRORFrame() {
		HashMap<String, String> properties = getProperties();
		ErrorImpl error = new ErrorImpl();
		String cl = (String) properties.get("content-length");
		int contentLength = -1;
		if (cl != null)
			contentLength = Integer.parseInt(cl);
		error.setMessage((String) properties.get("message"));
		error.setContent(getFrameBody(contentLength));
		if (session.getConnection().getErrorHandler() != null) {
			session.getConnection().getErrorHandler().onError(error);
		}
		return error;
	}

	/**
	 * Process a stomp CONNECTED message which is the response to sending a
	 * CONNECT message to the message broker.
	 * 
	 * @throws IOException
	 */
	private void processCONNECTEDFrame() throws IOException {
		HashMap<String, String> prop = getProperties();
		sessionId = (String) prop.get("session");
		getFrameBody(-1);
	}

	/**
	 * read properties line by line and return them as Map
	 * 
	 * @return map of message properties
	 */
	private HashMap<String, String> getProperties() {
		HashMap<String, String> properties = new HashMap<String, String>();
		do {
			String line = getLine();
			if (line.length() != 0) {
				String p[] = line.split(":", 2);
				if (p.length == 1)
					properties.put(p[0], "");
				if (p.length == 0)
					properties.put("", "");
				properties.put(p[0], p[1]);
			} else {
				return properties;
			}
		} while (true);
	}

	/**
	 * read the message content until the terminating ASCII NUL character. when
	 * the bodyLength is given, embedded NUL characters are read, else the first
	 * NUL will terminate reading. So make sure non-string messages have a
	 * correct body-length set!
	 * 
	 * @param bodyLength
	 *            length of content or -1
	 * @return message content
	 */
	private byte[] getFrameBody(int bodyLength) {
		ByteArrayOutputStream bos;
		label0: {
			bos = new ByteArrayOutputStream();
			try {
				do {
					if (bodyLength == 0) {
						getFrameBody(-1);
						break label0;
					}
					byte b = (byte) input.read();
					if (b == 0 && bodyLength == -1)
						break label0;
					bos.write(b);
				} while (bodyLength != bos.size());
				getFrameBody(-1);
			} catch (IOException e) {
				throw new StompJRuntimeException(e.getMessage(), e);
			}
		}
		return bos.toByteArray();
	}

	private StompJSession session;
	private InputStream input;
	@SuppressWarnings("unused")
	private String sessionId;
	private ConcurrentHashMap<String, CopyOnWriteArraySet<MessageHandler>> messageHandlers;
	private MessageHandler mh;
}
