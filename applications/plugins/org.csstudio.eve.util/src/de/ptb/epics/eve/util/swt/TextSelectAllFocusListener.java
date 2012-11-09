package de.ptb.epics.eve.util.swt;

import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.widgets.Text;

/**
 * {@link org.eclipse.swt.events.FocusListener} for a text widget selecting 
 * all text when focus is gained.
 * 
 * @author Marcus Michalsky
 * @since 1.8
 */
public class TextSelectAllFocusListener extends FocusAdapter {
	
	private Text widget;
	
	/**
	 * @param widget the widget the listener is attached to
	 */
	public TextSelectAllFocusListener(Text widget) {
		this.widget = widget;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void focusGained(FocusEvent e) {
		this.widget.selectAll();
		super.focusGained(e);
	}
}