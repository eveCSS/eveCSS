package de.ptb.epics.eve.data.scandescription.axismode;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.Duration;

import org.apache.log4j.Logger;

import de.ptb.epics.eve.data.scandescription.Axis;

/**
 * @author Marcus Michalsky
 * @since 1.7
 */
public class AddMultiplyModeDuration extends AddMultiplyMode<Duration> {

	private static final Logger LOGGER = Logger
			.getLogger(AddMultiplyModeDuration.class.getName());
	
	protected AddMultiplyModeDuration(Axis axis) {
		super(axis);
		// initializing
		DatatypeFactory factory;
		try {
			factory = DatatypeFactory.newInstance();
			this.start = factory.newDuration(0);
			this.stop = factory.newDuration(3600000);
			this.stepwidth = factory.newDuration(60000);
		} catch (DatatypeConfigurationException e) {
			LOGGER.error(e.getMessage(), e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void adjust() {
		boolean autoAdjustValue = this.isAutoAdjust();
		this.setAutoAdjust(false);
		LOGGER.debug("adjusting");
		switch (this.getAdjustParameter()) {
		case START:
			// TODO
			break;
		case STEPCOUNT:
			// TODO
			break;
		case STEPWIDTH:
			// TODO
			break;
		case STOP:
			// TODO
			break;
		}
		this.setAutoAdjust(autoAdjustValue);
	}
}