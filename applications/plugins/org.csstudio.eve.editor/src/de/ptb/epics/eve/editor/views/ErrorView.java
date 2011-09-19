package de.ptb.epics.eve.editor.views;

import java.util.Iterator;

import org.apache.log4j.Logger;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.TableColumn;

import de.ptb.epics.eve.data.scandescription.errors.IModelError;
import de.ptb.epics.eve.data.scandescription.ScanDescription;
import de.ptb.epics.eve.data.scandescription.updatenotification.IModelUpdateListener;
import de.ptb.epics.eve.data.scandescription.updatenotification.ModelUpdateEvent;
import de.ptb.epics.eve.editor.Activator;

/**
 * 
 * @author ?
 * @author Marcus Michalsky
 */
public class ErrorView extends ViewPart implements IModelUpdateListener {

	/**
	 * the unique identifier of <code>ErrorView</code>. 
	 */
	public static final String ID = "de.ptb.epics.eve.editor.views.ErrorView";

	private static Logger logger = Logger.getLogger(ErrorView.class);
	
	private Composite top = null;

	private Table errorTable = null;
	
	private ScanDescription currentScanDescription;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void createPartControl(Composite parent) {
		logger.debug("createPartControl");
		
		parent.setLayout(new FillLayout());
		
		// if no measuring station was loaded -> show error and return
		if( Activator.getDefault().getMeasuringStation() == null ) {
			final Label errorLabel = new Label(parent, SWT.NONE);
			errorLabel.setText("No Measuring Station has been loaded. " 
					+ "Please check Preferences!");
			return;
		}
		top = new Composite(parent, SWT.NONE);
		top.setLayout(new FillLayout());
		errorTable = new Table(top, SWT.NONE);
		errorTable.setHeaderVisible(true);
		errorTable.setLinesVisible(true);
		TableColumn tableColumn = new TableColumn(errorTable, SWT.NONE);
		tableColumn.setWidth(60);
		tableColumn.setText("Level");
		TableColumn tableColumn1 = new TableColumn(errorTable, SWT.NONE);
		tableColumn1.setWidth(60);
		tableColumn1.setText("Type");
		TableColumn tableColumn2 = new TableColumn(errorTable, SWT.NONE);
		tableColumn2.setWidth(60);
		tableColumn2.setText("Description");
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setFocus() {
	}
	
	/**
	 * Sets the currently "active" scan description.
	 * 
	 * @param scanDescription the scan description
	 */
	public void setCurrentScanDescription(final ScanDescription scanDescription) {
		logger.debug("setScanDescription");
		
		// if there was already a scan description saved -> remove it
		if(currentScanDescription != null) {
			this.currentScanDescription.removeModelUpdateListener(this);
		}
		this.currentScanDescription = scanDescription;
		if(currentScanDescription != null)
		{
			this.currentScanDescription.addModelUpdateListener(this);
		}
		// update table with errors present in the new scan description
		this.updateEvent(null);
	}

	/**
	 * Gets all currently present errors of the model and displays them 
	 * in the table.<br><br>
	 * {@inheritDoc}
	 */
	@Override
	public void updateEvent(final ModelUpdateEvent modelUpdateEvent) {
		if(modelUpdateEvent == null) logger.debug("update event (null)");
		else logger.debug("update event (" + 
				modelUpdateEvent.getSender().getClass().getName() + ")");
		
		this.errorTable.removeAll();
		
		if(currentScanDescription != null) {
			final Iterator<IModelError> it = 
				this.currentScanDescription.getModelErrors().iterator();
			while(it.hasNext()) {
				final IModelError modelError = it.next();
				TableItem tableItem = new TableItem(this.errorTable, 0);
				tableItem.setData(modelError);
				tableItem.setText(0, "");
				tableItem.setText(1, modelError.getErrorName());
				tableItem.setText(2, modelError.getErrorMessage());
			}
		}
	}
}