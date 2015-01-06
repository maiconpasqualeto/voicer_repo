/**
 * 
 */
package br.com.sixinf.voicer;

import java.text.ParseException;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.sip.SipManager;
import android.net.sip.SipProfile;

/**
 * @author maicon
 *
 */
public class VoicerFacade {
	
	private static VoicerFacade facade;
	
	private SipManager sipManager;
	private SipProfile sipProfile;
	private String usuario;
	private String senha;
	private String usuarioPeer;
	
	public static VoicerFacade getInstance() {
		if (facade == null)
			facade = new VoicerFacade();
		
		return facade;
	}

	/**
	 * 
	 * @param context
	 * @throws ParseException 
	 */
	public void createSipManager(Context context) throws ParseException {
		sipManager = SipManager.newInstance(context);
		SipProfile.Builder builder = new SipProfile.Builder(usuario, "sip.linphone.org");
		builder.setPassword(senha);
		sipProfile = builder.build();
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
	
}
