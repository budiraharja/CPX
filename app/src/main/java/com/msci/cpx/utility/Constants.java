package com.msci.cpx.utility;

/**
 * 
 * @author MSCI
 *
 * Constant for the Share Note Apps
 */
public class Constants {
	
	public static final int CONNECT_PORT = 8080;
	public static final char[] APP_KEY = new char[]{'M','5','C','1','S','M','S','9','!','#'};

	/**
	 * 
	 * @author MSCI
	 *
	 * Constant class for the key when sending intent through Bundle
	 */
	public static class BundleKey {
		public static final String SERVER_IP = "server_ip";
		public static final String SERVER_ID = "server_id";
		public static final String CONTENT_CURSOR = "content_cursor";
		public static final String CLIENT_STATUS = "client_status";
		public static final String CLIENT_COMMAND = "client_command";
		public static final String CLIENT_ID = "client_id";
		public static final String SERVER_STATUS = "server_status";
	}
	
	public static final String BROADCAST_SERVER = "com.msci.sharenote.serverbroadcast";
	public static final String BROADCAST_CLIENT = "com.msci.sharenote.clientbroadcast";
	public static final String BROADCAST_PING = "com.msci.sharenote.serverping";
	
	public static final String PING_FINISH = "finish";
	
	/**
	 * 
	 * @author MSCI
	 * Status Constant of the Client Service 
	 */
	public static class ClientStatus {
		public static final int CONNECTED = 1;
		public static final int DISCONNECTED = 2;
		public static final int CONNECTION_REFUSED = 3;
		public static final int OTHER_ERROR = 4;
		public static final int INCOMING_MESSAGE = 5;
		public static final int SERVER_MAX_CONNECTION_REACHED = 6;
		public static final int CARD_PLAY = 7;
	}
	
	public static class ClientCommand {
		public static String INDEX_START = "index_start";
		public static String INDEX_END = "index_end";
		public static String TEXT = "text";
		public static String IP = "ip";
		public static String CARD = "card";
		public static String CARD2 = "card2";
		public static String ARG = "arg";
		public static String ARG2 = "arg2";
		public static String ARG3 = "arg3";
		public static String OBJ = "obj";
		public static String OBJ2 = "obj2";
		public static String BOOLEAN = "bool";
	}
	
	public static class ServerStatus {
		public static final int SERVER_STARTED = 1;
		public static final int SERVER_STOPPED = 2;
	}
	
	public static class Play {
		public static final int SPECIAL_CARD_EXCHANGE = 0;
		public static final int SPECIAL_CHOOSE_TURN = 1;
	}
	
	public static class PlayStatus {
		public static final int STATUS_OK = 1;
		public static final int STATUS_ERROR = 0;
	}
	
}
