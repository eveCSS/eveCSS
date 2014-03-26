package de.ptb.epics.eve.data.scandescription;

import org.apache.log4j.Logger;

import de.ptb.epics.eve.data.measuringstation.AbstractPrePostscanDevice;

/**
 * This class describes a prescan behavior.
 * 
 * @author Stephan Rehfeld <stephan.rehfeld( -at -) ptb.de>
 */
public class Prescan extends AbstractPrescanBehavior {
	private static final Logger LOGGER = Logger.getLogger(Prescan.class
			.getName());
	
	/**
	 * Constructor.
	 */
	public Prescan() {
		super();
	}
	
	/**
	 * Copy Constructor.
	 * 
	 * @param prescan the prescan to be copied
	 * @return a copy of the given prescan
	 * @author Marcus Michalsky
	 * @since 1.19
	 */
	public static Prescan newInstance(Prescan prescan) {
		LOGGER.debug("Prescan Modul: " + prescan.getScanModule());
		Prescan newPrescan = new Prescan(prescan.getAbstractPrePostscanDevice());
		newPrescan.setValue(prescan.getValue());
		return newPrescan;
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
