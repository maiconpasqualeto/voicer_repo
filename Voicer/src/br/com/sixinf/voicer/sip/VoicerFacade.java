/**
 * 
 */
package br.com.sixinf.voicer.sip;

import java.util.Observable;
import java.util.Observer;

import android.app.Activity;
import android.content.Context;
import br.com.sixinf.voicer.persistencia.Config;
import br.com.sixinf.voicer.persistencia.VoicerDAO;
import br.com.sixinf.voicer.telas.IUpdateStatus;
import br.com.sixinf.voicer.telas.VoicerActivity;
import android.net.sip.SipAudioCall;
import android.net.sip.SipManager;
import android.net.sip.SipProfile;

/**
 * @author maicon
 *
 */
public class VoicerFacade implements Observer {
	
	private static VoicerFacade facade;
	
	private SipManager sipManager;
	private SipProfile sipProfile;
	private String usuarioPeer;
	private SipAudioCall chamadaRecebida;
	private SipAudioCall chamadaEncaminhada;
	private Activity mainActivity;
	private Context context;
	private VoicerService voicerService;
	private VoicerDAO dao;
	
	public static VoicerFacade getInstance(Context context) {
		if (facade == null)
			facade = new VoicerFacade(context);
		
		return facade;
	}
	
	public VoicerFacade(Context context) {
		this.context = context;
		this.dao = new VoicerDAO(context);
	}
	
	/**
	 * 
	 */
	public void createVoicerService(Activity mainActivity) {
		if (mainActivity == null)
			throw new UnsupportedOperationException("Main activity must be set");
		
		Config conf = VoicerDAO.getInstance(mainActivity).buscaConfiguracao();
		
		voicerService = new VoicerService(mainActivity, conf);
		
	}
	
	/**
	 * 
	 */
	public void startSipService() {
		
		voicerService.addObserver(this);
		voicerService.startEngine();
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

	public Activity getMainActivity() {
		return mainActivity;
	}

	public void setMainActivity(Activity mainActivity) {
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
		if (observable instanceof VoicerService) {
			((IUpdateStatus)mainActivity).updateStatus(data.toString());
		}
			
	}
	
	/**
	 * 
	 * @return
	 */
	public void registerNoServidorSIP() {
		voicerService.setupConfig();
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
	public void fazerChamadaAudio(String nomePeer) {
		String sipUri = "sip:" + nomePeer + "@openjsip.net";
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
	
	public void atualizaConfiguracao(
			String usuario, 
			String senha, 
			String realm, 
			String domain,
			String host, 
			Integer porta) {
		
		Config conf = dao.buscaConfiguracao();
		conf.setUsuario(usuario);
		conf.setSenha(senha);
		conf.setRealm(realm);
		conf.setDomain(domain);
		conf.setHost(host);
		conf.setPorta(porta);
		
		voicerService.setConf(conf);
	}
}
