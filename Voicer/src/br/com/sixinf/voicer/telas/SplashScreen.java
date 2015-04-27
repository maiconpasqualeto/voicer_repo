package br.com.sixinf.voicer.telas;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import br.com.sixinf.voicer.R;
import br.com.sixinf.voicer.Voicer;
import br.com.sixinf.voicer.sip.VoicerFacade;

public class SplashScreen extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash_screen);
		
		// Inicializa a fachada e a engine do Audio
		VoicerFacade.getInstance().createVoicerService(this);
		VoicerFacade.getInstance().startSipService();
		
		Handler h = new Handler();
        h.postDelayed(new Runnable() {
            public void run() {
                Intent it = new Intent(Voicer.getAppContext(), VoicerActivity.class);
                startActivity(it);
            }

        }, 3000);
	}
}
