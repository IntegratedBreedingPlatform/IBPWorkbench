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
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.math.NumberUtils;
import org.generationcp.commons.breedingview.xml.DesignType;
import org.generationcp.commons.vaadin.spring.InternationalizableComponent;
import org.generationcp.commons.vaadin.spring.SimpleResourceBundleMessageSource;
import org.generationcp.commons.vaadin.theme.Bootstrap;
import org.generationcp.commons.vaadin.ui.ConfirmDialog;
import org.generationcp.commons.vaadin.util.MessageNotifier;
import org.generationcp.ibpworkbench.Message;
import org.generationcp.ibpworkbench.actions.BreedingViewDesignTypeValueChangeListener;
import org.generationcp.ibpworkbench.actions.BreedingViewEnvFactorValueChangeListener;
import org.generationcp.ibpworkbench.actions.BreedingViewReplicatesValueChangeListener;
import org.generationcp.ibpworkbench.actions.RunBreedingViewAction;
import org.generationcp.ibpworkbench.model.SeaEnvironmentModel;
import org.generationcp.ibpworkbench.ui.window.FileUploadBreedingViewOutputWindow;
import org.generationcp.ibpworkbench.ui.window.IContentWindow;
import org.generationcp.ibpworkbench.util.BreedingViewInput;
import org.generationcp.middleware.domain.dms.DataSet;
import org.generationcp.middleware.domain.dms.DataSetType;
import org.generationcp.middleware.domain.dms.PhenotypicType;
import org.generationcp.middleware.domain.dms.TrialEnvironment;
import org.generationcp.middleware.domain.dms.TrialEnvironments;
import org.generationcp.middleware.domain.dms.Variable;
import org.generationcp.middleware.domain.dms.VariableType;
import org.generationcp.middleware.domain.dms.VariableTypeList;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.exceptions.ConfigException;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.manager.Database;
import org.generationcp.middleware.manager.ManagerFactory;
import org.generationcp.middleware.manager.api.StudyDataManager;
import org.generationcp.middleware.pojos.workbench.Project;
import org.generationcp.middleware.pojos.workbench.Tool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.beans.factory.annotation.Value;

import com.mysql.jdbc.StringUtils;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Select;
import com.vaadin.ui.Table;
import com.vaadin.ui.Table.ColumnGenerator;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;

/**
 * 
 * @author Jeffrey Morales
 * 
 */
@Configurable
public class SingleSiteAnalysisDetailsPanel extends VerticalLayout implements InitializingBean,
		InternationalizableComponent {

	private static final String MARGIN_TOP10 = "marginTop10";
	private static final String REPLICATES = "REPLICATES";

	private final class RunBreedingViewButtonListener implements Button.ClickListener {
		private static final long serialVersionUID = -6682011023617457906L;

		@Override
		public void buttonClick(final ClickEvent event) {

			
			if (Boolean.parseBoolean(isServerApp)){
				new RunBreedingViewAction(SingleSiteAnalysisDetailsPanel.this, project)
				.buttonClick(event);
				return;
			}
			
			List<DataSet> dataSets;
			try {

				dataSets = studyDataManager.getDataSetsByType(breedingViewInput.getStudyId(),
						DataSetType.MEANS_DATA);
				if (!dataSets.isEmpty()) {

					DataSet meansDataSet = dataSets.get(0);
					TrialEnvironments envs = studyDataManager
							.getTrialEnvironmentsInDataset(meansDataSet.getId());

					Boolean environmentExists = false;
					for (SeaEnvironmentModel model : getSelectedEnvironments()) {

						TrialEnvironment env = envs.findOnlyOneByLocalName(
								breedingViewInput.getTrialInstanceName(), model.getTrialno());
						if (env != null) {
							environmentExists = true;
							break;
						}

					}

					if (environmentExists) {
						ConfirmDialog
								.show(event.getComponent().getWindow(),
										"",
										"One or more of the selected traits has existing means data. If you save the results of this analysis, the existing values will be overwritten.",
										"OK", "Cancel", new Runnable() {

											@Override
											public void run() {

												new RunBreedingViewAction(
														SingleSiteAnalysisDetailsPanel.this,
														project).buttonClick(event);
											}

										});
					} else {
						new RunBreedingViewAction(SingleSiteAnalysisDetailsPanel.this, project)
								.buttonClick(event);
					}

				} else {
					new RunBreedingViewAction(SingleSiteAnalysisDetailsPanel.this, project)
							.buttonClick(event);
				}

			} catch (MiddlewareQueryException e) {
				new RunBreedingViewAction(SingleSiteAnalysisDetailsPanel.this, project)
						.buttonClick(event);
				LOG.error(e.getMessage(), e);
			} catch (Exception e) {
				new RunBreedingViewAction(SingleSiteAnalysisDetailsPanel.this, project)
						.buttonClick(event);
				LOG.error(e.getMessage(), e);
			}

		}
	}

	private final class FooterCheckBoxListener implements Property.ValueChangeListener {
		private static final long serialVersionUID = 1L;

		@Override
		public void valueChange(ValueChangeEvent event) {
			boolean selected = (Boolean)event.getProperty().getValue();
			if (!selected) {
				disableEnvironmentEntries();
				tblEnvironmentSelection.refreshRowCache();
				return;
			}

			try {

				List<String> invalidEnvs = new ArrayList<String>() {

					@Override
					public String toString() {
						StringBuilder sb = new StringBuilder();
						Iterator<String> itr = this.listIterator();
						while (itr.hasNext()) {
							sb.append("\"" + itr.next() + "\"");
							if (itr.hasNext()) {
								sb.append(",");
							}
						}
						return sb.toString();
					}
				};

				for (Iterator<?> itr = tblEnvironmentSelection.getContainerDataSource()
						.getItemIds().iterator(); itr.hasNext();) {
					SeaEnvironmentModel m = (SeaEnvironmentModel) itr.next();

					Boolean valid = studyDataManager.containsAtLeast2CommonEntriesWithValues(
							getBreedingViewInput().getDatasetId(), m.getLocationId());

					if (!valid) {
						invalidEnvs.add(m.getEnvironmentName());
						m.setActive(false);
					} else {
						m.setActive(true);
					}

				}

				tblEnvironmentSelection.refreshRowCache();

				if (!invalidEnvs.isEmpty()) {
					MessageNotifier
							.showError(
									getWindow(),
									INVALID_SELECTION_STRING,
									getSelEnvFactor().getValue().toString()
											+ " "
											+ invalidEnvs.toString()
											+ " cannot be used for analysis because the plot data is not complete. The data must contain at least 2 common entries with values.");
				}

			} catch (Exception e) {
				LOG.error(e.getMessage(), e);
			}

		}
	}

	private final class EnvironmentCheckBoxListener implements Property.ValueChangeListener {
		private static final long serialVersionUID = 1L;

		@Override
		public void valueChange(ValueChangeEvent event) {

			CheckBox chk = (CheckBox) event.getProperty();
			Boolean val = (Boolean) event.getProperty().getValue();

			if (!val) {
				footerCheckBox.removeListener(footerCheckBoxListener);
				footerCheckBox.setValue(false);
				footerCheckBox.addListener(footerCheckBoxListener);
				return;
			}

			SeaEnvironmentModel model = (SeaEnvironmentModel) chk.getData();

			TrialEnvironments trialEnvironments;
			try {
				trialEnvironments = studyDataManager
						.getTrialEnvironmentsInDataset(getBreedingViewInput().getDatasetId());
				TrialEnvironment trialEnv = trialEnvironments.findOnlyOneByLocalName(
						getSelEnvFactor().getValue().toString(), model.getEnvironmentName());

				if (trialEnv == null) {

					MessageNotifier.showError(getWindow(), INVALID_SELECTION_STRING,
							"\"" + model.getEnvironmentName()
									+ "\" value is not a valid selection for breeding view.");
					chk.setValue(false);
					model.setActive(false);

				} else {

					Boolean valid = studyDataManager.containsAtLeast2CommonEntriesWithValues(
							getBreedingViewInput().getDatasetId(), model.getLocationId());

					if (!valid) {
						MessageNotifier
								.showError(
										getWindow(),
										INVALID_SELECTION_STRING,
										getSelEnvFactor().getValue().toString()
												+ " \""
												+ model.getEnvironmentName()
												+ "\" cannot be used for analysis because the plot data is not complete. The data must contain at least 2 common entries with values.");
						chk.setValue(false);
						model.setActive(false);
					} else {
						model.setActive(val);
					}

				}
			} catch (ConfigException e) {
				LOG.error(e.getMessage(), e);
			} catch (MiddlewareQueryException e) {
				LOG.error(e.getMessage(), e);
			}

		}
	}

	private static final Logger LOG = LoggerFactory.getLogger(SingleSiteAnalysisDetailsPanel.class);
	private static final long serialVersionUID = 1L;

	private static final String REPLICATION_FACTOR = "replication factor";
	private static final String BLOCKING_FACTOR = "blocking factor";
	private static final String ROW_FACTOR = "row in layout";
	private static final String COLUMN_FACTOR = "column in layout";
	private static final String INVALID_SELECTION_STRING = "Invalid Selection";
	private static final String LABEL_BOLD_STYLING = "label-bold";
	private static final String LABEL_WIDTH = "160px";
	private static final String SELECT_BOX_WIDTH = "191px";
	private static final String SELECT_COLUMN = "select";
	private static final String TRIAL_NO_COLUMN = "trialno";
	
	@Value("${workbench.is.server.app}")
	private String isServerApp;

	private SingleSiteAnalysisPanel selectDatasetForBreedingViewPanel;

	private Label lblPageTitle;
	private Label lblTitle;
	private Label lblDatasetName;
	private Label lblDatasourceName;

	private Label lblVersion;
	private Label lblProjectType;
	private Label lblAnalysisName;
	private Label lblSiteEnvironment;
	private Label lblSpecifyEnvFactor;
	private Label lblSelectEnvironmentForAnalysis;
	private Label lblSpecifyNameForAnalysisEnv;
	private Label lblDesign;
	private Label lblDesignType;
	private Label lblReplicates;
	private Label lblBlocks;
	private Label lblSpecifyRowFactor;
	private Label lblSpecifyColumnFactor;
	private Label lblGenotypes;
	private Label lblDataSelectedForAnalysisHeader;
	private Label lblChooseEnvironmentHeader;
	private Label lblChooseEnvironmentDescription;
	private Label lblChooseEnvironmentForAnalysisDescription;
	private Label lblSpecifyDesignDetailsHeader;
	private Label lblSpecifyGenotypesHeader;
	private Button btnRun;
	private Button btnUpload;
	private Button btnReset;
	private Button btnBack;
	private TextField txtVersion;
	private Label valueProjectType;
	private TextField txtAnalysisName;
	private TextField txtNameForAnalysisEnv;
	private Label valueDatasetName;
	private Label valueDatasourceName;
	private Select selDesignType;
	private Select selEnvFactor;
	private Select selReplicates;
	private Select selBlocks;
	private Select selRowFactor;
	private Select selColumnFactor;
	private Select selGenotypes;

	private VerticalLayout blockRowColumnContainer;

	private CheckBox footerCheckBox;

	private Map<String, Boolean> environmentsCheckboxState;

	private VerticalLayout tblEnvironmentLayout;
	private Table tblEnvironmentSelection;

	private BreedingViewInput breedingViewInput;
	private Tool tool;
	private List<VariableType> factorsInDataset;
	private List<VariableType> trialVariablesInDataset;

	private Project project;

	private VerticalLayout mainLayout;

	private ManagerFactory managerFactory;

	private StudyDataManager studyDataManager;

	@Autowired
	private SimpleResourceBundleMessageSource messageSource;

	private Property.ValueChangeListener envCheckBoxListener;
	private Property.ValueChangeListener footerCheckBoxListener;

	public SingleSiteAnalysisDetailsPanel() {
		setWidth("100%");
	}

	public SingleSiteAnalysisDetailsPanel(Tool tool, BreedingViewInput breedingViewInput,
			List<VariableType> factorsInDataset, List<VariableType> trialVariablesInDataset,
			Project project, StudyDataManager studyDataManager, ManagerFactory managerFactory,
			SingleSiteAnalysisPanel selectDatasetForBreedingViewPanel) {

		this.tool = tool;
		this.setBreedingViewInput(breedingViewInput);
		this.factorsInDataset = factorsInDataset;
		this.trialVariablesInDataset = trialVariablesInDataset;
		this.project = project;
		this.studyDataManager = studyDataManager;
		this.managerFactory = managerFactory;
		this.selectDatasetForBreedingViewPanel = selectDatasetForBreedingViewPanel;

		setWidth("100%");

	}

	public Tool getTool() {
		return tool;
	}

	public TextField getTxtVersion() {
		return txtVersion;
	}

	public Select getSelDesignType() {
		return selDesignType;
	}

	public BreedingViewInput getBreedingViewInput() {
		return breedingViewInput;
	}

	public Label getValueProjectType() {
		return valueProjectType;
	}

	public TextField getTxtAnalysisName() {
		return txtAnalysisName;
	}

	public TextField getTxtNameForAnalysisEnv() {
		return txtNameForAnalysisEnv;
	}

	public Select getSelEnvFactor() {
		return selEnvFactor;
	}

	public Select getSelReplicates() {
		return selReplicates;
	}

	public Select getSelBlocks() {
		return selBlocks;
	}

	public Select getSelRowFactor() {
		return selRowFactor;
	}

	public Select getSelColumnFactor() {
		return selColumnFactor;
	}

	public Select getSelGenotypes() {
		return selGenotypes;
	}

	protected void initialize() {
		// not yet implemented
	}

	protected void initializeComponents() {

		lblPageTitle = new Label();
		lblPageTitle.setStyleName(Bootstrap.Typography.H1.styleName());

		environmentsCheckboxState = new HashMap<String, Boolean>();

		tblEnvironmentLayout = new VerticalLayout();
		tblEnvironmentLayout.setSizeUndefined();
		tblEnvironmentLayout.setSpacing(true);
		tblEnvironmentLayout.setWidth("100%");

		tblEnvironmentSelection = new Table();
		tblEnvironmentSelection.setHeight("200px");
		tblEnvironmentSelection.setWidth("100%");

		setBlockRowColumnContainer(new VerticalLayout());

		envCheckBoxListener = new EnvironmentCheckBoxListener();

		tblEnvironmentSelection.addGeneratedColumn(SELECT_COLUMN, new ColumnGenerator() {

			private static final long serialVersionUID = 8164025367842219781L;

			@Override
			public Object generateCell(Table source, Object itemId, Object columnId) {
				SeaEnvironmentModel item = (SeaEnvironmentModel) itemId;

				CheckBox chk = new CheckBox();
				chk.setData(item);
				chk.setValue(item.getActive());
				chk.setImmediate(true);
				chk.addListener(envCheckBoxListener);
				return chk;
			}

		});

		footerCheckBoxListener = new FooterCheckBoxListener();

		footerCheckBox = new CheckBox("Select All", false);
		footerCheckBox.addListener(footerCheckBoxListener);
		footerCheckBox.setImmediate(true);

		tblEnvironmentLayout.addComponent(tblEnvironmentSelection);
		tblEnvironmentLayout.addComponent(footerCheckBox);

		mainLayout = new VerticalLayout();

		lblTitle = new Label();
		lblTitle.setStyleName(Bootstrap.Typography.H4.styleName());
		lblTitle.addStyleName(LABEL_BOLD_STYLING);
		lblTitle.setHeight("25px");

		lblDatasetName = new Label();
		lblDatasetName.setContentMode(Label.CONTENT_XHTML);
		lblDatasetName.setStyleName(LABEL_BOLD_STYLING);
		lblDatasourceName = new Label();
		lblDatasourceName.setContentMode(Label.CONTENT_XHTML);
		lblDatasourceName.setStyleName(LABEL_BOLD_STYLING);

		lblVersion = new Label();
		lblVersion.setStyleName(LABEL_BOLD_STYLING);
		lblProjectType = new Label();
		lblProjectType.setStyleName(LABEL_BOLD_STYLING);
		lblProjectType.setWidth("100px");
		lblAnalysisName = new Label();
		lblAnalysisName.setContentMode(Label.CONTENT_XHTML);
		lblAnalysisName.setStyleName(LABEL_BOLD_STYLING);
		lblSiteEnvironment = new Label();
		lblSpecifyEnvFactor = new Label();
		lblSpecifyEnvFactor.setContentMode(Label.CONTENT_XHTML);
		lblSpecifyEnvFactor.setStyleName(LABEL_BOLD_STYLING);
		lblSelectEnvironmentForAnalysis = new Label();
		lblSelectEnvironmentForAnalysis.setContentMode(Label.CONTENT_XHTML);
		lblSelectEnvironmentForAnalysis.setStyleName(LABEL_BOLD_STYLING);
		lblSpecifyNameForAnalysisEnv = new Label();
		lblSpecifyNameForAnalysisEnv.setContentMode(Label.CONTENT_XHTML);
		lblSpecifyNameForAnalysisEnv.setStyleName(LABEL_BOLD_STYLING);
		lblDesign = new Label();
		lblDesignType = new Label();
		lblDesignType.setContentMode(Label.CONTENT_XHTML);
		lblDesignType.setStyleName(LABEL_BOLD_STYLING);
		lblDesignType.setWidth(LABEL_WIDTH);
		lblReplicates = new Label();
		lblReplicates.setContentMode(Label.CONTENT_XHTML);
		lblReplicates.setWidth(LABEL_WIDTH);
		lblReplicates.setStyleName(LABEL_BOLD_STYLING);
		lblBlocks = new Label();
		lblBlocks.setContentMode(Label.CONTENT_XHTML);
		lblBlocks.setWidth(LABEL_WIDTH);
		lblBlocks.setStyleName(LABEL_BOLD_STYLING);
		lblSpecifyRowFactor = new Label();
		lblSpecifyRowFactor.setContentMode(Label.CONTENT_XHTML);
		lblSpecifyRowFactor.setWidth(LABEL_WIDTH);
		lblSpecifyRowFactor.setStyleName(LABEL_BOLD_STYLING);
		lblSpecifyColumnFactor = new Label();
		lblSpecifyColumnFactor.setContentMode(Label.CONTENT_XHTML);
		lblSpecifyColumnFactor.setWidth(LABEL_WIDTH);
		lblSpecifyColumnFactor.setStyleName(LABEL_BOLD_STYLING);
		lblGenotypes = new Label();
		lblGenotypes.setWidth(LABEL_WIDTH);
		lblGenotypes.setStyleName(LABEL_BOLD_STYLING);

		lblDataSelectedForAnalysisHeader = new Label(
				"<span class='bms-dataset' style='position:relative; top: -1px; color: #FF4612; "
						+ "font-size: 20px; font-weight: bold;'></span><b>&nbsp;"
						+ messageSource.getMessage(Message.BV_DATA_SELECTED_FOR_ANALYSIS_HEADER)
						+ "</b>", Label.CONTENT_XHTML);
		lblDataSelectedForAnalysisHeader.setStyleName(Bootstrap.Typography.H3.styleName());

		lblChooseEnvironmentHeader = new Label(
				"<span class='bms-environments' style='position:relative; top: -2px; color: #0076A9; "
						+ "font-size: 25px; font-weight: bold;'></span><b>&nbsp;"
						+ "<span style='position:relative; top: -3px;'>"
						+ messageSource.getMessage(Message.BV_CHOOSE_ENVIRONMENT_HEADER)
						+ "</span></b>", Label.CONTENT_XHTML);
		lblChooseEnvironmentHeader.setStyleName(Bootstrap.Typography.H3.styleName());

		lblChooseEnvironmentDescription = new Label();
		lblChooseEnvironmentForAnalysisDescription = new Label();
		lblChooseEnvironmentForAnalysisDescription.setContentMode(Label.CONTENT_XHTML);
		lblChooseEnvironmentForAnalysisDescription.setStyleName(LABEL_BOLD_STYLING);

		lblSpecifyDesignDetailsHeader = new Label(
				"<span class='bms-exp-design' style='color: #9A8478; "
						+ "font-size: 22px; font-weight: bold;'></span><b>&nbsp;"
						+ messageSource.getMessage(Message.BV_SPECIFY_DESIGN_DETAILS_HEADER)
						+ "</b>", Label.CONTENT_XHTML);
		lblSpecifyDesignDetailsHeader.setStyleName(Bootstrap.Typography.H3.styleName());

		lblSpecifyGenotypesHeader = new Label("<span class='bms-factors' style='color: #39B54A; "
				+ "font-size: 20px; font-weight: bold;'></span><b>&nbsp;"
				+ messageSource.getMessage(Message.BV_SPECIFY_GENOTYPES_HEADER) + "</b>",
				Label.CONTENT_XHTML);
		lblSpecifyGenotypesHeader.setStyleName(Bootstrap.Typography.H3.styleName());

		txtVersion = new TextField();
		txtVersion.setNullRepresentation("");

		if (!StringUtils.isNullOrEmpty(getBreedingViewInput().getVersion())) {

			txtVersion.setValue(getBreedingViewInput().getVersion());
			txtVersion.setReadOnly(true);
			txtVersion.setRequired(false);

		} else {

			txtVersion.setNullSettingAllowed(false);
			txtVersion.setRequired(false);

		}

		valueProjectType = new Label();
		valueProjectType.setValue("Field Trial");

		valueDatasetName = new Label();
		valueDatasetName.setWidth("100%");
		valueDatasetName.setValue(getBreedingViewInput().getDatasetName());

		valueDatasourceName = new Label();
		valueDatasourceName.setWidth("100%");
		valueDatasourceName.setValue(getBreedingViewInput().getDatasetSource());

		txtAnalysisName = new TextField();
		txtAnalysisName.setNullRepresentation("");
		if (!StringUtils.isNullOrEmpty(getBreedingViewInput().getBreedingViewAnalysisName())) {
			txtAnalysisName.setValue(getBreedingViewInput().getBreedingViewAnalysisName());
		}
		txtAnalysisName.setRequired(false);
		txtAnalysisName.setWidth("450");

		selEnvFactor = new Select();
		selEnvFactor.setImmediate(true);
		populateChoicesForEnvironmentFactor();
		selEnvFactor.setNullSelectionAllowed(true);
		selEnvFactor.setNewItemsAllowed(false);

		populateChoicesForEnvForAnalysis();

		txtNameForAnalysisEnv = new TextField();
		txtNameForAnalysisEnv.setNullRepresentation("");
		txtNameForAnalysisEnv.setRequired(false);

		selDesignType = new Select();
		selDesignType.setImmediate(true);
		selDesignType.setNullSelectionAllowed(true);
		selDesignType.setNewItemsAllowed(false);
		selDesignType.addItem(DesignType.INCOMPLETE_BLOCK_DESIGN.getName());
		selDesignType.addItem(DesignType.RANDOMIZED_BLOCK_DESIGN.getName());
		selDesignType.addItem(DesignType.ROW_COLUMN_DESIGN.getName());
		selDesignType.setWidth(SELECT_BOX_WIDTH);

		selReplicates = new Select();
		selReplicates.setImmediate(true);
		populateChoicesForReplicates();
		selReplicates.setNullSelectionAllowed(true);
		selReplicates.setNewItemsAllowed(false);
		selReplicates.setWidth(SELECT_BOX_WIDTH);

		selBlocks = new Select();
		selBlocks.setImmediate(true);
		selBlocks.setEnabled(false);
		populateChoicesForBlocks();
		selBlocks.setNullSelectionAllowed(false);
		selBlocks.setNewItemsAllowed(false);
		selBlocks.setWidth(SELECT_BOX_WIDTH);

		selRowFactor = new Select();
		selRowFactor.setImmediate(true);
		populateChoicesForRowFactor();
		selRowFactor.setNullSelectionAllowed(false);
		selRowFactor.setNewItemsAllowed(false);
		selRowFactor.setWidth(SELECT_BOX_WIDTH);

		selColumnFactor = new Select();
		selColumnFactor.setImmediate(true);
		populateChoicesForColumnFactor();
		selColumnFactor.setNullSelectionAllowed(false);
		selColumnFactor.setNewItemsAllowed(false);
		selColumnFactor.setWidth(SELECT_BOX_WIDTH);

		refineChoicesForBlocksReplicationRowAndColumnFactos();

		setSelGenotypes(new Select());
		getSelGenotypes().setImmediate(true);
		populateChoicesForGenotypes();
		getSelGenotypes().setNullSelectionAllowed(true);
		getSelGenotypes().setNewItemsAllowed(false);
		getSelGenotypes().setWidth(SELECT_BOX_WIDTH);

		btnRun = new Button();
		btnRun.addStyleName(Bootstrap.Buttons.PRIMARY.styleName());
		btnUpload = new Button();
		btnUpload.addStyleName(Bootstrap.Buttons.PRIMARY.styleName());
		btnReset = new Button();
		btnBack = new Button();

		checkDesignFactor();

	}

	private void populateChoicesForEnvironmentFactor() {

		if (this.trialVariablesInDataset == null) {
			return;
		}

		for (VariableType factor : trialVariablesInDataset) {
			if (factor.getStandardVariable().getPhenotypicType() == PhenotypicType.TRIAL_ENVIRONMENT) {
				this.selEnvFactor.addItem(factor.getLocalName());
				if (PhenotypicType.TRIAL_ENVIRONMENT.getLabelList().contains(factor.getLocalName())) {
					this.selEnvFactor.setValue(factor.getLocalName());
				}
			}
		}

		selEnvFactor.select(selEnvFactor.getItemIds().iterator().next());

		if (this.selEnvFactor.getItemIds().isEmpty()) {
			this.selEnvFactor.setEnabled(false);
		} else {
			this.selEnvFactor.setEnabled(true);
		}

	}

	public VariableType getVariableByLocalName(List<VariableType> variables, String name) {
		for (VariableType factor : variables) {
			if (factor.getLocalName().equals(name)) {
				return factor;
			}
		}
		return null;
	}

	public void populateChoicesForEnvForAnalysis() {

		footerCheckBox.setValue(false);
		String trialInstanceFactor = "";

		try {
			environmentsCheckboxState.clear();
			tblEnvironmentSelection.removeAllItems();
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
		}

		String envFactorName = (String) this.selEnvFactor.getValue();

		VariableType factor = getVariableByLocalName(trialVariablesInDataset, envFactorName);

		if (factor == null) {
			return;
		}

		try {

			BeanItemContainer<SeaEnvironmentModel> container = new BeanItemContainer<SeaEnvironmentModel>(
					SeaEnvironmentModel.class);
			tblEnvironmentSelection.setContainerDataSource(container);

			VariableTypeList trialEnvFactors = studyDataManager
					.getDataSet(getBreedingViewInput().getDatasetId()).getVariableTypes()
					.getFactors();

			for (VariableType f : trialEnvFactors.getVariableTypes()) {

				// Always Show the TRIAL INSTANCE Factor
				if (f.getStandardVariable().getStoredIn().getId() == TermId.TRIAL_INSTANCE_STORAGE
						.getId()) {
					trialInstanceFactor = f.getLocalName();
				}

			}

			TrialEnvironments trialEnvironments;
			trialEnvironments = studyDataManager
					.getTrialEnvironmentsInDataset(getBreedingViewInput().getDatasetId());

			for (TrialEnvironment env : trialEnvironments.getTrialEnvironments()) {

				Variable trialVar = env.getVariables().findByLocalName(trialInstanceFactor);
				Variable selectedEnvVar = env.getVariables().findByLocalName(envFactorName);

				if (trialVar == null || selectedEnvVar == null) {
					continue;
				}

				TrialEnvironment temp = trialEnvironments.findOnlyOneByLocalName(
						envFactorName, selectedEnvVar.getValue());

				if (temp == null) {
					continue;
				}

				SeaEnvironmentModel bean = new SeaEnvironmentModel();
				bean.setActive(false);
				bean.setEnvironmentName(selectedEnvVar.getValue());
				bean.setTrialno(trialVar.getValue());
				bean.setLocationId(temp.getId());
				container.addBean(bean);

			}

			if (trialInstanceFactor.equalsIgnoreCase(envFactorName)) {
				tblEnvironmentSelection.setVisibleColumns(new Object[] { SELECT_COLUMN, TRIAL_NO_COLUMN });
				tblEnvironmentSelection.setColumnHeaders(new String[] { "SELECT",
						trialInstanceFactor });
				tblEnvironmentSelection.setColumnWidth(SELECT_COLUMN, 45);
				tblEnvironmentSelection.setColumnWidth(TRIAL_NO_COLUMN, -1);
				tblEnvironmentSelection.setWidth("45%");
				getBreedingViewInput().setTrialInstanceName(trialInstanceFactor);
			} else {
				tblEnvironmentSelection.setVisibleColumns(new Object[] { SELECT_COLUMN, TRIAL_NO_COLUMN,
						"environmentName" });
				tblEnvironmentSelection.setColumnHeaders(new String[] { "SELECT",
						trialInstanceFactor, envFactorName });
				tblEnvironmentSelection.setColumnWidth(SELECT_COLUMN, 45);
				tblEnvironmentSelection.setColumnWidth(TRIAL_NO_COLUMN, 60);
				tblEnvironmentSelection.setColumnWidth("environmentName", 500);
				tblEnvironmentSelection.setWidth("90%");

				getBreedingViewInput().setTrialInstanceName(trialInstanceFactor);
			}

		} catch (ConfigException e) {
			LOG.error(e.getMessage(), e);
		} catch (MiddlewareQueryException e) {
			LOG.error(e.getMessage(), e);

		}


	}

	protected void populateChoicesForGenotypes() {

		for (VariableType factor : factorsInDataset) {
			if (factor.getStandardVariable().getPhenotypicType() == PhenotypicType.GERMPLASM) {
				this.getSelGenotypes().addItem(factor.getLocalName());
				this.getSelGenotypes().setValue(factor.getLocalName());
			}
		}

		getSelGenotypes().select(getSelGenotypes().getItemIds().iterator().next());

	}

	protected void populateChoicesForReplicates() {
		for (VariableType factor : this.factorsInDataset) {
			if (factor.getStandardVariable().getProperty().getName().toString().trim()
					.equalsIgnoreCase(REPLICATION_FACTOR)) {
				this.selReplicates.addItem(factor.getLocalName());
				this.selReplicates.setValue(factor.getLocalName());
			}
		}

		if (this.selReplicates.getItemIds().isEmpty()) {
			this.selReplicates.setEnabled(false);
		} else {
			this.selReplicates.setEnabled(true);
		}
	}

	protected void populateChoicesForBlocks() {

		for (VariableType factor : this.factorsInDataset) {
			if (factor.getStandardVariable().getProperty().getName().toString().trim()
					.equalsIgnoreCase(BLOCKING_FACTOR)) {
				this.selBlocks.addItem(factor.getLocalName());
				this.selBlocks.setValue(factor.getLocalName());
				selBlocks.setEnabled(true);
			}
		}

	}

	protected void populateChoicesForRowFactor() {

		for (VariableType factor : this.factorsInDataset) {
			if (factor.getStandardVariable().getProperty().getName().toString().trim()
					.equalsIgnoreCase(ROW_FACTOR)) {
				this.selRowFactor.addItem(factor.getLocalName());
				this.selRowFactor.setValue(factor.getLocalName());
			}
		}

	}

	protected void populateChoicesForColumnFactor() {

		for (VariableType factor : this.factorsInDataset) {
			if (factor.getStandardVariable().getProperty().getName().toString().trim()
					.equalsIgnoreCase(COLUMN_FACTOR)) {
				this.selColumnFactor.addItem(factor.getLocalName());
				this.selColumnFactor.setValue(factor.getLocalName());
			}
		}

	}

	public void refineChoicesForBlocksReplicationRowAndColumnFactos() {
		if (this.selReplicates.getValue() != null) {
			this.selBlocks.removeItem(this.selReplicates.getValue());
			this.selRowFactor.removeItem(this.selReplicates.getValue());
			this.selColumnFactor.removeItem(this.selReplicates.getValue());
		}

		if (this.selBlocks.getValue() != null) {
			this.selReplicates.removeItem(this.selBlocks.getValue());
			this.selRowFactor.removeItem(this.selBlocks.getValue());
			this.selColumnFactor.removeItem(this.selBlocks.getValue());
		}

		if (this.selRowFactor.getValue() != null) {
			this.selReplicates.removeItem(this.selRowFactor.getValue());
			this.selBlocks.removeItem(this.selRowFactor.getValue());
			this.selColumnFactor.removeItem(this.selRowFactor.getValue());
		}

		if (this.selColumnFactor.getValue() != null) {
			this.selReplicates.removeItem(this.selColumnFactor.getValue());
			this.selBlocks.removeItem(this.selColumnFactor.getValue());
			this.selRowFactor.removeItem(this.selColumnFactor.getValue());
		}
	}

	public void checkDesignFactor() {
		String designFactor = null;
		int designType = retrieveExperimentalDesignTypeID();
		if (designType != 0) {

			if (designType == TermId.RANDOMIZED_COMPLETE_BLOCK.getId()) {
				designFactor = DesignType.RANDOMIZED_BLOCK_DESIGN.getName();
				displayRandomizedBlockDesignElements();
			} else if (designType == TermId.RESOLVABLE_INCOMPLETE_BLOCK.getId()
					|| designType == TermId.RESOLVABLE_INCOMPLETE_BLOCK_LATIN.getId()) {
				designFactor = DesignType.INCOMPLETE_BLOCK_DESIGN.getName();
				displayIncompleteBlockDesignElements();
			} else if (designType == TermId.RESOLVABLE_INCOMPLETE_ROW_COL.getId()
					|| designType == TermId.RESOLVABLE_INCOMPLETE_ROW_COL_LATIN.getId()) {
				designFactor = DesignType.ROW_COLUMN_DESIGN.getName();
				displayRowColumnDesignElements();
			}

			selDesignType.setValue(designFactor);
		} else {
			selDesignType.select(null);
		}


	}

	protected int retrieveExperimentalDesignTypeID() {
		try {
			String expDesign = studyDataManager.getGeolocationPropValue(Database.LOCAL,
					TermId.EXPERIMENT_DESIGN_FACTOR.getId(), breedingViewInput.getStudyId());
			if (expDesign != null && !("").equals(expDesign.trim())
					&& NumberUtils.isNumber(expDesign)) {
				return Integer.parseInt(expDesign);
			}
		} catch (MiddlewareQueryException e) {
			LOG.error(e.getMessage(), e);
		}

		return 0;
	}


	protected void initializeLayout() {

		mainLayout.setSizeUndefined();
		mainLayout.setWidth("100%");
		mainLayout.setSpacing(true);

		VerticalLayout selectedInfoLayout = new VerticalLayout();
		selectedInfoLayout.setSizeUndefined();
		selectedInfoLayout.setWidth("100%");
		selectedInfoLayout.setSpacing(true);

		HorizontalLayout row1 = new HorizontalLayout();
		row1.setSpacing(true);
		row1.addComponent(lblDataSelectedForAnalysisHeader);

		HorizontalLayout row2a = new HorizontalLayout();
		row2a.setSpacing(true);
		row2a.addComponent(lblDatasetName);
		row2a.addComponent(valueDatasetName);
		HorizontalLayout row2b = new HorizontalLayout();
		row2b.setSpacing(true);
		row2b.addComponent(lblProjectType);
		row2b.addComponent(valueProjectType);

		GridLayout row2 = new GridLayout(2, 1);
		row2.setSizeUndefined();
		row2.setWidth("100%");
		row2.setColumnExpandRatio(0, 0.45f);
		row2.setColumnExpandRatio(1, 0.55f);
		row2.addComponent(row2a);
		row2.addComponent(row2b);

		HorizontalLayout row3 = new HorizontalLayout();
		row3.setSpacing(true);
		row3.addComponent(lblDatasourceName);
		row3.addComponent(valueDatasourceName);

		VerticalLayout row4 = new VerticalLayout();
		row4.setSpacing(true);
		row4.addComponent(lblAnalysisName);
		row4.addComponent(txtAnalysisName);

		selectedInfoLayout.addComponent(row1);
		selectedInfoLayout.addComponent(row2);
		selectedInfoLayout.addComponent(row3);
		selectedInfoLayout.addComponent(row4);

		GridLayout chooseEnvironmentLayout = new GridLayout(2, 9);
		chooseEnvironmentLayout.setColumnExpandRatio(0, 4);
		chooseEnvironmentLayout.setColumnExpandRatio(1, 2);
		chooseEnvironmentLayout.setWidth("100%");
		chooseEnvironmentLayout.setSpacing(true);
		chooseEnvironmentLayout.setMargin(false, true, true, false);
		chooseEnvironmentLayout.addComponent(lblChooseEnvironmentHeader, 0, 0, 1, 0);
		chooseEnvironmentLayout.addComponent(lblChooseEnvironmentDescription, 0, 1, 1, 1);
		chooseEnvironmentLayout.addComponent(lblSpecifyEnvFactor, 0, 2);
		chooseEnvironmentLayout.addComponent(selEnvFactor, 1, 2);
		chooseEnvironmentLayout
				.addComponent(lblChooseEnvironmentForAnalysisDescription, 0, 3, 1, 3);
		chooseEnvironmentLayout.addComponent(tblEnvironmentLayout, 0, 4, 1, 4);
		chooseEnvironmentLayout.addComponent(lblVersion, 0, 5);
		chooseEnvironmentLayout.addComponent(txtVersion, 1, 5);

		VerticalLayout designDetailsWrapper = new VerticalLayout();

		GridLayout designDetailsLayout = new GridLayout(2, 3);
		designDetailsLayout.setColumnExpandRatio(0, 0);
		designDetailsLayout.setColumnExpandRatio(1, 1);
		designDetailsLayout.setWidth("100%");
		designDetailsLayout.setSpacing(true);
		designDetailsLayout.setMargin(false, false, false, false);
		designDetailsLayout.addComponent(lblSpecifyDesignDetailsHeader, 0, 0, 1, 0);
		designDetailsLayout.addComponent(lblDesignType, 0, 1);
		designDetailsLayout.addComponent(selDesignType, 1, 1);
		designDetailsLayout.addComponent(lblReplicates, 0, 2);
		designDetailsLayout.addComponent(selReplicates, 1, 2);

		designDetailsWrapper.addComponent(designDetailsLayout);

		GridLayout gLayout = new GridLayout(2, 2);
		gLayout.setColumnExpandRatio(0, 0);
		gLayout.setColumnExpandRatio(1, 1);
		gLayout.setWidth("100%");
		gLayout.setSpacing(true);
		gLayout.addStyleName(MARGIN_TOP10);
		gLayout.addComponent(getLblSpecifyGenotypesHeader(), 0, 0, 1, 0);
		gLayout.addComponent(getLblGenotypes(), 0, 1);
		gLayout.addComponent(getSelGenotypes(), 1, 1);
		getBlockRowColumnContainer().addComponent(gLayout);

		designDetailsWrapper.addComponent(getBlockRowColumnContainer());

		mainLayout.addComponent(lblPageTitle);
		mainLayout.addComponent(new Label(""));

		VerticalLayout subMainLayout = new VerticalLayout();
		subMainLayout.addComponent(lblTitle);
		subMainLayout.addComponent(selectedInfoLayout);
		mainLayout.addComponent(subMainLayout);

		HorizontalLayout combineLayout = new HorizontalLayout();
		combineLayout.setSizeUndefined();
		combineLayout.setWidth("100%");
		combineLayout.addComponent(chooseEnvironmentLayout);
		combineLayout.addComponent(designDetailsWrapper);
		mainLayout.addComponent(combineLayout);

		HorizontalLayout combineLayout2 = new HorizontalLayout();
		combineLayout2.setSpacing(true);
		combineLayout2.addComponent(btnBack);
		combineLayout2.addComponent(btnReset);
		combineLayout2.addComponent(btnRun);
		combineLayout2.addComponent(btnUpload);
		combineLayout2.setComponentAlignment(btnReset, Alignment.TOP_CENTER);
		combineLayout2.setComponentAlignment(btnRun, Alignment.TOP_CENTER);
		combineLayout2.setComponentAlignment(btnUpload, Alignment.TOP_CENTER);
		mainLayout.addComponent(combineLayout2);
		mainLayout.setComponentAlignment(combineLayout2, Alignment.TOP_CENTER);

		mainLayout.setMargin(new MarginInfo(false, true, true, true));

		addComponent(mainLayout);
	}

	private void reset() {

		selEnvFactor.select(selEnvFactor.getItemIds().iterator().next());
		checkDesignFactor();
		selReplicates.select(selReplicates.getItemIds().iterator().next());
		getSelGenotypes().select(getSelGenotypes().getItemIds().iterator().next());
		footerCheckBox.setValue(false);
		txtAnalysisName.setValue(getBreedingViewInput().getBreedingViewAnalysisName());

	}

	protected void initializeActions() {

		btnBack.addListener(new Button.ClickListener() {

			private static final long serialVersionUID = 3878612968330447329L;

			@Override
			public void buttonClick(ClickEvent event) {

				IContentWindow w = (IContentWindow) event.getComponent().getWindow();
				selectDatasetForBreedingViewPanel.setParent(null);
				w.showContent(selectDatasetForBreedingViewPanel);

			}
		});

		btnReset.addListener(new Button.ClickListener() {

			private static final long serialVersionUID = 3878612968330447329L;

			@Override
			public void buttonClick(ClickEvent event) {

				reset();

			}
		});

		Button.ClickListener runBreedingView = new RunBreedingViewButtonListener();

		btnRun.addListener(runBreedingView);
		btnRun.setClickShortcut(KeyCode.ENTER);
		btnRun.addStyleName("primary");
		
		btnUpload.addListener(new Button.ClickListener() {
			
			private static final long serialVersionUID = 1L;

			@Override
			public void buttonClick(ClickEvent event) {
				Map<String, Boolean> visibleTraitsMap = new HashMap<>();
				for (VariableType factor : factorsInDataset){
					visibleTraitsMap.put(factor.getLocalName(), true);
				}
				visibleTraitsMap.putAll(breedingViewInput.getVariatesActiveState());
				
				FileUploadBreedingViewOutputWindow window = new FileUploadBreedingViewOutputWindow(event.getComponent().getWindow(), breedingViewInput.getStudyId(), project, visibleTraitsMap);
				
				event.getComponent().getWindow().addWindow(window);
				
			}
		});
		btnUpload.addStyleName("primary");

		selDesignType.addListener(new BreedingViewDesignTypeValueChangeListener(this));
		selReplicates.addListener(new BreedingViewReplicatesValueChangeListener(this));
		selEnvFactor.addListener(new BreedingViewEnvFactorValueChangeListener(this));
	}

	protected void assemble() {
		initialize();
		initializeComponents();
		initializeLayout();
		initializeActions();
	}

	@Override
	public void afterPropertiesSet() {
		assemble();
		managerFactory.close();
	}

	@Override
	public void attach() {
		super.attach();

		updateLabels();
	}

	@Override
	public void updateLabels() {
		messageSource.setValue(lblVersion, Message.BV_VERSION);
		messageSource.setValue(lblProjectType, Message.BV_PROJECT_TYPE);
		messageSource.setValue(lblAnalysisName, Message.BV_ANALYSIS_NAME);
		messageSource.setValue(lblSiteEnvironment, Message.BV_SITE_ENVIRONMENT);
		messageSource.setValue(lblSpecifyEnvFactor, Message.BV_SPECIFY_ENV_FACTOR);
		messageSource.setValue(lblSelectEnvironmentForAnalysis, Message.BV_SELECT_ENV_FOR_ANALYSIS);
		messageSource.setValue(lblSpecifyNameForAnalysisEnv,
				Message.BV_SPECIFY_NAME_FOR_ANALYSIS_ENV);
		messageSource.setValue(lblDesign, Message.BV_DESIGN);
		messageSource.setValue(lblDesignType, Message.DESIGN_TYPE);
		messageSource.setValue(getLblReplicates(), Message.BV_SPECIFY_REPLICATES);
		messageSource.setValue(getLblBlocks(), Message.BV_SPECIFY_BLOCKS);
		messageSource.setValue(getLblSpecifyRowFactor(), Message.BV_SPECIFY_ROW_FACTOR);
		messageSource.setValue(getLblSpecifyColumnFactor(), Message.BV_SPECIFY_COLUMN_FACTOR);
		messageSource.setValue(getLblGenotypes(), Message.BV_GENOTYPES);
		
		if (Boolean.parseBoolean(isServerApp)){
			messageSource.setCaption(btnRun, Message.DOWNLOAD_INPUT_FILES);
			btnUpload.setVisible(true);
			btnUpload.setCaption("Upload Output Files to BMS");
		}else{
			messageSource.setCaption(btnRun, Message.RUN_BREEDING_VIEW);
			btnUpload.setVisible(false);
		}
		messageSource.setCaption(btnReset, Message.RESET);
		messageSource.setCaption(btnBack, Message.BACK);

		messageSource.setValue(lblTitle, Message.BV_TITLE);
		messageSource.setValue(lblPageTitle, Message.TITLE_SSA);
		messageSource.setValue(lblDatasetName, Message.BV_DATASET_NAME);
		messageSource.setValue(lblDatasourceName, Message.BV_DATASOURCE_NAME);
		messageSource.setValue(lblChooseEnvironmentDescription,
				Message.BV_CHOOSE_ENVIRONMENT_DESCRIPTION);
		messageSource.setValue(lblChooseEnvironmentForAnalysisDescription,
				Message.BV_CHOOSE_ENVIRONMENT_FOR_ANALYSIS_DESC);
	}

	public void setBreedingViewInput(BreedingViewInput breedingViewInput) {
		this.breedingViewInput = breedingViewInput;
	}

	public ManagerFactory getManagerFactory() {
		return managerFactory;
	}

	public void setManagerFactory(ManagerFactory managerFactory) {
		this.managerFactory = managerFactory;
	}

	public Label getLblBlocks() {
		return lblBlocks;
	}

	public void setLblBlocks(Label lblBlocks) {
		this.lblBlocks = lblBlocks;
	}

	public Label getLblSpecifyRowFactor() {
		return lblSpecifyRowFactor;
	}

	public void setLblSpecifyRowFactor(Label lblSpecifyRowFactor) {
		this.lblSpecifyRowFactor = lblSpecifyRowFactor;
	}

	public Label getLblSpecifyColumnFactor() {
		return lblSpecifyColumnFactor;
	}

	public void setLblSpecifyColumnFactor(Label lblSpecifyColumnFactor) {
		this.lblSpecifyColumnFactor = lblSpecifyColumnFactor;
	}

	public Label getLblReplicates() {
		return lblReplicates;
	}

	public void setLblReplicates(Label lblReplicates) {
		this.lblReplicates = lblReplicates;
	}

	public Map<String, Boolean> getEnvironmentsCheckboxState() {
		return environmentsCheckboxState;
	}

	public void setEnvironmentsCheckboxState(Map<String, Boolean> environmentsCheckboxState) {
		this.environmentsCheckboxState = environmentsCheckboxState;
	}

	public List<SeaEnvironmentModel> getSelectedEnvironments() {

		List<SeaEnvironmentModel> envs = new ArrayList<SeaEnvironmentModel>();
		for (Iterator<?> itr = tblEnvironmentSelection.getContainerDataSource().getItemIds()
				.iterator(); itr.hasNext();) {
			SeaEnvironmentModel m = (SeaEnvironmentModel) itr.next();
			if (m.getActive()) {
				envs.add(m);
			}
		}
		return envs;
	}

	public VerticalLayout getBlockRowColumnContainer() {
		return blockRowColumnContainer;
	}

	public void setBlockRowColumnContainer(VerticalLayout blockRowColumnContainer) {
		this.blockRowColumnContainer = blockRowColumnContainer;
	}

	public void setSelGenotypes(Select selGenotypes) {
		this.selGenotypes = selGenotypes;
	}

	public Label getLblGenotypes() {
		return lblGenotypes;
	}

	public void setLblGenotypes(Label lblGenotypes) {
		this.lblGenotypes = lblGenotypes;
	}

	public Label getLblSpecifyGenotypesHeader() {
		return lblSpecifyGenotypesHeader;
	}

	public void setLblSpecifyGenotypesHeader(Label lblSpecifyGenotypesHeader) {
		this.lblSpecifyGenotypesHeader = lblSpecifyGenotypesHeader;
	}

	public SimpleResourceBundleMessageSource getMessageSource() {
		return messageSource;
	}

	public void setMessageSource(SimpleResourceBundleMessageSource messageSource) {
		this.messageSource = messageSource;
	}

	public void displayRowColumnDesignElements() {
		GridLayout gLayout = new GridLayout(2, 4);
		gLayout.setColumnExpandRatio(0, 0);
		gLayout.setColumnExpandRatio(1, 1);
		gLayout.setWidth("100%");
		gLayout.setSpacing(true);
		gLayout.addStyleName(MARGIN_TOP10);

		getBlockRowColumnContainer().removeAllComponents();
		gLayout.addComponent(getLblSpecifyColumnFactor(), 0, 0);
		gLayout.addComponent(getSelColumnFactor(), 1, 0);
		gLayout.addComponent(getLblSpecifyRowFactor(), 0, 1);
		gLayout.addComponent(getSelRowFactor(), 1, 1);
		gLayout.addComponent(getLblSpecifyGenotypesHeader(), 0, 2, 1, 2);
		gLayout.addComponent(getLblGenotypes(), 0, 3);
		gLayout.addComponent(getSelGenotypes(), 1, 3);
		getBlockRowColumnContainer().addComponent(gLayout);

		if (!getSelReplicates().isEnabled()
				|| getSelReplicates().getItemIds().isEmpty()) {

			for (Object itemId : getSelBlocks().getItemIds()) {
				getSelReplicates().addItem(itemId);
				getSelReplicates().setItemCaption(itemId, REPLICATES);
				getSelReplicates().select(itemId);
				getSelReplicates().setEnabled(true);
			}
		}
	}

	public void displayRandomizedBlockDesignElements() {
		GridLayout gLayout = new GridLayout(2, 2);
		gLayout.setColumnExpandRatio(0, 0);
		gLayout.setColumnExpandRatio(1, 1);
		gLayout.setWidth("100%");
		gLayout.setSpacing(true);
		gLayout.addStyleName(MARGIN_TOP10);

		getBlockRowColumnContainer().removeAllComponents();
		gLayout.addComponent(getLblSpecifyGenotypesHeader(), 0, 0, 1, 0);
		gLayout.addComponent(getLblGenotypes(), 0, 1);
		gLayout.addComponent(getSelGenotypes(), 1, 1);
		getBlockRowColumnContainer().addComponent(gLayout);

		if (!getSelReplicates().isEnabled()
				|| getSelReplicates().getItemIds().isEmpty()) {

			for (Object itemId : getSelBlocks().getItemIds()) {
				getSelReplicates().addItem(itemId);
				getSelReplicates().setItemCaption(itemId, REPLICATES);
				getSelReplicates().select(itemId);
				getSelReplicates().setEnabled(true);
			}
		}
	}

	public void displayIncompleteBlockDesignElements() {
		GridLayout gLayout = new GridLayout(2, 3);
		gLayout.setColumnExpandRatio(0, 0);
		gLayout.setColumnExpandRatio(1, 1);
		gLayout.setWidth("100%");
		gLayout.setSpacing(true);
		gLayout.addStyleName(MARGIN_TOP10);

		getBlockRowColumnContainer().removeAllComponents();
		gLayout.addComponent(getLblBlocks(), 0, 0);
		gLayout.addComponent(getSelBlocks(), 1, 0);
		gLayout.addComponent(getLblSpecifyGenotypesHeader(), 0, 1, 1, 1);
		gLayout.addComponent(getLblGenotypes(), 0, 2);
		gLayout.addComponent(getSelGenotypes(), 1, 2);

		getBlockRowColumnContainer().addComponent(gLayout);

		if (!getSelReplicates().isEnabled()
				|| getSelReplicates().getItemIds().isEmpty()) {

			for (Object itemId : getSelBlocks().getItemIds()) {
				getSelReplicates().addItem(itemId);
				getSelReplicates().setItemCaption(itemId, REPLICATES);
				getSelReplicates().select(itemId);
				getSelReplicates().setEnabled(true);
			}
		}
	}

	protected void disableEnvironmentEntries() {

		setEnvironmentEntryValues(false);
		tblEnvironmentSelection.refreshRowCache();
	}

	protected void setEnvironmentEntryValues(boolean value) {
		for (Iterator<?> itr = tblEnvironmentSelection.getContainerDataSource()
				.getItemIds().iterator(); itr.hasNext(); ) {
			SeaEnvironmentModel m = (SeaEnvironmentModel) itr.next();
			m.setActive(value);
		}
	}



}
