/**
 * 
 */
package br.com.sixinf.voicer.sip;

import java.util.Observable;

import org.doubango.ngn.NgnEngine;
import org.doubango.ngn.media.NgnMediaType;
import org.doubango.ngn.services.INgnConfigurationService;
import org.doubango.ngn.services.INgnSipService;
import org.doubango.ngn.sip.NgnAVSession;
import org.doubango.ngn.utils.NgnConfigurationEntry;

import android.app.Activity;
import android.util.Log;

/**
 * @author maicon
 *
 */
public class VoicerService extends Observable {
	
	private final NgnEngine engine;
	private INgnSipService sipService;
	private NgnAVSession avSession; 
	private String realm;
	private String domain;
	private String proxyHost;
	private int port;
			
	public VoicerService(Activity context) {
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
	public void setupConfig(String usuario, String senha) {
		/*String realm = "sip:openjsip.net";
		String publicIdentity = "sip:" + usuario + "@openjsip.net";
		String proxyHost = "192.168.25.155";
		int port = 5060;*/
		
		String publicIdentity = "sip:" + usuario + "@" + domain;
		
		NgnEngine mEngine = NgnEngine.getInstance();
		INgnConfigurationService mConfigurationService = mEngine.getConfigurationService();
		mConfigurationService.putString(
				NgnConfigurationEntry.IDENTITY_IMPI, usuario);
		mConfigurationService.putString(
				NgnConfigurationEntry.IDENTITY_IMPU, publicIdentity);
		mConfigurationService.putString(
				NgnConfigurationEntry.IDENTITY_PASSWORD, senha);
		mConfigurationService.putString(
				NgnConfigurationEntry.NETWORK_PCSCF_HOST, proxyHost);
		mConfigurationService.putInt(
				NgnConfigurationEntry.NETWORK_PCSCF_PORT, port);
		mConfigurationService.putString(
				NgnConfigurationEntry.NETWORK_REALM, realm);
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
	public void stopAudioCall() {
		if (avSession != null)
			avSession.hangUpCall();
	}
	
	/**
	 * 
	 * @param incommingSession
	 */
	public void receiveAudioCall(NgnAVSession incommingSession){
		this.avSession = incommingSession;
	}
	
	/**
	 * 
	 */
	public void acceptCall() {
		if (avSession != null)
			avSession.acceptCall();
	}
	
	public void updateObservers(String mensagem){
		setChanged();
		notifyObservers(mensagem);
	}

	public String getRealm() {
		return realm;
	}

	public void setRealm(String realm) {
		this.realm = realm;
	}

	public String getDomain() {
		return domain;
	}

	public void setDomain(String domain) {
		this.domain = domain;
	}

	public String getProxyHost() {
		return proxyHost;
	}

	public void setProxyHost(String proxyHost) {
		this.proxyHost = proxyHost;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}
	
	
}
