package de.ptb.epics.eve.ecp1.client.model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import de.ptb.epics.eve.ecp1.commands.MeasurementDataCommand;
import de.ptb.epics.eve.ecp1.types.AcquisitionStatus;
import de.ptb.epics.eve.ecp1.types.DataModifier;
import de.ptb.epics.eve.ecp1.types.DataType;
import de.ptb.epics.eve.ecp1.types.EpicsSeverity;
import de.ptb.epics.eve.ecp1.types.EpicsStatus;

/**
 * @author ?
 * @since 1.0
 */
public final class MeasurementData {
	private String name;
	private int chid;
	private int smid;
	private AcquisitionStatus acquisitionStatus;
	private DataType dataType;
	private DataModifier dataModifier;
	private EpicsSeverity epicsSeverity;
	private EpicsStatus epicsStatus;
	private int generalTimeStamp;
	private int nanoseconds;
	private int positionCounter;
	private List<?> values;

	/**
	 * Constructor.
	 * 
	 * @param measurementDataCommand the measurement data command
	 */
	public MeasurementData(final MeasurementDataCommand measurementDataCommand) {
		this.chid = measurementDataCommand.getChainId();
		this.smid = measurementDataCommand.getScanModuleId();
		this.positionCounter = measurementDataCommand.getPositionCounter();
		this.dataType = measurementDataCommand.getDataType();
		this.dataModifier = measurementDataCommand.getDataModifier();
		this.epicsSeverity = measurementDataCommand.getEpicsSeverity();
		this.epicsStatus = measurementDataCommand.getEpicsStatus();
		this.acquisitionStatus = measurementDataCommand.getAcquisitionStatus();
		this.generalTimeStamp = measurementDataCommand.getGeneralTimeStamp();
		this.nanoseconds = measurementDataCommand.getNanoseconds();
		this.name = measurementDataCommand.getName();

		switch (this.dataType) {
		case INT8: {
			this.values = new ArrayList<Byte>();
			final Iterator<Byte> it = (Iterator<Byte>) measurementDataCommand
					.iterator();
			while (it.hasNext()) {
				((List<Byte>) this.values).add(it.next());
			}
		}
			break;

		case INT16: {
			this.values = new ArrayList<Short>();
			final Iterator<Short> it = (Iterator<Short>) measurementDataCommand
					.iterator();
			while (it.hasNext()) {
				((List<Short>) this.values).add(it.next());
			}
		}
			break;

		case INT32: {
			this.values = new ArrayList<Integer>();
			final Iterator<Integer> it = (Iterator<Integer>) measurementDataCommand
					.iterator();
			while (it.hasNext()) {
				((List<Integer>) this.values).add(it.next());
			}
		}
			break;

		case FLOAT: {
			this.values = new ArrayList<Float>();
			final Iterator<Float> it = (Iterator<Float>) measurementDataCommand
					.iterator();
			while (it.hasNext()) {
				((List<Float>) this.values).add(it.next());
			}
		}
			break;

		case DOUBLE: {
			this.values = new ArrayList<Double>();
			final Iterator<Double> it = (Iterator<Double>) measurementDataCommand
					.iterator();
			while (it.hasNext()) {
				((List<Double>) this.values).add(it.next());
			}
		}
			break;

		case STRING: {
			this.values = new ArrayList<String>();
			final Iterator<String> it = (Iterator<String>) measurementDataCommand
					.iterator();
			while (it.hasNext()) {
				((List<String>) this.values).add(it.next());
			}
		}
			break;

		case DATETIME: {
			this.values = new ArrayList<String>();
			final Iterator<String> it = (Iterator<String>) measurementDataCommand
					.iterator();
			while (it.hasNext()) {
				((List<String>) this.values).add(it.next());
			}
		}
			break;
		}

	}

	public String getName() {
		return this.name;
	}

	public int getChainId() {
		return this.chid;
	}

	public int getScanModuleId() {
		return this.smid;
	}

	public AcquisitionStatus getAcquisitionStatus() {
		return this.acquisitionStatus;
	}

	public DataType getDataType() {
		return this.dataType;
	}

	public DataModifier getDataModifier() {
		return this.dataModifier;
	}

	public EpicsSeverity getEpicsSeverity() {
		return this.epicsSeverity;
	}

	public EpicsStatus getEpicsStatus() {
		return this.epicsStatus;
	}

	public int getGeneralTimeStamp() {
		return this.generalTimeStamp;
	}

	public int getNanoseconds() {
		return this.nanoseconds;
	}

	public int getPositionCounter() {
		return this.positionCounter;
	}

	public List<?> getValues() {
		return this.values;
	}
}