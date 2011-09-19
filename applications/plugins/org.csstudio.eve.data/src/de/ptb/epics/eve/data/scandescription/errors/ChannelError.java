package de.ptb.epics.eve.data.scandescription.errors;

import de.ptb.epics.eve.data.scandescription.Channel;

/**
 * <code>ChannelError</code> represents an error which occurred in a 
 * {@link de.ptb.epics.eve.data.scandescription.Channel}.
 * 
 * @author ?
 * @author Marcus Michalsky
 */
public class ChannelError implements IModelError {

	// the channel where the error occurred
	private final Channel channel;
	
	// the error type
	private final ChannelErrorTypes errorType;
	
	/**
	 * Constructs a <code>ChannelError</code>.
	 * 
	 * @param channel the {@link de.ptb.epics.eve.data.scandescription.Channel} 
	 * 				  where the error occurred
	 * @param errorType the error type
	 * @throws IllegalArgumentException 
	 * 			<ul>
	 * 			  <li>if <code>channel</code> is <code>null</code></li>
	 * 			  <li>if <code>errorType</code> is <code>null</code></li»
	 * 			</ul>
	 */
	public ChannelError(final Channel channel, final ChannelErrorTypes errorType) {
		if(channel == null) {
			throw new IllegalArgumentException(
					"The parameter 'channel' must not be null!");
		}
		if(errorType == null) {
			throw new IllegalArgumentException(
					"The parameter 'errorType' must not be null!");
		}
		this.channel = channel;
		this.errorType = errorType;
	}

	/**#
	 * Returns the {@link de.ptb.epics.eve.data.scandescription.Channel} where 
	 * the error occurred.
	 * 
	 * @return the {@link de.ptb.epics.eve.data.scandescription.Channel} where 
	 * 		   the error occurred
	 */
	public Channel getChannel() {
		return this.channel;
	}

	/**
	 * Returns the error type as defined in 
	 * {@link de.ptb.epics.eve.data.scandescription.errors.ChannelErrorTypes}.
	 * 
	 * @return the error type
	 */
	public ChannelErrorTypes getErrorType() {
		return this.errorType;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((channel == null) ? 0 : channel.hashCode());
		result = prime * result + 
				 ((errorType == null) ? 0 : errorType.hashCode());
		return result;
	}


	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean equals(final Object obj) {
		if(this == obj) {
			return true;
		}
		if(obj == null) {
			return false;
		}
		if(getClass() != obj.getClass()) {
			return false;
		}
		final ChannelError other = (ChannelError)obj;
		if(channel == null) {
			if(other.channel != null) {
				return false;
			}
		} else if(!channel.equals(other.channel)) {
			return false;
		}
		if(errorType == null) {
			if(other.errorType != null) {
				return false;
			}
		} else if(!errorType.equals(other.errorType)) {
			return false;
		}
		return true;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return "ChannelError [channel=" + channel + 
				", errorType=" + errorType + "]";
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getErrorMessage() {
		return "Error in channel " + this.channel + " because " + errorType;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getErrorName() {
		return "Channel Error";
	}	
}