package de.ptb.epics.eve.viewer.views.deviceinspectorview;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ComboBoxCellEditor;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.graphics.Color;
import org.epics.vtype.AlarmSeverity;

import de.ptb.epics.eve.data.TransportTypes;
import de.ptb.epics.eve.data.measuringstation.AbstractDevice;
import de.ptb.epics.eve.data.measuringstation.Detector;
import de.ptb.epics.eve.data.measuringstation.DetectorChannel;
import de.ptb.epics.eve.data.measuringstation.Device;
import de.ptb.epics.eve.data.measuringstation.Motor;
import de.ptb.epics.eve.data.measuringstation.MotorAxis;
import de.ptb.epics.eve.viewer.Activator;
import de.ptb.epics.eve.viewer.MessageSource;
import de.ptb.epics.eve.viewer.views.messagesview.Levels;
import de.ptb.epics.eve.viewer.views.messagesview.ViewerMessage;

/**
 * <code>CommonTableElement</code> is an element (row entry) of the tables 
 * defined in the
 * {@link de.ptb.epics.eve.viewer.views.deviceinspectorview.ui.DeviceInspectorView}.
 * 
 * @author ?
 * @author Marcus Michalsky
 * @author Hartmut Scherr
 */
public class CommonTableElement {
	public static final String NAME_PROP = "name";
	public static final String VALUE_PROP = "value";
	public static final String GOTO_PROP = "goto";
	public static final String ENGINE_PROP = "engine";
	public static final String UNIT_PROP = "unit";
	public static final String STOP_PROP = "stop";
	public static final String STATUS_PROP = "status";
	public static final String TWEAKVALUE_PROP = "tweakvalue";
	public static final String DEFINE_PROP = "define";
	
	private static final Logger LOGGER = 
			Logger.getLogger(CommonTableElement.class.getName());
	
	private AbstractDevice device;
	private TableViewer viewer;
	private String name;
	private CommonTableElementPV valuePv = null;
	private CommonTableElementPV gotoPv = null;
	private CommonTableElementPV unitPv = null;
	private CommonTableElementPV statusPv = null;
	private CommonTableElementPV movedonePv = null;
	private CommonTableElementPV stopPv = null;
	private CommonTableElementPV triggerPv = null;
	private CommonTableElementPV tweakvaluePv = null;
	private CommonTableElementPV tweakforwardPv = null;
	private CommonTableElementPV tweakreversePv = null;
	private CommonTableElementPV offsetPv = null;
	private CommonTableElementPV softHighLimitPv = null;
	private CommonTableElementPV softLowLimitPv = null;
	private CommonTableElementPV limitViolationPv = null;
	private String unit;
	private CommonTableElementEngineData engine;
	private boolean initialized = false;
	private Map<String, CellEditor> cellEditorHash;
	
	private TransportTypes type;
	
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
			this.type = motorAxis.getPosition().getAccess().getTransport();
			engine = new CommonTableElementEngineData(abstractdevice.getID(), 
					this);
			if ((motorAxis.getPosition() != null) && 
					(motorAxis.getPosition().getAccess().getTransport() == 
					TransportTypes.CA)) {
				valuePv = new CommonTableElementPV(motorAxis.getPosition().
						getAccess().getVariableID(), this);
			}
			if ((motorAxis.getGoto().getAccess() != null) &&
					(motorAxis.getGoto().getAccess().getTransport() == 
					TransportTypes.CA)) {
				// getMotorTrigger
				String motorTrigger = null;
				if (motorAxis.getTrigger() != null &&
						motorAxis.getTrigger().getAccess().getTransport() == 
						TransportTypes.CA) {
					motorTrigger = motorAxis.getTrigger().getAccess().
							getVariableID();
				} else if (motorAxis.getParent() != null) {
					Motor parent = motorAxis.getMotor();
					if (parent.getTrigger() != null &&
							parent.getTrigger().getAccess().getTransport() == 
							TransportTypes.CA) {
						motorTrigger = parent.getTrigger().getAccess().
								getVariableID();
					}
				}
				gotoPv = new CommonTableElementPV(motorAxis.getGoto().
						getAccess().getVariableID(), motorTrigger, this );
			}
			if (motorAxis.getUnit() != null) {
				if (motorAxis.getUnit().getAccess() != null) {
					if (motorAxis.getUnit().getAccess().getTransport() == 
							TransportTypes.CA){
						unitPv = new CommonTableElementPV( motorAxis.getUnit().
								getAccess().getVariableID(), this);
					}
				} else {
					unit = motorAxis.getUnit().getValue();
				}
			}
			if ((motorAxis.getStatus() != null && 
					motorAxis.getStatus().getAccess() != null) &&
					(motorAxis.getStatus().getAccess().getTransport() == 
					TransportTypes.CA)) {
				statusPv = new CommonTableElementPV(motorAxis.getStatus().
							getAccess().getVariableID(), this);
			}
			if ((motorAxis.getMoveDone() != null && 
					motorAxis.getMoveDone().getAccess() != null) &&
					(motorAxis.getMoveDone().getAccess().getTransport() == 
					TransportTypes.CA)) {
				movedonePv = new CommonTableElementPV(motorAxis.getMoveDone().
							getAccess().getVariableID(), this);
			}
			if ((motorAxis.getStop() != null && 
					motorAxis.getStop().getAccess() != null) &&	
					(motorAxis.getStop().getAccess().getTransport() == 
					TransportTypes.CA)) {
				stopPv = new CommonTableElementPV(motorAxis.getStop().
							getAccess().getVariableID(), this);
			}
			if ((motorAxis.getTweakForward() != null && 
					motorAxis.getTweakForward().getAccess() != null) &&
					(motorAxis.getTweakForward().getAccess().getTransport() == 
					TransportTypes.CA)) {
				tweakforwardPv = new CommonTableElementPV(motorAxis.
							getTweakForward().getAccess().getVariableID(), this);
			}
			if ((motorAxis.getTweakReverse() != null && 
					motorAxis.getTweakReverse().getAccess() != null) &&
					(motorAxis.getTweakReverse().getAccess().getTransport() == 
					TransportTypes.CA)) {
				tweakreversePv = new CommonTableElementPV(motorAxis.
							getTweakReverse().getAccess().getVariableID(), this);
			}
			if ((motorAxis.getTweakValue() != null && 
					motorAxis.getTweakValue().getAccess() != null) &&
					(motorAxis.getTweakValue().getAccess().getTransport() == 
					TransportTypes.CA)) {
				tweakvaluePv = new CommonTableElementPV(motorAxis.
							getTweakValue().getAccess().getVariableID(), this);
			}
			if ((motorAxis.getOffset() != null && 
					motorAxis.getOffset().getAccess() != null) &&
					(motorAxis.getOffset().getAccess().getTransport() == 
					TransportTypes.CA)) {
				offsetPv = new CommonTableElementPV(motorAxis.
							getOffset().getAccess().getVariableID(), this);
				// set Pv's for Soft High and Low Limit
				if (motorAxis.getSoftHighLimit() != null) {
					softHighLimitPv = new CommonTableElementPV(motorAxis
							.getSoftHighLimit().getAccess().getVariableID(),
							this);
				}
				if (motorAxis.getSoftLowLimit() != null) {
					softLowLimitPv = new CommonTableElementPV(motorAxis
							.getSoftLowLimit().getAccess().getVariableID(),
							this);
				}
			}
			if (motorAxis.getLimitViolation() != null) {
				this.limitViolationPv = new CommonTableElementPV(motorAxis
						.getLimitViolation().getAccess().getVariableID(), this);
			}
		}
		if(device instanceof DetectorChannel) {
			DetectorChannel channel = (DetectorChannel)device;
			this.type = channel.getRead().getAccess().getTransport();
			engine = new CommonTableElementEngineData(abstractdevice.getID(), 
					this);
			if ((channel.getRead() != null) && 
					(channel.getRead().getAccess().getTransport() == 
					TransportTypes.CA)) {
				valuePv = new CommonTableElementPV(channel.getRead().
						getAccess().getVariableID(), this);
			}
			if (channel.getUnit() != null){
				if (channel.getUnit().getAccess() != null) {
					if (channel.getUnit().getAccess().getTransport() == 
							TransportTypes.CA) {
						unitPv = new CommonTableElementPV( channel.getUnit().
								getAccess().getVariableID(), this);
					}
				} else {
					unit = channel.getUnit().getValue();
				}
			}
			if (channel.getTrigger() != null &&
					channel.getTrigger().getAccess().getTransport() == 
					TransportTypes.CA) {
				triggerPv = new CommonTableElementPV(channel.getTrigger().
						getAccess().getVariableID(), this);
			} else if (channel.getParent() != null) {
				Detector parent = channel.getDetector();
				if (parent.getTrigger() != null &&
						parent.getTrigger().getAccess().getTransport() == 
						TransportTypes.CA) {
					triggerPv = new CommonTableElementPV(parent.getTrigger().
								getAccess().getVariableID(), this);
				}
			}
			
			if (channel.getStop() != null &&
					channel.getStop().getAccess().getTransport() == 
					TransportTypes.CA) {
				stopPv = new CommonTableElementPV(channel.getStop().getAccess()
						.getVariableID(), this);
			} else if (channel.getParent() != null) {
				Detector parent = channel.getDetector();
				if (parent.getStop() != null &&
						parent.getStop().getAccess().getTransport() == 
						TransportTypes.CA) {
					stopPv = new CommonTableElementPV(parent.getStop()
							.getAccess().getVariableID(), this);
				}
			}
			
			if (channel.getStatus() != null &&
					channel.getStatus().getAccess().getTransport() == 
					TransportTypes.CA) {
				statusPv = new CommonTableElementPV(channel.getStatus()
						.getAccess().getVariableID(), this);
			} else if (channel.getParent() != null) {
				Detector parent = channel.getDetector();
				if (parent.getStatus() != null &&
						parent.getStatus().getAccess().getTransport() == 
						TransportTypes.CA) {
					statusPv = new CommonTableElementPV(parent.getStatus()
							.getAccess().getVariableID(), this);
				}
			}
		}
		if(device instanceof Device) {
			Device realDevice = (Device)device;
			if ((realDevice.getValue() != null) && 
					(realDevice.getValue().getAccess().getTransport() == 
					TransportTypes.CA)) {
				valuePv = new CommonTableElementPV(
						realDevice.getValue().getAccess().getVariableID(), this);
			}
			if (realDevice.getUnit() != null){
				if (realDevice.getUnit().getAccess() != null) {
					if (realDevice.getUnit().getAccess().getTransport() == 
							TransportTypes.CA) {
						unitPv = new CommonTableElementPV(realDevice.getUnit().
								getAccess().getVariableID(), this);
					}
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
		if (valuePv != null) {
			valuePv.disconnect();
		}
		if (gotoPv != null) {
			gotoPv.disconnect();
		}
		if (unitPv != null) {
			unitPv.disconnect();
		}
		if (statusPv != null) {
			statusPv.disconnect();
		}
		if (movedonePv != null) {
			movedonePv.disconnect();
		}
		if (stopPv != null) {
			stopPv.disconnect();
		}
		if (triggerPv != null) {
			triggerPv.disconnect();
		}
		if (tweakvaluePv != null) {
			tweakvaluePv.disconnect();
		}
		if (tweakforwardPv != null) {
			tweakforwardPv.disconnect();
		}
		if (tweakreversePv != null) {
			tweakreversePv.disconnect();
		}
		if (offsetPv != null) {
			offsetPv.disconnect();
		}
		if (softHighLimitPv != null) {
			softHighLimitPv.disconnect();
		}
		if (softLowLimitPv != null) {
			softLowLimitPv.disconnect();
		}
		if (limitViolationPv != null) {
			limitViolationPv.disconnect();
		}
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
		if (property.equals(CommonTableElement.VALUE_PROP) && (valuePv != null)) {
			return valuePv.isReadOnly();
		}
		else if (property.equals(CommonTableElement.GOTO_PROP) && (gotoPv != null)) {
			return gotoPv.isReadOnly();
		}
		else if (property.equals(CommonTableElement.UNIT_PROP) && (unitPv != null)) {
			return unitPv.isReadOnly();
		}
		else if (property.equals(CommonTableElement.STATUS_PROP) && (statusPv != null)) {
			return statusPv.isReadOnly();
		}
		else if (property.equals("stop") && (stopPv != null)) {
			return stopPv.isReadOnly();
		}
		else if (property.equals("trigger") && (triggerPv != null)) {
			return triggerPv.isReadOnly();
		}
		else if (property.equals(CommonTableElement.TWEAKVALUE_PROP) && (tweakvaluePv != null)) {
			return tweakvaluePv.isReadOnly();
		}
		else if (property.equals("tweakforward") && (tweakforwardPv != null)) {
			return tweakforwardPv.isReadOnly();
		}
		else if (property.equals("tweakreverse") && (tweakreversePv != null)) {
			return tweakreversePv.isReadOnly();
		}
		else if (property.equals(CommonTableElement.DEFINE_PROP) && (offsetPv != null)) {
			return offsetPv.isReadOnly();
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
		if (property.equals(CommonTableElement.VALUE_PROP) && valuePv != null) {
			return valuePv.isDiscrete();
		}
		else if (property.equals(CommonTableElement.GOTO_PROP) && gotoPv != null) {
			return gotoPv.isDiscrete();
		}
		else if (property.equals(CommonTableElement.UNIT_PROP) && unitPv != null) {
			return unitPv.isDiscrete();
		}
		else if (property.equals(CommonTableElement.STATUS_PROP) && statusPv != null) {
			return statusPv.isDiscrete();
		}
		else if (property.equals("stop") && stopPv != null) {
			return stopPv.isDiscrete();
		}
		else if (property.equals("trigger") && triggerPv != null) {
			return triggerPv.isDiscrete();
		}
		else if (property.equals(CommonTableElement.TWEAKVALUE_PROP) && tweakvaluePv != null) {
			return tweakvaluePv.isDiscrete();
		}
		else if (property.equals("tweakforward") && tweakforwardPv != null) {
			return tweakforwardPv.isDiscrete();
		}
		else if (property.equals("tweakreverse") && tweakreversePv != null) {
			return tweakreversePv.isDiscrete();
		}
		else if (property.equals(CommonTableElement.DEFINE_PROP) && offsetPv != null) {
			return offsetPv.isDiscrete();
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
		if (property.equals("name")) {
			if (this.type != null && this.type.equals(TransportTypes.LOCAL)) {
				return true;
			}
			if (valuePv != null) {
				return valuePv.isConnected();
			}
		}
		else if (property.equals(CommonTableElement.VALUE_PROP)) {
			if (this.type != null && this.type.equals(TransportTypes.LOCAL)) {
				return true;
			}
			if (valuePv != null) {
				return valuePv.isConnected();
			}
		}
		else if (property.equals(CommonTableElement.GOTO_PROP) && gotoPv != null) {
			return gotoPv.isConnected();
		}
		else if (property.equals(CommonTableElement.UNIT_PROP) && unitPv != null) {
			return unitPv.isConnected();
		}
		else if (property.equals(CommonTableElement.STATUS_PROP) && statusPv != null) {
			return statusPv.isConnected();
		}
		else if (property.equals("stop") && stopPv != null) {
			return stopPv.isConnected();
		}
		else if (property.equals("trigger") && triggerPv != null) {
			return triggerPv.isConnected();
		}
		else if (property.equals(CommonTableElement.TWEAKVALUE_PROP) && tweakvaluePv != null) {
			return tweakvaluePv.isConnected();
		}
		else if (property.equals("tweakforward") && tweakforwardPv != null) {
			return tweakforwardPv.isConnected();
		}
		else if (property.equals("tweakreverse") && tweakreversePv != null) {
			return tweakreversePv.isConnected();
		}
		else if (property.equals(CommonTableElement.DEFINE_PROP) && offsetPv != null) {
			return offsetPv.isConnected();
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
		if ((property.equals(CommonTableElement.VALUE_PROP)) && (valuePv != null)) {
			String statusVal = getValue(CommonTableElement.STATUS_PROP);
			if (statusVal.equals(PVStatus.MOVING.toString())) {
				return Activator.getDefault().getColor("COLOR_PV_MOVING");
			}
			status = valuePv.getSeverity();
		} else if (property.equals(CommonTableElement.UNIT_PROP) && unitPv != null) {
				status = unitPv.getSeverity();
		} else if (property.equals(CommonTableElement.GOTO_PROP) && gotoPv != null) {
				status = gotoPv.getSeverity();
		} else if (property.equals(CommonTableElement.TWEAKVALUE_PROP) && tweakvaluePv != null) {
				status = tweakvaluePv.getSeverity();
		} else if ((property.equals(CommonTableElement.STATUS_PROP)) && (statusPv != null)) {
			String statusVal = getValue(CommonTableElement.STATUS_PROP);
			if (statusVal.equals(PVStatus.MOVING.toString())) {
				return Activator.getDefault().getColor("COLOR_PV_MOVING");
			}
			if (statusVal.equals(PVStatus.LIMIT_POSITIVE.toString()) 
					|| statusVal.equals(PVStatus.LIMIT_NEGATIVE.toString())
					|| statusVal.equals(PVStatus.SOFT_LIMIT.toString())) {
				status = AlarmSeverity.MAJOR;
			} else {
				status = AlarmSeverity.NONE;
			}
		}
		String color = "COLOR_PV_INITIAL";
		
		switch(status) {
			case INVALID: 
				color = "COLOR_PV_INVALID"; 
				break;
			case MAJOR: 
				color = "COLOR_PV_MAJOR"; 
				break;
			case MINOR: 
				color = "COLOR_PV_MINOR"; 
				break;
			case NONE: 
				color = "COLOR_PV_OK"; 
				break;
			case UNDEFINED: 
				color = "COLOR_PV_INITIAL"; 
				break;
		}
		return Activator.getDefault().getColor(color);
	}

	/**
	 * 
	 * @param property
	 * @return
	 */
	public String[] getSelectStrings(String property) {
		if (isDiscrete(property)) {
			if (property.equals(CommonTableElement.VALUE_PROP)) {
				if ((valuePv != null) && valuePv.isDiscrete()) {
					return valuePv.getDiscreteValues();
				}
			} else if (property.equals(CommonTableElement.GOTO_PROP)) {
				if ((gotoPv != null) && gotoPv.isDiscrete()) {
					return gotoPv.getDiscreteValues();
				}
			}
		}
		return new ArrayList<String>().toArray(new String[0]);
	}

	/**
	 * 
	 */
	public void update() {
		final CommonTableElement thisCommonTableElement = this;
		if (!initialized) {
			return;
		}
		if (!viewer.getControl().isDisposed()) {
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
		if (property.equals(CommonTableElement.GOTO_PROP) && (gotoPv != null)) {
			return gotoPv.getValue();
		} else if (property.equals("engine") && (engine != null)) {
			return engine.getValue();
		} else if (property.equals( "name")) {
			return name;
		} else if (property.equals(CommonTableElement.VALUE_PROP) && (valuePv != null)) {
			return valuePv.getValue();
		} else if (property.equals(CommonTableElement.STATUS_PROP)) {
			if (device instanceof DetectorChannel) {
				if (statusPv != null) {
					return statusPv.getValue();
				}
				return "";
			}
			
			if(movedonePv != null) {
				String moveStatus = movedonePv.getValue();
				if ((moveStatus != null) && (!moveStatus.isEmpty())) {
					try {
						int moveStatusInt = (int) Double.parseDouble(moveStatus);
						if (moveStatusInt == 0) {
							return PVStatus.MOVING.toString();
						}
					} catch (NumberFormatException e) {
						LOGGER.error(e.getMessage(), e);
					}
				}
			}
			if(statusPv != null) {
				String valueString = statusPv.getValue();
				try {
					int status = (int) Double.parseDouble(valueString);
					if((status & 4) > 0) {
						return PVStatus.LIMIT_POSITIVE.toString();
					} else if ((status & 8) > 0 || (status & 128) > 0) {
						return PVStatus.HOME.toString();
					} else if ((status & 512) > 0) {
						return PVStatus.PROBLEM.toString();
					} else if ((status & 8192) > 0) {
						return PVStatus.LIMIT_NEGATIVE.toString();
					} else if (limitViolationPv != null) {
						String limitString = limitViolationPv.getValue();
						int limitViolation = (int) Double
								.parseDouble(limitString);
						if (limitViolation == 1) {
							return PVStatus.SOFT_LIMIT.toString();
						} else {
							return PVStatus.IDLE.toString();
						}
					} else {
						return PVStatus.IDLE.toString();
					}
				} catch (Exception e) {
					return valueString;
				}
			}
		} else if (property.equals(CommonTableElement.UNIT_PROP)) {
			if (unitPv != null) {
				return unitPv.getValue();
			}
			return unit;
		}
		else if (property.equals(CommonTableElement.TWEAKVALUE_PROP) && (tweakvaluePv != null)) {
			return tweakvaluePv.getValue();
		}
		else if (property.equals(CommonTableElement.DEFINE_PROP) && (offsetPv != null)) {
			return "";
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
		if (getCellEditor(column) instanceof ComboBoxCellEditor) {
			int index = 0;
			String[] items = ((ComboBoxCellEditor)getCellEditor(column)).
					getItems();
			if (value instanceof Integer) {
				index = ((Integer)value).intValue();
			}
			if (items.length > index) {
				newValue = items[index];
			}
		} else if (value instanceof String) {
			newValue = (String) value;
		}

		if (column.equals(CommonTableElement.GOTO_PROP) && (gotoPv != null)) {
			gotoPv.setValue(newValue);
		} else if (column.equals(CommonTableElement.VALUE_PROP) && (valuePv != null)) {
			valuePv.setValue(newValue);
		} else if (column.equals(CommonTableElement.UNIT_PROP) && (unitPv != null)) {
			unitPv.setValue(newValue);
		} else if (column.equals(CommonTableElement.TWEAKVALUE_PROP) && (tweakvaluePv != null)) {
			tweakvaluePv.setValue(newValue);
		} else if (column.equals(CommonTableElement.DEFINE_PROP) && (offsetPv != null)) {
			// this is a define of the motor position
			// first, remember softHighLimit and softLowLimit
			String softHighLimit = softHighLimitPv.getValue();
			String softLowLimit = softLowLimitPv.getValue();
			// second, calculate new offset value
			// offset = newValue + offset - position
			Double newOffset = Double.parseDouble(newValue) + 
					Double.parseDouble(offsetPv.getValue()) - 
					Double.parseDouble(valuePv.getValue());
			MotorAxis motorAxis = (MotorAxis)device;
			Activator.getDefault().getMessageList().add(
					new ViewerMessage(MessageSource.VIEWER, 
							Levels.INFO, "Define " + 
							motorAxis.getName() + " from " +
							gotoPv.getValue() + " to " + newValue));
			offsetPv.setValue(newOffset.toString());
			// third, set old softHighLimit and softLowLimit
			softHighLimitPv.setValue(softHighLimit);
			softLowLimitPv.setValue(softLowLimit);
		}
	}

	/**
	 * 
	 */
	public void trigger() {
		if (triggerPv != null && triggerPv.isConnected()) {
			if (device.getClass().getSimpleName().equals("DetectorChannel")) {
				// device is a DetectorChannel
				DetectorChannel channel = (DetectorChannel)device;
				if (channel.getTrigger() != null &&
					channel.getTrigger().getValue() != null) {
						triggerPv.setValue(channel.getTrigger().getValue().
								getDefaultValue());
				} else {
					// triggerPv is the detector of the channel
					Detector detector = channel.getDetector();
					if (detector.getTrigger().getValue() != null) {
						triggerPv.setValue(detector.getTrigger().getValue().
								getDefaultValue());
					}
				}
			} 
		}
	}

	/**
	 * 
	 */
	public void stop() {
		if (stopPv != null && stopPv.isConnected()) {
			if (device instanceof MotorAxis) {
				MotorAxis axis = (MotorAxis) device;
				if (axis.getStop().getValue() != null) {
					stopPv.setValue(Integer.parseInt(axis.getStop().getValue()
							.getDefaultValue()));
				}
			} else if (device instanceof DetectorChannel) {
				DetectorChannel ch = (DetectorChannel) device;
				if (ch.getStop() != null &&
					ch.getStop().getValue() != null) {
					try {
						stopPv.setValue(Integer.parseInt(ch.getStop().getValue()
								.getDefaultValue()));
					} catch (NumberFormatException e) {
						stopPv.setValue(ch.getStop().getValue()
								.getDefaultValue());
					}
				} else if (ch.getDetector().getStop().getValue() != null) {
					try {
						stopPv.setValue(Integer.parseInt(ch.getDetector()
								.getStop().getValue().getDefaultValue()));
					} catch (NumberFormatException e) {
						stopPv.setValue(ch.getDetector().getStop().getValue()
								.getDefaultValue());
					}
				}
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
		} else if(!forward && (tweakreversePv != null) && 
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