package de.ptb.epics.eve.viewer.views.plotview.table;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.Formatter;
import java.util.Locale;

import org.apache.log4j.Logger;

import de.ptb.epics.eve.data.measuringstation.Motor;
import de.ptb.epics.eve.data.measuringstation.MotorAxis;
import de.ptb.epics.eve.data.scandescription.PlotWindow;
import de.ptb.epics.eve.ecp1.client.interfaces.IEngineStatusListener;
import de.ptb.epics.eve.ecp1.client.interfaces.IMeasurementDataListener;
import de.ptb.epics.eve.ecp1.client.model.MeasurementData;
import de.ptb.epics.eve.ecp1.types.DataModifier;
import de.ptb.epics.eve.ecp1.types.DataType;
import de.ptb.epics.eve.ecp1.types.EngineStatus;
import de.ptb.epics.eve.viewer.Activator;
import de.ptb.epics.eve.viewer.jobs.plot.GotoJob;

/**
 * Aggregate Value of plot raw data.
 * 
 * @author Marcus Michalsky
 * @since 1.22
 */
public abstract class Data implements IMeasurementDataListener,
		IEngineStatusListener {
	private static final Logger LOGGER = Logger.getLogger(Data.class.getName());
	public static final String MOTOR_PROP = "motorPosition";
	public static final String DETECTOR_PROP = "detectorValue";
	
	public static final String NO_VALUE = "-";
	private String motorPosition;
	private Object motorRawValue;
	private String detectorValue;
	private PlotWindow plotWindow;
	private int yAxis;
	
	private PropertyChangeSupport propertyChangeSupport;
	
	/**
	 * Constructor.
	 * 
	 * @param plotWindow the plot window
	 * @param yAxis defines which y axis of the plot is used
	 * @throws IllegalArgumentException if plot window is <code>null</code>
	 */
	public Data(PlotWindow plotWindow, int yAxis) {
		if (plotWindow == null || yAxis < 0 || yAxis > 1) {
			throw new IllegalArgumentException("plot window must not be null");
		}
		this.plotWindow = plotWindow;
		this.yAxis = yAxis;
		this.motorPosition = Data.NO_VALUE;
		this.detectorValue = Data.NO_VALUE;
		this.propertyChangeSupport = new PropertyChangeSupport(this);
		Activator.getDefault().getEcp1Client().addMeasurementDataListener(this);
		Activator.getDefault().getEcp1Client().addEngineStatusListener(this);
	}
	
	/**
	 * Unregisters listeners. Should be called when associated view is disposed.
	 * @since 1.29.6
	 */
	public void deactivate() {
		Activator.getDefault().getEcp1Client().removeMeasurementDataListener(
				this);
		Activator.getDefault().getEcp1Client().removeEngineStatusListener(this);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void engineStatusChanged(EngineStatus engineStatus, String xmlName,
			int repeatCount) {
		if (EngineStatus.IDLE_NO_XML_LOADED.equals(engineStatus)) {
			Activator.getDefault().getEcp1Client()
					.removeMeasurementDataListener(this);
			Activator.getDefault().getEcp1Client()
					.removeEngineStatusListener(this);
		}
	}

	// TODO Match method to check if data is for current aggregate
		// should be called from subclass at the beginning of measurementData.
		// true / false. only continue if true... see old MathTableElement#measurementData
	
	protected boolean isAssociated(MeasurementData measurementData) {
		if (!Data.isData(measurementData)) {
			return false;
		}
		if (!measurementData.getDataModifier().equals(this.getDataModifier())
				&& !measurementData.getName().equals(
						this.getPlotWindow().getXAxis().getID())) {
			return false;
		}
		if (measurementData.getChainId() != this.plotWindow
						.getScanModule().getChain().getId()
				|| measurementData.getScanModuleId() != this.plotWindow
						.getScanModule().getId()) {
			return false;
		}
		return true;
	}
	
	/**
	 * @return the motorPosition
	 */
	public String getMotorPosition() {
		return motorPosition;
	}
	
	/**
	 * @param motorPosition the motorPosition to set
	 */
	public void setMotorPosition(String motorPosition) {
		String oldValue = this.motorPosition;
		this.motorPosition = motorPosition;
		this.propertyChangeSupport.firePropertyChange(Data.MOTOR_PROP,
				oldValue, motorPosition);
		LOGGER.debug("new motor value " + this.getDataModifier());
	}

	/**
	 * Sets the raw value (as received) of the motor axis
	 * @param value the raw value (as received)
	 */
	protected void setMotorRawValue(Object value) {
		this.motorRawValue = value;
	}
	
	/**
	 * @return the value
	 */
	public String getDetectorValue() {
		return detectorValue;
	}
	
	/**
	 * @param detectorValue the detectorValue to set
	 */
	public void setDetectorValue(String detectorValue) {
		String oldValue = this.detectorValue;
		this.detectorValue = detectorValue;
		this.propertyChangeSupport.firePropertyChange(Data.DETECTOR_PROP,
				oldValue, detectorValue);
	}

	/**
	 * @return the plotWindow
	 */
	public PlotWindow getPlotWindow() {
		return plotWindow;
	}
	
	/**
	 * Returns whether a goto is possible
	 * 
	 * @return <code>true</code> if a goto is possible, <code>false</code>
	 *         otherwise
	 */
	public boolean isGoToEnabled() {
		return false;
	}
	
	/**
	 * 
	 */
	public void gotoPos() {
		if (!isGoToEnabled()) {
			return;
		}
		MotorAxis ma = this.plotWindow.getXAxis();
		String triggerPv = null;
		if (ma.getTrigger() != null) {
			triggerPv = ma.getTrigger().getAccess().getVariableID();
		} else if (((Motor)ma.getParent()).getTrigger() != null) {
			triggerPv = ((Motor)ma.getParent()).getTrigger().getAccess().
					getVariableID();
		}
		GotoJob gotoJob = new GotoJob("Goto "
				+ this.getDataModifier().toString(), ma.getGoto().getAccess()
				.getVariableID(), triggerPv, this.motorRawValue);
		gotoJob.setUser(true);
		gotoJob.schedule();
		LOGGER.debug("goto job scheduled");
	}
	
	/**
	 * @return the yAxis
	 */
	public int getyAxis() {
		return yAxis;
	}

	public abstract DataModifier getDataModifier();
	
	/**
	 * 
	 * @return
	 */
	public String getDetectorId() {
		if (this.plotWindow.getYAxes().get(this.getyAxis()).isNormalized()) {
			return this.getPlotWindow().getYAxes().get(this.getyAxis())
					.getDetectorChannel().getID()
					+ "__"
					+ this.getPlotWindow().getYAxes().get(this.getyAxis())
							.getNormalizeChannel().getID();
		}
		return this.getPlotWindow().getYAxes().get(this.getyAxis())
				.getDetectorChannel().getID(); 
	}
	
	/**
	 * 
	 * @param mData
	 * @return
	 */
	public static boolean isData(MeasurementData mData) {
		return mData != null;
	}
	
	@SuppressWarnings("resource")
	public static String convert(MeasurementData mData, int element) {
		if (mData.getValues().size() <= element) {
			return "error";
		}

		if (mData.getDataType() == DataType.DOUBLE
				|| mData.getDataType() == DataType.FLOAT) {
			Double data = (Double) mData.getValues().get(element);
			if (data.isNaN()) {
				return " ";
			}
			Formatter formatter = new Formatter(new Locale(
					Locale.ENGLISH.getCountry()));
			return formatter.format("%14.7g", data).out().toString().trim();
		}
		return mData.getValues().get(element).toString();
	}
	
	/**
	 * Adds a listener for the given property.
	 * 
	 * @param property the property to listen to
	 * @param listener the listener
	 */
	public void addPropertyChangeListener(String property,
			PropertyChangeListener listener) {
		this.propertyChangeSupport
				.addPropertyChangeListener(property, listener);
	}
	
	/**
	 * Removes a listener for the given property.
	 * 
	 * @param property the property to stop listen to
	 * @param listener the listener
	 */
	public void removePropertyChangeListener(String property,
			PropertyChangeListener listener) {
		this.propertyChangeSupport.removePropertyChangeListener(property,
				listener);
	}
}