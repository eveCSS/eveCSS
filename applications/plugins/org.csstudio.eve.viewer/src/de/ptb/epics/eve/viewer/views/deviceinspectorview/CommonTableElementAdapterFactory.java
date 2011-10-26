package de.ptb.epics.eve.viewer.views.deviceinspectorview;

import org.csstudio.csdata.ProcessVariable;
import org.eclipse.core.runtime.IAdapterFactory;

import de.ptb.epics.eve.data.measuringstation.AbstractDevice;
import de.ptb.epics.eve.data.measuringstation.DetectorChannel;
import de.ptb.epics.eve.data.measuringstation.Device;
import de.ptb.epics.eve.data.measuringstation.MotorAxis;

/**
 * <code>CommonTableElementAdapterFactory</code> produces adapters of 
 * {@link de.ptb.epics.eve.viewer.views.deviceinspectorview.CommonTableElement}s.
 * 
 * @author Marcus Michalsky
 * @since 1.1
 */
public class CommonTableElementAdapterFactory implements IAdapterFactory {
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Object getAdapter(Object adaptableObject, Class adapterType) {
		if(adapterType == ProcessVariable.class) {
			CommonTableElement cte = (CommonTableElement)adaptableObject;
			AbstractDevice device = cte.getAbstractDevice();
			if(device instanceof MotorAxis) {
				return new ProcessVariable(((MotorAxis)device).
						getPosition().getAccess().getVariableID());
			} else if(device instanceof DetectorChannel) {
				return new ProcessVariable(((DetectorChannel)device).
						getRead().getAccess().getVariableID());
			} else if(device instanceof Device) {
				return new ProcessVariable(((Device)device).
						getValue().getAccess().getVariableID());
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