import java.io.Serializable;

/**
 * This class is a subclass of the Hand class, and are used to model a hand of Straight Flush
 * @author yuening
 *
 */
public class StraightFlush extends Hand implements Serializable {
	private static final long serialVersionUID = -4478424439221375478L;

	/**
	 * a constructor for building a hand of Straight Flush with the specified player and list of cards
	 * @param player
	 * @param cards
	 */
	public StraightFlush(CardGamePlayer player, CardList cards) {
		super(player, cards);
	}
	
	/**
	 * check if this is a valid Straight Flush
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
					return false;
			}
			return true;
		}
		return false;
	}
	
	/**
	 * return a string specifying the type 'Straight Flush'
	 */
	public String getType() {
		return "Straight Flush";
	}
}
