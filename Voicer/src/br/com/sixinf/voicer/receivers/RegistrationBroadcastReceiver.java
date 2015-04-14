/**
 * 
 */
package br.com.sixinf.voicer.receivers;

import org.doubango.ngn.NgnEngine;
import org.doubango.ngn.events.NgnEventArgs;
import org.doubango.ngn.events.NgnInviteEventArgs;
import org.doubango.ngn.events.NgnRegistrationEventArgs;
import org.doubango.ngn.sip.NgnAVSession;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import br.com.sixinf.voicer.sip.VoicerService;
import br.com.sixinf.voicer.telas.VoicerActivity;

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
		
		//Log.d("VOICER", "Action: " + action);
		
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
			switch (args.getEventType()) {
			case REGISTRATION_NOK:
				Log.d("VOICER", "Failed to register :(");
				voicerService.updateObservers("Failed to register :(");
				break;
			case UNREGISTRATION_OK:
				Log.d("VOICER", "You are now unregistered :)");
				voicerService.updateObservers("You are now unregistered :)");
				break;
			case REGISTRATION_OK:
				Log.d("VOICER", "You are now registered :)");
				voicerService.updateObservers("You are now registered :)");
				Intent i = new Intent(context, VoicerActivity.class);
				context.startActivity(i);
				break;
			case REGISTRATION_INPROGRESS:
				Log.d("VOICER", "Trying to register...");
				voicerService.updateObservers("Trying to register...");
				break;
			case UNREGISTRATION_INPROGRESS:
				Log.d("VOICER", "Trying to unregister...");
				voicerService.updateObservers("Trying to unregister...");
				break;
			case UNREGISTRATION_NOK:
				Log.d("VOICER", "Failed to unregister :(");
				voicerService.updateObservers("Failed to unregister :(");
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
			
			if (avSession == null) {
				Log.d("VOICER", "avSession null");
				voicerService.updateObservers(args.getPhrase());
				return;
			}
			
			NgnEngine mEngine = NgnEngine.getInstance();
			
			switch (avSession.getState()) {
				case NONE:
					break;
				case INCOMING:
					Log.i("VOICER", "Incoming call");
					voicerService.updateObservers("Incoming call");
					mEngine.getSoundService().startRingTone();
					voicerService.setAvSession(avSession);
					break;
				case INPROGRESS:
					Log.i("VOICER", "Call in progress");
					voicerService.updateObservers("Call in progress");
					break;
				case REMOTE_RINGING:
					Log.i("VOICER", "Remote party is ringing");
					voicerService.updateObservers("Remote party is ringing");
					break;
				case EARLY_MEDIA:
					Log.i("VOICER", "Early media started");
					voicerService.updateObservers("Early media started");
					break;
				case INCALL:
					Log.i("VOICER", "Call connected");
					voicerService.updateObservers("Call connected");
					mEngine.getSoundService().stopRingTone();
					mEngine.getSoundService().stopRingBackTone();
					break;
				case TERMINATING:
					Log.i("VOICER", "Call terminating");
					voicerService.updateObservers("Call terminating");
					break;
				case TERMINATED:
					Log.i("VOICER", "Call terminated");
					voicerService.updateObservers("Call terminated");
					mEngine.getSoundService().stopRingTone();
					mEngine.getSoundService().stopRingBackTone();
					voicerService.stopAudioCall();
					break;
			}
		}
	}

}
