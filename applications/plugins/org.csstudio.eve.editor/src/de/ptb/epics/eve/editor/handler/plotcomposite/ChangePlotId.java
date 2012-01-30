package de.ptb.epics.eve.editor.handler.plotcomposite;

import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.core.commands.IHandlerListener;
import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.handlers.HandlerUtil;

import de.ptb.epics.eve.data.scandescription.PlotWindow;

/**
 * Changes the id of the plot where the context menu was opened from.
 * <p>
 * The user is asked for a new Id. It is set only if a valid (new) id is 
 * provided (and confirmed via the OK button).
 * 
 * @author Marcus Michalsky
 * @since 1.1
 */
public class ChangePlotId implements IHandler {

	private static Logger logger = 
			Logger.getLogger(ChangePlotId.class.getName());
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		ISelection selection = HandlerUtil.getCurrentSelection(event);
		if(selection instanceof IStructuredSelection) {
			Object o = ((IStructuredSelection)selection).getFirstElement();
			if(o instanceof PlotWindow) {
				PlotWindow plotWindow = (PlotWindow)o;
				Shell shell = HandlerUtil.getActiveShell(event);
				InputValidator iv = new InputValidator(plotWindow.
						getScanModule().getPlotIds());
				InputDialog dialog = new InputDialog(shell,
						"Change Id of Plot Window",
						"Please enter the new Id", 
						Integer.toString(plotWindow.getId()), 
						iv);
				if (dialog.open() == InputDialog.OK) {
					plotWindow.setId(Integer.parseInt(dialog.getValue()));
					logger.debug("plot window id changed to: " + 
							Integer.parseInt(dialog.getValue()));
				} else {
					logger.debug("dialog canceled");
				}
			}
		}
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isEnabled() {
		return true;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isHandled() {
		return true;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void dispose() {
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void addHandlerListener(IHandlerListener handlerListener) {
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void removeHandlerListener(IHandlerListener handlerListener) {
	}
	
	/* ********************************************************************* */
	
	/**
	 * 
	 * @author Marcus Michalsky
	 * @since 1.1
	 */
	private class InputValidator implements IInputValidator {
		
		private List<Integer> ids;
		
		/**
		 * Constructor.
		 * 
		 * @param ids list of invalid ids
		 */
		public InputValidator(List<Integer> ids) {
			this.ids = ids;
		}
		
		/**
		 * {@inheritDoc}
		 */
		@Override
		public String isValid(String newText) {
			try {
				Integer.parseInt(newText);
				if(ids.contains(Integer.parseInt(newText))) {
					return "The given id already exists in this scan module!";
				}
			} catch(NumberFormatException nfe) {
				return "Please provide an Integer value!";
			}
			return null;
		}
	}
}