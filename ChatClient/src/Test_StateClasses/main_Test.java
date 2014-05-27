package Test_StateClasses;

import javax.jms.JMSException;

import messaging.interfaces.ChatServerMessageProducer;
import States.ChatClientState;
import de.fh_zwickau.pti.chatclientcommon.ChatServerMessageReceiver;

public class main_Test {

	private static ChatServerMessageProducer messageProducer;
	private static ChatServerMessageReceiver messageReceiver;

	public main_Test() {
		messageProducer = new ChatServerMessageProducer() {

			@Override
			public void startChat() throws JMSException {
				System.out.println(this.toString() + "startChat()");
			}

			@Override
			public void setState(ChatClientState chatClientState) {
				System.out.println(this.toString() + "setState( parameter: "
						+ chatClientState.toString() + ")");

			}

			@Override
			public void requestParticipian(String chatterID)
					throws JMSException {
				System.out.println(this.toString()
						+ "requestParticipian( parameter: " + chatterID + ")");
			}



			@Override
			public void register(String uname, String pword) throws Exception {
				// TODO Auto-generated method stub

			}

			@Override
			public void logout() throws Exception {
				// TODO Auto-generated method stub

			}

			@Override
			public void login(String uname, String pword) throws Exception {
				// TODO Auto-generated method stub

			}

			@Override
			public void leave() throws JMSException {
				// TODO Auto-generated method stub

			}

			@Override
			public void invite(String CNN) throws JMSException {
				// TODO Auto-generated method stub

			}

			@Override
			public void deny() throws JMSException {
				// TODO Auto-generated method stub

			}

			@Override
			public void connectToServer(String brokerUri) {
				// TODO Auto-generated method stub

			}

			@Override
			public void close() throws JMSException {
				// TODO Auto-generated method stub

			}

			@Override
			public void chat(String messageText) throws JMSException {
				// TODO Auto-generated method stub

			}

			@Override
			public void cancel() throws JMSException {
				// TODO Auto-generated method stub

			}

			@Override
			public void askForChatters() throws JMSException {
				// TODO Auto-generated method stub

			}

			@Override
			public void askForChats() throws JMSException {
				// TODO Auto-generated method stub

			}

			@Override
			public void acceptInvitation() throws JMSException {
				// TODO Auto-generated method stub

			}


			@Override
			public void reject(String chatterID) throws JMSException {
				System.out.println(this.toString() + "requestParticipian() ");

			}

			@Override
			public void accept(String chatterID) throws JMSException {
				// TODO Auto-generated method stub

			}
		};
		messageReceiver = new ChatServerMessageReceiver() {
			
			@Override
			public void gotSuccess() {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void gotLogout() {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void gotFail() {
				// TODO Auto-generated method stub
				
			}
		};
	}
	

	// public static void main(String[] args) {
	// NotLoggedIn notLoggedInTest = new NotLoggedIn(messageProducer,
	// messageReceiver);
	//
	// }
}
