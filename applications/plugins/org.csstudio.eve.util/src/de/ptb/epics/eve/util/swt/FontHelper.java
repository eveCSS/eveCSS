package de.ptb.epics.eve.util.swt;

import org.eclipse.swt.graphics.GC;

/**
 * @author Marcus Michalsky
 * @since 1.22
 */
public class FontHelper {
	/**
	 * Should be used in addition to {@link #getCharWidth(GC, String)} to 
	 * include margin space.
	 */
	public static final int MARGIN_WIDTH = 20;
	
	/**
	 * Returns the width of the given string by calculating the width of each 
	 * char in the given graphics context.
	 * <p>
	 * Usage could be as follows:
	 * <pre>
	 * GC gc = new GC(((TableViewer)viewer).getTable()); 			// get graphics context (e.g. from a table)
	 * int columnWidth = 120; 										// define a default column width
	 * for (Object o : Something.getList()) {						// iterate elements of the table
	 *     int width = FontHelper.getCharWidth(gc, o.toString());	// get width of elements string representation
	 *     if (width > columnWidth) {								// adjust width
	 *         columnWidth = width;									// if current width is too small
	 *     }
	 * }
	 * ((TableViewer)viewer).getTable().getColumn(i)				// set max width as column width
	 *     .setWidth(columnWidth + FontHelper.MARGIN_WIDTH);		// including margin space
	 * </pre>
	 * 
	 * @param gc the graphics context (containing font metrics)
	 * @param text the text of characters
	 * @return the width of the given string in the given graphics context
	 */
	public static int getCharWidth(GC gc, String text) {
		int charWidth = 0;
		for (char c : text.toCharArray()) {
			charWidth += gc.getCharWidth(c);
		}
		return charWidth;
	}
}