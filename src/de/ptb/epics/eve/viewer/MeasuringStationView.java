package de.ptb.epics.eve.viewer;

import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.part.ViewPart;

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
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setFocus() {

	}

	public void setMeasuringStation( final MeasuringStation measuringStation ) {
		this.measuringStation = measuringStation;
		this.treeViewer.setInput( this.measuringStation );
	}

}
