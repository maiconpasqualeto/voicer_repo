package br.com.skylane.voicer;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.widget.FrameLayout;
import br.com.skylane.voicer.camera.CameraPreview;
import br.com.skylane.voicer.camera.CameraService;


public class HelloAndroidActivity extends Activity {
	
	private CameraPreview cp;
	
    /**
     * Called when the activity is first created.
     * @param savedInstanceState If the activity is being re-initialized after 
     * previously being shut down then this Bundle contains the data it most 
     * recently supplied in onSaveInstanceState(Bundle). <b>Note: Otherwise it is null.</b>
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        cp = new CameraPreview(this);        
        FrameLayout previewLocal = (FrameLayout) findViewById(R.id.video_local_video);
		previewLocal.addView(cp);
    }
    
    

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
	// Inflate the menu; this adds items to the action bar if it is present.
	getMenuInflater().inflate(br.com.skylane.voicer.R.menu.main, menu);
	return true;
    }
    
    @Override
    protected void onDestroy() {
    	super.onDestroy();
    	
    	if (cp != null)
    		cp.releaseEncoder();
    	
    	CameraService.getInstance().releaseCamera();
    }

}

