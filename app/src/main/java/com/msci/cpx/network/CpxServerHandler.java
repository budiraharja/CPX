package com.msci.cpx.network;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelHandler;

import android.content.Intent;
import android.util.Log;

import com.msci.cpx.utility.Helper;
import com.msci.cpx.utility.Constants;

/**
 * Class encoder object ShareNoteNetCommand into stream network
 * 
 * @author MSCI a.hendrawan / faren.f
 * 
 */
public class CpxServerHandler extends SimpleChannelHandler {
	private CpxServer cpxServer;
	private final int MAX_CONN = 20;
	private Map<String, String> participantList;

	public CpxServerHandler(CpxServer shareNoteServer) {

		this.cpxServer = shareNoteServer;
		participantList = new HashMap<String, String>();
	}

	@Override
	public void channelConnected(ChannelHandlerContext ctx, ChannelStateEvent e)
			throws Exception {
		super.channelConnected(ctx, e);
		Log.e("TAG", "connected");
//		if (shareNoteServer.getChannelGroup().size() > MAX_CONN) {
//			e.getChannel().close();
//		}

	}

	@Override
	public void channelDisconnected(ChannelHandlerContext ctx,
			ChannelStateEvent e) throws Exception {
		cpxServer.getChannelGroup().remove(e.getChannel());
		if(participantList.remove(e.getChannel().getRemoteAddress().toString()) != null)
		sendParticipantListToClient();
		Log.i("TRACE", "disconnected "
				+ e.getChannel().getRemoteAddress().toString());

		super.channelDisconnected(ctx, e);
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e)
			throws Exception {
		// super.exceptionCaught(ctx, e);
		Log.d("TRACE", "", e.getCause());
	}

	private String getParticipantListString() {
		String participant = "";

		if (participantList.size() > 0) {
			Iterator<String> iterator = participantList.keySet().iterator();

			participant = participantList.get(iterator.next());
			while (iterator.hasNext()) {
				participant = participant + ";"
						+ participantList.get(iterator.next());

			}
		}

		return participant;
	}

	/**
	 * Responsibility Receive Message from client , if
	 * {@link Command#COMMAND_JOIN} , reply with
	 * {@link Command#COMMAND_RESET_NOTE} then broadcast to all client
	 */
	@Override
	public void messageReceived(ChannelHandlerContext ctx, MessageEvent e)
			throws Exception {
		Log.e("ShareNoteServerHandler", "messageReceived");
		super.messageReceived(ctx, e);
		Command cmd = (Command) e.getMessage();
		
		switch (cmd.getCommand()) {
		case Command.COMMAND_JOIN:
			if (cpxServer.getChannelGroup().size() > MAX_CONN) {
				Command shareNoteNetCommand = Helper.getInstance()
						.createShareNoteNetCommand();
				shareNoteNetCommand.setCommand(Command.COMMAND_MAX_CLIENT_REACHED);
				e.getChannel().write(shareNoteNetCommand);
			}
			else {
				
				Command shareNoteNetCommand = Helper.getInstance()
						.createShareNoteNetCommand();
				cpxServer.getChannelGroup().add(e.getChannel());
				
				shareNoteNetCommand.setIndexStart(0);
				shareNoteNetCommand.setIndexEnd(0);
				//String sourceTemp = Helper.getInstance().getInitEditText()
				//		.getText().toString();
				//shareNoteNetCommand.setText(sourceTemp);
				shareNoteNetCommand.setCommand(Command.COMMAND_RESET_NOTE);
				
				e.getChannel().write(shareNoteNetCommand);
	
				participantList.put(e.getRemoteAddress().toString(), cmd.getText());
				if (participantList.size() > 0) {
					sendParticipantListToClient();
	
				}
			}
			break;

		// TODO
		case Command.COMMAND_INSERT:
			Intent broadcastIntent = Helper.getInstance().createIntent(
					Constants.BROADCAST_SERVER);
			broadcastIntent.putExtra(Constants.BundleKey.CLIENT_COMMAND,
					Command.COMMAND_INSERT);
			//broadcastIntent.putExtra(Constants.ClientCommand.INDEX_START, cmd.getIndexStart());
			broadcastIntent.putExtra(Constants.ClientCommand.TEXT, cmd.getText());

			cpxServer.getContext().sendBroadcast(broadcastIntent);
			cpxServer.sendBroadcastNote(cmd);
			break;
			
		case Command.COMMAND_DELETE:
			broadcastIntent = Helper.getInstance().createIntent(
					Constants.BROADCAST_SERVER);
			broadcastIntent.putExtra(Constants.BundleKey.CLIENT_COMMAND,
					Command.COMMAND_DELETE);
			broadcastIntent.putExtra(Constants.ClientCommand.INDEX_START, cmd.getIndexStart());
			broadcastIntent.putExtra(Constants.ClientCommand.INDEX_END, cmd.getIndexEnd());

			cpxServer.getContext().sendBroadcast(broadcastIntent);
			break;
			
		case Command.COMMAND_REPLACE:
			broadcastIntent = Helper.getInstance().createIntent(
					Constants.BROADCAST_SERVER);
			broadcastIntent.putExtra(Constants.BundleKey.CLIENT_COMMAND,
					Command.COMMAND_REPLACE);
			broadcastIntent.putExtra(Constants.ClientCommand.INDEX_START, cmd.getIndexStart());
			broadcastIntent.putExtra(Constants.ClientCommand.INDEX_END, cmd.getIndexEnd());
			broadcastIntent.putExtra(Constants.ClientCommand.TEXT, cmd.getText());

			cpxServer.getContext().sendBroadcast(broadcastIntent);
			break;
		case Command.COMMAND_INSERT_DECK:
			broadcastIntent = Helper.getInstance().createIntent(
					Constants.BROADCAST_SERVER);
			broadcastIntent.putExtra(Constants.BundleKey.CLIENT_COMMAND,
					Command.COMMAND_INSERT_DECK);
			broadcastIntent.putExtra(Constants.ClientCommand.TEXT, cmd.getDeckEngine());

			cpxServer.getContext().sendBroadcast(broadcastIntent);
			cpxServer.sendBroadcastNote(cmd);
			break;
		case Command.COMMAND_REQUEST_DECK_INIT:
			broadcastIntent = Helper.getInstance().createIntent(
					Constants.BROADCAST_SERVER);
			broadcastIntent.putExtra(Constants.BundleKey.CLIENT_COMMAND,
					cmd.getCommand());
			broadcastIntent.putExtra(Constants.ClientCommand.IP, cmd.getIp());
			broadcastIntent.putExtra(Constants.ClientCommand.TEXT, cmd.getText());

			cpxServer.getContext().sendBroadcast(broadcastIntent);
			break;
		case Command.COMMAND_REQUEST_PLAY:
			broadcastIntent = Helper.getInstance().createIntent(
					Constants.BROADCAST_SERVER);
			broadcastIntent.putExtra(Constants.BundleKey.CLIENT_COMMAND,
					cmd.getCommand());
			broadcastIntent.putExtra(Constants.ClientCommand.ARG, cmd.getArg());
			broadcastIntent.putExtra(Constants.ClientCommand.CARD, cmd.getCard());
			broadcastIntent.putExtra(Constants.ClientCommand.BOOLEAN, cmd.isBool());

			cpxServer.getContext().sendBroadcast(broadcastIntent);
			break;
		case Command.COMMAND_SET_PLAY:
			broadcastIntent = Helper.getInstance().createIntent(
					Constants.BROADCAST_SERVER);
			broadcastIntent.putExtra(Constants.BundleKey.CLIENT_COMMAND,
					cmd.getCommand());
			broadcastIntent.putExtra(Constants.ClientCommand.ARG, cmd.getArg());
			broadcastIntent.putExtra(Constants.ClientCommand.ARG2, cmd.getArg2());
			broadcastIntent.putExtra(Constants.ClientCommand.ARG3, cmd.getArg3());
			broadcastIntent.putExtra(Constants.ClientCommand.CARD, cmd.getCard());

			cpxServer.sendBroadcastNote(cmd);
			break;
		case Command.COMMAND_REQUEST_DRAW:
			broadcastIntent = Helper.getInstance().createIntent(
					Constants.BROADCAST_SERVER);
			broadcastIntent.putExtra(Constants.BundleKey.CLIENT_COMMAND,
					cmd.getCommand());
			broadcastIntent.putExtra(Constants.ClientCommand.ARG, cmd.getArg());
			broadcastIntent.putExtra(Constants.ClientCommand.BOOLEAN, cmd.isBool());

			cpxServer.getContext().sendBroadcast(broadcastIntent);
			break;
		case Command.COMMAND_SET_DRAW:
			broadcastIntent = Helper.getInstance().createIntent(
					Constants.BROADCAST_SERVER);
			broadcastIntent.putExtra(Constants.BundleKey.CLIENT_COMMAND,
					cmd.getCommand());
			broadcastIntent.putExtra(Constants.ClientCommand.ARG, cmd.getArg());
			broadcastIntent.putExtra(Constants.ClientCommand.CARD, cmd.getCard());
			broadcastIntent.putExtra(Constants.ClientCommand.BOOLEAN, cmd.isBool());
			broadcastIntent.putExtra(Constants.ClientCommand.TEXT, cmd.getText());

			cpxServer.sendBroadcastNote(cmd);
			break;
		case Command.COMMAND_REQUEST_NEXT_TURN:
			broadcastIntent = Helper.getInstance().createIntent(
					Constants.BROADCAST_SERVER);
			broadcastIntent.putExtra(Constants.BundleKey.CLIENT_COMMAND,
					cmd.getCommand());
			broadcastIntent.putExtra(Constants.ClientCommand.ARG, cmd.getArg());
			broadcastIntent.putExtra(Constants.ClientCommand.ARG2, cmd.getArg2());
			broadcastIntent.putExtra(Constants.ClientCommand.ARG3, cmd.getArg3());
			broadcastIntent.putExtra(Constants.ClientCommand.BOOLEAN, cmd.isBool());

			cpxServer.getContext().sendBroadcast(broadcastIntent);
			break;
		case Command.COMMAND_SET_NEXT_TURN:
			broadcastIntent = Helper.getInstance().createIntent(
					Constants.BROADCAST_SERVER);
			broadcastIntent.putExtra(Constants.BundleKey.CLIENT_COMMAND,
					cmd.getCommand());
			broadcastIntent.putExtra(Constants.ClientCommand.ARG, cmd.getArg());
			broadcastIntent.putExtra(Constants.ClientCommand.ARG2, cmd.getArg2());
			broadcastIntent.putExtra(Constants.ClientCommand.ARG3, cmd.getArg3());
			broadcastIntent.putExtra(Constants.ClientCommand.TEXT, cmd.getText());

			cpxServer.sendBroadcastNote(cmd);
			break;
		case Command.COMMAND_REQUEST_DROP_CARD:
			broadcastIntent = Helper.getInstance().createIntent(
					Constants.BROADCAST_SERVER);
			broadcastIntent.putExtra(Constants.BundleKey.CLIENT_COMMAND,
					cmd.getCommand());
			broadcastIntent.putExtra(Constants.ClientCommand.ARG, cmd.getArg());
			broadcastIntent.putExtra(Constants.ClientCommand.CARD, cmd.getCard());

			cpxServer.getContext().sendBroadcast(broadcastIntent);
			break;
		case Command.COMMAND_SET_DROP_CARD:
			broadcastIntent = Helper.getInstance().createIntent(
					Constants.BROADCAST_SERVER);
			broadcastIntent.putExtra(Constants.BundleKey.CLIENT_COMMAND,
					cmd.getCommand());
			broadcastIntent.putExtra(Constants.ClientCommand.ARG, cmd.getArg());
			broadcastIntent.putExtra(Constants.ClientCommand.ARG2, cmd.getArg2());
			broadcastIntent.putExtra(Constants.ClientCommand.ARG3, cmd.getArg3());
			broadcastIntent.putExtra(Constants.ClientCommand.CARD, cmd.getCard());
			broadcastIntent.putExtra(Constants.ClientCommand.TEXT, cmd.getText());

			cpxServer.sendBroadcastNote(cmd);
			break;
		case Command.COMMAND_REQUEST_FINISH_GAME:
			broadcastIntent = Helper.getInstance().createIntent(
					Constants.BROADCAST_SERVER);
			broadcastIntent.putExtra(Constants.BundleKey.CLIENT_COMMAND,
					cmd.getCommand());

			cpxServer.getContext().sendBroadcast(broadcastIntent);
			break;
		case Command.COMMAND_SET_FINISH_GAME:
			broadcastIntent = Helper.getInstance().createIntent(
					Constants.BROADCAST_SERVER);
			broadcastIntent.putExtra(Constants.BundleKey.CLIENT_COMMAND,
					cmd.getCommand());

			cpxServer.sendBroadcastNote(cmd);
			break;
		case Command.COMMAND_REQUEST_SCORE:
			broadcastIntent = Helper.getInstance().createIntent(
					Constants.BROADCAST_SERVER);
			broadcastIntent.putExtra(Constants.BundleKey.CLIENT_COMMAND,
					cmd.getCommand());
			broadcastIntent.putExtra(Constants.ClientCommand.ARG,
					cmd.getArg());
			broadcastIntent.putExtra(Constants.ClientCommand.ARG2,
					cmd.getArg2());

			cpxServer.getContext().sendBroadcast(broadcastIntent);
			break;
		case Command.COMMAND_SET_SCORE:
			broadcastIntent = Helper.getInstance().createIntent(
					Constants.BROADCAST_SERVER);
			broadcastIntent.putExtra(Constants.BundleKey.CLIENT_COMMAND,
					cmd.getCommand());
			broadcastIntent.putExtra(Constants.ClientCommand.ARG,
					cmd.getArg());
			broadcastIntent.putExtra(Constants.ClientCommand.ARG2,
					cmd.getArg2());
			broadcastIntent.putExtra(Constants.ClientCommand.TEXT,
					cmd.getText());

			cpxServer.sendBroadcastNote(cmd);
			break;
		case Command.COMMAND_REQUEST_CARD_OTHER:
			broadcastIntent = Helper.getInstance().createIntent(
					Constants.BROADCAST_SERVER);
			broadcastIntent.putExtra(Constants.BundleKey.CLIENT_COMMAND,
					cmd.getCommand());
			broadcastIntent.putExtra(Constants.ClientCommand.ARG,
					cmd.getArg());
			broadcastIntent.putExtra(Constants.ClientCommand.ARG2,
					cmd.getArg2());

			cpxServer.getContext().sendBroadcast(broadcastIntent);
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

			cpxServer.sendBroadcastNote(cmd);
			break;
		case Command.COMMAND_REQUEST_CARD_OTHER_BACK:
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

			cpxServer.getContext().sendBroadcast(broadcastIntent);
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

			cpxServer.sendBroadcastNote(cmd);
			break;
		case Command.COMMAND_REQUEST_EXCHANGE_CARD:
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
			broadcastIntent.putExtra(Constants.ClientCommand.ARG3,
					cmd.getArg3());

			cpxServer.getContext().sendBroadcast(broadcastIntent);
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

			cpxServer.sendBroadcastNote(cmd);
			break;
		case Command.COMMAND_REQUEST_LOOSER:
			broadcastIntent = Helper.getInstance().createIntent(
					Constants.BROADCAST_SERVER);
			broadcastIntent.putExtra(Constants.BundleKey.CLIENT_COMMAND,
					cmd.getCommand());
			broadcastIntent.putExtra(Constants.ClientCommand.ARG,
					cmd.getArg());
			broadcastIntent.putExtra(Constants.ClientCommand.ARG2,
					cmd.getArg2());

			cpxServer.getContext().sendBroadcast(broadcastIntent);
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

			cpxServer.sendBroadcastNote(cmd);
			break;
		default:
			break;
		}
	}

	public Map<String, String> getParticipantList() {
		return participantList;
	}

	public void sendParticipantListToClient() {
		Command note = Helper.getInstance().createShareNoteNetCommand();
		note.setText(getParticipantListString());
		note.setCommand(Command.COMMAND_PARTICIPANT_LIST);
		cpxServer.sendBroadcastNote(note);

		Intent broadcastIntent = Helper.getInstance().createIntent(
				Constants.BROADCAST_SERVER);
		broadcastIntent.putExtra(Constants.BundleKey.CLIENT_COMMAND,
				Command.COMMAND_PARTICIPANT_LIST);
		broadcastIntent.putExtra(Constants.ClientCommand.TEXT,
				getParticipantListString());

		cpxServer.getContext().sendBroadcast(broadcastIntent);
	}
}
