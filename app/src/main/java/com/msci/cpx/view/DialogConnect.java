package com.msci.cpx.view;

import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.msci.cpx.R;
import com.msci.cpx.network.DiscoveryClient;
import com.msci.cpx.service.ClientService;
import com.msci.cpx.utility.Helper;
import com.msci.cpx.utility.Constants;

/**
 * Class interface DialogShareNote to manage all dialog on ShareNote
 * 
 * @author MSCI
 * 
 */
public class DialogConnect extends AlertDialog {
	private final static String TAG = "ShareNote." + DialogConnect.class.getSimpleName();
	
	private ListView listView;

	private List<String> serverList = new ArrayList<String>();
	private ActivityMainCpx activityMainCpx = null;
	private DialogConnectReceiver dcr;
	private DiscoveryClient discoveryClient;
	
	private int availableServer;

	/**
	 * Constructor of DialogShareNote
	 * 
	 * @param context
	 *            : all activity resources included
	 * @param action
	 *            : action of dialog
	 * @param dialogShareNoteInterface
	 *            : listener of dialogsharenote
	 */
	public DialogConnect(Context context) {
		super(context);
		activityMainCpx = (ActivityMainCpx) context;

		dcr = new DialogConnectReceiver(activityMainCpx, this);
		context.registerReceiver(dcr, new IntentFilter(
				Constants.BROADCAST_PING));
		availableServer = 0; // set available server to 0
		discoveryClient = new DiscoveryClient(activityMainCpx);
		discoveryClient.start();

		listView = Helper.getInstance().createListView(context);
		listView.setBackgroundColor(activityMainCpx.getResources().getColor(android.R.color.white));
		setView(listView);

		listView.setAdapter(new ArrayAdapter<String>(getContext(),
				android.R.layout.simple_list_item_1, serverList));
		// list item clicked
		listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				activityMainCpx.unregisterReceiver(dcr);
				discoveryClient.stopDiscovery();
				String ip = (String) parent.getItemAtPosition(position);
				Intent intent = Helper.getInstance().createIntent(
						activityMainCpx, ClientService.class);
				intent.putExtra(Constants.BundleKey.SERVER_IP,
						ip.split(" - ")[1]);
				Helper.getInstance().startService(activityMainCpx, intent);
				activityMainCpx.mTitleServerIP = ip.split(" - ")[1];
				activityMainCpx.serverId = ip.split(" - ")[0];
				dismiss();
			}
		});

		// button cancel clicked
		setButton(DialogInterface.BUTTON_NEGATIVE,
				context.getString(R.string.btn_cancel),
				new OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						activityMainCpx.unregisterReceiver(dcr);
						discoveryClient.stopDiscovery();
						dismiss();
					}
				});
		setTitle(R.string.search_available_server);
	}

	public void registerOnlineServer(String nameAndIp) {
		//Log.e("TAG", "Finishing for " + nameAndIp);
		Log.e(TAG, "registerOnlineServer " + nameAndIp + " " + availableServer);
		if(availableServer++ == 0) {
			setTitle(R.string.btn_connect);
		}
		serverList.add(nameAndIp);
		listView.invalidate();
		((ArrayAdapter) listView.getAdapter()).notifyDataSetChanged();
	}
	
	public void finishDiscover() {
		Log.e(TAG, "finishDiscover " + availableServer);
		if(availableServer == 0) {
			setTitle(R.string.no_server_available);
		}
	}
}
