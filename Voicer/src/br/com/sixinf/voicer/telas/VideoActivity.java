package br.com.sixinf.voicer.telas;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.LinearLayout.LayoutParams;
import br.com.sixinf.voicer.ObserverData;
import br.com.sixinf.voicer.R;
import br.com.sixinf.voicer.ObserverData.EventType;
import br.com.sixinf.voicer.sip.VoicerFacade;

public class VideoActivity extends ActionBarActivity implements IUpdateStatus {
	
	private TextView txtRamal;
	private TextView lblStatus;
	private Button btnEncerra;
	private Button btnAceita;
	private Button btnRejeita;
	private String numeroRamal;
	private boolean chamadaRealizada;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_video);
		
		Intent it = getIntent();
		numeroRamal = it.getStringExtra("ramal");
		chamadaRealizada = it.getBooleanExtra("chamadaRealizada", true);
		
		txtRamal = (TextView) findViewById(R.id.video_lblRamal);
		txtRamal.setText("Ramal: " + numeroRamal);
		
		lblStatus = (TextView) findViewById(R.id.video_lblStatus);
		
		btnEncerra = new Button(getApplicationContext());
		LinearLayout.LayoutParams layoutParams = 
				new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT,	
								 LayoutParams.WRAP_CONTENT);
		layoutParams.gravity = Gravity.BOTTOM;
		btnEncerra.setLayoutParams(layoutParams);
		btnEncerra.setText("Encerrar");
		btnEncerra.setTextSize(TypedValue.COMPLEX_UNIT_SP, 35);
		btnEncerra.setTypeface(null, Typeface.BOLD);
		btnEncerra.setTextColor(Color.WHITE);
		btnEncerra.setGravity(Gravity.CENTER);
		btnEncerra.setPadding(0, 30, 0, 30);
		btnEncerra.setBackgroundResource(R.xml.custom_botao_vermelho);		
		btnEncerra.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				VoicerFacade.getInstance().encerrarChamadaAudioVideo();
				
			}
		});
		
		final LinearLayout buttonContainer = (LinearLayout) findViewById(R.id.video_buttonsContainer);
			
		if (chamadaRealizada) {
			
			buttonContainer.addView(btnEncerra);
			
			VoicerFacade.getInstance().fazerChamadaVideo(numeroRamal);
			
		} else {
		
			LinearLayout.LayoutParams paramsChamRec = 
					new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT,	
									 LayoutParams.WRAP_CONTENT);
			paramsChamRec.gravity = Gravity.BOTTOM;
			paramsChamRec.weight = 1;
			btnAceita = new Button(getApplicationContext());
			btnAceita.setLayoutParams(paramsChamRec);
			btnAceita.setText("Aceita");
			btnAceita.setTextSize(TypedValue.COMPLEX_UNIT_SP, 35);
			btnAceita.setTypeface(null, Typeface.BOLD);
			btnAceita.setTextColor(Color.WHITE);
			btnAceita.setGravity(Gravity.CENTER);
			btnAceita.setPadding(0, 30, 0, 30);
			btnAceita.setBackgroundResource(R.xml.custom_botao_verde);		
			btnAceita.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					buttonContainer.removeAllViews();
					buttonContainer.addView(btnEncerra);
					
					VoicerFacade.getInstance().aceitarChamada();
				}
			});
			
			btnRejeita = new Button(getApplicationContext());
			btnRejeita.setLayoutParams(paramsChamRec);
			btnRejeita.setText("Rejeita");
			btnRejeita.setTextSize(TypedValue.COMPLEX_UNIT_SP, 35);
			btnRejeita.setTypeface(null, Typeface.BOLD);
			btnRejeita.setTextColor(Color.WHITE);
			btnRejeita.setGravity(Gravity.CENTER);
			btnRejeita.setPadding(0, 30, 0, 30);
			btnRejeita.setBackgroundResource(R.xml.custom_botao_vermelho);		
			btnRejeita.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					VoicerFacade.getInstance().encerrarChamadaAudioVideo();
					
				}
			});
			
			buttonContainer.addView(btnAceita);
			buttonContainer.addView(btnRejeita);
			
		}
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		
		VoicerFacade.getInstance().setMainActivity(this);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		//getMenuInflater().inflate(R.menu.video, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		/*int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}*/
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void updateStatus(final ObserverData observerData) {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				
				if (observerData.getEventType().equals(EventType.EVENT_INVITE)) {
					if ( observerData.getSipMessage().contains("Call Terminated") ||
							observerData.getSipMessage().contains("Call Cancelled") || 
							observerData.getSipMessage().contains("Request cancelled") ||
							observerData.getSipMessage().contains("Decline")) {
						
						finish();
					}
					
					 lblStatus.setText(
						observerData.getEventMessage() + 
						(observerData.getSipMessage() != null ? ( " - " + observerData.getSipMessage()) : ""));
				}
			}
		});
		
	}
}
