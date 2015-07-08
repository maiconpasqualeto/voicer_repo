/**
 * 
 */
package br.com.sixinf.voicer.sip;

import java.util.Observable;

import org.doubango.ngn.NgnApplication;
import org.doubango.ngn.NgnEngine;
import org.doubango.ngn.media.NgnMediaType;
import org.doubango.ngn.services.INgnConfigurationService;
import org.doubango.ngn.services.INgnSipService;
import org.doubango.ngn.sip.NgnAVSession;
import org.doubango.ngn.utils.NgnConfigurationEntry;

import android.app.Activity;
import android.util.Log;
import android.view.View;
import br.com.sixinf.voicer.ObserverData;
import br.com.sixinf.voicer.Voicer;
import br.com.sixinf.voicer.persistencia.Config;

/**
 * @author maicon
 *
 */
public class VoicerService extends Observable {
	
	private final NgnEngine engine;
	private INgnSipService sipService;
	private NgnAVSession avSession;
	private Config conf;
			
	public VoicerService(Activity context, Config conf) {
		this.conf = conf;
		engine = NgnEngine.getInstance();
		engine.setMainActivity(context);		
		sipService = engine.getSipService();
	}
	
	public void setAvSession(NgnAVSession avSession) {
		this.avSession = avSession;
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
	 * @param usuario
	 * @param senha
	 */
	public void setupConfig() {
		/*String realm = "sip:openjsip.net";
		String publicIdentity = "sip:" + usuario + "@openjsip.net";
		String proxyHost = "192.168.25.155";
		int port = 5060;*/
		
		String publicIdentity = "sip:" + conf.getUsuario() + "@" + conf.getDomain();
		
		NgnEngine mEngine = NgnEngine.getInstance();
		INgnConfigurationService mConfigurationService = mEngine.getConfigurationService();
		mConfigurationService.putString(
				NgnConfigurationEntry.IDENTITY_IMPI, conf.getUsuario());
		mConfigurationService.putString(
				NgnConfigurationEntry.IDENTITY_IMPU, publicIdentity);
		mConfigurationService.putString(
				NgnConfigurationEntry.IDENTITY_PASSWORD, conf.getSenha());
		mConfigurationService.putString(
				NgnConfigurationEntry.NETWORK_PCSCF_HOST, conf.getHost());
		mConfigurationService.putInt(
				NgnConfigurationEntry.NETWORK_PCSCF_PORT, conf.getPorta());
		mConfigurationService.putString(
				NgnConfigurationEntry.NETWORK_REALM, conf.getRealm());
		mConfigurationService.putBoolean(
				NgnConfigurationEntry.NETWORK_USE_WIFI, true);
		// By default, using 3G for calls disabled
		mConfigurationService.putBoolean(
				NgnConfigurationEntry.NETWORK_USE_3G, true);
		// You may want to leave the registration timeout to the default 1700 seconds
		mConfigurationService.putInt(
				NgnConfigurationEntry.NETWORK_REGISTRATION_TIMEOUT, 3600);
		mConfigurationService.putString(
				NgnConfigurationEntry.NETWORK_TRANSPORT, NgnConfigurationEntry.DEFAULT_NETWORK_TRANSPORT);		
		
		mConfigurationService.commit();
	}

	/**
	 * 
	 */
	public void sipRegister() {
		
		// Register
		if (!sipService.isRegistered()) {
			sipService.register(engine.getMainActivity());
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
	public boolean makeAudioCall(String sipUri) {
		if (avSession != null &&
				avSession.isActive())
			return false;
		
		avSession = NgnAVSession.createOutgoingSession(
				engine.getSipService().getSipStack(), NgnMediaType.Audio);
		
		engine.getSoundService().startRingBackTone();
		
		return avSession.makeCall(sipUri);
	}
	
	/**
	 * 
	 */
	public void stopAudioVideoCall() {
		if (avSession != null)
			avSession.hangUpCall();
	}
			
	/**
	 * 
	 */
	public void acceptCall() {
		if (avSession != null)
			avSession.acceptCall();
	}
	
	public void updateObservers(ObserverData observerData){
		setChanged();
		notifyObservers(observerData);
	}

	public Config getConf() {
		return conf;
	}

	public void setConf(Config conf) {
		this.conf = conf;
	}
	
	/**
	 * 
	 */
	public boolean makeVideoCall(String sipUri) {
		if (avSession != null &&
				avSession.isActive())
			return false;
		
		avSession = NgnAVSession.createOutgoingSession(
				engine.getSipService().getSipStack(), NgnMediaType.AudioVideo);
		
		avSession.setContext(Voicer.getAppContext());
		
		engine.getSoundService().startRingBackTone();
		
		return avSession.makeCall(sipUri);
	}
	
	/**
	 * 
	 * @return
	 */
	public View startVideoConsumerPreview() {
		avSession.setContext(Voicer.getAppContext());
		return avSession.startVideoConsumerPreview();
	}

	/**
	 * 
	 * @return
	 */
	public View startVideoProducerPreview() {
		avSession.setContext(Voicer.getAppContext());
		avSession.setRotation(270);
		return avSession.startVideoProducerPreview();
	}
	
	/**
	 * 
	 * @return
	 */
	public boolean isVideoCall() {
		if (avSession != null &&
				( avSession.getMediaType() == NgnMediaType.AudioVideo || 
					avSession.getMediaType() == NgnMediaType.Video) )
			return true;
		
		return false;
	}
}
