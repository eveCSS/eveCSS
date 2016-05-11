package de.ptb.epics.eve.data.scandescription.updater;

import org.w3c.dom.Document;

/**
 * @author Marcus Michalsky
 * @since 1.18
 */
public interface Modification {
	
	/**
	 * @return a change log text
	 */
	String getChangeLog();
	
	/**
	 * @return the patch it belongs to
	 */
	Patch belongsTo();
	
	/**
	 * @param document the document that should be modified
	 */
	void modify(Document document);
}