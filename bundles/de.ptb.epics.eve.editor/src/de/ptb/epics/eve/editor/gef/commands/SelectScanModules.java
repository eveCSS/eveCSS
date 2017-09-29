package de.ptb.epics.eve.editor.gef.commands;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.gef.EditPart;
import org.eclipse.gef.commands.Command;

import de.ptb.epics.eve.data.scandescription.ScanModule;
import de.ptb.epics.eve.editor.gef.ScanDescriptionEditor;

/**
 * @author Marcus Michalsky
 * @since 1.19
 */
public class SelectScanModules extends Command {
	private List<EditPart> previousSelection;
	private ScanDescriptionEditor editor;
	private List<ScanModule> newSelection;
	
	/**
	 * Constructor.
	 * 
	 * @param editor the editor
	 * @param scanModules the scan modules to selecht
	 */
	@SuppressWarnings("unchecked")
	public SelectScanModules(ScanDescriptionEditor editor,
			List<ScanModule> scanModules) {
		this.previousSelection = new ArrayList<EditPart>(editor.getViewer()
				.getSelectedEditParts());
		this.editor = editor;
		this.newSelection = scanModules;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void execute() {
		editor.getViewer().deselectAll();
		for (ScanModule scanModule : this.newSelection) {
			EditPart part = (EditPart) editor.getViewer().getEditPartRegistry()
					.get(scanModule);
			editor.getViewer().reveal(part);
			editor.getViewer().appendSelection(part);
		}
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void undo() {
		for (ScanModule scanModule : this.newSelection) {
			EditPart part = (EditPart) editor.getViewer().getEditPartRegistry()
					.get(scanModule);
			editor.getViewer().deselect(part);
		}
		for (EditPart part : this.previousSelection) {
			editor.getViewer().appendSelection(part);
		}
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void redo() {
		this.execute();
	}
}