/*******************************************************************************
 * Copyright (c) 2001, 2008 Physikalisch Technische Bundesanstalt.
 * All rights reserved.
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package de.ptb.epics.eve.data.scandescription;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import de.ptb.epics.eve.data.SaveAxisPositionsTypes;
import de.ptb.epics.eve.data.scandescription.errors.IModelError;
import de.ptb.epics.eve.data.scandescription.errors.IModelErrorProvider;
import de.ptb.epics.eve.data.scandescription.updatenotification.ControlEventManager;
import de.ptb.epics.eve.data.scandescription.updatenotification.ControlEventMessage;
import de.ptb.epics.eve.data.scandescription.updatenotification.ControlEventMessageEnum;
import de.ptb.epics.eve.data.scandescription.updatenotification.ControlEventTypes;
import de.ptb.epics.eve.data.scandescription.updatenotification.IModelUpdateListener;
import de.ptb.epics.eve.data.scandescription.updatenotification.IModelUpdateProvider;
import de.ptb.epics.eve.data.scandescription.updatenotification.ModelUpdateEvent;

/**
 * This class represents a scan module.
 * 
 * @author Stephan Rehfeld <stephan.rehfeld( -at -) ptb.de>
 * @version 1.2
 */
public class ScanModul implements IModelUpdateListener, IModelUpdateProvider, IModelErrorProvider {
	
	/**
	 * The id of the scan module
	 */
	private int id;
	
	/**
	 * The type of the scan module
	 */
	private String type;
	
	/**
	 * The name of the scan module
	 */
	private String name;
	
	/**
	 * The settle time
	 */
	private double settletime;
	
	/**
	 * The trigger delay
	 */
	private double triggerdelay;
	
	/**
	 * This field says if a trigger should be confirmed by hand.
	 */
	private boolean triggerconfirm;
	
	/**
	 * A list, containing all prescans.
	 */
	private List<Prescan> prescans;
	
	/**
	 * A list, containing all postscans
	 */
	private List<Postscan> postscans;
	
	/**
	 * A list, containing all channels that chould be read.
	 */
	private List<Channel> channels;
	
	/**
	 * A list, containing all axis commands.
	 */
	private List<Axis> axis;
	
	/**
	 * A list, containing all plot windows.
	 */
	private List<PlotWindow> plotWindows;
	
	/**
	 * The connector to the appended scan modul
	 */
	private Connector appended;
	
	/**
	 * The connector to the nested scan modul
	 */
	private Connector nested;
	
	/**
	 * The connector to the parent element.
	 */
	private Connector parent;
	
	/**
	 * A list of ControlEvents for the trigger events.
	 */
	private List< ControlEvent > triggerEvents;
	
	/**
	 * A list of ControlEvents for the redo events.
	 */
	private List< ControlEvent > redoEvents;
	
	/**
	 * A list of ControlEvents for the break events.
	 */
	private List< ControlEvent > breakEvents;
	
	/**
	 * A list of PauseEvents for the pause events.
	 */
	private List< PauseEvent > pauseEvents;
	
	/**
	 * The chain of the scan modul.
	 */
	private Chain chain;
	
	/**
	 * The x position of the scan modul in the graphical editor.
	 */
	private int x;
	
	/**
	 * The y position of the scan modul in the graphical editor.
	 */
	private int y;
	
	/**
	 * This enum contains the event when all motor axes gets saved.
	 */
	private SaveAxisPositionsTypes saveAxisPositions;
	
	/**
	 * The control event manager that controls the break events of this scan module.
	 */
	private ControlEventManager breakControlEventManager;
	
	/**
	 * The control event manager that controls the redo events of this scan module.
	 */
	private ControlEventManager redoControlEventManager;
	
	/**
	 * The control event manager that controls the trigger events of this scan module.
	 */
	private ControlEventManager triggerControlEventManager;
	
	/**
	 * The control event manager that controls the pause events of this scan module.
	 */
	private ControlEventManager pauseControlEventManager;
	
	/**
	 * A List that is holding all object that needs to get an update message if this object was updated.
	 */
	private List< IModelUpdateListener > updateListener;
		
	/**
	 * A list that holds all Positionings.
	 */
	private List< Positioning > positionings;
	
	/**
	 * This constructor constructs a new scan modul with the given id.
	 * 
	 * @param id
	 */
	public ScanModul( final int id ) {
		if( id < 1 ) {
			throw new IllegalArgumentException( "The parameter 'id' must be larger than 0!" );
		}
		this.id = id;
		this.prescans = new ArrayList<Prescan>();
		this.postscans = new ArrayList<Postscan>();
		this.channels = new ArrayList<Channel>();
		this.axis = new ArrayList<Axis>();
		this.plotWindows = new ArrayList<PlotWindow>();
		this.settletime = Double.NEGATIVE_INFINITY;
		this.triggerdelay = Double.NEGATIVE_INFINITY;
		this.triggerEvents = new ArrayList< ControlEvent >();
		this.redoEvents = new ArrayList< ControlEvent >();
		this.breakEvents = new ArrayList< ControlEvent >();
		this.pauseEvents = new ArrayList< PauseEvent >();
		this.type = "classic";
		this.name = "";
	
		this.saveAxisPositions = SaveAxisPositionsTypes.NEVER;
		
		this.breakControlEventManager = new ControlEventManager( this, this.breakEvents, ControlEventTypes.CONTROL_EVENT );
		this.redoControlEventManager = new ControlEventManager( this, this.redoEvents, ControlEventTypes.CONTROL_EVENT );
		this.pauseControlEventManager = new ControlEventManager( this, this.pauseEvents, ControlEventTypes.PAUSE_EVENT );
		this.triggerControlEventManager = new ControlEventManager( this, this.triggerEvents, ControlEventTypes.CONTROL_EVENT );
		
		this.breakControlEventManager.addModelUpdateListener( this );
		this.redoControlEventManager.addModelUpdateListener( this );
		this.pauseControlEventManager.addModelUpdateListener( this );
		this.triggerControlEventManager.addModelUpdateListener( this );
		
		this.updateListener = new ArrayList< IModelUpdateListener >();
		
		this.positionings = new ArrayList< Positioning >();
		
	}

	/**
	 * Gives back an Array that contains all Prescans.
	 * 
	 * @return An Array, that contains all Prescans.
	 */
	public Prescan[] getPrescans() {
		return this.prescans.toArray( new Prescan[0]);
	}
	
	/**
	 * Gives back an Array that contains all Postscans.
	 * 
	 * @return An Array, that contains all Postscans.
	 */
	public Postscan[] getPostscans() {
		return this.postscans.toArray( new Postscan[0] );
	}
	
	/**
	 * Gives back an Array that contains all channel behaviors.
	 * 
	 * @return An Array, that contains all channel behaviors.
	 */
	public Channel[] getChannels() {
		return this.channels.toArray( new Channel[0]);
	}
	
	/**
	 * Gives back an Array that contains all axis behaviors.
	 * 
	 * @return An Array, that contains all axos behaviors.
	 */
	public Axis[] getAxis() {
		return this.axis.toArray( new Axis[0] );
	}
	
	/**
	 * Gives back an Array that contains all plot windows.
	 * 
	 * @return An Array, that contains all plot windows.
	 */
	public PlotWindow[] getPlotWindows() {
		return this.plotWindows.toArray( new PlotWindow[0] );
	}
	
	/**
	 * This method returns an array of all positionings.
	 * 
	 * @return An array of all positionings.
	 */
	public Positioning[] getPositionings() {
		return this.positionings.toArray( new Positioning[0] );
	}
	
	/**
	 * Adds a prescan to the Scan Modul.
	 * 
	 * @param prescan The prescan that should be added to the Scan Modul.
	 */
	public void add( final Prescan prescan ) {
		this.prescans.add( prescan );
		prescan.addModelUpdateListener( this );
		final Iterator<IModelUpdateListener> updateIterator = this.updateListener.iterator();
		while( updateIterator.hasNext() ) {
			updateIterator.next().updateEvent( new ModelUpdateEvent( this, null ) );
		}
	}
	
	/**
	 * Adds a pstscan to the Scan Modul.
	 * 
	 * @param postscan The postscan that should be added to the Scan Modul.
	 */
	public void add( final Postscan postscan ) {
		this.postscans.add( postscan );
		postscan.addModelUpdateListener( this );
		final Iterator<IModelUpdateListener> updateIterator = this.updateListener.iterator();
		while( updateIterator.hasNext() ) {
			updateIterator.next().updateEvent( new ModelUpdateEvent( this, null ) );
		}
	}
	
	/**
	 * Adds a channel behavior to the Scan Modul.
	 * 
	 * @param channel The channel behavior that should be added to the Scan Modul.
	 */
	public void add( final Channel channel ) {
		this.channels.add( channel );
		channel.addModelUpdateListener( this );
		final Iterator<IModelUpdateListener> updateIterator = this.updateListener.iterator();
		while( updateIterator.hasNext() ) {
			updateIterator.next().updateEvent( new ModelUpdateEvent( this, null ) );
		}
	}
	
	/**
	 * Adds a axis behavior to the Scan Modul.
	 * 
	 * @param axis The axis behavior that should be added to the Scan Modul.
	 */
	public void add( final Axis axis ) {
		this.axis.add( axis );
		axis.addModelUpdateListener( this );
		final Iterator<IModelUpdateListener> updateIterator = this.updateListener.iterator();
		while( updateIterator.hasNext() ) {
			updateIterator.next().updateEvent( new ModelUpdateEvent( this, null ) );
		}
	}
	
	/**
	 * Adds a plot window to the Scan Modul.
	 * 
	 * @param plotWindow The plot window that should be added to the Scan Modul.
	 */
	public void add( final PlotWindow plotWindow ) {
		this.plotWindows.add( plotWindow );
		plotWindow.addModelUpdateListener( this );
		final Iterator<IModelUpdateListener> updateIterator = this.updateListener.iterator();
		while( updateIterator.hasNext() ) {
			updateIterator.next().updateEvent( new ModelUpdateEvent( this, null ) );
		}
	}
	
	/**
	 * This methods adds a positioning to a scan module.
	 * 
	 * @param positioning The positioning to add.
	 */
	public void add( final Positioning positioning ) {
		this.positionings.add( positioning );
		positioning.addModelUpdateListener( this );
		final Iterator<IModelUpdateListener> updateIterator = this.updateListener.iterator();
		while( updateIterator.hasNext() ) {
			updateIterator.next().updateEvent( new ModelUpdateEvent( this, null ) );
		}
	}
	
	/**
	 * Removes a prescan from the Scan Modul.
	 * 
	 * @param prescan The prescan that should be removed.
	 */
	public void remove( final Prescan prescan ) {
		this.prescans.remove( prescan );
		prescan.removeModelUpdateListener( this );
		final Iterator<IModelUpdateListener> updateIterator = this.updateListener.iterator();
		while( updateIterator.hasNext() ) {
			updateIterator.next().updateEvent( new ModelUpdateEvent( this, null ) );
		}
	}
	
	/**
	 * Removes a postscan from the Scan Modul.
	 * 
	 * @param postscan The postscan that should be removed.
	 */
	public void remove( final Postscan postscan ) {
		this.postscans.remove( postscan );
		postscan.removeModelUpdateListener( this );
		final Iterator<IModelUpdateListener> updateIterator = this.updateListener.iterator();
		while( updateIterator.hasNext() ) {
			updateIterator.next().updateEvent( new ModelUpdateEvent( this, null ) );
		}
	}
	
	/**
	 * Removes a channel behavior from the Scan Modul.
	 * 
	 * @param channel The channel behavior that should be removed.
	 */
	public void remove( final Channel channel ) {
		Positioning[] positionings = getPositionings();
		
		for( int i = 0; i < positionings.length; ++i ) {
			// Wenn positioning Channel oder Normalize = remove Channel, 
			// channel entfernen und Fehlermeldung ausgeben.
			if (channel.getDetectorChannel().equals(positionings[i].getDetectorChannel())) {
				// DetectorChannel gibt es nicht mehr.
				positionings[i].setDetectorChannel(null);
			}
			if (channel.getDetectorChannel().equals(positionings[i].getNormalization())) {
				positionings[i].setNormalization(null);
			}
		}

		// Wenn die Achse in einem Plot verwendet wird, muß die Achse entfernt werden
		for ( Iterator< PlotWindow > it = this.plotWindows.iterator(); it.hasNext(); ) {
			PlotWindow aktPlotWindow = it.next();

			YAxis wegAxis1 = null;
			YAxis wegAxis2 = null;
			for( Iterator< YAxis > ityAxis = aktPlotWindow.getYAxisIterator(); ityAxis.hasNext();) {
				YAxis yAxis = ityAxis.next();
				if (yAxis.getNormalizeChannel() != null) {
					if (yAxis.getNormalizeChannel().equals(channel.getDetectorChannel())) {
						yAxis.setNormalizeChannel(null);
					}
				}
				if (yAxis.getDetectorChannel() != null) {
					if (yAxis.getDetectorChannel().equals(channel.getDetectorChannel())) {
						yAxis.setDetectorChannel(null);
						if (wegAxis1 == null)
							wegAxis1 = yAxis;
						else
							wegAxis2 = yAxis;
					}
				}
			}
			if (wegAxis1 != null)
				aktPlotWindow.removeYAxis(wegAxis1);
			if (wegAxis2 != null)
				aktPlotWindow.removeYAxis(wegAxis2);
		}

		// falls es DetektorReadyEvents zu dem Channel gibt, werden diese entfernt
		if (channel.getDetectorReadyEvent() != null) {
			channel.getParentScanModul().getChain().getScanDescription().removeEventById( channel.getDetectorReadyEvent().getID() );
			channel.setDetectorReadyEvent(null);
		};

		this.channels.remove( channel );
		channel.removeModelUpdateListener( this );
		final Iterator<IModelUpdateListener> updateIterator = this.updateListener.iterator();
		while( updateIterator.hasNext() ) {
			updateIterator.next().updateEvent( new ModelUpdateEvent( this, null ) );
		}
	}
	
	/**
	 * Removes a axis behavior from the Scan Modul.
	 * 
	 * @param axis The axis behavior that should be removed.
	 */
	public void remove( final Axis axis ) {
		// wenn es für die Achse ein Positioning gibt, muß es entfernt werden
		Positioning[] positionings = getPositionings();

		for( int i = 0; i < positionings.length; ++i ) {
			// Wenn positioning Achse = remove Achse, Positioning entfernen
			if (axis.getMotorAxis().equals(positionings[i].getMotorAxis())) {
				remove(positionings[i]);
			}
		}

		// Wenn die Achse in einem Plot verwendet wird, muß die Achse entfernt werden
		for ( Iterator< PlotWindow > it = this.plotWindows.iterator(); it.hasNext(); ) {
			PlotWindow aktPlotWindow = it.next();
			if (aktPlotWindow.getXAxis() != null) {
				// nur wenn die aktuelle Anzeige der X-Achse nicht leer ist,
				// wird ein neuer Wert gesetzt
				if (aktPlotWindow.getXAxis().equals(axis.getMotorAxis())) {
					aktPlotWindow.setXAxis(null);
				}
			}
		}
		this.axis.remove( axis );
		axis.removeModelUpdateListener( this );
		final Iterator<IModelUpdateListener> updateIterator = this.updateListener.iterator();
		while( updateIterator.hasNext() ) {
			updateIterator.next().updateEvent( new ModelUpdateEvent( this, null ) );
		}
	}
	
	/**
	 * Removes a plot window behavior from the Scan Modul.
	 * 
	 * @param plotWindow The plot window that should be removed.
	 */
	public void remove( final PlotWindow plotWindow ) {
		this.plotWindows.remove( plotWindow );
		plotWindow.removeModelUpdateListener( this );
		final Iterator<IModelUpdateListener> updateIterator = this.updateListener.iterator();
		while( updateIterator.hasNext() ) {
			updateIterator.next().updateEvent( new ModelUpdateEvent( this, null ) );
		}
	}
	
	/**
	 * This method removes a positioning from the scan module.
	 * 
	 * @param positioning The positioning to remove.
	 */
	public void remove( final Positioning positioning ) {
		this.positionings.remove( positioning );
		positioning.removeModelUpdateListener( this );
		final Iterator<IModelUpdateListener> updateIterator = this.updateListener.iterator();
		while( updateIterator.hasNext() ) {
			updateIterator.next().updateEvent( new ModelUpdateEvent( this, null ) );
		}
	}
	
	/**
	 * Gives back the connector, that brings you to the appended scan modul.
	 * 
	 * @return The connector to the appended scan modul or null if it's not setted.
	 */
	public Connector getAppended() {
		return appended;
	}


	/**
	 * Sets the connector, that brings you to the appended scan modul.
	 * 
	 * @param appended The connector that brings you to the appended scan modul.
	 */
	public void setAppended( final Connector appended ) {
		this.appended = appended;
		final Iterator<IModelUpdateListener> updateIterator = this.updateListener.iterator();
		while( updateIterator.hasNext() ) {
			updateIterator.next().updateEvent( new ModelUpdateEvent( this, null ) );
		}
	}

	/**
	 * Gives back the id of the scan modul.
	 * 
	 * @return The id of the scan modul.
	 */
	public int getId() {
		return id;
	}

	/**
	 * Sets the id of the scan modul.
	 * 
	 * @param id The new id of the scan modul.
	 */
	public void setId( final int id) {
		if( id < 1 ) {
			throw new IllegalArgumentException( "The parameter 'id' must be larger than 0!" );
		}
		this.id = id;
		final Iterator<IModelUpdateListener> updateIterator = this.updateListener.iterator();
		while( updateIterator.hasNext() ) {
			updateIterator.next().updateEvent( new ModelUpdateEvent( this, null ) );
		}
	}

	/**
	 * Gives back the name of the scan modul.
	 * 
	 * @return The name of the scan modul.
	 */
	public String getName() {
		return name;
	}

	/**
	 * Sets the name of the scan module.
	 * 
	 * @param name The name of the scan modul. Must not be null!
	 */
	public void setName( final String name ) {
		this.name = name;
		final Iterator<IModelUpdateListener> updateIterator = this.updateListener.iterator();
		while( updateIterator.hasNext() ) {
			updateIterator.next().updateEvent( new ModelUpdateEvent( this, null ) );
		}
	}

	/**
	 * Gives back the Connector that brings you to the nested scan module.
	 * 
	 * @return The connector to the nested scan module.
	 */
	public Connector getNested() {
		return nested;
	}

	/**
	 * Sets the Connector to the nested scan module.
	 * 
	 * @param nested The Connector to the nested scan module.
	 */
	public void setNested( final Connector nested ) {
		this.nested = nested;
	}

	/**
	 * Gives back the Connector that brings you to the parent element.
	 * 
	 * @return The Connector to the parent Element.
	 */
	public Connector getParent() {
		return parent;
	}

	/**
	 * Sets the Connector to the parent element.
	 * 
	 * @param parent The Connector to the parent element.
	 */
	public void setParent( final Connector parent ) {
		this.parent = parent;
	}



	/**
	 * Gives back the settle time of the scan module.
	 * 
	 * @return The settle time.
	 */
	public double getSettletime() {
		return settletime;
	}

	/**
	 * Sets the settle time.
	 * 
	 * @param settletime The settle time.
	 */
	public void setSettletime( final double settletime ) {
		this.settletime = settletime;
		final Iterator<IModelUpdateListener> updateIterator = this.updateListener.iterator();
		while( updateIterator.hasNext() ) {
			updateIterator.next().updateEvent( new ModelUpdateEvent( this, null ) );
		}
	}

	/**
	 * Gives back if trigger have to be confirmed by hand.
	 * 
	 * @return 
	 */
	public boolean isTriggerconfirm() {
		return triggerconfirm;
	}

	/**
	 * Sets if trigger have to be confirmed by hand.
	 * 
	 * @param triggerconfirm
	 */
	public void setTriggerconfirm( final boolean triggerconfirm ) {
		this.triggerconfirm = triggerconfirm;
		final Iterator<IModelUpdateListener> updateIterator = this.updateListener.iterator();
		while( updateIterator.hasNext() ) {
			updateIterator.next().updateEvent( new ModelUpdateEvent( this, null ) );
		}
	}

	/**
	 * Gives back the trigger delay.
	 * 
	 * @return The trigger delay
	 */
	public double getTriggerdelay() {
		return triggerdelay;
	}

	/**
	 * Sets the trigger delay
	 * 
	 * @param triggerdelay The trigger delay
	 */
	public void setTriggerdelay( final double triggerdelay ) {
		this.triggerdelay = triggerdelay;
		final Iterator<IModelUpdateListener> updateIterator = this.updateListener.iterator();
		while( updateIterator.hasNext() ) {
			updateIterator.next().updateEvent( new ModelUpdateEvent( this, null ) );
		}
	}


	/**
	 * Gives back the type of the scan modul.
	 * 
	 * @return The type of the scan modul.
	 */
	public String getType() {
		return type;
	}

	/**
	 * Sets the type of the scan modul.
	 * 
	 * @param type The type of the scan modul.
	 */
	public void setType( final String type ) {
		this.type = type;
		final Iterator<IModelUpdateListener> updateIterator = this.updateListener.iterator();
		while( updateIterator.hasNext() ) {
			updateIterator.next().updateEvent( new ModelUpdateEvent( this, null ) );
		}
	}

	/**
	 * Gives back the Chain, where this scan modul is in.
	 * 
	 * @return The Chain where the scan modul is in or null if it is in no chain.
	 */
	public Chain getChain() {
		return chain;
	}

	/**
	 * Sets the chain, where this scan modul is in. This method gets called by the add and remove method of Chain.
	 * 
	 * @param chain The Chain where the scan modul is in.
	 */
	protected void setChain( final Chain chain) {
		this.chain = chain;
	}

	/**
	 * Gives back the x-position of the scan modul in the graphic diagramm.
	 * 
	 * @return The x-position of the scan modul in the graphic diagram.
	 */
	public int getX() {
		return x;
	}

	/**
	 * Sets the x-position in the graphical diagramm.
	 * 
	 * @param x The x-position in the graphical diagramm.
	 */
	public void setX( final int x) {
		this.x = x;
		final Iterator<IModelUpdateListener> updateIterator = this.updateListener.iterator();
		while( updateIterator.hasNext() ) {
			updateIterator.next().updateEvent( new ModelUpdateEvent( this, null ) );
		}
	}

	/**
	 * Gives back the y-position in the graphical diagram.
	 * 
	 * @return The y-position in the graphical diagram.
	 */
	public int getY() {
		return y;
	}

	/**
	 * Sets the y-position in the graphical diagram
	 * 
	 * @param y The y-position in the graphical diagram.
	 */
	public void setY( final int y) {
		this.y = y;
		final Iterator<IModelUpdateListener> updateIterator = this.updateListener.iterator();
		while( updateIterator.hasNext() ) {
			updateIterator.next().updateEvent( new ModelUpdateEvent( this, null ) );
		}
	}
	
	
	
	/**
	 * Adds a pause event to the scan modul.
	 * 
	 * @param pauseEvent The pause event that should be added to the scan modul.
	 * @return Gives back 'true' if the event has been added and false if not.
	 */
	public boolean addPauseEvent( final PauseEvent pauseEvent ) {
		if( this.pauseEvents.add( pauseEvent ) ) {
			pauseEvent.addModelUpdateListener( this.pauseControlEventManager );
			this.pauseControlEventManager.updateEvent( new ModelUpdateEvent( this, new ControlEventMessage( pauseEvent, ControlEventMessageEnum.ADDED ) ) );
			final Iterator<IModelUpdateListener> updateIterator = this.updateListener.iterator();
			while( updateIterator.hasNext() ) {
				updateIterator.next().updateEvent( new ModelUpdateEvent( this, new ControlEventMessage( pauseEvent, ControlEventMessageEnum.ADDED ) ) );
			}
			return true;
		} 
		return false;
	}
	
	/**
	 * Removes a pause event from the scan modul.
	 * 
	 * @param pauseEvent The pause event that should be removed from the scan modul.
	 * @return Gives back 'true' if the event has been removed and false if not.
	 */
	public boolean removePauseEvent( final PauseEvent pauseEvent ) {
		if( this.pauseEvents.remove( pauseEvent ) ) {
			pauseEvent.removeModelUpdateListener( this.pauseControlEventManager );
			this.pauseControlEventManager.updateEvent( new ModelUpdateEvent( this, new ControlEventMessage( pauseEvent, ControlEventMessageEnum.REMOVED ) ) );
			final Iterator<IModelUpdateListener> updateIterator = this.updateListener.iterator();
			while( updateIterator.hasNext() ) {
				updateIterator.next().updateEvent( new ModelUpdateEvent( this, new ControlEventMessage( pauseEvent, ControlEventMessageEnum.ADDED ) ) );
			}
			return true;
		} 
		return false;
	}
	
	/**
	 * Adds a break event to the scan modul.
	 * 
	 * @param breakEvent The break event that should be added to the scan modul.
	 * @return Gives back 'true' if the event has been added and false if not.
	 */
	public boolean addBreakEvent( final ControlEvent breakEvent ) {
		if( this.breakEvents.add( breakEvent ) ) {
			final Iterator<IModelUpdateListener> updateIterator = this.updateListener.iterator();
			while( updateIterator.hasNext() ) {
				updateIterator.next().updateEvent( new ModelUpdateEvent( this, null ) );
			}
			breakEvent.addModelUpdateListener( this.breakControlEventManager );
			this.breakControlEventManager.updateEvent( new ModelUpdateEvent( this, new ControlEventMessage( breakEvent, ControlEventMessageEnum.ADDED ) ) );
			return true;
		} 
		return false;
	}
	
	/**
	 * Removes a break event from the scan modul.
	 * 
	 * @param breakEvent The break event that should be removed from the scan modul.
	 * @return Gives back 'true' if the event has been removed and false if not.
	 */
	public boolean removeBreakEvent( final ControlEvent breakEvent ) {
		if( this.breakEvents.remove( breakEvent ) ) {
			final Iterator<IModelUpdateListener> updateIterator = this.updateListener.iterator();
			while( updateIterator.hasNext() ) {
				updateIterator.next().updateEvent( new ModelUpdateEvent( this, null ) );
			}
			breakEvent.removeModelUpdateListener( this.breakControlEventManager );
			this.breakControlEventManager.updateEvent( new ModelUpdateEvent( this, new ControlEventMessage( breakEvent, ControlEventMessageEnum.REMOVED ) ) );
			return true;
		} 
		return false;
	}
	
	/**
	 * Adds a redo event to the scan modul.
	 * 
	 * @param redoEvent The redo event that should be added to the scan modul.
	 * @return Gives back 'true' if the event has been added and false if not.
	 */
	public boolean addRedoEvent( final ControlEvent redoEvent ) {
		if( this.redoEvents.add( redoEvent ) ) {
			final Iterator<IModelUpdateListener> updateIterator = this.updateListener.iterator();
			while( updateIterator.hasNext() ) {
				updateIterator.next().updateEvent( new ModelUpdateEvent( this, null ) );
			}
			this.redoControlEventManager.updateEvent( new ModelUpdateEvent( this, new ControlEventMessage( redoEvent, ControlEventMessageEnum.ADDED ) ) );
			redoEvent.addModelUpdateListener( this.redoControlEventManager );
			return true;
		} 
		return false;
	}
	
	/**
	 * Removes a redo event from the scan modul.
	 * 
	 * @param redoEvent The redo event that should be removed from the scan modul.
	 * @return Gives back 'true' if the event has been removed and false if not.
	 */
	public boolean removeRedoEvent( final ControlEvent redoEvent ) {
		if( this.redoEvents.remove( redoEvent ) ) {
			final Iterator<IModelUpdateListener> updateIterator = this.updateListener.iterator();
			while( updateIterator.hasNext() ) {
				updateIterator.next().updateEvent( new ModelUpdateEvent( this, null ) );
			}
			this.redoControlEventManager.updateEvent( new ModelUpdateEvent( this, new ControlEventMessage( redoEvent, ControlEventMessageEnum.REMOVED ) ) );
			redoEvent.removeModelUpdateListener( this.redoControlEventManager );
			return true;
		} 
		return false;
	}
	
	
	
	
	
	
	
	/**
	 * Adds a trigger event to the scan modul.
	 * 
	 * @param triggerEvent The trigger event that should be added to the scan modul.
	 * @return Gives back 'true' if the event has been added and false if not.
	 */
	public boolean addTriggerEvent( final ControlEvent triggerEvent ) {
		if( this.triggerEvents.add( triggerEvent ) ) {
			final Iterator<IModelUpdateListener> updateIterator = this.updateListener.iterator();
			while( updateIterator.hasNext() ) {
				updateIterator.next().updateEvent( new ModelUpdateEvent( this, null ) );
			}
			this.triggerControlEventManager.updateEvent( new ModelUpdateEvent( this, new ControlEventMessage( triggerEvent, ControlEventMessageEnum.ADDED ) ) );
			triggerEvent.addModelUpdateListener( this.triggerControlEventManager );
			return true;
		} 
		return false;
	}
	
	/**
	 * Removes a trigger event from the scan modul.
	 * 
	 * @param triggerEvent The trigger event that should be removed from the scan modul.
	 * @return Gives back 'true' if the event has been removed and false if not.
	 */
	public boolean removeTriggerEvent( final ControlEvent triggerEvent ) {
		if( this.triggerEvents.remove( triggerEvent ) ) {
			final Iterator<IModelUpdateListener> updateIterator = this.updateListener.iterator();
			while( updateIterator.hasNext() ) {
				updateIterator.next().updateEvent( new ModelUpdateEvent( this, null ) );
			}
			this.triggerControlEventManager.updateEvent( new ModelUpdateEvent( this, new ControlEventMessage( triggerEvent, ControlEventMessageEnum.REMOVED ) ) );
			triggerEvent.removeModelUpdateListener( this.triggerControlEventManager );
			return true;
		} 
		return false;
	}
	
	/**
	 * Gives back if the pause event is one of the pause events of this scan modul.
	 * 
	 * @param controlEvent The pause Event that should be checked.
	 * @return Gives back 'true' if the event is one of the pause events.
	 */
	public boolean isPauseEventOfTheScanModul( final PauseEvent controlEvent ) {
		return this.pauseEvents.contains( controlEvent );
	}
	
	/**
	 * Gives back if the control event is one of the redo events of this scan modul.
	 * 
	 * @param redoEvent The control Event that should be checked.
	 * @return Gives back 'true' if the event is one of the redo events.
	 */
	public boolean isRedoEventOfTheScanModul( final ControlEvent redoEvent ) {
		return this.redoEvents.contains( redoEvent );
	}
	
	/**
	 * Gives back if the control event is one of the break events of this scan modul.
	 * 
	 * @param breakEvent The control Event that should be checked.
	 * @return Gives back 'true' if the event is one of the redo events.
	 */
	public boolean isBreakEventOfTheScanModul( final ControlEvent breakEvent ) {
		return this.breakEvents.contains( breakEvent );
	}
	
	/**
	 * Gives back if the control event is one of the trigger events of this scan modul.
	 * 
	 * @param triggerEvent The control Event that should be checked.
	 * @return Gives back 'true' if the event is one of the redo events.
	 */
	public boolean isTriggerEventOfTheScanModul( final ControlEvent triggerEvent ) {
		return this.triggerEvents.contains( triggerEvent );
	}

	
	
	/**
	 * This methods checks if the given control event is a pause event, redo event or break event of this scan modul.
	 * 
	 * @param controlEvent The control Event that should be checked.
	 * @return Gives back 'true' if the event is one of the event types of this chain.
	 */
	public boolean isAEventOfTheScanModul( final ControlEvent controlEvent ) {
		return ( controlEvent instanceof PauseEvent && this.isPauseEventOfTheScanModul( (PauseEvent)controlEvent ) ) || this.isBreakEventOfTheScanModul( controlEvent ) || this.isRedoEventOfTheScanModul( controlEvent ) || this.isTriggerEventOfTheScanModul( controlEvent );
	}
	
	/**
	 * This method returns an iterator over all pause events.
	 * 
	 * @return An iterator over all pause events.
	 */
	public Iterator< PauseEvent > getPauseEventsIterator() {
		return this.pauseEvents.iterator();
	}
	
	/**
	 * This method returns an iterator over all break events.
	 * 
	 * @return An iterator over all break events.
	 */
	public Iterator< ControlEvent > getBreakEventsIterator() {
		return this.breakEvents.iterator();
	}
	
	/**
	 * This method returns an iterator over all redo events.
	 * 
	 * @return An iterator over all redo events.
	 */
	public Iterator< ControlEvent > getRedoEventsIterator() {
		return this.redoEvents.iterator();
	}
	/**
	 * This method returns an iterator over all trigger events.
	 * 
	 * @return An iterator over all trigger events.
	 */
	public Iterator< ControlEvent > getTriggerEventsIterator() {
		return this.triggerEvents.iterator();
	}

	/**
	 * This method returns the control event manager of the break events.
	 * 
	 * @return The control event manager of the break events.
	 */
	public ControlEventManager getBreakControlEventManager() {
		return breakControlEventManager;
	}

	/**
	 * This method returns the control event manager of the pause events.
	 * 
	 * @return The control event manager of the pause events.
	 */
	public ControlEventManager getPauseControlEventManager() {
		return pauseControlEventManager;
	}

	/**
	 * This method returns the control event manager of the redo events.
	 * 
	 * @return The control event manager of the redo events.
	 */
	public ControlEventManager getRedoControlEventManager() {
		return redoControlEventManager;
	}
	
	/**
	 * This method returns the control event manager of the trigger events.
	 * 
	 * @return The control event manager of the trigger events.
	 */
	public ControlEventManager getTriggerControlEventManager() {
		return triggerControlEventManager;
	}

	/*
	 * (non-Javadoc)
	 * @see de.ptb.epics.eve.data.scandescription.updatenotification.IModelUpdateListener#updateEvent(de.ptb.epics.eve.data.scandescription.updatenotification.ModelUpdateEvent)
	 */
	public void updateEvent( final ModelUpdateEvent modelUpdateEvent ) {
		final Iterator< IModelUpdateListener > it = this.updateListener.iterator();
		while( it.hasNext() ) {
			it.next().updateEvent( new ModelUpdateEvent( this, modelUpdateEvent ) );
		}
		
	}

	/*
	 * (non-Javadoc)
	 * @see de.ptb.epics.eve.data.scandescription.updatenotification.IModelUpdateProvider#addModelUpdateListener(de.ptb.epics.eve.data.scandescription.updatenotification.IModelUpdateListener)
	 */
	public boolean addModelUpdateListener( final IModelUpdateListener modelUpdateListener ) {
		return this.updateListener.add( modelUpdateListener );
	}

	/*
	 * (non-Javadoc)
	 * @see de.ptb.epics.eve.data.scandescription.updatenotification.IModelUpdateProvider#removeModelUpdateListener(de.ptb.epics.eve.data.scandescription.updatenotification.IModelUpdateListener)
	 */
	public boolean removeModelUpdateListener( final IModelUpdateListener modelUpdateListener ) {
		return this.updateListener.remove( modelUpdateListener );
	}

	
	/**
	 * This method returns on which events all axis positions are saved.
	 * 
	 * @return A value of the correpondering enum.
	 */
	public SaveAxisPositionsTypes getSaveAxisPositions() {
		return saveAxisPositions;
	}

	/**
	 * This method sets on which events all axis positons are saved.
	 * 
	 * @param saveAxisPositions A value of the correponderung enum.
	 */
	public void setSaveAxisPositions( final SaveAxisPositionsTypes saveAxisPositions ) {
		this.saveAxisPositions = saveAxisPositions;
	}

	@Override
	public List< IModelError> getModelErrors() {
		final List< IModelError > errorList = new ArrayList< IModelError >();
		errorList.addAll( this.pauseControlEventManager.getModelErrors() );
		errorList.addAll( this.breakControlEventManager.getModelErrors() );
		errorList.addAll( this.redoControlEventManager.getModelErrors() );
		errorList.addAll( this.triggerControlEventManager.getModelErrors() );
		
		final Iterator< Axis > axisIterator = this.axis.iterator();
		while( axisIterator.hasNext() ) {
			errorList.addAll( axisIterator.next().getModelErrors() );
		}
		
		final Iterator< Channel > channelIterator = this.channels.iterator();
		while( channelIterator.hasNext() ) {
			errorList.addAll( channelIterator.next().getModelErrors() );
		}
		
		final Iterator< Prescan > prescanIterator = this.prescans.iterator();
		while( prescanIterator.hasNext() ) {
			errorList.addAll( prescanIterator.next().getModelErrors() );
		}
		
		final Iterator< Postscan > postscanIterator = this.postscans.iterator();
		while( postscanIterator.hasNext() ) {
			errorList.addAll( postscanIterator.next().getModelErrors() );
		}
		
		final Iterator< Positioning > positioningIterator = this.positionings.iterator();
		while( positioningIterator.hasNext() ) {
			errorList.addAll( positioningIterator.next().getModelErrors() );
		}

		final Iterator< PlotWindow > plotWindowIterator = this.plotWindows.iterator();
		while( plotWindowIterator.hasNext() ) {
			errorList.addAll( plotWindowIterator.next().getModelErrors() );
		}

		return errorList;
	}
	
	
}
