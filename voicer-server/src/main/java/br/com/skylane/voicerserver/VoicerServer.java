package br.com.skylane.voicerserver;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;

/**
 * Hello world!
 *
 */
public class VoicerServer {
	
	private static final int PORTA = 51058; // porta padrao
	private static final String IP_LOCAL = "127.0.0.1";
	private static DatagramSocket serverSocketUDP;
	
	public static void main(String[] args) {
		
		
		final ThreadSocketMain socketMain = ThreadSocketMain.getInstance();
		socketMain.setConnectionType(connectionType);
		socketMain.setPorta(Integer.valueOf(porta));
		socketMain.setIpLocal(ipLocal);
		Thread t = new Thread(socketMain);
		t.start();
		
		
		
		
	}
}
