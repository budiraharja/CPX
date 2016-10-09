package com.msci.cpx.network;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableEntryException;
import java.security.cert.CertificateException;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Executors;

import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.channel.group.ChannelGroup;
import org.jboss.netty.channel.group.DefaultChannelGroup;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.msci.cpx.service.ServerService;
import com.msci.cpx.utility.PrefUtils;
import com.msci.cpx.utility.Constants;

/**
 * Class ServerConnect
 * @author MSCI
 *
 */
public class CpxServer {
	private ServerBootstrap bootstrap;
	private Channel serverChannel;
	private ChannelGroup channelGroup;
	private Context context;
	private CpxServerHandler shareNoteServerHandler;
	private Timer pingTimer = null;
	
	public CpxServer(Context context, ServerService serverService) {
		this.context = context;
	}

	public void bootServer() {

		channelGroup = new DefaultChannelGroup();
		bootstrap = new ServerBootstrap(new NioServerSocketChannelFactory(
				Executors.newCachedThreadPool(),
				Executors.newCachedThreadPool()));
		shareNoteServerHandler = new CpxServerHandler(CpxServer.this);
		bootstrap.setPipelineFactory(new ChannelPipelineFactory() {
			public ChannelPipeline getPipeline() throws Exception {
				ChannelPipeline channelPipeline = Channels.pipeline(
						new CommandEncoder(),
						new CommandDecoder(),
						shareNoteServerHandler );

				return channelPipeline;
			};
		});

		serverChannel = bootstrap.bind(new InetSocketAddress("0.0.0.0", Constants.CONNECT_PORT));
		channelGroup.add(serverChannel);
		PrefUtils.getInstance().setServerStatus(context, true);
		PrefUtils.getInstance().setNetworkID(context, 1);
		PrefUtils.getInstance().setAppKey(context, new String(Constants.APP_KEY));

		Log.i("server", "started");

		Intent broadcastIntent = new Intent(Constants.BROADCAST_SERVER);
		broadcastIntent.putExtra(Constants.BundleKey.SERVER_STATUS, Constants.ServerStatus.SERVER_STARTED);
		broadcastIntent.putExtra(Constants.BundleKey.SERVER_ID, Math.abs(serverChannel.getId()));
		
		context.sendBroadcast(broadcastIntent);
		
		startKeepAlive();
	}
	
	public void startKeepAlive() {
		if (pingTimer != null) {
			pingTimer.cancel();
		}
		pingTimer = new Timer();
		pingTimer.scheduleAtFixedRate(new TimerTask() {
			
			@Override
			public void run() {
				if (channelGroup != null) {
					Command c = new Command();
					c.setCommand(Command.COMMAND_KEEP_ALIVE);
					sendBroadcastNote(c);
				}
			}
		}, 1000,1000);
	}

	public void stop() {
		channelGroup.close();
		if (pingTimer != null) {
			pingTimer.cancel();
		}
		bootstrap.releaseExternalResources();
		shareNoteServerHandler.getParticipantList().clear();
		PrefUtils.getInstance().setServerStatus(context, false);
		Intent broadcastIntent = new Intent(Constants.BROADCAST_SERVER);
		broadcastIntent.putExtra(Constants.BundleKey.SERVER_STATUS, Constants.ServerStatus.SERVER_STOPPED);
		context.sendBroadcast(broadcastIntent);
	}
	
	public ChannelGroup getChannelGroup() {
		return this.channelGroup;
	}

	public void sendBroadcastNote(Command note) {
		channelGroup.write(note);
	}
	
	public void sendBroadcastNote(Command note, String ip) {
		channelGroup.write(note, new InetSocketAddress(ip, Constants.CONNECT_PORT));
	}
	
	public Context getContext() {
		return context;
	}
}
