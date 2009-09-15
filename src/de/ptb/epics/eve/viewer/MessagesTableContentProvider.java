package de.ptb.epics.eve.viewer;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;

public class MessagesTableContentProvider implements IStructuredContentProvider, IMessagesContainerUpdateListener {

	private Viewer viewer;
	
	private boolean showEngineMessages = true;
	private boolean showApplicationMessages = true;
	
	private boolean showDebugMessages = false;
	private boolean showInfoMessages = true;
	private boolean showMinorMessages = true;
	private boolean showErrorMessages = true;
	private boolean showFatalMessages = true;
	
	public Object[] getElements( final Object inputElement ) {
		
		final List< Object > returnList = new ArrayList< Object >();
		final MessagesContainer messagesContainer = (MessagesContainer)inputElement;
		final Iterator< ViewerMessage > it = messagesContainer.getList().iterator();
		while( it.hasNext() ) {
			final ViewerMessage currentViewerMessage = it.next();
			if( currentViewerMessage.getMessageSource() == MessageSource.APPLICATION && !showApplicationMessages ) {
				continue;
			}
			if( currentViewerMessage.getMessageSource() == MessageSource.ENGINE && !showEngineMessages ) {
				continue;
			}
			if( currentViewerMessage.getMessageType() == MessageTypes.DEBUG && showDebugMessages ) {
				returnList.add( currentViewerMessage );
				continue;
			}
			if( currentViewerMessage.getMessageType() == MessageTypes.INFO && showInfoMessages ) {
				returnList.add( currentViewerMessage );
				continue;
			}
			if( currentViewerMessage.getMessageType() == MessageTypes.MINOR && showMinorMessages ) {
				returnList.add( currentViewerMessage );
				continue;
			}
			if( currentViewerMessage.getMessageType() == MessageTypes.ERROR && showErrorMessages ) {
				returnList.add( currentViewerMessage );
				continue;
			}
			if( currentViewerMessage.getMessageType() == MessageTypes.FATAL && showFatalMessages ) {
				returnList.add( currentViewerMessage );
				continue;
			}
		}
		
		return returnList.toArray();
	}

	public void dispose() {

	}

	public void inputChanged( final Viewer viewer, final Object oldInput, final Object newInput) {
		this.viewer = viewer;
		if( oldInput != null ) {
			((MessagesContainer)oldInput).removeMessagesContainerUpdateListener( this );
		}
		if( newInput != null ) {
			((MessagesContainer)newInput).addMessagesContainerUpdateListener( this );
		}
	}

	public void update() {
		this.viewer.getControl().getDisplay().syncExec( new Runnable() {

			public void run() {
				viewer.refresh();
			}
			
		});
		
	}

	public boolean isShowApplicationMessages() {
		return this.showApplicationMessages;
	}

	public void setShowApplicationMessages( final boolean showApplicationMessages ) {
		this.showApplicationMessages = showApplicationMessages;
	}

	public boolean isShowDebugMessages() {
		return this.showDebugMessages;
	}

	public void setShowDebugMessages( final boolean showDebugMessages ) {
		this.showDebugMessages = showDebugMessages;
	}

	public boolean isShowEngineMessages() {
		return this.showEngineMessages;
	}

	public void setShowEngineMessages( final boolean showEngineMessages ) {
		this.showEngineMessages = showEngineMessages;
	}

	public boolean isShowErrorMessages() {
		return this.showErrorMessages;
	}

	public void setShowErrorMessages( final boolean showErrorMessages ) {
		this.showErrorMessages = showErrorMessages;
	}

	public boolean isShowFatalMessages() {
		return this.showFatalMessages;
	}

	public void setShowFatalMessages( final boolean showFatalMessages ) {
		this.showFatalMessages = showFatalMessages;
	}

	public boolean isShowInfoMessages() {
		return this.showInfoMessages;
	}

	public void setShowInfoMessages( final boolean showInfoMessages ) {
		this.showInfoMessages = showInfoMessages;
	}

	public boolean isShowMinorMessages() {
		return this.showMinorMessages;
	}

	public void setShowMinorMessages( final boolean showMinorMessages ) {
		this.showMinorMessages = showMinorMessages;
	}
	
	

}
