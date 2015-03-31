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
import br.com.sixinf.voicer.VoicerActivity;
import br.com.sixinf.voicer.VoicerService;

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
				break;
			case UNREGISTRATION_OK:
				Log.d("VOICER", "You are now unregistered :)");
				break;
			case REGISTRATION_OK:
				Log.d("VOICER", "You are now registered :)");
				Intent i = new Intent(context, VoicerActivity.class);
				context.startActivity(i);
				break;
			case REGISTRATION_INPROGRESS:
				Log.d("VOICER", "Trying to register...");
				break;
			case UNREGISTRATION_INPROGRESS:
				Log.d("VOICER", "Trying to unregister...");
				break;
			case UNREGISTRATION_NOK:
				Log.d("VOICER", "Failed to unregister :(");
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
				return;
			}
			
			NgnEngine mEngine = NgnEngine.getInstance();
			
			switch (avSession.getState()) {
				case NONE:
					break;
				case INCOMING:
					Log.i("VOICER", "Incoming call");
					mEngine.getSoundService().startRingTone();
					voicerService.setAvSession(avSession);
					break;
				case INPROGRESS:
					Log.i("VOICER", "Call in progress");
					break;
				case REMOTE_RINGING:
					Log.i("VOICER", "Remote party is ringing");
					break;
				case EARLY_MEDIA:
					Log.i("VOICER", "Early media started");
					break;
				case INCALL:
					Log.i("VOICER", "Call connected");
					mEngine.getSoundService().stopRingTone();
					avSession.setSpeakerphoneOn(true);
					break;
				case TERMINATING:
					Log.i("VOICER", "Call terminating");
					break;
				case TERMINATED:
					Log.i("VOICER", "Call terminated");
					mEngine.getSoundService().stopRingTone();
					mEngine.getSoundService().stopRingBackTone();
					break;
			}
		}
	}

}
