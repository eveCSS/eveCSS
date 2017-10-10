package de.ptb.epics.eve.ecp1.client;

import de.ptb.epics.eve.ecp1.client.interfaces.IPlayController;
import de.ptb.epics.eve.ecp1.commands.BreakCommand;
import de.ptb.epics.eve.ecp1.commands.EndProgramCommand;
import de.ptb.epics.eve.ecp1.commands.HaltCommand;
import de.ptb.epics.eve.ecp1.commands.LiveDescriptionCommand;
import de.ptb.epics.eve.ecp1.commands.PauseCommand;
import de.ptb.epics.eve.ecp1.commands.StartCommand;
import de.ptb.epics.eve.ecp1.commands.StopCommand;

public class PlayController implements IPlayController {

	private ECP1Client ecp1Client;

	protected PlayController(final ECP1Client ecp1Client) {
		if (ecp1Client == null) {
			throw new IllegalArgumentException(
					"The paramter 'ecp1Client' must not be null!");
		}
		this.ecp1Client = ecp1Client;
	}

	public void addLiveComment(final String liveComment) {
		this.ecp1Client.addToOutQueue(new LiveDescriptionCommand(liveComment));

	}

	public void breakExecution() {
		this.ecp1Client.addToOutQueue(new BreakCommand());

	}

	public void halt() {
		this.ecp1Client.addToOutQueue(new HaltCommand());

	}

	public void shutdownEngine() {
		this.ecp1Client.addToOutQueue(new EndProgramCommand());

	}

	public void start() {
		this.ecp1Client.addToOutQueue(new StartCommand());

	}

	public void stop() {
		this.ecp1Client.addToOutQueue(new StopCommand());

	}

	public void pause() {
		this.ecp1Client.addToOutQueue(new PauseCommand());

	}
}