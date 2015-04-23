package br.com.sixinf.voicer.telas;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import br.com.sixinf.voicer.ObserverData;
import br.com.sixinf.voicer.R;
import br.com.sixinf.voicer.persistencia.Config;
import br.com.sixinf.voicer.sip.VoicerFacade;

public class SetupActivity extends ActionBarActivity implements IUpdateStatus {
	
	private EditText txtUsuario;
	private EditText txtSenha;
	private EditText txtRealm;
	private EditText txtDominio;	
	private EditText txtHost;
	private EditText txtPorta;
		
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setup);
		
		txtUsuario = (EditText) findViewById(R.id.setup_txtUsuario);
		txtSenha = (EditText) findViewById(R.id.setup_txtSenha);
		txtRealm = (EditText) findViewById(R.id.setup_txtRealm);
		txtDominio = (EditText) findViewById(R.id.setup_txtDomain);
		txtHost = (EditText) findViewById(R.id.setup_txtHost);
		txtPorta = (EditText) findViewById(R.id.setup_txtPorta);
		
		Config c = VoicerFacade.getInstance().buscarConfiguracao();
		
		txtUsuario.setText(c.getUsuario());
		txtSenha.setText(c.getSenha());
		txtRealm.setText(c.getRealm());
		txtDominio.setText(c.getDomain());
		txtHost.setText(c.getHost());
		txtPorta.setText(c.getPorta().toString());
		
		Button btnSalvar = (Button) findViewById(R.id.setup_btnSalvar);
		btnSalvar.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				VoicerFacade f = VoicerFacade.getInstance();
				Integer porta = Integer.valueOf(txtPorta.getText().toString());
				f.atualizaConfiguracao(
						txtUsuario.getText().toString(),
						txtSenha.getText().toString(), 
						txtRealm.getText().toString(), 
						txtDominio.getText().toString(), 
						txtHost.getText().toString(), 
						porta);
				finish();
			}
		});
		
		Button btnVoltar = (Button) findViewById(R.id.setup_btnVoltar);
		btnVoltar.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
				
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		//getMenuInflater().inflate(R.menu.setup, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		/*int id = item.getItemId();
		if (id == R.id.login_mnuSetup) {
			
			
			return true;
		}*/
		return super.onOptionsItemSelected(item);
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		
	}

	@Override
	public void updateStatus(final ObserverData observerData) {
		
	}
}
