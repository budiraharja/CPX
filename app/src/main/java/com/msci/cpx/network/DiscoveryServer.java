package com.msci.cpx.network;

import java.net.DatagramPacket;
import java.net.DatagramSocket;

import android.content.Context;
import android.net.wifi.WifiManager;
import android.text.format.Formatter;
import android.util.Log;

import com.msci.cpx.utility.Constants;
import com.msci.cpx.utility.PrefUtils;

public class DiscoveryServer extends Thread {
	private final static String TAG = "ShareNote."
			+ DiscoveryServer.class.getSimpleName();
	
	DatagramSocket socket;
	private Context context;
	
	public DiscoveryServer(Context ctx) {
		this.context  = ctx;
	}
	
	public void run() {
		try {
			socket = new DatagramSocket(Constants.CONNECT_PORT);
		socket.setBroadcast(false);
		//socket.setSoTimeout(5000);
		
	    byte[] buf = new byte[1024];
	      while (true) {
	          DatagramPacket packet = new DatagramPacket(buf, buf.length);
	          socket.receive(packet);
	          String s = new String(packet.getData(), 0, packet.getLength());
	          if(s.equals("PING")) {
	        	  String data = "PING-SERVER" + PrefUtils.getInstance().getNameID(context) + ";" + getIpLocalAddress();
//	        	  String data = "servename- ip";
	        	  DatagramPacket packet2 = new DatagramPacket(data.getBytes(), data.length(), packet.getAddress(), Constants.CONNECT_PORT);
	        	  //packet2.setAddress(packet.getAddress());
	      		  socket.send(packet2);
	        	  Log.d(TAG, "sending reply");
	          }
	        }
		}catch(Exception e) {
			Log.e(TAG, "", e);
		}
	}
	
	@SuppressWarnings("deprecation")
	public String getIpLocalAddress() {
		WifiManager wim = (WifiManager) context
				.getSystemService(Context.WIFI_SERVICE);
		return Formatter.formatIpAddress(wim.getConnectionInfo().getIpAddress());
	}

	public void stopServer() {
		if(socket != null) {
			socket.close();
		}
	}
}
