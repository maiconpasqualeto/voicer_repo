/**
 * 
 */
package br.com.skylane.voicer.udp;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.MulticastSocket;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

import com.biasedbit.efflux.packet.DataPacket;
import com.biasedbit.efflux.participant.RtpParticipant;

import android.util.Log;
import br.com.skylane.voicer.VoicerHelper;

/**
 * @author maicon
 *
 */
public class UDPControl {

	private static final int SERVER_DATA_PORT = 5006;
	//private static final int SERVER_CONTROL_PORT = 5007;
	protected final AtomicInteger sequence = new AtomicInteger(0);
	
	private MulticastSocket sSocket;
	private BlockingQueue<DataPacket> fila = new LinkedBlockingQueue<DataPacket>();  
	private Thread readThread;
	private Thread sendThread;
	private PacketReceivedListener listener; 
	private RtpParticipant localParticipant;
	
	
	
	//InetAddress.getByName("192.168.25.131");

	public UDPControl(RtpParticipant localParticipant) {
		this.localParticipant = localParticipant;
		
		try {
			sSocket = new MulticastSocket(SERVER_DATA_PORT);
			sSocket.setBroadcast(true);
			
		} catch (IOException e) {
			Log.d(VoicerHelper.TAG, "Erro na conexão socket", e);
			return;
		}
		
		readThread = new ReadThread();
		readThread.start();
		sendThread = new SendThread();
		sendThread.start();
		
	}
	

	private class ReadThread extends Thread {
		@Override
		public void run() {
			super.run();
			
			byte[] buffer = new byte[256];
			
			while (!isInterrupted()) {
				try {
					if (sSocket == null)
						return;
					
					DatagramPacket dp = new DatagramPacket(buffer, buffer.length);
					//InetSocketAddress sa = (InetSocketAddress) dp.getSocketAddress();
					
					sSocket.receive(dp);
					//Log.d("VOICER", new String(lMsg, 0, dp.getLength()));
					
					if (listener != null)
						listener.processDatagramPacket(dp);
					else 
						throw new IllegalStateException("'PacketReceivedListener' not set");
				
				} catch (IOException e) {
					Log.e(VoicerHelper.TAG, "Erro de IO: " + e);					
				}
			}
		}
	}

	// ----------------------------------------------------
	private class SendThread extends Thread {
		
		@Override
		public void run() {
			super.run();
						
			while (!isInterrupted()) {
				
				try {
					if (sSocket == null)
						return;
					
					DataPacket pct = fila.take();
					byte[] dados = pct.encode().array();
					DatagramPacket dp = new DatagramPacket(dados, dados.length, 
							localParticipant.getDataDestination());
					
					sSocket.send(dp);
					
				} catch (Throwable e) {
					Log.e(VoicerHelper.TAG, "Throwable: " + e);
				}
			}			
		}
	}

	/**
	 * 
	 * @param pct
	 */
	public void send(DataPacket pct) {
		try {
			pct.setSsrc(this.localParticipant.getSsrc());
			pct.setSequenceNumber(this.sequence.incrementAndGet());
			
			fila.put(pct);
			
		} catch (InterruptedException e) {
			Log.e(VoicerHelper.TAG, "Erro ao colocar pct na fila" + e);
		}
	}
	
	/**
	 * 
	 * @param data
	 * @param timestamp
	 * @param marked
	 * @param payloadType
	 * @return
	 */
    public void sendData(byte[] data, long timestamp, boolean marked, PayloadType payloadType) {        
    	DataPacket packet = new DataPacket();
        packet.setTimestamp(timestamp);
        packet.setData(data);
        packet.setMarker(marked);
        packet.setPayloadType(payloadType.getValue());
        
        send(packet);
    }
    
	
	/**
	 * 
	 */
	public void close() {
		if (readThread != null)
			readThread.interrupt();
		
		if (sendThread != null)
			sendThread.interrupt();
		
		if (sSocket != null) {
			sSocket.close();
			sSocket = null;
			fila.clear();
		}
	}

	public void setListener(PacketReceivedListener listener) {
		this.listener = listener;
	}
		

}
