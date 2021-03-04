package de.ptb.epics.eve.editor.views.chainview;

import org.apache.log4j.Logger;

import de.ptb.epics.eve.data.measuringstation.AbstractDevice;

/**
 * @author Marcus Michalsky
 * @since 1.36
 */
public class ContinueLimitTextCellEditorValidator extends LimitTextCellEditorValidator {
	private static final Logger LOGGER = Logger.getLogger(
			ContinueLimitTextCellEditorValidator.class.getName());
	
	public ContinueLimitTextCellEditorValidator(AbstractDevice device) {
		super(device);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String isValid(Object value) {
		if (value == null) {
			LOGGER.debug("null is valid. (no continue limit set)");
			return null;
		}
		if (value.toString().isEmpty()) {
			LOGGER.debug("empty string is valid --> delete existing entry");
			return null;
		}
		return super.isValid(value);
	}
}
