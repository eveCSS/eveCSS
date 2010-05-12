/*******************************************************************************
 * Copyright (c) 2001, 2007 Physikalisch Technische Bundesanstalt.
 * All rights reserved.
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package de.ptb.epics.eve.data.measuringstation;

import de.ptb.epics.eve.data.measuringstation.exceptions.ParentNotAllowedException;

/**
 * This class representes a device. Currently it's not really implementing functions, because
 * the class AbstractPrePostscanDevice is already implementing everything. This class is just
 * overriding the setParent() method.
 *
 * 
 * @author Stephan Rehfeld <stephan.rehfeld( -at -) ptb.de>
 * @version 1.3
 */
public class Device extends AbstractPrePostscanDevice {

	/**
	 * This method is just overriding the getParent Method of AbstractDevice, because a Device
	 * can have no parent. This method is simly doing nothing.
	 * 
	 * @param parent Pass whatever you want. It will not be used at all!
	 */
	@Override
	protected void setParent( final AbstractDevice parent ) throws ParentNotAllowedException {
	}
	
}
