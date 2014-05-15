/**
 * 
 */
package de.fh_zwickau.pti.mqgamecommon;

/**
 * @author georg beier
 * 
 */
public enum MessageKind {
	// basic for session management
	login,
	logout,
	register,
	authenticated,
	loggedOut,
	failed,
	// messages from chatter to chatroom
	chatCreate,
	chatChat,
	chatNewParticipant,
	chatLeave,
	chatParticipationRequest,
	chatCancelRequest,
	chatClose,
	// messages from chatroom or peer chatter to chatter
	chatterInvited,
	chatterReject,
	chatterChatCreated,
	chatterClosed,
	chatterAccepted,
	chatterDenied,
	chatterParticipationRequest,
	chatterRequestCanceled,
	chatterParticipantEntered,
	chatterParticipantLeft,
	chatterNewChat,
	// messages from client to chatter
	chatterMsgDeny,
	chatterMsgRequestParticipation,
	chatterMsgCancel,
	chatterMsgAcceptInvitation,
	chatterMsgLeave,
	chatterMsgStartChat,
	chatterMsgClose,
	chatterMsgChat,
	chatterMsgInvite,
	chatterMsgAccept,
	chatterMsgReject,
	// messages from chatter to client
	clientChatStarted,
	clientChatClosed, 
	clientAccepted, clientDenied,
	clientInvitation,
	clientNewChat,
	clientParticipating,
	clientParticipantEntered,
	clientParticipantLeft,
	clientRejected,
	clientRequest, 
	clientRequestCancelled,
}
