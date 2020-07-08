package de.ptb.epics.eve.editor.views.axeschannelsview.classiccomposite.axes.addmultiply.datetime.ui;

import org.eclipse.jface.viewers.DialogCellEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

import de.ptb.epics.eve.data.scandescription.Axis;

/**
 * @author Marcus Michalsky
 * @since 1.34
 */
public class AddDateTimeDialogCellEditor extends DialogCellEditor {
	private Axis axis;
	
	public AddDateTimeDialogCellEditor(Composite parent, Axis axis) {
		super(parent, SWT.NONE);
		this.axis = axis;
	}
	
	@Override
		protected void doSetFocus() {
			//openDialogBox(getControl());
			// TODO Auto-generated method stub
			super.doSetFocus();
		}
	
	@Override
	protected Object openDialogBox(Control cellEditorWindow) {
		return new AddDateTimeDialog(cellEditorWindow.getShell(), getControl(), axis).open();
		// TODO Auto-generated method stub
		//focusLost();
		// return null;
	}
}
