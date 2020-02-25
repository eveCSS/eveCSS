package de.ptb.epics.eve.editor.views.axeschannelsview.classiccomposite.axes.positionlist.ui;

import org.eclipse.core.databinding.Binding;
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.UpdateValueStrategy;
import org.eclipse.core.databinding.beans.BeanProperties;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.jface.databinding.fieldassist.ControlDecorationSupport;
import org.eclipse.jface.databinding.swt.SWTObservables;
import org.eclipse.jface.databinding.swt.WidgetProperties;
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
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import de.ptb.epics.eve.data.scandescription.Axis;
import de.ptb.epics.eve.data.scandescription.axismode.AxisMode;
import de.ptb.epics.eve.data.scandescription.axismode.PositionlistMode;
import de.ptb.epics.eve.editor.views.axeschannelsview.classiccomposite.axes.positionlist.PositionlistValidator;
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
	private final PositionlistMode positionlistMode;
	private Binding binding;
	
	public PositionlistDialogCellEditor(Composite parent, Axis axis) {
		super(parent, SWT.NONE);
		this.axis = axis;
		this.positionlistMode = (PositionlistMode)axis.getMode();
		
		/*final ControlDecoration decoration = new ControlDecoration(getControl(), SWT.LEFT);
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
	
	/**
	 * {@inheritDoc}
	 */
/*	@Override
	protected Control createContents(Composite cell) {
		this.text = new Text(cell, SWT.NONE);
		this.text.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				keyReleased(e);
			}
		});
		DataBindingContext context = new DataBindingContext();
		IObservableValue positionlistTargetObservable = 
				WidgetProperties.text(SWT.Modify).observe(text);
		IObservableValue positionlistModelObservable = 
				BeanProperties.value(PositionlistMode.POSITIONLIST_PROP, 
						String.class).observe(positionlistMode);
		UpdateValueStrategy targetToModel = new UpdateValueStrategy(
				UpdateValueStrategy.POLICY_UPDATE);
		targetToModel.setAfterGetValidator(new PositionlistValidator(axis));
		UpdateValueStrategy modelToTarget = new UpdateValueStrategy(
				UpdateValueStrategy.POLICY_UPDATE);
		binding = context.bindValue(positionlistTargetObservable, 
				positionlistModelObservable, targetToModel, modelToTarget);
		ControlDecorationSupport.create(binding, SWT.LEFT);
		return text;
	}*/
	
/*	@Override
	protected void keyReleaseOccured(KeyEvent keyEvent) {
		if (keyEvent.keyCode == SWT.CR && this.validate(this.text.getText()).isEmpty()) {
			//this.axis.setPositionlist(this.text.getText());
			//markDirty();
			//doSetValue(this.text.getText());
			// TODO
		} else {
			
		}
		// TODO Auto-generated method stub
		super.keyReleaseOccured(keyEvent);
	}*/
	
	/**
	 * {@inheritDoc}
	 */
/*	@Override
	protected void doSetFocus() {
		this.text.setFocus();
	}*/
	
	/**
	 * ´{@inheritDoc}
	 */
/*	@Override
	protected void updateContents(Object value) {
		if (this.text == null) {
			return;
		}
		if (value != null) {
			//this.text.setText(value.toString());
			this.binding.updateTargetToModel();
			this.text.getParent().layout();
		}
		super.updateContents(value);
	}*/
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Object openDialogBox(Control cellEditorWindow) {
		new PositionlistDialog(cellEditorWindow.getShell(), getControl(), axis).open();
		return axis.getPositionlist();
	}
}
