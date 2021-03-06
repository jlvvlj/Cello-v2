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
package org.cellocad.v2.results.common;

import java.io.File;

import org.cellocad.v2.common.CObject;

/**
 * A result object.
 *
 * @author Timothy Jones
 *
 * @date 2020-03-30
 *
 */
public class Result extends CObject {

	private String stage;
	private String description;
	private File file;

	/**
	 * Create a new result object.
	 *
	 * @param name        The result name.
	 * @param stage       The stage that generated the result.
	 * @param description A description of the result.
	 * @param file        The result file.
	 */
	public Result(final String name, final String stage, final String description, final File file) {
		super();
		this.setName(name);
		this.stage = stage;
		this.description = description;
		this.file = file;
	}

	/**
	 * Getter for {@code stage}.
	 *
	 * @return The value of {@code stage}.
	 */
	private String getStage() {
		return this.stage;
	}

	/**
	 * Getter for {@code description}.
	 *
	 * @return The value of {@code description}.
	 */
	private String getDescription() {
		return this.description;
	}

	/**
	 * Getter for {@code file}.
	 *
	 * @return The value of {@code file}.
	 */
	public File getFile() {
		return this.file;
	}

}
