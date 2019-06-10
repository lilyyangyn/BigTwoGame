import java.io.Serializable;

/**
 * This class is a subclass of the Hand class, and are used to model a hand of straight
 * @author yuening
 *
 */
public class Straight extends Hand implements Serializable {
	private static final long serialVersionUID = 1280531341531695799L;

	/**
	 * a constructor for building a hand of straight with the specified player and list of cards
	 * @param player
	 * @param cards
	 */
	public Straight(CardGamePlayer player, CardList cards) {
		super(player, cards);
	}
	
	/**
	 * check if this is a valid straight
	 */
	public boolean isValid() {
		if(size() == 5) {
			sort();
			for(int i=0; i<4; i++) {
				if ((getCard(i).getRank()+11)%13+1 != (getCard(i+1).getRank()+11)%13) 
					return false;
			}
			for(int i=0; i<4; i++) {
				if(getCard(i).getSuit() != getCard(i+1).getSuit())
					return true;
			}
		}
		return false;
	}
	
	/**
	 * return a string specifying the type 'straight'
	 */
	public String getType() {
		return "Straight";
	}
}
