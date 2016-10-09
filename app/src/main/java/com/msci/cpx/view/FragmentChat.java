package com.msci.cpx.view;

import java.util.ArrayList;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.msci.cpx.R;
import com.msci.cpx.model.Participant;
import com.msci.cpx.utility.PrefUtils;
import com.msci.cpx.view.FragmentMainConnected.MainConnectedListenerChat;

public class FragmentChat extends Fragment implements OnClickListener, MainConnectedListenerChat {
	private ActivityMainCpx activity;
	private ListView listViewChat;
	private EditText etChatInput;
	private Button btChatSend;
	private ArrayAdapter<String> adapter;
	private FragmentMainConnected parentFragment;
	
	public FragmentChat(FragmentMainConnected fragment) {
		this.parentFragment = fragment;
		//this.parentFragment.setListener(this);
	}
	
	@Override
	public void onAttach(Activity activity) {
		this.activity = (ActivityMainCpx)activity;
		this.parentFragment.setListenerChat(this);
		super.onAttach(activity);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_chat, null);
		
		etChatInput = (EditText) view.findViewById(R.id.etChatInput);
		btChatSend = (Button) view.findViewById(R.id.btChatSend);
		listViewChat = (ListView) view.findViewById(R.id.lvChat);
		adapter = new ArrayAdapter<String>(getActivity(), R.layout.custom_list_simple_text, new ArrayList<String>());
		
		listViewChat.setAdapter(adapter);
		btChatSend.setOnClickListener(this);
		
		return view;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btChatSend:
			if (isAdded()) {
				String nameId = PrefUtils.getInstance().getNameID(getActivity());
				
				if (PrefUtils.getInstance().getServerStatus(getActivity())) {
					nameId += " [host]";
				}
				
				String message = nameId + ": ";
				message += etChatInput.getText().toString();
				activity.SendMsgToServer(message);
				etChatInput.setText("");
			}
			break;
		default:
			break;
		}
	}

	@Override
	public void onChatUpdate(String text) { 
		if (isAdded() && text != null && !"".equals(text)) {
			adapter.add(text);
			adapter.notifyDataSetChanged();
			
			if (adapter.getCount() > 0) {
				listViewChat.setSelection(adapter.getCount() - 1);
			}
		}
	}
}
