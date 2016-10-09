package com.msci.cpx.engine;

import java.util.Arrays;

/**
 * 
 * @author Ethan Wilder
 * 
 *         Hand Implementation is a class that implements the hand interface
 *         with methods that create generic hands.
 * 
 *         It contains an uninitialized Card array much like abstract deck which
 *         contains the cards for the hands.
 * 
 */
class HandImplementation implements Hand {
	protected StandardCard[] hand;

	/**
	 * The constructor takes one parameter described below and creates a sorted
	 * hand out of it.
	 * 
	 * @param incoming
	 *            is a Card array parameter used to create the hand.
	 */
	protected HandImplementation(StandardCard[] incoming) {
		StandardCard[] newhand = new StandardCard[incoming.length];
		for (int i = 0; i < incoming.length; i++) {
			newhand[i] = incoming[i];
		}
		hand = newhand;
		Arrays.sort(hand);
	}

	/**
	 * @return a the card array representation of the Hand.
	 */
	public StandardCard[] cards() {
		return hand;
	}

	/**
	 * Play is a method that searches through the hand for the designated card
	 * by using the comparable method compareTo() and if the card is found then
	 * it is removed from the deck
	 * 
	 * @param c
	 *            is the Card that should be played.
	 * @return True if the card is found and removed but False if the card is
	 *         not found in the deck.
	 */
	public boolean play(StandardCard c) {
		if (hand.length == 0) {
			return false;
		}
		int i = 0;
		for (i = 0; i < hand.length; i++) {
			if (c.compareTo(hand[i]) == 0) {
				hand[i] = null;
				resizeHand();
				break;
			}
		}
		if (i == hand.length) {
			return false;
		}
		return true;
	}

	/**
	 * resizeHand is a private method used to resize the card array upon
	 * removing the card from the array. This just keeps things tidy and is
	 * inaccessable outside the class.
	 * 
	 * @see HandImplementation#play(Card c)
	 */
	private void resizeHand() {
		StandardCard[] newhand = new StandardCard[hand.length - 1];

		int j = 0;
		for (int i = 0; i < hand.length; i++) {
			if (hand[i] != null) {
				newhand[j] = hand[i];
				j++;
			}
		}
		hand = newhand;
		Arrays.sort(hand);
	}

	/**
	 * The add method takes a card as a parameter and adds it to the hand. No
	 * test for duplicates is implied necessary for this method.
	 * 
	 * @param c
	 *            is the card to add to the hand
	 * @return true if the card is added to the deck. Never returns false.
	 */
	public boolean add(StandardCard c) {
		if (hand.length > 0) {
			hand[0].compareTo(c);
		}

		StandardCard[] newhand = new StandardCard[hand.length + 1];
		for (int i = 0; i < hand.length; i++) {
			newhand[i] = hand[i];
		}
		newhand[hand.length] = c;

		hand = newhand;
		Arrays.sort(hand);
		return true;
	}

}