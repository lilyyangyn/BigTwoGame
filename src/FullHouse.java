import java.io.Serializable;

/**
 * This class is a subclass of the Hand class, and are used to model a hand of Full House
 * @author yuening
 *
 */
public class FullHouse extends Hand implements Serializable {
	private static final long serialVersionUID = 6798436003570096502L;
	private boolean isLarger;
	/**
	 * a constructor for building a hand of Full House with the specified player and list of cards
	 * @param player
	 * @param cards
	 */
	public FullHouse(CardGamePlayer player, CardList cards) {
		super(player, cards);
	}
	
	/**
	 * check if this is a valid Full House
	 */
	public boolean isValid() {
		if(size() == 5) {
			sort();
			if(getCard(0).getRank()==getCard(1).getRank() && getCard(1).getRank()==getCard(2).getRank()) {
				if(getCard(3).getRank()==getCard(4).getRank() && getCard(3).getRank()!=getCard(2).getRank()) {
					isLarger = false;
					return true;
				}
			} else if (getCard(2).getRank()==getCard(3).getRank() && getCard(3).getRank()==getCard(4).getRank()) {
				if(getCard(0).getRank()==getCard(1).getRank() && getCard(1).getRank()!=getCard(2).getRank()) {
					isLarger = true;
					return true;
				}
			}
		}
		return false;
	}
	
	/**
	 * return a string specifying the type 'Full House'
	 */
	public String getType() {
		return isValid()?"Full House":null;
	}
	
	/**
	 * override the method getTopCard() in the Hand class
	 * retrieve the top card of this hand
	 * @return the top card. If this hand contains no card, return null
	 */
	public Card getTopCard() {
		if(isValid()) {
			sort();
			return isLarger?getCard(4):getCard(2);
		}
		return null;
		
	}
}
