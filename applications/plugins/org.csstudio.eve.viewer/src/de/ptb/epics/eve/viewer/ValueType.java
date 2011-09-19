package de.ptb.epics.eve.viewer;

import java.util.HashMap;
import java.util.Map;

public enum ValueType {

	/**
	 * An object value.
	 */
	OBJECT("object", "Object"),

	/**
	 * An array of double values.
	 */
	ENUM("enum", "Enumeration"), //$NON-NLS-1$

	/**
	 * An array of double values.
	 */
	DOUBLE_SEQUENCE(
			"doubleSeq", "Sequence of Doubles"), //$NON-NLS-1$

	/**
	 * An array of string values.
	 */
	STRING_SEQUENCE(
			"stringSeq", "Sequence of Strings"), //$NON-NLS-1$

	/**
	 * An array of long values.
	 */
	LONG_SEQUENCE(
			"longSeq", "Sequence of Longs"), //$NON-NLS-1$

	/**
	 * An array of object values.
	 */
	OBJECT_SEQUENCE(
			"objectSeq", "Sequence of Objects"), //$NON-NLS-1$

	/**
	 * A long value.
	 */
	LONG("long", "Long"), //$NON-NLS-1$

	/**
	 * A double value.
	 */
	DOUBLE("double", "Double"), //$NON-NLS-1$

	/**
	 * A string.
	 */
	STRING("string", "String"); //$NON-NLS-1$

	/**
	 * The ID of the property type. Will be used as portable representation of
	 * the created instance.
	 */
	private String _id;

	private String _description;

	/**
	 * A hint for the necessary DAL property type.
	 */
	/**
	 * Constructor.
	 * 
	 * @param id
	 *            The ID. Will be used as portable representation of the created
	 *            instance.
	 * @param javaType
	 *            the Java type, which is expected for property values
	 * @param dalType
	 *            a hint for the necessary DAL property type
	 */
	@SuppressWarnings("unchecked")
	private ValueType(final String id, String description) {
		assert id != null;
		assert description != null;
		_id = id;
		_description = description;
	}

	/**
	 * @return An ID that allows for persisting and recreating instances of this
	 *         class.
	 */
	public String toPortableString() {
		return _id;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return _description;
	}


	/**
	 * A map that contains all instances of this class.
	 */
	private static Map<String, ValueType> _mapping;

	static {
		_mapping = new HashMap<String, ValueType>();

		for (ValueType type : ValueType.values()) {
			_mapping.put(type.toPortableString(), type);
		}
	}

	/**
	 * Creates an instance of this class from a string representation.
	 * 
	 * @param portableString
	 *            Required.
	 * @return The instance that is represented by the string or null
	 */
	public static ValueType createFromPortable(final String portableString) {
		assert portableString != null;
		ValueType result = null;
		if (_mapping.containsKey(portableString)) {
			result = _mapping.get(portableString);
		}

		return result;
	}

}
