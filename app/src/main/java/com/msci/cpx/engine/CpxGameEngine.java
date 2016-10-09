package com.msci.cpx.engine;

import com.msci.cpx.model.CpxRule;
import com.msci.cpx.model.Rule;
import com.msci.cpx.utility.Constants;

public class CpxGameEngine {
	public static final int JOKER_VALUE = -999;
	public static final int INIT_GAME_SCORE = 99;
	
	private StandardDeckEngine deck;
	private int score = 99;
	public int deckCount = 1;
	private int playStatus = Constants.PlayStatus.STATUS_OK;
	private int dropStatus = Constants.PlayStatus.STATUS_OK;
	private Rule rule;
	private boolean isSpecial;
	
	public CpxGameEngine(int n, Rule rule) {
		isSpecial = ((CpxRule)rule).isSpecialCard();
		this.rule = rule;
		this.deck = new StandardDeckEngine(n, isSpecial);
	}
	
	public StandardCard[] deal() throws IllegalArgumentException, EmptyDeckException {
		int cardCount = StandardCard.CARD_COUNT;
		if (isSpecial) {
			cardCount += 4;
		}
		
		return deck.getHand(4, cardCount).cards();
	}
	
	public StandardCard draw() throws EmptyDeckException {
		return deck.deal();
	}
	
	public int play(StandardCard card, boolean specialAction) {
		if (card.getRank().equals(StandardCard.Rank.Ten)) {
			if (!specialAction) {
				addScore(value(card));
			} else {
				addScore(-value(card));
			}
		} else if (card.getRank().equals(StandardCard.Rank.King)) {
			setScore(value(card));
		} else if (card.getRank().equals(StandardCard.Rank.Joker)) {
			return playJoker(card);
		} else {
			addScore(value(card));
		}
		
		if (getScore() > 100) {
			subScore(value(card));
			setPlayStatus(Constants.PlayStatus.STATUS_ERROR);
		} else {
			setPlayStatus(Constants.PlayStatus.STATUS_OK);
		}
		
		return getScore();
	}
	
	public int dropCard(StandardCard card) {
		int dropScore = 0;
		
		if (card.getRank().equals(StandardCard.Rank.Ten)
				|| card.getRank().equals(StandardCard.Rank.Jack)
				|| card.getRank().equals(StandardCard.Rank.Queen)
				|| card.getRank().equals(StandardCard.Rank.King)
				|| card.getRank().equals(StandardCard.Rank.Joker)
				) {
			setDropStatus(Constants.PlayStatus.STATUS_ERROR);
		} else {
			dropScore = value(card);
		}
		
		return dropScore;
	}
	
	public int playJoker(StandardCard card) {
		return value(card);
	}
	
	public int value(StandardCard card) {
		int value = 0;
		
		if (card.getRank().equals(StandardCard.Rank.Ace)) {
			value = 1;
		} else if (card.getRank().equals(StandardCard.Rank.Two)) {
			value = 2;
		} else if (card.getRank().equals(StandardCard.Rank.Three)) {
			value = 3;
		} else if (card.getRank().equals(StandardCard.Rank.Four)) {
			value = 4;
		} else if (card.getRank().equals(StandardCard.Rank.Five)) {
			value = 5;
		} else if (card.getRank().equals(StandardCard.Rank.Six)) {
			value = 6;
		} else if (card.getRank().equals(StandardCard.Rank.Seven)) {
			value = 7;
		} else if (card.getRank().equals(StandardCard.Rank.Eight)) {
			value = 8;
		} else if (card.getRank().equals(StandardCard.Rank.Nine)) {
			value = 9;
		} else if (card.getRank().equals(StandardCard.Rank.Ten)) {
			value = 10;
		} else if (card.getRank().equals(StandardCard.Rank.Jack)) {
			value = 0;
		} else if (card.getRank().equals(StandardCard.Rank.Queen)) {
			value = -20;
		} else if (card.getRank().equals(StandardCard.Rank.King)) {
			value = 99;
		} else if (card.getRank().equals(StandardCard.Rank.Joker)) {
			value = JOKER_VALUE;
			setPlayStatus(Constants.PlayStatus.STATUS_OK);
		}
		
		return value;
	}

	public StandardDeckEngine getDeck() {
		return deck;
	}

	public void setDeck(StandardDeckEngine deckEngine) {
		this.deck = deckEngine;
	}

	public int getPlayStatus() {
		return playStatus;
	}

	public void setPlayStatus(int playStatus) {
		this.playStatus = playStatus;
	}

	public int getDropStatus() {
		return dropStatus;
	}

	public void setDropStatus(int dropStatus) {
		this.dropStatus = dropStatus;
	}

	public int getScore() {
		return score;
	}
	
	private void setScore(int score) {
		this.score = score;
	}

	private void addScore(int score) {
		this.score += score;
	}
	
	private void subScore(int score) {
		this.score -= score;
	}
}
