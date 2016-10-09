package com.msci.cpx.network;

import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

import org.jboss.netty.bootstrap.ClientBootstrap;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFactory;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelFutureListener;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.handler.codec.string.StringDecoder;
import org.jboss.netty.handler.timeout.IdleState;
import org.jboss.netty.handler.timeout.IdleStateAwareChannelHandler;
import org.jboss.netty.handler.timeout.IdleStateEvent;
import org.jboss.netty.handler.timeout.IdleStateHandler;
import org.jboss.netty.util.HashedWheelTimer;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.msci.cpx.service.ClientService;
import com.msci.cpx.utility.Helper;
import com.msci.cpx.utility.PrefUtils;
import com.msci.cpx.utility.Constants;
import com.msci.cpx.view.ActivityMainCpx;

public class CpxClient {

	private ClientBootstrap bootstrap;
	private Channel clientChannel;
	private Context context;
	
	private ChannelFuture cf;
	private String serverIP;
	private ClientService clientService;
	private HashedWheelTimer timer = new HashedWheelTimer();
/**
 * Constructor method, responsible for setting up Client Connect  
 * 
 */
	public CpxClient(Context context, String serverIP,ClientService clientService) {
		this.context = context;
		this.serverIP = serverIP;
		this.clientService = clientService;
	}
	
	
	/**
	 * Responsibility
	 * - Establish Connection to Server using Netty
	 * 	* Initialize {@link ChannelFactory}, Set {@link ChannelPipelineFactory} and add {@link StringDecoder} , {@link CpxClientHandler} to {@link ChannelPipeline}
	 * 	* after extablish connection, it will trigger Broadcast Intent to send Broadcast to {@link ActivityMainCpx}
	 */
	public void connectClient() {
		ChannelFactory channelFactory = Helper.getInstance().createNioClientSocketChannelFactory(
				Executors.newCachedThreadPool(),
				Executors.newCachedThreadPool());
		ChannelPipelineFactory pipelineFactory = new ChannelPipelineFactory() {
					public ChannelPipeline getPipeline() throws Exception {
						ChannelPipeline channelPipeline = Channels.pipeline(
								new CommandEncoder(),
								new CommandDecoder(),
								new CpxClientHandler(clientService),
								new IdleStateHandler(timer, 10, 0, 0),
								new TimeoutHandler());

						return channelPipeline;
					};
				};
		bootstrap = Helper.getInstance().createClientBootstrap(channelFactory);
		bootstrap.setPipelineFactory(pipelineFactory);
		bootstrap.setOption("connectTimeoutMillis", 10000);
		
		InetSocketAddress addressToConnectTo = Helper.getInstance().createInetSocketAddress(serverIP, Constants.CONNECT_PORT);
		
		Intent broadcastIntent = Helper.getInstance().createIntent(Constants.BROADCAST_CLIENT);
		cf = bootstrap.connect(addressToConnectTo);
		clientChannel = cf.awaitUninterruptibly().getChannel();
		PrefUtils.getInstance().setClientStatus(context, true);
		PrefUtils.getInstance().setAppKey(context, new String(Constants.APP_KEY));
		context.sendBroadcast(broadcastIntent);		
	}


	/**
	 * Responsible
	 * - Stop Connection to Server
	 * * after Connection Stop send Broadcast Intent to {@link ActivityMainCpx}
	 */
	public void stop() {
		clientChannel.close().awaitUninterruptibly();
        bootstrap.releaseExternalResources();
		PrefUtils.getInstance().setClientStatus(context, false);
//		Intent broadcastIntent = new Intent(ShareNoteConst.BROADCAST_CLIENT);
//		broadcastIntent.putExtra(ShareNoteConst.BundleKey.CLIENT_STATUS, ShareNoteConst.ClientStatus.DISCONNECTED);
//		context.sendBroadcast(broadcastIntent);
	}
	
	public void sendNoteToServer(Command note) {
		
		cf = clientChannel.write(note);
		cf.addListener(new ChannelFutureListener() {
			@Override
			public void operationComplete(ChannelFuture f) throws Exception {
				if (f.getCause() != null) {
					Log.e("sendNoteToServer", f.getCause().getMessage());
				}
				else {
					Log.e("sendNoteToServer", f.toString());
				}
			}
		});
	}
	
	private class TimeoutHandler extends IdleStateAwareChannelHandler {
		@Override
		public void channelIdle(ChannelHandlerContext ctx, IdleStateEvent e)
				throws Exception {
			 if (e.getState() == IdleState.READER_IDLE)
	             e.getChannel().close();
		}
	}
	
}
