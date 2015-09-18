/**
 * 
 */
package br.com.skylane.voicer.camera;

import java.io.IOException;

import android.content.Context;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;

/**
 * @author maicon
 *
 */
@SuppressWarnings("deprecation")
public class CameraPreview extends SurfaceView implements Callback {
	
	private SurfaceHolder mHolder;
	private Camera mCamera;
	
	public CameraPreview(Context context, Camera camera) {
		super(context);
		
		mHolder = getHolder();
		mHolder.addCallback(this);
		
		mCamera = camera;
		// deprecated setting, but required on Android versions prior to 3.0
		mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
	}

	/* (non-Javadoc)
	 * @see android.view.SurfaceHolder.Callback#surfaceChanged(android.view.SurfaceHolder, int, int, int)
	 */
	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
		if (mHolder.getSurface() == null){
            // preview surface does not exist
            return;
        }
		
        // stop preview before making changes
        try {
            mCamera.stopPreview();
        } catch (Exception e){
            // ignore: tried to stop a non-existent preview
        }
        
        Parameters parameters = mCamera.getParameters();
        mCamera.setDisplayOrientation(90);
        mCamera.setParameters(parameters);
        
        // start preview with new settings
        try {
            mCamera.setPreviewDisplay(mHolder);
            mCamera.startPreview();

        } catch (Exception e){
            e.printStackTrace();
        }

	}

	/* (non-Javadoc)
	 * @see android.view.SurfaceHolder.Callback#surfaceCreated(android.view.SurfaceHolder)
	 */
	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		try {
            mCamera.setPreviewDisplay(holder);
            mCamera.startPreview();
        } catch (IOException e) {
            e.printStackTrace();
        }

	}

	/* (non-Javadoc)
	 * @see android.view.SurfaceHolder.Callback#surfaceDestroyed(android.view.SurfaceHolder)
	 */
	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		// TODO Auto-generated method stub

	}

}
