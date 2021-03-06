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
package org.cellocad.v2.placing.algorithm.Eugene;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.apache.commons.io.FileUtils;
import org.cellocad.v2.common.CelloException;
import org.cellocad.v2.common.Utils;
import org.cellocad.v2.common.netlistConstraint.data.NetlistConstraint;
import org.cellocad.v2.common.netlistConstraint.data.NetlistConstraintUtils;
import org.cellocad.v2.common.runtime.environment.ArgString;
import org.cellocad.v2.common.stage.Stage;
import org.cellocad.v2.common.stage.StageUtils;
import org.cellocad.v2.common.stage.runtime.environment.StageArgString;
import org.cellocad.v2.common.target.data.TargetData;
import org.cellocad.v2.common.target.data.TargetDataUtils;
import org.cellocad.v2.placing.runtime.PLRuntimeObject;
import org.cellocad.v2.placing.runtime.environment.PLRuntimeEnv;
import org.cellocad.v2.results.common.Results;
import org.cellocad.v2.results.netlist.Netlist;
import org.cellocad.v2.results.netlist.NetlistUtils;
import org.junit.Before;
import org.junit.Test;

/**
 * Integration test for the {@link Eugene} algorithm using the Eco1C1G1T1
 * library.
 *
 * @author Timothy Jones
 *
 * @date 2020-01-09
 *
 */
public class EugeneEco1C1G1T1IT {

	private static boolean initIsDone = false;

	@Before
	public void init() throws CelloException, IOException {
		if (initIsDone)
			return;
		Path dir = Files.createTempDirectory("cello_");
		this.output = dir.toFile();
		String[] args = { "-" + ArgString.INPUTNETLIST, Utils.getResource("and_Eco1C1G1T1_TM.netlist.json").getFile(),
		        "-" + ArgString.USERCONSTRAINTSFILE, Utils.getResource("lib/ucf/Eco/Eco1C1G1T1.UCF.json").getFile(),
		        "-" + ArgString.INPUTSENSORFILE, Utils.getResource("lib/input/Eco/Eco1C1G1T1.input.json").getFile(),
		        "-" + ArgString.OUTPUTDEVICEFILE,
		        Utils.getResource("lib/output/Eco/Eco1C1G1T1.output.json").getFile(),
		        "-" + StageArgString.ALGORITHMNAME, "Eugene",
		        "-" + ArgString.OUTPUTDIR, dir.toString(), "-" + ArgString.PYTHONENV, "python" };
		PLRuntimeEnv runEnv = new PLRuntimeEnv(args);
		runEnv.setName("placing");
		// InputFile
		String inputFilePath = runEnv.getOptionValue(ArgString.INPUTNETLIST);
		File inputFile = new File(inputFilePath);
		if (!(inputFile.exists() && !inputFile.isDirectory())) {
			throw new CelloException("Input file does not exist!");
		}
		// Read Netlist
		Netlist netlist = NetlistUtils.getNetlist(runEnv, ArgString.INPUTNETLIST);
		if (!netlist.isValid()) {
			throw new CelloException("Netlist is invalid!");
		}
		// get Stage
		Stage stage = StageUtils.getStage(runEnv, StageArgString.ALGORITHMNAME);
		stage.setName("placing");
		String stageName = runEnv.getOptionValue(StageArgString.STAGENAME);
		if (stageName != null) {
			stage.setName(stageName);
		}
		// get TargetData
		TargetData td = TargetDataUtils.getTargetTargetData(runEnv, ArgString.USERCONSTRAINTSFILE,
		        ArgString.INPUTSENSORFILE, ArgString.OUTPUTDEVICEFILE);
		if (!td.isValid()) {
			throw new CelloException("TargetData is invalid!");
		}
		// NetlistConstraint
		NetlistConstraint netlistConstraint = NetlistConstraintUtils.getNetlistConstraintData(runEnv,
		        ArgString.NETLISTCONSTRAINTFILE);
		if (netlistConstraint == null) {
			netlistConstraint = new NetlistConstraint();
		}
		// Results
		File outputDir = new File(runEnv.getOptionValue(ArgString.OUTPUTDIR));
		Results results = new Results(outputDir);
		// Execute
		PLRuntimeObject PL = new PLRuntimeObject(stage, td, netlistConstraint, netlist, results, runEnv);
		PL.setName("placing");
		this.PL = PL;
		initIsDone = true;
	}

	@Test
	public void Eugene_Eco1C1G1T1_ShouldReturn() throws CelloException, IOException {
		this.PL.execute();
		FileUtils.deleteDirectory(this.output);
	}

	private PLRuntimeObject PL;
	private File output;

}
