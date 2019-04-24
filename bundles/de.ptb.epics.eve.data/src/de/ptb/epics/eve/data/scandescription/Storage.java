package de.ptb.epics.eve.data.scandescription;

import java.util.LinkedList;
import java.util.List;

/**
 * Defines the position the data is written to. Intended to be used for 
 * {@link de.ptb.epics.eve.data.scandescription.ScanModule}.
 * 
 * Kept only for updating SCMLs of version 2.3 to 3.0 !
 * 
 * @author Marcus Michalsky
 * @since 1.18
 */
public enum Storage {
	
	/**
	 * The default location.
	 */
	DEFAULT {
		@Override
		public String toString() {
			return "default";
		}
	},
	
	/**
	 * An alternate, i.e. separate, location of the data file
	 */
	ALTERNATE {
		@Override
		public String toString() {
			return "alternate";
		}
	},
	
	/**
	 * no data will be written
	 */
	NONE {
		@Override
		public String toString() {
			return "none";
		}
	};
	
	/**
	 * 
	 * @return
	 */
	public static String[] stringValues() {
		List<String> values = new LinkedList<String>();
		for (Storage storage : Storage.values()) {
			values.add(storage.toString());
		}
		return values.toArray(new String[0]);
	}
}