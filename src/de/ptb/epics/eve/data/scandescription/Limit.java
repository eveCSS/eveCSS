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

import de.ptb.epics.eve.data.ComparisonTypes;
import de.ptb.epics.eve.data.DataTypes;
import de.ptb.epics.eve.data.scandescription.updatenotification.IModelUpdateListener;
import de.ptb.epics.eve.data.scandescription.updatenotification.IModelUpdateProvider;
import de.ptb.epics.eve.data.scandescription.updatenotification.ModelUpdateEvent;

/**
 * A Limit is used to specify something.
 * 
 * @author Stephan Rehfeld <stephan.rehfeld( -at -) ptb.de>
 * @version 1.2
 */
public class Limit implements IModelUpdateProvider {

	/**
	 * The data type for the value.
	 */
	private DataTypes type;
	
	/**
	 * The comparsion type or operator.
	 */
	private ComparisonTypes comparison;
	
	/**
	 * The value on the right side.
	 */
	private String value;

	/**
	 * A List of all Listeners that will be notfied on an Update of this object.
	 */
	private List< IModelUpdateListener > modelUpdateListener;
	
	/**
	 * Use this constructor to construct a new Limit value with the data type STRING an the operator EQ.
	 *
	 */
	public Limit() {
		this( DataTypes.STRING, ComparisonTypes.EQ );
	}
	
	/**
	 * This constructor constructs a new Limit with the given data type and operator.
	 *  
	 * @param type The data type of the comparison. Must not be null!
	 * @param comparison The operator of the limit. Must not be null!
	 */
	public Limit( final DataTypes type, final ComparisonTypes comparison ) {
		if( type == null ) {
			throw new IllegalArgumentException( "The parameter 'type' must not be null!" );
		}
		if( comparison == null ) {
			throw new IllegalArgumentException( "The paremeter 'comparison' must not be null!" );
		}
		this.type = type;
		this.comparison = comparison;
		this.value = "";
		this.modelUpdateListener = new ArrayList<IModelUpdateListener>();
	}
	
	/**
	 * Gives back the operator.
	 * 
	 * @return The operator of the comparison.
	 */
	public ComparisonTypes getComparison() {
		return this.comparison;
	}
	
	/**
	 * Sets the operator of the comparion.
	 * 
	 * @param comparison The operator of the comparison. Must not be null!
	 */
	public void setComparison( final ComparisonTypes comparison ) {
		if( comparison == null ) {
			throw new IllegalArgumentException( "The paremeter 'comparison' must not be null!" );
		}
		this.comparison = comparison;
		Iterator< IModelUpdateListener > it = this.modelUpdateListener.iterator();
		while( it.hasNext() ) {
			it.next().updateEvent( new ModelUpdateEvent( this, null ) );
		}
	}
	
	/**
	 * Gives back the data type of the value.
	 * 
	 * @return The data type of the value.
	 */
	public DataTypes getType() {
		return this.type;
	}
	
	/**
	 * Sets the data type of the value.
	 * 
	 * @param type The data type of the value. Must not be null!
	 */
	public void setType( final DataTypes type ) {
		if( type == null ) {
			throw new IllegalArgumentException( "The parameter 'type' must not be null!" );
		}
		this.type = type;
		Iterator< IModelUpdateListener > it = this.modelUpdateListener.iterator();
		while( it.hasNext() ) {
			it.next().updateEvent( new ModelUpdateEvent( this, null ) );
		}
	}
	
	/**
	 * Gives back the value of this limit.
	 * 
	 * @return The value of this limit. Never returns null!
	 */
	public String getValue() {
		return this.value;
	}
	
	/**
	 * Sets the value of this limit.
	 * 
	 * @param value The value of this Limit. Must not be null.
	 */
	public void setValue( final String value) {
		if( value == null ) {
			throw new IllegalArgumentException( "The parameter 'value' must not be null!" );
		}
		this.value = value;
		Iterator< IModelUpdateListener > it = this.modelUpdateListener.iterator();
		while( it.hasNext() ) {
			it.next().updateEvent( new ModelUpdateEvent( this, null ) );
		}
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
	
}
