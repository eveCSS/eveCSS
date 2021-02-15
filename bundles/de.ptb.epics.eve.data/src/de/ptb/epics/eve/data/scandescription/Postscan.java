package de.ptb.epics.eve.data.scandescription;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import de.ptb.epics.eve.data.measuringstation.AbstractPrePostscanDevice;
import de.ptb.epics.eve.data.scandescription.errors.IModelError;
import de.ptb.epics.eve.data.scandescription.updatenotification.IModelUpdateListener;
import de.ptb.epics.eve.data.scandescription.updatenotification.ModelUpdateEvent;

/**
 * <code>Postscan</code> represents a post scan behavior.
 * 
 * @author Stephan Rehfeld <stephan.rehfeld( -at -) ptb.de>
 * @author Marcus Michalsky
 */
public class Postscan extends AbstractPostscanBehavior {

	public Postscan() {
		super();
	}
	
	/**
	 * Constructor.
	 * 
	 * @param dev the corresponding device
	 */
	public Postscan(AbstractPrePostscanDevice dev) {
		super();
		this.setAbstractPrePostscanDevice(dev);
	}
	
	/**
	 * 
	 * @param device
	 * @param initialValue
	 * @since 1.35
	 */
	public Postscan(AbstractPrePostscanDevice device, String initialValue) {
		this(device);
		if (this.isValuePossible(initialValue)) {
			this.setValue(initialValue);
		} else {
			throw new IllegalArgumentException("value not possible");
		}
	}
	
	/**
	 * Copy Constructor.
	 * 
	 * @param postscan the postscan to be copied
	 * @return a copy of the given postscan
	 * @author Marcus Michalsky
	 * @since 1.19
	 */
	public static Postscan newInstance(Postscan postscan) {
		Postscan newPostscan = new Postscan(
				postscan.getAbstractPrePostscanDevice());
		newPostscan.setValue(postscan.getValue());
		newPostscan.setReset(postscan.isReset());
		return newPostscan;
	}
	
	/*
	 * indicates whether the value should be reset to the value it had when
	 * the scan module started.
	 */
	private boolean reset;
	
	/**
	 * Checks whether reset is set.
	 * 
	 * @return <code>true</code> if set, 
	 * 		   <code>false</code> otherwise
	 */
	public boolean isReset() {
		return this.reset;
	}
	
	/**
	 * Sets the reset value. 
	 * If you pass true here, the bahavior will forget the previous setted value.
	 * 
	 * @param reset <code>true</code>if you want that the value will be resetted.
	 */
	public void setReset(final boolean reset) {
		this.reset = reset;
		if (reset) {
			super.setValue("");
		}
		updateListeners();
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setValue(final String value) {
		this.reset = false;
		super.setValue(value);
	}
	
	/**
	 * Checks whether a value is set.
	 * 
	 * @return <code>true</code> if a value is set, 
	 * 		   <code>false</code> otherwise
	 */
	public boolean isValue() {
		return (super.getValue() != null);
	}
	
	/**
	 * {@inheritDoc} 
	 */
	@Override
	public List<IModelError> getModelErrors() {
		return this.reset?new ArrayList<IModelError>():super.getModelErrors();
	}
	
	/*
	 * 
	 */
	private void updateListeners() {
		final CopyOnWriteArrayList<IModelUpdateListener> list = 
			new CopyOnWriteArrayList<>(this.modelUpdateListener);
		
		for(IModelUpdateListener imul : list) {
			imul.updateEvent(new ModelUpdateEvent(this, null));
		}
	}
}