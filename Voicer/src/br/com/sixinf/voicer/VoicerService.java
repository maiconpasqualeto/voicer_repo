/**
 * 
 */
package br.com.sixinf.voicer;

import org.doubango.ngn.NgnEngine;
import org.doubango.tinyWRAP.DialogEvent;
import org.doubango.tinyWRAP.RegistrationEvent;
import org.doubango.tinyWRAP.RegistrationSession;
import org.doubango.tinyWRAP.SipCallback;
import org.doubango.tinyWRAP.SipSession;
import org.doubango.tinyWRAP.SipStack;
import org.doubango.tinyWRAP.tinyWRAPConstants;

import android.app.Activity;
import android.util.Log;

/**
 * @author maicon
 *
 */
public class VoicerService {
		
	private RegistrationSession registrationSession;
	private final NgnEngine engine;
	
	public VoicerService(Activity context) {
		//Voicer c = new Voicer();
		// Sets main activity (should be done before starting services)
		engine = NgnEngine.getInstance();
		engine.setMainActivity(context);
	}

	public void sipRegister() {
		
		
		final String realm = "sip.linphone.org";
		final String privateIdentity = "maiconpas";
		final String publicIdentity = "maiconpas@sip.linphone.org";
		final String password = "mariana123";
		final String proxyHost = "sip.linphone.org";		
		// Sip Callback
		final SipCallback callback = new SipCallback(){
		
			@Override
			public int OnDialogEvent(DialogEvent e) {
				final SipSession sipSession = e.getBaseSession();
				final long sipSessionId = sipSession.getId();
				final short code = e.getCode();
				
				switch (code){
					case
						tinyWRAPConstants.tsip_event_code_dialog_connecting:
						if(registrationSession != null && registrationSession.getId() == sipSessionId){
							Log.d("VOICER", "Tentando registrar....");							
						}
						break;
					case
						tinyWRAPConstants.tsip_event_code_dialog_connected:
						if(registrationSession != null && registrationSession.getId() == sipSessionId){
							Log.d("VOICER", "Registrado...");
						}
						break;
					case
						tinyWRAPConstants.tsip_event_code_dialog_terminating:
						if(registrationSession != null && registrationSession.getId() == sipSessionId){
							Log.d("VOICER", "Desregistrando....");
						}
						break;
					case
						tinyWRAPConstants.tsip_event_code_dialog_terminated:
						if(registrationSession !=null && registrationSession.getId() == sipSessionId){
							Log.d("VOICER", "Não registrado....");
						}
						break;
				}
				return 0;
			}
			
			@Override
			public int OnRegistrationEvent(RegistrationEvent e) {
				// low level events
				return 0;
			}
		};
		// Create the SipStack
		SipStack sipStack = new SipStack(callback, realm, privateIdentity, publicIdentity);
		// Set Proxy Host and port
		sipStack.setProxyCSCF(proxyHost, 5060, "UDP", "IPv4");
		// Set password
		sipStack.setPassword(password);
		if(sipStack.isValid()){
			if(sipStack.start()){
				registrationSession = new RegistrationSession(sipStack);
				registrationSession.setFromUri(publicIdentity);
				// Send SIP register request
				registrationSession.register_();
			}
		}
	}
	
}
