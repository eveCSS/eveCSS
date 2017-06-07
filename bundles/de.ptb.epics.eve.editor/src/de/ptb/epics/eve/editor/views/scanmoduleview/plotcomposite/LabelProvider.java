package de.ptb.epics.eve.editor.views.scanmoduleview.plotcomposite;

import org.eclipse.swt.graphics.Image;

import de.ptb.epics.eve.data.scandescription.PlotWindow;
import de.ptb.epics.eve.editor.views.scanmoduleview.ActionCompositeLabelProvider;

/**
 * <code>PlotLabelProvider</code> is the label provider of the table 
 * viewer defined in 
 * {@link de.ptb.epics.eve.editor.views.scanmoduleview.plotcomposite.PlotComposite}. 
 * 
 * @author Hartmut Scherr
 * @author Marcus Michalsky
 */
public class LabelProvider extends ActionCompositeLabelProvider {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Image getColumnImage(final Object plotWindow, final int colIndex) {
		final PlotWindow pw = (PlotWindow)plotWindow;
		boolean noYaxes = pw.getYAxes().size() == 0;
		switch(colIndex) {
			case 0: return getDeleteImage();
			case 1: break;
			case 2: break;
			case 3: if(pw.getXAxis() == null) {
						return getErrorImage(); 
					}
					break;
			case 4: if(noYaxes) {
						return getErrorImage();
					}
					break;
			case 5: if(noYaxes) {
						return getErrorImage();
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
			case 1: return Integer.toString(pw.getId());
			case 2: return pw.getName();
			case 3: if (pw.getXAxis() != null && 
						pw.getXAxis().getName() != null) {
							return pw.getXAxis().getName();
					}
					break;
			case 4: if (yAxisCount > 0 && 
						pw.getYAxes().get(0).getDetectorChannel() != null) {
							return pw.getYAxes().get(0).getDetectorChannel().
									getName();
					}
					break;
			case 5: if (yAxisCount > 1 &&
						pw.getYAxes().get(1).getDetectorChannel() != null) {
							return pw.getYAxes().get(1).getDetectorChannel().
									getName();
					}
					break;
		}
		return null;
	}
}