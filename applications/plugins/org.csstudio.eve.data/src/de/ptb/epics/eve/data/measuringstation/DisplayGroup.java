package de.ptb.epics.eve.data.measuringstation;

/**
 * Defines whether an Option or Device will be shown.
 * <p>
 * Not Used so far.
 * 
 * @author Marcus Michalsky
 * @since 1.18
 */
public enum DisplayGroup {

	/**
	 * Option or Device is always shown.
	 */
	SUMMARY {
		@Override
		public String toString() {
			return "summary";
		}
	},
	
	/**
	 * Option or Device is only shown in detailed view. 
	 */
	DETAIL {
		@Override
		public String toString() {
			return "detail";
		}
	}
}