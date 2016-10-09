package com.msci.cpx.view;

import java.util.ArrayList;
import java.util.Random;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.jess.ui.TwoWayGridView;
import com.msci.cpx.R;
import com.msci.cpx.engine.CpxGameEngine;
import com.msci.cpx.engine.StandardCard;
import com.msci.cpx.engine.StandardCard.Rank;
import com.msci.cpx.model.CpxRule;
import com.msci.cpx.model.Participant;
import com.msci.cpx.model.Rule;
import com.msci.cpx.utility.Constants;
import com.msci.cpx.utility.Helper;
import com.msci.cpx.utility.PrefUtils;
import com.msci.cpx.view.ActivityMainCpx.ActivityMainCpxPlayListener;
import com.msci.cpx.view.FragmentMainConnected.MainConnectedListenerParticipant;

public class FragmentCpx extends Fragment implements OnClickListener, ActivityMainCpxPlayListener, MainConnectedListenerParticipant {
	private ActivityMainCpx activity;
	private FragmentMainConnected parentFragment;
	private ListView listView;
	private AdapterParticipant adapter;
	private AdapterCardGallery adapterCard;
	private TextView tvCardScore;
	private TextView tvLastAction;
	private ImageView ivLastCard;
	private Button btnPlay;
	private Button btnDrop;
	private Button btnDisconnect;
	private LinearLayout llButtonContainer;
	private Button btnRestart;
	private int playerId;
	private int dropped = 0;
	private StandardCard[] cards = null;
	private TwoWayGridView gvCards;
	private int turnIndex = 0;
	private MediaPlayer sound;
	private MediaPlayer soundLoose;
	private int countLoose = 0;
	private boolean turnRevert = false;
	private boolean isGameFinish = false;
	
	public boolean isTurnRevert() {
		return turnRevert;
	}

	public void setTurnRevert(boolean turnRevert) {
		this.turnRevert = turnRevert;
	}
	
	public FragmentCpx(FragmentMainConnected fragment, StandardCard[] cards, int startIndex) {
		this.parentFragment = fragment;
		this.cards = cards;
		this.turnIndex = startIndex;
	}
	
	@Override
	public void onAttach(Activity activity) {
		this.activity = (ActivityMainCpx)activity;
		this.activity.setListenerMainCpx(this);
		this.parentFragment.setListenerParticipant(this);
		super.onAttach(activity);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_cpx, null);
		
		hideProgress();
		setGameFinish(false);
		
		sound = MediaPlayer.create(getActivity(), R.raw.turn_in);
		soundLoose = MediaPlayer.create(getActivity(), R.raw.loose);
		
		showProgress();
		
		this.gvCards = (TwoWayGridView) view.findViewById(R.id.gvCards);
		this.listView = (ListView) view.findViewById(R.id.lvCpxCardParticipantList);
		
		this.playerId = PrefUtils.getInstance().getNetworkID(getActivity());
		
		this.tvCardScore = (TextView) view.findViewById(R.id.tvCpxCardScore);
		this.tvLastAction = (TextView) view.findViewById(R.id.tvLastAction);
		this.ivLastCard = (ImageView) view.findViewById(R.id.ivLastCard);
		this.btnPlay = (Button) view.findViewById(R.id.btnCpxCardPlay);
		this.btnDrop = (Button) view.findViewById(R.id.btnCpxCardDrop);
		this.btnDisconnect = (Button) view.findViewById(R.id.btDisconnect);
		this.llButtonContainer = (LinearLayout) view.findViewById(R.id.llCpxCardDeckButton);
		this.btnRestart = (Button) view.findViewById(R.id.btnCpxRestart);
		
		this.tvCardScore.setText(String.valueOf(CpxGameEngine.INIT_GAME_SCORE));
		this.btnPlay.setOnClickListener(this);
		this.btnDrop.setText(String.format(getString(R.string.bt_drop), dropped));
		this.btnDrop.setOnClickListener(this);
		this.btnDisconnect.setOnClickListener(this);
		this.btnRestart.setOnClickListener(this);
		
		this.llButtonContainer.setVisibility(View.VISIBLE);
		this.btnRestart.setVisibility(View.INVISIBLE);
		
		if (this.cards == null) {
			this.activity.sendRequestCardDealToServer(Helper.getInstance().getIpLocalAddress(getActivity()),
					PrefUtils.getInstance().getAppKey(getActivity()));
		} else {
			hideProgress();
			this.adapter = new AdapterParticipant(getActivity(), activity.getPlayerList(), R.layout.custom_list_small, R.color.color_white);
			this.listView.setAdapter(this.adapter);
			
			Toast.makeText(getActivity(), "ON CREATE VIEW CPX", Toast.LENGTH_LONG).show();
			this.adapter.setSelectedIndex(turnIndex);
			
			if (adapter.getIndexById(playerId) != turnIndex) {
				this.btnPlay.setEnabled(false);
				this.btnDrop.setEnabled(false);
			}
			
			this.adapterCard = new AdapterCardGallery(getActivity(), this.cards);
			this.gvCards.setAdapter(adapterCard);
		}
		
		return view;
	}
	
	@Override
	public void onStop() {
		if (activity != null) {
			activity.hideProgress();
		}
		super.onStop();
	}

	public boolean isGameFinish() {
		return isGameFinish;
	}

	public void setGameFinish(boolean isGameFinish) {
		this.isGameFinish = isGameFinish;
	}

	private void showProgress() {
		if (activity != null) {
			activity.showProgress();
		}
	}
	
	private void hideProgress() {
		if (activity != null) {
			activity.hideProgress();
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btnCpxCardPlay:
			play();
			break;
		case R.id.btnCpxCardDrop:
			drop();
			break;
		case R.id.btDisconnect:
			if (activity != null) {
				if (PrefUtils.getInstance().getClientStatus(getActivity())) {
					activity.onConnectClick(getView());
				} else if (PrefUtils.getInstance().getServerStatus(getActivity())) {
					activity.onShareClick(getView());
				}
			}
			break;
		case R.id.btnCpxRestart:
			if (activity != null) {
				activity.startCpx();
			}
			break;
		default:
			break;
		}
	}
	
	private void play() {
		final StandardCard card = adapterCard.getSelectedCard(); 
		if (card != null) {
			Rule rule = activity.getRule();
			if (rule != null) {
				if (((CpxRule)rule).isSpecialCard() && card.getRank().equals(Rank.Special)) {
					showDialogSpecialCard(card);
				} else if (card.getRank().equals(Rank.Ten)) {
					AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
				    builder.setTitle(R.string.set_action)
				           .setItems(R.array.ten_card_action, new DialogInterface.OnClickListener() {
				               public void onClick(DialogInterface dialog, int which) {
				            	   switch (which) {
									case 0:
										activity.sendRequestPlayCard(playerId, card, false);
										break;
									case 1:
										activity.sendRequestPlayCard(playerId, card, true);
										break;
									}
				               }
				           });
				    builder.setNegativeButton(getString(R.string.bt_cancel), new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();
						}
					});
				    builder.create().show();
				} else {
					showProgress();
					this.activity.sendRequestPlayCard(playerId, card, false);
				}
			}
			
		} else {
			Toast.makeText(getActivity(), getString(R.string.select_card), Toast.LENGTH_SHORT).show();
		}
	}
	
	private void showDialogSpecialCard(final StandardCard card) {
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
	    builder.setTitle(R.string.set_action)
	           .setItems(R.array.special_card_action, new DialogInterface.OnClickListener() {
	               public void onClick(DialogInterface dialog, int which) {
	            	   switch (which) {
						case 0:
							showDialogParticipant(Constants.Play.SPECIAL_CARD_EXCHANGE);
							break;
						case 1:
							showDialogParticipant(Constants.Play.SPECIAL_CHOOSE_TURN);
							break;
						}
	               }
	           });
	    builder.setNegativeButton(getString(R.string.bt_cancel), new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});
	    builder.create().show();
	}
	
	private void showDialogParticipant(final int action) {
		View view = getActivity().getLayoutInflater().inflate(R.layout.custom_list_view, null);
		
		ArrayList<Participant> list = activity.getPlayerList();
		
		final AdapterParticipant adapter = new AdapterParticipant(getActivity(), list, R.layout.custom_list, R.color.color_black);
		ListView listView = (ListView) view.findViewById(R.id.lvCustomList);
		listView.setAdapter(adapter);
		
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
	    builder.setTitle(R.string.set_action)
	           .setView(view)
	           .setNegativeButton(getString(R.string.bt_cancel), new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});
	    
	    final AlertDialog dialog = builder.create();
	    dialog.show();
	    
	    listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> adapterView, View view, int position,
					long id) {
				int playerIndex = adapter.getIndexById(playerId);
				Participant participant = (Participant)adapter.getItem(position);
				if (playerIndex == position) {
					Toast.makeText(getActivity(), getString(R.string.exchange_invalid), Toast.LENGTH_SHORT).show();
				} else if (participant != null) {
					showProgress();
					StandardCard[] cards = adapterCard.getCards();
					
					switch (action) {
					case Constants.Play.SPECIAL_CARD_EXCHANGE: {
							activity.sendRequestCardOther(playerId, participant.getId());
							dialog.dismiss();
						}
						break;
					case Constants.Play.SPECIAL_CHOOSE_TURN: {
							int index = position;
							
							if (!turnRevert) {
								if (index == 0) {
									index = adapter.getCount() - 1;
								} else {
									index--;
								}
							} else {
								if (index >= adapter.getCount() -1) {
									index = 0;
								} else {
									index++;
								}
							}
							activity.sendRequestDrawCard(playerId, false);
							activity.sendRequestNextTurn(playerId, index, adapterCard.isCardEmpty(), turnRevert);
							dialog.dismiss();
						}
						break;
					default:
						dialog.dismiss();
						break;
					}
				}
			}
		});
	}
	
	private void drop() {
		StandardCard card = adapterCard.getSelectedCard(); 
		if (card != null && isDropValid()) {
			showProgress();
			this.activity.sendRequestDropCard(playerId, card);
		} else if (!isDropValid()) {
			Toast.makeText(getActivity(), getString(R.string.drop_invalid), Toast.LENGTH_SHORT).show();
		} else {
			Toast.makeText(getActivity(), getString(R.string.select_card), Toast.LENGTH_SHORT).show();
		}
	}
	
	private boolean isDropValid() {
		Rule rule = activity.getRule();
		
		for (StandardCard card:adapterCard.getCards()) {
			if (card != null) {
				if (Rank.Jack.equals(card.getRank())
						|| Rank.Queen.equals(card.getRank())
						|| Rank.King.equals(card.getRank())
						|| Rank.Ten.equals(card.getRank())
						|| Rank.Joker.equals(card.getRank())) {
					return false;
				} else if (Rank.Special.equals(card.getRank()) && ((CpxRule)rule).isSpecialCard()) {
					return false;
				}
			}
		}
		
		return true;
	}
	
	private void playSound(){
  		try{
  			if (sound.isPlaying()) {
  				sound.stop();
  				sound.release();
  			}
  		}catch(Exception e){
  		}

  		sound.setLooping(false); // Set looping
  		sound.start();
  		vibrateShort();
  	}
	
	private void playSoundLooser(){
		
  		try{
  			if (soundLoose.isPlaying()) {
  				soundLoose.stop();
  				soundLoose.release();
  			}
  			
	  		soundLoose.setLooping(false); // Set looping
	  		soundLoose.start();
	  		vibrateLong();
  		}catch(Exception e){
  		}
  	}
	
	private void vibrateShort() {
		vibrate(new long[]{0, 100, 100, 100});
	}
	
	private void vibrateLong() {
		vibrate(new long[]{0, 1000, 100, 100, 100, 100, 2000});
	}
	
	private void vibrate(long[] pattern) {
		Vibrator v = (Vibrator) activity.getSystemService(Context.VIBRATOR_SERVICE);
		v.vibrate(pattern, -1);
	}

	@Override
	public void onDeckReady() {
	}

	@Override
	public void onCardDeal(StandardCard[] cards, String message, int turnPlayerIndex) {
		hideProgress();
		if (cards != null) {
			this.adapter = new AdapterParticipant(getActivity(), activity.getPlayerList(), R.layout.custom_list_small, R.color.color_white);
			this.listView.setAdapter(this.adapter);
			
			Toast.makeText(getActivity(), "ON CARD DEAL", Toast.LENGTH_LONG).show();
			this.adapter.setSelectedIndex(turnIndex);
			this.turnIndex = turnPlayerIndex;
			
			if (adapter.getIndexById(playerId) != turnIndex) {
				this.btnPlay.setEnabled(false);
				this.btnDrop.setEnabled(false);
			} else {
				this.btnPlay.setEnabled(true);
				this.btnDrop.setEnabled(true);
			}
			
			ivLastCard.setImageBitmap(null);
			CpxRule rule = (CpxRule)activity.getRule();
			setGameFinish(false);
			
			if (rule != null) {
				String[] rules = new String[2];
				rules[0] = String.format(getString(R.string.rule_deck_count), rule.getDeckCount());
				rules[1] = String.format(getString(R.string.rule_special_card), rule.isSpecialCard() ? "Available" : "Not Available");
				
				AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
				builder.setTitle(R.string.rule)
						.setItems(rules, null)
						.setPositiveButton(getString(R.string.bt_ok), new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								dialog.dismiss();
							}
						})
						.create()
						.show();
			}
			
			if (turnPlayerIndex >= 0) {
				this.turnIndex = turnPlayerIndex;
				adapter.setSelectedIndex(turnPlayerIndex);
			}
			this.cards = cards;
			this.adapterCard = new AdapterCardGallery(getActivity(), cards);
			this.gvCards.setAdapter(adapterCard);
		} else {
			Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
		}
	}

	@Override
	public void onCardDraw(int requestId, StandardCard card, boolean setNextTurn, String message) {
		hideProgress();
		if (!setNextTurn) {
			Participant participant = adapter.getParticipantById(requestId);
			if (participant != null) {
				this.tvLastAction.setText(String.format(getString(R.string.last_action), participant.getNameId(), "played " + StandardCard.Rank.Special.toString()));
			}
			this.ivLastCard.setImageBitmap(Helper.getInstance().getSpecialCardBitmap(getActivity()));
		}
		if (requestId == playerId) {
			adapterCard.replaceCard(card);
			if (card == null) {
				Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
			}
			if (setNextTurn) {
				activity.sendRequestNextTurn(playerId, adapter.getIndexById(playerId), adapterCard.isCardEmpty(), turnRevert);
			}
		}
	}

	@Override
	public void onCardPlay(int status, StandardCard card, int requestId, int score, String message) {
		hideProgress();
		if (score == CpxGameEngine.JOKER_VALUE && status == Constants.PlayStatus.STATUS_OK) {
			setTurnRevert(!turnRevert);
			Toast.makeText(getActivity(), getString(R.string.play_joker), Toast.LENGTH_SHORT).show();
			
			Participant participant = adapter.getParticipantById(requestId);
			if (participant != null) {
				this.tvLastAction.setText(String.format(getString(R.string.last_action), participant.getNameId(), "played " + card.toString()));
			}
			this.ivLastCard.setImageBitmap(Helper.getInstance().getBitmap(getActivity(), card));
			
			if (this.playerId == requestId) {
				activity.sendRequestDrawCard(playerId, true);
			}
		} else if (this.playerId == requestId && status != Constants.PlayStatus.STATUS_OK) {
			Toast.makeText(getActivity(), getString(R.string.invalid_card), Toast.LENGTH_SHORT).show();
		} else if (status == Constants.PlayStatus.STATUS_OK && score != CpxGameEngine.JOKER_VALUE) {
			this.tvCardScore.setText(String.valueOf(score));
			Participant participant = adapter.getParticipantById(requestId);
			if (participant != null) {
				this.tvLastAction.setText(String.format(getString(R.string.last_action), participant.getNameId(), "played " + card.toString()));
			}
			this.ivLastCard.setImageBitmap(Helper.getInstance().getBitmap(getActivity(), card));

			//draw a card
			if (this.playerId == requestId) {
				activity.sendRequestDrawCard(playerId, true);
			}
		}
		
	}

	@Override
	public void onTurn(int requestId, int index, boolean isRevert, String message) {
		hideProgress();
		turnIndex = index;
		
		if (adapter.getCount() != activity.getPlayerList().size()) {
			adapter.updateList(activity.getPlayerList());
		}
		adapter.setSelectedIndex(turnIndex);
		turnRevert = isRevert;

		int playerIndex = adapter.getIndexById(playerId);
		if (turnIndex == playerIndex) {
			boolean isEmpty = adapterCard.isCardEmpty();
			
			if (!isEmpty) {
				playSound();
				Toast.makeText(getActivity(), getString(R.string.your_turn), Toast.LENGTH_SHORT).show();

				this.btnPlay.setEnabled(true);
				this.btnDrop.setEnabled(true);
			} else {
				activity.sendRequestNextTurn(playerId, adapter.getIndexById(playerId), isEmpty, turnRevert);
			}
		} else {
			this.btnPlay.setEnabled(false);
			this.btnDrop.setEnabled(false);
		}
	}

	@Override
	public void onCardDrop(int status, int requestId, int value,
			StandardCard card, String message) {
		hideProgress();
		Participant participant = adapter.getParticipantById(requestId);
		if (participant != null) {
			this.tvLastAction.setText(String.format(getString(R.string.last_action), participant.getNameId(), "dropped a card"));
		}
		
		if (requestId == playerId) {
			if (status == Constants.PlayStatus.STATUS_OK) {
				dropped += value;
				this.btnDrop.setText(String.format(getString(R.string.bt_drop), dropped));
				
				adapterCard.replaceCard(card);
				showProgress();
				activity.sendRequestDrawCard(playerId, true);
			} else {
				Toast.makeText(getActivity(), getString(R.string.invalid_card), Toast.LENGTH_SHORT).show();
			}
		}
	}

	@Override
	public void onCardScore(int requestId, int score, String message) {
		hideProgress();
		setGameFinish(true);
		adapter.setShowScore(true);
		adapter.updateScore(requestId, score);
	}

	@Override
	public void onGameFinish(String message) {
		showProgress();
		setGameFinish(true);
		Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
		activity.sendRequestScore(playerId, dropped);
		adapter.setSelectedIndex(-1);
	}

	@Override
	public void onCardRequestOther(int requestId, int requestId2) {
		if (playerId == requestId2) {
			showProgress();
			activity.sendRequestCardOtherBack(requestId, playerId, adapterCard.getCards());
		}
	}

	@Override
	public void onCardRequestOtherBack(int requestId, int requestId2,
			StandardCard[] cards) {
		if (playerId == requestId) {
			showProgress();
			adapterCard.replaceCard(null);
			activity.sendRequestExchangeCard(requestId, adapterCard.getCards(), adapterCard.getSelectedIndex(), requestId2, cards);
		}
	}

	@Override
	public void onCardExchange(int requestId, StandardCard[] cards,
			int requestId2, StandardCard[] cards2) {
		Participant participant = adapter.getParticipantById(requestId);
		if (participant != null) {
			this.tvLastAction.setText(String.format(getString(R.string.last_action), participant.getNameId(), "played " + StandardCard.Rank.Special.toString()));
		}
		this.ivLastCard.setImageBitmap(Helper.getInstance().getSpecialCardBitmap(getActivity()));
		
		if (requestId == playerId) {
			hideProgress();
			adapterCard.replaceCards(cards2);
			activity.sendRequestNextTurn(playerId, adapter.getIndexById(playerId), adapterCard.isCardEmpty(), turnRevert);
			Toast.makeText(getActivity(), getString(R.string.exchange_cards), Toast.LENGTH_SHORT).show();
		}
		
		if (requestId2 == playerId) {
			hideProgress();
			if (participant != null) {
				AlertDialog dialog = Helper.getInstance().createAlertDialog(
						getActivity(), 
						null, 
						String.format(getString(R.string.exchange_message), participant.getNameId()), 
						new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog, int which) {
									dialog.dismiss();
								}
							}, 
						null);
				dialog.show();
			}
			adapterCard.replaceCards(cards);
			Toast.makeText(getActivity(), getString(R.string.exchange_cards), Toast.LENGTH_SHORT).show();
		}
	}

	@Override
	public void onCardLooser(int requestId, int score) {
		if (playerId == requestId) {
			playSoundLooser();
			AlertDialog dialog = Helper.getInstance().createAlertDialog(
					getActivity(), 
					null, 
					getString(R.string.loose), 
					new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								dialog.dismiss();
							}
						}, 
					null);
			dialog.show();
		}
		
		btnPlay.setEnabled(false);
		btnDrop.setEnabled(false);
		
		if (PrefUtils.getInstance().getServerStatus(getActivity())) {
			llButtonContainer.setVisibility(View.INVISIBLE);
			btnRestart.setVisibility(View.VISIBLE);
		}
	}

	@Override
	public void onParticipantUpdate(Participant participant) {
	}

	@Override
	public void onListParticipantUpdate(ArrayList<Participant> list) {
		if (list.size() < adapter.getCount()) {
			Participant missingParticipant = adapter.getMissingParticipant(list);
			
			if (missingParticipant != null) {
				Toast.makeText(getActivity(), String.format(getString(R.string.participant_leave), missingParticipant.getNameId()), Toast.LENGTH_SHORT).show();
				
				//int missingIndex = adapter.getIndexById(missingParticipant.getId());
				
				//if (missingIndex == adapter.getSelectedIndex()) {
					adapter.updateList(list);
					listView.setAdapter(adapter);
					
					activity.updatePlayerList(list);
					
					int turn = 0;
					
					Toast.makeText(getActivity(), getString(R.string.participant_leave_update_turn), Toast.LENGTH_SHORT).show();
					
					if (adapter.getCount() > 0) {
						Participant p = (Participant)adapter.getItem(turn);
						
						if (p != null && p.getId() == playerId) {
							
							Random rnd = new Random(System.nanoTime());
							int playerTurn = rnd.nextInt(list.size());
							
							activity.sendRequestNextTurn(p.getId(), playerTurn, false, turnRevert);
						}
					}
				//} else {
				//	adapter = new AdapterParticipant(getActivity(), list, R.layout.custom_list_small, R.color.color_white);
				//	listView.setAdapter(adapter);
				//}
			}
		}
	}

	@Override
	public void onParticipantLeave(int participantId) {
	}

	@Override
	public void onGameStatus(int requestId, boolean gameStatus) {
	}
	
}
