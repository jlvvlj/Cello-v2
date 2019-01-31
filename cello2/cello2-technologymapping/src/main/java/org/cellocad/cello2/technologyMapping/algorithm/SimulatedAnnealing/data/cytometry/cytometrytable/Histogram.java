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
import java.util.Collections;
import java.util.List;

import org.cellocad.cello2.common.CObject;

/**
 * The Histogram class represents a histogram.
 *
 * @author Timothy Jones
 *
 * @date 2019-01-29
 *
 */
public class Histogram extends CObject{

	/**
	 *  Initialize class members
	 */
	private void init() {
		outputBins = new ArrayList<>();
		outputCounts = new ArrayList<>();
	}

	public Histogram() {
		init();
	}

	public Histogram(final List<Double> bins, final List<Double> counts) {
		outputBins = bins;
		outputCounts = counts;
	}

	public Double getMean() {
		Double rtn = null;
		double sum = 0.0;
		double num = 0.0;
		for (int i = 0; i < this.getNumOutputBins(); i++) {
			double bin = this.getOutputBinsAtIdx(i);
			double count = this.getOutputCountsAtIdx(i);
			sum += count;
			num += bin*count;
		}
		rtn = num / sum;
		return rtn;
	}

	/*
	 * OutputBins
	 */
	private List<Double> getOutputBins(){
		return this.outputBins;
	}

	public Double getOutputBinsAtIdx(final int index){
		Double rtn = null;
		if (
				(0 <= index)
				&&
				(index < this.getNumOutputBins())
				) {
			rtn = this.getOutputBins().get(index);
		}
		return rtn;
	}

	public int getNumOutputBins(){
		return this.getOutputBins().size();
	}

	private List<Double> outputBins;

	/*
	 * OutputCounts
	 */
	private List<Double> getOutputCounts(){
		return this.outputCounts;
	}

	public Double getOutputCountsAtIdx(final int index){
		Double rtn = null;
		if (
				(0 <= index)
				&&
				(index < this.getNumOutputCounts())
				) {
			rtn = this.getOutputCounts().get(index);
		}
		return rtn;
	}

	public int getNumOutputCounts(){
		return this.getOutputCounts().size();
	}

	private List<Double> outputCounts;

	public String getOutputBinsAsString() {
		String rtn = "";
		rtn += String.valueOf(this.getOutputBins().get(0));
		for (int i = 0; i < this.getNumOutputBins()-1; i++) {
			rtn += ",";
			rtn += String.valueOf(this.getOutputBins().get(i+1));
		}
		return rtn;
	}

	public String getOutputCountsAsString() {
		String rtn = "";
		rtn += String.valueOf(this.getOutputCounts().get(0));
		for (int i = 0; i < this.getNumOutputCounts()-1; i++) {
			rtn += ",";
			rtn += String.valueOf(this.getOutputCounts().get(i+1));
		}
		return rtn;
	}

}
