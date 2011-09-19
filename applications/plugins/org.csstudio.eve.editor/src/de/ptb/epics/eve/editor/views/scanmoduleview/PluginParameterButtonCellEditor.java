package de.ptb.epics.eve.editor.views.scanmoduleview;

import org.eclipse.jface.viewers.DialogCellEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

import de.ptb.epics.eve.data.scandescription.PluginController;
import de.ptb.epics.eve.editor.dialogs.PluginControllerDialog;

/**
 * <code>PluginParameterButtonCellEditor</code>.
 * 
 * @author ?
 * @author Marcus Michalsky
 */
public class PluginParameterButtonCellEditor extends DialogCellEditor {

	/**
	 * Constructs a <code>PluginParameterButtonCellEditor</code>.
	 * 
	 * @param parent the parent composite
	 */
	public PluginParameterButtonCellEditor(final Composite parent) {
		super(parent);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Button createButton(final Composite parent) {
		final Button button = new Button(parent, SWT.PUSH); 
		button.setText("Edit");
		return button;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Object openDialogBox(final Control cellEditorWindow) {
		PluginControllerDialog dialog = 
			new PluginControllerDialog(null, (PluginController)this.getValue());
		dialog.setBlockOnOpen(true);
		dialog.open();
		return null;
	}
}