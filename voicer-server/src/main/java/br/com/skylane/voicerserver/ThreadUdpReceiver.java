/**
 * 
 */
package br.com.skylane.voicerserver;

import java.lang.reflect.Array;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Base64;

/**
 * @author maicon
 *
 */
public class ThreadUdpReceiver implements Runnable {
	
	private DatagramPacket receivePacket;
	
	public ThreadUdpReceiver(DatagramPacket receivePacket) {
		this.receivePacket = receivePacket;
	}
	
	public void run() {
		
		byte[] pct = new byte[receivePacket.getLength()];
		pct = Arrays.copyOfRange(receivePacket.getData(), 0, receivePacket.getLength());
		//ByteBuffer dados = Base64.getDecoder().decode(buf);
		
        InetAddress ip = receivePacket.getAddress();
        int port = receivePacket.getPort();
        
        //String mensagem = new String(pacote, Charset.forName("ISO-8859-1"));        		
        try {
        	ByteBuffer bb = ByteBuffer.wrap(pct);
        	byte[] bufferIp = new byte[4];
        	bb.get(bufferIp);
        	int bufferPort = bb.getShort();
        	
			InetAddress tabletIp = InetAddress.getByAddress(bufferIp);
			
			System.out.println("Tablet IP: " + tabletIp.getHostAddress());
			System.out.println("Tablet Port: " + bufferPort);
			
			System.out.println("Datagram IP: " + ip.getHostAddress());
			System.out.println("Datagram Port: " + port);
			
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
        
	}
	
}
