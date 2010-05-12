/*******************************************************************************
 * Copyright (c) 2001, 2008 Physikalisch Technische Bundesanstalt.
 * All rights reserved.
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package de.ptb.epics.eve.data.scandescription;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import de.ptb.epics.eve.data.measuringstation.PlugIn;
import de.ptb.epics.eve.data.measuringstation.PluginParameter;
import de.ptb.epics.eve.data.scandescription.errors.IModelError;
import de.ptb.epics.eve.data.scandescription.errors.IModelErrorProvider;
import de.ptb.epics.eve.data.scandescription.errors.PluginError;
import de.ptb.epics.eve.data.scandescription.errors.PluginErrorTypes;
import de.ptb.epics.eve.data.scandescription.updatenotification.IModelUpdateListener;
import de.ptb.epics.eve.data.scandescription.updatenotification.IModelUpdateProvider;
import de.ptb.epics.eve.data.scandescription.updatenotification.ModelUpdateEvent;

/**
 * This class represents a plug in controller.
 * 
 * A plug in controller take a plugin and save the corresponding parameter values.
 * 
 * 
 * @author Stephan Rehfeld <stephan.rehfeld (-at-) ptb.de>
 *
 */
public class PluginController implements IModelErrorProvider, IModelUpdateProvider {
	
	/**
	 * The plug in to control.
	 * 
	 */
	private PlugIn plugin;
	
	/**
	 * The values of the parameter of the plug in.
	 */
	private Map< String, String > values;
	
	/**
	 * The list of model update listener.
	 */
	private List< IModelUpdateListener > modelUpdateListener;
	
	/**
	 * This flag indicates if the values has been filled with default values. 
	 */
	private boolean defaultFlag;
	
	/**
	 * The parent scan module.
	 */
	private ScanModul scanModul;
	
	/**
	 * This constructor creates a new plug in controller.
	 */
	public PluginController() {
		this( null );
	}
	
	/**
	 * This constructor create a new plug in controller with a given plug in to control.
	 * 
	 * @param plugin The plug in to control.
	 */
	public PluginController( final PlugIn plugin ) {
		this.plugin = plugin;
		this.values = new HashMap< String, String >();
		this.modelUpdateListener = new ArrayList< IModelUpdateListener >();
		
		if( this.plugin != null ) {
			this.fillWithDefaults();
		}
	}
	
	/**
	 * This method returns the current plug in to control.
	 * 
	 * @return The current plug in that is controlled by this controller. Maybe 'null'. 
	 */
	public PlugIn getPlugin() {
		return this.plugin;
	}
	
	/**
	 * This methods sets the controlled plug in.
	 * 
	 * @param plugin The plug in to control.
	 */
	public void setPlugin( final PlugIn plugin ) {
		this.plugin = plugin;
		this.values.clear();
		if( this.plugin != null ) {
			this.fillWithDefaults();
		}
		final Iterator< IModelUpdateListener > it = this.modelUpdateListener.iterator();
		while( it.hasNext() ) {
			it.next().updateEvent( new ModelUpdateEvent( this, null ) );
		}
	}
	
	/**
	 * This method sets a value for a parameter of the plug in.
	 * 
	 * @param name The name of the parameter.
	 * @param value The value for the parameter.
	 */
	public void set( final String name, final String value ) {
		this.values.put( name, value );
		this.defaultFlag = false;
		Iterator< IModelUpdateListener > it = this.modelUpdateListener.iterator();
		while( it.hasNext() ) {
			it.next().updateEvent( new ModelUpdateEvent( this, null ) );
		}
	}
	
	/**
	 * This methods unsets a value.
	 * 
	 * @param name the name of the value to unset.
	 */
	public void unset( final String name ) {
		this.values.remove( name );
		Iterator< IModelUpdateListener > it = this.modelUpdateListener.iterator();
		while( it.hasNext() ) {
			it.next().updateEvent( new ModelUpdateEvent( this, null ) );
		}
	}
	
	/**
	 * This method returns a value of a parameter.
	 * 
	 * @param name The name of the parameter.
	 * @return The value of the parameter.
	 */
	public String get( final String name ) {
		return this.values.get( name );
	}

	/*
	 * (non-Javadoc)
	 * @see de.ptb.epics.eve.data.scandescription.updatenotification.IModelUpdateProvider#addModelUpdateListener(de.ptb.epics.eve.data.scandescription.updatenotification.IModelUpdateListener)
	 */
	public boolean addModelUpdateListener( final IModelUpdateListener modelUpdateListener ) {
		return this.modelUpdateListener.add( modelUpdateListener );
	}

	/*
	 * (non-Javadoc)
	 * @see de.ptb.epics.eve.data.scandescription.updatenotification.IModelUpdateProvider#removeModelUpdateListener(de.ptb.epics.eve.data.scandescription.updatenotification.IModelUpdateListener)
	 */
	public boolean removeModelUpdateListener( final IModelUpdateListener modelUpdateListener ) {
		return this.modelUpdateListener.remove( modelUpdateListener );
	}

	
	
	/**
	 * This method return if the values are currently filled with default values.
	 * 
	 * @return Returns 'true' if the values are currentyl filled with default values.
	 */
	public boolean isFilledWithDefault() {
		return this.defaultFlag;
	}
	
	/**
	 * This method fills the values with default values specified by the plug in.
	 */
	private void fillWithDefaults() {
		Iterator< PluginParameter > it = this.plugin.getParameters().iterator();
		while( it.hasNext() ) {
			final PluginParameter currentPluginParameter = it.next();
			// TODO um hier vernünftige Werte vorzuschlagen muß irgendwie
			// erkannt werden, welche Parameter diskrete Werte beinhalten
			// z.B. referenceaxis aus position Plugin.
			// kann man da einen Zusatz in das Messplatz.xml File schreiben
			// wie z.B. type=discrete?
			// es müßte über den datatype axisid erkannt werden!!!

			if (currentPluginParameter.getType().toString() .equals("AXISID")) {
				// aus dem scanModul wird der erste Wert des Plugins erzeugt!
				if ( scanModul != null) {
					Axis[] cur_axis = scanModul.getAxis();
					String[] cur_feld = new String[cur_axis.length];
					for (int i=0; i<cur_axis.length; ++i) {
						cur_feld[i] = cur_axis[i].getMotorAxis().getFullIdentifyer();
					}
					this.values.put( currentPluginParameter.getName(), cur_feld[0] );
				}
				else
				   this.values.put( currentPluginParameter.getName(), currentPluginParameter.getDefaultValue() );
			}
			else
			   this.values.put( currentPluginParameter.getName(), currentPluginParameter.getDefaultValue() );
		}
		this.defaultFlag = true;
	}
	
	/**
	 * This method returns an array of all elements of the plug in controller.
	 * 
	 * @return An array with all key/value pairs.
	 */
	@SuppressWarnings("unchecked")
	public Entry< String, String >[] getElements() {
		return this.values.entrySet().toArray( new Entry[0] );
	}
	
	@Override
	public String toString() {
		final StringBuffer stringBuffer = new StringBuffer();
		if( this.plugin != null ) {
			Iterator< PluginParameter > it = this.plugin.getParameters().iterator();
			while( it.hasNext() ) {
				final PluginParameter pp = it.next();
				stringBuffer.append( pp.getName() );
				stringBuffer.append( '=' );
				stringBuffer.append( this.values.get( pp.getName() ) );
				stringBuffer.append( "; " );

			}
		}
		return stringBuffer.toString();
	}

	/**
	 * This method return the scan module.
	 * 
	 * @return The scan module.
	 */
	public ScanModul getScanModul() {
		return this.scanModul;
	}

	/**
	 * This method sets the scan module.
	 * 
	 * @param scanModul The current scan module.
	 */
	public void setScanModul( final ScanModul scanModul ) {
		this.scanModul = scanModul;
	}

	/*
	 * (non-Javadoc)
	 * @see de.ptb.epics.eve.data.scandescription.errors.IModelErrorProvider#getModelErrors()
	 */
	@Override
	public List< IModelError > getModelErrors() {
		final List< IModelError > errorList = new ArrayList< IModelError >();
		if( this.plugin != null ) {
			final Iterator< PluginParameter > it = this.plugin.getParameters().iterator();
			while( it.hasNext() ) {
				final PluginParameter parameter = it.next();
				if( parameter.isMandatory() && !this.values.containsKey( parameter.getName() ) ) {
					errorList.add( new PluginError( this, PluginErrorTypes.MISSING_MANDATORY_PARAMETER, parameter.getName() ) );
				}
				if( this.values.containsKey( parameter.getName() ) && !parameter.isValuePossible( this.values.get( parameter.getName() ) ) ) {
					errorList.add( new PluginError( this, PluginErrorTypes.WRONG_VALUE, parameter.getName() ) );
				}
			}
		} else {
			errorList.add( new PluginError( this, PluginErrorTypes.PLUING_NOT_SET, "" ) );
		}
		return errorList;
	}
}
