package de.ptb.epics.eve.editor.views.detectorchannelview.ui;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IViewPart;

import de.ptb.epics.eve.data.scandescription.Axis;
import de.ptb.epics.eve.data.scandescription.Channel;

/**
 * A detector channel view composite is part of the detector channel view.
 * The channel to be shown is set via {@link #setChannel(Channel)}. If a 
 * previous channel was set its bindings have to be reset via {@link #reset()}.
 * The new channel is bound by {@link #createBinding()}. The databinding 
 * context is accesible via {@link #getContext()}.
 * 
 * @author Marcus Michalsky
 * @since 1.27
 */
public abstract class DetectorChannelViewComposite extends Composite {
	private DataBindingContext context;
	private IViewPart parentView;
	
	public DetectorChannelViewComposite(final Composite parent, final int style, 
			final IViewPart parentView) {
		super(parent, style);
		this.parentView = parentView;
		this.context = new DataBindingContext();
	}
	
	/**
	 * @return the parentView
	 */
	public IViewPart getParentView() {
		return parentView;
	}

	/**
	 * Sets the current axis of the composite
	 * 
	 * @param axis the axis to set or <code>null</code> to reset
	 */
	public abstract void setChannel(Channel channel);
	
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

	/**
	 * @return the context
	 */
	protected DataBindingContext getContext() {
		return context;
	}
}