package de.ptb.epics.eve.viewer.views;

import gov.aps.jca.dbr.TimeStamp;

// import org.apache.log4j.Logger;
import org.eclipse.draw2d.LightweightSystem;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;

import de.ptb.epics.eve.data.scandescription.PlotWindow;
import de.ptb.epics.eve.ecp1.client.interfaces.IMeasurementDataListener;
import de.ptb.epics.eve.ecp1.client.model.MeasurementData;
import de.ptb.epics.eve.ecp1.intern.DataModifier;
import de.ptb.epics.eve.ecp1.intern.DataType;
import de.ptb.epics.eve.viewer.Activator;
import de.ptb.epics.eve.viewer.plot.XYPlot;

/**
 * <code>plotGraphComposite</code> contains the xy-plot and is located in the 
 * Plot View.
 * 
 * @author Jens Eden
 * @author Marcus Michalsky
 */
public class PlotViewGraphComposite extends Composite 
							implements IMeasurementDataListener {
	
	// TODO temporary logging for plot
	//private static Logger logger = Logger.getLogger(PlotViewGraphComposite.class);
	
	private String detector1Id;
	private String detector2Id;
	private String motorId;
	private int detector1PosCount;
	private int detector2PosCount;
	private int posCount;
	TimeStamp timestamp;
	private Double xValue;
	private int chid;
	private int smid;
	private Double y2value;
	private Double y1value;
	private Canvas canvas;
	private XYPlot xyPlot;
	private String detector1Name;
	private String detector2Name;
	
	/**
	 * Constructs a <code>plotGraphComposite</code>.
	 * 
	 * @param parent the parent
	 * @param style the style
	 */
	public PlotViewGraphComposite(Composite parent, int style) {
		super(parent, style);

		// this composite wants to be informed if new data is available...
		Activator.getDefault().getEcp1Client().addMeasurementDataListener(this);
		
		final GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 2;
		setLayout(gridLayout);
		
		GridData gridData = new GridData();
		gridData.verticalSpan = 2;
		gridData.horizontalSpan = 2;
		gridData.grabExcessHorizontalSpace = true;
		gridData.grabExcessVerticalSpace = true;
		gridData.horizontalAlignment = SWT.FILL;
		gridData.verticalAlignment = SWT.FILL;
		gridData.minimumHeight = 400;
		gridData.minimumWidth = 600;
		canvas = new Canvas(this, SWT.NONE);
        canvas.setLayoutData(gridData);
	    //use LightweightSystem to create the bridge between SWT and draw2D
		final LightweightSystem lws = new LightweightSystem(canvas);
		//set it as the content of LightwightSystem
		xyPlot = new XYPlot();
		lws.setContents(xyPlot);
	}

	/**
	 * Refreshes the plot.
	 * 
	 * @param plotWindow the plot window
	 * @param chid the id of the chain
	 * @param smid the id of the scan module
	 * @param motorId the id of the motor
	 * @param motorName the name of the motor
	 * @param detector1Id the id of the first detector
	 * @param detector1Name the name of the first detector
	 * @param detector2Id the id of the second detector
	 * @param detector2Name the name of the second detector
	 */
	public void refresh(PlotWindow plotWindow, int chid, int smid, 
						 String motorId, String motorName, String detector1Id, 
						 String detector1Name, String detector2Id, 
						 String detector2Name) {
		
		// do not clean if plot has "isInit=false" AND detectors and motors are 
		// still the same
		if ((this.motorId == motorId) && (this.detector1Id == detector1Id) && 
				(this.detector2Id == detector2Id)) {
			if (plotWindow.isInit()) xyPlot.init(true);
		}
		else {
			xyPlot.init(true);
		}

		// set new values for chain, scan module, motor and detectors
		this.chid = chid;
		this.smid = smid;
		this.detector1Id = detector1Id;
		this.detector2Id = detector2Id;
		this.motorId = motorId;
		timestamp = null;
	
		// update first y axis 
		if (this.detector1Id != null)
		{
			// set the current (1st) detector name
			this.detector1Name = detector1Name;
			// does the plot already have a trace of this detector ?
			// if not -> add as new trace
			if (xyPlot.getTrace(detector1Name) == null)
				xyPlot.addTrace(detector1Name, detector1Id, 
								motorName, motorId, plotWindow);
		}
		else {
			// no first detector -> remove the trace (if present) and set null
			xyPlot.removeTrace(detector1Name);
			this.detector1Name = null;
		}
		// update second y axis
		if (this.detector2Id != null)
		{
			// set the current (2nd) detector name
			this.detector2Name = detector2Name;
			// does the plot already have a trace of this detector ?
			// if not -> add as new trace
			if (xyPlot.getTrace(detector2Name) == null)
				xyPlot.addTrace(detector2Name, detector2Id, 
								motorName, motorId, plotWindow);
		}
		else {
			// no second detector -> remove the trace (if present) and set null
			xyPlot.removeTrace(detector2Name);
			this.detector2Name = null;
		}
		
		// redraw
		canvas.layout();
		canvas.redraw();
		this.layout();
		this.redraw();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void measurementDataTransmitted(MeasurementData measurementData) {

		// do nothing if no measurement data was given
		if (measurementData == null) return;

		// indicators for new data / for time data
		boolean detector1HasData = false;
		boolean detector2HasData = false;
		boolean motorHasData = false;
		
		// TODO  temporary logging command
		/*
		
		String val = "";
		if(measurementData.getValues().size() > 0)
			val = measurementData.getValues().get(0).toString();
		else val = "no value";
		
		logger.debug("Measurement Data arrived: " + 
				     "ChainID: " + measurementData.getChainId() + ", " + 
				     "ScanModuleID: " + measurementData.getScanModuleId() + ", " +
				     "Position: " + measurementData.getPositionCounter() + ", " + 
				     "Value: " + val + ", " + 
				     "Name: " + measurementData.getName() + ", " + 
				     "Data Type: " + measurementData.getDataType() + ", " +
				     "Data Modifier: " + measurementData.getDataModifier() + ", " + 
				     "at: " + new TimeStamp(measurementData.getGerenalTimeStamp(), measurementData.getNanoseconds()).toMONDDYYYY());
		*/
		
		// are we still in the same scan module of the same chain ?
		if ((measurementData.getChainId() == chid) && 
			(measurementData.getScanModuleId() == smid))
		{
			// is the data for detector 1 AND is it unmodified ?
			if (this.detector1Id != null && 
				this.detector1Id.equals(measurementData.getName()) && 
				measurementData.getDataModifier() == DataModifier.UNMODIFIED) 
			{
				detector1PosCount = measurementData.getPositionCounter();
				DataType dt = measurementData.getDataType();

				// is the data type correct ?
				if (dt == DataType.DOUBLE || dt == DataType.FLOAT || 
					dt == DataType.INT32 || dt == DataType.INT16 || 
					dt == DataType.INT8)
				{
					// get the data and indicate that detector 1 has new data
					y1value = (Double) measurementData.getValues().get(0);
					detector1HasData = true;
					
					//logger.debug("Detector1: " + detector1Id + " - y value: " + 
					//			 y1value + "(" + detector1PosCount + ")");
				}
			}			
			else if (this.detector2Id != null && 
					  this.detector2Id.equals(measurementData.getName()) && 
				   measurementData.getDataModifier() == DataModifier.UNMODIFIED) 
			{
				detector2PosCount = measurementData.getPositionCounter();
				DataType dt = measurementData.getDataType();
				
				// is the data type correct ?
				if (dt == DataType.DOUBLE || dt == DataType.FLOAT || 
					dt == DataType.INT32 || dt == DataType.INT16 || 
					dt == DataType.INT8)
				{
					// get the data and indicate that detector 2 has new data
					y2value = (Double) measurementData.getValues().get(0);
					detector2HasData = true;
					//logger.debug("Detector2: " + detector2Id + " - y value: " + 
					//		 y2value + "(" + detector2PosCount + ")");
				}
			}
			else if (this.motorId != null && 
					this.motorId.equals(measurementData.getName()) && 
				   measurementData.getDataModifier() == DataModifier.UNMODIFIED) 
			{
				posCount = measurementData.getPositionCounter();
				DataType dt = measurementData.getDataType();
				
				if (dt == DataType.DOUBLE || 
					dt == DataType.FLOAT || dt == DataType.INT32 || 
					dt == DataType.INT16 || dt == DataType.INT8)
				{
					xValue = (Double) measurementData.getValues().get(0);
					motorHasData = true;	
					//logger.debug("Motor: " + motorId + " - value: (" + 
					//		 posCount + ", " + xValue + ")");
				}				
				else if(dt == DataType.DATETIME)
				{
					// measurementData offers a date with Epoch=1/1/1970 (UNIX)
					// the TimeStamp Object has an epoch of 1/1/1990 (EPICS)
					// but the plot widget is also based on the unix epoch
					// therefore nothing needs to be done, but be aware of
					// the fact that timestamp.toMONDDYYYY returns the year +20

					timestamp = new TimeStamp(
							measurementData.getGerenalTimeStamp(), 
							measurementData.getNanoseconds());
					
					xValue = Double.valueOf(posCount);
					motorHasData = true;
					
					//logger.debug("Motor: " + motorId + " - time data: " + 
					//		 timestamp.toMONDDYYYY() + "(" + posCount + ")");
				}
			}
		
			// if there is new data, update the plot 
			final boolean plotDetector1 = (detector1HasData || motorHasData)&& 
										(detector1PosCount == posCount);
			final boolean plotDetector2 = (detector2HasData || motorHasData)&& 
										(detector2PosCount == posCount);
			
			if ((plotDetector1 || plotDetector2) && !this.isDisposed()) 
			{
				// plot synchronously (to assure no side effects) 
				// TODO necessary ? or async ?
				this.getDisplay().syncExec( new Runnable() {
	
					public void run() 
					{
						if (!isDisposed()) 
						{
							if(plotDetector1 && timestamp == null) 
								xyPlot.setData(detector1Name, xValue,y1value);
							if(plotDetector2 && timestamp == null)
								xyPlot.setData(detector2Name, xValue,y2value);				
							if(plotDetector1 && timestamp != null)
								xyPlot.setData(detector1Name, posCount, 
											   y1value, timestamp);
							if(plotDetector2 && timestamp != null)
								xyPlot.setData(detector2Name, posCount, 
											   y2value, timestamp);
						}
					}
				});
			}
		}
	}
}