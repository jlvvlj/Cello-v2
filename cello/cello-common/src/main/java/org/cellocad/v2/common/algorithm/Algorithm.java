/**
 * Copyright (C) 2017-2020
 * Massachusetts Institute of Technology (MIT)
 * Boston University (BU)
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:

 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.

 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package org.cellocad.v2.common.algorithm;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cellocad.v2.common.CObject;
import org.cellocad.v2.common.CelloException;
import org.cellocad.v2.common.Utils;
import org.cellocad.v2.common.netlistConstraint.data.NetlistConstraint;
import org.cellocad.v2.common.profile.AlgorithmProfile;
import org.cellocad.v2.common.runtime.environment.RuntimeEnv;
import org.cellocad.v2.common.target.data.TargetData;
import org.cellocad.v2.results.common.Results;
import org.cellocad.v2.results.netlist.Netlist;

/**
 * Algorithm class is the base class for all algorithms using the Poros
 * framework.
 *
 * @author Vincent Mirian
 * @author Timothy Jones
 *
 * @date Nov 17, 2017
 *
 */
public abstract class Algorithm extends CObject {

	/**
	 * Executes the algorithm. Executes the following methods in sequential
	 * order:<br>
	 * {@link #getConstraintFromNetlistConstraintFile()}<br>
	 * {@link #getDataFromUCF()}<br>
	 * {@link #setParameterValues()}<br>
	 * {@link #validateParameterValues()}<br>
	 * {@link #preprocessing()}<br>
	 * {@link #run()}<br>
	 * {@link #postprocessing()}<br>
	 *
	 * @param netlist           Netlist used during execution
	 * @param targetData        TargetData used during execution
	 * @param netlistConstraint NetlistConstraint used during execution
	 * @param results           Results used during execution
	 * @param AProfile          AlgorithmProfile used during execution
	 * @param runtimeEnv        RuntimeEnv used during execution
	 * @throws CelloException
	 * @throws RuntimeException if any of the parameters are null
	 */
	public void execute(final Netlist netlist, final TargetData targetData, final NetlistConstraint netlistConstraint,
	        final Results results, final AlgorithmProfile AProfile, final RuntimeEnv runtimeEnv) throws CelloException {
		Utils.isNullRuntimeException(netlist, "netlist");
		Utils.isNullRuntimeException(targetData, "targetData");
		Utils.isNullRuntimeException(netlistConstraint, "netlistConstraint");
		Utils.isNullRuntimeException(results, "results");
		Utils.isNullRuntimeException(AProfile, "AProfile");
		Utils.isNullRuntimeException(runtimeEnv, "runtimeEnv");
		// init
		this.setNetlist(netlist);
		this.setTargetData(targetData);
		this.setNetlistConstraint(netlistConstraint);
		this.setResults(results);
		this.setAlgorithmProfile(AProfile);
		this.setRuntimeEnv(runtimeEnv);
		// execute
		this.getConstraintFromNetlistConstraintFile();
		this.getDataFromUCF();
		this.setParameterValues();
		this.validateParameterValues();
		this.preprocessing();
		this.run();
		this.postprocessing();
	}

	/*
	 * Getter and Setter
	 */

	/**
	 * Setter for <i>netlist</i>
	 *
	 * @param netlist the Netlist to set <i>netlist</i>
	 */
	private void setNetlist(final Netlist netlist) {
		this.netlist = netlist;
	}

	/**
	 * Getter for <i>netlist</i>
	 *
	 * @return the Netlist of this instance
	 */
	protected Netlist getNetlist() {
		return this.netlist;
	}

	/**
	 * Setter for <i>targetData</i>
	 *
	 * @param targetData the TargetData to set <i>targetData</i>
	 */
	private void setTargetData(final TargetData targetData) {
		this.targetData = targetData;
	}

	/**
	 * Getter for <i>targetData</i>
	 *
	 * @return the TargetData of this instance
	 */
	protected TargetData getTargetData() {
		return this.targetData;
	}

	/**
	 * Setter for <i>netlistConstraint</i>
	 *
	 * @param netlistConstraint the NetlistConstraint to set
	 *                          <i>netlistConstraint</i>
	 */
	private void setNetlistConstraint(final NetlistConstraint netlistConstraint) {
		this.netlistConstraint = netlistConstraint;
	}

	/**
	 * Getter for <i>netlistConstraint</i>
	 *
	 * @return the NetlistConstraint of this instance
	 */
	protected NetlistConstraint getNetlistConstraint() {
		return this.netlistConstraint;
	}

	/**
	 * Getter for {@code results}.
	 *
	 * @return The value of {@code results}.
	 */
	protected Results getResults() {
		return this.results;
	}

	/**
	 * Setter for {@code results}.
	 *
	 * @param results The value to set {@code results}.
	 */
	private void setResults(Results results) {
		this.results = results;
	}

	/**
	 * Setter for <i>AProfile</i>
	 *
	 * @param AProfile the AlgorithmProfile to set <i>AProfile</i>
	 */
	private void setAlgorithmProfile(final AlgorithmProfile AProfile) {
		this.AProfile = AProfile;
	}

	/**
	 * Getter for <i>AProfile</i>
	 *
	 * @return the AlgorithmProfile of this instance
	 */
	protected AlgorithmProfile getAlgorithmProfile() {
		return this.AProfile;
	}

	/**
	 * Setter for <i>runtimeEnv</i>
	 *
	 * @param runtimeEnv the RuntimeEnv to set <i>runtimeEnv</i>
	 */
	private void setRuntimeEnv(final RuntimeEnv runtimeEnv) {
		this.runtimeEnv = runtimeEnv;
	}

	/**
	 * Getter for <i>runtimeEnv</i>
	 *
	 * @return the RuntimeEnv of this instance
	 */
	protected RuntimeEnv getRuntimeEnv() {
		return this.runtimeEnv;
	}

	private Netlist netlist;
	private TargetData targetData;
	private NetlistConstraint netlistConstraint;
	private Results results;
	private AlgorithmProfile AProfile;
	private RuntimeEnv runtimeEnv;

	/**
	 * Gets the Constraint data from the NetlistConstraintFile
	 */
	abstract protected void getConstraintFromNetlistConstraintFile();

	/**
	 * Gets the data from the UCF
	 *
	 * @throws CelloException
	 */
	abstract protected void getDataFromUCF() throws CelloException;

	/**
	 * Set parameter(s) value(s) of the algorithm
	 */
	abstract protected void setParameterValues();

	/**
	 * Validate parameter value of the algorithm
	 */
	abstract protected void validateParameterValues();

	/**
	 * Perform preprocessing
	 *
	 * @throws CelloException
	 */
	abstract protected void preprocessing() throws CelloException;

	/**
	 * Run the (core) algorithm
	 *
	 * @throws CelloException
	 */
	abstract protected void run() throws CelloException;

	/**
	 * Perform postprocessing
	 *
	 * @throws CelloException
	 */
	abstract protected void postprocessing() throws CelloException;

	/**
	 * Log parameter <i>str</i> at the Trace level
	 *
	 * @param str string to log
	 */
	protected void logTrace(String str) {
		this.getLogger().trace(str);
	}

	/**
	 * Log parameter <i>str</i> at the Debug level
	 *
	 * @param str string to log
	 */
	protected void logDebug(String str) {
		this.getLogger().debug(str);
	}

	/**
	 * Log parameter <i>str</i> at the Info level
	 *
	 * @param str string to log
	 */
	protected void logInfo(String str) {
		this.getLogger().info(str);
	}

	/**
	 * Log parameter <i>str</i> at the Warn level
	 *
	 * @param str string to log
	 */
	protected void logWarn(String str) {
		this.getLogger().warn(str);
	}

	/**
	 * Log parameter <i>str</i> at the Error level
	 *
	 * @param str string to log
	 */
	protected void logError(String str) {
		this.getLogger().error(str);
	}

	/**
	 * Log parameter <i>str</i> at the Fatal level
	 *
	 * @param str string to log
	 */
	protected void logFatal(String str) {
		this.getLogger().fatal(str);
	}

	/**
	 * Returns the Logger instance for the class
	 *
	 * @return the Logger instance for the class
	 */
	protected Logger getLogger() {
		return Algorithm.logger;
	}

	private static final Logger logger = LogManager.getLogger(Algorithm.class);

}
