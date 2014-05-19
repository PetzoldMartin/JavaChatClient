package States.StatesClasses;

import States.ChatClientState;

public abstract class Chatting extends ChatClientState {

	public abstract void onChat();
	public abstract void gotNewChat();
	public abstract void gotParticipantEntered();
	public abstract void gotParticipantLeft();
}
