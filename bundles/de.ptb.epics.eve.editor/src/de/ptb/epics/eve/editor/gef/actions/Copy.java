package de.ptb.epics.eve.editor.gef.actions;


import org.apache.log4j.Logger;
import org.eclipse.gef.ui.actions.ActionRegistry;
import org.eclipse.gef.ui.actions.Clipboard;
import org.eclipse.gef.ui.actions.CopyTemplateAction;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.actions.ActionFactory;

import de.ptb.epics.eve.data.scandescription.Connector;
import de.ptb.epics.eve.editor.Activator;
import de.ptb.epics.eve.editor.gef.ClipboardContent;
import de.ptb.epics.eve.editor.gef.ScanDescriptionEditor;
import de.ptb.epics.eve.editor.gef.editparts.ConnectionEditPart;
import de.ptb.epics.eve.editor.gef.editparts.ScanModuleEditPart;

/**
 * @author Marcus Michalsky
 * @since 1.19
 */
public class Copy extends CopyTemplateAction {
	private static final Logger LOGGER = Logger.getLogger(Copy.class.getName());
	
	/**
	 * Constructor.
	 * 
	 * @param editor the editor
	 */
	public Copy(IEditorPart editor) {
		super(editor);
		setId(ActionFactory.COPY.getId());
		setActionDefinitionId("org.eclipse.ui.edit.copy"); //$NON-NLS-1$
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected boolean calculateEnabled() {
		ISelection selection = Activator.getDefault().getWorkbench()
				.getActiveWorkbenchWindow().getSelectionService()
				.getSelection();
		if (!(selection instanceof IStructuredSelection)) {
			return false;
		}
		if (((IStructuredSelection) selection).size() == 0) {
			return false;
		}
		// selection must contain at least one scan module
		for (Object o : ((IStructuredSelection)selection).toList()) {
			if (o instanceof ScanModuleEditPart) {
				LOGGER.debug("Copy enabled");
				return true;
			}
		}
		LOGGER.debug("Copy disabled");
		return false;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void run() {
		ISelection selection = Activator.getDefault().getWorkbench()
				.getActiveWorkbenchWindow().getSelectionService()
				.getSelection();
		if (!(selection instanceof IStructuredSelection) ||
				((IStructuredSelection) selection).size() == 0) {
			Clipboard.getDefault().setContents(null);
			LOGGER.debug("Clipboard cleared.");
			new Refresh().run();
			return;
		}
		ClipboardContent clipboardContent = new ClipboardContent();
		for (Object o : ((IStructuredSelection)selection).toList()) {
			if (o instanceof ScanModuleEditPart) {
				clipboardContent.getScanModules().add(
						((ScanModuleEditPart)o).getModel());
			}
		}
		for (Object o : ((IStructuredSelection)selection).toList()) {
			if (o instanceof ConnectionEditPart) {
				Connector conn = ((ConnectionEditPart)o).getModel();
				if (clipboardContent.getScanModules().contains(
						conn.getParentScanModule())
						&& clipboardContent.getScanModules().contains(
								conn.getChildScanModule())) {
					clipboardContent.getConnections().add(
							((ConnectionEditPart) o).getModel());
				}
			}
		}
		
		Clipboard.getDefault().setContents(clipboardContent);
		LOGGER.debug(clipboardContent.getScanModules().size() + 
				" Scanmodule(s) detected and added to clipboard.");
		LOGGER.debug(clipboardContent.getConnections().size() + 
				" Connection(s) detected and added to clipboard.");
		new Refresh().run();
	}
	
	private class Refresh implements Runnable {
		@Override
		public void run() {
			IAction pasteAction = ((ActionRegistry)(
					(ScanDescriptionEditor)getWorkbenchPart()).
					getAdapter(ActionRegistry.class)).
					getAction(ActionFactory.PASTE.getId());
			if(pasteAction != null) {
				((Paste)pasteAction).refresh();
			}
		}
	}
}