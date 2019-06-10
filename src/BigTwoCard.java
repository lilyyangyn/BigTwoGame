import java.io.Serializable;

/**
 * this class is a subclass of Card. 
 * it is used to model a card used in Big Two card game
 * @author yuening
 *
 */
public class BigTwoCard extends Card implements Serializable {
	private static final long serialVersionUID = -7464391994945268241L;

	/**
	 * a constructor for building a card with the specified suit and rank
	 * @param suit
	 * 		an integer between 0 and 3
	 * @param rank
	 * 		an integer between 0 and 12
	 */
	public BigTwoCard(int suit, int rank) {
		super(suit, rank);
	}
	
	/**
	 * compare the order of this card with the specified card
	 * @param card
	 * 		the card to be compared
	 * @return a negative integer, zero, or a positive integer 
	 * 		as this card is less than, equal, or greater than the 
	 * 		specified card.
	 */
	public int compareTo(Card card) {
		int newrank1 = (this.rank + 11) % 13;
		int newrank2 = (card.getRank() + 11) % 13;
		if(newrank1 > newrank2) {
			return 1;
		} else if (newrank1 < newrank2) {
			return -1;
		} else if (this.suit > card.getSuit()) {
			return 1;
		} else if (this.suit < card.getSuit()) {
			return -1;
		} else {
			return 0;
		}
	}
	
	
	
}
