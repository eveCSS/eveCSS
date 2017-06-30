package de.ptb.epics.eve.util.io;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

/**
 * @author Marcus Michalsky
 * @since 1.10
 */
public final class StringUtil {

	private StringUtil() {
	}
	
	/**
	 * Checks whether a given String contains a list of double values with 
	 * comma as delimiter.
	 * 
	 * @param list the list to check
	 * @param clazz one of {@link java.lang.Double}, {@link java.lang.Integer}, 
	 * 		{@link java.lang.String}
	 * @return <code>true</code> if the given string contains a list of double 
	 * 		values with comma as delimiter, <code>false</code> otherwise
	 * @author Marcus Michalsky
	 * @since 1.10
	 */
	public static boolean isPositionList(String list, Object clazz) {
		if (list.endsWith(",")) {
			return false;
		}
		String[] values = list.split(",");
		for (String s : values) {
			if (clazz.equals(Double.class)) {
				try {
					Double.parseDouble(s);
				} catch (NumberFormatException e) {
					return false;
				}
			} else if (clazz.equals(Integer.class)) {
				try {
					Integer.parseInt(s);
				} catch (NumberFormatException e) {
					return false;
				}
			} else if (clazz.equals(String.class)) {
					return true;
			}
		}
		return true;
	}
	
	/**
	 * Checks whether a given list of strings consists of doubles. If true 
	 * the list is returned.
	 * 
	 * @param values the list to check
	 * @return a list of doubles or <code>null</code> if invalid
	 * @author Marcus Michalsky
	 * @since 1.10
	 */
	public static List<Double> getDoubleList(List<String> values) {
		List<Double> doubles = new ArrayList<Double>();
		for (String s : values) {
			try {
				doubles.add(Double.parseDouble(s));
			} catch (NumberFormatException e) {
				return null;
			}
		}
		return doubles;
	}
	
	/**
	 * Checks whether a given list of strings consists of integers. If true 
	 * the list is returned.
	 * 
	 * @param values the list to check
	 * @return a list of integers or <code>null</code> if invalid
	 * @author Marcus Michalsky
	 * @since 1.10
	 */
	public static List<Integer> getIntegerList(List<String> values) {
		List<Integer> integers = new ArrayList<Integer>();
		for (String s : values) {
			try {
				integers.add(Integer.parseInt(s));
			} catch (NumberFormatException e) {
				return null;
			}
		}
		return integers;
	}
	
	/**
	 * Builds a comma separated list of given values. Each value is represented 
	 * by its {@link java.lang.Object#toString()}.
	 * 
	 * @param values the value list
	 * @return a string containing a comma separated list of the given values
	 */
	public static String buildCommaSeparatedString(List<?> values) {
		if (values.isEmpty()) {
			return "";
		}
		StringBuffer buff = new StringBuffer();
		for (Object o : values) {
			buff.append(o.toString());
			buff.append(", ");
		}
		return buff.toString().substring(0, buff.toString().lastIndexOf(","));
	}
	
	/**
	 * 
	 * @param text
	 * @param searchList
	 * @param replacementList
	 * @return
	 * @since 1.23
	 */
	public static String replaceEach(String text, List<String> searchList,
			List<String> replacementList) {
		return StringUtils.replaceEach(text, searchList.toArray(new String[0]),
				replacementList.toArray(new String[0]));
	}
	
	/**
	 * Returns a list containing the string representations of the elements of the given list
	 * @param list the list where each element should be converted to a string
	 * @return a list containing the string representations of the elments of the given list
	 * @since 1.28
	 */
	public static List<String> getStringList(List<? extends Object> list) {
		List<String> stringList = new ArrayList<>();
		for (Object o : list) {
			stringList.add(o.toString());
		}
		return stringList;
	}
}