package com.msci.cpx.view;

import java.util.ArrayList;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.msci.cpx.R;
import com.msci.cpx.model.Participant;
import com.msci.cpx.utility.PrefUtils;
import com.msci.cpx.view.ActivityMainCpx.ActivityMainCpxListener;

public class FragmentSetting extends Fragment implements OnClickListener, ActivityMainCpxListener {
	private ActivityMainCpx activity;
	private TextView tvSettingHost;
	private EditText etParticipantId;
	private Button btSave;
	private Button btCancel;
	
	@Override
	public void onAttach(Activity activity) {
		this.activity = (ActivityMainCpx)activity;
		super.onAttach(activity);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_setting, null);
		
		tvSettingHost = (TextView) view.findViewById(R.id.tvSettingHostId);
		etParticipantId = (EditText) view.findViewById(R.id.etSettingId);
		btSave = (Button) view.findViewById(R.id.btSettingSave);
		btCancel = (Button) view.findViewById(R.id.btSettingCancel);
		
		tvSettingHost.setText(activity.getServerId());
		etParticipantId.setText(PrefUtils.getInstance().getNameID(getActivity()));
		btSave.setOnClickListener(this);
		btCancel.setOnClickListener(this);
		
		return view;
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btSettingSave:
			save();
			break;
		case R.id.btSettingCancel:
			cancel();
			break;

		default:
			break;
		}
	}
	
	private void save() {
		String participantId = etParticipantId.getText().toString();
		PrefUtils.getInstance().setNameID(getActivity(), participantId);
		
		Toast.makeText(getActivity(), "saved", Toast.LENGTH_SHORT).show();
	}
	
	private void cancel() {
		etParticipantId.setText(PrefUtils.getInstance().getNameID(getActivity()));
	}

	@Override
	public void onParticipantUpdate(Participant participant) {
	}

	@Override
	public void onListParticipantUpdate(ArrayList<Participant> list) {
	}

	@Override
	public void onParticipantLeave(int participantId) {
	}

	@Override
	public void onChatUpdate(String text) {
	}

	@Override
	public void onGameStatus(int requestId, boolean gameStatus) {
	}
}
