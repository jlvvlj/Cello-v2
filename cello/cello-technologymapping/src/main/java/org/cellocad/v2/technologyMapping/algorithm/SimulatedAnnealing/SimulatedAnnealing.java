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
package org.cellocad.v2.technologyMapping.algorithm.SimulatedAnnealing;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cellocad.v2.common.CObjectCollection;
import org.cellocad.v2.common.CelloException;
import org.cellocad.v2.common.Utils;
import org.cellocad.v2.common.netlistConstraint.data.NetlistConstraint;
import org.cellocad.v2.common.runtime.environment.ArgString;
import org.cellocad.v2.common.target.data.data.AssignableDevice;
import org.cellocad.v2.common.target.data.data.Gate;
import org.cellocad.v2.common.target.data.data.Input;
import org.cellocad.v2.common.target.data.data.InputSensor;
import org.cellocad.v2.common.target.data.data.OutputDevice;
import org.cellocad.v2.results.logicSynthesis.LSResultsUtils;
import org.cellocad.v2.results.logicSynthesis.logic.LSLogicEvaluation;
import org.cellocad.v2.results.logicSynthesis.netlist.LSResultNetlistUtils;
import org.cellocad.v2.results.netlist.Netlist;
import org.cellocad.v2.results.netlist.NetlistEdge;
import org.cellocad.v2.results.netlist.NetlistNode;
import org.cellocad.v2.results.netlist.data.ResultNetlistNodeData;
import org.cellocad.v2.results.technologyMapping.TMResultsUtils;
import org.cellocad.v2.results.technologyMapping.activity.TMActivityEvaluation;
import org.cellocad.v2.results.technologyMapping.cytometry.TMCytometryEvaluation;
import org.cellocad.v2.technologyMapping.algorithm.TMAlgorithm;
import org.cellocad.v2.technologyMapping.algorithm.SimulatedAnnealing.data.assignment.GateManager;
import org.cellocad.v2.technologyMapping.algorithm.SimulatedAnnealing.data.score.ScoreUtils;
import org.cellocad.v2.technologyMapping.algorithm.SimulatedAnnealing.data.toxicity.TMToxicityEvaluation;
import org.cellocad.v2.technologyMapping.algorithm.SimulatedAnnealing.results.ResponsePlotUtils;
import org.cellocad.v2.technologyMapping.algorithm.SimulatedAnnealing.results.SimulatedAnnealingResultsUtils;
import org.cellocad.v2.technologyMapping.target.data.TMTargetDataInstance;
import org.json.simple.JSONObject;

/**
 * The SimulatedAnnealing class implements the <i>SimulatedAnnealing</i>
 * algorithm in the <i>technologyMapping</i> stage.
 *
 * @author Vincent Mirian
 * @author Timothy Jones
 *
 * @date 2018-05-21
 *
 */
public class SimulatedAnnealing extends TMAlgorithm {

	/**
	 * Gets the Constraint data from the NetlistConstraintFile
	 */
	@Override
	protected void getConstraintFromNetlistConstraintFile() {
		NetlistConstraint constraint = getNetlistConstraint();
		String type = "";
		// input constraints
		type = "input_constraints";
		Map<String, String> inputMap = new HashMap<>();
		for (int i = 0; i < constraint.getNumJSONObject(type); i++) {
			JSONObject jObj = constraint.getJSONObjectAtIdx(type, i);
			JSONObject map = (JSONObject) jObj.get("sensor_map");
			for (Object obj : map.keySet()) {
				String key = (String) obj;
				String value = (String) map.get(obj);
				inputMap.put(key, value);
			}
		}
		setInputMap(inputMap);
		// ouptut constraints
		type = "output_constraints";
		Map<String, String> outputMap = new HashMap<>();
		for (int i = 0; i < constraint.getNumJSONObject(type); i++) {
			JSONObject jObj = constraint.getJSONObjectAtIdx(type, i);
			JSONObject map = (JSONObject) jObj.get("reporter_map");
			for (Object obj : map.keySet()) {
				String key = (String) obj;
				String value = (String) map.get(obj);
				outputMap.put(key, value);
			}
		}
		setOutputMap(outputMap);
	}

	/**
	 * Gets the data from the UCF
	 *
	 * @throws CelloException
	 */
	@Override
	protected void getDataFromUCF() throws CelloException {
		TMTargetDataInstance tdi = new TMTargetDataInstance(getTargetData());
		setTargetDataInstance(tdi);
	}

	/**
	 * Set parameter(s) value(s) of the algorithm
	 */
	@Override
	protected void setParameterValues() {
	}

	/**
	 * Validate parameter value of the algorithm
	 */
	@Override
	protected void validateParameterValues() {

	}

	/**
	 * Perform preprocessing
	 */
	protected void setTruthTable() {
		LSResultNetlistUtils.setVertexTypeUsingLSResult(getNetlist());
		setLSLogicEvaluation(new LSLogicEvaluation(getNetlist()));
		logInfo(getLSLogicEvaluation().toString());
	}

	protected void assignInputNodes() {
		// assign input
		CObjectCollection<NetlistNode> inputNodes = LSResultsUtils.getPrimaryInputNodes(getNetlist());
		Map<String, String> inputMap = getInputMap();
		Iterator<InputSensor> it = getTargetDataInstance().getInputSensors().iterator();
		for (int i = 0; i < inputNodes.size(); i++) {
			NetlistNode node = inputNodes.get(i);
			ResultNetlistNodeData data = node.getResultNetlistNodeData();
			if (!it.hasNext()) {
				throw new RuntimeException("Not enough input sensors in the library to cover the netlist inputs.");
			}
			InputSensor sensor = null;
			if (inputMap.containsKey(node.getName())) {
				String value = inputMap.get(node.getName());
				sensor = getTargetDataInstance().getInputSensors().findCObjectByName(value);
			}
			while (sensor == null) {
				InputSensor temp = it.next();
				if (!inputMap.containsValue(temp.getName())) {
					sensor = temp;
				}
			}
			data.setDevice(sensor);
		}
	}

	protected void assignOutputNodes() {
		// assign output
		CObjectCollection<NetlistNode> outputNodes = LSResultsUtils.getPrimaryOutputNodes(getNetlist());
		Map<String, String> outputMap = getOutputMap();
		Iterator<OutputDevice> it = getTargetDataInstance().getOutputDevices().iterator();
		for (int i = 0; i < outputNodes.size(); i++) {
			NetlistNode node = outputNodes.get(i);
			ResultNetlistNodeData data = node.getResultNetlistNodeData();
			if (!it.hasNext()) {
				throw new RuntimeException("Not enough output reporters in the library to cover the netlist outputs.");
			}
			OutputDevice reporter = null;
			if (outputMap.containsKey(node.getName())) {
				String value = outputMap.get(node.getName());
				reporter = getTargetDataInstance().getOutputDevices().findCObjectByName(value);
			}
			while (reporter == null) {
				OutputDevice temp = it.next();
				if (!outputMap.containsValue(temp.getName())) {
					reporter = temp;
				}
			}
			data.setDevice(reporter);
		}
	}

	protected void assignNodes() {
		// assign random gates
		GateManager GM = getGateManager();
		Netlist netlist = getNetlist();
		for (int i = 0; i < netlist.getNumVertex(); i++) {
			NetlistNode node = netlist.getVertexAtIdx(i);
			if (LSResultsUtils.isPrimary(node) || LSResultsUtils.isInputOutput(node)) {
				continue;
			}
			ResultNetlistNodeData data = node.getResultNetlistNodeData();
			Gate gate = GM.getRandomGateFromUnassignedGroup();
			if (gate == null) {
				throw new RuntimeException("Gate assignment error!");
			}
			data.setDevice(gate);
			GM.setAssignedGate(gate);
		}
	}

	@Override
	protected void preprocessing() throws CelloException {
		random = new Random(L_SEED);
		// GateManager
		setGateManager(new GateManager(getTargetDataInstance().getGates()));
		// truth table
		setTruthTable();
	}

	private void swap(NetlistNode nA, Gate gA, NetlistNode nB, Gate gB) {
		if (nA == null && nB != null) {
			getGateManager().setUnassignedGate(gB);
			nB.getResultNetlistNodeData().setDevice(gA);
			getGateManager().setAssignedGate(gA);
		}
		if (nA != null && nB == null) {
			getGateManager().setUnassignedGate(gA);
			nA.getResultNetlistNodeData().setDevice(gB);
			getGateManager().setAssignedGate(gB);
		}
		if (nA != null && nB != null) {
			nA.getResultNetlistNodeData().setDevice(gB);
			nB.getResultNetlistNodeData().setDevice(gA);
		}
	}

	private NetlistNode getRandomNode() {
		NetlistNode rtn = null;
		while (rtn == null) {
			int rand = random(0, getNetlist().getNumVertex() - 1);
			NetlistNode temp = getNetlist().getVertexAtIdx(rand);
			if (!LSResultsUtils.isAllOutput(temp) && !LSResultsUtils.isAllInput(temp)) {
				rtn = temp;
			}
		}
		return rtn;
	}

	/**
	 * Run the (core) algorithm
	 *
	 * @throws CelloException
	 */
	@Override
	protected void run() throws CelloException {
		logDebug("Running the (core) algorithm.");

		Double MAXTEMP = 100.0;
		Double MINTEMP = 0.001;

		Integer STEPS = 500;

		Double LOGMAX = Math.log10(MAXTEMP);
		Double LOGMIN = Math.log10(MINTEMP);

		Double LOGINC = (LOGMAX - LOGMIN) / STEPS;

		Integer T0_STEPS = 100;

		// input node assignment
		assignInputNodes();
		// output node assignment
		assignOutputNodes();
		// logic node assignment
		assignNodes();
		updateNetlist();

		setTMActivityEvaluation(new TMActivityEvaluation(getNetlist(), getLSLogicEvaluation()));
		setTMToxicityEvaluation(new TMToxicityEvaluation(getNetlist(), getTMActivityEvaluation()));

		// evaluate
		for (int j = 0; j < STEPS + T0_STEPS; ++j) {
			Double LOGTEMP = LOGMAX - j * LOGINC;
			Double TEMP = Math.pow(10, LOGTEMP);

			if (j >= STEPS) {
				TEMP = 0.0;
			}

			Double before = ScoreUtils.score(getNetlist(), getLSLogicEvaluation(),
			        getTMActivityEvaluation());

			NetlistNode nA = null;
			Gate gA = getGateManager().getRandomGateFromUnassignedGroup();
			if (gA == null) {
				nA = getRandomNode();
				gA = (Gate) nA.getResultNetlistNodeData().getDevice();
			}
			NetlistNode nB = null;
			Gate gB = null;
			do {
				nB = getRandomNode();
			} while (nB == nA);
			gB = (Gate) nB.getResultNetlistNodeData().getDevice();

			swap(nA, gA, nB, gB);

			// evaluate
			TMActivityEvaluation tmae = new TMActivityEvaluation(getNetlist(), getLSLogicEvaluation());
			Double after = ScoreUtils.score(getNetlist(), getLSLogicEvaluation(), tmae);

			// toxicity
			TMToxicityEvaluation tmte = new TMToxicityEvaluation(getNetlist(), getTMActivityEvaluation());
			if (getTMToxicityEvaluation().getMinimumGrowth() < D_GROWTH_THRESHOLD) {
				if (tmte.getMinimumGrowth() > getTMToxicityEvaluation().getMinimumGrowth()) {
					setTMToxicityEvaluation(tmte);
					setTMActivityEvaluation(tmae);
					continue;
				} else {
					// undo
					swap(nB, gB, nA, gA);
					continue;
				}
			} else if (tmte.getMinimumGrowth() < D_GROWTH_THRESHOLD) {
				// undo
				swap(nB, gB, nA, gA);
				continue;
			}

			// accept or reject
			Double probability = Math.exp((after - before) / TEMP); // e^b
			Double ep = Math.random();

			if (ep < probability) {
				// accept
				setTMToxicityEvaluation(tmte);
				setTMActivityEvaluation(tmae);
			} else {
				// undo
				swap(nB, gB, nA, gA);
			}

		}

	}

	/**
	 * Copy the gate assignements to the netlist
	 */
	protected void updateNetlist() {
		for (int i = 0; i < getNetlist().getNumVertex(); i++) {
			NetlistNode node = getNetlist().getVertexAtIdx(i);
			AssignableDevice device = node.getResultNetlistNodeData().getDevice();
			if (device != null) {
				node.getResultNetlistNodeData().setDeviceName(device.getName());
			}
			int num = node.getNumInEdge();
			if (node.getNumInEdge() > device.getStructure().getInputs().size())
				throw new RuntimeException("Device structure does not have enough inputs.");
			for (int j = 0; j < num; j++) {
				NetlistEdge e = node.getInEdgeAtIdx(j);
				Input input = device.getStructure().getInputs().get(j);
				e.getResultNetlistEdgeData().setInput(input);
			}
		}
	}

	/**
	 * Perform postprocessing
	 *
	 * @throws CelloException
	 */
	@Override
	protected void postprocessing() throws CelloException {
		updateNetlist();
		String inputFilename = getNetlist().getInputFilename();
		String filename = Utils.getFilename(inputFilename);
		String outputDir = getRuntimeEnv().getOptionValue(ArgString.OUTPUTDIR);
		String outputFile = outputDir + Utils.getFileSeparator() + filename;
		// logic
		LSResultsUtils.writeCSVForLSLogicEvaluation(getLSLogicEvaluation(), outputFile + "_logic.csv");
		// cytometry
		setTMCytometryEvaluation(new TMCytometryEvaluation());
		// toxicity
		setTMToxicityEvaluation(new TMToxicityEvaluation(getNetlist(), getTMActivityEvaluation()));
		SimulatedAnnealingResultsUtils.writeCSVForTMToxicityEvaluation(getTMToxicityEvaluation(),
		        outputFile + "_toxicity.csv");
		logInfo(getTMToxicityEvaluation().toString());
		// activity
		TMResultsUtils.writeCSVForTMActivityEvaluation(getTMActivityEvaluation(), outputFile + "_activity.csv");
		logInfo(getTMActivityEvaluation().toString());
		for (int i = 0; i < getNetlist().getNumVertex(); i++) {
			NetlistNode node = getNetlist().getVertexAtIdx(i);
			AssignableDevice gate = node.getResultNetlistNodeData().getDevice();
			if (gate != null) {
				String str = "";
				str += String.format("Node: %-5s", node.getName());
				str += Utils.getTabCharacter();
				str += String.format("Type: %-10s", node.getResultNetlistNodeData().getNodeType());
				str += Utils.getTabCharacter();
				str += String.format("Gate: %-10s", gate.getName());
				str += Utils.getTabCharacter();
				logInfo(str);
			}
		}
		logInfo(String.format("Score: %.2f",
		        ScoreUtils.score(getNetlist(), getLSLogicEvaluation(), getTMActivityEvaluation())));
		// plots
		logInfo("Generating plots");
		ResponsePlotUtils.generatePlots(getNetlist(), getLSLogicEvaluation(), getTMActivityEvaluation(),
		        getRuntimeEnv());
	}

	/**
	 * Returns the Logger for the <i>SimulatedAnnealing</i> algorithm
	 *
	 * @return the logger for the <i>SimulatedAnnealing</i> algorithm
	 */
	@Override
	protected Logger getLogger() {
		return SimulatedAnnealing.logger;
	}

	private static final Logger logger = LogManager.getLogger(SimulatedAnnealing.class);

	/**
	 * Getter for <i>targetDataInstance</i>.
	 *
	 * @return value of targetDataInstance
	 */
	protected TMTargetDataInstance getTargetDataInstance() {
		return targetDataInstance;
	}

	/**
	 * Setter for <i>targetDataInstance</i>.
	 *
	 * @param targetDataInstance the targetDataInstance to set
	 */
	protected void setTargetDataInstance(TMTargetDataInstance targetDataInstance) {
		this.targetDataInstance = targetDataInstance;
	}

	private TMTargetDataInstance targetDataInstance;

	/**
	 * Getter for <i>inputMap</i>
	 *
	 * @return value of <i>inputMap</i>
	 */
	protected Map<String, String> getInputMap() {
		return inputMap;
	}

	/**
	 * Setter for <i>inputMap</i>
	 *
	 * @param inputMap the value to set <i>inputMap</i>
	 */
	protected void setInputMap(final Map<String, String> inputMap) {
		this.inputMap = inputMap;
	}

	private Map<String, String> inputMap;

	/**
	 * Getter for <i>outputMap</i>
	 *
	 * @return value of <i>outputMap</i>
	 */
	protected Map<String, String> getOutputMap() {
		return outputMap;
	}

	/**
	 * Setter for <i>outputMap</i>
	 *
	 * @param outputMap the value to set <i>outputMap</i>
	 */
	protected void setOutputMap(final Map<String, String> outputMap) {
		this.outputMap = outputMap;
	}

	private Map<String, String> outputMap;

	/**
	 * @param unitConversion the unitConversion to set
	 */
	protected void setUnitConversion(final Double unitConversion) {
		this.unitConversion = unitConversion;
	}

	/**
	 * @return the unitConversion
	 */
	public Double getUnitConversion() {
		return unitConversion;
	}

	private Double unitConversion;

	/*
	 * GateManager
	 */
	protected void setGateManager(final GateManager gateManager) {
		this.gateManager = gateManager;
	}

	public GateManager getGateManager() {
		return gateManager;
	}

	private GateManager gateManager;

	/*
	 * LSLogicEvaluation
	 */
	protected void setLSLogicEvaluation(final LSLogicEvaluation lsle) {
		this.lsle = lsle;
	}

	public LSLogicEvaluation getLSLogicEvaluation() {
		return lsle;
	}

	private LSLogicEvaluation lsle;

	/*
	 * TMActivityEvaluation
	 */
	/**
	 * Getter for <i>tmae</i>
	 *
	 * @return value of <i>tmae</i>
	 */
	public TMActivityEvaluation getTMActivityEvaluation() {
		return tmae;
	}

	/**
	 * Setter for <i>tmte</i>
	 *
	 * @param tmte the value to set <i>tmte</i>
	 */
	protected void setTMToxicityEvaluation(final TMToxicityEvaluation tmte) {
		this.tmte = tmte;
	}

	private TMToxicityEvaluation tmte;

	/*
	 * TMToxicityEvaluation
	 */
	/**
	 * Setter for <i>tmae</i>
	 *
	 * @param tmae the value to set <i>tmae</i>
	 */
	protected void setTMActivityEvaluation(final TMActivityEvaluation tmae) {
		this.tmae = tmae;
	}

	/**
	 * Getter for <i>tmte</i>
	 *
	 * @return value of <i>tmte</i>
	 */
	public TMToxicityEvaluation getTMToxicityEvaluation() {
		return tmte;
	}

	private TMActivityEvaluation tmae;

	/**
	 * Getter for <i>tmce</i>
	 *
	 * @return value of <i>tmce</i>
	 */
	public TMCytometryEvaluation getTMCytometryEvaluation() {
		return tmce;
	}

	/**
	 * Setter for <i>tmce</i>
	 *
	 * @param tmce the value to set <i>tmce</i>
	 */
	protected void setTMCytometryEvaluation(final TMCytometryEvaluation tmce) {
		this.tmce = tmce;
	}

	private TMCytometryEvaluation tmce;

	/*
	 * Random
	 */
	private int random(int min, int max) {
		int rtn = 0;
		Random random = getRandom();
		rtn = random.nextInt(max - min + 1) + min;
		return rtn;
	}

	private Random getRandom() {
		return random;
	}

	private Random random;
	private static long L_SEED = 21;
	private static final double D_GROWTH_THRESHOLD = 0.75;

}
