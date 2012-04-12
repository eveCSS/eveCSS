package de.ptb.epics.eve.data.measuringstation;

import java.util.Iterator;
import java.util.List;

/**
 *  This class represents a motor axis of a device.
 * 
 * @author Stephan Rehfeld <stephan.rehfeld( -at -) ptb.de>
 */
public class Option extends AbstractPrePostscanDevice {

	/**
	 * Returns a copy of the calling Option object
	 * 
	 * @return a clone of the current Option
	 */
	@Override
	public Object clone() {
		final Option option = new Option();
		
		option.setClassName(this.getClassName());
		option.setDisplaygroup(this.getDisplaygroup());
		option.setValue((Function)
				(this.getValue()!=null?this.getValue().clone():null));
		this.setName(this.getName());
		option.setName(this.getName());
		option.setId(this.getID());
		option.setUnit((Unit)
				(this.getUnit()!=null?this.getUnit().clone():null));
		return option;
	}

	/**
	 * Returns a copy of the internal options list.
	 * 
	 * @return A list of Option objects.
	 */
	@Override
	public List<Option> getOptions() {
		return null;
	}

	/**
	 * Returns an <code>Iterator</code> over the internal <code>List</code> of 
	 * <code>Option</code>s.
	 * 
	 * @return An Iterator<Option> object over the internal options list.
	 */
	@Override
	public Iterator<Option> optionIterator() {
		return null;
	}
}