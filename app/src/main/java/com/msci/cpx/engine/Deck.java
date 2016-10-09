package com.msci.cpx.engine;

/**
 * @author Ethan Wilder This is an interface for all decks, These conditions of
 *         the deck must be adheared to for it to be considered a deck.
 */
public interface Deck {
	/**
	 * Deals a shuffled Card from the deck in question
	 * 
	 * @return a Card randomly chosen from the deck without duplication untill
	 *         next shuffle()
	 * @throws EmptyDeckException
	 *             if there are no cards left in the deck.
	 */
	public Card deal() throws EmptyDeckException;

	/**
	 * Reshuffles the deck returning all Cards to the deck. The next call to
	 * Deal() will deal from a full deck.
	 * 
	 * @see Deck#deal()
	 */
	public void shuffle();

	/**
	 * @return true if and only if the deck is empty.
	 * @see Deck#deal()
	 */
	public boolean isEmpty();

	/**
	 * This is used to deal out a specific number of cards also known as a hand.
	 * 
	 * @param size
	 *            is the number of cards in a hand
	 * @return a specific number of cards chosen randomly
	 * @throws EmptyDeckException
	 *             if the deck is empty or becomes empty. In other words the
	 *             deck doesn't have enough cards left to make the hand.
	 * @throws IllegalArgumentException
	 *             if the number of cards requested in the hand is more than the
	 *             deck of cards or some other invalid number.
	 */
	public Hand getHand(int size, int cardCount) throws EmptyDeckException,
			IllegalArgumentException;

	/**
	 * Deals out a specific number of hands of a certain card size
	 * 
	 * @param number
	 *            is the number of hands to be dealt
	 * @param size
	 *            is the size of each hand in cards
	 * @return an array of set number of hands a set size
	 * @throws EmptyDeckException
	 *             if the deck doesn't have enough cards to perform the deal
	 * @throws IllegalArgumentException
	 *             if the number of hands or size of cards or any combination
	 *             thereof is invalid in any way.
	 */
	public Hand[] getHands(int number, int size, int cardCount) throws EmptyDeckException,
			IllegalArgumentException;
}
