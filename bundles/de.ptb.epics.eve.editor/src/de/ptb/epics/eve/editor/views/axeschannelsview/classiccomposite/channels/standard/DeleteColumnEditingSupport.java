package de.ptb.epics.eve.editor.views.axeschannelsview.classiccomposite.channels.standard;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.TableViewer;

import de.ptb.epics.eve.data.scandescription.Channel;
import de.ptb.epics.eve.data.scandescription.ControlEvent;

/**
 * @author Marcus Michalsky
 * @since 1.34
 */
public class DeleteColumnEditingSupport extends EditingSupport {
	private Channel channel;
	
	public DeleteColumnEditingSupport(TableViewer viewer, Channel channel) {
		super(viewer);
		this.channel = channel;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected CellEditor getCellEditor(Object element) {
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected boolean canEdit(Object element) {
		this.channel.removeRedoEvent((ControlEvent)element);
		return false;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Object getValue(Object element) {
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void setValue(Object element, Object value) {
		// there is no value to set here, the can edit check already
		// triggers the delete.
	}
}
