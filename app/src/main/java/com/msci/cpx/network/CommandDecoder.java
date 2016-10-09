package com.msci.cpx.network;

import java.nio.charset.Charset;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.handler.codec.replay.ReplayingDecoder;
import org.jboss.netty.handler.codec.replay.VoidEnum;


/**
 * Class decoder object ShareNoteNetCommand from stream network
 * @author MSCI a.hendrawan / faren.f
 *
 */
public class CommandDecoder extends ReplayingDecoder<VoidEnum> {

	@Override
	protected Object decode(ChannelHandlerContext channelHandlerContext, Channel channel,
			ChannelBuffer channelBuffer, VoidEnum state) throws Exception {

		Command cmd = new Command();
		cmd.setCommand(channelBuffer.readByte());
		cmd.setIndexStart(channelBuffer.readInt());
		cmd.setIndexEnd(channelBuffer.readInt());
		cmd.setArg(channelBuffer.readInt());
		cmd.setArg2(channelBuffer.readInt());
		cmd.setArg3(channelBuffer.readInt());
		cmd.setBool(channelBuffer.readInt() == 1 ? true : false);
		cmd.setLength(channelBuffer.readInt());
		cmd.setText(new String(channelBuffer.readBytes(cmd.getLength()).array(), Charset.defaultCharset()));
		cmd.setLengthIp(channelBuffer.readInt());
		cmd.setIp(new String(channelBuffer.readBytes(cmd.getLengthIp()).array(), Charset.defaultCharset()));
		cmd.setLengthObj(channelBuffer.readInt());
		cmd.setObj(cmd.getObjectFromBytes(channelBuffer.readBytes(cmd.getLengthObj()).array()));
		cmd.setLengthObj2(channelBuffer.readInt());
		cmd.setObj2(cmd.getObjectFromBytes(channelBuffer.readBytes(cmd.getLengthObj2()).array()));
		cmd.setLengthCard(channelBuffer.readInt());
		cmd.setCard(cmd.getCardFromBytes(channelBuffer.readBytes(cmd.getLengthCard()).array()));
		cmd.setLengthCards(channelBuffer.readInt());
		cmd.setCards(cmd.getCardsFromBytes(channelBuffer.readBytes(cmd.getLengthCards()).array()));
		cmd.setLengthCards2(channelBuffer.readInt());
		cmd.setCards2(cmd.getCards2FromBytes(channelBuffer.readBytes(cmd.getLengthCards2()).array()));
		
		return cmd;
	}

}
