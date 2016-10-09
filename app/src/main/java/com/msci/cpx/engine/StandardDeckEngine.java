package com.msci.cpx.engine;

import java.io.Serializable;
import java.util.Random; //generating random numbers

import com.msci.cpx.engine.StandardCard.Rank;

/**
 * 
 * @author Ethan Wilder. FourStandardDeck is a class that is used to deal from 4
 *         Standard Decks.
 * 
 *         I import the Cards package and implement Deck, but instead of a
 *         single deck I use 4 StandardDecks defined in DeckArray. The bulk of
 *         the work is done a method "draw()" since that's what the instructions
 *         said but having to implement the Deck interface I decided to make
 *         draw() private and use the public method deal() to call draw().
 * 
 */
public class StandardDeckEngine implements Deck, Serializable {
	private static final long serialVersionUID = -1057561571501937543L;
	private StandardDeck[] DeckArray;
	private Random rnd;
	private int deckCount = 1;
	
	public StandardDeck[] getDeckArray() {
		return DeckArray;
	}

	public int getDeckCount() {
		return deckCount;
	}

	/**
	 * FourStandardDeck includes a constructor that is used to intialize the
	 * DeckArray to (n) StandardDeck Objects.
	 * 
	 */
	public StandardDeckEngine(int n, boolean isSpecial) {
		this(n, isSpecial, null);
	}
	
	public StandardDeckEngine(int n, boolean isSpecial, Rank[] excludeRank) {
		this.deckCount = n;
		DeckArray = new StandardDeck[n];

		// initialization of the arrays here
		for (int i = 0; i < DeckArray.length; i++) {
			DeckArray[i] = new StandardDeck(isSpecial, excludeRank);
		}
	}

	/**
	 * Deal is basically a public wrapper for the draw method.
	 * 
	 * @throws EmptyDeckException
	 *             when there are no more cards in all of the (n) decks.
	 * @see StandardDeckEngine#draw()
	 */
	public StandardCard deal() throws EmptyDeckException {
		return draw();
	}

	/**
	 * draw() draws a StandardCard from each of the (n) decks randomly. I see
	 * the random number generator with System.nanotime() And I use a while loop
	 * and a counter varible to count how many decks I tried to deal from and
	 * got an EmptyDeckException from.
	 * 
	 * @return a StandardCard drawn randomly from 1 of the (n) decks
	 * @throws EmptyDeckException
	 *             if the while loop breaks with count at (n) then all of the
	 *             decks were empty and the EmptyDeckException is thrown.
	 */
	private StandardCard draw() throws EmptyDeckException {
		rnd = new Random(System.nanoTime());
		int deckChoice = rnd.nextInt(DeckArray.length);
		int count = 0;

		StandardCard retCard = null;

		while (count < getDeckCount()) {
			/*
			 * This is a "Just In Case" check to simplify the processing incase
			 * the loop screwed up or something weird happens. It ensures there
			 * wont be an infinite loop.
			 */
			if (isEmpty()) {
				throw new EmptyDeckException("All " + getDeckCount() + " Decks Are Empty");
			}
			try {
				retCard = (StandardCard) DeckArray[deckChoice].deal();
				break;
			} catch (EmptyDeckException e) {
				deckChoice++;
				count++;
			}
			/*
			 * if a deck is empty I catch EmptyDeckException and increment the
			 * deckChoice by 1 which may exceed the array. In that event it is
			 * only logical to go back to the beginning which is what this
			 * enforces.
			 */
			catch (ArrayIndexOutOfBoundsException e) {
				deckChoice = 0;
			}
		}
		if (count == getDeckCount()) {
			throw new EmptyDeckException("All " + getDeckCount() + " Decks Are Empty");
		}
		return retCard;
	}

	/**
	 * Shuffle uses each deck's shuffle method in the DeckArray.
	 */
	public void shuffle() {
		for (int i = 0; i < DeckArray.length; i++) {
			DeckArray[i].shuffle();
		}
	}

	/**
	 * isEmpty() uses each deck's isEmpty() method in the DeckArray
	 */
	public boolean isEmpty() {
		boolean empty = true;
		for (StandardDeck deck:DeckArray) {
			if (!deck.isEmpty()) {
				empty = false;
				break;
			}
		}
		return empty;
	}

	/**
	 * @param size
	 *            is the size of the hand to be made
	 * @return a Hand from HandFactory. The hand is picked using the draw()
	 *         method.
	 * @see StandardDeckEngine#draw()
	 * @throws EmptyDeckException
	 *             when the decks are empty
	 * @throws IllegalArgumentException
	 *             if the hand can not be generated with the parameter.
	 */
	public Hand getHand(int size, int cardCount) throws EmptyDeckException,
			IllegalArgumentException {
		/*
		 * Checks greater than (n * StandardCard.CARD_COUNT)
		 * max number of cards possible to be dealt.
		 */
		if (size > (getDeckCount() * cardCount) || size < 1) {
			throw new IllegalArgumentException();
		}

		StandardCard[] hand = new StandardCard[size];

		// draws a card the hand.
		for (int i = 0; i < size; i++) {
			hand[i] = draw();
		}

		// uses hand factory to return a single hand.
		return HandFactory.makeHand(hand);
	}

	/**
	 * I would generate my own hand array but I can't because I can't view the
	 * HandImplementation from this package. So I'm stuck using a single deck's
	 * implementation.
	 * 
	 * @param number
	 *            is the number of hands to generate
	 * @param size
	 *            is the size of each hand
	 * @returns a hand array based on an individual deck's hand.
	 * @throws EmptyDeckException
	 *             when the decks are empty
	 * @throws IllegalArgumentException
	 *             when it is not possible to make a deck using the parameters
	 */
	public Hand[] getHands(int number, int size, int cardCount) throws EmptyDeckException,
			IllegalArgumentException {
		// checks for valid parameter, makes sure that it isn't over
		// StandardCard.CARD_COUNT because I'm only using one deck here.
		if (size * number > cardCount || size * number < 1) {
			throw new IllegalArgumentException();
		}

		/*
		 * Picks a nonempty hand to use. It's the best I can do with what I have
		 * to work with.
		 */
		for (int i = 0; i < DeckArray.length; i++) {
			if (!DeckArray[i].isEmpty()) {
				return DeckArray[i].getHands(number, size, cardCount);
			}
		}
		/*
		 * If there were no decks with cards left then this exception is thrown.
		 */
		throw new EmptyDeckException("All " + getDeckCount() + " Decks are Empty");
	}
}
