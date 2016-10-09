package com.msci.cpx.engine;

/**
 * @author Ethan Wilder This is a specific error to the cards package denoting
 *         when a deck is empty
 */
public class EmptyDeckException extends Exception {
	/**
	 * There is a private static constant required by the use of the superclass
	 * Exception. It's private so it won't matter. in the implementation
	 * process. The value is 0.
	 */
	private static final long serialVersionUID = 0;

	/**
	 * @param e
	 *            is a specialized output for the exception thrown It creates an
	 *            output easily noticable and readible for the user including
	 *            line number and an English translation of what's going on.
	 */
	public EmptyDeckException(String e) {
		System.err.println("\n" + e + "\n");
	}
}
