package de.ptb.epics.eve.viewer.views.devicesview;

import org.csstudio.csdata.ProcessVariable;
import org.eclipse.core.runtime.IAdapterFactory;

import de.ptb.epics.eve.data.measuringstation.DetectorChannel;
import de.ptb.epics.eve.data.measuringstation.MotorAxis;

/**
 * <code>AbstractDeviceAdapterFactory</code>.
 * 
 * @author Marcus Michalsky
 * @since 1.1
 */
public class AbstractDeviceAdapterFactory implements IAdapterFactory {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Object getAdapter(Object adaptableObject, Class adapterType) {
		if(adapterType == ProcessVariable.class) {
			if(adaptableObject instanceof MotorAxis) {
				return new ProcessVariable(((MotorAxis)adaptableObject).
						getPosition().getAccess().getVariableID());
			} else if(adaptableObject instanceof DetectorChannel) {
				return new ProcessVariable(((DetectorChannel)adaptableObject).
						getRead().getAccess().getVariableID());
			}
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