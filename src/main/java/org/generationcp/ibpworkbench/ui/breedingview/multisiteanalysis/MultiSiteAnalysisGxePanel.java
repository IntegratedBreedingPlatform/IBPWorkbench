/*******************************************************************************
 * Copyright (c) 2012, All Rights Reserved.
 * 
 * Generation Challenge Programme (GCP)
 * 
 * 
 * This software is licensed for use under the terms of the GNU General Public
 * License (http://bit.ly/8Ztv8M) and the provisions of Part F of the Generation
 * Challenge Programme Amended Consortium Agreement (http://bit.ly/KQX1nL)
 * 
 *******************************************************************************/

package org.generationcp.ibpworkbench.ui.breedingview.multisiteanalysis;

import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.util.IndexedContainer;
import com.vaadin.ui.*;
import com.vaadin.ui.Button.ClickEvent;
import org.generationcp.commons.breedingview.xml.Genotypes;
import org.generationcp.commons.breedingview.xml.Trait;
import org.generationcp.commons.gxe.xml.GxeEnvironment;
import org.generationcp.commons.hibernate.ManagerFactoryProvider;
import org.generationcp.commons.sea.xml.Environment;
import org.generationcp.commons.vaadin.spring.InternationalizableComponent;
import org.generationcp.commons.vaadin.spring.SimpleResourceBundleMessageSource;
import org.generationcp.commons.vaadin.theme.Bootstrap;
import org.generationcp.commons.vaadin.util.MessageNotifier;
import org.generationcp.ibpworkbench.IBPWorkbenchLayout;
import org.generationcp.ibpworkbench.Message;
import org.generationcp.ibpworkbench.util.GxeInput;
import org.generationcp.ibpworkbench.util.GxeUtility;
import org.generationcp.ibpworkbench.util.ToolUtil;
import org.generationcp.middleware.domain.dms.*;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.manager.ManagerFactory;
import org.generationcp.middleware.manager.api.StudyDataManager;
import org.generationcp.middleware.manager.api.WorkbenchDataManager;
import org.generationcp.middleware.pojos.workbench.Project;
import org.generationcp.middleware.pojos.workbench.Tool;
import org.generationcp.middleware.pojos.workbench.ToolName;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.Map.Entry;

/**
 * 
 * @author Aldrin Batac
 *
 */
@Configurable
public class MultiSiteAnalysisGxePanel extends VerticalLayout implements InitializingBean, 
										InternationalizableComponent, IBPWorkbenchLayout {

    private static final long serialVersionUID = 1L;
    
    private GxeTable gxeTable;
    private Table selectTraitsTable;
    
    private Project currentProject;

    private Study currentStudy;
    
    private Integer currentRepresentationId;
    
    private Integer currentDataSetId;
    
    private String currentDatasetName;

    private String selectedEnvFactorName;
    private String selectedEnvGroupFactorName;
    private String selectedGenotypeFactorName;
    
    private Button btnBack;
    private Button btnReset;
    private Button btnRunBreedingView;
    private Map<String, Boolean> variatesCheckboxState;
    private MultiSiteAnalysisSelectPanel gxeSelectEnvironmentPanel;
    
    private List<DataSet> ds;
    
    @Autowired
	private WorkbenchDataManager workbenchDataManager;
    
    @Autowired
	private ToolUtil toolUtil;

    @Autowired
    private ManagerFactoryProvider managerFactoryProvider;
    
    @Autowired
    private SimpleResourceBundleMessageSource messageSource;
    
    private StudyDataManager studyDataManager;
    
    private ManagerFactory managerFactory;

	private Label lblDataSelectedForAnalysisHeader;
	private Label lblDatasetName;
	private Label txtDatasetName;
	private Label lblDatasourceName;
	private Label txtDatasourceName;
	private Label lblSelectedEnvironmentFactor;
	private Label txtSelectedEnvironmentFactor;
	private Label lblSelectedEnvironmentGroupFactor;
	private Label txtSelectedEnvironmentGroupFactor;
	private Label lblAdjustedMeansHeader;
	private Label lblAdjustedMeansDescription;
	private Label lblSelectTraitsForAnalysis;
	private CheckBox chkSelectAllEnvironments;
	private CheckBox chkSelectAllTraits;
	private Property.ValueChangeListener selectAllEnvironmentsListener;
	private Property.ValueChangeListener selectAllTraitsListener;
	
	
    public MultiSiteAnalysisGxePanel(StudyDataManager studyDataManager,
    		Project currentProject, 
    		Study study,  
    		MultiSiteAnalysisSelectPanel gxeSelectEnvironmentPanel, 
    		String selectedEnvFactorName,
    		String selectedGenotypeFactorName,
    		String selectedEnvGroupFactorName,
    		Map<String, Boolean> variatesCheckboxState) {
    	this.studyDataManager = studyDataManager;
        this.currentProject = currentProject;
        this.currentStudy = study;
        this.gxeSelectEnvironmentPanel = gxeSelectEnvironmentPanel;
        this.selectedEnvFactorName = selectedEnvFactorName;
        this.selectedGenotypeFactorName = selectedGenotypeFactorName;
        this.selectedEnvGroupFactorName = selectedEnvGroupFactorName;
        this.variatesCheckboxState = variatesCheckboxState;        
        setCaption(study.getName());
    }
    
    @Override
    public void afterPropertiesSet() throws Exception {
        instantiateComponents();
		initializeValues();
		addListeners();
		layoutComponents();
		
		updateLabels();
    }
    
    @Override
    public void attach() {
        super.attach();
        
        updateLabels();
    }
    
    @Override
    public void updateLabels() {
        messageSource.setCaption(btnBack, Message.BACK);
        messageSource.setCaption(btnReset, Message.RESET);
        messageSource.setCaption(btnRunBreedingView, Message.LAUNCH_BREEDING_VIEW);
        messageSource.setValue(lblDataSelectedForAnalysisHeader, Message.GXE_SELECTED_INFO);
        messageSource.setValue(lblDatasetName , Message.BV_DATASET_NAME);
        messageSource.setValue(lblDatasourceName, Message.BV_DATASOURCE_NAME);
        messageSource.setValue(lblSelectedEnvironmentFactor, Message.GXE_SELECTED_ENVIRONMENT_FACTOR);
        messageSource.setValue(lblSelectedEnvironmentGroupFactor, Message.GXE_SELECTED_ENVIRONMENT_GROUP_FACTOR);
        messageSource.setValue(lblAdjustedMeansDescription  , Message.GXE_ADJUSTED_MEANS_DESCRIPTION);
        messageSource.setValue(lblSelectTraitsForAnalysis, Message.GXE_SELECT_TRAITS_FOR_ANALYSIS);
    }
    
	@Override
	public void instantiateComponents() {
		managerFactory = managerFactoryProvider.getManagerFactoryForProject(currentProject);
		
	   	lblDataSelectedForAnalysisHeader = new Label();
    	lblDataSelectedForAnalysisHeader.setStyleName(Bootstrap.Typography.H2.styleName());
    	
    	lblDatasetName = new Label();
    	lblDatasetName.setStyleName("label-bold");
    	
    	txtDatasetName = new Label();
    	
    	lblDatasourceName = new Label();
    	lblDatasourceName.setStyleName("label-bold");
    	
    	txtDatasourceName = new Label();
    	
    	lblSelectedEnvironmentFactor = new Label();
    	lblSelectedEnvironmentFactor.setStyleName("label-bold");
    	
    	txtSelectedEnvironmentFactor = new Label();
    	
    	lblSelectedEnvironmentGroupFactor = new Label();
    	lblSelectedEnvironmentGroupFactor.setStyleName("label-bold");
    	
    	txtSelectedEnvironmentGroupFactor = new Label();
    	
    	chkSelectAllEnvironments = new CheckBox("Select all environments", true);
    	chkSelectAllEnvironments.setImmediate(true);
    	
    	chkSelectAllTraits = new CheckBox("Select all traits", true);
    	chkSelectAllTraits.setImmediate(true);
    	
    	lblAdjustedMeansHeader = new Label("<span class='bms-dataset' style='position:relative; top: -1px; color: #FF4612; "
        		+ "font-size: 22px; font-weight: bold;'></span><b>&nbsp;"+
        		messageSource.getMessage(Message.GXE_ADJUSTED_MEANS_HEADER)+"</b>",Label.CONTENT_XHTML);
    	lblAdjustedMeansHeader.setStyleName(Bootstrap.Typography.H2.styleName());
    	
    	lblAdjustedMeansDescription  = new Label();
    	
    	lblSelectTraitsForAnalysis = new Label();
    	    	
    	btnRunBreedingView = new Button();
		btnBack = new Button();  
		btnReset = new Button(); 
        
	}

	@Override
	public void initializeValues() {
		ds = null;
		try {
			ds = studyDataManager.getDataSetsByType(currentStudy.getId(), DataSetType.MEANS_DATA);
		} catch (MiddlewareQueryException e) {
			e.printStackTrace();
		}
		
		if (ds != null && ds.size() > 0){
			setCaption(ds.get(0).getName());
			txtDatasetName.setValue(ds.get(0).getName());
			txtDatasourceName.setValue(currentStudy.getName());
			txtSelectedEnvironmentFactor.setValue(getSelectedEnvFactorName());
			txtSelectedEnvironmentGroupFactor.setValue(getSelectedEnvGroupFactorName());
			
			Property.ValueChangeListener envCheckBoxListener = new Property.ValueChangeListener(){
				
				private static final long serialVersionUID = 1L;

				@Override
				public void valueChange(ValueChangeEvent event) {
					Boolean val = (Boolean) event.getProperty().getValue();
					if (val == false){
						chkSelectAllEnvironments.removeListener(selectAllEnvironmentsListener);
						chkSelectAllEnvironments.setValue(false);
						chkSelectAllEnvironments.addListener(selectAllEnvironmentsListener);
					}
					
				}
				
			};
			
			setGxeTable(new GxeTable(studyDataManager, currentStudy.getId(), getSelectedEnvFactorName(), selectedEnvGroupFactorName, variatesCheckboxState, envCheckBoxListener));
			getGxeTable().setHeight("300px");
		}
		
		selectTraitsTable = new Table();
		IndexedContainer container = new IndexedContainer();
		
		Property.ValueChangeListener traitCheckBoxListener = new Property.ValueChangeListener(){
		
			private static final long serialVersionUID = -1109780465477901066L;

			@Override
			public void valueChange(ValueChangeEvent event) {
				Boolean val = (Boolean) event.getProperty().getValue();
				
				if (val == false){
					chkSelectAllTraits.removeListener(selectAllTraitsListener);
					chkSelectAllTraits.setValue(false);
					chkSelectAllTraits.addListener(selectAllTraitsListener);
				}
				
			}
			
		};
				
		List<CheckBox> cells = new ArrayList<CheckBox>();
		List<String> columnNames = new ArrayList<String>();

		SortedSet<String> keys = new TreeSet<String>(getVariatesCheckboxState().keySet());
		for (String key : keys) { 
		   if(getVariatesCheckboxState().get(key)){
			   container.addContainerProperty(key, CheckBox.class, null);
				columnNames.add(key.replace("_Means", ""));
				CheckBox chk = new CheckBox("", true);
				chk.setImmediate(true);
				chk.addListener(traitCheckBoxListener);
				cells.add(chk);
		   }
		 
		}
		
		selectTraitsTable.setContainerDataSource(container);
		selectTraitsTable.addItem(cells.toArray(new Object[0]), 1);
		selectTraitsTable.setHeight("80px");
		selectTraitsTable.setWidth("100%");
		selectTraitsTable.setColumnHeaders(columnNames.toArray(new String[0]));
		selectTraitsTable.setColumnCollapsingAllowed(true);
		for (Entry<String, Boolean> trait : getVariatesCheckboxState().entrySet()){
			if (trait.getValue()){
				selectTraitsTable.setColumnWidth(trait.getKey(), 100);
			}
		}
	}

	@Override
	public void addListeners() {
		selectAllEnvironmentsListener = new Property.ValueChangeListener() {
			
			private static final long serialVersionUID = 1L;

			@Override
			public void valueChange(ValueChangeEvent event) {
				
				for (Iterator<?> itr = gxeTable.getItemIds().iterator(); itr.hasNext();){
					CheckBox chk = (CheckBox) gxeTable.getItem(itr.next()).getItemProperty((Object) " ").getValue();
					chk.setValue((Boolean) event.getProperty().getValue());
				}
				
			}
		};
    	
    	selectAllTraitsListener = new Property.ValueChangeListener() {
			
			private static final long serialVersionUID = 1L;

			@Override
			public void valueChange(ValueChangeEvent event) {
				
				for (Iterator<?> itr = selectTraitsTable.getContainerPropertyIds().iterator(); itr.hasNext();){
					CheckBox chk = (CheckBox) selectTraitsTable.getItem(1).getItemProperty(itr.next()).getValue();
					chk.setValue(event.getProperty().getValue());
				}
				
				
			}
		};

		//Generate Buttons
		btnRunBreedingView.addListener(new Button.ClickListener() {
		private static final long serialVersionUID = -7090745965019240566L;

		@Override
		public void buttonClick(ClickEvent event) {
			final ClickEvent buttonClickEvent = event;
			launchBV(false,buttonClickEvent.getComponent().getWindow());
					
		}
		
		private void launchBV(boolean isXLS,final Window windowSource) {
			String inputDir = "";
			Tool breedingViewTool = null;
			try{
				breedingViewTool = workbenchDataManager.getToolWithName(ToolName.breeding_view.toString());
				inputDir = toolUtil.getInputDirectoryForTool(currentProject, breedingViewTool);
			}catch(MiddlewareQueryException ex){
				
			}

			String inputFileName = "";
			
			if (currentStudy == null){
				MessageNotifier
				.showError(windowSource,
						"Cannot export dataset",
						"No dataset is selected. Please open a study that has a dataset.");
				
				return;
			}
			
			
			if (gxeTable != null) {
				
				
				GxeInput gxeInput =  new GxeInput(currentProject, "", 0, 0, "", "", "", "");
	
				
				inputFileName = String.format("%s_%s_%s", currentProject.getProjectName().trim(), gxeTable.getMeansDataSetId(), gxeTable.getMeansDataSet().getName());
				GxeEnvironment gxeEnv = gxeTable.getGxeEnvironment();
				
				
				List<Trait> selectedTraits = new ArrayList<Trait>();
				Iterator<?> itr = selectTraitsTable.getItem(1).getItemPropertyIds().iterator();
				
				while (itr.hasNext()){
					Object propertyId = itr.next();
					CheckBox cb = (CheckBox)selectTraitsTable.getItem(1).getItemProperty(propertyId).getValue();
					if ((Boolean)cb.getValue()){
						Trait t = new Trait();
						t.setName(propertyId.toString());
						t.setActive(true);
						selectedTraits.add(t);
					}
				}
				
				
				List<Environment> selectedEnnvironments = gxeTable.getSelectedEnvironments();
			

				
				File datasetExportFile = null;
				datasetExportFile = GxeUtility.exportGxEDatasetToBreadingViewCsv(gxeTable.getMeansDataSet(), gxeTable.getExperiments(),gxeTable.getEnvironmentName(), selectedEnvGroupFactorName , selectedGenotypeFactorName ,gxeEnv,selectedTraits, currentProject);
				
				

				
				try{
					DataSet trialDataSet;
					List<DataSet> dataSets = studyDataManager.getDataSetsByType(currentStudy.getId(), DataSetType.SUMMARY_DATA);
					if (dataSets.size() > 0) {
						trialDataSet = dataSets.get(0);
					}else{
						trialDataSet = getTrialDataSet(currentStudy.getId());
					}
					List<Experiment> trialExperiments = studyDataManager.getExperiments(trialDataSet.getId(), 0, Integer.MAX_VALUE);
					File summaryStatsFile = GxeUtility.exportTrialDatasetToSummaryStatsCsv(trialDataSet, trialExperiments, gxeTable.getEnvironmentName(), selectedTraits, currentProject);
					gxeInput.setSourceCSVSummaryStatsFilePath(summaryStatsFile.getAbsolutePath());
				}catch(Exception e){
					e.printStackTrace();
				}
				
				
				
				if (isXLS)
					gxeInput.setSourceXLSFilePath(datasetExportFile.getAbsolutePath());
				else
					gxeInput.setSourceCSVFilePath(datasetExportFile.getAbsolutePath());
			
				gxeInput.setDestXMLFilePath(String.format("%s\\%s.xml", inputDir, inputFileName));
				gxeInput.setTraits(selectedTraits);
				gxeInput.setEnvironment(gxeEnv);
				gxeInput.setSelectedEnvironments(selectedEnnvironments);
				gxeInput.setEnvironmentGroup(selectedEnvGroupFactorName);
				
			
				Genotypes genotypes = new Genotypes();
				if (selectedGenotypeFactorName != null && selectedGenotypeFactorName != "") {
						genotypes.setName(selectedGenotypeFactorName);
					}else{
						genotypes.setName("G!");
					}
					
			

				gxeInput.setGenotypes(genotypes);
				gxeInput.setEnvironmentName(gxeTable.getEnvironmentName());
				gxeInput.setBreedingViewProjectName(currentProject.getProjectName());
				
				
				
				
				GxeUtility.generateXmlFieldBook(gxeInput);
				
				File absoluteToolFile = new File(breedingViewTool.getPath()).getAbsoluteFile();
	            try {
					ProcessBuilder pb = new ProcessBuilder(absoluteToolFile.getAbsolutePath(), "-project=", gxeInput.getDestXMLFilePath());
					pb.start();

                    MessageNotifier.showMessage(windowSource, "GxE files saved", "Successfully generated the means dataset and xml input files for breeding view.");
                }
                catch (IOException e) {
                    e.printStackTrace();
                    MessageNotifier.showMessage(windowSource, "Cannot launch " + absoluteToolFile.getName(), "But it successfully created GxE Excel and XML input file for the breeding_view!");
                }
                
			}
			
			managerFactory.close();
		}
	});
	
		btnBack.addListener(new Button.ClickListener() {
			
			private static final long serialVersionUID = 1L;
	
			@Override
			public void buttonClick(ClickEvent event) {
				
				TabSheet tabSheet = gxeSelectEnvironmentPanel.getGxeAnalysisComponentPanel()
					.getStudiesTabsheet();
				tabSheet.replaceComponent(tabSheet.getSelectedTab(), gxeSelectEnvironmentPanel);
					
			}
		});
	
		btnReset.addListener(new Button.ClickListener() {
			
			private static final long serialVersionUID = 1L;
	
			@Override
			public void buttonClick(ClickEvent event) {
				
				chkSelectAllTraits.setValue(true);
				chkSelectAllEnvironments.setValue(true);
			}
		});
		
		chkSelectAllEnvironments.addListener(selectAllEnvironmentsListener);
		
		chkSelectAllTraits.addListener(selectAllTraitsListener);
	}

	@Override
	public void layoutComponents() {
        setWidth("100%");
        setSpacing(true);
		setMargin(true);
		
		GridLayout selectedInfoLayout = new GridLayout(4, 3);
        selectedInfoLayout.setSizeUndefined();
        selectedInfoLayout.setSpacing(true);
        selectedInfoLayout.setMargin(false, false, true, false);
        selectedInfoLayout.setColumnExpandRatio(0, 1);
        selectedInfoLayout.setColumnExpandRatio(1, 3);
        selectedInfoLayout.setColumnExpandRatio(2, 2);
        selectedInfoLayout.setColumnExpandRatio(3, 1);
        selectedInfoLayout.addComponent(lblDatasetName, 0, 1);
        selectedInfoLayout.addComponent(txtDatasetName, 1, 1);
        selectedInfoLayout.addComponent(lblDatasourceName, 0, 2);
        selectedInfoLayout.addComponent(txtDatasourceName, 1, 2);
        selectedInfoLayout.addComponent(lblSelectedEnvironmentFactor, 2, 1);
        selectedInfoLayout.addComponent(txtSelectedEnvironmentFactor, 3, 1);
        selectedInfoLayout.addComponent(lblSelectedEnvironmentGroupFactor , 2, 2);
        selectedInfoLayout.addComponent(txtSelectedEnvironmentGroupFactor, 3, 2);
		
        addComponent(lblDataSelectedForAnalysisHeader);
		addComponent(selectedInfoLayout);
		
		addComponent(lblAdjustedMeansHeader);
		addComponent(lblAdjustedMeansDescription);
		addComponent(getGxeTable());
		addComponent(chkSelectAllEnvironments);
		setExpandRatio(getGxeTable(), 1.0F);
		
		// hack, just wanna add space here
		addComponent(new Label("<br/>", Label.CONTENT_XHTML));
		
		addComponent(lblSelectTraitsForAnalysis);		
		addComponent(selectTraitsTable);
		setExpandRatio(selectTraitsTable, 1.0F);
		addComponent(chkSelectAllTraits);
		
        addComponent(layoutButtonArea());
	}
    
	private DataSet getTrialDataSet(int studyId) throws MiddlewareQueryException{
    	
    	int trialDatasetId = studyId-1;//default
		List<DatasetReference> datasets = studyDataManager.getDatasetReferences(studyId);
		for (DatasetReference datasetReference : datasets) {
			String name = datasetReference.getName();
			int id = datasetReference.getId();
			
				if(name!=null && (name.startsWith("TRIAL_") || name.startsWith("NURSERY_"))) {
					trialDatasetId = id;
					break;
				} else {
					DataSet ds = studyDataManager.getDataSet(id);
					if(ds!=null && ds.getVariableTypes().getVariableTypes()!=null) {
						boolean aTrialDataset = true;
						for (VariableType variableType: ds.getVariableTypes().getVariableTypes()) {
							if(variableType.getStandardVariable().getPhenotypicType() 
									== PhenotypicType.GERMPLASM) {
								aTrialDataset = false;
								break;
							}
						}
						if(aTrialDataset) {
							trialDatasetId = id;
						}
					}
				}
			
		}
		
		DataSet trialDataSet = studyDataManager.getDataSet(trialDatasetId);
		return trialDataSet;
		
    }
    

    
    protected Component layoutButtonArea() {
        HorizontalLayout buttonLayout = new HorizontalLayout();
        
        buttonLayout.setSizeFull();
        buttonLayout.setSpacing(true);
        buttonLayout.setMargin(true);

        btnRunBreedingView.addStyleName(Bootstrap.Buttons.PRIMARY.styleName());
        Label spacer = new Label("&nbsp;",Label.CONTENT_XHTML);
        spacer.setSizeFull();
        
        buttonLayout.addComponent(spacer);
        buttonLayout.setExpandRatio(spacer,1.0F);
        buttonLayout.addComponent(btnBack);
        buttonLayout.addComponent(btnReset);
        buttonLayout.addComponent(btnRunBreedingView);

        return buttonLayout;
    }

    @Override
	public Object getData(){
		return this.getCurrentStudy();
		
	}
    
	//SETTERS AND GETTERS
	public Project getCurrentProject() {
        return currentProject;
    }

    public void setCurrentProject(Project currentProject) {
        this.currentProject = currentProject;
    }

    public Study getCurrentStudy() {
        return currentStudy;
    }

    public void setCurrentStudy(Study currentStudy) {
        this.currentStudy = currentStudy;
    }

    public Integer getCurrentRepresentationId() {
        return currentRepresentationId;
    }

    public void setCurrentRepresentationId(Integer currentRepresentationId) {
        this.currentRepresentationId = currentRepresentationId;
    }
    
    public Integer getCurrentDataSetId() {
        return currentDataSetId;
    }

    public void setCurrentDataSetId(Integer currentDataSetId) {
        this.currentDataSetId = currentDataSetId;
    }

    public String getCurrentDatasetName() {
        return currentDatasetName;
    }

    public void setCurrentDatasetName(String currentDatasetName) {
        this.currentDatasetName = currentDatasetName;
    }
    
    public StudyDataManager getStudyDataManager() {
    	if (this.studyDataManager == null) this.studyDataManager = managerFactory.getNewStudyDataManager();
		return this.studyDataManager;
	}

	public Map<String, Boolean> getVariatesCheckboxState() {
		return variatesCheckboxState;
	}
	
	public void setVariatesCheckboxState(HashMap<String, Boolean> hashMap) {
			this.variatesCheckboxState = hashMap;
	}

	public GxeTable getGxeTable() {
		return gxeTable;
	}

	public void setGxeTable(GxeTable gxeTable) {
		this.gxeTable = gxeTable;
	}

	public String getSelectedEnvFactorName() {
		return selectedEnvFactorName;
	}

	public void setSelectedEnvFactorName(String selectedEnvFactorName) {
		this.selectedEnvFactorName = selectedEnvFactorName;
	}
	
	public String getSelectedEnvGroupFactorName() {
		return selectedEnvGroupFactorName;
	}

	public void setSelectedEnvGroupFactorName(String selectedEnvGroupFactorName) {
		this.selectedEnvGroupFactorName = selectedEnvGroupFactorName;
	}

}// End of MultiSiteAnalysisGxePanel