/* 
 * Copyright (c) 2001, 2008 Physikalisch-Technische Bundesanstalt.
 * All rights reserved.
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 */
package de.ptb.epics.eve.viewer.math;

import java.util.Locale;

import org.eclipse.jface.viewers.TableViewer;

import com.cosylab.util.PrintfFormat;

import de.ptb.epics.eve.ecp1.client.interfaces.IMeasurementDataListener;
import de.ptb.epics.eve.ecp1.client.model.MeasurementData;
import de.ptb.epics.eve.ecp1.intern.DataType;
import de.ptb.epics.eve.viewer.Activator;

/**
 * 
 * @author ?
 * @author Marcus Michalsky
 */
public class MathTableElement implements IMeasurementDataListener {
	
	private String value;
	private MathFunction mathFunction;
	private String motorPv;
	private String detectorId;
	private int chid;
	private int smid;
	private TableViewer viewer;
	private String position;
	private String motorId;

	/**
	 * Constructs a <code>MathTableElement</code>.
	 * 
	 * @param chid the id of the channel
	 * @param smid the id of the scan module
	 * @param viewer the table viewer that should be used
	 * @param mathFunction the desired mathematical function
	 * @param motorPv the process variable of the motor
	 * @param motorId the id of the motor
	 * @param detectorId the id of the detector
	 */
	public MathTableElement(int chid, int smid, TableViewer viewer, 
							 MathFunction mathFunction, String motorPv, 
							 String motorId, String detectorId) {
		// set an initial value
		value = " - ";
		position = " - ";
		this.viewer = viewer;
		this.chid = chid;
		this.smid = smid;
		this.mathFunction = mathFunction;
		this.motorPv = motorPv;
		this.motorId = motorId;
		this.detectorId = detectorId;
		
		// add the constructed math table element to the listener of the engine, 
		// so that it is notified, when new data has arrived
		Activator.getDefault().getEcp1Client().addMeasurementDataListener(this);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void measurementDataTransmitted(MeasurementData measurementData) {
		
		if ((measurementData == null) || (detectorId == null)) return;
		
		// are we still in the same chain and scan module ?
		if (measurementData.getChainId() == chid && 
			measurementData.getScanModuleId() == smid)
		{
			if(detectorId.equals(measurementData.getName()) && 
			 measurementData.getDataModifier() == mathFunction.toDataModifier())
			{
				value = convert(measurementData);
				doUpdate();
			}
			else if(motorId.equals(measurementData.getName()) && 
			 measurementData.getDataModifier() == mathFunction.toDataModifier())
			{
				position = convert(measurementData);
				doUpdate();
			}
		}	
	}
	
	/*
	 * called by measurementDataTransmitted to convert the data
	 */
	private String convert(MeasurementData mData) {
				
		if (mData.getDataType() == DataType.DOUBLE || 
			mData.getDataType() == DataType.FLOAT) 
		{
			for(int i=0;i<mData.getValues().size();i++)
			{
				System.out.print(mData.getName() + " : " + mData.getPositionCounter() + " : " + 
						        this.mathFunction.toString() + " : ");
				System.out.print(mData.getValues().get(i).toString());
				System.out.print(", ");
			}
			System.out.println("");
			System.out.println("");
			
			Double data = (Double) mData.getValues().get(0);
			return 
				new PrintfFormat(Locale.ENGLISH, "%12.4g").sprintf(data).trim();
		}
		else
			return mData.getValues().get(0).toString();		
	}
	
	/*
	 * called by measurementDataTransmitted to update the table contents
	 */
	private void doUpdate() {
		if (!viewer.getControl().isDisposed())
		{
			final MathTableElement thisMathTableElement = this;
			viewer.getControl().getDisplay().asyncExec(new Runnable() {
				
				@Override
				public void run() {
					if (!viewer.getControl().isDisposed()) {
						viewer.update(thisMathTableElement, null);
					}
				}
			});
		}
	}
	
	/**
	 * 
	 */
	public void gotoPos() {
		if (motorPv == null) return;
		return;
	}

	/**
	 * 
	 * @return
	 */
	public boolean drawIcon() {
		switch (mathFunction) {
		case UNMODIFIED:
		case AVERAGE:
		case DEVIATION:
		case FWHM:
			return false;
		default:
			return true;
		}
	}
	
	/**
	 * 
	 * @return
	 */
	public String getName() {
			return mathFunction.toString();
	}

	/**
	 * 
	 * @return
	 */
	public String getValue() {
		return value;
	}

	/**
	 * 
	 * @return
	 */
	public String getPosition() {
		if ((mathFunction == MathFunction.UNMODIFIED) || drawIcon())
			return position;
		else
			return null;
	}
}