package de.ptb.epics.eve.editor.views.errorview;

import org.apache.log4j.Logger;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.IPartService;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.TableColumn;

import de.ptb.epics.eve.data.scandescription.errors.IModelError;
import de.ptb.epics.eve.data.scandescription.ScanDescription;
import de.ptb.epics.eve.data.scandescription.updatenotification.IModelUpdateListener;
import de.ptb.epics.eve.data.scandescription.updatenotification.ModelUpdateEvent;
import de.ptb.epics.eve.editor.Activator;
import de.ptb.epics.eve.editor.views.EditorViewPerspectiveListener;
import de.ptb.epics.eve.editor.views.IEditorView;

/**
 * 
 * @author ?
 * @author Marcus Michalsky
 */
public class ErrorView extends ViewPart implements IEditorView, IModelUpdateListener {

	/** the unique identifier of <code>ErrorView</code>. */
	public static final String ID = "de.ptb.epics.eve.editor.views.ErrorView";

	private static Logger logger = Logger.getLogger(ErrorView.class);
	
	private Composite top = null;

	private Table errorTable = null;
	
	private ScanDescription currentScanDescription;
	
	// delegation
	private PartListener partListener;
	private EditorViewPerspectiveListener perspectiveListener;
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void createPartControl(Composite parent) {
		logger.debug("createPartControl");
		
		parent.setLayout(new FillLayout());
		
		// if no measuring station was loaded -> show error and return
		if(Activator.getDefault().getMeasuringStation() == null) {
			final Label errorLabel = new Label(parent, SWT.NONE);
			errorLabel.setText("No Measuring Station has been loaded. " 
					+ "Please check Preferences!");
			return;
		}
		top = new Composite(parent, SWT.NONE);
		top.setLayout(new FillLayout());
		errorTable = new Table(top, SWT.H_SCROLL | SWT.V_SCROLL);
		errorTable.setHeaderVisible(true);
		errorTable.setLinesVisible(true);
		TableColumn levelColumn = new TableColumn(errorTable, SWT.NONE);
		levelColumn.setWidth(60);
		levelColumn.setText("Level");
		TableColumn typeColumn = new TableColumn(errorTable, SWT.NONE);
		typeColumn.setWidth(80);
		typeColumn.setText("Type");
		TableColumn descriptionColumn = new TableColumn(errorTable, SWT.NONE);
		descriptionColumn.setWidth(150);
		descriptionColumn.setText("Description");
		top.setVisible(false);
		
		partListener = new PartListener(this);
		IPartService service = (IPartService) getSite().
				getService(IPartService.class);
		service.addPartListener(partListener);
		perspectiveListener = new EditorViewPerspectiveListener(this);
		PlatformUI.getWorkbench().getActiveWorkbenchWindow().
				addPerspectiveListener(perspectiveListener);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setFocus() {
		logger.debug("set Focus -> forward to top composite");
		this.top.setFocus();
	}
	
	/**
	 * Sets the currently "active" scan description.
	 * 
	 * @param scanDescription the scan description
	 */
	protected void setCurrentScanDescription(final ScanDescription scanDescription) {
		
		// if there was already a scan description saved -> remove it
		if(currentScanDescription != null) {
			this.currentScanDescription.removeModelUpdateListener(this);
		}
		this.currentScanDescription = scanDescription;
		if(currentScanDescription != null) {
			this.currentScanDescription.addModelUpdateListener(this);
			this.top.setVisible(true);
		} else {
			this.top.setVisible(false);
		}
		// update table with errors present in the new scan description
		this.updateEvent(null);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void reset() {
		setCurrentScanDescription(null);
	}
	
	/**
	 * Gets all currently present errors of the model and displays them 
	 * in the table.<br><br>
	 * {@inheritDoc}
	 */
	@Override
	public void updateEvent(final ModelUpdateEvent modelUpdateEvent) {
		this.errorTable.removeAll();
		if(currentScanDescription != null) {
			for(IModelError error : this.currentScanDescription.getModelErrors()) {
				TableItem tableItem = new TableItem(this.errorTable, 0);
				tableItem.setData(error);
				tableItem.setText(0, "");
				tableItem.setText(1, error.getErrorName());
				tableItem.setText(2, error.getErrorMessage());
			}
		}
	}
}