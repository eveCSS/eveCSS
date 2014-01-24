package de.ptb.epics.eve.data.scandescription.updater;

import org.w3c.dom.Document;

/**
 * @author Marcus Michalsky
 * @since 1.18
 */
public interface Modification {
	/**
	 * 
	 * @param document
	 */
	public void modify(Document document);
}