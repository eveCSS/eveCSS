package de.ptb.epics.eve.editor.views.axeschannelsview.classiccomposite.channels;

import org.eclipse.jface.viewers.ColumnLabelProvider;

import de.ptb.epics.eve.data.ComparisonTypes;
import de.ptb.epics.eve.data.measuringstation.event.MonitorEvent;
import de.ptb.epics.eve.data.scandescription.Channel;
import de.ptb.epics.eve.data.scandescription.ControlEvent;
import de.ptb.epics.eve.data.scandescription.channelmode.ChannelModes;
import de.ptb.epics.eve.editor.StringLabels;

/**
 * @author Marcus Michalsky
 * @since 1.34
 */
public class ParametersColumnLabelProvider extends ColumnLabelProvider {
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getText(Object element) {
		Channel channel = (Channel)element;
		
		if (channel.getScanModule().isUsedAsNormalizeChannel(channel)) {
			return StringLabels.EM_DASH;
		}
		
		switch (channel.getChannelMode()) {
		case INTERVAL:
			StringBuilder sbInterval = new StringBuilder();
			sbInterval.append(channel.getTriggerInterval() + "s");
			if (channel.getStoppedBy() != null) {
				sbInterval.append(" " + StringLabels.STOPPED_BY + " " + 
						channel.getStoppedBy().getName());
			}
			return sbInterval.toString();
		case STANDARD:
			StringBuilder sbStandard = new StringBuilder();
			sbStandard.append("n = " + channel.getAverageCount());
			if (channel.getMaxDeviation() != null) {
				sbStandard.append(", " + StringLabels.SIGMA + " " + 
						StringLabels.LESS_OR_EQUAL + " " + 
						channel.getMaxDeviation() + " %");
			}
			if (channel.getMinimum() != null) {
				sbStandard.append(", x " + StringLabels.GREATER_OR_EQUAL + " " + 
						channel.getMinimum());
			}
			if (channel.getMaxAttempts() != null) {
				sbStandard.append(", a " + StringLabels.LESS_OR_EQUAL + " " + 
						channel.getMaxAttempts());
			}
			if (!channel.getRedoEvents().isEmpty()) {
				sbStandard.append(", " + StringLabels.REDO);
			}
			return sbStandard.toString();
		default:
			return StringLabels.EM_DASH;
		}
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getToolTipText(Object element) {
		Channel channel = (Channel)element;
		if (channel.getChannelMode().equals(ChannelModes.INTERVAL)) {
			return null;
		}
		
		StringBuilder sb = new StringBuilder();
		
		for (ControlEvent controlEvent : channel.getRedoEvents()) {
			sb.append("Redo Event: " + controlEvent.getEvent().getName());
			if (controlEvent.getEvent() instanceof MonitorEvent) {
				sb.append(", Operator: " + ComparisonTypes.typeToString(
						controlEvent.getLimit().getComparison()));
			}
			if (controlEvent.getEvent() instanceof MonitorEvent) {
				sb.append(", Limit: " + controlEvent.getLimit().getValue());
			}
			sb.append("\n");
		}
		if (sb.toString().isEmpty()) {
			return null;
		}
		// cut last line break
		return sb.toString().substring(0, sb.toString().length()-1);
	}
}
