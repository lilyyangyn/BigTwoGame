import java.io.Serializable;

/**
 * This class is a subclass of the Hand class, and are used to model a hand of pair
 * @author yuening
 *
 */
public class Pair extends Hand implements Serializable {
	private static final long serialVersionUID = -756058377977073648L;

	/**
	 * a constructor for building a hand of pair with the specified player and list of cards
	 * @param player
	 * @param cards
	 */
	public Pair(CardGamePlayer player, CardList cards) {
		super(player, cards);
	}
	
	/**
	 * check if this is a valid pair
	 */
	public boolean isValid() {
		if(size() == 2) {
			sort();
			if(getCard(0).getRank()==getCard(1).getRank()) 
				return true;
		}
		return false;
	}
	
	/**
	 * return a string specifying the type 'pair'
	 */
	public String getType() {
		return "Pair";
	}

}
