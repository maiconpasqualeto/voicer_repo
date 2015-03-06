package br.com.sixinf.voicer;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class SetupActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setup);
		
		final EditText txtUsuario = (EditText) findViewById(R.id.txtUsuario);
		final EditText txtSenha = (EditText) findViewById(R.id.txtSenha);
		Button btnLogar = (Button) findViewById(R.id.btnLogar);
		btnLogar.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				/*VoicerFacade.getInstance().setUsuario(txtUsuario.getText().toString());
				VoicerFacade.getInstance().setSenha(txtSenha.getText().toString());
				Intent i = new Intent(SetupActivity.this, VoicerActivity.class);
				startActivity(i);*/
				VoicerService s = new VoicerService(SetupActivity.this);
				s.sipRegister();
			}
		});
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
}
