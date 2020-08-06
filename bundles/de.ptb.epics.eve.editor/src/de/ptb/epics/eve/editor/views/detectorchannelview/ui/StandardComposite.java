package de.ptb.epics.eve.editor.views.detectorchannelview.ui;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.apache.log4j.Logger;
import org.eclipse.core.databinding.Binding;
import org.eclipse.core.databinding.UpdateValueStrategy;
import org.eclipse.core.databinding.beans.BeanProperties;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.jface.databinding.fieldassist.ControlDecorationSupport;
import org.eclipse.jface.databinding.swt.WidgetProperties;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IViewPart;

import de.ptb.epics.eve.data.EventImpacts;
import de.ptb.epics.eve.data.scandescription.Channel;
import de.ptb.epics.eve.data.scandescription.ScanModule;
import de.ptb.epics.eve.data.scandescription.channelmode.ChannelModes;
import de.ptb.epics.eve.data.scandescription.channelmode.StandardMode;
import de.ptb.epics.eve.data.scandescription.updatenotification.ControlEventManager;
import de.ptb.epics.eve.data.scandescription.updatenotification.ControlEventTypes;
import de.ptb.epics.eve.data.scandescription.updatenotification.IModelUpdateListener;
import de.ptb.epics.eve.data.scandescription.updatenotification.ModelUpdateEvent;
import de.ptb.epics.eve.editor.views.detectorchannelview.AverageTargetToModelConverter;
import de.ptb.epics.eve.editor.views.detectorchannelview.AverageTargetToModelValidator;
import de.ptb.epics.eve.editor.views.detectorchannelview.DoubleModelToTargetConverter;
import de.ptb.epics.eve.editor.views.detectorchannelview.DoubleTargetToModelConverter;
import de.ptb.epics.eve.editor.views.detectorchannelview.MaxAttemptsModelToTargetConverter;
import de.ptb.epics.eve.editor.views.detectorchannelview.MaxAttemptsTargetToModelConverter;
import de.ptb.epics.eve.editor.views.detectorchannelview.MaxAttemptsTargetToModelValidator;
import de.ptb.epics.eve.editor.views.detectorchannelview.MaxDeviationTargetToModelValidator;
import de.ptb.epics.eve.editor.views.detectorchannelview.MinimumTargetToModelValidator;
import de.ptb.epics.eve.editor.views.eventcomposite.EventComposite;
import de.ptb.epics.eve.util.ui.swt.TextSelectAllFocusListener;
import de.ptb.epics.eve.util.ui.swt.TextSelectAllMouseListener;

/**
 * @author Marcus Michalsky
 * @since 1.27
 */
public class StandardComposite extends DetectorChannelViewComposite 
		implements PropertyChangeListener, IModelUpdateListener {
	private static final Logger LOGGER = Logger.getLogger(
			StandardComposite.class.getName());
	
	private static final String TOOLTIP_AVERAGE = "Determines how many " +
		"channel readings are taken to calculate the average. ";
	private static final String TOOLTIP_MAX_DEVIATION = "The deviation of " +
		"the first two measured values must be less or equal then the given " +
		"value in percent to be valid. If that constraint ist not fulfilled, " +
		"the first measured value is discarded and a new value is taken " +
		"until the condition is met.";
	private static final String TOOLTIP_MINIMUM = "If the first measured " +
		"value (absolute) is greater than or equal the given value a " +
		"tolerance check is conducted. ";
	private static final String TOOLTIP_MAX_ATTEMPTS = "If the tolerance " +
		"check failed the given value amount of times, the check is " +
		"stopped an the next values are used for the average " +
		"calculation. If Max. Attempts = 0, no tolerance check is conducted.";
	private static final String TOOLTIP_DEFERRED = "If set, this channel is " + 
		"triggered after all other channels in the same scan module " + 
		"(which are not deferred) are read";
	
	private Label averageLabel;
	private Text averageText;
	private Label maxDeviationLabel;
	private Text maxDeviationText;
	private Label minimumLabel;
	private Text minimumText;
	private Label maxAttemptsLabel;
	private Text maxAttemptsText;
	private Button deferredCheckBox;
	private CTabFolder eventsTabFolder;
	private EventComposite redoEventComposite;
	
	private Binding averageBinding;
	private IObservableValue averageTargetObservable;
	private IObservableValue averageModelObservable;

	private Binding maxDeviationBinding;
	private IObservableValue maxDeviationTargetObservable;
	private IObservableValue maxDeviationModelObservable;

	private Binding minimumBinding;
	private IObservableValue minimumTargetObservable;
	private IObservableValue minimumModelObservable;

	private Binding maxAttemptsBinding;
	private IObservableValue maxAttemptsTargetObservable;
	private IObservableValue maxAttemptsModelObservable;

	private Binding deferredBinding;
	private IObservableValue deferredTriggerTargetObservable;
	private IObservableValue deferredTriggerModelObservable;
	
	private Channel currentChannel;
	private ControlEventManager redoControlEventManager;
	
	public StandardComposite(Composite parent, int style, IViewPart parentView) {
		super(parent, style, parentView);
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 2;
		this.setLayout(gridLayout);
		
		this.averageLabel = new Label(this, SWT.NONE);
		this.averageLabel.setText("Average:");
		this.averageLabel.setToolTipText(TOOLTIP_AVERAGE);
		GridData gridData;
		gridData = new GridData();
		gridData.horizontalAlignment = GridData.FILL;
		this.averageLabel.setLayoutData(gridData);

		this.averageText = new Text(this, SWT.BORDER);
		gridData = new GridData();
		gridData.horizontalAlignment = GridData.FILL;
		gridData.horizontalIndent = 7;
		gridData.grabExcessHorizontalSpace = true;
		this.averageText.setLayoutData(gridData);
		this.averageText.addFocusListener(new TextSelectAllFocusListener(this.averageText));
		this.averageText.addMouseListener(new TextSelectAllMouseListener(this.averageText));
		this.averageText.addFocusListener(new TextFocusListener(this.averageText));
		
		this.maxDeviationLabel = new Label(this, SWT.NONE);
		this.maxDeviationLabel.setText("Max. Deviation (%):");
		this.maxDeviationLabel.setToolTipText(TOOLTIP_MAX_DEVIATION);
		gridData = new GridData();
		gridData.horizontalAlignment = GridData.FILL;
		this.maxDeviationLabel.setLayoutData(gridData);

		this.maxDeviationText = new Text(this, SWT.BORDER);
		gridData = new GridData();
		gridData.horizontalAlignment = GridData.FILL;
		gridData.horizontalIndent = 7;
		gridData.grabExcessHorizontalSpace = true;
		this.maxDeviationText.setLayoutData(gridData);
		this.maxDeviationText.addFocusListener(new TextSelectAllFocusListener(this.maxDeviationText));
		this.maxDeviationText.addMouseListener(new TextSelectAllMouseListener(this.maxDeviationText));
		this.maxDeviationText.addFocusListener(new TextFocusListener(this.maxDeviationText));
		
		this.minimumLabel = new Label(this, SWT.NONE);
		this.minimumLabel.setText("Minimum:");
		this.minimumLabel.setToolTipText(TOOLTIP_MINIMUM);
		gridData = new GridData();
		gridData.horizontalAlignment = GridData.FILL;
		this.minimumLabel.setLayoutData(gridData);

		this.minimumText = new Text(this, SWT.BORDER);
		gridData = new GridData();
		gridData.horizontalAlignment = GridData.FILL;
		gridData.horizontalIndent = 7;
		gridData.grabExcessHorizontalSpace = true;
		this.minimumText.setLayoutData(gridData);
		this.minimumText.addFocusListener(new TextSelectAllFocusListener(this.minimumText));
		this.minimumText.addMouseListener(new TextSelectAllMouseListener(this.minimumText));
		this.minimumText.addFocusListener(new TextFocusListener(this.minimumText));
		
		this.maxAttemptsLabel = new Label(this, SWT.NONE);
		this.maxAttemptsLabel.setText("Max. Attempts:");
		this.maxAttemptsLabel.setToolTipText(TOOLTIP_MAX_ATTEMPTS);
		gridData = new GridData();
		gridData.horizontalAlignment = GridData.FILL;
		this.maxAttemptsLabel.setLayoutData(gridData);

		this.maxAttemptsText = new Text(this, SWT.BORDER);
		gridData = new GridData();
		gridData.horizontalAlignment = GridData.FILL;
		gridData.horizontalIndent = 7;
		gridData.grabExcessHorizontalSpace = true;
		this.maxAttemptsText.setLayoutData(gridData);
		this.maxAttemptsText.addFocusListener(new TextSelectAllFocusListener(this.maxAttemptsText));
		this.maxAttemptsText.addMouseListener(new TextSelectAllMouseListener(this.maxAttemptsText));
		this.maxAttemptsText.addFocusListener(new TextFocusListener(this.maxAttemptsText));
		
		this.deferredCheckBox = new Button(this, SWT.CHECK);
		this.deferredCheckBox.setText("Deferred Trigger");
		this.deferredCheckBox.setToolTipText(TOOLTIP_DEFERRED);
		gridData = new GridData();
		gridData.horizontalAlignment = GridData.FILL;
		gridData.horizontalSpan = 2;
		gridData.grabExcessHorizontalSpace = true;
		this.deferredCheckBox.setLayoutData(gridData);
		
		this.eventsTabFolder = new CTabFolder(this, SWT.FLAT);
		this.eventsTabFolder.setSimple(true);
		this.eventsTabFolder.setBorderVisible(true);
		gridData = new GridData();
		gridData.horizontalSpan = 2;
		gridData.grabExcessVerticalSpace = true;
		gridData.grabExcessHorizontalSpace = true;
		gridData.minimumHeight = 150;
		gridData.verticalAlignment = GridData.FILL;
		gridData.horizontalAlignment = GridData.FILL;
		eventsTabFolder.setLayoutData(gridData);

		this.redoEventComposite = new EventComposite(eventsTabFolder, SWT.NONE, ControlEventTypes.CONTROL_EVENT, 
				this.getParentView());

		CTabItem redoEventTabItem = new CTabItem(eventsTabFolder, SWT.FLAT);
		redoEventTabItem.setText("Redo");
		redoEventTabItem.setToolTipText("Repeat the current reading " + "of the channel, if redo event occurs");
		redoEventTabItem.setControl(redoEventComposite);
		
		this.eventsTabFolder.setSelection(redoEventTabItem);
		
		// set the table viewer of the event composite as selection provider
		// (used by add/delete event commands)
		this.getParentView().getSite().setSelectionProvider(
				this.redoEventComposite.getTableViewer());
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setChannel(Channel channel) {
		this.reset();
		if (channel == null) {
			LOGGER.debug("set channel (null)");
			return;
		}
		if (!(channel.getChannelMode().equals(ChannelModes.STANDARD))) {
			LOGGER.warn("invalid channel mode");
			return;
		}
		this.currentChannel = channel;
		this.redoControlEventManager = this.currentChannel.getRedoControlEventManager();
		LOGGER.debug("set channel (" + channel.getAbstractDevice().getFullIdentifyer() + ")");
		if (this.currentChannel != null) {
			this.redoControlEventManager.addModelUpdateListener(this);
			this.currentChannel.getScanModule().addPropertyChangeListener(
					ScanModule.REMOVE_CHANNEL_PROP, this);
			
			this.createBinding();
			this.redoEventComposite.setEvents(this.currentChannel, EventImpacts.REDO);
		}
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void createBinding() {
		this.averageTargetObservable = WidgetProperties.text(SWT.Modify).
				observe(this.averageText);
		this.averageModelObservable = BeanProperties.value(
				StandardMode.AVERAGE_COUNT_PROP, Integer.class).
				observe(this.currentChannel);
		this.averageBinding = this.getContext().bindValue(
				averageTargetObservable, averageModelObservable,
				new UpdateValueStrategy(UpdateValueStrategy.POLICY_UPDATE).
					setConverter(new AverageTargetToModelConverter()).
					setAfterGetValidator(new AverageTargetToModelValidator()),
				new UpdateValueStrategy(UpdateValueStrategy.POLICY_UPDATE));
		ControlDecorationSupport.create(this.averageBinding, SWT.LEFT);
		
		this.maxDeviationTargetObservable = WidgetProperties.text(SWT.Modify).
				observe(this.maxDeviationText);
		this.maxDeviationModelObservable = BeanProperties.value(
				StandardMode.MAX_DEVIATION_PROP, Double.class).
				observe(this.currentChannel);
		this.maxDeviationBinding = this.getContext().bindValue(
				maxDeviationTargetObservable, maxDeviationModelObservable, 
				new UpdateValueStrategy(UpdateValueStrategy.POLICY_UPDATE).
					setConverter(new DoubleTargetToModelConverter()).
					setAfterGetValidator(new MaxDeviationTargetToModelValidator()),
				new UpdateValueStrategy(UpdateValueStrategy.POLICY_UPDATE).
					setConverter(new DoubleModelToTargetConverter()));
		ControlDecorationSupport.create(this.maxDeviationBinding, SWT.LEFT);
		
		this.minimumTargetObservable = WidgetProperties.text(SWT.Modify).
				observe(this.minimumText);
		this.minimumModelObservable = BeanProperties.value(
				StandardMode.MINIMUM_PROP, Double.class).
				observe(this.currentChannel);
		this.minimumBinding = this.getContext().bindValue(
				minimumTargetObservable, minimumModelObservable,
				new UpdateValueStrategy(UpdateValueStrategy.POLICY_UPDATE).
				setConverter(new DoubleTargetToModelConverter()).
				setAfterGetValidator(new MinimumTargetToModelValidator()),
			new UpdateValueStrategy(UpdateValueStrategy.POLICY_UPDATE).
				setConverter(new DoubleModelToTargetConverter()));
		ControlDecorationSupport.create(this.minimumBinding, SWT.LEFT);
		
		this.maxAttemptsTargetObservable = WidgetProperties.text(SWT.Modify).
				observe(this.maxAttemptsText);
		this.maxAttemptsModelObservable = BeanProperties.value(
				StandardMode.MAX_ATTEMPTS_PROP, Integer.class).
				observe(this.currentChannel);
		this.maxAttemptsBinding = this.getContext().bindValue(
				maxAttemptsTargetObservable, maxAttemptsModelObservable,
				new UpdateValueStrategy(UpdateValueStrategy.POLICY_UPDATE).
					setConverter(new MaxAttemptsTargetToModelConverter()).
					setAfterGetValidator(new MaxAttemptsTargetToModelValidator()),
				new UpdateValueStrategy(UpdateValueStrategy.POLICY_UPDATE).
					setConverter(new MaxAttemptsModelToTargetConverter()));
		ControlDecorationSupport.create(this.maxAttemptsBinding, SWT.LEFT);
		
		this.deferredTriggerTargetObservable = WidgetProperties.selection().
				observe(this.deferredCheckBox);
		this.deferredTriggerModelObservable = BeanProperties.value(
				StandardMode.DEFERRED_PROP, Boolean.class).
				observe(this.currentChannel);
		this.deferredBinding = this.getContext().bindValue(
				deferredTriggerTargetObservable, deferredTriggerModelObservable);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void reset() {
		if (this.currentChannel != null) {
			this.getContext().removeBinding(averageBinding);
			this.averageBinding.dispose();
			this.averageTargetObservable.dispose();
			this.averageModelObservable.dispose();
			
			this.getContext().removeBinding(maxDeviationBinding);
			this.maxDeviationBinding.dispose();
			this.maxDeviationTargetObservable.dispose();
			this.maxDeviationModelObservable.dispose();
			
			this.getContext().removeBinding(minimumBinding);
			this.minimumBinding.dispose();
			this.minimumTargetObservable.dispose();
			this.minimumModelObservable.dispose();
			
			this.getContext().removeBinding(maxAttemptsBinding);
			this.maxAttemptsBinding.dispose();
			this.maxAttemptsTargetObservable.dispose();
			this.maxAttemptsModelObservable.dispose();
			
			this.getContext().removeBinding(deferredBinding);
			this.deferredBinding.dispose();
			this.deferredTriggerTargetObservable.dispose();
			this.deferredTriggerModelObservable.dispose();
			
			this.redoControlEventManager.removeModelUpdateListener(this);
			this.currentChannel.getScanModule().removePropertyChangeListener(
					ScanModule.REMOVE_CHANNEL_PROP, this);
		}
		this.currentChannel = null;
		this.redoEventComposite.setEvents(this.currentChannel, null);
		this.redraw();
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void propertyChange(PropertyChangeEvent e) {
		if (e.getOldValue().equals(this.currentChannel)) {
			// current Axis will be removed
			setChannel(null);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void updateEvent(ModelUpdateEvent modelUpdateEvent) {
		if (this.currentChannel != null) {
			this.redoEventComposite.setEvents(
					this.currentChannel, EventImpacts.REDO);
		} else {
			this.redoEventComposite.setEvents(
					this.currentChannel, null);
		}
	}
	
	/**
	 * @author Marcus Michalsky
	 * @since 1.8
	 */
	private class TextFocusListener implements FocusListener {

		private Text widget;

		/**
		 * @param widget
		 *            the widget to observe
		 */
		public TextFocusListener(Text widget) {
			this.widget = widget;
		}

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
			if (widget == averageText) {
				averageText.setSelection(0, 0);
				averageBinding.updateModelToTarget();
			} else if (widget == maxDeviationText) {
				maxDeviationText.setSelection(0, 0);
				maxDeviationBinding.updateModelToTarget();
			} else if (widget == minimumText) {
				minimumText.setSelection(0, 0);
				minimumBinding.updateModelToTarget();
			} else if (widget == maxAttemptsText) {
				maxAttemptsText.setSelection(0, 0);
				maxAttemptsBinding.updateModelToTarget();
			}
		}
	}
}