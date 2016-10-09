package com.msci.cpx.view;

import android.app.Activity;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.msci.cpx.R;

public class FragmentMain extends Fragment implements OnClickListener {
	private ActivityMainCpx activity;
	private Button btnHost;
	private Button btnConnect;
	private TextView tvAppVersion;
	
	@Override
	public void onAttach(Activity activity) {
		this.activity = (ActivityMainCpx)activity;
		this.activity.setListenerChat(null);
		this.activity.setListenerMainCpx(null);
		this.activity.setListenerParticipant(null);
		super.onAttach(activity);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_main, null);
		
		hideProgress();
		
		btnHost = (Button) view.findViewById(R.id.btShare);
		btnHost.setOnClickListener(this);
		
		btnConnect = (Button) view.findViewById(R.id.btConnect);
		btnConnect.setOnClickListener(this);
		
		tvAppVersion = (TextView) view.findViewById(R.id.tvAppVersion);
		
		PackageInfo pInfo;
		try {
			pInfo = getActivity().getPackageManager().getPackageInfo(getActivity().getPackageName(), 0);
			String version = pInfo.versionName;
			
			tvAppVersion.setText(String.format(getString(R.string.app_version), version));
		} catch (NameNotFoundException e) {
		}
		
		
		return view;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btShare:
			if (activity != null) {
				activity.onShareClick(getView());
			}
			break;
		case R.id.btConnect:
			if (activity != null) {
				activity.onConnectClick(getView());
			}
			break;

		default:
			break;
		}
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
}
