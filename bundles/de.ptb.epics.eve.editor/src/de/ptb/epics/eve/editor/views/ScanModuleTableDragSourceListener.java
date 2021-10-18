package de.ptb.epics.eve.editor.views;

import org.apache.log4j.Logger;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.widgets.TableItem;

import de.ptb.epics.eve.data.scandescription.AbstractBehavior;

/**
 * @author Marcus Michalsky
 * @since 1.34
 */
public class ScanModuleTableDragSourceListener extends ViewerTableDragSourceListener {
	private static final Logger LOGGER = Logger.getLogger(
			ScanModuleTableDragSourceListener.class.getName());
	
	public ScanModuleTableDragSourceListener(TableViewer viewer) {
		super(viewer);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Logger getLogger() {
		return LOGGER;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getModelName(TableItem item) {
		if (item.getData() instanceof AbstractBehavior) {
			AbstractBehavior device = (AbstractBehavior)item.getData();
			return device.getAbstractDevice().getName();
		}
		return "";
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getModelId(TableItem item) {
		if (item.getData() instanceof AbstractBehavior) {
			AbstractBehavior device = (AbstractBehavior)item.getData();
			return device.getAbstractDevice().getID();
		}
		return "";
	}
}
