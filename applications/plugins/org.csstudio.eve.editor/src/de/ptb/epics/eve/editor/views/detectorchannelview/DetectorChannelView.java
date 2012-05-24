package de.ptb.epics.eve.editor.views.detectorchannelview;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.apache.log4j.Logger;
import org.eclipse.jface.fieldassist.ControlDecoration;
import org.eclipse.jface.fieldassist.FieldDecorationRegistry;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.events.ExpandEvent;
import org.eclipse.swt.events.ExpandListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.ExpandBar;
import org.eclipse.swt.widgets.ExpandItem;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.layout.GridData;

import de.ptb.epics.eve.data.measuringstation.Event;
import de.ptb.epics.eve.data.scandescription.Channel;
import de.ptb.epics.eve.data.scandescription.ScanModule;
import de.ptb.epics.eve.data.scandescription.errors.ChannelError;
import de.ptb.epics.eve.data.scandescription.errors.IModelError;
import de.ptb.epics.eve.data.scandescription.updatenotification.ControlEventTypes;
import de.ptb.epics.eve.data.scandescription.updatenotification.IModelUpdateListener;
import de.ptb.epics.eve.data.scandescription.updatenotification.ModelUpdateEvent;
import de.ptb.epics.eve.editor.Activator;
import de.ptb.epics.eve.editor.graphical.editparts.ScanDescriptionEditPart;
import de.ptb.epics.eve.editor.graphical.editparts.ScanModuleEditPart;
import de.ptb.epics.eve.editor.views.eventcomposite.EventComposite;

/**
 * <code>DetectorChannelView</code> shows attributes of a 
 * {@link de.ptb.epics.eve.data.measuringstation.DetectorChannel} and allows 
 * modification.
 * 
 * @author Hartmut Scherr
 * @author Marcus Michalsky
 */
public class DetectorChannelView extends ViewPart 
						implements ISelectionListener, PropertyChangeListener,
						IModelUpdateListener {

	/**
	 * the unique identifier of the view.
	 */
	public static final String ID = 
		"de.ptb.epics.eve.editor.views.DetectorChannelView";

	// logging
	private static Logger logger = 
			Logger.getLogger(DetectorChannelView.class.getName());
	
	
	// *******************************************************************
	// ********************** underlying model ***************************
	// ******************************************************************* 
	
	// the detector channel containing the information that is shown and 
	// allowed for editing.
	private Channel currentChannel;

	private ScanModule scanModule;
	
	private Channel[] availableDetectorChannels;
	
	// *******************************************************************
	// ****************** end of: underlying model ***********************
	// *******************************************************************

	private Composite top = null;
	private ScrolledComposite sc = null;

	private Label averageLabel;
	private Text averageText;
	private ControlDecoration averageTextControlDecoration;
	private TextNumberVerifyListener averageTextVerifyListener;
	private AverageTextModifyListener averageTextModifyListener;

	private Label maxDeviationLabel;
	private Text maxDeviationText;
	private ControlDecoration maxDeviationTextControlDecoration;
	private TextDoubleVerifyListener maxDeviationTextVerifyListener;
	private MaxDeviationTextModifyListener maxDeviationTextModifyListener;

	private Label minimumLabel;
	private Text minimumText;
	private ControlDecoration minimumTextControlDecoration;
	private TextDoubleVerifyListener minimumTextVerifyListener;
	private MinimumTextModifyListener minimumTextModifyListener;

	private Label maxAttemptsLabel;
	private Text maxAttemptsText;
	private ControlDecoration maxAttemptsTextControlDecoration;
	private TextNumberVerifyListener maxAttemptsTextVerifyListener;
	private MaxAttemptsTextModifyListener maxAttemptsTextModifyListener;

	private Label normalizeChannelLabel;
	private Combo normalizeChannelCombo;
	private NormalizeChannelComboSelectionListener 
			normalizeChannelComboSelectionListener;

	private Button confirmTriggerManualCheckBox;
	private ConfirmTriggerManualCheckBoxSelectionListener
			confirmTriggerManualCheckBoxSelectionListener;

	private ExpandBar bar = null;
	private ExpandItem eventExpandItem;
	public CTabFolder eventsTabFolder = null;
	private EventComposite redoEventComposite = null;
	private Composite eventComposite = null;

	private CTabItem redoEventTabItem;

	private Button detectorReadyEventCheckBox;
	private DetectorReadyEventCheckBoxSelectionListener 
			detectorReadyEventCheckBoxSelectionListener;

	private Image errorImage;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void createPartControl(final Composite parent) {
		parent.setLayout(new FillLayout());

		if(Activator.getDefault().getMeasuringStation() == null) {
			final Label errorLabel = new Label(parent, SWT.NONE);
			errorLabel.setText("No Measuring Station has been loaded. " +
					"Please check Preferences!");
			return;
		}
		
		this.errorImage = FieldDecorationRegistry.getDefault().
			getFieldDecoration(FieldDecorationRegistry.DEC_ERROR).getImage();

		this.sc = new ScrolledComposite(parent, SWT.V_SCROLL);

		this.top = new Composite(sc, SWT.NONE);
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 2;
		this.top.setLayout(gridLayout);

		this.sc.setExpandHorizontal(true);
		this.sc.setExpandVertical(true);
		this.sc.setContent(this.top);

		// GUI: Average: <TextBox> x
		this.averageLabel = new Label(this.top, SWT.NONE);
		this.averageLabel.setText("Average:");
		GridData gridData;
		gridData = new GridData();
		gridData.horizontalAlignment = GridData.FILL;
		this.averageLabel.setLayoutData(gridData);

		this.averageText = new Text(this.top, SWT.BORDER);
		gridData = new GridData();
		gridData.horizontalAlignment = GridData.FILL;
		gridData.horizontalIndent = 7;
		gridData.grabExcessHorizontalSpace = true;
		this.averageText.setLayoutData(gridData);
		this.averageTextVerifyListener = new TextNumberVerifyListener();
		this.averageText.addVerifyListener(averageTextVerifyListener);
		this.averageTextModifyListener = new AverageTextModifyListener();
		this.averageText.addModifyListener(averageTextModifyListener); 
		this.averageTextControlDecoration = new ControlDecoration(
				this.averageText, SWT.LEFT);
		this.averageTextControlDecoration.setDescriptionText("");
		this.averageTextControlDecoration.setImage(errorImage);
		this.averageTextControlDecoration.hide();

		// GUI: Max. Deviation (%): <TextBox> x
		this.maxDeviationLabel = new Label(this.top, SWT.NONE);
		this.maxDeviationLabel.setText("Max. Deviation (%):");
		gridData = new GridData();
		gridData.horizontalAlignment = GridData.FILL;
		this.maxDeviationLabel.setLayoutData(gridData);

		this.maxDeviationText = new Text(this.top, SWT.BORDER);
		gridData = new GridData();
		gridData.horizontalAlignment = GridData.FILL;
		gridData.horizontalIndent = 7;
		gridData.grabExcessHorizontalSpace = true;
		this.maxDeviationText.setLayoutData(gridData);
		this.maxDeviationTextVerifyListener = new TextDoubleVerifyListener();
		this.maxDeviationText.addVerifyListener(maxDeviationTextVerifyListener);
		this.maxDeviationTextModifyListener = new 
				MaxDeviationTextModifyListener();
		this.maxDeviationText.addModifyListener(maxDeviationTextModifyListener);
		this.maxDeviationTextControlDecoration = new ControlDecoration(
				this.maxDeviationText, SWT.LEFT);
		this.maxDeviationTextControlDecoration
				.setDescriptionText("Value not possible!");
		this.maxDeviationTextControlDecoration.setImage(errorImage);
		this.maxDeviationTextControlDecoration.hide();

		// GUI: Minimum: <TextBox> x
		this.minimumLabel = new Label(this.top, SWT.NONE);
		this.minimumLabel.setText("Minimum:");
		this.minimumLabel.setToolTipText(
				"for values < minimum no deviation check");
		gridData = new GridData();
		gridData.horizontalAlignment = GridData.FILL;
		this.minimumLabel.setLayoutData(gridData);

		this.minimumText = new Text(this.top, SWT.BORDER);
		gridData = new GridData();
		gridData.horizontalAlignment = GridData.FILL;
		gridData.horizontalIndent = 7;
		gridData.grabExcessHorizontalSpace = true;
		this.minimumText.setLayoutData(gridData);
		this.minimumTextVerifyListener = new TextDoubleVerifyListener();
		this.minimumText.addVerifyListener(minimumTextVerifyListener);
		this.minimumTextModifyListener = new MinimumTextModifyListener();
		this.minimumText.addModifyListener(minimumTextModifyListener);
		this.minimumTextControlDecoration = new ControlDecoration(
				this.minimumText, SWT.LEFT);
		this.minimumTextControlDecoration
				.setDescriptionText("Value not possible!");
		this.minimumTextControlDecoration.setImage(errorImage);
		this.minimumTextControlDecoration.hide();

		// GUI: Max. Attempts: <TextBox> x
		this.maxAttemptsLabel = new Label(this.top, SWT.NONE);
		this.maxAttemptsLabel.setText("Max. Attempts:");
		this.maxAttemptsLabel.setToolTipText(
				"Maximum attemps to calculate deviation:" );
		gridData = new GridData();
		gridData.horizontalAlignment = GridData.FILL;
		this.maxAttemptsLabel.setLayoutData(gridData);
		
		this.maxAttemptsText = new Text(this.top, SWT.BORDER);
		gridData = new GridData();
		gridData.horizontalAlignment = GridData.FILL;
		gridData.horizontalIndent = 7;
		gridData.grabExcessHorizontalSpace = true;
		this.maxAttemptsText.setLayoutData(gridData);
		this.maxAttemptsTextVerifyListener = new TextNumberVerifyListener();
		this.maxAttemptsText.addVerifyListener(maxAttemptsTextVerifyListener);
		this.maxAttemptsTextModifyListener = new 
				MaxAttemptsTextModifyListener();
		this.maxAttemptsText.addModifyListener(maxAttemptsTextModifyListener);
		this.maxAttemptsTextControlDecoration = new ControlDecoration(
				this.maxAttemptsText, SWT.LEFT);
		this.maxAttemptsTextControlDecoration.setDescriptionText("");
		this.maxAttemptsTextControlDecoration.setImage(errorImage);
		this.maxAttemptsTextControlDecoration.hide();

		//
		this.normalizeChannelLabel = new Label(this.top, SWT.NONE);
		this.normalizeChannelLabel.setText("Normalize Channel:");
		this.normalizeChannelCombo = new Combo(this.top, SWT.BORDER
				| SWT.READ_ONLY);
		gridData = new GridData();
		gridData.horizontalAlignment = GridData.FILL;
		gridData.horizontalIndent = 7;
		gridData.grabExcessHorizontalSpace = true;
		this.normalizeChannelCombo.setLayoutData(gridData);
		this.normalizeChannelComboSelectionListener = 
				new NormalizeChannelComboSelectionListener();
		this.normalizeChannelCombo.addSelectionListener(
				normalizeChannelComboSelectionListener);

		// GUI: [] Confirm Trigger Manual
		this.confirmTriggerManualCheckBox = new Button(this.top, SWT.CHECK);
		this.confirmTriggerManualCheckBox.setText("Confirm Trigger manual");
		this.confirmTriggerManualCheckBox.setToolTipText(
				"Mark to ask before trigger this channel");
		gridData = new GridData();
		gridData.horizontalSpan = 2;
		this.confirmTriggerManualCheckBox.setLayoutData(gridData);
		this.confirmTriggerManualCheckBoxSelectionListener = 
				new ConfirmTriggerManualCheckBoxSelectionListener();
		this.confirmTriggerManualCheckBox.addSelectionListener( 
				confirmTriggerManualCheckBoxSelectionListener);

		// Expand Bar
		this.bar = new ExpandBar(this.top, SWT.NONE);
		gridData = new GridData();
		gridData.grabExcessHorizontalSpace = true;
		gridData.grabExcessVerticalSpace = true;
		gridData.horizontalAlignment = GridData.FILL;
		gridData.verticalAlignment = GridData.FILL;
		gridData.horizontalSpan = 2;
		this.bar.setLayoutData(gridData);

		this.bar.addExpandListener(new BarExpandListener());
		
		// Event Section
		this.eventComposite = new Composite(this.bar, SWT.NONE);
		gridLayout = new GridLayout();
		this.eventComposite.setLayout(gridLayout);

		this.detectorReadyEventCheckBox = 
				new Button(this.eventComposite, SWT.CHECK);
		this.detectorReadyEventCheckBox.setText("Send Detector Ready Event");
		this.detectorReadyEventCheckBox.setToolTipText(
				"Mark to send detector ready event if channel is ready");
		gridData = new GridData();
		gridData.horizontalAlignment = GridData.FILL;
		this.detectorReadyEventCheckBox.setLayoutData(gridData);
		this.detectorReadyEventCheckBoxSelectionListener = 
				new DetectorReadyEventCheckBoxSelectionListener();
		this.detectorReadyEventCheckBox.addSelectionListener(
				detectorReadyEventCheckBoxSelectionListener);
		
		// Event Options Tab
		eventsTabFolder = new CTabFolder(this.eventComposite, SWT.FLAT);
		this.eventsTabFolder.setSimple(false);
		this.eventsTabFolder.setBorderVisible(true);
		gridData = new GridData();
		gridData.grabExcessHorizontalSpace = true;
		gridData.grabExcessVerticalSpace = true;
		gridData.verticalAlignment = GridData.FILL;
		gridData.horizontalAlignment = GridData.FILL;
		eventsTabFolder.setLayoutData(gridData);
		
		redoEventComposite = new EventComposite(eventsTabFolder, SWT.NONE, 
				ControlEventTypes.CONTROL_EVENT, this);
		 
		this.redoEventTabItem = new CTabItem(eventsTabFolder, SWT.FLAT);
		this.redoEventTabItem.setText("Redo");
		this.redoEventTabItem.setToolTipText("Repeat the current reading " +
				"of the channel, if redo event occurs");
		this.redoEventTabItem.setControl(redoEventComposite);
		
		// expand item (Events)
		this.eventExpandItem = new ExpandItem(this.bar, SWT.NONE, 0);
		this.eventExpandItem.setText("Event options");
		this.eventExpandItem.setHeight(this.eventComposite.computeSize(
				SWT.DEFAULT, SWT.DEFAULT).y);
		this.eventExpandItem.setControl(this.eventComposite);
		
		this.sc.setMinSize(this.top.computeSize(SWT.DEFAULT, SWT.DEFAULT));
		
		top.setVisible(false);
		
		// set the table viewer of the event composite as selection provider
		// (used by add/delete event commands)
		this.getSite().setSelectionProvider(
				this.redoEventComposite.getTableViewer());
		
		// listen to selection changes (if a detector channel is selected, its 
		// attributes are made available for editing)
		getSite().getWorkbenchWindow().getSelectionService().
				addSelectionListener(this);
	}
	// ************************************************************************
	// ********************** end of createPartControl ************************
	// ************************************************************************

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setFocus() {
		logger.debug("got focus -> forward to top composite");
		this.top.setFocus();
	}

	/**
	 * Returns the {@link de.ptb.epics.eve.data.scandescription.Channel} 
	 * currently shown by this view.
	 * 
	 * @return the {@link de.ptb.epics.eve.data.scandescription.Channel} 
	 * 		currently shown
	 */
	public Channel getCurrentChannel() {
		return this.currentChannel;
	}
	
	/**
	 * Sets the {@link de.ptb.epics.eve.data.scandescription.Channel}
	 * (the underlying model whose contents is presented by this view).
	 *  
	 * @param channel the {@link de.ptb.epics.eve.data.scandescription.Channel} 
	 * 		  that should be set
	 */
	private void setChannel(final Channel channel) {
		if(channel != null) {
			logger.debug("set channel (" + channel.getAbstractDevice().
					getFullIdentifyer() + ")");
		} else {
			logger.debug("set channel (null)");
		}
		if (this.currentChannel != null) {
			this.currentChannel.removeModelUpdateListener(this);
			this.scanModule.removePropertyChangeListener("removeChannel", this);
		}
		// update the underlying model to the new one
		this.currentChannel = channel;
		this.scanModule = null;
		
		if(this.currentChannel != null) {
			this.currentChannel.addModelUpdateListener(this);
			this.scanModule = this.currentChannel.getScanModule();
		}
		updateEvent(null);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void selectionChanged(IWorkbenchPart part, ISelection selection) {
		logger.debug("selection changed");
		
		if(selection instanceof IStructuredSelection) {
			if(((IStructuredSelection) selection).size() == 0) {
				return;
			}
			// since at any given time this view can only display the attributes 
			// of one detector channel, we take the first element of the 
			// selection
			Object o = ((IStructuredSelection) selection).toList().get(0);
			if (o instanceof Channel) {
				// set new Channel
				if(logger.isDebugEnabled()) {
					logger.debug("Channel: " + ((Channel)o).
								getDetectorChannel().getFullIdentifyer() + 
								" selected.");
				}
				setChannel((Channel)o);
			} else if (o instanceof ScanModuleEditPart) {
				// ScanModule was selected
				if(logger.isDebugEnabled()) {
					logger.debug("selection is ScanModuleEditPart: " + o);
					logger.debug("ScanModule: " + ((ScanModule)
							((ScanModuleEditPart)o).getModel()).getId() + 
							" selected."); 
				}
				
				if (this.scanModule != null && !this.scanModule.equals(
						((ScanModuleEditPart)o).getModel())) {
							setChannel(null);
				}
			} else if (o instanceof ScanDescriptionEditPart) {
				logger.debug("selection is ScanDescriptionEditPart: " + o);
				setChannel(null);
			} else {
				logger.debug("unknown selection -> ignore");
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void propertyChange(PropertyChangeEvent e) {
		if (e.getOldValue().equals(currentChannel)) {
			// current Axis will be removed
			setChannel(null);
		}
	}

	/*
	 * 
	 */
	private void checkForErrors() {
		// reset errors
		this.averageTextControlDecoration.hide();
		this.maxDeviationTextControlDecoration.hide();
		this.minimumTextControlDecoration.hide();
		this.maxAttemptsTextControlDecoration.hide();

		for(IModelError error : this.currentChannel.getModelErrors()) {
			if(error instanceof ChannelError) {
				final ChannelError channelError = (ChannelError) error;
				switch(channelError.getErrorType()) {
					case MAX_DEVIATION_NOT_POSSIBLE:
						this.maxDeviationTextControlDecoration.show();
						break;
					case MINIMUM_NOT_POSSIBLE:
						this.minimumTextControlDecoration.show();
						break;
				}
			}
		}
		if(this.currentChannel.getRedoControlEventManager().
							getModelErrors().size() > 0) {
			this.redoEventTabItem.setImage(
					PlatformUI.getWorkbench().getSharedImages().
					getImage(ISharedImages.IMG_OBJS_ERROR_TSK));
		} else {
			this.redoEventTabItem.setImage(null);
		}
	}
	
	/*
	 * 
	 */
	private void addListeners() {
		averageText.addModifyListener(averageTextModifyListener);
		averageText.addVerifyListener(averageTextVerifyListener);
		maxDeviationText.addModifyListener(maxDeviationTextModifyListener);
		maxDeviationText.addVerifyListener(maxDeviationTextVerifyListener);
		minimumText.addModifyListener(minimumTextModifyListener);
		minimumText.addVerifyListener(minimumTextVerifyListener);
		maxAttemptsText.addModifyListener(maxAttemptsTextModifyListener);
		maxAttemptsText.addVerifyListener(maxAttemptsTextVerifyListener);
		normalizeChannelCombo.addSelectionListener(
				normalizeChannelComboSelectionListener);
		
		confirmTriggerManualCheckBox.addSelectionListener(
				confirmTriggerManualCheckBoxSelectionListener);
		detectorReadyEventCheckBox.addSelectionListener(
				detectorReadyEventCheckBoxSelectionListener);
	}
	
	/*
	 * 
	 */
	private void removeListeners() {
		averageText.removeModifyListener(averageTextModifyListener);
		averageText.removeVerifyListener(averageTextVerifyListener);
		maxDeviationText.removeModifyListener(maxDeviationTextModifyListener);
		maxDeviationText.removeVerifyListener(maxDeviationTextVerifyListener);
		minimumText.removeModifyListener(minimumTextModifyListener);
		minimumText.removeVerifyListener(minimumTextVerifyListener);
		maxAttemptsText.removeModifyListener(maxAttemptsTextModifyListener);
		maxAttemptsText.removeVerifyListener(maxAttemptsTextVerifyListener);
		normalizeChannelCombo.removeSelectionListener(
				normalizeChannelComboSelectionListener);
		
		confirmTriggerManualCheckBox.removeSelectionListener(
				confirmTriggerManualCheckBoxSelectionListener);
		detectorReadyEventCheckBox.removeSelectionListener(
				detectorReadyEventCheckBoxSelectionListener);
	}
	
	private void suspendModelUpdateListener () {
		this.currentChannel.removeModelUpdateListener(this);
	}
	
	private void resumeModelUpdateListener () {
		this.currentChannel.addModelUpdateListener(this);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void updateEvent(ModelUpdateEvent modelUpdateEvent) {
		removeListeners();
		
		if(this.currentChannel != null) {
			// current channel set -> update widgets
			top.setVisible(true);
			
			// used to notice if the channel is being deleted
			this.scanModule.addPropertyChangeListener("removeChannel", this);
			
			// set the view title
			this.setPartName(
					currentChannel.getAbstractDevice().getName());
			
			// set average text
			this.averageText.setText(Integer.toString(
					this.currentChannel.getAverageCount()));

			// set max deviation
			if(this.currentChannel.getMaxDeviation() != 
					Double.NEGATIVE_INFINITY) {
				this.maxDeviationText.setText(
						Double.toString(this.currentChannel.getMaxDeviation()));
			} else {
				this.maxDeviationText.setText("");
			}
			
			// set minimum
			if(this.currentChannel.getMinimum() != Double.NEGATIVE_INFINITY) {
				this.minimumText.setText(Double.toString(
						this.currentChannel.getMinimum()));
			} else {
				this.minimumText.setText("");
			}
			
			// set max attempts
			if(this.currentChannel.getMaxAttempts() != Integer.MIN_VALUE) {
				this.maxAttemptsText.setText(
						Integer.toString(this.currentChannel.getMaxAttempts()));
			} else {
				this.maxAttemptsText.setText("");
			}
			
			availableDetectorChannels = this.scanModule.getChannels();
			String[] detectorItems = new String[availableDetectorChannels.length];
			for (int i = 0; i < availableDetectorChannels.length; ++i) {
				detectorItems[i] = availableDetectorChannels[i].
									getDetectorChannel().getName();
			}
			this.normalizeChannelCombo.setItems(detectorItems);
			this.normalizeChannelCombo.add("none");
			
			if (this.currentChannel.getNormalizeChannel() != null) {
				this.normalizeChannelCombo.setText(
						this.currentChannel.getNormalizeChannel().getName());
			}
			
			// set confirm trigger check box
			this.confirmTriggerManualCheckBox.setSelection(
					this.currentChannel.isConfirmTrigger());
			
			// set detector ready event check box
			this.detectorReadyEventCheckBox.setSelection(
					this.currentChannel.getDetectorReadyEvent() != null);
			
			this.redoEventComposite.setControlEventManager(
					this.currentChannel.getRedoControlEventManager());

			this.sc.setMinSize(this.top.computeSize(SWT.DEFAULT, SWT.DEFAULT));
			
			checkForErrors();
		} else {
			// this.currentChannel == null (no channel selected)
			this.setPartName("No Detector Channel selected");

			this.averageText.setText("");
			this.maxDeviationText.setText("");
			this.minimumText.setText("");
			this.maxAttemptsText.setText("");
			this.confirmTriggerManualCheckBox.setSelection(false);
			this.detectorReadyEventCheckBox.setSelection(false);
			
			this.redoEventComposite.setControlEventManager(null);
			
			top.setVisible(false);
		}

		// re-enable listeners
		addListeners();
		
	}

	/* ********************************************************************* */
	/* ******************************* Listeners *************************** */
	/* ********************************************************************* */
	
	/**
	 * {@link org.eclipse.swt.events.ModifyListener} of 
	 * <code>averageText</code>.
	 */
	private class AverageTextModifyListener implements ModifyListener {

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void modifyText(final ModifyEvent e) {
			logger.debug("average text modified");
			
			if(currentChannel != null) {
				try {
					currentChannel.setAverageCount(Integer.parseInt(
							averageText.getText()));
					logger.debug("set average text to: " + Integer.parseInt(
							averageText.getText()));
				} catch(final NumberFormatException ex) {
					// set default value (1)
					currentChannel.setAverageCount(1);
				}
			}
			checkForErrors();
		}
	}

	/**
	 * {@link org.eclipse.swt.events.ModifyListener} of 
	 * <code>maxDeviationText</code>.
	 */
	private class MaxDeviationTextModifyListener implements ModifyListener {

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void modifyText(final ModifyEvent e) {
			logger.debug("max deviation text modified");

			if(currentChannel != null) {
				if(maxDeviationText.getText().equals("")) {
					currentChannel.setMaxDeviation(Double.NEGATIVE_INFINITY);
				} else {
					try {
						currentChannel.setMaxDeviation(Double.parseDouble(
								maxDeviationText.getText()));
					} catch(final NumberFormatException ex) {
						currentChannel.setMaxDeviation(Double.NaN);
					}
				}
			}
			checkForErrors();
		}
	}
	
	/**
	 * {@link org.eclipse.swt.events.ModifyListener} of 
	 * <code>minimumText</code>.
	 */
	private class MinimumTextModifyListener implements ModifyListener {

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void modifyText(final ModifyEvent e) {
			logger.debug("minimum text modified");

			suspendModelUpdateListener();

			if(currentChannel != null) {
				if(minimumText.getText().equals("")) {
					currentChannel.setMinimum(Double.NEGATIVE_INFINITY);
				} else {
					try {
						currentChannel.setMinimum(Double.parseDouble(
								minimumText.getText()));
					} catch(final NumberFormatException ex) {
						currentChannel.setMinimum(Double.NaN);
					}
				}
			}
			checkForErrors();
			resumeModelUpdateListener();
		}
	}

	/**
	 * {@link org.eclipse.swt.events.ModifyListener} of 
	 * <code>maxAttemptsText</code>.
	 */
	private class MaxAttemptsTextModifyListener implements ModifyListener {

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void modifyText(final ModifyEvent e) {
			logger.debug("max attempts text modified");

			if(currentChannel != null) {
				if(maxAttemptsText.getText().equals("")) {
					currentChannel.setMaxAttempts(Integer.MIN_VALUE);
				} else {
					try {
						currentChannel.setMaxAttempts(Integer.parseInt(
								maxAttemptsText.getText()));
					} catch(final NumberFormatException ex) {
						currentChannel.setMaxAttempts(-1);
					}
				}
			}
			checkForErrors();
		}
	}

	/**
	 * {@link org.eclipse.swt.events.SelectionListener} of
	 * normalizeChannelCombo.
	 * 
	 * @author Marcus Michalsky
	 * @since 1.2
	 */
	private class NormalizeChannelComboSelectionListener implements
			SelectionListener {

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void widgetDefaultSelected(SelectionEvent e) {
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void widgetSelected(SelectionEvent e) {
			suspendModelUpdateListener();
			if (normalizeChannelCombo.getText().equals("none")) {
				currentChannel.setNormalizeChannel(null);
				normalizeChannelCombo.deselectAll();
			} else {
				currentChannel.setNormalizeChannel(
						availableDetectorChannels[normalizeChannelCombo
								.getSelectionIndex()].getDetectorChannel());
			}
			resumeModelUpdateListener();
		}
	}
	
	/**
	 * {@link org.eclipse.swt.events.SelectionListener} of 
	 * <code>confirmTriggerManualCheckBox</code>.
	 */
	private class ConfirmTriggerManualCheckBoxSelectionListener implements 
			SelectionListener {

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void widgetDefaultSelected(SelectionEvent e) {
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void widgetSelected(SelectionEvent e) {
			logger.debug("confirm trigger manual modified");
			if(currentChannel != null) {
				currentChannel.setConfirmTrigger(
						confirmTriggerManualCheckBox.getSelection());
			}
		}
	}

	/**
	 * {@link org.eclipse.swt.events.SelectionListener} of 
	 * <code>detectorReadyEventCheckBox</code>.
	 */
	private class DetectorReadyEventCheckBoxSelectionListener implements 
			SelectionListener {
		
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
			logger.debug("send detector ready event modified");
			// we create an event and add it to the list if selected 
			// or remove the event with same id from the list if deselected
			Event detReadyEvent = new Event(
					currentChannel.getAbstractDevice().getID(), 
					currentChannel.getAbstractDevice().getParent().getName(), 
					currentChannel.getAbstractDevice().getName(), 
					currentChannel.getScanModule().getChain().getId(), 
					currentChannel.getScanModule().getId());
			
			if(detectorReadyEventCheckBox.getSelection()) {
				currentChannel.getScanModule().getChain().
							   getScanDescription().add(detReadyEvent);
				currentChannel.setDetectorReadyEvent(detReadyEvent);
			} else {
				currentChannel.getScanModule().getChain().
							   getScanDescription().removeEventById(
									   detReadyEvent.getID());
				currentChannel.setDetectorReadyEvent(null);
			}
		}
	}

	/**
	 * {@link org.eclipse.swt.events.ExpandListener} of bar.
	 * 
	 * @author Marcus Michalsky
	 * @since 1.4
	 */
	private class BarExpandListener implements ExpandListener {

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void itemCollapsed(ExpandEvent e) {
			logger.debug("collapse");
			Point topPoint = top.computeSize(SWT.DEFAULT, SWT.DEFAULT);
			Point eventPoint = eventComposite.computeSize(SWT.DEFAULT,
					SWT.DEFAULT);
			sc.setMinSize(topPoint.x, topPoint.y - eventPoint.y);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void itemExpanded(ExpandEvent e) {
			logger.debug("expand");
			Point topPoint = top.computeSize(SWT.DEFAULT, SWT.DEFAULT);
			Point eventPoint = eventComposite.computeSize(SWT.DEFAULT,
					SWT.DEFAULT);
			sc.setMinSize(topPoint.x, topPoint.y + eventPoint.y);
		}
	}

	/**
	 * {@link org.eclipse.swt.events.VerifyListener}.
	 */
	private class TextNumberVerifyListener implements VerifyListener {

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void verifyText(VerifyEvent e) {
			switch (e.keyCode) {
			case SWT.BS: // Backspace
			case SWT.DEL: // Delete
			case SWT.HOME: // Home
			case SWT.END: // End
			case SWT.ARROW_LEFT: // Left arrow
			case SWT.ARROW_RIGHT: // Right arrow
				return;
			}

			if (!Character.isDigit(e.character)) {
				e.doit = false; // disallow the action
			}
		}
	}

	/**
	 * {@link org.eclipse.swt.events.VerifyListener}.
	 */
	private class TextDoubleVerifyListener implements VerifyListener {

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void verifyText(VerifyEvent e) {
			switch (e.keyCode) {
			case SWT.BS: // Backspace
			case SWT.DEL: // Delete
			case SWT.HOME: // Home
			case SWT.END: // End
			case SWT.ARROW_LEFT: // Left arrow
			case SWT.ARROW_RIGHT: // Right arrow
				return;
			}

			String oldText = ((Text) (e.widget)).getText();

			if (!Character.isDigit(e.character)) {
				if (e.character == '.') {
					// character . is a valid character, if he is not in the
					// old string
					if (oldText.contains("."))
						e.doit = false;
				} else if (e.character == '-') {
					// character - is a valid character as first sign and after
					// an e
					if (oldText.isEmpty()) {
						// oldText is emtpy, - is valid
					} else if ((((Text) e.widget).getSelection().x) == 0) {
						// - is the first sign an valid
					} else {
						// wenn das letzte Zeichen von oldText ein e ist,
						// ist das minus auch erlaubt
						int index = oldText.length();
						if (oldText.substring(index - 1).equals("e")) {
							// letzte Zeichen ist ein e und damit erlaubt
						} else {
							e.doit = false;
						}
					}
				} else if (e.character == 'e') {
					// character e is a valid character, if he is not in the
					// old string
					if (oldText.contains("e"))
						e.doit = false;
				} else {
					e.doit = false; // disallow the action
				}
			}
		}
	}
}