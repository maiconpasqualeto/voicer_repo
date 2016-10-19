package br.com.skylane.voicerserver;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Hello world!
 *
 */
public class VoicerServer {
	
	private static final int PORTA = 5100; // porta padrao
	private static final String IP_LOCAL = "172.31.20.178";
	//private static DatagramSocket serverSocketUDP;
	private static final int MTU = 1536;
	
	
	public static void main(String[] args) {
		
		ExecutorService pool = Executors.newFixedThreadPool(15);
		/*try {
			
			serverSocketUDP = new DatagramSocket(new InetSocketAddress(IP_LOCAL, PORTA));
			
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		byte[] buffEntrada = new byte[MTU];
		
		while (!Thread.interrupted()) {
		
			try {
				
				DatagramPacket receivePacket = new DatagramPacket(buffEntrada, buffEntrada.length);
				serverSocketUDP.receive(receivePacket);
				
				pool.execute(new ThreadUdpReceiver(receivePacket));
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
		}
		
		serverSocketUDP.close();*/
		
		ServerSocket serverSocket = null;
		
		try {
			
			try {
			
				serverSocket = new ServerSocket(PORTA);
				
				while (!Thread.interrupted()) {
					Socket s = serverSocket.accept();
					pool.execute(new ThreadTcpReceiver(s));
				}
			
			} finally {
				serverSocket.close();
			}
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		
		
		
		
		pool.shutdown();
		
	}
}
