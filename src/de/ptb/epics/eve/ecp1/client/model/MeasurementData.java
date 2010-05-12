package de.ptb.epics.eve.ecp1.client.model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import de.ptb.epics.eve.ecp1.intern.AcquisitionStatus;
import de.ptb.epics.eve.ecp1.intern.DataModifier;
import de.ptb.epics.eve.ecp1.intern.DataType;
import de.ptb.epics.eve.ecp1.intern.EpicsSeverity;
import de.ptb.epics.eve.ecp1.intern.EpicsStatus;
import de.ptb.epics.eve.ecp1.intern.MeasurementDataCommand;

public final class MeasurementData {

	private DataType dataType;
	private DataModifier dataModifier;
	private EpicsSeverity epicsSeverity;
	private EpicsStatus epicsStatus;
	private AcquisitionStatus acquisitionStatus;
	private int gerenalTimeStamp;
	private int nanoseconds;
	private String name;
	private List< ? > values;
	
	public MeasurementData( final MeasurementDataCommand measurementDataCommand ) {
		this.dataType = measurementDataCommand.getDataType();
		this.dataModifier = measurementDataCommand.getDataModifier();
		this.epicsSeverity = measurementDataCommand.getEpicsSeverity();
		this.epicsStatus = measurementDataCommand.getEpicsStatus();
		this.acquisitionStatus = measurementDataCommand.getAcquisitionStatus();
		this.gerenalTimeStamp = measurementDataCommand.getGerenalTimeStamp();
		this.nanoseconds = measurementDataCommand.getNanoseconds();
		this.name = measurementDataCommand.getName();
		
		switch( this.dataType ) {
			case INT8:
				{
					this.values = new ArrayList< Byte >();
					final Iterator< Byte > it = (Iterator<Byte>) measurementDataCommand.iterator();
					while( it.hasNext() ) {
						((List< Byte >)this.values).add( it.next() );
					}
				}
				break;
				
			case INT16:
				{
					this.values = new ArrayList< Character >();
					final Iterator< Character > it = (Iterator<Character>) measurementDataCommand.iterator();
					while( it.hasNext() ) {
						((List< Character >)this.values).add( it.next() );
					}
				}
				break;
				
			case INT32:
				{
					this.values = new ArrayList< Integer >();
					final Iterator< Integer > it = (Iterator<Integer>) measurementDataCommand.iterator();
					while( it.hasNext() ) {
						((List< Integer >)this.values).add( it.next() );
					}
				}
				break;
				
			case FLOAT:
				{
					this.values = new ArrayList< Float >();
					final Iterator< Float > it = (Iterator<Float>) measurementDataCommand.iterator();
					while( it.hasNext() ) {
						((List< Float >)this.values).add( it.next() );
					}
				}
				break;
				
			case DOUBLE:
				{
					this.values = new ArrayList< Double >();
					final Iterator< Double > it = (Iterator<Double>) measurementDataCommand.iterator();
					while( it.hasNext() ) {
						((List< Double >)this.values).add( it.next() );
					}
				}
				break;
				
			case STRING:
				{
					this.values = new ArrayList< String >();
					final Iterator< String > it = (Iterator<String>) measurementDataCommand.iterator();
					while( it.hasNext() ) {
						((List< String >)this.values).add( it.next() );
					}
				}
				break;
		}
		
	}



	public final AcquisitionStatus getAcquisitionStatus() {
		return this.acquisitionStatus;
	}

	public final DataModifier getDataModifier() {
		return this.dataModifier;
	}

	public final DataType getDataType() {
		return this.dataType;
	}

	public final EpicsSeverity getEpicsSeverity() {
		return this.epicsSeverity;
	}

	public final EpicsStatus getEpicsStatus() {
		return this.epicsStatus;
	}

	public final int getGerenalTimeStamp() {
		return this.gerenalTimeStamp;
	}

	public final String getName() {
		return this.name;
	}

	public final int getNanoseconds() {
		return this.nanoseconds;
	}
	
	public final List< ? > getValues() {
		return this.values;
	}

}