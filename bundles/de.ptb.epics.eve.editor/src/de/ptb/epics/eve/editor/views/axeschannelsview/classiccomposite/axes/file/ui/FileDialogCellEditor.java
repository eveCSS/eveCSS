package de.ptb.epics.eve.editor.views.axeschannelsview.classiccomposite.axes.file.ui;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.IStatus;
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
import de.ptb.epics.eve.editor.views.axeschannelsview.classiccomposite.axes.file.FilenameValidator;
import de.ptb.epics.eve.util.io.FileUtil;
import de.ptb.epics.eve.util.io.StringUtil;

/**
 * @author Marcus Michalsky
 * @since 1.34
 */
public class FileDialogCellEditor extends DialogCellEditor {
	private static final Logger LOGGER = Logger.getLogger(
			FileDialogCellEditor.class.getName());
	
	private FieldDecorationRegistry registry = 
			FieldDecorationRegistry.getDefault();
	private Image warnImage = registry.getFieldDecoration(
			FieldDecorationRegistry.DEC_WARNING).getImage();
	private Image errorImage = registry.getFieldDecoration(
			FieldDecorationRegistry.DEC_ERROR).getImage();
	
	private Axis axis;
	private Text filenameText;
	
	public FileDialogCellEditor(Composite parent, Axis axis) {
		super(parent, SWT.NONE);
		this.axis = axis;
		
		final ControlDecoration decoration = new ControlDecoration(getControl(), 
				SWT.LEFT);
		this.setValidator(new ICellEditorValidator() {
			@Override
			public String isValid(Object value) {
				FilenameValidator validator = new FilenameValidator();
				IStatus status = validator.validate(value);
				if (status.isOK()) {
					return null;
				} else {
					return status.getMessage();
				}
			}
		});
		this.addListener(new ICellEditorListener() {
			@Override
			public void editorValueChanged(boolean oldValidState, boolean newValidState) {
				if (!newValidState) {
					decoration.setDescriptionText(getErrorMessage());
					String filename = filenameText.getText();
					File file = new File(filename);
					if (filename.isEmpty()) {
						decoration.setImage(errorImage);
					} else if (!file.exists()) {
						decoration.setImage(warnImage);
					} else {
						try {
							List<Double> values = StringUtil.getDoubleList(
									FileUtil.readLines(file));
							if (values == null) {
								decoration.setImage(warnImage);
							}
						} catch (IOException e) {
							decoration.setImage(errorImage);
						}
					}
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
	protected Object openDialogBox(Control cellEditorWindow) {
		PositionFileDialog dialog = new PositionFileDialog(
				cellEditorWindow.getShell(), cellEditorWindow, axis);
		dialog.open();
		if (axis.getFile() != null) {
			return axis.getFile().getAbsolutePath();
		}
		return null;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Control createContents(Composite cell) {
		this.filenameText = new Text(cell, SWT.NONE);
		this.filenameText.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				LOGGER.debug("key pressed: " + e.keyCode);
				keyReleaseOccured(e);
			}
		});
		this.filenameText.addTraverseListener(new TraverseListener() {
			@Override
			public void keyTraversed(TraverseEvent e) {
				LOGGER.debug("Traverse, keycode:" + e.keyCode);
				e.doit = false;
			}
		});
		this.filenameText.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				boolean oldValidState = isValueValid();
				boolean newValidState = isCorrect(filenameText.getText());
				LOGGER.debug("modify event - oldState: " + oldValidState + 
						", newState: " + newValidState);
				if (!newValidState) {
					setErrorMessage(MessageFormat.format(getErrorMessage(), 
							filenameText.getText()));
				}
				valueChanged(oldValidState, newValidState);
			}
		});
		return filenameText;
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
		if (keyEvent.keyCode == SWT.CR && isCorrect(filenameText.getText())) {
			LOGGER.debug("CR detected -> Apply Value and deactivate editor");
			setValue();
			fireApplyEditorValue();
		}
	}
	
	private void setValue() {
		String value = filenameText.getText();
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
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void updateContents(Object value) {
		LOGGER.debug("update contents");
		if (filenameText == null) {
			return;
		}
		if (value != null) {
			filenameText.setText(value.toString());
		} else {
			filenameText.setText("");
		}
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void doSetFocus() {
		LOGGER.debug("doSetFocus");
		filenameText.setFocus();
		filenameText.selectAll();
		//openDialogBox(getControl());
		// TODO Auto-generated method stub
		//super.doSetFocus();
		//openDialogBox(getControl());
	}
}
