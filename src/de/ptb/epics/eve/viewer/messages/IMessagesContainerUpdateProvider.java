package de.ptb.epics.eve.viewer.messages;


public interface IMessagesContainerUpdateProvider {
	public void addMessagesContainerUpdateListener( final IMessagesContainerUpdateListener listener );
	public void removeMessagesContainerUpdateListener( final IMessagesContainerUpdateListener listener );
}
