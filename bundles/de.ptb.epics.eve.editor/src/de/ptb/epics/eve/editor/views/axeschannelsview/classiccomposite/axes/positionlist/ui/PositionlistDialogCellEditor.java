package de.ptb.epics.eve.editor.views.axeschannelsview.classiccomposite.axes.positionlist.ui;

import org.apache.log4j.Logger;
import org.eclipse.jface.fieldassist.ControlDecoration;
import org.eclipse.jface.fieldassist.FieldDecorationRegistry;
import org.eclipse.jface.viewers.DialogCellEditor;
import org.eclipse.jface.viewers.ICellEditorListener;
import org.eclipse.jface.viewers.ICellEditorValidator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
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
import de.ptb.epics.eve.util.io.StringUtil;

/**
 * @author Marcus Michalsky
 * @since 1.34
 */
public class PositionlistDialogCellEditor extends DialogCellEditor {
	private static final Logger LOGGER = Logger.getLogger(
			PositionlistDialogCellEditor.class.getName());
	
	private FieldDecorationRegistry registry = 
			FieldDecorationRegistry.getDefault();
	private Image errorImage = registry.getFieldDecoration(
			FieldDecorationRegistry.DEC_ERROR).getImage();
	
	private final Axis axis;
	private Text valuesText;
	
	public PositionlistDialogCellEditor(Composite parent, Axis axis) {
		super(parent, SWT.NONE);
		this.axis = axis;
		
		LOGGER.debug("constructing new PositionlistDialogCellEditor");

		final ControlDecoration decoration = new ControlDecoration(getControl(), SWT.LEFT);
		this.setValidator(new ICellEditorValidator() {
			@Override
			public String isValid(Object value) {
				LOGGER.debug("validate " + (String)value);
				LOGGER.debug("Validation: " + validate((String)value));
				return validate((String)value);
			}
		});
		this.addListener(new ICellEditorListener() {
			@Override
			public void editorValueChanged(boolean oldValidState, boolean newValidState) {
				LOGGER.debug("editorValueChanged");
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
	
	/*
	 * empty return value = valid position list. Otherwise string contains error
	 * message.
	 */
	private String validate(String positions) {
		if (positions == null) {
			return null;
		}
		if (positions.isEmpty()) {
			return "Positionlist is empty.";
		}
		boolean valid = false;
		switch (axis.getType()) {
		case DOUBLE:
			valid = StringUtil.isPositionList(positions, Double.class);
			break;
		case INT:
			valid = StringUtil.isPositionList(positions, Integer.class);
			break;
		case STRING:
			valid = StringUtil.isPositionList(positions, String.class);
			break;
		default:
			return "Only axes of type int, double or string are supported.";
		}
		if (valid) {
			return null;
		}
		return "The list contains syntax errors";
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Object openDialogBox(Control cellEditorWindow) {
		new PositionlistDialog(cellEditorWindow.getShell(), getControl(), axis).open();
		return axis.getPositionlist();
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Control createContents(Composite cell) {
		LOGGER.debug("creating Content");
		valuesText = new Text(cell, SWT.NONE);
		valuesText.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent event) {
				LOGGER.debug("key Pressed: " + event.keyCode);
				keyReleaseOccured(event);
			}
		});
		valuesText.addTraverseListener(new TraverseListener() {
			@Override
			public void keyTraversed(TraverseEvent e) {
				LOGGER.debug("Traverse: Keycode=" + e.keyCode);
				e.doit = false;//true;
				//keyReleaseOccured(e);
			}
		});
		/*valuesText.addFocusListener(new FocusAdapter() {
			@Override
			public void focusLost(FocusEvent e) {
				LOGGER.debug("focus lost");
				//setValue();
				if (isActivated()) {
					fireCancelEditor();
				}
				deactivate();
			}
		});*/
		
		valuesText.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				boolean oldValidState = isValueValid();
				boolean newValidState = isCorrect(valuesText.getText());
				LOGGER.debug("modify event - oldState: " + oldValidState + 
						", newState: " + newValidState);
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
		LOGGER.debug("keyReleaseOccured");
		if (keyEvent.keyCode == SWT.ESC) {
			LOGGER.debug("Escape pressed -> Cancel Editing");
			fireCancelEditor();
			//deactivate();
		}
		if (keyEvent.keyCode == SWT.CR && isCorrect(valuesText.getText())) {
			LOGGER.debug("CR detected -> Apply Value and deactivate editor");
			setValue();
			fireApplyEditorValue();
			//deactivate();
		}
	}
	
	private void setValue() {
		String value = valuesText.getText();
		LOGGER.debug("setValue: " + value);
		if (isCorrect(value)) {
			LOGGER.debug("setValue: isCorrect");
			markDirty();
			doSetValue(value);
		} else {
			LOGGER.debug(getErrorMessage());
			setErrorMessage(MessageFormat.format(getErrorMessage(), value));
		}
	}
	
	@Override
	protected void updateContents(Object value) {
		LOGGER.debug("updateContents");
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
		LOGGER.debug("doSetFocus");
		valuesText.setFocus();
		valuesText.selectAll();
	}
	
	@Override
	protected void doSetValue(Object value) {
		LOGGER.debug("doSetValue: " + value);
		// TODO Auto-generated method stub
		super.doSetValue(value);
	}
	
	@Override
	protected boolean isCorrect(Object value) {
		LOGGER.debug("isCorrect: " + value);
		// TODO Auto-generated method stub
		return super.isCorrect(value);
	}
	
	@Override
	protected void fireApplyEditorValue() {
		LOGGER.debug("fireApplyEditorValue");
		// TODO Auto-generated method stub
		super.fireApplyEditorValue();
	}
	
	@Override
	protected void fireCancelEditor() {
		LOGGER.debug("fireCancelEditor");
		// TODO Auto-generated method stub
		super.fireCancelEditor();
	}
	
	@Override
	public void deactivate() {
		LOGGER.debug("deactivate");
		// TODO Auto-generated method stub
		super.deactivate();
	}
	
	@Override
	protected void focusLost() {
		LOGGER.debug("focus lost");
		// TODO Auto-generated method stub
		super.focusLost();
	}
}
