package de.ptb.epics.eve.editor.adapters;

import org.eclipse.core.runtime.IAdapterFactory;
import org.eclipse.ui.views.properties.IPropertySource;

import de.ptb.epics.eve.editor.gef.editparts.ScanModuleEditPart;

/**
 * @author Marcus Michalsky
 * @since 1.18
 */
public class ScanModuleAdapterFactory implements IAdapterFactory {
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Object getAdapter(Object adaptableObject, Class adapterType) {
		if (adapterType == IPropertySource.class) {
			return new ScanModulePropertySource(
					((ScanModuleEditPart) adaptableObject).getModel());
		}
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Class[] getAdapterList() {
		return new Class[] {IPropertySource.class};
	}
}