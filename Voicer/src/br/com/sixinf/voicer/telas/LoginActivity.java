/**
 * 
 */
package br.com.sixinf.voicer.telas;

import org.doubango.ngn.events.NgnInviteEventArgs;
import org.doubango.ngn.events.NgnRegistrationEventArgs;

import br.com.sixinf.voicer.R;
import br.com.sixinf.voicer.receivers.RegistrationBroadcastReceiver;
import br.com.sixinf.voicer.sip.VoicerFacade;
import android.app.Activity;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

/**
 * @author maicon
 *
 */
public class LoginActivity extends Activity implements IUpdateStatus {
	
	private EditText txtUsuario;
	private EditText txtSenha;
	private TextView txtStatus;
	private RegistrationBroadcastReceiver regBroadcastReceiver;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_login);
		
		// Inicializa a fachada e a engine do Audio
		VoicerFacade.getInstance().setMainActivity(this);
		VoicerFacade.getInstance().startSipService();
		
		txtUsuario = (EditText) findViewById(R.id.login_txtUsuario);
		txtSenha = (EditText) findViewById(R.id.login_txtSenha);
		txtStatus = (TextView) findViewById(R.id.login_lblStatus);
		
		Button btnLogar = (Button) findViewById(R.id.login_btnLogar);
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
		int id = item.getItemId();
		
		if (id == R.id.login_mnuSetup) {
			Intent it = new Intent(this, SetupActivity.class);			
			startActivity(it);
			return true;
		}
		
		return super.onOptionsItemSelected(item);
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (regBroadcastReceiver != null)
			unregisterReceiver(regBroadcastReceiver);
	}

	@Override
	public void updateStatus(final String mensagem) {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				txtStatus.setText(mensagem);
			}
		});
	}

}
