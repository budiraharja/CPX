package com.msci.cpx.network;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.StreamCorruptedException;
import java.nio.charset.Charset;

import com.msci.cpx.engine.StandardCard;
import com.msci.cpx.engine.StandardDeckEngine;

/**
 * ShareNoteNetCommand is an object to be used in sending command over network
 * @author MSCI: a.hendrawan / faren.f
 * 
 */
public class Command {
	// server reply when client try to connect
	public final static byte COMMAND_PING = 5; // the text contains server name
	
	// server tell client to reset displayed text with new text 
	public final static byte COMMAND_RESET_NOTE = 0; // swith note will be using this command 
	
	// server tell client to insert text from indexStart
	public final static byte COMMAND_INSERT = 1;
	
	// server tell client to delete text from indexStart to indexEnd
	public final static byte COMMAND_DELETE = 2;
	
	// server tell client to replace text from indexStart to indexEnd
	public final static byte COMMAND_REPLACE = 3;

	// server tell client to update participant list
	public final static byte COMMAND_PARTICIPANT_LIST = 6; // participant list is put in text, with each of client is separated by ','
	// Command for client tell server to join
	public final static byte COMMAND_JOIN = 7;
	
	// Command for enabling connect button status
	public final static byte COMMAND_ENABLE_CONNECT = 8;
	
	public final static byte COMMAND_CLIENT_INSERT = 9;
	public final static byte COMMAND_CLIENT_DELETE = 10;
	public final static byte COMMAND_CLIENT_REPLACE = 11;
	

	public final static byte COMMAND_KEEP_ALIVE = 12;

	public final static byte COMMAND_MAX_CLIENT_REACHED = 13;
	
	public final static byte COMMAND_INSERT_DECK = 14;
	
	public final static byte COMMAND_REQUEST_DECK_INIT = 15;
	
	public final static byte COMMAND_REQUEST_DRAW = 16;
	
	public final static byte COMMAND_SET_DRAW = 17;
	
	public final static byte COMMAND_REQUEST_PLAY = 18;
	
	public final static byte COMMAND_SET_PLAY = 19;
	
	public final static byte COMMAND_REQUEST_SCORE = 20;
	
	public final static byte COMMAND_SET_SCORE = 21;
	
	public final static byte COMMAND_REQUEST_DROP_CARD = 22;
	
	public final static byte COMMAND_SET_DROP_CARD = 23;
	
	public final static byte COMMAND_REQUEST_NEXT_TURN = 24;
	
	public final static byte COMMAND_SET_NEXT_TURN = 25;
	
	public final static byte COMMAND_REQUEST_FINISH_GAME = 26;
	
	public final static byte COMMAND_SET_FINISH_GAME = 27;
	
	public final static byte COMMAND_REQUEST_CARD_OTHER = 28;
	
	public final static byte COMMAND_SET_REQUEST_CARD_OTHER = 29;
	
	public final static byte COMMAND_REQUEST_CARD_OTHER_BACK = 30;
	
	public final static byte COMMAND_SET_REQUEST_CARD_OTHER_BACK = 31;
	
	public final static byte COMMAND_REQUEST_EXCHANGE_CARD = 32;
	
	public final static byte COMMAND_SET_EXCHANGE_CARD = 33;
	
	public final static byte COMMAND_REQUEST_LOOSER = 34;
	
	public final static byte COMMAND_SET_LOOSER = 35;
	
	public final static byte COMMAND_REQUEST_GAME_STATUS = 35;
	
	public final static byte COMMAND_SET_GAME_STATUS = 36;

	//public final static int COMMAND_CURSOR = 4;
	
	// command type, the value is one of the above constant
	private byte command;
	// indexStart (used in some command e.g insert, delete) 
	private int indexStart;
	// indexEnd (used in some command e.g delete,replace)
	private int indexEnd;
	// length of text payload
	private int length;
	// text payload (used in some command)
	private String text;
	
	private Object obj;
	private Object obj2;
	private int arg;
	private int arg2;
	private int arg3;
	private boolean bool;
	
	private int lengthObj = 0;
	private int lengthObj2 = 0;
	
	private StandardDeckEngine deckEngine;
	private StandardCard card;
	private int lengthCard;
	private StandardCard[] cards;
	private StandardCard[] cards2;
	private int lengthCards;
	private int lengthCards2;
	
	private String ip;
	private int lengthIp;
	
	/**
	 * Get command protocol
	 * @return
	 */
	public byte getCommand() {
		return command;
	}
	
	/**
	 * set command protocol
	 * @param command
	 */
	public void setCommand(byte command) {
		this.command = command;
	}
	
	/**
	 * get index start text to be action: insert, delete and replace
	 * @return
	 */
	public int getIndexStart() {
		return indexStart;
	}

	/**
	 * set index start text to be action: insert, delete and replace
	 * @param indexStart
	 */
	public void setIndexStart(int indexStart) {
		this.indexStart = indexStart;
	}
	
	/**
	 * get index end text to be action: delete and replace
	 * @return 
	 */
	public int getIndexEnd() {
		return indexEnd;
	}
	
	/**
	 * set index end text to be action: delete and replace
	 * @param indexEnd
	 */
	public void setIndexEnd(int indexEnd) {
		this.indexEnd = indexEnd;
	}
	
	/**
	 * get text to be action: init, insert, replace
	 * @return
	 */
	public String getText() {
		return text;
	}
	
	/**
	 * set text to be action: init, insert, replace
	 * @param text
	 */
	public void setText(String text) {
		this.text = text;
		//length of text payload
		length = text.getBytes(Charset.defaultCharset()).length;
	}
	
	public byte[] getTextBytes() {
		return text.getBytes(Charset.defaultCharset());
	}
	
	public void setTextBytes(byte[] bytes) {
		text = new String(bytes, Charset.defaultCharset());
	}
	
	/**
	 * get Length of text
	 * @return
	 */
	public int getLength() {
		return length;
	}
	
	public void setLength(int length) {
		this.length = length;
	}

	public StandardDeckEngine getDeckEngine() {
		return deckEngine;
	}

	public void setDeckEngine(StandardDeckEngine deckEngine) {
		this.deckEngine = deckEngine;
	}

	public Object getObj() {
		return obj;
	}

	public void setObj(Object obj) {
		this.obj = obj;
		
		this.lengthObj = getObjectBytes(obj).length;
	}
	
	public Object getObj2() {
		return obj2;
	}

	public void setObj2(Object obj2) {
		this.obj2 = obj2;
		
		this.lengthObj2 = getObjectBytes(obj2).length;
	}

	public int getArg() {
		return arg;
	}

	public void setArg(int arg) {
		this.arg = arg;
	}

	public int getArg2() {
		return arg2;
	}

	public void setArg2(int arg2) {
		this.arg2 = arg2;
	}

	public int getArg3() {
		return arg3;
	}

	public void setArg3(int arg3) {
		this.arg3 = arg3;
	}

	public boolean isBool() {
		return bool;
	}

	public void setBool(boolean bool) {
		this.bool = bool;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
		this.lengthIp = ip.getBytes(Charset.defaultCharset()).length;
	}
	
	public byte[] getIpBytes() {
		return ip.getBytes(Charset.defaultCharset());
	}
	
	public void setIpBytes(byte[] bytes) {
		ip = new String(bytes, Charset.defaultCharset());
	}

	public int getLengthIp() {
		return lengthIp;
	}

	public void setLengthIp(int lengthIp) {
		this.lengthIp = lengthIp;
	}
	
	public int getLengthObj() {
		return lengthObj;
	}

	public void setLengthObj(int lengthObj) {
		this.lengthObj = lengthObj;
	}
	
	public int getLengthObj2() {
		return lengthObj2;
	}

	public void setLengthObj2(int lengthObj2) {
		this.lengthObj2 = lengthObj2;
	}

	public byte[] getObjectBytes(Object obj) {
		if (obj != null) {
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			ObjectOutput out = null;
			try {
			  out = new ObjectOutputStream(bos);   
			  out.writeObject(obj);
			  return bos.toByteArray();
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
			  try {
					out.close();
					bos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		
		return new byte[0];
	}
	
	public Object getObjectFromBytes(byte[] bytes) {
		if (bytes != null && bytes.length > 0) {
			ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
			ObjectInput in = null;
			try {
			  in = new ObjectInputStream(bis);
			  return in.readObject(); 
			} catch (StreamCorruptedException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			} finally {
			  try {
					bis.close();
					in.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		
		return null;
	}
	
	
	public int getLengthCard() {
		return lengthCard;
	}

	public void setLengthCard(int lengthCard) {
		this.lengthCard = lengthCard;
	}

	public StandardCard getCard() {
		return card;
	}

	public void setCard(StandardCard card) {
		this.card = card;
		this.lengthCard = getCardBytes().length;
	}

	public byte[] getCardBytes() {
		if (this.card != null) {
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			ObjectOutput out = null;
			try {
			  out = new ObjectOutputStream(bos);   
			  out.writeObject(this.card);
			  return bos.toByteArray();
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
			  try {
					out.close();
					bos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		
		return new byte[0];
	}
	
	public StandardCard getCardFromBytes(byte[] bytes) {
		if (bytes != null && bytes.length > 0) {
			ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
			ObjectInput in = null;
			try {
			  in = new ObjectInputStream(bis);
			  return (StandardCard)in.readObject(); 
			} catch (StreamCorruptedException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			} finally {
			  try {
					bis.close();
					in.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		
		return null;
	}
	
	
	public int getLengthCards() {
		return lengthCards;
	}

	public void setLengthCards(int lengthCards) {
		this.lengthCards = lengthCards;
	}

	public StandardCard[] getCards() {
		return cards;
	}

	public void setCards(StandardCard[] cards) {
		this.cards = cards;
		this.lengthCards = getCardsBytes().length;
	}

	public byte[] getCardsBytes() {
		if (this.cards != null) {
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			ObjectOutput out = null;
			try {
			  out = new ObjectOutputStream(bos);   
			  out.writeObject(this.cards);
			  return bos.toByteArray();
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
			  try {
					out.close();
					bos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		
		return new byte[0];
	}
	
	public StandardCard[] getCardsFromBytes(byte[] bytes) {
		if (bytes != null && bytes.length > 0) {
			ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
			ObjectInput in = null;
			try {
			  in = new ObjectInputStream(bis);
			  return (StandardCard[])in.readObject(); 
			} catch (StreamCorruptedException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			} finally {
			  try {
					bis.close();
					in.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		
		return null;
	}
	
	public int getLengthCards2() {
		return lengthCards2;
	}

	public void setLengthCards2(int lengthCards2) {
		this.lengthCards2 = lengthCards2;
	}

	public StandardCard[] getCards2() {
		return cards2;
	}

	public void setCards2(StandardCard[] cards2) {
		this.cards2 = cards2;
		this.lengthCards2 = getCards2Bytes().length;
	}

	public byte[] getCards2Bytes() {
		if (this.cards2 != null) {
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			ObjectOutput out = null;
			try {
			  out = new ObjectOutputStream(bos);   
			  out.writeObject(this.cards2);
			  return bos.toByteArray();
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
			  try {
					out.close();
					bos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		
		return new byte[0];
	}
	
	public StandardCard[] getCards2FromBytes(byte[] bytes) {
		if (bytes != null && bytes.length > 0) {
			ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
			ObjectInput in = null;
			try {
			  in = new ObjectInputStream(bis);
			  return (StandardCard[])in.readObject(); 
			} catch (StreamCorruptedException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			} finally {
			  try {
					bis.close();
					in.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		
		return null;
	}
}
