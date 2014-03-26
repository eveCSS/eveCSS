package de.ptb.epics.eve.editor.gef.actions;

import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.commands.CompoundCommand;
import org.eclipse.gef.ui.actions.Clipboard;
import org.eclipse.gef.ui.actions.PasteTemplateAction;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.actions.ActionFactory;

import de.ptb.epics.eve.data.scandescription.Chain;
import de.ptb.epics.eve.data.scandescription.ScanModule;
import de.ptb.epics.eve.data.scandescription.ScanModuleTypes;
import de.ptb.epics.eve.editor.Activator;
import de.ptb.epics.eve.editor.gef.ScanDescriptionEditor;
import de.ptb.epics.eve.editor.gef.commands.CopyScanModuleAxes;
import de.ptb.epics.eve.editor.gef.commands.CopyScanModuleChannels;
import de.ptb.epics.eve.editor.gef.commands.CopyScanModulePlotWindows;
import de.ptb.epics.eve.editor.gef.commands.CopyScanModulePositionings;
import de.ptb.epics.eve.editor.gef.commands.CopyScanModulePostScans;
import de.ptb.epics.eve.editor.gef.commands.CopyScanModulePreScans;
import de.ptb.epics.eve.editor.gef.commands.CopyScanModuleProperties;
import de.ptb.epics.eve.editor.gef.commands.CreateScanModule;

/**
 * @author Marcus Michalsky
 * @since 1.19
 */
public class Paste extends PasteTemplateAction {
	private static final Logger LOGGER = Logger.getLogger(Paste.class.getName());
	
	/**
	 * Constructor.
	 * 
	 * @param editor the editor
	 */
	public Paste(IWorkbenchPart editor) {
		super(editor);
		setActionDefinitionId("org.eclipse.ui.edit.paste"); //$NON-NLS-1$
		setId(ActionFactory.PASTE.getId());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected boolean calculateEnabled() {
		if (Clipboard.getDefault().getContents() != null) {
			LOGGER.debug("Paste enabled");
			return true;
		}
		LOGGER.debug("Paste disabled");
		return false;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings("unchecked")
	@Override
	protected Command createPasteCommand() {
		LOGGER.debug("create paste command");
		Object o = Clipboard.getDefault().getContents();
		if (o == null) {
			LOGGER.error("Paste not possible, Clipboard is empty!");
			return null;
		}
		
		CompoundCommand pasteCommand = new CompoundCommand();
		
		IEditorPart editor = Activator.getDefault().getWorkbench()
				.getActiveWorkbenchWindow().getActivePage().getActiveEditor();
		if (!(editor instanceof ScanDescriptionEditor)) {
			LOGGER.error("No Editor active!");
			return null;
		}
		
		Chain chain = ((ScanDescriptionEditor) editor).getContent().getChains()
				.get(0);
		chain.setReserveIds(true);
		
		for (ScanModule sm : ((List<ScanModule>)o)) {
			LOGGER.debug("should clone and add SM " + sm.getName() + "here.");
			CreateScanModule createCommand = new CreateScanModule(chain,
					new Rectangle(100, 100, 0, 0), ScanModuleTypes.CLASSIC);
			
			CopyScanModuleProperties propertiesCommand = 
					new CopyScanModuleProperties(
							sm, createCommand.getScanModule());
			CopyScanModuleAxes axesCommand = 
					new CopyScanModuleAxes(
							sm, createCommand.getScanModule());
			CopyScanModuleChannels channelsCommand = 
					new CopyScanModuleChannels(
							sm, createCommand.getScanModule());
			CopyScanModulePreScans prescanCommand =
					new CopyScanModulePreScans(
							sm, createCommand.getScanModule());
			CopyScanModulePostScans postscanCommand = 
					new CopyScanModulePostScans(
							sm, createCommand.getScanModule());
			CopyScanModulePositionings positioningCommand = 
					new CopyScanModulePositionings(
							sm, createCommand.getScanModule());
			CopyScanModulePlotWindows plotWindowCommand = 
					new CopyScanModulePlotWindows(
							sm, createCommand.getScanModule());
			
			pasteCommand.add(createCommand.
					chain(propertiesCommand).
					chain(axesCommand).
					chain(channelsCommand).
					chain(prescanCommand).
					chain(postscanCommand).
					chain(positioningCommand).
					chain(plotWindowCommand));
		}
		
		chain.resetReservedIds();
		chain.setReserveIds(false);
		return pasteCommand;
	}
	
	/**
	 * Refreshes the enabled state.
	 */
	public void refresh() {
		super.refresh();
	}
}