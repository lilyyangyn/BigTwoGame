import java.io.Serializable;

/**
 * This class is a subclass of the Hand class, and are used to model a hand of flush
 * @author yuening
 *
 */
public class Flush extends Hand implements Serializable {
	private static final long serialVersionUID = 5338535395387611184L;

	/**
	 * a constructor for building a hand of flush with the specified player and list of cards
	 * @param player
	 * @param cards
	 */
	public Flush(CardGamePlayer player, CardList cards) {
		super(player, cards);
	}
	
	/**
	 * check if this is a valid flush
	 */
	public boolean isValid() {
		if(size() == 5) {
			sort();
			for(int i=0; i<4; i++) {
				if(getCard(i).getSuit() != getCard(i+1).getSuit())
					return false;
			}
			for(int i=0; i<4; i++) {
				if ((getCard(i).getRank()+11)%13+1 != (getCard(i+1).getRank()+11)%13)
					return true;
			}
		}
		return false;
	}
	
	/**
	 * return a string specifying the type 'flush'
	 */
	public String getType() {
		return "Flush";
	}
	
	/**
	 * overwrite the beats() method
	 * @param hand
	 * @return true if this hand beats a specified hand, and false, otherwise.
	 */
	public boolean beats(Hand hand) {
		if(isValid() && size()==hand.size()) {
			if(getType() == hand.getType()) {
				if(getTopCard().getSuit() == hand.getTopCard().getSuit())
					return (getTopCard().getRank()+11)%13 > (hand.getTopCard().getRank()+11)%13?true:false;
				else
					return getTopCard().getSuit() > hand.getTopCard().getSuit()?true:false;
			} else {
				return beatType(hand)?true:false;
			}
		}
		return false;
	}
	
}
