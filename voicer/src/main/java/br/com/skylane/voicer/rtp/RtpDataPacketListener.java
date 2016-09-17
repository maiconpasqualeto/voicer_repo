/**
 * 
 */
package br.com.skylane.voicer.rtp;

import java.net.DatagramPacket;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;

import com.biasedbit.efflux.packet.DataPacket;

import br.com.skylane.voicer.udp.PacketReceivedListener;

/**
 * @author maicon
 *
 */
public class RtpDataPacketListener implements PacketReceivedListener {

	@Override
	public void processDatagramPacket(DatagramPacket pct) {
		
		ChannelBuffer buffer = ChannelBuffers.buffer(pct.getLength());
		buffer.writeBytes(pct.getData(), 0, pct.getLength());
		
		DataPacket dp = DataPacket.decode(buffer);
		
		byte[] packet = dp.getData().array();
		
		int x = packet.length;
		
		System.out.println("ok");
	}

}
