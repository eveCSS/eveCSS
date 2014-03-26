package de.ptb.epics.eve.data.scandescription;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import de.ptb.epics.eve.data.measuringstation.AbstractDevice;
import de.ptb.epics.eve.data.scandescription.errors.IModelErrorProvider;
import de.ptb.epics.eve.data.scandescription.updatenotification.IModelUpdateListener;
import de.ptb.epics.eve.data.scandescription.updatenotification.IModelUpdateProvider;
import de.ptb.epics.eve.data.scandescription.updatenotification.ModelUpdateEvent;

/**
 * This class the basics of all behavior descriptions inside of a scan module.
 * 
 * @author Stephan Rehfeld <stephan.rehfeld( -at -) ptb.de>
 * @author Marcus Michalsky
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

	protected ScanModule scanModule;

	/**
	 * Returns the abstract device of this behavior
	 * 
	 * @return An AbstractDevice.
	 */
	public AbstractDevice getAbstractDevice() {
		return abstractDevice;
	}

	/**
	 * Returns the scan module the axis is corresponding to.
	 * 
	 * @return the corresponding scan module
	 */
	public ScanModule getScanModule() {
		return this.scanModule;
	}

	private void updateListeners() {
		final CopyOnWriteArrayList<IModelUpdateListener> list = 
			new CopyOnWriteArrayList<IModelUpdateListener>(this.modelUpdateListener);
		
		Iterator<IModelUpdateListener> it = list.iterator();
		
		while(it.hasNext()) {
			it.next().updateEvent(new ModelUpdateEvent(this, null));
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void updateEvent(final ModelUpdateEvent modelUpdateEvent) {
		updateListeners();
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