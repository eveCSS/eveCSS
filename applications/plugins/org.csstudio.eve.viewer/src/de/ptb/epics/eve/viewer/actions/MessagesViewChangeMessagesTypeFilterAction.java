package de.ptb.epics.eve.viewer.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.ui.actions.ActionFactory.IWorkbenchAction;

import de.ptb.epics.eve.viewer.dialogs.MessageSourcesFilterDialog;
import de.ptb.epics.eve.viewer.dialogs.MessageTypesFilterDialog;
import de.ptb.epics.eve.viewer.messages.MessagesTableContentProvider;


public class MessagesViewChangeMessagesTypeFilterAction extends Action implements IWorkbenchAction {

	private static final String ID = "de.ptb.epics.eve.viewer.actions.ChangeMessagesTypeFilterAction";  
	private final TableViewer tableViewer;
	
	public MessagesViewChangeMessagesTypeFilterAction( final TableViewer tableViewer ){  
		this.setId( MessagesViewChangeMessagesTypeFilterAction.ID ); 
		this.tableViewer = tableViewer;
	} 
	
	public void run() {  
		final MessageTypesFilterDialog messageTypesFilterDialog = new MessageTypesFilterDialog( null, this.tableViewer );
		messageTypesFilterDialog.setBlockOnOpen( true );
		if( messageTypesFilterDialog.open() == MessageSourcesFilterDialog.OK ) {
			final MessagesTableContentProvider messagesTableContentProvider = (MessagesTableContentProvider)this.tableViewer.getContentProvider();
			messagesTableContentProvider.setShowDebugMessages( messageTypesFilterDialog.isShowDebugMessages() );
			messagesTableContentProvider.setShowInfoMessages( messageTypesFilterDialog.isShowInfoMessages() );
			messagesTableContentProvider.setShowMinorMessages( messageTypesFilterDialog.isShowMinorMessages() );
			messagesTableContentProvider.setShowErrorMessages( messageTypesFilterDialog.isShowErrorMessages() );
			messagesTableContentProvider.setShowFatalMessages( messageTypesFilterDialog.isShowFatalMessages() );
			this.tableViewer.refresh();
		}
	}   
	
	public void dispose() {
		// TODO Auto-generated method stub

	}

}
