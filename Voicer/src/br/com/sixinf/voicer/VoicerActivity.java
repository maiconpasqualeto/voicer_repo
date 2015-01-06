package br.com.sixinf.voicer;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.sip.SipAudioCall;
import android.net.sip.SipException;
import android.net.sip.SipManager;
import android.net.sip.SipProfile;
import android.net.sip.SipRegistrationListener;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class VoicerActivity extends Activity {
	
	private SipAudioCall audioCall;
	
	private VoicerFacade facade;
	private TextView txtStatus;
	private VoicerChamadaRecebida chamadaRecebidaReceiver;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_voicer);
		
		if (facade == null)
			 facade = VoicerFacade.getInstance();
		
		Button btnChamar = (Button) findViewById(R.id.btnChamar);
		btnChamar.setOnClickListener(new View.OnClickListener() {				
			@Override
			public void onClick(View v) {
				fazerChamada();
			}
		});
		
		txtStatus = (TextView) findViewById(R.id.txtStatus);
		txtStatus.setText("Idle");
		
		// Registrar Receiver
		IntentFilter filter = new IntentFilter();
        filter.addAction("android.SipDemo.INCOMING_CALL");
        chamadaRecebidaReceiver = new VoicerChamadaRecebida();
        this.registerReceiver(chamadaRecebidaReceiver, filter);
		
        registrarServicoSIP();
		
	}
	
	/**
	 * 
	 */
	private void registrarServicoSIP() {
		
		try {
		
			facade.createSipManager(this);
			
			SipManager sipManager = facade.getSipManager();
			SipProfile sipProfile = facade.getSipProfile();
			
			Intent intent = new Intent();
			intent.setAction("android.SipDemo.INCOMING_CALL");
			PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent, Intent.FILL_IN_DATA);
			sipManager.open(sipProfile, pendingIntent, null);
			
			sipManager.setRegistrationListener(sipProfile.getUriString(), new SipRegistrationListener() {
				
				@Override
				public void onRegistrationFailed(String localProfileUri, int errorCode,
						String errorMessage) {
					updateStatus("Registration failed. " + errorMessage);					
					
				}
				
				@Override
				public void onRegistrationDone(String localProfileUri, long expiryTime) {
					updateStatus("Ready");
				}
				
				@Override
				public void onRegistering(String localProfileUri) {
					updateStatus("Registering with SIP Server...");
				}
			});
		
		} catch (Exception e) {
			Log.e("VOICER", "Erro ao criar SIP", e);
		}
	}

	private void fazerChamada() {
		try {
		
			SipAudioCall.Listener listener = new SipAudioCall.Listener() {
	
				@Override
				public void onCallEstablished(SipAudioCall call) {
					call.startAudio();
					call.setSpeakerMode(true);
				}
	
				@Override
				public void onCallEnded(SipAudioCall call) {
					updateStatus("Call ended");
				}
				
				@Override
				public void onError(SipAudioCall call, int errorCode,
						String errorMessage) {
					updateStatus(errorMessage);
				}
			};
			
			SipManager sipManager = facade.getSipManager();
			SipProfile sipProfile = facade.getSipProfile();
			
			audioCall = sipManager.makeAudioCall(sipProfile.getUriString(), "sip:maiconpas@sip.linphone.org", listener, 30);
			
		} catch (SipException e) {
			Log.e("VOICER", "Erro ao fazer chamada", e);
		}

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.voicer, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		
		if (audioCall != null)
			audioCall.close();
        
		closeLocalProfile();
		
		if (chamadaRecebidaReceiver != null) 
			unregisterReceiver(chamadaRecebidaReceiver);
	}
	
	/**
	 * 
	 */
	public void closeLocalProfile() {
		SipManager sipManager = facade.getSipManager();
		SipProfile sipProfile = facade.getSipProfile();
	    if (sipManager == null) {
	       return;
	    }
	    try {
	    	
	       if (sipProfile != null) {
	    	   sipManager.close(sipProfile.getUriString());
	       }
	       
	     } catch (Exception ee) {
	       Log.d("VOICER", "Failed to close local profile.", ee);
	     }
	}

	public SipAudioCall getAudioCall() {
		return audioCall;
	}

	public void setAudioCall(SipAudioCall audioCall) {
		this.audioCall = audioCall;
	}
	
	/**
	 * 
	 * @param status
	 */
	public void updateStatus(final String status) {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				VoicerActivity.this.txtStatus.setText(status);
			}
		});
	}
			
}
