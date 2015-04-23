package br.com.sixinf.voicer.telas;

import org.doubango.ngn.events.NgnInviteEventArgs;
import org.doubango.ngn.events.NgnRegistrationEventArgs;
import org.doubango.ngn.events.NgnRegistrationEventTypes;

import android.app.Activity;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import br.com.sixinf.voicer.ObserverData;
import br.com.sixinf.voicer.ObserverData.EventType;
import br.com.sixinf.voicer.R;
import br.com.sixinf.voicer.receivers.RegistrationBroadcastReceiver;
import br.com.sixinf.voicer.sip.VoicerFacade;

public class VoicerActivity extends Activity implements IUpdateStatus {
	
	private TextView lblStatus;
	private TextView lblRamal;
	private Button btnChamar;
	private Button btnContatos;
	private RegistrationBroadcastReceiver regBroadcastReceiver;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_voicer);
		
		btnChamar = (Button) findViewById(R.id.voicer_btnChamar);
		btnChamar.setVisibility(View.INVISIBLE);
		
		btnChamar.setOnClickListener(new View.OnClickListener() {				
			@Override
			public void onClick(View v) {
				/*String nomePeer = txtNomePeer.getText().toString();
				if (nomePeer.isEmpty())
					Toast.makeText(VoicerActivity.this, 
							"Nome para chamar não pode ser vazio", Toast.LENGTH_SHORT).show();
				else 
					VoicerFacade.getInstance(VoicerActivity.this).fazerChamadaAudio(nomePeer);*/
			}
		});
		
		btnContatos = (Button) findViewById(R.id.voicer_btnContatos);
		btnContatos.setVisibility(View.INVISIBLE);
		btnContatos.setOnClickListener(new View.OnClickListener() {				
			@Override
			public void onClick(View v) {
				
			}
		});
				
		lblStatus = (TextView) findViewById(R.id.voicer_lblStatus);
		
		lblRamal = (TextView) findViewById(R.id.voicer_lblRamal);
		lblRamal.setText("Desconectado");
		
		// Inicializa a fachada e a engine do Audio
		VoicerFacade.getInstance().createVoicerService(this);
		VoicerFacade.getInstance().startSipService();
		
		VoicerFacade.getInstance().registerNoServidorSIP();
		
		// Register broadcast receivers
		regBroadcastReceiver = new RegistrationBroadcastReceiver(
				VoicerFacade.getInstance().getVoicerService());
		final IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(NgnRegistrationEventArgs.ACTION_REGISTRATION_EVENT);
		intentFilter.addAction(NgnInviteEventArgs.ACTION_INVITE_EVENT);
		registerReceiver(regBroadcastReceiver, intentFilter);
		
	}
	

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.voicer, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == R.id.login_mnuSetup) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		
		VoicerFacade.getInstance().unregisterServicoSIP();
		
		if (regBroadcastReceiver != null)
			unregisterReceiver(regBroadcastReceiver);
	}
		
	/**
	 * 
	 * @param status
	 */
	@Override
	public void updateStatus(final ObserverData observerData) {
		
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				String strRamal = "Desconectado";
				if (observerData.getEventType().equals(EventType.EVENT_REGISTRATION) &&
						observerData.getRegisterState().equals(NgnRegistrationEventTypes.REGISTRATION_OK)) {
					 strRamal = "Ramal: " + 
						VoicerFacade.getInstance().getVoicerService().getConf().getUsuario();
					 
					 btnChamar.setVisibility(View.VISIBLE);
					 btnContatos.setVisibility(View.VISIBLE);			 
				}
				
				lblStatus.setText(observerData.getEventMessage());
				lblRamal.setText(strRamal);
			}
		});
	}
				
}
