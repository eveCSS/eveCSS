package de.ptb.epics.eve.ecp1.client.interfaces;

public interface IPlayController {

	public void start();
	public void stop();
	public void pause();
	public void breakExecution();
	public void halt();
	public void shutdownEngine();
	public void addLiveComment( final String liveComment );
	
}
