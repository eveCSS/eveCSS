package de.ptb.epics.eve.editor.views.prepostposplotview.classiccomposite.positioning.ui;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.TableViewer;

import de.ptb.epics.eve.data.measuringstation.DetectorChannel;
import de.ptb.epics.eve.data.scandescription.Channel;
import de.ptb.epics.eve.data.scandescription.Positioning;
import de.ptb.epics.eve.data.scandescription.ScanModule;
import de.ptb.epics.eve.util.ui.jface.MyComboBoxCellEditor;

/**
 * @author Marcus Michalsky
 * @since 1.35
 */
public class ChannelEditingSupport extends EditingSupport {
	private TableViewer viewer;
	private List<String> channelNames;
	private List<DetectorChannel> channels;
	
	public ChannelEditingSupport(TableViewer viewer) {
		super(viewer);
		this.viewer = viewer;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected CellEditor getCellEditor(Object element) {
		ScanModule scanModule = (ScanModule)this.viewer.getInput();
		channelNames = new ArrayList<>();
		channels = new ArrayList<>();
		for (Channel ch : scanModule.getChannels()) {
			channels.add(ch.getDetectorChannel());
			channelNames.add(ch.getDetectorChannel().getName());
		}
		return new MyComboBoxCellEditor(this.viewer.getTable(), 
				channelNames.toArray(new String[0]));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected boolean canEdit(Object element) {
		return true;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Object getValue(Object element) {
		final Positioning positioning = (Positioning)element;
		if (positioning.getDetectorChannel() != null) {
			return channels.indexOf(positioning.getDetectorChannel());
		}
		return 0;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void setValue(Object element, Object value) {
		final Positioning positioning = (Positioning)element;
		DetectorChannel ch = channels.get((Integer)value);
		positioning.setDetectorChannel(ch);
	}
}
