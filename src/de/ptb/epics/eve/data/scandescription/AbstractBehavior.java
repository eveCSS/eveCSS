/*
 * Copyright (c) 2001, 2007 Physikalisch-Technische Bundesanstalt.
 * All rights reserved.
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 */
package de.ptb.epics.eve.data.scandescription;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import de.ptb.epics.eve.data.measuringstation.AbstractDevice;
import de.ptb.epics.eve.data.scandescription.errors.IModelErrorProvider;
import de.ptb.epics.eve.data.scandescription.updatenotification.IModelUpdateListener;
import de.ptb.epics.eve.data.scandescription.updatenotification.IModelUpdateProvider;
import de.ptb.epics.eve.data.scandescription.updatenotification.ModelUpdateEvent;

/**
 * This class the basics of all behavior descriptions inside of a scan module.
 * 
 * @author Stephan Rehfeld <stephan.rehfeld( -at -) ptb.de>
 * @version 1.2
 */
public abstract class AbstractBehavior implements IModelUpdateListener, 
		IModelUpdateProvider, IModelErrorProvider {

	/**
	 * This list contains all listener for update of this model object.
	 */
	protected List<IModelUpdateListener> modelUpdateListener;
	
	/**
	 * Constructs an <code>AbstractBehavior</code>.
	 */
	public AbstractBehavior() {
		this.modelUpdateListener = new ArrayList<IModelUpdateListener>();
	}
	
	/**
	 * the abstract device that is used for this behavior
	 */
	protected AbstractDevice abstractDevice;

	/**
	 * Returns the abstract device of this behavior
	 * 
	 * @return An AbstractDevice.
	 */
	public AbstractDevice getAbstractDevice() {
		return abstractDevice;
	}

	/**
	 * {@inheritDoc}	
	 */
	@Override
	public void updateEvent(final ModelUpdateEvent modelUpdateEvent) {
		final Iterator<IModelUpdateListener> it = 
				this.modelUpdateListener.iterator();
		while(it.hasNext()) {
			it.next().updateEvent(new ModelUpdateEvent(this,modelUpdateEvent));
		}	
	}

	/**
	 * {@inheritDoc} 
	 */
	@Override
	public boolean addModelUpdateListener(
				final IModelUpdateListener modelUpdateListener) {
		return this.modelUpdateListener.add(modelUpdateListener);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean removeModelUpdateListener(
			final IModelUpdateListener modelUpdateListener) {
		return this.modelUpdateListener.remove(modelUpdateListener);
	}	
}