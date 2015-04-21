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

package org.generationcp.ibpworkbench.ui.workflow;

import com.vaadin.terminal.ThemeResource;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.BaseTheme;
import org.generationcp.commons.constant.ToolEnum;
import org.generationcp.commons.vaadin.spring.InternationalizableComponent;
import org.generationcp.commons.vaadin.spring.SimpleResourceBundleMessageSource;
import org.generationcp.commons.vaadin.theme.Bootstrap;
import org.generationcp.ibpworkbench.Message;
import org.generationcp.ibpworkbench.actions.ChangeWindowAction;
import org.generationcp.ibpworkbench.actions.ChangeWindowAction.WindowEnums;
import org.generationcp.ibpworkbench.actions.LaunchWorkbenchToolAction;
import org.generationcp.ibpworkbench.ui.WorkflowConstants;
import org.generationcp.middleware.pojos.workbench.Project;
import org.generationcp.middleware.pojos.workbench.Role;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

@Configurable
public class MarsWorkflowDiagram extends Panel implements InitializingBean, InternationalizableComponent {

    private static final long serialVersionUID = 1L;
    
    private static final String DOWN_ARROW_THEME_RESOURCE = "images/blc-arrow-d.png";
    
    private boolean workflowPreview;

    private Project project;

    private Label dashboardTitle;

    // titles
    private Label projectPlanningTitle;
    private Label populationDevelopmentTitle;
    private Label fieldTrialManagementTitle;
    private Label genotypingTitle;
    private Button mainHeadToHeadButton;
    private Button mainHeadToHeadButton2;
    private Label phenotypicAnalysisTitle;
    private Label qtlAnalysisTitle;
    private Label singleSiteAnalysisTitle;

    private Label qtlSelectionTitle;
    
    private Label recombinationCycleTitle;
    
    private Label finalBreedingDecisionTitle;

    // buttons
    private Button browseGermplasmButton;
    private Button browseStudiesButton;
    private Button browseGermplasmListsButton;
    private Button browseGenotypingDataButton;

    private Button breedingManagerButton;
    
    private Button fieldBookButton;
    
    private Button gdmsButton;

    private Button phenotypicBreedingViewButton;
    private Button breedingViewSingleSiteAnalysisCentralButton;
    private Button breedingViewSingleSiteAnalysisLocalButton;
    
    private Button qtlBreedingViewButton;

    private Button optimasButton;
    
    private Button manageGermplasmListsButton;
    
    private Button breedingViewMultiSiteAnalysisButton;
    
    private Button makeCrossesButton;
    private Button recomMakeCrossesButton;

    private Embedded downArrow11;
    private Embedded downArrow12;
    private Embedded downArrow13;
    
    private Embedded downArrow21;
    
    private Embedded downArrow31;
    private Embedded downArrow32;
    
    @Autowired
    private SimpleResourceBundleMessageSource messageSource;

    private Button recomBreedingManagerButton;

    private Role role;

    private Button breedingPlannerButton;

    private Button germplasmImportButton;

    private Button germplasmImportButton2;
    
    private Button queryForAdaptedGermplasmButton;
    
    private Button queryForAdaptedGermplasmButton2;
    
    private Button breedingManagerListManager;
    private Button ontologyBrowserFBBtn;
    private Button metaAnalysisBtn;
    private Button metaAnalysisBtn2;

    public MarsWorkflowDiagram(boolean workflowPreview, Project project, Role role) {
        this.workflowPreview = workflowPreview;
        
        if (!workflowPreview) {
            this.project = project;
        }
        
        this.role = role;
    }

    @Override
    public void afterPropertiesSet() {
        assemble();
    }

    protected void initializeComponents() {
        // dashboard title
        dashboardTitle = new Label();
        dashboardTitle.setStyleName("gcp-content-title");

        // project planning
        projectPlanningTitle = new Label();
        projectPlanningTitle.setStyleName("gcp-section-title");

        populationDevelopmentTitle = new Label();
        populationDevelopmentTitle.setStyleName("gcp-section-title");

        fieldTrialManagementTitle = new Label();
        fieldTrialManagementTitle.setStyleName("gcp-section-title");

        genotypingTitle = new Label();
        genotypingTitle.setStyleName("gcp-section-title");

        phenotypicAnalysisTitle = new Label();
        phenotypicAnalysisTitle.setStyleName("gcp-section-title");

        qtlAnalysisTitle = new Label();
        qtlAnalysisTitle.setStyleName("gcp-section-title");

        singleSiteAnalysisTitle = new Label();
        singleSiteAnalysisTitle.setStyleName("gcp-section-title");

        qtlSelectionTitle = new Label();
        qtlSelectionTitle.setStyleName("gcp-section-title");

        recombinationCycleTitle = new Label();
        recombinationCycleTitle.setStyleName("gcp-section-title");

        finalBreedingDecisionTitle = new Label();
        finalBreedingDecisionTitle.setStyleName("gcp-section-title");

        // project planning buttons
        browseGermplasmButton = new Button();
        browseGermplasmButton.setStyleName(BaseTheme.BUTTON_LINK + " gcp-workflow-link");
        browseGermplasmButton.setSizeUndefined();

        browseStudiesButton = new Button();
        browseStudiesButton.setStyleName(BaseTheme.BUTTON_LINK + " gcp-workflow-link");
        browseStudiesButton.setSizeUndefined();
        
        browseGermplasmListsButton = new Button();
        browseGermplasmListsButton.setStyleName(BaseTheme.BUTTON_LINK + " gcp-workflow-link");
        browseGermplasmListsButton.setSizeUndefined();
        
        browseGenotypingDataButton = new Button("Browse Genotyping Data");
        browseGenotypingDataButton.setStyleName(BaseTheme.BUTTON_LINK + " gcp-workflow-link");
        browseGenotypingDataButton.setSizeUndefined();
        browseGenotypingDataButton.setDescription("Click to launch genotyping data");
        
        
        // population development buttons
        breedingManagerButton = new Button();
        breedingManagerButton.setStyleName(BaseTheme.BUTTON_LINK + " gcp-workflow-link");
        breedingManagerButton.setSizeUndefined();
        
        breedingPlannerButton = new Button(messageSource.getMessage(Message.BREEDING_PLANNER_MARS));
        breedingPlannerButton.setStyleName(BaseTheme.BUTTON_LINK + " gcp-workflow-link");
        breedingPlannerButton.setSizeUndefined();
        breedingPlannerButton.setDescription("Click to launch the freestanding Breeding Planner application.");
        
        germplasmImportButton = new Button("IBFB Import Germplasm Lists");
        germplasmImportButton.setStyleName(BaseTheme.BUTTON_LINK + " gcp-workflow-link");
        germplasmImportButton.setSizeUndefined();
        germplasmImportButton.setDescription("Click to launch Fieldbook on Nursery Manager View.");
        
        germplasmImportButton2 = new Button("Import Germplasm Lists");
        germplasmImportButton2.setStyleName(BaseTheme.BUTTON_LINK + " gcp-workflow-link");
        germplasmImportButton2.setSizeUndefined();
        germplasmImportButton2.setDescription("Click to launch the Germplasm Import View.");
        
        // field trial management buttons
        fieldBookButton = new Button("Manage Trials");
        fieldBookButton.setStyleName(BaseTheme.BUTTON_LINK + " gcp-workflow-link");
        fieldBookButton.setDescription("Click to launch Fieldbook on Trial Manager View");
        fieldBookButton.setSizeUndefined();

        // genotyping buttons
        gdmsButton = new Button();
        gdmsButton.setStyleName(BaseTheme.BUTTON_LINK + " gcp-workflow-link");
        gdmsButton.setSizeUndefined();

        // phenotypic analysis buttons
        phenotypicBreedingViewButton = new Button();
        phenotypicBreedingViewButton.setStyleName(BaseTheme.BUTTON_LINK + " gcp-workflow-link");
        phenotypicBreedingViewButton.setSizeUndefined();

        // qtl analysis buttons
        qtlBreedingViewButton = new Button();
        qtlBreedingViewButton.setStyleName(BaseTheme.BUTTON_LINK + " gcp-workflow-link");
        qtlBreedingViewButton.setSizeUndefined();
        
        breedingViewSingleSiteAnalysisCentralButton = new Button("Single-Site Analysis for Central Datasets");
        breedingViewSingleSiteAnalysisCentralButton.setStyleName(BaseTheme.BUTTON_LINK + " gcp-workflow-link");
        breedingViewSingleSiteAnalysisCentralButton.setSizeUndefined();
        breedingViewSingleSiteAnalysisCentralButton.setDescription("Click to launch Single-Site Analysis on Study Datasets from Central IBDB");
        
        breedingViewSingleSiteAnalysisLocalButton = new Button("Single-Site Analysis");
        breedingViewSingleSiteAnalysisLocalButton.setStyleName(BaseTheme.BUTTON_LINK + " gcp-workflow-link");
        breedingViewSingleSiteAnalysisLocalButton.setSizeUndefined();
        breedingViewSingleSiteAnalysisLocalButton.setDescription("Click to launch Single-Site Analysis on Study Datasets");
        
        // recombination cycle selection buttons
        optimasButton = new Button();
        optimasButton.setStyleName(BaseTheme.BUTTON_LINK + " gcp-workflow-link");
        optimasButton.setSizeUndefined();
        
        recomBreedingManagerButton = new Button();
        recomBreedingManagerButton.setStyleName(BaseTheme.BUTTON_LINK + " gcp-workflow-link");
        recomBreedingManagerButton.setSizeUndefined();
        
        
        manageGermplasmListsButton = new Button();
        manageGermplasmListsButton.setStyleName(BaseTheme.BUTTON_LINK + " gcp-workflow-link");
        manageGermplasmListsButton.setSizeUndefined();
        
        breedingViewMultiSiteAnalysisButton = new Button();
        breedingViewMultiSiteAnalysisButton.setStyleName(BaseTheme.BUTTON_LINK + " gcp-workflow-link");
        breedingViewMultiSiteAnalysisButton.setSizeUndefined();
        
        makeCrossesButton = new Button();
        makeCrossesButton.setStyleName(BaseTheme.BUTTON_LINK + " gcp-workflow-link");
        makeCrossesButton.setSizeUndefined();
        
        recomMakeCrossesButton = new Button();
        recomMakeCrossesButton.setStyleName(BaseTheme.BUTTON_LINK + " gcp-workflow-link");
        recomMakeCrossesButton.setSizeUndefined();
        
        // arrows
        downArrow11 = new Embedded(null, new ThemeResource(DOWN_ARROW_THEME_RESOURCE));
        downArrow12 = new Embedded(null, new ThemeResource(DOWN_ARROW_THEME_RESOURCE));
        downArrow13 = new Embedded(null, new ThemeResource(DOWN_ARROW_THEME_RESOURCE));
        
        downArrow21 = new Embedded(null, new ThemeResource(DOWN_ARROW_THEME_RESOURCE));
        
        downArrow31 = new Embedded(null, new ThemeResource(DOWN_ARROW_THEME_RESOURCE));
        downArrow32 = new Embedded(null, new ThemeResource(DOWN_ARROW_THEME_RESOURCE));

        mainHeadToHeadButton = new Button(messageSource.getMessage(Message.MAIN_HEAD_TO_HEAD_LAUNCH));
        mainHeadToHeadButton.setStyleName(BaseTheme.BUTTON_LINK + " gcp-workflow-link");
        mainHeadToHeadButton.setSizeUndefined();
        mainHeadToHeadButton.setDescription(messageSource.getMessage(Message.CLICK_TO_LAUNCH_MAIN_HEAD_TO_HEAD));

        mainHeadToHeadButton2 = new Button(messageSource.getMessage(Message.MAIN_HEAD_TO_HEAD_LAUNCH));
        mainHeadToHeadButton2.setStyleName(BaseTheme.BUTTON_LINK + " gcp-workflow-link");
        mainHeadToHeadButton2.setSizeUndefined();
        mainHeadToHeadButton2.setDescription(messageSource.getMessage(Message.CLICK_TO_LAUNCH_MAIN_HEAD_TO_HEAD));
        
        queryForAdaptedGermplasmButton = new Button(messageSource.getMessage(Message.QUERY_FOR_ADAPTED_GERMPLASM));
        queryForAdaptedGermplasmButton.setStyleName(BaseTheme.BUTTON_LINK + " gcp-workflow-link");
        queryForAdaptedGermplasmButton.setSizeUndefined();
        queryForAdaptedGermplasmButton.setDescription(messageSource.getMessage(Message.CLICK_TO_LAUNCH_QUERY_FOR_ADAPTED_GERMPLASM));
        
        queryForAdaptedGermplasmButton2 = new Button(messageSource.getMessage(Message.QUERY_FOR_ADAPTED_GERMPLASM));
        queryForAdaptedGermplasmButton2.setStyleName(BaseTheme.BUTTON_LINK + " gcp-workflow-link");
        queryForAdaptedGermplasmButton2.setSizeUndefined();
        queryForAdaptedGermplasmButton2.setDescription(messageSource.getMessage(Message.CLICK_TO_LAUNCH_QUERY_FOR_ADAPTED_GERMPLASM));
        
        breedingManagerListManager = new Button(messageSource.getMessage(Message.BREEDING_MANAGER_BROWSE_FOR_GERMPLASMS_AND_LISTS));
        breedingManagerListManager.setStyleName(BaseTheme.BUTTON_LINK + " gcp-workflow-link");
        breedingManagerListManager.setSizeUndefined();
        breedingManagerListManager.setDescription(messageSource.getMessage(Message.CLICK_TO_BROWSE_FOR_GERMPLASMS_AND_LISTS));

        ontologyBrowserFBBtn = new Button("Manage Ontologies");
        ontologyBrowserFBBtn.setStyleName(BaseTheme.BUTTON_LINK + " gcp-workflow-link");
        ontologyBrowserFBBtn.setSizeUndefined();
        ontologyBrowserFBBtn.setDescription("Click to launch Fieldbook on Ontology Browser view");

        metaAnalysisBtn = new Button("Meta Analysis of Field Trials");
        metaAnalysisBtn.setStyleName(BaseTheme.BUTTON_LINK + " gcp-workflow-link");
        metaAnalysisBtn.setSizeUndefined();
        metaAnalysisBtn.setDescription("Click to launch Meta Analysis of Field Trial Tool");


        metaAnalysisBtn2 = new Button("Meta Analysis of Field Trials");
        metaAnalysisBtn2.setStyleName(BaseTheme.BUTTON_LINK + " gcp-workflow-link");
        metaAnalysisBtn2.setSizeUndefined();
        metaAnalysisBtn2.setDescription("Click to launch Meta Analysis of Field Trial Tool");
    }

    protected void initializeLayout() {
        this.setSizeFull();
        this.setScrollable(true);
        this.setContent(layoutWorkflowArea());
    }

    protected ComponentContainer layoutWorkflowArea() {
        HorizontalLayout layout = new HorizontalLayout();
        layout.setSpacing(true);
        layout.setHeight("1500px");

        Component breedingManagementArea = layoutBreedingManagementArea();
        breedingManagementArea.setHeight("100%");
        layout.addComponent(breedingManagementArea);

        Component markerTraitAnalysisArea = layoutMarkerTraitAnalysisArea();
        markerTraitAnalysisArea.setHeight("100%");
        layout.addComponent(markerTraitAnalysisArea);

        Component markerImplementationArea = layoutMarkerImplementationArea();
        markerImplementationArea.setHeight("100%");
        layout.addComponent(markerImplementationArea);

        final VerticalLayout rootContainer = new VerticalLayout();
        rootContainer.setSizeUndefined();
        rootContainer.setMargin(new Layout.MarginInfo(false,true,true,true));
        rootContainer.setSpacing(false);

        if (!workflowPreview) {
            Label header = new Label();
            header.setStyleName(Bootstrap.Typography.H1.styleName());
            header.setValue(role.getLabel());
            rootContainer.addComponent(header);

        }
        rootContainer.addComponent(layout);

        return rootContainer;
    }

    protected ComponentContainer layoutBreedingManagementArea() {
        VerticalLayout layout = new VerticalLayout();
        layout.setHeight("750px");
        layout.setMargin(new Layout.MarginInfo(true,false,false,false));
        layout.setSpacing(true);

        Component projectPlanningArea = layoutProjectPlanning();
        layout.addComponent(projectPlanningArea);
        
        layout.addComponent(downArrow11);
        layout.setComponentAlignment(downArrow11, Alignment.MIDDLE_CENTER);

        Component populationManagementArea = layoutPopulationDevelopment();
        layout.addComponent(populationManagementArea);
        
        layout.addComponent(downArrow12);
        layout.setComponentAlignment(downArrow12, Alignment.MIDDLE_CENTER);

        Component fieldTrialArea = layoutFieldTrialManagement();
        layout.addComponent(fieldTrialArea);
        
        Label emptyLabel = new Label(" ");
        emptyLabel.setWidth("100%");
        emptyLabel.setHeight("20px");
        layout.addComponent(emptyLabel);
        layout.setExpandRatio(emptyLabel, 100);

        return layout;
    }

    protected ComponentContainer layoutProjectPlanning() {
        VerticalLayout layout = new VerticalLayout();
        configureWorkflowStepLayout(layout);

        layout.addComponent(projectPlanningTitle);
        layout.setComponentAlignment(projectPlanningTitle, Alignment.TOP_CENTER);
        layout.setExpandRatio(projectPlanningTitle, 0);

        Label emptyLabel = new Label(" ");
        emptyLabel.setWidth("100%");
        emptyLabel.setHeight("20px");
        layout.addComponent(emptyLabel);
        layout.setExpandRatio(emptyLabel, 100);

        layout.addComponent(breedingPlannerButton);
        layout.setComponentAlignment(breedingPlannerButton, Alignment.TOP_CENTER);
        layout.setExpandRatio(breedingPlannerButton, 0);

        layout.addComponent(browseStudiesButton);
        layout.setComponentAlignment(browseStudiesButton, Alignment.TOP_CENTER);
        layout.setExpandRatio(browseStudiesButton, 0);
        
        layout.addComponent(browseGermplasmListsButton);
        browseGermplasmListsButton.setHeight("20px");
        layout.setComponentAlignment(browseGermplasmListsButton, Alignment.TOP_CENTER);
        layout.setExpandRatio(browseGermplasmListsButton, 0);

        layout.addComponent(ontologyBrowserFBBtn);
        ontologyBrowserFBBtn.setHeight("20px");
        layout.setComponentAlignment(ontologyBrowserFBBtn, Alignment.TOP_CENTER);
        layout.setExpandRatio(ontologyBrowserFBBtn, 0);

        layout.addComponent(mainHeadToHeadButton2);
        layout.setComponentAlignment(mainHeadToHeadButton2, Alignment.TOP_CENTER);
        layout.setExpandRatio(mainHeadToHeadButton2, 0);
        
        layout.addComponent(queryForAdaptedGermplasmButton2);
        layout.setComponentAlignment(queryForAdaptedGermplasmButton2, Alignment.TOP_CENTER);
        layout.setExpandRatio(queryForAdaptedGermplasmButton2, 0);

        return layout;
    }

    protected ComponentContainer layoutPopulationDevelopment() {
        VerticalLayout layout = new VerticalLayout();
        configureWorkflowStepLayout(layout);

        layout.addComponent(populationDevelopmentTitle);
        layout.setComponentAlignment(populationDevelopmentTitle, Alignment.TOP_CENTER);
        layout.setExpandRatio(populationDevelopmentTitle, 0);

        Label emptyLabel = new Label(" ");
        emptyLabel.setWidth("100%");
        emptyLabel.setHeight("20px");
        layout.addComponent(emptyLabel);
        layout.setExpandRatio(emptyLabel, 100);
        
        layout.addComponent(germplasmImportButton2);
        layout.setComponentAlignment(germplasmImportButton2, Alignment.TOP_CENTER);
        layout.setExpandRatio(germplasmImportButton2, 0);
        
        layout.addComponent(makeCrossesButton);
        layout.setComponentAlignment(makeCrossesButton, Alignment.TOP_CENTER);
        layout.setExpandRatio(makeCrossesButton, 0);
        
        layout.addComponent(breedingManagerButton);
        layout.setComponentAlignment(breedingManagerButton, Alignment.TOP_CENTER);
        layout.setExpandRatio(breedingManagerButton, 0);

        return layout;
    }

    protected ComponentContainer layoutFieldTrialManagement() {
        VerticalLayout layout = new VerticalLayout();
        configureWorkflowStepLayout(layout);

        layout.addComponent(fieldTrialManagementTitle);
        layout.setComponentAlignment(fieldTrialManagementTitle, Alignment.TOP_CENTER);
        layout.setExpandRatio(fieldTrialManagementTitle, 0);

        Label emptyLabel = new Label(" ");
        emptyLabel.setWidth("100%");
        emptyLabel.setHeight("20px");
        layout.addComponent(emptyLabel);
        layout.setExpandRatio(emptyLabel, 100);

        layout.addComponent(fieldBookButton);
        layout.setComponentAlignment(fieldBookButton, Alignment.TOP_CENTER);
        layout.setExpandRatio(fieldBookButton, 0);

        return layout;
    }

    protected ComponentContainer layoutGenotyping() {
        VerticalLayout layout = new VerticalLayout();
        configureWorkflowStepLayout(layout);

        layout.addComponent(genotypingTitle);
        layout.setComponentAlignment(genotypingTitle, Alignment.TOP_CENTER);
        layout.setExpandRatio(genotypingTitle, 0);

        Label emptyLabel = new Label(" ");
        emptyLabel.setWidth("100%");
        emptyLabel.setHeight("20px");
        layout.addComponent(emptyLabel);
        layout.setExpandRatio(emptyLabel, 100);

        layout.addComponent(gdmsButton);
        layout.setComponentAlignment(gdmsButton, Alignment.TOP_CENTER);
        layout.setExpandRatio(gdmsButton, 0);

        return layout;
    }

    protected ComponentContainer layoutMarkerTraitAnalysisArea() {
        VerticalLayout layout = new VerticalLayout();
        layout.setHeight("750px");
        layout.setMargin(new Layout.MarginInfo(true,false,false,false));
        layout.setSpacing(true);

        Component markerTraitAnalysisArea = layoutPhenotypicAnalysis();
        layout.addComponent(markerTraitAnalysisArea);
        
        layout.addComponent(downArrow21);
        layout.setComponentAlignment(downArrow21, Alignment.MIDDLE_CENTER);
        
        Component qtlAnalysisArea = layoutQtlAnalysis();
        layout.addComponent(qtlAnalysisArea);

        Label emptyLabel = new Label(" ");
        emptyLabel.setWidth("100%");
        emptyLabel.setHeight("20px");
        layout.addComponent(emptyLabel);
        layout.setExpandRatio(emptyLabel, 100);

        return layout;
    }

    protected ComponentContainer layoutPhenotypicAnalysis() {
        VerticalLayout layout = new VerticalLayout();
        configureWorkflowStepLayout(layout);

        layout.addComponent(phenotypicAnalysisTitle);
        layout.setComponentAlignment(phenotypicAnalysisTitle, Alignment.TOP_CENTER);
        layout.setExpandRatio(phenotypicAnalysisTitle, 0);

        Label emptyLabel = new Label(" ");
        emptyLabel.setWidth("100%");
        emptyLabel.setHeight("20px");
        layout.addComponent(emptyLabel);
        layout.setExpandRatio(emptyLabel, 100);
        
        layout.addComponent(breedingViewSingleSiteAnalysisLocalButton);
        layout.setComponentAlignment(breedingViewSingleSiteAnalysisLocalButton,
                Alignment.TOP_CENTER);
        layout.setExpandRatio(breedingViewSingleSiteAnalysisLocalButton, 0);

        layout.addComponent(breedingViewMultiSiteAnalysisButton);
        layout.setComponentAlignment(breedingViewMultiSiteAnalysisButton, Alignment.TOP_CENTER);
        layout.setExpandRatio(breedingViewMultiSiteAnalysisButton, 0);

        layout.addComponent(metaAnalysisBtn2);
        layout.setComponentAlignment(metaAnalysisBtn2, Alignment.TOP_CENTER);
        layout.setExpandRatio(metaAnalysisBtn2, 0);

        layout.addComponent(phenotypicBreedingViewButton);
        layout.setComponentAlignment(phenotypicBreedingViewButton, Alignment.TOP_CENTER);
        layout.setExpandRatio(phenotypicBreedingViewButton, 0);

        return layout;
    }
    
    protected ComponentContainer layoutQtlAnalysis() {
        VerticalLayout layout = new VerticalLayout();
        configureWorkflowStepLayout(layout);

        layout.addComponent(qtlAnalysisTitle);
        layout.setComponentAlignment(qtlAnalysisTitle, Alignment.TOP_CENTER);
        layout.setExpandRatio(qtlAnalysisTitle, 0);

        Label emptyLabel = new Label(" ");
        emptyLabel.setWidth("100%");
        emptyLabel.setHeight("20px");
        layout.addComponent(emptyLabel);
        layout.setExpandRatio(emptyLabel, 100);

        layout.addComponent(qtlBreedingViewButton);
        layout.setComponentAlignment(qtlBreedingViewButton, Alignment.TOP_CENTER);
        layout.setExpandRatio(qtlBreedingViewButton, 0);

        return layout;
    }
    
    protected ComponentContainer layoutSingleSiteAnalysis() {
        VerticalLayout layout = new VerticalLayout();
        configureWorkflowStepLayout(layout);

        layout.addComponent(singleSiteAnalysisTitle);
        layout.setComponentAlignment(singleSiteAnalysisTitle, Alignment.TOP_CENTER);
        layout.setExpandRatio(singleSiteAnalysisTitle, 0);

        Label emptyLabel = new Label(" ");
        emptyLabel.setWidth("100%");
        emptyLabel.setHeight("20px");
        layout.addComponent(emptyLabel);
        layout.setExpandRatio(emptyLabel, 100);

        layout.addComponent(breedingViewSingleSiteAnalysisLocalButton);
        layout.setComponentAlignment(breedingViewSingleSiteAnalysisLocalButton, Alignment.TOP_CENTER);
        layout.setExpandRatio(breedingViewSingleSiteAnalysisLocalButton, 0);
        
        
        layout.addComponent(breedingViewSingleSiteAnalysisCentralButton);
        layout.setComponentAlignment(breedingViewSingleSiteAnalysisCentralButton, Alignment.TOP_CENTER);
        layout.setExpandRatio(breedingViewSingleSiteAnalysisCentralButton, 0);

        layout.addComponent(metaAnalysisBtn);
        layout.setComponentAlignment(metaAnalysisBtn, Alignment.TOP_CENTER);
        layout.setExpandRatio(metaAnalysisBtn, 0);

        return layout;
    }

    protected Component layoutMarkerImplementationArea() {
        VerticalLayout layout = new VerticalLayout();
        layout.setHeight("750px");
        layout.setMargin(new Layout.MarginInfo(true,false,false,false));
        layout.setSpacing(true);

        Component qtlSelectionArea = layoutQtlSelection();
        layout.addComponent(qtlSelectionArea);
        
        layout.addComponent(downArrow31);
        layout.setComponentAlignment(downArrow31, Alignment.MIDDLE_CENTER);
        
        Component recombinationCycleArea = layoutRecombinationCycle();
        layout.addComponent(recombinationCycleArea);

        layout.addComponent(downArrow32);
        layout.setComponentAlignment(downArrow32, Alignment.MIDDLE_CENTER);
        
        Component finalBreedingDecisionArea = layoutFinalBreedingDecision();
        layout.addComponent(finalBreedingDecisionArea);

        Label emptyLabel = new Label(" ");
        emptyLabel.setWidth("100%");
        emptyLabel.setHeight("20px");
        layout.addComponent(emptyLabel);
        layout.setExpandRatio(emptyLabel, 100);
        
        return layout;
    }

    protected ComponentContainer layoutQtlSelection() {
        VerticalLayout layout = new VerticalLayout();
        configureWorkflowStepLayout(layout);

        layout.addComponent(qtlSelectionTitle);
        layout.setComponentAlignment(qtlSelectionTitle, Alignment.TOP_CENTER);
        layout.setExpandRatio(qtlSelectionTitle, 0);

        Label emptyLabel = new Label(" ");
        emptyLabel.setWidth("100%");
        emptyLabel.setHeight("20px");
        layout.addComponent(emptyLabel);
        layout.setExpandRatio(emptyLabel, 100);

       

        return layout;
    }

    protected ComponentContainer layoutRecombinationCycle() {
        VerticalLayout layout = new VerticalLayout();
        configureWorkflowStepLayout(layout);

        layout.addComponent(recombinationCycleTitle);
        layout.setComponentAlignment(recombinationCycleTitle, Alignment.TOP_CENTER);
        layout.setExpandRatio(recombinationCycleTitle, 0);

        Label emptyLabel = new Label(" ");
        emptyLabel.setWidth("100%");
        emptyLabel.setHeight("20px");
        layout.addComponent(emptyLabel);
        layout.setExpandRatio(emptyLabel, 100);
        
        layout.addComponent(optimasButton);
        layout.setComponentAlignment(optimasButton, Alignment.TOP_CENTER);
        layout.setExpandRatio(optimasButton, 0);
        
        layout.addComponent(recomMakeCrossesButton);
        layout.setComponentAlignment(recomMakeCrossesButton, Alignment.TOP_CENTER);
        layout.setExpandRatio(recomMakeCrossesButton, 0);
        
        layout.addComponent(recomBreedingManagerButton);
        layout.setComponentAlignment(recomBreedingManagerButton, Alignment.TOP_CENTER);
        layout.setExpandRatio(recomBreedingManagerButton, 0);
        
        return layout;
    }
    
    protected ComponentContainer layoutFinalBreedingDecision() {
        VerticalLayout layout = new VerticalLayout();
        configureWorkflowStepLayout(layout);

        layout.addComponent(finalBreedingDecisionTitle);
        layout.setComponentAlignment(finalBreedingDecisionTitle, Alignment.TOP_CENTER);
        layout.setExpandRatio(finalBreedingDecisionTitle, 0);

        Label emptyLabel = new Label(" ");
        emptyLabel.setWidth("100%");
        emptyLabel.setHeight("20px");
        layout.addComponent(emptyLabel);
        layout.setExpandRatio(emptyLabel, 100);
        
        layout.addComponent(mainHeadToHeadButton);
        layout.setComponentAlignment(mainHeadToHeadButton, Alignment.TOP_CENTER);
        layout.setExpandRatio(mainHeadToHeadButton, 0);
        
        layout.addComponent(queryForAdaptedGermplasmButton);
        layout.setComponentAlignment(queryForAdaptedGermplasmButton, Alignment.TOP_CENTER);
        layout.setExpandRatio(queryForAdaptedGermplasmButton, 0);

        return layout;
    }

    protected ComponentContainer createPanel(String caption, String... buttonCaptions) {
        VerticalLayout layout = new VerticalLayout();
        configureWorkflowStepLayout(layout);

        Label titleLabel = new Label(caption);
        titleLabel.setStyleName("gcp-section-title");
        titleLabel.setSizeUndefined();

        layout.addComponent(titleLabel);
        layout.setComponentAlignment(titleLabel, Alignment.TOP_CENTER);
        layout.setExpandRatio(titleLabel, 0);

        Label emptyLabel = new Label(" ");
        emptyLabel.setWidth("100%");
        emptyLabel.setHeight("20px");
        layout.addComponent(emptyLabel);
        layout.setExpandRatio(emptyLabel, 100);

        for (String buttonCaption : buttonCaptions) {
            Button button = new Button(buttonCaption);
            button.setStyleName(BaseTheme.BUTTON_LINK + " gcp-workflow-link");

            layout.addComponent(button);
            layout.setComponentAlignment(button, Alignment.TOP_CENTER);
            layout.setExpandRatio(button, 0);
        }

        return layout;
    }

    protected void configureWorkflowStepLayout(VerticalLayout layout) {
        layout.setWidth("280px");
        layout.setHeight("215px");
        layout.setStyleName("gcp-workflow-step");
        layout.setMargin(false, false, true, false);
    }

    protected void initializeActions() {
        if (!workflowPreview) {
            germplasmImportButton.addListener(new LaunchWorkbenchToolAction(ToolEnum.IBFB_GERMPLASM_IMPORT));
            germplasmImportButton2.addListener(new LaunchWorkbenchToolAction(ToolEnum.GERMPLASM_IMPORT));
            
            breedingPlannerButton.addListener(new LaunchWorkbenchToolAction(ToolEnum.BREEDING_PLANNER)); //TODO
            
            mainHeadToHeadButton.addListener(new LaunchWorkbenchToolAction(ToolEnum.MAIN_HEAD_TO_HEAD_BROWSER));
            mainHeadToHeadButton2.addListener(new LaunchWorkbenchToolAction(ToolEnum.MAIN_HEAD_TO_HEAD_BROWSER));  

            browseGermplasmButton.addListener(new LaunchWorkbenchToolAction(ToolEnum.GERMPLASM_BROWSER));
            browseStudiesButton.addListener(new LaunchWorkbenchToolAction(ToolEnum.STUDY_BROWSER));
            browseGermplasmListsButton.addListener(new LaunchWorkbenchToolAction(ToolEnum.BM_LIST_MANAGER_MAIN));
            
            browseGenotypingDataButton.addListener(new LaunchWorkbenchToolAction(ToolEnum.GDMS));
            
            gdmsButton.addListener(new LaunchWorkbenchToolAction(ToolEnum.GDMS));

            fieldBookButton.addListener(new LaunchWorkbenchToolAction(ToolEnum.TRIAL_MANAGER_FIELDBOOK_WEB));

            optimasButton.addListener(new LaunchWorkbenchToolAction(ToolEnum.OPTIMAS));
            breedingManagerButton.addListener(new LaunchWorkbenchToolAction(ToolEnum.NURSERY_MANAGER_FIELDBOOK_WEB));

            recomBreedingManagerButton.addListener(new LaunchWorkbenchToolAction(ToolEnum.NURSERY_MANAGER_FIELDBOOK_WEB));
            
            phenotypicBreedingViewButton.addListener(new LaunchWorkbenchToolAction(ToolEnum.BREEDING_VIEW));
            qtlBreedingViewButton.addListener(new LaunchWorkbenchToolAction(ToolEnum.BREEDING_VIEW));

            breedingViewSingleSiteAnalysisCentralButton.addListener(new ChangeWindowAction(WindowEnums.BREEDING_VIEW,this.project,WorkflowConstants.BREEDING_VIEW_SINGLE_SITE_ANALYSIS_CENTRAL));
            breedingViewSingleSiteAnalysisLocalButton.addListener(new ChangeWindowAction(WindowEnums.BREEDING_VIEW,this.project,WorkflowConstants.BREEDING_VIEW_SINGLE_SITE_ANALYSIS_LOCAL));


            manageGermplasmListsButton.addListener(new LaunchWorkbenchToolAction(ToolEnum.BREEDING_MANAGER));
            
            breedingViewMultiSiteAnalysisButton.addListener(new ChangeWindowAction(WindowEnums.BREEDING_GXE,this.project,null));
            
            makeCrossesButton.addListener(new LaunchWorkbenchToolAction(ToolEnum.CROSSING_MANAGER));
            recomMakeCrossesButton.addListener(new LaunchWorkbenchToolAction(ToolEnum.CROSSING_MANAGER));
            
            queryForAdaptedGermplasmButton.addListener(new LaunchWorkbenchToolAction(ToolEnum.QUERY_FOR_ADAPTED_GERMPLASM));
            queryForAdaptedGermplasmButton2.addListener(new LaunchWorkbenchToolAction(ToolEnum.QUERY_FOR_ADAPTED_GERMPLASM));
            breedingManagerListManager.addListener(new LaunchWorkbenchToolAction(ToolEnum.BM_LIST_MANAGER_MAIN));

            ontologyBrowserFBBtn.addListener(new LaunchWorkbenchToolAction(ToolEnum.ONTOLOGY_BROWSER_FIELDBOOK_WEB));
            metaAnalysisBtn.addListener(new ChangeWindowAction(WindowEnums.BV_META_ANALYSIS,this.project,null));
            metaAnalysisBtn2.addListener(new ChangeWindowAction(WindowEnums.BV_META_ANALYSIS,this.project,null));
        }
    }

    protected void assemble() {
        initializeComponents();
        initializeLayout();
        initializeActions();
        
        if (workflowPreview) {
            this.setStyleName("gcp-removelink");
        }
    }
    
    @Override
    public void attach() {
        super.attach();
        
        updateLabels();
    }
    
    @Override
    public void updateLabels() {
        if (workflowPreview) {
            messageSource.setValue(dashboardTitle, Message.WORKFLOW_PREVIEW_TITLE, "MARS");
        } else {
            messageSource.setValue(dashboardTitle, Message.PROJECT_TITLE, project.getProjectName());
        }
        
        // titles
        messageSource.setValue(projectPlanningTitle, Message.PROJECT_PLANNING);
        messageSource.setValue(populationDevelopmentTitle, Message.POPULATION_DEVELOPMENT);
        messageSource.setValue(fieldTrialManagementTitle, Message.FIELD_TRIAL_MANAGEMENT);
        messageSource.setValue(genotypingTitle, Message.GENOTYPING);

        messageSource.setValue(phenotypicAnalysisTitle, Message.PHENOTYPIC_ANALYSIS);
        messageSource.setValue(qtlAnalysisTitle, Message.QTL_ANALYSIS);

        messageSource.setValue(qtlSelectionTitle, Message.QTL_SELECTION);
        
        messageSource.setValue(recombinationCycleTitle, Message.RECOMBINATION_CYCLE);
        
        messageSource.setValue(finalBreedingDecisionTitle, Message.FINAL_BREEDING_DECISION);
        
        // buttons
        messageSource.setCaption(browseGermplasmButton, Message.BROWSE_GERMPLASM_INFORMATION);
        messageSource.setDescription(browseGermplasmButton, Message.CLICK_TO_LAUNCH_GERMPLASM_BROWSER);
        
        messageSource.setCaption(browseStudiesButton, Message.BROWSE_STUDIES_AND_DATASETS);
        messageSource.setDescription(browseStudiesButton, Message.CLICK_TO_LAUNCH_STUDY_BROWSER);
        
        messageSource.setCaption(browseGermplasmListsButton, Message.BROWSE_GERMPLAM_LISTS);
        messageSource.setDescription(browseGermplasmListsButton, Message.CLICK_TO_LAUNCH_GERMPLASM_LIST_BROWSER);

        messageSource.setCaption(breedingManagerButton, Message.BREEDING_MANAGER);
        messageSource.setDescription(breedingManagerButton, Message.CLICK_TO_LAUNCH_BREEDING_MANAGER);
        
        messageSource.setCaption(recomBreedingManagerButton, Message.BREEDING_MANAGER);
        messageSource.setDescription(recomBreedingManagerButton, Message.CLICK_TO_LAUNCH_BREEDING_MANAGER);
        
        messageSource.setCaption(gdmsButton, Message.MANAGE_GENOTYPING_DATA);
        messageSource.setDescription(gdmsButton, Message.CLICK_TO_LAUNCH_GDMS);

        messageSource.setCaption(phenotypicBreedingViewButton, Message.BREEDING_VIEW);
        messageSource.setDescription(phenotypicBreedingViewButton, Message.CLICK_TO_LAUNCH_BREEDING_VIEW);
        
        messageSource.setCaption(qtlBreedingViewButton, Message.BREEDING_VIEW);
        messageSource.setDescription(qtlBreedingViewButton, Message.CLICK_TO_LAUNCH_BREEDING_VIEW);
        
        messageSource.setCaption(optimasButton, Message.OPTIMAS_MARS);
        messageSource.setDescription(optimasButton, Message.CLICK_TO_LAUNCH_OPTIMAS);
        
        messageSource.setCaption(breedingViewMultiSiteAnalysisButton, Message.MULTI_SITE_ANALYSIS_LINK);
        
        messageSource.setCaption(manageGermplasmListsButton, Message.LIST_MANAGER);
        messageSource.setDescription(manageGermplasmListsButton, Message.CLICK_TO_LAUNCH_LIST_MANAGER);
        
        messageSource.setCaption(makeCrossesButton, Message.MAKE_CROSSES);
        messageSource.setDescription(makeCrossesButton, Message.CLICK_TO_LAUNCH_CROSSING_MANAGER);
        
        messageSource.setCaption(recomMakeCrossesButton, Message.MAKE_CROSSES);
        messageSource.setDescription(recomMakeCrossesButton, Message.CLICK_TO_LAUNCH_CROSSING_MANAGER);
        
        
        messageSource.setCaption(makeCrossesButton, Message.MAKE_CROSSES);
        messageSource.setDescription(makeCrossesButton, Message.CLICK_TO_LAUNCH_CROSSING_MANAGER);
        
        messageSource.setCaption(recomMakeCrossesButton, Message.MAKE_CROSSES);
        messageSource.setDescription(recomMakeCrossesButton, Message.CLICK_TO_LAUNCH_CROSSING_MANAGER);
        
        
        messageSource.setCaption(queryForAdaptedGermplasmButton, Message.QUERY_FOR_ADAPTED_GERMPLASM);
        messageSource.setDescription(queryForAdaptedGermplasmButton, Message.CLICK_TO_LAUNCH_QUERY_FOR_ADAPTED_GERMPLASM);
        
        messageSource.setCaption(queryForAdaptedGermplasmButton2, Message.QUERY_FOR_ADAPTED_GERMPLASM);
        messageSource.setDescription(queryForAdaptedGermplasmButton2, Message.CLICK_TO_LAUNCH_QUERY_FOR_ADAPTED_GERMPLASM);
    }
}
