package de.ptb.epics.eve.editor.views.scanmoduleview;

import org.csstudio.swt.xygraph.figures.Trace.PointStyle;
import org.csstudio.swt.xygraph.figures.Trace.TraceType;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;

import de.ptb.epics.eve.data.measuringstation.AbstractDevice;
import de.ptb.epics.eve.data.measuringstation.Detector;
import de.ptb.epics.eve.data.measuringstation.DetectorChannel;
import de.ptb.epics.eve.data.measuringstation.IMeasuringStation;
import de.ptb.epics.eve.data.measuringstation.filter.ExcludeDevicesOfScanModuleFilterManualUpdate;
import de.ptb.epics.eve.data.scandescription.Channel;
import de.ptb.epics.eve.data.scandescription.PlotWindow;
import de.ptb.epics.eve.data.scandescription.ScanModule;
import de.ptb.epics.eve.data.scandescription.YAxis;
import de.ptb.epics.eve.editor.Activator;

/**
 * <code>DetectorChannelComposite</code>. is part of the
 * {@link de.ptb.epics.eve.editor.views.scanmoduleview.ScanModuleView}.
 * 
 * @author ?
 * @author Marcus Michalsky
 * @author Hartmut Scherr
 */
public class DetectorChannelComposite extends Composite {

	private TableViewer tableViewer;
	private ScanModule scanModule;
	private MenuManager menuManager;
	private final IMeasuringStation measuringStation;
	private ViewPart parentView;
	
	/**
	 * Constructs a <code>DetectorChannelComposite</code>.
	 * 
	 * @param parent the parent composite
	 * @param style the style
	 * @param measuringStation the measuring station (containing available 
	 * 		  detector channels)
	 */
	public DetectorChannelComposite(final ViewPart parentView, final Composite parent, final int style, 
									final IMeasuringStation measuringStation) {
		super(parent, style);
		this.parentView = parentView;
		this.measuringStation = measuringStation;

		final GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 2;
		
		this.setLayout(gridLayout);
		
		GridData gridData = new GridData();
		gridData.horizontalAlignment = GridData.FILL;
		gridData.verticalAlignment = GridData.FILL;
		gridData.horizontalSpan = 2;
		gridData.grabExcessHorizontalSpace = true;
		gridData.grabExcessVerticalSpace = true;
		
		this.tableViewer = new TableViewer(this, SWT.NONE);
		this.tableViewer.getControl().setLayoutData(gridData);
		
		TableColumn column = 
			new TableColumn(this.tableViewer.getTable(), SWT.LEFT, 0);
	    column.setText("Detector Channel");
	    column.setWidth(250);

	    column = new TableColumn(this.tableViewer.getTable(), SWT.LEFT, 1);
	    column.setText("Average");
	    column.setWidth(80);

	    this.tableViewer.getTable().setHeaderVisible(true);
	    this.tableViewer.getTable().setLinesVisible(true);
	    
	    this.tableViewer.setContentProvider(new DetectorChannelContentProvider());
	    this.tableViewer.setLabelProvider(new DetectorChannelLabelProvider());
	    
	    final CellEditor[] editors = new CellEditor[2];
	    
	    editors[0] = new TextCellEditor(this.tableViewer.getTable());
	    editors[1] = new TextCellEditor(this.tableViewer.getTable());
	    
	    this.tableViewer.setCellEditors(editors);
	    
	    final String[] props = {"device", "value"};
	    
	    this.tableViewer.setColumnProperties(props);
	    
		this.tableViewer.getTable().addFocusListener(new TableViewerFocusListener());

		menuManager = new MenuManager("#PopupMenu");
		
		menuManager.setRemoveAllWhenShown(true);
		menuManager.addMenuListener(new MenuManagerMenuListener());
				    
		final Menu contextMenu = 
			menuManager.createContextMenu(this.tableViewer.getTable());
		this.tableViewer.getControl().setMenu(contextMenu);

	}

	/**
	 * Returns the currently set 
	 * {@link de.ptb.epics.eve.data.scandescription.ScanModule}.
	 * 
	 * @return the currently set 
	 * 		   {@link de.ptb.epics.eve.data.scandescription.ScanModule}
	 */
	public ScanModule getScanModule() {
		return this.scanModule;
	}
	
	/**
	 * Sets the {@link de.ptb.epics.eve.data.scandescription.ScanModule}.
	 * 
	 * @param scanModule the 
	 * 		  {@link de.ptb.epics.eve.data.scandescription.ScanModule} that 
	 * 		  should be set
	 */
	public void setScanModule(final ScanModule scanModule) {

		System.out.println("\nsetScanModule von DetectorChannelComposite aufgerufen");
		
		if(scanModule != null) {
			this.tableViewer.getTable().setEnabled(true);
		} else {
			this.tableViewer.getTable().setEnabled(false);
		}
		this.scanModule = scanModule;
		this.tableViewer.setInput(scanModule);

		// if there are detector channels present... 
		if(tableViewer.getTable().getItems().length > 0)
		{	// ... and none is selected ...
			if(tableViewer.getTable().getSelectionCount() == 0)
			{	// ... select the first one and set the detector channel view
				tableViewer.getTable().select(0);
			}
		} 
	}
	
	/*
	 * Sets the Plot Detector Channel if only one channel is available 
	 */
	private void setPlotDetectorChannel()
	{
		final Channel[] availableDetectorChannels;

		availableDetectorChannels = scanModule.getChannels();
		String[] channelItems = new String[availableDetectorChannels.length];
		for (int i = 0; i < availableDetectorChannels.length; ++i) {
			channelItems[i] = 
				availableDetectorChannels[i].getDetectorChannel().getFullIdentifyer();
		}		
		
		// if only one channel available, create a yAxis and set 
		// this channel as default
		if (availableDetectorChannels.length == 1) {

			YAxis yAxis1 = new YAxis();
			// default values for color, line style and mark style
			yAxis1.setColor(new RGB(0,0,255));
			yAxis1.setLinestyle(TraceType.SOLID_LINE);
			yAxis1.setMarkstyle(PointStyle.NONE);

			yAxis1.setDetectorChannel(availableDetectorChannels[0].getDetectorChannel());
			PlotWindow[] plotWindows = scanModule.getPlotWindows();
			for (int i = 0; i < plotWindows.length; ++i) {
				plotWindows[i].addYAxis(yAxis1);
			}
		}
	}

	// ************************************************************************
	// **************************** Listeners *********************************
	// ************************************************************************
	
	/**
	 * 
	 */
	class TableViewerFocusListener implements FocusListener {

		@Override
		public void focusGained(FocusEvent e) {
			((ScanModuleView)parentView).selectionProviderWrapper.
								setSelectionProvider(tableViewer);
		}

		@Override
		public void focusLost(FocusEvent e) {
		}
	}
	
	/**
	 * 
	 */
	class MenuManagerMenuListener implements IMenuListener {
		
		/**
		 * {@inheritDoc}
		 */
		@Override
		public void menuAboutToShow(IMenuManager manager) {
			
			final ImageDescriptor classImage = ImageDescriptor.createFromImage(
					Activator.getDefault().
					getImageRegistry().get("CLASS"));
			final ImageDescriptor detectorImage = ImageDescriptor.createFromImage(
					Activator.getDefault().
					getImageRegistry().get("DETECTOR"));
			final ImageDescriptor channelImage = ImageDescriptor.createFromImage(
					Activator.getDefault().
					getImageRegistry().get("CHANNEL"));

			((ExcludeDevicesOfScanModuleFilterManualUpdate)measuringStation).update();

			for(final String className : measuringStation.getClassNameList()) {
				
				final MenuManager currentClassMenu = new MenuManager(
						className, classImage, className);
				
				for(final AbstractDevice device : 
					measuringStation.getDeviceList(className)) {
				
					if(device instanceof Detector) {
						final Detector detector = (Detector)device;
						final MenuManager currentDetectorMenu = 
							new MenuManager(detector.getName(), detectorImage, detector.getName());
						currentClassMenu.add(currentDetectorMenu);

						// iterate for each channel of the detector
						for(final DetectorChannel channel : detector.getChannels()) {
							if (channel.getClassName().isEmpty()) {
								// add only channels which have no className
								final Action setChannelAction = new SetChannelAction(channel);
								setChannelAction.setImageDescriptor(channelImage);
								currentDetectorMenu.add(setChannelAction);
							}
						}
						// if only one channel in DetectorMenu, switch channel from DetectorMenu into ClassMenu
						if (currentDetectorMenu.getSize() == 1) {
							currentDetectorMenu.removeAll();
							// Eintrag muß zur Class hinzugefügt werden.
							for(final DetectorChannel channel : detector.getChannels()) {
								if (channel.getClassName().isEmpty()) {
									// add only channels which have no className
									final Action setChannelAction = new SetChannelAction(channel);
									setChannelAction.setImageDescriptor(channelImage);
									currentClassMenu.add(setChannelAction);
								}
							}
						}					
					} else if(device instanceof DetectorChannel) {
						final Action setChannelAction = new SetChannelAction((DetectorChannel)device);
						setChannelAction.setImageDescriptor(channelImage);
						currentClassMenu.add(setChannelAction);
					}
					manager.add(currentClassMenu);
				}
			}
				
			for(final Detector detector : measuringStation.getDetectors()) {
				if("".equals(detector.getClassName()) || detector.getClassName() == null) {
					final MenuManager currentDetectorMenu = 
							new MenuManager(detector.getName(), detectorImage, detector.getName());
					for(final DetectorChannel channel : detector.getChannels()) {
						if("".equals(channel.getClassName()) || channel.getClassName() == null) {
							final Action setChannelAction = 
								new SetChannelAction(channel);
							setChannelAction.setImageDescriptor(channelImage);
							currentDetectorMenu.add( setChannelAction );
						}
					}
					// if only one channel in DetectorMenu, switch channel from DetectorMenu into ClassMenu
					if (currentDetectorMenu.getSize() == 1) {
						currentDetectorMenu.removeAll();
						// Eintrag muß zur Class hinzugefügt werden.
						for(final DetectorChannel channel : detector.getChannels()) {
							if (channel.getClassName().isEmpty()) {
								// add only channels which have no className
								final Action setChannelAction = new SetChannelAction(channel);
								setChannelAction.setImageDescriptor(channelImage);
								manager.add(setChannelAction);
							}
						}
					}					
					manager.add(currentDetectorMenu);
				}
			}

			if (scanModule.getChannels().length > 0) {
				Action deleteAction = new DeleteAction();
				deleteAction.setEnabled(true);
				deleteAction.setText("Delete Channel");
				deleteAction.setToolTipText("Deletes Channel");
				deleteAction.setImageDescriptor(PlatformUI.getWorkbench().
				   								getSharedImages().
				   								getImageDescriptor(
				   								ISharedImages.IMG_TOOL_DELETE));
				manager.add(deleteAction);	
			}
			
		}
	}
	
	// ****************************** Actions *********************************
	
	/**
	 * 
	 */
	class SetChannelAction extends Action {
		
		final DetectorChannel ch;
		
		SetChannelAction(DetectorChannel ch)
		{
			this.ch = ch;
			this.setText("".equals(ch.getName())
						 ? ch.getID()
						 : ch.getName());
		}
		
		/**
		 * {@inheritDoc}
		 */
		@Override
		public void run() {
			super.run();
			for(final Channel c : scanModule.getChannels()) {
				if(c.getAbstractDevice() == ch) {
					return;
				}
			}
			Channel c = new Channel(scanModule);
			c.setDetectorChannel(ch);
			scanModule.add(c);
			
			// the new channel (the last itemCount) will be selected in the table and 
			// displayed in the detectorChannelView
			tableViewer.getTable().select(tableViewer.getTable().getItemCount()-1);

			tableViewer.refresh();
		}
	}
	
	/**
	 * 
	 */
	class DeleteAction extends Action {
		
		/**
		 * {@inheritDoc}
		 */
		@Override
		public void run() {
    		
    		scanModule.remove((Channel)((IStructuredSelection)
    				tableViewer.getSelection()).getFirstElement());

			// if only one channel available, set this channel as for the Plot
			setPlotDetectorChannel();

			tableViewer.refresh();
    	}
	}
}