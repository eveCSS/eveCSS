package de.ptb.epics.eve.viewer.views.plotview.plot;

import gov.aps.jca.dbr.TimeStamp;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;
import org.csstudio.swt.xygraph.dataprovider.IDataProvider;
import org.csstudio.swt.xygraph.dataprovider.IDataProviderListener;
import org.csstudio.swt.xygraph.dataprovider.ISample;
import org.csstudio.swt.xygraph.linearscale.Range;

import de.ptb.epics.eve.data.scandescription.YAxisModifier;
import de.ptb.epics.eve.ecp1.client.interfaces.IMeasurementDataListener;
import de.ptb.epics.eve.ecp1.client.model.MeasurementData;
import de.ptb.epics.eve.ecp1.types.DataModifier;
import de.ptb.epics.eve.viewer.Activator;

/**
 * @author Marcus Michalsky
 * @since 1.13
 */
public class TraceDataCollector implements IDataProvider,
		IMeasurementDataListener {

	private static final Logger LOGGER = Logger
			.getLogger(TraceDataCollector.class.getName());
	
	private List<IDataProviderListener> listeners;
	private TraceInfo traceInfo;
	private int size;
	private List<Sample> data;
	
	private int motorPosCount;
	private int detectorPosCount;
	private double motorValue;
	private double detectorValue;
	
	private double xMin;
	private double xMax;
	private double yMin;
	private double yMax;
	
	private Calendar calendar;
	private UpdateStrategy updateStrategy;
	private int sampleSizeOfLastUpdate;
	private long timeOfLastSample;
	
	/**
	 * 
	 * @param size
	 */
	public TraceDataCollector(int size, TraceInfo traceInfo) {
		this.size = size;
		this.traceInfo = traceInfo;
		this.motorPosCount = 0;
		this.detectorPosCount = 0;
		this.motorValue = 0;
		this.detectorValue = 0;
		
		this.xMin = this.yMin = Double.POSITIVE_INFINITY;
		this.xMax = this.yMax = Double.NEGATIVE_INFINITY;
		
		this.calendar = Calendar.getInstance();
		this.updateStrategy = new TimeAndSampleSizeUpdateStrategy(15000);
		this.sampleSizeOfLastUpdate = 0;
		this.timeOfLastSample = this.calendar.getTimeInMillis();
		
		this.data = new ArrayList<Sample>(this.size);
		this.listeners = new LinkedList<IDataProviderListener>();
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public synchronized void measurementDataTransmitted(MeasurementData measurementData) {
		if (measurementData.getName().equals(traceInfo.getMotorId())) {
			this.motorPosCount = measurementData.getPositionCounter();
			switch (measurementData.getDataType()) {
			case INT8:
				this.motorValue = ((Byte) measurementData.getValues().get(0)).
					doubleValue();
				break;
			case INT16:
				this.motorValue = ((Short) measurementData.getValues().get(0)).
					doubleValue();
				break;
			case INT32:
				this.motorValue = ((Integer) measurementData.getValues().get(0))
					.doubleValue();
				break;
			case FLOAT:
				this.motorValue = ((Float) measurementData.getValues().get(0))
				.doubleValue();
				break;
			case DOUBLE:
				this.motorValue = ((Double) measurementData.getValues().get(0));
				break;
			case DATETIME:
				TimeStamp time = new TimeStamp(
						measurementData.getGeneralTimeStamp(),
						measurementData.getNanoseconds());
				this.motorValue = time.secPastEpoch() * 1000 + time.nsec()
						/ 1000000;
				break;
			case STRING:
				// not implemented yet
				break;
			}
			this.checkForData();
		}
		if (!measurementData.getDataModifier().equals(traceInfo.getModifier())) {
			return;
		}
		if ((this.traceInfo.getModifier().equals(DataModifier.UNMODIFIED) && 
				measurementData.getName().equals(traceInfo.getDetectorId()))
			|| (this.traceInfo.getModifier().equals(DataModifier.NORMALIZED) && 
				measurementData.getName().equals(this.traceInfo.getNormalizeId()))) {
			this.detectorPosCount = measurementData.getPositionCounter();
			switch (measurementData.getDataType()) {
			case INT8: 
				this.detectorValue = ((Byte) measurementData.getValues().
						get(0)).doubleValue();
				break;
			case INT16:
				this.detectorValue = ((Short) measurementData.getValues().
						get(0)).doubleValue();
				break;
			case INT32:
				this.detectorValue = ((Integer) measurementData.getValues()
						.get(0)).doubleValue();
				break;
			case FLOAT:
				this.detectorValue = ((Float) measurementData.getValues()
						.get(0)).doubleValue();
				break;
			case DOUBLE:
				this.detectorValue = ((Double) measurementData.getValues()
						.get(0));
				break;
			case DATETIME:
				TimeStamp time = new TimeStamp(
						measurementData.getGeneralTimeStamp(),
						measurementData.getNanoseconds());
				this.detectorValue = time.secPastEpoch() * 1000 + time.nsec()
						/ 1000000;
				break;
			case STRING:
				// not implemented yet
				break;
			}
			this.checkForData();
		}
	}

	/*
	 * 
	 */
	private void checkForData() {
		if (this.motorPosCount == this.detectorPosCount) {
			Sample sample = new Sample(this.motorValue, this.detectorValue);
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Plot " + this.traceInfo.getPlotId() + " ("
						+ this.traceInfo.getPlotName() + ") - " 
						+ (!this.traceInfo.getNormalizeId().isEmpty() 
							? this.traceInfo.getNormalizeId() 
							: this.traceInfo.getDetectorId())
						+ ": Pos: "
						+ this.motorPosCount + sample.getInfo());
			}
			if (this.traceInfo.isyAxisNumeric() && this.traceInfo.getyAxisModifier().
					equals(YAxisModifier.INVERSE)) {
				LOGGER.debug("Inverse modifier is set -> inverting sample");
				sample.invertYValue();
			}
			LOGGER.debug("adding sample " + sample.toString());
			this.data.add(sample);
			
			if (this.motorValue < this.xMin) {
				this.xMin = this.motorValue;
			} 
			if (this.motorValue > this.xMax) {
				this.xMax = this.motorValue;
			}
			if (this.detectorValue < this.yMin) {
				this.yMin = this.detectorValue;
			}
			if (this.detectorValue > this.yMax) {
				this.yMax = this.detectorValue;
			}
		
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("New Sample: (" + sample.getXValue() + ", " 
						+ sample.getYValue() + ")");
				if (this.traceInfo.getModifier().equals(YAxisModifier.INVERSE)) {
					LOGGER.debug("y value of sample is inverted");
				}
				LOGGER.debug("Current x-Range: (" + this.xMin + ", "
						+ this.xMax + ")");
				LOGGER.debug("Current y-Range: (" + this.yMin + ", "
						+ this.yMax + ")");
			}
			
			if (this.updateStrategy.update(timeOfLastSample,
					sampleSizeOfLastUpdate, this.data.size())) {
				this.timeOfLastSample = Calendar.getInstance().getTimeInMillis();
				this.sampleSizeOfLastUpdate = this.data.size();
				LOGGER.debug("publish data");
				this.publish();
			}
		}
	}
	
	/**
	 * 
	 */
	public void publish() {
		final IDataProvider idpl = this;
		for (final IDataProviderListener listener : this.listeners) {
			Activator.getDefault().getWorkbench().getDisplay()
			.asyncExec(new Runnable() {
				@Override
				public void run() {
					listener.dataChanged(idpl);
				}});
		}
	}
	
	/**
	 * 
	 * @return
	 * @since 1.28
	 */
	public synchronized TraceInfo getTraceInfo() {
		return this.traceInfo;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public synchronized ISample getSample(int i) {
		LOGGER.trace("get sample called (sample #" + (i+1) + ")");
		return this.data.get(i);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int getSize() {
		LOGGER.trace("get size called (size is " + this.data.size() + ")");
		return this.data.size();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public synchronized Range getXDataMinMax() {
		LOGGER.trace("get x min max called ([" + this.xMin + ", " + this.xMax + "]");
		return new Range(this.xMin, this.xMax);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public synchronized Range getYDataMinMax() {
		LOGGER.trace("get y min max called ([" + this.yMin + ", " + this.yMax + "]");
		return new Range(this.yMin, this.yMax);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isChronological() {
		LOGGER.trace("is chronological called (always true)");
		return true;
	}

	/**
	 * 
	 * @return
	 * @since 1.28
	 */
	public synchronized YAxisModifier getYAxisModifier() {
		return this.traceInfo.getyAxisModifier();
	}
	
	/**
	 * 
	 * @param modifier the modifier to set
	 * @since 1.28
	 */
	public synchronized void setYAxisModifier(YAxisModifier modifier) {
		LOGGER.info("set modifier to " + modifier);
		this.invertSamples();
		this.traceInfo.setyAxisModifier(modifier);
		this.publish();
	}
	
	private void invertSamples() {
		this.xMin = this.yMin = Double.POSITIVE_INFINITY;
		this.xMax = this.yMax = Double.NEGATIVE_INFINITY;
		for (Sample sample : this.data) {
			sample.invertYValue();
			if (sample.getYValue() < this.yMin) {
				this.yMin = sample.getYValue();
			}
			if (sample.getYValue() > this.yMax) {
				this.yMax = sample.getYValue();
			}
		}
	}
	
	/**
	 * 
	 * @param strategy
	 */
	public void setUpdateStratety(UpdateStrategy strategy) {
		this.updateStrategy = strategy;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void addDataProviderListener(IDataProviderListener listener) {
		this.listeners.add(listener);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean removeDataProviderListener(IDataProviderListener listener) {
		return this.listeners.remove(listener);
	}
}