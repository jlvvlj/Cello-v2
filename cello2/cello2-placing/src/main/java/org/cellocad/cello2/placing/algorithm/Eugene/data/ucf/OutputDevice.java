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
package org.cellocad.cello2.placing.algorithm.Eugene.data.ucf;

import org.cellocad.cello2.common.profile.ProfileUtils;
import org.json.simple.JSONObject;

/**
 * 
 *
 * @author Timothy Jones
 *
 * @date 2018-06-29
 *
 */
public class OutputDevice extends Assignable{
	
	private void init() {
	}

	private void parseName(final JSONObject JObj){
		String value = ProfileUtils.getString(JObj, "name");
		this.setName(value);
	}
	
	private void parseOutputDevice(final JSONObject jObj) {
		this.parseName(jObj);
	}
	
	public OutputDevice(final JSONObject jObj) {
		this.init();
		this.parseOutputDevice(jObj);
	}
	
	@Override
	public boolean isValid() {
		boolean rtn = super.isValid();
		rtn = rtn && (this.getName() != null);
		return rtn;
	}
	
	/**
	 * Getter for <i>outputDeviceStructure</i>
	 *
	 * @return value of <i>outputDeviceStructure</i>
	 */
	public OutputDeviceStructure getOutputDeviceStructure() {
		return outputDeviceStructure;
	}

	/**
	 * Setter for <i>outputDeviceStructure</i>
	 *
	 * @param outputDeviceStructure the value to set <i>outputDeviceStructure</i>
	 */
	public void setOutputDeviceStructure(final OutputDeviceStructure outputDeviceStructure) {
		this.outputDeviceStructure = outputDeviceStructure;
	}

	private OutputDeviceStructure outputDeviceStructure;
	
}
