package de.ptb.epics.eve.editor.views.axeschannelsview.classiccomposite.channels;

import java.util.Arrays;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.TableViewer;

import de.ptb.epics.eve.data.scandescription.Channel;
import de.ptb.epics.eve.data.scandescription.channelmode.ChannelModes;
import de.ptb.epics.eve.util.io.StringUtil;
import de.ptb.epics.eve.util.ui.jface.MyComboBoxCellEditor;

/**
 * @author Marcus Michalsky
 * @since 1.34
 */
public class AcquisitionTypeEditingSupport extends EditingSupport {
	private TableViewer viewer;
	
	public AcquisitionTypeEditingSupport(TableViewer viewer) {
		super(viewer);
		this.viewer = viewer;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected CellEditor getCellEditor(Object element) {
		return new MyComboBoxCellEditor(this.viewer.getTable(), 
			// ugly without Java 8+
			(StringUtil.getStringList(
				Arrays.asList(ChannelModes.values())).toArray(new String[] {})
		));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected boolean canEdit(Object element) {
		Channel channel = (Channel) element;
		return (!channel.getScanModule().isUsedAsNormalizeChannel(channel));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Object getValue(Object element) {
		return ((Channel)element).getChannelMode().ordinal();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void setValue(Object element, Object value) {
		((Channel)element).setChannelMode(ChannelModes.values()[(Integer)value]);
	}
}
