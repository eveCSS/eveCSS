package de.ptb.epics.eve.util.swt;

import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.widgets.Text;

/**
 * @author Marcus Michalsky
 * @since 1.7
 */
public class DoubleVerifyListener extends TextVerifyListener {

	/**
	 * @param textField the text field to be verified
	 */
	public DoubleVerifyListener(Text textField) {
		super(textField);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void verifyText(VerifyEvent e) {
		super.verifyText(e);
		if (e.doit) {
			return;
		}
		e.doit = true;
		String oldText = ((Text) (e.widget)).getText();
		if (!Character.isDigit(e.character)) {
			if (e.character == '.') {
				// character . is a valid character, if he is not in the
				// old string
				if (oldText.contains("."))
					e.doit = false;
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
					if (oldText.substring(index - 1).equals("e")) {
						// letzte Zeichen ist ein e und damit erlaubt
					} else
						e.doit = false;
				}
			} else if (e.character == 'e') {
				// character e is a valid character, if he is not in the
				// old string
				if (oldText.contains("e"))
					e.doit = false;
			} else {
				e.doit = false; // disallow the action
			}
		}
		// double regexp ^\d*\.?\d*$
	}
}
