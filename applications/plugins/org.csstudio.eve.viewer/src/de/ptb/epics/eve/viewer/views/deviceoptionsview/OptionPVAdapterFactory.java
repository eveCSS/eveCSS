package de.ptb.epics.eve.viewer.views.deviceoptionsview;

import org.eclipse.core.runtime.IAdapterFactory;

import org.csstudio.csdata.ProcessVariable;

/**
 * <code>OptionPVAdapterFactory</code>.
 * 
 * @author Marcus Michalsky
 * @since 
 */
public class OptionPVAdapterFactory implements IAdapterFactory {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Object getAdapter(Object adaptableObject, Class adapterType) {
		if(adapterType == ProcessVariable.class) {
			return new ProcessVariable(((OptionPV)adaptableObject).getName());
		}
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Class[] getAdapterList() {
		return new Class[] {ProcessVariable.class};
	}
}