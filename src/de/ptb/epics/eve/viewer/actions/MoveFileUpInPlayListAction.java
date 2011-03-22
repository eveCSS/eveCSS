package de.ptb.epics.eve.viewer.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.actions.ActionFactory.IWorkbenchAction;

import de.ptb.epics.eve.ecp1.client.model.PlayListEntry;
import de.ptb.epics.eve.viewer.Activator;
import de.ptb.epics.eve.viewer.MessageSource;
import de.ptb.epics.eve.viewer.PlayListView;
import de.ptb.epics.eve.viewer.messages.MessageTypes;
import de.ptb.epics.eve.viewer.messages.ViewerMessage;

public class MoveFileUpInPlayListAction extends Action implements IWorkbenchAction {

	private static final String ID = "de.ptb.epics.eve.viewer.actions.MoveFileUpInPlayListAction";  
	
	private PlayListView playListView;
	
	public MoveFileUpInPlayListAction( final PlayListView playListView ){  
		this.setId( MoveFileUpInPlayListAction.ID );
		this.playListView = playListView;
	} 
	
	public void run() {  
		TableViewer tableViewer = this.playListView.getTableViewer();
		TableItem[] selectedItems = tableViewer.getTable().getSelection();
		for( int i = selectedItems.length - 1; i >= 0; --i ) {
			final PlayListEntry entry = (PlayListEntry)selectedItems[i].getData();
			Activator.getDefault().getMessagesContainer().addMessage( new ViewerMessage(MessageTypes.INFO, "Moving up entry: id = " + entry.getId() + " name = " + entry.getName() + " author " + entry.getAuthor() + "." ) );
			Activator.getDefault().getEcp1Client().getPlayListController().movePlayListEntry( entry, 1 );
		}
	} 
	
	public void dispose() {
		// TODO Auto-generated method stub

	}

}
