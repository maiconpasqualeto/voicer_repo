/**
 * 
 */
package br.com.skylane.voicer.udp;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import br.com.skylane.voicer.VoicerHelper;
import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;

/**
 * @author maicon
 *
 */
public class UDPClient {
	private AsyncTask<Void, Void, Void> async_cient;
	public String Message;
	
	private static final int SERVER_PORT = 1234;
	private InetAddress BroadcastAddress;

	@SuppressLint("NewApi")
	public void NachrichtSenden() {		
		async_cient = new AsyncTask<Void, Void, Void>() {
			@Override
			protected Void doInBackground(Void... params) {
				DatagramSocket ds = null;

				try {
					
					BroadcastAddress = InetAddress.getByName("192.168.25.131");
					
					ds = new DatagramSocket();
					DatagramPacket dp;
					dp = new DatagramPacket(Message.getBytes(),
							Message.length(), BroadcastAddress,
							SERVER_PORT);
					ds.setBroadcast(true);
					ds.send(dp);
				} catch (Exception e) {
					Log.e(VoicerHelper.TAG, "erro ao enviar pacote udp", e);
				} finally {
					if (ds != null) {
						ds.close();
					}
				}
				return null;
			}

			protected void onPostExecute(Void result) {
				super.onPostExecute(result);
			}
		};

		if (Build.VERSION.SDK_INT >= 11)
			async_cient.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
		else
			async_cient.execute();
	}
}