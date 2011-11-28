package de.ptb.epics.eve.viewer.views.deviceinspectorview;

import java.util.ArrayList;
import java.util.HashMap;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ComboBoxCellEditor;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.graphics.Color;
import org.epics.pvmanager.data.AlarmSeverity;

import de.ptb.epics.eve.data.TransportTypes;
import de.ptb.epics.eve.data.measuringstation.AbstractDevice;
import de.ptb.epics.eve.data.measuringstation.DetectorChannel;
import de.ptb.epics.eve.data.measuringstation.Device;
import de.ptb.epics.eve.data.measuringstation.MotorAxis;
import de.ptb.epics.eve.viewer.Activator;

/**
 * <code>CommonTableElement</code> is an element (row entry) of the tables 
 * defined in the
 * {@link de.ptb.epics.eve.viewer.views.deviceinspectorview.DeviceInspectorView}.
 * 
 * @author ?
 * @author Marcus Michalsky
 */
public class CommonTableElement {

	private AbstractDevice device;
	private TableViewer viewer;
	private String name;
	private CommonTableElementPV valuePv = null;
	private CommonTableElementPV gotoPv = null;
	private CommonTableElementPV unitPv = null;
	private CommonTableElementPV setPv = null;
	private CommonTableElementPV statusPv = null;
	private CommonTableElementPV movedonePv = null;
	private CommonTableElementPV stopPv = null;
	private CommonTableElementPV triggerPv = null;
	private CommonTableElementPV tweakvaluePv = null;
	private CommonTableElementPV tweakforwardPv = null;
	private CommonTableElementPV tweakreversePv = null;
	private String unit;
	private CommonTableElementEngineData engine;
	private boolean initialized = false;
	private HashMap<String, CellEditor> cellEditorHash;
	
	/**
	 * Constructs a <code>CommonTableElement</code>.
	 * 
	 * @param abstractdevice the 
	 * 		{@link de.ptb.epics.eve.data.measuringstation.AbstractDevice} the 
	 * 		element represents
	 * @param viewer the viewer the element is contained in
	 */
	public CommonTableElement(AbstractDevice abstractdevice, TableViewer viewer) {

		this.device = abstractdevice;
		this.viewer = viewer;
		name = abstractdevice.getName();
		if(name == null || name.isEmpty()) {
			this.name = abstractdevice.getID();
		}
		unit = "";
		cellEditorHash = new HashMap<String, CellEditor>();

		if(device instanceof MotorAxis) {
			MotorAxis motorAxis = (MotorAxis)device;
			engine = new CommonTableElementEngineData(abstractdevice.getID(), this);
			if ((motorAxis.getPosition() != null) && 
				(motorAxis.getPosition().getAccess().getTransport() == TransportTypes.CA)) {
				valuePv = new CommonTableElementPV(motorAxis.getPosition().
						getAccess().getVariableID(), this);
			}
			if ((motorAxis.getGoto().getAccess() != null) &&
				(motorAxis.getGoto().getAccess().getTransport() == TransportTypes.CA)) {
				gotoPv = new CommonTableElementPV(motorAxis.getGoto().
						getAccess().getVariableID(), this);
			}
			if ((motorAxis.getSet() != null) &&
				(motorAxis.getSet().getAccess().getTransport() == TransportTypes.CA)) {
				setPv = new CommonTableElementPV(motorAxis.getSet().
						getAccess().getVariableID(), this);
			}
			if (motorAxis.getUnit() != null) {
				if (motorAxis.getUnit().getAccess() != null) {
					if (motorAxis.getUnit().getAccess().getTransport() == TransportTypes.CA){
						unitPv = new CommonTableElementPV( motorAxis.getUnit().
								getAccess().getVariableID(), this);
					}
				} else {
					unit = motorAxis.getUnit().getValue();
				}
			}
			if ((motorAxis.getStatus() != null && motorAxis.getStatus().getAccess() != null) &&
				(motorAxis.getStatus().getAccess().getTransport() == TransportTypes.CA)) {
					statusPv = new CommonTableElementPV(motorAxis.getStatus().
							getAccess().getVariableID(), this);
			}
			if ((motorAxis.getMoveDone() != null && motorAxis.getMoveDone().getAccess() != null) &&
				(motorAxis.getMoveDone().getAccess().getTransport() == TransportTypes.CA)) {
					movedonePv = new CommonTableElementPV(motorAxis.getMoveDone().
							getAccess().getVariableID(), this);
			}
			if ((motorAxis.getStop() != null && motorAxis.getStop().getAccess() != null) &&
				(motorAxis.getStop().getAccess().getTransport() == TransportTypes.CA)) {
					stopPv = new CommonTableElementPV(motorAxis.getStop().
							getAccess().getVariableID(), this);
			}
			if ((motorAxis.getTweakForward() != null && motorAxis.getTweakForward().getAccess() != null) &&
				(motorAxis.getTweakForward().getAccess().getTransport() == TransportTypes.CA)) {
					tweakforwardPv = new CommonTableElementPV(motorAxis.
							getTweakForward().getAccess().getVariableID(), this);
			}
			if ((motorAxis.getTweakReverse() != null && motorAxis.getTweakReverse().getAccess() != null) &&
				(motorAxis.getTweakReverse().getAccess().getTransport() == TransportTypes.CA)) {
					tweakreversePv = new CommonTableElementPV(motorAxis.
							getTweakReverse().getAccess().getVariableID(), this);
			}
			if ((motorAxis.getTweakValue() != null && motorAxis.getTweakValue().getAccess() != null) &&
				(motorAxis.getTweakValue().getAccess().getTransport() == TransportTypes.CA)) {
					tweakvaluePv = new CommonTableElementPV(motorAxis.
							getTweakValue().getAccess().getVariableID(), this);
			}
		}
		if(device instanceof DetectorChannel) {
			DetectorChannel channel = (DetectorChannel)device;
			engine = new CommonTableElementEngineData(abstractdevice.getID(), this);
			if ((channel.getRead() != null) && 
				(channel.getRead().getAccess().getTransport() == TransportTypes.CA)) {
				valuePv = new CommonTableElementPV(channel.getRead().
						getAccess().getVariableID(), this);
			}
			if (channel.getUnit() != null){
				if (channel.getUnit().getAccess() != null) {
					if (channel.getUnit().getAccess().getTransport() == TransportTypes.CA) {
						unitPv = new CommonTableElementPV( channel.getUnit().
								getAccess().getVariableID(), this);
					}
				} else {
					unit = channel.getUnit().getValue();
				}
			}
			if (channel.getTrigger() != null &&
				channel.getTrigger().getAccess().getTransport() == TransportTypes.CA) {
					triggerPv = new CommonTableElementPV(channel.getTrigger().
						getAccess().getVariableID(), this);
			}
		}
		if(device instanceof Device) {
			Device realDevice = (Device)device;
			if ((realDevice.getValue() != null) && 
					(realDevice.getValue().getAccess().getTransport() == TransportTypes.CA)) {
				valuePv = new CommonTableElementPV( realDevice.getValue().getAccess().getVariableID(), this);
			}
			if (realDevice.getUnit() != null){
				if (realDevice.getUnit().getAccess() != null) {
					if (realDevice.getUnit().getAccess().getTransport() == TransportTypes.CA)
						unitPv = new CommonTableElementPV(realDevice.getUnit().getAccess().getVariableID(), this);
				} else {
					unit = realDevice.getUnit().getValue();
				}
			}
		}
	}
	
	/**
	 * 
	 */
	public void init() {
		initialized  = true;
	}

	/**
	 * 
	 */
	public void dispose() {
		if (valuePv != null) valuePv.disconnect();
		if (gotoPv != null) gotoPv.disconnect();
		if (unitPv != null) unitPv.disconnect();
		if (setPv != null) setPv.disconnect();
		if (statusPv != null) statusPv.disconnect();
		if (movedonePv != null) movedonePv.disconnect();
		if (stopPv != null) stopPv.disconnect();
		if (triggerPv != null) triggerPv.disconnect();
		if (tweakvaluePv != null) tweakvaluePv.disconnect();
		if (tweakforwardPv != null) tweakforwardPv.disconnect();
		if (tweakreversePv != null) tweakreversePv.disconnect();
		cellEditorHash.clear();
	}
	
	/**
	 * Checks whether the process variable corresponding to the given property 
	 * is read only.
	 * 
	 * @param property the property that should be checked
	 * @return <code>true</code> if the process variable corresponding to the 
	 * 			given property is read only, <code>false</code> otherwise
	 */
	public boolean isReadonly(String property) {
		if (property.equals("value") && (valuePv != null)) {
			return valuePv.isReadOnly();
		}
		else if (property.equals("goto") && (gotoPv != null)) {
			return gotoPv.isReadOnly();
		}
		else if (property.equals("unit") && (unitPv != null)) {
			return unitPv.isReadOnly();
		}
		else if (property.equals("set") && (setPv != null)) {
			return setPv.isReadOnly();
		}
		else if (property.equals("status") && (statusPv != null)) {
			return statusPv.isReadOnly();
		}
		else if (property.equals("stop") && (stopPv != null)) {
			return stopPv.isReadOnly();
		}
		else if (property.equals("trigger") && (triggerPv != null)) {
			return triggerPv.isReadOnly();
		}
		else if (property.equals("tweakvalue") && (tweakvaluePv != null)) {
			return tweakvaluePv.isReadOnly();
		}
		else if (property.equals("tweakforward") && (tweakforwardPv != null)) {
			return tweakforwardPv.isReadOnly();
		}
		else if (property.equals("tweakreverse") && (tweakreversePv != null)) {
			return tweakreversePv.isReadOnly();
		}
		return true;
	}
	
	/**
	 * Checks whether the process variable corresponding to the given property 
	 * is discrete.
	 * 
	 * @param property the property that should be checked
	 * @return <code>true</code> if the process variable corresponding to the 
	 * 			given property is discrete, <code>false</code> otherwise
	 */
	public boolean isDiscrete(String property) {
		if (property.equals("value") && valuePv != null) {
			return valuePv.isDiscrete();
		}
		else if (property.equals("goto") && gotoPv != null) {
			return gotoPv.isDiscrete();
		}
		else if (property.equals("unit") && unitPv != null) {
			return unitPv.isDiscrete();
		}
		else if (property.equals("set") && setPv != null) {
			return setPv.isDiscrete();
		}
		else if (property.equals("status") && statusPv != null) {
			return statusPv.isDiscrete();
		}
		else if (property.equals("stop") && stopPv != null) {
			return stopPv.isDiscrete();
		}
		else if (property.equals("trigger") && triggerPv != null) {
			return triggerPv.isDiscrete();
		}
		else if (property.equals("tweakvalue") && tweakvaluePv != null) {
			return tweakvaluePv.isDiscrete();
		}
		else if (property.equals("tweakforward") && tweakforwardPv != null) {
			return tweakforwardPv.isDiscrete();
		}
		else if (property.equals("tweakreverse") && tweakreversePv != null) {
			return tweakreversePv.isDiscrete();
		}
		return false;
	}

	/**
	 * Checks whether the process variable corresponding to the given property 
	 * is connected.
	 * 
	 * @param property the property that should be checked
	 * @return <code>true</code> if the process variable corresponding to the 
	 * 			given property is connected, <code>false</code> otherwise
	 */
	public boolean isConnected(String property) {
		if (property.equals("name") && valuePv != null) {
			return valuePv.isConnected();
		}
		else if (property.equals("value") && valuePv != null) {
			return valuePv.isConnected();
		}
		else if (property.equals("goto") && gotoPv != null) {
			return gotoPv.isConnected();
		}
		else if (property.equals("unit") && unitPv != null) {
			return unitPv.isConnected();
		}
		else if (property.equals("set") && setPv != null) {
			return setPv.isConnected();
		}
		else if (property.equals("status") && statusPv != null) {
			return statusPv.isConnected();
		}
		else if (property.equals("stop") && stopPv != null) {
			return stopPv.isConnected();
		}
		else if (property.equals("trigger") && triggerPv != null) {
			return triggerPv.isConnected();
		}
		else if (property.equals("tweakvalue") && tweakvaluePv != null) {
			return tweakvaluePv.isConnected();
		}
		else if (property.equals("tweakforward") && tweakforwardPv != null) {
			return tweakforwardPv.isConnected();
		}
		else if (property.equals("tweakreverse") && tweakreversePv != null) {
			return tweakreversePv.isConnected();
		}
		return false;
	}
	
	/**
	 * 
	 * @param property
	 * @return
	 */
	public Color getConnectColor(String property) {
		if (isConnected(property)) {
			return Activator.getDefault().getColor("COLOR_PV_CONNECTED");
		}
		return Activator.getDefault().getColor("COLOR_PV_DISCONNECTED");
	}

	/**
	 * 
	 * @param property
	 * @return
	 */
	public Color getSeverityColor(String property) {
		AlarmSeverity status = AlarmSeverity.UNDEFINED; 
		if (property.equals("value")) {
			if (valuePv != null) status = valuePv.getStatus();
		}
		else if (property.equals("unit")) {
			if (unitPv != null) status = unitPv.getStatus();
		}
		else if (property.equals("goto")) {
			if (gotoPv != null) status = gotoPv.getStatus();
		}
		else if (property.equals("set")) {
			if (gotoPv != null) status = gotoPv.getStatus();
		}
		else if (property.equals("goto")) {
			if (gotoPv != null) status = gotoPv.getStatus();
		}
		else if (property.equals("tweakvalue")) {
			if (tweakvaluePv != null) status = tweakvaluePv.getStatus();
		}
		else if ((property.equals("status")) && (statusPv != null)) {
			String statusVal = getValue("status");
			if (statusVal.equals("Limit")) {
				return Activator.getDefault().getColor("COLOR_PV_MAJOR");
			}
			return Activator.getDefault().getColor("COLOR_PV_OK");
		}

		String color = "COLOR_PV_INITIAL";
		
		switch(status) {
			case INVALID: color = "COLOR_PV_INVALID"; break;
			case MAJOR: color = "COLOR_PV_MAJOR"; break;
			case MINOR: color = "COLOR_PV_MINOR"; break;
			case NONE: color = "COLOR_PV_OK"; break;
			case UNDEFINED: color = "COLOR_PV_INITIAL"; break;
		}
		
		return Activator.getDefault().getColor(color);
	}

	/**
	 * 
	 * @param property
	 * @return
	 */
	public String[] getSelectStrings(String property) {
		if (isDiscrete(property)){
			if (property.equals("value")){
				if ((valuePv != null) && valuePv.isDiscrete()) 
					return valuePv.getDiscreteValues();
			}
			else if (property.equals("goto")){
				if ((gotoPv != null) && gotoPv.isDiscrete()) 
					return gotoPv.getDiscreteValues();
			}
			else if (property.equals("set")){
				if ((setPv != null) && setPv.isDiscrete())
					return setPv.getDiscreteValues();
			}
		}
		return new ArrayList<String>().toArray(new String[0]);
	}

	/**
	 * 
	 */
	public void update() {

		final CommonTableElement thisCommonTableElement = this;
		if (!initialized) return;
		if (!viewer.getControl().isDisposed()){
			viewer.getControl().getDisplay().asyncExec(new Runnable() {
				
				@Override
				public void run() {
					if (!viewer.getControl().isDisposed()) {
						viewer.update(thisCommonTableElement, null);
					}
				}
			});
		}
	}

	/**
	 * 
	 * @param cellEditor
	 * @param column
	 */
	public void setCellEditor(CellEditor cellEditor, String column) {
		cellEditorHash.put(column, cellEditor);
	}

	/**
	 * 
	 * @param column
	 * @return
	 */
	public CellEditor getCellEditor(String column) {
		if (cellEditorHash.containsKey(column)) {
			return cellEditorHash.get(column);
		}
		return null;
	}

	/**
	 * 
	 * @param property
	 * @return
	 */
	public String getValue(String property) {
		if (property.equals("set") && (setPv != null))
			return setPv.getValue();
		else if (property.equals("goto") && (gotoPv != null))
			return gotoPv.getValue();
		else if (property.equals("engine") && (engine != null))
			return engine.getValue();
		else if (property.equals( "name"))
			return name;
		else if (property.equals("value") && (valuePv != null)) {
			return valuePv.getValue();
		}
		else if (property.equals("status") && (statusPv != null)) {
			if(movedonePv != null && movedonePv.getValue().equals("0")) {
				return "Moving";
			}
			String valueString = statusPv.getValue();
			try {
				int status = (int) Double.parseDouble(valueString);
				if((status & 4) > 0) {
					return "Limit (+)";
				} else if ((status & 8) > 0 || (status & 128) > 0) {
					return "Home";
				} else if ((status & 512) > 0) {
					return "Problem";
				} else if ((status & 1024) > 0) {
					return "Moving";
				} else if ((status & 8192) > 0) {
					return "Limit (-)";
				} else {
					return "Idle";
				}
			} catch (Exception e) {
				return valueString;
			}
		}
		else if (property.equals("unit")) {
			if (unitPv != null) {
				return unitPv.getValue();
			}
			return unit;
		}
		else if (property.equals("tweakvalue") && (tweakvaluePv != null)) {
			return tweakvaluePv.getValue();
		}
		return "";
	}

	/**
	 * 
	 * @param value
	 * @param column
	 */
	public void setValue(Object value, String column) {
		String newValue = "";
		if (getCellEditor(column) instanceof ComboBoxCellEditor){
			int index = 0;
			String[] items = ((ComboBoxCellEditor)getCellEditor(column)).getItems();
			if (value instanceof Integer) index = ((Integer)value).intValue();
			if (items.length > index) newValue = items[index];
		}
		else if (value instanceof String)
			newValue = (String) value;

		if (column.equals("set") && (setPv != null))
			setPv.setValue(newValue);
		else if (column.equals("goto") && (gotoPv != null))
			gotoPv.setValue(newValue);
		else if (column.equals("value") && (valuePv != null))
			valuePv.setValue(newValue);
		else if (column.equals("unit") && (unitPv != null))
			unitPv.setValue(newValue);
		else if (column.equals("tweakvalue") && (tweakvaluePv != null))
			tweakvaluePv.setValue(newValue);
	}

	/**
	 * 
	 */
	public void trigger() {
		if (triggerPv != null && triggerPv.isConnected()) {
			DetectorChannel channel = (DetectorChannel)device;
			if (channel.getTrigger().getValue() != null)
				triggerPv.setValue(channel.getTrigger().getValue().getDefaultValue());
		}
	}

	/**
	 * 
	 */
	public void stop() {
		if (stopPv != null && stopPv.isConnected()) {
			MotorAxis axis = (MotorAxis)device;
			if (axis.getStop().getValue() != null) {
				stopPv.setValue(Integer.parseInt(
						axis.getStop().getValue().getDefaultValue()));
			}
		}
	}

	/**
	 * Tweaks the device in the given direction with the amount of the tweak 
	 * value.
	 * 
	 * @precondition Depending on the direction (<code>forward</code> parameter) 
	 * 				either the tweak forward process variable or the tweak 
	 * 				reverse process variable must exist (usually the case for 
	 * 				motor axes)
	 * @param forward the direction of the tweak<br>
	 * 			<code>true</code> tweaks forward, 
	 * 			<code>false</code> tweaks backward (reverse)
	 */
	public void tweak(boolean forward) {
		if (forward && (tweakforwardPv != null) && 
			tweakforwardPv.isConnected()) {
			MotorAxis motorAxis = (MotorAxis)device;
			if (motorAxis.getTweakForward().getValue() != null) {
				tweakforwardPv.setValue(
					motorAxis.getTweakForward().getValue().getDefaultValue());
			}
		}
		else if(!forward && (tweakreversePv != null) && 
				tweakreversePv.isConnected()) {
			MotorAxis motorAxis = (MotorAxis)device;
			if (motorAxis.getTweakReverse().getValue() != null) {
				tweakreversePv.setValue(
					motorAxis.getTweakReverse().getValue().getDefaultValue());
			}
		}
	}

	/**
	 * Returns the {@link de.ptb.epics.eve.data.measuringstation.AbstractDevice} 
	 * the <code>CommonTableElement</code> represents.
	 * 
	 * @return the {@link de.ptb.epics.eve.data.measuringstation.AbstractDevice}
	 * 			the <code>CommonTableElement</code> represents
	 */
	public AbstractDevice getAbstractDevice() {
		return device;
	}
}