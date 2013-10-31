package de.ptb.epics.eve.viewer;

import org.apache.log4j.Logger;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;

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

	// logging
	private static final Logger LOGGER = Logger
			.getLogger(RequestProcessor.class.getName());

	/**
	 * This constructor constructs a new requestProcessor with the given
	 * display.
	 * 
	 * @param display
	 */
	public RequestProcessor(final Display display) {
		this.display = display;
	}

	/**
	 * This method removes the message Dialog from the window
	 * 
	 * {@inheritDoc}
	 * 
	 * @param request
	 *            The request that dialog will be opened
	 */
	@Override
	public void request(final Request request) {
		// TODO: nur für die Case Fälle YES_NO, OK_CANCEL und TRIGGER werden
		// eigene MessageDialoge geöffnet die dann auch geschlossen werden
		// können, wenn ein request von einem anderen Programm
		// verursacht wird.

		LOGGER.debug("request -> type: " + request.getRequestText());

		switch (request.getRequestType()) {
		case YES_NO: {
			final String[] buttons = { "Yes", "No" };

			this.display.syncExec(new Runnable() {
				@Override
				public void run() {
					yesNoMessageDialog = new MessageDialog(PlatformUI
							.getWorkbench().getModalDialogShellProvider()
							.getShell(), "Question", null, request
							.getRequestText(), MessageDialog.QUESTION, buttons,
							1);

					int answer = yesNoMessageDialog.open();
					final boolean result = answer == 0;
					request.sendYesNoAnswer(result);
				}
			});
			break;
		}

		case OK_CANCEL: {
			final String[] buttons = { "OK", "Cancell" };

			this.display.syncExec(new Runnable() {
				@Override
				public void run() {
					okCancelMessageDialog = new MessageDialog(PlatformUI
							.getWorkbench().getModalDialogShellProvider()
							.getShell(), "OK or Cancel", null, request
							.getRequestText(), MessageDialog.QUESTION, buttons,
							1);
					int answer = okCancelMessageDialog.open();
					final boolean result = answer == 0;
					request.sendOkCancelAnswer(result);
				}
			});
			break;
		}

		case TRIGGER: {
			final String[] buttons = { "Trigger" };

			this.display.syncExec(new Runnable() {
				@Override
				public void run() {
					triggerMessageDialog = new MessageDialog(PlatformUI
							.getWorkbench().getModalDialogShellProvider()
							.getShell(), "Trigger", null, request
							.getRequestText(), MessageDialog.INFORMATION,
							buttons, 0);

					int answer = triggerMessageDialog.open();
					final boolean result = answer == 0;
					request.sendTriggerAnswer(result);
				}
			});
			break;
		}

		case INT32: {
			// TODO: Dialog muß noch umgestellt werden
			this.display.syncExec(new Runnable() {
				@Override
				public void run() {
					final InputDialog input = new InputDialog(PlatformUI
							.getWorkbench().getModalDialogShellProvider()
							.getShell(), "Enter a number", request
							.getRequestText(), "0", null);
					input.open();
					request.sendInt32Answer(Integer.parseInt(input.getValue()));
				}
			});
			break;
		}

		case FLOAT32: {
			// TODO: Dialog muß noch umgestellt werden
			this.display.syncExec(new Runnable() {
				@Override
				public void run() {
					final InputDialog input = new InputDialog(PlatformUI
							.getWorkbench().getModalDialogShellProvider()
							.getShell(), "Enter a number", request
							.getRequestText(), "0", null);
					input.open();
					request.sendFloat32Answer(Float.parseFloat(input.getValue()));
				}
			});
			break;
		}

		case TEXT: {
			this.display.syncExec(new Runnable() {
				@Override
				public void run() {
					MessageDialog.openInformation(PlatformUI.getWorkbench()
							.getModalDialogShellProvider().getShell(),
							"Information", request.getRequestText());
				}
			});
			break;
		}

		case ERROR_TEXT: {
			this.display.syncExec(new Runnable() {
				@Override
				public void run() {
					MessageDialog.openError(PlatformUI.getWorkbench()
							.getModalDialogShellProvider().getShell(),
							"Information", request.getRequestText());
				}
			});
			break;
		}
		}

	}

	/**
	 * This method removes the message Dialog from the window
	 * 
	 * @param request
	 *            The request that dialog will be closed
	 */
	@Override
	public void cancelRequest(Request request) {

		LOGGER.debug("cancel request -> type: " + request.getRequestText());

		switch (request.getRequestType()) {
		case YES_NO: {
			this.display.syncExec(new Runnable() {
				@Override
				public void run() {
					yesNoMessageDialog.close();
				}
			});
			break;
		}

		case OK_CANCEL: {
			this.display.syncExec(new Runnable() {
				@Override
				public void run() {
					okCancelMessageDialog.close();
				}
			});
			break;
		}

		case TRIGGER: {
			this.display.syncExec(new Runnable() {
				@Override
				public void run() {
					triggerMessageDialog.close();
				}
			});
			break;
		}

		case INT32: {
			this.display.syncExec(new Runnable() {
				@Override
				public void run() {
					// INT32 Window muß geschlossen werden
				}
			});
			break;
		}

		case FLOAT32: {
			this.display.syncExec(new Runnable() {
				@Override
				public void run() {
					// FLOAT32 WIndow muß geschlossen werden
				}
			});
			break;
		}

		case TEXT: {
			this.display.syncExec(new Runnable() {
				@Override
				public void run() {
					// TEXT Window muß geschlossen werden
				}
			});
			break;
		}

		case ERROR_TEXT: {
			this.display.syncExec(new Runnable() {
				@Override
				public void run() {
					// ERROR TEXT Window muß geschlossen werden
				}
			});
			break;
		}
		}
	}
}
