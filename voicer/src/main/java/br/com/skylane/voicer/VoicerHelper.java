/**
 * 
 */
package br.com.skylane.voicer;

import java.util.Locale;

/**
 * @author maicon
 *
 */
public class VoicerHelper {
	
	public static final boolean VERBOSE = false;
	public static final String TAG = "VOICER";	

	
	/**
	 * 
	 * @param bytes
	 * @return
	 */
	public static String converteDadosBinariosParaStringHexa(byte[] bytes) {
        StringBuilder hex = new StringBuilder();
        int i = 0;
        while (i < bytes.length) {
            String str = Integer.toHexString(bytes[i++] & 0xFF);
            hex.append(str.length() >= 2 ? str : "0" + str);
        }
        return hex.toString().toUpperCase(Locale.getDefault());
    }
}
