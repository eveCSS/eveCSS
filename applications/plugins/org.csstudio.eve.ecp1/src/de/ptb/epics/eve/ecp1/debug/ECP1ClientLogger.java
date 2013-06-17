package de.ptb.epics.eve.ecp1.debug;

import org.apache.log4j.Logger;

import de.ptb.epics.eve.ecp1.client.interfaces.IChainStatusListener;
import de.ptb.epics.eve.ecp1.client.interfaces.IConnectionStateListener;
import de.ptb.epics.eve.ecp1.client.interfaces.IEngineStatusListener;
import de.ptb.epics.eve.ecp1.client.interfaces.IErrorListener;
import de.ptb.epics.eve.ecp1.client.interfaces.IMeasurementDataListener;
import de.ptb.epics.eve.ecp1.client.interfaces.INewXMLFileListener;
import de.ptb.epics.eve.ecp1.client.interfaces.IPlayListController;
import de.ptb.epics.eve.ecp1.client.interfaces.IPlayListListener;
import de.ptb.epics.eve.ecp1.client.interfaces.IRequestListener;
import de.ptb.epics.eve.ecp1.client.model.Error;
import de.ptb.epics.eve.ecp1.client.model.MeasurementData;
import de.ptb.epics.eve.ecp1.client.model.Request;
import de.ptb.epics.eve.ecp1.commands.ChainStatusCommand;
import de.ptb.epics.eve.ecp1.types.EngineStatus;

/**
 * @author Marcus Michalsky
 * @since 1.13
 */
public class ECP1ClientLogger implements IEngineStatusListener,
		IChainStatusListener, IErrorListener, IMeasurementDataListener,
		IRequestListener, INewXMLFileListener, IConnectionStateListener,
		IPlayListListener {
	
	private static final Logger LOGGER = Logger
			.getLogger(ECP1ClientLogger.class.getName());

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void measurementDataTransmitted(MeasurementData measurementData) {
		LOGGER.debug(measurementData.getDataModifier() + 
				" | Chain: " + measurementData.getChainId() + 
				" | SM: " + measurementData.getScanModuleId() + 
				" | PosCnt: " + measurementData.getPositionCounter() +
				" | Name: " + measurementData.getName() + 
				" | Type: " + measurementData.getDataType() +
				" | " + measurementData.getValues().get(0)
				);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void errorOccured(Error error) {
		LOGGER.debug(error.getErrorType() + 
				" | Facility: " + error.getErrorFacility() + 
				" | Severity: " + error.getErrorSeverity() + 
				" | : " + error.getText()
				);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void chainStatusChanged(ChainStatusCommand chainStatusCommand) {
		LOGGER.debug(chainStatusCommand.getChainStatus() + 
				" | Chain: " + chainStatusCommand.getChainId() +
				" | SM: " + chainStatusCommand.getScanModulId() +
				" | PosCnt: " + chainStatusCommand.getPositionCounter()
				);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void engineStatusChanged(EngineStatus engineStatus, String xmlName,
			int repeatCount) {
		LOGGER.debug(engineStatus + 
				" | XML: " + xmlName
				);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void playListHasChanged(IPlayListController playListController) {
		LOGGER.debug("Size: " + playListController.getEntries().size());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void autoPlayHasChanged(IPlayListController playListController) {
		LOGGER.debug("Autoplay: " + playListController.isAutoplay());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void stackConnected() {
		LOGGER.debug("connected");
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void stackDisconnected() {
		LOGGER.debug("disconnected");
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void newXMLFileReceived(byte[] xmlData) {
		LOGGER.debug("new XML");
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void request(Request request) {
		LOGGER.debug("Request: ID " + request.getRequestId() + 
					" | Type: " + request.getRequestType() + 
					" | " + request.getRequestText()
				);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void cancelRequest(Request request) {
		LOGGER.debug("Cancel Request: ID " + request.getRequestId() + 
				" | Type: " + request.getRequestType() + 
				" | " + request.getRequestText()
			);
	}
}