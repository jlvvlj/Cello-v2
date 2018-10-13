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
package org.cellocad.cello2.partitioning.algorithm.data;

import org.cellocad.cello2.common.algorithm.data.NetlistEdgeDataFactory;
import org.cellocad.cello2.partitioning.algorithm.GPCC_BASE.data.GPCC_BASENetlistEdgeData;
import org.cellocad.cello2.partitioning.algorithm.GPCC_SCIP_BASE.data.GPCC_SCIP_BASENetlistEdgeData;
import org.cellocad.cello2.partitioning.algorithm.GPCC_SUGARM_BASE.data.GPCC_SUGARM_BASENetlistEdgeData;
import org.cellocad.cello2.partitioning.algorithm.HMetis.data.HMetisNetlistEdgeData;

/**
 * The PTNetlistEdgeDataFactory is a NetlistEdgeData factory for the <i>partitioning</i> stage.
 * 
 * @author Vincent Mirian
 * 
 * @date 2018-05-21
 *
 */
public class PTNetlistEdgeDataFactory extends NetlistEdgeDataFactory<PTNetlistEdgeData>{

	/**
	 *  Returns the PTNetlistEdgeData that has the same name as the parameter <i>name</i> within the PTNetlistEdgeDataFactory
	 *  
	 *  @param name string used for searching this instance
	 *  @return the PTNetlistEdgeData instance if the PTNetlistEdgeData exists within the PTNetlistEdgeDataFactory, otherwise null
	 */
	@Override
	protected PTNetlistEdgeData getNetlistEdgeData(final String name) {
		PTNetlistEdgeData rtn = null;
		if (name.equals("GPCC_SUGARM_BASE")){
			rtn = new GPCC_SUGARM_BASENetlistEdgeData();
		}
		if (name.equals("GPCC_SCIP_BASE")){
			rtn = new GPCC_SCIP_BASENetlistEdgeData();
		}
		if (name.equals("GPCC_BASE")){
			rtn = new GPCC_BASENetlistEdgeData();
		}
		if (name.equals("HMetis")){
			rtn = new HMetisNetlistEdgeData();
		}
		return rtn;
	}
	
}