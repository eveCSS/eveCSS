package de.ptb.epics.eve.ecp1.client.interfaces;

public interface INewXMLFileListener {
	
	/**
	 * 
	 * @param xmlData
	 */
	void newXMLFileReceived(final byte[] xmlData);
}