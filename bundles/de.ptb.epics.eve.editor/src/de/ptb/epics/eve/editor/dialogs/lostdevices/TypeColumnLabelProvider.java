package de.ptb.epics.eve.editor.dialogs.lostdevices;

import org.eclipse.jface.viewers.ColumnLabelProvider;

import de.ptb.epics.eve.data.scandescription.processors.ScanDescriptionLoaderDeviceMessage;

/**
 * @author Marcus Michalsky
 * @since 1.5
 */
public class TypeColumnLabelProvider extends ColumnLabelProvider {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getText(Object element) {
		return ((ScanDescriptionLoaderDeviceMessage) element).getType()
				.toString();
	}
}
