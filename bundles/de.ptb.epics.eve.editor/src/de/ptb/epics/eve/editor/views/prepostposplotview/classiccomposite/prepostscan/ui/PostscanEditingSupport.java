package de.ptb.epics.eve.editor.views.prepostposplotview.classiccomposite.prepostscan.ui;

import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.ui.IWorkbenchPart;

import de.ptb.epics.eve.data.scandescription.Postscan;
import de.ptb.epics.eve.data.scandescription.ScanModule;
import de.ptb.epics.eve.editor.Activator;
import de.ptb.epics.eve.editor.StringLabels;
import de.ptb.epics.eve.editor.views.AbstractScanModuleView;
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
			List<String> discreteValues = postscan.
				getAbstractPrePostscanDevice().getValue().getDiscreteValues();
			// add dummy value (remove existing postscan)
			discreteValues.add(StringLabels.EM_DASH);
			return new MyComboBoxCellEditor(this.viewer.getTable(), 
					discreteValues.toArray(new String[0]));
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
		PrePostscanEntry entry = (PrePostscanEntry)element;
		if (entry.getPostscan() == null) {
			LOGGER.info("no postscan present, creating new with default value");
			IWorkbenchPart part = Activator.getDefault().getWorkbench().
				getActiveWorkbenchWindow().getPartService().getActivePart();
			if (!(part instanceof AbstractScanModuleView)) {
				LOGGER.error("active part is not a scan module view");
				return false;
			}
			ScanModule sm = ((AbstractScanModuleView)part).getScanModule();
			Postscan postscan = new Postscan(entry.getDevice(), 
					entry.getDevice().getValue().getDefaultValue());
			sm.add(postscan);
			return false;
		}
		if (entry.getPostscan().isReset()) {
			LOGGER.debug("reset ist set, edit invalid");
			return false;
		}
		LOGGER.debug("edit existing postscan entry");
		return true;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Object getValue(Object element) {
		PrePostscanEntry entry = (PrePostscanEntry)element;
		Postscan postscan = entry.getPostscan();
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
		PrePostscanEntry entry = (PrePostscanEntry)element;
		Postscan postscan = entry.getPostscan();
		IWorkbenchPart part = Activator.getDefault().getWorkbench().
				getActiveWorkbenchWindow().getPartService().getActivePart();
		if (!(part instanceof AbstractScanModuleView)) {
			LOGGER.error("active part is not a scan module view");
			return;
		}
		ScanModule sm = ((AbstractScanModuleView)part).getScanModule();
		if (postscan.getAbstractPrePostscanDevice().isDiscrete()) {
			List<String> discreteValues = postscan.
				getAbstractPrePostscanDevice().getValue().getDiscreteValues();
			int index = (Integer) value;
			if (index == discreteValues.size()) {
				// index is the dummy item -> delete postscan
				sm.remove(postscan);
			} else {
				// index is "real", set the selected value
				postscan.setValue(discreteValues.get(index));
			}
		} else {
			if (value.toString().isEmpty()) {
				sm.remove(postscan);
			} else {
				postscan.setValue(value.toString());
			}
		}
	}
}
