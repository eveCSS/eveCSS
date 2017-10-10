package de.ptb.epics.eve.editor.views.motoraxisview;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.swt.widgets.Composite;

import de.ptb.epics.eve.data.scandescription.Axis;

/**
 * @author Marcus Michalsky
 * @since 1.7
 */
public abstract class MotorAxisViewComposite extends Composite {

	protected DataBindingContext context;
	
	/**
	 * @param parent the parent
	 * @param style the style
	 */
	public MotorAxisViewComposite(final Composite parent, final int style) {
		super(parent, style);
		
		this.context = new DataBindingContext();
	}
	
	/**
	 * Sets the current axis of the composite
	 * 
	 * @param axis the axis to set or <code>null</code> to reset
	 */
	public abstract void setAxis(Axis axis);
	
	/**
	 * Creates the binding to the model 
	 * ({@link de.ptb.epics.eve.data.scandescription.axismode.AxisMode}) 
	 * after it was set by {@link #setAxis(Axis)}.
	 */
	protected abstract void createBinding();
	
	/**
	 * Resets the view such that no reference to the previously set axis (if 
	 * any) remains.
	 * Should be called if {@link #setAxis(Axis)} is called with 
	 * <code>null</code> parameter.
	 */
	protected abstract void reset();
}