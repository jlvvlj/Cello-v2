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
package org.cellocad.cello2.technologyMapping.common;

import java.io.File;

import org.cellocad.cello2.common.Utils;

/**
 * The TMUtils class is class with utility methods for the <i>technologyMapping</i> stage.
 * 
 * @author Vincent Mirian
 * 
 * @date 2018-05-21
 *
 */
public class TMUtils {

	/**
	 * Returns the path of the ClassLoader
	 * 
	 * @return the path of the ClassLoader
	 *
	 */
	static public String getFilepath(){
		String rtn = "";
		rtn += (new File(TMUtils.class.getProtectionDomain().getCodeSource().getLocation().getPath())).getParent();
		rtn += Utils.getFileSeparator();
		// rtn = TMUtils.class.getClassLoader().getResource(".").getPath();
		return rtn;
	}

	/**
	 * Returns the path of the Resources directory for the <i>technologyMapping</i> stage
	 * 
	 * @return the path of the Resources directory for the <i>technologyMapping</i> stage
	 *
	 */	
	static public String getResourcesFilepath(){
		String rtn = "";
		rtn += TMUtils.getFilepath();
		rtn += "resources-";
		rtn += "technologyMapping";
		return rtn;
	}
	
}