/**
 * 
 */
package br.com.skylane.voicerserver;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.charset.Charset;
import java.util.ArrayList;

/**
 * @author maicon
 *
 */
public class ThreadUdpReceiver {
	
	private DatagramPacket receivePacket;
	private DatagramSocket serverSocket;
	
	public ThreadUdpReceiver(DatagramSocket serverSocket, DatagramPacket receivePacket) {
		this.receivePacket = receivePacket;
		this.serverSocket = serverSocket;
	}
	
	public void startaProcessamentoPacote() {
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				
				byte[] pacote = receivePacket.getData();
				
                InetAddress ip = receivePacket.getAddress();
                int port = receivePacket.getPort();
                
                String mensagem = new String(pacote, Charset.forName("ISO-8859-1"));        		
        		
        		
                
                
			}
		}, "Thread UDP Receiver").start();
	}
	
}
