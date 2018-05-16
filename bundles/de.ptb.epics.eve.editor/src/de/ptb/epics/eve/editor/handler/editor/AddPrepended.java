package de.ptb.epics.eve.editor.handler.editor;

import org.apache.log4j.Logger;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.RequestConstants;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.commands.CommandStack;
import org.eclipse.gef.requests.ChangeBoundsRequest;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.handlers.HandlerUtil;

import de.ptb.epics.eve.data.scandescription.Connector;
import de.ptb.epics.eve.data.scandescription.ScanModule;
import de.ptb.epics.eve.data.scandescription.ScanModuleTypes;
import de.ptb.epics.eve.data.scandescription.StartEvent;
import de.ptb.epics.eve.editor.gef.ScanDescriptionEditor;
import de.ptb.epics.eve.editor.gef.commands.CreateSEConnection;
import de.ptb.epics.eve.editor.gef.commands.CreateSMConnection;
import de.ptb.epics.eve.editor.gef.commands.CreateScanModule;
import de.ptb.epics.eve.editor.gef.commands.DeleteConnection;
import de.ptb.epics.eve.editor.gef.commands.MoveScanModule;
import de.ptb.epics.eve.editor.gef.editparts.ScanModuleEditPart;

/**
 * @author Marcus Michalsky
 * @since 1.30
 */
public class AddPrepended extends AbstractHandler {
	private static final Logger LOGGER = Logger.getLogger(
			AddPrepended.class.getName());
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		ISelection selection = HandlerUtil.getCurrentSelection(event);
		if (!(selection instanceof IStructuredSelection)) {
			LOGGER.error("selection is not compatible");
			return null; // not compatible
		}
		if (((IStructuredSelection) selection).size() == 0) {
			LOGGER.error("selection is empty");
			return null; // nothing selected
		}
		
		ScanModule scanModule = null;
		Object element = ((IStructuredSelection)selection).getFirstElement();
		if (element instanceof ScanModuleEditPart) {
			scanModule = ((ScanModuleEditPart)element).getModel();
		} else {
			LOGGER.error("no scan module selected");
			return null;
		}
		
		ScanDescriptionEditor editor = null;
		if (HandlerUtil.getActiveEditor(event) instanceof ScanDescriptionEditor) {
			editor = (ScanDescriptionEditor) HandlerUtil.getActiveEditor(event);
		} else {
			LOGGER.error("editor not found");
			return null;
		}
		
		boolean append = event.getParameter(
				"de.ptb.epics.eve.editor.command.addprepended.dowithcurrent").
					equals("append");
		boolean nest = event.getParameter(
				"de.ptb.epics.eve.editor.command.addprepended.dowithcurrent").
				equals("nest");
		
		CommandStack commandStack = editor.getCommandStack();
		Command compositeCmd = null;
		
		// the new scan module has to be created in all cases
		CreateScanModule createCmd = new CreateScanModule(
				scanModule.getChain(), new Rectangle(
						scanModule.getX(), scanModule.getY(),
						scanModule.getWidth(), scanModule.getHeight()),
				ScanModuleTypes.CLASSIC);
		
		compositeCmd = createCmd;
		
		// if the current SM has a parent 
		// - remember the parent and type (appended or nested)
		// - delete the connection
		// - create a new connection between the parent and the new scan module
		if (scanModule.getParent() != null) {
			StartEvent parentEvent = scanModule.getParent().getParentEvent();
			ScanModule parentScanModule = scanModule.getParent().
					getParentScanModule();
			String connectionType = null;
			if (parentScanModule != null) {
				if (parentScanModule.getAppended() != null && 
						parentScanModule.getAppended().equals(
								scanModule.getParent())) {
					connectionType = Connector.APPENDED;
				} else if (parentScanModule.getNested() != null && 
						parentScanModule.getNested().equals(
								scanModule.getParent())) {
					connectionType = Connector.NESTED;
				}
			}
			compositeCmd = compositeCmd.chain(
					new DeleteConnection(scanModule.getParent()));
			if (parentEvent != null) {
				compositeCmd = compositeCmd.chain(new CreateSEConnection(
						parentEvent, createCmd.getScanModule()));
			} else if (parentScanModule != null) {
				compositeCmd = compositeCmd.chain(new CreateSMConnection(
						parentScanModule, createCmd.getScanModule(), 
						connectionType));
			}
		}
		
		// connect the new scanmodule (the prepended) with the selected one
		if (append) {
			compositeCmd = compositeCmd.chain(new CreateSMConnection(
					createCmd.getScanModule(), scanModule, Connector.APPENDED));
			appendMoveCommands(compositeCmd, scanModule, 130, 0);
		} else if (nest) {
			compositeCmd = compositeCmd.chain(new CreateSMConnection(
					createCmd.getScanModule(), scanModule, Connector.NESTED));
			appendMoveCommands(compositeCmd, scanModule, 130, 100);
		}
		
		commandStack.execute(compositeCmd);
		return null;
	}
	
	private void appendMoveCommands(Command cmd, ScanModule root, 
			int offsetX, int offsetY) {
		cmd = cmd.chain(new MoveScanModule(root, new Rectangle(
				root.getX() + offsetX, root.getY() + offsetY, 
				root.getWidth(), root.getHeight()), 
				new ChangeBoundsRequest(RequestConstants.REQ_MOVE)));
		if (root.getAppended() != null) {
			appendMoveCommands(cmd, root.getAppended().getChildScanModule(), 
					offsetX, offsetY);
		}
		if (root.getNested() != null) {
			appendMoveCommands(cmd, root.getNested().getChildScanModule(), 
					offsetX, offsetY);
		}
	}
}