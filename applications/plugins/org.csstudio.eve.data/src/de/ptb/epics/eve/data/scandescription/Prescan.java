package de.ptb.epics.eve.data.scandescription;

import de.ptb.epics.eve.data.measuringstation.AbstractPrePostscanDevice;

/**
 * This class describes a prescan behavior.
 * 
 * @author Stephan Rehfeld <stephan.rehfeld( -at -) ptb.de>
 */
public class Prescan extends AbstractPrescanBehavior {

	/**
	 * Constructor.
	 */
	public Prescan() {
		super();
	}
	
	/**
	 * Constructor.
	 * 
	 * @param dev the corresponding device
	 */
	public Prescan(AbstractPrePostscanDevice dev) {
		super();
		this.setAbstractPrePostscanDevice(dev);
	}
}
