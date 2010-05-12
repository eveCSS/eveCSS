package de.ptb.epics.eve.data.scandescription.errors;

import java.util.List;

/**
 * 
 * 
 * @author Stephan Rehfeld <stephan.rehfeld (-at-) ptb.de>
 *
 */
public interface IModelErrorProvider {
	
	/**
	 * An implementation of this method should return the errors of this model entity and all leafes.
	 * 
	 * @return The errors of this and all sub entities. Never returns null.
	 */
	public List< IModelError > getModelErrors();
	
}
