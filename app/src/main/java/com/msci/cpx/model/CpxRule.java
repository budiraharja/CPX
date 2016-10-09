package com.msci.cpx.model;

import java.io.Serializable;

public class CpxRule implements Rule, Serializable {
	private static final long serialVersionUID = -4280520993239810609L;
	private int deckCount = 1;
	private boolean isSpecialCard = false;
	private boolean isCheat = false;
	private boolean isTimer = false;
	
	public int getDeckCount() {
		return deckCount;
	}
	public void setDeckCount(int deckCount) {
		this.deckCount = deckCount;
	}
	public boolean isSpecialCard() {
		return isSpecialCard;
	}
	public void setSpecialCard(boolean isSpecialCard) {
		this.isSpecialCard = isSpecialCard;
	}
	public boolean isCheat() {
		return isCheat;
	}
	public void setCheat(boolean isCheat) {
		this.isCheat = isCheat;
	}
	public boolean isTimer() {
		return isTimer;
	}
	public void setTimer(boolean isTimer) {
		this.isTimer = isTimer;
	}
}
