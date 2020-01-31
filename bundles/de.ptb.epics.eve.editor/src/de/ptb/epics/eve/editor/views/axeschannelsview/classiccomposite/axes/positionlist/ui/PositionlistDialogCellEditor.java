package de.ptb.epics.eve.editor.views.axeschannelsview.classiccomposite.axes.positionlist.ui;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IDialogLabelKeys;
import org.eclipse.jface.fieldassist.ControlDecoration;
import org.eclipse.jface.fieldassist.FieldDecorationRegistry;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.viewers.DialogCellEditor;
import org.eclipse.jface.viewers.ICellEditorListener;
import org.eclipse.jface.viewers.ICellEditorValidator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import de.ptb.epics.eve.data.scandescription.Axis;
import de.ptb.epics.eve.util.io.StringUtil;

/**
 * @author Marcus Michalsky
 * @since 1.34
 */
public class PositionlistDialogCellEditor extends DialogCellEditor {
	private FieldDecorationRegistry registry = 
			FieldDecorationRegistry.getDefault();
	private Image errorImage = registry.getFieldDecoration(
			FieldDecorationRegistry.DEC_ERROR).getImage();
	
	private Text text;
	//private final ControlDecoration decoration;
	private final Axis axis;
	
	public PositionlistDialogCellEditor(Composite parent, Axis axis) {
		super(parent, SWT.NONE);
		this.axis = axis;
		/*
		this.decoration = new ControlDecoration(getControl(), SWT.LEFT);
		this.setValidator(new ICellEditorValidator() {
			@Override
			public String isValid(Object value) {
				return validate((String)value);
			}
		});
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
				// nothing to do
			}
			
			@Override
			public void applyEditorValue() {
				// nothing to do
			}
		});*/
	}
	
	/*
	 * empty return value = valid position list. Otherwise string contains error
	 * message.
	 */
	private String validate(String positions) {
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
			return "";
		}
		return "The list contains syntax errors";
	}
	
	/*
	@Override
	protected Control createContents(Composite cell) {
		this.text = new Text(cell, SWT.NONE);
		return text;
	}*/
	
	@Override
	protected void doSetFocus() {
		// TODO Auto-generated method stub
		super.doSetFocus();
		//this.text.setFocus();
	}
	
	/*
	@Override
	protected void updateContents(Object value) {
		if (this.text == null) {
			return;
		}
		if (value != null) {
			this.text.setText(value.toString());
		}
		// TODO Auto-generated method stub
		super.updateContents(value);
	}*/
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Object openDialogBox(Control cellEditorWindow) {
		return new PositionlistDialog(cellEditorWindow.getShell(), getControl(), axis).open();
		//return axis.getPositionlist();
		// TODO Auto-generated method stub
		// return null;
	}
}
