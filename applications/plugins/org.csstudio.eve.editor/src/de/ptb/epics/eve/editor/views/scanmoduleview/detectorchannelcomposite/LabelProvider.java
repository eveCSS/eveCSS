package de.ptb.epics.eve.editor.views.scanmoduleview.detectorchannelcomposite;

import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;

import de.ptb.epics.eve.data.scandescription.Channel;
import de.ptb.epics.eve.data.scandescription.channelmode.ChannelModes;
import de.ptb.epics.eve.data.scandescription.errors.IModelError;
import de.ptb.epics.eve.data.scandescription.errors.ChannelError;

/**
 * <code>DetectorChannelLabelProvider</code> is the label provider of the table
 * viewer defined in
 * {@link de.ptb.epics.eve.editor.views.scanmoduleview.detectorchannelcomposite.DetectorChannelComposite}
 * .
 * 
 * @author ?
 * @author Marcus Michalsky
 */
public class LabelProvider implements ITableLabelProvider {
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Image getColumnImage(final Object channel, final int colIndex) {
		if (colIndex == 0) {
			return PlatformUI.getWorkbench().getSharedImages()
					.getImageDescriptor(ISharedImages.IMG_TOOL_DELETE)
					.createImage();
		} else if (colIndex == 1) {
			for (IModelError error : ((Channel) channel).getModelErrors()) {
				if (error instanceof ChannelError) {
					return PlatformUI.getWorkbench().getSharedImages()
							.getImage(ISharedImages.IMG_OBJS_ERROR_TSK);
				}
			}
		}
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getColumnText(final Object channel, final int colIndex) {
		String dash = Character.toString('\u2014');
		Channel chan = (Channel) channel;
		if ( colIndex > 1 && colIndex < 5 && chan.getScanModule().isUsedAsNormalizeChannel(chan)) {
			return dash;
		}
		switch (colIndex) {
		case 1:
			return chan.getAbstractDevice().getName();
		case 2:
			if (chan.getChannelMode().equals(ChannelModes.STANDARD)) {
				return Integer.toString(chan.getAverageCount());
			} else {
				return dash;
			}
		case 3:
			if (chan.getChannelMode().equals(ChannelModes.INTERVAL)) {
				return Double.toString(chan.getTriggerInterval());
			} else {
				return dash;
			}
		case 4:
			if (chan.getChannelMode().equals(ChannelModes.STANDARD)) {
				return Boolean.toString(chan.isDeferred());
			} else if (chan.getChannelMode().equals(ChannelModes.INTERVAL) && 
					chan.getStoppedBy() != null) {
				return Boolean.toString(chan.getScanModule().getChannel(
						chan.getStoppedBy()).isDeferred());
			} else {
				return dash;
			}
		case 5:
			if (chan.getNormalizeChannel() != null) {
				return chan.getNormalizeChannel().getName();
			} else {
				return dash;
			}
		case 6:
			if (chan.getChannelMode().equals(ChannelModes.INTERVAL) && 
					chan.getStoppedBy() != null) {
				return chan.getStoppedBy().getName();
			} else {
				return dash;
			}
		}
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void addListener(final ILabelProviderListener arg0) {
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void dispose() {
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isLabelProperty(final Object arg0, String arg1) {
		return false;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void removeListener(final ILabelProviderListener arg0) {
	}
}