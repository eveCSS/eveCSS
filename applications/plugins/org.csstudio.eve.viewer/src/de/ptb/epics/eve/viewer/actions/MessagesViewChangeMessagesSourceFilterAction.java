package de.ptb.epics.eve.viewer.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.ui.actions.ActionFactory.IWorkbenchAction;

import de.ptb.epics.eve.viewer.dialogs.MessageSourcesFilterDialog;
import de.ptb.epics.eve.viewer.messages.MessagesTableContentProvider;

public class MessagesViewChangeMessagesSourceFilterAction extends Action implements IWorkbenchAction {

	private static final String ID = "de.ptb.epics.eve.viewer.actions.ChangeMessagesSourceFilterAction";  
	private final TableViewer tableViewer;
	
	public MessagesViewChangeMessagesSourceFilterAction( final TableViewer tableViewer ){  
		this.setId( MessagesViewChangeMessagesSourceFilterAction.ID ); 
		this.tableViewer = tableViewer;
	} 
	
	public void run() {  
		final MessageSourcesFilterDialog messageSourcesFilterDialog = new MessageSourcesFilterDialog( null, this.tableViewer );
		messageSourcesFilterDialog.setBlockOnOpen( true );
		if( messageSourcesFilterDialog.open() == MessageSourcesFilterDialog.OK ) {
			final MessagesTableContentProvider messagesTableContentProvider = (MessagesTableContentProvider)this.tableViewer.getContentProvider();
			messagesTableContentProvider.setShowApplicationMessages( messageSourcesFilterDialog.isShowApplicationMessages() );
			messagesTableContentProvider.setShowEngineMessages( messageSourcesFilterDialog.isShowEngineMessages() );
			this.tableViewer.refresh();
		}
	}   
	
	public void dispose() {
		// TODO Auto-generated method stub

	}

}
