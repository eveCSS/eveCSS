package de.ptb.epics.eve.viewer;

import de.ptb.epics.eve.data.DataTypes;

public class Helper {

	public static ValueType dataTypesToValueType( final DataTypes dataTypes ) {
		if( dataTypes == null ) {
			throw new IllegalArgumentException( "The parameter 'dataTypes' must not be null!" );
		}
		
		switch( dataTypes ) {
		case ONOFF:
			return ValueType.STRING;
		case OPENCLOSE:
			return ValueType.STRING;
		case INT:
			return ValueType.LONG;
		case DOUBLE:
			return ValueType.DOUBLE;
		case STRING:
			return ValueType.STRING;
		}
		
		throw new UnsupportedOperationException( "The passed value for dataTypes " + dataTypes + " is unknown!" );
	}
	
}
