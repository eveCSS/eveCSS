package de.ptb.epics.eve.editor.views.axeschannelsview.classiccomposite.axes.plugin.ui;

import org.eclipse.jface.viewers.DialogCellEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

import de.ptb.epics.eve.data.scandescription.Axis;

/**
 * @author Marcus Michalsky
 * @since 1.34
 */
public class PluginDialogCellEditor extends DialogCellEditor {
	private Axis axis;
	
	public PluginDialogCellEditor(Composite parent, Axis axis) {
		super(parent, SWT.NONE);
		this.axis = axis;
	}
	
	@Override
	protected void doSetFocus() {
		//openDialogBox(getControl());
		// TODO Auto-generated method stub
		super.doSetFocus();
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Object openDialogBox(Control cellEditorWindow) {
		new PluginDialog(cellEditorWindow.getShell(), getControl(), axis).open();
		// TODO focusLost() ?
		//focusLost();
		// TODO Auto-generated method stub
		return "Plugin (" + axis.getPluginController().getPlugin().getName() + ")";
		//return null;
	}
}
