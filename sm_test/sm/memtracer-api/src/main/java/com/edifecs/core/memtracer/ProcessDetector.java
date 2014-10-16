// -----------------------------------------------------------------------------
//  Copyright (c) Edifecs Inc. All Rights Reserved.
//
// This software is the confidential and proprietary information of Edifecs Inc.
// ("Confidential Information").  You shall not disclose such Confidential
// Information and shall use it only in accordance with the terms of the license
// agreement you entered into with Edifecs.
//
// EDIFECS MAKES NO REPRESENTATIONS OR WARRANTIES ABOUT THE SUITABILITY OF THE
// SOFTWARE, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE IMPLIED
// WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE, OR
// NON-INFRINGEMENT. EDIFECS SHALL NOT BE LIABLE FOR ANY DAMAGES SUFFERED BY
// LICENSEE AS A RESULT OF USING, MODIFYING OR DISTRIBUTING THIS SOFTWARE OR
// ITS DERIVATIVES.
// -----------------------------------------------------------------------------
package com.edifecs.core.memtracer;

import org.hyperic.sigar.Sigar;
import org.hyperic.sigar.SigarException;
import org.hyperic.sigar.ptql.ProcessQuery;
import org.hyperic.sigar.ptql.ProcessQueryFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.edifecs.core.configuration.helper.SystemVariables;

/**
 * <p>
 * Utility methods that use Sigar to detect existing Agent and Node processes.
 * </p>
 * 
 * @author i-adamnels
 */
public final class ProcessDetector {
    private static final Logger logger = LoggerFactory.getLogger(ProcessDetector.class);

	private ProcessDetector() { /* SPACE FOR RENT */
	}

	/**
	 * <p>
	 * Hunts down and kills rogue Node processes. Sounds a lot more awesome than
	 * it actually is.
	 * </p>
	 */
	public static void terminateAllNodes() {
		try {
			Sigar sigar = new Sigar();
			ProcessQuery query = new ProcessQueryFactory()
					.getQuery("State.Name.eq=java,"
							+ "Args.*.re=com\\.edifecs\\.agent\\.launcher\\.NodeStarter");
			long[] pids = query.find(sigar);
			if (pids.length > 0) {
				logger.warn("Detected " + pids.length + " unmanaged nodes running!");
				logger.warn("Killing unmanaged nodes.");
				for (long pid : pids) {
                    logger.error("Force killed process: {}", pid);
					sigar.kill(pid, -9); // Apparently, -9 is the
												// "magic number" to force-kill
												// a process.
				}
			}
		} catch (SigarException ex) {
			logger.error("A Sigar error occurred, preventing the agent from detecting still-running nodes.", ex);
		}
	}

	/**
	 * <p>
	 * Detects all running Edifecs Agent processes (Java processes with the main
	 * class <code>com.edifecs.agent.launcher.AgentStarter</code>), and returns
	 * <code>true</code> if more than one such process is running.
	 * </p>
	 * 
	 * @return <code>true</code> if more than one agent process is currently
	 *         running.
	 * @throws SigarException 
	 */
	public static boolean isOtherAgentRunning() throws SigarException {
	    System.setProperty("org.hyperic.sigar.path", SystemVariables.NATIVE_LIB_PATH);

		Sigar sigar = new Sigar();
		ProcessQuery query = new ProcessQueryFactory()
				.getQuery("State.Name.re=^java[w]?$,"
						+ "Args.*.re=com\\.edifecs\\.agent\\.launcher\\.AgentStarter");
		long[] pids = query.find(sigar);
		return pids.length > 1;
	}

	public static String getAgentPID() {
		try {
			Sigar sigar = new Sigar();

			ProcessQuery query = new ProcessQueryFactory()
					.getQuery("State.Name.re=^java[w]?$,"
							+ "Args.*.re=com\\.edifecs\\.agent\\.launcher\\.AgentStarter");

			long[] pids = query.find(sigar);
			if (pids.length > 0) {
                return String.valueOf(pids[0]);
            } else {
                return null;
            }

		} catch (SigarException ex) {
			throw new RuntimeException(
					"A Sigar error occurred, preventing the agent from detecting other Agent processes: "
							+ ex.getMessage());
		}
	}

	public static String getPIDForNode(String nodeName) {
		try {
			Sigar sigar = new Sigar();

			ProcessQuery query = new ProcessQueryFactory()
					.getQuery("State.Name.eq=java,"
							+ "Args.*.re=com\\.edifecs\\.agent\\.launcher\\.NodeStarter");

			String nodePID = null;

			for (long pid : query.find(sigar)) {

				for (String proc : sigar.getProcArgs(pid)) {

					String[] arg = proc.split("=");
					if (arg.length >= 2) {
						if (arg[0].equalsIgnoreCase("-nodeName")
								&& arg[1].equals(nodeName)) {

							nodePID = String.valueOf(pid);
							break;
						}
					}
				}
			}

			return nodePID;

		} catch (SigarException ex) {
			throw new RuntimeException(
					"A Sigar error occurred, preventing the agent from detecting other Agent processes: "
							+ ex.getMessage());
		}
	}
}
