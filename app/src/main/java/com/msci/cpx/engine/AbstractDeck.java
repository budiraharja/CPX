package com.msci.cpx.engine;

//Will need this for the Random Number Generation
import java.util.Random;

/**
 * 
 * @author Ethan Wilder Abstract Deck is the superclass for all decks. This
 *         contains all the necessary deck methods and exception handling for
 *         decks but does not include the initialization of the deck itself.
 *         Card[] deck is the array of cards known as the deck. It is to remain
 *         uninitialized until the implementations of the abstractDeck
 */
abstract class AbstractDeck implements Deck {
	protected StandardCard[] deck;

	private int count = 0;

	/**
	 * The Deal method deals a card from the top of a shuffled deck with
	 * repeating cards. This means the card is chosen randomly and ensured that
	 * it will not be repeated.
	 * 
	 * @throws EmptyDeckException
	 *             when it tries to deal from an empty deck.
	 * @return the Card from the top of the deck.
	 */
	public StandardCard deal() throws EmptyDeckException {
		if (isEmpty()) {
			throw new EmptyDeckException("Empty Deck");
		}
		Random rnd = new Random(System.nanoTime());
		int number = rnd.nextInt(deck.length - count);

		/*
		 * Swap function
		 */
		StandardCard temp = deck[deck.length - 1 - count];
		deck[deck.length - 1 - count] = deck[number];
		deck[number] = temp;

		return deck[deck.length - 1 - count++];
	}

	/**
	 * Shuffle is a method to shuffle the deck.
	 */
	public void shuffle() {
		count = 0;
	}

	/**
	 * isEmpty tests to see if the deck is empty or not
	 * 
	 * @return true if and only if the deck has no more cards to deal.
	 */
	public boolean isEmpty() {
		return count == deck.length;
	}

	/**
	 * getHand is a method to generate a quick hand from the deck.
	 * 
	 * @param size
	 *            is the number of cards to deal into the hand
	 * @return A hand of specified size.
	 * @throws EmptyDeckException
	 *             when there are no cards left to be dealt or not enough cards
	 *             to deal to the hand size.
	 * @throws IllegalArgumentException
	 *             when the input is an invalid number, ie. negative number
	 */
	public Hand getHand(int size, int cardCount) throws EmptyDeckException,
			IllegalArgumentException {
		if (isEmpty() || (size > deck.length - count)) {
			throw new EmptyDeckException(
					"Trying to deal card from Empty Deck: Method getHand(int size)");
		}
		if (size < 1) {
			throw new IllegalArgumentException();
		}
		StandardCard[] cardarr = new StandardCard[size];
		for (int i = 0; i < size; i++) {
			if (isEmpty()) {
				throw new EmptyDeckException(
						"Ran out of cards while dealing: Method getHand(int size)");
			}
			cardarr[i] = deal();
		}
		return new HandImplementation(cardarr);
	}

	/**
	 * getHand is a method to generate a quick hand from the deck.
	 * 
	 * @param size
	 *            is the number of cards to deal into the hand
	 * @param number
	 *            is the number of hands to create each of specific size
	 * @return A hand of specified size.
	 * @throws EmptyDeckException
	 *             when there are no cards left to be dealt or not enough cards
	 *             to deal to the hand size.
	 * @throws IllegalArgumentException
	 *             when the input is an invalid number, ie. negative number
	 */
	public Hand[] getHands(int number, int size, int cardCount) throws EmptyDeckException,
			IllegalArgumentException {
		if (isEmpty() || (size * number > deck.length - count)) {
			throw new EmptyDeckException(
					"Trying to deal from empty deck: Method getHands(int size int number)");
		}
		if (size * number < 1) {
			throw new IllegalArgumentException();
		}
		HandImplementation[] hands = new HandImplementation[number];

		for (int i = 0; i < number; i++) {
			if (isEmpty()) {
				throw new EmptyDeckException(
						"Should never get here: Method getHands(int size int number)");
			}
			hands[i] = (HandImplementation) getHand(size, cardCount);
		}

		return hands;
	}

	/**
	 * @return a string representation of all the cards in the deck separated by
	 *         a new line each. If the deck is empty the string "Empty" is
	 *         returned.
	 */
	public String toString() {
		String cards = "";
		if (isEmpty()) {
			return "Empty";
		}
		for (int i = 0; i < deck.length; i++) {
			cards += deck[i].toString() + "\n";
		}
		return cards;
	}

}