package Test_StateClasses;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;

import javax.jms.JMSException;

import messaging.interfaces.ChatServerMessageProducer;
import messaging.interfaces.ChatServerMessageReceiver;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import States.ChatClientState;
import States.StatesClasses.NotLoggedIn;

;

public class NotLoggedInTest {

	private static ChatServerMessageReceiver messageReceiver;
	private static ChatServerMessageProducer messageProducer;
	public String result;
	public ChatClientState chatClientState;

	@Before
	public void setUp() throws Exception {
		messageProducer = new ChatServerMessageProducer() {


			 
			@Override
			public void startChat() throws JMSException {
				System.out.println(":");
			}

			@Override
			public void setState(ChatClientState chatClientState) {
				System.out.println(":");

			}

			@Override
			public void requestParticipian(String chatterID)
					throws JMSException {
				System.out.println(":");

			}

			@Override
			public void reject(String chatterID) throws JMSException {
				System.out.println(":");

			}

			@Override
			public void register(String uname, String pword) throws Exception {
				result = "register()";

			}

			@Override
			public void logout() throws Exception {
				System.out.println(":");

			}

			@Override
			public void login(String uname, String pword) throws Exception {
				result = "onLogin()";

			}

			@Override
			public void leave() throws JMSException {
				System.out.println(":");

			}

			@Override
			public void invite(String CNN) throws JMSException {
				System.out.println(":");

			}

			@Override
			public void deny() throws JMSException {
				System.out.println(":");

			}

			@Override
			public void connectToServer(String brokerUri) {
				System.out.println(":");

			}

			@Override
			public void close() throws JMSException {
				System.out.println(":");

			}

			@Override
			public void chat(String messageText) throws JMSException {
				System.out.println(":");

			}

			@Override
			public void cancel() throws JMSException {
				System.out.println(":");

			}

			@Override
			public void askForChatters() throws JMSException {
				System.out.println(":");

			}

			@Override
			public void askForChats() throws JMSException {
				System.out.println(":");

			}

			@Override
			public void acceptInvitation(String request) throws JMSException {
				System.out.println(":");

			}

			@Override
			public void accept(String chatterID) throws JMSException {
				System.out.println(":");

			}
		};
		messageReceiver = new ChatServerMessageReceiver() {

			@Override
			public void setState(ChatClientState chatClientState) {
				System.out.println(":");

			}

			@Override
			public void gotSuccess() {
				result = "gotSucess()";


			}

			@Override
			public void gotRequestCancelled(String chatterID) {
				System.out.println(":");

			}

			@Override
			public void gotRequest(String chatterID) {
				System.out.println(":");

			}

			@Override
			public void gotRejected(String chatterID) {
				System.out.println(":");

			}

			@Override
			public void gotParticipating() {
				System.out.println(":");

			}

			@Override
			public void gotParticipantLeft(String chatterID) {
				System.out.println(":");

			}

			@Override
			public void gotParticipantEntered(String chatterID) {
				System.out.println(":");

			}

			@Override
			public void gotNewChat(String chatter, String messageText) {
				System.out.println(":");

			}

			@Override
			public void gotLogout() {
				System.out.println(":");

			}

			@Override
			public void gotInvite(String chatter, String chatID) {
				System.out.println(":");

			}

			@Override
			public void gotFail() {
				result = "gotFail()";

			}

			@Override
			public void gotDenied(String chatterID) {
				System.out.println(":");

			}

			@Override
			public void gotChatters(ArrayList<String> chatters) {
				System.out.println(":");

			}

			@Override
			public void gotChats(ArrayList<String> chatsWithOwner) {
				System.out.println(":");

			}

			@Override
			public void gotChatStarted(String chatID) {
				System.out.println(":");

			}

			@Override
			public void gotChatClosed() {
				System.out.println(":");

			}

			@Override
			public void gotAccepted(String chatterID) {
				System.out.println(":");

			}
		};
		result = null;
		chatClientState = new NotLoggedIn(messageProducer, messageReceiver);
	}

	@After
	public void tearDown() throws Exception {

	}

	@Test
	public void testOnRegister() {
		String expected = "register()";
		NotLoggedIn notLoggedIn = new NotLoggedIn(chatClientState);
		notLoggedIn.onRegister("username", "passwort");
		assertTrue(expected.equals(this.result));
	}

	@Test
	public void testOnLogin() {
		String expected = "onLogin()";
		NotLoggedIn notLoggedIn = new NotLoggedIn(chatClientState);
		notLoggedIn.onLogin("username", "passwort");
		assertTrue(expected.equals(this.result));
	}

	@Test
	public void testGotFail() {
		String expected = "gotFail()";
		NotLoggedIn notLoggedIn = new NotLoggedIn(chatClientState);
		notLoggedIn.gotFail();
		assertTrue(expected.equals(this.result));
	}

	@Test
	public void testGotSucess() {
		String expected = "gotSucess()";
		NotLoggedIn notLoggedIn = new NotLoggedIn(chatClientState);
		notLoggedIn.gotSucess();
		assertTrue(expected.equals(this.result));
		assertTrue(true);
	}

	@Test
	public void testNotLoggedInChatClientState() {
		assertTrue(true);// konstruktor muss laufen
	}

	@Test
	public void testNotLoggedInChatServerMessageProducerChatServerMessageReceiver() {
		assertTrue(true);// konstruktor muss laufen
	}

}
