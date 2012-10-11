package de.ptb.epics.eve.data.scandescription.axismode;

import java.util.Date;

import de.ptb.epics.eve.data.scandescription.Axis;

/**
 * @author Marcus Michalsky
 * @since 1.7
 */
public class AddMultiplyModeDate extends AddMultiplyMode<Date> {

	protected AddMultiplyModeDate(Axis axis) {
		super(axis);
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
	protected void adjust() {
		boolean autoAdjustValue = this.isAutoAdjust();
		this.setAutoAdjust(false);
		// TODO Auto-generated method stub
		
		this.setAutoAdjust(autoAdjustValue);
	}
}