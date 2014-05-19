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

import java.io.UnsupportedEncodingException;
import java.util.HashMap;

import de.fh_zwickau.informatik.stompj.StompMessage;

public class MessageImpl implements StompMessage {

	private static final long serialVersionUID = 9124127430655793092L;

	public MessageImpl() {
		properties = new HashMap<String, String>();
	}

	/**
	 * access arbitrary content
	 * 
	 * @return content as byte[]
	 */
	public byte[] getContentAsBytes() {
		return content;
	}

	/**
	 * access string message
	 * 
	 * @return string content
	 */
	public String getContentAsString() {
		try {
			return new String(content, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * set content for arbitrary message
	 * 
	 * @param content
	 *            as byte[]
	 */
	public void setContent(byte content[]) {
		this.content = content;
	}

	/**
	 * set content for string message
	 * @param content as String
	 */
	public void setContent(String content) {
		try {
			this.content = content.getBytes("UTF-8");
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * get the stomp destination of a message.
	 */
	public String getDestination() {
		return destination;
	}

	/**
	 * set the stomp destination of a message.
	 * 
	 * @param destination
	 *            a legal stomp destination
	 */
	public void setDestination(String destination) {
		this.destination = destination;
	}

	public String getMessageId() {
		return messageId;
	}

	public void setMessageId(String id) {
		messageId = id;
	}

	public String getProperty(String key) {
		return properties.get(key);
	}

	public void setProperty(String name, String value) {
		properties.put(name, value);
	}

	public String[] getPropertyNames() {
		return (String[]) properties.keySet().toArray(new String[0]);
	}

	public void setProperties(HashMap<String, String> properties) {
		this.properties = properties;
	}

	private String messageId;
	private String destination;
	private HashMap<String, String> properties;
	private byte content[];
}
