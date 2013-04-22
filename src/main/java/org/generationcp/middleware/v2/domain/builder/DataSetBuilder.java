package org.generationcp.middleware.v2.domain.builder;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.v2.domain.DataSet;
import org.generationcp.middleware.v2.domain.Experiment;
import org.generationcp.middleware.v2.domain.Study;
import org.generationcp.middleware.v2.domain.VariableInfo;
import org.generationcp.middleware.v2.domain.VariableType;
import org.generationcp.middleware.v2.pojos.DmsProject;

public class DataSetBuilder extends Builder {

	public DataSetBuilder(HibernateSessionProvider sessionProviderForLocal,
			                 HibernateSessionProvider sessionProviderForCentral) {
		super(sessionProviderForLocal, sessionProviderForCentral);
	}

	public DataSet build(int dataSetId) throws MiddlewareQueryException {
		DataSet dataSet = null;
		if (setWorkingDatabase(dataSetId)) {
			DmsProject project = getDmsProjectDao().getById(dataSetId);
			if (project != null) {
				dataSet = createDataSet(project);
			}
		}
		return dataSet;
	}

	private DataSet createDataSet(DmsProject project) throws MiddlewareQueryException {
		DataSet dataSet = new DataSet();
		dataSet.setId(project.getProjectId());
		dataSet.setName(project.getName());
		dataSet.setDescription(project.getDescription());
		dataSet.setStudy(getStudy(project));
		dataSet.setVariableTypes(getVariableTypes(dataSet.getStudy(), project));
		dataSet.setExperiments(createExperiments(project));
		return dataSet;
	}

	private Set<VariableType> getVariableTypes(Study study, DmsProject project) throws MiddlewareQueryException {
		Set<VariableType> variableTypes = new HashSet<VariableType>();
		variableTypes.addAll(study.getVariableTypes());
		
		Set<VariableInfo> variableInfoList = getVariableInfoBuilder().create(project.getProperties());
		for (VariableInfo variableInfo : variableInfoList) {
			variableTypes.add(getVariableTypeBuilder().create(variableInfo));
		}
		
		return variableTypes;
	}

	private Study getStudy(DmsProject dataSet) throws MiddlewareQueryException {
		int studyId = getStudyId(dataSet);
		return getStudyBuilder().createStudyWithoutDataSets(studyId);
	}

	private int getStudyId(DmsProject project) {
		DmsProject study = project.getRelatedTos().get(0).getObjectProject();
		return study.getProjectId();
	}
	
	private List<Experiment> createExperiments(DmsProject project) {
		return getExperimentBuilder().create(project.getExperimentModels());
	}
	
}
