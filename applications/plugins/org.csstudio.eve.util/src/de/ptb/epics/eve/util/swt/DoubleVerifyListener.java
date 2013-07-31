package de.ptb.epics.eve.util.swt;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.widgets.Text;

/**
 * @author Hartmut Scherr
 * @author Marcus Michalsky
 * @since 1.7
 */
public class DoubleVerifyListener implements VerifyListener {
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void verifyText(VerifyEvent e) {
		if (e.doit == false) {
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
		
		String oldText = ((Text) (e.widget)).getText();
		if (!Character.isDigit(e.character)) {
			if (e.character == '.') {
				// character . is a valid character, if he is not in the
				// old string
				if (oldText.contains(".")) {
					e.doit = false;
				}
			} else if (e.character == '-') {
				// character - is a valid character as first sign and
				// after an e
				if (oldText.isEmpty()) {
					// oldText is empty, - is valid
				} else if ((((Text) e.widget).getSelection().x) == 0) {
					// - is the first sign an valid
				} else {
					// wenn das letzte Zeichen von oldText ein e ist,
					// ist das minus auch erlaubt
					int index = oldText.length();
					if (oldText.substring(index - 1).equals("e") ||
							oldText.substring(index - 1).equals("E")) {
						// letzte Zeichen ist ein e und damit erlaubt
					} else
						e.doit = false;
				}
			} else if (e.character == 'e' || e.character == 'E') {
				// character e/E is a valid character, if he is not in the
				// old string
				if (oldText.contains("e") || oldText.contains("E")) {
					e.doit = false;
				}
			} else {
				e.doit = false; // disallow the action
			}
		}
	}
}