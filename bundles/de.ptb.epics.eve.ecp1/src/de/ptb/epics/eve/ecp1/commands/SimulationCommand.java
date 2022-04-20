package de.ptb.epics.eve.ecp1.commands;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import de.ptb.epics.eve.ecp1.intern.exceptions.BrokenByteArrayException;
import de.ptb.epics.eve.ecp1.intern.exceptions.WrongLengthException;
import de.ptb.epics.eve.ecp1.intern.exceptions.WrongStartTagException;
import de.ptb.epics.eve.ecp1.intern.exceptions.WrongTypeIdException;
import de.ptb.epics.eve.ecp1.intern.exceptions.WrongVersionException;

/**
 * @since 1.37
 */
public class SimulationCommand implements IECP1Command {
	public static final char COMMAND_TYPE_ID = 0x0015;
	
	private boolean simulation;
	
	public SimulationCommand() {
		this.simulation = false;
	}
	
	public SimulationCommand(final boolean simulation) {
		this.simulation = simulation;
	}
	
	public SimulationCommand(final byte[] byteArray) throws IOException, 
			WrongStartTagException, WrongVersionException, WrongTypeIdException, 
			WrongLengthException, BrokenByteArrayException {
		if (byteArray == null) {
			throw new IllegalArgumentException(
					"The parameter 'byteArray' must not be null!");
		}
		final ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(
				byteArray);
		final DataInputStream dataInputStream = new DataInputStream(
				byteArrayInputStream);

		final int startTag = dataInputStream.readInt();
		if (startTag != IECP1Command.START_TAG) {
			throw new WrongStartTagException(byteArray, startTag);
		}

		final char version = dataInputStream.readChar();
		if (version != IECP1Command.VERSION) {
			throw new WrongVersionException(byteArray, version);
		}
		
		final char commandTypeID = dataInputStream.readChar();
		if(commandTypeID != SimulationCommand.COMMAND_TYPE_ID) {
			throw new WrongTypeIdException(byteArray, commandTypeID, 
					SimulationCommand.COMMAND_TYPE_ID);
		}
		
		final int length = dataInputStream.readInt();
		if (length != 4) {
			throw new WrongLengthException(byteArray, length, 4);
		}
		
		final int sim = dataInputStream.readInt();
		if (sim == 0) {
			this.simulation = false;
		} else if (sim == 1) {
			this.simulation = true;
		} else {
			throw new BrokenByteArrayException(byteArray, 
				"Wrong value for simulation. Expected 0 or 1, got " + 
						simulation + "!");
		}
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public byte[] getByteArray() throws IOException {
		final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		final DataOutputStream dataOutputStream = new DataOutputStream(
				byteArrayOutputStream);

		dataOutputStream.writeInt(IECP1Command.START_TAG);
		dataOutputStream.writeChar(IECP1Command.VERSION);
		
		dataOutputStream.writeChar(SimulationCommand.COMMAND_TYPE_ID);
		dataOutputStream.writeInt(4);
		if (this.simulation) {
			dataOutputStream.writeInt(1);
		} else {
			dataOutputStream.writeInt(0);
		}

		dataOutputStream.close();
		return byteArrayOutputStream.toByteArray();
	}
	
	public boolean isSimulation() {
		return this.simulation;
	}
}
