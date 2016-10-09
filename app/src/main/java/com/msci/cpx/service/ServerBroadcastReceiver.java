package com.msci.cpx.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.msci.cpx.engine.StandardCard;
import com.msci.cpx.model.Participant;
import com.msci.cpx.network.Command;
import com.msci.cpx.utility.Constants;
import com.msci.cpx.utility.PrefUtils;
import com.msci.cpx.view.ActivityMainCpx;

public class ServerBroadcastReceiver extends BroadcastReceiver {
	ActivityMainCpx mainCpxActivity;

	public ServerBroadcastReceiver(ActivityMainCpx shareNoteActivity) {
		this.mainCpxActivity = shareNoteActivity;
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		int serverStat = intent.getIntExtra(Constants.BundleKey.SERVER_STATUS, -1);
		int serverId = intent.getIntExtra(Constants.BundleKey.SERVER_ID, -1);
		if(serverStat != -1) {
			if (serverId != -1) {
				mainCpxActivity.resetParticipantList();
				mainCpxActivity.setServerChannelId(serverId);
				
				Participant participant = new Participant();
				participant.setId(1);
				participant.setNameId(PrefUtils.getInstance().getNameID(mainCpxActivity) + " [host]");
				mainCpxActivity.setParticipantList(participant);
			}
			
			mainCpxActivity.setShareButtonState();
		}
		else {
			
			byte command = intent.getByteExtra(
					Constants.BundleKey.CLIENT_COMMAND, (byte) -1);
			Log.i("ServerBroadcastReceiver", "Client command = " + command);
	
			String fulltext = null;
			int indexStart = -1, indexEnd = -1;
	
			switch (command) {
			case Command.COMMAND_PARTICIPANT_LIST:
				String text = intent
						.getStringExtra(Constants.ClientCommand.TEXT);
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
	
			case Command.COMMAND_DELETE:
				indexStart = intent.getIntExtra(
						Constants.ClientCommand.INDEX_START, -1);
				indexEnd = intent.getIntExtra(
						Constants.ClientCommand.INDEX_END, -1);
	
				mainCpxActivity.doCommandDelete(fulltext, indexStart, indexEnd);
				break;
	
			case Command.COMMAND_RESET_NOTE:
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
			case Command.COMMAND_INSERT_DECK:
				break;
			case Command.COMMAND_REQUEST_DECK_INIT:
				String ip = (String)intent.getStringExtra(Constants.ClientCommand.IP);
				String appKey = intent.getStringExtra(Constants.ClientCommand.TEXT);
				mainCpxActivity.doCommandCardDeal(ip, appKey);
				break;
			case Command.COMMAND_REQUEST_PLAY:
				int requestId = intent.getIntExtra(Constants.ClientCommand.ARG, -1);
				StandardCard card = (StandardCard) intent.getSerializableExtra(Constants.ClientCommand.CARD);
				boolean isSpecialAction = intent.getBooleanExtra(Constants.ClientCommand.BOOLEAN, false);
				mainCpxActivity.doCommandPlayCard(requestId, card, isSpecialAction);
				break;
			case Command.COMMAND_SET_PLAY:
				int setRequestId = intent.getIntExtra(Constants.ClientCommand.ARG, -1);
				int setScore = intent.getIntExtra(Constants.ClientCommand.ARG2, -1);
				int setStatus = intent.getIntExtra(Constants.ClientCommand.ARG3, Constants.PlayStatus.STATUS_OK);
				StandardCard setCard = (StandardCard) intent.getSerializableExtra(Constants.ClientCommand.CARD);
				String setMessage = intent.getStringExtra(Constants.ClientCommand.TEXT);
				mainCpxActivity.setPlayCard(setStatus, setRequestId, setScore, setCard, setMessage);
				break;
			case Command.COMMAND_REQUEST_DRAW:
				int drawRequestId = intent.getIntExtra(Constants.ClientCommand.ARG, -1);
				boolean setNextTurn = intent.getBooleanExtra(Constants.ClientCommand.BOOLEAN, true);
				mainCpxActivity.doCommandDrawCard(drawRequestId, setNextTurn);
				break;
			case Command.COMMAND_SET_DRAW:
				int setDrawRequestId = intent.getIntExtra(Constants.ClientCommand.ARG, -1);
				StandardCard setDrawCard = (StandardCard) intent.getSerializableExtra(Constants.ClientCommand.CARD);
				String setDrawMessage = intent.getStringExtra(Constants.ClientCommand.TEXT);
				boolean setDrawNextTurn = intent.getBooleanExtra(Constants.ClientCommand.BOOLEAN, true);
				mainCpxActivity.setDrawCard(setDrawRequestId, setDrawCard, setDrawNextTurn, setDrawMessage);
				break;
			case Command.COMMAND_REQUEST_NEXT_TURN:
				int turnRequestId = intent.getIntExtra(Constants.ClientCommand.ARG, -1);
				int turnCurrentIndex = intent.getIntExtra(Constants.ClientCommand.ARG2, -1);
				int turnIsRevert = intent.getIntExtra(Constants.ClientCommand.ARG3, 0);
				boolean turnIsFinish = intent.getBooleanExtra(Constants.ClientCommand.BOOLEAN, false);
				mainCpxActivity.doCommandNextTurn(turnRequestId, turnCurrentIndex, turnIsFinish, (turnIsRevert == 1 ? true : false));
				break;
			case Command.COMMAND_SET_NEXT_TURN:
				int setTurnRequestId = intent.getIntExtra(Constants.ClientCommand.ARG, -1);
				int setTurnNextIndex = intent.getIntExtra(Constants.ClientCommand.ARG2, -1);
				int setTurnIsRevert = intent.getIntExtra(Constants.ClientCommand.ARG3, 0);
				String setTurnMessage = intent.getStringExtra(Constants.ClientCommand.TEXT);
				mainCpxActivity.setNextTurn(setTurnRequestId, setTurnNextIndex, (setTurnIsRevert == 1 ? true : false), setTurnMessage);
				break;
			case Command.COMMAND_REQUEST_DROP_CARD:
				int dropRequestId = intent.getIntExtra(Constants.ClientCommand.ARG, -1);
				StandardCard dropCard = (StandardCard) intent.getSerializableExtra(Constants.ClientCommand.CARD);
				mainCpxActivity.doCommandDropCard(dropRequestId, dropCard);
				break;
			case Command.COMMAND_SET_DROP_CARD:
				int setDropStatus = intent.getIntExtra(Constants.ClientCommand.ARG, -1);
				int setDropRequestId = intent.getIntExtra(Constants.ClientCommand.ARG2, -1);
				int setDropValue = intent.getIntExtra(Constants.ClientCommand.ARG3, -1);
				StandardCard setDropCard = (StandardCard) intent.getSerializableExtra(Constants.ClientCommand.CARD);
				String setDropMessage = intent.getStringExtra(Constants.ClientCommand.TEXT);
				mainCpxActivity.setDropCard(setDropStatus, setDropRequestId, setDropValue, setDropCard, setDropMessage);
				break;
			case Command.COMMAND_REQUEST_FINISH_GAME:
				mainCpxActivity.doCommandFinishGame();
				break;
			case Command.COMMAND_SET_FINISH_GAME:
				mainCpxActivity.setFinishGame();
				break;
			case Command.COMMAND_REQUEST_SCORE:
				int scoreRequestId = intent.getIntExtra(Constants.ClientCommand.ARG, -1);
				int score = intent.getIntExtra(Constants.ClientCommand.ARG2, -1);
				mainCpxActivity.doCommandScore(scoreRequestId, score);
				break;
			case Command.COMMAND_SET_SCORE:
				int setScoreRequestId = intent.getIntExtra(Constants.ClientCommand.ARG, -1);
				int setScoreValue = intent.getIntExtra(Constants.ClientCommand.ARG2, -1);
				String setScoreMessage = intent.getStringExtra(Constants.ClientCommand.TEXT);
				mainCpxActivity.setScore(setScoreRequestId, setScoreValue, setScoreMessage);
				break;
			case Command.COMMAND_REQUEST_CARD_OTHER: {
					int exchRequestId = intent.getIntExtra(Constants.ClientCommand.ARG, -1);
					int exchRequestId2 = intent.getIntExtra(Constants.ClientCommand.ARG2, -1);
					
					mainCpxActivity.doCommandRequestCardOther(exchRequestId, exchRequestId2);
				}
				break;
			case Command.COMMAND_SET_REQUEST_CARD_OTHER: {
					int exchRequestId = intent.getIntExtra(Constants.ClientCommand.ARG, -1);
					int exchRequestId2 = intent.getIntExtra(Constants.ClientCommand.ARG2, -1);
					
					mainCpxActivity.setRequestCardOther(exchRequestId, exchRequestId2);
				}
				break;
			case Command.COMMAND_REQUEST_CARD_OTHER_BACK: {
					int exchRequestId = intent.getIntExtra(Constants.ClientCommand.ARG, -1);
					int exchRequestId2 = intent.getIntExtra(Constants.ClientCommand.ARG2, -1);
					Object[] exchCards = (Object[]) intent.getSerializableExtra(Constants.ClientCommand.CARD);
					
					StandardCard[] cards = new StandardCard[exchCards.length];
					
					int idx = 0;
					for (Object obj:exchCards) {
						StandardCard exchCard = (StandardCard)obj;
						cards[idx++] = exchCard;
					}
					
					mainCpxActivity.doCommandRequestCardOtherBack(exchRequestId, exchRequestId2, cards);
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
			case Command.COMMAND_REQUEST_EXCHANGE_CARD: {
					int exchRequestId = intent.getIntExtra(Constants.ClientCommand.ARG, -1);
					Object[] exchCards = (Object[]) intent.getSerializableExtra(Constants.ClientCommand.CARD);
					int exchRequestId2 = intent.getIntExtra(Constants.ClientCommand.ARG2, -1);
					Object[] exchCards2 = (Object[]) intent.getSerializableExtra(Constants.ClientCommand.CARD2);
					int exchSpecialCardIndex = intent.getIntExtra(Constants.ClientCommand.ARG3, -1);
					
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
					
					mainCpxActivity.doCommandExchangeCard(exchRequestId, cards, exchSpecialCardIndex, exchRequestId2, cards2);
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
			case Command.COMMAND_REQUEST_LOOSER: {
					int exchRequestId = intent.getIntExtra(Constants.ClientCommand.ARG, -1);
					int loosScore = intent.getIntExtra(Constants.ClientCommand.ARG2, -1);
					
					mainCpxActivity.doCommandLooser(exchRequestId, loosScore);
				}
				break;
			case Command.COMMAND_SET_LOOSER: {
					int exchRequestId = intent.getIntExtra(Constants.ClientCommand.ARG, -1);
					int loosScore = intent.getIntExtra(Constants.ClientCommand.ARG2, -1);
					
					mainCpxActivity.setLooser(exchRequestId, loosScore);
				}
				break;
	
			default:
				break;
			}
		}
	}

}
