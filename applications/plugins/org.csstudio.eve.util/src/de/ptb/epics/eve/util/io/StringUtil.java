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
}
