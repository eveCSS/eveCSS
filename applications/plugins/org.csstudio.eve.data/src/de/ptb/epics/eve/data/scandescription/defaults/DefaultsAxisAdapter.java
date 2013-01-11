package de.ptb.epics.eve.data.scandescription.defaults;

import java.text.SimpleDateFormat;

import javax.xml.bind.annotation.adapters.XmlAdapter;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.Duration;

import de.ptb.epics.eve.data.DataTypes;
import de.ptb.epics.eve.data.scandescription.PositionMode;

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
		DefaultsAxisModeAdaptee mode = new DefaultsAxisModeAdaptee();
		switch(from.getStepfunction()) {
		case ADD:
		case MULTIPLY:
			mode.setMainAxis(from.isMainAxis());
			switch(from.getType()) {
			case DATETIME:
				if (from.getPositionmode().equals(PositionMode.ABSOLUTE)) {
					mode.setStart(new DefaultsAxisTypeValue(from.getType(),
							new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS")
									.format(from.getStart())));
					mode.setStop(new DefaultsAxisTypeValue(from.getType(),
							new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS")
									.format(from.getStop())));
					mode.setStepwidth(new DefaultsAxisTypeValue(from.getType(),
							new SimpleDateFormat("HH:mm:ss.SSS")
									.format(from.getStepwidth())));
				} else {
					mode.setStart(new DefaultsAxisTypeValue(from.getType(),
							((Duration) from.getStart()).toString()));
					mode.setStop(new DefaultsAxisTypeValue(from.getType(),
							((Duration) from.getStop()).toString()));
					mode.setStepwidth(new DefaultsAxisTypeValue(from.getType(),
							((Duration) from.getStepwidth()).toString()));
				}
				break;
			case DOUBLE:
			case INT:
				mode.setStart(new DefaultsAxisTypeValue(from.getType(), from
						.getStart().toString()));
				mode.setStop(new DefaultsAxisTypeValue(from.getType(), from
						.getStop().toString()));
				mode.setStepwidth(new DefaultsAxisTypeValue(from.getType(), from
						.getStepwidth().toString()));
				break;
			default:
				break;
			}
			to.setMode(mode);
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
			if (from.getMode() instanceof DefaultsAxisModeAdaptee) {
				DefaultsAxisModeAdaptee adaptee = (DefaultsAxisModeAdaptee) 
						from.getMode();
				switch(adaptee.getStart().getType()) {
				case DATETIME:
					to.setStartStopStepType(DataTypes.DATETIME);
					if (from.getPositionmode().equals(PositionMode.ABSOLUTE)) {
						SimpleDateFormat format = new SimpleDateFormat(
								"yyyy-MM-dd HH:mm:ss.SSS");
						to.setStart(format.parse(adaptee.getStart().getValue()));
						to.setStop(format.parse(adaptee.getStop().getValue()));
						to.setStepwidth(new SimpleDateFormat("HH:mm:ss.SSS")
								.parse((adaptee.getStepwidth()
								.getValue())));
					} else {
						DatatypeFactory factory = DatatypeFactory
								.newInstance();
						to.setStart(factory.newDuration(adaptee.getStart()
								.getValue()));
						to.setStop(factory.newDuration(adaptee.getStop()
								.getValue()));
						to.setStepwidth(factory.newDuration(adaptee
								.getStepwidth().getValue()));
					}
					break;
				case DOUBLE:
					to.setStartStopStepType(DataTypes.DOUBLE);
					to.setStart(Double.parseDouble(adaptee.getStart()
							.getValue()));
					to.setStop(Double.parseDouble(adaptee.getStop().getValue()));
					to.setStepwidth(Double.parseDouble(adaptee.getStepwidth()
							.getValue()));
					break;
				case INT:
					to.setStartStopStepType(DataTypes.INT);
					to.setStart(Integer.parseInt(adaptee.getStart().getValue()));
					to.setStop(Integer.parseInt(adaptee.getStop().getValue()));
					to.setStepwidth(Integer.parseInt(adaptee.getStepwidth()
							.getValue()));
					break;
				default:
					break;
				}
			}
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