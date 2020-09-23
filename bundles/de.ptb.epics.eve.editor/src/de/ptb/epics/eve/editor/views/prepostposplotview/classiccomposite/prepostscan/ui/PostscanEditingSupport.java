package de.ptb.epics.eve.editor.views.prepostposplotview.classiccomposite.prepostscan.ui;

import org.apache.log4j.Logger;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.TableViewer;

import de.ptb.epics.eve.data.scandescription.Postscan;
import de.ptb.epics.eve.editor.views.prepostposplotview.classiccomposite.prepostscan.PrePostscanEntry;
import de.ptb.epics.eve.util.ui.jface.MyComboBoxCellEditor;

/**
 * @author Marcus Michalsky
 * @since 1.35
 */
public class PostscanEditingSupport extends EditingSupport {
	private static final Logger LOGGER = Logger.getLogger(
			PostscanEditingSupport.class.getName());
	
	private TableViewer viewer;
	
	public PostscanEditingSupport(TableViewer viewer) {
		super(viewer);
		this.viewer = viewer;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected CellEditor getCellEditor(Object element) {
		PrePostscanEntry entry = (PrePostscanEntry) element;
		Postscan postscan = entry.getPostscan();
		if (postscan != null && postscan.getAbstractPrePostscanDevice().
				isDiscrete()) {
			LOGGER.debug(entry.getDevice().getName() + " is discrete.");
			return new MyComboBoxCellEditor(this.viewer.getTable(), 
					postscan.getAbstractPrePostscanDevice().getValue().
					getDiscreteValues().toArray(new String[0]));
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
		Postscan postscan = ((PrePostscanEntry)element).getPostscan();
		return postscan != null && !postscan.isReset();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Object getValue(Object element) {
		Postscan postscan = ((PrePostscanEntry)element).getPostscan();
		if (postscan == null) {
			return null;
		}
		if (postscan.getAbstractPrePostscanDevice().isDiscrete()) {
			int index = postscan.getAbstractPrePostscanDevice().getValue().
					getDiscreteValues().indexOf(postscan.getValue());
			return index == -1 ? 0 : index;
		} else {
			return postscan.getValue();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void setValue(Object element, Object value) {
		Postscan postscan = ((PrePostscanEntry)element).getPostscan();
		if (postscan.getAbstractPrePostscanDevice().isDiscrete()) {
			postscan.setValue(postscan.getAbstractPrePostscanDevice().
					getValue().getDiscreteValues().get((Integer)value));
		} else {
			postscan.setValue(value.toString());
		}
	}
}
