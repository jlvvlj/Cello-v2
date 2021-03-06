/**
 * Copyright (C) 2018 Boston University (BU)
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
package org.cellocad.v2.results.technologyMapping.activity;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.cellocad.v2.common.CelloException;
import org.cellocad.v2.common.Utils;
import org.cellocad.v2.common.graph.algorithm.BFS;
import org.cellocad.v2.common.target.data.data.EvaluationContext;
import org.cellocad.v2.common.target.data.data.FunctionType;
import org.cellocad.v2.results.logicSynthesis.logic.LSLogicEvaluation;
import org.cellocad.v2.results.logicSynthesis.logic.truthtable.State;
import org.cellocad.v2.results.logicSynthesis.logic.truthtable.States;
import org.cellocad.v2.results.netlist.Netlist;
import org.cellocad.v2.results.netlist.NetlistEdge;
import org.cellocad.v2.results.netlist.NetlistNode;
import org.cellocad.v2.results.technologyMapping.activity.activitytable.Activity;
import org.cellocad.v2.results.technologyMapping.activity.activitytable.ActivityTable;

/**
 *
 *
 * @author Timothy Jones
 *
 * @date 2018-05-24
 *
 */
public class TMActivityEvaluation {

	/**
	 * Initialize class members
	 */
	private void init() {
		activitytables = new HashMap<NetlistNode, ActivityTable<NetlistNode, NetlistNode>>();
	}

	/**
	 * Initializes a newly created LSLogicEvaluation using the Netlist defined by
	 * parameter <i>netlist</i>
	 *
	 * @param netlist the Netlist
	 * @throws CelloException
	 */
	public TMActivityEvaluation(Netlist netlist, LSLogicEvaluation lsle) throws CelloException {
		init();
		if (!netlist.isValid()) {
			throw new RuntimeException("netlist is not valid!");
		}
		setStates(lsle.getStates());
		List<NetlistNode> outputNodes = new ArrayList<NetlistNode>();
		for (int i = 0; i < netlist.getNumVertex(); i++) {
			NetlistNode node = netlist.getVertexAtIdx(i);
			outputNodes.clear();
			outputNodes.add(node);
			ActivityTable<NetlistNode, NetlistNode> activityTable = new ActivityTable<NetlistNode, NetlistNode>(states,
			        outputNodes);
			getActivityTables().put(node, activityTable);
		}
		evaluate(netlist);
	}

	/**
	 * Returns a List of Double representation of the input values for NetlistNode
	 * defined by parameter <i>node</i> at the state defined by parameter
	 * <i>state</i>
	 *
	 * @param node     the NetlistNode
	 * @param activity the activity
	 * @return a List of Double representation of the input values for NetlistNode
	 *         defined by parameter <i>node</i> at the activity defined by parameter
	 *         <i>activity</i>
	 */
	public List<Double> getInputActivity(final NetlistNode node, final State<NetlistNode> activity) {
		List<Double> rtn = new ArrayList<Double>();
		for (int i = 0; i < node.getNumInEdge(); i++) {
			NetlistNode inputNode = node.getInEdgeAtIdx(i).getSrc();
			ActivityTable<NetlistNode, NetlistNode> activityTable = getActivityTables().get(inputNode);
			activityTable.getActivityOutput(activity);
			Activity<NetlistNode> outputActivity = activityTable.getActivityOutput(activity);
			if (outputActivity.getNumActivityPosition() != 1) {
				throw new RuntimeException("Invalid number of output(s)!");
			}
			rtn.add(outputActivity.getActivity(inputNode));
		}
		return rtn;
	}

	private void evaluateActivityTable(final NetlistNode node, final EvaluationContext ec) throws CelloException {
		ec.setNode(node);
		ActivityTable<NetlistNode, NetlistNode> activityTable = getActivityTables().get(node);
		for (int i = 0; i < activityTable.getNumStates(); i++) {
			State<NetlistNode> inputState = activityTable.getStateAtIdx(i);
			Activity<NetlistNode> outputActivity = activityTable.getActivityOutput(inputState);
			ec.setState(inputState);
			Double result = node.getResultNetlistNodeData().getDevice().getModel()
			        .getFunctionByName(FunctionType.S_RESPONSEFUNCTION).evaluate(ec).doubleValue();
			if (outputActivity.getNumActivityPosition() != 1) {
				throw new RuntimeException("Invalid number of output(s)!");
			}
			Utils.isNullRuntimeException(result, "result");
			if (!outputActivity.setActivity(node, result)) {
				throw new RuntimeException("Node does not exist");
			}
		}
	}

	/**
	 * Evaluates the Netlist defined by parameter <i>netlist</i>
	 *
	 * @param netlist the Netlist
	 * @throws CelloException
	 */
	protected void evaluate(Netlist netlist) throws CelloException {
		BFS<NetlistNode, NetlistEdge, Netlist> BFS = new BFS<NetlistNode, NetlistEdge, Netlist>(netlist);
		NetlistNode node = null;
		EvaluationContext ec = new EvaluationContext();
		node = BFS.getNextVertex();
		while (node != null) {
			evaluateActivityTable(node, ec);
			node = BFS.getNextVertex();
		}
	}

	protected Map<NetlistNode, ActivityTable<NetlistNode, NetlistNode>> getActivityTables() {
		return activitytables;
	}

	/**
	 * Setter for <i>states</i>.
	 *
	 * @param states the states
	 */
	protected void setStates(States<NetlistNode> states) {
		this.states = states;
	}

	/**
	 * Getter for <i>states</i>.
	 *
	 * @return the states of this instance
	 */
	public States<NetlistNode> getStates() {
		return states;
	}

	/**
	 * Returns the truthTable of NetlistNode defined by parameter <i>node</i>
	 *
	 * @param node the NetlistNode
	 * @return the truthTable of NetlistNode defined by parameter <i>node</i>
	 */
	public ActivityTable<NetlistNode, NetlistNode> getActivityTable(final NetlistNode node) {
		ActivityTable<NetlistNode, NetlistNode> rtn = null;
		rtn = getActivityTables().get(node);
		return rtn;
	}

	@Override
	public String toString() {
		String rtn = "";
		rtn += Utils.getNewLine();
		rtn += S_HEADER + Utils.getNewLine();
		rtn += "TMActivityEvaluation" + Utils.getNewLine();
		rtn += S_HEADER + Utils.getNewLine();
		for (NetlistNode node : getActivityTables().keySet()) {
			rtn += String.format("%-15s", node.getName()) + Utils.getTabCharacter();
			ActivityTable<NetlistNode, NetlistNode> activityTable = getActivityTables().get(node);
			for (int i = 0; i < activityTable.getNumStates(); i++) {
				State<NetlistNode> input = activityTable.getStateAtIdx(i);
				Activity<NetlistNode> output = activityTable.getActivityOutput(input);
				rtn += String.format("%.4f", output.getActivity(node)) + Utils.getTabCharacter();
			}
			rtn += Utils.getNewLine();
		}
		rtn += S_HEADER + Utils.getNewLine();
		return rtn;
	}

	/**
	 * Writes this instance in CSV format to the writer defined by parameter
	 * <i>os</i> with the delimiter equivalent to the parameter <i>delimiter</i>
	 *
	 * @param delimiter the delimiter
	 * @param os        the writer
	 * @throws IOException If an I/O error occurs
	 */
	public void writeCSV(String delimiter, Writer os) throws IOException {
		String str = "";
		for (NetlistNode node : getActivityTables().keySet()) {
			str += node.getName();
			ActivityTable<NetlistNode, NetlistNode> activityTable = getActivityTable(node);
			for (int i = 0; i < activityTable.getNumStates(); i++) {
				State<NetlistNode> input = activityTable.getStateAtIdx(i);
				Activity<NetlistNode> output = activityTable.getActivityOutput(input);
				str += delimiter;
				str += String.format("%1.5e", output.getActivity(node));
			}
			str += Utils.getNewLine();
		}
		os.write(str);
	}

	private static final String S_HEADER = "--------------------------------------------";

	private Map<NetlistNode, ActivityTable<NetlistNode, NetlistNode>> activitytables;
	private States<NetlistNode> states;

}
