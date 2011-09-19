package de.ptb.epics.eve.viewer.messages;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;

import de.ptb.epics.eve.viewer.MessageSource;

/**
 * 
 * @author ?
 * @author Marcus Michalsky
 */
public class MessagesTableContentProvider 
		implements IStructuredContentProvider, IMessagesContainerUpdateListener {

	private TableViewer viewer;
	
	// indicators of message types
	private boolean showEngineMessages = true;
	private boolean showApplicationMessages = true;
	
	private boolean showDebugMessages = false;
	private boolean showInfoMessages = true;
	private boolean showMinorMessages = true;
	private boolean showErrorMessages = true;
	private boolean showFatalMessages = true;
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Object[] getElements(final Object inputElement) {
		
		final List<Object> returnList = new ArrayList<Object>();
		final MessagesContainer messagesContainer = 
				(MessagesContainer) inputElement;
		final Iterator<ViewerMessage> it = 
				messagesContainer.getList().iterator();
		while(it.hasNext()) {
			final ViewerMessage currentViewerMessage = it.next();
			if (checkFilter(currentViewerMessage)) 
				returnList.add(currentViewerMessage);
		}	
		return returnList.toArray();
	}

	/*
	 * called by getElements
	 */
	private boolean checkFilter(ViewerMessage message) {
		if (message.getMessageSource() == MessageSource.VIEWER && 
			!showApplicationMessages) {
			return false;
		}
		if (message.getMessageSource() != MessageSource.VIEWER && 
			!showEngineMessages) {
			return false;
		}
		if (message.getMessageType() == MessageTypes.DEBUG && 
			showDebugMessages) {
			return true;
		}
		if (message.getMessageType() == MessageTypes.INFO && 
			showInfoMessages) {
			return true;
		}
		if (message.getMessageType() == MessageTypes.MINOR && 
			showMinorMessages) {
			return true;
		}
		if (message.getMessageType() == MessageTypes.ERROR && 
			showErrorMessages) {
			return true;
		}
		if (message.getMessageType() == MessageTypes.FATAL && 
			showFatalMessages) {
			return true;
		}
		return false;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void dispose() {
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void inputChanged(final Viewer viewer, final Object oldInput, 
			final Object newInput) {
		this.viewer = (TableViewer) viewer;
		if(oldInput != null) {
			((MessagesContainer)oldInput).
					removeMessagesContainerUpdateListener(this);
		}
		if(newInput != null) {
			((MessagesContainer)newInput).
					addMessagesContainerUpdateListener(this);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void update() {
		if (!viewer.getControl().isDisposed()){
			
			// TODO this is a severe performance killer
			// do not switch to syncExec and do not do viewer.refresh()
			// use a tableviewer with  viewer.update() or viewer.add() instead!
			
			this.viewer.getControl().getDisplay().asyncExec( new Runnable() {
	
				@Override
				public void run() {
					if (!viewer.getControl().isDisposed()) viewer.refresh();
				}
				
			});
		}
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void addElement(final ViewerMessage viewerMessage) {

		if (!checkFilter(viewerMessage)) return;
		
		if (!viewer.getControl().isDisposed()){
			this.viewer.getControl().getDisplay().asyncExec(new Runnable() {
				public void run() {
					if (!viewer.getControl().isDisposed()) 
						viewer.add(viewerMessage);
				}
			});
		}
	}
	
	/**
	 * Checks whether messages from the application are shown.
	 * 
	 * @return <code>true</code> if application messages will be shown, 
	 * 		   <code>false</code> otherwise
	 */
	public boolean isShowApplicationMessages() {
		return this.showApplicationMessages;
	}

	/**
	 * Sets whether messages from the application should be shown.
	 * 
	 * @param showApplicationMessages <code>true</code> if messages from the 
	 * 		  application should be shown, <code>false</code> otherwise
	 */
	public void setShowApplicationMessages(
			final boolean showApplicationMessages) {
		this.showApplicationMessages = showApplicationMessages;
	}

	/**
	 * Checks whether debug messages are shown.
	 * 
	 * @return <code>true</code> if debug messages will be shown, 
	 * 		   <code>false</code> otherwise
	 */
	public boolean isShowDebugMessages() {
		return this.showDebugMessages;
	}

	/**
	 * Sets whether debug level messages should be shown.
	 * 
	 * @param showDebugMessages <code>true</code> if debug messages should be 
	 * 		  shown, <code>false</code> otherwise
	 */
	public void setShowDebugMessages(final boolean showDebugMessages) {
		this.showDebugMessages = showDebugMessages;
	}

	/**
	 * Checks whether messages from the engine are shown.
	 * 
	 * @return <code>true</code> if engine messages will be shown, 
	 * 		   <code>false</code> otherwise
	 */
	public boolean isShowEngineMessages() {
		return this.showEngineMessages;
	}

	/**
	 * Sets whether messages from the engine should be shown.
	 * 
	 * @param showEngineMessages <code>true</code> if messages from the engine 
	 * 		  should be shown, <code>false</code> otherwise
	 */
	public void setShowEngineMessages(final boolean showEngineMessages) {
		this.showEngineMessages = showEngineMessages;
	}

	/**
	 * Checks whether error messages are shown.
	 * 
	 * @return <code>true</code> if error messages will be shown, 
	 * 		   <code>false</code> otherwise
	 */
	public boolean isShowErrorMessages() {
		return this.showErrorMessages;
	}

	/**
	 * Sets whether error messages should be shown.
	 * 
	 * @param showErrorMessages <code>true</code> if error messages should be 
	 * 		  shown, <code>false</code> otherwise
	 */
	public void setShowErrorMessages(final boolean showErrorMessages) {
		this.showErrorMessages = showErrorMessages;
	}

	/**
	 * Checks whether fatal messages are shown.
	 * 
	 * @return <code>true</code> if fatal messages will be shown,
	 * 		   <code>false</code> otherwise
	 */
	public boolean isShowFatalMessages() {
		return this.showFatalMessages;
	}

	/** 
	 * Sets whether fatal messages should be shown.
	 * 
	 * @param showFatalMessages <code>true</code> if fatal messages should be 
	 * 		  shown, <code>false</code> otherwise
	 */
	public void setShowFatalMessages(final boolean showFatalMessages) {
		this.showFatalMessages = showFatalMessages;
	}

	/**
	 * Checks whether info messages are shown.
	 * 
	 * @return <code>true</code> if info messages will be shown, 
	 * 	 	   <code>false</code> otherwise
	 */
	public boolean isShowInfoMessages() {
		return this.showInfoMessages;
	}

	/**
	 * Sets whether info messages should be shown.
	 * 
	 * @param showInfoMessages <code>true</code> if info messages should be 
	 * 		  shown, <code>false</code> otherwise
	 */
	public void setShowInfoMessages(final boolean showInfoMessages) {
		this.showInfoMessages = showInfoMessages;
	}

	/**
	 * Checks whether minor messages are shown.
	 * 
	 * @return <code>true</code> if minor messages will be shown, 
	 * 		   <code>false</code> otherwise
	 */
	public boolean isShowMinorMessages() {
		return this.showMinorMessages;
	}

	/**
	 * Sets whether minor messages should be shown.
	 * 
	 * @param showMinorMessages <code>true</code> if minor messages should be 
	 * 	      shown, <code>false</code> otherwise
	 */
	public void setShowMinorMessages(final boolean showMinorMessages) {
		this.showMinorMessages = showMinorMessages;
	}
}