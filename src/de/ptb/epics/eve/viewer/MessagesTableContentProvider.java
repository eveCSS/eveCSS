package de.ptb.epics.eve.viewer;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;

public class MessagesTableContentProvider implements IStructuredContentProvider, IMessagesContainerUpdateListener {

	private TableViewer viewer;
	
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
			if (checkFilter(currentViewerMessage)) returnList.add( currentViewerMessage );
		}
		
		return returnList.toArray();
	}

	private boolean checkFilter(ViewerMessage message){
		if ((message.getMessageSource() == MessageSource.VIEWER) && !showApplicationMessages ) {
			return false;
		}
		if ((message.getMessageSource() != MessageSource.VIEWER) && !showEngineMessages ) {
			return false;
		}
		if ((message.getMessageType() == MessageTypes.DEBUG) && showDebugMessages ) {
			return true;
		}
		if ((message.getMessageType() == MessageTypes.INFO) && showInfoMessages ) {
			return true;
		}
		if ((message.getMessageType() == MessageTypes.MINOR) && showMinorMessages ) {
			return true;
		}
		if ((message.getMessageType() == MessageTypes.ERROR) && showErrorMessages ) {
			return true;
		}
		if ((message.getMessageType() == MessageTypes.FATAL) && showFatalMessages ) {
			return true;
		}
		return false;
	}
	
	public void dispose() {

	}

	public void inputChanged( final Viewer viewer, final Object oldInput, final Object newInput) {
		this.viewer = (TableViewer)viewer;
		if( oldInput != null ) {
			((MessagesContainer)oldInput).removeMessagesContainerUpdateListener( this );
		}
		if( newInput != null ) {
			((MessagesContainer)newInput).addMessagesContainerUpdateListener( this );
		}
	}

	public void update() {
		if (!viewer.getControl().isDisposed()){
			
			// TODO this is a severe performance killer
			// do not switch to syncExec and do not do viewer.refresh()
			// use a tableviewer with  viewer.update() or viewer.add() instead!
			
			this.viewer.getControl().getDisplay().asyncExec( new Runnable() {
	
				public void run() {
					if (!viewer.getControl().isDisposed()) viewer.refresh();
				}
				
			});
		}
	}
	
	@Override
	public void addElement(final ViewerMessage viewerMessage) {

		if (!checkFilter(viewerMessage)) return;
		
		if (!viewer.getControl().isDisposed()){
			this.viewer.getControl().getDisplay().asyncExec( new Runnable() {
				public void run() {
					if (!viewer.getControl().isDisposed()) viewer.add(viewerMessage);
				}
			});
		}
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
