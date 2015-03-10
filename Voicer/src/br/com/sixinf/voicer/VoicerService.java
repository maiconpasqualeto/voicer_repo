/**
 * 
 */
package br.com.sixinf.voicer;

import java.util.Observable;

import org.doubango.ngn.NgnEngine;
import org.doubango.ngn.media.NgnMediaType;
import org.doubango.ngn.services.INgnSipService;
import org.doubango.ngn.sip.NgnAVSession;
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
public class VoicerService extends Observable {
		
	private RegistrationSession registrationSession;
	private final NgnEngine engine;
	private SipStack sipStack;
			
	public VoicerService(Activity context) {
		//Voicer c = new Voicer();
		// Sets main activity (should be done before starting services)
		engine = NgnEngine.getInstance();
		engine.setMainActivity(context);
	}
	
	/**
	 * 
	 */
	public void startEngine() {
		final Thread thread = new Thread(new Runnable(){
			@Override
			public void run() {
				if(!engine.isStarted()){
					Log.d("VOICER", "Starts the engine");
					engine.start();
				}
			}
		});
		thread.setPriority(Thread.MAX_PRIORITY);
		thread.start();
	}

	/**
	 * 
	 */
	public void sipRegister(String usuario, String senha) {
		
		final String realm = "sip:linphone.org";
		final String publicIdentity = "sip:maiconpas@sip.linphone.org";
		/*final String privateIdentity = "maiconpas";
		final String password = "mariana123";*/
		final String proxyHost = "sip.linphone.org";
		// Sip Callback
		final SipCallback callback = new SipCallback(){
		
			@Override
			public int OnDialogEvent(DialogEvent e) {
				final SipSession sipSession = e.getBaseSession();
				final long sipSessionId = sipSession.getId();
				final short code = e.getCode();
				Log.d("VOICER", "Register return code: " + code);	
				switch (code){
					case
						tinyWRAPConstants.tsip_event_code_dialog_connecting:
						if(registrationSession != null && registrationSession.getId() == sipSessionId){
							Log.d("VOICER", "Tentando registrar....");
							setChanged();
							notifyObservers(StatusRegistroSIP.SOLICITANDO_REGISTRO);
						}
						break;
					case
						tinyWRAPConstants.tsip_event_code_dialog_connected:
						if(registrationSession != null && registrationSession.getId() == sipSessionId){
							Log.d("VOICER", "Registrado");
							setChanged();
							notifyObservers(StatusRegistroSIP.REGISTRADO);							
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
							setChanged();
							notifyObservers(StatusRegistroSIP.NAO_REGISTRADO);
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
		sipStack = new SipStack(callback, realm, usuario, publicIdentity);
		// Set Proxy Host and port
		sipStack.setProxyCSCF(proxyHost, 5060, "UDP", "IPv4");
		// Set password
		sipStack.setPassword(senha);
		if(sipStack.isValid()){
			if(sipStack.start()){
				registrationSession = new RegistrationSession(sipStack);
				registrationSession.setFromUri(publicIdentity);
				// Send SIP register request
				registrationSession.register_();
			}
		}
	}
	
	/**
	 * 
	 */
	public void sipUnregister() {
		INgnSipService service = engine.getSipService();
		
		if (service.isRegistered())
			service.unRegister();
		
		/*if (registrationSession != null)
			registrationSession.unRegister();*/
	}
	
	/**
	 * 
	 */
	public void makeAudioCall(String sipUri) {
		NgnAVSession avSession = NgnAVSession.createOutgoingSession(
		engine.getSipService().getSipStack(), NgnMediaType.Audio);
		avSession.makeCall(sipUri);
		//...
	}
	
}
