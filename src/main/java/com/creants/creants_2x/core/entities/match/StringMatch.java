package com.creants.creants_2x.core.entities.match;

/**
 * @author LamHa
 *
 */
public enum StringMatch implements IMatcher {
	EQUALS("EQUALS", 0, "=="),
	NOT_EQUALS("NOT_EQUALS", 1, "!="),
	CONTAINS("CONTAINS", 2, "contains"),
	STARTS_WITH("STARTS_WITH", 3, "startsWith"),
	ENDS_WITH("ENDS_WITH", 4, "endsWith");

	private String symbol;

	private StringMatch(final String s, final int n, final String symbol) {
		this.symbol = symbol;
	}

	@Override
	public String getSymbol() {
		return this.symbol;
	}

	@Override
	public int getType() {
		return 2;
	}

	public static StringMatch fromSymbol(final String symbol) {
		StringMatch[] values;
		for (int length = (values = values()).length, i = 0; i < length; ++i) {
			final StringMatch item = values[i];
			if (item.symbol.equals(symbol)) {
				return item;
			}
		}
		return null;
	}
}
