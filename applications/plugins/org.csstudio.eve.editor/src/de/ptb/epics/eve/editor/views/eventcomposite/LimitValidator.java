package de.ptb.epics.eve.editor.views.eventcomposite;

import org.apache.log4j.Logger;
import org.eclipse.jface.viewers.ICellEditorValidator;

import de.ptb.epics.eve.data.scandescription.ControlEvent;

/**
 * 
 * 
 * @author Marcus Michalsky
 * @since 1.1
 */
public class LimitValidator implements ICellEditorValidator {

	private static Logger logger = 
			Logger.getLogger(LimitValidator.class.getName());
	
	private ControlEvent ce;
	
	/**
	 * Constructor.
	 * 
	 * @param ce the control event of Limit
	 */
	public LimitValidator(ControlEvent ce) {
		this.ce = ce;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String isValid(Object value) {
		logger.debug(value);
		String input = (String)value;
		if(ce.getEvent().getMonitor().getAccess().isValuePossible(input)) {
			return null;
		} 
		return "value not possible";
	}
}