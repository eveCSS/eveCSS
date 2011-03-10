package de.ptb.epics.eve.data.scandescription.updatenotification;

/**
 * Implemented by classes that listen to model changes of the scan description.
 * 
 * @author Stephan Rehfeld <stephan.rehfeld ( -at- ) ptb.de>
 * @author Marcus Michalsky
 */
public interface IModelUpdateListener {

	/**
	 * This method gets called if a model element has changed.
	 * 
	 * @param modelUpdateEvent
	 */
	public void updateEvent(final ModelUpdateEvent modelUpdateEvent);	
}