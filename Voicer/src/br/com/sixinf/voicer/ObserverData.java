/**
 * 
 */
package br.com.sixinf.voicer;

import java.io.Serializable;

import org.doubango.ngn.events.NgnRegistrationEventTypes;
import org.doubango.ngn.sip.NgnInviteSession;

/**
 * @author maicon
 *
 */
public class ObserverData implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private NgnRegistrationEventTypes registerState; 
	private NgnInviteSession.InviteState inviteState;
	private String eventMessage;
	private String sipCode;
	private String sipMessage;	
	private EventType eventType;
	private String incommingCallerId;

	public NgnRegistrationEventTypes getRegisterState() {
		return registerState;
	}

	public void setRegisterState(NgnRegistrationEventTypes registerState) {
		this.registerState = registerState;
	}

	public NgnInviteSession.InviteState getInviteState() {
		return inviteState;
	}

	public void setInviteState(NgnInviteSession.InviteState inviteState) {
		this.inviteState = inviteState;
	}
		
	public String getEventMessage() {
		return eventMessage;
	}

	public void setEventMessage(String eventMessage) {
		this.eventMessage = eventMessage;
	}

	public String getSipMessage() {
		return sipMessage;
	}

	public void setSipMessage(String sipMessage) {
		this.sipMessage = sipMessage;
	}

	public EventType getEventType() {
		return eventType;
	}

	public void setEventType(EventType eventType) {
		this.eventType = eventType;
	}

	public String getSipCode() {
		return sipCode;
	}

	public void setSipCode(String sipCode) {
		this.sipCode = sipCode;
	}

	public String getIncommingCallerId() {
		return incommingCallerId;
	}

	public void setIncommingCallerId(String incommingCallerId) {
		this.incommingCallerId = incommingCallerId;
	}

	public enum EventType {
		EVENT_REGISTRATION,
		EVENT_INVITE;
	}
		
}
