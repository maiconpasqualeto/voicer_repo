/**
 * 
 */
package br.com.skylane.voicer.tcp;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.UnknownHostException;

import br.com.skylane.voicer.VoicerHelper;
import android.util.Log;

/**
 * @author maicon
 *
 */
public class TcpControl {
	
	private static final String SERVER_IP = "192.168.21.58";
	private static final int SERVER_PORT = 5100;
	
	public TcpControl() {
		
	}
	
	public void enviaSolicitacao(final byte[] solicitacao) {
		Thread t = new Thread(new Runnable() {
			
			@Override
			public void run() {
				try {
					Socket s = null;
					
					try {				
						s = new Socket(SERVER_IP, SERVER_PORT);
						
						OutputStream os = s.getOutputStream();
						BufferedInputStream bis = new BufferedInputStream(s.getInputStream());
						os.write(solicitacao);
						os.flush();
						
						ByteArrayOutputStream baos = new ByteArrayOutputStream();
				        byte[] buf = new byte[1024];
				        int lidos = bis.read(buf, 0, buf.length);
				        if (lidos > -1) {
				        	baos.write(buf, 0, lidos);
					        baos.flush();					       
				        }
				        
				        byte[] pacote = baos.toByteArray();		        
				        
				        baos.close();
				        
					} finally {
						if (s != null)
							s.close();
					}
					
				} catch (IOException e) {
					Log.e(VoicerHelper.TAG, "Erro de conexão socket tcp", e);
				}
				
			}
		});
		t.start();
		
		
	}

}
