package de.ptb.epics.eve.util.swt;

import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.widgets.Text;

/**
 * {@link org.eclipse.swt.events.VerifyListener} accepting only positive 
 * integer values.
 * 
 * @author Marcus Michalsky
 * @since 1.5
 */
public class PositiveIntegerVerifyListener extends IntegerVerifyListener {

	/**
	 * Constructor.
	 * 
	 * @param textField the {@link org.eclipse.swt.widgets.Text} field the 
	 * 		listener is added to
	 */
	public PositiveIntegerVerifyListener(Text textField) {
		super(textField);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void verifyText(VerifyEvent e) {
		super.verifyText(e);
		if (Integer.parseInt(textField.getText() + String.valueOf(e.character)) < 1) {
			// result is not positive
			e.doit = false;
			return;
		}
	}
}