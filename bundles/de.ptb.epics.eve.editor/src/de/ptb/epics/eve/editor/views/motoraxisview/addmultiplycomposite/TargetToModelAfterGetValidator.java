package de.ptb.epics.eve.editor.views.motoraxisview.addmultiplycomposite;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;

import org.apache.log4j.Logger;
import org.eclipse.core.databinding.validation.IValidator;
import org.eclipse.core.databinding.validation.ValidationStatus;
import org.eclipse.core.runtime.IStatus;

import de.ptb.epics.eve.data.DataTypes;
import de.ptb.epics.eve.data.scandescription.Axis;
import de.ptb.epics.eve.util.math.Constants;

/**
 * @author Marcus Michalsky
 * @since 1.7
 */
public class TargetToModelAfterGetValidator implements IValidator {

	private static final Logger LOGGER = Logger
			.getLogger(TargetToModelAfterGetValidator.class.getName());
	
	private DataTypes type;
	private Axis axis;
	
	/**
	 * @param type the type
	 * @param axis the axis
	 */
	public TargetToModelAfterGetValidator(DataTypes type, Axis axis) {
		this.type = type;
		this.axis = axis;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public IStatus validate(Object value) {
		if (value instanceof Double) {
			return ValidationStatus.ok();
		}
		switch (this.type) {
		case DOUBLE:
			if (!((String)value).matches(Constants.FLOATING_POINT_REGEXP)) {
				LOGGER.debug("error validating target to model " + "(" + type
						+ ")");
				return ValidationStatus.error("cannot parse double");
			}
			break;
		case INT:
			try {
				Integer.parseInt((String)value);
			} catch (Exception e) {
				LOGGER.debug("error validating target to model " + "(" + type
						+ ")");
				return ValidationStatus.error("cannot parse int");
			}
			break;
		case DATETIME:
			switch (this.axis.getPositionMode()) {
			case ABSOLUTE:
				try {
					new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS")
							.parse((String) value);
				} catch (ParseException e) {
					try {
						new SimpleDateFormat("HH:mm:ss.SSS")
						.parse((String) value);
					} catch (ParseException e1) {
						LOGGER.debug("error validating target to model " + 
								"(" + type + "): " + e1.getMessage());
						return ValidationStatus.error(e1.getMessage());
					}
				}
				break;
			case RELATIVE:
				try {
					DatatypeFactory factory = DatatypeFactory.newInstance();
					factory.newDuration("P0Y0M0DT" + (String)value);
				} catch(IllegalArgumentException e) {
					return ValidationStatus.error("Invalid duration");
				} catch (DatatypeConfigurationException e) {
					LOGGER.error(e.getMessage(), e);
					return ValidationStatus.error(e.getMessage());
				}
				break;
			}
			break;
		default:
			return ValidationStatus.error("type is neither double nor int");
		}
		LOGGER.debug("after get validation ok");
		return ValidationStatus.ok();
	}
}