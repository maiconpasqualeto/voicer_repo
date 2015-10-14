/**
 * 
 */
package br.com.skylane.voicer.camera;

import java.io.IOException;
import java.nio.ByteBuffer;

import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.media.MediaCodec;
import android.media.MediaCodecInfo;
import android.media.MediaFormat;
import android.util.Log;
import br.com.skylane.voicer.VoicerHelper;
import br.com.skylane.voicer.view.CodecInputSurface;
import br.com.skylane.voicer.view.SurfaceTextureManager;

/**
 * @author maicon
 *
 */
@SuppressWarnings("deprecation")
public class CameraPreview {
	
	private Camera mCamera;
	private CodecInputSurface mInputSurface;
	private SurfaceTextureManager mStManager;
	private MediaCodec mEncoder;	
	
	private static final String MIME_TYPE = "video/avc";    // H.264 Advanced Video Coding
    private static final int FRAME_RATE = 30;               // 30fps
    private static final int IFRAME_INTERVAL = 5;           // 5 seconds between I-frames
    private static final long DURATION_SEC = 8;             // 8 seconds of video
    
    private static final String SWAPPED_FRAGMENT_SHADER =
            "#extension GL_OES_EGL_image_external : require\n" +
            "precision mediump float;\n" +
            "varying vec2 vTextureCoord;\n" +
            "uniform samplerExternalOES sTexture;\n" +
            "void main() {\n" +
            "  gl_FragColor = texture2D(sTexture, vTextureCoord).gbra;\n" +
            "}\n";
	
	private MediaCodec.BufferInfo mBufferInfo;
		
	public CameraPreview() {		
		 		
	}
	
	public void startPreview() {
		CameraPreviewThread.runPreview(this);
	}
		
	private void startCameraPreviewAndStream() throws IOException {
		// arbitrary but popular values
        int encWidth = 640;
        int encHeight = 480;
        int encBitRate = 6000000;      // Mbps
        Log.d(VoicerHelper.TAG, MIME_TYPE + " output " + encWidth + "x" + encHeight + " @" + encBitRate);
        
        try {
            prepareCamera();
            prepareMediaEncoder(encWidth, encHeight, encBitRate);
            mInputSurface.makeCurrent();
            prepareSurfaceTexture();

            mCamera.startPreview();

            long startWhen = System.nanoTime();
            long desiredEnd = startWhen + DURATION_SEC * 1000000000L;
            SurfaceTexture st = mStManager.getSurfaceTexture();
            int frameCount = 0;
            
            while (System.nanoTime() < desiredEnd) {
                // Feed any pending encoder output into the muxer.
                drainEncoder(false);

                // Switch up the colors every 15 frames.  Besides demonstrating the use of
                // fragment shaders for video editing, this provides a visual indication of
                // the frame rate: if the camera is capturing at 15fps, the colors will change
                // once per second.
                if ((frameCount % 15) == 0) {
                    String fragmentShader = null;
                    if ((frameCount & 0x01) != 0) {
                        fragmentShader = SWAPPED_FRAGMENT_SHADER;
                    }
                    mStManager.changeFragmentShader(fragmentShader);
                }
                frameCount++;

                // Acquire a new frame of input, and render it to the Surface.  If we had a
                // GLSurfaceView we could switch EGL contexts and call drawImage() a second
                // time to render it on screen.  The texture can be shared between contexts by
                // passing the GLSurfaceView's EGLContext as eglCreateContext()'s share_context
                // argument.
                mStManager.awaitNewImage();
                mStManager.drawImage();

                // Set the presentation time stamp from the SurfaceTexture's time stamp.  This
                // will be used by MediaMuxer to set the PTS in the video.
                if (VoicerHelper.VERBOSE) {
                    Log.d(VoicerHelper.TAG, "present: " +
                            ((st.getTimestamp() - startWhen) / 1000000.0) + "ms");
                }
                mInputSurface.setPresentationTime(st.getTimestamp());

                // Submit it to the encoder.  The eglSwapBuffers call will block if the input
                // is full, which would be bad if it stayed full until we dequeued an output
                // buffer (which we can't do, since we're stuck here).  So long as we fully drain
                // the encoder before supplying additional input, the system guarantees that we
                // can supply another frame without blocking.
                if (VoicerHelper.VERBOSE) Log.d(VoicerHelper.TAG, "sending frame to encoder");
                mInputSurface.swapBuffers();
            }

            // send end-of-stream to encoder, and drain remaining output
            drainEncoder(true);
            
        } finally {
            // release everything we grabbed
            releaseCamera();
            releaseEncoder();
            releaseSurfaceTexture();
        }
	}	
	
	/**
	 * 
	 */
	private void prepareCamera() {
		mCamera = CameraService.getInstance().getFrontCamera();
	}
	
	private void releaseCamera() {
        if (VoicerHelper.VERBOSE) Log.d(VoicerHelper.TAG, "releasing camera");
        if (mCamera != null) {
            mCamera.stopPreview();
            mCamera.release();
            mCamera = null;
        }
    }
	
	/**
	 * 
	 * @param width
	 * @param height
	 * @param bitRate
	 * @throws IOException
	 */
	private void prepareMediaEncoder(int width, int height, int bitRate) throws IOException {
		mBufferInfo = new MediaCodec.BufferInfo();

        MediaFormat format = MediaFormat.createVideoFormat(MIME_TYPE, width, height);
        
        // Set some properties.  Failing to specify some of these can cause the MediaCodec
        // configure() call to throw an unhelpful exception.
        format.setInteger(MediaFormat.KEY_COLOR_FORMAT,
                MediaCodecInfo.CodecCapabilities.COLOR_FormatSurface);
        format.setInteger(MediaFormat.KEY_BIT_RATE, bitRate);
        format.setInteger(MediaFormat.KEY_FRAME_RATE, FRAME_RATE);
        format.setInteger(MediaFormat.KEY_I_FRAME_INTERVAL, IFRAME_INTERVAL);
        
        if (VoicerHelper.VERBOSE) Log.d(VoicerHelper.TAG, "format: " + format);
        
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
     * Releases the SurfaceTexture.
     */
    private void releaseSurfaceTexture() {
        if (mStManager != null) {
            mStManager.release();
            mStManager = null;
        }
    }
	
	
	/**
     * Releases encoder resources.
     */
    public void releaseEncoder() {
        if (VoicerHelper.VERBOSE) Log.d(VoicerHelper.TAG, "releasing encoder objects");
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
    
    /**
     * Extracts all pending data from the encoder and forwards it to the muxer.
     * <p>
     * If endOfStream is not set, this returns when there is no more data to drain.  If it
     * is set, we send EOS to the encoder, and then iterate until we see EOS on the output.
     * Calling this with endOfStream set should be done once, right before stopping the muxer.
     * <p>
     * We're just using the muxer to get a .mp4 file (instead of a raw H.264 stream).  We're
     * not recording audio.
     */
    private void drainEncoder(boolean endOfStream) {
        final int TIMEOUT_USEC = 10000;
        if (VoicerHelper.VERBOSE) Log.d(VoicerHelper.TAG, "drainEncoder(" + endOfStream + ")");

        if (endOfStream) {
            if (VoicerHelper.VERBOSE) Log.d(VoicerHelper.TAG, "sending EOS to encoder");
            mEncoder.signalEndOfInputStream();
        }

        ByteBuffer[] encoderOutputBuffers = mEncoder.getOutputBuffers();
        while (true) {
            int encoderStatus = mEncoder.dequeueOutputBuffer(mBufferInfo, TIMEOUT_USEC);
            if (encoderStatus == MediaCodec.INFO_TRY_AGAIN_LATER) {
                // no output available yet
                if (!endOfStream) {
                    break;      // out of while
                } else {
                    if (VoicerHelper.VERBOSE) 
                    	Log.d(VoicerHelper.TAG, "no output available, spinning to await EOS");
                }
            } else if (encoderStatus == MediaCodec.INFO_OUTPUT_BUFFERS_CHANGED) {
                // not expected for an encoder
                encoderOutputBuffers = mEncoder.getOutputBuffers();
            } else if (encoderStatus == MediaCodec.INFO_OUTPUT_FORMAT_CHANGED) {
                // should happen before receiving buffers, and should only happen once
                /*if (mMuxerStarted) {
                    throw new RuntimeException("format changed twice");
                }*/
                MediaFormat newFormat = mEncoder.getOutputFormat();
                Log.d(VoicerHelper.TAG, "encoder output format changed: " + newFormat);

                // now that we have the Magic Goodies, start the muxer
                /*mTrackIndex = mMuxer.addTrack(newFormat);
                mMuxer.start();
                mMuxerStarted = true;*/
                
                // TODO [Maicon] starts here, execute once
                
            } else if (encoderStatus < 0) {
                Log.w(VoicerHelper.TAG, "unexpected result from encoder.dequeueOutputBuffer: " +
                        encoderStatus);
                // let's ignore it
            } else {
                ByteBuffer encodedData = encoderOutputBuffers[encoderStatus];
                if (encodedData == null) {
                    throw new RuntimeException("encoderOutputBuffer " + encoderStatus +
                            " was null");
                }

                if ((mBufferInfo.flags & MediaCodec.BUFFER_FLAG_CODEC_CONFIG) != 0) {
                    // The codec config data was pulled out and fed to the muxer when we got
                    // the INFO_OUTPUT_FORMAT_CHANGED status.  Ignore it.
                    if (VoicerHelper.VERBOSE) 
                    	Log.d(VoicerHelper.TAG, "ignoring BUFFER_FLAG_CODEC_CONFIG");
                    mBufferInfo.size = 0;
                }

                if (mBufferInfo.size != 0) {
                    /*if (!mMuxerStarted) {
                        throw new RuntimeException("muxer hasn't started");
                    }*/

                    // adjust the ByteBuffer values to match BufferInfo (not needed?)
                    encodedData.position(mBufferInfo.offset);
                    encodedData.limit(mBufferInfo.offset + mBufferInfo.size);

                    /*mMuxer.writeSampleData(mTrackIndex, encodedData, mBufferInfo);*/
                    // TODO [Maicon] send data to socket                    
                    if (VoicerHelper.VERBOSE) 
                    	Log.d(VoicerHelper.TAG, "sent " + mBufferInfo.size + " bytes to muxer");
                }

                mEncoder.releaseOutputBuffer(encoderStatus, false);

                if ((mBufferInfo.flags & MediaCodec.BUFFER_FLAG_END_OF_STREAM) != 0) {
                    if (!endOfStream) {
                        Log.w(VoicerHelper.TAG, "reached end of stream unexpectedly");
                    } else {
                        if (VoicerHelper.VERBOSE) Log.d(VoicerHelper.TAG, "end of stream reached");
                    }
                    break;      // out of while
                }
            }
        }
    }

    
    private static class CameraPreviewThread implements Runnable {
    	
    	private CameraPreview preview;
    	
    	public CameraPreviewThread(CameraPreview preview) {
    		this.preview = preview;
		}
    	
    	/*
    	 * (non-Javadoc)
    	 * @see java.lang.Runnable#run()
    	 */
    	public void run() {
    		try {
				preview.startCameraPreviewAndStream();
			} catch (IOException e) {
				Log.e(VoicerHelper.TAG, "Erro start preview thread.", e);
			}    		
    	}
    	
    	/**
    	 * 
    	 * @param preview
    	 */
    	public static void runPreview(CameraPreview preview) {
    		try {
	    		
    			CameraPreviewThread cpt = new CameraPreviewThread(preview);
	    		Thread t = new Thread(cpt, "Thread Camera Preview");
	    		t.start();
				t.join();
				
			} catch (InterruptedException e) {
				Log.e(VoicerHelper.TAG, "Join error camera preview thread.", e);
			}
    	}
    }

}
