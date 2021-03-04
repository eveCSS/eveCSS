package de.ptb.epics.eve.data.scandescription;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import de.ptb.epics.eve.data.ComparisonTypes;
import de.ptb.epics.eve.data.DataTypes;
import de.ptb.epics.eve.data.measuringstation.AbstractDevice;
import de.ptb.epics.eve.data.measuringstation.AbstractPrePostscanDevice;
import de.ptb.epics.eve.data.measuringstation.Detector;
import de.ptb.epics.eve.data.measuringstation.DetectorChannel;
import de.ptb.epics.eve.data.measuringstation.Device;
import de.ptb.epics.eve.data.measuringstation.Function;
import de.ptb.epics.eve.data.measuringstation.Motor;
import de.ptb.epics.eve.data.measuringstation.MotorAxis;
import de.ptb.epics.eve.data.measuringstation.Option;
import de.ptb.epics.eve.data.scandescription.updatenotification.IModelUpdateListener;
import de.ptb.epics.eve.data.scandescription.updatenotification.IModelUpdateProvider;
import de.ptb.epics.eve.data.scandescription.updatenotification.ModelUpdateEvent;

/**
 * @author Marcus Michalsky
 * @since 1.36
 */
public class PauseCondition implements IModelUpdateProvider {
	private static final String DELIMITER = " \u00BB ";
	
	private AbstractDevice device;
	private ComparisonTypes operator;
	private String pauseLimit;
	private String continueLimit;
	
	private List<IModelUpdateListener> updateListener;
	
	/**
	 * Constructs a pause condition with the given device.
	 * @param device the device the pause condition should be based upon 
	 * 		(must not be <code>null</code>)
	 */
	public PauseCondition(AbstractDevice device) {
		if (device == null) {
			throw new IllegalArgumentException("device is mandatory");
		}
		if (device instanceof Detector || device instanceof Motor) {
			throw new  IllegalArgumentException("device not supported");
		}
		this.device = device;
		this.operator = ComparisonTypes.EQ;
		
		this.pauseLimit = this.getValue().getDefaultValue();
		
		this.updateListener = new ArrayList<>();
	}

	/**
	 * @return the device
	 */
	public AbstractDevice getDevice() {
		return device;
	}
	
	/**
	 * Returns the function value.
	 * 
	 * helper: distinguishes device type and makes it transparent
	 */
	public Function getValue() {
		if (this.device instanceof MotorAxis) {
			return ((MotorAxis)device).getPosition();
		} else if (this.device instanceof DetectorChannel) {
			return ((DetectorChannel)device).getRead();
		}
		return ((AbstractPrePostscanDevice)device).getValue();
	}
	
	/**
	 * @return the data type
	 */
	public DataTypes getType() {
		return this.getValue().getType();
	}
	
	/**
	 * Returns whether the device has a discrete value space
	 * @return <code>true</code> if value space is discrete, 
	 * 		<code>false</code> otherwise
	 */
	public boolean isDiscrete() {
		return this.getValue().isDiscrete();
	}

	/**
	 * @return the operator
	 */
	public ComparisonTypes getOperator() {
		return operator;
	}

	/**
	 * State-testing method for {@link #setOperator(ComparisonTypes)}. Certain 
	 * operators are only valid for specific devices, i.e. only equality 
	 * comparisons are valid for discrete devices.
	 * @param operator the operator to check
	 * @return <code>true</code> if the given operator is valid, 
	 * 		<code>false</code> otherwise
	 */
	public boolean isValidOperator(ComparisonTypes operator) {
		if (this.isDiscrete()) {
			return (operator.equals(ComparisonTypes.EQ) || 
					operator.equals(ComparisonTypes.NE));
		}
		return true;
	}
	
	/**
	 * Returns a list of valid operators.
	 * @return a list of valid operators
	 */
	public List<ComparisonTypes> getValidOperators() {
		if (this.isDiscrete()) {
			return Arrays.asList(ComparisonTypes.EQ, ComparisonTypes.NE);
		}
		return Arrays.asList(ComparisonTypes.values());
	}
	
	/**
	 * @param operator the operator to set
	 * @throws IllegalArgumentException if given operator is invalid 
	 * 		(see also {@link #isValidOperator(ComparisonTypes)} 
	 */
	public void setOperator(ComparisonTypes operator) {
		if (!this.isValidOperator(operator)) {
			throw new IllegalArgumentException("Operator invalid");
		}
		this.operator = operator;
		// for eq/neq continueLimit is not available --> reset
		if (operator.equals(ComparisonTypes.EQ) || 
				operator.equals(ComparisonTypes.NE)) {
			this.continueLimit = null;
		}
		this.updateListeners();
	}

	/**
	 * @return the pauseLimit
	 */
	public String getPauseLimit() {
		return pauseLimit;
	}

	/**
	 * @param pauseLimit the pauseLimit to set
	 * @throws IllegalArgumentException if given value is invalid 
	 * 		(see also {@link #isValidValue(String)})
	 */
	public void setPauseLimit(String pauseLimit) {
		if (!this.isValidValue(pauseLimit)) {
			throw new IllegalArgumentException("given value is invalid");
		}
		this.pauseLimit = pauseLimit;
		this.updateListeners();
	}

	/**
	 * @return the continueLimit
	 */
	public String getContinueLimit() {
		return continueLimit;
	}

	/**
	 * Checks whether a continue limit is available at the current state.
	 * Keep in mind, that this class is not thread safe. Subsequent calls with 
	 * potential state changes invalidate the given result.
	 * @return <code>true</code> if a continue limit could be set, 
	 * 		<code>false</code> otherwise
	 */
	public boolean hasContinueLimit() {
		return !(this.operator.equals(ComparisonTypes.EQ) || 
				this.operator.equals(ComparisonTypes.NE));
	}
	
	/**
	 * @param continueLimit the continueLimit to set
	 * @throws UnsupportedOperationException if device is discrete or operator 
	 * 		is either EQ or NE
	 * @throws IllegalArgumentException if the given value is invalid 
	 * 		(i.e. has the wrong type/format)
	 */
	public void setContinueLimit(String continueLimit) {
		if (this.isDiscrete()) {
			throw new UnsupportedOperationException(
				"continue limit is not available for discrete devices");
		}
		if (this.operator.equals(ComparisonTypes.EQ) || 
				this.operator.equals(ComparisonTypes.NE)) {
			throw new UnsupportedOperationException(
				"continue limit is only available for inequality operators.");
		}
		if (continueLimit != null && !this.isValidValue(continueLimit)) {
			throw new IllegalArgumentException("given value is invalid");
		}
		this.continueLimit = continueLimit;
		this.updateListeners();
	}
	
	/**
	 * Checks whether the given value is a valid value. Does not check if it 
	 * could be set as continue limit for the current state. To check this, 
	 * use {@link #hasContinueLimit()} instead.
	 * @return <code>true</code> if the given value is a valid value, 
	 * 		<code>false</code> otherwise
	 */
	public boolean isValidValue(String value) {
		if (this.isDiscrete()) {
			return this.getValue().getDiscreteValues().contains(value);
		}
		switch (this.getType()) {
		case DOUBLE:
			try {
				Double.parseDouble(value);
			} catch (NumberFormatException e) {
				return false;
			}
			return true;
		case INT:
			try {
				Integer.parseInt(value);
			} catch (NumberFormatException e) {
				return false;
			}
			return true;
		case STRING:
			// strings for non-discrete values are always valid
			return true;
		default:
			break;
		}
		return false;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (other == null) {
			return false;
		}
		if (other.getClass() != getClass()) {
			return false;
		}
		if (!(device.equals(((PauseCondition)other).getDevice()))) {
			return false;
		}
		if (!operator.equals(((PauseCondition)other).getOperator())) {
			return false;
		}
		if (!pauseLimit.equals(((PauseCondition)other).getPauseLimit())) {
			return false;
		}
		if (continueLimit == null) {
			if (((PauseCondition)other).getContinueLimit() != null) {
				return false;
			}
		} else {
			if (!continueLimit.equals(((PauseCondition)other).getContinueLimit())) {
				return false;
			}
		}
		return true;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public int hashCode() {
		int result = operator.hashCode();
		result = 31 * result + device.hashCode();
		result = 31 * result + pauseLimit.hashCode();
		if (continueLimit == null) {
			result = 31 * result + 0;
		} else {
			result = 31 * result + continueLimit.hashCode();
		}
		return result;
	}

	private void updateListeners() {
		final CopyOnWriteArrayList<IModelUpdateListener> list = 
			new CopyOnWriteArrayList<>(this.updateListener);
		
		for (IModelUpdateListener imul : list) {
			imul.updateEvent(new ModelUpdateEvent(this, null));
		}
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean addModelUpdateListener(IModelUpdateListener modelUpdateListener) {
		return this.updateListener.add(modelUpdateListener);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean removeModelUpdateListener(IModelUpdateListener modelUpdateListener) {
		return this.updateListener.remove(modelUpdateListener);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		if (this.device instanceof MotorAxis) {
			return device.getName() + DELIMITER + "Position";
		} else if (this.device instanceof DetectorChannel) {
			return device.getName() + DELIMITER + "Value";
		} else if (this.device instanceof Device) {
			return device.getName() + DELIMITER + "Value";
		} else if (this.device instanceof Option) {
			return ((Option)device).getParent().getName() + DELIMITER + 
					device.getName();
		}
		return device.getName();
	}
}