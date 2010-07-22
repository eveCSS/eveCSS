package de.ptb.epics.eve.viewer;

import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;

import de.ptb.epics.eve.ecp1.client.interfaces.IRequestListener;
import de.ptb.epics.eve.ecp1.client.model.Request;
import de.ptb.epics.eve.ecp1.intern.RequestType;

public class RequestProcessor implements IRequestListener {

	private final Display display;
	
	public RequestProcessor( final Display display ) {
		this.display = display;
	}
	
	@Override
	public void request( final Request request ) {	
		
		switch( request.getRequestType() ) {
			case YES_NO: { 
				this.display.syncExec( new Runnable() {

					@Override
					public void run() {

						final boolean result = MessageDialog.openQuestion( null, "Question", request.getRequestText() );
						request.sendYesNoAnswer( result );
					}
					
				});
						
				break;
			}
			
			case OK_CANCEL: {
				
				this.display.syncExec( new Runnable() {

					@Override
					public void run() {
						final boolean result = MessageDialog.openConfirm( null, "Ok or Cancel", request.getRequestText() );
						request.sendOkCancelAnswer( result );
					}
					
				});
						
				
				break;
			}
				
				
			case INT32: {
				this.display.syncExec( new Runnable() {

					@Override
					public void run() {

						final InputDialog input = new InputDialog( null, "Enter a number", request.getRequestText(), "0", null );
						input.open();
						request.sendInt32Answer( Integer.parseInt( input.getValue() ) );
					}
					
				});
				break;
			}
				
			case FLOAT32: {
				this.display.syncExec( new Runnable() {

					@Override
					public void run() {

			
						final InputDialog input = new InputDialog( null, "Enter a number", request.getRequestText(), "0", null );
						input.open();
						request.sendFloat32Answer( Float.parseFloat( input.getValue() ) );
					}
					
				});
				break;
			}
			
			case TEXT: {
				this.display.syncExec( new Runnable() {

					@Override
					public void run() {

						MessageDialog.openInformation( null, "Information", request.getRequestText() );
					}
					
				});
				break;
			}
			
			case ERROR_TEXT: {
				this.display.syncExec( new Runnable() {

					@Override
					public void run() {
						MessageDialog.openError( null, "Information", request.getRequestText() );
					}
					
				});
				break;
			}
			
				
		}
		
		// TODO Auto-generated method stub

	}

}
