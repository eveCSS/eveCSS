package de.ptb.epics.eve.util.collection;

import java.util.Collections;
import java.util.List;

/**
 * @author Marcus Michalsky
 * @since 1.25
 */
public class ListUtil {
	
	/**
	 * Moves item with given sourceIndex behind item with given targetIndex in
	 * the given list.
	 * @param list the list containing the items
	 * @param sourceIndex index of item to be moved
	 * @param targetIndex index of item the other item is moved to (behind)
	 * @since 1.25
	 */
	public static void move(List<? extends Object> list, int sourceIndex, int targetIndex) {
		if (sourceIndex == targetIndex) {
			return;
		}
		if (sourceIndex < targetIndex) {
			Collections.rotate(list.subList(sourceIndex, targetIndex + 1), -1);
		} else {
			Collections.rotate(list.subList(targetIndex + 1, sourceIndex + 1), 1);
		}
	}
	
	public static String ListAsString(List<? extends Object> list) {
		StringBuffer buffer = new StringBuffer();
		for (Object o : list) {
			buffer.append(o);
			buffer.append(", ");
		}
		if (buffer.length() == 0) {
			return "List [ ]";
		}
		return "List [" 
				+ buffer.toString().substring(0, buffer.toString().length()-2)
				+ "]";
	}
	
	private ListUtil() {
	}
}