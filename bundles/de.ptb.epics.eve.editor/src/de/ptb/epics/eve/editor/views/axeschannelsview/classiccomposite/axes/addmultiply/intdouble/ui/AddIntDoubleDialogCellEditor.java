package de.ptb.epics.eve.editor.views.axeschannelsview.classiccomposite.axes.addmultiply.intdouble.ui;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

import de.ptb.epics.eve.data.scandescription.Axis;
import de.ptb.epics.eve.editor.views.axeschannelsview.classiccomposite.axes.addmultiply.intdouble.AddIntDoubleTextValidator;
import de.ptb.epics.eve.editor.views.axeschannelsview.classiccomposite.ui.ValuesDialogCellEditor;

/**
 * @author Marcus Michalsky
 * @since 1.34
 */
public class AddIntDoubleDialogCellEditor extends ValuesDialogCellEditor {
	
	public AddIntDoubleDialogCellEditor(Composite parent, Axis axis) {
		super(parent, axis);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Object openDialogBox(Control cellEditorWindow) {
		new AddIntDoubleDialog(cellEditorWindow.getShell(), getControl(), 
				getAxis()).open();
		return ""; //null;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected String validate(String value) {
		return (new AddIntDoubleTextValidator(getAxis())).isValid(value);
	}
}
