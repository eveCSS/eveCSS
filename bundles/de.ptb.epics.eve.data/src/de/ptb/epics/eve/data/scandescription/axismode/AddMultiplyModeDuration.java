package de.ptb.epics.eve.data.scandescription.axismode;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.Duration;

import org.apache.log4j.Logger;

import de.ptb.epics.eve.data.scandescription.Axis;
import de.ptb.epics.eve.util.math.Sequence;

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
		
		if (axis.getScanModule() != null) {
			this.matchMainAxis(axis.getScanModule().getMainAxis());
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
			this.setStart(Sequence.getStart(this.stop, this.stepwidth,
					this.stepcount));
			break;
		case STEPCOUNT:
			this.setStepcount(Sequence.getStepcount(this.start, this.stop,
					this.stepwidth));
			break;
		case STEPWIDTH:
			this.setStepwidth(Sequence.getStepwidth(this.start, this.stop,
					this.stepcount));
			break;
		case STOP:
			this.setStop(Sequence.getStop(this.start, this.stepwidth,
					this.stepcount));
			break;
		}
		this.setAutoAdjust(autoAdjustValue);
	}
}