/**
 * 
 */
package br.com.skylane.voicer;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.util.Enumeration;

import com.biasedbit.efflux.participant.RtpParticipant;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import br.com.skylane.voicer.udp.UDPControl;
import br.com.skylane.voicer.udp.UdpPacket;

/**
 * @author maicon
 *
 */
public class TestActivity extends Activity {
	
	private BroadcastReceiver rec;
	private UDPControl control; 

	
	protected void onCreate(android.os.Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.udp_test);
		
		
		InetAddress ipTarget = null;
		InetAddress ipSource = null;
		try {
			//ip = InetAddress.getByName("192.168.25.131");
			ipSource = InetAddress.getByName("127.0.0.1");
			ipTarget = InetAddress.getByName("192.168.25.33");
		} catch (UnknownHostException e) {
			Log.e(VoicerHelper.TAG, "Erro ao pegar o ip", e);
		}
		control = new UDPControl(RtpParticipant.createReceiver("192.168.21.58", 5006, 5007));
        
        
        final EditText txtReceive = (EditText) findViewById(R.id.txtReceive);
        txtReceive.append(getIpAddress().getHostAddress());
        
        final EditText txtSend = (EditText) findViewById(R.id.txtSend);
        
        Log.d(VoicerHelper.TAG, getIpAddress().getHostAddress());
        
        Button btnSend = (Button) findViewById(R.id.btnSend);
        btnSend.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				try {
					ByteBuffer bb = ByteBuffer.allocate(20);
					bb.put(getIpAddress().getAddress());
					bb.putShort((short)5006);
					bb.flip();
					
					byte[] bytes = new byte[bb.remaining()];
					bb.get(bytes, 0, bytes.length);
					
					DatagramPacket dp = new DatagramPacket(bytes, bytes.length);
					
					dp.setAddress(InetAddress.getByName("54.94.172.118"));
					dp.setPort(1050);
					control.send(dp);
					
				} catch (UnknownHostException e) {
					Log.e(VoicerHelper.TAG, "Erro ao enviar o pacote", e);
				}
			}
        });
        
		rec = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				byte[] pacote = intent.getExtras().getByteArray("pct");
				txtReceive.append(new String(pacote) + "\r\n");
			}
		};
		
		IntentFilter intf = new IntentFilter("br.com.skylane.voicer.MESSAGE_RECEIVED");
        registerReceiver(rec, intf);
	};
	
	private InetAddress getIpAddress() {
        InetAddress ip = null;
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
                        ip = inetAddress;
                    }

                }

            }

        } catch (SocketException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();            
        }

        return ip;
    }
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		unregisterReceiver(rec);
		control.close();
	}
}
