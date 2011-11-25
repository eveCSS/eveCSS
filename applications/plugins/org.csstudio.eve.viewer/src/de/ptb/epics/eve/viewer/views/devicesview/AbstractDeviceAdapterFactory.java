package de.ptb.epics.eve.viewer.views.devicesview;

import org.csstudio.csdata.ProcessVariable;
import org.eclipse.core.runtime.IAdapterFactory;

import de.ptb.epics.eve.data.measuringstation.DetectorChannel;
import de.ptb.epics.eve.data.measuringstation.MotorAxis;

/**
 * @author mmichals
 *
 */
public class AbstractDeviceAdapterFactory implements IAdapterFactory {

	/* (non-Javadoc)
	 * @see org.eclipse.core.runtime.IAdapterFactory#getAdapter(java.lang.Object, java.lang.Class)
	 */
	@Override
	public Object getAdapter(Object adaptableObject, Class adapterType) {
		if(adapterType == ProcessVariable.class) {
			if(adaptableObject instanceof MotorAxis) {
				return new ProcessVariable(((MotorAxis)adaptableObject).getPosition().getAccess().getVariableID());
			} else if(adaptableObject instanceof DetectorChannel) {
				return new ProcessVariable(((DetectorChannel)adaptableObject).getRead().getAccess().getVariableID());
			}
		}
		return null;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.core.runtime.IAdapterFactory#getAdapterList()
	 */
	@Override
	public Class[] getAdapterList() {
		return new Class[] {ProcessVariable.class};
	}

}
