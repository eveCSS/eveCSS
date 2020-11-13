package de.ptb.epics.eve.editor.views.prepostposplotview.classiccomposite.prepostscan.ui;

import org.eclipse.jface.viewers.ICellEditorValidator;

import de.ptb.epics.eve.data.measuringstation.AbstractPrePostscanDevice;

/**
 * @author Marcus Michalsky
 * @since 1.35
 */
public class PrePostscanTextCellEditorValidator implements ICellEditorValidator {
	private AbstractPrePostscanDevice device;
	
	public PrePostscanTextCellEditorValidator(AbstractPrePostscanDevice device) {
		super();
		this.device = device;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String isValid(Object value) {
		if (value.toString().isEmpty()) {
			// empty string is valid (i.e. existing entry should be removed)
			return null;
		}
		switch (device.getValue().getType()) {
		case DATETIME:
			break;
		case DOUBLE:
			try {
				Double.parseDouble(value.toString());
			} catch (NumberFormatException e) {
				return "Input is not a valid double.";
			}
			break;
		case INT:
			try {
				Integer.parseInt(value.toString());
			} catch (NumberFormatException e) {
				return "Input is not a valid integer.";
			}
			break;
		case ONOFF:
			// ??
			break;
		case OPENCLOSE:
			// ??
			break;
		case STRING:
			// strings for non-discrete values are always valid
			break;
		default:
			break;
		}
		return null;
	}
}
