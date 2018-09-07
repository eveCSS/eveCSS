package de.ptb.epics.eve.editor.views.batchupdateview;

/**
 * @author Marcus Michalsky
 * @since 1.30
 */
public enum FileStatus {
	/**
	 * indicates that the file status is currently determined
	 */
	PENDING {
		@Override
		public String toString() {
			return "pending...";
		}
	},
	/**
	 * indicates that the file is of an older version
	 */
	OUTDATED {
		@Override
		public String toString() {
			return "outdated";
		}
	},
	/**
	 * update in progress
	 */
	UPDATING {
		@Override
		public String toString() {
			return "updating";
		}
	},
	/**
	 * indicates that the file was updated to the current version
	 */
	UPDATED {
		@Override
		public String toString() {
			return "updated";
		}
	},
	
	/**
	 * indicates that the file has the current version
	 */
	UPTODATE{
		@Override
		public String toString() {
			return "up to date";
		}
	}, 
	
	/**
	 * indicates that an error occurred reading the file
	 */
	ERROR {
		@Override
		public String toString() {
			return "error";
		}
	},
	
	/**
	 * indicates that an error occurred during the update process
	 */
	ERROR_DURING_UPDATE {
		@Override
		public String toString() {
			return "error during update";
		}
	}
}