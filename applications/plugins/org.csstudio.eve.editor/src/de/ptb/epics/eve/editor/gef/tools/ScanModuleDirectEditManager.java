package de.ptb.epics.eve.editor.gef.tools;

import org.eclipse.gef.tools.DirectEditManager;
import org.eclipse.jface.viewers.TextCellEditor;

import de.ptb.epics.eve.editor.gef.editparts.ScanModuleEditPart;

/**
 * @author Marcus Michalsky
 * @since 1.6
 */
public class ScanModuleDirectEditManager extends DirectEditManager {
	
	private ScanModuleEditPart scanModuleEditPart;
	
	/**
	 * Constructor.
	 * 
	 * @param scanModuleEditPart the edit part to edit
	 * @param editorType determines what type of CellEditor will be created
	 * @param locator the locator
	 */
	public ScanModuleDirectEditManager(ScanModuleEditPart scanModuleEditPart,
			Class<TextCellEditor> editorType,
			ScanModuleCellEditorLocator locator) {
		super(scanModuleEditPart, editorType, locator);
		this.scanModuleEditPart = scanModuleEditPart;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void initCellEditor() {
		this.getCellEditor().setValue(
				this.scanModuleEditPart.getModel().getName());
	}
}