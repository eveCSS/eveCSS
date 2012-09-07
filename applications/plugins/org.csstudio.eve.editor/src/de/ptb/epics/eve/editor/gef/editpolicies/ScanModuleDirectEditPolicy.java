/**
 * 
 */
package de.ptb.epics.eve.editor.gef.editpolicies;

import org.apache.log4j.Logger;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.editpolicies.DirectEditPolicy;
import org.eclipse.gef.requests.DirectEditRequest;

/**
 * @author mmichals
 *
 */
public class ScanModuleDirectEditPolicy extends DirectEditPolicy {

	private static Logger logger = Logger
			.getLogger(ScanModuleDirectEditPolicy.class.getName());
	
	/* (non-Javadoc)
	 * @see org.eclipse.gef.editpolicies.DirectEditPolicy#getDirectEditCommand(org.eclipse.gef.requests.DirectEditRequest)
	 */
	@Override
	protected Command getDirectEditCommand(DirectEditRequest arg0) {
		logger.debug("getDirectEditCommand");
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.gef.editpolicies.DirectEditPolicy#showCurrentEditValue(org.eclipse.gef.requests.DirectEditRequest)
	 */
	@Override
	protected void showCurrentEditValue(DirectEditRequest arg0) {
		logger.debug("showCurrentEditValue");
		// TODO Auto-generated method stub

	}

}
