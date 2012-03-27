package de.ptb.epics.eve.data.scandescription;

import java.util.concurrent.CopyOnWriteArrayList;

import de.ptb.epics.eve.data.measuringstation.AbstractPrePostscanDevice;
import de.ptb.epics.eve.data.measuringstation.Device;
import de.ptb.epics.eve.data.measuringstation.Option;
import de.ptb.epics.eve.data.scandescription.updatenotification.IModelUpdateListener;
import de.ptb.epics.eve.data.scandescription.updatenotification.ModelUpdateEvent;

/**
 * This class is the basic of all pre- and postscan behaviors. It's implementing
 * the setting of a value and testing if the value is correct for this kind of device
 * or option.
 * 
 * @author Stephan Rehfeld <stephan.rehfeld( -at -) ptb.de>
 * @author Marcus Michalsky
 */
public abstract class AbstractPrePostscanBehavior extends AbstractBehavior {

	/**
	 * The value of the AbstractPrePostscanDevice.
	 */
	private String value;
	
	/**
	 * Constructs an <code>AbstractPrePostscanBehavior</code>.
	 */
	public AbstractPrePostscanBehavior() {
		this.value = "";
	}
	
	/**
	 * Gives back the value, that should be setted for this Behavior.
	 * 
	 * @return A String object. Never returs null!
	 */
	public String getValue() {
		return this.value;
	}
	
	/**
	 * Sets the value, that should be setted to the AbstractPrePostscanDevice.
	 * 
	 * @param value a String containing the value.
	 * @throws IllegalArgumentException if the argument is <code>null</code>.
	 */
	public void setValue(final String value) {
		if( value == null ) {
			throw new IllegalArgumentException(
					"The parameter 'value' must not be null!");
		}
		this.value = value;
		final CopyOnWriteArrayList<IModelUpdateListener> list = 
			new CopyOnWriteArrayList<IModelUpdateListener>(this.modelUpdateListener);
		
		for(IModelUpdateListener imul : list) {
			imul.updateEvent(new ModelUpdateEvent(this,null));
		}
	}
	
	/**
	 * Gives back the AbstractPrePostscanDevice that is controlled by this behavior.
	 * 
	 * @return
	 */
	public AbstractPrePostscanDevice getAbstractPrePostscanDevice() {
		return (AbstractPrePostscanDevice)this.getAbstractDevice();
	}
	
	/**
	 * Sets the AbstractPrePostscanDevice that is controlled by this behavior.
	 * 
	 * @param abstractPrePostscanDevice The device that should be controlled by 
	 * 		  this behavior.
	 * @throws IllegalArgumentException if the argument is <code>null</code>.
	 */
	public void setAbstractPrePostscanDevice(
			final AbstractPrePostscanDevice abstractPrePostscanDevice) {
		if(abstractPrePostscanDevice == null) {
			throw new IllegalArgumentException(
				"The parameter 'abstractPrePostscanDevice' must not be null!");
		}
		this.abstractDevice = abstractPrePostscanDevice;
	}
	
	/**
	 * Finds out if the controlled device is a option.
	 * 
	 * @return Returns true if the device is an option and false if it's not.
	 */
	public boolean isOption() {
		return this.getAbstractDevice() instanceof Option;
	}
	
	/**
	 * Finds out if the controlled device is a Device.
	 * 
	 * @return  Returns true if the device is an Device and false if it's not.
	 */
	public boolean isDevice() {
		return this.getAbstractDevice() instanceof Device;
	}
	
	/**
	 * Checks whether the given value is valid.
	 * 
	 * @param value the value that should be checked
	 * @return <code>true</code> if value is possible, 
	 * 		   <code>false</code> otherwise
	 * @throws IllegalArgumentException if the argument is <code>null</code>
	 */
	public boolean isValuePossible(final String value) {
		if(value == null) {
			throw new IllegalArgumentException(
					"The parameter'value' must not be null!");
		}
		return ((AbstractPrePostscanDevice)
				this.getAbstractDevice()).isValuePossible(value);
	}	
}