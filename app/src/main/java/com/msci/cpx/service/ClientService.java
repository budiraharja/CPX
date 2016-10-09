package com.msci.cpx.service;

import java.net.SocketException;
import java.util.ArrayList;

import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.MessageEvent;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import com.msci.cpx.model.Participant;
import com.msci.cpx.model.Rule;
import com.msci.cpx.network.Command;
import com.msci.cpx.network.CpxClient;
import com.msci.cpx.utility.Constants;
import com.msci.cpx.utility.Helper;
import com.msci.cpx.utility.PrefUtils;

public class ClientService extends Service {
	
	private final IBinder iBinder = new ClientLocalBinder();

	private final static String TAG = ClientService.class.getSimpleName(); 
	
	private CpxClient cpxClient;

	@Override
	public IBinder onBind(Intent intent) {
		return iBinder;
	}
	
	public class ClientLocalBinder extends Binder {
		public ClientService getService() {
			return ClientService.this;
		}
	}

	/**
	 * Responsible: - Check if application is already connected - if not
	 * connected it will call {@link #connectClient(String)} - if it's connected
	 * do nothing
	 * 
	 */
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		super.onStartCommand(intent, flags, startId);

		boolean clientUp = PrefUtils.getInstance().getClientStatus(this);
		String serverIP = intent
				.getStringExtra(Constants.BundleKey.SERVER_IP);
		if (!clientUp) {
			Log.i(TAG, "connect: " + serverIP);
			connect(serverIP);
		}

		return START_STICKY;
	}

	public void connect(final String serverIP) {
		new Thread(new Runnable() {

			// Handle Jelly Bean Exception
			public void run() {
				cpxClient = Helper.getInstance().createClientConnect(ClientService.this, serverIP, ClientService.this);
				cpxClient.connectClient();
			}
		}).start();

	}

	/*
	public void handleMessageReceived(String message) {

		Log.i("STATUS", "Message");
		Intent broadcastIntent = new Intent(ShareNoteConst.BROADCAST_CLIENT);
		broadcastIntent.putExtra(ShareNoteConst.BundleKey.CLIENT_STATUS,
				ShareNoteConst.ClientStatus.INCOMING_MESSAGE);
		broadcastIntent
				.putExtra(ShareNoteConst.BundleKey.CONTENT_TEXT, message);
		this.sendBroadcast(broadcastIntent);
	}
	*/

	public void handleMessageReceived(Command cmd, MessageEvent e) {

		Log.i("TRACE", "messageReceived");
		Intent broadcastIntent = Helper.getInstance().createIntent(Constants.BROADCAST_CLIENT);
		broadcastIntent.putExtra(Constants.BundleKey.CLIENT_STATUS,
				Constants.ClientStatus.INCOMING_MESSAGE);
		broadcastIntent.putExtra(Constants.BundleKey.CLIENT_COMMAND,
				cmd.getCommand());
		switch (cmd.getCommand()) {
		case Command.COMMAND_DELETE:

			broadcastIntent.putExtra(Constants.ClientCommand.INDEX_START,
					cmd.getIndexStart());
			broadcastIntent.putExtra(Constants.ClientCommand.INDEX_END,
					cmd.getIndexEnd());
			break;
		case Command.COMMAND_RESET_NOTE:
			broadcastIntent.putExtra(Constants.ClientCommand.TEXT,
					cmd.getText());
			break;
		case Command.COMMAND_INSERT:
			//broadcastIntent.putExtra(Constants.ClientCommand.INDEX_START,
			//		shareNoteNetCommand.getIndexStart());
			broadcastIntent.putExtra(Constants.ClientCommand.TEXT,
					cmd.getText());
			break;
		case Command.COMMAND_REPLACE:

			broadcastIntent.putExtra(Constants.ClientCommand.INDEX_START,
					cmd.getIndexStart());
			broadcastIntent.putExtra(Constants.ClientCommand.INDEX_END,
					cmd.getIndexEnd());
			broadcastIntent.putExtra(Constants.ClientCommand.TEXT,
					cmd.getText());
			break;
		case Command.COMMAND_PING:
		case Command.COMMAND_PARTICIPANT_LIST:
			broadcastIntent.putExtra(Constants.ClientCommand.TEXT,
					cmd.getText());
			break;
		case Command.COMMAND_MAX_CLIENT_REACHED:
		{
			sendClientStatus(Constants.ClientStatus.SERVER_MAX_CONNECTION_REACHED);
			e.getChannel().close();
			break;
		}
		case Command.COMMAND_INSERT_DECK:
			broadcastIntent.putExtra(Constants.BundleKey.CLIENT_STATUS,
					Constants.ClientStatus.CARD_PLAY);
			broadcastIntent.putExtra(Constants.ClientCommand.TEXT,
					cmd.getDeckEngine());
			break;
		case Command.COMMAND_REQUEST_DECK_INIT:
			broadcastIntent.putExtra(Constants.BundleKey.CLIENT_STATUS,
					Constants.ClientStatus.CARD_PLAY);
			broadcastIntent.putExtra(Constants.ClientCommand.CARD,
					cmd.getCards());
			broadcastIntent.putExtra(Constants.ClientCommand.TEXT,
					cmd.getText());
			broadcastIntent.putExtra(Constants.ClientCommand.IP,
					cmd.getIp());
			broadcastIntent.putExtra(Constants.ClientCommand.ARG,
					cmd.getArg());
			broadcastIntent.putExtra(Constants.ClientCommand.OBJ,
					(Rule)cmd.getObj());
			broadcastIntent.putExtra(Constants.ClientCommand.ARG2,
					cmd.getArg2());
			broadcastIntent.putExtra(Constants.ClientCommand.OBJ2,
					(ArrayList<Participant>)cmd.getObj2());
			break;
		case Command.COMMAND_SET_PLAY:
			broadcastIntent.putExtra(Constants.BundleKey.CLIENT_STATUS,
					Constants.ClientStatus.CARD_PLAY);
			broadcastIntent.putExtra(Constants.ClientCommand.CARD,
					cmd.getCard());
			broadcastIntent.putExtra(Constants.ClientCommand.TEXT,
					cmd.getText());
			broadcastIntent.putExtra(Constants.ClientCommand.ARG,
					cmd.getArg());
			broadcastIntent.putExtra(Constants.ClientCommand.ARG2,
					cmd.getArg2());
			broadcastIntent.putExtra(Constants.ClientCommand.ARG3,
					cmd.getArg3());
			break;
		case Command.COMMAND_SET_DRAW:
			broadcastIntent.putExtra(Constants.BundleKey.CLIENT_STATUS,
					Constants.ClientStatus.CARD_PLAY);
			broadcastIntent.putExtra(Constants.ClientCommand.CARD,
					cmd.getCard());
			broadcastIntent.putExtra(Constants.ClientCommand.TEXT,
					cmd.getText());
			broadcastIntent.putExtra(Constants.ClientCommand.ARG,
					cmd.getArg());
			broadcastIntent.putExtra(Constants.ClientCommand.BOOLEAN,
					cmd.isBool());
			break;
		case Command.COMMAND_SET_NEXT_TURN:
			broadcastIntent.putExtra(Constants.BundleKey.CLIENT_STATUS,
					Constants.ClientStatus.CARD_PLAY);
			broadcastIntent.putExtra(Constants.ClientCommand.TEXT,
					cmd.getText());
			broadcastIntent.putExtra(Constants.ClientCommand.ARG,
					cmd.getArg());
			broadcastIntent.putExtra(Constants.ClientCommand.ARG2,
					cmd.getArg2());
			broadcastIntent.putExtra(Constants.ClientCommand.ARG3,
					cmd.getArg3());
			break;
		case Command.COMMAND_SET_DROP_CARD:
			broadcastIntent.putExtra(Constants.BundleKey.CLIENT_STATUS,
					Constants.ClientStatus.CARD_PLAY);
			broadcastIntent.putExtra(Constants.ClientCommand.TEXT,
					cmd.getText());
			broadcastIntent.putExtra(Constants.ClientCommand.ARG,
					cmd.getArg());
			broadcastIntent.putExtra(Constants.ClientCommand.ARG2,
					cmd.getArg2());
			broadcastIntent.putExtra(Constants.ClientCommand.ARG3,
					cmd.getArg3());
			broadcastIntent.putExtra(Constants.ClientCommand.CARD,
					cmd.getCard());
			break;
		case Command.COMMAND_SET_FINISH_GAME:
			broadcastIntent.putExtra(Constants.BundleKey.CLIENT_STATUS,
					Constants.ClientStatus.CARD_PLAY);
			break;
		case Command.COMMAND_SET_SCORE:
			broadcastIntent.putExtra(Constants.BundleKey.CLIENT_STATUS,
					Constants.ClientStatus.CARD_PLAY);
			broadcastIntent.putExtra(Constants.ClientCommand.TEXT,
					cmd.getText());
			broadcastIntent.putExtra(Constants.ClientCommand.ARG,
					cmd.getArg());
			broadcastIntent.putExtra(Constants.ClientCommand.ARG2,
					cmd.getArg2());
			break;
		case Command.COMMAND_SET_REQUEST_CARD_OTHER:
			broadcastIntent = Helper.getInstance().createIntent(
					Constants.BROADCAST_SERVER);
			broadcastIntent.putExtra(Constants.BundleKey.CLIENT_COMMAND,
					cmd.getCommand());
			broadcastIntent.putExtra(Constants.ClientCommand.ARG,
					cmd.getArg());
			broadcastIntent.putExtra(Constants.ClientCommand.ARG2,
					cmd.getArg2());
			break;
		case Command.COMMAND_SET_REQUEST_CARD_OTHER_BACK:
			broadcastIntent = Helper.getInstance().createIntent(
					Constants.BROADCAST_SERVER);
			broadcastIntent.putExtra(Constants.BundleKey.CLIENT_COMMAND,
					cmd.getCommand());
			broadcastIntent.putExtra(Constants.ClientCommand.ARG,
					cmd.getArg());
			broadcastIntent.putExtra(Constants.ClientCommand.ARG2,
					cmd.getArg2());
			broadcastIntent.putExtra(Constants.ClientCommand.CARD,
					cmd.getCards());
			break;
		case Command.COMMAND_SET_EXCHANGE_CARD:
			broadcastIntent = Helper.getInstance().createIntent(
					Constants.BROADCAST_SERVER);
			broadcastIntent.putExtra(Constants.BundleKey.CLIENT_COMMAND,
					cmd.getCommand());
			broadcastIntent.putExtra(Constants.ClientCommand.ARG,
					cmd.getArg());
			broadcastIntent.putExtra(Constants.ClientCommand.CARD,
					cmd.getCards());
			broadcastIntent.putExtra(Constants.ClientCommand.ARG2,
					cmd.getArg2());
			broadcastIntent.putExtra(Constants.ClientCommand.CARD2,
					cmd.getCards2());
			break;
		case Command.COMMAND_SET_LOOSER:
			broadcastIntent = Helper.getInstance().createIntent(
					Constants.BROADCAST_SERVER);
			broadcastIntent.putExtra(Constants.BundleKey.CLIENT_COMMAND,
					cmd.getCommand());
			broadcastIntent.putExtra(Constants.ClientCommand.ARG,
					cmd.getArg());
			broadcastIntent.putExtra(Constants.ClientCommand.ARG2,
					cmd.getArg2());
			break;
		default:
			break;
		}

		this.sendBroadcast(broadcastIntent);
	}

	public void handleDisconnect() {
		Log.i("STATUS", "disconnected");
		PrefUtils.getInstance().setClientStatus(this, false);
		PrefUtils.getInstance().setNetworkID(this, -1);
		sendClientStatus(Constants.ClientStatus.DISCONNECTED);
		stopSelf();
	}

	public void handleException(ExceptionEvent e) {
//		Log.i("STATUS", "exception");
//		PrefUtils.getInstance().setClientStatus(this, false);
//		sendClientStatus(ShareNoteConst.ClientStatus.CONNECTION_REFUSED);
//		stopSelf();
		Log.d("TRACE", "", e.getCause());
		if (e.getCause() instanceof SocketException && e.getCause().getMessage().indexOf("Connection refused") > -1) {
			handleDisconnect();
		}
	}

	public void sendClientStatus(int status) {

		Log.i("STATUS", "send client");
		Intent broadcastIntent = Helper.getInstance().createIntent(Constants.BROADCAST_CLIENT);
		broadcastIntent
				.putExtra(Constants.BundleKey.CLIENT_STATUS, status);
		broadcastIntent
				.putExtra(Constants.BundleKey.CLIENT_ID, PrefUtils.getInstance().getNetworkID(getApplicationContext()));
		sendBroadcast(broadcastIntent);
	}
	
	public void sendClientStatus(int status, Command command) {

		Log.i("STATUS", "send client");
		Intent broadcastIntent = Helper.getInstance().createIntent(Constants.BROADCAST_CLIENT);
		broadcastIntent
				.putExtra(Constants.BundleKey.CLIENT_STATUS, status);
		broadcastIntent
				.putExtra(Constants.ClientCommand.TEXT, command.getText());
		sendBroadcast(broadcastIntent);
	}

	@Override
	public void onDestroy() {
		if (cpxClient != null) {
			cpxClient.stop();
		}
		Log.i("client", "stopped");
		super.onDestroy();

	}

	public CpxClient getShareNoteClient() {
		return cpxClient;
	}

	public void setShareNoteClient(CpxClient shareNoteClient) {
		this.cpxClient = shareNoteClient;
	}
}
