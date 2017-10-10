package de.ptb.epics.eve.viewer.views.messagesview.ui;

import org.apache.log4j.Logger;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Slider;
import org.eclipse.ui.IWorkbenchPart;

import de.ptb.epics.eve.viewer.Activator;
import de.ptb.epics.eve.viewer.views.messagesview.FilterSettings;
import de.ptb.epics.eve.viewer.views.messagesview.Levels;

/**
 * Dialog for choosing the sources messages should be shown from.
 * Choices are {@link de.ptb.epics.eve.viewer.views.messagesview.Sources}.
 * 
 * @author Marcus Michalsky
 * @since 1.4
 */
public class FilterDialog extends TitleAreaDialog {
	private static final Logger LOGGER = Logger.getLogger(FilterDialog.class
			.getName());
	private Button showApplicationMessagesButton;
	private ShowApplicationMessagesButtonSelectionListener 
			showApplicationMessagesButtonSelectionListener;
	private Button showEngineMessagesButton;
	private ShowEngineMessagesButtonSelectionListener
			showEngineMessagesButtonSelectionListener;
	
	private Slider slider;
	private SliderSelectionListener sliderSelectionListener;
	
	private boolean showApplicationMessages;
	private boolean showEngineMessages;
	
	private Levels level;
	private Label levelLabel;
	
	/**
	 * Constructor.
	 * 
	 * @param shell  the shell
	 */
	public FilterDialog(final Shell shell) {
		super(shell);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Control createDialogArea(final Composite parent) {
		Composite composite = new Composite(parent, SWT.NONE);
		GridLayout gridLayout = new GridLayout();
		gridLayout.marginLeft = 10;
		gridLayout.marginTop = 10;
		composite.setLayout(gridLayout);
		GridData gridData = new GridData();
		gridData.grabExcessHorizontalSpace = true;
		gridData.grabExcessVerticalSpace = true;
		composite.setLayoutData(gridData);

		Group sourceGroup = new Group(composite, SWT.SHADOW_ETCHED_IN);
		sourceGroup.setText("Source");
		gridLayout = new GridLayout();
		sourceGroup.setLayout(gridLayout);
		gridData = new GridData();
		gridData.grabExcessHorizontalSpace = true;
		gridData.grabExcessVerticalSpace = true;
		sourceGroup.setLayoutData(gridData);
		
		this.showApplicationMessagesButton = new Button(sourceGroup, SWT.CHECK);
		this.showApplicationMessagesButton
				.setText("Show messages from Viewer");
		this.showApplicationMessagesButtonSelectionListener = 
				new ShowApplicationMessagesButtonSelectionListener();
		this.showApplicationMessagesButton.addSelectionListener(
				showApplicationMessagesButtonSelectionListener);

		this.showEngineMessagesButton = new Button(sourceGroup, SWT.CHECK);
		this.showEngineMessagesButton.setText("Show messages from Engine");
		this.showEngineMessagesButtonSelectionListener = 
				new ShowEngineMessagesButtonSelectionListener();
		this.showEngineMessagesButton.addSelectionListener(
				showEngineMessagesButtonSelectionListener);
		
		Group levelGroup = new Group(composite, SWT.SHADOW_ETCHED_IN);
		levelGroup.setText("Level");
		gridLayout = new GridLayout();
		gridLayout.numColumns = 2;
		levelGroup.setLayout(gridLayout);
		gridData = new GridData();
		gridData.grabExcessHorizontalSpace = true;
		gridData.grabExcessVerticalSpace = true;
		levelGroup.setLayoutData(gridData);
		
		this.slider = new Slider(levelGroup, SWT.HORIZONTAL);
		this.slider.setValues(3, 1, Levels.values().length, 1, 1, 1);
		gridData = new GridData();
		gridData.grabExcessHorizontalSpace = true;
		gridData.grabExcessVerticalSpace = true;
		gridData.widthHint = 200;
		this.slider.setLayoutData(gridData);
		this.sliderSelectionListener = new SliderSelectionListener();
		this.slider.addSelectionListener(sliderSelectionListener);
		levelLabel = new Label(levelGroup, SWT.NONE);
		levelLabel.setText("DEBUG");
		levelLabel.pack();
		
		setTitle("Messages Filter");
		setMessage("Choose source and boundary of messages that should be shown.", 
				IMessageProvider.INFORMATION);
		this.initFields();
		return composite;
	}

	/*
	 * 
	 */
	private void initFields() {
		IWorkbenchPart part = Activator.getDefault().getWorkbench().
				getActiveWorkbenchWindow().getActivePage().getActivePart();
		if (!(part instanceof MessagesView)) {
			LOGGER.error("active part is not MessagesView!");
			return;
		}
		FilterSettings filterSettings = ((MessagesView)part).getFilterSettings();
		this.showApplicationMessages = filterSettings.isShowViewerMessages(); 
		this.showApplicationMessagesButton.setSelection(
				this.showApplicationMessages);
		this.showEngineMessages = filterSettings.isShowEngineMessages();
		this.showEngineMessagesButton.setSelection(this.showEngineMessages);
		
		this.level = filterSettings.getMessageThreshold();
		
		switch(this.level) {
		case DEBUG:
			slider.setSelection(5);
			levelLabel.setText("DEBUG");
			break;
		case INFO:
			slider.setSelection(4);
			levelLabel.setText("INFO");
			break;
		case MINOR:
			slider.setSelection(3);
			levelLabel.setText("MINOR");
			break;
		case ERROR:
			slider.setSelection(2);
			levelLabel.setText("ERROR");
			break;
		case FATAL:
			slider.setSelection(1);
			levelLabel.setText("FATAL");
			break;
		case SYSTEM:
			break;
		}
		levelLabel.pack();
		levelLabel.getParent().layout();
	}
	
	/**
	 * Checks whether application messages are shown.
	 * 
	 * @return <code>true</code> if application messages are shown, 
	 * 			<code>false</code> otherwise
	 */
	public boolean isShowApplicationMessages() {
		return this.showApplicationMessages;
	}

	/**
	 * Checks whether engine messages are shown.
	 * 
	 * @return <code>true</code> if engine messages are shown, 
	 * 			<code>false</code>otherwise
	 */
	public boolean isShowEngineMessages() {
		return this.showEngineMessages;
	}
	
	/**
	 * Returns the level (as in {@link de.ptb.epics.eve.viewer.views.messagesview.Levels}) 
	 * of the dialog.
	 * 
	 * @return the level (as in {@link de.ptb.epics.eve.viewer.views.messagesview.Levels})
	 */
	public Levels getLevel() {
		return this.level;
	}
	
	/* ********************************************************************* */
	/* ************************* Listeners ********************************* */
	/* ********************************************************************* */
	
	/**
	 * {@link org.eclipse.swt.events.SelectionListener} of 
	 * <code>showApplicationMessagesButton</code>.
	 * 
	 * @author Marcus Michalsky
	 * @since 1.4
	 */
	private class ShowApplicationMessagesButtonSelectionListener implements
			SelectionListener {

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void widgetSelected(SelectionEvent e) {
			showApplicationMessages = showApplicationMessagesButton
					.getSelection();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void widgetDefaultSelected(SelectionEvent e) {
		}
	}
	
	/**
	 * {@link org.eclipse.swt.events.SelectionListener} of 
	 * <code>showEngineMessagesButton</code>.
	 * 
	 * @author Marcus Michalsky
	 * @since 1.4
	 */
	private class ShowEngineMessagesButtonSelectionListener implements
			SelectionListener {

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void widgetSelected(SelectionEvent e) {
			showEngineMessages = showEngineMessagesButton.getSelection();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void widgetDefaultSelected(SelectionEvent e) {
		}
	}
	
	/**
	 * {@link org.eclipse.swt.events.SelectionListener} of slider
	 * 
	 * @author Marcus Michalsky
	 * @since 1.4
	 */
	private class SliderSelectionListener implements SelectionListener {

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void widgetSelected(SelectionEvent e) {
			switch(slider.getSelection()) {
			case 1:
				level = Levels.FATAL;
				levelLabel.setText("FATAL");
				break;
			case 2:
				level = Levels.ERROR;
				levelLabel.setText("ERROR");
				break;
			case 3:
				level = Levels.MINOR;
				levelLabel.setText("MINOR");
				break;
			case 4:
				level = Levels.INFO;
				levelLabel.setText("INFO");
				break;
			case 5:
				level = Levels.DEBUG;
				levelLabel.setText("DEBUG");
				break;
			}
			levelLabel.pack();
			levelLabel.getParent().layout();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void widgetDefaultSelected(SelectionEvent e) {
		}
	}
}