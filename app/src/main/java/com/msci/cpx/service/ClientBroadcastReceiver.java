package com.msci.cpx.service;

import java.util.ArrayList;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.msci.cpx.engine.StandardCard;
import com.msci.cpx.model.Participant;
import com.msci.cpx.model.Rule;
import com.msci.cpx.network.Command;
import com.msci.cpx.utility.Constants;
import com.msci.cpx.utility.Helper;
import com.msci.cpx.view.ActivityMainCpx;
import com.msci.cpx.view.DialogShareNote;

public class ClientBroadcastReceiver extends BroadcastReceiver {
	ActivityMainCpx mainCpxActivity;

	public ClientBroadcastReceiver(ActivityMainCpx shareNoteActivity) {
		this.mainCpxActivity = shareNoteActivity;
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		Log.e("TEST", "onReceive");
		DialogShareNote dialogShareNote = null;
		int clientStatus = intent.getIntExtra(
				Constants.BundleKey.CLIENT_STATUS, -1);
		int clientId = intent.getIntExtra(
				Constants.BundleKey.CLIENT_ID, -1);
		byte command = intent.getByteExtra(
				Constants.BundleKey.CLIENT_COMMAND, (byte)-1);
		
		switch (clientStatus) {
		case Constants.ClientStatus.CONNECTED:
			mainCpxActivity.setConnectButtonState();
			
			String text = intent.getStringExtra(Constants.ClientCommand.TEXT);
			String[] temp = text.split(";");

			mainCpxActivity.resetParticipantList();
			if (mainCpxActivity.serverId != null && !"".equals(mainCpxActivity.serverId)) {
				Participant participant = new Participant();
				participant.setId(1);
				participant.setNameId(mainCpxActivity.serverId + " [host]");
				mainCpxActivity.setParticipantList(participant);
			}
			
			for (String x : temp) {
				String[] split = x.split("-");
				
				if (x != null && !"".equals(x) && split.length == 2) {
					Participant participant = new Participant();
					participant.setId(Integer.parseInt(split[1].trim()));
					participant.setNameId(split[0].trim());
					
					mainCpxActivity.setParticipantList(participant);
				}
			}
			
			mainCpxActivity.refreshParticipantList();
			
			break;
		case Constants.ClientStatus.CONNECTION_REFUSED:
			dialogShareNote = new DialogShareNote(mainCpxActivity,
					DialogShareNote.ACTION_DIALOG_INVALID_CONNECTION,
					mainCpxActivity);
			dialogShareNote.show();
			mainCpxActivity.refreshParticipantList();
			break;
		case Constants.ClientStatus.DISCONNECTED:
			mainCpxActivity.setConnectButtonState();
			dialogShareNote = new DialogShareNote(mainCpxActivity,
					DialogShareNote.ACTION_DIALOG_DISCONNECT, mainCpxActivity);
			dialogShareNote.show();
			mainCpxActivity.setParticipantLeave(clientId);
			break;
		case Constants.ClientStatus.INCOMING_MESSAGE: {
			if (command != Command.COMMAND_INSERT_DECK) {
				handleMessage(intent, intent.getByteExtra(
					Constants.BundleKey.CLIENT_COMMAND, (byte) -1));
			}
		}
			break;
		case Constants.ClientStatus.OTHER_ERROR:
			break;
		case Constants.ClientStatus.SERVER_MAX_CONNECTION_REACHED:
			dialogShareNote = new DialogShareNote(mainCpxActivity,
					DialogShareNote.ACTION_DIALOG_SERVER_MAX_CONNECTION_REACHED, mainCpxActivity);
			dialogShareNote.show();
			break;
		case Constants.ClientStatus.CARD_PLAY:
			switch (command) {
			case Command.COMMAND_INSERT_DECK: {
					mainCpxActivity.startMainCpx(null, null, 0);
				}
				break;
			case Command.COMMAND_REQUEST_DECK_INIT:
				String ip = intent.getStringExtra(Constants.ClientCommand.IP);
				if (Helper.getInstance().getIpLocalAddress(mainCpxActivity).equals(ip)) {
					Object[] cardsObject = (Object[]) intent.getSerializableExtra(Constants.ClientCommand.CARD);
					int playerTurnIndex = (Integer) intent.getIntExtra(Constants.ClientCommand.ARG, -1);
					int gameStatus = (Integer) intent.getIntExtra(Constants.ClientCommand.ARG2, Constants.PlayStatus.STATUS_OK);
					Rule rule = (Rule)intent.getSerializableExtra(Constants.ClientCommand.OBJ);
					ArrayList<Participant> players = (ArrayList<Participant>)intent.getSerializableExtra(Constants.ClientCommand.OBJ2);
					
					StandardCard[] cards = new StandardCard[cardsObject.length];
					
					int idx = 0;
					for (Object obj:cardsObject) {
						StandardCard card = (StandardCard)obj;
						cards[idx++] = card;
					}
					
					String message = intent.getStringExtra(Constants.ClientCommand.TEXT);
					mainCpxActivity.setCardDeal(gameStatus, cards, message, playerTurnIndex, rule, players);
				}
				break;
			case Command.COMMAND_SET_PLAY:
				int setRequestId = intent.getIntExtra(Constants.ClientCommand.ARG, -1);
				int setScore = intent.getIntExtra(Constants.ClientCommand.ARG2, -1);
				int setStatus = intent.getIntExtra(Constants.ClientCommand.ARG3, Constants.PlayStatus.STATUS_OK);
				StandardCard setCard = (StandardCard) intent.getSerializableExtra(Constants.ClientCommand.CARD);
				String setMessage = intent.getStringExtra(Constants.ClientCommand.TEXT);
				
				mainCpxActivity.setPlayCard(setStatus, setRequestId, setScore, setCard, setMessage);
				break;
			case Command.COMMAND_SET_DRAW:
				int setDrawRequestId = intent.getIntExtra(Constants.ClientCommand.ARG, -1);
				StandardCard setDrawCard = (StandardCard) intent.getSerializableExtra(Constants.ClientCommand.CARD);
				String setDrawMessage = intent.getStringExtra(Constants.ClientCommand.TEXT);
				boolean setNextTurn = intent.getBooleanExtra(Constants.ClientCommand.BOOLEAN, true);
				
				mainCpxActivity.setDrawCard(setDrawRequestId, setDrawCard, setNextTurn, setDrawMessage);
				break;
			case Command.COMMAND_SET_NEXT_TURN:
				int setTurnRequestId = intent.getIntExtra(Constants.ClientCommand.ARG, -1);
				int setTurnNextId = intent.getIntExtra(Constants.ClientCommand.ARG2, -1);
				int setTurnIsRevert = intent.getIntExtra(Constants.ClientCommand.ARG3, 0);
				String setTurnMessage = intent.getStringExtra(Constants.ClientCommand.TEXT);
				
				mainCpxActivity.setNextTurn(setTurnRequestId, setTurnNextId, (setTurnIsRevert == 1 ? true : false), setTurnMessage);
				break;
			case Command.COMMAND_SET_DROP_CARD:
				int setDropStatus = intent.getIntExtra(Constants.ClientCommand.ARG, -1);
				int setDropRequestId = intent.getIntExtra(Constants.ClientCommand.ARG2, -1);
				int setDropValue = intent.getIntExtra(Constants.ClientCommand.ARG3, -1);
				StandardCard setDropCard = (StandardCard) intent.getSerializableExtra(Constants.ClientCommand.CARD);
				String setDropMessage = intent.getStringExtra(Constants.ClientCommand.TEXT);
				
				mainCpxActivity.setDropCard(setDropStatus, setDropRequestId, setDropValue, setDropCard, setDropMessage);
				break;
			case Command.COMMAND_SET_FINISH_GAME:
				mainCpxActivity.setFinishGame();
				break;
			case Command.COMMAND_SET_SCORE:
				int setScoreRequestId = intent.getIntExtra(Constants.ClientCommand.ARG, -1);
				int setScoreValue = intent.getIntExtra(Constants.ClientCommand.ARG2, -1);
				String setScoreMessage = intent.getStringExtra(Constants.ClientCommand.TEXT);
				mainCpxActivity.setScore(setScoreRequestId, setScoreValue, setScoreMessage);
				break;
			case Command.COMMAND_SET_REQUEST_CARD_OTHER: {
					int exchRequestId = intent.getIntExtra(Constants.ClientCommand.ARG, -1);
					int exchRequestId2 = intent.getIntExtra(Constants.ClientCommand.ARG2, -1);
					
					mainCpxActivity.setRequestCardOther(exchRequestId, exchRequestId2);
				}
				break;
			case Command.COMMAND_SET_REQUEST_CARD_OTHER_BACK: {
					int exchRequestId = intent.getIntExtra(Constants.ClientCommand.ARG, -1);
					int exchRequestId2 = intent.getIntExtra(Constants.ClientCommand.ARG2, -1);
					Object[] exchCards = (Object[]) intent.getSerializableExtra(Constants.ClientCommand.CARD);
					
					StandardCard[] cards = new StandardCard[exchCards.length];
					
					int idx = 0;
					for (Object obj:exchCards) {
						StandardCard exchCard = (StandardCard)obj;
						cards[idx++] = exchCard;
					}
					
					mainCpxActivity.setRequestCardOtherBack(exchRequestId, exchRequestId2, cards);
				}
				break;
			case Command.COMMAND_SET_EXCHANGE_CARD: {
					int exchRequestId = intent.getIntExtra(Constants.ClientCommand.ARG, -1);
					Object[] exchCards = (Object[]) intent.getSerializableExtra(Constants.ClientCommand.CARD);
					int exchRequestId2 = intent.getIntExtra(Constants.ClientCommand.ARG2, -1);
					Object[] exchCards2 = (Object[]) intent.getSerializableExtra(Constants.ClientCommand.CARD2);
					
					StandardCard[] cards = new StandardCard[exchCards.length];
					
					int idx = 0;
					for (Object obj:exchCards) {
						StandardCard exchCard = (StandardCard)obj;
						cards[idx++] = exchCard;
					}
					
					StandardCard[] cards2 = new StandardCard[exchCards.length];
					
					idx = 0;
					for (Object obj:exchCards2) {
						StandardCard exchCard = (StandardCard)obj;
						cards2[idx++] = exchCard;
					}
					
					mainCpxActivity.setExchangeCard(exchRequestId, cards, exchRequestId2, cards2);
				}
				break;
			case Command.COMMAND_SET_LOOSER: {
				int requestId = intent.getIntExtra(Constants.ClientCommand.ARG, -1);
				int score = intent.getIntExtra(Constants.ClientCommand.ARG2, -1);
				
				mainCpxActivity.setLooser(requestId, score);
			}
			break;
			default:
				break;
			}
			break;
		default:
			break;
		}

	}

	public void handleMessage(Intent intent, byte command) {

		int indexStart = -1;
		int indexEnd = -1;
		String text = "";
		String fulltext = "";

		switch (command) {
		case Command.COMMAND_DELETE:

			indexStart = intent.getIntExtra(
					Constants.ClientCommand.INDEX_START, -1);
			indexEnd = intent.getIntExtra(
					Constants.ClientCommand.INDEX_END, -1);

			mainCpxActivity.doCommandDelete(fulltext, indexStart, indexEnd);
			break;
		case Command.COMMAND_RESET_NOTE:
			text = intent.getStringExtra(Constants.ClientCommand.TEXT);
			mainCpxActivity.isReceiveMsgFromServer = true;
			break;
		case Command.COMMAND_INSERT:
			//indexStart = intent.getIntExtra(
			//		Constants.ClientCommand.INDEX_START, -1);
			text = intent.getStringExtra(Constants.ClientCommand.TEXT);

			mainCpxActivity.doCommandInsert(text);
			break;
		case Command.COMMAND_REPLACE:

			indexStart = intent.getIntExtra(
					Constants.ClientCommand.INDEX_START, -1);
			indexEnd = intent.getIntExtra(
					Constants.ClientCommand.INDEX_END, -1);
			text = intent.getStringExtra(Constants.ClientCommand.TEXT);

			mainCpxActivity.doCommandReplace(text, indexStart, indexEnd);
			break;
		case Command.COMMAND_PARTICIPANT_LIST:
			text = intent.getStringExtra(Constants.ClientCommand.TEXT);
			String[] temp = text.split(";");

			mainCpxActivity.resetParticipantList();
			if (mainCpxActivity.serverId != null && !"".equals(mainCpxActivity.serverId)) {
				Participant participant = new Participant();
				participant.setId(1);
				participant.setNameId(mainCpxActivity.serverId + " [host]");
				mainCpxActivity.setParticipantList(participant);
			}
			
			for (String x : temp) {
				String[] split = x.split("-");
				
				if (x != null && !"".equals(x) && split.length == 2) {
					Participant participant = new Participant();
					participant.setId(Integer.parseInt(split[1].trim()));
					participant.setNameId(split[0].trim());
					
					mainCpxActivity.setParticipantList(participant);
				}
			}
			
			mainCpxActivity.refreshParticipantList();
			break;
		default:
			break;
		}
	}

}
