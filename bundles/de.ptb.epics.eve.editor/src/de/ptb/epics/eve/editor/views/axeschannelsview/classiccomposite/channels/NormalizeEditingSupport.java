package de.ptb.epics.eve.editor.views.axeschannelsview.classiccomposite.channels;

import java.util.List;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.TableViewer;

import de.ptb.epics.eve.data.scandescription.Channel;
import de.ptb.epics.eve.util.io.StringUtil;
import de.ptb.epics.eve.util.ui.jface.MyComboBoxCellEditor;

/**
 * @author Marcus Michalsky
 * @since 1.34
 */
public class NormalizeEditingSupport extends EditingSupport {
	private TableViewer viewer;
	private List<Channel> entries;
	
	public NormalizeEditingSupport(TableViewer viewer) {
		super(viewer);
		this.viewer = viewer;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected CellEditor getCellEditor(Object element) {
		Channel channel = (Channel)element;
		entries = channel.getScanModule().
				getValidNormalizationChannels(channel);
		List<String> entriesAsString = StringUtil.getStringList(entries);
		entriesAsString.add(0, ""); // add empty entry to deselect
		return new MyComboBoxCellEditor(this.viewer.getTable(), 
				entriesAsString.toArray(new String[] {}));
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
		Channel channel = (Channel) element;
		if (channel.getNormalizeChannel() == null) {
			return 0;
		} else {
			for (int i = 0; i < this.entries.size(); i++) {
				if (entries.get(i).getDetectorChannel().equals(
						channel.getNormalizeChannel())) {
					return i+1; // offset because of empty entry (deselect)
				}
			}
		}
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void setValue(Object element, Object value) {
		int index = (Integer)value;
		if (index == 0) {
			((Channel)element).setNormalizeChannel(null);
		} else {
			((Channel)element).setNormalizeChannel(
					entries.get(index-1).getDetectorChannel()); // offset
		}
	}
}
