package de.ptb.epics.eve.viewer.plot;

import java.util.Locale;

import org.eclipse.jface.viewers.TableViewer;

import com.cosylab.util.PrintfFormat;

import de.ptb.epics.eve.ecp1.client.interfaces.IMeasurementDataListener;
import de.ptb.epics.eve.ecp1.client.model.MeasurementData;
import de.ptb.epics.eve.ecp1.intern.DataModifier;
import de.ptb.epics.eve.ecp1.intern.DataType;
import de.ptb.epics.eve.viewer.Activator;

/**
 * <code>MathTableElement</code> represents an entry (row) of the tables 
 * contained in 
 * {@link de.ptb.epics.eve.viewer.views.plotview.PlotViewDetectorComposite}.
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
				if ((mathFunction == MathFunction.UNMODIFIED) || (mathFunction == MathFunction.NORMALIZED)){
					value = convert(measurementData, 0);
				}
				else {
					position = convert(measurementData, 0);
					value = convert(measurementData, 1);
				}
				doUpdate();
			}
			else if(motorId.equals(measurementData.getName()))
			{
				if ((mathFunction == MathFunction.UNMODIFIED) || (mathFunction == MathFunction.NORMALIZED)){
					position = convert(measurementData, 0);
					doUpdate();
				}
			}
		}	
	}
	
	/*
	 * called by measurementDataTransmitted to convert the data
	 */
	private String convert(MeasurementData mData, int element) {
				
		if (mData.getValues().size() <= element) return "error";

		if (mData.getDataType() == DataType.DOUBLE || 
			mData.getDataType() == DataType.FLOAT) 
		{
			Double data = (Double) mData.getValues().get(element);
			if (data.isNaN())
				return " ";
			else 
				return new PrintfFormat(Locale.ENGLISH, "%12.4g").sprintf(data).trim();
		}
		else
			return mData.getValues().get(element).toString();		
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
	 * &lt;not implemented yet&gt;
	 */
	public void gotoPos() {
		if (motorPv == null) return;
		// TODO IMPLEMENT
		return;
	}

	/**
	 * Checks whether an icon should be shown in the table.
	 * 
	 * @return <code>true</code> if an icon should be shown, 
	 * 		   <code>false</code> otherwise
	 */
	public boolean drawIcon() {
		// UNMODIFIED is the recently measured value (the motor is/was there)
		// and AVERAGE, DEVIATION and FWHM are no "real" positions but statistics
		// -> no GoTo icon is shown for them
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
	 * Returns the name of the element.
	 * 
	 * @return the name of the element
	 */
	public String getName() {
			return mathFunction.toString();
	}

	/**
	 * Returns the value of the element.
	 * 
	 * @return the value of the elemetn
	 */
	public String getValue() {
		return value;
	}

	/**
	 * Returns the position of the motor where the value was measured.
	 * 
	 * @return the position of the motor where the value was measured
	 */
	public String getPosition() {
		if ((mathFunction == MathFunction.UNMODIFIED) || drawIcon())
			return position;
		else
			return null;
	}
}