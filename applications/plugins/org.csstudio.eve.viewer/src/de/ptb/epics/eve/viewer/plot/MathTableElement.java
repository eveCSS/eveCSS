package de.ptb.epics.eve.viewer.plot;

import java.util.Formatter;
import java.util.Locale;

import org.apache.log4j.Logger;
import org.eclipse.jface.viewers.TableViewer;

import de.ptb.epics.eve.data.measuringstation.Motor;
import de.ptb.epics.eve.data.measuringstation.MotorAxis;
import de.ptb.epics.eve.ecp1.client.interfaces.IMeasurementDataListener;
import de.ptb.epics.eve.ecp1.client.model.MeasurementData;
import de.ptb.epics.eve.ecp1.types.DataType;
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
	
	private static Logger logger = Logger.getLogger(
			MathTableElement.class.getName());
	
	private String value;
	private MathFunction mathFunction;
	private String motorPv;
	private String detectorId;
	private int chid;
	private int smid;
	private TableViewer viewer;
	private String position;
	private Object rawPosition;
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
			measurementData.getScanModuleId() == smid) {
			if(detectorId.equals(measurementData.getName()) && 
			 measurementData.getDataModifier() == mathFunction.toDataModifier()) {
				if ((mathFunction == MathFunction.UNMODIFIED) || 
					(mathFunction == MathFunction.NORMALIZED)) {
						value = convert(measurementData, 0);
				} else {
					position = convert(measurementData, 0);
					value = convert(measurementData, 1);
				}
				doUpdate();
			}
			else if(motorId.equals(measurementData.getName())) {
				if ((mathFunction == MathFunction.UNMODIFIED) || 
					(mathFunction == MathFunction.NORMALIZED)){
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
			mData.getDataType() == DataType.FLOAT) {
				Double data = (Double) mData.getValues().get(element);
				if (element == 0) {
					rawPosition = data;
				}
				if (data.isNaN()) {
					return " ";
				} else {
					Formatter formatter = new Formatter(
							new Locale(Locale.ENGLISH.getCountry()));
					return formatter.format("%12.4g", data).out().
							toString().trim();
				}
		} else {
			if (element == 0) {
				rawPosition = mData.getValues().get(element).toString();
			}
			return mData.getValues().get(element).toString();
		}
	}
	
	/*
	 * called by measurementDataTransmitted to update the table contents
	 */
	private void doUpdate() {
		if (!viewer.getControl().isDisposed()) {
			final MathTableElement thisMathTableElement = this;
			viewer.getControl().getDisplay().asyncExec(new Runnable() {
				@Override public void run() {
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
		MotorAxis ma = Activator.getDefault().getMeasuringStation().
				getMotorAxisById(motorId);
		String triggerPv = null;
		if (ma.getTrigger() != null) {
			triggerPv = ma.getTrigger().getAccess().getVariableID();
		} else if (((Motor)ma.getParent()).getTrigger() != null) {
			triggerPv = ((Motor)ma.getParent()).getTrigger().getAccess().
					getVariableID();
		}
		GotoJob gotoJob = new GotoJob("Goto " + this.mathFunction, motorPv, 
				triggerPv, rawPosition);
		gotoJob.setUser(true);
		gotoJob.schedule();
		logger.debug("goto job scheduled");
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
		case NORMALIZED:
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
	 * @return the value of the element
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
		if ((mathFunction == MathFunction.UNMODIFIED) || drawIcon()) {
			return position;
		} else {
			return null;
		}
	}
}