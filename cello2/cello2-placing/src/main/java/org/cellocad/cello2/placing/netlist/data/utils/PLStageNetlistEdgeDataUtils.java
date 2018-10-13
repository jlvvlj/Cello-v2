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
package org.cellocad.cello2.placing.netlist.data.utils;

import org.cellocad.cello2.placing.netlist.data.PLStageNetlistEdgeData;
import org.cellocad.cello2.results.netlist.Netlist;
import org.cellocad.cello2.results.netlist.NetlistEdge;


/**
 * The PLStageNetlistEdgeDataUtils class is class with utility methods for PLStageNetlistEdgeData instances in the <i>placing</i> stage.
 * 
 * @author Vincent Mirian
 * 
 * @date 2018-05-21
 *
 */
public class PLStageNetlistEdgeDataUtils {

	/**
	 * Resets the stage data for all edges in the netlist instance defined by parameter <i>netlist</i>
	 *
	 * @param netlist the Netlist
	 *
	 */
	static public void resetStageNetlistEdgeData(Netlist netlist){
		for (int i = 0; i < netlist.getNumEdge(); i++) {
			NetlistEdge edge = netlist.getEdgeAtIdx(i);
			PLStageNetlistEdgeDataUtils.resetStageNetlistEdgeData(edge);
		}
	}

	/**
	 * Resets the stage data for a NetlistEdge instance
	 *
	 * @param edge the NetlistEdge
	 *
	 */
	static public void resetStageNetlistEdgeData(NetlistEdge edge){
		PLStageNetlistEdgeData data = new PLStageNetlistEdgeData();
		edge.setStageNetlistEdgeData(data);
	}
	
}