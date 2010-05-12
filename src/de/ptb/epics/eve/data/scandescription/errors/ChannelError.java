package de.ptb.epics.eve.data.scandescription.errors;

import de.ptb.epics.eve.data.scandescription.Channel;

public class ChannelError implements IModelError {

	private final Channel channel;
	private final ChannelErrorTypes errorType;
	
	
	public ChannelError( final Channel channel, final ChannelErrorTypes errorType ) {
		if( channel == null ) {
			throw new IllegalArgumentException( "The parameter 'channel' must not be null!" );
		}
		if( errorType == null ) {
			throw new IllegalArgumentException( "The parameter 'errorType' must not be null!" );
		}
		this.channel = channel;
		this.errorType = errorType;
	}


	public Channel getChannel() {
		return this.channel;
	}


	public ChannelErrorTypes getErrorType() {
		return this.errorType;
	}


	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((channel == null) ? 0 : channel.hashCode());
		result = prime * result + ((errorType == null) ? 0 : errorType.hashCode());
		return result;
	}


	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals( final Object obj ) {
		if( this == obj ) {
			return true;
		}
		if( obj == null ) {
			return false;
		}
		if( getClass() != obj.getClass() ) {
			return false;
		}
		final ChannelError other = (ChannelError)obj;
		if( channel == null ) {
			if( other.channel != null ) {
				return false;
			}
		} else if( !channel.equals(other.channel ) ) {
			return false;
		}
		if( errorType == null ) {
			if( other.errorType != null ) {
				return false;
			}
		} else if( !errorType.equals(other.errorType ) ) {
			return false;
		}
		return true;
	}


	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "ChannelError [channel=" + channel + ", errorType=" + errorType + "]";
	}

	/*
	 * (non-Javadoc)
	 * @see de.ptb.epics.eve.data.scandescription.errors.IModelError#getErrorMessage()
	 */
	@Override
	public String getErrorMessage() {
		return "Error in channel " + this.channel + " because " + errorType;
	}

	/*
	 * (non-Javadoc)
	 * @see de.ptb.epics.eve.data.scandescription.errors.IModelError#getErrorName()
	 */
	@Override
	public String getErrorName() {
		return "Channel Error";
	}
	
	
}
