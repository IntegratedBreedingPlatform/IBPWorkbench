package org.generationcp.ibpworkbench.util;

import au.com.bytecode.opencsv.CSVWriter;
import org.generationcp.commons.hibernate.ManagerFactoryProvider;
import org.generationcp.commons.util.ObjectUtil;
import org.generationcp.ibpworkbench.IBPWorkbenchApplication;
import org.generationcp.ibpworkbench.model.SeaEnvironmentModel;
import org.generationcp.middleware.domain.dms.*;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.manager.ManagerFactory;
import org.generationcp.middleware.manager.api.StudyDataManager;
import org.generationcp.middleware.manager.api.WorkbenchDataManager;
import org.generationcp.middleware.pojos.workbench.Project;
import org.generationcp.middleware.service.api.OntologyService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import javax.annotation.Resource;
import java.io.File;
import java.io.FileWriter;
import java.util.*;

@Configurable
public class DatasetExporter {

	public static final String NUMERIC_VARIABLE = "Numeric variable";
	public static final String DUMMY_REPLICATES = "_REPLICATES_";
	public static final String REGEX_VALID_BREEDING_VIEW_CHARACTERS = "[^a-zA-Z0-9-_%']+";
	
	private static final Logger LOG = LoggerFactory.getLogger(DatasetExporter.class);

	@Autowired
	private WorkbenchDataManager workbenchDataManager;
	private StudyDataManager studyDataManager;
	private Integer datasetId;
	private List<String[]> tableItems;
	private Map<String, Integer> columnsMap = new HashMap<>();
	private Map<Integer, String> variateColumnsMap = new HashMap<>();
	private Map<String, String> headerNameAliasMap = new HashMap<>();
	private int observationSheetColumnIndex;

	private static final String MISSING_VALUE_STRING = "missing";

	@Resource
	private ManagerFactoryProvider provider;

	public DatasetExporter(StudyDataManager studyDataManager, Integer studyId, Integer datasetId) {
		this.studyDataManager = studyDataManager;
		this.datasetId = datasetId;
		this.tableItems = new ArrayList<>();
	}

	private DataSet getDataSet(int dataSetId) {

		DataSet dataset;
		try {
			dataset = this.studyDataManager.getDataSet(datasetId);
			return dataset;
		} catch (MiddlewareQueryException ex) {
			LOG.error(ex.getMessage(), ex);
		}

		return null;
	}

	private void getFactorDetails(DataSet dataset) {

		List<VariableType> factorVariableTypes = dataset.getVariableTypes().getFactors()
				.getVariableTypes();

		
		for (VariableType factor : factorVariableTypes) {

			if (factor.getStandardVariable().getPhenotypicType() == PhenotypicType.DATASET){
				continue;
			}
				

			String factorName = factor.getLocalName();
			if (factorName != null) {
				factorName = factorName.trim();
			}

			// check if factor is already written as a condition
			Integer temp = columnsMap.get(factorName);
			if (temp == null && !"STUDY".equals(factorName)) {
				// add entry to columns mapping
				columnsMap.put(factorName, Integer.valueOf(observationSheetColumnIndex));
				observationSheetColumnIndex++;
			}
		}

	}

	private void getVariateDetails(DataSet dataset,
			BreedingViewInput breedingViewInput) {

		List<VariableType> variateVariableTypes = dataset.getVariableTypes().getVariates()
				.getVariableTypes();
		

		for (VariableType variate : variateVariableTypes) {

			String variateName = variate.getLocalName();

			// get only the selected traits
			if (breedingViewInput.getVariatesActiveState().get(variateName).booleanValue()) {
				// add entry to columns mapping
				columnsMap.put(variateName, Integer.valueOf(observationSheetColumnIndex));
				variateColumnsMap.put(Integer.valueOf(observationSheetColumnIndex), variateName);
				observationSheetColumnIndex++;
			}

		}
	}
	
	
	private List<String> generateRowHeader(Map<String, String> headerNameAliasMap){
		
		List<String> keys = new ArrayList<>(columnsMap.keySet());

		// Sort keys by values.
		final Map<String, Integer> langForComp = columnsMap;
		Collections.sort(keys, new Comparator<Object>() {
			public int compare(Object left, Object right) {
				String leftKey = (String) left;
				String rightKey = (String) right;

				Integer leftValue = langForComp.get(leftKey);
				Integer rightValue = langForComp.get(rightKey);
				return leftValue.compareTo(rightValue);
			}
		});

		List<String> rowHeader = new ArrayList<>();
		for (Iterator<String> i = keys.iterator(); i.hasNext();) {
			String k = i.next();
			Integer columnIndex = columnsMap.get(k).intValue();
			if (columnIndex >= 0) {
				String nameSanitized = k.replaceAll(REGEX_VALID_BREEDING_VIEW_CHARACTERS, "_");
				rowHeader.add(nameSanitized);
				headerNameAliasMap.put(nameSanitized, k);
			}
		}
		
		return rowHeader;
		
	}

	public void exportToCSVForBreedingView(String filename, String selectedFactor,
			List<String> selectedEnvironment, BreedingViewInput breedingViewInput)
			throws DatasetExporterException {

		OntologyService ontologyService = retrieveCurrentOntologyService();
		
		Map<Integer, String> selectEnvironmentsMap = new HashMap<>();
		if (!selectedFactor.equalsIgnoreCase(breedingViewInput.getTrialInstanceName())){
			for (SeaEnvironmentModel model : breedingViewInput.getSelectedEnvironments()) {
				selectEnvironmentsMap.put(model.getLocationId(), model.getEnvironmentName());
			}
		}
		
		DataSet dataset = getDataSet(datasetId);

		if (dataset == null){
			return;
		}
			
		observationSheetColumnIndex = 0;
		
		getFactorDetails(dataset);
		getVariateDetails(dataset, breedingViewInput);


		if (DUMMY_REPLICATES.equals(breedingViewInput.getReplicates().getName())) {
			columnsMap.put(DUMMY_REPLICATES, Integer.valueOf(observationSheetColumnIndex));
			observationSheetColumnIndex++;
		}
		
		boolean trialEnvironmentFactorAlreadyExists = true;
		if (!selectedFactor.equalsIgnoreCase(breedingViewInput.getTrialInstanceName())
				&& !columnsMap.containsKey(selectedFactor)){
			columnsMap.put(selectedFactor, Integer.valueOf(observationSheetColumnIndex));
			observationSheetColumnIndex++;
			trialEnvironmentFactorAlreadyExists=false;
		}

		List<Experiment> experiments = new ArrayList<>();

		try {
			experiments = this.studyDataManager
					.getExperiments(this.datasetId, 0, Integer.MAX_VALUE);
		} catch (Exception ex) {
			LOG.error(ex.getMessage(), ex);
		}

		
		List<String> rowHeader = generateRowHeader(headerNameAliasMap);
		getTableItems().add(rowHeader.toArray(new String[0]));

		for (Experiment experiment : experiments) {

			
			boolean outerBreak = true;
			for (Variable factorVariables1 : experiment.getFactors().getVariables()) {
				if (factorVariables1.getVariableType().getLocalName().trim()
						.equalsIgnoreCase(breedingViewInput.getTrialInstanceName())
						&& selectedEnvironment.contains(factorVariables1.getValue())) {
					outerBreak = false;
				}
			}
			if (outerBreak){
				continue;
			}
				

			List<String> row = new ArrayList<>();

			processFactors(row, experiment, breedingViewInput);

			processVariates(row, experiment, breedingViewInput, ontologyService);

			if (breedingViewInput.getReplicates().getName().equals(DUMMY_REPLICATES)) {
				row.add("1");
			}
			
			if (!trialEnvironmentFactorAlreadyExists){
				row.add(selectEnvironmentsMap.get(experiment.getLocationId()).trim().replace(",", ";"));
			}

			getTableItems().add(row.toArray(new String[0]));
		}

		serializeHeaderAliasMap();

		writeCSVFile(filename);

	}

	protected void serializeHeaderAliasMap() {
		try {
			String tempFolder = String.format("%s\\temp", getWorkbenchDataManager()
					.getWorkbenchSetting().getInstallationDirectory());
			new File(tempFolder).mkdir();
			String fileName = tempFolder + "\\mapping.ser";
			new ObjectUtil<Map<String, String>>().serializeObject(headerNameAliasMap, fileName);
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
		}
	}

	protected void writeCSVFile(String filename) {
		try {
			File csvFile = new File(filename);
			CSVWriter csvWriter = new CSVWriter(new FileWriter(csvFile),
					CSVWriter.DEFAULT_SEPARATOR, CSVWriter.NO_QUOTE_CHARACTER, "\r\n");
			csvWriter.writeAll(getTableItems());
			csvWriter.flush();
			csvWriter.close();
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
		}
	}

	protected void processFactors(List<String> rowContent, Experiment currentExperiment,
			BreedingViewInput breedingViewInput) {
		List<Variable> factorsOfExperiments = currentExperiment.getFactors().getVariables();
		Map<String, Variable> factorsOfExperimentsMap = new HashMap<>();
		for (Variable factorVariable : factorsOfExperiments) {
			factorsOfExperimentsMap.put(factorVariable.getVariableType().getLocalName(),
					factorVariable);
		}

		for (Variable factorVariable : factorsOfExperiments) {
			String factorName = factorVariable.getVariableType().getLocalName();
			if (factorName != null) {
				factorName = factorName.trim();
			}
			Integer columnIndexInteger = columnsMap.get(factorName);
			if (columnIndexInteger != null) {
				short columnIndex = columnIndexInteger.shortValue();
				if (columnIndex >= 0) {

					if (factorVariable.getVariableType().getStandardVariable().getDataType()
							.getName().equals(NUMERIC_VARIABLE)) {
						double elemValue = 0;
						if (factorVariable.getValue() != null) {


								if (factorVariable.getValue().isEmpty()
										&& factorVariable.getVariableType().getLocalName().equalsIgnoreCase(
												breedingViewInput.getReplicates().getName())) {
									Variable variable = factorsOfExperimentsMap
											.get(breedingViewInput.getBlocks()
													.getName());
									if (variable != null) {
										rowContent.add(variable.getValue().trim());
									} else {
										rowContent.add("");
									}

								} else {
									try {
										elemValue = Double.valueOf(factorVariable.getValue());

										if (elemValue == Double.valueOf("-1E+36")) {
											rowContent.add("");
										} else {
											rowContent
													.add(String.valueOf(factorVariable.getValue()));
										}
									} catch (NumberFormatException ex) {
										String value = factorVariable.getValue();
										if (value != null) {
											value = value.trim();
										}
										rowContent.add(value);
									}
								}


						} else {
							String nullValue = null;
							rowContent.add(nullValue);
						}
					} else {
						String value = factorVariable.getValue();
						if (value != null) {
							if (factorVariable.getVariableType().getStandardVariable()
									.getPhenotypicType() == PhenotypicType.TRIAL_ENVIRONMENT
									|| factorVariable.getVariableType().getStandardVariable()
									.getPhenotypicType() == PhenotypicType.GERMPLASM) {
								value = value.trim().replace(",", ";");

								if (value.isEmpty()
										&& factorVariable
										.getVariableType()
										.getLocalName()
										.equalsIgnoreCase(
												breedingViewInput
														.getReplicates()
														.getName())) {
									Variable variable = factorsOfExperimentsMap
											.get(breedingViewInput.getBlocks()
													.getName());
									if (variable != null) {
										value = variable.getValue().trim();
									} else {
										value = "";
									}

								}

							} else {
								value = value.trim();
							}

						}
						rowContent.add(value);
					}
				}
			}
		}
	}

	protected void processVariates(List<String> rowContent, Experiment currentExperiment, BreedingViewInput breedingViewInput, OntologyService ontologyService) {
		List<Integer> variatekeys = new ArrayList<>(variateColumnsMap.keySet());
		Collections.sort(variatekeys, new Comparator<Object>() {
			public int compare(Object left, Object right) {
				Integer leftKey = (Integer) left;
				Integer rightKey = (Integer) right;

				return leftKey.compareTo(rightKey);
			}
		});

		for (Object key : variatekeys) {
			String variateName = "";
			Variable variateVariable = currentExperiment.getVariates().findByLocalName(
					variateColumnsMap.get(key));
			if (variateVariable != null) {
				variateName = variateVariable.getVariableType().getLocalName();

				Integer columnIndexInteger = columnsMap.get(variateName);
				if (columnIndexInteger != null) {
					short columnIndex = columnIndexInteger.shortValue();
					if (columnIndex >= 0) {

						if (variateVariable.getVariableType().getStandardVariable().getDataType()
								.getName().equals(NUMERIC_VARIABLE)) {
							double elemValue = 0;
							if (variateVariable.getValue() != null) {
								try {
									elemValue = Double.valueOf(variateVariable.getValue());

									if (elemValue == Double.valueOf("-1E+36")) {
										rowContent.add("");
									} else {
										rowContent.add(String.valueOf(elemValue));
									}

								} catch (NumberFormatException ex) {
									String value = variateVariable.getValue();
									if (value != null) {
										value = value.trim();
									}
									rowContent.add(value);
								}
							} else {

								rowContent.add("");
							}
						} else if (variateVariable.getVariableType().getStandardVariable().
								getDataType().getId() == TermId.CATEGORICAL_VARIABLE.getId()) {
							String value = variateVariable.getActualValue();

							if (value == null) {
								value = "";
							} else {
								try {
									List<ValueReference> possibleValues = ontologyService
											.getDistinctStandardVariableValues(
													variateVariable.getVariableType()
															.getStandardVariable().getId());
									if (MISSING_VALUE_STRING.equals(value)
											&& isCategoricalValueOutOfBounds(value,
											possibleValues)) {
										value = "";
									}
								} catch (MiddlewareQueryException e) {
									e.printStackTrace();
								}
							}

							rowContent.add(value);
						} else {
							String value = variateVariable.getActualValue();
							if (value != null) {
								value = value.trim();
							} else {
								value = "";
							}
							rowContent.add(value);
						}
					}
				}

			} else {
				rowContent.add("");
			}
		}
	}

	protected boolean isCategoricalValueOutOfBounds(String value, List<ValueReference> possibleValues) {

		for (ValueReference ref : possibleValues) {
			if (ref.getName().equals(value) || ref.getKey().equals(value)) {
				return false;
			}
		}

		return true;
	}

	public List<String[]> getTableItems() {
		return tableItems;
	}
	
	public Map<String,String> getHeaderNameAliasMap() {
		return headerNameAliasMap;
	}

	public WorkbenchDataManager getWorkbenchDataManager() {
		return workbenchDataManager;
	}

	public void setWorkbenchDataManager(WorkbenchDataManager workbenchDataManager) {
		this.workbenchDataManager = workbenchDataManager;
	}

	protected OntologyService retrieveCurrentOntologyService() {
		IBPWorkbenchApplication app = IBPWorkbenchApplication.get();

		Project currentProject = app.getSessionData().getSelectedProject();
		if (currentProject == null) {
			currentProject = app.getSessionData().getLastOpenedProject();
		}

		ManagerFactory factory = provider.getManagerFactoryForProject(currentProject);
		return factory.getOntologyService();
	}

}