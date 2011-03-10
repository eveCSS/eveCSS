package de.ptb.epics.eve.viewer.math;

import java.util.ArrayList;
import java.util.HashMap;

import org.csstudio.swt.xygraph.dataprovider.CircularBufferDataProvider;
import org.csstudio.swt.xygraph.figures.Axis;
import org.csstudio.swt.xygraph.figures.ToolbarArmedXYGraph;
import org.csstudio.swt.xygraph.figures.Trace;
import org.csstudio.swt.xygraph.figures.XYGraph;
import org.eclipse.draw2d.Figure;

/**
 * <code>XYPlot</code> is a plot based on the xygraph from csstudio.
 * 
 * @author Jens Eden
 * @author Marcus Michalsky
 */
public class XYPlot extends Figure {

	private XYGraph xyGraph;
	private ToolbarArmedXYGraph toolbarArmedXYGraph;
	private HashMap<String, Trace> traceMap;
	private ArrayList<Trace> traceList = new ArrayList<Trace>();
	
	/**
	 * Constructs an <code>XYPlot</code>.
	 */
	public XYPlot() {

		xyGraph = new XYGraph();	
		xyGraph.primaryXAxis.setAutoScale(true);
        
		toolbarArmedXYGraph = new ToolbarArmedXYGraph(xyGraph);		
		add(toolbarArmedXYGraph);
		traceMap = new HashMap<String, Trace>();

		// add a trace for primaryYaxis
		CircularBufferDataProvider dataProvider = 
				new CircularBufferDataProvider(false);
        // TODO here is the data limit of the plot
		dataProvider.setBufferSize(200);
        
		Trace trace = new Trace("Y-Axis", xyGraph.primaryXAxis, 
								xyGraph.primaryYAxis, dataProvider);
		traceList.add(trace);
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
	 * (Does nothing id name is <code>null</code>.)
	 * 
	 * @param name the name of the trace that should be added
	 */
	public void addTrace(String name) {
		// TODO
		// adding/removing axes/traces is not working in all cases  yet
		// Note: xyGraph always needs at least one y axis as primary y axis
		
		if (name == null) return;
		
		Axis axis;
		Trace trace;
		
		int traceNumber = traceMap.size();
		if (traceNumber < traceList.size()){
			// we have free traces in the list
			trace = traceList.get(traceNumber);
			axis = trace.getYAxis();
			((CircularBufferDataProvider)trace.getDataProvider()).clearTrace();
			axis.setTitle(name);
			trace.setName(name);
		}
		else {
			// create a new axis / trace
			CircularBufferDataProvider dataProvider = 
					new CircularBufferDataProvider(false);
	        dataProvider.setBufferSize(200);
			if (traceMap.isEmpty()){
				axis = xyGraph.primaryYAxis;
				axis.setTitle(name);
			}
			else {
				axis = new Axis(name, true);	
			}
			trace = new Trace(name, xyGraph.primaryXAxis, axis, dataProvider);
			traceList.add(trace);
		}
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
	 * (Does nothing if at least one argument is <code>null</code> or if the 
	 *  trace is not present.)
	 * 
	 * @param name the name of the trace the point should be added to
	 * @param xValue the x value of the data point
	 * @param yValue the y value of the data point
	 */
	public void setData(String name, Double xValue, Double yValue) {
		if ((name == null) || (xValue == null) ||  (yValue == null)) return;
		
		if (traceMap.containsKey(name)){
			Trace trace = traceMap.get(name);
			((CircularBufferDataProvider)trace.getDataProvider()).
														setCurrentXData(xValue);
			((CircularBufferDataProvider)trace.getDataProvider()).
														setCurrentYData(yValue);
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
}