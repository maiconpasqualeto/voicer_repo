/**
 * 
 */
package br.com.sixinf.voicer;

import org.doubango.ngn.NgnApplication;
import org.webrtc.PeerConnectionFactory;

import android.content.Context;
import android.util.Log;

/**
 * @author maicon
 *
 */
public class Voicer extends NgnApplication {
	
	private static Context context;
	
	public Voicer() {
    	Log.d(Voicer.class.getName(),"VoicerClass()");    	
    }
	
	@Override
	public void onCreate() {
		super.onCreate();
		Voicer.context = getApplicationContext();
		boolean ok = PeerConnectionFactory.initializeAndroidGlobals(
				context,
			    true,
			    true,
			    true,
			    null);
		Log.d("VOICER", "*********** " + ok);
				
	}
	
	public static Context getAppContext() {
		return Voicer.context;
	}
}
