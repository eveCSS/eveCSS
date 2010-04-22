/**
 * 
 */
package de.ptb.epics.eve.viewer.actions;

import java.io.IOException;
import java.net.InetSocketAddress;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.handlers.HandlerUtil;

import de.ptb.epics.eve.ecp1.client.interfaces.IConnectionStateListener;
import de.ptb.epics.eve.preferences.PreferenceConstants;
import de.ptb.epics.eve.viewer.Activator;
import de.ptb.epics.eve.viewer.MessageSource;
import de.ptb.epics.eve.viewer.MessageTypes;
import de.ptb.epics.eve.viewer.ViewerMessage;

/**
 * @author eden
 *
 */
public class connectCommandHandler extends AbstractHandler {

	/**
	 * 
	 */
	public connectCommandHandler() {
	}

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		
		if( !Activator.getDefault().getEcp1Client().isRunning() ) {

			int port = 0;
			String EngineString = de.ptb.epics.eve.preferences.Activator.getDefault().getPreferenceStore().getString( PreferenceConstants.P_DEFAULT_ENGINE_ADDRESS );

			int index  = EngineString.lastIndexOf( ":" );
			if( index != -1 ) {
				port = Integer.parseInt( EngineString.substring( index + 1) );
				EngineString = EngineString.substring( 0, index );
			}
			
			if( (port > 0) && (EngineString.length() > 1) ) {
				Activator.getDefault().getMessagesContainer().addMessage( new ViewerMessage( MessageSource.APPLICATION, MessageTypes.INFO, "Trying to connect to: " + EngineString + "." ) );
				try {
					Activator.getDefault().getEcp1Client().connect( new InetSocketAddress( EngineString, port ), "" );
					Activator.getDefault().getMessagesContainer().addMessage( new ViewerMessage( MessageSource.APPLICATION, MessageTypes.INFO, "Connection established to: " + EngineString + "." ) );
				} catch( final IOException e ) {
					Activator.getDefault().getMessagesContainer().addMessage( new ViewerMessage( MessageSource.APPLICATION, MessageTypes.ERROR, "Cannot establish connection! Reasion: " + e.getMessage() + "." ) );
					e.printStackTrace();
				}
			}
			else {
				MessageDialog.openInformation(HandlerUtil.getActiveWorkbenchWindow(event).getShell(), "Error", "Please provide a valid engine in Preferences / CSS Applications / EVE");
			}
		}
		
		return null;
	}

}
