package de.ptb.epics.eve.data.scandescription;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlValue;

import de.ptb.epics.eve.data.ComparisonTypes;
import de.ptb.epics.eve.data.DataTypes;
import de.ptb.epics.eve.data.scandescription.updatenotification.IModelUpdateListener;
import de.ptb.epics.eve.data.scandescription.updatenotification.IModelUpdateProvider;
import de.ptb.epics.eve.data.scandescription.updatenotification.ModelUpdateEvent;

/**
 * A Limit is used to specify something.
 * 
 * @author Stephan Rehfeld <stephan.rehfeld( -at -) ptb.de>
 * @author Marcus Michalsky
 */
@XmlType
public class Limit implements IModelUpdateProvider {
	private DataTypes type;
	private ComparisonTypes comparison;
	private String value;

	private List<IModelUpdateListener> modelUpdateListener;
	
	/**
	 * Constructs a <code>Limit</code> with the data type STRING an the 
	 * operator EQ.
	 *
	 */
	public Limit() {
		this(DataTypes.STRING, ComparisonTypes.EQ);
	}
	
	/**
	 * Constructs a <code>Limit</code> with the given data type and operator.
	 *  
	 * @param type the data type of the comparison.
	 * @param comparison the operator of the limit.
	 * @throws IllegalArgumentException if at least one argument is 
	 * 		   <code>null</code>.
	 */
	public Limit(final DataTypes type, final ComparisonTypes comparison) {
		if(type == null) {
			throw new IllegalArgumentException(
					"The parameter 'type' must not be null!");
		}
		if(comparison == null) {
			throw new IllegalArgumentException(
					"The paremeter 'comparison' must not be null!");
		}
		this.type = type;
		this.comparison = comparison;
		this.value = "";
		this.modelUpdateListener = new ArrayList<IModelUpdateListener>();
	}
	
	/**
	 * Copy Constructor.
	 * 
	 * @param limit the limit to be copied
	 * @return a copy of the given limit.
	 * @author Marcus Michalsky
	 * @since 1.19
	 */
	public static Limit newInstance(Limit limit) {
		Limit newLimit = new Limit();
		newLimit.setType(limit.getType());
		newLimit.setComparison(limit.getComparison());
		newLimit.setValue(limit.getValue());
		return newLimit;
	}
	
	/**
	 * Returns the operator.
	 * 
	 * @return the operator of the comparison.
	 */
	public ComparisonTypes getComparison() {
		return this.comparison;
	}
	
	/**
	 * Sets the operator of the comparison.
	 * 
	 * @param comparison the operator of the comparison
	 * @throws IllegalArgumentException if the argument is <code>null</code>
	 */
	@XmlAttribute(name = "comparison")
	public void setComparison(final ComparisonTypes comparison) {
		if(comparison == null) {
			throw new IllegalArgumentException(
					"The paremeter 'comparison' must not be null!");
		}
		this.comparison = comparison;
		updateListeners();
	}
	
	/**
	 * Returns the data type of the value.
	 * 
	 * @return the data type of the value.
	 */
	public DataTypes getType() {
		return this.type;
	}
	
	/**
	 * Sets the data type of the value.
	 * 
	 * @param type the data type of the value.
	 * @throws IllegalArgumentException if the argument is <code>null</code>.
	 */
	@XmlAttribute(name = "type")
	public void setType(final DataTypes type) {
		if(type == null) {
			throw new IllegalArgumentException(
					"The parameter 'type' must not be null!");
		}
		this.type = type;
		updateListeners();
	}
	
	/**
	 * Returns the value of this limit.
	 * 
	 * @return the value of this limit.
	 */
	public String getValue() {
		return this.value;
	}
	
	/**
	 * Sets the value of the limit.
	 * 
	 * @param value the value of the Limit.
	 * @throws IllegalArgumentException if the argument is <code>null</code>.
	 */
	@XmlValue
	public void setValue(final String value) {
		if(value == null) {
			throw new IllegalArgumentException(
					"The parameter 'value' must not be null!");
		}
		this.value = value;
		updateListeners();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (other == null) {
			return false;
		}
		if (other.getClass() != getClass()) {
			return false;
		}
		if (!this.type.equals(((Limit)other).getType())) {
			return false;
		}
		if (!this.comparison.equals(((Limit)other).getComparison())) {
			return false;
		}
		if (!this.value.equals(((Limit)other).getValue())) {
			return false;
		}
		return true;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public int hashCode() {
		int result = type.hashCode();
		result = 31 * result + comparison.hashCode();
		result = 31 * result + value.hashCode();
		return result;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean addModelUpdateListener(
			final IModelUpdateListener modelUpdateListener) {
		return this.modelUpdateListener.add(modelUpdateListener);
	}

	/**
	 * {@inheritDoc} 
	 */
	@Override
	public boolean removeModelUpdateListener(
			final IModelUpdateListener modelUpdateListener) {
		return this.modelUpdateListener.remove(modelUpdateListener);
	}
	
	/*
	 * 
	 */
	private void updateListeners()
	{
		final CopyOnWriteArrayList<IModelUpdateListener> list = 
			new CopyOnWriteArrayList<IModelUpdateListener>(this.modelUpdateListener);
		
		Iterator<IModelUpdateListener> it = list.iterator();
		
		while(it.hasNext()) {
			it.next().updateEvent(new ModelUpdateEvent(this, null));
		}
	}
}