package de.ptb.epics.eve.editor.views.batchupdateview;

/**
 * @author Marcus Michalsky
 * @since 1.30
 */
public enum UpdateStatus {
	/** initial state */
	IDLE,
	/** currently determining version of files */
	INITIALIZING,
	/** files are initialized but target is not set */
	INITIALIZED,
	/** files are initialized and target is set */
	READY_FOR_UPDATE,
	/** currently updating files to current version */
	UPDATING,
	/** files updated to current version */
	UPDATE_DONE
}