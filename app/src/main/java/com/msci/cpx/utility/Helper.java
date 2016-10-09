package com.msci.cpx.utility;

import java.net.InetSocketAddress;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.jboss.netty.bootstrap.ClientBootstrap;
import org.jboss.netty.channel.ChannelFactory;
import org.jboss.netty.channel.socket.nio.NioClientSocketChannelFactory;

import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.wifi.WifiManager;
import android.text.format.Formatter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.msci.cpx.R;
import com.msci.cpx.engine.StandardCard;
import com.msci.cpx.engine.StandardCard.Rank;
import com.msci.cpx.engine.StandardCard.Suit;
import com.msci.cpx.network.Command;
import com.msci.cpx.network.CommandDecoder;
import com.msci.cpx.network.CpxClient;
import com.msci.cpx.network.CpxClientHandler;
import com.msci.cpx.network.CpxServer;
import com.msci.cpx.service.ClientService;
import com.msci.cpx.service.ServerService;
import com.msci.cpx.view.DialogConnect;
import com.msci.cpx.view.DialogShareNote;
import com.msci.cpx.view.DialogShareNote.DialogShareNoteInterface;

public class Helper {

	private static Helper instance;

	protected Helper() {
	}

	public synchronized static Helper getInstance() {
		if (instance == null) {
			instance = new Helper();
		}
		return instance;
	}

	// For mocking
	public synchronized static void setInstance(Helper classBuilder) {
		instance = classBuilder;
	}

	public AlertDialog createAlertDialog(Context context) {
		return new AlertDialog.Builder(context).create();
	}

	public EditText createEditText(Context context) {
		return new EditText(context);
	}

	public ListView createListView(Context context) {
		return new ListView(context);
	}

	public Intent createIntent(Context context, Class<?> cls) {
		return new Intent(context, cls);
	}

	public ComponentName startService(ContextWrapper cw, Intent intent) {
		Toast.makeText(cw, "Starting", Toast.LENGTH_SHORT).show();
		return cw.startService(intent);
	}

	public DialogShareNote createDialogShareNote(Context context, int action,
			DialogShareNoteInterface dialogShareNoteInterface) {
		return new DialogShareNote(context, action, dialogShareNoteInterface);
	}

	public DialogConnect createDialogConnect(Context context) {
		return new DialogConnect(context);
	}

	public ClientBootstrap createClientBootstrap(ChannelFactory factory) {
		return new ClientBootstrap(factory);
	}

	public boolean stopService(ContextWrapper cw, Intent intent) {
		return cw.stopService(intent);
	}

	public void unBindService(ContextWrapper cw, ServiceConnection conn) {
		cw.unbindService(conn);
	}

	public CpxClient createClientConnect(Context context, String serverIP,
			ClientService clientService) {
		return new CpxClient(context, serverIP, clientService);
	}

	public Intent createIntent(String broadcastClient) {
		return new Intent(broadcastClient);
	}

	public CpxServer createServerConnect(Context context,
			ServerService serverService) {
		return new CpxServer(context, serverService);
	}

	public Command createShareNoteNetCommand() {
		return new Command();
	}

	public CommandDecoder createShareNoteNetCommandDecoder() {
		return new CommandDecoder();
	}

	//
	// public ShareNotePipelineFactory createShareNotePipelineFactory(
	// ClientService clientService) {
	// return new ShareNotePipelineFactory(clientService);
	// }

	public CpxClientHandler createClientNoteHandler(ClientService clientService) {
		return new CpxClientHandler(clientService);
	}

	public NioClientSocketChannelFactory createNioClientSocketChannelFactory(
			ExecutorService newCachedThreadPool,
			ExecutorService newCachedThreadPool2) {
		return new NioClientSocketChannelFactory(
				Executors.newCachedThreadPool(),
				Executors.newCachedThreadPool());
	}

	public InetSocketAddress createInetSocketAddress(String serverIP, int i) {
		return new InetSocketAddress(serverIP, i);
	}

	public ContentValues createContentValues() {
		// TODO Auto-generated method stub
		return new ContentValues();
	}

	@SuppressWarnings("deprecation")
	public String getIpLocalAddress(Context context) {
		WifiManager wim = (WifiManager) context
				.getSystemService(Context.WIFI_SERVICE);
		return Formatter
				.formatIpAddress(wim.getConnectionInfo().getIpAddress());
	}
	
	public Bitmap getSpecialCardBitmap(Context context) {
		return decodeSampledBitmap(context, R.drawable.card_special);
	}

	public Bitmap getBitmap(Context context, StandardCard card) {
		Bitmap bmp = null;

		Suit suit = card.getSuit();
		Rank rank = card.getRank();
		
		if (rank.equals(Rank.Special)) {
			bmp = decodeSampledBitmap(context, R.drawable.card_special);
		} else if (suit.equals(Suit.Club)) {
			if (rank.equals(Rank.Ace)) {
				bmp = decodeSampledBitmap(context, R.drawable.card_a_club);
			} else if (rank.equals(Rank.Two)) {
				bmp = decodeSampledBitmap(context, R.drawable.card_2_club);
			} else if (rank.equals(Rank.Three)) {
				bmp = decodeSampledBitmap(context, R.drawable.card_3_club);
			} else if (rank.equals(Rank.Four)) {
				bmp = decodeSampledBitmap(context, R.drawable.card_4_club);
			} else if (rank.equals(Rank.Five)) {
				bmp = decodeSampledBitmap(context, R.drawable.card_5_club);
			} else if (rank.equals(Rank.Six)) {
				bmp = decodeSampledBitmap(context, R.drawable.card_6_club);
			} else if (rank.equals(Rank.Seven)) {
				bmp = decodeSampledBitmap(context, R.drawable.card_7_club);
			} else if (rank.equals(Rank.Eight)) {
				bmp = decodeSampledBitmap(context, R.drawable.card_8_club);
			} else if (rank.equals(Rank.Nine)) {
				bmp = decodeSampledBitmap(context, R.drawable.card_9_club);
			} else if (rank.equals(Rank.Ten)) {
				bmp = decodeSampledBitmap(context, R.drawable.card_10_club);
			} else if (rank.equals(Rank.Jack)) {
				bmp = decodeSampledBitmap(context, R.drawable.card_j_club);
			} else if (rank.equals(Rank.Queen)) {
				bmp = decodeSampledBitmap(context, R.drawable.card_q_club);
			} else if (rank.equals(Rank.King)) {
				bmp = decodeSampledBitmap(context, R.drawable.card_k_club);
			} else if (rank.equals(Rank.Joker)) {
				bmp = decodeSampledBitmap(context, R.drawable.card_jok_black);
			}
		} else if (suit.equals(Suit.Diamond)) {
			if (rank.equals(Rank.Ace)) {
				bmp = decodeSampledBitmap(context, R.drawable.card_a_diamond);
			} else if (rank.equals(Rank.Two)) {
				bmp = decodeSampledBitmap(context, R.drawable.card_2_diamond);
			} else if (rank.equals(Rank.Three)) {
				bmp = decodeSampledBitmap(context, R.drawable.card_3_diamond);
			} else if (rank.equals(Rank.Four)) {
				bmp = decodeSampledBitmap(context, R.drawable.card_4_diamond);
			} else if (rank.equals(Rank.Five)) {
				bmp = decodeSampledBitmap(context, R.drawable.card_5_diamond);
			} else if (rank.equals(Rank.Six)) {
				bmp = decodeSampledBitmap(context, R.drawable.card_6_diamond);
			} else if (rank.equals(Rank.Seven)) {
				bmp = decodeSampledBitmap(context, R.drawable.card_7_diamond);
			} else if (rank.equals(Rank.Eight)) {
				bmp = decodeSampledBitmap(context, R.drawable.card_8_diamond);
			} else if (rank.equals(Rank.Nine)) {
				bmp = decodeSampledBitmap(context, R.drawable.card_9_diamond);
			} else if (rank.equals(Rank.Ten)) {
				bmp = decodeSampledBitmap(context, R.drawable.card_10_diamond);
			} else if (rank.equals(Rank.Jack)) {
				bmp = decodeSampledBitmap(context, R.drawable.card_j_diamond);
			} else if (rank.equals(Rank.Queen)) {
				bmp = decodeSampledBitmap(context, R.drawable.card_q_diamond);
			} else if (rank.equals(Rank.King)) {
				bmp = decodeSampledBitmap(context, R.drawable.card_k_diamond);
			} else if (rank.equals(Rank.Joker)) {
				bmp = decodeSampledBitmap(context, R.drawable.card_jok_red);
			}
		} else if (suit.equals(Suit.Heart)) {
			if (rank.equals(Rank.Ace)) {
				bmp = decodeSampledBitmap(context, R.drawable.card_a_heart);
			} else if (rank.equals(Rank.Two)) {
				bmp = decodeSampledBitmap(context, R.drawable.card_2_heart);
			} else if (rank.equals(Rank.Three)) {
				bmp = decodeSampledBitmap(context, R.drawable.card_3_heart);
			} else if (rank.equals(Rank.Four)) {
				bmp = decodeSampledBitmap(context, R.drawable.card_4_heart);
			} else if (rank.equals(Rank.Five)) {
				bmp = decodeSampledBitmap(context, R.drawable.card_5_heart);
			} else if (rank.equals(Rank.Six)) {
				bmp = decodeSampledBitmap(context, R.drawable.card_6_heart);
			} else if (rank.equals(Rank.Seven)) {
				bmp = decodeSampledBitmap(context, R.drawable.card_7_heart);
			} else if (rank.equals(Rank.Eight)) {
				bmp = decodeSampledBitmap(context, R.drawable.card_8_heart);
			} else if (rank.equals(Rank.Nine)) {
				bmp = decodeSampledBitmap(context, R.drawable.card_9_heart);
			} else if (rank.equals(Rank.Ten)) {
				bmp = decodeSampledBitmap(context, R.drawable.card_10_heart);
			} else if (rank.equals(Rank.Jack)) {
				bmp = decodeSampledBitmap(context, R.drawable.card_j_heart);
			} else if (rank.equals(Rank.Queen)) {
				bmp = decodeSampledBitmap(context, R.drawable.card_q_heart);
			} else if (rank.equals(Rank.King)) {
				bmp = decodeSampledBitmap(context, R.drawable.card_k_heart);
			} else if (rank.equals(Rank.Joker)) {
				bmp = decodeSampledBitmap(context, R.drawable.card_jok_red);
			}
		} else if (suit.equals(Suit.Spade)) {
			if (rank.equals(Rank.Ace)) {
				bmp = decodeSampledBitmap(context, R.drawable.card_a_spade);
			} else if (rank.equals(Rank.Two)) {
				bmp = decodeSampledBitmap(context, R.drawable.card_2_spade);
			} else if (rank.equals(Rank.Three)) {
				bmp = decodeSampledBitmap(context, R.drawable.card_3_spade);
			} else if (rank.equals(Rank.Four)) {
				bmp = decodeSampledBitmap(context, R.drawable.card_4_spade);
			} else if (rank.equals(Rank.Five)) {
				bmp = decodeSampledBitmap(context, R.drawable.card_5_spade);
			} else if (rank.equals(Rank.Six)) {
				bmp = decodeSampledBitmap(context, R.drawable.card_6_spade);
			} else if (rank.equals(Rank.Seven)) {
				bmp = decodeSampledBitmap(context, R.drawable.card_7_spade);
			} else if (rank.equals(Rank.Eight)) {
				bmp = decodeSampledBitmap(context, R.drawable.card_8_spade);
			} else if (rank.equals(Rank.Nine)) {
				bmp = decodeSampledBitmap(context, R.drawable.card_9_spade);
			} else if (rank.equals(Rank.Ten)) {
				bmp = decodeSampledBitmap(context, R.drawable.card_10_spade);
			} else if (rank.equals(Rank.Jack)) {
				bmp = decodeSampledBitmap(context, R.drawable.card_j_spade);
			} else if (rank.equals(Rank.Queen)) {
				bmp = decodeSampledBitmap(context, R.drawable.card_q_spade);
			} else if (rank.equals(Rank.King)) {
				bmp = decodeSampledBitmap(context, R.drawable.card_k_spade);
			} else if (rank.equals(Rank.Joker)) {
				bmp = decodeSampledBitmap(context, R.drawable.card_jok_black);
			}
		}

		return bmp;
	}

	private static Bitmap decodeSampledBitmap(Context context, int id) {

		// First decode with inJustDecodeBounds=true to check dimensions

		final BitmapFactory.Options options = new BitmapFactory.Options();

		/*
		 * options.inJustDecodeBounds = true;
		 * BitmapFactory.decodeByteArray(bytes, 0, bytes.length, options);
		 */

		// Decode bitmap with inSampleSize set
		options.inJustDecodeBounds = false;
		options.inPurgeable = true;
		options.inDither = false;
		options.inScaled = false;
		options.inSampleSize = 1;

		Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(),
				id, options);

		return bitmap;
	}

	public AlertDialog createAlertDialog(Context context,
			String title, 
			String message,
			DialogInterface.OnClickListener positiveButton,
			DialogInterface.OnClickListener negativeButton) {
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setTitle(title)
				.setMessage(message);
		
		if (positiveButton != null) {
			builder.setPositiveButton(context.getString(R.string.bt_ok), positiveButton);
		}
		
		if (negativeButton != null) {
			builder.setPositiveButton(context.getString(R.string.bt_cancel), negativeButton);
		}
		
		return builder.create();
	}
}
