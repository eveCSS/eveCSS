package de.ptb.epics.eve.editor.dialogs.monitoroptions;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.widgets.Table;

import de.ptb.epics.eve.data.measuringstation.AbstractDevice;
import de.ptb.epics.eve.data.measuringstation.Option;
import de.ptb.epics.eve.util.swt.FontHelper;

/**
 * @author Marcus Michalsky
 * @since 1.14
 */
public class TableViewerContentProvider implements IStructuredContentProvider {
	
	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings("unchecked")
	@Override
	public Object[] getElements(Object inputElement) {
		List<Option> options = new ArrayList<Option>();

		for (AbstractDevice dev : (List<AbstractDevice>)inputElement) {
			for (Option o : dev.getOptions()) {
				options.add(o);
			}
		}

		return options.toArray();
	}

	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings({"unchecked"})
	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		List<AbstractDevice> devices = (List<AbstractDevice>)newInput;
		if (devices == null) {
			return;
		}
		Table table = ((TableViewer)viewer).getTable();
		GC gc = new GC(table);
		int optionColumnWidth = 120;
		int deviceColumnWidth = 120;
		for (AbstractDevice device : devices) {
			int deviceNameWidth = FontHelper.getCharWidth(gc, device.getName())
					+ FontHelper.MARGIN_WIDTH;
			if (deviceNameWidth > deviceColumnWidth) {
				deviceColumnWidth = deviceNameWidth;
			}
			for (Option o : device.getOptions()) {
				int optionNameWidth = FontHelper.getCharWidth(gc, o.getName())
						+ FontHelper.MARGIN_WIDTH;
				if (optionNameWidth > optionColumnWidth) {
					optionColumnWidth = optionNameWidth;
				}
			}
		}
		table.getColumn(1).setWidth(optionColumnWidth);
		table.getColumn(2).setWidth(deviceColumnWidth);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void dispose() {	
	}
}