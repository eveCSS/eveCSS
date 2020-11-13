package de.ptb.epics.eve.data.scandescription;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
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
	public static final String CHANNEL_PROP = "detectorChannel";
	public static final String NORMALIZE_PROP = "normalizeChannel";
	public static final String MODE_PROP = "mode";
	public static final String MODIFIER_PROP = "modifier";
	public static final String LINESTYLE_PROP = "linestyle";
	public static final String MARKSTYPE_PROP = "markstyle";
	public static final String COLOR_PROP = "color";
	
	// the detector channel of the y axis
	private DetectorChannel detectorChannel;
	
	// the plot mode of the y axis. (initialized with linear)
	private PlotModes mode = PlotModes.LINEAR;
	
	private YAxisModifier modifier;
	
	// the detector channel used to normalize the axis
	private DetectorChannel normalizeChannel;
	
	// the line style the axis should be plotted with
	private TraceType linestyle;
	
	// the point style the axis should be plotted with
	private PointStyle markstyle;
	
	// the color the axis should be plotted with
	private RGB color;

	/*
	 * A List of objects that need to get an update message if the axis is 
	 * updated.
	 */
	private List<IModelUpdateListener> updateListener;
	
	private PropertyChangeSupport propertyChangeSupport;
	
	/**
	 * Constructs a <code>YAxis</code>.
	 */
	public YAxis() {
		this.detectorChannel = null;
		this.modifier = YAxisModifier.NONE;
		this.normalizeChannel = null;
		this.linestyle = null;
		this.markstyle = null;
		this.color = null;
		this.updateListener = new ArrayList<>();
		this.propertyChangeSupport = new PropertyChangeSupport(this);
	}
	
	/**
	 * Constructs a YAxis with a preset detector channel and default values 
	 * for plot mode, trace type, point style and color.
	 * 
	 * @param channel the detector channel that should be set
	 */
	public YAxis(DetectorChannel channel) {
		this();
		this.setDetectorChannel(channel);
		this.mode = PlotModes.LINEAR;
		this.linestyle = TraceType.SOLID_LINE;
		this.markstyle = PointStyle.POINT;
		this.color = new RGB(0, 0, 255);
	}
	
	/**
	 * Copy Constructor.
	 * 
	 * @param yAxis the yaxis to be copied
	 * @return a copy fo the given yaxis
	 * @author Marcus Michalsky
	 * @since 1.19
	 */
	public static YAxis newInstance(YAxis yAxis) {
		YAxis newYAxis = new YAxis(yAxis.getDetectorChannel());
		newYAxis.setNormalizeChannel(yAxis.getNormalizeChannel());
		newYAxis.setLinestyle(yAxis.getLinestyle());
		newYAxis.setMarkstyle(yAxis.getMarkstyle());
		newYAxis.setMode(yAxis.getMode());
		newYAxis.setColor(new RGB(yAxis.getColor().red, yAxis.getColor().green,
				yAxis.getColor().blue));
		return newYAxis;
	}
	
	/**
	 * @return the modifier
	 * 
	 * @since 1.28
	 */
	public YAxisModifier getModifier() {
		return modifier;
	}

	/**
	 * @param modifier the modifier to set
	 * 
	 * @since 1.28
	 */
	public void setModifier(YAxisModifier modifier) {
		YAxisModifier oldValue = this.modifier;
		this.modifier = modifier;
		updateListeners();
		this.propertyChangeSupport.firePropertyChange(MODIFIER_PROP, oldValue, 
				modifier);
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
		RGB oldValue = this.color;
		this.color = color;
		updateListeners();
		this.propertyChangeSupport.firePropertyChange(COLOR_PROP, oldValue, 
				color);
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
		TraceType oldValue = this.linestyle;
		this.linestyle = linestyle;
		updateListeners();
		this.propertyChangeSupport.firePropertyChange(LINESTYLE_PROP, oldValue, 
				linestyle);
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
		PointStyle oldValue = this.markstyle;
		this.markstyle = markstyle;
		updateListeners();
		this.propertyChangeSupport.firePropertyChange(MARKSTYPE_PROP, oldValue, 
				markstyle);
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
		PlotModes oldValue = this.mode;
		this.mode = mode;
		updateListeners();
		this.propertyChangeSupport.firePropertyChange(MODE_PROP, oldValue, mode);
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
		DetectorChannel oldValue = this.detectorChannel;
		this.detectorChannel = detectorChannel;
		updateListeners();
		this.propertyChangeSupport.firePropertyChange(CHANNEL_PROP, oldValue, 
				detectorChannel);
	}

	/**
	 * Returns whether the axis is normalized.
	 * 
	 * @return <code>true</code> if axis normalized, <code>false</code> otherwise
	 */
	public boolean isNormalized() {
		return this.normalizeChannel != null;
	}
	
	/**
	 * Returns the composite id of detector channel and normalize channel or 
	 * the channel name if {@link #isNormalized()} is <code>false</code>.
	 * If the no channel is set an empty String is returned.
	 * 
	 * @return the composite id of detector channel and normalize channel or an 
	 * 	empty string if none
	 * @since 1.22
	 */
	public String getNormalizedName() {
		if (this.detectorChannel == null) {
			return "";
		}
		if (!isNormalized()) {
			return this.detectorChannel.getName();
		}
		return this.detectorChannel.getName() + " / "
				+ this.normalizeChannel.getName();
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
	 * Sets the detector channel used for normalization.
	 * 
	 * @param normalizeChannel the detector channel that should be used for 
	 * 		   normalization
	 */
	public void setNormalizeChannel(final DetectorChannel normalizeChannel) {
		DetectorChannel oldValue = this.normalizeChannel;
		this.normalizeChannel = normalizeChannel;
		updateListeners();
		this.propertyChangeSupport.firePropertyChange(NORMALIZE_PROP, oldValue, 
				normalizeChannel);
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
	private void updateListeners() {
		final CopyOnWriteArrayList<IModelUpdateListener> list = 
			new CopyOnWriteArrayList<>(this.updateListener);
		
		for(IModelUpdateListener imul : list) {
			imul.updateEvent(new ModelUpdateEvent(this, null));
		}
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean equals(Object other) {
		if (other == null) {
			return false;
		}
		if (other == this) {
			return true;
		}
		YAxis yAxis = (YAxis) other;
		if (this.detectorChannel == null) {
			if (yAxis.getDetectorChannel() != null) {
				return false;
			}
		} else if (!this.detectorChannel.equals(yAxis.getDetectorChannel())) {
			return false;
		}
		if (this.normalizeChannel == null) {
			if (yAxis.getNormalizeChannel() != null) {
				return false;
			}
		} else if (!this.normalizeChannel.equals(yAxis.getNormalizeChannel())) {
			return false;
		}
		if (!this.mode.equals(yAxis.getMode())) {
			return false;
		}
		if (!this.modifier.equals(yAxis.getModifier())) {
			return false;
		}
		return true;
	}
	
	public void addPropertyChangeListener(String property, 
			PropertyChangeListener listener) {
		this.propertyChangeSupport.addPropertyChangeListener(property, listener);
	}
	
	public void removePropertyChangeListener(String property, 
			PropertyChangeListener listener) {
		this.propertyChangeSupport.removePropertyChangeListener(property, 
				listener);
	}
}