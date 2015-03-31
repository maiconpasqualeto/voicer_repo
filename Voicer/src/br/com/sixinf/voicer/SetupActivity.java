package br.com.sixinf.voicer;

import org.doubango.ngn.events.NgnInviteEventArgs;
import org.doubango.ngn.events.NgnRegistrationEventArgs;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import br.com.sixinf.voicer.receivers.RegistrationBroadcastReceiver;

public class SetupActivity extends Activity {
	
	private EditText txtUsuario;
	private EditText txtSenha;
	private RegistrationBroadcastReceiver regBroadcastReceiver;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setup);
		
		// Inicializa a fachada e a engine do Audio
		VoicerFacade.getInstance().setMainActivity(this);
		VoicerFacade.getInstance().startSipService();
		
		txtUsuario = (EditText) findViewById(R.id.txtUsuario);
		txtSenha = (EditText) findViewById(R.id.txtSenha);
		Button btnLogar = (Button) findViewById(R.id.btnLogar);
		btnLogar.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				VoicerFacade.getInstance().setUsuario(txtUsuario.getText().toString());
				VoicerFacade.getInstance().setSenha(txtSenha.getText().toString());
				VoicerFacade.getInstance().registerNoServidorSIP();
			}
		});
		
		// Register broadcast receivers
		regBroadcastReceiver = new RegistrationBroadcastReceiver(VoicerFacade.getInstance().getVoicerService());
		final IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(NgnRegistrationEventArgs.ACTION_REGISTRATION_EVENT);
		intentFilter.addAction(NgnInviteEventArgs.ACTION_INVITE_EVENT);
		registerReceiver(regBroadcastReceiver, intentFilter);
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.setup, menu);
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

	/**
	 * 
	 * @param data
	 */
	public void updateStatusRegistroSIP(final StatusRegistroSIP status) {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				txtUsuario.setText(status.name());
			}
		});
		
		if (status.equals(StatusRegistroSIP.REGISTRADO)) {
			Intent i = new Intent(SetupActivity.this, VoicerActivity.class);
			startActivity(i);
		}
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (regBroadcastReceiver != null)
			unregisterReceiver(regBroadcastReceiver);
	}
}
