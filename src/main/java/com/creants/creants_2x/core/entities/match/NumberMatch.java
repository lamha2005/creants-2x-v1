package com.creants.creants_2x.core.entities.match;

/**
 * @author LamHa
 *
 */
public enum NumberMatch implements IMatcher {
	EQUALS("EQUALS", 0, "=="),
	NOT_EQUALS("NOT_EQUALS", 1, "!="),
	LESS_THAN("LESS_THAN", 2, "<"),
	GREATER_THAN("GREATER_THAN", 3, ">"),
	LESS_THAN_OR_EQUAL_TO("LESS_THAN_OR_EQUAL_TO", 4, "<="),
	GREATER_THAN_OR_EQUAL_TO("GREATER_THAN_OR_EQUAL_TO", 5, ">=");

	String symbol;

	private NumberMatch(final String s, final int n, final String symbol) {
		this.symbol = symbol;
	}

	@Override
	public String getSymbol() {
		return this.symbol;
	}

	@Override
	public int getType() {
		return 1;
	}

	public static NumberMatch fromSymbol(final String symbol) {
		NumberMatch[] values;
		for (int length = (values = values()).length, i = 0; i < length; ++i) {
			final NumberMatch item = values[i];
			if (item.symbol.equals(symbol)) {
				return item;
			}
		}
		return null;
	}
}
