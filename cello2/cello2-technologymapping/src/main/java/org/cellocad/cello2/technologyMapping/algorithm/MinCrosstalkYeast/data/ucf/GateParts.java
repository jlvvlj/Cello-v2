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
package org.cellocad.cello2.technologyMapping.algorithm.MinCrosstalkYeast.data.ucf;

import java.util.HashMap;
import java.util.Map;

import org.cellocad.cello2.common.CObject;
import org.cellocad.cello2.common.CObjectCollection;
import org.cellocad.cello2.common.profile.ProfileUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

/**
 * The GateParts is class representing the parts description for a gate in the gate assignment of the <i>SimulatedAnnealing</i> algorithm.
 * 
 * @author Vincent Mirian
 * 
 * @date 2018-05-21
 *
 */
public class GateParts extends CObject{

	private void parseGateName(final JSONObject JObj){
		String value = ProfileUtils.getString(JObj, "gate_name");
		this.setGateName(value);
	}
	
	private void parseExpressionCassettes(final JSONObject JObj, CObjectCollection<Part> parts){
		Map< String, CasetteParts > expressionCassettes = this.getExpressionCassettes();
		JSONArray jArr = (JSONArray) JObj.get("expression_cassettes");
		for (int i = 0; i < jArr.size(); i++) {
			JSONObject jObj = (JSONObject) jArr.get(i);
			String name = ProfileUtils.getString(jObj, "maps_to_variable");
			if (name == null) {
				continue;
			}
			JSONArray JArr = (JSONArray) jObj.get("cassette_parts");
			int size = JArr.size();
			if ((JArr == null) || (size == 0)) {
				continue;
			}
			CasetteParts data = new CasetteParts(JArr, parts);
			expressionCassettes.put(name, data);
		}
	}

	private void parseRegulates(final JSONObject JObj){
		String value = ProfileUtils.getString(JObj, "regulates");
		this.setRegulates(value);
	}
	
	private void parsePromoterParts(final JSONObject JObj, CObjectCollection<Part> parts) {
		JSONArray jArr = (JSONArray) JObj.get("promoter_parts");
		PromoterParts data = new PromoterParts(jArr, parts);
		this.setPromoterParts(data);
	}

	private void parseGateParts(final JSONObject jObj, CObjectCollection<Part> parts) {
		this.parseGateName(jObj);
		this.parseExpressionCassettes(jObj, parts);
		this.parseRegulates(jObj);
		this.parsePromoterParts(jObj, parts);
    }
	
	private void init() {
		expressionCassettes = new HashMap< String, CasetteParts >();
	}
	
	public GateParts(final JSONObject jobj, CObjectCollection<Part> parts) {
		this.init();
		this.parseGateParts(jobj, parts);
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
	 * Regulates
	 */
	private void setRegulates(final String regulates){
		this.regulates = regulates;
	}
	
	public String getRegulates(){
		return this.regulates;
	}
	
	private String regulates;
	
	/*
	 * PromoterParts
	 */
	private void setPromoterParts(final PromoterParts parts){
		this.promoterParts = parts;
	}
	
	public PromoterParts getPromoterParts(){
		return this.promoterParts;
	}
	
	private PromoterParts promoterParts;
	
	/*
	 * CasetteParts
	 */
	/**
	 *  Returns the CasetteParts of Part of the variable defined by parameter, <i>variable</i>
	 *  
	 *  @param variable the variable
	 *  @return the CasetteParts
	 */
	public CasetteParts getCasetteParts(String variable) {
		CasetteParts rtn = null;
		rtn = this.getExpressionCassettes().get(variable);
		return rtn;
	}

	private Map< String, CasetteParts > getExpressionCassettes() {
		return this.expressionCassettes;
	}
	
	Map<String, CasetteParts> expressionCassettes;
}
