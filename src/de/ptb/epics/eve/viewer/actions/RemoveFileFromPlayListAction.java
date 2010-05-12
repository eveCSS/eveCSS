package de.ptb.epics.eve.viewer.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.actions.ActionFactory.IWorkbenchAction;

import de.ptb.epics.eve.ecp1.client.model.PlayListEntry;
import de.ptb.epics.eve.viewer.Activator;
import de.ptb.epics.eve.viewer.MessageSource;
import de.ptb.epics.eve.viewer.MessageTypes;
import de.ptb.epics.eve.viewer.PlayListView;
import de.ptb.epics.eve.viewer.ViewerMessage;

public class RemoveFileFromPlayListAction extends Action implements IWorkbenchAction {

	private static final String ID = "de.ptb.epics.eve.viewer.actions.RemoveFileFromPlayListAction";  
	
	private PlayListView playListView;
	
	public RemoveFileFromPlayListAction( final PlayListView playListView ) {
		this.playListView = playListView;
		this.setId( RemoveFileFromPlayListAction.ID );  
	} 
	
	public void run() {  
		TableViewer tableViewer = this.playListView.getTableViewer();
		TableItem[] selectedItems = tableViewer.getTable().getSelection();
		for( int i = 0; i < selectedItems.length; ++i ) {
			final PlayListEntry entry = (PlayListEntry)selectedItems[i].getData();
			Activator.getDefault().getMessagesContainer().addMessage( new ViewerMessage( MessageTypes.INFO, "Removing entry: id = " + entry.getId() + " name = " + entry.getName() + " author " + entry.getAuthor() + "." ) );
			Activator.getDefault().getEcp1Client().getPlayListController().removePlayListEntry( entry );
		}
		
	}   
	
	public void dispose() {
		// TODO Auto-generated method stub

	}

}
