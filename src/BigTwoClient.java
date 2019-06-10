import java.util.ArrayList;
import java.net.*;
import java.io.*;

public class BigTwoClient implements CardGame, NetworkGame, Serializable {
	private static final long serialVersionUID = 6337687344026903227L;
	//the number of players
	private int numOfPlayers;
	//a deck of cards
	private Deck deck;
	//a list of players
	private ArrayList<CardGamePlayer> playerList;
	//a list of hands played on the table
	private ArrayList<Hand> handsOnTable;
	//the playerID (i.e., index) of the local player
	private int playerID;
	//the name of the local player
	private String playerName;
	//the IP address of the game server
	private String serverIP = "127.0.0.1";
	//the TCP port of the game server
	private int serverPort;
	//a socket connection to the game server
	private Socket sock;
	//an ObjectOutputStream for sending messages to the server
	private ObjectOutputStream oos;
	//the index of the player for the current turn
	private int currentIdx;
	//a Big Two table which builds the GUI for the game and handles all user actions
	private BigTwoTable table;

	/**
	 * a constructor creating bigTwoClient
	 * 4 users and Big Two table for GUI are created, a connection is made to the game server
	 */
	public BigTwoClient(int serverPort){
		playerList = new ArrayList<CardGamePlayer>();
		handsOnTable = new ArrayList<Hand>();
		for(int i=0; i<4; i++) {
			playerList.add(new CardGamePlayer(null));
		}
		table = new BigTwoTable(this);
		table.setUserName();
		table.repaint();
		table.disable();
		this.serverPort = serverPort;
		makeConnection();
	}
	
	/**
	 * Get the number of players
	 */
	public int getNumOfPlayers() {
		return numOfPlayers;
	}

	/**
	 * Get the deck of cards being used
	 */
	public Deck getDeck() {
		return deck;
	}

	/**
	 * Get the list of player
	 */
	public ArrayList<CardGamePlayer> getPlayerList() {
		return playerList;
	}

	/**
	 * Get the list of hands played on the table
	 */
	public ArrayList<Hand> getHandsOnTable() {
		return handsOnTable;
	}

	/**
	 * Get the index of the player for the current turn
	 */
	public int getCurrentIdx() {
		return currentIdx;
	}

	/**
	 * Start or restart the game with a given shuffled deck of cards
	 * @param deck. Notice that the deck is already shuffled
	 */
	public synchronized void start(Deck deck) {
		this.deck = deck;
		removeAllCards();
		dealCards();
		findBeginner();
		table.repaintBigTwoPanel();
	}

	/**
	 * Make a move by a player with the specified playerID using the cards specified by the list of indices.
	 * @param playerID, the playerId of the active player
	 * @param cardIdx, the index of cards chosen by the active player 
	 */
	public void makeMove(int playerID, int[] cardIdx) {
		CardList select = playerList.get(playerID).play(cardIdx);
		if(addValidMove(playerList.get(currentIdx), select)) {
			if(currentIdx == this.playerID) {
				table.disable();
			}
			if(!endOfGame()) {
				currentIdx = (currentIdx+1)%4;
				table.setActivePlayer(currentIdx);
				table.repaintBigTwoPanel();
			} else {
				currentIdx = -1;
				table.setActivePlayer(currentIdx);
				table.showGameResult(null);
				removeAllCards();
				table.reset();
				table.repaintBigTwoPanel();
				sendMessage(new CardGameMessage(CardGameMessage.READY, -1, null));
			}	
		}
	}

	/**
	 * check a move made by a player
	 * @param playerID, the playerId of the active player
	 * @param cardIdx, the index of cards chosen by the active player 
	 */
	public synchronized void checkMove(int playerID, int[] cardIdx) {
		if(playerID != this.playerID) {
			makeMove(playerID, cardIdx);
			if(currentIdx == this.playerID) {
				table.enable();
			}
		}
	}

	/**
	 * Check if the game ends
	 * @return true if the game is end, and false, otherwise.
	 */
	public boolean endOfGame() {
		return playerList.get(0).getNumOfCards()==0 || playerList.get(1).getNumOfCards()==0 
				|| playerList.get(2).getNumOfCards()==0 || playerList.get(3).getNumOfCards()==0;
	}
	
	/**
	 * Get the playerID of the local player
	 */
	public int getPlayerID() {
		return playerID;
	}

	/**
	 * Set the playerID of the local player
	 * @param playerID
	 */
	public void setPlayerID(int playerID) {
		this.playerID = playerID;
	}

	/**
	 * Get the name of the local player
	 */
	public String getPlayerName() {
		return playerName;
	}

	/**
	 * Set the name of the local player
	 * @param playerName
	 */
	public void setPlayerName(String playerName) {
		this.playerName = playerName;
	}

	/**
	 * Get the IP address of the game server
	 */
	public String getServerIP() {
		return serverIP;
	}

	/**
	 * Set the IP address of the game server
	 * @param serverIP
	 */
	public void setServerIP(String serverIP) {
		this.serverIP = serverIP;
	}

	/**
	 * Get the TCP port of the game server
	 */
	public int getServerPort() {
		return serverPort;
	}

	/**
	 * Set the TCP port of the game server
	 * @param serverPort
	 */
	public void setServerPort(int serverPort) {
		this.serverPort = serverPort;
	}

	/**
	 * Make a socket connection with the game server
	 */
	public synchronized void makeConnection() {
		try {
			//connect to the server
			sock = new Socket(serverIP, serverPort);
			//create an ObjectOutputStream for sending message to the game server
			oos = new ObjectOutputStream(sock.getOutputStream());
		} catch (Exception ex) {
			table.failToConnectServer();
			ex.printStackTrace();
			return;
		}
		try {
			//create a thread for receiving messages from the game server
			Thread t = new Thread(new ServerHandler(sock));
			t.start();
			//enable chatting
			table.enableAfterConnect();
			//send a message of JOIN and READY
			sendMessage(new CardGameMessage(CardGameMessage.JOIN, -1, playerName));
			sendMessage(new CardGameMessage(CardGameMessage.READY, -1, null));
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	/**
	 * parse the message received from the game server
	 * @param message, which is received from the game server 
	 */
	public synchronized void parseMessage(GameMessage message) {
		switch (message.getType()) {
		case CardGameMessage.PLAYER_LIST:
			setPlayersInfo(message);
			break;
		case CardGameMessage.JOIN:
			addNewPalyer(message);
			break;
		case CardGameMessage.FULL:
			table.printMsg(" The server is full. No new join is allowed.\n");
			break;
		case CardGameMessage.QUIT:
			removePlayer(message);
			break;
		case CardGameMessage.READY:
			if(message.getPlayerID() > -1 && message.getPlayerID() < 4) { 
				table.printMsg(String.format(" %s is ready for the game.\n", 
						playerList.get(message.getPlayerID()).getName()));
			}
			break;
		case CardGameMessage.START:
			table.printMsg("\n All players are ready. Game starts\n");
			start((BigTwoDeck)message.getData());
			break;
		case CardGameMessage.MOVE:
			checkMove(message.getPlayerID(), (int[])message.getData());
			break;
		case CardGameMessage.MSG:
			table.printChatMsg(" " + (String)message.getData() + "\n");
			break;
		default:
			break;
		}
	}

	/**
	 * Send the specified message to the game server
	 * @param message
	 */
	public synchronized void sendMessage(GameMessage message) {
		try {
			oos.writeObject(message);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	/**
	 * Set the local playerID, and update the name of the players
	 * @param message
	 */
	private synchronized void setPlayersInfo(GameMessage message) {
		playerID = message.getPlayerID();
		String[] data = (String[])message.getData();
		for(int i=0; i<4; i++) {
			playerList.get(i).setName(data[i]);;
		}
		table.repaintBigTwoPanel();
	}
	
	/**
	 * Add a new player to the player list by updating his/her name
	 * @param message
	 */
	private synchronized void addNewPalyer(GameMessage message) {
		table.clearMsgArea();
		table.clearChatArea();
		if(message.getPlayerID() > -1 && message.getPlayerID() < 4) {
			if(playerList.get(message.getPlayerID()).getName() == null) {
				playerList.get(message.getPlayerID()).setName((String)message.getData());
			}
		}
		table.repaintBigTwoPanel();
	}
	
	/**
	 * Remove a player from the game by setting his/her name to an empty string
	 * If the game is in progress, stop the game and send a READY message to the server
	 * @param message
	 */
	private synchronized void removePlayer(GameMessage message) {
		table.repaintBigTwoPanel();
		if(message.getPlayerID() > -1 && message.getPlayerID() < 4) { 
			table.printMsg(String.format(" %s leaves the game.\n", 
					playerList.get(message.getPlayerID()).getName()));
		}
		if (currentIdx > -1 && currentIdx < 4) {
			currentIdx = -1;
			table.setActivePlayer(currentIdx);
			if(currentIdx == playerID) {
				table.disable();
			}
			String player = playerList.get(message.getPlayerID()).getName() + " (" + (String)message.getData() + ")";
			table.showGameResult(player);
			removeAllCards();
			table.reset();
			table.repaintBigTwoPanel();
			sendMessage(new CardGameMessage(CardGameMessage.READY, -1, null));
		}
		if(message.getPlayerID() > -1 && message.getPlayerID() < 4) {
			if(playerList.get(message.getPlayerID()).getName() != null) {
				playerList.get(message.getPlayerID()).setName(null);
			}
		}
	}
	
	/**
	 * An inner class handles message receiving from the server
	 * @author yuening
	 */
	private class ServerHandler implements Runnable {
		private ObjectInputStream ois;
		/**
		 * Creates and returns an instance of the ServerHandler class.
		 * @param clientSocket, the socket connection to the server
		 */
		public ServerHandler(Socket sock) {
			try {
				ois = new ObjectInputStream(sock.getInputStream());
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
		
		public void run() {
			 CardGameMessage msg;
			 try {
				 //waits for the messages from server
				 while((msg = (CardGameMessage)ois.readObject()) != null) {
					 parseMessage(msg);
				 }	 
			 } catch (Exception ex) {
				 table.failToConnectServer();
				 ex.printStackTrace();
			 }
		}
	 }
	
	/**
	 * Start a Big Two card game. 
	 * Create an instance of BigTwoClient
	 * @param args
	 */
	public static void main(String[] args) {
		if(args.length > 0) {
			new BigTwoClient(Integer.parseInt(args[0]));
		} else {
			new BigTwoClient(2396);
		}
		
	}
	
	/**
	 * Return a valid hand from the specified list of cards of the player
	 * @param player
	 * @param cards
	 * @return a valid hand from the specified list of cards of the player. return null 
	 * 		if no valid hand can be composed from the specified list of cards
	 */
	public static Hand composeHand(CardGamePlayer player, CardList cards) {
		Hand matchedHand;
		if(cards == null) {
			return null;
		}
		if(cards.size()==1) {
			matchedHand = new Single(player, cards);
			return matchedHand;
		} else if (cards.size()==2) {
			matchedHand = new Pair(player, cards);
			return matchedHand.isValid()?matchedHand:null;		
		} else if (cards.size()==3) {
			matchedHand = new Triple(player, cards);
			return matchedHand.isValid()?matchedHand:null;	
		} else if (cards.size()==5) {		
			matchedHand = new Straight(player, cards);
			if(matchedHand.isValid())
				return matchedHand;	
			matchedHand = new Flush(player, cards);
			if(matchedHand.isValid())
				return matchedHand;		
			matchedHand = new FullHouse(player, cards);
			if(matchedHand.isValid())
				return matchedHand;		
			matchedHand = new Quad(player, cards);
			if(matchedHand.isValid())
				return matchedHand;		
			matchedHand = new StraightFlush(player, cards);
			return matchedHand.isValid()?matchedHand:null;	
		}
		return null;
	}
	
	/**
	 * Remove all cards from the table as well as from the players
	 */
	private void removeAllCards() {
		handsOnTable = new ArrayList<Hand>();
		for(int i=0; i<4; i++) {
			playerList.get(i).removeAllCards();
		}
	}
	/**
	 * Deal the cards from the deck to the four users.Each player gets one card in each round
	 * After dealing, all players sort the cards in hand
	 */
	private void dealCards() {
		for(int j=0; j<13; j++) {
			for(int i=0; i<4; i++) {	
				playerList.get(i).addCard(deck.getCard(i+4*j));
			}
		}
		for(int i=0; i<4; i++) {
			playerList.get(i).sortCardsInHand();
		}
	}
	/**
	 * Set the user with Three of Diamonds as the beginner.
	 * Set the value of currentIdx equal to the beginner.
	 */
	private void findBeginner() {
		for(int i=0; i<4; i++) {
			Card card = new Card(0, 2);
			if(playerList.get(i).getCardsInHand().contains(card)) {
				currentIdx = i;
				break;
			}
		}
		table.setActivePlayer(currentIdx);
		if(playerID == currentIdx) {
			table.enable();
		}
	}
	/**
	 * private function so that it can only be accessed by start()
	 * 
	 * change the index of player's card list into the card list
	 * judge whether this move is legal and the hand is valid.
	 * if legal and valid, add the newest hand into the 'handsOnTable'
	 * @param: select
	 * 		the list of the index of the current player's card list, which they selected as their newest move.
	 * @return: true if this move is legal and the hand is valid. false, otherwise
	 */
	private boolean addValidMove(CardGamePlayer currentPlayer, CardList selectedCards) {
		Hand newHand = composeHand(currentPlayer, selectedCards);
		Card threeOfDiamond = new Card(0, 2);
		if(newHand != null) {
			table.printMsg(" {"+ newHand.getType() +"} "+ selectedCards);
		} else if (selectedCards == null) {
			table.printMsg(" {Pass} ");
		} else {
			table.printMsg(" " + selectedCards.toString());
		}
		if(handsOnTable.size() == 0){
			if(newHand != null && newHand.contains(threeOfDiamond)) {
				handsOnTable.add(newHand);
				playerList.get(currentIdx).removeCards(newHand);
				table.printMsg("\n");
				return true;
			}
		} else {
			if(playerList.get(currentIdx) == handsOnTable.get(handsOnTable.size()-1).getPlayer()) {
				if(newHand != null) {
					handsOnTable.add(newHand);
					playerList.get(currentIdx).removeCards(newHand);
					table.printMsg("\n");
					return true;
				}
			} else {
				if (selectedCards == null) {
					table.printMsg("\n");
					return true;
				} else if (newHand != null && newHand.beats(handsOnTable.get(handsOnTable.size()-1))) {
					handsOnTable.add(newHand);
					playerList.get(currentIdx).removeCards(newHand);
					table.printMsg("\n");
					return true;
				}
			}
		}
		table.printMsg(" <== No a legal move!!!\n");
		return false;
	}
	
}
