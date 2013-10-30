package de.ptb.epics.eve.viewer.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.actions.ActionFactory.IWorkbenchAction;

import de.ptb.epics.eve.ecp1.client.model.PlayListEntry;
import de.ptb.epics.eve.viewer.Activator;
import de.ptb.epics.eve.viewer.views.messages.Levels;
import de.ptb.epics.eve.viewer.views.messages.ViewerMessage;
import de.ptb.epics.eve.viewer.views.playlistview.PlayListView;

/**
 * <code>MoveFileDownInPlayListAction</code>.
 * 
 * @author ?
 * @author Marcus Michalsky
 */
public class MoveFileDownInPlayListAction extends Action 
												implements IWorkbenchAction {

	//
	private static final String ID = 
		"de.ptb.epics.eve.viewer.actions.MoveFileDownInPlayListAction";
	
	//
	private PlayListView playListView;
	
	/**
	 * Constructs a <code>MoveFileDownInPlayListAction</code>.
	 * 
	 * @param playListView the view where the action is registered (triggered)
	 */
	public MoveFileDownInPlayListAction(final PlayListView playListView){
		this.setId(MoveFileDownInPlayListAction.ID);
		this.playListView = playListView;
	} 
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void run() {
		TableViewer tableViewer = this.playListView.getTableViewer();
		TableItem[] selectedItems = tableViewer.getTable().getSelection();
		for(int i = selectedItems.length - 1; i >= 0; --i) {
			final PlayListEntry entry = (PlayListEntry)selectedItems[i].getData();
			Activator.getDefault().getMessagesContainer().addMessage(
					new ViewerMessage(Levels.INFO, 
									  "Moving down entry:" +
									  " id = " + entry.getId() + 
									  " name = " + entry.getName() + 
									  " author " + entry.getAuthor() + 
									  "."));
			Activator.getDefault().getEcp1Client().getPlayListController().
					movePlayListEntry(entry, 1);
		}
	} 
	
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void dispose() {
	}
}