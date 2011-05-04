package de.ptb.epics.eve.data.scandescription.errors;

import java.util.List;

/**
 * 
 * @author Stephan Rehfeld <stephan.rehfeld (-at-) ptb.de>
 */
public interface IModelErrorProvider {
	
	/**
	 * Should return the errors of this model entity and all leafs.
	 * 
	 * @return The errors of this and all sub entities.
	 */
	public List<IModelError> getModelErrors();
}