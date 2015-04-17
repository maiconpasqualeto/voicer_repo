package br.com.sixinf.voicer.telas;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import br.com.sixinf.voicer.R;
import br.com.sixinf.voicer.sip.VoicerFacade;

public class VoicerActivity extends Activity implements IUpdateStatus {
	
	private TextView txtStatus;
	
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
					VoicerFacade.getInstance().fazerChamadaAudio(VoicerActivity.this, nomePeer);
			}
		});
		
		Button btnEncerrar = (Button) findViewById(R.id.btnEncerrar);
		btnEncerrar.setOnClickListener(new View.OnClickListener() {				
			@Override
			public void onClick(View v) {
				
				VoicerFacade.getInstance().encerrarChamadaAudio();
				
			}
		});
		
		Button btnAceitar = (Button) findViewById(R.id.btnAceitar);
		btnAceitar.setOnClickListener(new View.OnClickListener() {				
			@Override
			public void onClick(View v) {
				
				VoicerFacade.getInstance().aceitarChamada();
				
			}
		});
		
		txtStatus = (TextView) findViewById(R.id.txtStatus);
		txtStatus.setText("Idle");
		
		VoicerFacade.getInstance().setMainActivity(this);
						
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
		
	}
		
	/**
	 * 
	 * @param status
	 */
	@Override
	public void updateStatus(final String status) {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				txtStatus.setText(status);
			}
		});
	}
				
}
