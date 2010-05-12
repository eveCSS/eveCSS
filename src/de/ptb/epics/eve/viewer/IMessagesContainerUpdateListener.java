package de.ptb.epics.eve.viewer;

public interface IMessagesContainerUpdateListener {
	public void update();

	public void addElement(ViewerMessage viewerMessage);
}
