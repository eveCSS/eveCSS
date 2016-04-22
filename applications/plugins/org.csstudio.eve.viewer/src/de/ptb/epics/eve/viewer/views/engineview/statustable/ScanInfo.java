package de.ptb.epics.eve.viewer.views.engineview.statustable;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import de.ptb.epics.eve.ecp1.types.ChainStatus;

/**
 * Represents status information of a scan (and its contained chains).
 * 
 * @author Marcus Michalsky
 * @since 1.26
 */
public class ScanInfo {
	private Map<Integer, ChainInfo> chainInfos;
	
	public ScanInfo() {
		this.chainInfos = new HashMap<>();
	}
	
	/**
	 * Returns status information of all contained chains.
	 * @return status information of all contained chains
	 */
	public Collection<ChainInfo> getChainInfos() {
		return this.chainInfos.values();
	}
	
	/**
	 * Returns status information of the chain with the given chain id
	 * @param chainId the id of the chain to get status information from
	 * @return status information of the chain with the given id
	 */
	public ChainInfo getChainInfo(int chainId) {
		return chainInfos.get(chainId);
	}
	
	/**
	 * Sets the status of the chain with the given id.
	 * @param chainId the id of the chain of which the status should be set
	 * @param chainStatus the status to set
	 */
	public void setChainStatus(int chainId, ChainStatus chainStatus) {
		ChainInfo chainInfo = this.chainInfos.get(chainId);
		if (chainInfo == null) {
			chainInfo = new ChainInfo(chainId);
			this.chainInfos.put(chainId, chainInfo);
		}
		chainInfo.setStatus(chainStatus);
	}
}