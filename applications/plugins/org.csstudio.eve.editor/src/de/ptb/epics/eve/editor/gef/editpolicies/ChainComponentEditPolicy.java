/**
 * 
 */
package de.ptb.epics.eve.editor.gef.editpolicies;

import org.apache.log4j.Logger;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.editpolicies.ComponentEditPolicy;
import org.eclipse.gef.requests.GroupRequest;

/**
 * @author mmichals
 *
 */
public class ChainComponentEditPolicy extends ComponentEditPolicy {

	private static Logger logger = Logger
			.getLogger(ChainComponentEditPolicy.class.getName());
	
	@Override
	protected Command createDeleteCommand(GroupRequest deleteRequest) {
		logger.debug("createDeleteCommand");
		// TODO Auto-generated method stub
		return super.createDeleteCommand(deleteRequest);
	}
}
