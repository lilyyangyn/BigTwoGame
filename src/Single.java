import java.io.Serializable;

/**
 * This class is a subclass of the Hand class, and are used to model a hand of single
 * @author yuening
 *
 */
public class Single extends Hand implements Serializable {
	private static final long serialVersionUID = -581791488372691717L;

	/**
	 * a constructor for building a hand of single with the specified player and list of cards
	 * @param player
	 * @param cards
	 */
	public Single(CardGamePlayer player, CardList cards) {
		super(player, cards);
	}
	
	/**
	 * check if this is a valid single
	 */
	public boolean isValid() {
		if(size() == 1) {
			return true;
		}
		return false;
	}
	
	/**
	 * return a string specifying the type 'single'
	 */
	public String getType() {
		return "Single";
	}
}

