package de.ptb.epics.eve.editor.gef;

import org.eclipse.gef.ui.actions.ActionBarContributor;
import org.eclipse.gef.ui.actions.DeleteRetargetAction;
import org.eclipse.gef.ui.actions.RedoRetargetAction;
import org.eclipse.gef.ui.actions.UndoRetargetAction;
import org.eclipse.gef.ui.actions.ZoomComboContributionItem;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.actions.ActionFactory;

/**
 * @author Marcus Michalsky
 * @since 1.6
 */
public class ScanDescriptionEditorActionBarContributor extends
		ActionBarContributor {

	/**
	 * 
	 */
	public ScanDescriptionEditorActionBarContributor() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void contributeToToolBar(IToolBarManager toolBarManager) {
		//toolBarManager..insertAfter("de.ptb.epics.eve.editor.toolbar.zoom",
			//	new ZoomComboContributionItem(getPage()));
		toolBarManager.add(getAction(ActionFactory.UNDO.getId()));
		toolBarManager.add(getAction(ActionFactory.REDO.getId()));
		toolBarManager.add(getAction(ActionFactory.DELETE.getId()));
		toolBarManager.add(new Separator());
		toolBarManager.add(new ZoomComboContributionItem(getPage()));
	}

	@Override
	protected void buildActions() {
		// TODO Auto-generated method stub
		addRetargetAction(new DeleteRetargetAction());
		addRetargetAction(new UndoRetargetAction());
		addRetargetAction(new RedoRetargetAction());
	}

	@Override
	protected void declareGlobalActionKeys() {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void contributeToMenu(IMenuManager menuManager) {
	}
}
