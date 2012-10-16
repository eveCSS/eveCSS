package de.ptb.epics.eve.util.swt;

import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.widgets.Text;

/**
 * @author Marcus Michalsky
 * @since 1.7
 */
public class IntegerVerifyListener extends TextVerifyListener {

	/**
	 * @param textField the text field to be verified
	 */
	public IntegerVerifyListener(Text textField) {
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
		try {
			Integer.parseInt(textField.getText() + String.valueOf(e.character));
		} catch (NumberFormatException e1) {
			// result is no Integer
			e.doit = false;
			return;
		}
	}
}