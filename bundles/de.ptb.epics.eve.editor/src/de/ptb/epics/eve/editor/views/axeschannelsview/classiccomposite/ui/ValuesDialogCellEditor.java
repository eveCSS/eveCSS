package de.ptb.epics.eve.editor.views.axeschannelsview.classiccomposite.ui;

import org.apache.log4j.Logger;
import org.eclipse.core.databinding.validation.IValidator;
import org.eclipse.jface.fieldassist.ControlDecoration;
import org.eclipse.jface.fieldassist.FieldDecorationRegistry;
import org.eclipse.jface.viewers.DialogCellEditor;
import org.eclipse.jface.viewers.ICellEditorListener;
import org.eclipse.jface.viewers.ICellEditorValidator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.TraverseEvent;
import org.eclipse.swt.events.TraverseListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Text;

import com.ibm.icu.text.MessageFormat;

import de.ptb.epics.eve.data.scandescription.Axis;

/**
 * Superclass to achieve common behavior between DialogCellEditors with additional
 * text editing support. Creates an editable text field with decoration instead 
 * of the default label. Subclasses must implement {@link #validate(String)}, 
 * {@link #decorate(boolean, boolean)} and {@link #openDialogBox(Control)}.
 * 
 * @author Marcus Michalsky
 * @since 1.34
 */
public abstract class ValuesDialogCellEditor extends DialogCellEditor {
	private static final Logger LOGGER = Logger.getLogger(
			ValuesDialogCellEditor.class.getName());
	
	private FieldDecorationRegistry registry = 
			FieldDecorationRegistry.getDefault();
	private Image warnImage = registry.getFieldDecoration(
			FieldDecorationRegistry.DEC_WARNING).getImage();
	private Image errorImage = registry.getFieldDecoration(
			FieldDecorationRegistry.DEC_ERROR).getImage();
	
	private Axis axis;
	private Text valuesText;
	private final ControlDecoration decoration;
	
	public ValuesDialogCellEditor(Composite parent, Axis axis) {
		super(parent, SWT.NONE);
		this.axis = axis;
		
		this.decoration = new ControlDecoration(getControl(), SWT.LEFT);
		this.setValidator(new ICellEditorValidator() {
			@Override
			public String isValid(Object value) {
				LOGGER.debug("validate: " + (String)value);
				return validate((String)value);
			}
		});
		this.addListener(new ICellEditorListener() {
			@Override
			public void editorValueChanged(boolean oldValidState, 
					boolean newValidState) {
				LOGGER.debug("value changed - old sate: " + oldValidState + 
						", new state: " + newValidState);
				decorate(oldValidState, newValidState);
			}

			@Override
			public void applyEditorValue() {
				// nothing to do here
			}

			@Override
			public void cancelEditor() {
				// nothing to do here
			}
		});
	}
	
	/**
	 * A {@link ICellEditorListener} is automatically registered. Changes will 
	 * call this function. It should set the decoration according to the given 
	 * valid states. The default implementation shows an error icon with tooltip
	 * error text if newValidState is false. Override for specific behavior.
	 * 
	 * Use {@link #getDecoration()} to get the text field decorator.
	 * Use {@link #getWarnImage()} and {@link #getErrorImage()} for images.
	 * 
	 * @param oldValidState the valid state before the end user changed the value
	 * @param newValidState the current valid state
	 */
	protected void decorate(boolean oldValidState, boolean newValidState) {
		if (!newValidState) {
			this.decoration.setDescriptionText(getErrorMessage());
			this.decoration.setImage(errorImage);
			this.decoration.show();
		} else {
			this.decoration.setDescriptionText("");
			this.decoration.setImage(null);
			this.decoration.hide();
		}
	}
	
	/**
	 * A {@link IValidator} is automatically registered. It forwards the call to
	 * this method. Override accordingly.
	 * 
	 * Use {@link #getAxis()} to get the axis.
	 * 
	 * @param value the value to be validated
	 * @return the error message, or null indicating that the value is valid
	 */
	protected abstract String validate(String value);
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Control createContents(Composite cell) {
		this.valuesText = new Text(cell, SWT.NONE);
		valuesText.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				LOGGER.debug("key Pressed: " + e.keyCode);
				keyReleaseOccured(e);
			}
		});
		valuesText.addTraverseListener(new TraverseListener() {
			@Override
			public void keyTraversed(TraverseEvent e) {
				LOGGER.debug("Traverse: Keycode=" + e.keyCode);
				e.doit = false;
			}
		});
		valuesText.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				boolean oldValidState = isValueValid();
				boolean newValidState = isCorrect(valuesText.getText());
				LOGGER.debug("modify event - old state: " + oldValidState + 
						", new state: " + newValidState);
				if (!newValidState) {
					setErrorMessage(MessageFormat.format(getErrorMessage(), 
							valuesText.getText()));
				}
				valueChanged(oldValidState, newValidState);
			}
		});
		return valuesText;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void keyReleaseOccured(KeyEvent keyEvent) {
		if (keyEvent.keyCode == SWT.ESC) {
			LOGGER.debug("Escape pressed -> Cancel Editing");
			fireCancelEditor();
		}
		if ((keyEvent.keyCode == SWT.CR || keyEvent.keyCode == SWT.KEYPAD_CR) 
				&& isCorrect(valuesText.getText())) {
			LOGGER.debug("CR pressed (and valid text); -> Apply Value");
			setValue();
			fireApplyEditorValue();
		}
	}
	
	private void setValue() {
		String value = valuesText.getText();
		if (isCorrect(value)) {
			LOGGER.debug("value correct -> set to model");
			markDirty();
			doSetValue(value);
		} else {
			LOGGER.debug(getErrorMessage());
			setErrorMessage(MessageFormat.format(getErrorMessage(), value));
		}
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void updateContents(Object value) {
		if (valuesText == null) {
			return;
		}
		if (value != null) {
			valuesText.setText(value.toString());
		} else {
			valuesText.setText("");
		}
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void doSetFocus() {
		LOGGER.debug("set focus to text");
		this.valuesText.setFocus();
		this.valuesText.selectAll();
	}

	protected Axis getAxis() {
		return this.axis;
	}
	
	protected Text getValuesText() {
		return this.valuesText;
	}
	
	protected ControlDecoration getDecoration() {
		return this.decoration;
	}
	
	protected Image getWarnImage() {
		return this.warnImage;
	}
	
	protected Image getErrorImage() {
		return this.errorImage;
	}
}
