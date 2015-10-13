/**
 * 
 */
package br.com.skylane.voicer.camera;

import java.io.IOException;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.media.MediaCodec;
import android.media.MediaCodecInfo;
import android.media.MediaFormat;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import br.com.skylane.voicer.VoicerHelper;
import br.com.skylane.voicer.view.CodecInputSurface;
import br.com.skylane.voicer.view.SurfaceTextureManager;

/**
 * @author maicon
 *
 */
@SuppressWarnings("deprecation")
public class CameraPreview extends SurfaceView implements SurfaceHolder.Callback {
	
	private static final String TAG = "VOICER";
	
	private SurfaceHolder mHolder;
	private Camera mCamera;
	private CodecInputSurface mInputSurface;
	private SurfaceTextureManager mStManager;
	private MediaCodec mEncoder;	
	
	private static final String MIME_TYPE = "video/avc";    // H.264 Advanced Video Coding
    private static final int FRAME_RATE = 30;               // 30fps
    private static final int IFRAME_INTERVAL = 5;           // 5 seconds between I-frames
	
	private MediaCodec.BufferInfo mBufferInfo;
	
	public CameraPreview(Context context) {
		super(context);
		
		mHolder = getHolder();
		mHolder.addCallback(this);
		
		
		try {
			// arbitrary but popular values
	        int encWidth = 640;
	        int encHeight = 480;
	        int encBitRate = 6000000;      // Mbps
	        Log.d(TAG, MIME_TYPE + " output " + encWidth + "x" + encHeight + " @" + encBitRate);
			
			mBufferInfo = new MediaCodec.BufferInfo();

	        MediaFormat format = MediaFormat.createVideoFormat(MIME_TYPE, encWidth, encHeight);

	        // Set some properties.  Failing to specify some of these can cause the MediaCodec
	        // configure() call to throw an unhelpful exception.
	        format.setInteger(MediaFormat.KEY_COLOR_FORMAT,
	                MediaCodecInfo.CodecCapabilities.COLOR_FormatSurface);
	        format.setInteger(MediaFormat.KEY_BIT_RATE, encBitRate);
	        format.setInteger(MediaFormat.KEY_FRAME_RATE, FRAME_RATE);
	        format.setInteger(MediaFormat.KEY_I_FRAME_INTERVAL, IFRAME_INTERVAL);
	        
	        if (VoicerHelper.VERBOSE) Log.d(TAG, "format: " + format);
	        
	        // Create a MediaCodec encoder, and configure it with our format.  Get a Surface
	        // we can use for input and wrap it with a class that handles the EGL work.
	        //
	        // If you want to have two EGL contexts -- one for display, one for recording --
	        // you will likely want to defer instantiation of CodecInputSurface until after the
	        // "display" EGL context is created, then modify the eglCreateContext call to
	        // take eglGetCurrentContext() as the share_context argument.
	        mEncoder = MediaCodec.createEncoderByType(MIME_TYPE);
	        mEncoder.configure(format, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE);
	        mInputSurface = new CodecInputSurface(mEncoder.createInputSurface());
	        mEncoder.start();			
			
		} catch (IOException e) {
			Log.e("VOICER", "Erro ao definir codec video", e);
		}
				
		mCamera = CameraService.getInstance().getFrontCamera(); 
		
		// deprecated setting, but required on Android versions prior to 3.0
		mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
	}
	
	/**
     * Configures SurfaceTexture for camera preview.  Initializes mStManager, and sets the
     * associated SurfaceTexture as the Camera's "preview texture".
     * <p>
     * Configure the EGL surface that will be used for output before calling here.
     */
    private void prepareSurfaceTexture() {
        mStManager = new SurfaceTextureManager();
        SurfaceTexture st = mStManager.getSurfaceTexture();
        try {
            mCamera.setPreviewTexture(st);
        } catch (IOException ioe) {
            throw new RuntimeException("setPreviewTexture failed", ioe);
        }
    }
	
	
	/**
     * Releases encoder resources.
     */
    public void releaseEncoder() {
        if (VoicerHelper.VERBOSE) Log.d(TAG, "releasing encoder objects");
        if (mEncoder != null) {
            mEncoder.stop();
            mEncoder.release();
            mEncoder = null;
        }
        if (mInputSurface != null) {
            mInputSurface.release();
            mInputSurface = null;
        }
        /*if (mMuxer != null) {
            mMuxer.stop();
            mMuxer.release();
            mMuxer = null;
        }*/
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
        	
        	mInputSurface.makeCurrent();
        	prepareSurfaceTexture();
        	
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
