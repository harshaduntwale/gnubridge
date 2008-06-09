package org.gnubridge.core;

import org.gnubridge.core.deck.Color;
import org.gnubridge.core.deck.Trump;

public class Card {
	public static final int TWO = 0;

	public static final int THREE = 1;

	public static final int FOUR = 2;

	public static final int FIVE = 3;

	public static final int SIX = 4;

	public static final int SEVEN = 5;

	public static final int EIGHT = 6;

	public static final int NINE = 7;

	public static final int TEN = 8;

	public static final int JACK = 9;

	public static final int QUEEN = 10;

	public static final int KING = 11;

	public static final int ACE = 12;

	public static final String[] FullSuit = { "2", "3", "4", "5", "6", "7",
			"8", "9", "10", "J", "Q", "K", "A" };

	public static final int COUNT = 52;

	private int value;

	private Color denomination;

	public Card(int value, Color d) {
		this.value = value;
		this.denomination = d;
	}

	public Card(String value, Color d) {
		this(strToIntValue(value), d);
	}

	public static int strToIntValue(String value) {
		if ("2".equals(value)) {
			return TWO;
		} else if ("3".equals(value)) {
			return THREE;
		} else if ("4".equals(value)) {
			return FOUR;
		} else if ("5".equals(value)) {
			return FIVE;
		} else if ("6".equals(value)) {
			return SIX;
		} else if ("7".equals(value)) {
			return SEVEN;
		} else if ("8".equals(value)) {
			return EIGHT;
		} else if ("9".equals(value)) {
			return NINE;
		} else if ("10".equals(value)) {
			return TEN;
		} else if ("J".equals(value.toUpperCase())) {
			return JACK;
		} else if ("Q".equals(value.toUpperCase())) {
			return QUEEN;
		} else if ("K".equals(value.toUpperCase())) {
			return KING;
		} else if ("A".equals(value.toUpperCase())) {
			return ACE;
		} else {
			throw new RuntimeException("'" + value
					+ "' is not a valid card value");
		}

	}

	public boolean equals(Object obj) {
		if (obj instanceof Card) {
			return ((Card) obj).getDenomination() == denomination
					&& ((Card) obj).getValue() == value;
		} else {
			return false;
		}
	}

	public String toString() {
		return valueToString(value) + " of " + denomination;
	}

	public static String valueToString(int i) {
		switch (i) {
		case TWO:
			return "2";
		case THREE:
			return "3";
		case FOUR:
			return "4";
		case FIVE:
			return "5";
		case SIX:
			return "6";
		case SEVEN:
			return "7";
		case EIGHT:
			return "8";
		case NINE:
			return "9";
		case TEN:
			return "10";
		case JACK:
			return "J";
		case QUEEN:
			return "Q";
		case KING:
			return "K";
		case ACE:
			return "A";
		}
		return null;
	}

	public Color getDenomination() {
		return denomination;
	}

	public int getValue() {
		return value;
	}

	public boolean trumps(Card other, Trump trump) {
		return getDenomination().equals(trump) && !other.getDenomination().equals(trump);
	}

	public boolean hasSameColorAs(Card other) {
		return getDenomination().equals(other.getDenomination());
	}

	public boolean hasGreaterValueThan(Card other) {
		return getValue() > other.getValue();
	}

	public int getIndex() {
		return value + Color.getIndex(denomination)*(ACE+1);
	}

}
