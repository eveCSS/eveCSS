package de.ptb.epics.eve.data.measuringstation;

import java.beans.PropertyChangeListener;

import org.apache.log4j.Logger;

import de.ptb.epics.eve.data.DataTypes;
import de.ptb.epics.eve.data.measuringstation.exceptions.ParentNotAllowedException;

/**
 * This class represents a motor axis at a measuring station.
 * 
 * @author Stephan Rehfeld <stephan.rehfeld( -at -) ptb.de>
 * @author Marcus Michalsky
 * @author Hartmut Scherr
 */
public class MotorAxis extends AbstractMainPhaseDevice implements Cloneable {

	private static Logger logger = Logger.getLogger(MotorAxis.class.getName());
	
	// the position (read back value)
	private Function position;

	// the status
	private Function status;

	// move done flag
	private Function moveDone;

	// the goto of the motor axis
	private Function goTo;

	// the stop trigger
	private Function stop;

	// accepted deviation from target position
	private Function deadband;

	// offset between user and system coordinates
	private Function offset;

	// value used for tweak forward / reverse
	private Function tweakValue;

	private Function tweakForward;
	private Function tweakReverse;

	private Function softHighLimit;
	private Function softLowLimit;

	// channel access work is delegated
	private MotorAxisChannelAccess channelAccess;
	private int connections;
	
	/**
	 * Constructor.
	 */
	public MotorAxis() {
		this(new Function(), null, null, new Function(), new Function());
		this.channelAccess = new MotorAxisChannelAccess(this);
	}

	/**
	 * Constructs a <code>MotorAxis</code>
	 * 
	 * @param position
	 *            The position <code>Function</code> of the motor axis.
	 * @param status
	 *            The status <code>Function</code> of the motor axis.
	 * @param moveDone
	 *            moveDone PV
	 * @param gotoAdvisor
	 *            The goto of the motor axis.
	 * @param stop
	 *            the stop trigger of the motor axis.
	 * @throws IllegalArgumentException
	 *             if position, gotoAdvisor or stop is <code>null</code>
	 */
	public MotorAxis(final Function position, final Function status,
			final Function moveDone, final Function gotoAdvisor,
			final Function stop) {
		if (position == null) {
			throw new IllegalArgumentException(
					"The parameter 'position' must not be null!");
		}
		if (gotoAdvisor == null) {
			throw new IllegalArgumentException(
					"The parameter 'gotoAdvisor' must not be null!");
		}
		if (stop == null) {
			throw new IllegalArgumentException(
					"The parameter 'stop' must not be null!");
		}
		this.position = position;
		this.status = status;
		this.moveDone = moveDone;
		this.goTo = gotoAdvisor;
		this.stop = stop;
		this.channelAccess = new MotorAxisChannelAccess(this);
		this.connections = 0;
	}

	/**
	 * Returns the position <code>Function</code> of the motor axis.
	 * 
	 * @return the position <code>Function</code>
	 */
	public Function getPosition() {
		return this.position;
	}

	/**
	 * Returns the status <code>Function</code> of the motor axis.
	 * 
	 * @return the status <code>Function</code> or <code>null</code> if it's not
	 *         set.
	 */
	public Function getStatus() {
		return this.status;
	}

	/**
	 * Returns the moveDone
	 * {@link de.ptb.epics.eve.data.measuringstation.Function}.
	 * 
	 * @return the moveDone
	 *         {@link de.ptb.epics.eve.data.measuringstation.Function}
	 */
	public Function getMoveDone() {
		return this.moveDone;
	}

	/**
	 * Returns the goto <code>Function</code> of the motor axis.
	 * 
	 * @return the goto <code>Function</code> or <code>null</code> if it's not
	 *         set.
	 */
	public Function getGoto() {
		return this.goTo;
	}

	/**
	 * Returns the stop <code>Function</code> of the motor axis.
	 * 
	 * @return the stop <code>Function</code> or <code>null</code> if it's not
	 *         set.
	 */
	public Function getStop() {
		return this.stop;
	}

	/**
	 * Sets the goto <code>Function</code> of the motor axis.
	 * 
	 * @param gotoAdvisor
	 *            the goto <code>Function</code> that should be set.
	 * @throws IllegalArgumentException
	 *             if the argument is <code>null</code>
	 */
	public void setGoto(final Function gotoAdvisor) {
		if (gotoAdvisor == null) {
			throw new IllegalArgumentException(
					"The parameter 'gotoAdvisor' must not be null!");
		}
		this.goTo = gotoAdvisor;
	}

	/**
	 * This method sets the position <code>Function</code> of the motor axis.
	 * 
	 * @param position
	 *            the position <code>Function</code> that should be set.
	 * @throws IllegalArgumentException
	 *             if the argument is <code>null</code>
	 */
	public void setPosition(final Function position) {
		if (position == null) {
			throw new IllegalArgumentException(
					"The parameter 'position' must not be null!");
		}
		this.position = position;
	}

	/**
	 * Sets the status <code>Function</code> of the motor axis.
	 * 
	 * @param status
	 *            the status <code>Function</code> that should be set.
	 */
	public void setStatus(final Function status) {
		this.status = status;
	}

	/**
	 * Sets the moveDone {@link de.ptb.epics.eve.data.measuringstation.Function}
	 * .
	 * 
	 * @param moveDone
	 *            the moveDone
	 *            {@link de.ptb.epics.eve.data.measuringstation.Function} that
	 *            should be set
	 */
	public void setMoveDone(final Function moveDone) {
		this.moveDone = moveDone;
	}

	/**
	 * Sets the stop <code>Function</code> of the motor axis.
	 * 
	 * @param stop
	 *            the stop <code>Function</code> that should be set.
	 * @throws IllegalArgumentException
	 *             if the argument is <code>null</code>
	 */
	public void setStop(final Function stop) {
		if (stop == null) {
			throw new IllegalArgumentException(
					"The parameter 'stop' must not be null!");
		}
		this.stop = stop;
	}

	/**
	 * Returns the parent as motor.
	 * 
	 * @return a Motor or null.
	 */
	public Motor getMotor() {
		return (Motor) this.getParent();
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @throws ParentNotAllowedException
	 *             if parent Device incompatible
	 */
	@Override
	protected void setParent(final AbstractDevice parent)
			throws ParentNotAllowedException {
		if (parent != null) {
			if (!(parent instanceof Motor)) {
				throw new ParentNotAllowedException("Your class is directly "
						+ "or indirectly inhereting from "
						+ "AbstractMainPhaseDevice. The parent of an "
						+ "AbstractMainPhaseDevice can only be an "
						+ "AbstractDevice. Please fix your "
						+ "implementation to check this constraint!");
			}
		}
		super.setParent(parent);
	}

	/**
	 * Checks if a value is possible for this MotorAxis object.
	 * 
	 * @param value
	 *            the value that should be checked.
	 * @return <code>true</code> if the value is valid, <code>false</code>
	 *         otherwise
	 * @throws IllegalArgumentException
	 *             if the argument is <code>null</code>
	 */
	public boolean isValuePossible(final String value) {
		if (value == null) {
			throw new IllegalArgumentException(
					"The parameter value must not be null");
		}

		if (value.equals("Undefined")) {
			return false;
		}

		if (this.goTo != null) {
			return this.goTo.isValuePossible(value);
		}
		return this.position.isValuePossible(value);
	}

	/**
	 * Returns a <code>String</code> with a valid value for the data type. If
	 * the value can not be converted, return a default value.
	 * 
	 * @param value
	 *            the value that should be formatted
	 * @return a <code>String</code> with a valid value
	 */
	public String formatValueDefault(final String value) {
		if (this.goTo != null) {
			return this.goTo.formatValueDefault(value);
		}
		return this.position.formatValueDefault(value);
	}

	/**
	 * Returns a <code>String</code> with a valid value for the data type. If
	 * the value cannot be converted, return null.
	 * 
	 * @param value
	 *            the value that will be formatted.
	 * @return a <code>String</code> with a valid value or <code>null</code> if
	 *         the value isn't convertible
	 */
	public String formatValue(final String value) {
		if (this.goTo != null) {
			return this.goTo.formatValue(value);
		}
		return this.position.formatValue(value);
	}

	/**
	 * Returns a <code>String</code> with a valid value for the data type. If
	 * the value can not be converted, return a default value.
	 * 
	 * @return a <code>String</code> with a valid default value
	 */
	public String getDefaultValue() {
		if (this.goTo != null) {
			return this.goTo.getDefaultValue();
		}
		return this.position.getDefaultValue();
	}

	/**
	 * Returns the data type of the goto <code>Access</code> or the position
	 * <code>Access</code>, if there isn't a goto <code>Access</code>.
	 * 
	 * @return the data type of this axis
	 */
	public DataTypes getType() {
		if (this.goTo != null) {
			return this.goTo.getType();
		}
		return this.position.getType();
	}

	/**
	 * Sets the <code>Function</code> for the offset.
	 * 
	 * @param offset
	 *            the <code>Function</code> for the offset
	 */
	public void setOffset(final Function offset) {
		this.offset = offset;
	}

	/**
	 * Returns the Function for the offset
	 * 
	 * @return The Function for the offset.
	 */
	public Function getOffset() {
		return this.offset;
	}

	/**
	 * Sets the Function for the dead band
	 * 
	 * @param deadband
	 *            The Function for the dead band.
	 */
	public void setDeadband(final Function deadband) {
		this.deadband = deadband;
	}

	/**
	 * Returns the Function for the dead band
	 * 
	 * @return The Function for the dead band.
	 */
	public Function getDeadband() {
		return this.deadband;
	}

	/**
	 * Sets the Function for the tweakValue
	 * 
	 * @param tweakValue
	 *            The Function for the tweakValue.
	 */
	public void setTweakValue(final Function tweakValue) {
		this.tweakValue = tweakValue;
	}

	/**
	 * Returns the Function for the tweakValue
	 * 
	 * @return The Function for the tweakValue.
	 */
	public Function getTweakValue() {
		return this.tweakValue;
	}

	/**
	 * Sets the Function for the tweakForward
	 * 
	 * @param tweakForward
	 *            The Function for the tweakForward.
	 */
	public void setTweakForward(final Function tweakForward) {
		this.tweakForward = tweakForward;
	}

	/**
	 * Returns the Function for the tweakForward
	 * 
	 * @return The Function for the tweakForward.
	 */
	public Function getTweakForward() {
		return this.tweakForward;
	}

	/**
	 * Sets the Function for the tweakReverse
	 * 
	 * @param tweakReverse
	 *            The Function for the tweakReverse.
	 */
	public void setTweakReverse(final Function tweakReverse) {
		this.tweakReverse = tweakReverse;
	}

	/**
	 * Returns the Function for the tweakReverse
	 * 
	 * @return The Function for the tweakReverse.
	 */
	public Function getTweakReverse() {
		return this.tweakReverse;
	}

	/**
	 * @return the softHighLimit
	 */
	public Function getSoftHighLimit() {
		return softHighLimit;
	}

	/**
	 * @param softHighLimit the softHighLimit to set
	 */
	public void setSoftHighLimit(Function softHighLimit) {
		this.softHighLimit = softHighLimit;
	}

	/**
	 * @return the softLowLimit
	 */
	public Function getSoftLowLimit() {
		return softLowLimit;
	}

	/**
	 * @param softLowLimit the softLowLimit to set
	 */
	public void setSoftLowLimit(Function softLowLimit) {
		this.softLowLimit = softLowLimit;
	}

	/**
	 * Connects the motor axis via channel access
	 */
	public void connect() {
		this.channelAccess.connect();
		connections++;
		logger.debug("connected (" + connections + " connections)");
	}
	
	/**
	 * Disconnects the motor axis from channel access
	 */
	public void disconnect() {
		if(--connections != 0) {
			logger.debug("no disconnect (" + connections + " still open)");
			return;
		}
		this.channelAccess.disconnect();
		logger.debug("disconnected");
	}

	/**
	 * Returns the channel access delegate or <code>null</code> if not 
	 * connected.
	 * 
	 * @return the channel access delegate or <code>null</code> if not 
	 * 			connected
	 */
	public MotorAxisChannelAccess getChannelAccess() {
		if (this.channelAccess.isConncted()) {
			return this.channelAccess;
		}
		return null;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!super.equals(obj)) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		return true;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Object clone() {
		final MotorAxis motorAxis = new MotorAxis();
		motorAxis.position = (Function) (this.position != null ? this.position
				.clone() : null);
		motorAxis.goTo = (Function) (this.goTo != null ? this.goTo.clone()
				: null);
		motorAxis.stop = (Function) (this.stop != null ? this.stop.clone()
				: null);
		motorAxis.deadband = (Function) (this.deadband != null ? this.deadband
				.clone() : null);
		motorAxis.offset = (Function) (this.offset != null ? this.offset
				.clone() : null);
		motorAxis.tweakValue = (Function) (this.tweakValue != null ? this.tweakValue
				.clone() : null);
		motorAxis.tweakForward = (Function) (this.tweakForward != null ? this.tweakForward
				.clone() : null);
		motorAxis.tweakReverse = (Function) (this.tweakReverse != null ? this.tweakReverse
				.clone() : null);
		motorAxis.softHighLimit = (Function) (this.softHighLimit != null ? this.softHighLimit
				.clone() : null);
		motorAxis.softLowLimit = (Function) (this.softLowLimit != null ? this.softLowLimit
				.clone() : null);
		motorAxis.setClassName(this.getClassName());
		motorAxis.setTrigger((Function) (this.getTrigger() != null ? this
				.getTrigger().clone() : null));
		motorAxis.setName(this.getName());
		motorAxis.setId(this.getID());
		motorAxis.setUnit((Unit) (this.getUnit() != null ? this.getUnit()
				.clone() : null));
		for (final Option option : this.getOptions()) {
			motorAxis.add((Option) option.clone());
		}
		return motorAxis;
	}
	
	/**
	 * 
	 * @param propertyName
	 * @param listener
	 */
	public void addPropertyChangeListener(String propertyName,
			PropertyChangeListener listener) {
		this.channelAccess.addPropertyChangeListener(propertyName, listener);
	}
	
	/**
	 * 
	 * @param propertyName
	 * @param listener
	 */
	public void removePropertyChangeListener(String propertyName, 
			PropertyChangeListener listener) {
		this.channelAccess.removePropertyChangeListener(propertyName, listener);
	}
}