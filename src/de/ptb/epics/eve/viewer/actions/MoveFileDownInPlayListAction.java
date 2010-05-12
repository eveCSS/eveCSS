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

public class MoveFileDownInPlayListAction extends Action implements IWorkbenchAction {

	private static final String ID = "de.ptb.epics.eve.viewer.actions.MoveFileDownInPlayListAction";  
	
	private PlayListView playListView;
	
	public MoveFileDownInPlayListAction( final PlayListView playListView ){  
		this.setId( MoveFileDownInPlayListAction.ID );
		this.playListView = playListView;
	} 
	
	public void run() {  
		TableViewer tableViewer = this.playListView.getTableViewer();
		TableItem[] selectedItems = tableViewer.getTable().getSelection();
		for( int i = 0; i < selectedItems.length; ++i ) {
			final PlayListEntry entry = (PlayListEntry)selectedItems[i].getData();
			Activator.getDefault().getMessagesContainer().addMessage( new ViewerMessage( MessageTypes.INFO, "Moving down entry: id = " + entry.getId() + " name = " + entry.getName() + " author " + entry.getAuthor() + "." ) );
			Activator.getDefault().getEcp1Client().getPlayListController().movePlayListEntry( entry, -1 );
		}
	} 
	
	public void dispose() {
		// TODO Auto-generated method stub

	}

}
