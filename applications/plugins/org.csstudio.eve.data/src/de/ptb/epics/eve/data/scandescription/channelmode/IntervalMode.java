package de.ptb.epics.eve.data.scandescription.channelmode;

import java.util.ArrayList;
import java.util.List;

import de.ptb.epics.eve.data.measuringstation.DetectorChannel;
import de.ptb.epics.eve.data.scandescription.Channel;
import de.ptb.epics.eve.data.scandescription.errors.ChannelError;
import de.ptb.epics.eve.data.scandescription.errors.ChannelErrorTypes;
import de.ptb.epics.eve.data.scandescription.errors.IModelError;

/**
 * @author Marcus Michalsky
 * @since 1.27
 */
public class IntervalMode extends ChannelMode {
	private static final double TRIGGER_INTERVAL_DEFAULT_VALUE = 1.0;
	
	public static final String TRIGGER_INTERVAL_PROP = "triggerInterval";
	public static final String STOPPED_BY_PROP = "stoppedBy";
	
	private double triggerInterval;
	private DetectorChannel stoppedBy;
	
	public IntervalMode(Channel channel) {
		super(channel);
		this.triggerInterval = IntervalMode.TRIGGER_INTERVAL_DEFAULT_VALUE;
	}
	
	/**
	 * @return the triggerDelay
	 */
	@Override
	public double getTriggerInterval() {
		return triggerInterval;
	}
	
	/**
	 * @param triggerInterval the triggerDelay to set
	 */
	@Override
	public void setTriggerInterval(double triggerInterval) {
		double oldValue = this.triggerInterval;
		this.triggerInterval = triggerInterval;
		this.getPropertyChangeSupport().firePropertyChange(IntervalMode.TRIGGER_INTERVAL_PROP, 
				oldValue, this.triggerInterval);
	}
	
	/**
	 * @return the stoppedBy
	 */
	@Override
	public DetectorChannel getStoppedBy() {
		return stoppedBy;
	}
	
	/**
	 * @param stoppedBy the stoppedBy to set
	 */
	@Override
	public void setStoppedBy(DetectorChannel stoppedBy) {
		DetectorChannel oldValue = this.stoppedBy;
		this.stoppedBy = stoppedBy;
		this.getPropertyChangeSupport().firePropertyChange(IntervalMode.STOPPED_BY_PROP, 
				oldValue, this.stoppedBy);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void reset() {
		this.triggerInterval = IntervalMode.TRIGGER_INTERVAL_DEFAULT_VALUE;
		this.stoppedBy = null;
		// TODO invoke change events ?
	}
	
	@Override
	public List<IModelError> getModelErrors() {
		final List<IModelError> modelErrors = new ArrayList<IModelError>();
		if (this.stoppedBy == null) {
			modelErrors.add(new ChannelError(this.getChannel(), 
					ChannelErrorTypes.INTERVAL_STOPPED_BY_NOT_SET));
		}
		return modelErrors;
	}
}