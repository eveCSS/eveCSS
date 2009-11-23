package de.ptb.epics.eve.viewer;

import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DragSource;
import org.eclipse.swt.dnd.DragSourceEvent;
import org.eclipse.swt.dnd.DragSourceListener;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.part.ViewPart;

import de.ptb.epics.eve.data.measuringstation.AbstractDevice;
import de.ptb.epics.eve.data.measuringstation.MeasuringStation;

/**
 * A simple view implementation, which only displays a label.
 * 
 * @author Sven Wende
 *
 */
public final class MeasuringStationView extends ViewPart {

	public static final String ID = "MeasuringStationView"; // TODO Needs to be whatever is mentioned in plugin.xml

	private MeasuringStation measuringStation;
	
	private TreeViewer treeViewer;
	
	private DragSource source;
	
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
					event.data = ((AbstractDevice)items[0].getData()).getFullIdentifyer();
				}
			}
			
			public void dragFinished( final DragSourceEvent event ) {
				
			}

			

			
		});
	}

	public void setMeasuringStation( final MeasuringStation measuringStation ) {
		this.measuringStation = measuringStation;
		this.treeViewer.setInput( this.measuringStation );
		this.treeViewer.getTree().setEnabled( this.measuringStation != null );
	}
	
	public MeasuringStation getMeasuringStation() {
		return this.measuringStation;
	}

}
