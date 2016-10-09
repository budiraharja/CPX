package com.msci.cpx.network;

import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelHandler;

import android.util.Log;

import com.msci.cpx.service.ClientService;
import com.msci.cpx.utility.Constants;
import com.msci.cpx.utility.PrefUtils;

public class CpxClientHandler extends SimpleChannelHandler {

	private ClientService clientService;
	
	public CpxClientHandler(ClientService clientService) {
		super();
		this.clientService = clientService;
	}
	
	@Override
	public void messageReceived(ChannelHandlerContext ctx, MessageEvent e)
			throws Exception {
		super.messageReceived(ctx, e);
		Log.i("STATUS", "masuk pesan12312312");
		//clientService.handleMessageReceived((String) e.getMessage());
		
		Command shareNoteNetCommand = (Command) e.getMessage();
		Log.i("STATUS", "get Command" + shareNoteNetCommand.getCommand());

		clientService.handleMessageReceived(shareNoteNetCommand, e);
	}

	@Override
	public void channelConnected(ChannelHandlerContext ctx,
			ChannelStateEvent e) throws Exception {
		super.channelConnected(ctx, e);
		Log.i("STATUS", "connected");
		Command clientCommand = new Command();
		clientCommand.setCommand(Command.COMMAND_JOIN);
		clientCommand.setText(PrefUtils.getInstance().getNameID(clientService) + " - " + Math.abs(e.getChannel().getId()));
		
		PrefUtils.getInstance().setNetworkID(clientService, Math.abs(e.getChannel().getId()));
		
		clientService.sendClientStatus(Constants.ClientStatus.CONNECTED, clientCommand);
		e.getChannel().write(clientCommand);
	}
	
	@Override
	public void channelDisconnected(ChannelHandlerContext ctx,
			ChannelStateEvent e) throws Exception {
		super.channelDisconnected(ctx, e);
		Log.i("STATUS", "disconnected");
		clientService.handleDisconnect();
	}
	
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e)
			throws Exception {
		Log.i("STATUS", e.toString());
		clientService.handleException(e);			
	}
	
	
}
