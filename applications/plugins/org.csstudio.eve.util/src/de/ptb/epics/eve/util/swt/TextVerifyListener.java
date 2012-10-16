package de.ptb.epics.eve.util.swt;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.widgets.Text;

/**
 * A verify listener for text field inputs allowing characters used for 
 * navigating through the input (e.g. arrow left/right, backspace, etc.).
 * 
 * @author Marcus Michalsky
 * @since 1.7
 */
public class TextVerifyListener implements VerifyListener {
	protected Text textField;

	/**
	 * @param textField the text field to be verified
	 */
	public TextVerifyListener(Text textField) {
		this.textField = textField;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void verifyText(VerifyEvent e) {
		switch (e.keyCode) {
		case SWT.BS: 			// Backspace
		case SWT.DEL: 			// Delete
		case SWT.HOME: 			// Home
		case SWT.END: 			// End
		case SWT.ARROW_LEFT: 	// Left arrow
		case SWT.ARROW_RIGHT: 	// Right arrow
			return;
		}
		e.doit = false;
	}
}