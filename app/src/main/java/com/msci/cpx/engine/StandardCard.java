package com.msci.cpx.engine;

import java.io.Serializable;

/**
 * @author Ethan Wilder. StandardCard is one implementation of Abstract Card. It
 *         uses 2 enumerated inner classes to define 2 values that makes a card
 *         a card.
 */
public class StandardCard extends AbstractCard implements Serializable {
	private static final long serialVersionUID = -8025085605165061581L;
	public static final int CARD_COUNT = 56;

	/**
	 * 
	 * @author Ethan Wilder. Suit is an enumeration of the suit value for
	 *         Standard Card Values: Spade, Club, Heart, and Diamond in that
	 *         order
	 * 
	 */
	public enum Suit {
		/**
		 * Spade
		 */
		Spade,
		/**
		 * Club
		 */
		Club,
		/**
		 * Heart
		 */
		Heart,
		/**
		 * Diamond
		 */
		Diamond
	}

	/**
	 * 
	 * @author Ethan Wilder. Rank is an enumeration for the rank value for
	 *         Standard Card Values: Ace, Two, Three, Four, Five, Six, Seven,
	 *         Eight, Nine, Ten, Jack, Queen, King, Jack in that order.
	 * 
	 */
	public enum Rank {
		/**
		 * Ace
		 */
		Ace,
		/**
		 * Two
		 */
		Two,
		/**
		 * Three
		 */
		Three,
		/**
		 * Four
		 */
		Four,
		/**
		 * Five
		 */
		Five,
		/**
		 * Six
		 */
		Six,
		/**
		 * Seven
		 */
		Seven,
		/**
		 * Eight
		 */
		Eight,
		/**
		 * Nine
		 */
		Nine,
		/**
		 * Ten
		 */
		Ten,
		/**
		 * Jack
		 */
		Jack,
		/**
		 * Queen
		 */
		Queen,
		/**
		 * King
		 */
		King,
		/**
		 * Joker
		 */
		Joker,
		/**
		 * Special
		 */
		Special
	}

	private Suit suit;

	private Rank rank;

	/**
	 * Standard Card includes a specific constructor for creating Standard
	 * Cards. It takes 2 parameters
	 * 
	 * @param s
	 *            is the suit parameter of enumerated type Suit
	 * @param r
	 *            is the rank parameter of enumerated type Rank
	 */
	public StandardCard(Suit s, Rank r) {
		suit = s;
		rank = r;
	}

	/**
	 * StandardCard extends abstract card which implements the compareTo method.
	 * So this has a compareTo method. This method checks to make sure the
	 * object its comparing against is of the right type, then compares the
	 * StandardCard first by suit and if the suits are found equal than it
	 * compares by rank.
	 * 
	 * @param other
	 *            is the other object it's being compared against.
	 * @return an integer -1, 0, or 1 if this object is less than, equal to, or
	 *         greater than specified object.
	 */
	public int compareTo(Object other) {
		if (!other.getClass().equals(StandardCard.class)) {
			throw new ClassCastException();
		}
		StandardCard oCard = (StandardCard) other;
		if (!oCard.suit.equals(suit)) {
			return suit.compareTo(oCard.suit);
		} else {
			return rank.compareTo(oCard.rank);
		}
	}

	/**
	 * @return a string representation of the card based on the enumerated types
	 *         rank and suit's toString methods.
	 */
	public String toString() {
		String stdCard = rank.toString() + " of " + suit.toString() + "s";
		
		if (rank == Rank.Joker) {
			stdCard = "The Joker";
		} else if (rank == Rank.Special) {
			stdCard = "Special Card";
		}
		
		return stdCard;
	}

	/**
	 * @return the rank value.
	 */
	public Rank getRank() {
		return rank;
	}

	/**
	 * @return the suit value
	 */
	public Suit getSuit() {
		return suit;
	}
}
