/**
 * 
 */
package br.com.skylane.voicer.service;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketException;
import java.nio.ByteBuffer;

import android.os.ParcelFileDescriptor;
import android.util.Log;

/**
 * @author maicon
 *
 */
public class SocketService {
	
	private DatagramSocket socket;
	
	public void enviarPacoteServidor(ByteBuffer dados) {
		try {
			socket.connect(new InetSocketAddress("192.168.25.155", 12345));
			ParcelFileDescriptor pfd = ParcelFileDescriptor.fromDatagramSocket(socket);			
		} catch (SocketException e) {
			Log.e("VOICER", "Erro ao enviar pacote", e);
		}
	}

}
