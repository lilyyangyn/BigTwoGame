import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.Serializable;

/**
 * a class used to build a GUI for the Big Two card game and handle all user
 * actions
 * 
 * @author yuening
 */
public class BigTwoTable implements CardGameTable, Serializable {
	private static final long serialVersionUID = 780190203261577487L;
	private BigTwoClient game;
	private boolean[] selected;
	private int activePlayer = -1;
	private JFrame frame;
	private JPanel bigTwoPanel;
	private BigTwoPanel[] playerPanel;
	private TablePanel tablePanel;
	private JMenuItem connectMenuItem;
	private JButton playButton;
	private JButton passButton;
	private JTextArea msgArea;
	private JTextField chatField;
	private JTextArea chatArea;
	private JButton chatButton;
	private Image[][] cardImages;
	private Image cardBackImage;
	private Image[] avatars;

	/**
	 * A constructor for creating a BigTwoTable
	 * 
	 * @param game, which is a reference to a card game associates with this table
	 */
	public BigTwoTable(BigTwoClient game) {
		this.game = game;
		importAvatarsImage();
		importCardsImage();
		resetSelected();
	}
	
	/**
	 * inform player there is an error connecting the server 
	 * enable "connect" menu item and disable the "Send" button of chatting
	 */
	public void failToConnectServer() {
		JOptionPane.showMessageDialog(frame, "Error when connecting!", "Connect Error", JOptionPane.ERROR_MESSAGE);
		connectMenuItem.setEnabled(true);
		chatButton.setEnabled(false);
	}
	
	/**
	 * Enable the chat button "Send" to enable chatting and disable "connect" menu item, after the connect is successgul
	 */
	public void enableAfterConnect() {
		connectMenuItem.setEnabled(false);
		chatButton.setEnabled(true);
	}
	
	/**
	 * Get a valid name from user and name/rename him/her with this name
	 */
	public void setUserName() {
		try {
			String name; 
			name = JOptionPane.showInputDialog(frame, "What's your name?", "User Name", JOptionPane.PLAIN_MESSAGE);
			if(isValidName(name)) {
				game.setPlayerName(name);
				JOptionPane.showMessageDialog(frame, "Succeed!");
			}
			while(game.getPlayerName() == null) {
				name = JOptionPane.showInputDialog(frame, "What's your name?", "User Name", JOptionPane.PLAIN_MESSAGE);
				if(isValidName(name)) {
					game.setPlayerName(name);
					JOptionPane.showMessageDialog(frame, "Succeed!");
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	/**
	 * validate the name
	 * @param newName
	 * @return true, if the name is valid, otherwise, false
	 */
	private boolean isValidName(String newName) {
		if(newName == null) {
			System.exit(0);
		} else if(newName.equals("")) {
			JOptionPane.showMessageDialog(frame, "Empty Name!", "Failure In Naming", JOptionPane.ERROR_MESSAGE);
		} else if(newName.equals("Waiting For Player") || newName.equals("null")) {
			JOptionPane.showMessageDialog(frame, "Invalid Name!", "Failure In Naming", JOptionPane.ERROR_MESSAGE);
		} else {
			boolean uniqueName = true;
			for(int i=0; i<4; i++) {
				if(newName.equals(game.getPlayerList().get(i).getName())) {
					uniqueName = false;
					JOptionPane.showMessageDialog(frame, "Duplicated Name!", "Failure In Naming", JOptionPane.ERROR_MESSAGE);
					break;
				}
			}
			return uniqueName;
		}
		return false;
	}
	
	/**
	 * repaint bigTwoPanel
	 */
	public void repaintBigTwoPanel() {
		bigTwoPanel.repaint();
	}
	
	/**
	 * Set the index of the active player (i.e. current player)
	 */
	public void setActivePlayer(int activePlayer) {
		if((this.activePlayer > -1 && this.activePlayer < 4) && this.playerPanel != null) {
			playerPanel[this.activePlayer].setBorder(BorderFactory.createRaisedBevelBorder());
			playerPanel[this.activePlayer].setBackground(new Color(255, 218, 185));
		}
		this.activePlayer = activePlayer;
		if((this.activePlayer > -1 && this.activePlayer < 4) && playerPanel != null) {
			printMsg(String.format(" %s's turn:\n", game.getPlayerList().get(activePlayer).getName()));
			resetSelected();
			playerPanel[this.activePlayer].setBorder(BorderFactory.createLoweredBevelBorder());
			playerPanel[this.activePlayer].setBackground(new Color(255, 239, 213));
		}
	}

	/**
	 * Get an array of indices of the cards selected
	 * @return the index of selected cards
	 */
	public int[] getSelected() {
		int[] selectedIdx = null;
		int count = 0;
		for(int i=0; i < selected.length; i++) {
			if(selected[i]) {
				count++;
			}
		}
		if(count != 0) {
			selectedIdx = new int[count];
			count = 0;
			for(int i=0; i < selected.length; i++) {
				if(selected[i]) {
					selectedIdx[count] = i;
					count++;
				}
			}
		}
		return selectedIdx;
	}

	/**
	 * Reset the list of selected cards
	 */
	public void resetSelected() {
		if(game.getPlayerList().get(game.getPlayerID()).getNumOfCards() > 0) {
			selected = new boolean[game.getPlayerList().get(game.getPlayerID()).getNumOfCards()];
		}
	}

	/**
	 * Print the specified string to the message area of the GUI
	 * @param msg, the message to be printed
	 */
	public void printMsg(String msg) {
		msgArea.append(msg);
		msgArea.setCaretPosition(msgArea.getDocument().getLength());
	}

	/**
	 * Clear the message area of the GUI
	 */
	public void clearMsgArea() {
		msgArea.setText("");
	}
	
	/**
	 * Print the specified message to the chat area of the GUI
	 * @param msg, the message to be printed
	 */
	public void printChatMsg(String msg) {
		chatArea.append(msg);
		chatArea.setCaretPosition(chatArea.getDocument().getLength());
	}

	/**
	 * Clear the message area of the GUI
	 */
	public void clearChatArea() {
		chatArea.setText("");
	}

	/**
	 * Reset the GUI
	 */
	public void reset() {
		resetSelected();
		clearMsgArea();
		clearChatArea();
		disable();
	}

	/**
	 * Enable user interactions with the GUI, including enabling the "Play" and
	 * "Pass" buttons and selection of cards by mouse clicks
	 */
	public void enable() {
		playButton.setEnabled(true);
		passButton.setEnabled(true);		
		playerPanel[game.getPlayerID()].addMouseListener(playerPanel[game.getPlayerID()]);
	}

	/**
	 * Disable user interaction with the GUI
	 */
	public void disable() {
		playButton.setEnabled(false);
		passButton.setEnabled(false);
		playerPanel[game.getPlayerID()].removeMouseListener(playerPanel[game.getPlayerID()]);
	}
	
	/**
	 * show the result of game
	 */
	public void showGameResult(String player) {
		if(activePlayer < 0 || activePlayer > 3) {
			bigTwoPanel.repaint();
			printMsg("\n Game ends.\n");
			for(int i=0; i<4; i++) {
				if(game.getPlayerList().get(i).getNumOfCards() == 0) {
					printMsg(String.format(" %s wins the game.\n", game.getPlayerList().get(i).getName()));
				} else {
					if(game.getPlayerList().get(i).getNumOfCards() == 1)
						printMsg(String.format(" %s has 1 card in hands.\n", game.getPlayerList().get(i).getName()));
					else
						printMsg(String.format(" %s has %d card in hands.\n", game.getPlayerList().get(i).getName(), 
								game.getPlayerList().get(i).getNumOfCards()));
				}
			}
			try {
				if(game.endOfGame()) {
					if(game.getPlayerList().get(game.getPlayerID()).getNumOfCards() == 0) {
						JOptionPane.showMessageDialog(frame, "You Win!");
					} else {
						JOptionPane.showMessageDialog(frame, "You Lose!");
					}
				} else {
					JOptionPane.showMessageDialog(frame, String.format("Game ends because %s leaves the game.", player));
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}

	/**
	 * Repaint the GUI
	 */
	public void repaint() {
		frame = new JFrame("Big Two");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setMinimumSize(new Dimension(830, 630));
		frame.setSize(950, 650);
		Container container = frame.getContentPane();

		// menu bar
		JMenuBar menuBar = new JMenuBar();
		// Game menu: restart, quit
		JMenu menu = new JMenu("Game");
		connectMenuItem = new JMenuItem("Connect");
		connectMenuItem.addActionListener(new ConnectMenuItemListener());
		menu.add(connectMenuItem);
		JMenuItem quitMenuItem = new JMenuItem("Quit");
		quitMenuItem.addActionListener(new QuitMenuItemListener());
		menu.add(quitMenuItem);
		menuBar.add(menu);
		// Help menu: rules
		JMenu help = new JMenu("Help");
		JMenuItem rules = new JMenuItem("Rules");
		rules.addActionListener(new RulesMenuItemListener());
		help.add(rules);
		menuBar.add(help);

		// msgPanel
		JPanel msgPanel = new JPanel();
			// msgArea
		msgArea = new JTextArea();
		msgArea.setLineWrap(true);
		msgArea.setWrapStyleWord(true);
		msgArea.setEditable(false);
		msgArea.setBackground(new Color(253, 245, 230));
		JScrollPane scroller = new JScrollPane(msgArea);
		scroller.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		scroller.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		scroller.setBorder(BorderFactory.createLoweredBevelBorder());
		
			// chatArea
		chatArea = new JTextArea();
		chatArea.setLineWrap(true);
		chatArea.setWrapStyleWord(true);
		chatArea.setEditable(false);
		chatArea.setBackground(new Color(255, 239, 213));
		JScrollPane chatScroller = new JScrollPane(chatArea);
		chatScroller.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		chatScroller.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		chatScroller.setBorder(BorderFactory.createRaisedSoftBevelBorder());
			//chatControlPanel
		JPanel chatControlPanel = new JPanel();
		JLabel chatLabel = new JLabel("Message: ");
		chatField = new JTextField();
		chatButton = new JButton("Send");
		chatButton.addActionListener(new ChatButtonListener());
		chatControlPanel.setLayout(new BorderLayout());
		chatControlPanel.add(chatLabel, BorderLayout.WEST);
		chatControlPanel.add(chatField, BorderLayout.CENTER);
		chatControlPanel.add(chatButton, BorderLayout.EAST);
		chatControlPanel.setBorder(BorderFactory.createRaisedBevelBorder());
		chatControlPanel.setBackground(new Color(255, 239, 213));
			// add msgArea, chatArea, chatControlPanel to msgPanel
		msgPanel.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c = new GridBagConstraints();
		c.gridheight = 30;
		c.weightx = 1;
		c.weighty = 30;
		c.fill = GridBagConstraints.BOTH;
		msgPanel.add(scroller, c);
		c.gridx = 0;
		c.gridy = 30;
		c.gridheight = 20;
		c.weightx = 1;
		c.weighty = 20;
		c.fill = GridBagConstraints.BOTH;
		msgPanel.add(chatScroller, c);
		c.gridy = 50;
		c.gridheight = 1;
		c.weighty = 1;
		msgPanel.add(chatControlPanel, c);
		

		// big two panel
		bigTwoPanel = new JPanel();

		// big two panel - desk panel
		JPanel deskPanel = new JPanel();
		playerPanel = new BigTwoPanel[4];
		tablePanel = new TablePanel();

		// set player panels and add them to desk panel
		deskPanel.setLayout(new GridLayout(5, 1));
		for (int i = 0; i < 4; i++) {
			playerPanel[i] = new BigTwoPanel(i);
			if(i == game.getPlayerID()) {
				playerPanel[i].addMouseListener(playerPanel[i]);
			}
			if(i == activePlayer) {
				playerPanel[i].setBorder(BorderFactory.createLoweredBevelBorder());
				playerPanel[i].setBackground(new Color(255, 239, 213));
			} else {
				playerPanel[i].setBorder(BorderFactory.createRaisedBevelBorder());
				playerPanel[i].setBackground(new Color(255, 218, 185));
			}
			deskPanel.add(playerPanel[i]);
		}

		// add table panel to desk panel
		tablePanel.setBorder(BorderFactory.createLoweredBevelBorder());
		tablePanel.setBackground(new Color(255, 218, 185));
		deskPanel.add(tablePanel);

		// big two panel - control panel
		JPanel controlPanel = new JPanel();
		playButton = new JButton("play");
		playButton.addActionListener(new PlayButtonListener());
		passButton = new JButton("pass");
		passButton.addActionListener(new PassButtonListener());
		controlPanel.setLayout(new BoxLayout(controlPanel, BoxLayout.X_AXIS));
		controlPanel.add(Box.createGlue());
		controlPanel.add(playButton);
		controlPanel.add(passButton);
		controlPanel.add(Box.createGlue());
		controlPanel.setBorder(BorderFactory.createRaisedBevelBorder());
		controlPanel.setBackground(new Color(255, 239, 213));

		// add desk and control panels to big two panel
		bigTwoPanel.setLayout(new GridBagLayout());
		c = new GridBagConstraints();
		c.gridheight = 50;
		c.weightx = 1;
		c.weighty = 50;
		c.fill = GridBagConstraints.BOTH;
		bigTwoPanel.add(deskPanel, c);
		c.gridx = 0;
		c.gridy = 50;
		c.gridheight = 1;
		c.weighty = 1;
		bigTwoPanel.add(controlPanel, c);

		// add big two panel and msgArea to frame and set frame visible
		container.setLayout(new GridLayout(0, 2));
		container.add(bigTwoPanel);
		container.add(msgPanel);
		frame.setJMenuBar(menuBar);
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);

	}

	/**
	 * an inner class handling cards display and card selection event
	 * @author yuening
	 */
	class BigTwoPanel extends JPanel implements MouseListener, Serializable {
		private static final long serialVersionUID = 3133294498384220982L;
		private int playerID;
		/**
		 * a constructor of the inner class BigTwoPanel
		 * @param playerID, the ID of the player in control of this panel
		 */
		public BigTwoPanel(int playerID) {
			this.playerID = playerID;
		}
		
		/**
		 * overwrite method of the inner class BigTwoPanel: paint the components in the panel
		 * @param g
		 */
		public void paintComponent(Graphics g) {
			super.paintComponent(g);
			//paint name
			String name = game.getPlayerList().get(playerID).getName();
			if(name != null) {
				g.drawString(String.format("%s", game.getPlayerList().get(playerID).getName()), 5, 15);
			} else {
				g.drawString("Waiting For Player", 5, 15);
			}
			//paint avatar
			g.drawImage(avatars[playerID], 5, 20, this);
			//paint cards
			CardList cardsOfPlayer;
			Image cardImage;
			if (game.getPlayerList().get(playerID).getNumOfCards() > 0) {
				if(activePlayer > 3 || activePlayer < 0) {
					cardsOfPlayer = game.getPlayerList().get(playerID).getCardsInHand();
					for (int i = 0; i < cardsOfPlayer.size(); i++) {
						cardImage=cardImages[cardsOfPlayer.getCard(i).getSuit()][cardsOfPlayer.getCard(i).getRank()];
						g.drawImage(cardImage, 100 + i * 20, 18, this);
					}
				} else {
					if (playerID == game.getPlayerID()) {
						cardsOfPlayer = game.getPlayerList().get(playerID).getCardsInHand();
						for (int i = 0; i < cardsOfPlayer.size(); i++) {
							cardImage=cardImages[cardsOfPlayer.getCard(i).getSuit()][cardsOfPlayer.getCard(i).getRank()];
							if(!selected[i]) {
								g.drawImage(cardImage, 100 + i * 20, 18, this);
							} else {
								g.drawImage(cardImage, 100 + i * 20, 10, this);
							}
						}
					} else {
						for (int i = 0; i < game.getPlayerList().get(playerID).getNumOfCards(); i++) {
							g.drawImage(cardBackImage, 100 + i * 20, 18, this);
						}
					}
				}
			}
		}
		
		/**
		 * overwrite method of the inner class BigTwoPanel: select cards by mouse click
		 * @param e
		 */
		public void mouseClicked(MouseEvent e) {
			int index;
			int n = game.getPlayerList().get(playerID).getNumOfCards();
			if(n > 0) {
				if(e.getX() > 100 && e.getX() < 170+n*20) {
					index = (e.getX()-100)/20;
					if(index > n-1) {
						index = n-1;
					}
					if(!selected[index]) {
						if(e.getY() >= 18 && e.getY() <= 103) {
							selected[index] = true;
							this.repaint();
						} else if((index > 0) && selected[index-1]) {
							if(e.getY() >= 10 && e.getY() < 18) {
								selected[index-1] = false;
								this.repaint();
							}
						} else if((index > 1) && !selected[index-1] && selected[index-2]) {
							if((e.getY() >= 10 && e.getY() < 18) && (e.getX() <= 170+(index-2)*20)) {
								selected[index-2] = false;
								this.repaint();
							}
						}
					} else if (selected[index]) {
						if(e.getY() >= 10 && e.getY() <= 95) {
							selected[index] = false;
							this.repaint();
						} else if((index > 0) && !selected[index-1]) {
							if(e.getY() > 95 && e.getY() <= 103) {
								selected[index-1] = true;
								this.repaint();
							}
						} else if((index > 1) && selected[index-1] && !selected[index-2]) {
							if((e.getY() > 95 && e.getY() <= 103) && (e.getX() <= 170+(index-2)*20)) {
								selected[index-2] = true;
								this.repaint();
							}
						}
					} 
				}
			}
		}
		
		/**
		 * Dummy method
		 */
		public void mousePressed(MouseEvent e) {
		}
		/**
		 * Dummy method
		 */
		public void mouseReleased(MouseEvent e) {
		}
		/**
		 * Dummy method
		 */
		public void mouseEntered(MouseEvent e) {
		}
		/**
		 * Dummy method
		 */
		public void mouseExited(MouseEvent e) {
		}
	}

	/**
	 * an inner class handling cards display of cards on table
	 * @author yuening
	 */
	class TablePanel extends JPanel implements Serializable {
		private static final long serialVersionUID = -1568090461150624089L;

		/**
		 * overwrite method of the inner class TabelPanel: paint the components in the panel
		 * @param g
		 */
		public void paintComponent(Graphics g) {
			super.paintComponent(g);
			CardList lastHandOnTable;
			Image cardImage;
			if(game.getHandsOnTable().size() != 0) {
				if(activePlayer > -1 && activePlayer < 4) {
					g.drawString(String.format("Played by %s", game.getHandsOnTable()
							.get(game.getHandsOnTable().size()-1).getPlayer().getName()), 5, 15);
				} else {
					g.drawString("Game ends", 5, 15);
				}
				lastHandOnTable = game.getHandsOnTable().get(game.getHandsOnTable().size() - 1);
				for (int i = 0; i < lastHandOnTable.size(); i++) {
					cardImage = cardImages[lastHandOnTable.getCard(i).getSuit()][lastHandOnTable.getCard(i).getRank()];
					g.drawImage(cardImage, 5 + i * 17, 20, this);
				}
			} else {
				g.drawString("No card on table", 5, 15);
			}
		}
	}

	/**
	 * an inner class handling event when play button is clicked by active player
	 * @author yuening
	 */
	class PlayButtonListener implements ActionListener {
		/**
		 * play the hand selected by the user
		 * @param e
		 */
		public void actionPerformed(ActionEvent e) {
			boolean isSelected = false;
			for(int i=0; i < selected.length; i++) {
				if(selected[i]) {
					isSelected = true;
				}
			}
			if(isSelected) {
				int[] selectedCards = getSelected();
				game.makeMove(activePlayer, selectedCards);
				game.sendMessage(new CardGameMessage(CardGameMessage.MOVE, -1, selectedCards));
			}
		}
	}

	/**
	 * an inner class handling event when pass button is clicked by active player
	 * @author yuening
	 */
	class PassButtonListener implements ActionListener {
		/**
		 * pass the active user to the next one
		 * @param e
		 */
		public void actionPerformed(ActionEvent e) {
			resetSelected();
			game.makeMove(activePlayer, null);
			game.sendMessage(new CardGameMessage(CardGameMessage.MOVE, -1, null));
		}
	}

	/**
	 * an inner class handling event when connect menu item is clicked and make connection to the server
	 * @author yuening
	 */
	class ConnectMenuItemListener implements ActionListener {
		/**
		 * make connection to the server
		 * @param e
		 */
		public void actionPerformed(ActionEvent e) {
			game.makeConnection();
		}
	}
	
	/**
	 * an inner class handling event when chat button is clicked and sent chat information to the server
	 * @author yuening
	 */
	class ChatButtonListener implements ActionListener {
		/**
		 * sent chat information to the server
		 * @param e
		 */
		public void actionPerformed(ActionEvent e) {
			if(chatField.getText() != null && chatField.getText() != "") {
				game.sendMessage(new CardGameMessage(CardGameMessage.MSG, -1, chatField.getText()));
				chatField.setText("");
			}
		}
	}

	/** 
	 * an inner class handling event when restart menu item is clicked and quit the game
	 * @author yuening
	 */
	class QuitMenuItemListener implements ActionListener {
		/**
		 * quit the game
		 * @param e
		 */
		public void actionPerformed(ActionEvent e) {
			System.exit(0);
		}
	}

	/**
	 * an inner class handling event when rules menu item is clicked and display the
	 * rules of the Big Two Game
	 * @author yuening
	 */
	class RulesMenuItemListener implements ActionListener {
		private JFrame helpFrame;
		/**
		 * show rules of the game
		 * @param e
		 */
		public void actionPerformed(ActionEvent e) {
			helpFrame = new JFrame();
			helpFrame.setTitle("Rules");

			JTextArea helpText = new JTextArea();
			helpText.setLineWrap(true);
			helpText.setWrapStyleWord(true);
			helpText.setEditable(false);
			helpText.setBackground(new Color(253, 245, 230));
			JScrollPane scroller = new JScrollPane(helpText);
			scroller.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
			scroller.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
			
			helpText.append("Objective\n\n");
			helpText.append(" The point of the game is to be the first to get rid of all your cards. " +
							"There are several combinations (see Ranks and Combinations), but the 2 of spades♠, Big Two, is always the highest single card. " + "\n\n\n\n");
			helpText.append("Playing\n\n");
			helpText.append(" 1. The game is played with a deck of 52 cards. Remove the jokers from the deck and shuffle the cards.\n\n" + 
							" 2. Deal the cards out to each player counter-clockwise until all out.\n\n" + 
							" 3. The person with the 3 of diamonds♦ goes first, either with a single card or a set of combination. The 3 of diamonds♦ must be put down.\n\n" + 
							" 4. Play proceeds counter-clockwise, with the usual climbing-game rules: each player must play a higher card or combination than the one before, "
									+ "with the same number of cards. (If the starter puts down a single card, all players have to follow with a single card. If the starter plays "
									+ "with five cards combination, then the other players have to follow.)\n\n" + 
							" 5. All plays are made by placing the cards face up in the centre of the table.\n\n" + 
							" 6. Players may also pass. You can continue playing when your turn comes again. (You are not obligated to beat a card or a combination just because you can. "
									+ "You may choose to pass each time and keep your high cards for a better opportunity.)\n\n" + 
							" 7. The play continues for several circuits if necessary, until all but one of the players pass in succession, no one being able or willing to beat the "
									+ "last play. When this happens, the heap of played cards are discarded.\n\n" + 
							" 8. Each player is allowed to know how many cards the other players have in their hands at any time.\n" + "\n\n\n\n"); 
			helpText.append("Winning\n\n");
			helpText.append(" Whoever gets rid of all of his/her cards first is the winner. Game ends either when winner is declared or when there is only one player left. \n" + "\n\n\n\n");
			helpText.append("Ranks and Combinations\n\n");
			helpText.append(" - Denomination ranking: Cards ranked from high to low: 2-A-K-Q-J-10-9-8-7-6-5-4-3.\n\n" + 
							" - Suit ranking: Cards ranked from high to low: spades♠, hearts♥, clubs♣, diamonds♦\n\n" + 
							" - Pairs: A pair of equal ranked cards - twos are highest and threes are lowest. Between equal ranked pairs, the one containing the highest suit is better.\n\n" + 
							" - Triples: Three equal ranked cards - twos are highest and threes are lowest. Between equal ranked pairs, the one containing the highest suit is better.\n\n" + 
							" - Five cards: Cards combination ranked from high to low: straight flush, quad(4 of a kind plus 1 extra card), full house (3 of a " +
							"kind plus a double), flush (5 cards of a suit in any order), straight. \n" + "\n\n\n\n");
			helpText.append("Note\n\n");
			helpText.append(" A combination can only be beaten by a better combination with the same number of cards: so a single card can only be beaten by a single card, a pair by "+
							"a better pair and a triple by a better triple. You cannot for example use a triple to beat a pair or a straight to beat a triple. \n\n");
			helpText.setCaretPosition(0);
			
			JButton iknowButton = new JButton("I Know");
			iknowButton.addActionListener(new iknowButtonListener());
			
			helpFrame.getContentPane().add(scroller, BorderLayout.CENTER);
			helpFrame.getContentPane().add(iknowButton, BorderLayout.SOUTH);
			helpFrame.setSize(600, 400);
			helpFrame.setLocationRelativeTo(frame);
			helpFrame.setVisible(true);
		}
		/**
		 * an inner class of Rules Menu Item Listener which handles event when "I know" button is clicked and close the window
		 * @author yuening
		 */
		class iknowButtonListener implements ActionListener {
			public void actionPerformed(ActionEvent e) {
				helpFrame.setVisible(false);
				helpFrame.dispose();
			}
		}
	}

	/**
	 * import the images of avatars 
	 */
	private void importAvatarsImage() {
		final int WIDTH = 78;
		final int HEIGHT = 85;
		avatars = new Image[4];
		avatars[0] = new ImageIcon("avatars/player0.jpeg").getImage().getScaledInstance(WIDTH, HEIGHT, Image.SCALE_DEFAULT);
		avatars[1] = new ImageIcon("avatars/player1.jpeg").getImage().getScaledInstance(WIDTH, HEIGHT, Image.SCALE_DEFAULT);
		avatars[2] = new ImageIcon("avatars/player2.jpeg").getImage().getScaledInstance(WIDTH, HEIGHT, Image.SCALE_DEFAULT);
		avatars[3] = new ImageIcon("avatars/player3.jpeg").getImage().getScaledInstance(WIDTH, HEIGHT, Image.SCALE_DEFAULT);
	};

	/**
	 *  import the images of cards 
	 */
	private void importCardsImage() {
		final int WIDTH = 59;
		final int HEIGHT = 85;
		cardBackImage = new ImageIcon("cards/b.gif").getImage().getScaledInstance(WIDTH, HEIGHT,
				Image.SCALE_DEFAULT);
		cardImages = new Image[4][13];
		String suit = "";
		String rank = "";
		for (int i = 0; i < 4; i++) {
			for (int j = 0; j < 13; j++) {
				switch (i) {
				case 0:
					suit = "d";
					break;
				case 1:
					suit = "c";
					break;
				case 2:
					suit = "h";
					break;
				case 3:
					suit = "s";
					break;
				}
				switch (j) {
				case 0:
					rank = "a";
					break;
				case 9:
					rank = "t";
					break;
				case 10:
					rank = "j";
					break;
				case 11:
					rank = "q";
					break;
				case 12:
					rank = "k";
					break;
				default:
					rank = "" + (j+1);
				}
				cardImages[i][j] = new ImageIcon(String.format("cards/%s%s.gif", rank, suit)).getImage()
						.getScaledInstance(WIDTH, HEIGHT, Image.SCALE_DEFAULT);
			}
		}
	};
	
}