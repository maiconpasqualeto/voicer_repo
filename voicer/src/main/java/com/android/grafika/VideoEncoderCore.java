/*
 * Copyright 2014 Google Inc. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.grafika;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Timer;
import java.util.TimerTask;

import android.media.MediaCodec;
import android.media.MediaCodecInfo;
import android.media.MediaFormat;
import android.media.MediaMuxer;
import android.preference.PreferenceActivity.Header;
import android.util.Log;
import android.view.Surface;
import br.com.skylane.voicer.VoicerHelper;
import br.com.skylane.voicer.udp.PayloadType;
import br.com.skylane.voicer.udp.UDPControl;

/**
 * This class wraps up the core components used for surface-input video encoding.
 * <p>
 * Once created, frames are fed to the input surface.  Remember to provide the presentation
 * time stamp, and always call drainEncoder() before swapBuffers() to ensure that the
 * producer side doesn't get backed up.
 * <p>
 * This class is not thread-safe, with one exception: it is valid to use the input surface
 * on one thread, and drain the output on a different thread.
 */
public class VideoEncoderCore {
    private static final String TAG = VoicerHelper.TAG;
    private static final boolean VERBOSE = false;

    // TODO: these ought to be configurable as well
    private static final String MIME_TYPE = "video/avc";    // H.264 Advanced Video Coding
    private static final int FRAME_RATE = 30;               // 30fps
    private static final int IFRAME_INTERVAL = 5;           // 5 seconds between I-frames
    private static final int MAX_PACK_SIZE = 1300;           // Max package size

    private Surface mInputSurface;
    private MediaMuxer mMuxer;
    private MediaCodec mEncoder;
    private MediaCodec.BufferInfo mBufferInfo;
    private boolean mMuxerStarted;
    
    private UDPControl control; 
    private byte[] fulHeader = new byte[2];
    private byte[] decodedHeader = new byte[5];
    private Timer t = new Timer();
    private byte[] spsPPS;


    /**
     * Configures encoder and muxer state, and prepares the input Surface.
     */
    public VideoEncoderCore(int width, int height, int bitRate, File outputFile, UDPControl control)
            throws IOException {
    	    	
		this.control = control;    	
    	
    	
        mBufferInfo = new MediaCodec.BufferInfo();

        MediaFormat format = MediaFormat.createVideoFormat(MIME_TYPE, width, height);

        // Set some properties.  Failing to specify some of these can cause the MediaCodec
        // configure() call to throw an unhelpful exception.
        format.setInteger(MediaFormat.KEY_COLOR_FORMAT,
                MediaCodecInfo.CodecCapabilities.COLOR_FormatSurface);
        format.setInteger(MediaFormat.KEY_BIT_RATE, bitRate);
        format.setInteger(MediaFormat.KEY_FRAME_RATE, FRAME_RATE);
        format.setInteger(MediaFormat.KEY_I_FRAME_INTERVAL, IFRAME_INTERVAL);
        if (VERBOSE) Log.d(TAG, "format: " + format);

        // Create a MediaCodec encoder, and configure it with our format.  Get a Surface
        // we can use for input and wrap it with a class that handles the EGL work.
        mEncoder = MediaCodec.createEncoderByType(MIME_TYPE);
        mEncoder.configure(format, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE);
        mInputSurface = mEncoder.createInputSurface();
        mEncoder.start();

        // Create a MediaMuxer.  We can't add the video track and start() the muxer here,
        // because our MediaFormat doesn't have the Magic Goodies.  These can only be
        // obtained from the encoder after it has started processing data.
        //
        // We're not actually interested in multiplexing audio.  We just want to convert
        // the raw H.264 elementary stream we get from MediaCodec into a .mp4 file.
        /*mMuxer = new MediaMuxer(outputFile.toString(),
                MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4);*/
 
        //mTrackIndex = -1;
        mMuxerStarted = false;
    }

    /**
     * Returns the encoder's input surface.
     */
    public Surface getInputSurface() {
        return mInputSurface;
    }

    /**
     * Releases encoder resources.
     */
    public void release() {
        if (VERBOSE) Log.d(TAG, "releasing encoder objects");
        if (mEncoder != null) {
            mEncoder.stop();
            mEncoder.release();
            mEncoder = null;
        }
        if (mMuxer != null) {
            // TODO: stop() throws an exception if you haven't fed it any data.  Keep track
            //       of frames submitted, and don't call stop() if we haven't written anything.
            mMuxer.stop();
            mMuxer.release();
            mMuxer = null;
        }
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
    public void drainEncoder(boolean endOfStream) {
        final int TIMEOUT_USEC = 10000;
        if (VERBOSE) Log.d(TAG, "drainEncoder(" + endOfStream + ")");

        if (endOfStream) {
            if (VERBOSE) Log.d(TAG, "sending EOS to encoder");
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
                    if (VERBOSE) Log.d(TAG, "no output available, spinning to await EOS");
                }
            } else if (encoderStatus == MediaCodec.INFO_OUTPUT_BUFFERS_CHANGED) {
                // not expected for an encoder
                encoderOutputBuffers = mEncoder.getOutputBuffers();
            } else if (encoderStatus == MediaCodec.INFO_OUTPUT_FORMAT_CHANGED) {
                // should happen before receiving buffers, and should only happen once
                if (mMuxerStarted) {
                    throw new RuntimeException("format changed twice");
                }
                MediaFormat newFormat = mEncoder.getOutputFormat();
                Log.d(TAG, "encoder output format changed: " + newFormat);

                // now that we have the Magic Goodies, start the muxer
                /*[Maicon] mTrackIndex = mMuxer.addTrack(newFormat);
                mMuxer.start();*/
                mMuxerStarted = true;
            } else if (encoderStatus < 0) {
                Log.w(TAG, "unexpected result from encoder.dequeueOutputBuffer: " +
                        encoderStatus);
                // let's ignore it
            } else {
                ByteBuffer encodedData = encoderOutputBuffers[encoderStatus];
                if (encodedData == null) {
                    throw new RuntimeException("encoderOutputBuffer " + encoderStatus +
                            " was null");
                }

                if (mBufferInfo.size != 0) {
                    if (!mMuxerStarted) {
                        throw new RuntimeException("muxer hasn't started");
                    }
                    // adjust the ByteBuffer values to match BufferInfo (not needed?)
                    encodedData.position(mBufferInfo.offset);
                    encodedData.limit(mBufferInfo.offset + mBufferInfo.size);
                    
                    //encodedData.remaining();
                    //mMuxer.writeSampleData(mTrackIndex, encodedData, mBufferInfo);
                    int length = encodedData.remaining();
                    
                    long pst = mBufferInfo.presentationTimeUs * 90 / 1000;
                    
                    
                    if ((mBufferInfo.flags & MediaCodec.BUFFER_FLAG_CODEC_CONFIG) != 0) {
                        // The codec config data was pulled out and fed to the muxer when we got
                        // the INFO_OUTPUT_FORMAT_CHANGED status.  Ignore it.
                        if (VERBOSE) Log.d(TAG, "ignoring BUFFER_FLAG_CODEC_CONFIG");
                        
                        TimerTask tt = new TimerTask() {
							@Override
							public void run() {
								control.sendData(spsPPS, System.nanoTime() / 1000, false, PayloadType.VIDEO);
							}
						};
						t.schedule(tt, 3000, 3000);
                        
                        spsPPS = new byte[encodedData.remaining() + 1];
                    	encodedData.get(spsPPS, 1, encodedData.remaining());
                    	spsPPS[0] = (byte) ((spsPPS[5] & 0x60) & 0xFF); // STAP-A indicator NRI
                    	spsPPS[0] += 24; 
                    	
                    	control.sendData(spsPPS, pst, false, PayloadType.VIDEO);
                        
                        mBufferInfo.size = 0;
                    } else 
                    	if (length > MAX_PACK_SIZE) {
	                    	int remains = length;
	                    	
	                    	encodedData.mark();
	                    	encodedData.get(decodedHeader, 0, 5);
	                    	encodedData.reset();
	                    	
	                    	// Set FU-A indicator
	                    	fulHeader[0] = (byte) ((decodedHeader[4] & 0x60) & 0xFF); // FU indicator NRI
	                    	fulHeader[0] += 28;
	            			
	                    	fulHeader[1] = (byte) (decodedHeader[4] & 0x1f); // FU header type
	                    	fulHeader[1] += 0x80; // Start bit
	                    	
	                    	while ( remains > MAX_PACK_SIZE ) {
		                    	byte[] pct = new byte[MAX_PACK_SIZE];
		                    	
		                    	encodedData.get(pct, 2, MAX_PACK_SIZE - 2);
		                    	
		                    	pct[0] = fulHeader[0];
		                    	pct[1] = fulHeader[1];
		                    	
		                    	control.sendData(pct, pst, false, PayloadType.VIDEO);
		                    	
		                    	// after firt pack, set s byte
		                    	fulHeader[1] = (byte) (fulHeader[1] & 0x7F);
		                    	
		                    	remains -= (MAX_PACK_SIZE -2);
	                    	}
	                    	
	                    	byte[] pct = new byte[remains + 2];
	                    	fulHeader[1] += 0x40; // set end bit
	                    	pct[0] = fulHeader[0];
	                    	pct[1] = fulHeader[1];
	                    	
	                    	encodedData.get(pct, 2, remains);
	                    	
	                    	control.sendData(pct, pst, false, PayloadType.VIDEO);
	                    	
	                    } else {
	                    	byte[] pct = new byte[encodedData.remaining() + 1];                    	
	                    	encodedData.get(pct, 1, encodedData.remaining());
	                    	pct[0] = (byte) (pct[5] & 0x1F); // NAL header - copia o tipo do byte 5 do stream
	                    	control.sendData(pct, pst, false, PayloadType.VIDEO);
	                    }
	                    
	                    /*if (VERBOSE) {                    	
	                        Log.d(TAG, ">> sent " + mBufferInfo.size + " bytes to muxer, ts=" +
	                                mBufferInfo.presentationTimeUs);
	                        Log.d(TAG, ">> HEX " + VoicerHelper.converteDadosBinariosParaStringHexa(pct));                        
	                    }*/
                }

                mEncoder.releaseOutputBuffer(encoderStatus, false);

                if ((mBufferInfo.flags & MediaCodec.BUFFER_FLAG_END_OF_STREAM) != 0) {
                    if (!endOfStream) {
                        Log.w(TAG, "reached end of stream unexpectedly");
                    } else {
                        if (VERBOSE) Log.d(TAG, "end of stream reached");
                    }
                    break;      // out of while
                }
            }
        }
    }
}
