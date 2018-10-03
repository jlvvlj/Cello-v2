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
package org.cellocad.cello2.technologyMapping.algorithm.RandomYeast.data;

import java.io.IOException;
import java.io.Writer;

import org.cellocad.cello2.technologyMapping.algorithm.RandomYeast.data.ucf.Assignable;
import org.cellocad.cello2.technologyMapping.algorithm.data.TMNetlistNodeData;

/**
 * The RandomYeastNetlistNodeData class contains all data for a node used within the <i>RandomYeast</i> algorithm class of the <i>technologyMapping</i> stage.
 * 
 * @author Timothy Jones
 * 
 * @date Today
 *
 */
public class RandomYeastNetlistNodeData extends TMNetlistNodeData{

	private void setDefault() {
	}
	
	/**
	 *  Initializes a newly created RandomYeastNetlistNodeData
	 */
	public RandomYeastNetlistNodeData(){
		super();
		this.setDefault();
	}

	/**
	 *  Writes this instance in JSON format to the writer defined by parameter <i>os</i> with the number of indents equivalent to the parameter <i>indent</i>
	 *  @param indent the number of indents
	 *  @param os the writer
	 *  @throws IOException If an I/O error occurs
	 */
	public void writeJSON(int indent, final Writer os) throws IOException {
		
	}
	
	/*
	 * Gate
	 */
	public void setGate(final Assignable gate) {
		this.gate = gate;
	}
	
	public Assignable getGate() {
		return this.gate;
	}
	
	private Assignable gate;

}
