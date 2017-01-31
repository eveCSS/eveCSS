package de.ptb.epics.eve.data.scandescription;

/**
 * @author Marcus Michalsky
 * @since 1.28
 */
public enum YAxisModifier {
	
	/**
	 * The additive inverse of a number a is the number that, whenn added to a, 
	 * yields 0 (it reverses its sign).
	 */
	INVERSE {
		@Override
		public String toString() {
			return "times -1";
		}
	},
	
	/**
	 * Data is handled as is.
	 */
	NONE {
		@Override
		public String toString() {
			return "none";
		}
	};
	
	public static String[] valuesAsString() {
		final YAxisModifier[] yAxisModifier = YAxisModifier.values();
		final String[] values = new String[yAxisModifier.length];
		for (int i = 0; i < yAxisModifier.length; ++i) {
			values[i] = yAxisModifier[i].toString();
		}
		return values;
	}
}