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
package org.cellocad.v2.export.algorithm.SBOL;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cellocad.v2.common.CObjectCollection;
import org.cellocad.v2.common.CelloException;
import org.cellocad.v2.common.Utils;
import org.cellocad.v2.common.target.data.data.AssignableDevice;
import org.cellocad.v2.common.target.data.data.DNAComponent;
import org.cellocad.v2.common.target.data.data.Gate;
import org.cellocad.v2.common.target.data.data.InputSensor;
import org.cellocad.v2.common.target.data.data.OutputDevice;
import org.cellocad.v2.common.target.data.data.Part;
import org.cellocad.v2.common.target.data.data.Structure;
import org.cellocad.v2.common.target.data.data.StructureDevice;
import org.cellocad.v2.common.target.data.data.StructureObject;
import org.cellocad.v2.common.target.data.data.StructurePart;
import org.cellocad.v2.common.target.data.data.StructureTemplate;
import org.cellocad.v2.export.algorithm.EXAlgorithm;
import org.cellocad.v2.export.algorithm.SBOL.data.SBOLDataUtils;
import org.cellocad.v2.export.algorithm.SBOL.data.SBOLNetlistData;
import org.cellocad.v2.export.algorithm.SBOL.data.SBOLNetlistEdgeData;
import org.cellocad.v2.export.algorithm.SBOL.data.SBOLNetlistNodeData;
import org.cellocad.v2.export.runtime.environment.EXArgString;
import org.cellocad.v2.export.target.data.EXTargetDataInstance;
import org.cellocad.v2.results.netlist.Netlist;
import org.cellocad.v2.results.netlist.NetlistEdge;
import org.cellocad.v2.results.netlist.NetlistNode;
import org.cellocad.v2.results.placing.placement.Placement;
import org.cellocad.v2.results.placing.placement.PlacementGroup;
import org.cellocad.v2.results.placing.placement.Placements;
import org.sbolstandard.core2.AccessType;
import org.sbolstandard.core2.ComponentDefinition;
import org.sbolstandard.core2.DirectionType;
import org.sbolstandard.core2.ModuleDefinition;
import org.sbolstandard.core2.RestrictionType;
import org.sbolstandard.core2.SBOLConversionException;
import org.sbolstandard.core2.SBOLDocument;
import org.sbolstandard.core2.SBOLValidationException;
import org.sbolstandard.core2.SBOLWriter;
import org.sbolstandard.core2.Sequence;
import org.sbolstandard.core2.SequenceAnnotation;
import org.sbolstandard.core2.SequenceOntology;
import org.synbiohub.frontend.SynBioHubException;
import org.synbiohub.frontend.SynBioHubFrontend;
import org.virtualparts.VPRException;
import org.virtualparts.VPRTripleStoreException;
import org.virtualparts.data.QueryParameters;
import org.virtualparts.data.SBOLInteractionAdder_GeneCentric;

/**
 * The SBOL class implements the <i>SBOL</i> algorithm in the <i>export</i>
 * stage.
 *
 * @author Timothy Jones
 *
 * @date 2018-06-04
 *
 */
public class SBOL extends EXAlgorithm {

	/**
	 * Returns the <i>SBOLNetlistNodeData</i> of the <i>node</i>
	 *
	 * @param node a node within the <i>netlist</i> of this instance
	 * @return the <i>SBOLNetlistNodeData</i> instance if it exists, null otherwise
	 */
	protected SBOLNetlistNodeData getSBOLNetlistNodeData(NetlistNode node) {
		SBOLNetlistNodeData rtn = null;
		rtn = (SBOLNetlistNodeData) node.getNetlistNodeData();
		return rtn;
	}

	/**
	 * Returns the <i>SBOLNetlistEdgeData</i> of the <i>edge</i>
	 *
	 * @param edge an edge within the <i>netlist</i> of this instance
	 * @return the <i>SBOLNetlistEdgeData</i> instance if it exists, null otherwise
	 */
	protected SBOLNetlistEdgeData getSBOLNetlistEdgeData(NetlistEdge edge) {
		SBOLNetlistEdgeData rtn = null;
		rtn = (SBOLNetlistEdgeData) edge.getNetlistEdgeData();
		return rtn;
	}

	/**
	 * Returns the <i>SBOLNetlistData</i> of the <i>netlist</i>
	 *
	 * @param netlist the netlist of this instance
	 * @return the <i>SBOLNetlistData</i> instance if it exists, null otherwise
	 */
	protected SBOLNetlistData getSBOLNetlistData(Netlist netlist) {
		SBOLNetlistData rtn = null;
		rtn = (SBOLNetlistData) netlist.getNetlistData();
		return rtn;
	}

	/**
	 * Gets the Constraint data from the NetlistConstraintFile
	 */
	@Override
	protected void getConstraintFromNetlistConstraintFile() {

	}

	/**
	 * Gets the data from the UCF
	 * 
	 * @throws CelloException
	 */
	@Override
	protected void getDataFromUCF() throws CelloException {
		this.setTargetDataInstance(new EXTargetDataInstance(this.getTargetData()));
	}

	/**
	 * Set parameter(s) value(s) of the algorithm
	 */
	@Override
	protected void setParameterValues() {
		Boolean present = false;
		present = this.getAlgorithmProfile().getStringParameter("RepositoryUrl").getFirst();
		if (present) {
			this.setRepositoryUrl(this.getAlgorithmProfile().getStringParameter("RepositoryUrl").getSecond());
		}
		present = this.getAlgorithmProfile().getStringParameter("CollectionUri").getFirst();
		if (present) {
			this.setCollectionUri(this.getAlgorithmProfile().getStringParameter("CollectionUri").getSecond());
		}
		present = this.getAlgorithmProfile().getBooleanParameter("AddInteractions").getFirst();
		if (present) {
			this.setAddInteractions(this.getAlgorithmProfile().getBooleanParameter("AddInteractions").getSecond());
		}
		present = this.getAlgorithmProfile().getBooleanParameter("AddDesignModules").getFirst();
		if (present) {
			this.setAddDesignModules(this.getAlgorithmProfile().getBooleanParameter("AddDesignModules").getSecond());
		}
	}

	/**
	 * Validate parameter value for <i>repositoryUrl</i>
	 */
	protected void validateRepositoryUrlValue() {
		String url = this.getRepositoryUrl();
		if (url != null) {
			try {
				new URL(url);
			} catch (MalformedURLException e) {
				this.logError(url + " is not a valid URL!");
				Utils.exit(-1);
			}
		}
	}

	/**
	 * Validate parameter value of the algorithm
	 */
	@Override
	protected void validateParameterValues() {
		this.validateRepositoryUrlValue();
	}

	/**
	 * Perform preprocessing
	 */
	@Override
	protected void preprocessing() {
		// sbh frontend
		if (this.getRepositoryUrl() != null) {
			this.setSbhFrontend(new SynBioHubFrontend(this.getRepositoryUrl()));
		}
		// output directory
		String outputDir = this.getRuntimeEnv().getOptionValue(EXArgString.OUTPUTDIR);
		// output filename
		String inputFilename = this.getNetlist().getInputFilename();
		String filename = Utils.getFilename(inputFilename);
		this.setSbolFilename(outputDir + Utils.getFileSeparator() + filename + ".xml");
	}

	/**
	 * Add component definitions for all parts of all gates in the netlist.
	 * 
	 * @param netlist  the <i>netlist</i> with component definitions to add
	 * @param document the <i>SBOLDocument</i> to which to add the definitions
	 * @throws SynBioHubException      unable to fetch part SBOL from SynBioHub
	 * @throws SBOLValidationException unable to create component definition
	 * @throws CelloException
	 */
	protected void addComponentDefinitions(SBOLDocument document)
			throws SynBioHubException, SBOLValidationException, CelloException {
		Netlist netlist = this.getNetlist();
		Placements placements = netlist.getResultNetlistData().getPlacements();
		for (int i = 0; i < placements.getNumPlacement(); i++) {
			Placement placement = placements.getPlacementAtIdx(i);
			for (int j = 0; j < placement.getNumPlacementGroup(); j++) {
				PlacementGroup group = placement.getPlacementGroupAtIdx(j);
				for (int k = 0; k < group.getNumComponent(); k++) {
					org.cellocad.v2.results.placing.placement.Component component = group.getComponentAtIdx(k);
					for (int l = 0; l < component.getNumPart(); l++) {
						String str = component.getPartAtIdx(l);
						Part part = this.getTargetDataInstance().getParts().findCObjectByName(str);
						AssignableDevice ad = this.getTargetDataInstance().getAssignableDeviceByName(str);
						if (part != null) {
							SBOLUtils.addPartDefinition(part, document, this.getSbhFrontend());
							continue;
						}
						if (ad != null) {
							SBOLUtils.addDeviceDefinition(ad, document, this.getSbhFrontend());
							continue;
						}
						String nodeName = component.getNode();
						NetlistNode node = this.getNetlist().getVertexByName(nodeName);
						String deviceName = node.getResultNetlistNodeData().getDeviceName();
						ad = this.getTargetDataInstance().getAssignableDeviceByName(deviceName);
						Structure s = ad.getStructure();
						StructureDevice sd = s.getDeviceByName(str);
						Collection<String> parts = getFlattenedPartList(sd);
						for (String partName : parts) {
							Part c = (Part) this.getDNAComponentByName(partName);
							SBOLUtils.addPartDefinition(c, document, this.getSbhFrontend());
						}
					}
				}
			}
		}
	}

	private static Collection<String> getFlattenedPartList(StructureDevice device) throws CelloException {
		Collection<String> rtn = new ArrayList<>();
		for (StructureObject o : device.getComponents()) {
			if (o instanceof StructureTemplate) {
				throw new CelloException("Cannot flatten with unreferenced input parts.");
			}
			if (o instanceof StructurePart) {
				rtn.add(o.getName());
			}
			if (o instanceof StructureDevice) {
				rtn.addAll(getFlattenedPartList((StructureDevice) o));
			}
		}
		return rtn;
	}

	/**
	 * Add transcriptional unit component definitions to an SBOLDocument.
	 * 
	 * @param document the SBOLDocument
	 * @throws SBOLValidationException unable to add transcriptional unit
	 * @throws CelloException          Unable to get parts from nested device.
	 */
	protected void addTranscriptionalUnitDefinitions(SBOLDocument document)
			throws SBOLValidationException, CelloException {
		Netlist netlist = this.getNetlist();
		Placements placements = netlist.getResultNetlistData().getPlacements();
		for (int i = 0; i < placements.getNumPlacement(); i++) {
			Placement placement = placements.getPlacementAtIdx(i);
			for (int j = 0; j < placement.getNumPlacementGroup(); j++) {
				PlacementGroup group = placement.getPlacementGroupAtIdx(j);
				String name = String.format("Design%d_", i);
				for (int k = 0; k < group.getNumComponent(); k++) {
					org.cellocad.v2.results.placing.placement.Component component = group.getComponentAtIdx(k);

					// ComponentDefinition
					ComponentDefinition cd = document.createComponentDefinition(name + component.getName(), "1",
							ComponentDefinition.DNA_REGION);
					cd.addRole(SequenceOntology.ENGINEERED_REGION);
					component.setUri(cd.getIdentity());

					// parts
					String sequence = "";
					CObjectCollection<DNAComponent> components = new CObjectCollection<>();
					for (int l = 0; l < component.getNumPart(); l++) {
						String componentName = component.getPartAtIdx(l);
						DNAComponent comp = this.getDNAComponentByName(componentName);
						if (comp == null) {
							String nodeName = component.getNode();
							NetlistNode node = this.getNetlist().getVertexByName(nodeName);
							String deviceName = node.getResultNetlistNodeData().getDeviceName();
							AssignableDevice ad = this.getTargetDataInstance().getAssignableDeviceByName(deviceName);
							Structure s = ad.getStructure();
							StructureDevice sd = s.getDeviceByName(componentName);
							Collection<String> parts = getFlattenedPartList(sd);
							for (String str : parts) {
								DNAComponent c = this.getDNAComponentByName(str);
								components.add(c);
							}
						} else {
							components.add(comp);
						}
					}
					for (int l = 0; l < components.size(); l++) {
						DNAComponent co = components.get(l);
						// Component
						String cDisplayId = co.getName() + "_Component";
						AccessType cAccess = AccessType.PUBLIC;
						URI cDefinitionURI = co.getUri();
						org.sbolstandard.core2.Component c = cd.createComponent(cDisplayId, cAccess, cDefinitionURI);

						// SequenceAnnotation
						String s = SBOLDataUtils.getDNASequence(co);
						String saDisplayId = "SequenceAnnotation" + String.valueOf(l);
						String saLocationId = saDisplayId + "_Range";
						int start = sequence.length() + 1;
						int end = start + s.length() - 1;
						SequenceAnnotation sa = cd.createSequenceAnnotation(saDisplayId, saLocationId, start, end);
						sa.setComponent(c.getIdentity());
						sequence += s;

						// SequenceConstraint
						if (l != 0) {
							String scDisplayId = String.format("%s_Constraint%d", cd.getDisplayId(), l);
							RestrictionType scRestriction = RestrictionType.PRECEDES;
							URI scSubjectId = cd.getComponent(components.get(l - 1).getName() + "_Component")
									.getIdentity();
							URI scObjectId = cd.getComponent(co.getName() + "_Component").getIdentity();
							cd.createSequenceConstraint(scDisplayId, scRestriction, scSubjectId, scObjectId);
						}
					}
					// Sequence
					Sequence s = document.createSequence(cd.getDisplayId() + "_Sequence", sequence, Sequence.IUPAC_DNA);
					cd.addSequence(s);
				}
			}
		}
	}

	/**
	 * Create an SBOL document.
	 * 
	 * @return The generated SBOLDocument.
	 * @throws SynBioHubException      - Unable to fetch SBOL from SynBioHub for a
	 *                                 part.
	 * @throws SBOLValidationException - Unable to create Component or
	 *                                 ComponentDefinition.
	 * @throws CelloException
	 */
	protected SBOLDocument createSBOLDocument() throws SynBioHubException, SBOLValidationException, CelloException {
		SBOLDocument document = new SBOLDocument();
		document.setDefaultURIprefix("http://cellocad.org/v2");

		this.addComponentDefinitions(document);
		this.addTranscriptionalUnitDefinitions(document);

		return document;
	}

	/**
	 * Add device interactions via the Virtual Parts (VPR) API.
	 * 
	 * @param selectedRepo   - The specified synbiohub repository the user wants VPR
	 *                       model generator to connect to.
	 * @param generatedModel - The file to generate the model from.
	 * @param name           - The top level design name.
	 * @return The SBOL Document with interactions.
	 * @throws SBOLValidationException
	 * @throws IOException             - Unable to read or write the given
	 *                                 SBOLDocument
	 * @throws SBOLConversionException - Unable to perform conversion for the given
	 *                                 SBOLDocument.
	 * @throws VPRException            - Unable to perform VPR Model Generation on
	 *                                 the given SBOLDocument.
	 * @throws VPRTripleStoreException - Unable to perform VPR Model Generation on
	 *                                 the given SBOLDocument.
	 */
	protected SBOLDocument addInteractions(String selectedRepo, SBOLDocument document, String name)
			throws SBOLValidationException, IOException, SBOLConversionException, VPRException, VPRTripleStoreException,
			URISyntaxException {
		Set<URI> collections = new HashSet<URI>();
		collections.add(new URI(this.getCollectionUri()));
		QueryParameters params = new QueryParameters();
		params.setCollectionURIs(new ArrayList<>(collections));
		URI endpoint = new URL(new URL(selectedRepo), "/sparql").toURI();
		SBOLInteractionAdder_GeneCentric interactionAdder = new SBOLInteractionAdder_GeneCentric(endpoint, name,
				params);
		interactionAdder.addInteractions(document);
		return document;
	}

	protected DNAComponent getDNAComponentByName(String name) {
		DNAComponent rtn = null;
		Part part = this.getTargetDataInstance().getParts().findCObjectByName(name);
		Gate gate = this.getTargetDataInstance().getGates().findCObjectByName(name);
		InputSensor sensor = this.getTargetDataInstance().getInputSensors().findCObjectByName(name);
		OutputDevice reporter = this.getTargetDataInstance().getOutputDevices().findCObjectByName(name);
		if (part != null)
			rtn = part;
		if (gate != null)
			rtn = gate;
		if (sensor != null)
			rtn = sensor;
		if (reporter != null)
			rtn = reporter;
		return rtn;
	}

	/**
	 * Generate plasmid component definitions.
	 * 
	 * @param document the <i>SBOLDocument</i>
	 * @throws SBOLValidationException unable to add plasmid definition
	 * @throws SynBioHubException
	 */
	protected void addGroupDefinitions(SBOLDocument document) throws SBOLValidationException, SynBioHubException {
		Netlist netlist = this.getNetlist();
		Placements placements = netlist.getResultNetlistData().getPlacements();
		for (int i = 0; i < placements.getNumPlacement(); i++) {
			Placement placement = placements.getPlacementAtIdx(i);
			for (int j = 0; j < placement.getNumPlacementGroup(); j++) {
				PlacementGroup group = placement.getPlacementGroupAtIdx(j);

				String plasmidName = String.format("Design%d_Group%d", i, j);
				String version = "1";
				URI type = ComponentDefinition.DNA_REGION;

				ComponentDefinition cd = document.createComponentDefinition(plasmidName, version, type);
				cd.addRole(SequenceOntology.ENGINEERED_REGION);

				group.setUri(cd.getIdentity());

				String sequence = "";

				// transcriptional units
				for (int k = 0; k < group.getNumComponent(); k++) {
					org.cellocad.v2.results.placing.placement.Component component = group.getComponentAtIdx(k);
					String seq = "";
					for (int l = 0; l < component.getNumPart(); l++) {
						String name = component.getPartAtIdx(l);
						DNAComponent c = this.getDNAComponentByName(name);
						seq += SBOLDataUtils.getDNASequence(c);
					}

					// Component
					String cDisplayId = component.getName() + "_Component";
					URI cDefinitionId = component.getUri();
					AccessType cAccess = AccessType.PUBLIC;
					org.sbolstandard.core2.Component c = cd.createComponent(cDisplayId, cAccess, cDefinitionId);

					// SequenceAnnotation
					String saDisplayId = String.format("SequenceAnnotation%d", k);
					String saLocationId = saDisplayId + "_Range";
					int start = sequence.length() + 1;
					int end = start + seq.length() - 1;
					SequenceAnnotation sa = cd.createSequenceAnnotation(saDisplayId, saLocationId, start, end);
					sa.setComponent(c.getIdentity());
					sequence += seq;

					// SequenceConstraint
					if (k != 0) {
						String scDisplayId = String.format("%s_Constraint%d", cd.getDisplayId(), k);
						RestrictionType scRestriction = RestrictionType.PRECEDES;
						URI scSubjectId = cd.getComponent(group.getComponentAtIdx(k - 1).getName() + "_Component")
								.getIdentity();
						URI scObjectId = cd.getComponent(component.getName() + "_Component").getIdentity();
						cd.createSequenceConstraint(scDisplayId, scRestriction, scSubjectId, scObjectId);
					}
				}

				// Sequence
				String sDisplayId = cd.getDisplayId() + "_Sequence";
				URI sEncoding = Sequence.IUPAC_DNA;
				Sequence s = document.createSequence(sDisplayId, sequence, sEncoding);
				cd.addSequence(s);
			}
		}
	}

	/**
	 * Add a ModuleDefinition representation of each circuit design.
	 * 
	 * @param document
	 * @throws SBOLValidationException
	 */
	protected void addDesignModules(SBOLDocument document) throws SBOLValidationException {
		Netlist netlist = this.getNetlist();
		Placements placements = netlist.getResultNetlistData().getPlacements();
		for (int i = 0; i < placements.getNumPlacement(); i++) {
			Placement placement = placements.getPlacementAtIdx(i);

			String mdDisplayId = String.format("Design%d_Module", i);
			ModuleDefinition md = document.createModuleDefinition(mdDisplayId);
			placement.setUri(md.getIdentity());

			for (int j = 0; j < placement.getNumPlacementGroup(); j++) {
				PlacementGroup group = placement.getPlacementGroupAtIdx(j);
				String fcDisplayId = String.format("Design%d_Group%d", i, j);
				AccessType fcAccess = AccessType.PUBLIC;
				URI fcDefinitionURI = group.getUri();
				DirectionType fcDirection = DirectionType.NONE;
				md.createFunctionalComponent(fcDisplayId, fcAccess, fcDefinitionURI, fcDirection);
			}

		}

	}

	/**
	 * Run the (core) algorithm
	 * 
	 * @throws CelloException
	 */
	@Override
	protected void run() throws CelloException {
		logInfo("creating SBOL document");

		// create document
		try {
			SBOLDocument sbolDocument = this.createSBOLDocument();
			this.setSbolDocument(sbolDocument);
		} catch (SynBioHubException | SBOLValidationException e) {
			throw new CelloException(e);
		}

		// add interactions
		if (this.getAddInteractions()) {
			logInfo("modeling component interactions");
			try {
				SBOLDocument sbolDocument = this.addInteractions(this.getRepositoryUrl().toString(),
						this.getSbolDocument(), this.getDesignName());
				this.setSbolDocument(sbolDocument);
			} catch (IOException | SBOLValidationException | SBOLConversionException | VPRException
					| VPRTripleStoreException | URISyntaxException e) {
				throw new CelloException(e);
			}
		}

		// plasmid and design
		if (this.getAddDesignModules()) {
			logInfo("grouping inserts");
			try {
				this.addGroupDefinitions(this.getSbolDocument());
			} catch (SBOLValidationException | SynBioHubException e) {
				throw new CelloException(e);
			}
			if (!this.getAddInteractions()) {
				logInfo("adding design modules");
				try {
					this.addDesignModules(this.getSbolDocument());
				} catch (SBOLValidationException e) {
					throw new CelloException(e);
				}
			}
		}
	}

	/**
	 * Perform postprocessing
	 */
	@Override
	protected void postprocessing() {
		logInfo("writing SBOL document");
		logDebug("SBOL filename " + getSbolFilename());
		try {
			SBOLWriter.write(this.getSbolDocument(), getSbolFilename());
		} catch (SBOLConversionException | IOException e) {
			e.printStackTrace();
		}
	}

	protected String getDesignName() {
		String rtn = null;
		rtn = this.getNetlist().getName();
		if (rtn == null) {
			rtn = "circuit";
		}
		return rtn;
	}

	/*
	 * Getter and Setter
	 */
	/**
	 * Getter for <i>repositoryUrl</i>
	 * 
	 * @return value of <i>repositoryUrl</i>
	 */
	protected String getRepositoryUrl() {
		return this.repositoryUrl;
	}

	/**
	 * Setter for <i>repositoryUrl</i>
	 * 
	 * @param repositoryUrl the value to set <i>repositoryUrl</i>
	 */
	protected void setRepositoryUrl(final String repositoryUrl) {
		this.repositoryUrl = repositoryUrl;
	}

	/**
	 * Getter for <i>collectionUri</i>
	 * 
	 * @return value of <i>collectionUri</i>
	 */
	public String getCollectionUri() {
		return collectionUri;
	}

	/**
	 * Setter for <i>collectionUri</i>
	 * 
	 * @param collectionUri the value to set <i>collectionUri</i>
	 */
	public void setCollectionUri(final String collectionUri) {
		this.collectionUri = collectionUri;
	}

	/**
	 * Getter for <i>addInteractions</i>
	 * 
	 * @return value of <i>addInteractions</i>
	 */
	protected Boolean getAddInteractions() {
		return this.addInteractions;
	}

	/**
	 * Setter for <i>addInteractions</i>
	 * 
	 * @param addInteractions the value to set <i>addInteractions</i>
	 */
	protected void setAddInteractions(final Boolean addInteractions) {
		this.addInteractions = addInteractions;
	}

	/**
	 * Getter for <i>addPlasmidModules</i>
	 * 
	 * @return value of <i>addPlasmidModules</i>
	 */
	protected Boolean getAddDesignModules() {
		return this.addDesignModules;
	}

	/**
	 * Setter for <i>addPlasmidModules</i>
	 * 
	 * @param addPlasmidModules the value to set <i>addPlasmidModules</i>
	 */
	protected void setAddDesignModules(final Boolean addPlasmidModules) {
		this.addDesignModules = addPlasmidModules;
	}

	/**
	 * Getter for <i>sbhFrontend</i>
	 * 
	 * @return value of <i>sbhFrontend</i>
	 */
	protected SynBioHubFrontend getSbhFrontend() {
		return this.sbhFrontend;
	}

	/**
	 * Setter for <i>sbhFrontend</i>
	 * 
	 * @param sbhFrontend the value to set <i>sbhFrontend</i>
	 */
	protected void setSbhFrontend(final SynBioHubFrontend sbhFrontend) {
		this.sbhFrontend = sbhFrontend;
	}

	/**
	 * Getter for <i>sbolDocument</i>
	 * 
	 * @return value of <i>sbolDocument</i>
	 */
	protected SBOLDocument getSbolDocument() {
		return sbolDocument;
	}

	/**
	 * Setter for <i>sbolDocument</i>
	 * 
	 * @param sbolDocument the value to set <i>sbolDocument</i>
	 */
	protected void setSbolDocument(final SBOLDocument sbolDocument) {
		this.sbolDocument = sbolDocument;
	}

	/**
	 * Getter for <i>sbolFilename</i>
	 * 
	 * @return value of <i>sbolFilename</i>
	 */
	protected String getSbolFilename() {
		return sbolFilename;
	}

	/**
	 * Setter for <i>sbolFilename</i>
	 * 
	 * @param sbolFilename the value to set <i>sbolFilename</i>
	 */
	protected void setSbolFilename(final String sbolFilename) {
		this.sbolFilename = sbolFilename;
	}

	/**
	 * Getter for <i>targetDataInstance</i>.
	 *
	 * @return value of targetDataInstance
	 */
	protected EXTargetDataInstance getTargetDataInstance() {
		return targetDataInstance;
	}

	/**
	 * Setter for <i>targetDataInstance</i>.
	 *
	 * @param targetDataInstance the targetDataInstance to set
	 */
	protected void setTargetDataInstance(EXTargetDataInstance targetDataInstance) {
		this.targetDataInstance = targetDataInstance;
	}

	private EXTargetDataInstance targetDataInstance;

	/**
	 * Returns the Logger for the <i>SBOL</i> algorithm
	 *
	 * @return the logger for the <i>SBOL</i> algorithm
	 */
	@Override
	protected Logger getLogger() {
		return SBOL.logger;
	}

	private String repositoryUrl;
	private String collectionUri;
	private Boolean addInteractions;
	private Boolean addDesignModules;
	private SynBioHubFrontend sbhFrontend;
	private SBOLDocument sbolDocument;
	private String sbolFilename;
	private static final Logger logger = LogManager.getLogger(SBOL.class);

}
