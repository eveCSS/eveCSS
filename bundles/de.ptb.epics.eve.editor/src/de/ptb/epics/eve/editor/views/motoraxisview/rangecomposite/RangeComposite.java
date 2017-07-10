package de.ptb.epics.eve.editor.views.motoraxisview.rangecomposite;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.eclipse.core.databinding.Binding;
import org.eclipse.core.databinding.UpdateValueStrategy;
import org.eclipse.core.databinding.beans.BeansObservables;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.jface.databinding.fieldassist.ControlDecorationSupport;
import org.eclipse.jface.databinding.swt.ISWTObservableValue;
import org.eclipse.jface.databinding.swt.SWTObservables;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import de.ptb.epics.eve.data.scandescription.Axis;
import de.ptb.epics.eve.data.scandescription.axismode.RangeMode;
import de.ptb.epics.eve.editor.views.motoraxisview.MotorAxisViewComposite;

/**
 * @author Marcus Michalsky
 * @since 1.28
 */
public class RangeComposite extends MotorAxisViewComposite implements PropertyChangeListener {
	private RangeMode rangeMode;
	
	private Text rangeText;
	private Binding rangeBinding;
	private IObservableValue rangeModelObservable;
	private IObservableValue rangeTargetObservable;
	private ISWTObservableValue rangeTargetDelayedObservable;
	private ControlDecorationSupport rangeControlDecoration;
	
	private Text previewText;
	private Binding previewBinding;
	private IObservableValue previewModelObservable;
	private IObservableValue previewTargetObservable;
	
	private Label positionCountLabel;
	
	public RangeComposite(Composite parent, int style) {
		super(parent, style);
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 2;
		this.setLayout(gridLayout);
		
		Label rangeLabel = new Label(this, SWT.NONE);
		rangeLabel.setText("Range:");
		this.rangeText = new Text(this, SWT.BORDER);
		GridData gridData = new GridData();
		gridData.grabExcessHorizontalSpace = true;
		gridData.horizontalAlignment = GridData.FILL;
		gridData.horizontalIndent = 7;
		this.rangeText.setLayoutData(gridData);
		
		this.previewText = new Text(this, SWT.MULTI | SWT.READ_ONLY | 
				SWT.WRAP | SWT.BORDER | SWT.V_SCROLL);
		gridData = new GridData();
		gridData.horizontalSpan = 2;
		gridData.grabExcessHorizontalSpace = true;
		gridData.horizontalAlignment = GridData.FILL;
		gridData.heightHint = 150;
		this.previewText.setLayoutData(gridData);
		
		this.positionCountLabel = new Label(this, SWT.NONE);
		this.positionCountLabel.setText("no positions");
		gridData = new GridData();
		gridData.horizontalSpan = 2;
		gridData.grabExcessHorizontalSpace = true;
		gridData.horizontalAlignment = SWT.LEFT;
		this.positionCountLabel.setLayoutData(gridData);
		
		this.rangeMode = null;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setAxis(Axis axis) {
		this.reset();
		if (axis == null) {
			return;
		}
		this.rangeMode = ((RangeMode)axis.getMode());
		this.rangeMode.addPropertyChangeListener(RangeMode.POSITIONS_PROP, this);
		this.createBinding();
		this.refreshPositionCountLabel();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void createBinding() {
		this.rangeModelObservable = BeansObservables.observeValue(
				this.rangeMode, RangeMode.RANGE_PROP);
		this.rangeTargetObservable = SWTObservables.observeText(
				this.rangeText, SWT.Modify);
		UpdateValueStrategy rangeTargetToModel = new UpdateValueStrategy(
				UpdateValueStrategy.POLICY_UPDATE);
		rangeTargetToModel.setAfterGetValidator(new RangeTargetToModelValidator(
				this.rangeMode.getAxis().getType()));
		UpdateValueStrategy rangeModelToTarget = new UpdateValueStrategy(
				UpdateValueStrategy.POLICY_UPDATE);
		this.rangeTargetDelayedObservable = SWTObservables.observeDelayedValue(
				500, (ISWTObservableValue) this.rangeTargetObservable);
		this.rangeBinding = context.bindValue(rangeTargetDelayedObservable, 
				rangeModelObservable, rangeTargetToModel, rangeModelToTarget);
		this.rangeControlDecoration = ControlDecorationSupport.create(
				rangeBinding, SWT.LEFT);
		
		this.previewModelObservable = BeansObservables.observeValue(
				this.rangeMode, RangeMode.POSITIONS_PROP);
		this.previewTargetObservable = SWTObservables.observeText(
				this.previewText);
		UpdateValueStrategy previewTargetToModel = new UpdateValueStrategy(
				UpdateValueStrategy.POLICY_NEVER);
		UpdateValueStrategy previewModelToTarget = new UpdateValueStrategy(
				UpdateValueStrategy.POLICY_UPDATE);
		this.previewBinding = context.bindValue(previewTargetObservable, 
				previewModelObservable, previewTargetToModel, previewModelToTarget);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void reset() {
		if (this.rangeMode != null) {
			this.rangeMode.removePropertyChangeListener(
					RangeMode.POSITIONS_PROP, this);
			if (this.rangeControlDecoration != null) {
				this.rangeControlDecoration.dispose();
			}
			this.context.removeBinding(previewBinding);
			this.previewBinding.dispose();
			this.previewModelObservable.dispose();
			this.previewTargetObservable.dispose();
			
			this.context.removeBinding(this.rangeBinding);
			this.rangeBinding.dispose();
			this.rangeModelObservable.dispose();
			this.rangeTargetDelayedObservable.dispose();
			this.rangeTargetObservable.dispose();
		}
		this.rangeMode = null;
		this.rangeText.setText("");
		this.positionCountLabel.setText("no positions");
		this.redraw();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void propertyChange(PropertyChangeEvent e) {
		if (e.getPropertyName().equals(RangeMode.POSITIONS_PROP)) {
			this.refreshPositionCountLabel();
		}
	}
	
	private void refreshPositionCountLabel() {
		Integer poscnt = this.rangeMode.getPositionCount();
		if (poscnt == null) {
			return;
		}
		if (poscnt.equals(1)) {
			this.positionCountLabel.setText("1 position");
		} else {
			this.positionCountLabel.setText(poscnt + " positions");
		}
		this.positionCountLabel.getParent().layout();
	}
}