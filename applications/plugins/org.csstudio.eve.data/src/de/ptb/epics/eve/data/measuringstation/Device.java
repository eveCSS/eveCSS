package de.ptb.epics.eve.data.measuringstation;

import de.ptb.epics.eve.data.measuringstation.exceptions.ParentNotAllowedException;

/**
 * This class represents a Device.
 * 
 * @author Stephan Rehfeld <stephan.rehfeld( -at -) ptb.de>
 * @author Marcus Michalsky
 */
public class Device extends AbstractPrePostscanDevice {

	/**
	 * This method is just overriding the getParent Method of AbstractDevice,
	 * because a Device can have no parent. This method is simply doing
	 * nothing.
	 * 
	 * @param parent Pass whatever you want. It will not be used at all!
	 * @throws ParentNotAllowedException ?
	 */
	@Override
	protected void setParent(final AbstractDevice parent) 
							throws ParentNotAllowedException {
		// TODO empty but throws declaration ?
		// get or set Parent ???
	}
	
	/**
	 * Clones the calling device.
	 * 
	 *@return a clone of the current <code>Device</code>
	 */
	@Override
	public Object clone() {
		final Device device = new Device();
		
		device.setClassName(this.getClassName());
		device.setDisplaygroup(this.getDisplaygroup());
		device.setValue((Function)
				(this.getValue()!=null?this.getValue().clone():null));
		
		device.setName(this.getName());
		device.setId(this.getID());
		device.setUnit((Unit)
				(this.getUnit()!=null?this.getUnit().clone():null));
		
		for(Option o : this.getOptions()) {
			device.add(o);
		}
		
		return device;
	}
}