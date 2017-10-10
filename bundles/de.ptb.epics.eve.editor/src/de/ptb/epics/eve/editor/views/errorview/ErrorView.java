package de.ptb.epics.eve.editor.views.errorview;

import org.apache.log4j.Logger;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IPartService;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;

import de.ptb.epics.eve.data.scandescription.ScanDescription;
import de.ptb.epics.eve.editor.Activator;
import de.ptb.epics.eve.editor.views.EditorViewPerspectiveListener;
import de.ptb.epics.eve.editor.views.IEditorView;

/**
 * <code>ErrorView</code> shows 
 * {@link de.ptb.epics.eve.data.scandescription.errors.IModelError}s of a 
 * {@link de.ptb.epics.eve.data.scandescription.ScanDescription}.
 * <p>
 * The view clears itself when the last editor is closed and updates itself 
 * when a {@link de.ptb.epics.eve.editor.graphical.GraphicalEditor} is 
 * activated.
 * 
 * @author Marcus Michalsky
 * @since 1.1
 */
public class ErrorView extends ViewPart implements IEditorView {

	/** the unique identifier of <code>ErrorView</code>. */
	public static final String ID = "de.ptb.epics.eve.editor.views.ErrorView";

	private static Logger logger = Logger.getLogger(ErrorView.class);

	private Composite top = null;

	private ScanDescription currentScanDescription;

	private TableViewer viewer;
	
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
		GridLayout layout = new GridLayout(2, false);
		top.setLayout(layout);
		
		viewer = new TableViewer(top, SWT.V_SCROLL | SWT.H_SCROLL);
		GridData gridData = new GridData();
		gridData.verticalAlignment = GridData.FILL;
		gridData.horizontalSpan = 2;
		gridData.grabExcessHorizontalSpace = true;
		gridData.grabExcessVerticalSpace = true;
		gridData.horizontalAlignment = GridData.FILL;
		viewer.getControl().setLayoutData(gridData);
		viewer.getTable().setHeaderVisible(true);
		viewer.getTable().setLinesVisible(true);
		TableViewerColumn typeColumn = new TableViewerColumn(viewer, SWT.NONE);
		typeColumn.getColumn().setText("Type");
		typeColumn.getColumn().setWidth(120);
		typeColumn.getColumn().setResizable(true);
		TableViewerColumn messageColumn = new TableViewerColumn(viewer, SWT.NONE);
		messageColumn.getColumn().setText("Description");
		messageColumn.getColumn().setWidth(90);
		messageColumn.getColumn().setResizable(true);
		TableViewerColumn emptyColumn = new TableViewerColumn(viewer, SWT.NONE);
		emptyColumn.getColumn().setText("");
		emptyColumn.getColumn().setWidth(1);
		viewer.setContentProvider(new ContentProvider());
		viewer.setLabelProvider(new LabelProvider());
		viewer.setInput(null);
		
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
		this.currentScanDescription = scanDescription;
		if(currentScanDescription != null) {
			this.top.setVisible(true);
			this.viewer.setInput(this.currentScanDescription);
		} else {
			this.viewer.setInput(null);
			this.top.setVisible(false);
		}
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void reset() {
		setCurrentScanDescription(null);
	}
}