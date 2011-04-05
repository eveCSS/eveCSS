package de.ptb.epics.eve.viewer;

import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;

import de.ptb.epics.eve.ecp1.client.interfaces.IRequestListener;
import de.ptb.epics.eve.ecp1.client.model.Request;

/**
 * This class represents a request Processor.
 * 
 * @author Hartmut Scherr
 * @version 1.2
 */
public class RequestProcessor implements IRequestListener {

	private final Display display;
	private MessageDialog yesNoMessageDialog;
	private MessageDialog okCancelMessageDialog;
	private MessageDialog triggerMessageDialog;
	
	/**
	 * This constructor constructs a new requestProcessor with the given display.
	 * 
	 * @param display
	 */
	public RequestProcessor( final Display display ) {
		this.display = display;
	}
	
	/**
	 * This method removes the message Dialog from the window
	 * 
	 * {@inheritDoc}
	 * @param request The request that dialog will be opened
	 */
	@Override
	public void request( final Request request ) {	
		// TODO: nur für die Case Fälle YES_NO, OK_CANCEL und TRIGGER werden eigene MessageDialoge 
		// geöffnet die dann auch geschlossen werden können, wenn ein request von einem anderen Programm
		// verursacht wird. 
		
		switch( request.getRequestType() ) {
			case YES_NO: { 
				String[] buttons = {"Yes","No"};
				yesNoMessageDialog = new MessageDialog(null, "Question", null, request.getRequestText(), 0, buttons, 0);

				this.display.syncExec( new Runnable() {
					@Override
					public void run() {
						int answer = yesNoMessageDialog.open();
						final boolean result = answer==0;
						request.sendYesNoAnswer( result );
					}
				});
				break;
			}
			
			case OK_CANCEL: {
				String[] buttons = {"OK","Cancel"};
				okCancelMessageDialog = new MessageDialog(null, "OK or Cancel", null, request.getRequestText(), 0, buttons, 0);
				
				this.display.asyncExec( new Runnable() {
					@Override
					public void run() {
						int answer = okCancelMessageDialog.open();
						final boolean result = answer==0;
						request.sendOkCancelAnswer( result );
					}
				});
				break;
			}
			
			case INT32: {
				// TODO: Dialog muß noch umgestellt werden
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
				// TODO: Dialog muß noch umgestellt werden
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

			case TRIGGER: {
				String[] buttons = {"Trigger"};
				triggerMessageDialog = new MessageDialog(null, "Trigger", null, request.getRequestText(), 0, buttons, 0);
				this.display.asyncExec( new Runnable() {
					@Override
					public void run() {
						int answer = triggerMessageDialog.open();
						final boolean result = answer==0;
						request.sendTriggerAnswer( result );
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
		
	}

	/**
	 * This method removes the message Dialog from the window
	 * 
	 * @param request The request that dialog will be closed
	 */
	@Override
	public void cancelRequest(Request request) {

		switch( request.getRequestType() ) {
			case YES_NO: { 
				this.display.syncExec( new Runnable() {
					@Override
					public void run() {
						yesNoMessageDialog.close();
					}
				});
				break;
			}
		
			case OK_CANCEL: {
				this.display.syncExec( new Runnable() {
					@Override
					public void run() {
						okCancelMessageDialog.close();
					}
				});
				break;
			}
			
			case INT32: {
				this.display.syncExec( new Runnable() {
					@Override
					public void run() {
						// INT32 Window muß geschlossen werden
					}
				});
				break;
			}
			
			case FLOAT32: {
				this.display.syncExec( new Runnable() {
					@Override
					public void run() {
						// FLOAT32 WIndow muß geschlossen werden
					}
				});
				break;
			}
		
			case TEXT: {
				this.display.syncExec( new Runnable() {
					@Override
					public void run() {
						// TEXT Window muß geschlossen werden
					}
				});
				break;
			}

			case TRIGGER: {
				this.display.syncExec( new Runnable() {
					@Override
					public void run() {
						triggerMessageDialog.close();
					}
				});
				break;
			}
		
			case ERROR_TEXT: {
				this.display.syncExec( new Runnable() {
					@Override
					public void run() {
						// ERROR TEXT Window muß geschlossen werden
					}
				});
				break;
			}
		};
	}

}
