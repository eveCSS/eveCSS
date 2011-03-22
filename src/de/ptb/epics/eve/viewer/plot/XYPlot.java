package de.ptb.epics.eve.viewer.plot;

import gov.aps.jca.dbr.TimeStamp;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import org.apache.log4j.Logger;
import org.csstudio.swt.xygraph.dataprovider.CircularBufferDataProvider;
import org.csstudio.swt.xygraph.figures.Axis;
import org.csstudio.swt.xygraph.figures.ToolbarArmedXYGraph;
import org.csstudio.swt.xygraph.figures.Trace;
import org.csstudio.swt.xygraph.figures.XYGraph;
import org.csstudio.swt.xygraph.linearscale.Range;
import org.eclipse.draw2d.Figure;
import org.eclipse.swt.graphics.Color;

import de.ptb.epics.eve.data.DataTypes;
import de.ptb.epics.eve.data.PlotModes;
import de.ptb.epics.eve.data.scandescription.PlotWindow;
import de.ptb.epics.eve.data.scandescription.YAxis;
import de.ptb.epics.eve.viewer.Activator;

/**
 * <code>XYPlot</code> is a plot based on the xygraph from csstudio.
 * 
 * @author Jens Eden
 * @author Marcus Michalsky
 */
public class XYPlot extends Figure {

	// temporary logging 
	private static Logger logger = Logger.getLogger(XYPlot.class);
	
	private XYGraph xyGraph;
	private ToolbarArmedXYGraph toolbarArmedXYGraph;
	private HashMap<String, Trace> traceMap;
	
	/**
	 * Constructs an <code>XYPlot</code>.
	 */
	public XYPlot() {
		init(false);
		
		logger.debug("a new xy plot has been created."); // TODO
	}
	
	/**
	 * Returns the trace with the given name.
	 * 
	 * @param name the name of the trace that should be returned
	 * @return the trace with the given name 
	 * 			(or <code>null</code> if name is <code>null</code> or 
	 * 			 not present)
	 */
	public Trace getTrace(String name) {
		if (name == null) return null;

		if (traceMap.containsKey(name)) {
			return traceMap.get(name);
		}
		else
			return null;
	}
		
	/**
	 * Adds a trace with a specific name to the plot.
	 * (Does nothing if name is <code>null</code>.)
	 * 
	 * @param name the name of the trace that should be added
	 * @param id the detector id of the detector that should be added as a trace
	 * @param motorName the name of the motor that is used as x axis
	 * @param motorId the id of the motor that is used as x axis
	 * @param plotWindow the 
	 * 		  {@link de.ptb.epics.eve.data.scandescription.PlotWindow} which
	 * 		  contains style information (color, line style, mark style, mode)
	 */
	public void addTrace(String name, String id, 
						 String motorName, String motorId, 
						 PlotWindow plotWindow) {
		// Note: xyGraph always needs at least one y axis as primary y axis
		
		if (name == null) return;
		
		logger.debug("Trace added: " + name + " , id: " + id); // TODO 
		
		Axis axis;
		Trace trace;
		
		// check if we deal with date time data
		boolean timedata = Activator.getDefault().getMeasuringStation().
						   getMotorAxisById(motorId).getType() 
						   == DataTypes.DATETIME;
		
			// create a new trace
			
			// create the buffer where the data will be held
			// ! notice the boolean parameter in the constructor which defines 
			// if the data is chronological (see CSS)
			CircularBufferDataProvider dataProvider = 
					new CircularBufferDataProvider(timedata); 
	        // the size of the buffer // TODO maybe increase
			dataProvider.setBufferSize(200);
			
			// if the plot currently has no trace, we take the primary y axis
			// as y axis of our new trace
	        if (traceMap.isEmpty()){
				axis = xyGraph.primaryYAxis;
				axis.setTitle(name);
			}
			else {
				axis = new Axis(name, true); // true defines a y axis
			}
			
	        // set the time unit of the x axis if we deal with time data
	        if(timedata) xyGraph.primaryXAxis.setTimeUnit(Calendar.MILLISECOND);
	        
	        // we create the trace with the detector name as name, 
	        // the primary x axis and either the primary y axis or a second one
	        // as decided above and our data Provider
	        trace = new Trace(name, xyGraph.primaryXAxis, axis, dataProvider);
			
	        // find the y axis of the detector channel in the plotWindow
	        int axis_pos = -1;
	        
	        for(int i=0;i<plotWindow.getYAxes().size();i++)
	        {
	        	if(plotWindow.getYAxes().get(i).getDetectorChannel().getID() == id)
	        	{
	        		axis_pos = i;
	        	}
	        }
	        
	        // now that we know which y axis we need, we can style it
	        
	        YAxis axis_to_change = plotWindow.getYAxes().get(axis_pos);
	        
	        trace.setTraceType(axis_to_change.getLinestyle());
	        trace.setPointStyle(axis_to_change.getMarkstyle());
	        trace.setTraceColor(new Color(null,axis_to_change.getColor()));
	        
	        // if wanted, change to logarithm scale (and change axis title)
	        
	        if(axis_to_change.getMode() == PlotModes.LOG)
	        {
	        	trace.getYAxis().setLogScale(true);
	        	trace.getYAxis().setTitle(name + " (log)");
	        }
	        
	        if(plotWindow.getMode() == PlotModes.LOG)
	        {
	        	trace.getXAxis().setLogScale(true);
	        	trace.getXAxis().setTitle(motorName + " (log)");
	        }
	        else
	        {
	        	trace.getXAxis().setTitle(motorName);
	        }
    
	        // if our motor sends time data, we have to signal it to the x axis
	        xyGraph.primaryXAxis.setDateEnabled(timedata);
	
		
		axis.setAutoScale(true);
		xyGraph.addTrace(trace);
		if (axis != xyGraph.primaryYAxis) xyGraph.addAxis(axis);
		traceMap.put(name, trace);
		if ((traceMap.size() > 1) && (traceMap.size() % 2) == 0) 
			axis.setPrimarySide(false);
	}

	/**
	 * Removes the trace with a specific name from the plot.
	 * 
	 * @param name the name of the trace that should be removed
	 */
	public void removeTrace(String name) {
		if (name == null) return;
		
		logger.debug("Trace has been removed: " + name); // TODO 
		
		if (traceMap.containsKey(name)){
			Trace trace = traceMap.get(name);
			traceMap.remove(name);
			Axis axis = trace.getYAxis();
			if (axis != xyGraph.primaryYAxis){
				xyGraph.removeAxis(trace.getYAxis());
			}
			xyGraph.removeTrace(trace);
			((CircularBufferDataProvider)trace.getDataProvider()).clearTrace();
		}
	}
	
	/**
	 * Sets a new data point (x,y) to a trace of the plot.
	 * (Has no effect if at least one argument is <code>null</code> or if the 
	 *  trace is not present.)
	 * 
	 * @param name the name of the trace the point should be added to
	 * @param xValue the x value of the data point
	 * @param yValue the y value of the data point
	 */
	public void setData(String name, Double xValue, Double yValue) {
		if ((name == null) || (xValue == null) ||  (yValue == null)) return;
		
		if (traceMap.containsKey(name)){
			
			logger.info("data set: " + name + "(" + xValue + ", " + yValue + ")"); // TODO 
			
			Trace trace = traceMap.get(name);
			((CircularBufferDataProvider)trace.getDataProvider()).
														setCurrentXData(xValue);
			((CircularBufferDataProvider)trace.getDataProvider()).
														setCurrentYData(yValue);
		}
	}
	
	/**
	 * Sets a new data point (x,y) with a time stamp to a trace of the plot.
	 * 
	 * @param name the name of the trace the point should be added to
	 * @param motorPosCount the measurement count (position counter of the motor)
	 * @param yValue the y value of the data point
	 * @param timestamp the time when the y value was measured
	 */
	public void setData(String name, int motorPosCount, Double yValue, TimeStamp timestamp) {
		if ((name == null) || (yValue == null)) return;
		
		if (traceMap.containsKey(name)){
						
			Trace trace = traceMap.get(name);

			long time_in_msecs = timestamp.secPastEpoch()*1000 + timestamp.nsec()/1000000;
			
			((CircularBufferDataProvider)trace.getDataProvider()).
												setCurrentXData(motorPosCount);
			((CircularBufferDataProvider)trace.getDataProvider()).
												setCurrentYData(yValue, time_in_msecs);
			
			logger.info("data set: " + name + "(" + motorPosCount + ", " + yValue + 
					    ", " + time_in_msecs  + ") - " + timestamp.toMONDDYYYY()); // TODO
		}
	}
	
	
	/**
	 * Removes all traces currently in the plot.
	 */
	public void removeAllTraces() {
		ArrayList<String> allNames = new ArrayList<String>(traceMap.keySet());
		for (String name : allNames) {
			removeTrace(name);
		}
	}
	
	/**
	 * Sets the title of the x axis.
	 * 
	 * @param name the title the x axis should be set to
	 */
	public void setXAxisTitle(String name) {
		xyGraph.primaryXAxis.setTitle(name);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void layout() {
		toolbarArmedXYGraph.setBounds(bounds.getCopy().shrink(5, 5));
		super.layout();
	}
	
	/**
	 * (Re-)Initializes the plot by cleaning it and setting initial axis names 
	 * and parameters. 
	 * 
	 * @param clear indicates whether contained data of the plot should be 
	 * 		  removed. (should always be <code>true</code)
	 */
	public void init(boolean clear)
	{
		if(clear)
		{
			this.removeAllTraces();
			this.traceMap.clear();
			this.xyGraph.erase();
		}

		xyGraph = new XYGraph();	
		xyGraph.setTitle("XY - Plot");
		xyGraph.primaryXAxis.setTitle("x - axis");
		xyGraph.primaryYAxis.setTitle("y - axis");
		xyGraph.primaryXAxis.setRange(new Range(0,200));
		xyGraph.primaryXAxis.setAutoScale(true); 
		xyGraph.primaryYAxis.setAutoScale(true);
		xyGraph.primaryXAxis.setShowMajorGrid(true);
		xyGraph.primaryYAxis.setShowMajorGrid(true);
		xyGraph.primaryXAxis.setAutoScaleThreshold(0);
        
		toolbarArmedXYGraph = new ToolbarArmedXYGraph(xyGraph);		
		add(toolbarArmedXYGraph);
		
		traceMap = new HashMap<String, Trace>();
		
		logger.debug("xy plot has been (re-)initialized."); // TODO
	}
}