package de.ptb.epics.eve.editor.views.motoraxisview;

import java.io.File;

import org.eclipse.jface.fieldassist.ControlDecoration;
import org.eclipse.jface.fieldassist.FieldDecorationRegistry;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Button;

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
public class FileComposite extends Composite {

	private Label filenameLabel;
	
	private Text filenameText;
	private FilenameTextModifyListener filenameTextModifyListener;
	private ControlDecoration filenameTextControlDecoration;
	
	private Image warnImage;
	private Image errorImage;
	
	private Button searchButton;
	private SearchButtonSelectionListener searchButtonSelectionListener;
	
	private Axis axis;
	
	private MotorAxisView motorAxisView;
	
	/**
	 * Constructs a <code>MotorAxisFileComposite</code>.
	 * 
	 * @param parent the parent composite
	 * @param style the style
	 * @param parentView the view the composite is contained in
	 */
	public FileComposite(Composite parent, int style, MotorAxisView parentView) {
		super(parent, style);
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 3;
		gridLayout.horizontalSpacing = 12;
		this.setLayout(gridLayout);
		
		this.motorAxisView = parentView;
		
		// "Filename:"
		this.filenameLabel = new Label(this, SWT.NONE);
		this.filenameLabel.setText("Filename:");
		
		// Filename Text field
		this.filenameText = new Text(this, SWT.BORDER);
		GridData gridData = new GridData();
		gridData.grabExcessHorizontalSpace = true;
		gridData.horizontalAlignment = GridData.FILL;
		this.filenameText.setLayoutData(gridData);
		this.filenameTextModifyListener = new FilenameTextModifyListener();
		this.filenameText.addModifyListener(filenameTextModifyListener);
		
		this.filenameTextControlDecoration = new ControlDecoration(
				filenameText, SWT.RIGHT);
		
		this.warnImage = FieldDecorationRegistry.getDefault().getFieldDecoration(
				FieldDecorationRegistry.DEC_WARNING).getImage();
		this.errorImage = FieldDecorationRegistry.getDefault().getFieldDecoration(
				FieldDecorationRegistry.DEC_ERROR).getImage();
		this.filenameTextControlDecoration.hide();

		// Search Button
		this.searchButton = new Button(this, SWT.NONE);
		this.searchButton.setText("Search...");
		searchButtonSelectionListener = new SearchButtonSelectionListener();
		this.searchButton.addSelectionListener(searchButtonSelectionListener);
		
		this.axis = null;
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
	 * 
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
			checkForErrors();

			this.filenameText.setEnabled(true);
			this.searchButton.setEnabled(true);
		} else {
			// new axis is null -> reset widgets
			this.filenameText.setText("");
			this.filenameTextControlDecoration.hide();
			this.filenameText.setEnabled(false);
			this.searchButton.setEnabled(false);
		}
		addListeners();
	}
	
	/*
	 * 
	 */
	private void checkForErrors() {
		this.filenameTextControlDecoration.hide();
		
		// decoration if file doesn't exist
		if(this.axis.getPositionfile() != null) {
			File file = new File(this.axis.getPositionfile());
			if(!file.isFile() || !file.exists()) {
				this.filenameTextControlDecoration.setImage(warnImage);
				this.filenameTextControlDecoration.setDescriptionText(
						"File doesn't exist!");
				this.filenameTextControlDecoration.show();
			}
			file = null;
		}
		
		// decoration for file not set (overrules decoration above)
		for(IModelError error : this.axis.getModelErrors()) {
			if(error instanceof AxisError) {
				final AxisError axisError = (AxisError)error;
				if(axisError.getErrorType() == AxisErrorTypes.FILENAME_NOT_SET) {
					this.filenameTextControlDecoration.setImage(errorImage);
					this.filenameTextControlDecoration.setDescriptionText(
							"Filename not set!");
					this.filenameTextControlDecoration.show();
					break;
				}
			}
		}
	}
	
	/*
	 * 
	 */
	private void addListeners() {
		filenameText.addModifyListener(filenameTextModifyListener);
		searchButton.addSelectionListener(searchButtonSelectionListener);
	}
	
	/*
	 * 
	 */
	private void removeListeners() {
		filenameText.removeModifyListener(filenameTextModifyListener);
		searchButton.removeSelectionListener(searchButtonSelectionListener);
	}
	
	// **********************************************************************
	// ************************** Listeners *********************************
	// **********************************************************************
	
	/**
	 * {@link org.eclipse.swt.events.ModifyListener} of 
	 * <code>filenameText</code>.
	 * 
	 * @author Marcus Michalsky
	 * @since 1.1
	 */
	private class FilenameTextModifyListener implements ModifyListener {
		
		/**
		 * {@inheritDoc}
		 */
		@Override
		public void modifyText(ModifyEvent e) {
			if(axis != null) {
				axis.setPositionfile(filenameText.getText());
			}
			checkForErrors();
		}
	}
	
	/**
	 * {@link org.eclipse.swt.events.SelectionListener} of 
	 * <code>searchButton</code>.
	 */
	private class SearchButtonSelectionListener implements SelectionListener {
		
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
			int lastSeperatorIndex;
			final String filePath;
			
			if (axis.getPositionfile() == null || 
				axis.getPositionfile().isEmpty()) {
					filePath = Activator.getDefault().getRootDirectory();
			} else {
				// als filePath wird das vorhandene Verzeichnis gesetzt
				lastSeperatorIndex = 
						axis.getPositionfile().lastIndexOf(File.separatorChar);
				filePath = axis.getPositionfile().substring(
						0, lastSeperatorIndex + 1);
			}
			
			FileDialog fileWindow = new FileDialog(getShell(), SWT.SAVE);
			fileWindow.setFilterPath(filePath);
			String name = fileWindow.open();
			if(name == null) {
				// dialog was cancelled
				return;
			}
			
			filenameText.setText(name);
			axis.setPositionfile(name);
			checkForErrors();
		}
	}
}