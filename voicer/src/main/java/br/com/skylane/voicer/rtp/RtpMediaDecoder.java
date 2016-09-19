/*
* Copyright (C) 2015 Creativa77 SRL and others
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
* http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*
* Contributors:
*
* Ayelen Chavez ashi@creativa77.com.ar
* Julian Cerruti jcerruti@creativa77.com.ar
*
*/

package br.com.skylane.voicer.rtp;

import java.net.DatagramPacket;
import java.nio.ByteBuffer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;

import com.biasedbit.efflux.packet.DataPacket;

import android.media.MediaCodec;
import android.media.MediaCodecInfo;
import android.media.MediaFormat;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import br.com.skylane.voicer.udp.PacketReceivedListener;

/**
 * Implementation of the decoder that uses RTP as transport protocol to decode H264 encoded frames.
 * This object wraps up an Android API decoder and uses it to decode video frames.
 *
 * @author Ayelen Chavez
 */
public class RtpMediaDecoder implements SurfaceHolder.Callback, PacketReceivedListener {

    // configuration constants
    public static final String DEBUGGING_PROPERTY = "DEBUGGING";
    public static final String CONFIG_USE_NIO = "USE_NIO";
    public static final String CONFIG_BUFFER_TYPE = "BUFFER_TYPE";
    public static final String CONFIG_RECEIVE_BUFFER_SIZE = "RECEIVE_BUFFER_SIZE_BYTES";
    public static final int DATA_STREAMING_PORT = 5006;
    public static final int SURFACE_WIDTH = 640;
    public static final int SURFACE_HEIGHT = 480;
    public static final String TRANSPORT_PROTOCOL = "RTP";
    public static final String VIDEO_CODEC = "H.264";
    private static final int FRAME_RATE = 30;               // 30fps
    private static final int IFRAME_INTERVAL = 5;           // 5 seconds between I-frames

    // constant used to activate and deactivate logs
    public static boolean DEBUGGING;
    // surface view where to play video
    private final SurfaceView surfaceView;
    //private final Properties configuration;
    public String bufferType = "time-window";
    public boolean useNio = true;
    public int receiveBufferSize = 50000;
    private PlayerThread playerThread;
    private ByteBuffer[] inputBuffers;
    private ByteBuffer[] outputBuffers;
    private MediaCodec.BufferInfo info = new MediaCodec.BufferInfo();
    private MediaCodec decoder;
    private Log log = LogFactory.getLog(RtpMediaDecoder.class);
    // If this stream is set, use it to trace packet arrival data
    //private OutputStream traceOutputStream = null;

    public RtpMediaDecoder(SurfaceView surfaceView) {
        
        log.info("RtpMediaDecoder started with params (" + DEBUGGING + "," + bufferType + "," + useNio + "," + receiveBufferSize + ")");

        this.surfaceView = surfaceView;
        surfaceView.getHolder().addCallback(this);
    }

    /**
     * Defines the output stream where to trace packet's data while they arrive to the decoder
     *
     * @param outputStream stream where to dump data
     */
    /*public void setTraceOutputStream(OutputStream outputStream) {
        traceOutputStream = outputStream;
    }*/
    
    /**
     * Stops the underlying RTP session and properly releases the Android API decoder
     */
    public void release() {
        if (decoder != null) {
            try {
                decoder.stop();
            } catch (Exception e) {
                log.error("Encountered error while trying to stop decoder", e);
            }
            decoder.release();
            decoder = null;
        }
    }
    
    

    /**
     * Resizes surface view to 640x480
     *
     * @param holder
     */
    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        android.view.ViewGroup.LayoutParams layoutParams = surfaceView.getLayoutParams();
        //layoutParams.width = SURFACE_WIDTH; // required width
        //layoutParams.height = SURFACE_HEIGHT; // required height
        //surfaceView.setLayoutParams(layoutParams);
        
        if (playerThread == null) {
            playerThread = new PlayerThread(holder.getSurface());
            playerThread.start();
        }
    }

    /**
     * Starts playing video when surface view is ready
     *
     * @param holder
     * @param format
     * @param width
     * @param height
     */
    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }

    public String getResolution() {
        return SURFACE_WIDTH + "x" + SURFACE_HEIGHT;
    }

    public String getTransportProtocol() {
        return TRANSPORT_PROTOCOL;
    }

    public String getVideoCodec() {
        return VIDEO_CODEC;
    }

    public int getDataStreamingPort() {
        return DATA_STREAMING_PORT;
    }

    public String getBufferType() {
        return bufferType;
    }

    /**
     * Creates the Android API decoder, configures it and starts it.
     */
    private class PlayerThread extends Thread {
        private Surface surface;

        /**
         * Thread constructor.
         *
         * @param surface where video will be played
         */
        public PlayerThread(Surface surface) {
            this.surface = surface;
        }

        @Override
        public void run() {
            // Wait a little bit to make sure the RtpClientThread had the opportunity to start
            // and create the rtpMediaExtractor
            try {
                sleep(500);
            } catch (InterruptedException e) {
            }
            String mimeType = "video/avc";
            int width = 640;
            int height = 480;

            MediaFormat mediaFormat = MediaFormat.createVideoFormat(mimeType, width, height);
            mediaFormat.setInteger(MediaFormat.KEY_COLOR_FORMAT,
                    MediaCodecInfo.CodecCapabilities.COLOR_FormatSurface);
            mediaFormat.setInteger(MediaFormat.KEY_BIT_RATE, 1000000);
            mediaFormat.setInteger(MediaFormat.KEY_FRAME_RATE, FRAME_RATE);
            mediaFormat.setInteger(MediaFormat.KEY_I_FRAME_INTERVAL, IFRAME_INTERVAL);
            String mime = mediaFormat.getString(MediaFormat.KEY_MIME);
            if (mime.startsWith("video/")) {
                decoder = MediaCodec.createDecoderByType(mime);
                decoder.configure(mediaFormat, surface, null, 0);
            }

            if (decoder == null) {
                log.info("Can't find video info!");
                return;
            }

            decoder.start();
            inputBuffers = decoder.getInputBuffers();
            outputBuffers = decoder.getOutputBuffers();
        }
        
        /**
         * Decodes a frame
         *
         * @param decodeBuffer
         * @throws Exception
         */
        public void decodeFrame(DataPacket decodeBuffer) {
            /*if (DEBUGGING) {
                // Dump buffer to logcat
                log.info(decodeBuffer.toString());
            }*/
        	
        	int inputBufIndex = decoder.dequeueInputBuffer(-1);
            ByteBuffer inputBuf = inputBuffers[inputBufIndex];
            inputBuf.clear();
            inputBuf.put(decodeBuffer.getDataAsArray());
        	
            // Queue the sample to be decoded
            decoder.queueInputBuffer(inputBufIndex, 0,
                    decodeBuffer.getDataSize(), decodeBuffer.getTimestamp(), 0);

            // Read the decoded output
            int outIndex = decoder.dequeueOutputBuffer(info, 10000);
            switch (outIndex) {
                case MediaCodec.INFO_OUTPUT_BUFFERS_CHANGED:
                    if (DEBUGGING) {
                        log.info("The output buffers have changed.");
                    }
                    outputBuffers = decoder.getOutputBuffers();
                    break;
                case MediaCodec.INFO_OUTPUT_FORMAT_CHANGED:
                    if (DEBUGGING) {
                        log.info("New format " + decoder.getOutputFormat());
                    }
                    break;
                case MediaCodec.INFO_TRY_AGAIN_LATER:
                    if (DEBUGGING) {
                        log.info("Call to dequeueOutputBuffer timed out.");
                    }
                    break;
                default:
                    if (DEBUGGING) {
                        ByteBuffer buffer = outputBuffers[outIndex];
                        log.info("We can't use this buffer but render it due to the API limit, " + buffer);
                    }

                    // return buffer to the codec
                    decoder.releaseOutputBuffer(outIndex, true);
                    break;
            }

            // All decoded frames have been rendered, we can stop playing now
            if (((info.flags & MediaCodec.BUFFER_FLAG_END_OF_STREAM) != 0) && DEBUGGING) {
                log.info("All decoded frames have been rendered");
            }
        }
    }

	@Override
	public void processDatagramPacket(DatagramPacket pct) {
		ChannelBuffer buffer = ChannelBuffers.buffer(pct.getLength());
		buffer.writeBytes(pct.getData(), 0, pct.getLength());
		
		DataPacket dp = DataPacket.decode(buffer);
		
		playerThread.decodeFrame(dp);
	}
    
}
