/**
 * 
 */
package br.com.skylane.voicer.udp;

/**
 * @author maicon
 *
 */
public enum PayloadType {
	
	AUDIO(8), 
	VIDEO(96);
	
	private int value;
	
	PayloadType(int value) {
		this.value = value;
	}

	public int getValue() {
		return value;
	}
	
}
