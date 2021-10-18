package de.ptb.epics.eve.util.ui.jface;

import org.eclipse.jface.fieldassist.ControlDecoration;
import org.eclipse.jface.fieldassist.FieldDecorationRegistry;
import org.eclipse.jface.viewers.ICellEditorListener;
import org.eclipse.jface.viewers.ICellEditorValidator;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;

/**
 * @author Marcus Michalsky
 * @since 1.36
 */
public class TextCellEditorWithValidator extends TextCellEditor {
	private Image errorImage = FieldDecorationRegistry.getDefault().
			getFieldDecoration(FieldDecorationRegistry.DEC_ERROR).getImage();
	
	private final ControlDecoration decoration;
	
	public TextCellEditorWithValidator(Composite parent, ICellEditorValidator validator) {
		super(parent);
		if (validator == null) {
			throw new IllegalArgumentException("Validator must not be null");
		}
		
		this.decoration = new ControlDecoration(getControl(), SWT.LEFT);
		this.setValidator(validator);
		this.addListener(new ICellEditorListener() {
			@Override
			public void editorValueChanged(boolean oldValidState, boolean newValidState) {
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
				// nothing to do here
			}
			
			@Override
			public void applyEditorValue() {
				// nothing to do here
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
	}
}
