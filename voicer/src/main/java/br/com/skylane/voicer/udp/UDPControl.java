/**
 * 
 */
package br.com.skylane.voicer.udp;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.SocketException;
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
	private InetAddress ipAddress;
	private Context ctx;
	private BlockingQueue<byte[]> fila = new LinkedBlockingQueue<byte[]>();  
	private Thread readThread;
	private Thread sendThread;
	
	//InetAddress.getByName("192.168.25.131");

	public UDPControl(Context ctx, InetAddress ipAddress) {
		this.ctx = ctx;
		this.ipAddress = ipAddress;
		
		try {
			sSocket = new MulticastSocket(SERVER_PORT);
			
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
					
					byte[] buffer = fila.take();
					
					DatagramPacket dp = new DatagramPacket(buffer,
							buffer.length, ipAddress,
							SERVER_PORT);
					sSocket.setBroadcast(true);
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
	public void send(byte[] pct) {
		try {
			
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
