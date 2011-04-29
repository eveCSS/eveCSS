package de.ptb.epics.eve.editor.views.motoraxisview;

import java.io.File;
import java.util.Iterator;

import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Button;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;

import de.ptb.epics.eve.data.scandescription.Axis;
import de.ptb.epics.eve.data.scandescription.errors.AxisError;
import de.ptb.epics.eve.data.scandescription.errors.AxisErrorTypes;
import de.ptb.epics.eve.data.scandescription.errors.IModelError;
import de.ptb.epics.eve.editor.Activator;

/**
 * <code>MotorAxisFileComposite</code> is a composite to input a filename
 * with positions of the motor axis.
 * 
 * @author Hartmut Scherr
 * @author Marcus Michalsky
 */
public class MotorAxisFileComposite extends Composite {

	private Label filenameLabel = null;
	private Text filenameText = null;
	private FilenameTextFocusListener filenameTextFocusListener;
	private Label filenameErrorLabel = null;
	
	private Button searchButton = null;
	private SearchButtonSelectionListener searchButtonSelectionListener;
	
	private Axis axis = null;
	
	private MotorAxisView motorAxisView;
	
	/**
	 * Constructs a <code>MotorAxisFileComposite</code>.
	 * 
	 * @param parent the parent composite
	 * @param style the style
	 * @param parentView the view the composite is contained in
	 */
	public MotorAxisFileComposite(Composite parent, int style, MotorAxisView parentView) {
		super(parent, style);
		
		motorAxisView = parentView;
		
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 4;
		this.setLayout(gridLayout);
		this.filenameLabel = new Label(this, SWT.NONE);
		this.filenameLabel.setText("Filename:");
		
		this.filenameText = new Text(this, SWT.BORDER);
		GridData gridData = new GridData();
		gridData.grabExcessHorizontalSpace = true;
		gridData.horizontalAlignment = GridData.FILL;
		this.filenameText.setLayoutData(gridData);
		filenameTextFocusListener = new FilenameTextFocusListener();
		this.filenameText.addFocusListener(filenameTextFocusListener);
		
		this.filenameErrorLabel = new Label(this, SWT.NONE);
		
		this.searchButton = new Button(this, SWT.NONE);
		this.searchButton.setText("Search");
		searchButtonSelectionListener = new SearchButtonSelectionListener();
		this.searchButton.addSelectionListener(searchButtonSelectionListener);
	}

	/**
	 * calculate the height to see all entries of this composite
	 * 
	 * @return the needed height of Composite to see all entries
	 */
	public int getTargetHeight() {
		return (filenameText.getBounds().y + filenameText.getBounds().height + 5);
	}

	/**
	 * calculate the width to see all entries of this composite
	 * @return the needed width of Composite to see all entries
	 */
	public int getTargetWidth() {
		return (filenameText.getBounds().x + filenameText.getBounds().width + 5);
	}

	/**
	 * Sets the {@link de.ptb.epics.eve.data.scandescription.Axis}.
	 *  
	 * @param axis the {@link de.ptb.epics.eve.data.scandescription.Axis} that 
	 * 		  should be set
	 */
	public void setAxis(final Axis axis) {
		
		removeListeners();

		// update represented axis to the new one
		this.axis = axis;
		
		if(this.axis != null) {
			// new axis is not null -> set widgets
			this.filenameText.setText(this.axis.getPositionfile() == null
									  ? ""
									  : this.axis.getPositionfile());
			
			this.filenameErrorLabel.setImage(null);
			
			checkForErrors();
			
			this.filenameText.setEnabled(true);
			this.searchButton.setEnabled(true);
		} else {
			// new axis is null -> reset widgets
			this.filenameText.setText("");
			this.filenameErrorLabel.setImage(null);
			this.filenameText.setEnabled(false);
			this.searchButton.setEnabled(false);
		}
		addListeners();
	}

	
	/*
	 * 
	 */
	private void checkForErrors()
	{
		this.filenameErrorLabel.setImage(null);
		
		final Iterator<IModelError> it = this.axis.getModelErrors().iterator();
		while(it.hasNext()) {
			final IModelError modelError = it.next();
			if(modelError instanceof AxisError) {
				final AxisError axisError = (AxisError)modelError;
				if(axisError.getErrorType() == AxisErrorTypes.FILENAME_NOT_SET) {
					this.filenameErrorLabel.setImage(PlatformUI.getWorkbench().
									getSharedImages().getImage(
									ISharedImages.IMG_OBJS_ERROR_TSK));
					break;
				}
			}
		}
	}
	
	/*
	 * 
	 */
	private void addListeners()
	{
		filenameText.addFocusListener(filenameTextFocusListener);
		searchButton.addSelectionListener(searchButtonSelectionListener);
	}
	
	/*
	 * 
	 */
	private void removeListeners()
	{
		filenameText.removeFocusListener(filenameTextFocusListener);
		searchButton.removeSelectionListener(searchButtonSelectionListener);
	}
	
	// **********************************************************************
	// ************************** Listeners *********************************
	// **********************************************************************

	/**
	 * <code>ModifyListener</code> of <code>filenameText</code>.
	 */
	class FilenameTextFocusListener implements FocusListener {

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void focusGained(FocusEvent e) {	
		}
		
		/**
		 * {@inheritDoc}
		 */
		@Override
		public void focusLost(FocusEvent e) {
			motorAxisView.suspendModelUpdateListener();
			
			if(axis != null) {
				axis.setPositionfile(filenameText.getText());
			}
			
			motorAxisView.resumeModelUpdateListener();
		}
	}
	
	/**
	 * <code>SelectionListener</code> of <code>searchButton</code>.
	 */
	class SearchButtonSelectionListener implements SelectionListener {

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void widgetDefaultSelected(final SelectionEvent e) {
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void widgetSelected(final SelectionEvent e) {

			Shell shell = getShell();

			int lastSeperatorIndex;
			final String filePath;
			
			if ((axis.getPositionfile() == null) || (axis.getPositionfile().equals(""))) {
				lastSeperatorIndex = Activator.getDefault().getMeasuringStation().getLoadedFileName().lastIndexOf( File.separatorChar );
				filePath = Activator.getDefault().getMeasuringStation().getLoadedFileName().substring( 0, lastSeperatorIndex);
			}
			else {
				// als filePath wird das vorhandene Verzeichnis gesetzt
				lastSeperatorIndex = axis.getPositionfile().lastIndexOf( File.separatorChar );
				filePath = axis.getPositionfile().substring( 0, lastSeperatorIndex + 1 );
			}
			
			FileDialog fileWindow = new FileDialog(shell, SWT.SAVE);
			fileWindow.setFilterPath(filePath);
			String name = fileWindow.open();
			
			if(name == null)
			      return;
			
			motorAxisView.suspendModelUpdateListener();
			
			filenameText.setText(name);
			axis.setPositionfile(name);
			
			checkForErrors();
			
			motorAxisView.resumeModelUpdateListener();
		}
	}
}