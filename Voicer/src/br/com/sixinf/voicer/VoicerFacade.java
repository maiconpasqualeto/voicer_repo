/**
 * 
 */
package br.com.sixinf.voicer;

import java.text.ParseException;
import java.util.Observable;
import java.util.Observer;

import android.app.PendingIntent;
import android.content.Intent;
import android.net.sip.SipAudioCall;
import android.net.sip.SipException;
import android.net.sip.SipManager;
import android.net.sip.SipProfile;
import android.net.sip.SipRegistrationListener;

/**
 * @author maicon
 *
 */
public class VoicerFacade implements Observer {
	
	private static VoicerFacade facade;
	
	private SipManager sipManager;
	private SipProfile sipProfile;
	private String usuario;
	private String senha;
	private String usuarioPeer;
	private SipAudioCall chamadaRecebida;
	private SipAudioCall chamadaEncaminhada;
	private SetupActivity mainActivity;
	private VoicerService voicerService;
	
	public static VoicerFacade getInstance() {
		if (facade == null)
			facade = new VoicerFacade();
		
		return facade;
	}
	
	/**
	 * 
	 */
	public void startSipService() {
		if (mainActivity == null)
			throw new UnsupportedOperationException("Main activity must be set");
				
		voicerService = new VoicerService(mainActivity);
		voicerService.addObserver(this);
		voicerService.startEngine();
	}

	/**
	 * 
	 * @param context
	 * @throws ParseException 
	 * @throws SipException 
	 */
	public void createSipManager(final VoicerActivity activity) throws ParseException, SipException {
		
		sipManager = SipManager.newInstance(activity);
		SipProfile.Builder builder = new SipProfile.Builder(usuario, "sip.linphone.org");
		builder.setPassword(senha);
		sipProfile = builder.build();
		
		Intent intent = new Intent();
		intent.setAction("android.SipDemo.INCOMING_CALL");
		PendingIntent pendingIntent = PendingIntent.getBroadcast(activity, 0, intent, Intent.FILL_IN_DATA);
		sipManager.open(sipProfile, pendingIntent, null);
		
		sipManager.setRegistrationListener(sipProfile.getUriString(), new SipRegistrationListener() {
			
			@Override
			public void onRegistrationFailed(String localProfileUri, int errorCode,
					String errorMessage) {
				activity.updateStatus("Falha ao registrar, verifique as configurações. " + errorMessage);					
			}
			
			@Override
			public void onRegistrationDone(String localProfileUri, long expiryTime) {
				activity.updateStatus("Pronto");
			}
			
			@Override
			public void onRegistering(String localProfileUri) {
				activity.updateStatus("Registrando no servidor SIP...");
			}
		});
				
		
		
	}

	/**
	 * 
	 */
	public void fazerChamada(final VoicerActivity activity, String nomePeer) 
			throws SipException{
		
		if (chamadaEncaminhada != null &&
				chamadaEncaminhada.isInCall())
			return;
		
		SipAudioCall.Listener listener = new SipAudioCall.Listener() {
			
			@Override
			public void onCallEstablished(SipAudioCall call) {
				call.setSpeakerMode(true);
				call.startAudio();
				activity.updateStatus("Chamada realizada em andamento...");
			}

			@Override
			public void onCallEnded(SipAudioCall call) {
				activity.updateStatus("Pronto");
			}
			
			@Override
			public void onError(SipAudioCall call, int errorCode,
					String errorMessage) {
				activity.updateStatus("ERRO - " + errorMessage);
			}
		};
				
		chamadaEncaminhada = sipManager.makeAudioCall(
				sipProfile.getUriString(), "sip:" + nomePeer + "@sip.linphone.org", listener, 30);
	}
	
	/**
	 * @throws SipException 
	 * 
	 */
	public void closeLocalProfile() throws SipException {
		if (chamadaRecebida != null)
			chamadaRecebida.close();
				
		if (chamadaEncaminhada != null)
			chamadaEncaminhada.close();
		
		if (sipManager != null)
			sipManager.close(sipProfile.getUriString());
        	
	}
	
	/**
	 * 
	 * @throws SipException
	 */
	public void encerraTodasChamada() throws SipException {
		if (chamadaRecebida != null &&
				chamadaRecebida.isInCall()) {
			chamadaRecebida.endCall();
		} else 
			if (chamadaEncaminhada != null &&
				chamadaEncaminhada.isInCall()) {
			chamadaEncaminhada.endCall();				
		}
		
	}
	
	public SipManager getSipManager() {
		return sipManager;
	}

	public void setSipManager(SipManager sipManager) {
		this.sipManager = sipManager;
	}

	public SipProfile getSipProfile() {
		return sipProfile;
	}

	public void setSipProfile(SipProfile sipProfile) {
		this.sipProfile = sipProfile;
	}

	public String getUsuario() {
		return usuario;
	}

	public void setUsuario(String usuario) {
		this.usuario = usuario;
	}

	public String getSenha() {
		return senha;
	}

	public void setSenha(String senha) {
		this.senha = senha;
	}

	public String getUsuarioPeer() {
		return usuarioPeer;
	}

	public void setUsuarioPeer(String usuarioPeer) {
		this.usuarioPeer = usuarioPeer;
	}

	public SipAudioCall getChamadaRecebida() {
		return chamadaRecebida;
	}

	public void setChamadaRecebida(SipAudioCall chamadaRecebida) {
		this.chamadaRecebida = chamadaRecebida;
	}

	public SipAudioCall getChamadaEncaminhada() {
		return chamadaEncaminhada;
	}

	public void setChamadaEncaminhada(SipAudioCall chamadaEncaminhada) {
		this.chamadaEncaminhada = chamadaEncaminhada;
	}

	public SetupActivity getMainActivity() {
		return mainActivity;
	}

	public void setMainActivity(SetupActivity mainActivity) {
		this.mainActivity = mainActivity;
	}

	public VoicerService getVoicerService() {
		return voicerService;
	}

	public void setVoicerService(VoicerService voicerService) {
		this.voicerService = voicerService;
	}

	/**
	 * Observer
	 */
	@Override
	public void update(Observable observable, Object data) {
		if (data instanceof StatusRegistroSIP) {
			mainActivity.updateStatusRegistroSIP((StatusRegistroSIP) data);
		}
			
	}
	
	/**
	 * 
	 * @return
	 */
	public void registerNoServidorSIP() {
		voicerService.setupConfig(usuario, senha);
		voicerService.sipRegister();
		
	}
	
	/**
	 * 
	 */
	public void unregisterServicoSIP() {
		voicerService.sipUnregister();
	}

	/**
	 * 
	 */
	public void fazerChamadaAudio(final VoicerActivity activity, String nomePeer) {
		String sipUri = "sip:" + nomePeer + "@iptel.org";
		voicerService.makeAudioCall(sipUri);
	}
	
	/**
	 * 
	 */
	public void encerrarChamadaAudio() {
		voicerService.stopAudioCall();
	}

	/*
	 * 
	 */
	public void aceitarChamada() {
		voicerService.acceptCall();
	}
}
