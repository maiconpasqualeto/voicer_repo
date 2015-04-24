/**
 * 
 */
package br.com.sixinf.voicer.telas;

import br.com.sixinf.voicer.R;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.WindowManager;

/**
 * @author maicon
 *
 */
public class ChamadaActivity extends ActionBarActivity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_chamada);
		
		getWindow().setSoftInputMode(
		    WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
		);
	}

}
