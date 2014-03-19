package de.ptb.epics.eve.editor.gef.actions;

import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.ui.actions.Clipboard;
import org.eclipse.gef.ui.actions.PasteTemplateAction;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.actions.ActionFactory;

import de.ptb.epics.eve.data.scandescription.ScanModule;

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
		for (ScanModule sm : ((List<ScanModule>)o)) {
			LOGGER.debug("should clone and add SM " + sm.getName() + "here.");
			// TODO create CompoundCommand
		}
		return super.createPasteCommand();
	}
	
	/**
	 * Refreshes the enabled state.
	 */
	public void refresh() {
		super.refresh();
	}
}