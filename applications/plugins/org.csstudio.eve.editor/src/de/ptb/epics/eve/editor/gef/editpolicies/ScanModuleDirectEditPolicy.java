package de.ptb.epics.eve.editor.gef.editpolicies;

import org.apache.log4j.Logger;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.editpolicies.DirectEditPolicy;
import org.eclipse.gef.requests.DirectEditRequest;

import de.ptb.epics.eve.data.scandescription.ScanModule;
import de.ptb.epics.eve.editor.gef.commands.RenameScanModule;
import de.ptb.epics.eve.editor.gef.figures.ScanModuleFigure;

/**
 * @author Marcus Michalsky
 * @since 1.6
 */
public class ScanModuleDirectEditPolicy extends DirectEditPolicy {

	private static Logger logger = Logger
			.getLogger(ScanModuleDirectEditPolicy.class.getName());
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Command getDirectEditCommand(DirectEditRequest request) {
		logger.trace("GetDirectEditCommand");
		return new RenameScanModule(
				(String) request.getCellEditor().getValue(),
				(ScanModule) getHost().getModel());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void showCurrentEditValue(DirectEditRequest request) {
		logger.trace("showCurrentEditValue");
		 String value = (String) request.getCellEditor().getValue();
		 ((ScanModuleFigure)getHostFigure()).setName(value);
	}
	
	
}