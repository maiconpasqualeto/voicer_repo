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
		SipProfile.Builder builder = new SipProfile.Builder("sixinf", "sip.linphone.org");
		builder.setPassword("mariana123");
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
	
}
