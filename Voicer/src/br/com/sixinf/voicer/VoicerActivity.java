package br.com.sixinf.voicer;

import android.app.Activity;
import android.content.IntentFilter;
import android.net.sip.SipException;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class VoicerActivity extends Activity {
	
	private TextView txtStatus;
	private VoicerChamadaRecebida chamadaRecebidaReceiver;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_voicer);
		
		
		final EditText txtNomePeer = (EditText) findViewById(R.id.txtNomePeer);
		if (VoicerFacade.getInstance().getUsuario().equals("maiconpas"))
			txtNomePeer.setText("sixinf");
		else
			if (VoicerFacade.getInstance().getUsuario().equals("sixinf"))
				txtNomePeer.setText("maiconpas");
		
		Button btnChamar = (Button) findViewById(R.id.btnChamar);
		btnChamar.setOnClickListener(new View.OnClickListener() {				
			@Override
			public void onClick(View v) {
				String nomePeer = txtNomePeer.getText().toString();
				if (nomePeer.isEmpty())
					Toast.makeText(VoicerActivity.this, 
							"Nome para chamar não pode ser vazio", Toast.LENGTH_SHORT).show();
				else 
					fazerChamada(nomePeer);
			}
		});
		
		Button btnEncerrar = (Button) findViewById(R.id.btnEncerrar);
		btnEncerrar.setOnClickListener(new View.OnClickListener() {				
			@Override
			public void onClick(View v) {
				
				encerrarChamada();
				
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
		
			VoicerFacade.getInstance().createSipManager(this);
					
		} catch (Exception e) {
			Log.e("VOICER", "Erro ao criar SIP", e);
		}
	}

	/**
	 * 
	 * @param nomePeer
	 */
	private void fazerChamada(String nomePeer) {
		try {
		
			VoicerFacade.getInstance().fazerChamada(this, nomePeer);
			
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
		try {
			
			VoicerFacade.getInstance().closeLocalProfile();
			
		} catch (SipException e) {
			Log.e("VOICER", "Erro ao encerrar perfil", e);
		}
			
		if (chamadaRecebidaReceiver != null) 
			unregisterReceiver(chamadaRecebidaReceiver);
	}
	
	/**
	 * 
	 
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
	}*/
	
	/**
	 * 
	 * @param status
	 */
	public void updateStatus(final String status) {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				txtStatus.setText(status);
			}
		});
	}
	
	/**
	 * 
	 */
	public void encerrarChamada(){
		try {
			
			VoicerFacade.getInstance().encerraTodasChamada();
			
			updateStatus("Pronto");
			
		} catch (SipException e) {
			Log.e("VOICER", "Erro ao encerrar chamada", e);
		}
	}
			
}
