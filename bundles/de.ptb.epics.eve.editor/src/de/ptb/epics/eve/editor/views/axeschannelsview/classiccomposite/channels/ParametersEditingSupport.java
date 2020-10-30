package de.ptb.epics.eve.editor.views.axeschannelsview.classiccomposite.channels;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.TableViewer;

import de.ptb.epics.eve.data.scandescription.Channel;
import de.ptb.epics.eve.editor.views.axeschannelsview.classiccomposite.channels.interval.ui.IntervalDialogCellEditor;
import de.ptb.epics.eve.editor.views.axeschannelsview.classiccomposite.channels.standard.ui.StandardDialogCellEditor;

/**
 * @author Marcus Michalsky
 * @since 1.34
 */
public class ParametersEditingSupport extends EditingSupport {
	private TableViewer viewer;
	private ColumnLabelProvider labelProvider;
	
	public ParametersEditingSupport(TableViewer viewer, ColumnLabelProvider labelProvider) {
		super(viewer);
		this.viewer = viewer;
		this.labelProvider = labelProvider;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected CellEditor getCellEditor(Object element) {
		Channel channel = (Channel)element;
		switch (channel.getChannelMode()) {
		case INTERVAL:
			return new IntervalDialogCellEditor(this.viewer.getTable(), channel);
		case STANDARD:
			return new StandardDialogCellEditor(this.viewer.getTable(), channel);
		default:
			break;
		}
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected boolean canEdit(Object element) {
		Channel channel = (Channel)element;
		return !channel.getScanModule().isUsedAsNormalizeChannel(channel);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Object getValue(Object element) {
		return this.labelProvider.getText(element);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void setValue(Object element, Object value) {
		// Parameters are bound in the dialog
	}
}
