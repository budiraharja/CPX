package com.msci.cpx.view;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.msci.cpx.utility.Constants;

public class DialogConnectReceiver extends BroadcastReceiver {
	ActivityMainCpx shareNoteActivity;
	//KeyListener keyListenerEditText;
	DialogConnect dialogConnect;

	public DialogConnectReceiver(ActivityMainCpx shareNoteActivity, DialogConnect dc) {
		this.shareNoteActivity = shareNoteActivity;
		//keyListenerEditText = shareNoteActivity.etNoteShare.getKeyListener();
		this.dialogConnect = dc;
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		Log.e("TAG", "onReceive DialogConnectReceiver");
		
		String cmd = intent.getStringExtra(Constants.ClientCommand.TEXT);
		if(cmd.equals(Constants.PING_FINISH)) {
			dialogConnect.finishDiscover();
		}else {
			dialogConnect.registerOnlineServer(cmd);
		}
	}
}
