package de.ptb.epics.eve.editor.views.axeschannelsview.classiccomposite.axes.addmultiply.intdouble.ui;

import org.eclipse.jface.fieldassist.ControlDecoration;
import org.eclipse.jface.fieldassist.FieldDecorationRegistry;
import org.eclipse.jface.viewers.ICellEditorListener;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;

import de.ptb.epics.eve.data.scandescription.Axis;
import de.ptb.epics.eve.editor.views.axeschannelsview.classiccomposite.axes.addmultiply.intdouble.AddIntDoubleTextValidator;

/**
 * @author Marcus Michalsky
 * @since 1.34
 */
public class AddIntDoubleTextCellEditor extends TextCellEditor {
	private FieldDecorationRegistry registry = 
			FieldDecorationRegistry.getDefault();
	private Image errorImage = registry.getFieldDecoration(
			FieldDecorationRegistry.DEC_ERROR).getImage();
	
	private final ControlDecoration decoration;
	private final Axis axis;
	
	public AddIntDoubleTextCellEditor(Composite parent, Axis axis) {
		super(parent, SWT.NONE);
		this.axis = axis;
		this.decoration = new ControlDecoration(getControl(), SWT.LEFT);
		this.setValidator(new AddIntDoubleTextValidator());
		this.addListener(new ICellEditorListener() {
			@Override
			public void editorValueChanged(boolean oldValidState, boolean newValidState) {
				// TODO Auto-generated method stub
				if (!newValidState) {
					decoration.setDescriptionText(getErrorMessage());
					decoration.setImage(errorImage);
					decoration.show();
				} else {
					decoration.setDescriptionText("");
					decoration.setImage(null);
					decoration.hide();
				}
			}
			
			@Override
			public void cancelEditor() {
				// nothing to do
			}
			
			@Override
			public void applyEditorValue() {
				// nothing to do
			}
		});
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void focusLost() {
		if (isActivated()) {
			fireCancelEditor();
		}
		deactivate();
		// TODO Auto-generated method stub
		//super.focusLost();
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void handleDefaultSelection(SelectionEvent event) {
		if (!isValueValid()) {
			// deny apply with enter if content is invalid
			event.doit = false;
			return;
		}
		super.handleDefaultSelection(event);
	}
}
