package de.ptb.epics.eve.data.measuringstation;

import java.util.List;

import de.ptb.epics.eve.data.DataTypes;
import de.ptb.epics.eve.data.MethodTypes;
import de.ptb.epics.eve.data.TypeValue;

/**
 * A Function is a complex description that can extend the description of a Access with discrepte values or range values.
 * 
 * @author Stephan Rehfeld <stephan.rehfeld( -at- )ptb.de>
 *
 */
public class Function {

	/**
	 * The Access of the function.
	 */
	private Access access;
	
	/**
	 * value which is sent or received using access.
	 */
	private TypeValue value;
	
	/**
	 * This constructor constructs a new Function with a GET Access.
	 *
	 */
	public Function() {
		this( new Access( MethodTypes.GET ) );
	}
	
	/**
	 * This constructor constructs a new Function with the given Access.
	 * 
	 * @param access The Access for the Function. Must not be null.
	 */
	public Function( final Access access ) {
		if( access == null ) {
			throw new IllegalArgumentException( "The parameter 'access' must not be null!" );
		}
		this.access = access;
	}

	/**
	 * This constructor construct a new Function with a given Access und TypeValue desciption.
	 * 
	 * @param access The Access. Must not be null.
	 * @param value The TypeValue object that should be used.
	 */
	public Function( final Access access, final TypeValue value ) {
		if( access == null ) {
			throw new IllegalArgumentException( "The parameter 'access' must not be null!" );
		}
		this.access = access;
		this.value = value;
	}

	/**
	 * This method gives back the Access of the Function.
	 * 
	 * @return Gives back the Access of the Function. Never gives back null.
	 */
	public Access getAccess() {
		return this.access;
	}

	/**
	 * This methods sets an other Access for the Function.
	 * 
	 * @param access The new Access for the Function. Must not be null.
	 */
	public void setAccess( final Access access ) {
		if( access == null ) {
			throw new IllegalArgumentException( "The parameter 'access' must not be null!" );
		}
		this.access = access;
	}

	/**
	 * This Method gives back the TypeValue description for this Function.
	 * 
	 * @return The TypeValue description for this Access.
	 */
	public TypeValue getValue() {
		return value;
	}

	/**
	 * This method sets the TypeValue desciption for this Function.
	 * 
	 * @param value The TypeValue descipriotn for this Function.
	 */
	public void setValue( final TypeValue value ) {
		this.value = value;
	}
	
	/**
	 * This method gives back is the values for this Function are discrete.
	 * 
	 * @return Gives back true if this Function can only have discrete values.
	 */
	public boolean isDiscrete() {
		return this.value!=null?this.value.isDiscrete():false;
	}
	
	/**
	 * This method gives back if this Function can have the given value.
	 * 
	 * @param value The value that should be checked.
	 * @return Gives back 'true' if this Function can have the given value and 'false' if not.
	 */
	public boolean isValuePossible( final String value ) {
		return this.value!=null?this.value.isValuePossible( value ):this.access.isValuePossible( value );
	}

	/**
	 * Return a well-formatted string with a valid value for the datatype.
	 * If value can not be converted, return a default value
	 * 
	 * @param value The value that will be formatted.
	 * @return a well-formatted string with a valid value
	 */
	public String formatValueDefault( final String value ) {
		return this.value!=null ? this.value.formatValueDefault( value ):this.access.formatValueDefault( value );
	}

	/**
	 * Return a well-formatted string with a valid value for the datatype.
	 * If value can not be converted, return null
	 * 
	 * @param value The value that will be formatted.
	 * @return a well-formatted string or null
	 */
	public String formatValue( final String value ) {
		return this.value!=null ? this.value.formatValue( value ):this.access.formatValue( value );
	}

	/**
	 * Return a well-formatted default value for the datatype.
	 * 
	 * @return a well-formatted string with a default value
	 */
	public String getDefaultValue() {
		return this.value!=null ? this.value.getDefaultValue():this.access.getDefaultValue();
	}

	/**
	 * 
	 * @return the type of the value object or the access object if there is no value
	 */
	public DataTypes getType(){
		return this.value!=null ? this.value.getType():this.access.getType();
	}

	/**
	 * If this Function is discrete, this method gives back List of String that contains all possible values.
	 * 
	 * @return A List that contains all possible discrete values. If this Function is not discrete null will be returned.
	 */
	public List <String > getDiscreteValues() {
		return this.value.getDiscreteValues();
	}
	
	public boolean isReadOnly() {
		if (access != null)
			return access.isReadOnly();
		else
			return false;
	}
}
