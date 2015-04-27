/**
 * 
 */
package br.com.sixinf.voicer.telas;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import br.com.sixinf.voicer.ObserverData;
import br.com.sixinf.voicer.ObserverData.EventType;
import br.com.sixinf.voicer.R;
import br.com.sixinf.voicer.sip.VoicerFacade;

/**
 * @author maicon
 *
 */
public class VozActivity extends ActionBarActivity implements IUpdateStatus {
	
	private TextView txtRamal;
	private TextView lblStatus;
	private Button btnEncerra;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_voz);
		
		Intent it = getIntent();
				
		txtRamal = (TextView) findViewById(R.id.voz_lblRamal);
		txtRamal.setText("Ramal: " + it.getStringExtra("ramal"));
		
		lblStatus = (TextView) findViewById(R.id.voz_lblStatus);
		
		btnEncerra = (Button) findViewById(R.id.voz_btnEncerrar);
		btnEncerra.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				VoicerFacade.getInstance().encerrarChamadaAudio();
				
				finish();
			}
		});
		
		VoicerFacade.getInstance().setMainActivity(this);
		
		VoicerFacade.getInstance().fazerChamadaAudio(txtRamal.getText().toString());
	}
	
	@Override
	public void updateStatus(final ObserverData observerData) {
		
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				
				if (observerData.getEventType().equals(EventType.EVENT_INVITE)) {
					 lblStatus.setText(
						observerData.getEventMessage() + 
						(observerData.getSipMessage() != null ? ( " - " + observerData.getSipMessage()) : ""));
				}
			}
		});
	}

}
