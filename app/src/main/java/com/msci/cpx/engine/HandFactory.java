package com.msci.cpx.engine;

/**
 * @author Ethan Wilder
 * 
 *         Hand factory generates hands. It's literally a Hand Factory. It
 *         basically wraps HandImplementation.
 * 
 */
public class HandFactory {
	/**
	 * The Static method makeHand is used to make hands from a Card array
	 * 
	 * @param newHand
	 *            is the Card Array consisting of the cards to be included in a
	 *            hand.
	 * @return a new HandImplementation object of the Card Array
	 * @throws IllegalArgumentException
	 *             if the Card Array consists of any null values
	 */
	public static Hand makeHand(StandardCard[] newHand) throws IllegalArgumentException {
		for (int i = 0; i < newHand.length; i++) {
			if (newHand[i] == null) {
				throw new IllegalArgumentException();
			}
		}
		return new HandImplementation(newHand);
	}
}
