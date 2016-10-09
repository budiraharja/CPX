package com.msci.cpx.view;

import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.msci.cpx.R;
import com.msci.cpx.model.CpxRule;
import com.msci.cpx.model.Participant;
import com.msci.cpx.utility.PrefUtils;
import com.msci.cpx.view.FragmentMainConnected.MainConnectedListenerParticipant;

public class FragmentParticipantList extends Fragment implements OnClickListener, MainConnectedListenerParticipant {
	private ActivityMainCpx activity;
	private ListView listView;
	private TextView tvParticipantListTitle;
	private AdapterParticipant adapter;
	private Button btnStart;
	private FragmentMainConnected parentFragment;
	
	public FragmentParticipantList(FragmentMainConnected fragment) {
		this.parentFragment = fragment;
		//this.parentFragment.setListener(this);
	}
	
	@Override
	public void onAttach(Activity activity) {
		this.activity = (ActivityMainCpx)activity;
		this.parentFragment.setListenerParticipant(this);
		super.onAttach(activity);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_participant_list, null);
		
		String title = String.format(getString(R.string.participant_list_title), 1);
		tvParticipantListTitle = (TextView) view.findViewById(R.id.tvParticipantListTitle);
		tvParticipantListTitle.setText(title);
		
		listView = (ListView) view.findViewById(R.id.lvParticipantList);
		adapter = new AdapterParticipant(getActivity(), activity.getParticipantList());
		listView.setAdapter(adapter);
		
		btnStart = (Button) view.findViewById(R.id.btCpxStart);
		btnStart.setOnClickListener(this);
		
		if (!PrefUtils.getInstance().getServerStatus(getActivity())) {
			btnStart.setEnabled(false);
			btnStart.setText(getString(R.string.wait_server_to_start));
			btnStart.setTextColor(getActivity().getResources().getColor(R.color.background_color));
		}
		
		return view;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btCpxStart:
			if (activity != null) {
				activity.startCpx();
			}
			break;

		default:
			break;
		}
	}
	
	@Override
	public void onParticipantUpdate(Participant participant) {
		if (isAdded() && participant != null && !"".equals(participant)) {
			if (this.activity == null) {
				this.activity = (ActivityMainCpx) getActivity();
			}
			
			ArrayList<Participant> listParticipant = activity.getParticipantList();
				
			adapter = new AdapterParticipant(getActivity(), listParticipant);
			listView.setAdapter(adapter);
			
			String title = String.format(getString(R.string.participant_list_title), listParticipant.size());
			tvParticipantListTitle.setText(title);
			
			if (listParticipant != null && listParticipant.size() > 1 && PrefUtils.getInstance().getServerStatus(getActivity())) {
				btnStart.setEnabled(true);
			} else {
				btnStart.setEnabled(false);
			}
			
			parentFragment.updatePagerTitle(FragmentParticipantList.class, String.format(getString(R.string.title_participant_list), listParticipant.size()));
		}
	}

	@Override
	public void onListParticipantUpdate(ArrayList<Participant> list) {
	}

	@Override
	public void onParticipantLeave(int participantId) {
	}

	@Override
	public void onGameStatus(int requestId, boolean gameStatus) {
	}
}
