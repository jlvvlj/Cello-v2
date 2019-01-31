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
package org.cellocad.cello2.technologyMapping.algorithm.SimulatedAnnealing.data.cytometry;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.cellocad.cello2.common.CObject;
import org.cellocad.cello2.common.Pair;
import org.cellocad.cello2.common.graph.algorithm.SinkDFS;
import org.cellocad.cello2.results.logicSynthesis.LSResultsUtils;
import org.cellocad.cello2.results.netlist.Netlist;
import org.cellocad.cello2.results.netlist.NetlistEdge;
import org.cellocad.cello2.results.netlist.NetlistNode;
import org.cellocad.cello2.results.technologyMapping.activity.TMActivityEvaluation;
import org.cellocad.cello2.results.technologyMapping.activity.activitytable.Activities;
import org.cellocad.cello2.results.technologyMapping.activity.activitytable.Activity;
import org.cellocad.cello2.results.technologyMapping.activity.activitytable.ActivityTable;
import org.cellocad.cello2.technologyMapping.algorithm.SimulatedAnnealing.data.SimulatedAnnealingNetlistNodeData;
import org.cellocad.cello2.technologyMapping.algorithm.SimulatedAnnealing.data.cytometry.cytometrytable.Cytometry;
import org.cellocad.cello2.technologyMapping.algorithm.SimulatedAnnealing.data.cytometry.cytometrytable.CytometryTable;
import org.cellocad.cello2.technologyMapping.algorithm.SimulatedAnnealing.data.cytometry.cytometrytable.Histogram;
import org.cellocad.cello2.technologyMapping.algorithm.SimulatedAnnealing.data.cytometry.cytometrytable.HistogramInterpolator;
import org.cellocad.cello2.technologyMapping.algorithm.SimulatedAnnealing.data.ucf.CytometryData;
import org.cellocad.cello2.technologyMapping.algorithm.SimulatedAnnealing.data.ucf.Gate;
import org.cellocad.cello2.technologyMapping.algorithm.SimulatedAnnealing.data.ucf.ResponseFunction;
import org.cellocad.cello2.technologyMapping.algorithm.SimulatedAnnealing.data.ucf.ResponseFunctionVariable;

/**
 * The TMCytometryEvaluation class evaluates the cytometry of a netlist used within the <i>SimulatedAnnealing</i> algorithm class of the <i>technologyMapping</i> stage.
 *
 * @author Timothy Jones
 *
 * @date 2019-01-29
 *
 */
public class TMCytometryEvaluation extends CObject{

	/**
	 * Initialize class members
	 */
	private void init() {
		this.cytometrytables = new HashMap<NetlistNode, CytometryTable<NetlistNode, NetlistNode>>();
	}

	/**
	 * Initializes a newly created TMCytometryEvaluation using the Netlist defined by parameter <i>netlist</i>
	 *
	 * @param netlist the Netlist
	 */
	public TMCytometryEvaluation(final Netlist netlist, final TMActivityEvaluation tmae) {
		this.init();
		if (!netlist.isValid()) {
			throw new RuntimeException("netlist is not valid!");
		}
		this.setTMActivityEvaluation(tmae);
		Activities<NetlistNode> activities = tmae.getActivities();
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
			CytometryTable<NetlistNode, NetlistNode> cytometryTable = new CytometryTable<NetlistNode, NetlistNode>(activities, outputNodes);
			this.getCytometryTables().put(node, cytometryTable);
		}
		this.evaluate(netlist);
	}

	/**
	 * Get the Histogram representation of the CytometryData defined by <i>cd</i>
	 *
	 * @param cd the CytometryData
	 * @return the Histogram representation of the CytometryData defined by <i>cd</i>
	 */
	private Histogram getHistogram(CytometryData cd) {
		Histogram rtn = null;
		List<Double> bins = new ArrayList<>();
		List<Double> counts = new ArrayList<>();
		for (int i = 0; i < cd.getNumOutputBins(); i++) {
			bins.add(cd.getOutputBinsAtIdx(i));
			counts.add(cd.getOutputCountsAtIdx(i));
		}
		rtn = new Histogram(bins,counts);
		return rtn;
	}

	/**
	 * Returns a Pair of CytometryData with closest input values above and below of <i>input</i>
	 *
	 * @param node the NetlistNode
	 * @param input the input
	 * @return a Pair of CytometryData with closest input values above and below of <i>input</i>
	 */
	private Pair<CytometryData,CytometryData> getAdjacentCytometryData(final NetlistNode node, Double input) {
		Pair<CytometryData,CytometryData> rtn = null;
		// histogram
		SimulatedAnnealingNetlistNodeData data = (SimulatedAnnealingNetlistNodeData) node.getNetlistNodeData();
		Gate gate = (Gate) data.getGate();
		ResponseFunction rf = gate.getResponseFunction();
		ResponseFunctionVariable var = rf.getVariableAtIdx(0);
		CytometryData a = var.getCytometryDataAtIdx(0);
		CytometryData b = var.getCytometryDataAtIdx(var.getNumCytometryData() - 1);
		for (int i = 0; i < var.getNumCytometryData(); i++) {
			CytometryData cd = var.getCytometryDataAtIdx(i);
			cd.getInput();
			if ((cd.getInput() < input)
				&&
				(Math.abs(cd.getInput() - input) < Math.abs(a.getInput() - input))) {
				a = cd;
			}
			else if ((cd.getInput() > input)
					 &&
					 (Math.abs(cd.getInput() - input) < Math.abs(b.getInput() - input))) {
				b = cd;
			}
		}
		rtn = new Pair<>(a,b);
		return rtn;
	}

	/**
	 * Evaluates the cytometry table for the NetlistNode defined by parameter <i>node</i>
	 *
	 * @param node the NetlistNode
	 */
	private void evaluateCytometryTable(final NetlistNode node) {
		Histogram result = null;
		CytometryTable<NetlistNode, NetlistNode> cytometryTable = this.getCytometryTables().get(node);
		for (int i = 0; i < cytometryTable.getNumActivities(); i++) {
			Activity<NetlistNode> inputActivity = cytometryTable.getActivityAtIdx(i);
			Cytometry<NetlistNode> outputCytometry = cytometryTable.getCytometryOutput(inputActivity);
			double input = 0.0;
			for (int j = 0; j < node.getNumInEdge(); j++) {
				NetlistEdge edge = node.getInEdgeAtIdx(j);
				NetlistNode src = edge.getSrc();
				ActivityTable<NetlistNode,NetlistNode> table = this.getTMActivityEvaluation().getActivityTable(src);
				Double value = table.getActivityOutput(inputActivity).getActivity(src);
				input += value;
			}
			Histogram histogram = new Histogram();
			Pair<CytometryData,CytometryData> pair = this.getAdjacentCytometryData(node,input);
			Histogram a = this.getHistogram(pair.getFirst());
			Histogram b = this.getHistogram(pair.getSecond());
			if (pair.getFirst().equals(pair.getSecond())) {
				histogram = a;
			}
			else {
				HistogramInterpolator interp = new HistogramInterpolator(a,b);
				Double r = (input - pair.getFirst().getInput()) / (pair.getSecond().getInput() - pair.getFirst().getInput());
				histogram = interp.interpolate(r);
			}
			outputCytometry.setCytometry(node, histogram);
		}
	}

	/**
	 * Evaluates the Netlist defined by parameter <i>netlist</i>
	 *
	 * @param netlist the Netlist
	 */
	protected void evaluate(final Netlist netlist){
		SinkDFS<NetlistNode, NetlistEdge, Netlist> DFS = new SinkDFS<NetlistNode, NetlistEdge, Netlist>(netlist);
		NetlistNode node = null;
		while ((node = DFS.getNextVertex()) != null) {
			if (LSResultsUtils.isPrimaryInput(node)
			    ||
			    LSResultsUtils.isPrimaryOutput(node)) {
				continue;
			}
			evaluateCytometryTable(node);
		}
	}

	protected Map<NetlistNode, CytometryTable<NetlistNode, NetlistNode>> getCytometryTables(){
		return this.cytometrytables;
	}

	/**
	 * Returns the cytometryTable of NetlistNode defined by parameter <i>node</i>
	 *
	 * @param node the NetlistNode
	 * @return the truthTable of NetlistNode defined by parameter <i>node</i>
	 */
	public CytometryTable<NetlistNode, NetlistNode> getCytometryTable(final NetlistNode node){
		CytometryTable<NetlistNode, NetlistNode> rtn = null;
		rtn = this.getCytometryTables().get(node);
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

	private Map<NetlistNode, CytometryTable<NetlistNode, NetlistNode>> cytometrytables;
	private TMActivityEvaluation tmae;

}
