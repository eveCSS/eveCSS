package de.ptb.epics.eve.editor.views.scanmoduleview.plotcomposite;

import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;

import de.ptb.epics.eve.data.scandescription.PlotWindow;

/**
 * <code>PlotLabelProvider</code> is the label provider of the table 
 * viewer defined in 
 * {@link de.ptb.epics.eve.editor.views.scanmoduleview.plotcomposite.PlotComposite}. 
 * 
 * @author Hartmut Scherr
 * @author Marcus Michalsky
 */
public class LabelProvider implements ITableLabelProvider {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Image getColumnImage(final Object plotWindow, final int colIndex) {
		
		Image error = PlatformUI.getWorkbench().getSharedImages().
				getImage(ISharedImages.IMG_OBJS_ERROR_TSK);
		
		final PlotWindow pw = (PlotWindow)plotWindow;
		boolean noYaxes = pw.getYAxes().size() == 0;
		switch(colIndex) {
			case 0: break;
			case 1: if(pw.getXAxis() == null) {
						return error; 
					}
					break;
			case 2: if(noYaxes) {
						return error;
					}
					break;
			case 3: if(noYaxes) {
						return error;
					}
					break;
		}
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getColumnText(final Object plotWindow, final int colIndex) {
		
		final PlotWindow pw = (PlotWindow)plotWindow;
		int yAxisCount = pw.getYAxisAmount();
		
		switch(colIndex) {
			case 0: return Integer.toString(pw.getId());
			case 1: if (pw.getXAxis() != null && 
						pw.getXAxis().getName() != null) {
							return pw.getXAxis().getName();
					}
					break;
			case 2: if (yAxisCount > 0 && 
						pw.getYAxes().get(0).getDetectorChannel() != null) {
							return pw.getYAxes().get(0).getDetectorChannel().
									getName();
					}
					break;
			case 3: if (yAxisCount > 1 &&
						pw.getYAxes().get(1).getDetectorChannel() != null) {
							return pw.getYAxes().get(1).getDetectorChannel().
									getName();
					}
					break;
		}
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void dispose() {
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isLabelProperty(final Object arg0, String arg1) {
		return false;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void addListener(final ILabelProviderListener arg0) {
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void removeListener(final ILabelProviderListener arg0) {
	}
}