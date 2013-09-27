package de.ptb.epics.eve.editor.gef;

import org.eclipse.gef.ui.actions.ActionBarContributor;
import org.eclipse.gef.ui.actions.DeleteRetargetAction;
import org.eclipse.gef.ui.actions.GEFActionConstants;
import org.eclipse.gef.ui.actions.RedoRetargetAction;
import org.eclipse.gef.ui.actions.UndoRetargetAction;
import org.eclipse.gef.ui.actions.ZoomComboContributionItem;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.ui.actions.ActionFactory;

import de.ptb.epics.eve.editor.Activator;

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
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void contributeToToolBar(IToolBarManager toolBarManager) {
		//toolBarManager.add(getAction(ActionFactory.UNDO.getId()));
		//toolBarManager.add(getAction(ActionFactory.REDO.getId()));
		//toolBarManager.add(getAction(ActionFactory.DELETE.getId()));
		//toolBarManager.add(new Separator());
		toolBarManager.add(new ZoomComboContributionItem(getPage()) {
			@Override
			public boolean isVisible() {
				if (Activator.getDefault().getWorkbench()
						.getActiveWorkbenchWindow().getActivePage() == null) {
					return false;
				}
				return Activator.getDefault().getWorkbench()
						.getActiveWorkbenchWindow().getActivePage()
						.getPerspective().getId().equals("EveEditorPerpective");
			}
		});
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void buildActions() {
		addRetargetAction(new DeleteRetargetAction());
		addRetargetAction(new UndoRetargetAction());
		addRetargetAction(new RedoRetargetAction());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void declareGlobalActionKeys() {
		addGlobalActionKey(ActionFactory.SELECT_ALL.getId());
		addGlobalActionKey(ActionFactory.PRINT.getId());
		addGlobalActionKey(GEFActionConstants.ZOOM_IN);
		addGlobalActionKey(GEFActionConstants.ZOOM_OUT);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void contributeToMenu(IMenuManager menuManager) {
	}
}