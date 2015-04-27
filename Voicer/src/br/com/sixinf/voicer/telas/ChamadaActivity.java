/**
 * 
 */
package br.com.sixinf.voicer.telas;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import br.com.sixinf.voicer.R;
import br.com.sixinf.voicer.Voicer;

/**
 * @author maicon
 *
 */
public class ChamadaActivity extends ActionBarActivity {
	
	private Button btn1;
	private Button btn2;
	private Button btn3;
	private Button btn4;
	private Button btn5;
	private Button btn6;
	private Button btn7;
	private Button btn8;
	private Button btn9;
	private Button btn0;
	private Button btnVoz;
	private Button btnVideo;
	private Button btnApaga;
	private EditText txtNumChamar;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_chamada);
		
		getWindow().setSoftInputMode(
		    WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
		);
		
		btn1 = (Button) findViewById(R.id.chamada_btn1);
		btn1.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				appendCharacter("1");
			}
		});
		btn2 = (Button) findViewById(R.id.chamada_btn2);
		btn2.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				appendCharacter("2");
			}
		});
		btn3 = (Button) findViewById(R.id.chamada_btn3);
		btn3.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				appendCharacter("3");
			}
		});
		btn4 = (Button) findViewById(R.id.chamada_btn4);
		btn4.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				appendCharacter("4");
			}
		});
		btn5 = (Button) findViewById(R.id.chamada_btn5);
		btn5.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				appendCharacter("5");
			}
		});
		btn6 = (Button) findViewById(R.id.chamada_btn6);
		btn6.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				appendCharacter("6");
			}
		});
		btn7 = (Button) findViewById(R.id.chamada_btn7);
		btn7.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				appendCharacter("7");
			}
		});
		btn8 = (Button) findViewById(R.id.chamada_btn8);
		btn8.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				appendCharacter("8");
			}
		});
		btn9 = (Button) findViewById(R.id.chamada_btn9);
		btn9.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				appendCharacter("9");
			}
		});
		btn0 = (Button) findViewById(R.id.chamada_btn0);
		btn0.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				appendCharacter("0");
			}
		});
		btnVoz = (Button) findViewById(R.id.chamada_btnVoz);
		btnVoz.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent it = new Intent(Voicer.getAppContext(), VozActivity.class);
				it.putExtra("ramal", txtNumChamar.getText().toString());
				startActivity(it);
			}
		});
		btnVideo = (Button) findViewById(R.id.chamada_btnVideo);
		btnVideo.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				
			}
		});
		btnApaga = (Button) findViewById(R.id.chamada_btnApaga);
		btnApaga.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				int start = txtNumChamar.getSelectionStart();
				int end = txtNumChamar.getSelectionEnd();				
				if (start > 0)
					txtNumChamar.getText().delete(start - 1, end);
			}
		});
		
		txtNumChamar = (EditText) findViewById(R.id.chamada_txtNumChamar);
		txtNumChamar.setTextIsSelectable(true);
		
	}

	/**
	 * 
	 * @param string
	 */
	private void appendCharacter(String string) {
		int start = txtNumChamar.getSelectionStart();		
		txtNumChamar.getText().insert(start, string);
	}

}
