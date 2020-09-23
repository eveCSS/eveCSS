package de.ptb.epics.eve.editor.views.prepostposplotview.classiccomposite.prepostscan.ui;

import org.apache.log4j.Logger;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.TableViewer;

import de.ptb.epics.eve.data.scandescription.Prescan;
import de.ptb.epics.eve.editor.views.prepostposplotview.classiccomposite.prepostscan.PrePostscanEntry;
import de.ptb.epics.eve.util.ui.jface.MyComboBoxCellEditor;

/**
 * @author Marcus Michalsky
 * @since 1.35
 */
public class PrescanEditingSupport extends EditingSupport {
	private static final Logger LOGGER = Logger.getLogger(
			PrescanEditingSupport.class.getName());
	
	private TableViewer viewer;
	
	public PrescanEditingSupport(TableViewer viewer) {
		super(viewer);
		this.viewer = viewer;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected CellEditor getCellEditor(Object element) {
		PrePostscanEntry entry = (PrePostscanEntry) element;
		if (entry.getPrescan() != null && entry.getPrescan().
				getAbstractPrePostscanDevice().isDiscrete()) {
			LOGGER.debug(entry.getDevice().getName() + " is discrete.");
			return new MyComboBoxCellEditor(this.viewer.getTable(),
					entry.getPrescan().getAbstractPrePostscanDevice().
					getValue().getDiscreteValues().toArray(new String[0]));
		} else {
			LOGGER.debug(entry.getDevice().getName() + " is not discrete.");
			return new PrePostscanTextCellEditor(this.viewer.getTable(), 
					entry.getDevice());
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected boolean canEdit(Object element) {
		PrePostscanEntry entry = (PrePostscanEntry) element;
		return entry.getPrescan() != null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Object getValue(Object element) {
		PrePostscanEntry entry = (PrePostscanEntry) element;
		Prescan prescan = entry.getPrescan();
		if (prescan == null) {
			return null;
		}
		if (prescan.getAbstractPrePostscanDevice().isDiscrete()) {
			int index = prescan.getAbstractPrePostscanDevice().getValue().
					getDiscreteValues().indexOf(prescan.getValue());
			return index == -1 ? 0 : index;
		} else {
			return entry.getPrescan().getValue();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void setValue(Object element, Object value) {
		PrePostscanEntry entry = (PrePostscanEntry) element;
		Prescan prescan = entry.getPrescan();
		if (prescan.getAbstractPrePostscanDevice().isDiscrete()) {
			prescan.setValue(prescan.getAbstractPrePostscanDevice().getValue().
					getDiscreteValues().get((Integer)value));
		} else {
			prescan.setValue(value.toString());
		}
	}
}
