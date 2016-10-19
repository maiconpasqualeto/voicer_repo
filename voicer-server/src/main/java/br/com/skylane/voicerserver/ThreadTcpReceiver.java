/**
 * 
 */
package br.com.skylane.voicerserver;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;

/**
 * @author maicon
 *
 */
public class ThreadTcpReceiver implements Runnable {
	
	private Socket s; 
	
	public ThreadTcpReceiver(Socket s) {
		this.s = s;
	}

	@Override
	public void run() {
		try {
			
			BufferedInputStream bis = null;
			OutputStream os = null;
						
			try {
				bis = new BufferedInputStream(s.getInputStream());
				
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
		        byte[] buf = new byte[1024];
		        int lidos = bis.read(buf, 0 , buf.length);
		        if (lidos > -1) {
		        	baos.write(buf, 0, lidos);
			        baos.flush();			        
		        }
		        
		        byte[] pacote = baos.toByteArray();
		        
		        baos.close();
		        
		        try {
		        	ByteBuffer bb = ByteBuffer.wrap(pacote);
		        	byte[] bufferIp = new byte[4];
		        	bb.get(bufferIp);
		        	int bufferPort = bb.getShort();
		        	
					InetAddress tabletIp = InetAddress.getByAddress(bufferIp);
					
					System.out.println("Tablet IP: " + tabletIp.getHostAddress());
					System.out.println("Tablet Port: " + bufferPort);
					
					System.out.println("Socket IP: " + s.getInetAddress().getHostAddress());
					System.out.println("Socket Port: " + s.getPort());
					
				} catch (UnknownHostException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		        
		        
		        //envia resposta para cliente
		        
		        String resposta = "200 - ok";
		        
		        os = s.getOutputStream();
		        os.write(resposta.getBytes());
		        os.flush();
		        
		        
			} finally {
				if (bis != null)
					bis.close();
				if (os != null)
					os.close();
				if (s != null)
					s.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
