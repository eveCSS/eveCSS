package de.ptb.epics.eve.editor.views.axeschannelsview.classiccomposite.channels;

import org.eclipse.jface.viewers.ColumnLabelProvider;

import de.ptb.epics.eve.data.scandescription.Channel;

/**
 * @author Marcus Michalsky
 * @since 1.34
 */
public class ParametersColumnLabelProvider extends ColumnLabelProvider {
	private static final String DASH = Character.toString('\u2014');
	private static final String STOPPED_BY = Character.toString('\u21E5');
	private static final String REDO = Character.toString('\u27F2');
	private static final String SIGMA = Character.toString('\u03C3');
	private static final String LESS_OR_EQUAL = Character.toString('\u2264');
	private static final String GREATER_OR_EQUAL = Character.toString('\u2267');
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getText(Object element) {
		Channel channel = (Channel)element;
		
		if (channel.getScanModule().isUsedAsNormalizeChannel(channel)) {
			return DASH;
		}
		
		switch (channel.getChannelMode()) {
		case INTERVAL:
			StringBuilder sbInterval = new StringBuilder();
			sbInterval.append(channel.getTriggerInterval() + "s");
			if (channel.getStoppedBy() != null) {
				sbInterval.append(" " + STOPPED_BY + " " + 
						channel.getStoppedBy().getName());
			}
			return sbInterval.toString();
		case STANDARD:
			StringBuilder sbStandard = new StringBuilder();
			sbStandard.append("n = " + channel.getAverageCount());
			if (channel.getMaxDeviation() != null) {
				sbStandard.append(", " + SIGMA + " " + LESS_OR_EQUAL + " " + 
						channel.getMaxDeviation() + " %");
			}
			if (channel.getMinimum() != null) {
				sbStandard.append(", x " + GREATER_OR_EQUAL + " " + 
						channel.getMinimum());
			}
			if (channel.getMaxAttempts() != null) {
				sbStandard.append(", a " + LESS_OR_EQUAL + " " + 
						channel.getMaxAttempts());
			}
			if (!channel.getRedoEvents().isEmpty()) {
				sbStandard.append(", " + REDO);
			}
			return sbStandard.toString();
		default:
			return DASH;
		}
	}
}
