package de.ptb.epics.eve.editor.views.axeschannelsview.classiccomposite.axes.positionlist.ui;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.eclipse.core.databinding.Binding;
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.UpdateValueStrategy;
import org.eclipse.core.databinding.beans.BeansObservables;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.jface.databinding.fieldassist.ControlDecorationSupport;
import org.eclipse.jface.databinding.swt.SWTObservables;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import de.ptb.epics.eve.data.scandescription.Axis;
import de.ptb.epics.eve.data.scandescription.axismode.PositionlistMode;
import de.ptb.epics.eve.editor.views.axeschannelsview.classiccomposite.axes.positionlist.PositionlistValidator;
import de.ptb.epics.eve.editor.views.axeschannelsview.classiccomposite.ui.DialogCellEditorDialog;

/**
 * @author Marcus Michalsky
 * @since 1.34
 */
public class PositionlistDialog extends DialogCellEditorDialog implements PropertyChangeListener {
	private final Axis axis;
	private final PositionlistMode positionlistMode;
	
	private Text positionlistText;
	private Label positionCountLabel;
	private Binding positionlistBinding;
	
	protected PositionlistDialog(Shell parentShell, Control control, Axis axis) {
		super(parentShell, control);
		this.axis = axis;
		this.positionlistMode = (PositionlistMode)axis.getMode();
		this.positionlistMode.addPropertyChangeListener(
				PositionlistMode.POSITIONLIST_PROP, this);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Control createDialogArea(Composite parent) {
		Composite composite = new Composite(parent, SWT.NONE);
		GridLayout gridLayout = new GridLayout();
		// gridLayout.numColumns = 3;
		composite.setLayout(gridLayout);
		GridData gridData = new GridData();
		gridData.horizontalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = true;
		gridData.minimumWidth = 400;
		composite.setLayoutData(gridData);

		// TODO clear button
		// TODO reset button (for discrete: clear and fill with default values)
		
		positionlistText = new Text(composite, 
				SWT.BORDER | SWT.V_SCROLL | SWT.WRAP);
		gridData = new GridData();
		gridData.grabExcessHorizontalSpace = true;
		gridData.grabExcessVerticalSpace = true;
		gridData.horizontalAlignment = GridData.FILL;
		gridData.verticalAlignment = GridData.FILL;
		gridData.minimumHeight = 70;
		gridData.horizontalIndent = 7;
		positionlistText.setLayoutData(gridData);
		positionlistText.addFocusListener(new FocusAdapter() {
			@Override
			public void focusLost(FocusEvent e) {
				positionlistBinding.updateModelToTarget();
				positionlistBinding.validateTargetToModel();
				countPositions();
				super.focusLost(e);
			}
		});
		
		positionCountLabel = new Label(composite, SWT.NONE);
		positionCountLabel.setText("0 positions");
		
		this.createBinding();
		this.countPositions();
		
		return composite;
	}
	
	private void createBinding() {
		DataBindingContext context = new DataBindingContext();
		IObservableValue positionlistModelObservable = 
				BeansObservables.observeValue(this.positionlistMode, 
						PositionlistMode.POSITIONLIST_PROP);
		IObservableValue positionlistTargetObservable = 
				SWTObservables.observeText(this.positionlistText, SWT.Modify);
		UpdateValueStrategy targetToModel = new UpdateValueStrategy(
				UpdateValueStrategy.POLICY_UPDATE);
		targetToModel.setAfterGetValidator(new PositionlistValidator(axis));
		UpdateValueStrategy modelToTarget = new UpdateValueStrategy(
				UpdateValueStrategy.POLICY_UPDATE);
		positionlistBinding = context.bindValue(
				positionlistTargetObservable, positionlistModelObservable, 
				targetToModel, modelToTarget);
		ControlDecorationSupport.create(positionlistBinding, SWT.LEFT);
	}

	private void countPositions() {
		if (this.positionlistMode.getPositionCount() == null) {
			this.positionCountLabel.setText("calculation not possible");
		} else {
			switch (this.positionlistMode.getPositionCount()) {
			case 0:
				this.positionCountLabel.setText("0 positions");
				break;
			case 1:
				this.positionCountLabel.setText("1 position");
				break;
			default:
				this.positionCountLabel.setText(this.positionlistMode
						.getPositionCount() + " positions");
				break;
			}
		}
		this.positionCountLabel.getParent().layout();
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		if (evt.getPropertyName().equals(PositionlistMode.POSITIONLIST_PROP)) {
			this.countPositions();
		}
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean close() {
		this.positionlistMode.removePropertyChangeListener(
				PositionlistMode.POSITIONLIST_PROP, this);
		return super.close();
	}
}
