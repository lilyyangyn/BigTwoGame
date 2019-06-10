import java.io.Serializable;

/**
 * This class is a subclass of the Deck class, 
 * and is used to model a deck of cards used in a Big Two card game
 * @author yuening
 *
 */
public class BigTwoDeck extends Deck implements Serializable {
	private static final long serialVersionUID = -7223851500822393400L;

	/**
	 * initialize a deck of Big Two cards.
	 * remove all cards, create 52 Big Two cards and add to the deck
	 */
	public void initialize() {
		removeAllCards();
		for (int i = 0; i < 4; i++) {
			for (int j = 0; j < 13; j++) {
				Card card = new BigTwoCard(i, (j+2)%13);
				addCard(card);
			}
		}

	}
}
