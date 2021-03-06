/**
 * Copyright (C) 2017-2020
 * Massachusetts Institute of Technology (MIT)
 * Boston University (BU)
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

import java.util.ArrayList;
/**
 * The GateParts is class representing the parts description for a gate in the gate assignment of the <i>SimulatedAnnealing</i> algorithm.
 * 
 * @author Vincent Mirian
 * @author Timothy Jones
 * 
 * @date 2018-05-21
 *
 */
import java.util.List;

import org.cellocad.v2.common.CObject;
import org.cellocad.v2.common.Pair;
import org.cellocad.v2.common.profile.ProfileUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class GateToxicity extends CObject{

	private void parseName(final JSONObject JObj){
		String value = ProfileUtils.getString(JObj, S_GATENAME);
		this.setGateName(value);
	}
	
	private void parseMapVariable(final JSONObject JObj){
		String value = ProfileUtils.getString(JObj, S_MAPSTOVARIABLE);
		this.setMapVariable(value);
	}

	private void parseInput(final JSONObject JObj){
		JSONArray jArr = (JSONArray) JObj.get(S_INPUT);
		for (int i = 0; i < jArr.size(); i++) {
			Double input = ((Number)jArr.get(i)).doubleValue();
			this.getInput().add(input);
		}
	}

	private void parseGrowth(final JSONObject JObj){
		JSONArray jArr = (JSONArray) JObj.get(S_GROWTH);
		for (int i = 0; i < jArr.size(); i++) {
			Double growth = ((Number)jArr.get(i)).doubleValue();
			this.getGrowth().add(growth);
		}
	}
	
	private void parseInputGrowthPairs(){
		if (this.getNumInput() != this.getNumGrowth()) {
			throw new RuntimeException ("Error with GateToxicity");
		}
		for (int i = 0; i < this.getNumInput(); i++) {
			Double input = this.getInputAtIdx(i);
			Double growth = this.getGrowthAtIdx(i);
			Pair<Double, Double> pair = new Pair<Double,Double>(input, growth);
			this.getInputGrowthPairs().add(pair);
		}
	}
	
	private void init() {
		input = new ArrayList<Double>();
		growth = new ArrayList<Double>();
		inputGrowthPairs = new ArrayList<Pair<Double,Double>>();
	}
	

	private void parseToxicity(final JSONObject jObj) {
		init();
		this.parseName(jObj);
		this.parseMapVariable(jObj);
		this.parseInput(jObj);
		this.parseGrowth(jObj);
		this.parseInputGrowthPairs();
    }
	
	public GateToxicity(final JSONObject jObj) {
		this.parseToxicity(jObj);
	}

	/*
	 * GateName
	 */
	private void setGateName(final String gateName){
		this.setName(gateName);
	}
	
	public String getGateName(){
		return this.getName();
	}
	
	/*
	 * Gate
	 */
	public void setGate(final Gate gate) {
		this.gate = gate;
	}
	
	public Gate getGate() {
		return this.gate;
	}
	
	private Gate gate;
	
	/*
	 * MapVariable
	 */
	private void setMapVariable(final String mapVariable){
		this.mapVariable = mapVariable;
	}
	
	public String getMapVariable(){
		return this.mapVariable;
	}
	
	private String mapVariable;
	
	/*
	 * Input
	 */
	private List<Double> getInput(){
		return this.input;
	}
	
	public Double getInputAtIdx(final int index){
		Double rtn = null;
		if (
				(0 <= index)
				&&
				(index < this.getNumInput())
				) {
			rtn = this.getInput().get(index);
		}
		return rtn;
	}
	
	public int getNumInput(){
		return this.getInput().size();
	}
	
	private List<Double> input;
	
	/*
	 * Growth
	 */
	private List<Double> getGrowth(){
		return this.growth;
	}
	
	public Double getGrowthAtIdx(final int index){
		Double rtn = null;
		if (
				(0 <= index)
				&&
				(index < this.getNumGrowth())
				) {
			rtn = this.getGrowth().get(index);
		}
		return rtn;
	}
	
	public int getNumGrowth(){
		return this.getGrowth().size();
	}
	
	private List<Double> growth;
	
	/*
	 * Growth
	 */
	private List<Pair<Double,Double>> getInputGrowthPairs(){
		return this.inputGrowthPairs;
	}
	
	public Pair<Double,Double> getInputGrowthPairAtIdx(final int index){
		Pair<Double,Double> rtn = null;
		if (
				(0 <= index)
				&&
				(index < this.getNumInputGrowthPairs())
				) {
			rtn = this.getInputGrowthPairs().get(index);
		}
		return rtn;
	}
	
	public int getNumInputGrowthPairs(){
		return this.getInputGrowthPairs().size();
	}
	
	private List<Pair<Double,Double>> inputGrowthPairs;
	
	private static final String S_GATENAME = "gate_name";
	private static final String S_MAPSTOVARIABLE = "maps_to_variable";
	private static final String S_INPUT = "input";
	private static final String S_GROWTH = "growth";

}
