import java.io.Serializable;

/**
 * This class is a subclass of the Hand class, and are used to model a hand of triple
 * @author yuening
 *
 */
public class Triple extends Hand implements Serializable {
	private static final long serialVersionUID = 8248296073656838788L;

	/**
	 * a constructor for building a hand of triple with the specified player and list of cards
	 * @param player
	 * @param cards
	 */
	public Triple(CardGamePlayer player, CardList cards) {
		super(player, cards);
	}
	
	/**
	 * check if this is a valid triple
	 */
	public boolean isValid() {
		if(size() == 3) {
			sort();
			if(getCard(0).getRank()==getCard(1).getRank() && getCard(1).getRank()==getCard(2).getRank())
				return true;
		}
		return false;
	}
	
	/**
	 * return a string specifying the type 'triple'
	 */
	public String getType() {
		return "Triple";
	}
}
