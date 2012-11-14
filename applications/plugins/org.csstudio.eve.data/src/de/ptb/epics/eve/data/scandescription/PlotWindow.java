package de.ptb.epics.eve.data.scandescription;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.apache.log4j.Logger;
import org.csstudio.swt.xygraph.figures.Trace.PointStyle;
import org.csstudio.swt.xygraph.figures.Trace.TraceType;
import org.eclipse.swt.graphics.RGB;

import de.ptb.epics.eve.data.PlotModes;
import de.ptb.epics.eve.data.measuringstation.DetectorChannel;
import de.ptb.epics.eve.data.measuringstation.MotorAxis;
import de.ptb.epics.eve.data.scandescription.errors.IModelError;
import de.ptb.epics.eve.data.scandescription.errors.PlotWindowError;
import de.ptb.epics.eve.data.scandescription.errors.PlotWindowErrorTypes;
import de.ptb.epics.eve.data.scandescription.updatenotification.IModelUpdateListener;
import de.ptb.epics.eve.data.scandescription.updatenotification.IModelUpdateProvider;
import de.ptb.epics.eve.data.scandescription.updatenotification.ModelUpdateEvent;

/**
 * The Representation of a plot window.
 * 
 * @author Stephan Rehfeld <stephan.rehfeld( -at -) ptb.de>
 * @author Marcus Michalsky
 * @author Hartmut Scherr
 */
public class PlotWindow implements IModelUpdateListener, IModelUpdateProvider,
				PropertyChangeListener {

	// logging 
	private static final Logger logger = 
		Logger.getLogger(PlotWindow.class.getName());
	
	// the id 
	private int id;
	
	// a name/title/short description of the plot
	private String name;
	
	// the scan module the plot belongs to
	private ScanModule scanModule;

	// the plot mode
	private PlotModes mode;
	
	// indicates whether the plot window should be initialized
	private boolean init;
	
	// the motor axis that used as the x axis
	private MotorAxis xAxis;
	
	// contains all y axes
	private List<YAxis> yAxes;
	
	// parties interested in updates of the plot window
	private List<IModelUpdateListener> updateListener;
	
	/**
	 * Constructs a new plot window. Notice that an id has to be set via 
	 * {@link #setId(int)} afterwards.
	 * 
	 * @param scanModule the scanModule the plot window will be added to
	 * @throws IllegalArgumentException if <code>scanModule</code> is 
	 * 		<code>null</code>
	 */
	public PlotWindow(final ScanModule scanModule) {
		if(scanModule == null) {
			throw new IllegalArgumentException(
					"The parameter 'scanModule' must not be null!");
		}
		this.scanModule = scanModule;
		this.scanModule.addPropertyChangeListener("removeAxis", this);
		this.scanModule.addPropertyChangeListener("removeChannel", this);

		this.scanModule.addPropertyChangeListener("addAxis", this);

		this.updateListener = new ArrayList<IModelUpdateListener>();
		this.yAxes = new ArrayList<YAxis>();
		this.mode = PlotModes.LINEAR;
	}
	
	/**
	 * Constructs a <code>PlotWindow</code>. If <code>generateId</code> is 
	 * <code>true</code> an available id is automatically assigned, otherwise 
	 * {@link #PlotWindow(ScanModule)} is called.<br>
	 * It also sets an x and y axis if only one of each is available.
	 * 
	 * @param scanModule the scan module the plot window will be added to
	 * @param generateId <code>true</code> if an id should be generated, 
	 * 					<code>false</code> otherwise
	 * @throws IllegalArgumentException if <code>scanModule</code> is 
	 * 		<code>null</code>
	 * @author Marcus Michalsky
	 * @since 1.1
	 */
	public PlotWindow(final ScanModule scanModule, final boolean generateId) {
		this(scanModule);
		if(generateId) {
			this.id = this.getScanModule().getChain().getScanDescription().
				getAvailablePlotId();
			this.name = "Plot " + this.id;
		}
		if(scanModule.getAxes().length == 1) {
			this.xAxis = scanModule.getAxes()[0].getMotorAxis();
		}
		if(scanModule.getChannels().length == 1) {
			YAxis yAxis = new YAxis();
			// default values for color, line style and mark style
			yAxis.setColor(new RGB(0,0,255));
			yAxis.setLinestyle(TraceType.SOLID_LINE);
			yAxis.setMarkstyle(PointStyle.NONE);
			yAxis.setDetectorChannel(scanModule.getChannels()[0].
					getDetectorChannel());
			this.yAxes.add(yAxis);
		}
	}
	
	/**
	 * Returns the id of the plot window.
	 * 
	 * @return the id of the plot window
	 */
	public int getId() {
		return this.id;
	}
	
	/**
	 * Returns the scan module the plot is corresponding to.
	 * 
	 * @return the corresponding scan module
	 */
	public ScanModule getScanModule() {
		return this.scanModule;
	}

	/**
	 * Sets the id of the plot window.
	 * 
	 * @param id the id that should be set
	 * @throws IllegalArgumentException if id is less than 1
	 */
	public void setId(final int id) {
		if(id < 1) {
			throw new IllegalArgumentException(
					"The parameter 'id' must be larger than 0.");
		}
		this.id = id;
		updateListeners();
	}
	
	/**
	 * @return the name
	 */
	public String getName() {
		if(this.name == null) {
			return "";
		}
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
		updateListeners();
	}

	/**
	 * Checks whether the plot window will be initialized.
	 * 
	 * @return <code>true</code> if the plot window will be initialized, 
	 * 			<code>false</code> otherwise
	 */
	public boolean isInit() {
		return this.init;
	}
	
	/**
	 * Set whether the plot window should be initialized.
	 * 
	 * @param init <code>true</code> if the plot window should be initialized, 
	 * 		   <code>false</code> otherwise
	 */
	public void setInit(final boolean init) {
		this.init = init;
		updateListeners();
	}
	
	/**
	 * Returns the plot mode of the plot window as defined in 
	 * {@link de.ptb.epics.eve.data.PlotModes}.
	 * 
	 * @return the plot mode of the plot window as defined in 
	 * 		{@link de.ptb.epics.eve.data.PlotModes}
	 */
	public PlotModes getMode() {
		return this.mode;
	}
	
	/**
	 * Sets the plot mode of the plot window.
	 * 
	 * @param mode the plot mode of this plot window
	 * @throws IllegalArgumentException if the argument is <code>null</code>
	 */
	public void setMode(final PlotModes mode) {
		if(mode == null) {
			throw new IllegalArgumentException(
					"The parameter 'mode' must not be null!");
		}
		this.mode = mode;
		updateListeners();
	}
	
	/**
	 * Returns the motor axis that is used as the x-axis.
	 * 
	 * @return the motor axis that is used as the x-axis
	 */
	public MotorAxis getXAxis() {
		return this.xAxis;
	}
	
	/**
	 * Sets the motor axis that should be used as the x axis.
	 * 
	 * @param axis the motor axis that should be used as the x-axis
	 */
	public void setXAxis(final MotorAxis axis) {
		this.xAxis = axis;
		updateListeners();
	}

	/**
	 * Adds a y axis to the plot window.
	 * 
	 * @param yAxis the y axis that should be added
	 * @throws IllegalArgumentException if the argument is <code>null</code>
	 */
	public void addYAxis(final YAxis yAxis) {
		if(yAxis == null) {
			throw new IllegalArgumentException(
					"The parameter 'yAxis' must not be null!");
		}
		yAxis.addModelUpdateListener(this);
		this.yAxes.add(yAxis);
		updateListeners();
	}
	
	/**
	 * Adds a y axis to the plot window and sets the given detector channel.
	 * 
	 * @param channel the detector channel that should be set
	 */
	public void addYAxis(final DetectorChannel channel) {
		YAxis axis = new YAxis(channel);
		this.addYAxis(axis);
	}
	
	/**
	 * Removes a y axis from the plot window.
	 * 
	 * @param yAxis the y axis that should be removed
	 * @throws IllegalArgumentException if the argument is <code>null</code>
	 */
	public void removeYAxis(final YAxis yAxis) {
		if(yAxis == null) {
			throw new IllegalArgumentException(
					"The parameter 'yAxis' must not be null!");
		}
		yAxis.removeModelUpdateListener(this);
		this.yAxes.remove(yAxis);
		updateListeners();
	}
	
	/**
	 * This method returns an iterator over all y-axes.
	 * 
	 * @return An iterator over all y axes.
	 */
	public Iterator<YAxis> getYAxisIterator() {
		return this.yAxes.iterator();
	}
	/**
	 * get a list of y-axes
	 * 
	 * @return a list of y axes
	 */
	public List<YAxis> getYAxes() {
		return new ArrayList<YAxis>(this.yAxes);
	}

	/**
	 * Returns the amount of y-axes.
	 * 
	 * @return the amount of y-axes of the plot window
	 */
	public int getYAxisAmount() {
		return this.yAxes.size();
	}

	/**
	 * Removes all y-axes of this plot window.
	 */
	public void clearYAxis() {
		for(YAxis yAxis : this.yAxes) {
			yAxis.removeModelUpdateListener(this);
		}
		this.yAxes.clear();
	}

	/**
	 * {@inheritDoc} 
	 */
	public void updateEvent(final ModelUpdateEvent modelUpdateEvent) {
		updateListeners();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		logger.debug("PropertyChange: " + evt.getPropertyName());
		
		if (evt.getPropertyName().equals("removeAxis")) {
			// if x Axis is selected, check if entry has to delete
			if (this.xAxis != null) {
				MotorAxis deletedAxis = ((Axis)evt.getOldValue()).getMotorAxis();
				if (this.xAxis.equals(deletedAxis)) {
					this.setXAxis(null);
				}
			}
			// if only one axis is present, set this axis for the plot
			final Axis[] presentAxes = scanModule.getAxes();
			if (presentAxes.length == 1) {
				this.setXAxis((presentAxes[0]).getMotorAxis());
			}
			updateListeners();
		} else if (evt.getPropertyName().equals("addAxis")) {
			// if no xAxis selected, set the new axis
			if (this.xAxis == null) {
				MotorAxis addAxis = ((Axis)evt.getOldValue()).getMotorAxis();
				this.setXAxis(addAxis);
			}
		} else if (evt.getPropertyName().equals("removeChannel")) {
			DetectorChannel deletedChannel = ((Channel)evt.getOldValue()).
														getDetectorChannel();
			YAxis wegAxis1 = null;
			YAxis wegAxis2 = null;
			for( Iterator< YAxis > ityAxis = this.getYAxisIterator(); 
					ityAxis.hasNext();) {
				YAxis yAxis = ityAxis.next();
				if (yAxis.getNormalizeChannel() != null) {
					if (yAxis.getNormalizeChannel().equals(deletedChannel)) {
						yAxis.setNormalizeChannel(null);
					}
				}
				if (yAxis.getDetectorChannel() != null) {
					if (yAxis.getDetectorChannel().equals(deletedChannel)) {
						yAxis.setDetectorChannel(null);
						// Wenn es nur noch einen vorhandenen Channel gibt, 
						// diese als Plot setzen!
						final Channel[] presentChannels = scanModule.
															getChannels();
						if (presentChannels.length == 1) {
							yAxis.setDetectorChannel(presentChannels[0].
														getDetectorChannel());
							yAxis.setNormalizeChannel(null);
						} else {
							yAxis.setDetectorChannel(null);
							if (wegAxis1 == null)
								wegAxis1 = yAxis;
							else
								wegAxis2 = yAxis;
						}
					}
				}
			}
			if (wegAxis1 != null)
				this.removeYAxis(wegAxis1);
			if (wegAxis2 != null)
				this.removeYAxis(wegAxis2);
			
			updateListeners();
		}
	}

	/**
	 * 
	 * @param channel
	 * @param oldChannel
	 * @param newChannel
	 */
	protected void normalizeChannelChanged(Channel channel,
			DetectorChannel normalizeChannel) {
		for(YAxis yAxis : this.yAxes) {
			if (yAxis.getDetectorChannel().equals(channel.getDetectorChannel())) {
				if (yAxis.getNormalizeChannel() != null && 
					yAxis.getNormalizeChannel().equals(normalizeChannel)) {
						yAxis.setNormalizeChannel(null);
				}
			}
		}
	}
	
	/**
	 * {@inheritDoc}
	 */
	public boolean addModelUpdateListener(
			final IModelUpdateListener modelUpdateListener) {
		return this.updateListener.add(modelUpdateListener);
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean removeModelUpdateListener(
			final IModelUpdateListener modelUpdateListener) {
		return this.updateListener.remove(modelUpdateListener);
	}

	/**
	 * Returns a <code>List</code> of all model errors that occurred.
	 * 
	 * @return a <code>List</code> of all model errors that occurred
	 */
	public List<IModelError> getModelErrors() {
		final List<IModelError> modelErrors = new ArrayList<IModelError>();
		if(this.xAxis == null) {
			modelErrors.add(new PlotWindowError(
					this, PlotWindowErrorTypes.NO_X_AXIS_SET));
		}
		if(this.getYAxisAmount() == 0) {
			modelErrors.add(new PlotWindowError(
					this, PlotWindowErrorTypes.NO_Y_AXIS_SET));
		}
		return modelErrors;
	}	
	
	/*
	 * 
	 */
	private void updateListeners() {
		final CopyOnWriteArrayList<IModelUpdateListener> list = 
			new CopyOnWriteArrayList<IModelUpdateListener>(this.updateListener);
		
		for(IModelUpdateListener imul : list) {
			imul.updateEvent(new ModelUpdateEvent(this, null));
		}
	}
}