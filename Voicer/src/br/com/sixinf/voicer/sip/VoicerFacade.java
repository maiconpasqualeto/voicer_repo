/**
 * 
 */
package br.com.sixinf.voicer.sip;

import java.util.Observable;
import java.util.Observer;

import android.app.Activity;
import android.net.sip.SipAudioCall;
import android.net.sip.SipManager;
import android.net.sip.SipProfile;
import android.view.View;
import br.com.sixinf.voicer.ObserverData;
import br.com.sixinf.voicer.Voicer;
import br.com.sixinf.voicer.persistencia.Config;
import br.com.sixinf.voicer.persistencia.VoicerDAO;
import br.com.sixinf.voicer.telas.IUpdateStatus;

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
	private VoicerService voicerService;
	private VoicerDAO dao;
	
	public static VoicerFacade getInstance() {
		if (facade == null)
			facade = new VoicerFacade();
		
		return facade;
	}
	
	public VoicerFacade() {
		this.dao = new VoicerDAO(Voicer.getAppContext());
	}
	
	/**
	 * 
	 */
	public void createVoicerService(Activity mainActivity) {
		if (mainActivity == null)
			throw new UnsupportedOperationException("Main activity must be set");
		
		Config conf = VoicerDAO.getInstance(mainActivity).buscaConfiguracao();
		
		voicerService = new VoicerService(mainActivity, conf);
		
		this.mainActivity = mainActivity;
		
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
			((IUpdateStatus)mainActivity).updateStatus((ObserverData) data);
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
	public void encerrarChamadaAudioVideo() {
		voicerService.stopAudioVideoCall();
	}

	/*
	 * 
	 */
	public void aceitarChamada() {
		voicerService.acceptCall();
	}
	
	/**
	 * 
	 * @param usuario
	 * @param senha
	 * @param realm
	 * @param domain
	 * @param host
	 * @param porta
	 */
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
		
		dao.alterarConfiguracao(conf);
		
		voicerService.setConf(conf);
	}
	
	/**
	 * 
	 */
	public Config buscarConfiguracao() {
		return dao.buscaConfiguracao();
	}
	
	/**
	 * 
	 */
	public void fazerChamadaVideo(String nomePeer) {
		String sipUri = "sip:" + nomePeer + "@openjsip.net";
		voicerService.makeVideoCall(sipUri);
	}
	
	/**
	 * 
	 * @return
	 */
	public View startVideoRemoto(){
		return voicerService.startVideoConsumerPreview(); 
	}

	/**
	 * 
	 * @return
	 */
	public View startVideoLocal() {		
		return voicerService.startVideoProducerPreview();
	}
	
}
