package de.ptb.epics.eve.editor.views.scanmoduleview.positioningcomposite;

import java.util.Iterator;

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

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Image getColumnImage(final Object positioning, final int colIndex) {
		final Positioning pos = (Positioning)positioning;
		if( colIndex == 1 ) {
			final Iterator< IModelError > it = pos.getModelErrors().iterator();
			while( it.hasNext() )  {
				final IModelError modelError = it.next();
				if( modelError instanceof PluginError ) {
					final PluginError pluginError = (PluginError)modelError;
					if( pluginError.getPluginErrorType() == PluginErrorTypes.PLUING_NOT_SET ) {
						return PlatformUI.getWorkbench().getSharedImages().getImage( ISharedImages.IMG_OBJS_ERROR_TSK );
					}
				}
			}
		} else if( colIndex == 2 ) {
			final Iterator< IModelError > it = pos.getModelErrors().iterator();
			while( it.hasNext() )  {
				final IModelError modelError = it.next();
				if( modelError instanceof PositioningError ) {
					final PositioningError positioningError = (PositioningError)modelError;
					if( positioningError.getErrorType() == PositioningErrorTypes.NO_DETECTOR_CHANNEL_SET ) {
						return PlatformUI.getWorkbench().getSharedImages().getImage( ISharedImages.IMG_OBJS_ERROR_TSK );
					}
				}
			}
		} else if( colIndex == 4 ) {
			final Iterator< IModelError > it = pos.getModelErrors().iterator();
			while( it.hasNext() )  {
				final IModelError modelError = it.next();
				if( modelError instanceof PluginError ) {
					final PluginError pluginError = (PluginError)modelError;
					if( pluginError.getPluginErrorType() == PluginErrorTypes.MISSING_MANDATORY_PARAMETER ) {
						return PlatformUI.getWorkbench().getSharedImages().getImage( ISharedImages.IMG_OBJS_ERROR_TSK );
					} else if( pluginError.getPluginErrorType() == PluginErrorTypes.WRONG_VALUE ) {
						return PlatformUI.getWorkbench().getSharedImages().getImage( ISharedImages.IMG_OBJS_ERROR_TSK );
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
		switch( colIndex ) {
			case 0:
				return (pos.getMotorAxis()!=null)?pos.getMotorAxis().getFullIdentifyer():"";
			case 1:
				return (pos.getPluginController().getPlugin()!=null)?pos.getPluginController().getPlugin().getName():"";
			case 2:
				return (pos.getDetectorChannel()!=null)?pos.getDetectorChannel().getFullIdentifyer():"";
			case 3:
				return (pos.getNormalization()!=null)?pos.getNormalization().getFullIdentifyer():"";
			case 4:
				return (pos.getPluginController().getPlugin()!=null)?pos.getPluginController().toString():"Choose a plugin to see options";
		}
		return "";
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
	public void removeListener(final ILabelProviderListener arg0) {
	}
}