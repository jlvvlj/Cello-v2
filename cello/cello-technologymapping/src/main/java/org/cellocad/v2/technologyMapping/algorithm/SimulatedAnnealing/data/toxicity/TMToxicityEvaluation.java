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
package org.cellocad.v2.technologyMapping.algorithm.SimulatedAnnealing.data.toxicity;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.cellocad.v2.common.CObject;
import org.cellocad.v2.common.CelloException;
import org.cellocad.v2.common.Utils;
import org.cellocad.v2.common.graph.algorithm.SinkDFS;
import org.cellocad.v2.common.target.data.data.EvaluationContext;
import org.cellocad.v2.results.logicSynthesis.LSResultsUtils;
import org.cellocad.v2.results.logicSynthesis.logic.truthtable.State;
import org.cellocad.v2.results.logicSynthesis.logic.truthtable.States;
import org.cellocad.v2.results.netlist.Netlist;
import org.cellocad.v2.results.netlist.NetlistEdge;
import org.cellocad.v2.results.netlist.NetlistNode;
import org.cellocad.v2.results.technologyMapping.activity.TMActivityEvaluation;
import org.cellocad.v2.technologyMapping.algorithm.SimulatedAnnealing.data.toxicity.toxicitytable.Toxicity;
import org.cellocad.v2.technologyMapping.algorithm.SimulatedAnnealing.data.toxicity.toxicitytable.ToxicityTable;

/**
 * The TMToxicityEvaluation class evaluates the toxicity of a netlist used within the <i>SimulatedAnnealing</i> algorithm class of the <i>technologyMapping</i> stage.
 *
 * @author Timothy Jones
 *
 * @date 2019-01-29
 *
 */
public class TMToxicityEvaluation extends CObject{

	/**
	 * Initialize class members
	 */
	private void init() {
		this.toxicitytables = new HashMap<NetlistNode, ToxicityTable<NetlistNode, NetlistNode>>();
	}

	/**
	 * Initializes a newly created TMToxicityEvaluation using the Netlist defined by
	 * parameter <i>netlist</i>
	 *
	 * @param netlist the Netlist
	 * @throws CelloException
	 */
	public TMToxicityEvaluation(final Netlist netlist, final TMActivityEvaluation tmae) throws CelloException {
		this.init();
		if (!netlist.isValid()) {
			throw new RuntimeException("netlist is not valid!");
		}
		this.setTMActivityEvaluation(tmae);
		States<NetlistNode> states = tmae.getStates();
		List<NetlistNode> outputNodes = new ArrayList<NetlistNode>();
		for(int i = 0; i < netlist.getNumVertex(); i++) {
			NetlistNode node = netlist.getVertexAtIdx(i);
			if (LSResultsUtils.isPrimaryInput(node)
			    ||
			    LSResultsUtils.isPrimaryOutput(node)) {
				continue;
			}
			outputNodes.clear();
			outputNodes.add(node);
			ToxicityTable<NetlistNode, NetlistNode> toxicityTable = new ToxicityTable<NetlistNode, NetlistNode>(states, outputNodes);
			this.getToxicityTables().put(node, toxicityTable);
		}
		this.evaluate(netlist);
	}

	/**
	 * Evaluates the toxicity table for the NetlistNode defined by parameter
	 * <i>node</i>
	 *
	 * @param node the NetlistNode
	 * @throws CelloException
	 */
	private void evaluateToxicityTable(final NetlistNode node, EvaluationContext ec) throws CelloException {
		ec.setNode(node);
		ToxicityTable<NetlistNode,NetlistNode> toxicityTable = this.getToxicityTables().get(node);
		for (int i = 0; i < toxicityTable.getNumStates(); i++) {
			State<NetlistNode> inputState = toxicityTable.getStateAtIdx(i);
			Toxicity<NetlistNode> outputToxicity = toxicityTable.getToxicityOutput(inputState);
			ec.setState(inputState);
			Double result = node.getResultNetlistNodeData().getDevice().getModel()
					.getFunctionByName("toxicity").evaluate(ec).doubleValue();
			if (result > D_MAXGROWTH)
				result = D_MAXGROWTH;
			if (result < D_MINGROWTH)
				result = D_MINGROWTH;
			outputToxicity.setToxicity(node,result);
		}
	}

	/**
	 * Evaluates the Netlist defined by parameter <i>netlist</i>
	 *
	 * @param netlist the Netlist
	 * @throws CelloException
	 */
	protected void evaluate(final Netlist netlist) throws CelloException {
		SinkDFS<NetlistNode, NetlistEdge, Netlist> DFS = new SinkDFS<NetlistNode, NetlistEdge, Netlist>(netlist);
		NetlistNode node = null;
		EvaluationContext ec = new EvaluationContext();
		while ((node = DFS.getNextVertex()) != null) {
			if (LSResultsUtils.isPrimaryInput(node)
			    ||
			    LSResultsUtils.isPrimaryOutput(node)) {
				continue;
			}
			evaluateToxicityTable(node, ec);
		}
	}

	public Double getGrowth(final State<NetlistNode> state) {
		Double rtn = D_MAXGROWTH;
		for(NetlistNode node : this.getToxicityTables().keySet()) {
			ToxicityTable<NetlistNode,NetlistNode> table = this.getToxicityTables().get(node);
			Toxicity<NetlistNode> toxicity = table.getToxicityOutput(state);
			Double value = toxicity.getToxicity(node);
			rtn *= value;
		}
		if (rtn < D_MINGROWTH)
			rtn = D_MINGROWTH;
		return rtn;
	}

	// public Double getMinimumGrowth() {
	// 	Double rtn = D_MAXGROWTH;
	// 	for (NetlistNode node : this.getToxicityTables().keySet()) {
	// 		ToxicityTable<NetlistNode,NetlistNode> toxicitytable = this.getToxicityTable(node);
	// 		for (int i = 0; i < toxicitytable.getNumActivities(); i++) {
	// 			Activity<NetlistNode> input = toxicitytable.getActivityAtIdx(i);
	// 			GateToxicity<NetlistNode> output = toxicitytable.getToxicityOutput(input);
	// 			rtn = Math.min(rtn,output.getToxicity(node));
	// 		}
	// 	}
	// 	return rtn;
	// }

	public Double getMinimumGrowth() {
		Double rtn = D_MAXGROWTH;
		States<NetlistNode> states = this.getTMActivityEvaluation().getStates();
		for (int i = 0; i < states.getNumStates(); i++) {
			State<NetlistNode> state = states.getStateAtIdx(i);
			rtn = Math.min(rtn, this.getGrowth(state));
		}
		return rtn;
	}

	protected Map<NetlistNode, ToxicityTable<NetlistNode, NetlistNode>> getToxicityTables(){
		return this.toxicitytables;
	}

	/**
	 * Returns the toxicityTable of NetlistNode defined by parameter <i>node</i>
	 *
	 * @param node the NetlistNode
	 * @return the truthTable of NetlistNode defined by parameter <i>node</i>
	 */
	public ToxicityTable<NetlistNode, NetlistNode> getToxicityTable(final NetlistNode node){
		ToxicityTable<NetlistNode, NetlistNode> rtn = null;
		rtn = this.getToxicityTables().get(node);
		return rtn;
	}

	/**
	 * Getter for <i>tmae</i>
	 * @return value of <i>tmae</i>
	 */
	public TMActivityEvaluation getTMActivityEvaluation() {
		return tmae;
	}

	/**
	 * Setter for <i>tmae</i>
	 * @param tmae the value to set <i>tmae</i>
	 */
	protected void setTMActivityEvaluation(final TMActivityEvaluation tmae) {
		this.tmae = tmae;
	}

	public String toString() {
		String rtn = "";
		rtn += Utils.getNewLine();
		rtn += S_HEADER + Utils.getNewLine();
		rtn += "TMToxicityEvaluation" + Utils.getNewLine();
		rtn += S_HEADER + Utils.getNewLine();
		for (NetlistNode node : this.getToxicityTables().keySet()) {
			rtn += String.format("%-15s",node.getName()) + Utils.getTabCharacter();
			ToxicityTable<NetlistNode,NetlistNode> toxicityTable = this.getToxicityTable(node);
			for (int i = 0; i < toxicityTable.getNumStates(); i++) {
				State<NetlistNode> input = toxicityTable.getStateAtIdx(i);
				Toxicity<NetlistNode> output = toxicityTable.getToxicityOutput(input);
				rtn += String.format("%.2f",output.getToxicity(node)) + Utils.getTabCharacter();
			}
			rtn += Utils.getNewLine();
		}
		rtn += S_HEADER + Utils.getNewLine();
		rtn += String.format("%-15s","") + Utils.getTabCharacter();
		States<NetlistNode> states = this.getTMActivityEvaluation().getStates();
		for (int i = 0; i < states.getNumStates(); i++) {
			State<NetlistNode> state = states.getStateAtIdx(i);
			rtn += String.format("%.2f", this.getGrowth(state)) + Utils.getTabCharacter();
		}
		rtn += Utils.getNewLine();
		rtn += S_HEADER + Utils.getNewLine();
		return rtn;
	}

	/**
	 *  Writes this instance in CSV format to the writer defined by parameter <i>os</i> with the delimiter equivalent to the parameter <i>delimiter</i>
	 *  @param delimiter the delimiter
	 *  @param os the writer
	 *  @throws IOException If an I/O error occurs
	 */
	public void writeCSV(String delimiter, Writer os) throws IOException {
		String str = "";
		for (NetlistNode node : this.getToxicityTables().keySet()) {
			str += node.getName();
			ToxicityTable<NetlistNode,NetlistNode> toxicityTable = this.getToxicityTable(node);
			for (int i = 0; i < toxicityTable.getNumStates(); i++) {
				State<NetlistNode> input = toxicityTable.getStateAtIdx(i);
				Toxicity<NetlistNode> output = toxicityTable.getToxicityOutput(input);
				str += delimiter;
				str += String.format("%.2f",output.getToxicity(node));
			}
			str += Utils.getNewLine();
		}
		os.write(str);
	}

	private static final String S_HEADER = "--------------------------------------------";
	private static final double D_MAXGROWTH = 1.00;
	private static final double D_MINGROWTH = 0.01;

	private Map<NetlistNode,ToxicityTable<NetlistNode,NetlistNode>> toxicitytables;
	private TMActivityEvaluation tmae;

}
