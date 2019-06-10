import java.io.Serializable;

/**
 * This class is a subclass of the CardList class, and is used to model a hand of cards
 * @author yuening
 *
 */
abstract class Hand extends CardList implements Serializable {
	private static final long serialVersionUID = 3173433777192763273L;
	private CardGamePlayer player;
	
	/**
	 * a constructor for building a hand with the specified player and list of cards
	 * @param player
	 * @param cards
	 */
	public Hand(CardGamePlayer player, CardList cards) {
		this.player = player;
		removeAllCards();
		if(!cards.isEmpty()) {
			for(int i=0; i<cards.size(); i++) {
				addCard(cards.getCard(i));
			}
		}
	}
	
	/**
	 * retrieve the player of this hand
	 * @return the player of this hand
	 */
	public CardGamePlayer getPlayer() {
		return player;
	}
	
	/**
	 * retrieve the top card of this hand
	 * @return the top card. If this hand contains no card, return null
	 */
	public Card getTopCard() {
		if(isValid()) {
			sort();
			return getCard(size()-1);
		}
		return null;
	}
	
	/**
	 * check if this hand beats a specified hand
	 * @param hand
	 * @return true if this hand beats a specified hand, and false, otherwise.
	 */
	public boolean beats(Hand hand) {
		if(isValid() && size()==hand.size()) {
			if(getType() == hand.getType()) {
				return getTopCard().compareTo(hand.getTopCard())==1?true:false;
			} else {
				return beatType(hand)?true:false;
			}
		}
		return false;
	}
	
	/**
	 * an abstract method
	 * check if this is a valid hand
	 * @return true when it is a valid hand, and false, otherwise.
	 */
	public abstract boolean isValid();

	
	/**
	 * an abstract method
	 * return a string specifying the type of this hand
	 * @return a string specifying the type of this hand
	 */
	public abstract String getType();
	
	/**
	 * when size is equal to 5, compare the type of the hand according to the
	 * priority: Straight Flush > Quad > Full House > Flush > Straight
	 * @param hand to be compared with
	 * @return true if of higher priority than the hand compared with, false if 
	 * 		could not compare or is not of higher priority
	 */
	public boolean beatType(Hand hand) {
		if(size()==5 && hand.size()==5) {
			if(getType() == "Straight Flush") {
				if(hand.getType() != "Straight Flush")
					return true;
			} else if (getType() == "Quad") {
				if(hand.getType() != "Straight Flush" && hand.getType() != "Quad")
					return true;
			} else if (getType() == "Full House") {
				if(hand.getType() != "Straight" || hand.getType() != "Flush")
					return true;
			} else if (getType() == "Flush") {
				if(hand.getType() != "Straight")
					return true;
			}
		}
		return false;
	}

}
