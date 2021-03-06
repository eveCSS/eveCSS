package de.ptb.epics.eve.ecp1.types;

/**
 * @author ?
 * @author Marcus Michalsky
 */
public enum DataModifier {

	/**
	 * the plain value
	 */
	UNMODIFIED {
		@Override
		public String toString() {
			return "Recent";
		}
	},

	/**
	 * 
	 */
	CENTER {
		@Override
		public String toString() {
			return "Center";
		}
	},

	/**
	 * 
	 */
	EDGE {
		@Override
		public String toString() {
			return "Edge";
		}
	},

	/**
	 * the minimum
	 */
	MIN {
		@Override
		public String toString() {
			return "Minimum";
		}
	},

	/**
	 * the maximum
	 */
	MAX {
		@Override
		public String toString() {
			return "Maximum";
		}
	},

	/**
	 * the full width half ?
	 */
	FWHM {
		@Override
		public String toString() {
			return "FWHM";
		}
	},

	/**
	 * the arithmetic mean
	 */
	MEAN_VALUE {
		@Override
		public String toString() {
			return "Average";
		}
	},

	/**
	 * the standard deviation
	 */
	STANDARD_DEVIATION {
		@Override
		public String toString() {
			return "Deviation";
		}
	},

	/**
	 * the sum
	 */
	SUM {
		@Override
		public String toString() {
			return "Sum";
		}
	},

	/**
	 * the normalized data
	 */
	NORMALIZED {
		@Override
		public String toString() {
			return "Normalized";
		}
	},

	/** Minimum or maximum peak */
	PEAK {
		@Override
		public String toString() {
			return "Peak";
		}
	},

	/** unknown algorithm */
	UNKNOWN {
		@Override
		public String toString() {
			return "Unknown";
		}
	};
	
	/**
	 * Converts a {@link DataModifier} to its corresponding byte code.
	 * 
	 * @param dataModifyer the {@link DataModifier}
	 * @return the byte code of the given data modifier
	 */
	public static byte dataModifyerToByte(final DataModifier dataModifyer) {
		switch (dataModifyer) {
		case UNMODIFIED:
			return 0x00;
		case CENTER:
			return 0x01;
		case EDGE:
			return 0x02;
		case MIN:
			return 0x03;
		case MAX:
			return 0x04;
		case FWHM:
			return 0x05;
		case MEAN_VALUE:
			return 0x06;
		case STANDARD_DEVIATION:
			return 0x07;
		case SUM:
			return 0x08;
		case NORMALIZED:
			return 0x09;
		case PEAK:
			return 0x0a;
		case UNKNOWN:
			return 0x0b;
		}
		return Byte.MAX_VALUE;
	}

	/**
	 * Converts a byte code to its corresponding {@link DataModifier}.
	 * 
	 * @param theByte the byte code that should be converted
	 * @return the {@link DataModifier} corresponding to the given byte code
	 */
	public static DataModifier byteTotDataModifyer(final byte theByte) {
		switch (theByte) {
		case 0x00:
			return DataModifier.UNMODIFIED;
		case 0x01:
			return DataModifier.CENTER;
		case 0x02:
			return DataModifier.EDGE;
		case 0x03:
			return DataModifier.MIN;
		case 0x04:
			return DataModifier.MAX;
		case 0x05:
			return DataModifier.FWHM;
		case 0x06:
			return DataModifier.MEAN_VALUE;
		case 0x07:
			return DataModifier.STANDARD_DEVIATION;
		case 0x08:
			return DataModifier.SUM;
		case 0x09:
			return DataModifier.NORMALIZED;
		case 0x0a:
			return DataModifier.PEAK;
		case 0x0b:
			return DataModifier.UNKNOWN;
		}
		return null;
	}
}