package de.ptb.epics.eve.editor.views.scanmoduleview.positioningcomposite;

import org.eclipse.swt.graphics.Image;

import de.ptb.epics.eve.data.scandescription.Positioning;
import de.ptb.epics.eve.data.scandescription.errors.IModelError;
import de.ptb.epics.eve.data.scandescription.errors.PluginError;
import de.ptb.epics.eve.data.scandescription.errors.PluginErrorTypes;
import de.ptb.epics.eve.data.scandescription.errors.PositioningError;
import de.ptb.epics.eve.data.scandescription.errors.PositioningErrorTypes;
import de.ptb.epics.eve.editor.views.scanmoduleview.ActionCompositeLabelProvider;

/**
 * <code>PositioningLabelProvider</code> is the label provider for the table 
 * viewer in 
 * {@link de.ptb.epics.eve.editor.views.scanmoduleview.positioningcomposite.PositioningComposite}.
 * 
 * @author ?
 * @author Marcus Michalsky
 */
public class LabelProvider extends ActionCompositeLabelProvider {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Image getColumnImage(final Object positioning, final int colIndex) {
		final Positioning pos = (Positioning)positioning;
		if (colIndex == 0) {
			return getDeleteImage();
		} else if (colIndex == 2) { // Plugin column
			for(IModelError error : pos.getModelErrors()) {
				if (error instanceof PluginError) {
					final PluginError pluginError = (PluginError)error;
					if (pluginError.getPluginErrorType() == 
							PluginErrorTypes.PLUING_NOT_SET) {
						return getErrorImage();
					}
				}
			}
		} else if (colIndex == 3) { // Detector Channel column
			for(IModelError error : pos.getModelErrors()) {
				if (error instanceof PositioningError) {
					final PositioningError posError = (PositioningError)error;
					if (posError.getErrorType() == 
							PositioningErrorTypes.NO_DETECTOR_CHANNEL_SET) {
						return getErrorImage();
					}
				}
			}
		} else if (colIndex == 5) { // Parameters column
			for(IModelError error : pos.getModelErrors()) {
				if (error instanceof PluginError) {
					final PluginError pluginError = (PluginError)error;
					if (pluginError.getPluginErrorType() == 
							PluginErrorTypes.MISSING_MANDATORY_PARAMETER) {
						return getErrorImage();
					} else if (pluginError.getPluginErrorType() == 
							PluginErrorTypes.WRONG_VALUE) {
						return getErrorImage();
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
}