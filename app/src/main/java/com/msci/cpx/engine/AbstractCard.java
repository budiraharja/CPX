package com.msci.cpx.engine;

/**
 * @author Ethan Wilder
 * 
 * This is the foundation of all Card objects. There's really
 * no implementation here.
 */
abstract class AbstractCard implements Card {
	/**
	 * Necessary method for the Comparable Interface
	 * This will be needed for nearly every Card operation
	 * 
	 * @param other is the other object to be compared to the 
	 *        current object.
	 * @return an integer that can be determined as less than,
	 *         equal to, or greater than the other object.
	 */
	public abstract int compareTo(Object other);

	/**
	 * This will give a readible representation of the Card.
	 */
	public abstract String toString();
}