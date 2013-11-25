/***************************************************************
 * Copyright (c) 2012, All Rights Reserved.
 * 
 * Generation Challenge Programme (GCP)
 * 
 * @author Kevin L. Manansala
 * 
 * This software is licensed for use under the terms of the 
 * GNU General Public License (http://bit.ly/8Ztv8M) and the 
 * provisions of Part F of the Generation Challenge Programme 
 * Amended Consortium Agreement (http://bit.ly/KQX1nL)
 * 
 **************************************************************/
package org.generationcp.ibpworkbench.util;

import java.io.File;
import java.io.FileWriter;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.generationcp.commons.breedingview.xml.BreedingViewProject;
import org.generationcp.commons.breedingview.xml.BreedingViewProjectType;
import org.generationcp.commons.breedingview.xml.Data;
import org.generationcp.commons.breedingview.xml.Genotypes;
import org.generationcp.commons.breedingview.xml.Phenotypic;
import org.generationcp.commons.breedingview.xml.SSAParameters;
import org.generationcp.commons.breedingview.xml.Trait;
import org.generationcp.commons.hibernate.ManagerFactoryProvider;
import org.generationcp.commons.sea.xml.BreedingViewSession;
import org.generationcp.commons.sea.xml.DataConfiguration;
import org.generationcp.commons.sea.xml.DataFile;
import org.generationcp.commons.sea.xml.Design;
import org.generationcp.commons.sea.xml.Environments;
import org.generationcp.commons.sea.xml.Pipeline;
import org.generationcp.commons.sea.xml.Pipelines;
import org.generationcp.commons.sea.xml.Traits;
import org.generationcp.ibpworkbench.IBPWorkbenchApplication;
import org.generationcp.middleware.domain.dms.VariableType;
import org.generationcp.middleware.domain.dms.VariableTypeList;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.manager.ManagerFactory;
import org.generationcp.middleware.manager.api.StudyDataManager;
import org.generationcp.middleware.manager.api.WorkbenchDataManager;
import org.generationcp.middleware.pojos.workbench.Project;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;


@Configurable
public class BreedingViewXMLWriter implements InitializingBean, Serializable{

    private static final long serialVersionUID = 8844276834893749854L;

    private final static Logger LOG = LoggerFactory.getLogger(BreedingViewXMLWriter.class);
    
    @Autowired
    private ManagerFactoryProvider managerFactoryProvider;
    @Autowired
    private WorkbenchDataManager workbenchDataManager;
    @Value("${web.api.url}")
    private String webApiUrl;

    private BreedingViewInput breedingViewInput;
    
    private List<Integer> numericTypes;
    private List<Integer> characterTypes;
    

    public BreedingViewXMLWriter(BreedingViewInput breedingViewInput) {
        
        this.breedingViewInput = breedingViewInput;
        
        numericTypes = new ArrayList<Integer>();
        characterTypes =  new ArrayList<Integer>();
        
        numericTypes.add(TermId.NUMERIC_VARIABLE.getId());
        numericTypes.add(TermId.MIN_VALUE.getId());
        numericTypes.add(TermId.MAX_VALUE.getId());
        numericTypes.add(TermId.DATE_VARIABLE.getId());
        numericTypes.add(TermId.NUMERIC_DBID_VARIABLE.getId());
        
        characterTypes.add(TermId.CHARACTER_VARIABLE.getId());
        characterTypes.add(TermId.CHARACTER_DBID_VARIABLE.getId());
        characterTypes.add(1128);
        characterTypes.add(1130);
        
    }
    
    public void writeProjectXML() throws BreedingViewXMLWriterException{
  
    	List<Trait> traits = new ArrayList<Trait>();
        for( Entry<Integer, String> s : breedingViewInput.getVariateColumns().entrySet()){
        	 Trait trait = new Trait();
             trait.setName(s.getValue());
             trait.setActive(true);
             traits.add(trait);
        }
        
        //create Fieldbook element
        Data data = new Data();
        data.setFieldBookFile(breedingViewInput.getSourceXLSFilePath());
        
        //create the Phenotypic element
        Phenotypic phenotypic = new Phenotypic();
        phenotypic.setTraits(traits);
        phenotypic.setEnvironments(breedingViewInput.getEnvironment());
        phenotypic.setBlocks(breedingViewInput.getBlocks());
        phenotypic.setReplicates(breedingViewInput.getReplicates());
        phenotypic.setRows(breedingViewInput.getRows());
        phenotypic.setColumns(breedingViewInput.getColumns());
        phenotypic.setGenotypes(breedingViewInput.getGenotypes());
        phenotypic.setFieldbook(data);
        
        SSAParameters ssaParameters = new SSAParameters();
        ssaParameters.setWebApiUrl(webApiUrl);
        ssaParameters.setStudyId(breedingViewInput.getStudyId());
        ssaParameters.setInputDataSetId(breedingViewInput.getDatasetId());
        ssaParameters.setOutputDataSetId(breedingViewInput.getOutputDatasetId());

        Project workbenchProject = IBPWorkbenchApplication.get().getSessionData().getLastOpenedProject();
        if(workbenchProject != null) {
            ssaParameters.setWorkbenchProjectId(workbenchProject.getProjectId());
        }
        try{
            String installationDirectory = workbenchDataManager.getWorkbenchSetting().getInstallationDirectory();
            String outputDirectory = String.format("%s/workspace/%s/breeding_view/output", installationDirectory, workbenchProject.getProjectId());
            ssaParameters.setOutputDirectory(outputDirectory);
        } catch(MiddlewareQueryException ex){
            throw new BreedingViewXMLWriterException("Error with getting installation directory: " + breedingViewInput.getDatasetId()
                    + ": " + ex.getMessage(), ex);
        }

        //create the ProjectType element
        BreedingViewProjectType projectTypeElem = new BreedingViewProjectType();
        projectTypeElem.setDesign(breedingViewInput.getDesignType());
        projectTypeElem.setType(breedingViewInput.getProjectType());
        projectTypeElem.setEnvname(breedingViewInput.getEnvironmentName());
        
        //create the Breeding View project element
        BreedingViewProject project = new BreedingViewProject();
        project.setName(breedingViewInput.getBreedingViewProjectName());
        project.setVersion(breedingViewInput.getVersion());
        project.setType(projectTypeElem);
        project.setPhenotypic(phenotypic);
        project.setSsaParameters(ssaParameters);
        
        //prepare the writing of the xml
        JAXBContext context = null;
        Marshaller marshaller = null;
        try{
            context = JAXBContext.newInstance(BreedingViewProject.class);
            marshaller = context.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
        } catch(JAXBException ex){
            throw new BreedingViewXMLWriterException("Error with opening JAXB context and marshaller: "
                    + ex.getMessage(), ex);
        }
        
        //write the xml
        try{
        	
        	new File(new File(breedingViewInput.getDestXMLFilePath()).getParent()).mkdirs();
            FileWriter fileWriter = new FileWriter(breedingViewInput.getDestXMLFilePath());
            System.out.println(breedingViewInput.getDestXMLFilePath());
            marshaller.marshal(project, fileWriter);
            fileWriter.flush();
            fileWriter.close();
        } catch(Exception ex){
            throw new BreedingViewXMLWriterException("Error with writing xml to: " + breedingViewInput.getDestXMLFilePath() + ": " + ex.getMessage(), ex);
        }
    }

	public void writeProjectXMLV2() throws BreedingViewXMLWriterException {
	
		Traits traits = new Traits();
        for( Entry<Integer, String> s : breedingViewInput.getVariateColumns().entrySet()){
        	Trait trait = new Trait();
             trait.setName(s.getValue());
             trait.setActive(breedingViewInput.getVariatesActiveState().get(s.getValue()).booleanValue());
             traits.add(trait);
        }
        
        //create DataFile element
        DataFile data = new DataFile();
        data.setName(breedingViewInput.getSourceXLSFilePath());
        
        Design design = new Design();
        design.setType(breedingViewInput.getDesignType());
        design.setBlocks(breedingViewInput.getBlocks());
        design.setReplicates(breedingViewInput.getReplicates());
        design.setColumns(breedingViewInput.getColumns());
        design.setRows(breedingViewInput.getRows());
        
        Environments environments = new Environments();
        environments.setName(breedingViewInput.getEnvironment().getName());
        environments.setTrialName(breedingViewInput.getEnvironment().getName());
        
        for( Entry<String, Boolean> s : breedingViewInput.getEnvironmentsActiveState().entrySet()){
        	org.generationcp.commons.sea.xml.Environment env = new org.generationcp.commons.sea.xml.Environment();
        	env.setName(s.getKey());
        	env.setTrial(s.getKey());
        	if (s.getValue()) environments.add(env);
        }
        
        //create the DataConfiguration element
        DataConfiguration dataConfiguration = new DataConfiguration();
        dataConfiguration.setEnvironments(environments);
        dataConfiguration.setDesign(design);
        dataConfiguration.setGenotypes(breedingViewInput.getGenotypes());
        dataConfiguration.setTraits(traits);
        
        
        SSAParameters ssaParameters = new SSAParameters();
        ssaParameters.setWebApiUrl(webApiUrl);
        ssaParameters.setStudyId(breedingViewInput.getStudyId());
        ssaParameters.setInputDataSetId(breedingViewInput.getDatasetId());
        ssaParameters.setOutputDataSetId(breedingViewInput.getOutputDatasetId());

        Project workbenchProject = IBPWorkbenchApplication.get().getSessionData().getLastOpenedProject();
        if(workbenchProject != null) {
            ssaParameters.setWorkbenchProjectId(workbenchProject.getProjectId());
        }
        try{
            String installationDirectory = workbenchDataManager.getWorkbenchSetting().getInstallationDirectory();
            String outputDirectory = String.format("%s/workspace/%s/breeding_view/output", installationDirectory, workbenchProject.getProjectId());
            ssaParameters.setOutputDirectory(outputDirectory);
        } catch(MiddlewareQueryException ex){
            throw new BreedingViewXMLWriterException("Error with getting installation directory: " + breedingViewInput.getDatasetId()
                    + ": " + ex.getMessage(), ex);
        }
        
        Pipelines pipelines = new Pipelines();
        Pipeline pipeline = new Pipeline();
        pipeline.setType("SEA");
        pipeline.setDataConfiguration(dataConfiguration);
        pipelines.add(pipeline);
        
        
        //create the Breeding View project element
        org.generationcp.commons.sea.xml.BreedingViewProject project = new org.generationcp.commons.sea.xml.BreedingViewProject();
        project.setName(breedingViewInput.getBreedingViewProjectName());
        project.setVersion("1.2");
        project.setPipelines(pipelines);
        
        
        BreedingViewSession bvSession = new BreedingViewSession();
        bvSession.setBreedingViewProject(project);
        bvSession.setDataFile(data);
        bvSession.setIbws(ssaParameters);
        
        //prepare the writing of the xml
        JAXBContext context = null;
        Marshaller marshaller = null;
        try{
            context = JAXBContext.newInstance(BreedingViewSession.class, Pipelines.class, Environments.class, Pipeline.class);
            marshaller = context.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
        } catch(JAXBException ex){
            throw new BreedingViewXMLWriterException("Error with opening JAXB context and marshaller: "
                    + ex.getMessage(), ex);
        }
        
        //write the xml
        String filePath = breedingViewInput.getDestXMLFilePath() + ".new.xml";
        try{
        	
        	new File(new File(filePath).getParent()).mkdirs();
            FileWriter fileWriter = new FileWriter(filePath);
            System.out.println(filePath);
            marshaller.marshal(bvSession, fileWriter);
            fileWriter.flush();
            fileWriter.close();
        } catch(Exception ex){
            throw new BreedingViewXMLWriterException("Error with writing xml to: " + filePath + ": " + ex.getMessage(), ex);
        }
	}
	
	 @Override
	    public void afterPropertiesSet() throws Exception {
	    }
}
