package de.ptb.epics.eve.editor.views.axeschannelsview.classiccomposite.axes.addmultiply.intdouble;

import org.eclipse.jface.viewers.ICellEditorValidator;

/**
 * @author Marcus Michalsky
 * @since 1.34
 */
public class AddIntDoubleTextValidator implements ICellEditorValidator {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String isValid(Object value) {
		String s = (String)value;
		if (s.isEmpty()) {
			return "EMPTY !";
		}
		// TODO Auto-generated method stub
		return null;
	}
}
