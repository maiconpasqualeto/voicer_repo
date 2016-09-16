/**
 * 
 */
package br.com.sixinf.voicer.webrtc;

import org.webrtc.PeerConnectionFactory;
import org.webrtc.VideoCapturerAndroid;

import br.com.sixinf.voicer.Voicer;
import android.util.Log;

/**
 * @author maicon
 *
 */
public class RTCService {
	
	private static RTCService rtc;
	
	public static RTCService getInstance() {
		if (rtc == null)
			rtc = new RTCService();
		return rtc;
	}
	
	/**
	 * 
	 */
	public void startupService() {
		boolean ok = PeerConnectionFactory.initializeAndroidGlobals(
				Voicer.getAppContext(),
			    true,
			    true,
			    true,
			    null);
		Log.d("VOICER", "*********** Serviço WebRTC startado? " + ok);
	}
	
	/**
	 * 
	 */
	public void videoCapturer() {
		/*VideoCapturerAndroid.getDeviceCount();

		// Returns the front face device name
		VideoCapturerAndroid.getNameOfFrontFacingDevice();
		// Returns the back facing device name
		VideoCapturerAndroid.getNameOfBackFacingDevice();

		// Creates a VideoCapturerAndroid instance for the device name
		VideoCapturerAndroid.create(name);*/
	}

}
