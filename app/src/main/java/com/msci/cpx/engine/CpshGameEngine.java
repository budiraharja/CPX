package com.msci.cpx.engine;

import com.msci.cpx.engine.StandardCard.Rank;
import com.msci.cpx.model.Rule;
import com.msci.cpx.utility.Constants;

public class CpshGameEngine {
	public static final Rank[] EXCLUDE_CARD = {StandardCard.Rank.Joker, StandardCard.Rank.Special};
	public static final int CARD_COUNT = 52;
	
    private int playStatus = Constants.PlayStatus.STATUS_OK;
	private Rule rule;
	private StandardDeckEngine deck;
	private int score = 0;
	
	public CpshGameEngine(Rule rule) {
		this.rule = rule;
		this.deck = new StandardDeckEngine(1, false, EXCLUDE_CARD);
	}
	
	public StandardCard[] deal() throws IllegalArgumentException, EmptyDeckException {
		return deck.getHand((CARD_COUNT / 4), CARD_COUNT).cards();
	}
	
	public int value(StandardCard[] cards) {
		int value = 0;
		
		switch (cards.length) {
		case 1:
			
			break;
		}
		
		return value;
	}
	
	public int value(StandardCard card) {
		int value = 0;
		
		if (card.getRank().equals(StandardCard.Rank.Three)) {
			value = value(card, (4 * 0));
		} else if (card.getRank().equals(StandardCard.Rank.Four)) {
			value = value(card, (4 * 1));
		} else if (card.getRank().equals(StandardCard.Rank.Five)) {
			value = value(card, (4 * 2));
		} else if (card.getRank().equals(StandardCard.Rank.Six)) {
			value = value(card, (4 * 3));
		} else if (card.getRank().equals(StandardCard.Rank.Seven)) {
			value = value(card, (4 * 4));
		} else if (card.getRank().equals(StandardCard.Rank.Eight)) {
			value = value(card, (4 * 5));
		} else if (card.getRank().equals(StandardCard.Rank.Nine)) {
			value = value(card, (4 * 6));
		} else if (card.getRank().equals(StandardCard.Rank.Ten)) {
			value = value(card, (4 * 7));
		} else if (card.getRank().equals(StandardCard.Rank.Jack)) {
			value = value(card, (4 * 8));
		} else if (card.getRank().equals(StandardCard.Rank.Queen)) {
			value = value(card, (4 * 9));
		} else if (card.getRank().equals(StandardCard.Rank.King)) {
			value = value(card, (4 * 10));
		} else if (card.getRank().equals(StandardCard.Rank.Ace)) {
			value = value(card, (4 * 11));
		} else if (card.getRank().equals(StandardCard.Rank.Two)) {
			value = value(card, (4 * 12));
		}
		
		return value;
	}
	
	private int value(StandardCard card, int startValue) {
		if (card.getSuit().equals(StandardCard.Suit.Diamond)) {
			startValue += 1;
		} else if (card.getSuit().equals(StandardCard.Suit.Club)) {
			startValue += 2;
		} else if (card.getSuit().equals(StandardCard.Suit.Heart)) {
			startValue += 3;
		} else if (card.getSuit().equals(StandardCard.Suit.Spade)) {
			startValue += 4;
		}
		
		return startValue;
	}
}
