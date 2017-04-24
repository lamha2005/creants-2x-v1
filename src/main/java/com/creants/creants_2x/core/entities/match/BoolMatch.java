package com.creants.creants_2x.core.entities.match;

/**
 * @author LamHa
 *
 */
public enum BoolMatch implements IMatcher {
	EQUALS("EQUALS", 0, "=="), NOT_EQUALS("NOT_EQUALS", 1, "!=");

	String symbol;

	private BoolMatch(final String s, final int n, final String symbol) {
		this.symbol = symbol;
	}

	@Override
	public String getSymbol() {
		return this.symbol;
	}

	@Override
	public int getType() {
		return 0;
	}

	public static BoolMatch fromSymbol(final String symbol) {
		BoolMatch[] values;
		for (int length = (values = values()).length, i = 0; i < length; ++i) {
			final BoolMatch item = values[i];
			if (item.symbol.equals(symbol)) {
				return item;
			}
		}
		return null;
	}
}
