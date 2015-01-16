package de.ptb.epics.eve.editor.dialogs.monitoroptions;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;

import de.ptb.epics.eve.data.measuringstation.AbstractDevice;
import de.ptb.epics.eve.data.measuringstation.Option;

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
	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void dispose() {	
	}
}