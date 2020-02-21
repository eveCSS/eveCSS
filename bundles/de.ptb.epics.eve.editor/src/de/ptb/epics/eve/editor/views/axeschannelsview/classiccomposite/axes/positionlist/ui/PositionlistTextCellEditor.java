package de.ptb.epics.eve.editor.views.axeschannelsview.classiccomposite.axes.positionlist.ui;

import org.eclipse.jface.fieldassist.ControlDecoration;
import org.eclipse.jface.fieldassist.FieldDecorationRegistry;
import org.eclipse.jface.viewers.ICellEditorListener;
import org.eclipse.jface.viewers.ICellEditorValidator;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Text;

import de.ptb.epics.eve.data.scandescription.Axis;
import de.ptb.epics.eve.util.io.StringUtil;

/**
 * @author Marcus Michalsky
 * @since 1.34
 */
public class PositionlistTextCellEditor extends TextCellEditor {
	private FieldDecorationRegistry registry = 
			FieldDecorationRegistry.getDefault();
	private Image errorImage = registry.getFieldDecoration(
			FieldDecorationRegistry.DEC_ERROR).getImage();
	
	private final ControlDecoration decoration;
	private final Axis axis;
	private Text text;
	private Button button;

	public PositionlistTextCellEditor(Composite parent, Axis axis) {
		super(parent, SWT.NONE);
		this.axis = axis;
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
			public void applyEditorValue() {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void cancelEditor() {
				// TODO Auto-generated method stub
				
			}
		});
		// TODO Auto-generated constructor stub
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

	@Override
	protected Control createControl(final Composite parent) {
		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayout(new FillLayout(SWT.HORIZONTAL));
		this.text = (Text) super.createControl(composite);
		
		// text = new Text(parent, SWT.NONE);
		button = new Button(composite, SWT.PUSH);
		button.setText("...");
		button.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				new PositionlistDialog(parent.getShell(), parent, axis).open();
				// TODO Auto-generated method stub
				//super.widgetSelected(e);
			}
		});
		// TODO Auto-generated method stub
		return composite;
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
