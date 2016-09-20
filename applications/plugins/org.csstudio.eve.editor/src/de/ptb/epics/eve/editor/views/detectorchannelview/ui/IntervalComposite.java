package de.ptb.epics.eve.editor.views.detectorchannelview.ui;

import java.util.List;
import java.util.Locale;

import org.apache.log4j.Logger;
import org.eclipse.core.databinding.Binding;
import org.eclipse.core.databinding.UpdateValueStrategy;
import org.eclipse.core.databinding.beans.BeanProperties;
import org.eclipse.core.databinding.conversion.NumberToStringConverter;
import org.eclipse.core.databinding.conversion.StringToNumberConverter;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.jface.databinding.swt.WidgetProperties;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IViewPart;

import com.ibm.icu.text.NumberFormat;

import de.ptb.epics.eve.data.scandescription.Channel;
import de.ptb.epics.eve.data.scandescription.channelmode.ChannelModes;
import de.ptb.epics.eve.data.scandescription.channelmode.IntervalMode;

/**
 * @author Marcus Michalsky
 * @since 1.27
 */
public class IntervalComposite extends DetectorChannelViewComposite {
	private static final Logger LOGGER = Logger.getLogger(
			IntervalComposite.class.getName());
	
	private Label triggerIntervalLabel;
	private Text triggerIntervalText;
	private Label stoppedByLabel;
	private ComboViewer stoppedByComboViewer;
	
	private IObservableValue triggerIntervalTargetObservable;
	private IObservableValue triggerIntervalModelObservable;
	private Binding triggerIntervalBinding;
	
	private Channel currentChannel;
	
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
			}
		});
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
		List<Channel> entries = this.currentChannel.
				getScanModule().getValidStoppedByChannels(currentChannel);
		this.stoppedByComboViewer.setInput(entries);
		if (!entries.isEmpty()) {
			this.stoppedByComboViewer.insert(null, 0);
		}
		if (this.currentChannel.getStoppedBy() != null) {
			this.stoppedByComboViewer.getCombo().setText(
					this.currentChannel.getStoppedBy().getName());
		}
		// TODO Add prop listeners
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
						NumberFormat.getInstance(Locale.US), false)),
				new UpdateValueStrategy(UpdateValueStrategy.POLICY_UPDATE).
				setConverter(NumberToStringConverter.fromDouble(
						NumberFormat.getInstance(Locale.US), false)));
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
		}
		this.currentChannel = null;
		
		this.triggerIntervalText.setText("");
		this.stoppedByComboViewer.getCombo().clearSelection();
		
		this.redraw();
	}
}