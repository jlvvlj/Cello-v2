/**
 * Copyright (C) 2017 Massachusetts Institute of Technology (MIT)
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
package org.cellocad.v2.partitioning.algorithm;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cellocad.v2.common.algorithm.Algorithm;
import org.cellocad.v2.partitioning.netlist.data.PTStageNetlistData;
import org.cellocad.v2.partitioning.netlist.data.PTStageNetlistEdgeData;
import org.cellocad.v2.partitioning.netlist.data.PTStageNetlistNodeData;
import org.cellocad.v2.results.netlist.Netlist;
import org.cellocad.v2.results.netlist.NetlistEdge;
import org.cellocad.v2.results.netlist.NetlistNode;

/**
 * The PTAlgorithm class is the base class for all algorithms in the <i>partitioning</i> stage.
 * 
 * @author Vincent Mirian
 * 
 * @date 2018-05-21
 *
 */
public abstract class PTAlgorithm extends Algorithm{

	/**
	 *  Returns the <i>PTStageNetlistNodeData</i> of the <i>node</i>
	 *
	 *  @param node a node within the <i>netlist</i> of this instance
	 *  @return the <i>PTStageNetlistNodeData</i> instance if it exists, null otherwise
	 */
	protected PTStageNetlistNodeData getStageNetlistNodeData(NetlistNode node){
		PTStageNetlistNodeData rtn = null;
		rtn = (PTStageNetlistNodeData) node.getStageNetlistNodeData();
		return rtn;
	}

	/**
	 *  Returns the <i>PTStageNetlistEdgeData</i> of the <i>edge</i>
	 *
	 *  @param edge an edge within the <i>netlist</i> of this instance
	 *  @return the <i>PTStageNetlistEdgeData</i> instance if it exists, null otherwise
	 */
	protected PTStageNetlistEdgeData getStageNetlistEdgeData(NetlistEdge edge){
		PTStageNetlistEdgeData rtn = null;
		rtn = (PTStageNetlistEdgeData) edge.getStageNetlistEdgeData();
		return rtn;
	}

	/**
	 *  Returns the <i>PTStageNetlistData</i> of the <i>netlist</i>
	 *
	 *  @param netlist the netlist of this instance
	 *  @return the <i>PTStageNetlistData</i> instance if it exists, null otherwise
	 */
	protected PTStageNetlistData getStageNetlistData(Netlist netlist){
		PTStageNetlistData rtn = null;
		rtn = (PTStageNetlistData) netlist.getStageNetlistData();
		return rtn;
	}

	/**
	 *  Returns the Logger for the <i>PTAlgorithm</i> algorithm
	 *
	 *  @return the logger for the <i>PTAlgorithm</i> algorithm
	 */
	@Override
	protected Logger getLogger() {
		return PTAlgorithm.logger;
	}
	
	private static final Logger logger = LogManager.getLogger(PTAlgorithm.class);
}
