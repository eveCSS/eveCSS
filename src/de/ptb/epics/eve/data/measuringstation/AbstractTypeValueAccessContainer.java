/*******************************************************************************
 * Copyright (c) 2001, 2007 Physikalisch Technische Bundesanstalt.
 * All rights reserved.
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package de.ptb.epics.eve.data.measuringstation;

import de.ptb.epics.eve.data.TypeValue;

/**
 * The AbstractTypeValueAccessContainer is the base class of all classes that contains a
 * TypeValue and a Access. This are currently Event, Goto and Trigger. All of the
 * classes have in common, that they are need a access. So the basic not null
 * checkings for the access is implemented in this class
 * 
 * @author Stephan Rehfeld <stephan.rehfeld( -at -) ptb.de>
 * @version 1.3
 * 
 * @see de.trustedcode.scanmoduleditor.data.TypeValue
 * @see de.ptb.epics.eve.data.measuringstation.Access
 * 
 * @see de.ptb.epics.eve.data.measuringstation.Event
 * @see de.ptb.epics.eve.data.measuringstation.Goto
 * @see de.ptb.epics.eve.data.measuringstation.Trigger
 * 
 */
public abstract class AbstractTypeValueAccessContainer {

	/**
	 * The TypeValue object of this AbstractTypeValueAccessContainer 
	 */
	private TypeValue dataType;
	
	/**
	 * The Access object of this AbstractTypevalueAccessContainer  
	 */
	private Access access;
	
	/**
	 * Inhereting classes should call this constructor to construct a new empty AbstractTypeValueAccessContainer.
	 */
	public AbstractTypeValueAccessContainer( final Access access ) {
		this( access, null );
	}
	
	/**
	 * Inhereting classes should call this constructor the constrcut a new AbstractTypeValueAccessContainer
	 * with specific values. 
	 * 
	 * @param pv A Access object. Must not be null.
	 * @param dataType A TypeValue object or null.
	 */
	public AbstractTypeValueAccessContainer( final Access access, final TypeValue dataType ) {
		if( access == null ) {
			throw new IllegalArgumentException( "The parameter 'access' must not be null" );
		}
		this.access = access;
		this.dataType = dataType;
	}
	
	/**
	 * Gives back the TypeValue object of this Container.
	 * 
	 * @return A TypeValue object or null.
	 */
	public TypeValue getDataType() {
		return this.dataType;
	}
	
	/**
	 * Gives back the Access object of this Container.
	 * 
	 * @return A Access object.
	 */
	public Access getAccess() {
		return this.access;
	}
	
	/**
	 * Sets the Access of this AbstractTypeValueAccessContainer.
	 * 
	 * @param access A Access object. Must not be null.
	 */
	public void setAccess( final Access access ) {
		if( access == null ) {
			throw new IllegalArgumentException( "The parameter 'access' must not be null" );
		}
		this.access = access;
	}
	
	/**
	 * Sets the TypeValue of this AbstractTypeValuePVContainer.
	 * 
	 * @param dataType A TypeValu object or null.
	 */
	public void setDataType( final TypeValue dataType ) {
		this.dataType = dataType;
	}

}
