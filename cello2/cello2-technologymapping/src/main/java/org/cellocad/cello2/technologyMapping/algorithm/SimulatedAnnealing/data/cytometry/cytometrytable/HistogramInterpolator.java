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
import java.util.List;

import org.cellocad.cello2.common.CObject;

/**
 * The HistogramInterpolator class interpolates a new Histogram between two Histogram
 *
 * @author Timothy Jones
 *
 * @date 2019-01-29
 *
 */
public class HistogramInterpolator extends CObject {
	
	public HistogramInterpolator(final Histogram a, final Histogram b) {
		this.setHistogramA(a);
		this.setHistogramB(b);
	}
	
	public Histogram interpolate(Double r) {
		Histogram rtn = null;
		List<Double> bins = new ArrayList<>();
		List<Double> counts = new ArrayList<>();
		for (int i = 0; i < this.getHistogramA().getNumOutputBins(); i++) {
			bins.add(this.getHistogramA().getOutputBinsAtIdx(i));
			double a = this.getHistogramA().getOutputCountsAtIdx(i);
			double b = this.getHistogramB().getOutputCountsAtIdx(i);
			Double value = Math.min(a, b) + r*Math.abs(a - b);
			counts.add(value);
		}
		rtn = new Histogram(bins,counts);
		return rtn;
	}

	/**
	 * Getter for <i>histogramA</i>
	 * @return value of <i>histogramA</i>
	 */
	public Histogram getHistogramA() {
		return histogramA;
	}

	/**
	 * Setter for <i>histogramA</i>
	 * @param histogramA the value to set <i>histogramA</i>
	 */
	public void setHistogramA(final Histogram histogramA) {
		this.histogramA = histogramA;
	}

	/**
	 * Getter for <i>histogramB</i>
	 * @return value of <i>histogramB</i>
	 */
	public Histogram getHistogramB() {
		return histogramB;
	}

	/**
	 * Setter for <i>histogramB</i>
	 * @param histogramB the value to set <i>histogramB</i>
	 */
	public void setHistogramB(final Histogram histogramB) {
		this.histogramB = histogramB;
	}

	private Histogram histogramA;
	private Histogram histogramB;

}
