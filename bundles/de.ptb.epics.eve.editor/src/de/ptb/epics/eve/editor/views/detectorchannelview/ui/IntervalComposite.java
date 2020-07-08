package de.ptb.epics.eve.editor.views.detectorchannelview.ui;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;
import java.util.Locale;

import org.apache.log4j.Logger;
import org.eclipse.core.databinding.Binding;
import org.eclipse.core.databinding.UpdateValueStrategy;
import org.eclipse.core.databinding.beans.BeanProperties;
import org.eclipse.core.databinding.conversion.NumberToStringConverter;
import org.eclipse.core.databinding.conversion.StringToNumberConverter;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.jface.databinding.fieldassist.ControlDecorationSupport;
import org.eclipse.jface.databinding.swt.WidgetProperties;
import org.eclipse.jface.fieldassist.ControlDecoration;
import org.eclipse.jface.fieldassist.FieldDecorationRegistry;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IViewPart;

import com.ibm.icu.text.NumberFormat;

import de.ptb.epics.eve.data.scandescription.Channel;
import de.ptb.epics.eve.data.scandescription.channelmode.ChannelModes;
import de.ptb.epics.eve.data.scandescription.channelmode.IntervalMode;
import de.ptb.epics.eve.editor.Activator;
import de.ptb.epics.eve.editor.views.detectorchannelview.TriggerIntervalTargetToModelValidator;


/**
 * @author Marcus Michalsky
 * @since 1.27
 */
public class IntervalComposite extends DetectorChannelViewComposite 
		implements PropertyChangeListener {
	private static final Logger LOGGER = Logger.getLogger(
			IntervalComposite.class.getName());
	
	private Label triggerIntervalLabel;
	private Text triggerIntervalText;
	private Label stoppedByLabel;
	private ComboViewer stoppedByComboViewer;
	private ControlDecoration stoppedByComboControlDecoration;
	private Image errorImage;
	private Label triggerInfoImage;
	private Label triggerInfoText;
	private Label redoInfoImage;
	private Label redoInfoText;
	
	private IObservableValue triggerIntervalTargetObservable;
	private IObservableValue triggerIntervalModelObservable;
	private Binding triggerIntervalBinding;
	
	private Channel currentChannel;

	private Composite redoInfoComposite;

	private Composite triggerInfoComposite;
	
	public IntervalComposite(Composite parent, int style, IViewPart parentView) {
		super(parent, style, parentView);
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 2;
		this.setLayout(gridLayout);
		
		this.triggerIntervalLabel = new Label(this, SWT.NONE);
		this.triggerIntervalLabel.setText("Trigger Interval:");
		
		this.triggerIntervalText = new Text(this, SWT.BORDER);
		GridData gridData = new GridData();
		gridData.horizontalAlignment = GridData.FILL;
		gridData.horizontalIndent = 7;
		gridData.grabExcessHorizontalSpace = true;
		this.triggerIntervalText.setLayoutData(gridData);
		
		this.stoppedByLabel = new Label(this, SWT.NONE);
		this.stoppedByLabel.setText("Stopped By:");
		
		this.stoppedByComboViewer = new ComboViewer(
				this, SWT.BORDER | SWT.READ_ONLY);
		gridData = new GridData();
		gridData.horizontalAlignment = GridData.FILL;
		gridData.horizontalIndent = 7;
		gridData.grabExcessHorizontalSpace = true;
		this.stoppedByComboViewer.getCombo().setLayoutData(gridData);
		this.stoppedByComboViewer.setContentProvider(
				ArrayContentProvider.getInstance());
		this.stoppedByComboViewer.setLabelProvider(new LabelProvider() {
			@Override
			public String getText(Object element) {
				if (element == null) {
					return "";
				}
				return ((Channel)element).getAbstractDevice().getName();
			}
		});
		this.stoppedByComboViewer.addSelectionChangedListener(
				new ISelectionChangedListener() {
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				LOGGER.debug("Stopped By: " + event.getSelection().toString());
				if (event.getSelection().isEmpty()) {
					currentChannel.setStoppedBy((null));
				} else {
					currentChannel.setStoppedBy(((Channel)((IStructuredSelection)
						event.getSelection()).getFirstElement()).getDetectorChannel());
				}
				setInfos();
			}
		});
		
		this.errorImage = FieldDecorationRegistry.getDefault().getFieldDecoration(
				FieldDecorationRegistry.DEC_ERROR).getImage();
		this.stoppedByComboControlDecoration = new ControlDecoration(
				this.stoppedByComboViewer.getCombo(), SWT.LEFT);
		this.stoppedByComboControlDecoration.setDescriptionText("stopped by is mandatory");
		this.stoppedByComboControlDecoration.setImage(errorImage);
		this.stoppedByComboControlDecoration.hide();
		
		triggerInfoComposite = new Composite(this, SWT.NONE);
		gridData = new GridData();
		gridData.horizontalSpan = 2;
		gridData.grabExcessHorizontalSpace = true;
		gridData.horizontalAlignment = SWT.LEFT;
		gridData.verticalAlignment = SWT.TOP;
		triggerInfoComposite.setLayoutData(gridData);
		
		triggerInfoComposite.setLayout(new RowLayout());
		
		this.triggerInfoImage = new Label(triggerInfoComposite, SWT.NONE);
		this.triggerInfoImage.setImage(Activator.getDefault().getImageRegistry().get("INFO"));
		gridData = new GridData();
		gridData.verticalAlignment = SWT.TOP;
		this.triggerInfoImage.setVisible(false);
		
		this.triggerInfoText = new Label(triggerInfoComposite, SWT.WRAP);
		this.triggerInfoText.setText("deferred trigger");
		this.triggerInfoText.setVisible(false);
		
		redoInfoComposite = new Composite(this, SWT.NONE);
		gridData = new GridData();
		gridData.horizontalSpan = 2;
		gridData.grabExcessHorizontalSpace = true;
		gridData.horizontalAlignment = SWT.LEFT;
		gridData.verticalAlignment = SWT.TOP;
		redoInfoComposite.setLayoutData(gridData);
		
		redoInfoComposite.setLayout(new RowLayout());
		
		this.redoInfoImage = new Label(redoInfoComposite, SWT.NONE);
		this.redoInfoImage.setImage(Activator.getDefault().getImageRegistry().get("INFO"));
		gridData = new GridData();
		gridData.verticalAlignment = SWT.TOP;
		this.redoInfoImage.setVisible(false);
		
		this.redoInfoText = new Label(redoInfoComposite, SWT.WRAP);
		this.redoInfoText.setText("redo events");
		this.redoInfoText.setVisible(false);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setChannel(Channel channel) {
		this.reset();
		if (channel == null) {
			return;
		}
		if (!channel.getChannelMode().equals(ChannelModes.INTERVAL)) {
			LOGGER.error("wrong channel mode!");
			return;
		}
		this.currentChannel = channel;
		this.createBinding();
		this.currentChannel.addPropertyChangeListener(IntervalMode.STOPPED_BY_PROP, this);
		List<Channel> entries = this.currentChannel.
				getScanModule().getValidStoppedByChannels(currentChannel);
		this.stoppedByComboViewer.setInput(entries);
		if (!entries.isEmpty()) {
			this.stoppedByComboViewer.insert(null, 0);
		}
		if (this.currentChannel.getStoppedBy() != null) {
			this.stoppedByComboViewer.getCombo().setText(
					this.currentChannel.getStoppedBy().getName());
			this.stoppedByComboControlDecoration.hide();
		} else {
			this.stoppedByComboControlDecoration.show();
		}
		this.setInfos();
		this.triggerIntervalText.addFocusListener(new FocusAdapter() {
			@Override
			public void focusLost(FocusEvent e) {
				triggerIntervalBinding.updateModelToTarget();
			}
		});
	}
	
	private void setInfos() {
		if (this.currentChannel.getStoppedBy() != null) {
			Channel meanChannel = this.currentChannel.getScanModule().
					getChannel(this.currentChannel.getStoppedBy());
			if (meanChannel != null && meanChannel.isDeferred()) {
				this.triggerInfoText.setText("Trigger is deferred (since " + 
						meanChannel.getDetectorChannel().getName() + " also is).");
				this.triggerInfoImage.setVisible(true);
				this.triggerInfoText.setVisible(true);
				((GridData)this.triggerInfoComposite.getLayoutData()).exclude = false;
			} else {
				this.triggerInfoImage.setVisible(false);
				this.triggerInfoText.setVisible(false);
				((GridData)this.triggerInfoComposite.getLayoutData()).exclude = true;
				this.triggerInfoComposite.moveBelow(redoInfoComposite);
			}
			if (meanChannel != null && !meanChannel.getRedoEvents().isEmpty()) {
				this.redoInfoText.setText("Occuring redo events of " + 
						meanChannel.getDetectorChannel().getName() + 
							" will restart the mean calculation.");
				this.redoInfoImage.setVisible(true);
				this.redoInfoText.setVisible(true);
				((GridData)this.redoInfoComposite.getLayoutData()).exclude = false;
			} else {
				this.redoInfoImage.setVisible(false);
				this.redoInfoText.setVisible(false);
				((GridData)this.redoInfoComposite.getLayoutData()).exclude = true;
				this.redoInfoComposite.moveBelow(triggerInfoComposite);
			}
		} else {
			this.triggerInfoImage.setVisible(false);
			this.triggerInfoText.setVisible(false);
			this.redoInfoImage.setVisible(false);
			this.redoInfoText.setVisible(false);
			((GridData)this.triggerInfoComposite.getLayoutData()).exclude = true;
			((GridData)this.redoInfoComposite.getLayoutData()).exclude = true;
		}
		this.pack();
		this.getParent().layout();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void createBinding() {
		this.triggerIntervalTargetObservable = WidgetProperties.text(SWT.Modify).
				observe(this.triggerIntervalText);
		this.triggerIntervalModelObservable = BeanProperties.value(
				IntervalMode.TRIGGER_INTERVAL_PROP).observe(this.currentChannel);
		this.triggerIntervalBinding = getContext().bindValue(
				triggerIntervalTargetObservable, 
				triggerIntervalModelObservable, 
				new UpdateValueStrategy(UpdateValueStrategy.POLICY_UPDATE).
				setConverter(StringToNumberConverter.toDouble(
						NumberFormat.getInstance(Locale.US), false)).
				setAfterGetValidator(new TriggerIntervalTargetToModelValidator()),
				new UpdateValueStrategy(UpdateValueStrategy.POLICY_UPDATE).
				setConverter(NumberToStringConverter.fromDouble(
						NumberFormat.getInstance(Locale.US), false)));
		ControlDecorationSupport.create(this.triggerIntervalBinding, SWT.LEFT);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void reset() {
		if (this.currentChannel != null) {
			getContext().removeBinding(this.triggerIntervalBinding);
			this.triggerIntervalBinding.dispose();
			this.triggerIntervalModelObservable.dispose();
			this.triggerIntervalTargetObservable.dispose();
			
			this.currentChannel.removePropertyChangeListener(IntervalMode.STOPPED_BY_PROP, this);
		}
		this.currentChannel = null;
		
		this.triggerIntervalText.setText("");
		this.stoppedByComboViewer.getCombo().clearSelection();
		this.stoppedByComboControlDecoration.hide();
		
		this.redraw();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void propertyChange(PropertyChangeEvent e) {
		if (e.getPropertyName().equals(IntervalMode.STOPPED_BY_PROP)) {
			if (this.currentChannel.getStoppedBy() == null) {
				this.stoppedByComboControlDecoration.show();
			} else {
				this.stoppedByComboControlDecoration.hide();
			}
		}
	}
}