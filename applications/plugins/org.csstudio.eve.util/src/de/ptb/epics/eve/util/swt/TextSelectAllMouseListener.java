package de.ptb.epics.eve.util.swt;

import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.widgets.Text;

/**
 * {@link org.eclipse.swt.events.MouseListener} for a text widget selecting 
 * all text if clicked (left button).
 * 
 * @author Marcus Michalsky
 * @since 1.8
 */
public class TextSelectAllMouseListener extends MouseAdapter {

	private Text widget;
	
	/**
	 * 
	 * @param widget the widget the listener is attached to
	 */
	public TextSelectAllMouseListener(Text widget) {
		this.widget = widget;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void mouseDown(MouseEvent e) {
		this.widget.selectAll();
		super.mouseDown(e);
	}
}