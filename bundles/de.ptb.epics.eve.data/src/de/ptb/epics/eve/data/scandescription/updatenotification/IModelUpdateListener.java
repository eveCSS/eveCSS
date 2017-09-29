package de.ptb.epics.eve.data.scandescription.updatenotification;

/**
 * Implemented by classes that listen to model changes of the scan description.
 * 
 * @author Stephan Rehfeld <stephan.rehfeld ( -at- ) ptb.de>
 * @author Marcus Michalsky
 * @see {@link java.util.Observer}
 * @see {@link java.beans.PropertyChangeListener}
 */
public interface IModelUpdateListener {

	/**
	 * This method gets called if a model element has changed.
	 * 
	 * @param modelUpdateEvent the 
	 * 		  {@link de.ptb.epics.eve.data.scandescription.updatenotification.ModelUpdateEvent}
	 */
	void updateEvent(final ModelUpdateEvent modelUpdateEvent);
}