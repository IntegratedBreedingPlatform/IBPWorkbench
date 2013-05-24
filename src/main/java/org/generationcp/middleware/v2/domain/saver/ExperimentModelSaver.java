package org.generationcp.middleware.v2.domain.saver;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.manager.Database;
import org.generationcp.middleware.v2.domain.ExperimentType;
import org.generationcp.middleware.v2.domain.StandardVariable;
import org.generationcp.middleware.v2.domain.StudyValues;
import org.generationcp.middleware.v2.domain.TermId;
import org.generationcp.middleware.v2.domain.Values;
import org.generationcp.middleware.v2.domain.Variable;
import org.generationcp.middleware.v2.domain.VariableList;
import org.generationcp.middleware.v2.pojos.ExperimentModel;
import org.generationcp.middleware.v2.pojos.ExperimentPhenotype;
import org.generationcp.middleware.v2.pojos.ExperimentProject;
import org.generationcp.middleware.v2.pojos.ExperimentProperty;
import org.generationcp.middleware.v2.pojos.ExperimentStock;
import org.generationcp.middleware.v2.pojos.Phenotype;

public class ExperimentModelSaver extends Saver {

	public ExperimentModelSaver(
			HibernateSessionProvider sessionProviderForLocal,
			HibernateSessionProvider sessionProviderForCentral) {
		super(sessionProviderForLocal, sessionProviderForCentral);
	}
	
	public void addExperiment(int projectId, StudyValues values) throws MiddlewareQueryException {
		addExperiment(projectId, null, values);
	}
	
	public void addExperiment(int projectId, ExperimentType experimentType, Values values) throws MiddlewareQueryException {
		setWorkingDatabase(Database.LOCAL);
		TermId myExperimentType = null;
		if (values instanceof StudyValues) {
			myExperimentType = TermId.STUDY_EXPERIMENT;
		} else {
			myExperimentType = mapExperimentType(experimentType);
		}
		ExperimentModel experimentModel = create(projectId, values, myExperimentType);
		getExperimentDao().save(experimentModel);

		addExperimentProject(experimentModel, projectId);
		getPhenotypeSaver().savePhenotypes(experimentModel, values.getVariableList());
		
		//dataset projectprop values are already saved during addDataSet
		if (values instanceof StudyValues) {
			getProjectPropertySaver().saveProjectPropValues(projectId, values.getVariableList());
		}
	}
	
	private TermId mapExperimentType(ExperimentType experimentType) {
		switch (experimentType) {
		case PLOT: return TermId.PLOT_EXPERIMENT;
		case AVERAGE: return TermId.AVERAGE_EXPERIMENT;
		case SUMMARY: return TermId.SUMMARY_EXPERIMENT;
		case SAMPLE: return TermId.SAMPLE_EXPERIMENT;
		}
		return null;
	}

	private ExperimentModel create(int projectId, Values values, TermId expType) throws MiddlewareQueryException {
		ExperimentModel experimentModel = new ExperimentModel();
		
		experimentModel.setNdExperimentId(getExperimentDao().getNegativeId("ndExperimentId"));
		experimentModel.setTypeId(expType.getId());
		experimentModel.setProperties(createProperties(experimentModel, values.getVariableList()));
		//TODO: what if values.locationId is null?
		if (values.getLocationId() != null) {
			experimentModel.setGeoLocation(getGeolocationDao().getById(values.getLocationId())); 
		}
		if (values.getGermplasmId() != null) {
			experimentModel.setExperimentStocks(new ArrayList<ExperimentStock>());
			experimentModel.getExperimentStocks().add(createExperimentStock(experimentModel.getNdExperimentId(), values.getGermplasmId()));
		}
		return experimentModel;
	}

	private Set<ExperimentProperty> createProperties(ExperimentModel experimentModel, VariableList factors) throws MiddlewareQueryException {
		if (factors != null && factors.getVariables() != null && factors.getVariables().size() > 0) {
			for (Variable variable : factors.getVariables()) {
				if (TermId.TRIAL_DESIGN_INFO_STORAGE.getId() == variable.getVariableType().getStandardVariable().getStoredIn().getId()) {
					addProperty(experimentModel, variable);
				}
			}
		}
		
		return experimentModel.getProperties();
	}
	
	private void addProperty(ExperimentModel experimentModel, Variable variable) throws MiddlewareQueryException {
		if (experimentModel.getProperties() == null) {
			experimentModel.setProperties(new HashSet<ExperimentProperty>());
		}
		ExperimentProperty property = new ExperimentProperty();
		
		property.setNdExperimentpropId(getExperimentPropertyDao().getNegativeId("ndExperimentpropId"));
		property.setExperiment(experimentModel);
		property.setTypeId(variable.getVariableType().getId());
		property.setValue(variable.getValue());
		property.setRank(variable.getVariableType().getRank());
		
		experimentModel.getProperties().add(property);
	}
	
	private void addExperimentProject(ExperimentModel experimentModel, int projectId) throws MiddlewareQueryException {
		ExperimentProject exproj = new ExperimentProject();
		
		exproj.setExperimentProjectId(getExperimentProjectDao().getNegativeId("experimentProjectId"));
		exproj.setProjectId(projectId);
		exproj.setExperiment(experimentModel);
		
		getExperimentProjectDao().save(exproj);
	}
	
	private ExperimentStock createExperimentStock(int experimentModelId, int stockId) throws MiddlewareQueryException {
		ExperimentStock experimentStock = new ExperimentStock();
		experimentStock.setExperimentStockId(getExperimentStockDao().getNegativeId("experimentStockId"));
		experimentStock.setTypeId(TermId.IBDB_STRUCTURE.getId());
		experimentStock.setStockId(stockId);
		experimentStock.setExperimentId(experimentModelId);
		
		return experimentStock;
	}

	public void setExperimentValue(int experimentId, int variableId, Object value) throws MiddlewareQueryException {
		if (this.setWorkingDatabase(experimentId)) {
		    ExperimentModel experiment = getExperimentDao().getById(experimentId);
		    StandardVariable stdVariable = getStandardVariableBuilder().create(variableId);
		    if (experiment != null && stdVariable != null) {
	
		    	if (stdVariable.getStoredIn().getId() == TermId.TRIAL_DESIGN_INFO_STORAGE.getId()) {
		    		setExperimentValue(experiment, stdVariable, value);
		    	}
		    	else if (stdVariable.getStoredIn().getId() == TermId.OBSERVATION_VARIATE.getId()) {
		    		setObservationVariateValue(experiment, stdVariable, value);
		    	}
		    	else if (stdVariable.getStoredIn().getId() == TermId.CATEGORICAL_VARIATE.getId()) {
		    		setCategoricalVariateValue(experiment, stdVariable, value);
		    	}
		    }
		}
	}

	private void setObservationVariateValue(ExperimentModel experiment, StandardVariable stdVariable, Object value) throws MiddlewareQueryException {
		Phenotype phenotype = findPhenotype(experiment, stdVariable);
		if (phenotype != null) {
			phenotype.setValue(value == null ? null : value.toString());
			getPhenotypeDao().update(phenotype);
		}
		else {
			addNewPhenotype(experiment, stdVariable, value);
		}
	}
	
	private void setCategoricalVariateValue(ExperimentModel experiment, StandardVariable stdVariable, Object value) throws MiddlewareQueryException {
		Phenotype phenotype = findPhenotype(experiment, stdVariable);
		if (phenotype != null) {
			phenotype.setcValue(value == null ? null : new Integer(value.toString()));
			getPhenotypeDao().update(phenotype);
		}
		else {
			addNewCPhenotype(experiment, stdVariable, value);
		}
	}
	
	private Phenotype findPhenotype(ExperimentModel experiment, StandardVariable variable) {
		List<Phenotype> phenotypes = experiment.getPhenotypes();
		if (phenotypes != null) {
			for (Phenotype phenotype : phenotypes) {
				if (phenotype.getObservableId() == variable.getId()) {
					return phenotype;
				}
			}
		}
		return null;
	}
	
	private void addNewPhenotype(ExperimentModel experimentModel, StandardVariable stdVariable, Object value) throws MiddlewareQueryException {
		if (experimentModel.getPhenotypes() == null) {
			experimentModel.setPhenotypes(new ArrayList<Phenotype>());
		}
		
		Phenotype phenotype = new Phenotype();
		phenotype.setPhenotypeId(getPhenotypeDao().getNegativeId("phenotypeId"));
		phenotype.setValue(value == null ? null : value.toString());
		phenotype.setObservableId(stdVariable.getId());
		phenotype.setUniqueName(phenotype.getPhenotypeId().toString());
		phenotype.setName(String.valueOf(stdVariable.getId()));
		getPhenotypeDao().save(phenotype);
		
        ExperimentPhenotype experimentPhenotype = new ExperimentPhenotype();
		experimentPhenotype.setExperimentPhenotypeId(getExperimentPhenotypeDao().getNegativeId("experimentPhenotypeId"));
		experimentPhenotype.setExperiment(experimentModel.getNdExperimentId());
		experimentPhenotype.setPhenotype(phenotype.getPhenotypeId());
		getExperimentPhenotypeDao().save(experimentPhenotype);
		
		this.getExperimentDao().refresh(experimentModel);
	}
	
	private void addNewCPhenotype(ExperimentModel experimentModel, StandardVariable stdVariable, Object value) throws MiddlewareQueryException {
		if (experimentModel.getPhenotypes() == null) {
			experimentModel.setPhenotypes(new ArrayList<Phenotype>());
		}
		
		Phenotype phenotype = new Phenotype();
		phenotype.setPhenotypeId(getPhenotypeDao().getNegativeId("phenotypeId"));
		phenotype.setcValue(value == null ? null : new Integer(value.toString()));
		phenotype.setObservableId(stdVariable.getId());
		phenotype.setUniqueName(phenotype.getPhenotypeId().toString());
		phenotype.setName(String.valueOf(stdVariable.getId()));
		getPhenotypeDao().save(phenotype);
		
        ExperimentPhenotype experimentPhenotype = new ExperimentPhenotype();
		experimentPhenotype.setExperimentPhenotypeId(getExperimentPhenotypeDao().getNegativeId("experimentPhenotypeId"));
		experimentPhenotype.setExperiment(experimentModel.getNdExperimentId());
		experimentPhenotype.setPhenotype(phenotype.getPhenotypeId());
		getExperimentPhenotypeDao().save(experimentPhenotype);
		
		this.getExperimentDao().refresh(experimentModel);
	}

	private void setExperimentValue(ExperimentModel experiment, StandardVariable variable, Object value) throws MiddlewareQueryException {
		ExperimentProperty property = findProperty(experiment, variable);
		if (property != null) {
			property.setValue(value == null ? null : value.toString());
			getExperimentPropertyDao().update(property);
		}
		else {
			addNewProperty(experiment, variable, value);
		}
	}

	private ExperimentProperty findProperty(ExperimentModel experiment, StandardVariable variable) {
		Set<ExperimentProperty> experimentProperties = experiment.getProperties();
		if (experimentProperties != null) {
			for (ExperimentProperty property : experimentProperties) {
				if (property.getTypeId() == variable.getId()) {
					return property;
				}
			}
		}
		return null;
	}
	
	private void addNewProperty(ExperimentModel experimentModel, StandardVariable stdVariable, Object value) throws MiddlewareQueryException {
		if (experimentModel.getProperties() == null) {
			experimentModel.setProperties(new HashSet<ExperimentProperty>());
		}
		ExperimentProperty property = new ExperimentProperty();
		
		property.setNdExperimentpropId(getExperimentPropertyDao().getNegativeId("ndExperimentpropId"));
		property.setExperiment(experimentModel);
		property.setTypeId(stdVariable.getId());
		property.setValue(value == null ? null : value.toString());
	    property.setRank(0);
				
		experimentModel.getProperties().add(property);
		getExperimentPropertyDao().save(property);
	}
}
