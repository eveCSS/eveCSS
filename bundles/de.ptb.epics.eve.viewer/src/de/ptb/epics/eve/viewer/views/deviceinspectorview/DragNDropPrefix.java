package de.ptb.epics.eve.viewer.views.deviceinspectorview;

/**
 * Drag and Drop prefixed used for Drag and Drop Text Transfers to identify 
 * different actions/devices.
 * 
 * @author Marcus Michalsky
 * @since 1.17
 */
public enum DragNDropPrefix {
	
	/** 
	 * The prefix used for drag and drop text transfer of a motor 
	 */
	MOTOR {
		@Override
		public String toString() {
			return "M";
		}
	},

	/** 
	 * The prefix used for drag and drop text transfer of a motor axis 
	 */
	MOTOR_AXIS {
		@Override
		public String toString() {
			return "A";
		}
	},

	/** 
	 * The prefix used for drag and drop text transfer of a detector 
	 */
	DETECTOR {
		@Override
		public String toString() {
			return "D";
		}
	},

	/** 
	 * The prefix used for drag and drop text transfer of a detector channel 
	 */
	DETECTOR_CHANNEL {
		@Override
		public String toString() {
			return "C";
		}
	},

	/** 
	 * The prefix used for drag and drop text transfer of a device 
	 */
	DEVICE {
		@Override
		public String toString() {
			return "d";
		}
	},

	/** 
	 * The prefix used for drag and drop text transfer when moving a device
	 * (change its position in the table) 
	 */
	MOVE {
		@Override
		public String toString() {
			return "-";
		}
	}
}