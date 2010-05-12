/*******************************************************************************
 * Copyright (c) 2001, 2007 Physikalisch Technische Bundesanstalt.
 * All rights reserved.
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package de.ptb.epics.eve.data.scandescription;

import java.util.ArrayList;
import java.util.List;


import de.ptb.epics.eve.data.scandescription.errors.IModelError;
import de.ptb.epics.eve.data.scandescription.errors.PrescanError;
import de.ptb.epics.eve.data.scandescription.errors.PrescanErrorTypes;

/**
 * This class is the basic of all pre- and postscan behaviors. It's implementing
 * the setting of a value and testing if the value is correct for this kind of device
 * or option.
 * 
 * @author Hartmut Scherr <hartmut.scherr( -at -) ptb.de>
 * @version 1.1
 */
public abstract class AbstractPrescanBehavior extends AbstractPrePostscanBehavior {

	/*
	 * (non-Javadoc)
	 * @see de.ptb.epics.eve.data.scandescription.errors.IModelErrorProvider#getModelErrors()
	 */
	@Override
	public List< IModelError> getModelErrors() {
		final List< IModelError > modelErrors = new ArrayList< IModelError >();
		
		if( !this.isValuePossible( this.getValue() ) ) {
			modelErrors.add( new PrescanError( this, PrescanErrorTypes.VALUE_NOT_POSSIBLE ) );
		}
		
		return modelErrors;
	}

}
