package com.msci.cpx.engine;

import com.msci.cpx.engine.StandardCard.Rank;

/**
 * @author Ethan Wilder. This is an implementation of AbstractDeck. Since
 *         AbstractDeck contains the meat of the deck methods, the only problem
 *         is initializing the Card Array, which is done here in the
 *         constructor.
 * 
 */
public class StandardDeck extends AbstractDeck {
	/**
	 * Standard Deck inherits from AbstractDeck and so all the deck
	 * functionality is brought in from AbstractDeck. However the initialization
	 * of the array is done in the StandardDeck Constructor. Using the values()
	 * method from the enum class I'm able to run some loops to initialize the
	 * deck and cards.
	 * 
	 */
	public StandardDeck(boolean isSpecial) {
		this(isSpecial, null);
	}
	
	public StandardDeck(boolean isSpecial, Rank[] excludeRank) {
		int cardCount = StandardCard.CARD_COUNT;
		
		if (excludeRank == null || excludeRank.length == 0) {
			if (isSpecial) {
				cardCount += 4;
			}
		} else {
			cardCount -= (excludeRank.length * 4);
		}
		
		deck = new StandardCard[cardCount];
		
		StandardCard.Suit[] Suits = StandardCard.Suit.values();
		StandardCard.Rank[] Ranks = StandardCard.Rank.values();

		int counter = -1;

		for (int i = 0; i < Suits.length; i++) {
			for (int j = 0; j < Ranks.length; j++) {
				boolean isValid = true;
				StandardCard card = new StandardCard(Suits[i], Ranks[j]);
				
				if (excludeRank == null || excludeRank.length == 0) {
					if (!isSpecial) {
						if (card.getRank().equals(Rank.Special)) {
							isValid = false;
						}
					}
				} else {
					for (Rank rank:excludeRank) {
						if (card.getRank().equals(rank)) {
							isValid = false;
						}
					}
				}
				
				if (isValid) {
					deck[++counter] = card;
				}
			}
		}
		shuffle();
	}
}
