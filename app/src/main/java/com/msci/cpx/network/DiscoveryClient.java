package com.msci.cpx.network;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketTimeoutException;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.msci.cpx.utility.Helper;
import com.msci.cpx.utility.Constants;

public class DiscoveryClient extends Thread {
	private final static String TAG = "ShareNote."
			+ DiscoveryClient.class.getSimpleName();

	private final static int SO_TIMEOUT = 1000; // 1 second
	private final static int WAIT_TIMEOUT = 10; // total waiting 10 second
	
	private final static Object lock = new Object(); // lock object to ensure 'run' method executed by one thread only
	
	private DatagramSocket socket;
	private Context context;
	private int waiting;
	
	public DiscoveryClient(Context ctx) {
		this.context = ctx;
	}

	public void stopDiscovery() {
		waiting = 0;
	}
	
	public void run() {
		synchronized(lock) {
			//int serverFound = 0;
			try {
				socket = new DatagramSocket(Constants.CONNECT_PORT);
				socket.setBroadcast(true);
				socket.setSoTimeout(SO_TIMEOUT); // socket timeout
				waiting = WAIT_TIMEOUT;
				
				String data = "PING";
				Log.d(TAG, "Sending data " + data);
	
				DatagramPacket packet = new DatagramPacket(data.getBytes(),
						data.length(), InetAddress.getByName("255.255.255.255"),
						Constants.CONNECT_PORT);
				socket.send(packet);
	
				byte[] buf = new byte[1024];
				while (waiting > 0) {
					Log.d(TAG, "waiting next response...");
					try {
						DatagramPacket packet2 = new DatagramPacket(buf, buf.length);
						socket.receive(packet2);
						String s = new String(packet2.getData(), 0, packet2.getLength());
						Log.d(TAG, "got response: " + s);
						if (s.startsWith("PING-SERVER")) {
							//++serverFound;
							Intent broadcastIntent = Helper.getInstance().createIntent(Constants.BROADCAST_PING);
							broadcastIntent.putExtra(Constants.ClientCommand.TEXT, s.substring(11).replace(";", " - "));
							context.sendBroadcast(broadcastIntent);
						}
					}catch(SocketTimeoutException e) {
						--waiting;
					}
					
				}
			} catch (Exception ex) {
				Log.e(TAG, "", ex);
			} finally {
				socket.close();
				
				Intent broadcastIntent = Helper.getInstance().createIntent(Constants.BROADCAST_PING);
				broadcastIntent.putExtra(Constants.ClientCommand.TEXT, Constants.PING_FINISH);
				context.sendBroadcast(broadcastIntent);
				//setButtonConnectEnable(serverFound);
			}
		}
	}
//	
//	public void setButtonConnectEnable(int serverFound) {
//		Intent broadcastIntent =  Helper.getInstance().createIntent(ShareNoteConst.BROADCAST_CLIENT);
//		broadcastIntent.putExtra(ShareNoteConst.BundleKey.CLIENT_STATUS, ShareNoteConst.ClientStatus.INCOMING_MESSAGE);
//		broadcastIntent.putExtra(ShareNoteConst.BundleKey.CLIENT_COMMAND, Command.COMMAND_ENABLE_CONNECT);
//		broadcastIntent.putExtra(ShareNoteConst.BundleKey.SERVER_FOUND, serverFound);
//		context.sendBroadcast(broadcastIntent);
//	}
}
