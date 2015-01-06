/**
 * 
 */
package br.com.sixinf.voicer;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.sip.SipAudioCall;
import android.net.sip.SipProfile;
import android.net.sip.SipSession;
import android.util.Log;

/**
 * @author maicon
 *
 */
public class VoicerChamadaRecebida extends BroadcastReceiver {

	
	@Override
	public void onReceive(Context context, Intent intent) {
		SipAudioCall incomingCall = null;
        try {
            SipAudioCall.Listener listener = new SipAudioCall.Listener() {
                @Override
                public void onRinging(SipAudioCall call, SipProfile caller) {
                    try {
                        call.answerCall(30);
                    } catch (Exception e) {
                        Log.e("VOICER", "Erro ao atender chamada", e);
                    }
                }
            };
            
            VoicerActivity wtActivity = (VoicerActivity) context;
            incomingCall = VoicerFacade.getInstance().getSipManager().takeAudioCall(intent, listener);
            incomingCall.answerCall(30);
            incomingCall.startAudio();
            incomingCall.setSpeakerMode(true);
            
            wtActivity.setAudioCall(incomingCall);
            wtActivity.updateStatus(SipSession.State.toString(incomingCall.getState()));
        } catch (Exception e) {
            if (incomingCall != null) {
                incomingCall.close();
            }
        }

	}

}
