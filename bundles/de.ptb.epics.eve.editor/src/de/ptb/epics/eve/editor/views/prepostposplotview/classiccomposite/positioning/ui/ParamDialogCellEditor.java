package de.ptb.epics.eve.editor.views.prepostposplotview.classiccomposite.positioning.ui;

import org.eclipse.jface.viewers.DialogCellEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

import de.ptb.epics.eve.data.scandescription.PluginController;
import de.ptb.epics.eve.editor.dialogs.PluginControllerDialog;

/**
 * @author Marcus Michalsky
 * @since 1.35
 */
public class ParamDialogCellEditor extends DialogCellEditor {
	private Composite parent;
	
	public ParamDialogCellEditor(Composite parent) {
		super(parent, SWT.NONE);
		this.parent = parent;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Object openDialogBox(Control cellEditorWindow) {
		PluginControllerDialog dialog = new PluginControllerDialog(
				parent.getShell(), 
				(PluginController)getValue());
		dialog.setBlockOnOpen(true);
		return dialog.open();
	}
}
