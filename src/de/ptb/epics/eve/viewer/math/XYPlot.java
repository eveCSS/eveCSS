/* 
 * Copyright (c) 2001, 2008 Physikalisch-Technische Bundesanstalt.
 * All rights reserved.
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 */
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
	 * 
	 * 
	 * @param name
	 * @return
	 */
	public Trace getTrace(String name) {
		if (name == null) return null;

		if (traceMap.containsKey(name)){
			return traceMap.get(name);
		}
		else
			return null;
	}
		
	/**
	 * 
	 * 
	 * @param name
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
	 * 
	 * 
	 * @param name
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
	 * 
	 * 
	 * @param name
	 * @param xValue
	 * @param yValue
	 */
	public void setData(String name, Double xValue, Double yValue){
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
	 * 
	 */
	public void removeAllTraces(){
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