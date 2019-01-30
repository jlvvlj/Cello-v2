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
package org.cellocad.cello2.technologyMapping.algorithm.SimulatedAnnealing.data.cytometry.cytometrytable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.cellocad.cello2.common.CObject;
import org.cellocad.cello2.common.Pair;

/**
 * The Cytometry class contains the cytometry of a netlist used within the <i>SimulatedAnnealing</i> algorithm class of the <i>technologyMapping</i> stage.
 * @param T type index
 *
 * @author Timothy Jones
 *
 * @date 2019-01-29
 *
 */
public class Cytometry<T> extends CObject{

	/**
	 * Initialize class members
	 */
	private void init() {
		cytometryEntry = new ArrayList<Pair<T,Histogram>>();
		cytometryEntryMap = new HashMap<T,Histogram>();
	}

	/**
	 * Initializes a newly created Cytometry with the list of types defined by parameter <i>nodes</i>
	 * and value defined by parameter <i>value</i>.
	 *
	 * @param nodes the List of types
	 * @param value the value
	 */
	public Cytometry(final List<T> nodes) {
		init();
		for (int i = 0; i < nodes.size(); i++) {
			T node = nodes.get(i);
			Histogram histogram = new Histogram();
			Pair<T,Histogram> pair = new Pair<T,Histogram>(node, histogram);
			this.getCytometry().add(pair);
			this.getCytometryMap().put(node, histogram);
		}
	}

	/*
	 * Cytometry
	 */
	/**
	 * Getter for <i>cytometryEntry</i>
	 * @return the cytometryEntry of this instance
	 */
	protected List<Pair<T,Histogram>> getCytometry() {
		return cytometryEntry;
	}

	/**
	 * Returns the Pair<T,Histogram> at the specified position in this instance.
	 *
	 * @param index index of the Pair<T,Histogram> to return
	 * @return if the index is within the bounds (0 <= bounds < this.getNumCytometryPosition()), returns the Pair<T,Histogram> at the specified position in this instance, otherwise null
	 */
	protected Pair<T,Histogram> getCytometryPositionAtIdx(final int index){
		Pair<T,Histogram> rtn = null;
		if (
		    (0 <= index)
		    &&
		    (index < this.getNumCytometryPosition())
		    ) {
			rtn = this.getCytometry().get(index);
		}
		return rtn;
	}

	/**
	 * Returns the number of Pair<T,Double> in this instance.
	 *
	 * @return the number of Pair<T,Double> in this instance.
	 */
	public int getNumCytometryPosition() {
		return this.getCytometry().size();
	}

	/*
	 * CytometryMap
	 */
	/**
	 * Getter for <i>cytometryEntryMap</i>
	 * @return the cytometryEntryMap of this instance
	 */
	protected Map<T,Histogram> getCytometryMap() {
		return cytometryEntryMap;
	}

	/**
	 * Returns the cytometry of <i>node</i>
	 * @return the cytometry of <i>node</i> if the node exists, null otherwise
	 */
	public Histogram getCytometry(final T node){
		Histogram rtn = null;
		rtn = this.getCytometryMap().get(node);
		return rtn;
	}

	/**
	 * Returns true if the <i>node</i> exists in this instance, then assigns the Histogram <i>value</i> to the <i>node</i>
	 *
	 * @param node the node
	 * @param value the value
	 * @return true if the node exists in this instance, false otherwise
	 */
	public boolean setCytometry(final T node, final Histogram value){
		boolean rtn = false;
		for (int i = 0; i < this.getNumCytometryPosition(); i ++) {
			Pair<T,Histogram> position = this.getCytometryPositionAtIdx(i);
			if (position.getFirst().equals(node)) {
				position.setSecond(value);
				this.getCytometryMap().put(node, value);
				rtn = true;
			}
		}
		return rtn;
	}

	private List<Pair<T,Histogram>> cytometryEntry;
	private Map<T,Histogram> cytometryEntryMap;

}
