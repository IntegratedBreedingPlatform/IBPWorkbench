/*******************************************************************************
 * Copyright (c) 2012, All Rights Reserved.
 *
 * Generation Challenge Programme (GCP)
 *
 *
 * This software is licensed for use under the terms of the GNU General Public License (http://bit.ly/8Ztv8M) and the provisions of Part F
 * of the Generation Challenge Programme Amended Consortium Agreement (http://bit.ly/KQX1nL)
 *
 *******************************************************************************/

package org.generationcp.ibpworkbench.util;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import org.generationcp.commons.breedingview.xml.Blocks;
import org.generationcp.commons.breedingview.xml.Columns;
import org.generationcp.commons.breedingview.xml.Environment;
import org.generationcp.commons.breedingview.xml.Genotypes;
import org.generationcp.commons.breedingview.xml.Plot;
import org.generationcp.commons.breedingview.xml.Replicates;
import org.generationcp.commons.breedingview.xml.Rows;
import org.generationcp.ibpworkbench.model.SeaEnvironmentModel;
import org.generationcp.middleware.pojos.workbench.Project;

/**
 *
 * <br>
 * <br>
 *
 * <b>Author</b>: Jeffrey Morales <br>
 * <b>File Created</b>:
 */
public class BreedingViewInput implements Serializable {

	private static final long serialVersionUID = 7669967119863861617L;

	private final static String BV_INVALID_CHARACTER_EXPRESSION = "[\\\\/:*?|<>()\"']";

	private Project project;
	private String breedingViewProjectName;
	private String breedingViewAnalysisName;
	private Integer studyId;
	private Integer inputDatasetId;
	private Integer outputDatasetId;
	private String environmentName;
	private String trialInstanceName;
	private String version;
	private String sourceXLSFilePath;
	private String destXMLFilePath;
	private String projectType;
	private String designType;
	private Blocks blocks;
	private Replicates replicates;
	private Genotypes genotypes;
	private Rows rows;
	private Columns columns;
	private Plot plot;
	private Environment environment;
	private Map<Integer, String> variateColumns;
	private String datasetName;
	private String datasetSource;
	private Map<String, Boolean> variatesActiveState;
	private List<SeaEnvironmentModel> selectedEnvironments;

	public BreedingViewInput() {

	}

	public Map<Integer, String> getVariateColumns() {
		return this.variateColumns;
	}

	public void setVariateColumns(Map<Integer, String> variateColumns) {
		this.variateColumns = variateColumns;
	}

	public Project getProject() {
		return this.project;
	}

	public void setProject(Project project) {
		this.project = project;
	}

	public String getBreedingViewProjectName() {
		return this.breedingViewProjectName;
	}

	public void setBreedingViewProjectName(String breedingViewProjectName) {
		this.breedingViewProjectName = breedingViewProjectName;
	}

	public Integer getStudyId() {
		return this.studyId;
	}

	public void setStudyId(Integer studyId) {
		this.studyId = studyId;
	}

	public Integer getDatasetId() {
		return this.inputDatasetId;
	}

	public void setDatasetId(Integer datasetId) {
		this.inputDatasetId = datasetId;
	}

	public String getEnvironmentName() {
		return this.environmentName;
	}

	public void setEnvironmentName(String environmentName) {
		this.environmentName = environmentName;
	}

	public String getVersion() {
		return this.version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getSourceXLSFilePath() {
		return this.sourceXLSFilePath;
	}

	public void setSourceXLSFilePath(String sourceXLSFilePath) {
		this.sourceXLSFilePath = sourceXLSFilePath;
	}

	public String getDestXMLFilePath() {
		return this.destXMLFilePath;
	}

	public void setDestXMLFilePath(String destXMLFilePath) {
		this.destXMLFilePath = destXMLFilePath;
	}

	public String getProjectType() {
		return this.projectType;
	}

	public void setProjectType(String projectType) {
		this.projectType = projectType;
	}

	public String getDesignType() {
		return this.designType;
	}

	public void setDesignType(String designType) {
		this.designType = designType;
	}

	public Blocks getBlocks() {
		return this.blocks;
	}

	public void setBlocks(Blocks blocks) {
		this.blocks = blocks;
	}

	public Replicates getReplicates() {
		return this.replicates;
	}

	public void setReplicates(Replicates replicates) {
		this.replicates = replicates;
	}

	public Genotypes getGenotypes() {
		return this.genotypes;
	}

	public void setGenotypes(Genotypes genotypes) {
		this.genotypes = genotypes;
	}

	public Rows getRows() {
		return this.rows;
	}

	public void setRows(Rows rows) {
		this.rows = rows;
	}

	public Columns getColumns() {
		return this.columns;
	}

	public void setColumns(Columns columns) {
		this.columns = columns;
	}

	public Environment getEnvironment() {
		return this.environment;
	}

	public void setEnvironment(Environment environment) {
		this.environment = environment;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (this.breedingViewProjectName == null ? 0 : this.breedingViewProjectName.hashCode());
		result = prime * result + (this.blocks == null ? 0 : this.blocks.hashCode());
		result = prime * result + (this.inputDatasetId == null ? 0 : this.inputDatasetId.hashCode());
		result = prime * result + (this.designType == null ? 0 : this.designType.hashCode());
		result = prime * result + (this.destXMLFilePath == null ? 0 : this.destXMLFilePath.hashCode());
		result = prime * result + (this.environmentName == null ? 0 : this.environmentName.hashCode());
		result = prime * result + (this.project == null ? 0 : this.project.hashCode());
		result = prime * result + (this.projectType == null ? 0 : this.projectType.hashCode());
		result = prime * result + (this.replicates == null ? 0 : this.replicates.hashCode());
		result = prime * result + (this.sourceXLSFilePath == null ? 0 : this.sourceXLSFilePath.hashCode());
		result = prime * result + (this.version == null ? 0 : this.version.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (this.getClass() != obj.getClass()) {
			return false;
		}
		BreedingViewInput other = (BreedingViewInput) obj;
		if (this.breedingViewProjectName == null) {
			if (other.breedingViewProjectName != null) {
				return false;
			}
		} else if (!this.breedingViewProjectName.equals(other.breedingViewProjectName)) {
			return false;
		}
		if (this.blocks == null) {
			if (other.blocks != null) {
				return false;
			}
		} else if (!this.blocks.equals(other.blocks)) {
			return false;
		}
		if (this.inputDatasetId == null) {
			if (other.inputDatasetId != null) {
				return false;
			}
		} else if (!this.inputDatasetId.equals(other.inputDatasetId)) {
			return false;
		}
		if (this.designType == null) {
			if (other.designType != null) {
				return false;
			}
		} else if (!this.designType.equals(other.designType)) {
			return false;
		}
		if (this.destXMLFilePath == null) {
			if (other.destXMLFilePath != null) {
				return false;
			}
		} else if (!this.destXMLFilePath.equals(other.destXMLFilePath)) {
			return false;
		}
		if (this.environmentName == null) {
			if (other.environmentName != null) {
				return false;
			}
		} else if (!this.environmentName.equals(other.environmentName)) {
			return false;
		}
		if (this.project == null) {
			if (other.project != null) {
				return false;
			}
		} else if (!this.project.equals(other.project)) {
			return false;
		}
		if (this.projectType == null) {
			if (other.projectType != null) {
				return false;
			}
		} else if (!this.projectType.equals(other.projectType)) {
			return false;
		}
		if (this.replicates == null) {
			if (other.replicates != null) {
				return false;
			}
		} else if (!this.replicates.equals(other.replicates)) {
			return false;
		}
		if (this.sourceXLSFilePath == null) {
			if (other.sourceXLSFilePath != null) {
				return false;
			}
		} else if (!this.sourceXLSFilePath.equals(other.sourceXLSFilePath)) {
			return false;
		}
		if (this.version == null) {
			if (other.version != null) {
				return false;
			}
		} else if (!this.version.equals(other.version)) {
			return false;
		}

		return true;
	}

	@Override
	public String toString() {
		return "BreedingViewInput [project=" + this.project + ", BreedingViewProjectName=" + this.breedingViewProjectName + ", datasetId="
				+ this.inputDatasetId + ", environmentName=" + this.environmentName + ", version=" + this.version + ", sourceXLSFilePath="
				+ this.sourceXLSFilePath + ", destXMLFilePath=" + this.destXMLFilePath + ", projectType=" + this.projectType
				+ ", designType=" + this.designType + ", blocks=" + this.blocks + ", replicates=" + this.replicates + "]";
	}

	/**
	 * Used to normalize breeding view input so that it is acceptable for the BV application.
	 *
	 * So far, this method replaces invalid characters stemming from the project name
	 */
	public void normalizeBreedingViewInput() {
		this.breedingViewAnalysisName = this.breedingViewAnalysisName.replaceAll(BV_INVALID_CHARACTER_EXPRESSION, "_");
		this.breedingViewProjectName = this.breedingViewProjectName.replaceAll(BV_INVALID_CHARACTER_EXPRESSION, "_");

        this.sourceXLSFilePath = normalizeBVFilePath(this.sourceXLSFilePath);
        this.destXMLFilePath = normalizeBVFilePath(this.destXMLFilePath);
	}

    String normalizeBVFilePath(String filePath) {
        int index = filePath.lastIndexOf("\\");
        String forNormalization = filePath.substring(index + 1, filePath.length());
        forNormalization = forNormalization.replaceAll(BV_INVALID_CHARACTER_EXPRESSION, "_");
        // we use index + 1 here so that the file separator character is included in the resulting substring
        String output = filePath.substring(0, index + 1) + forNormalization;
        return output;
    }

	public Integer getOutputDatasetId() {
		return this.outputDatasetId;
	}

	public void setOutputDatasetId(Integer outputDatasetId) {
		this.outputDatasetId = outputDatasetId;
	}

	public String getDatasetName() {
		return this.datasetName;
	}

	public void setDatasetName(String datasetName) {
		this.datasetName = datasetName;
	}

	public String getDatasetSource() {
		return this.datasetSource;
	}

	public void setDatasetSource(String datasetSource) {
		this.datasetSource = datasetSource;
	}

	public Map<String, Boolean> getVariatesActiveState() {
		return this.variatesActiveState;
	}

	public void setVariatesActiveState(Map<String, Boolean> variateActiveStates) {
		this.variatesActiveState = variateActiveStates;
	}

	public String getBreedingViewAnalysisName() {
		return this.breedingViewAnalysisName;
	}

	public void setBreedingViewAnalysisName(String breedingViewAnalysisName) {
		this.breedingViewAnalysisName = breedingViewAnalysisName;
	}

	public String getTrialInstanceName() {
		return this.trialInstanceName;
	}

	public void setTrialInstanceName(String trialInstanceName) {
		this.trialInstanceName = trialInstanceName;
	}

	public List<SeaEnvironmentModel> getSelectedEnvironments() {
		return this.selectedEnvironments;
	}

	public void setSelectedEnvironments(List<SeaEnvironmentModel> selectedEnvironments) {
		this.selectedEnvironments = selectedEnvironments;
	}

	public Plot getPlot() {
		return this.plot;
	}

	public void setPlot(Plot plot) {
		this.plot = plot;
	}

}
