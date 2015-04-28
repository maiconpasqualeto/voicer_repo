/**
 * 
 */
package br.com.sixinf.voicer.telas;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
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
	private Button btnAceita;
	private Button btnRejeita;
	private String numeroRamal;
	private boolean chamadaRealizada;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_voz);
		
		Intent it = getIntent();
		numeroRamal = it.getStringExtra("ramal");
		chamadaRealizada = it.getBooleanExtra("chamadaRealizada", true);
		
		txtRamal = (TextView) findViewById(R.id.voz_lblRamal);
		txtRamal.setText("Ramal: " + numeroRamal);
		
		lblStatus = (TextView) findViewById(R.id.voz_lblStatus);
		
		VoicerFacade.getInstance().setMainActivity(this);
		
		if (chamadaRealizada) {
				
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
					VoicerFacade.getInstance().encerrarChamadaAudio();
					
					finish();
				}
			});
			
			LinearLayout buttonContainer = (LinearLayout) findViewById(R.id.voz_buttonsContainer);
			buttonContainer.addView(btnEncerra);
			
			VoicerFacade.getInstance().fazerChamadaAudio(numeroRamal);
			
		} else {
		
			LinearLayout.LayoutParams layoutParams = 
					new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT,	
									 LayoutParams.WRAP_CONTENT);
			layoutParams.gravity = Gravity.BOTTOM;
			layoutParams.weight = 1;
			btnAceita = new Button(getApplicationContext());
			btnAceita.setLayoutParams(layoutParams);
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
					VoicerFacade.getInstance().aceitarChamada();
				}
			});
			
			btnRejeita = new Button(getApplicationContext());
			btnRejeita.setLayoutParams(layoutParams);
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
					VoicerFacade.getInstance().encerrarChamadaAudio();
					
					finish();
				}
			});
			
			
			LinearLayout buttonContainer = (LinearLayout) findViewById(R.id.voz_buttonsContainer);
			buttonContainer.addView(btnAceita);
			buttonContainer.addView(btnRejeita);
			
		}
		
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
