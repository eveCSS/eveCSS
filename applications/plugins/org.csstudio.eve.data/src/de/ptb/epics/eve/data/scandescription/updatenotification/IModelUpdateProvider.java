package de.ptb.epics.eve.data.scandescription.updatenotification;

/**
 * This interface must be implemented by every object that provides model 
 * update events.
 * 
 * @author Stephan Rehfeld <stephan.rehfeld (-at-) ptb.de>
 * @author Marcus Michalsky
 * @see {@link java.util.Observable}
 * @see {@link java.beans.PropertyChangeSupport}
 */
public interface IModelUpdateProvider {

	/**
	 * Adds a model update listener.
	 * 
	 * @param modelUpdateListener the model update listener that should be 
	 * 		   added
	 * @return <code>true</code> if the update listener has been added
	 */
	boolean addModelUpdateListener(
			final IModelUpdateListener modelUpdateListener);
	
	/**
	 * 
	 * Removes a model update listener.
	 * 
	 * @param modelUpdateListener The model update listener that should be 
	 * 		  removed
	 * @return <code>true</code> if the update listener has been added
	 */
	boolean removeModelUpdateListener(
			final IModelUpdateListener modelUpdateListener);
}