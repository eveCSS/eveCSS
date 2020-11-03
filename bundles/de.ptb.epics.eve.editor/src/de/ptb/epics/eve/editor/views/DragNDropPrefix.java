package de.ptb.epics.eve.editor.views;

/**
 * @author Marcus Michalsky
 * @since 1.25
 */
public enum DragNDropPrefix {
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