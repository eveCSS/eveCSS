package de.ptb.epics.eve.viewer;

import de.ptb.epics.eve.ecp1.client.interfaces.INewXMLFileListener;

public class TestListener implements INewXMLFileListener {

	@Override
	public void newXMLFileReceived(byte[] xmlData) {
		// TODO Auto-generated method stub

		System.out.println("TestListener: NewXMLFileReceived");
		
	}

}
