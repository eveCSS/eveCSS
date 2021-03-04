package de.ptb.epics.eve.editor.views.prepostposplotview.classiccomposite.prepostscan.ui;

import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.ui.IWorkbenchPart;

import de.ptb.epics.eve.data.scandescription.Prescan;
import de.ptb.epics.eve.data.scandescription.ScanModule;
import de.ptb.epics.eve.editor.Activator;
import de.ptb.epics.eve.editor.StringLabels;
import de.ptb.epics.eve.editor.views.AbstractScanModuleView;
import de.ptb.epics.eve.editor.views.prepostposplotview.classiccomposite.prepostscan.PrePostscanEntry;
import de.ptb.epics.eve.util.ui.jface.MyComboBoxCellEditor;
import de.ptb.epics.eve.util.ui.jface.TextCellEditorWithValidator;

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
		Prescan prescan = entry.getPrescan();
		if (prescan != null && prescan.getAbstractPrePostscanDevice().
				isDiscrete()) {
			LOGGER.debug(entry.getDevice().getName() + " is discrete. " +
				StringLabels.RIGHT_ARROW + " ComboBoxEditor");
			List<String> discreteValues = prescan.
				getAbstractPrePostscanDevice().getValue().getDiscreteValues();
			// add dummy value (remove existing prescan)
			discreteValues.add(StringLabels.EM_DASH);
			return new MyComboBoxCellEditor(this.viewer.getTable(),
					discreteValues.toArray(new String[0]));
		} else {
			LOGGER.debug(entry.getDevice().getName() + " is not discrete. " +
				StringLabels.RIGHT_ARROW + " TextEditor");
			return new TextCellEditorWithValidator(this.viewer.getTable(), 
					new PrePostscanTextCellEditorValidator(entry.getDevice()));
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected boolean canEdit(Object element) {
		PrePostscanEntry entry = (PrePostscanEntry) element;
		if (entry.getPrescan() == null) {
			LOGGER.info("no prescan present, creating new with default value");
			IWorkbenchPart part = Activator.getDefault().getWorkbench().
					getActiveWorkbenchWindow().getPartService().getActivePart();
			if (!(part instanceof AbstractScanModuleView)) {
				LOGGER.error("active part is not a scan module view");
				return false;
			}
			ScanModule sm = ((AbstractScanModuleView)part).getScanModule();
			Prescan prescan = new Prescan(entry.getDevice(), 
					entry.getDevice().getValue().getDefaultValue());
			sm.add(prescan);
			return false;
		}
		LOGGER.info("edit existig prescan entry");
		return true;
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
			return prescan.getValue();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void setValue(Object element, Object value) {
		PrePostscanEntry entry = (PrePostscanEntry) element;
		Prescan prescan = entry.getPrescan();
		IWorkbenchPart part = Activator.getDefault().getWorkbench().
				getActiveWorkbenchWindow().getPartService().getActivePart();
		if (!(part instanceof AbstractScanModuleView)) {
			LOGGER.error("active part is not a scan module view");
			return;
		}
		ScanModule sm = ((AbstractScanModuleView)part).getScanModule();
		if (prescan.getAbstractPrePostscanDevice().isDiscrete()) {
			List<String> discreteValues = prescan.
				getAbstractPrePostscanDevice().getValue().getDiscreteValues();
			int index = (Integer) value;
			if (index == discreteValues.size()) {
				// index is the dummy item -> delete prescan
				sm.remove(prescan);
			} else {
				// index is "real", set the selected value
				prescan.setValue(discreteValues.get(index));
			}
		} else {
			if (value.toString().isEmpty()) {
				sm.remove(prescan);
			} else {
				prescan.setValue(value.toString());
			}
		}
	}
}
