package com.msci.cpx.view;

import java.security.Provider;
import java.util.ArrayList;
import java.util.Random;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.DialogInterface.OnShowListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.SeekBar.OnSeekBarChangeListener;

import com.msci.cpx.R;
import com.msci.cpx.engine.CpxGameEngine;
import com.msci.cpx.engine.EmptyDeckException;
import com.msci.cpx.engine.StandardCard;
import com.msci.cpx.engine.StandardDeckEngine;
import com.msci.cpx.model.CpxRule;
import com.msci.cpx.model.Participant;
import com.msci.cpx.model.Rule;
import com.msci.cpx.network.Command;
import com.msci.cpx.service.ClientBroadcastReceiver;
import com.msci.cpx.service.ClientService;
import com.msci.cpx.service.ClientService.ClientLocalBinder;
import com.msci.cpx.service.ServerBroadcastReceiver;
import com.msci.cpx.service.ServerService;
import com.msci.cpx.service.ServerService.LocalBinder;
import com.msci.cpx.utility.Constants;
import com.msci.cpx.utility.Helper;
import com.msci.cpx.utility.Note;
import com.msci.cpx.utility.PrefUtils;
import com.msci.cpx.view.DialogShareNote.DialogShareNoteInterface;

public class ActivityMainCpx extends FragmentActivity implements
		DialogShareNoteInterface {
	Button btnShare;
	Button btnConnect;
	FrameLayout flMainContainer;

	public boolean isReceiveMsgFromServer = false;

	ServerBroadcastReceiver serverBroadcastReceiver;
	ClientBroadcastReceiver clientBroadcastReceiver;
	AlertDialog alert;
	ArrayList<Participant> participantList;
	ArrayList<Participant> playerList;

	ServerService serverService = null;
	ClientService clientService = null;
	ServiceConnection serviceConnection;
	Intent serviceIntent;

	DialogShareNote dialogShareNote;
	DialogConnect dialogConnect;

	private Context mContext = null;
	private ActivityMainCpxListener listenerParticipant = null;
	private ActivityMainCpxListener listenerChat = null;
	private ActivityMainCpxPlayListener listenerMainCpx = null;

	public Menu mCurrentMenu = null;
	public boolean menuSelected = false;
	ArrayList<Note> mNotesHistory = new ArrayList<Note>();
	private static final String TAG = "ShareNoteActivity";

	public String mTitleServerIP = "";
	public String serverId = "";
	public Provider serverProvider;
	public int serverChannelId = -1;
	public CpxGameEngine cpxGameEngine = null;
	private int playStartIndex = 0;
	private int playerFinishCount = 0;
	private int playerLooseMax = 0;
	private int playerLooseId = -1;
	private int playerLooseCount = 0;
	private ProgressDialog progressDialog;

	private Rule rule;
	private boolean isGameRunning = false;

	public interface ActivityMainCpxListener {
		public void onParticipantUpdate(Participant participant);
		
		public void onListParticipantUpdate(ArrayList<Participant> list);

		public void onParticipantLeave(int participantNameId);

		public void onChatUpdate(String text);
		
		public void onGameStatus(int requestId, boolean gameStatus);
	}

	public interface ActivityMainCpxPlayListener {
		public void onDeckReady();

		public void onCardDeal(StandardCard[] cards, String message,
				int turnPlayerIndex);

		public void onCardDraw(int requestId, StandardCard card,
				boolean setNextTurn, String message);

		public void onCardPlay(int status, StandardCard card, int requestId,
				int score, String message);

		public void onCardScore(int requestId, int score, String message);

		public void onTurn(int requestId, int index, boolean isRevert,
				String message);

		public void onCardDrop(int status, int requestId, int value,
				StandardCard card, String message);

		public void onGameFinish(String message);

		public void onCardRequestOther(int requestId, int requestId2);

		public void onCardRequestOtherBack(int requestId, int requestId2,
				StandardCard[] cards);

		public void onCardExchange(int requestId, StandardCard[] cards,
				int requestId2, StandardCard[] cards2);

		public void onCardLooser(int requestId, int score);
	}

	public void onCreate(Bundle savedInstanceState) {
		Log.e(TAG, "onCreate");
		super.onCreate(savedInstanceState);

		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		
		progressDialog = new ProgressDialog(this);
		progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		progressDialog.setCancelable(false);

		PrefUtils.getInstance().setServerStatus(this, false);
		PrefUtils.getInstance().setClientStatus(this, false);

		setContentView(R.layout.activity_share_note);
		btnShare = (Button) findViewById(R.id.btnshare);
		btnConnect = (Button) findViewById(R.id.btnconnect);
		flMainContainer = (FrameLayout) findViewById(R.id.flMainContainer);

		playerList = new ArrayList<Participant>();
		participantList = new ArrayList<Participant>();

		FragmentTransaction fragmentTransaction = getSupportFragmentManager()
				.beginTransaction();
		fragmentTransaction
				.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);

		FragmentMain fragment = new FragmentMain();
		fragmentTransaction.add(R.id.flMainContainer, fragment);
		fragmentTransaction.commit();

		serviceIntent = Helper.getInstance().createIntent(ActivityMainCpx.this,
				ServerService.class);
		serviceConnection = new ServiceConnection() {

			public void onServiceDisconnected(ComponentName name) {
			}

			public void onServiceConnected(ComponentName name, IBinder binder) {

				if (PrefUtils.getInstance().getServerStatus(
						ActivityMainCpx.this)) {
					serverService = ((LocalBinder) binder).getService();
				} else {
					clientService = ((ClientLocalBinder) binder).getService();
				}
			}
		};

		mContext = this;
		prepareAll();
	}
	
	public void showProgress() {
		if (progressDialog != null && !progressDialog.isShowing()) {
			progressDialog.show();
		}
	}
	
	public void hideProgress() {
		if (progressDialog != null && progressDialog.isShowing()) {
			progressDialog.dismiss();
		}
	}

	public void sendBroadcast(Command command) {
		if (PrefUtils.getInstance().getServerStatus(this)) {
			if (serverService != null) {
				serverService.sendBroadcastNote(command);
			}
		}

		if (clientService != null)
			clientService.getShareNoteClient().sendNoteToServer(command);
	}

	public void SendMsgToServer(String source) {
		Log.e(TAG, "CLIENT SENDING MSG");
		Log.v(TAG, "Source '" + source + "'");
		Command shareNoteNetCommand = new Command();

		shareNoteNetCommand.setCommand(Command.COMMAND_INSERT);
		shareNoteNetCommand.setLength(source.getBytes().length);
		shareNoteNetCommand.setText(source);

		if (PrefUtils.getInstance().getServerStatus(this)) {
			if (serverService != null) {
				serverService.sendBroadcastNote(shareNoteNetCommand);
			}

			if (listenerChat != null) {
				listenerChat.onChatUpdate(source);
			}
		}

		if (clientService != null)
			clientService.getShareNoteClient().sendNoteToServer(
					shareNoteNetCommand);
	}

	// ####### PLAY CARD ############
	public void startCpx() {
		ArrayList<Participant> listParticipant = getParticipantList();

		if (listParticipant.size() > 1) {
			View deckCountView = (View) getLayoutInflater().inflate(
					R.layout.custom_dialog_deckcount, null);
			final CheckBox cbSpecialCard = (CheckBox) deckCountView
					.findViewById(R.id.cbSpecialCard);
			final TextView tvDeckCount = (TextView) deckCountView
					.findViewById(R.id.tvDeckCount);
			final SeekBar sbDeckCount = (SeekBar) deckCountView
					.findViewById(R.id.sbDeckCount);

			cbSpecialCard.setSelected(true);
			cbSpecialCard.setChecked(true);

			tvDeckCount.setText(String.valueOf(sbDeckCount.getProgress() + 1));

			sbDeckCount.setMax(4);
			sbDeckCount.setProgress(0);
			sbDeckCount
					.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
						@Override
						public void onStopTrackingTouch(SeekBar seekBar) {
						}

						@Override
						public void onStartTrackingTouch(SeekBar seekBar) {
						}

						@Override
						public void onProgressChanged(SeekBar seekBar,
								int progress, boolean fromUser) {
							tvDeckCount.setText(String.valueOf(progress + 1));
						}
					});

			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setView(deckCountView);
			builder.setPositiveButton(getString(R.string.bt_ok),
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							CpxRule rule = new CpxRule();
							rule.setDeckCount(sbDeckCount.getProgress() + 1);
							rule.setSpecialCard(cbSpecialCard.isChecked());

							sendCardDeckToServer(rule);
						}
					});
			builder.setNegativeButton(getString(R.string.bt_cancel),
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();
						}
					});

			AlertDialog dialog = builder.create();
			dialog.setCancelable(false);
			dialog.show();
		} else {
			Toast.makeText(this, getString(R.string.play_alone),
					Toast.LENGTH_LONG).show();
		}
	}

	// Init Game
	public void sendCardDeckToServer(CpxRule rule) {
		Log.e(TAG, "SENDING CARD DECK");
		setRule(rule);
		cpxGameEngine = new CpxGameEngine(rule.getDeckCount(), this.rule);
		StandardDeckEngine source = cpxGameEngine.getDeck();
		playerFinishCount = 0;
		playStartIndex = 0;
		playerLooseMax = 0;
		playerLooseId = -1;
		playerLooseCount = 0;
		
		playerList.clear();
		playerList.addAll(getParticipantList());
		
		Random rnd = new Random(System.nanoTime());
		int playerTurn = rnd.nextInt(playerList.size());
		
		setPlayStartIndex(playerTurn);

		Command command = new Command();
		command.setCommand(Command.COMMAND_INSERT_DECK);
		command.setDeckEngine(source);

		if (cpxGameEngine != null) {
			try {
				StandardCard[] cards = cpxGameEngine.deal();
				startMainCpx(cards, playerList, playStartIndex);
				sendBroadcast(command);
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (EmptyDeckException e) {
				e.printStackTrace();
			}
		}
	}

	// Card Deal
	public void sendRequestCardDealToServer(String ip, String key) {
		Log.e(TAG, "CLIENT REQUEST CARD DEAL");
		Command cpxNetCommand = new Command();

		cpxNetCommand.setCommand(Command.COMMAND_REQUEST_DECK_INIT);
		cpxNetCommand.setLengthIp(ip.length());
		cpxNetCommand.setIp(ip);
		cpxNetCommand.setText(key);

		sendBroadcast(cpxNetCommand);
	}

	public synchronized void doCommandCardDeal(String ip, String key) {
		if (PrefUtils.getInstance().getServerStatus(this)) {
			StandardCard[] cards = null;
			Command command = new Command();

			int status = Constants.PlayStatus.STATUS_OK;

			if (!key.equals(PrefUtils.getInstance().getAppKey(this))) {
				status = Constants.PlayStatus.STATUS_ERROR;
			}

			try {
				cards = cpxGameEngine.deal();
				command.setText(getString(R.string.status_ok));
			} catch (IllegalArgumentException e) {
				command.setText(getString(R.string.error_card_empty));
			} catch (EmptyDeckException e) {
				command.setText(getString(R.string.error_card_empty));
			}

			command.setCommand(Command.COMMAND_REQUEST_DECK_INIT);
			command.setIp(ip);
			command.setCards(cards);
			command.setArg(getPlayStartIndex());
			command.setArg2(status);
			command.setObj(getCpxRule());
			command.setObj2(playerList);

			sendBroadcast(command);
		}
	}

	public void setCardDeal(int status, StandardCard[] cards, String message,
			int turnPlayerIndex, Rule rule, ArrayList<Participant> players) {
		if (status == Constants.PlayStatus.STATUS_OK) {
			setRule(rule);
			setPlayerList(players);
			setPlayStartIndex(turnPlayerIndex);

			if (listenerMainCpx != null) {
				listenerMainCpx.onCardDeal(cards, message, turnPlayerIndex);
			}
		} else {
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setMessage(R.string.different_apk_key);
			builder.setPositiveButton(getString(R.string.bt_ok),
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();
							finish();
						}
					});

			AlertDialog dialog = builder.create();
			dialog.setCancelable(false);
			dialog.show();
		}
	}

	// Draw Card
	public void sendRequestDrawCard(int requestId, boolean setNextTurn) {
		Log.e(TAG, "CLIENT REQUEST DRAW DEAL");
		Command command = new Command();

		command.setCommand(Command.COMMAND_REQUEST_DRAW);
		command.setArg(requestId);
		command.setBool(setNextTurn);

		if (PrefUtils.getInstance().getServerStatus(this)) {
			doCommandDrawCard(requestId, setNextTurn);
		} else {
			sendBroadcast(command);
		}
	}

	public synchronized void doCommandDrawCard(int requestId,
			boolean setNextTurn) {
		if (PrefUtils.getInstance().getServerStatus(this)) {
			Command command = new Command();
			StandardCard card = null;
			try {
				card = cpxGameEngine.draw();
				command.setText(getString(R.string.status_ok));
			} catch (EmptyDeckException e) {
				command.setText(getString(R.string.error_card_empty));
			}

			command.setCommand(Command.COMMAND_SET_DRAW);
			command.setArg(requestId);
			command.setCard(card);
			command.setBool(setNextTurn);

			setDrawCard(requestId, card, setNextTurn, command.getText());

			sendBroadcast(command);
		}
	}

	public void setDrawCard(int requestId, StandardCard card,
			boolean setNextTurn, String message) {
		if (listenerMainCpx != null) {
			listenerMainCpx.onCardDraw(requestId, card, setNextTurn, message);
		}
	}

	// Play Card
	public void sendRequestPlayCard(int requestId, StandardCard card,
			boolean isSpecialAction) {
		Log.e(TAG, "CLIENT REQUEST PLAY CARD");
		Command command = new Command();

		command.setCommand(Command.COMMAND_REQUEST_PLAY);
		command.setArg(requestId);
		command.setBool(isSpecialAction);
		command.setCard(card);

		if (PrefUtils.getInstance().getServerStatus(this)) {
			doCommandPlayCard(requestId, card, isSpecialAction);
		} else {
			sendBroadcast(command);
		}
	}

	public synchronized void doCommandPlayCard(int requestId,
			StandardCard card, boolean isSpecialAction) {
		if (PrefUtils.getInstance().getServerStatus(this)) {
			int score = cpxGameEngine.play(card, isSpecialAction);
			int status = cpxGameEngine.getPlayStatus();

			Command command = new Command();
			command.setCommand(Command.COMMAND_SET_PLAY);
			command.setCard(card);
			command.setArg(requestId);
			command.setArg2(score);
			command.setArg3(status);
			command.setText(getString(R.string.status_ok));

			setPlayCard(status, requestId, score, card, command.getText());

			sendBroadcast(command);
		}
	}

	public void setPlayCard(int status, int requestId, int score,
			StandardCard card, String message) {
		if (listenerMainCpx != null) {
			listenerMainCpx.onCardPlay(status, card, requestId, score, message);
		}
	}

	// Next Turn
	public void sendRequestNextTurn(int requestId, int currentIndex,
			boolean isFinish, boolean isRevert) {
		Log.e(TAG, "CLIENT REQUEST NEXT TURN");
		Command command = new Command();

		command.setCommand(Command.COMMAND_REQUEST_NEXT_TURN);
		command.setArg(requestId);
		command.setArg2(currentIndex);
		command.setArg3(isRevert ? 1 : 0);
		command.setBool(isFinish);

		if (PrefUtils.getInstance().getServerStatus(this)) {
			doCommandNextTurn(requestId, currentIndex, isFinish, isRevert);
		} else {
			sendBroadcast(command);
		}
	}

	public synchronized void doCommandNextTurn(int requestId, int currentIndex,
			boolean isFinish, boolean isRevert) {
		if (PrefUtils.getInstance().getServerStatus(this)) {
			Log.e(TAG, "CLIENT SET NEXT TURN");
			Command command = new Command();
			int playerCount = getPlayerList().size();

			if (isFinish) {
				playerFinishCount++;
			}

			if (playerFinishCount == playerCount) {
				command.setCommand(Command.COMMAND_SET_FINISH_GAME);

				setFinishGame();
			} else {
				command.setCommand(Command.COMMAND_SET_NEXT_TURN);

				if (!isRevert) {
					if (currentIndex >= playerCount - 1) {
						currentIndex = 0;
					} else {
						currentIndex++;
					}
				} else {
					if (currentIndex == 0) {
						currentIndex = playerCount - 1;
					} else {
						currentIndex--;
					}
				}

				command.setArg(requestId);
				command.setArg2(currentIndex);
				command.setArg3(isRevert ? 1 : 0);
				command.setText(getString(R.string.status_ok));

				setNextTurn(requestId, currentIndex, isRevert,
						command.getText());
			}

			sendBroadcast(command);
		}
	}

	public void setNextTurn(int requestId, int nextIndex, boolean isRevert,
			String message) {
		Log.e(TAG, "LISTENER SET NEXT TURN " + nextIndex);
		if (listenerMainCpx != null) {
			listenerMainCpx.onTurn(requestId, nextIndex, isRevert, message);
		}
	}

	// Drop Card
	public void sendRequestDropCard(int requestId, StandardCard card) {
		Log.e(TAG, "CLIENT REQUEST DROP CARD");
		Command command = new Command();

		command.setCommand(Command.COMMAND_REQUEST_DROP_CARD);
		command.setArg(requestId);
		command.setCard(card);

		if (PrefUtils.getInstance().getServerStatus(this)) {
			doCommandDropCard(requestId, card);
		} else {
			sendBroadcast(command);
		}
	}

	public synchronized void doCommandDropCard(int requestId, StandardCard card) {
		if (PrefUtils.getInstance().getServerStatus(this)) {
			int value = cpxGameEngine.dropCard(card);
			int status = cpxGameEngine.getDropStatus();

			Command command = new Command();
			command.setCommand(Command.COMMAND_SET_DROP_CARD);

			if (status == Constants.PlayStatus.STATUS_ERROR) {
				command.setText(getString(R.string.invalid_card));
			} else {
				command.setText(getString(R.string.status_ok));
			}

			command.setArg(status);
			command.setArg2(requestId);
			command.setArg3(value);
			command.setCard(card);

			setDropCard(status, requestId, value, card, command.getText());

			sendBroadcast(command);
		}
	}

	public void setDropCard(int status, int requestId, int value,
			StandardCard card, String message) {
		if (listenerMainCpx != null) {
			listenerMainCpx.onCardDrop(status, requestId, value, card, message);
		}
	}

	// Finish
	public void sendRequestFinishGame() {
		Log.e(TAG, "REQUEST FINISH GAME");
		Command command = new Command();

		command.setCommand(Command.COMMAND_REQUEST_FINISH_GAME);

		if (PrefUtils.getInstance().getServerStatus(this)) {
			doCommandFinishGame();
		} else {
			sendBroadcast(command);
		}
	}

	public synchronized void doCommandFinishGame() {
		if (PrefUtils.getInstance().getServerStatus(this)) {
			Command command = new Command();
			command.setCommand(Command.COMMAND_SET_FINISH_GAME);

			setFinishGame();

			sendBroadcast(command);
		}
	}

	public void setFinishGame() {
		if (listenerMainCpx != null) {
			listenerMainCpx.onGameFinish(getString(R.string.game_finish));
		}
	}

	// Scoring
	public void sendRequestScore(int requestId, int score) {
		Log.e(TAG, "CLIENT REQUEST DROP CARD");
		Command command = new Command();

		command.setCommand(Command.COMMAND_REQUEST_SCORE);
		command.setArg(requestId);
		command.setArg2(score);

		if (PrefUtils.getInstance().getServerStatus(this)) {
			doCommandScore(requestId, score);
		} else {
			sendBroadcast(command);
		}
	}

	public synchronized void doCommandScore(int requestId, int score) {
		if (PrefUtils.getInstance().getServerStatus(this)) {
			Command command = new Command();
			command.setCommand(Command.COMMAND_SET_SCORE);
			command.setArg(requestId);
			command.setArg2(score);
			command.setText(getString(R.string.status_ok));

			int idx = getPlayerIndexById(requestId);
			getPlayerList().get(idx).setScore(score);

			playerLooseCount++;

			if (score > playerLooseMax) {
				playerLooseMax = score;
				playerLooseId = requestId;
			}

			if (playerLooseCount == getPlayerList().size()) {
				doCommandLooser(playerLooseId, playerLooseMax);
			}

			setScore(requestId, score, command.getText());

			sendBroadcast(command);
		}
	}

	public synchronized void setScore(int requestId, int score, String message) {
		if (listenerMainCpx != null) {
			listenerMainCpx.onCardScore(requestId, score, message);
		}
	}

	// Request Cards Exchange
	public void sendRequestCardOther(int requestId, int requestId2) {
		Log.e(TAG, "CLIENT REQUEST EXCHANGE CARDS");
		Command command = new Command();

		command.setCommand(Command.COMMAND_REQUEST_CARD_OTHER);
		command.setArg(requestId);
		command.setArg2(requestId2);

		if (PrefUtils.getInstance().getServerStatus(this)) {
			doCommandRequestCardOther(requestId, requestId2);
		} else {
			sendBroadcast(command);
		}
	}

	public void doCommandRequestCardOther(int requestId, int requestId2) {
		if (PrefUtils.getInstance().getServerStatus(this)) {
			Command command = new Command();

			command.setCommand(Command.COMMAND_SET_REQUEST_CARD_OTHER);
			command.setArg(requestId);
			command.setArg2(requestId2);

			setRequestCardOther(requestId, requestId2);
			sendBroadcast(command);
		}
	}

	public void setRequestCardOther(int requestId, int requestId2) {
		if (listenerMainCpx != null) {
			listenerMainCpx.onCardRequestOther(requestId, requestId2);
		}
	}

	// Request Cards Exchange Back
	public void sendRequestCardOtherBack(int requestId, int requestId2,
			StandardCard[] cards) {
		Log.e(TAG, "CLIENT REQUEST EXCHANGE CARDS");
		Command command = new Command();

		command.setCommand(Command.COMMAND_REQUEST_CARD_OTHER_BACK);
		command.setArg(requestId);
		command.setArg2(requestId2);
		command.setCards(cards);

		if (PrefUtils.getInstance().getServerStatus(this)) {
			doCommandRequestCardOtherBack(requestId, requestId2, cards);
		} else {
			sendBroadcast(command);
		}
	}

	public void doCommandRequestCardOtherBack(int requestId, int requestId2,
			StandardCard[] cards) {
		if (PrefUtils.getInstance().getServerStatus(this)) {
			Command command = new Command();

			command.setCommand(Command.COMMAND_SET_REQUEST_CARD_OTHER_BACK);
			command.setArg(requestId);
			command.setArg2(requestId2);
			command.setCards(cards);

			setRequestCardOtherBack(requestId, requestId2, cards);
			sendBroadcast(command);
		}
	}

	public void setRequestCardOtherBack(int requestId, int requestId2,
			StandardCard[] cards) {
		if (listenerMainCpx != null) {
			listenerMainCpx
					.onCardRequestOtherBack(requestId, requestId2, cards);
		}
	}

	// Exchange Cards
	public void sendRequestExchangeCard(int requestId, StandardCard[] cards,
			int specialCardIndex, int targetRequestId,
			StandardCard[] targetCards) {
		Log.e(TAG, "CLIENT REQUEST EXCHANGE CARDS");
		Command command = new Command();

		command.setCommand(Command.COMMAND_REQUEST_EXCHANGE_CARD);
		command.setArg(requestId);
		command.setCards(cards);
		command.setArg2(targetRequestId);
		command.setCards2(targetCards);
		command.setArg3(specialCardIndex);

		if (PrefUtils.getInstance().getServerStatus(this)) {
			doCommandExchangeCard(requestId, cards, specialCardIndex,
					targetRequestId, targetCards);
		} else {
			sendBroadcast(command);
		}
	}

	public void doCommandExchangeCard(int requestId, StandardCard[] cards,
			int specialCardIndex, int targetRequestId,
			StandardCard[] targetCards) {
		if (PrefUtils.getInstance().getServerStatus(this)) {
			StandardCard card = null;
			try {
				card = cpxGameEngine.draw();
			} catch (EmptyDeckException e) {
			}

			cards[specialCardIndex] = card;

			Command command = new Command();

			command.setCommand(Command.COMMAND_SET_EXCHANGE_CARD);
			command.setArg(requestId);
			command.setCards(cards);
			command.setArg2(targetRequestId);
			command.setCards2(targetCards);

			setExchangeCard(requestId, cards, targetRequestId, targetCards);
			sendBroadcast(command);
		}
	}

	public void setExchangeCard(int requestId, StandardCard[] cards,
			int targetRequestId, StandardCard[] targetCards) {
		if (listenerMainCpx != null) {
			listenerMainCpx.onCardExchange(requestId, cards, targetRequestId,
					targetCards);
		}
	}

	// Set Looser
	public synchronized void doCommandLooser(int requestId, int score) {
		if (PrefUtils.getInstance().getServerStatus(this)) {
			Command command = new Command();
			command.setCommand(Command.COMMAND_SET_LOOSER);
			command.setArg(requestId);
			command.setArg2(score);

			setLooser(requestId, score);

			sendBroadcast(command);
		}
	}

	public void setLooser(int requestId, int score) {
		if (listenerMainCpx != null) {
			listenerMainCpx.onCardLooser(requestId, score);
		}
	}

	// ####### END PLAY CARD ############

	private Participant getPlayerById(int id) {
		Participant player = null;

		for (Participant p : getPlayerList()) {
			if (p.getId() == id) {
				player = p;
				break;
			}
		}

		return player;
	}

	private int getPlayerIndexById(int id) {
		int idx = 0;

		for (Participant p : getPlayerList()) {
			if (p.getId() == id) {
				return idx;
			}
			idx++;
		}

		return idx;
	}

	private Participant getPlayerBiggestScore() {
		Participant player = null;
		int score = Integer.MIN_VALUE;
		for (Participant p : getPlayerList()) {
			if (p.getScore() > score) {
				score = p.getScore();
				player = p;
			}
		}

		return player;
	}

	@Override
	protected void onResume() {
		Log.e(TAG, "onResume");
		super.onResume();
	}

	private void prepareAll() {
		if (serverBroadcastReceiver == null) {
			serverBroadcastReceiver = new ServerBroadcastReceiver(this);
		}
		registerReceiver(serverBroadcastReceiver, new IntentFilter(
				Constants.BROADCAST_SERVER));

		if (clientBroadcastReceiver == null) {
			clientBroadcastReceiver = new ClientBroadcastReceiver(this);
		}
		registerReceiver(clientBroadcastReceiver, new IntentFilter(
				Constants.BROADCAST_CLIENT));

		setShareButtonState();
		setConnectButtonState();

		System.out.println("TRACE ME onResume = "
				+ PrefUtils.getInstance().getNameID(this));
		if (PrefUtils.getInstance().getNameID(this) == null) {
			setTitle("-");
			System.out.println("TRACE ME onResume");
			// dialogShareNote =
			// Helper.getInstance().createDialogShareNote(this,
			// DialogShareNote.ACTION_DIALOG_INPUT_NAME_ID, this);
			// dialogShareNote.show();

			AlertDialog initNameDialog = getInitNameDialog();
			initNameDialog.show();
		}
	}

	private AlertDialog getInitNameDialog() {
		final View view = getLayoutInflater().inflate(
				R.layout.custom_dialog_edittext, null);
		final EditText etInputType = Helper.getInstance().createEditText(this);
		final AlertDialog d = new AlertDialog.Builder(this)
				.setView(view)
				.setPositiveButton(getString(R.string.btn_ok),
						new OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								// TODO Auto-generated method stub
							}
						})
				.setMessage(getString(R.string.dialog_insert_name_id)).create();

		d.setOnShowListener(new OnShowListener() {
			@Override
			public void onShow(final DialogInterface dialog) {
				Button buttonOk = d.getButton(AlertDialog.BUTTON_POSITIVE);
				buttonOk.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						System.out.println("TRACE ME onClick 1");
						String inputTypeValueTemp;
						EditText etInputType = (EditText) view
								.findViewById(R.id.edittext);
						if (etInputType != null
								&& !"".equals(etInputType.getText().toString())) {
							inputTypeValueTemp = etInputType.getText()
									.toString();
							PrefUtils.getInstance()
									.setNameID(getApplicationContext(),
											inputTypeValueTemp);
							setTitleState(false);
							d.dismiss();
						}
					}
				});
			}
		});

		return d;
	}

	@Override
	protected void onPause() {
		Log.e(TAG, "onPause");
		super.onPause();
	}

	@Override
	protected void onDestroy() {
		Log.e(TAG, "onDestroy");
		super.onDestroy();

		unregisterReceiver(clientBroadcastReceiver);
		unregisterReceiver(serverBroadcastReceiver);

		Log.i("panggil", "pause");
		if (PrefUtils.getInstance().getServerStatus(this)) {
			unbindService(serviceConnection);
			Intent i = Helper.getInstance().createIntent(ActivityMainCpx.this,
					ServerService.class);
			Helper.getInstance().stopService(this, i);
		}

		Log.e("panggil", "pause 2");

		if (PrefUtils.getInstance().getClientStatus(this)) {
			Intent ii = Helper.getInstance().createIntent(ActivityMainCpx.this,
					ClientService.class);
			Helper.getInstance().stopService(this, ii);
		}
		Log.e("panggil", "pause 3");
	}

	public void sendWholeMessageToClients() {
		Command shareNoteNetCommand = new Command();

		/*
		 * resetToAllClient(shareNoteNetCommand,
		 * etNoteShare.getText().toString(), serverService);
		 */
	}

	// adhi temp
	// refactor code
	public void resetToAllClient(Command c, String note, ServerService s) {
		Command shareNoteNetCommand = c;

		String sourceTemp = note;
		shareNoteNetCommand.setText(sourceTemp);
		shareNoteNetCommand.setCommand(Command.COMMAND_RESET_NOTE);

		// service is started, when the person share
		if (s != null)
			s.sendBroadcastNote(shareNoteNetCommand);

	}// end of method

	// end adhi temp

	/**
	 * Responsible : - Check if thiss app is running as client / server - If
	 * server status is true, that mean user click stop share button then it
	 * will call {@link #unbindService(ServiceConnection)} and
	 * {@link #stopService(Intent) }with Intent {@link ServerService} - If server
	 * status is false, that mean user click share button then it will call
	 * {@link #startService(Intent)} with Intent {@link ServerService}
	 * 
	 * @param v
	 */
	public void onShareClick(View v) {
		Intent i = Helper.getInstance().createIntent(ActivityMainCpx.this,
				ServerService.class);
		if (PrefUtils.getInstance().getServerStatus(this)) {
			try {
				Helper.getInstance().unBindService(this, serviceConnection);
				Helper.getInstance().stopService(this, i);
			} catch (Exception e) {
			}

			this.serverId = "";
		} else {
			Helper.getInstance().startService(this, i);
			this.serverId = PrefUtils.getInstance().getNameID(this);
		}
	}

	/**
	 * Responsible : -Check if this app is running as client / server - If
	 * client status is true, that mean user click stop button then it will call
	 * {@link #stopService(Intent)} with Intent {@link ClientService} - If
	 * client status is false it will show alertdialog asking host IP * if user
	 * click OK on the alert dialog it will start the service
	 * {@link Helper#startService(ContextWrapper, Intent)} using Intent
	 * {@link ClientService} * if user click Cancel on the alert dialog it will
	 * close the alert dialog
	 * 
	 * @param v
	 */
	public void onConnectClick(View v) {
		if (PrefUtils.getInstance().getClientStatus(this)) {
			try {
				Helper.getInstance().unBindService(this, serviceConnection);

				Intent ii = Helper.getInstance().createIntent(
						ActivityMainCpx.this, ClientService.class);
				Helper.getInstance().stopService(this, ii);
			} catch (Exception e) {
			}

			mTitleServerIP = "";
			serverId = "";
			setTitleState(false);
		} else {
			dialogConnect = Helper.getInstance().createDialogConnect(this);
			dialogConnect.show();
		}
	}

	/**
	 * Responsible: to set state ShareButton which is set from
	 * {@link ServerBroadcastReceiver}.
	 * 
	 */
	public void setShareButtonState() {
		if (PrefUtils.getInstance().getServerStatus(this)) {
			btnShare.setText(R.string.btn_unshare);
			serviceIntent = Helper.getInstance().createIntent(
					ActivityMainCpx.this, ServerService.class);
			bindService(serviceIntent, serviceConnection,
					Context.BIND_AUTO_CREATE);
			setTitleState(true);
			serverId = PrefUtils.getInstance().getNameID(this);
			btnConnect.setEnabled(false);
			startMainSettingFragment();
		} else {
			btnShare.setText(R.string.btn_share);
			setTitleState(false);
			// handle unbindservice if serviceconnection not binded
			try {
				unbindService(serviceConnection);
			} catch (Exception e) {

			}
			btnConnect.setEnabled(true);
			startMainFragment();
		}
	}

	/**
	 * Responsible: to set state ConnectButton which is set from
	 * {@link ClientBroadcastReceiver}.
	 * 
	 */
	public void setConnectButtonState() {
		if (PrefUtils.getInstance().getClientStatus(this)) {
			serviceIntent = Helper.getInstance().createIntent(
					ActivityMainCpx.this, ClientService.class);
			bindService(serviceIntent, serviceConnection,
					Context.BIND_AUTO_CREATE);
			btnShare.setEnabled(false);
			btnConnect.setText(R.string.btn_disconnect);
			setTitleState(false);
			startMainSettingFragment();
			setListenerChat(null);
			setListenerMainCpx(null);
			setListenerParticipant(null);
		} else {
			btnShare.setEnabled(true);
			this.participantList.clear();
			this.playerList.clear();
			btnConnect.setText(R.string.btn_connect);
			startMainFragment();
		}

	}

	public void startMainFragment() {
		FragmentTransaction transaction = getSupportFragmentManager()
				.beginTransaction();
		transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
		transaction.replace(R.id.flMainContainer, new FragmentMain());
		// transaction.addToBackStack(null);
		transaction.commit();
	}

	public void startMainSettingFragment() {
		FragmentTransaction transaction = getSupportFragmentManager()
				.beginTransaction();
		transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
		transaction.replace(R.id.flMainContainer, new FragmentMainConnected());
		// transaction.addToBackStack(null);
		transaction.commit();
	}
	
	public void sendRequestGameStatus(int requestId) {
		Command command = new Command();
		command.setCommand(Command.COMMAND_REQUEST_GAME_STATUS);
		command.setArg(requestId);
		
		if (PrefUtils.getInstance().getServerStatus(this)) {
			doCommandRequestGameStatus(requestId);
		} else {
			sendBroadcast(command);
		}
	}
	
	public void doCommandRequestGameStatus(int requestId) {
		if (PrefUtils.getInstance().getServerStatus(this)) {
			Command command = new Command();
			command.setCommand(Command.COMMAND_REQUEST_GAME_STATUS);
			command.setArg(requestId);
			command.setBool(isGameRunning);
			
			setGameStatus(requestId, isGameRunning);
			sendBroadcast(command);
		}
	}
	
	public void setGameStatus(int requestId, boolean gameStatus) {
		
	}

	public void startMainCpx(StandardCard[] cards, ArrayList<Participant> participants, int playerTurn) {
		FragmentTransaction transaction = getSupportFragmentManager()
				.beginTransaction();
		transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);

		FragmentMainConnected fragment = new FragmentMainConnected(cards, getPlayStartIndex());
		transaction.replace(R.id.flMainContainer, fragment);
		// transaction.addToBackStack(null);
		transaction.commit();
	}

	private void setTitleState(boolean asServer) {
		String mode = PrefUtils.getInstance().getNameID(this);
		if (asServer) {
			// mode = " <Server>";
			mode += " <host>";
		}
		// else
		// mode = mTitleServerIP + " (host)";

		setTitle("CPX: " + mode);
	}

	public void onButtonClick(DialogInterface dialog, int which,
			String inputTypeValue, int action) {

		System.out.println("TRACE ME 1");
		if (action == DialogShareNote.ACTION_DIALOG_DISCONNECT) {
			System.out.println("TRACE ME 4");
		} else if (action == DialogShareNote.ACTION_DIALOG_INVALID_CONNECTION) {
			System.out.println("TRACE ME 5");
		} else if (action == DialogShareNote.ACTION_DIALOG_INPUT_NAME_ID) {
			PrefUtils.getInstance().setNameID(this, inputTypeValue);
			setTitleState(false);
		}
		System.out.println("TRACE ME 6");
	}

	public DialogShareNote getDialog() {
		return dialogShareNote;
	}

	public ServiceConnection getServiceConnection() {
		return serviceConnection;
	}

	public String getServerId() {
		return serverId;
	}

	public int getServerChannelId() {
		return serverChannelId;
	}

	public void setServerChannelId(int serverChannelId) {
		this.serverChannelId = serverChannelId;
	}

	public void setServerId(String serverId) {
		this.serverId = serverId;
	}

	public ArrayList<Participant> getParticipantList() {
		return participantList;
	}

	public ArrayList<Participant> getPlayerList() {
		return playerList;
	}

	public void setPlayerList(ArrayList<Participant> playerList) {
		this.playerList = playerList;
	}
	
	public synchronized void updatePlayerList(ArrayList<Participant> list) {
		playerList.clear();
		playerList.addAll(list);
	}

	public synchronized void refreshParticipantList() {
		if (listenerParticipant != null) {
			listenerParticipant.onListParticipantUpdate(getParticipantList());
		}
	}

	public synchronized void setParticipantList(Participant participant) {
		if (participant != null && !"".equals(participant)) {
			this.participantList.add(participant);
		}
		if (listenerParticipant != null) {
			listenerParticipant.onParticipantUpdate(participant);
		}
	}

	public void setParticipantLeave(int participantNameId) {
		if (listenerParticipant != null) {
			listenerParticipant.onParticipantLeave(participantNameId);
		}
	}

	public void resetParticipantList() {
		this.participantList.clear();
	}

	public Button getBtnConnect() {
		return btnConnect;
	}

	public void setBtnConnect(Button btnConnect) {
		this.btnConnect = btnConnect;
	}

	public void doCommandInsert(String text) {
		if (listenerChat != null) {
			listenerChat.onChatUpdate(text);
		}
	}

	public void doCommandDelete(String text, int indexStart, int indexEnd) {
	}

	public void doCommandReplace(String text, int indexStart, int indexEnd) {
	}

	public ClientService getClientService() {
		return clientService;
	}

	public ActivityMainCpxListener getListenerChat() {
		return listenerChat;
	}

	public void setListenerChat(ActivityMainCpxListener listener) {
		this.listenerChat = listener;
	}

	public ActivityMainCpxPlayListener getListenerMainCpx() {
		return listenerMainCpx;
	}

	public void setListenerMainCpx(ActivityMainCpxPlayListener listenerMainCpx) {
		this.listenerMainCpx = listenerMainCpx;
	}

	public ActivityMainCpxListener getListenerParticipant() {
		return listenerParticipant;
	}

	public void setListenerParticipant(
			ActivityMainCpxListener listenerParticipant) {
		this.listenerParticipant = listenerParticipant;
	}

	public int getPlayStartIndex() {
		return playStartIndex;
	}

	public void setPlayStartIndex(int playStartIndex) {
		this.playStartIndex = playStartIndex;
	}

	public Rule getRule() {
		return rule;
	}

	public CpxRule getCpxRule() {
		return (CpxRule) getRule();
	}

	public void setRule(Rule rule) {
		this.rule = rule;
	}
}
