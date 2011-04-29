package de.ptb.epics.eve.data.scandescription;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.csstudio.swt.xygraph.figures.Trace.PointStyle;
import org.csstudio.swt.xygraph.figures.Trace.TraceType;
import org.eclipse.swt.graphics.RGB;

import de.ptb.epics.eve.data.PlotModes;
import de.ptb.epics.eve.data.measuringstation.DetectorChannel;
import de.ptb.epics.eve.data.scandescription.updatenotification.IModelUpdateListener;
import de.ptb.epics.eve.data.scandescription.updatenotification.IModelUpdateProvider;
import de.ptb.epics.eve.data.scandescription.updatenotification.ModelUpdateEvent;

/**
 * <code>YAxis</code> represents an y axis of a plot.
 * 
 * @author Stephan Rehfeld <stephan.rehfeld( -at -) ptb.de>
 * @author Marcus Michalsky
 */
public class YAxis implements IModelUpdateListener, IModelUpdateProvider {

	/**
	 * The detector channel of the y axis.
	 */
	private DetectorChannel detectorChannel;
	
	/**
	 * The plot mode of the y axis. (initialized with linear)
	 */
	private PlotModes mode = PlotModes.LINEAR;
	
	/**
	 * The detector channel used to normalize the axis.
	 */
	private DetectorChannel normalizeChannel;
	
	/**
	 * The line style the axis should be plotted with.
	 */
	private TraceType linestyle;
	
	/**
	 * The point style the axis should be plotted with.
	 */
	private PointStyle markstyle;
	
	/**
	 * The color the axis should be plotted with.
	 */
	private RGB color;

	/**
	 * A List of objects that need to get an update message if the axis is 
	 * updated.
	 */
	private List<IModelUpdateListener> updateListener;
	
	/**
	 * This constructor creates a new y axis.
	 */
	public YAxis() {
		this.updateListener = new ArrayList<IModelUpdateListener>();
	}
	
	/**
	 * Returns the color of the line and mark points of the axis.
	 * 
	 * @return the color of the line and mark points
	 * @see org.eclipse.swt.graphics.RGB
	 */
	public RGB getColor() {
		return this.color;
	}
	
	/**
	 * Sets the color of the line and mark points of the axis.
	 * 
	 * @param color the color of the line and mark points
	 * @see org.eclipse.swt.graphics.RGB
	 */
	public void setColor(final RGB color) {
		this.color = color;
		updateListeners();
	}

	/**
	 * Returns the style of the line.
	 * 
	 * @return the style of the line
	 * @see org.csstudio.swt.xygraph.figures.Trace.TraceType
	 */
	public TraceType getLinestyle() {
		return this.linestyle;
	}

	/**
	 * Sets the style of the line.
	 * 
	 * @param linestyle the style of the line
	 * @see org.csstudio.swt.xygraph.figures.Trace.TraceType
	 */
	public void setLinestyle(final TraceType linestyle) {
		this.linestyle = linestyle;
		updateListeners();
	}

	/**
	 * Returns the style of the mark points.
	 * 
	 * @return the style of the mark points
	 * @see org.csstudio.swt.xygraph.figures.Trace.PointStyle
	 */
	public PointStyle getMarkstyle() {
		return this.markstyle;
	}

	/**
	 * Sets the style of the mark points.
	 * 
	 * @param markstyle the style of the mark points
	 * @see org.csstudio.swt.xygraph.figures.Trace.PointStyle
	 */
	public void setMarkstyle(final PointStyle markstyle) {
		this.markstyle = markstyle;
		updateListeners();
	}

	/**
	 * Returns the plot mode of the axis.
	 * 
	 * @return the plot mode of the axis
	 */
	public PlotModes getMode() {
		return this.mode;
	}

	/**
	 * Sets the plot mode of the axis.
	 * 
	 * @param mode the plot mode of the axis.
	 */
	public void setMode(final PlotModes mode) {
		this.mode = mode;
		updateListeners();
	}

	/**
	 * Returns the detector channel used as the axis.
	 * 
	 * @return the detector channel used as the axis
	 */
	public DetectorChannel getDetectorChannel() {
		return detectorChannel;
	}

	/**
	 * Sets the detector channel that should be used as the axis.
	 * 
	 * @param detectorChannel the detector channel that should be used as the 
	 * 		   axis
	 */
	public void setDetectorChannel(final DetectorChannel detectorChannel) {
		this.detectorChannel = detectorChannel;
		updateListeners();
	}

	/**
	 * Returns the detector channel used for normalization.
	 * 
	 * @return the detector channel used for normalization
	 */
	public DetectorChannel getNormalizeChannel() {
		return normalizeChannel;
	}

	/**
	 * Clears the detector channel used for normalization. 
	 */
	public void clearNormalizeChannel() {
		this.normalizeChannel = null;
	}

	/**
	 * Sets the detector channel used for normalization.
	 * 
	 * @param normalizeChannel the detector channel that should be used for 
	 * 		   normalization
	 */
	public void setNormalizeChannel(final DetectorChannel normalizeChannel) {
		this.normalizeChannel = normalizeChannel;
		updateListeners();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void updateEvent(final ModelUpdateEvent modelUpdateEvent) {
		updateListeners();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean addModelUpdateListener(
			final IModelUpdateListener modelUpdateListener) {
		return this.updateListener.add(modelUpdateListener);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean removeModelUpdateListener(
			final IModelUpdateListener modelUpdateListener) {
		return this.updateListener.remove(modelUpdateListener);
	}	
	
	/*
	 * called by several methods to inform interested parties about changes.
	 */
	private void updateListeners()
	{
		final CopyOnWriteArrayList<IModelUpdateListener> list = 
			new CopyOnWriteArrayList<IModelUpdateListener>(this.updateListener);
		
		Iterator<IModelUpdateListener> it = list.iterator();
		
		while(it.hasNext()) {
			it.next().updateEvent(new ModelUpdateEvent(this, null));
		}
	}
}