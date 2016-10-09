package com.msci.cpx.view;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.msci.cpx.R;
import com.msci.cpx.utility.Helper;

/**
 * Class interface DialogShareNote to manage all dialog on ShareNote
 * @author MSCI
 *
 */
public class DialogShareNote extends AlertDialog implements DialogInterface.OnClickListener {
	public final static int ACTION_DIALOG_CONNECT = 0;
	public final static int ACTION_DIALOG_DISCONNECT = 1;
	public final static int ACTION_DIALOG_INVALID_CONNECTION = 2;
	public final static int ACTION_DIALOG_INPUT_NAME_ID = 3;
	public final static int ACTION_DIALOG_SERVER_MAX_CONNECTION_REACHED = 4;
	
	EditText etInputType;
	int action;
	
	public interface DialogShareNoteInterface{
		public void onButtonClick(DialogInterface dialog, int which, String inputTypeValue, int action);
	}
	
	DialogShareNoteInterface mDialogShareNoteInterface;
	
	/**
	 * Constructor of DialogShareNote
	 * @param context: all activity resources included
	 * @param action: action of dialog
	 * @param dialogShareNoteInterface: listener of dialogsharenote
	 */
	public DialogShareNote(Context context, final int action, DialogShareNoteInterface dialogShareNoteInterface) {
		super(context);
		this.action = action;
		mDialogShareNoteInterface = dialogShareNoteInterface;
		
		if (action == ACTION_DIALOG_CONNECT){
			etInputType = Helper.getInstance().createEditText(context);
			setView(etInputType);
			setButton(DialogInterface.BUTTON_POSITIVE, context.getString(R.string.btn_connect), this);
			setButton(DialogInterface.BUTTON_NEGATIVE, context.getString(R.string.btn_cancel), this);
			setTitle(R.string.btn_connect);
			setMessage(context.getString(R.string.prompt_server_ip));
		}
		
		if (action == ACTION_DIALOG_DISCONNECT){
			setButton(DialogInterface.BUTTON_POSITIVE, context.getString(R.string.btn_ok), this);
			setTitle(R.string.app_name);
			setMessage(context.getString(R.string.dialog_disconnect));
		}
		
		if (action == ACTION_DIALOG_INVALID_CONNECTION){
			setButton(DialogInterface.BUTTON_POSITIVE, context.getString(R.string.btn_ok), this);			
			setTitle(R.string.app_name);
			setMessage(context.getString(R.string.dialog_invalid_connection));
		}
		
		if (action == ACTION_DIALOG_INPUT_NAME_ID){
			etInputType = Helper.getInstance().createEditText(context);
			final AlertDialog d = new AlertDialog.Builder(getContext())
				.setView(etInputType)
				.setPositiveButton(context.getString(R.string.btn_ok), new OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
					}
				})
				.setMessage(context.getString(R.string.dialog_insert_name_id))
				.create();
			
			d.setOnShowListener(new OnShowListener() {
				@Override
				public void onShow(final DialogInterface dialog) {
					Button buttonOk = getButton(BUTTON_POSITIVE);
					buttonOk.setOnClickListener(new View.OnClickListener() {
						@Override
						public void onClick(View v) {
							System.out.println("TRACE ME onClick 1");
							String inputTypeValueTemp;
							if (etInputType != null && etInputType.getText().toString() != ""){
								inputTypeValueTemp = etInputType.getText().toString();
								mDialogShareNoteInterface.onButtonClick(dialog, BUTTON_POSITIVE, inputTypeValueTemp, action);
								dismiss();
							}
						}
					});
				}
			});
		}
				
		if(action == ACTION_DIALOG_SERVER_MAX_CONNECTION_REACHED) {
			setButton(DialogInterface.BUTTON_POSITIVE, context.getString(R.string.btn_ok), this);
			setTitle(R.string.app_name);
			setMessage(context.getString(R.string.dialog_server_max_connection_reached));
		}
	}

	/**
	 * onClick for dialog interface
	 */
	public void onClick(DialogInterface dialog, int which) {
	}

	
}
