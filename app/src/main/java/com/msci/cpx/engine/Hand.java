package com.msci.cpx.engine;

/**
 * @author Ethan Wilder This is an interface for all Hands. Hands are by
 *         definition an array of cards like a deck except these are generated
 *         by decks.
 * 
 */
public interface Hand {
	/**
	 * @return a sorted card array of the cards in the hand. Returns 0 if the
	 *         hand is empty.
	 */
	public StandardCard[] cards();

	/**
	 * Removes a card from the hand.
	 * 
	 * @param c
	 *            the card in the hand to be removed
	 * @return true if and only if the card was removed
	 * @throws ClassCastException
	 *             if the cardtype of the parameter isn't comparable to any
	 *             cards in the hand.
	 */
	public boolean play(StandardCard c) throws ClassCastException;

	/**
	 * Adds a card from the hand.
	 * 
	 * @param c
	 *            the card in the hand to be added
	 * @return true if and only if the card was added
	 * @throws ClassCastException
	 *             if the cardtype of the parameter isn't comparable to any
	 *             cards in the hand.
	 */
	public boolean add(StandardCard c) throws ClassCastException;

}
