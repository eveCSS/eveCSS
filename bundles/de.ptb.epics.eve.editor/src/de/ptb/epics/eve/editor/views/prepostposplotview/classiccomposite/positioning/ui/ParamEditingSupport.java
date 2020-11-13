package de.ptb.epics.eve.editor.views.prepostposplotview.classiccomposite.positioning.ui;

import org.apache.log4j.Logger;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.TableViewer;

import de.ptb.epics.eve.data.scandescription.Positioning;

/**
 * @author Marcus Michalsky
 * @since 1.35
 */
public class ParamEditingSupport extends EditingSupport {
	private static final Logger LOGGER = Logger.getLogger(
			ParamEditingSupport.class.getName());
	
	private TableViewer viewer;
	
	public ParamEditingSupport(TableViewer viewer) {
		super(viewer);
		this.viewer = viewer;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected CellEditor getCellEditor(Object element) {
		return new ParamDialogCellEditor(viewer.getTable());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected boolean canEdit(Object element) {
		Positioning positioning = (Positioning)element;
		if (positioning.getPluginController() != null 
				&& positioning.getPluginController().getPlugin() != null) {
			if (!positioning.getPluginController().getPlugin().
					getParameters().isEmpty()) {
				LOGGER.debug("parameters found -> canEdit is true");
				return true;
			}
			LOGGER.debug("no parameters found -> canEdit is false");
			return false;
		}
		LOGGER.debug("plugin not set/null -> canEdit is false");
		return false;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Object getValue(Object element) {
		return ((Positioning)element).getPluginController();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void setValue(Object element, Object value) {
	}
}
