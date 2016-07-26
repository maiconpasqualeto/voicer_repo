/**
 * 
 */
package br.com.skylane.voicer.udp;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;

/**
 * @author maicon
 *
 */
public class UdpPacket {
	
	public static final byte AUDIO = 0x01;
	public static final byte VIDEO = 0x02;
		
	private InetAddress ipSource;
	private InetAddress ipTarget;
	private int port;
	private byte type;
	private byte[] packet;
		
	public UdpPacket(byte type, byte[] packet) {
		super();
		this.type = type;
		this.packet = packet;
	}

	public InetAddress getIpSource() {
		return ipSource;
	}

	public void setIpSource(InetAddress ipSource) {
		this.ipSource = ipSource;
	}
	
	public InetAddress getIpTarget() {
		return ipTarget;
	}

	public void setIpTarget(InetAddress ipTarget) {
		this.ipTarget = ipTarget;
	}

	public byte getType() {
		return type;
	}

	public void setType(byte type) {
		this.type = type;
	}
	
	public byte[] getPacket() {
		return packet;
	}

	public void setPacket(byte[] packet) {
		this.packet = packet;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public DatagramPacket getDatagramPacket() throws IOException{
		
		byte[] ips = ipSource.getAddress();
		byte[] ipt = ipTarget.getAddress();
		int totalLenght = ips.length + ipt.length + packet.length + 1;
		ByteArrayOutputStream baos = new ByteArrayOutputStream(totalLenght);
		baos.write(type);
		baos.write(ips);
		baos.write(ipt);
		baos.write(packet);
		baos.flush();
		byte[] buff = baos.toByteArray();
		
		return new DatagramPacket(buff, buff.length, ipTarget, port);
	}
}
