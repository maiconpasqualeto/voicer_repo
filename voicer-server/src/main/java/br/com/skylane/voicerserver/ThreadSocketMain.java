/**
 * 
 */
package br.com.skylane.voicerserver;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;

/**
 * @author maicon
 *
 */
public class ThreadSocketMain implements Runnable {
	
	private static final int PORTA = 51058; // porta padrao
	private static final String IP_LOCAL = "127.0.0.1";
	private static DatagramSocket serverSocketUDP;

	@Override
	public void run() {
		
		try {
			
			serverSocketUDP = new DatagramSocket(new InetSocketAddress(IP_LOCAL, PORTA));
		
				
			byte[] buffEntrada = new byte[1024];
			
			DatagramPacket receivePacket = new DatagramPacket(buffEntrada, buffEntrada.length);
			serverSocketUDP.receive(receivePacket);
			
			new ThreadUdpReceiver(serverSocketUDP, receivePacket).startaProcessamentoPacote();
			
		
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			serverSocketUDP.close();
		}
		
	}
	
	

}
