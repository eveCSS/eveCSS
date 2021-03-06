package de.ptb.epics.eve.editor.views.detectorchannelview.ui;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.layout.GridData;

import de.ptb.epics.eve.data.measuringstation.DetectorChannel;
import de.ptb.epics.eve.data.scandescription.Channel;
import de.ptb.epics.eve.data.scandescription.ScanModule;
import de.ptb.epics.eve.data.scandescription.ScanModuleTypes;
import de.ptb.epics.eve.data.scandescription.channelmode.ChannelModes;
import de.ptb.epics.eve.data.scandescription.defaults.DefaultsManager;
import de.ptb.epics.eve.data.scandescription.defaults.channel.DefaultsChannel;
import de.ptb.epics.eve.editor.Activator;
import de.ptb.epics.eve.editor.gef.editparts.ChainEditPart;
import de.ptb.epics.eve.editor.gef.editparts.ScanDescriptionEditPart;
import de.ptb.epics.eve.editor.gef.editparts.ScanModuleEditPart;
import de.ptb.epics.eve.editor.views.EditorViewPerspectiveListener;
import de.ptb.epics.eve.editor.views.IEditorView;

/**
 * <code>DetectorChannelView</code> shows attributes of a
 * {@link de.ptb.epics.eve.data.measuringstation.DetectorChannel} and allows
 * modification.
 * 
 * @author Marcus Michalsky
 */
public class DetectorChannelView extends ViewPart implements IEditorView,
		ISelectionListener, PropertyChangeListener {

	/**
	 * the unique identifier of the view.
	 */
	public static final String ID = "de.ptb.epics.eve.editor.views.DetectorChannelView";

	private static final Logger LOGGER = Logger
			.getLogger(DetectorChannelView.class.getName());
	
	private ScrolledComposite sc;
	private Composite top;
	private SashForm sashForm;
	private Composite emptyComposite;
	private NormalizeComposite normalizeComposite;
	private DetectorChannelViewComposite standardComposite;
	private DetectorChannelViewComposite intervalComposite;
	
	private Channel currentChannel;
	private ActiveComposite activeComposite;

	private ComboViewer acquisitionTypeComboViewer;
	private ComboViewer normalizeChannelComboViewer;
	private ISelectionChangedListener normalizeChannelChangeListener;
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void createPartControl(final Composite parent) {
		parent.setLayout(new FillLayout());

		if (Activator.getDefault().getMeasuringStation() == null) {
			final Label errorLabel = new Label(parent, SWT.NONE);
			errorLabel.setText("No Measuring Station has been loaded. "
					+ "Please check Preferences!");
			return;
		}

		this.sc = new ScrolledComposite(parent, SWT.H_SCROLL | 
				SWT.V_SCROLL);
		
		this.top = new Composite(sc, SWT.NONE) {
			@Override
			public Point computeSize(int wHint, int hHint, boolean changed) {
				switch (activeComposite) {
				case EMPTY:
					return super.computeSize(wHint, hHint, changed);
				case INTERVAL:
					return intervalComposite.computeSize(wHint, hHint, changed);
				case NORMALIZED:
					return normalizeComposite.computeSize(wHint, hHint, changed);
				case STANDARD:
					return standardComposite.computeSize(wHint, hHint, changed);
				default:
					return super.computeSize(wHint, hHint, changed);
				}
			}
		};
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 2;
		this.top.setLayout(gridLayout);
		this.sc.setContent(this.top);
		this.sc.setExpandHorizontal(true);
		this.sc.setExpandVertical(true);
		this.sc.addControlListener(new ControlAdapter() {
			@Override
			public void controlResized(ControlEvent e) {
				sc.setMinSize(top.computeSize(SWT.DEFAULT, SWT.DEFAULT));
			}
		});
		
		Label acquisitonType = new Label(top, SWT.NONE);
		acquisitonType.setText("Acquisition Type:");
		
		acquisitionTypeComboViewer = new ComboViewer(top, SWT.READ_ONLY);
		GridData gridData = new GridData();
		gridData.horizontalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = true;
		acquisitionTypeComboViewer.getCombo().setLayoutData(gridData);
		acquisitionTypeComboViewer.setContentProvider(ArrayContentProvider.getInstance());
		acquisitionTypeComboViewer.setLabelProvider(new LabelProvider() {
			@Override public String getText(Object element) {
				return ((ChannelModes)element).toString();
			}
		});
		acquisitionTypeComboViewer.setInput(ChannelModes.values());
		acquisitionTypeComboViewer.addSelectionChangedListener(new ISelectionChangedListener() {
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				if (currentChannel.getChannelMode().equals(ChannelModes.getEnum(
						acquisitionTypeComboViewer.getCombo().getText()))) {
					return;
				}
				currentChannel.setChannelMode(ChannelModes.getEnum(
						acquisitionTypeComboViewer.getCombo().getText()));
				DefaultsChannel defChannel = Activator.getDefault().getDefaults()
						.getChannel(currentChannel.getDetectorChannel().getID());
				if (defChannel != null && defChannel.getMode().getType().equals(
						currentChannel.getChannelMode())) {
					DefaultsManager.transferDefaults(defChannel, currentChannel);
				}
				setComposite();
			}
		});
		
		Label normalizeChannelLabel = new Label(this.top, SWT.NONE);
		normalizeChannelLabel.setText("Normalize Channel:");
		normalizeChannelComboViewer = new ComboViewer(this.top, SWT.BORDER | SWT.READ_ONLY);
		gridData = new GridData();
		gridData.horizontalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = true;
		normalizeChannelComboViewer.getCombo().setLayoutData(gridData);
		normalizeChannelComboViewer.setContentProvider(ArrayContentProvider.getInstance());
		normalizeChannelComboViewer.setLabelProvider(new LabelProvider() {
			@Override public String getText(Object element) {
				if (element == null) {
					return "";
				}
				return ((Channel)element).getAbstractDevice().getName();
			}
		});
		this.normalizeChannelChangeListener = 
				new NormalizedChannelSelectionChangedListener();
		normalizeChannelComboViewer.addSelectionChangedListener(
				normalizeChannelChangeListener);
		
		this.sashForm = new SashForm(top, SWT.VERTICAL);
		gridData = new GridData();
		gridData.horizontalSpan = 2;
		gridData.grabExcessHorizontalSpace = true;
		gridData.grabExcessVerticalSpace = true;
		gridData.horizontalAlignment = GridData.FILL;
		gridData.verticalAlignment = GridData.FILL;
		this.sashForm.setLayoutData(gridData);
		this.emptyComposite = new Composite(sashForm, SWT.NONE);
		this.normalizeComposite = new NormalizeComposite(sashForm, SWT.NONE);
		this.standardComposite = new StandardComposite(sashForm, SWT.NONE, this);
		this.intervalComposite = new IntervalComposite(sashForm, SWT.NONE, this);
		this.sashForm.setMaximizedControl(this.emptyComposite);
		this.activeComposite = ActiveComposite.EMPTY;
		
		this.currentChannel = null;
		
		this.sc.setVisible(false);
		
		// listen to selection changes (if a detector channel is selected, its
		// attributes are made available for editing)
		getSite().getWorkbenchWindow().getSelectionService().addSelectionListener(this);
		
		// reset view if last editor was closed
		PlatformUI.getWorkbench().getActiveWorkbenchWindow().addPerspectiveListener(
				new EditorViewPerspectiveListener(this));
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setFocus() {
		LOGGER.debug("got focus -> forward to top composite");
		this.sc.setFocus();
	}

	/**
	 * Returns the {@link de.ptb.epics.eve.data.scandescription.Channel}
	 * currently shown by this view.
	 * 
	 * @return the {@link de.ptb.epics.eve.data.scandescription.Channel}
	 *         currently shown
	 */
	public Channel getCurrentChannel() {
		return this.currentChannel;
	}
	
	private void setChannel(Channel channel) {
		if (channel != null) {
			LOGGER.debug("set channel (" + channel.getAbstractDevice().getFullIdentifyer() + ")");
		} else {
			LOGGER.debug("set channel (null)");
		}
		if (this.currentChannel != null) {
			this.currentChannel.getScanModule().removePropertyChangeListener(
					ScanModule.REMOVE_CHANNEL_PROP, this);
			this.currentChannel.removePropertyChangeListener(
					Channel.CHANNEL_MODE_PROP, this);
			this.currentChannel.removePropertyChangeListener(
					Channel.NORMALIZE_CHANNEL_PROP, this);
			this.standardComposite.setChannel(null);
			this.intervalComposite.setChannel(null);
		}

		this.currentChannel = channel;

		if (this.currentChannel != null) {
			this.sc.setVisible(true);
			this.currentChannel.getScanModule().addPropertyChangeListener(
					ScanModule.REMOVE_CHANNEL_PROP, this);
			this.currentChannel.addPropertyChangeListener(
					Channel.CHANNEL_MODE_PROP, this);
			this.currentChannel.addPropertyChangeListener(
					Channel.NORMALIZE_CHANNEL_PROP, this);
			this.setPartName(currentChannel.getAbstractDevice().getName());
			this.acquisitionTypeComboViewer.getCombo().setText(
					this.currentChannel.getChannelMode().toString());
			List<Channel> entries = currentChannel.getScanModule().
					getValidNormalizationChannels(currentChannel);
			this.normalizeChannelComboViewer.setInput(entries);
			if (!entries.isEmpty()) {
				this.normalizeChannelComboViewer.insert(null, 0);
			}
			if (currentChannel.getNormalizeChannel() != null) {
				this.normalizeChannelComboViewer.getCombo().setText(
						currentChannel.getNormalizeChannel().getName());
			}
			this.setComposite();
		} else {
			this.setPartName("No Channel selected.");
			this.sc.setVisible(false);
		}
		this.sc.layout();
	}
	
	private void setComposite() {
		List<Channel> normalizeChannels = new ArrayList<>();
		for (Channel ch : this.currentChannel.getScanModule().getChannels()) {
			if (ch.getNormalizeChannel() == null) {
				continue;
			}
			if (ch.getNormalizeChannel().getID().equals(this.currentChannel.
					getDetectorChannel().getID())) {
				normalizeChannels.add(ch);
			}
		}
		if (this.currentChannel.getScanModule().isUsedAsNormalizeChannel(currentChannel)) {
			this.sashForm.setMaximizedControl(this.normalizeComposite);
			this.activeComposite = ActiveComposite.NORMALIZED;
			this.acquisitionTypeComboViewer.getCombo().deselectAll();;
			this.acquisitionTypeComboViewer.getCombo().setEnabled(false);
			this.normalizeChannelComboViewer.getCombo().deselectAll();;
			this.normalizeChannelComboViewer.getCombo().setEnabled(false);
			this.normalizeComposite.setChannels(normalizeChannels);
			this.sc.setMinSize(this.top.computeSize(SWT.DEFAULT, SWT.DEFAULT));
			return;
		}
		if (this.currentChannel.getScanModule().isUsedAsStoppedByChannel(currentChannel)) {
			this.acquisitionTypeComboViewer.getCombo().setEnabled(false);
			this.normalizeChannelComboViewer.getCombo().setEnabled(false);
		} else {
			this.acquisitionTypeComboViewer.getCombo().setEnabled(true);
			this.normalizeChannelComboViewer.getCombo().setEnabled(true);
		}
		switch (this.currentChannel.getChannelMode()) {
		case INTERVAL:
			this.sashForm.setMaximizedControl(intervalComposite);
			this.activeComposite = ActiveComposite.INTERVAL;
			this.intervalComposite.setChannel(this.currentChannel);
			break;
		case STANDARD:
			this.sashForm.setMaximizedControl(standardComposite);
			this.activeComposite = ActiveComposite.STANDARD;
			this.standardComposite.setChannel(this.currentChannel);
			break;
		default:
			this.sashForm.setMaximizedControl(emptyComposite);
			this.activeComposite = ActiveComposite.EMPTY;
			break;
		}
		this.sc.setMinSize(this.top.computeSize(SWT.DEFAULT, SWT.DEFAULT));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void selectionChanged(IWorkbenchPart part, ISelection selection) {
		LOGGER.trace("selection changed");

		if (selection instanceof IStructuredSelection) {
			if (((IStructuredSelection) selection).size() == 0) {
				return;
			}
			// at any given time this view can only display the attributes
			// of one detector channel, we take the first element of the
			// selection
			Object o = ((IStructuredSelection) selection).toList().get(0);
			if (o instanceof Channel && ((Channel)o).getScanModule().getType().equals(ScanModuleTypes.CLASSIC)) {
				// set new Channel
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("Channel: "
							+ ((Channel) o).getDetectorChannel()
									.getFullIdentifyer() + " selected.");
				}
				setChannel((Channel) o);
			} else if (o instanceof ScanModuleEditPart) {
				// ScanModule was selected
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("selection is ScanModuleEditPart: " + o);
					LOGGER.debug("ScanModule: "
							+ ((ScanModuleEditPart) o).getModel().getId()
							+ " selected.");
				}

				if (this.currentChannel != null
						&& !this.currentChannel.getScanModule().equals(((ScanModuleEditPart) o)
								.getModel())) {
					setChannel(null);
				}
			} else if (o instanceof ChainEditPart) {
				LOGGER.debug("selection is ChainEditPart: " + o);
				setChannel(null);
			} else if (o instanceof ScanDescriptionEditPart) {
				LOGGER.debug("selection is ScanDescriptionEditPart: " + o);
				setChannel(null);
			} else {
				LOGGER.debug("unknown selection -> ignore");
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void reset() {
		this.setChannel(null);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		if (evt.getPropertyName().equals(ScanModule.REMOVE_CHANNEL_PROP)
				&& evt.getOldValue().equals(this.currentChannel)) {
			this.setChannel(null);
		} else if (evt.getPropertyName().equals(Channel.CHANNEL_MODE_PROP)) {
			this.acquisitionTypeComboViewer.getCombo().setText(
					this.currentChannel.getChannelMode().toString());
			this.setComposite();
		} else if (evt.getPropertyName().equals(Channel.NORMALIZE_CHANNEL_PROP)) {
			this.normalizeChannelComboViewer.removeSelectionChangedListener(
					this.normalizeChannelChangeListener);
			DetectorChannel normalizeChannel = 
					this.currentChannel.getNormalizeChannel();
			if (normalizeChannel == null) {
				this.normalizeChannelComboViewer.setSelection(
						StructuredSelection.EMPTY);
			} else {
				this.normalizeChannelComboViewer.getCombo().setText(
						this.currentChannel.getNormalizeChannel().getName());
			}
			this.normalizeChannelComboViewer.addSelectionChangedListener(
					this.normalizeChannelChangeListener);
		}
	}
	
	private enum ActiveComposite {
		EMPTY,
		INTERVAL,
		NORMALIZED,
		STANDARD
	}
	
	private class NormalizedChannelSelectionChangedListener implements ISelectionChangedListener {
		@Override
		public void selectionChanged(SelectionChangedEvent event) {
			LOGGER.debug("Normalize Channel: " + event.getSelection().toString());
			if (event.getSelection().isEmpty()) {
				currentChannel.setNormalizeChannel(null);
			} else {
				currentChannel.setNormalizeChannel(
					((Channel) ((IStructuredSelection) event.getSelection()).
							getFirstElement()).getDetectorChannel());
			}
		}
	}
}
