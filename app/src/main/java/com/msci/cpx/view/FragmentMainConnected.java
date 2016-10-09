package com.msci.cpx.view;

import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.msci.cpx.R;
import com.msci.cpx.engine.StandardCard;
import com.msci.cpx.model.Participant;
import com.msci.cpx.utility.PrefUtils;
import com.msci.cpx.view.ActivityMainCpx.ActivityMainCpxListener;

public class FragmentMainConnected extends Fragment implements android.view.View.OnClickListener, ActivityMainCpxListener {
	public static final int ACTION_HOME = 0;
	public static final int ACTION_CPX = 1;
	
	private ActivityMainCpx activity;
	private CollectionPagerAdapter collectionPagerAdapter;
	private ViewPager viewPager;
	private Button btnDisconnect;
	
	ArrayList<Fragment> listFragment;
	private ArrayList<String> listFragmentTitle;
	private MainConnectedListenerParticipant listenerParticipant;
	private MainConnectedListenerChat listenerChat;
	private int action;
	
	public interface MainConnectedListenerParticipant {
		public void onParticipantUpdate(Participant participant);
		public void onListParticipantUpdate(ArrayList<Participant> list);
		public void onParticipantLeave(int participantId);
		public void onGameStatus(int requestId, boolean gameStatus);
	}
	
	public interface MainConnectedListenerChat {
		public void onChatUpdate(String text);
	}
	
	private void init() {
		listFragment = new ArrayList<Fragment>();
		listFragmentTitle = new ArrayList<String>();
	}
	
	public FragmentMainConnected() {
		this.action = ACTION_HOME;
		init();
		listFragment.add(new FragmentParticipantList(this));
		listFragment.add(new FragmentChat(this));
	}
	
	public FragmentMainConnected(StandardCard[] cards, int startIndex) {
		this.action = ACTION_CPX;
		init();
		listFragment.add(new FragmentCpx(this, cards, startIndex));
		listFragment.add(new FragmentChat(this));
	}
	
	@Override
	public void onAttach(Activity activity) {
		this.activity = (ActivityMainCpx)activity;
		this.activity.setListenerChat(this);
		this.activity.setListenerParticipant(this);
		
		switch (this.action) {
		case ACTION_HOME:
			listFragmentTitle.add(String.format(getString(R.string.title_participant_list), 1));
			listFragmentTitle.add(String.format(getString(R.string.title_chat), ""));
			this.activity.sendRequestGameStatus(PrefUtils.getInstance().getNetworkID(getActivity()));
			break;
		case ACTION_CPX:
			listFragmentTitle.add(getString(R.string.title_cpx));
			listFragmentTitle.add(String.format(getString(R.string.title_chat), ""));
			break;

		default:
			break;
		}
		
		super.onAttach(activity);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_main_connected, null);
		
		hideProgress();
		
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setMessage(getString(R.string.welcome_participant))
				.setPositiveButton(getString(R.string.bt_ok), new OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				})
				.create()
				.show();
		
		viewPager = (ViewPager) view.findViewById(R.id.pager);
		
		//listFragment = new ArrayList<Fragment>();
		//listFragment.add(new FragmentParticipantList(this));
		//listFragment.add(new FragmentChat(this));
		//listFragment.add(new FragmentSetting());
		
		//listFragmentTitle = new ArrayList<String>();
		//listFragmentTitle.add(String.format(getString(R.string.title_participant_list), 1));
		//listFragmentTitle.add(String.format(getString(R.string.title_chat), ""));
		//listFragmentTitle.add(getString(R.string.title_settings));
		
		collectionPagerAdapter = new CollectionPagerAdapter(getFragmentManager(), listFragment, listFragmentTitle);
		viewPager.setAdapter(collectionPagerAdapter);
		
		btnDisconnect = (Button) view.findViewById(R.id.btDisconnect);
		btnDisconnect.setOnClickListener(this);
		
		return view;
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
		case R.id.btDisconnect:
			if (activity != null) {
				if (PrefUtils.getInstance().getClientStatus(getActivity())) {
					activity.onConnectClick(getView());
				} else if (PrefUtils.getInstance().getServerStatus(getActivity())) {
					activity.onShareClick(getView());
				}
			}
			break;

		default:
			break;
		}
	}

	public class CollectionPagerAdapter extends FragmentStatePagerAdapter {
		private ArrayList<Fragment> listFragment;
		private ArrayList<String> listTitle;
		
	    public CollectionPagerAdapter(FragmentManager fm, ArrayList<Fragment> list, ArrayList<String> titles) {
	        super(fm);
	        this.listFragment = list;
	        this.listTitle = titles;
	    }

	    @Override
	    public Fragment getItem(int i) {
	        Fragment fragment = listFragment.get(i);
	        return fragment;
	    }

	    @Override
	    public int getCount() {
	        return listFragment.size();
	    }

	    @Override
	    public CharSequence getPageTitle(int position) {
	        return listTitle.get(position);
	    }
	    
	    public void updateTitle(int position, String title) {
	    	listTitle.set(position, title);
	    	notifyDataSetChanged();
	    }
	}
	
	public void updatePagerTitle(Class<?> clazz, String title) {
		if (clazz.equals(FragmentParticipantList.class)) {
			collectionPagerAdapter.updateTitle(0, title);
			viewPager.setAdapter(collectionPagerAdapter);
		}
	}

	public MainConnectedListenerParticipant getListenerParticipant() {
		return listenerParticipant;
	}

	public void setListenerParticipant(
			MainConnectedListenerParticipant listenerParticipant) {
		this.listenerParticipant = listenerParticipant;
	}

	public MainConnectedListenerChat getListenerChat() {
		return listenerChat;
	}

	public void setListenerChat(MainConnectedListenerChat listenerChat) {
		this.listenerChat = listenerChat;
	}

	@Override
	public void onParticipantUpdate(Participant participant) {
		if (viewPager != null && viewPager.getCurrentItem() != 0) {
			if (participant != null) {
				Toast.makeText(getActivity(), String.format(getString(R.string.participant_update), participant.getNameId()), Toast.LENGTH_SHORT).show();
			}
		}
		if (listenerParticipant != null) {
			listenerParticipant.onParticipantUpdate(participant);
		}
	}

	@Override
	public void onListParticipantUpdate(ArrayList<Participant> list) {
		if (listenerParticipant != null) {
			listenerParticipant.onListParticipantUpdate(list);
		}
	}

	@Override
	public void onParticipantLeave(int participantNameId) {
		Toast.makeText(getActivity(), "Leave: " + participantNameId, Toast.LENGTH_SHORT).show();
		if (listenerParticipant != null) {
			listenerParticipant.onParticipantLeave(participantNameId);
		}
	}

	@Override
	public void onChatUpdate(String text) {
		if (viewPager != null && viewPager.getCurrentItem() != 1) {
			Toast.makeText(getActivity(), String.format(getString(R.string.chat_update), text), Toast.LENGTH_LONG).show();
			collectionPagerAdapter.updateTitle(1, String.format(getString(R.string.title_chat), ""));
		}
		if (listenerChat != null) {
			listenerChat.onChatUpdate(text);
		}
	}

	@Override
	public void onGameStatus(int requestId, boolean gameStatus) {
		if (listenerParticipant != null) {
			listenerParticipant.onGameStatus(requestId, gameStatus);
		}
	}
}
