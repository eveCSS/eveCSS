package de.ptb.epics.eve.data.scandescription.axismode;

import org.apache.log4j.Logger;

import de.ptb.epics.eve.data.scandescription.Axis;
import de.ptb.epics.eve.util.math.Sequence;

/**
 * @author Marcus Michalsky
 * @since 1.7
 */
public class AddMultiplyModeDouble extends AddMultiplyMode<Double> {

	private static final Logger LOGGER = Logger
			.getLogger(AddMultiplyModeDouble.class.getName());
	
	protected AddMultiplyModeDouble(Axis axis) {
		super(axis);
		this.start = new Double(axis.getDefaultValue());
		this.stop = new Double(axis.getDefaultValue());
		this.stepwidth = new Double(axis.getDefaultValue());
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void matchMainAxis(Axis mainAxis) {
		// TODO Auto-generated method stub
		
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected synchronized void adjust() {
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