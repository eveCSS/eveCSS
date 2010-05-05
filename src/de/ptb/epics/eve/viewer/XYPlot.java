/**
 * 
 */
package de.ptb.epics.eve.viewer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;
import java.util.Stack;

import org.csstudio.swt.xygraph.dataprovider.CircularBufferDataProvider;
import org.csstudio.swt.xygraph.figures.Axis;
import org.csstudio.swt.xygraph.figures.ToolbarArmedXYGraph;
import org.csstudio.swt.xygraph.figures.Trace;
import org.csstudio.swt.xygraph.figures.XYGraph;
import org.eclipse.draw2d.Figure;

/**
 * @author eden
 *
 */
public class XYPlot extends Figure {

	/**
	 * 
	 */
	private XYGraph xyGraph;
	private ToolbarArmedXYGraph toolbarArmedXYGraph;
	private HashMap<String, Trace> traceMap;
	private ArrayList<Trace> traceList = new ArrayList<Trace>();
	
	public XYPlot() {

		xyGraph = new XYGraph();	
		xyGraph.primaryXAxis.setAutoScale(true);
        
		toolbarArmedXYGraph = new ToolbarArmedXYGraph(xyGraph);		
		add(toolbarArmedXYGraph);
		traceMap = new HashMap<String, Trace>();

		// add a trace for primaryYaxis
		CircularBufferDataProvider dataProvider = new CircularBufferDataProvider(false);
        dataProvider.setBufferSize(200);
		Trace trace = new Trace("Y-Axis", xyGraph.primaryXAxis, xyGraph.primaryYAxis, dataProvider);
		traceList.add(trace);

	}
	
	public Trace getTrace(String name) {
		if (name == null) return null;

		if (traceMap.containsKey(name)){
			return traceMap.get(name);
		}
		else
			return null;
	}
		
	// TODO
	// adding/removing axes/traces is not working in all cases  yet
	// Note: xyGraph always needs at least one y axis as primary y axis
	public void addTrace(String name) {
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
			CircularBufferDataProvider dataProvider = new CircularBufferDataProvider(false);
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
		if ((traceMap.size() > 1) && (traceMap.size() % 2) == 0) axis.setPrimarySide(false);
	}

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
	
	public void setData(String name, Double xValue, Double yValue){
		if ((name == null) || (xValue == null) ||  (yValue == null)) return;
		
		if (traceMap.containsKey(name)){
			Trace trace = traceMap.get(name);
			((CircularBufferDataProvider)trace.getDataProvider()).setCurrentXData(xValue);
			((CircularBufferDataProvider)trace.getDataProvider()).setCurrentYData(yValue);
		}
	}
	
	public void removeAllTraces(){
		ArrayList<String> allNames = new ArrayList<String>(traceMap.keySet());
		for (String name : allNames) {
			removeTrace(name);
		}
	}
	
	public void setXAxisTitle(String name) {
		xyGraph.primaryXAxis.setTitle(name);
	}

	@Override
	protected void layout() {
		toolbarArmedXYGraph.setBounds(bounds.getCopy().shrink(5, 5));
		super.layout();
	}
}
