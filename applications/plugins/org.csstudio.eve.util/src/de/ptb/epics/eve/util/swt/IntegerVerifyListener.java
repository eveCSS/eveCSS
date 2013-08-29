package de.ptb.epics.eve.util.swt;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.widgets.Text;

/**
 * @author Marcus Michalsky
 * @since 1.7
 */
public class IntegerVerifyListener implements VerifyListener {
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void verifyText(VerifyEvent e) {
		if (!e.doit) {
			return;
		}

		switch (e.keyCode) {
		case SWT.BS: 			// Backspace
		case SWT.DEL: 			// Delete
		case SWT.HOME:			// Home
		case SWT.END: 			// End
		case SWT.ARROW_LEFT: 	// Left arrow
		case SWT.ARROW_RIGHT: 	// Right arrow
			return;
		}

		try {
			Integer.parseInt(((Text) e.widget).getText() + 
					String.valueOf(e.character));
		} catch (NumberFormatException e1) {
			// result is no Integer
			e.doit = false;
		}
	}
}