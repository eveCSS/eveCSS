package de.ptb.epics.eve.ecp1.commands;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import de.ptb.epics.eve.ecp1.intern.exceptions.AbstractRestoreECP1CommandException;

import de.ptb.epics.eve.ecp1.intern.exceptions.WrongLengthException;
import de.ptb.epics.eve.ecp1.intern.exceptions.WrongStartTagException;
import de.ptb.epics.eve.ecp1.intern.exceptions.WrongTypeIdException;
import de.ptb.epics.eve.ecp1.intern.exceptions.WrongVersionException;
import de.ptb.epics.eve.ecp1.types.ScanModuleReason;
import de.ptb.epics.eve.ecp1.types.ScanModuleStatus;
import de.ptb.epics.eve.ecp1.types.ChainStatus;

public class ChainStatusCommand implements IECP1Command {

	public static final char COMMAND_TYPE_ID = 0x0112;

	private int chainId;
	private int generalTimeStamp;
	private int nanoseconds;
	private ChainStatus chainStatus;
	private Map<Integer,Integer> sMFullStatus;
	
	public ChainStatusCommand(final byte[] byteArray) throws IOException,
			AbstractRestoreECP1CommandException {

		sMFullStatus = new HashMap<Integer,Integer>();
		
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
		if (commandTypeID != ChainStatusCommand.COMMAND_TYPE_ID) {
			throw new WrongTypeIdException(byteArray, commandTypeID,
					ChainStatusCommand.COMMAND_TYPE_ID);
		}

		int datalength = dataInputStream.readInt();
		if ((datalength < 16) || (datalength % 8 != 0)) {
			throw new WrongLengthException(byteArray, datalength, 16);
		}

		this.generalTimeStamp = dataInputStream.readInt();
		this.nanoseconds = dataInputStream.readInt();
		this.chainId = dataInputStream.readInt();
		this.chainStatus = ChainStatus.intToChainStatus(dataInputStream.readInt());
		
		datalength -= 16;
		while (datalength > 0){
			int smid = dataInputStream.readInt();
			int smstatus = dataInputStream.readInt();
			if (!sMFullStatus.containsKey(smid)){
				sMFullStatus.put(smid, smstatus);				
			}
			datalength -= 8;
		}
	}
	
	public byte[] getByteArray() throws IOException {
		final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		final DataOutputStream dataOutputStream = new DataOutputStream(
				byteArrayOutputStream);

		dataOutputStream.writeInt(IECP1Command.START_TAG);
		dataOutputStream.writeChar(IECP1Command.VERSION);
		
		dataOutputStream.writeChar(MeasurementDataCommand.COMMAND_TYPE_ID);

		final int length = 16 + sMFullStatus.size()*8;

		dataOutputStream.writeInt(length);
		dataOutputStream.writeInt(chainId);
		dataOutputStream.writeInt(ChainStatus.chainStatusToInt(chainStatus));
		for (int smid : sMFullStatus.keySet()) {
			dataOutputStream.writeInt(smid);
			dataOutputStream.writeInt(sMFullStatus.get(smid));
		}
		dataOutputStream.close();

		return byteArrayOutputStream.toByteArray();
	}
	
	public Boolean isAnyScanModulePaused(){
		for (int smid : sMFullStatus.keySet()) {
			if (getScanModuleStatus(smid) == ScanModuleStatus.PAUSED ) return true;
		}
		return false;
	}
	
	public int getChainId() {
		return this.chainId;
	}

	public ChainStatus getChainStatus() {
		return this.chainStatus;
	}
	
	public ScanModuleStatus getScanModuleStatus(int smid) {
		if (sMFullStatus.containsKey(smid)){
			return ScanModuleStatus.intToScanModuleStatus(sMFullStatus.get(smid) >> 16);
		}
		return ScanModuleStatus.UNKNOWN;
	}

	public ScanModuleReason getScanModuleReason(int smid) {
		if (sMFullStatus.containsKey(smid)){
			return ScanModuleReason.intToScanModuleReason(sMFullStatus.get(smid) & 0xff);
		}
		return ScanModuleReason.NONE;		
	}

	public Set<Integer> getAllScanModuleIds() {
		return this.sMFullStatus.keySet();
	}

	public int getTimeStampSeconds() {
		return this.generalTimeStamp;
	}

	public int getTimeStampNanoSeconds() {
		return this.nanoseconds;
	}
}