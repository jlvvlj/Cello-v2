/**
 * Copyright (C) 2020 Boston University (BU)
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
package org.cellocad.v2.common.target.data.data;

import org.cellocad.v2.common.CObject;
import org.cellocad.v2.common.profile.ProfileUtils;
import org.json.simple.JSONObject;

/**
 *
 *
 * @author Timothy Jones
 *
 * @date 2020-01-13
 *
 */
public class GeneticLocation extends CObject {

	private void init() {
	}

	private void parseSymbol(JSONObject jObj) {
		String str = ProfileUtils.getString(jObj, S_SYMBOL);
		this.setName(str);
	}

	private void parseLocus(JSONObject jObj) {
		Integer z = ProfileUtils.getInteger(jObj, S_LOCUS);
		this.locus = z;
	}

	private void parseLocation(JSONObject jObj) {
		this.parseSymbol(jObj);
		this.parseLocus(jObj);
	}

	public GeneticLocation(JSONObject jObj) {
		this.init();
		this.parseLocation(jObj);
	}
	
	/**
	 * Getter for <i>locus</i>
	 *
	 * @return value of <i>locus</i>
	 */
	public Integer getLocus() {
		return locus;
	}

	private Integer locus;

	private static final String S_SYMBOL = "symbol";
	private static final String S_LOCUS = "locus";

}
