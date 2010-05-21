package de.ptb.epics.eve.viewer;

import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DragSource;
import org.eclipse.swt.dnd.DragSourceEvent;
import org.eclipse.swt.dnd.DragSourceListener;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.IViewReference;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;

import de.ptb.epics.eve.data.measuringstation.AbstractDevice;
import de.ptb.epics.eve.data.measuringstation.MeasuringStation;
import de.ptb.epics.eve.viewer.Activator;
import de.ptb.epics.eve.viewer.actions.LoadMeasuringStationAction;

/**
 * A simple view implementation, which only displays a label.
 * 
 * @author PTB
 *
 */
public final class MeasuringStationView extends ViewPart {

	public static final String ID = "MeasuringStationView"; // TODO Needs to be whatever is mentioned in plugin.xml

	private MeasuringStation measuringStation;
	
	private TreeViewer treeViewer;
	
	private DragSource source;
	
	private LoadMeasuringStationAction loadMeasuringStationAction;
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setFocus() {
	
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void createPartControl( final Composite parent ) {
		
		measuringStation = Activator.getDefault().getMeasuringStation();
		if( measuringStation == null ) {
			final Label errorLabel = new Label( parent, SWT.NONE );
			errorLabel.setText( "No device description has been loaded. Please check Preferences!" );
			return;
		}
				
		final FillLayout fillLayout = new FillLayout();
		parent.setLayout( fillLayout );
		this.treeViewer = new TreeViewer( parent );
		this.treeViewer.setContentProvider( new MeasuringStationTreeViewContentProvider() );
		this.treeViewer.setLabelProvider( new MeasuringStationTreeViewLabelProvider() );
		this.treeViewer.getTree().setEnabled( false );
		
		this.source = new DragSource( this.treeViewer.getTree(), DND.DROP_COPY | DND.DROP_MOVE );
		Transfer[] types = new Transfer[] { TextTransfer.getInstance() };
		source.setTransfer( types );
		source.addDragListener( new DragSourceListener() {

			public void dragStart( final DragSourceEvent event ) {
				if( treeViewer.getTree().getSelection().length == 0 ) {
					event.doit = false;
				} else {
					event.doit = true;
				}
				event.data = null;
				
			}
			
			public void dragSetData( final DragSourceEvent event ) {
				TreeItem[] items = treeViewer.getTree().getSelection();
				if( TextTransfer.getInstance().isSupportedType( event.dataType ) ) {
					if (items[0].getData() instanceof AbstractDevice)
						event.data = ((AbstractDevice)items[0].getData()).getFullIdentifyer();
					else if (items[0].getData() instanceof String) {
						System.err.println("MeasuringStationView String item");
						event.data = (String)items[0].getData();		
					}
				}
			}
			
			public void dragFinished( final DragSourceEvent event ) {
				
			}

			

			
		});
		
		this.treeViewer.getTree().addSelectionListener( new SelectionListener() {

			public void widgetDefaultSelected(SelectionEvent e) {
				// TODO Auto-generated method stub
				
			}

			public void widgetSelected(SelectionEvent e) {
				TreeItem[] items = treeViewer.getTree().getSelection();
				if( items.length > 0 ) {
					if( items[0].getData() instanceof AbstractDevice ) {
						IViewReference[] ref = getSite().getPage().getViewReferences();
						DeviceOptionsViewer view = null;
						for( int i = 0; i < ref.length; ++i ) {
							if( ref[i].getId().equals( "DeviceOptionsView" ) ) {
								view = (DeviceOptionsViewer)ref[i].getPart( true );
							}
						}
						if (view != null) view.setDevice( (AbstractDevice)items[0].getData() );
					}
				}
				
			}
			
		});
		setMeasuringStation(measuringStation);
	}

	public void setMeasuringStation( final MeasuringStation measuringStation ) {
		Activator.getDefault().getMessagesContainer().addMessage( new ViewerMessage( MessageSource.VIEWER, MessageTypes.INFO, "Got new measuring station description." ) );
		Activator.getDefault().getChainStatusAnalyzer().reset();
		this.measuringStation = measuringStation;
		this.treeViewer.setInput( this.measuringStation );
		this.treeViewer.getTree().setEnabled( this.measuringStation != null );
	}
	
	public MeasuringStation getMeasuringStation() {
		return this.measuringStation;
	}

}
