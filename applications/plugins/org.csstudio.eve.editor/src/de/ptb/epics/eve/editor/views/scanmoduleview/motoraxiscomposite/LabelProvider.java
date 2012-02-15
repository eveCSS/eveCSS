package de.ptb.epics.eve.editor.views.scanmoduleview.motoraxiscomposite;

import java.util.Iterator;

import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;

import de.ptb.epics.eve.data.scandescription.Axis;
import de.ptb.epics.eve.data.scandescription.errors.AxisError;
import de.ptb.epics.eve.data.scandescription.errors.IModelError;

/**
 * <code>MotorAxisLabelProvider</code> is the label provider of the table 
 * viewer defined in 
 * {@link de.ptb.epics.eve.editor.views.scanmoduleview.motoraxiscomposite.MotorAxisComposite}. 
 * 
 * @author ?
 * @author Marcus Michalsky
 */
public class LabelProvider implements ITableLabelProvider {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Image getColumnImage(final Object axis, final int colIndex) {
		
		final Axis pos = (Axis)axis;
		
		if(colIndex == 1) {
			final Iterator<IModelError> it = pos.getModelErrors().iterator();
			while(it.hasNext()) {
				final IModelError modelError = it.next();
				if(modelError instanceof AxisError) {
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
	public String getColumnText(final Object axis, final int colIndex) {
		
		final Axis pos = (Axis) axis;
		
		switch(colIndex) {
			case 0:
				return (pos.getAbstractDevice()!=null)
					   ? pos.getAbstractDevice().getFullIdentifyer()
					   : "";
			case 1:
				return pos.getStepfunctionString();
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