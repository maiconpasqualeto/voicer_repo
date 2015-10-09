/**
 * 
 */
package br.com.skylane.voicer.camera;

import java.io.IOException;

import android.content.Context;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.media.MediaCodec;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

/**
 * @author maicon
 *
 */
@SuppressWarnings("deprecation")
public class CameraPreview extends SurfaceView implements SurfaceHolder.Callback {
	
	private SurfaceHolder mHolder;
	private Camera mCamera;
	private static final String CODEC_NAME = "video/avc";
	
	public CameraPreview(Context context) {
		super(context);
		
		mHolder = getHolder();
		mHolder.addCallback(this);
		
		MediaCodec mc;
		try {
			
			mc = MediaCodec.createByCodecName(CODEC_NAME);
			Surface surface = mc.createInputSurface();
			((SurfaceView)this).addMediaCodecSurface(surface);
			
			
		} catch (IOException e) {
			Log.e("VOICER", "Erro ao definir codec video", e);
		}
		
		
		mCamera = CameraService.getInstance().getFrontCamera(); 
		
		// deprecated setting, but required on Android versions prior to 3.0
		mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
	}
	
	/**
	 * 
	 * @param wid
	 * @param hei
	 */
	/*public void prepareMedia(int wid, int hei) {
        myMediaRecorder =  new MediaRecorder();
        mCamera.stopPreview();
        mCamera.unlock();
        
        myMediaRecorder.setCamera(mCamera);
        myMediaRecorder.setAudioSource(MediaRecorder.AudioSource.CAMCORDER);
        myMediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
	    
        CamcorderProfile targetProfile = CamcorderProfile.get(CamcorderProfile.QUALITY_LOW);
        targetProfile.videoFrameWidth = wid;
        targetProfile.videoFrameHeight = hei;
        targetProfile.videoFrameRate = 25;
        targetProfile.videoBitRate = 512*1024;
        targetProfile.videoCodec = MediaRecorder.VideoEncoder.H264;
        targetProfile.audioCodec = MediaRecorder.AudioEncoder.AMR_NB;
        targetProfile.fileFormat = MediaRecorder.OutputFormat.MPEG_4;
        
        myMediaRecorder.setProfile(targetProfile);
    }*/
	
	/**
	 * 
	 * @return
	 */
	/*private boolean mediaRecorderStart() {
        
        myMediaRecorder.setPreviewDisplay(mHolder.getSurface());
        
        try {
        	
        	myMediaRecorder.prepare();
        	
	    } catch (IllegalStateException e) {
	        releaseMediaRecorder();	
	        Log.d("TEAONLY", "JAVA:  camera prepare illegal error");
            return false;
	    } catch (IOException e) {
	        releaseMediaRecorder();	    
	        Log.d("TEAONLY", "JAVA:  camera prepare io error");
            return false;
	    }
	    
        try {
        	
            myMediaRecorder.start();
            
        } catch( Exception e) {
            releaseMediaRecorder();
	        Log.d("TEAONLY", "JAVA:  camera start error");
            return false;
        }

        return true;
    }*/

	/**
	 * 
	 * @param targetFd
	 * @return
	 */
    /*public boolean StartStreaming(FileDescriptor targetFd) {
        myMediaRecorder.setOutputFile(targetFd);
        myMediaRecorder.setMaxDuration(9600000); 	// Set max duration 4 hours
        //myMediaRecorder.setMaxFileSize(1600000000); // Set max file size 16G
        myMediaRecorder.setOnInfoListener(streamingEventHandler);
        return mediaRecorderStart();
    }*/

    /**
     * 
     * @param targetFile
     * @return
     */
    /*public boolean StartRecording(String targetFile) {
        myMediaRecorder.setOutputFile(targetFile);
        
        return mediaRecorderStart();
    }*/
    
    /**
     * 
     */
    /*public void StopMedia() {
        myMediaRecorder.stop();
        releaseMediaRecorder();        
    }*/

    /**
     * 
     */
    /*private void releaseMediaRecorder(){
        if (myMediaRecorder != null) {
        	myMediaRecorder.reset();   // clear recorder configuration
        	myMediaRecorder.release(); // release the recorder object
        	myMediaRecorder = null;
            mCamera.lock();           // lock camera for later use
            mCamera.startPreview();
        }
        myMediaRecorder = null;
    }

     
    private MediaRecorder.OnInfoListener streamingEventHandler = new MediaRecorder.OnInfoListener() {
        @Override
        public void onInfo(MediaRecorder mr, int what, int extra) {
            Log.d("TEAONLY", "MediaRecorder event = " + what);    
        }
    };*/


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
