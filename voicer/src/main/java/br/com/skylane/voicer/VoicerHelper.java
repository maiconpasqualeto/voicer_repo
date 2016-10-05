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
	
	/**
	 * 
	 * @param src - byte array onde será buscado o índice
	 * @param param - parâmetro de busca
	 * @param offset
	 * @return
	 */
	public static int indexOf(byte[] src, byte[] param, int offset) {
        if (offset < 0) {
            return -1;
        }

        int iStr = 0;

        for (int i = offset; i < src.length; i++) {
            // se encontrar o primeiro byte, compara o restante
            if (src[i] == param[iStr]) {
                boolean encontrou = true;
                for (int j = i; j < (i + param.length); j++) {
                    if (src[j] != param[iStr++]) {
                        encontrou = false;
                        iStr = 0;
                        break;
                    }
                }
                if (encontrou) {
                    return i;
                }
            }
        }

        return -1;

    }
}
