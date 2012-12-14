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

package org.generationcp.ibpworkbench.comp;

import org.generationcp.commons.vaadin.spring.InternationalizableComponent;
import org.generationcp.commons.vaadin.spring.SimpleResourceBundleMessageSource;
import org.generationcp.ibpworkbench.Message;
import org.generationcp.ibpworkbench.actions.LaunchWorkbenchToolAction;
import org.generationcp.ibpworkbench.actions.LaunchWorkbenchToolAction.ToolEnum;
import org.generationcp.middleware.pojos.workbench.Project;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import com.vaadin.terminal.ThemeResource;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.Embedded;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.BaseTheme;

@Configurable
public class ConventionalBreedingWorkflowDiagram extends VerticalLayout implements WorkflowConstants, InitializingBean, InternationalizableComponent {

    private static final long serialVersionUID = 1L;

    //this is in pixels and used for layouting
    private static final int WORKFLOW_STEP_HEIGHT = 110;
    private static final int WORKFLOW_STEP_EXTRA_HEIGHT = 130;
    private static final int WORKFLOW_STEP_WIDTH = 270;
    private static final int EXTRA_SPACE_BETWEEN_COMPONENTS = 10;
    private static final int ARROW_IMAGE_HEIGHT = 30;
    //private static final int ARROW_IMAGE_WIDTH = 40;
    private static final String FIRST_COLUMN_LEFT_FOR_ARROWS = "135px";
    private static final String DOWN_ARROW_THEME_RESOURCE = "../gcp-default/images/blc-arrow-d.png";
    
    private boolean workflowPreview;
    
    private Project project;

    private Label dashboardTitle;

    private Label projectPlanningTitle;
    private Label populationDevelopmentTitle;
    private Label fieldTrialManagementTitle;
    private Label statisticalAnalysisTitle;
    private Label breedingDecisionTitle;

    //links for tools
    private Button browseGermplasmButton;
    private Button browseStudiesButton;
    private Button browseGermplasmListsButton;
    private Button breedingManagerButton;
    private Button breedingViewButton;
    private Button breedingViewSingleSiteAnalysisButton;
    private Button fieldbookButton;
    private Button optimasButton;
    
    private Embedded downArrowImage1;
    private Embedded downArrowImage2;
    private Embedded downArrowImage3;
    private Embedded downArrowImage4;
    
    @Autowired
    private SimpleResourceBundleMessageSource messageSource;

    public ConventionalBreedingWorkflowDiagram(boolean workflowPreview, Project project) {
        this.workflowPreview = workflowPreview;
        
        if (!workflowPreview) {
            this.project = project;
        }
    }
    
    @Override
    public void afterPropertiesSet() {
        assemble();
    }

    protected void initializeComponents() {
        // dashboard title
        dashboardTitle = new Label();
        dashboardTitle.setStyleName("gcp-content-title");

        projectPlanningTitle = new Label("Project Planning");
        projectPlanningTitle.setStyleName("gcp-section-title-large");
        projectPlanningTitle.setSizeUndefined();

        populationDevelopmentTitle = new Label("Population Development");
        populationDevelopmentTitle.setStyleName("gcp-section-title-large");
        populationDevelopmentTitle.setSizeUndefined();

        fieldTrialManagementTitle = new Label("Field Trial Management");
        fieldTrialManagementTitle.setStyleName("gcp-section-title-large");
        fieldTrialManagementTitle.setSizeUndefined();

        statisticalAnalysisTitle = new Label("Statistical Analysis");
        statisticalAnalysisTitle.setStyleName("gcp-section-title-large");
        statisticalAnalysisTitle.setSizeUndefined();

        breedingDecisionTitle = new Label("Breeding Decision");
        breedingDecisionTitle.setStyleName("gcp-section-title-large");
        breedingDecisionTitle.setSizeUndefined();
        
        browseGermplasmButton = new Button("Browse Germplasm Information");
        browseGermplasmButton.setStyleName(BaseTheme.BUTTON_LINK);
        browseGermplasmButton.setSizeUndefined();
        browseGermplasmButton.setDescription("Click to launch Germplasm Browser");

        browseStudiesButton = new Button("Browse Studies and Datasets");
        browseStudiesButton.setStyleName(BaseTheme.BUTTON_LINK);
        browseStudiesButton.setSizeUndefined();
        browseStudiesButton.setDescription("Click to launch Study Browser");

        browseGermplasmListsButton = new Button("Browse Germplasm Lists");
        browseGermplasmListsButton.setStyleName(BaseTheme.BUTTON_LINK);
        browseGermplasmListsButton.setSizeUndefined();
        browseGermplasmListsButton.setDescription("Click to launch Germplasm List Browser");
        
        breedingManagerButton = new Button("Breeding Manager");
        breedingManagerButton.setStyleName(BaseTheme.BUTTON_LINK);
        breedingManagerButton.setSizeUndefined();
        breedingManagerButton.setDescription("Click to launch Breeding Manager");

        breedingViewButton = new Button("Breeding View");
        breedingViewButton.setStyleName(BaseTheme.BUTTON_LINK);
        breedingViewButton.setSizeUndefined();
        breedingViewButton.setDescription("Click to launch Breeding View");
        
        breedingViewSingleSiteAnalysisButton = new Button("Single-Site Analysis");
        breedingViewSingleSiteAnalysisButton.setStyleName(BaseTheme.BUTTON_LINK);
        breedingViewSingleSiteAnalysisButton.setSizeUndefined();
        breedingViewSingleSiteAnalysisButton.setDescription("Click to launch Single-Site Analysis");

        fieldbookButton = new Button("Fieldbook");
        fieldbookButton.setStyleName(BaseTheme.BUTTON_LINK);
        fieldbookButton.setSizeUndefined();
        fieldbookButton.setDescription("Click to launch Fieldbook");
        
        optimasButton = new Button("OptiMAS");
        optimasButton.setStyleName(BaseTheme.BUTTON_LINK);
        optimasButton.setSizeUndefined();
        optimasButton.setDescription("Click to launch OptiMAS");
        
        downArrowImage1 = new Embedded("", new ThemeResource(DOWN_ARROW_THEME_RESOURCE));
        downArrowImage2 = new Embedded("", new ThemeResource(DOWN_ARROW_THEME_RESOURCE));
        downArrowImage3 = new Embedded("", new ThemeResource(DOWN_ARROW_THEME_RESOURCE));
        downArrowImage4 = new Embedded("", new ThemeResource(DOWN_ARROW_THEME_RESOURCE));
    }

    protected void initializeLayout() {
        setSpacing(true);
        setMargin(true);
        setWidth("1000px");

        dashboardTitle.setSizeUndefined();
        addComponent(dashboardTitle);

        Component workFlowArea = layoutWorkflowArea();
        workFlowArea.setSizeUndefined();
        addComponent(workFlowArea);

    }

    protected Component layoutWorkflowArea() {
        Panel panel = new Panel();

        AbsoluteLayout layout = new AbsoluteLayout();
        layout.setMargin(true);
        layout.setWidth("300px");
        layout.setHeight("800px");
        
        String extraSpace = EXTRA_SPACE_BETWEEN_COMPONENTS + "px";
        int top = 10;
        String topInPixels = "";
        
        //the steps on the first column
        Component projectPlanningArea = layoutProjectPlanning();
        layout.addComponent(projectPlanningArea, "top:" + extraSpace + "; left:" + extraSpace);
        
        top = top + WORKFLOW_STEP_EXTRA_HEIGHT + EXTRA_SPACE_BETWEEN_COMPONENTS;
        topInPixels = top + "px";
        layout.addComponent(downArrowImage1, "top:" + topInPixels + "; left:" + FIRST_COLUMN_LEFT_FOR_ARROWS);
        
        top = top + ARROW_IMAGE_HEIGHT + EXTRA_SPACE_BETWEEN_COMPONENTS;
        topInPixels = top + "px";
        Component populationDevelopmentArea = layoutPopulationDevelopment();
        layout.addComponent(populationDevelopmentArea, "top:" + topInPixels  + "; left:" + extraSpace);
        
        top = top + WORKFLOW_STEP_HEIGHT + EXTRA_SPACE_BETWEEN_COMPONENTS;
        topInPixels = top + "px";
        layout.addComponent(downArrowImage2, "top:" + topInPixels + "; left:" + FIRST_COLUMN_LEFT_FOR_ARROWS);
        
        top = top + ARROW_IMAGE_HEIGHT + EXTRA_SPACE_BETWEEN_COMPONENTS;
        topInPixels = top + "px";
        Component fieldTrialArea = layoutFieldTrialManagement();
        layout.addComponent(fieldTrialArea, "top:" + topInPixels  + "; left:" + extraSpace);
        
        top = top + WORKFLOW_STEP_HEIGHT + EXTRA_SPACE_BETWEEN_COMPONENTS;
        topInPixels = top + "px";
        layout.addComponent(downArrowImage3, "top:" + topInPixels + "; left:" + FIRST_COLUMN_LEFT_FOR_ARROWS);
        
        top = top + ARROW_IMAGE_HEIGHT + EXTRA_SPACE_BETWEEN_COMPONENTS;
        topInPixels = top + "px";
        Component statisticalAnalysisArea = layoutStatisticalAnalysis();
        layout.addComponent(statisticalAnalysisArea, "top:" + topInPixels  + "; left:" + extraSpace);

        top = top + WORKFLOW_STEP_HEIGHT + EXTRA_SPACE_BETWEEN_COMPONENTS;
        topInPixels = top + "px";
        layout.addComponent(downArrowImage4, "top:" + topInPixels + "; left:" + FIRST_COLUMN_LEFT_FOR_ARROWS);
        
        top = top + ARROW_IMAGE_HEIGHT + EXTRA_SPACE_BETWEEN_COMPONENTS;
        topInPixels = top + "px";
        Component breedingDecisionArea = layoutBreedingDecision();
        layout.addComponent(breedingDecisionArea, "top:" + topInPixels  + "; left:" + extraSpace);

        panel.setContent(layout);
        return panel;
    }

    protected Component layoutProjectPlanning() {
        VerticalLayout layout = new VerticalLayout();
        configureWorkflowStepLayout(layout);
        layout.setHeight(WORKFLOW_STEP_EXTRA_HEIGHT + "px");
        
        layout.addComponent(projectPlanningTitle);
        layout.setComponentAlignment(projectPlanningTitle, Alignment.TOP_CENTER);
        layout.setExpandRatio(projectPlanningTitle, 0);

        Label emptyLabel = new Label(" ");
        emptyLabel.setWidth("100%");
        emptyLabel.setHeight("20px");
        layout.addComponent(emptyLabel);
        layout.setExpandRatio(emptyLabel, 100);
        
        layout.addComponent(browseGermplasmButton);
        browseGermplasmButton.setHeight("20px");
        layout.setComponentAlignment(browseGermplasmButton, Alignment.TOP_CENTER);
        layout.setExpandRatio(browseGermplasmButton, 0);

        layout.addComponent(browseStudiesButton);
        browseStudiesButton.setHeight("20px");
        layout.setComponentAlignment(browseStudiesButton, Alignment.TOP_CENTER);
        layout.setExpandRatio(browseStudiesButton, 0);
        
        layout.addComponent(browseGermplasmListsButton);
        layout.setComponentAlignment(browseGermplasmListsButton, Alignment.TOP_CENTER);
        layout.setExpandRatio(browseGermplasmListsButton, 0);

        return layout;
    }

    protected Component layoutPopulationDevelopment() {
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

        layout.addComponent(breedingManagerButton);
        layout.setComponentAlignment(breedingManagerButton, Alignment.TOP_CENTER);
        layout.setExpandRatio(breedingManagerButton, 0);

        return layout;
    }

    protected Component layoutFieldTrialManagement() {
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

        layout.addComponent(fieldbookButton);
        layout.setComponentAlignment(fieldbookButton, Alignment.TOP_CENTER);
        layout.setExpandRatio(fieldbookButton, 0);
        
        return layout;
    }

    protected Component layoutStatisticalAnalysis() {
        VerticalLayout layout = new VerticalLayout();
        configureWorkflowStepLayout(layout);

        layout.addComponent(statisticalAnalysisTitle);
        layout.setComponentAlignment(statisticalAnalysisTitle, Alignment.TOP_CENTER);
        layout.setExpandRatio(statisticalAnalysisTitle, 0);

        Label emptyLabel = new Label(" ");
        emptyLabel.setWidth("100%");
        emptyLabel.setHeight("20px");
        layout.addComponent(emptyLabel);
        layout.setExpandRatio(emptyLabel, 100);

        layout.addComponent(breedingViewButton);
        layout.setComponentAlignment(breedingViewButton, Alignment.TOP_CENTER);
        layout.setExpandRatio(breedingViewButton, 0);
        
        layout.addComponent(breedingViewSingleSiteAnalysisButton);
        layout.setComponentAlignment(breedingViewSingleSiteAnalysisButton, Alignment.TOP_CENTER);
        layout.setExpandRatio(breedingViewSingleSiteAnalysisButton, 0);

        return layout;
    }

    protected Component layoutBreedingDecision() {
        VerticalLayout layout = new VerticalLayout();
        configureWorkflowStepLayout(layout);

        layout.addComponent(breedingDecisionTitle);
        layout.setComponentAlignment(breedingDecisionTitle, Alignment.TOP_CENTER);
        layout.setExpandRatio(breedingDecisionTitle, 0);

        Label emptyLabel = new Label(" ");
        emptyLabel.setWidth("100%");
        emptyLabel.setHeight("20px");
        layout.addComponent(emptyLabel);
        layout.setExpandRatio(emptyLabel, 100);

        layout.addComponent(optimasButton);
        layout.setComponentAlignment(optimasButton, Alignment.TOP_CENTER);
        layout.setExpandRatio(optimasButton, 0);
        
        return layout;
    }

    protected Component createPanel(String caption, String... buttonCaptions) {
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
            button.setStyleName(BaseTheme.BUTTON_LINK);

            layout.addComponent(button);
            layout.setComponentAlignment(button, Alignment.TOP_CENTER);
            layout.setExpandRatio(button, 0);
        }

        return layout;
    }

    protected void configureWorkflowStepLayout(VerticalLayout layout) {
        layout.setWidth(WORKFLOW_STEP_WIDTH + "px");
        layout.setHeight(WORKFLOW_STEP_HEIGHT + "px");
        layout.setStyleName("gcp-mars-workflow-step");
        layout.setMargin(true, true, true, true);
    }
    
    protected void initializeActions() {
        if (!workflowPreview) {
            browseGermplasmButton.addListener(new LaunchWorkbenchToolAction(ToolEnum.GERMPLASM_BROWSER));
            browseStudiesButton.addListener(new LaunchWorkbenchToolAction(ToolEnum.STUDY_BROWSER));
            browseGermplasmListsButton.addListener(new LaunchWorkbenchToolAction(ToolEnum.GERMPLASM_LIST_BROWSER));
            breedingManagerButton.addListener(new LaunchWorkbenchToolAction(ToolEnum.BREEDING_MANAGER));
            breedingViewButton.addListener(new LaunchWorkbenchToolAction(ToolEnum.BREEDING_VIEW));
            breedingViewSingleSiteAnalysisButton.addListener(new LaunchWorkbenchToolAction(ToolEnum.BREEDING_VIEW, project, WorkflowConstants.BREEDING_VIEW_SINGLE_SITE_ANALYSIS));
            fieldbookButton.addListener(new LaunchWorkbenchToolAction(ToolEnum.FIELDBOOK));
            optimasButton.addListener(new LaunchWorkbenchToolAction(ToolEnum.OPTIMAS));
        }
    }

    protected void assemble() {
        initializeComponents();
        initializeLayout();
        initializeActions();
    }
    
    @Override
    public void attach() {
        super.attach();        
        updateLabels();
    }
    
    @Override
    public void updateLabels() {
        if (workflowPreview) {
            messageSource.setValue(dashboardTitle, Message.WORKFLOW_PREVIEW_TITLE, "Conventional Breeding");
        } else { 
            messageSource.setValue(dashboardTitle, Message.PROJECT_TITLE, project.getProjectName());
        }
    }
}
