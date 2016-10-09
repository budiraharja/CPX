package com.msci.cpx.utility;

import android.content.Context;
import android.content.SharedPreferences;

public class PrefUtils {

	private static final String PREF_NAME = "PREFS";
	private static final String KEY_SERVER_UP = "server_up";
	private static final String KEY_CLIENT_UP = "client_up";
	private static final String KEY_NAME_ID = "name_id";
	private static final String SETTING_CHEAT = "setting_cheat";
	private static final String KEY_NETWORK_ID = "network_id";
	private static final String KEY_APP = "key_provider_version";
	
	private static PrefUtils instance;
	
	protected PrefUtils(){
		
	}
	public static PrefUtils getInstance() {
		if(instance == null){
			instance = new PrefUtils();
		}
		return instance;
	}
	
	//For mocking
	public synchronized static void setInstance(PrefUtils prefUtils) {
		instance = prefUtils;
	}
	
	/**
	 * 
	 * @param context
	 * @param status
	 * 
	 * Set the Server Status
	 */
	public void setServerStatus(Context context, boolean status) {
		SharedPreferences sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
		sharedPreferences.edit().putBoolean(KEY_SERVER_UP, status).commit();
	}
	
	/**
	 * 
	 * @param context
	 * @return
	 */
	public boolean getServerStatus(Context context) {
		SharedPreferences sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
		return sharedPreferences.getBoolean(KEY_SERVER_UP, false);
	}
	
	public void setClientStatus(Context context, boolean status) {
		SharedPreferences sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
		sharedPreferences.edit().putBoolean(KEY_CLIENT_UP, status).commit();
	}
	
	public boolean getClientStatus(Context context) {
		SharedPreferences sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
		return sharedPreferences.getBoolean(KEY_CLIENT_UP, false);
	}
	
	/**
	 * @param context
	 * Responsibility
	 * get Name Id from {@link SharedPreferences} with {@value #KEY_NAME_ID}
	 */
	public String getNameID(Context context){
		SharedPreferences sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
		return sharedPreferences.getString(KEY_NAME_ID,null);
	}
	
	/**
	 * @param context
	 * @param status
	 * Responsibility
	 * set Name Id to {@link SharedPreferences} with {@value #KEY_NAME_ID}
	 */
	public void setNameID(Context context, String name) {
		SharedPreferences sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
		sharedPreferences.edit().putString(KEY_NAME_ID, name).commit();
	}
	
	/**
	 * @param context
	 * Responsibility
	 * get Network Id from {@link SharedPreferences} with {@value #KEY_NETWORK_ID}
	 */
	public int getNetworkID(Context context){
		SharedPreferences sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
		return sharedPreferences.getInt(KEY_NETWORK_ID, -1);
	}
	
	/**
	 * @param context
	 * @param status
	 * Responsibility
	 * set Network Id to {@link SharedPreferences} with {@value #KEY_NETWORK_ID}
	 */
	public void setNetworkID(Context context, int id) {
		SharedPreferences sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
		sharedPreferences.edit().putInt(KEY_NETWORK_ID, id).commit();
	}
	
	public String getAppKey(Context context){
		SharedPreferences sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
		return sharedPreferences.getString(KEY_APP, "");
	}
	
	public void setAppKey(Context context, String name) {
		SharedPreferences sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
		sharedPreferences.edit().putString(KEY_APP, name).commit();
	}
}
