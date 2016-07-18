/**
 * 
 */
package br.com.skylane.voicer.udp;

import java.net.DatagramPacket;
import java.net.DatagramSocket;

import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;
import br.com.skylane.voicer.VoicerHelper;

/**
 * @author maicon
 *
 */
public class UDPServer {
	private AsyncTask<Void, Void, Void> async;
	private boolean Server_aktiv = true;
	
	private static final int SERVER_PORT = 1234;

	/**
	 * 
	 */
	public void runUdpServer() {
		async = new AsyncTask<Void, Void, Void>() {
			@Override
			protected Void doInBackground(Void... params) {
				byte[] lMsg = new byte[4096];
				DatagramPacket dp = new DatagramPacket(lMsg, lMsg.length);
				DatagramSocket ds = null;

				try {
					ds = new DatagramSocket(SERVER_PORT);
					
					while (Server_aktiv) {
						ds.receive(dp);
						Log.d(VoicerHelper.TAG, new String(lMsg, 0, dp.getLength()));
						
						/*Intent i = new Intent();
						i.setAction(Main.MESSAGE_RECEIVED);
						i.putExtra(Main.MESSAGE_STRING,
								new String(lMsg, 0, dp.getLength()));
						Main.MainContext.getApplicationContext().sendBroadcast(i);*/
						
					}
				} catch (Exception e) {
					Log.e(VoicerHelper.TAG, "Erro no Socket", e);
				} finally {
					if (ds != null) {
						ds.close();
					}
				}

				return null;
			}
		};
		
		if (Build.VERSION.SDK_INT >= 11)
			async.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
		else
			async.execute();
	}

	/**
	 * 
	 */
	public void stop_UDP_Server() {
		Server_aktiv = false;
	}
}
