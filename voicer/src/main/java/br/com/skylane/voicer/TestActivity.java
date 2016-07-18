/**
 * 
 */
package br.com.skylane.voicer;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import br.com.skylane.voicer.udp.UDPClient;
import br.com.skylane.voicer.udp.UDPServer;

/**
 * @author maicon
 *
 */
public class TestActivity extends Activity {
	
	private BroadcastReceiver rec;

	
	protected void onCreate(android.os.Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.udp_test);
		
		UDPServer s = new UDPServer(this);
        s.runUdpServer();
        
        
        final EditText txtReceive = (EditText) findViewById(R.id.txtReceive);
        txtReceive.append(getIpAddress());
        
        final EditText txtSend = (EditText) findViewById(R.id.txtSend);
        
        Log.d(VoicerHelper.TAG, getIpAddress());
        
        Button btnSend = (Button) findViewById(R.id.btnSend);
        btnSend.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				UDPClient c = new UDPClient();
				c.Message = txtSend.getText().toString();
				c.NachrichtSenden();
			}
        });
        
		rec = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				String str = intent.getStringExtra("pct");
				txtReceive.append(str + "\r\n");
			}
		};
		
		IntentFilter intf = new IntentFilter("br.com.skylane.voicer.MESSAGE_RECEIVED");
        registerReceiver(rec, intf);
	};
	
	private String getIpAddress() {
        String ip = "";
        try {
            Enumeration<NetworkInterface> enumNetworkInterfaces = NetworkInterface
                    .getNetworkInterfaces();
            while (enumNetworkInterfaces.hasMoreElements()) {
                NetworkInterface networkInterface = enumNetworkInterfaces
                        .nextElement();
                Enumeration<InetAddress> enumInetAddress = networkInterface
                        .getInetAddresses();
                while (enumInetAddress.hasMoreElements()) {
                    InetAddress inetAddress = enumInetAddress.nextElement();

                    if (inetAddress.isSiteLocalAddress()) {
                        ip += "SiteLocalAddress: "
                                + inetAddress.getHostAddress() + "\n";
                    }

                }

            }

        } catch (SocketException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            ip += "Something Wrong! " + e.toString() + "\n";
        }

        return ip;
    }
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		unregisterReceiver(rec);
		
	}
}
