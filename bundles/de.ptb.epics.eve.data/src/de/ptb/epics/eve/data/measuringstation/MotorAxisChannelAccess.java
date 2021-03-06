package de.ptb.epics.eve.data.measuringstation;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

import org.apache.log4j.Logger;

import de.ptb.epics.eve.util.pv.PVWrapper;

/**
 * Delegate of {@link de.ptb.epics.eve.data.measuringstation.MotorAxis} doing 
 * channel access work.
 * 
 * @author Marcus Michalsky
 * @since 1.5
 */
public class MotorAxisChannelAccess implements PropertyChangeListener {
	private static Logger logger = Logger
			.getLogger(MotorAxisChannelAccess.class.getName());
	
	private MotorAxis motorAxis;
	
	private PVWrapper position;
	private PVWrapper goTo;
	private PVWrapper offset;
	private PVWrapper highLimit;
	private PVWrapper lowLimit;
	
	private PropertyChangeSupport propertyChangeSupport;
	
	private boolean isConnected;
	
	/**
	 * Constructor.
	 * 
	 * @param motorAxis the motor axis work is delegated from
	 */
	protected MotorAxisChannelAccess(MotorAxis motorAxis) {
		this.motorAxis = motorAxis;
		this.propertyChangeSupport = new PropertyChangeSupport(this.motorAxis);
		this.position = null;
		this.goTo = null;
		this.offset = null;
		this.highLimit = null;
		this.lowLimit = null;
		this.isConnected = false;
	}
	
	/**
	 * 
	 */
	protected void connect() {
		if (this.isConnected) {
			logger.debug(this.motorAxis.getName() + " already connected");
			return;
		}
		this.position = new PVWrapper(this.motorAxis.getPosition().getAccess().
				getVariableID());
		this.position.addPropertyChangeListener(PVWrapper.VALUE, this);
		this.goTo = new PVWrapper(this.motorAxis.getGoto().getAccess()
				.getVariableID());
		this.goTo.addPropertyChangeListener(PVWrapper.DISCRETE_VALUES, this);
		if (this.motorAxis.getOffset() != null) {
			this.offset = new PVWrapper(this.motorAxis.getOffset().getAccess()
					.getVariableID());
			this.offset.addPropertyChangeListener(PVWrapper.VALUE, this);
		}
		if (this.motorAxis.getSoftHighLimit() != null) {
			this.highLimit = new PVWrapper(this.motorAxis.getSoftHighLimit()
					.getAccess().getVariableID());
			this.highLimit.addPropertyChangeListener(PVWrapper.VALUE, this);
		}
		if (this.motorAxis.getSoftLowLimit() != null) {
			this.lowLimit = new PVWrapper(this.motorAxis.getSoftLowLimit()
					.getAccess().getVariableID());
			this.lowLimit.addPropertyChangeListener(PVWrapper.VALUE, this);
		}
		this.isConnected = true;
	}
	
	/**
	 * 
	 */
	protected void disconnect() {
		this.position.removePropertyChangeListener(this);
		this.position.disconnect();
		this.position = null;
		this.goTo.removePropertyChangeListener(this);
		this.goTo.disconnect();
		this.goTo = null;
		if (this.offset != null) {
			this.offset.removePropertyChangeListener(this);
			this.offset.disconnect();
			this.offset = null;
		}
		if (this.highLimit != null) {
			this.highLimit.removePropertyChangeListener(this);
			this.highLimit.disconnect();
			this.highLimit = null;
		}
		if (this.lowLimit != null) {
			this.lowLimit.removePropertyChangeListener(this);
			this.lowLimit.disconnect();
			this.lowLimit = null;
		}
		this.isConnected = false;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void propertyChange(PropertyChangeEvent e) {
		if (logger.isDebugEnabled()) {
			logger.debug("Property Change: " + 
					((PVWrapper)e.getSource()).getName() + " - "
					+ e.getOldValue() + " -> " + e.getNewValue());
		}
		if (e.getSource() == this.position) {
			this.propertyChangeSupport.firePropertyChange(
					MotorAxis.POSITION_PROP,
					e.getOldValue(), e.getNewValue());
		} else if (e.getSource() == this.goTo) {
			this.propertyChangeSupport.firePropertyChange(
					MotorAxis.DISCRETE_VALUES_PROP,
					null, e.getNewValue());
		} else if (e.getSource() == this.offset) {
			this.propertyChangeSupport.firePropertyChange(
					MotorAxis.OFFSET_PROP,
					e.getOldValue(), e.getNewValue());
		} else if (e.getSource() == this.highLimit) {
			this.propertyChangeSupport.firePropertyChange(
					MotorAxis.HIGH_LIMIT_PROP,
					e.getOldValue(), e.getNewValue());
		} else if (e.getSource() == this.lowLimit) {
			this.propertyChangeSupport.firePropertyChange(
					MotorAxis.LOW_LIMIT_PROP,
					e.getOldValue(), e.getNewValue());
		}
	}
	
	/**
	 * Returns whether the channel access connection is established.
	 * 
	 * @return <code>true</code> if channel access is available, 
	 * 			<code>false</code> otherwise
	 */
	public boolean isConncted() {
		return this.isConnected;
	}
	
	/**
	 * 
	 * @param propertyName
	 * @param listener
	 */
	protected void addPropertyChangeListener(String propertyName,
			PropertyChangeListener listener) {
		this.propertyChangeSupport.addPropertyChangeListener(propertyName,
				listener);
	}
	
	/**
	 * 
	 * @param propertyName
	 * @param listener
	 */
	protected void removePropertyChangeListener(String propertyName,
			PropertyChangeListener listener) {
		this.propertyChangeSupport.removePropertyChangeListener(propertyName,
				listener);
	}
	
	/**
	 * 
	 * @return
	 */
	public Double getPosition() {
		return this.position.getRawValue();
	}
	
	/**
	 * 
	 * @return
	 */
	public Double getHighLimit() {
		if (this.highLimit == null) {
			return null;
		}
		return this.highLimit.getRawValue();
	}
	
	/**
	 * 
	 * @return
	 */
	public Double getLowLimit() {
		if (this.lowLimit == null) {
			return null;
		}
		return this.lowLimit.getRawValue();
	}
	
	/**
	 * 
	 * @return
	 */
	public String[] getDiscretePositions() {
		return this.position.getDiscreteValues();
	}
}