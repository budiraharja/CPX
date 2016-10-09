package com.msci.cpx.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import com.msci.cpx.network.Command;
import com.msci.cpx.network.DiscoveryServer;
import com.msci.cpx.network.CpxServer;
import com.msci.cpx.utility.Helper;
import com.msci.cpx.utility.PrefUtils;

public class ServerService extends Service {

	private final IBinder iBinder = new LocalBinder();

	private CpxServer cpxServer;

	private DiscoveryServer discoveryServer;
	
	@Override
	public IBinder onBind(Intent intent) {
		return iBinder;
	}

	public class LocalBinder extends Binder {
		public ServerService getService() {
			return ServerService.this;
		}
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		super.onStartCommand(intent, flags, startId);

		boolean serverUp = PrefUtils.getInstance().getServerStatus(this);
		if (!serverUp) {
			bootServer();
		}
		return START_STICKY;
	}

	public void bootServer() {
		// Handle Jelly Bean Exception
		new Thread(new Runnable() {
			public void run() {
				cpxServer = Helper.getInstance().createServerConnect(
						ServerService.this, ServerService.this);
				cpxServer.bootServer();

				discoveryServer = new DiscoveryServer(ServerService.this);
				discoveryServer.start();
			}
		}).start();

	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		if (cpxServer != null) {
			cpxServer.stop();
		}
		if (discoveryServer != null) {
			discoveryServer.stopServer();
		}
		Log.i("server", "stopped");
	}

	public void sendBroadcastNote(Command note) {
		//if (note.getIp() != null) {
		//	cpxServer.sendBroadcastNote(note, note.getIp());
		//} else {
			cpxServer.sendBroadcastNote(note);
		//}
	}

}
