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
	public String getChangeLog();
	
	/**
	 * @return the patch it belongs to
	 */
	public Patch belongsTo();
	
	/**
	 * @param document the document that should be modified
	 */
	public void modify(Document document);
}