package de.ptb.epics.eve.editor.views.motoraxisview.positionlistcomposite;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.apache.log4j.Logger;
import org.eclipse.core.databinding.Binding;
import org.eclipse.core.databinding.UpdateValueStrategy;
import org.eclipse.core.databinding.beans.BeansObservables;
import org.eclipse.core.databinding.observable.ChangeEvent;
import org.eclipse.core.databinding.observable.IChangeListener;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.databinding.fieldassist.ControlDecorationSupport;
import org.eclipse.jface.databinding.swt.SWTObservables;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.fieldassist.ControlDecoration;
import org.eclipse.jface.fieldassist.FieldDecorationRegistry;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import de.ptb.epics.eve.data.scandescription.Axis;
import de.ptb.epics.eve.data.scandescription.axismode.PositionlistMode;
import de.ptb.epics.eve.editor.Activator;
import de.ptb.epics.eve.editor.jobs.file.PositionlistToFile;
import de.ptb.epics.eve.editor.views.motoraxisview.MotorAxisViewComposite;

/**
 * <code>MotorAxisPositionlistComposite</code> is a 
 * {@link org.eclipse.swt.widgets.Composite} contained in the 
 * <code>MotorAxisView</code>. It allows entering position lists for 
 * motor axes using a position list as step function.
 * 
 * @author Hartmut Scherr
 * @author Marcus Michalsky
 */
public class PositionlistComposite extends MotorAxisViewComposite implements
		PropertyChangeListener {
	
	private static final Logger LOGGER = 
			Logger.getLogger(PositionlistComposite.class.getName());
	
	private PositionlistMode positionlistMode;
	
	private Label positionlistLabel;
	private ControlDecoration positionlistLabelDecoration;
	
	private Text positionlistText;
	private Binding positionlistBinding;
	private IObservableValue positionlistModelObservable;
	private IObservableValue positionlistGUIObservable;
	private ControlDecorationSupport positionlistControlDecorationSupport;
	private PositionlistValidator positionlistValidator;
	
	private Label positionCountLabel;
	
	private Image infoImage;
	
	private Button saveButton;
	
	/**
	 * Constructs a <code>MotorAxisPositionlistComposite</code>.
	 * 
	 * @param parent the parent composite
	 * @param style the style
	 * @param parentView the view the composite is contained in
	 */
	public PositionlistComposite(final Composite parent, final int style) {
		super(parent, style | SWT.BORDER);
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 1;
		this.setLayout(gridLayout);
		
		this.infoImage = FieldDecorationRegistry.getDefault().getFieldDecoration(
				FieldDecorationRegistry.DEC_INFORMATION).getImage();
		
		Composite positionListHeaderComposite = new Composite(this, SWT.NONE);
		gridLayout = new GridLayout();
		gridLayout.numColumns = 2;
		gridLayout.marginBottom = 0;
		positionListHeaderComposite.setLayout(gridLayout);
		GridData gridData = new GridData();
		gridData.horizontalAlignment = SWT.FILL;
		positionListHeaderComposite.setLayoutData(gridData);
		
		// position list Label
		this.positionlistLabel = new Label(positionListHeaderComposite, SWT.NONE);
		this.positionlistLabel.setText("Positionlist:");
		this.positionlistLabelDecoration = new ControlDecoration(
				positionlistLabel, SWT.TOP | SWT.RIGHT);
		this.positionlistLabelDecoration.setImage(infoImage);
		this.positionlistLabelDecoration.setDescriptionText(
				"use , (comma) as delimiter");
		this.positionlistLabelDecoration.show();
		gridData = new GridData();
		gridData.verticalAlignment = SWT.BOTTOM;
		this.positionlistLabel.setLayoutData(gridData);
		
		Image saveIcon = Activator.getDefault().getImageRegistry().get("SAVE");
		this.saveButton = new Button(positionListHeaderComposite, SWT.NONE);
		this.saveButton.setImage(saveIcon);
		this.saveButton.setToolTipText("Save position list to file...");
		gridData = new GridData();
		gridData.grabExcessHorizontalSpace = true;
		gridData.horizontalAlignment = SWT.RIGHT;
		this.saveButton.setLayoutData(gridData);
		this.saveButton.setEnabled(false);
		this.saveButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDown(MouseEvent e) {
				FileDialog dialog = new FileDialog(saveButton.getParent().
						getDisplay().getActiveShell(), SWT.SAVE);
				String [] filterNames = new String [] {"*.txt"};
				String [] filterExtensions = new String [] {"*.txt;"};
				dialog.setFilterNames(filterNames);
				dialog.setFilterExtensions(filterExtensions);
				dialog.setFilterPath(
						de.ptb.epics.eve.resources.Activator.getDefault()
						.getDefaultsManager().getWorkingDirectory()
						.getAbsolutePath());
				Date date = Calendar.getInstance().getTime();
				String now = new SimpleDateFormat("yyyyMMdd-HHmmss").format(date);
				dialog.setFileName("positionlist-" + now + ".txt");
				String result = dialog.open();
				if (result == null) {
					LOGGER.info(
						"Save Positionlist to File canceled or error occurred.");
					return;
				}
				File positionlistFile = new File(result);
				if (positionlistFile.exists()) {
					boolean overwrite = MessageDialog.openConfirm(
						saveButton.getParent().getDisplay().getActiveShell(), 
						"File already exists", 
						"File " + result + " already exists. Overwrite ?");
					if (!overwrite) {
						LOGGER.info("File exists, do not overwrite -> Abort.");
						return;
					}
				}
				Job savePositionListToFile = new PositionlistToFile(
						"Save position list to file", positionlistFile, 
						positionlistMode.getPositionList());
				savePositionListToFile.setUser(true);
				savePositionListToFile.schedule();
			}
		});
		
		// position list Text field 
		this.positionlistText = new Text(this, SWT.BORDER | SWT.V_SCROLL | SWT.WRAP);
		gridData = new GridData();
		gridData.horizontalSpan = 2;
		gridData.grabExcessHorizontalSpace = true;
		gridData.grabExcessVerticalSpace = true;
		gridData.horizontalAlignment = GridData.FILL;
		gridData.verticalAlignment = GridData.FILL;
		gridData.horizontalIndent = 7;
		this.positionlistText.setLayoutData(gridData);
		this.positionlistText
				.addFocusListener(new PositionlistTextFocusListener());
		this.positionlistText.addModifyListener(new ModifyListener() {
			@Override public void modifyText(ModifyEvent e) {
					countPositions();
			}
		});
		this.positionlistText.addVerifyListener(new VerifyListener() {
			@Override
			public void verifyText(VerifyEvent e) {
				if (!e.doit) {
					return;
				}
				switch (e.keyCode) {
				case SWT.CR:
				case SWT.KEYPAD_CR:
					e.doit = false;
					break;
				default:
					break;
				}
			}
		});
		// position count label
		this.positionCountLabel = new Label(this, SWT.NONE);
		this.positionCountLabel.setText("0 positions");
		
		this.positionlistMode  = null;
	} // end of: Constructor

	/**
	 * Calculates the height to see all entries of this composite
	 * 
	 * @return the needed height of Composite to see all entries
	 */
	public int getTargetHeight() {
		return (positionCountLabel.getBounds().y + 
				positionCountLabel.getBounds().height + 5);
	}

	/**
	 * Calculates the width to see all entries of this composite
	 * 
	 * @return the needed width of Composite to see all entries
	 */
	public int getTargetWidth() {
		return (positionCountLabel.getBounds().x + 
				positionCountLabel.getBounds().width + 5);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setAxis(final Axis axis) {
		if (axis == null) {
			this.reset();
			return;
		}
		if (!(axis.getMode() instanceof PositionlistMode)) {
			LOGGER.warn("invalid axis mode");
			return;
		}
		this.positionlistMode = (PositionlistMode)axis.getMode();
		this.positionlistMode.addPropertyChangeListener(
				PositionlistMode.POSITIONLIST_PROP, this);
		this.createBinding();
		this.countPositions();
		this.refreshSaveButtion();
		this.positionlistMode.getAxis().getMotorAxis()
				.addPropertyChangeListener("highlimit", this);
		this.positionlistMode.getAxis().getMotorAxis()
				.addPropertyChangeListener("lowlimit", this);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void createBinding() {
		this.positionlistModelObservable = BeansObservables.observeValue(
				this.positionlistMode, PositionlistMode.POSITIONLIST_PROP);
		this.positionlistGUIObservable = SWTObservables.observeText(
				this.positionlistText, SWT.Modify);
		UpdateValueStrategy targetToModel = new UpdateValueStrategy(
				UpdateValueStrategy.POLICY_UPDATE);
		this.positionlistValidator = new PositionlistValidator(
				this.positionlistMode.getAxis());
		targetToModel.setAfterGetValidator(this.positionlistValidator);
		UpdateValueStrategy modelToTarget = new UpdateValueStrategy(
				UpdateValueStrategy.POLICY_UPDATE);
		this.positionlistBinding = context.bindValue(positionlistGUIObservable,
				positionlistModelObservable, targetToModel, modelToTarget);
		this.positionlistControlDecorationSupport = ControlDecorationSupport.
				create(positionlistBinding, SWT.LEFT);
		this.positionlistBinding.getTarget().addChangeListener(new IChangeListener() {
			@Override
			public void handleChange(ChangeEvent event) {
				refreshSaveButtion();
			}
		});
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void reset() {
		LOGGER.debug("reset");
		if (this.positionlistMode != null) {
			if (this.positionlistControlDecorationSupport != null) {
				this.positionlistControlDecorationSupport.dispose();
			}
			this.context.removeBinding(this.positionlistBinding);
			this.positionlistBinding.dispose();
			this.positionlistModelObservable.dispose();
			this.positionlistGUIObservable.dispose();
			this.positionlistMode.removePropertyChangeListener(this);
			this.positionlistMode.getAxis().getMotorAxis()
					.removePropertyChangeListener("highlimit", this);
			this.positionlistMode.getAxis().getMotorAxis()
					.removePropertyChangeListener("lowlimit", this);
		}
		this.positionlistMode = null;
		this.positionCountLabel.setText("0 positions");
		this.positionlistValidator = null;
		this.redraw();
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void propertyChange(PropertyChangeEvent e) {
		if (e.getPropertyName().equals(PositionlistMode.POSITIONLIST_PROP)) {
			this.countPositions();
		} else if (e.getPropertyName().equals("highlimit") || 
				e.getPropertyName().equals("lowlimit")) {
			this.positionlistBinding.updateTargetToModel();
		}
	}

	private void refreshSaveButtion() {
		this.positionlistBinding.validateTargetToModel();
		IStatus status = (IStatus)this.positionlistBinding.
				getValidationStatus().getValue();
		LOGGER.debug("Validation Status: " + status.getSeverity() + 
				" (" + status.getMessage() + ")");
		if(status.isOK()) {
			this.saveButton.setEnabled(true);
		} else {
			this.saveButton.setEnabled(false);
		}
	}
	
	private void countPositions() {
		if (this.positionlistMode.getPositionCount() == null) {
			this.positionCountLabel.setText("calculation not possible");
		} else {
			switch (this.positionlistMode.getPositionCount()) {
			case 0:
				this.positionCountLabel.setText("0 positions");
				break;
			case 1:
				this.positionCountLabel.setText("1 position");
				break;
			default:
				this.positionCountLabel.setText(this.positionlistMode
						.getPositionCount() + " positions");
				break;
			}
		}
		this.positionCountLabel.getParent().layout();
	}
	
	/**
	 * @author Marcus Michalsky
	 * @since 1.7
	 */
	private class PositionlistTextFocusListener implements FocusListener {

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
				positionlistBinding.updateModelToTarget();
				positionlistBinding.validateTargetToModel();
				countPositions();
		}
	}
}