package br.com.sixinf.voicer.telas;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import br.com.sixinf.voicer.R;
import br.com.sixinf.voicer.sip.VoicerFacade;
import br.com.sixinf.voicer.sip.VoicerService;

public class SetupActivity extends Activity implements IUpdateStatus {
	
	private EditText txtRealm;
	private EditText txtDominio;	
	private EditText txtHost;
	private EditText txtPorta;
		
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setup);
		
		txtRealm = (EditText) findViewById(R.id.setup_txtRealm);
		txtDominio = (EditText) findViewById(R.id.setup_txtDomain);
		txtHost = (EditText) findViewById(R.id.setup_txtHost);
		txtPorta = (EditText) findViewById(R.id.setup_txtPorta);
		
		String realm = getIntent().getStringExtra("realm");
		String dominio = getIntent().getStringExtra("dominio");
		String host = getIntent().getStringExtra("host");
		Integer porta = getIntent().getIntExtra("porta", 0);
		
		txtRealm.setText(realm);
		txtDominio.setText(dominio);
		txtHost.setText(host);
		txtPorta.setText(porta.toString());
		
		Button btnSalvar = (Button) findViewById(R.id.setup_btnSalvar);
		btnSalvar.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				VoicerService vs = VoicerFacade.getInstance().getVoicerService();
				vs.setRealm(txtRealm.getText().toString());
				vs.setDomain(txtDominio.getText().toString());
				vs.setProxyHost(txtHost.getText().toString());
				Integer porta = Integer.valueOf(txtPorta.getText().toString());
				vs.setPort(porta);
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
	public void updateStatus(final String mensagem) {
		
	}
}
