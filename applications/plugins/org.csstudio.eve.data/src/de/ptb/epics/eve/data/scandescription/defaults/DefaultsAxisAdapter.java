package de.ptb.epics.eve.data.scandescription.defaults;

import javax.xml.bind.annotation.adapters.XmlAdapter;

/**
 * @author Marcus Michalsky
 * @since 1.9
 */
public class DefaultsAxisAdapter extends
		XmlAdapter<DefaultsAxisAdaptee, DefaultsAxis> {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public DefaultsAxisAdaptee marshal(DefaultsAxis from) throws Exception {
		DefaultsAxisAdaptee to = new DefaultsAxisAdaptee();
		to.setId(from.getId());
		to.setPositionmode(from.getPositionmode());
		to.setStepfunction(from.getStepfunction());
		switch(from.getStepfunction()) {
		case ADD:
		case MULTIPLY:
			// TODO
			break;
		case FILE:
			to.setMode(from.getFile());
			break;
		case PLUGIN:
			// TODO
			break;
		case POSITIONLIST:
			to.setMode(from.getPositionList());
			break;
		}
		return to;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public DefaultsAxis unmarshal(DefaultsAxisAdaptee from) throws Exception {
		DefaultsAxis to = new DefaultsAxis();
		to.setId(from.getId());
		to.setPositionmode(from.getPositionmode());
		to.setStepfunction(from.getStepfunction());
		switch(from.getStepfunction()) {
		case ADD:
		case MULTIPLY:
			// TODO
			break;
		case FILE:
			to.setFile(from.getMode().toString());
			break;
		case PLUGIN:
			// TODO
			break;
		case POSITIONLIST:
			to.setPositionList(from.getMode().toString());
			break;
		}
		return to;
	}
}