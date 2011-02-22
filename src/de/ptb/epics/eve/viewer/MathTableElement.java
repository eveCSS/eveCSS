/* 
 * Copyright (c) 2001, 2008 Physikalisch-Technische Bundesanstalt.
 * All rights reserved.
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 */
package de.ptb.epics.eve.viewer;

import java.util.Locale;

import org.eclipse.jface.viewers.TableViewer;

import com.cosylab.util.PrintfFormat;

import de.ptb.epics.eve.ecp1.client.interfaces.IMeasurementDataListener;
import de.ptb.epics.eve.ecp1.client.model.MeasurementData;
import de.ptb.epics.eve.ecp1.intern.DataType;

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
	 * 
	 * @param chid
	 * @param smid
	 * @param viewer
	 * @param mathFunction
	 * @param motorPv
	 * @param motorId
	 * @param detectorId
	 */
	public MathTableElement(int chid, int smid, TableViewer viewer, MathFunction mathFunction, String motorPv, String motorId, String detectorId) {
		value = " - ";
		position = " - ";
		this.viewer = viewer;
		this.chid = chid;
		this.smid = smid;
		this.mathFunction = mathFunction;
		this.motorPv = motorPv;
		this.motorId = motorId;
		this.detectorId = detectorId;
		Activator.getDefault().getEcp1Client().addMeasurementDataListener( this );
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void measurementDataTransmitted(MeasurementData measurementData) {
		if ((measurementData == null) || (detectorId == null)) return;
		
		if ((measurementData.getChainId() == chid) && (measurementData.getScanModuleId() == smid)){
			if (detectorId.equals(measurementData.getName()) && (measurementData.getDataModifier() == mathFunction.toDataModifier())){
				value = convert(measurementData);
				doUpdate();
			}
			else if (motorId.equals(measurementData.getName()) && (measurementData.getDataModifier() == mathFunction.toDataModifier())){
				position = convert(measurementData);
				doUpdate();
			}
		}	
	}
	
	/*
	 * 
	 */
	private String convert(MeasurementData mData){
		if ((mData.getDataType() == DataType.DOUBLE) || (mData.getDataType() == DataType.FLOAT)) {
			Double data = (Double) mData.getValues().get(0);
			return new PrintfFormat(Locale.ENGLISH, "%12.4g").sprintf(data).trim();
		}
		else
			return mData.getValues().get( 0 ).toString();		
	}
	/*
	 * 
	 */
	private void doUpdate() {
		if (!viewer.getControl().isDisposed()){
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
		// TODO
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
		if (mathFunction == MathFunction.UNMODIFIED)
			return "";
		else
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