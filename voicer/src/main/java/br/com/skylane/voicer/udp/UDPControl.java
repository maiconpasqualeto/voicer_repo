/**
 * 
 */
package br.com.skylane.voicer.udp;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import br.com.skylane.voicer.VoicerHelper;

/**
 * @author maicon
 *
 */
public class UDPControl {

	private static final int SERVER_PORT = 1234;
	
	private MulticastSocket sSocket;
	private InetAddress ipSource;
	private InetAddress ipTarget;
	private Context ctx;
	private BlockingQueue<UdpPacket> fila = new LinkedBlockingQueue<UdpPacket>();  
	private Thread readThread;
	private Thread sendThread;
	
	//InetAddress.getByName("192.168.25.131");

	public UDPControl(Context ctx, InetAddress ipSource, InetAddress ipTarget) {
		this.ctx = ctx;
		this.ipSource = ipSource;
		this.ipTarget = ipTarget;
		
		try {
			sSocket = new MulticastSocket(SERVER_PORT);
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
			
			byte[] buffer = new byte[4096];
			
			while (!isInterrupted()) {
				try {
					if (sSocket == null)
						return;
					
					DatagramPacket dp = new DatagramPacket(buffer, buffer.length);
					//InetSocketAddress sa = (InetSocketAddress) dp.getSocketAddress();
					
					sSocket.receive(dp);
					//Log.d("VOICER", new String(lMsg, 0, dp.getLength()));
					
					Intent i = new Intent();
					i.setAction("br.com.skylane.voicer.MESSAGE_RECEIVED");
					i.putExtra("pct",
							new String(buffer, 0, dp.getLength()));
					ctx.sendBroadcast(i);
										
				} catch (Throwable e) {
					Log.e(VoicerHelper.TAG, "Throwable: " + e);
					return;
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
					
					UdpPacket pct = fila.take();
					
					DatagramPacket dp = pct.getDatagramPacket();					
					
					sSocket.send(dp);
					
				} catch (Throwable e) {
					Log.e(VoicerHelper.TAG, "Throwable: " + e);
					return;
				}
			}			
		}
	}

	/**
	 * 
	 * @param pct
	 */
	public void send(UdpPacket pct) {
		try {
			pct.setIpSource(ipSource);
			pct.setIpTarget(ipTarget);
			pct.setPort(SERVER_PORT);
			
			fila.put(pct);
			
		} catch (InterruptedException e) {
			Log.e(VoicerHelper.TAG, "Erro ao colocar pct na fila" + e);
		}
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

}
