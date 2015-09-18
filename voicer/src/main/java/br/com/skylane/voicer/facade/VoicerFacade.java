/**
 * 
 */
package br.com.skylane.voicer.facade;


/**
 * @author maicon
 *
 */
public class VoicerFacade {
	
	private static VoicerFacade facade;
	
	public static VoicerFacade getInstance() {
		if (facade == null)
			facade = new VoicerFacade();
		return facade;
	}
	
	

}
