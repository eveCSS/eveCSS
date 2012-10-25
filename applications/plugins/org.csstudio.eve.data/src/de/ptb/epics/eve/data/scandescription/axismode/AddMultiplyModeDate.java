package de.ptb.epics.eve.data.scandescription.axismode;

import java.util.Calendar;
import java.util.Date;

import org.apache.log4j.Logger;

import de.ptb.epics.eve.data.scandescription.Axis;
import de.ptb.epics.eve.util.math.Sequence;

/**
 * @author Marcus Michalsky
 * @since 1.7
 */
public class AddMultiplyModeDate extends AddMultiplyMode<Date> {

	private static final Logger LOGGER = Logger
			.getLogger(AddMultiplyModeDate.class.getName());
	
	protected AddMultiplyModeDate(Axis axis) {
		super(axis);
		// initialize with default values
		Calendar now = Calendar.getInstance();
		now.set(Calendar.MINUTE, now.get(Calendar.MINUTE) + 1);
		now.set(Calendar.SECOND, 0);
		now.set(Calendar.MILLISECOND, 0);
		this.start = now.getTime();
		now.add(Calendar.HOUR, 1);
		this.stop = now.getTime();
		now.set(Calendar.DAY_OF_MONTH, 1);
		now.set(Calendar.MONTH, 0);
		now.set(Calendar.YEAR, 1970);
		now.set(Calendar.AM_PM, Calendar.AM);
		now.set(Calendar.HOUR, 0);
		now.set(Calendar.MINUTE, 1);
		this.stepwidth = now.getTime();
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