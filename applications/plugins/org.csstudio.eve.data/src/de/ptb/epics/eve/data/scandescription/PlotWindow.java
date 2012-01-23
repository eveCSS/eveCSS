package de.ptb.epics.eve.data.scandescription;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

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

	/**
	 * The motor axis that used as the x axis.
	 */
	private MotorAxis xAxis;
	
	/**
	 * A List containing all y axes.
	 */
	private List<YAxis> yAxis;
	
	/**
	 * The id of the plot window.
	 */
	private int id;
	
	/**
	 * The scan module the plot corresponds to
	 */
	private ScanModule scanModule;

	/**
	 * The plot mode of the plot window.
	 */
	private PlotModes mode;
	
	/**
	 * Indicates whether the plot window should be initialized.
	 */
	private boolean init;
	
	/**
	 * A List of objects that need to be informed if the plot window is updated.
	 */
	private List<IModelUpdateListener> updateListener;
	
	/**
	 * Constructs a new plot window.
	 */
//	public PlotWindow() {
	public PlotWindow(final ScanModule scanModule) {
		if(scanModule == null) {
			throw new IllegalArgumentException(
					"The parameter 'scanModule' must not be null!");
		}
		this.scanModule = scanModule;
		this.scanModule.addPropertyChangeListener("removeAxis", this);
		this.scanModule.addPropertyChangeListener("removeChannel", this);

		this.scanModule.addPropertyChangeListener("addAxis", this);

		// TODO: Die Listener müssen auch noch entfernt werden,
		// wenn der Plot gelöscht wird!
		
		this.updateListener = new ArrayList< IModelUpdateListener >();
		this.yAxis = new ArrayList< YAxis >();
		this.mode = PlotModes.LINEAR;
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
		this.yAxis.add(yAxis);
		updateListeners();
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
		this.yAxis.remove(yAxis);
		updateListeners();
	}
	
	/**
	 * This method returns an iterator over all y-axes.
	 * 
	 * @return An iterator over all y axes.
	 */
	public Iterator<YAxis> getYAxisIterator() {
		return this.yAxis.iterator();
	}
	/**
	 * get a list of y-axes
	 * 
	 * @return a list of y axes
	 */
	public List<YAxis> getYAxes() {
		return new ArrayList<YAxis>(this.yAxis);
	}

	/**
	 * Returns the amount of y-axes.
	 * 
	 * @return the amount of y-axes of the plot window
	 */
	public int getYAxisAmount() {
		return this.yAxis.size();
	}

	/**
	 * Removes all y-axes of this plot window.
	 */
	public void clearYAxis() {
		Iterator<YAxis> it = this.yAxis.iterator();
		while(it.hasNext()) {
			YAxis yAxis = it.next();
			yAxis.removeModelUpdateListener(this);
		}
		this.yAxis.clear();
		
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
	private void updateListeners()
	{
		final CopyOnWriteArrayList<IModelUpdateListener> list = 
			new CopyOnWriteArrayList<IModelUpdateListener>(this.updateListener);
		
		Iterator<IModelUpdateListener> it = list.iterator();
		
		while(it.hasNext()) {
			it.next().updateEvent(new ModelUpdateEvent(this, null));
		}
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		
		if (evt.getPropertyName().equals("removeAxis")) {

			System.out.println("\tproperty Change (remove Axis) von PlotWIndow");
			System.out.println("\tthis.xAxis: " + this.xAxis);
			
			// if x Axis is selected, check if entry has to delete
			if (this.xAxis != null) {
				MotorAxis deletedAxis = ((Axis)evt.getOldValue()).getMotorAxis();
				if (this.xAxis.equals(deletedAxis)) {
					this.setXAxis(null);
				}
			}
			// Wenn es nur noch eine vorhanden Achse gibt, diese als Plot
			// setzen!
			final Axis[] presentAxes = scanModule.getAxes();
			if (presentAxes.length == 1) {
				this.setXAxis((presentAxes[0]).getMotorAxis());
			}
			updateListeners();
		} else if (evt.getPropertyName().equals("addAxis")) {
			System.out.println("\tproperty Change (add Axis) von PlotWIndow");
			System.out.println("\tthis.xAxis: " + this.xAxis);
			
			// if no xAxis selected, set the new axis
			if (this.xAxis == null) {
				MotorAxis addAxis = ((Axis)evt.getOldValue()).getMotorAxis();
				this.setXAxis(addAxis);
			}
			
			
		} else if (evt.getPropertyName().equals("removeChannel")) {
			System.out.println("\tproperty Change (remove Channel) von PlotWIndow");
			System.out.println("\tthis.yAxis: " + this.yAxis);

			System.out.println("\n\tDAS Programm kommt hier wirklich hin");
			
			DetectorChannel deletedChannel = ((Channel)evt.getOldValue()).getDetectorChannel();

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
						final Channel[] presentChannels = scanModule.getChannels();
						System.out.println("\tAnzahl der Channels: " + presentChannels.length);
						if (presentChannels.length == 1) {
							yAxis.setDetectorChannel(presentChannels[0].getDetectorChannel());
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
}