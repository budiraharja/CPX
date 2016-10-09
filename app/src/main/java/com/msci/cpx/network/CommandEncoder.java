package com.msci.cpx.network;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelDownstreamHandler;

import android.util.Log;


public class CommandEncoder extends SimpleChannelDownstreamHandler {
	@Override
	public void writeRequested(ChannelHandlerContext ctx, MessageEvent e)
			throws Exception {
		Log.d("TRACE", "write requested");
		Command cmd = (Command) e.getMessage();
		ChannelBuffer buf = ChannelBuffers.buffer(53 + cmd.getLength() + cmd.getLengthIp() + cmd.getLengthObj() + cmd.getLengthObj2() + cmd.getLengthCard() + cmd.getLengthCards() + cmd.getLengthCards2());
		buf.writeByte(cmd.getCommand());
		buf.writeInt(cmd.getIndexStart());
		buf.writeInt(cmd.getIndexEnd());
		buf.writeInt(cmd.getArg());
		buf.writeInt(cmd.getArg2());
		buf.writeInt(cmd.getArg3());
		buf.writeInt(cmd.isBool() ? 1 : 0);
		buf.writeInt(cmd.getLength());
		if(cmd.getLength() > 0) {
			buf.writeBytes(cmd.getText().getBytes());
		}
		buf.writeInt(cmd.getLengthIp());
		if(cmd.getLengthIp() > 0) {
			buf.writeBytes(cmd.getIp().getBytes());
		}
		buf.writeInt(cmd.getLengthObj());
		if(cmd.getLengthObj() > 0) {
			buf.writeBytes(cmd.getObjectBytes(cmd.getObj()));
		}
		buf.writeInt(cmd.getLengthObj2());
		if(cmd.getLengthObj2() > 0) {
			buf.writeBytes(cmd.getObjectBytes(cmd.getObj2()));
		}
		buf.writeInt(cmd.getLengthCard());
		if(cmd.getLengthCard() > 0) {
			buf.writeBytes(cmd.getCardBytes());
		}
		buf.writeInt(cmd.getLengthCards());
		if(cmd.getLengthCards() > 0) {
			buf.writeBytes(cmd.getCardsBytes());
		}
		buf.writeInt(cmd.getLengthCards2());
		if(cmd.getLengthCards2() > 0) {
			buf.writeBytes(cmd.getCards2Bytes());
		}
		
		Channels.write(ctx, e.getFuture(), buf);
		Log.d("TRACE", "write requested end");
	}
}
