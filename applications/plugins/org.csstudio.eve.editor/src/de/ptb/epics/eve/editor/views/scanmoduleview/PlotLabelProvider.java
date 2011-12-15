package de.ptb.epics.eve.editor.views.scanmoduleview;

import java.util.Iterator;

import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;

import de.ptb.epics.eve.data.scandescription.PlotWindow;
import de.ptb.epics.eve.data.scandescription.errors.IModelError;
import de.ptb.epics.eve.data.scandescription.errors.PlotWindowError;

/**
 * <code>PlotLabelProvider</code> is the label provider of the table 
 * viewer defined in 
 * {@link de.ptb.epics.eve.editor.views.scanmoduleview.PlotComposite}. 
 * 
 * @author Hartmut Scherr
 */
public class PlotLabelProvider implements ITableLabelProvider {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Image getColumnImage(final Object plotWindow, final int colIndex) {
		
		final PlotWindow pos = (PlotWindow)plotWindow;
		if(colIndex == 0) {
			final Iterator<IModelError> it = pos.getModelErrors().iterator();
			while(it.hasNext()) {
				final IModelError modelError = it.next();
				if(modelError instanceof PlotWindowError) {
					return PlatformUI.getWorkbench().getSharedImages().
									  getImage(ISharedImages.IMG_OBJS_ERROR_TSK);
				}
			}
		}
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getColumnText(final Object plotWindow, final int colIndex) {
		
		final PlotWindow pos = (PlotWindow)plotWindow;

		switch(colIndex) {
			case 0:
				return Integer.toString(pos.getId());
			case 1:
				if (pos.getXAxis() != null) {
				return (pos.getXAxis().getName() != null)
						? pos.getXAxis().getName() : "";
				}
				return "";
			case 2:
				int yaxisZahl = pos.getYAxisAmount();
				if (yaxisZahl > 0) {
					if (pos.getYAxes().get(0).getDetectorChannel() != null) {
					return (pos.getYAxes().get(0).getDetectorChannel().getName() != null)
						? pos.getYAxes().get(0).getDetectorChannel().getName() : "";
					}
				}
				return "";
			case 3:
				int yaxisZahl1 = pos.getYAxisAmount();
				if (yaxisZahl1 > 1) {
					if (pos.getYAxes().get(1).getDetectorChannel() != null) {
					 return (pos.getYAxes().get(1).getDetectorChannel().getName() != null)
						? pos.getYAxes().get(1).getDetectorChannel().getName() : "";
					}
				}
				return "";
		}
		return "";
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