package org.gnubridge.presentation.gui;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;

import org.gnubridge.core.Card;
import org.gnubridge.core.Direction;
import org.gnubridge.core.East;
import org.gnubridge.core.Game;
import org.gnubridge.core.Hand;
import org.gnubridge.core.North;
import org.gnubridge.core.Player;
import org.gnubridge.core.South;
import org.gnubridge.core.Trick;
import org.gnubridge.core.West;
import org.gnubridge.core.deck.Color;

public class PlayView extends GBContainer {

	private Game game;
	private final int DHEIGHT = 750;
	private final int WIDTH = 800;
	private final int CARD_OFFSET = 30;
	private Rectangle table;
	private Direction humanDirection;
	private GameController controller;
	private List<CardPanel> dummyCards;
	private List<CardPanel> currentTrickCards;
	private boolean cardPlayed = false;
	private boolean displayingPreviousTrick;

	public PlayView(MainView owner) {
		super(owner);
		panel.setLayout(null);
		panel.setPreferredSize(new Dimension(WIDTH, DHEIGHT));
		panel.setSize(new Dimension(WIDTH, DHEIGHT));
		table = new Rectangle(290, DHEIGHT - CardPanel.IMAGE_HEIGHT - 35 - 5
				- 275, 222, 275);
		dummyCards = new ArrayList<CardPanel>();
		currentTrickCards = new ArrayList<CardPanel>();
	}

	public void setController(GameController c) {
		controller = c;
	}

	private Container createPlayPane() {
		Container result = new JPanel();
		result.setPreferredSize(new Dimension(800, 750));
		placeOn(result);
		return result;
	}

	public void setGame(Game g, Direction human) {
		game = g;
		humanDirection = human;
		int i = 0;
		Hand humanHand = new Hand(game.getPlayer(human).getHand());
		for (Card card : humanHand.getCardsHighToLow()) {
			CardPanel cardPanel = new CardPanel(card);
			DaListener listener = new DaListener(cardPanel, game);
			cardPanel.addMouseListener(listener);
			cardPanel.addMouseMotionListener(listener);
			panel.add(cardPanel);
			cardPanel.setLocation(200 + CARD_OFFSET * i, DHEIGHT
					- CardPanel.IMAGE_HEIGHT - 35);
			panel.setComponentZOrder(cardPanel, 0);
			i++;
		}

		displayDummy(human);
	}

	private void displayDummy(Direction human) {
		dispose(dummyCards);
		Hand dummyHand = new Hand(game.getPlayer(North.i()).getHand());
		Point dummyUpperLeft = determineDummyPos(human, dummyHand
				.getLongestColorLength());
		for (Color color : Color.list) {
			int j = 0;
			for (Card card : dummyHand.getColorHi2Low(color)) {
				CardPanel cardPanel = new CardPanel(card);
				dummyCards.add(cardPanel);
				if (human.equals(South.i())) {
					DaListener listener = new DaListener(cardPanel, game);
					cardPanel.addMouseListener(listener);
					cardPanel.addMouseMotionListener(listener);
				}
				panel.add(cardPanel);
				cardPanel.setLocation((int) dummyUpperLeft.getX(),
						(int) dummyUpperLeft.getY() + CARD_OFFSET * j);
				panel.setComponentZOrder(cardPanel, 0);
				j++;
			}
			dummyUpperLeft.setLocation(dummyUpperLeft.getX()
					+ CardPanel.IMAGE_WIDTH + 2, dummyUpperLeft.getY());
		}

	}

	protected void dockingCard(boolean isDocking) {
		if (cardPlayed != isDocking) {
			cardPlayed = isDocking;
			drawPromptArrow(panel.getGraphics());
		}
	}

	private Point determineDummyPos(Direction human, int longestColorLength) {
		if (South.i().equals(human)) {
			return new Point(235, 5);
		} else if (West.i().equals(human)) {
			return new Point(10, DHEIGHT - 500);
		} else if (East.i().equals(human)) {
			return new Point(512, DHEIGHT - 500);
		}
		throw new RuntimeException("human should never have to play as dummy");
	}

	@Override
	protected JPanel createDisplayPanel() {
		return new JPanel() {
			public void paintComponent(Graphics g) {
				super.paintComponent(g);
				g.drawRect((int) table.getX(), (int) table.getY(), (int) table
						.getWidth(), (int) table.getHeight());
				g.drawString("Trump: " + game.getTrump() + "            " + message
						+ " ", 20, DHEIGHT - 25);
				if (currentTrickCards.size() < 4) {
					drawPromptArrow(g);
				}
			}
		};
	}

	private void drawPromptArrow(Graphics g) {
		Direction d = game.getNextToPlay().getDirection2();
		Point topLeft = getExpectedSlot(d, humanDirection);
		g.setColor(java.awt.Color.WHITE);
		g.fillRect((int) topLeft.getX() + 1, (int) topLeft.getY() + 1,
				CardPanel.IMAGE_WIDTH - 2, CardPanel.IMAGE_HEIGHT - 2);
		if (cardPlayed) {
			g.setColor(java.awt.Color.GREEN);
		} else {
			g.setColor(java.awt.Color.YELLOW);
		}
		g.drawRect((int) topLeft.getX() + 1, (int) topLeft.getY() + 1,
				CardPanel.IMAGE_WIDTH - 2, CardPanel.IMAGE_HEIGHT - 2);
	}

	private Point getExpectedSlot(Direction d, Direction human) {
		Direction rotation = South.i();
		Direction slot = South.i();

		while (!rotation.equals(d)) {
			slot = slot.clockwise();
			rotation = rotation.clockwise();
		}

		Direction humanOffset = human;
		while (!humanOffset.equals(South.i())) {
			slot = slot.clockwise();
			humanOffset = humanOffset.clockwise();
		}

		if (slot.equals(South.i())) {
			return new Point(
					(int) (table.getX() + table.getWidth() / 2 - CardPanel.IMAGE_WIDTH / 2),
					(int) (table.getY() + table.getHeight() - CardPanel.IMAGE_HEIGHT));
		} else if (slot.equals(West.i())) {
			return new Point((int) table.getX(), (int) (table.getY()
					+ table.getHeight() / 2 - CardPanel.IMAGE_HEIGHT / 2));
		} else if (slot.equals(North.i())) {
			return new Point(
					(int) (table.getX() + table.getWidth() / 2 - CardPanel.IMAGE_WIDTH / 2),
					(int) table.getY());
		} else if (slot.equals(East.i())) {
			return new Point(
					(int) (table.getX() + table.getWidth() - CardPanel.IMAGE_WIDTH),
					(int) (table.getY() + table.getHeight() / 2 - CardPanel.IMAGE_HEIGHT / 2));
		}
		return null;
	}

	public void gameStateChanged() {
		if (displayingPreviousTrick) {
			return;
		}
		message = " Tricks taken North/South: "
				+ game.getTricksTaken(Player.NORTH_SOUTH) + " out of "
				+ game.getTricksPlayed();
		displayCurrentTrick();
		displayDummy(humanDirection);
		

	}

	void displayCurrentTrick() {
		displayTrick(game.getCurrentTrick());
		displayingPreviousTrick = false;

	}

	void displayTrick(Trick trick) {
		dispose(currentTrickCards);
		for (Card card : trick.getCards()) {
			CardPanel cardPanel = new CardPanel(card);
			currentTrickCards.add(cardPanel);
			cardPanel.setLocation(getExpectedSlot(game.whoPlayed(card),
					humanDirection));
			panel.add(cardPanel);
			panel.repaint();
		}

	}

	private void dispose(List<CardPanel> trash) {
		for (CardPanel card : trash) {
			card.dispose();
		}
		trash.clear();

	}

	class DaListener implements MouseListener, MouseMotionListener {

		private CardPanel theCard;
		private boolean dragging;
		private int startX = -1;
		private int startY = -1;
		private Game theGame;

		public DaListener(CardPanel card, Game g) {
			theCard = card;
			theGame = g;
		}

		public void mouseClicked(MouseEvent arg0) {
		}

		public void mouseEntered(MouseEvent arg0) {
			if (!dragging && CardPanel.canSelect(theCard)
					&& theGame.isLegalMove(theCard.getCard())) {
				theCard.setSelected(true);
			}
		}

		public void mouseExited(MouseEvent arg0) {
			if (!dragging) {
				mouseReleased(arg0);
			}
		}

		public void mousePressed(MouseEvent arg0) {

		}

		public void mouseReleased(MouseEvent arg0) {
			dragging = false;
			if (!theCard.isPlayed() && theCard.isSelected()) {
				theCard.setSelected(false);
			} else if (theCard.isPlayed()) {
				theCard.dispose();
				controller.playCard(theCard.getCard());
				theCard = null;
				dockingCard(false);

			}
		}

		public void mouseDragged(MouseEvent arg0) {
			if (!theCard.isSelected()) {
				return;
			}
			if (!dragging) {
				startX = arg0.getX();
				startY = arg0.getY();
			}
			theCard.setLocation(theCard.getX() + arg0.getX() - startX, theCard
					.getY()
					+ arg0.getY() - startY);
			if (table.contains(theCard.getBounds().getCenterX(), theCard
					.getBounds().getCenterY())) {
				theCard.setPlayed(true);
				dockingCard(true);
			} else {
				theCard.setPlayed(false);
				dockingCard(false);
			}
			theCard.repaint();
			dragging = true;
		}

		public void mouseMoved(MouseEvent arg0) {

		}

	}

	public void show() {
		owner.setContent(createPlayPane());
	}

	public void displayPreviousTrick() {
		displayingPreviousTrick = true;
		displayTrick(game.getPreviousTrick());
		
	}
}
