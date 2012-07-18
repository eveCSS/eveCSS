package de.ptb.epics.eve.editor.views.scanmoduleview.positioningcomposite;

import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;

import de.ptb.epics.eve.data.scandescription.Positioning;
import de.ptb.epics.eve.data.scandescription.errors.IModelError;
import de.ptb.epics.eve.data.scandescription.errors.PluginError;
import de.ptb.epics.eve.data.scandescription.errors.PluginErrorTypes;
import de.ptb.epics.eve.data.scandescription.errors.PositioningError;
import de.ptb.epics.eve.data.scandescription.errors.PositioningErrorTypes;

/**
 * <code>PositioningLabelProvider</code> is the label provider for the table 
 * viewer in 
 * {@link de.ptb.epics.eve.editor.views.scanmoduleview.positioningcomposite.PositioningComposite}.
 * 
 * @author ?
 * @author Marcus Michalsky
 */
public class LabelProvider implements ITableLabelProvider {

	private Image errorImage = PlatformUI.getWorkbench().getSharedImages().
			getImage(ISharedImages.IMG_OBJS_ERROR_TSK);
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Image getColumnImage(final Object positioning, final int colIndex) {
		final Positioning pos = (Positioning)positioning;
		if (colIndex == 0) {
			return PlatformUI.getWorkbench().getSharedImages()
					.getImageDescriptor(ISharedImages.IMG_TOOL_DELETE)
					.createImage();
		} else if (colIndex == 2) { // Plugin column
			for(IModelError error : pos.getModelErrors()) {
				if (error instanceof PluginError) {
					final PluginError pluginError = (PluginError)error;
					if (pluginError.getPluginErrorType() == 
							PluginErrorTypes.PLUING_NOT_SET) {
						return errorImage;
					}
				}
			}
		} else if (colIndex == 3) { // Detector Channel column
			for(IModelError error : pos.getModelErrors()) {
				if (error instanceof PositioningError) {
					final PositioningError posError = (PositioningError)error;
					if (posError.getErrorType() == 
							PositioningErrorTypes.NO_DETECTOR_CHANNEL_SET) {
						return errorImage;
					}
				}
			}
		} else if (colIndex == 5) { // Parameters column
			for(IModelError error : pos.getModelErrors()) {
				if (error instanceof PluginError) {
					final PluginError pluginError = (PluginError)error;
					if (pluginError.getPluginErrorType() == 
							PluginErrorTypes.MISSING_MANDATORY_PARAMETER) {
						return errorImage;
					} else if (pluginError.getPluginErrorType() == 
							PluginErrorTypes.WRONG_VALUE) {
						return errorImage;
					}
				}
			}
		}
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getColumnText(final Object positioning, final int colIndex) {
		final Positioning pos = (Positioning)positioning;
		switch(colIndex) {
			case 1: return (pos.getMotorAxis() != null)
					? pos.getMotorAxis().getName()
					: null;
			case 2: return (pos.getPluginController().getPlugin() != null)
					? pos.getPluginController().getPlugin().getName()
					: null;
			case 3: return (pos.getDetectorChannel() != null)
					? pos.getDetectorChannel().getName()
					: null;
			case 4: return (pos.getNormalization() != null)
					? pos.getNormalization().getName()
					: null;
			case 5: return (pos.getPluginController().getPlugin() != null)
					? pos.getPluginController().toString()
					: "Choose a plugin to see options";
		}
		return null;
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
	public void dispose() {
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