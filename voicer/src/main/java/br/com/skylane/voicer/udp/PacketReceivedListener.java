/**
 * 
 */
package br.com.skylane.voicer.udp;

import java.net.DatagramPacket;

/**
 * @author maicon
 *
 */
public interface PacketReceivedListener {
	
	public void processDatagramPacket(DatagramPacket pct);

}
