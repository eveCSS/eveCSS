package de.ptb.epics.eve.util.io;

/**
 * @author Marcus Michalsky
 * @since 1.10
 */
public class StringUtil {
	
	/**
	 * 
	 * @param input
	 * @return
	 */
	public static String removeCarriageReturn(String input) {
		return input.replaceAll("\\u000D\\u000A", "\n");
	}
	
	/**
	 * Checks whether a given String contains a list of double values with 
	 * comma as delimiter.
	 * 
	 * @param list the list to check
	 * @return <code>true</code> if the given string contains a list of double 
	 * 		values with comma as delimiter, <code>false</code> otherwise
	 */
	public static boolean isPositionList(String list) {
		if (list.endsWith(",")) {
			return false;
		}
		String[] values = list.split(",");
		for (String s : values) {
			try {
				Double.parseDouble(s);
			} catch (NumberFormatException e) {
				return false;
			}
		}
		return true;
	}
}