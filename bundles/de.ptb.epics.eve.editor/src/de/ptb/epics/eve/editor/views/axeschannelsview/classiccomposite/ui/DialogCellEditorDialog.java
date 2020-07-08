package de.ptb.epics.eve.editor.views.axeschannelsview.classiccomposite.ui;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IDialogLabelKeys;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

/**
 * A DialogCellEditorDialog is a dialog used when editing a table cell via 
 * DialogCellEditor. Common functions are grouped here, i.e.:
 * 
 * <ul>
 *   <li>the position where the dialog is shown (origin at origin of given control)</li>
 *   <li>a close button to close the dialog</li>
 *   <li>"x" closed the dialog</li>
 * </ul>
 * 
 * @author Marcus Michalsky
 * @since 1.34
 */
public abstract class DialogCellEditorDialog extends Dialog {
	private final Control control;
	
	public DialogCellEditorDialog(Shell shell, Control control) {
		super(shell);
		this.control = control;
	}
	
	@Override
	protected Point getInitialLocation(Point initialSize) {
		return this.control.toDisplay(0, 0);
	}
	
	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		createButton(parent, IDialogConstants.CLOSE_ID, 
			JFaceResources.getString(IDialogLabelKeys.CLOSE_LABEL_KEY), true);
	}
	
	@Override
	protected void buttonPressed(int buttonId) {
		if (buttonId == IDialogConstants.CLOSE_ID) {
			close();
		}
		super.buttonPressed(buttonId);
	}
}
