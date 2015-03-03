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

package org.generationcp.ibpworkbench.ui.breedingview.singlesiteanalysis;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.generationcp.commons.hibernate.ManagerFactoryProvider;
import org.generationcp.commons.vaadin.spring.InternationalizableComponent;
import org.generationcp.commons.vaadin.spring.SimpleResourceBundleMessageSource;
import org.generationcp.commons.vaadin.theme.Bootstrap;
import org.generationcp.commons.vaadin.ui.HeaderLabelLayout;
import org.generationcp.commons.vaadin.util.MessageNotifier;
import org.generationcp.ibpworkbench.IBPWorkbenchLayout;
import org.generationcp.ibpworkbench.Message;
import org.generationcp.ibpworkbench.actions.OpenSelectDatasetForExportAction;
import org.generationcp.ibpworkbench.model.FactorModel;
import org.generationcp.ibpworkbench.model.VariateModel;
import org.generationcp.ibpworkbench.ui.breedingview.SelectStudyDialog;
import org.generationcp.ibpworkbench.ui.breedingview.SelectStudyDialogForBreedingViewUpload;
import org.generationcp.middleware.domain.dms.DataSet;
import org.generationcp.middleware.domain.dms.PhenotypicType;
import org.generationcp.middleware.domain.dms.Study;
import org.generationcp.middleware.domain.dms.VariableType;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.manager.Database;
import org.generationcp.middleware.manager.ManagerFactory;
import org.generationcp.middleware.manager.StudyDataManagerImpl;
import org.generationcp.middleware.manager.api.StudyDataManager;
import org.generationcp.middleware.pojos.workbench.Project;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.beans.factory.annotation.Value;

import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.util.BeanContainer;
import com.vaadin.terminal.ThemeResource;
import com.vaadin.ui.AbstractSelect.ItemDescriptionGenerator;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Table;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

/**
 * 
 * @author Aldrin Batac
 * 
 */
@Configurable
public class SingleSiteAnalysisPanel extends VerticalLayout implements
		InitializingBean, InternationalizableComponent, IBPWorkbenchLayout {

	private final class TableColumnGenerator implements Table.ColumnGenerator {
		private final Table table;
		private static final long serialVersionUID = 1L;

		private TableColumnGenerator(Table table) {
			this.table = table;
		}

		@SuppressWarnings("unchecked")
		@Override
		public Object generateCell(Table source, Object itemId,
				Object columnId) {

			BeanContainer<Integer, VariateModel> container = (BeanContainer<Integer, VariateModel>) table
					.getContainerDataSource();
			final VariateModel vm = container.getItem(itemId).getBean();

			final CheckBox checkBox = new CheckBox();
			checkBox.setImmediate(true);
			checkBox.setVisible(true);
			checkBox.addListener(new CheckBoxListener(vm));

			if (vm.getActive()) {
				checkBox.setValue(true);
			} else {
				checkBox.setValue(false);
			}

			return checkBox;

		}
	}

	private final class SelectAllListener implements Property.ValueChangeListener {
		private static final long serialVersionUID = 344514045768824046L;

		@SuppressWarnings("unchecked")
		@Override
		public void valueChange(ValueChangeEvent event) {

			Boolean val = (Boolean) event.getProperty().getValue();
			BeanContainer<Integer, VariateModel> container = (BeanContainer<Integer, VariateModel>) tblVariates.getContainerDataSource();
			for (Object itemId : container.getItemIds()){
				container.getItem(itemId).getBean().setActive(val);
			}
			tblVariates.refreshRowCache();
			for (Entry<String, Boolean> entry : variatesCheckboxState
					.entrySet()) {
				variatesCheckboxState.put(entry.getKey(), val);
			}
			if(val) {
				numOfSelectedVariates = variatesCheckboxState.size();
			} else {
				numOfSelectedVariates = 0;
			}
			
			if (numOfSelectedVariates == 0) {
				toggleNextButton(false);
			}else{
				toggleNextButton(val);
			}
			
		}
	}

	private final class CheckBoxListener implements Property.ValueChangeListener {
		private final VariateModel vm;
		private static final long serialVersionUID = 1L;

		private CheckBoxListener(VariateModel vm) {
			this.vm = vm;
		}

		@Override
		public void valueChange(final ValueChangeEvent event) {
			Boolean val = (Boolean) event.getProperty().getValue();
			variatesCheckboxState.put(vm.getName(), val);
			vm.setActive(val);

			if (!val) {
				chkVariatesSelectAll
						.removeListener(selectAllListener);
				chkVariatesSelectAll.setValue(val);
				chkVariatesSelectAll.addListener(selectAllListener);
				numOfSelectedVariates--;
				if(numOfSelectedVariates==0) {
					toggleNextButton(false);
				}
			} else {
				if(numOfSelectedVariates<variatesCheckboxState.size()) {//add this check to ensure that the number of selected does not exceed the total number of variates
					numOfSelectedVariates++;
				}
				toggleNextButton(true);
			}

		}
	}

	private static final long serialVersionUID = 1L;
	private Button browseLink;
	private Button uploadLink;

	private Label lblPageTitle;
	private HeaderLabelLayout heading;
	private Label lblFactors;
	private Label lblVariates;
	
	private Table tblFactors;
	private Table tblVariates;
	private Property.ValueChangeListener selectAllListener;
	private CheckBox chkVariatesSelectAll;
	
	private VerticalLayout lblFactorContainer;
	private VerticalLayout lblVariateContainer;
	private VerticalLayout tblFactorContainer;
	private VerticalLayout tblVariateContainer;

	private VerticalLayout rootLayout;

	private GridLayout studyDetailsLayout;

	private Project currentProject;

	private Study currentStudy;

	private Integer currentRepresentationId;

	private Integer currentDataSetId;

	private String currentDatasetName;

	private Button btnCancel;
	private Button btnNext;
	private Component buttonArea;

	private Map<String, Boolean> variatesCheckboxState;
	private int numOfSelectedVariates = 0;
	private List<FactorModel> factorList;
    private List<VariateModel> variateList;

	private OpenSelectDatasetForExportAction openSelectDatasetForExportAction;

	private static final Logger LOG = LoggerFactory
			.getLogger(SingleSiteAnalysisPanel.class);

	@Autowired
	private ManagerFactoryProvider managerFactoryProvider;

	@Autowired
	private SimpleResourceBundleMessageSource messageSource;
	
	@Value("${workbench.is.server.app}")
	private String isServerApp;

	private StudyDataManager studyDataManager;

	private ManagerFactory managerFactory;
	
	private static final String NAMED_COLUMN_1 = "name";
	private static final String NAMED_COLUMN_2 = "description";
	private static final String NAMED_COLUMN_3 = "scname";
	
	private static final String CAMEL_CASE_NAMED_COLUMN_1 = "Name";
	private static final String CAMEL_CASE_NAMED_COLUMN_2 = "Description";
	private static final String CAMEL_CASE_NAMED_COLUMN_3 = "Scale";

	public SingleSiteAnalysisPanel(Project currentProject,
			Database database) {
		this.currentProject = currentProject;
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
		messageSource.setCaption(btnCancel, Message.RESET);
		messageSource.setCaption(btnNext, Message.NEXT);
		messageSource.setValue(lblPageTitle, Message.TITLE_SSA);
	}
	
	@Override
	public void instantiateComponents() {
		managerFactory = managerFactoryProvider.getManagerFactoryForProject(currentProject);

		lblPageTitle = new Label();
    	lblPageTitle.setStyleName(Bootstrap.Typography.H1.styleName());
    	lblPageTitle.setHeight("26px");

        ThemeResource resource = new ThemeResource("../vaadin-retro/images/search-nurseries.png");
        Label headingLabel =  new Label("Select Data for Analysis");
        headingLabel.setStyleName(Bootstrap.Typography.H4.styleName());
        headingLabel.addStyleName("label-bold");
        heading = new HeaderLabelLayout(resource,headingLabel);
        
		browseLink = new Button();
		browseLink.setImmediate(true);
		browseLink.setStyleName("link");
		browseLink.setCaption("Browse");
		browseLink.setWidth("48px");
		
		uploadLink = new Button();
		uploadLink.setImmediate(true);
		uploadLink.setStyleName("link");
		uploadLink.setCaption("Upload");
		uploadLink.setWidth("48px");
				
		setVariatesCheckboxState(new HashMap<String, Boolean>());

		tblFactors = initializeFactorsTable();
		tblVariates = initializeVariatesTable();
		buttonArea = layoutButtonArea();
		
		lblFactors = new Label("<span class='bms-factors' style='color: #39B54A; font-size: 22px; font-weight: bold;'></span><b>&nbsp;FACTORS</b>",Label.CONTENT_XHTML);
		lblFactors.setStyleName(Bootstrap.Typography.H4.styleName());
		lblFactors.setWidth("100%");
        
		lblVariates = new Label("<span class='bms-variates' style='color: #B8D433; font-size: 22px; font-weight: bold;'></span><b>&nbsp;TRAITS</b>",Label.CONTENT_XHTML);
		lblVariates.setWidth("100%");
		lblVariates.setStyleName(Bootstrap.Typography.H4.styleName());
		
		chkVariatesSelectAll = new CheckBox();
		chkVariatesSelectAll.setImmediate(true);
		chkVariatesSelectAll.setCaption("Select All");

	}

	@Override
	public void initializeValues() {
		//no values to initialize
	}

	@Override
	public void addListeners() {
		selectAllListener = new SelectAllListener();
		
		chkVariatesSelectAll.addListener(selectAllListener);

		browseLink.addListener(new Button.ClickListener() {
			
			private static final long serialVersionUID = 1425892265723948423L;

			@Override
			public void buttonClick(ClickEvent event) {
					
				if(event.getComponent()!=null && event.getComponent().getWindow()!=null) {
					SelectStudyDialog dialog = new SelectStudyDialog(event.getComponent().getWindow(), SingleSiteAnalysisPanel.this ,(StudyDataManagerImpl) getStudyDataManager(), currentProject);
					event.getComponent().getWindow().addWindow(dialog);
				} else if(event.getComponent()==null){
					LOG.error("event component is null");
				} else if(event.getComponent().getWindow()==null){
					LOG.error("event component window is null");
				}
			}
			
		});
		
		uploadLink.addListener(new Button.ClickListener() {
			
			private static final long serialVersionUID = 1425892265723948423L;

			@Override
			public void buttonClick(ClickEvent event) {
					
				if(event.getComponent()!=null && event.getComponent().getWindow()!=null) {
					SelectStudyDialogForBreedingViewUpload dialog = new SelectStudyDialogForBreedingViewUpload(event.getComponent().getWindow(), SingleSiteAnalysisPanel.this ,(StudyDataManagerImpl) getStudyDataManager());
					event.getComponent().getWindow().addWindow(dialog);
				} else if(event.getComponent()==null){
					LOG.error("event component is null");
				} else if(event.getComponent().getWindow()==null){
					LOG.error("event component window is null");
				}
			}
			
		});

		btnCancel.addListener(new Button.ClickListener() {

			private static final long serialVersionUID = 1L;

			@Override
			public void buttonClick(ClickEvent event) {
				refreshFactorsAndVariatesTable();
				toggleNextButton(false);
			}
		});
		openSelectDatasetForExportAction = new OpenSelectDatasetForExportAction(
				this);
		btnNext.addListener(openSelectDatasetForExportAction);
	}

	@Override
	public void layoutComponents() {
		this.setWidth("100%");
		
		HorizontalLayout browseLabelLayout = new HorizontalLayout();
		browseLabelLayout.addComponent(browseLink);
		Label workWith = null;
		
		if (Boolean.parseBoolean(isServerApp)){
			workWith = new Label("for a study to work with ");
		}else{
			workWith = new Label("for a study to work with.");
		}
		
		workWith.setWidth("150px");
		browseLabelLayout.addComponent(workWith);
		Label orLabel = new Label("or");
		orLabel.setWidth("20px");
		
		if (Boolean.parseBoolean(isServerApp)){
			browseLabelLayout.addComponent(orLabel);
			browseLabelLayout.addComponent(uploadLink);
			browseLabelLayout.addComponent(new Label(" Breeding View output files to BMS."));
		}
		
		browseLabelLayout.setSizeUndefined();
		
		VerticalLayout selectDataForAnalysisLayout = new VerticalLayout();
    	selectDataForAnalysisLayout.addComponent(heading);
    	selectDataForAnalysisLayout.addComponent(browseLabelLayout);

		lblFactorContainer = new VerticalLayout();
		lblVariateContainer = new VerticalLayout();
		tblFactorContainer = new VerticalLayout();
		tblVariateContainer = new VerticalLayout();
		tblVariateContainer.setSpacing(true);
	
		lblFactorContainer.addComponent(lblFactors);
		lblVariateContainer.addComponent(lblVariates);
		tblFactorContainer.addComponent(tblFactors);
		tblVariateContainer.addComponent(tblVariates);
		tblVariateContainer.addComponent(chkVariatesSelectAll);
		
		lblFactorContainer.setMargin(true, false, false, false);
		lblVariateContainer.setMargin(true, true, false, true);
		tblFactorContainer.setMargin(false, false, false, false);
		tblVariateContainer.setMargin(false, true, false, true);
		
		studyDetailsLayout = new GridLayout(10, 3);
		studyDetailsLayout.setWidth("100%");
		studyDetailsLayout.addComponent(lblFactorContainer, 0, 0, 4, 0);
		studyDetailsLayout.addComponent(lblVariateContainer, 5, 0, 9, 0);
		studyDetailsLayout.addComponent(tblFactorContainer, 0, 1, 4, 1);
		studyDetailsLayout.addComponent(tblVariateContainer, 5, 1, 9, 1);
		
		rootLayout = new VerticalLayout();
		rootLayout.setWidth("100%");
		rootLayout.setSpacing(true);
		rootLayout.setMargin(false, false, false, true);
		rootLayout.addComponent(lblPageTitle);
		rootLayout.addComponent(selectDataForAnalysisLayout);
		rootLayout.addComponent(studyDetailsLayout);
		rootLayout.addComponent(buttonArea);
		rootLayout.setComponentAlignment(buttonArea, Alignment.TOP_CENTER);

		addComponent(rootLayout);
	}
	
	protected Component layoutButtonArea() {

		HorizontalLayout buttonLayout = new HorizontalLayout();

		buttonLayout.setSpacing(true);
		buttonLayout.setMargin(true);

		btnCancel = new Button();
		btnNext = new Button();
		btnNext.addStyleName(Bootstrap.Buttons.PRIMARY.styleName());
		btnNext.setEnabled(false);// default

		buttonLayout.addComponent(btnCancel);
		buttonLayout.addComponent(btnNext);
		buttonLayout.setComponentAlignment(btnCancel, Alignment.TOP_CENTER);
		buttonLayout.setComponentAlignment(btnNext, Alignment.TOP_CENTER);
		return buttonLayout;
	}
	
	protected Table initializeFactorsTable() {

		final Table table = new Table();
		table.setImmediate(true);
		table.setWidth("100%");
		table.setHeight("400px");

		BeanContainer<Integer, FactorModel> container = new BeanContainer<Integer, FactorModel>(
				FactorModel.class);
		container.setBeanIdProperty("id");
		table.setContainerDataSource(container);

		String[] columns = new String[] { NAMED_COLUMN_1, NAMED_COLUMN_2 };
		String[] columnHeaders = new String[] { CAMEL_CASE_NAMED_COLUMN_1, CAMEL_CASE_NAMED_COLUMN_2 };
		table.setVisibleColumns(columns);
		table.setColumnHeaders(columnHeaders);

		table.setItemDescriptionGenerator(new ItemDescriptionGenerator() {

			private static final long serialVersionUID = 1L;
			private String description = "<span class=\"gcp-table-header-bold\">%s</span><br>" +
					"<span>Property:</span> %s<br><span>Scale:</span> %s<br>" +
					"<span>Method:</span> %s<br><span>Data Type:</span> %s";
			
			@SuppressWarnings("unchecked")
			public String generateDescription(Component source, Object itemId,
					Object propertyId) {
				BeanContainer<Integer, FactorModel> container = (BeanContainer<Integer, FactorModel>) table
						.getContainerDataSource();
				FactorModel fm = container.getItem(itemId).getBean();
				return String.format(description, fm.getName(), fm.getTrname(), 
						fm.getScname(), fm.getTmname(), fm.getDataType());
			}
		});

		return table;
	}

	protected Table initializeVariatesTable() {

		variatesCheckboxState.clear();

		final Table table = new Table();
		table.setImmediate(true);
		table.setWidth("100%");
		table.setHeight("400px");
		table.setColumnExpandRatio("", 0.5f);
		table.setColumnExpandRatio(NAMED_COLUMN_1, 1);
		table.setColumnExpandRatio(NAMED_COLUMN_2, 4);
		table.setColumnExpandRatio(NAMED_COLUMN_3, 1);
		table.addGeneratedColumn("", new TableColumnGenerator(table));

		table.setItemDescriptionGenerator(new ItemDescriptionGenerator() {

			private static final long serialVersionUID = 1L;
			private String description = "<span class=\"gcp-table-header-bold\">%s</span><br>" +
					"<span>Property:</span> %s<br><span>Scale:</span> %s<br>" +
					"<span>Method:</span> %s<br><span>Data Type:</span> %s";
			
			@SuppressWarnings("unchecked")
			public String generateDescription(Component source, Object itemId,
					Object propertyId) {
				BeanContainer<Integer, VariateModel> container = (BeanContainer<Integer, VariateModel>) table.getContainerDataSource();
				VariateModel vm = container.getItem(itemId).getBean();
				return String.format(description, vm.getName(), vm.getTrname(), 
						vm.getScname(), vm.getTmname(), vm.getDatatype());
			}
		});

		BeanContainer<Integer, VariateModel> container = new BeanContainer<Integer, VariateModel>(
				VariateModel.class);
		container.setBeanIdProperty("id");
		table.setContainerDataSource(container);

		String[] columns = new String[] { "", NAMED_COLUMN_1, NAMED_COLUMN_2, NAMED_COLUMN_3 };
		String[] columnHeaders = new String[] { "<span class='glyphicon glyphicon-ok'></span>", 
				CAMEL_CASE_NAMED_COLUMN_1, CAMEL_CASE_NAMED_COLUMN_2, CAMEL_CASE_NAMED_COLUMN_3};
		table.setVisibleColumns(columns);
		table.setColumnHeaders(columnHeaders);
		table.setColumnWidth("", 18);
		return table;
	}

	private Table[] refreshFactorsAndVariatesTable() {
		Table[] toreturn = new Table[2];
		
		tblFactorContainer.removeAllComponents();
		tblVariateContainer.removeAllComponents();
		tblFactors = initializeFactorsTable();
		tblVariates = initializeVariatesTable();
		tblFactorContainer.addComponent(tblFactors);
		tblVariateContainer.addComponent(tblVariates);
		tblVariateContainer.addComponent(chkVariatesSelectAll);
		toreturn[0] = tblFactors;
		toreturn[1] = tblVariates;
		return toreturn;
	}

	public StudyDataManager getStudyDataManager() {
		if (this.studyDataManager == null) {
            this.studyDataManager = getManagerFactory()
                    .getNewStudyDataManager();
        }
		return this.studyDataManager;
	}

	public ManagerFactory getManagerFactory() {
		return managerFactory;
	}

	public Map<String, Boolean> getVariatesCheckboxState() {
		return variatesCheckboxState;
	}

	public void setVariatesCheckboxState(
			Map<String, Boolean> variatesCheckboxState) {
		this.variatesCheckboxState = variatesCheckboxState;
	}

	public void toggleNextButton(boolean enabled) {
		btnNext.setEnabled(enabled);
	}

	public int getNumOfSelectedVariates() {
		return numOfSelectedVariates;
	}

	public void setNumOfSelectedVariates(int numOfSelectedVariates) {
		this.numOfSelectedVariates = numOfSelectedVariates;
	}
	
	public void showDatasetVariatesDetails(int dataSetId) {

	        try {
	            
	            DataSet ds = studyDataManager.getDataSet(dataSetId);
	            
	            if (getCurrentStudy() == null){
	            	Study study = studyDataManager.getStudy(ds.getStudyId());
		            setCurrentStudy(study);
	            }else if (getCurrentStudy().getId() != ds.getStudyId()){
	            	Study study = studyDataManager.getStudy(ds.getStudyId());
		            setCurrentStudy(study);
	            }
	            
	            factorList = new ArrayList<FactorModel>();
	            variateList = new ArrayList<VariateModel>();
	            
	            for (VariableType factor : ds.getVariableTypes().getFactors().getVariableTypes()){
	            	
	            	if (factor.getStandardVariable().getPhenotypicType() == PhenotypicType.DATASET
	            			) {
                        continue;
                    }
	            	
	            	FactorModel fm = new FactorModel();
	            	fm.setId(factor.getRank());
	            	fm.setName(factor.getLocalName());
	            	fm.setDescription(factor.getLocalDescription());
	            	fm.setScname(factor.getStandardVariable().getScale().getName());
	            	fm.setScaleid(factor.getStandardVariable().getScale().getId());
	            	fm.setTmname(factor.getStandardVariable().getMethod().getName());
	            	fm.setTmethid(factor.getStandardVariable().getMethod().getId());
	            	fm.setTrname(factor.getStandardVariable().getProperty().getName());
	            	fm.setTraitid(factor.getStandardVariable().getProperty().getId());

	            	factorList.add(fm);
	            }
	            
	            for (VariableType variate : ds.getVariableTypes().getVariates().getVariableTypes()){
	            	VariateModel vm = transformVariableTypeToVariateModel(variate);
	            	variateList.add(vm);
	            }
	           
	            setCurrentDatasetName(ds.getName());
	            setCurrentDataSetId(ds.getId());
	            
	            updateFactorsTable(factorList);
	            updateVariatesTable(variateList);

	        } catch (MiddlewareQueryException e) {
	        	LOG.error(e.getMessage(),e);
	            showDatabaseError(this.getWindow());
	        }
	        
	        getManagerFactory().close();
	    }
	    
	public VariateModel transformVariableTypeToVariateModel(
			VariableType variate) {
		VariateModel vm = new VariateModel();
		vm.setId(variate.getRank());
    	vm.setName(variate.getLocalName());
    	vm.setDescription(variate.getLocalDescription());
    	vm.setScname(variate.getStandardVariable().getScale().getName());
    	vm.setScaleid(variate.getStandardVariable().getScale().getId());
    	vm.setTmname(variate.getStandardVariable().getMethod().getName());
    	vm.setTmethid(variate.getStandardVariable().getMethod().getId());
    	vm.setTrname(variate.getStandardVariable().getProperty().getName());
    	vm.setTraitid(variate.getStandardVariable().getProperty().getId());
    	vm.setDatatype(variate.getStandardVariable().getDataType().getName());
    	
    	if (variate.getStandardVariable().isNumeric()){
    		vm.setActive(true);
    		if(variate.getStandardVariable().isNumericCategoricalVariate()) {
    			vm.setNumericCategoricalVariate(true);
    		}
    	} else {
    		vm.setNonNumeric(true);
    	}
    	return vm;
	}

	private void updateFactorsTable(List<FactorModel> factorList){
	   Object[] oldColumns = tblFactors.getVisibleColumns();
       String[] columns = Arrays.copyOf(oldColumns, oldColumns.length, String[].class);
       
       BeanContainer<Integer, FactorModel> container = new BeanContainer<Integer, FactorModel>(FactorModel.class);
       container.setBeanIdProperty("id");
       tblFactors.setContainerDataSource(container);
       
       for (FactorModel f : factorList ){
    	   container.addBean(f);
       }
       
       tblFactors.setContainerDataSource(container);
       
       tblFactors.setVisibleColumns(columns);
    }
	    
	private void updateVariatesTable(List<VariateModel> variateList){ 	   
    	//reset
    	getVariatesCheckboxState().clear();
    	setNumOfSelectedVariates(0);
    	toggleNextButton(false);
    	
    	//load data
        BeanContainer<Integer, VariateModel> container = new BeanContainer<Integer, VariateModel>(VariateModel.class);
        container.setBeanIdProperty("id");
        
        for (VariateModel v : variateList ){
      	   container.addBean(v);
      	   getVariatesCheckboxState().put(v.getName(), v.getActive());
        }
        tblVariates.setContainerDataSource(container);
        tblVariates.setVisibleColumns(new String[]{"",NAMED_COLUMN_1, NAMED_COLUMN_2, NAMED_COLUMN_3});
        tblVariates.setColumnHeaders(new String[]{"<span class='glyphicon glyphicon-ok'></span>",
        		CAMEL_CASE_NAMED_COLUMN_1, CAMEL_CASE_NAMED_COLUMN_2, CAMEL_CASE_NAMED_COLUMN_3});
    }
	  
	private void showDatabaseError(Window window) {
        MessageNotifier.showError(window, 
                messageSource.getMessage(Message.DATABASE_ERROR), 
                "<br />" + messageSource.getMessage(Message.CONTACT_ADMIN_ERROR_DESC));
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

	public List<FactorModel> getFactorList() {
		return factorList;
	}

	public List<VariateModel> getVariateList() {
		return variateList;
	}

	public void setFactorList(List<FactorModel> factorList) {
		this.factorList = factorList;
	}

	public void setVariateList(List<VariateModel> variateList) {
		this.variateList = variateList;
	}

	public ManagerFactoryProvider getManagerFactoryProvider() {
		return managerFactoryProvider;
	}

	public void setManagerFactoryProvider(
			ManagerFactoryProvider managerFactoryProvider) {
		this.managerFactoryProvider = managerFactoryProvider;
	}

	public void setStudyDataManager(StudyDataManager studyDataManager) {
		this.studyDataManager = studyDataManager;
	}

	public void setManagerFactory(ManagerFactory managerFactory) {
		this.managerFactory = managerFactory;
	}	
	
	
}
