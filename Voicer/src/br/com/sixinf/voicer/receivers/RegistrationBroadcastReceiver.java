/**
 * 
 */
package br.com.sixinf.voicer.receivers;

import org.doubango.ngn.NgnEngine;
import org.doubango.ngn.events.NgnEventArgs;
import org.doubango.ngn.events.NgnInviteEventArgs;
import org.doubango.ngn.events.NgnMediaPluginEventArgs;
import org.doubango.ngn.events.NgnRegistrationEventArgs;
import org.doubango.ngn.media.NgnMediaType;
import org.doubango.ngn.sip.NgnAVSession;
import org.doubango.ngn.sip.NgnInviteSession.InviteState;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import br.com.sixinf.voicer.ObserverData;
import br.com.sixinf.voicer.ObserverData.EventType;
import br.com.sixinf.voicer.Voicer;
import br.com.sixinf.voicer.sip.VoicerService;

/**
 * @author maicon
 *
 */
public class RegistrationBroadcastReceiver extends BroadcastReceiver {
	
	private VoicerService voicerService;
	
	public RegistrationBroadcastReceiver() {
		
	}
	
	public RegistrationBroadcastReceiver(VoicerService voicerService) {
		this.voicerService = voicerService;
	}	
	

	@Override
	public void onReceive(Context context, Intent intent) {

		final String action = intent.getAction();
		
		Log.d("VOICER", "Action: " + action);
		ObserverData od = new ObserverData();
		
		// Registration Event
		if (NgnRegistrationEventArgs.ACTION_REGISTRATION_EVENT.equals(action)) {
			NgnRegistrationEventArgs args = intent
					.getParcelableExtra(NgnEventArgs.EXTRA_EMBEDDED);
			if (args == null) {
				Log.d("VOICER", "Invalid event args");
				return;
			} else {
				Log.d("VOICER", args.getSipCode() + " - " + args.getPhrase());
			}
			
			od.setEventType(EventType.EVENT_REGISTRATION);
			od.setRegisterState(args.getEventType());
			od.setSipCode(String.valueOf(args.getSipCode()));
			od.setSipMessage(args.getPhrase());
			
			switch (args.getEventType()) {
			case REGISTRATION_NOK:
				Log.d("VOICER", "Failed to register :(");
				od.setEventMessage("Failed to register :(");
				voicerService.updateObservers(od);
				break;
			case UNREGISTRATION_OK:
				Log.d("VOICER", "You are now unregistered :)");
				od.setEventMessage("You are now unregistered :)");
				voicerService.updateObservers(od);
				break;
			case REGISTRATION_OK:
				Log.d("VOICER", "You are now registered :)");
				od.setEventMessage("You are now registered :)");
				voicerService.updateObservers(od);				
				break;
			case REGISTRATION_INPROGRESS:
				Log.d("VOICER", "Trying to register...");
				od.setEventMessage("Trying to register...");
				voicerService.updateObservers(od);
				break;
			case UNREGISTRATION_INPROGRESS:
				Log.d("VOICER", "Trying to unregister...");
				od.setEventMessage("Trying to unregister...");				
				voicerService.updateObservers(od);
				break;
			case UNREGISTRATION_NOK:
				Log.d("VOICER", "Failed to unregister :(");
				od.setEventMessage("Failed to unregister :(");
				voicerService.updateObservers(od);
				break;
			}

		} else if (NgnInviteEventArgs.ACTION_INVITE_EVENT.equals(action)) {
			NgnInviteEventArgs args = intent
					.getParcelableExtra(NgnEventArgs.EXTRA_EMBEDDED);
			if (args == null) {
				Log.e("VOICER", "Cannot find session");
				return;
			} else {
				Log.d("VOICER", args.getPhrase());
			}
			Log.d("VOICER",
					"This is an event for session number "
							+ args.getSessionId());		
			
			// Retrieve the session from the store
			NgnAVSession avSession = NgnAVSession.getSession(args
					.getSessionId());
			
			od.setEventType(EventType.EVENT_INVITE);
			od.setSipMessage(args.getPhrase());
			
			NgnEngine mEngine = NgnEngine.getInstance();
			
			if (avSession == null) {
				Log.d("VOICER", "avSession null");
				od.setEventMessage("avSession null");
				od.setInviteState(InviteState.NONE);
				mEngine.getSoundService().stopRingTone();
				mEngine.getSoundService().stopRingBackTone();
				voicerService.updateObservers(od);
				return;
			}
			
			od.setInviteState(avSession.getState());
			
			switch (avSession.getState()) {
				case NONE:
					break;
				case INCOMING:
					Log.i("VOICER", "Incoming call");
					od.setEventMessage("Incoming call");
					String incommingCallerId = avSession.getRemotePartyDisplayName();
					od.setIncommingCallerId(incommingCallerId);
					mEngine.getSoundService().startRingTone();
					avSession.setContext(Voicer.getAppContext());
					voicerService.habilitaSpeakerphone();
					voicerService.setAvSession(avSession);
					voicerService.updateObservers(od);
					break;
				case INPROGRESS:
					Log.i("VOICER", "Call in progress");
					od.setEventMessage("Call in progress");
					voicerService.habilitaSpeakerphone();					
					mEngine.getSoundService().startRingBackTone();
					voicerService.updateObservers(od);					
					break;
				case REMOTE_RINGING:
					Log.i("VOICER", "Remote party is ringing");
					od.setEventMessage("Remote party is ringing");
					voicerService.updateObservers(od);
					break;
				case EARLY_MEDIA:
					Log.i("VOICER", "Early media started");
					od.setEventMessage("Early media started");
					voicerService.updateObservers(od);
					break;
				case INCALL:
					Log.i("VOICER", "Call connected");
					od.setEventMessage("Call connected");					
					voicerService.updateObservers(od);
					mEngine.getSoundService().stopRingTone();
					mEngine.getSoundService().stopRingBackTone();
					break;
				case TERMINATING:
					Log.i("VOICER", "Call terminating");
					od.setEventMessage("Call terminating");
					voicerService.updateObservers(od);
					break;
				case TERMINATED:
					Log.i("VOICER", "Call terminated");
					od.setEventMessage("Call terminated");
					mEngine.getSoundService().stopRingTone();
					mEngine.getSoundService().stopRingBackTone();
					voicerService.setAvSession(null);
					voicerService.updateObservers(od);
					break;
			}
		} else if(NgnMediaPluginEventArgs.ACTION_MEDIA_PLUGIN_EVENT.equals(intent.getAction())){
			NgnMediaPluginEventArgs args = intent.getParcelableExtra(NgnMediaPluginEventArgs.EXTRA_EMBEDDED);
			if (args == null) {
				Log.e("VOICER", "Invalid event args for Video call");
				return;
			}
			
			od.setEventType(EventType.EVENT_MEDIA_PLUGIN);
			
			
			switch(args.getEventType()){
				case STARTED_OK: //started or restarted (e.g. reINVITE)
				{
					Log.e("VOICER", "Action Media - STARTED_OK");
					if ((args.getMediaType() == NgnMediaType.AudioVideo || args.getMediaType() == NgnMediaType.Video)) {
						od.setEventMessage("STARTED_OK");
						voicerService.updateObservers(od);
					}
					break;
				}
				case PREPARED_OK:
					Log.e("VOICER", "Action Media - PREPARED_OK");
					break;
				case PREPARED_NOK:
					Log.e("VOICER", "Action Media - PREPARED_OK");
					break;
				case STARTED_NOK:
					Log.e("VOICER", "Action Media - STARTED_NOK");
					break;
				case STOPPED_OK:
					Log.e("VOICER", "Action Media - STOPPED_OK");
					break;
				case STOPPED_NOK:
					Log.e("VOICER", "Action Media - STOPPED_NOK");
					break;
				case PAUSED_OK:
					Log.e("VOICER", "Action Media - PAUSED_OK");
					break;
				case PAUSED_NOK:
					Log.e("VOICER", "Action Media - PAUSED_NOK");
					break;
				
			}
		}
	}

}
