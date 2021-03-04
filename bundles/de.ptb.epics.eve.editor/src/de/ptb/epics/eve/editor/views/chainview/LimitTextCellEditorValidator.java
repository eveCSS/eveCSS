package de.ptb.epics.eve.editor.views.chainview;

import org.apache.log4j.Logger;
import org.eclipse.jface.viewers.ICellEditorValidator;

import de.ptb.epics.eve.data.DataTypes;
import de.ptb.epics.eve.data.measuringstation.AbstractDevice;
import de.ptb.epics.eve.data.measuringstation.AbstractPrePostscanDevice;
import de.ptb.epics.eve.data.measuringstation.DetectorChannel;
import de.ptb.epics.eve.data.measuringstation.MotorAxis;

/**
 * @author Marcus Michalsky
 * @since 1.36
 */
public class LimitTextCellEditorValidator implements ICellEditorValidator {
	private static final Logger LOGGER = Logger.getLogger(
			LimitTextCellEditorValidator.class.getName());

	private AbstractDevice device;
	
	public LimitTextCellEditorValidator(AbstractDevice device) {
		this.device = device;
	}
	
	private DataTypes getType() {
		if (device instanceof MotorAxis) {
			return ((MotorAxis)device).getType();
		} else if (device instanceof DetectorChannel) {
			return ((DetectorChannel)device).getRead().getType();
		} else if (device instanceof AbstractPrePostscanDevice) {
			return ((AbstractPrePostscanDevice)device).getValue().getType();
		}
		return DataTypes.STRING;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String isValid(Object value) {
		switch (this.getType()) {
		case DATETIME:
		case ONOFF:
		case OPENCLOSE:
			LOGGER.debug("datetime, onoff, openclose are not supported");
			return "invalid type";
		case DOUBLE:
			try {
				Double.parseDouble(value.toString());
				LOGGER.debug("successfully validated double");
			} catch (NumberFormatException e) {
				LOGGER.debug(e.getMessage());
				return "Input is not a valid double.";
			}
			break;
		case INT:
			try {
				Integer.parseInt(value.toString());
				LOGGER.debug("successfully validated integer");
			} catch (NumberFormatException e) {
				LOGGER.debug(e.getMessage());
				return "Input is not a valid integer.";
			}
			break;
		case STRING:
			LOGGER.debug("(non-discrete) string is always valid");
			// strings are free text and always valid
			break;
		default:
			break;
		}
		return null;
	}
}
