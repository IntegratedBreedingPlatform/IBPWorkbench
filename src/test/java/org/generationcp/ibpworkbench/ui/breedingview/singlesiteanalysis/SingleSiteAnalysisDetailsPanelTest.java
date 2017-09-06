
package org.generationcp.ibpworkbench.ui.breedingview.singlesiteanalysis;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang3.ArrayUtils;
import org.generationcp.commons.breedingview.xml.DesignType;
import org.generationcp.commons.vaadin.spring.SimpleResourceBundleMessageSource;
import org.generationcp.ibpworkbench.Message;
import org.generationcp.ibpworkbench.model.SeaEnvironmentModel;
import org.generationcp.ibpworkbench.util.BreedingViewInput;
import org.generationcp.middleware.domain.dms.DMSVariableType;
import org.generationcp.middleware.domain.dms.DMSVariableTypeTestDataInitializer;
import org.generationcp.middleware.domain.dms.DataSet;
import org.generationcp.middleware.domain.dms.PhenotypicType;
import org.generationcp.middleware.domain.dms.StandardVariable;
import org.generationcp.middleware.domain.dms.TrialEnvironment;
import org.generationcp.middleware.domain.dms.TrialEnvironments;
import org.generationcp.middleware.domain.dms.Variable;
import org.generationcp.middleware.domain.dms.VariableList;
import org.generationcp.middleware.domain.dms.VariableTypeList;
import org.generationcp.middleware.domain.oms.Term;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.manager.api.StudyDataManager;
import org.generationcp.middleware.pojos.workbench.Project;
import org.generationcp.middleware.pojos.workbench.Tool;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.Select;
import com.vaadin.ui.Table;
import com.vaadin.ui.Table.ColumnGenerator;

/**
 * Created by IntelliJ IDEA. User: Daniel Villafuerte Date: 12/17/2014 Time:
 * 1:39 PM
 */

@RunWith(MockitoJUnitRunner.class)
public class SingleSiteAnalysisDetailsPanelTest {

	private static final String LOCATION_NAME = "LOCATION_NAME";
	private static final String DATASET_TYPE = "DATASET_TYPE";
	private static final String DATASET_TITLE = "DATASET_TITLE";
	private static final String DATASET_NAME = "DATASET_NAME";
	private static final String LOC_ID = "LOC_ID";
	private static final String LOC_NAME = "LOC_NAME";
	private static final String EXPT_DESIGN = "EXPT_DESIGN";
	private static final String TRIAL_INSTANCE = "TRIAL_INSTANCE";
	private static final String DEFAULT_REPLICATES = "REPLICATES";

	private static final String ROW_FACTOR_LABEL = "Specify Row Factor";
	private static final String COLUMN_FACTOR_LABEL = "Specify Column Factor";

	private static final String BLOCK_NO = "BLOCK_NO";

	private static final String[] TRIAL_ENV_FACTORS = { SingleSiteAnalysisDetailsPanelTest.TRIAL_INSTANCE,
			SingleSiteAnalysisDetailsPanelTest.LOC_ID, SingleSiteAnalysisDetailsPanelTest.LOC_NAME,
			SingleSiteAnalysisDetailsPanelTest.EXPT_DESIGN };
	private static final String[] DATASET_FACTORS = { SingleSiteAnalysisDetailsPanelTest.DATASET_NAME,
			SingleSiteAnalysisDetailsPanelTest.DATASET_TITLE, SingleSiteAnalysisDetailsPanelTest.DATASET_TYPE };

	@InjectMocks
	private SingleSiteAnalysisDetailsPanel ssaPanel;

	private List<DMSVariableType> factors;
	private List<DMSVariableType> trialFactors;

	@Mock
	private SimpleResourceBundleMessageSource messageSource;

	private BreedingViewInput input;

	@Mock
	private StudyDataManager studyDataManager;

	@Before
	public void setup() {
		this.initializeBreedingViewInput();
		this.factors = this.createTestFactors();
		this.trialFactors = this.createTrialVariables();

		final Project project = new Project();
		this.ssaPanel = new SingleSiteAnalysisDetailsPanel(new Tool(), this.input, this.factors, this.trialFactors,
				project, new SingleSiteAnalysisPanel(project, null));
		this.ssaPanel.setMessageSource(this.messageSource);
		this.ssaPanel.setStudyDataManager(this.studyDataManager);

		final Select selEnvFactor = new Select();
		selEnvFactor.addItem(SingleSiteAnalysisDetailsPanelTest.TRIAL_INSTANCE);
		selEnvFactor.setValue(SingleSiteAnalysisDetailsPanelTest.TRIAL_INSTANCE);
		this.ssaPanel.setSelEnvFactor(selEnvFactor);

		this.mockStudyDataManagerCalls();
		this.mockMessageResource();

	}

	private void mockMessageResource() {
		Mockito.when(this.messageSource.getMessage(Message.PLEASE_CHOOSE)).thenReturn("Please choose");
		Mockito.when(this.messageSource.getMessage(Message.BV_SPECIFY_ROW_FACTOR))
				.thenReturn(SingleSiteAnalysisDetailsPanelTest.ROW_FACTOR_LABEL);
		Mockito.when(this.messageSource.getMessage(Message.BV_SPECIFY_COLUMN_FACTOR))
				.thenReturn(SingleSiteAnalysisDetailsPanelTest.COLUMN_FACTOR_LABEL);
	}

	private void mockStudyDataManagerCalls() {
		final DataSet dataset = new DataSet();
		final VariableTypeList variableTypes = new VariableTypeList();
		variableTypes.setVariableTypes(this.factors);
		dataset.setVariableTypes(variableTypes);
		Mockito.when(this.studyDataManager.getDataSet(this.input.getDatasetId())).thenReturn(dataset);

		final TrialEnvironments trialEnvironments = new TrialEnvironments();
		final TrialEnvironment trialEnvironment = new TrialEnvironment(2);
		trialEnvironments.add(trialEnvironment);
		Mockito.when(this.studyDataManager.getTrialEnvironmentsInDataset(this.input.getDatasetId()))
				.thenReturn(trialEnvironments);
	}

	private void initializeBreedingViewInput() {
		this.input = new BreedingViewInput();
		this.input.setStudyId(1);
		this.input.setDatasetId(3);
	}

	@Test
	public void testDesignTypeIncompleteBlockDesignResolvableNonLatin() {

		Mockito.when(this.studyDataManager.getGeolocationPropValue(TermId.EXPERIMENT_DESIGN_FACTOR.getId(),
				this.input.getStudyId())).thenReturn(Integer.toString(TermId.RESOLVABLE_INCOMPLETE_BLOCK.getId()));

		this.ssaPanel.initializeComponents();

		final List<Component> components = this.getComponentsListFromGridLayout();

		Assert.assertTrue(components.contains(this.ssaPanel.getLblBlocks()));
		Assert.assertTrue(components.contains(this.ssaPanel.getSelBlocks()));
		Assert.assertTrue(components.contains(this.ssaPanel.getLblSpecifyGenotypesHeader()));
		Assert.assertTrue(components.contains(this.ssaPanel.getLblGenotypes()));
		Assert.assertTrue(components.contains(this.ssaPanel.getSelGenotypes()));

		Assert.assertFalse(components.contains(this.ssaPanel.getLblSpecifyColumnFactor()));
		Assert.assertFalse(components.contains(this.ssaPanel.getSelColumnFactor()));
		Assert.assertFalse(components.contains(this.ssaPanel.getLblSpecifyRowFactor()));
		Assert.assertFalse(components.contains(this.ssaPanel.getSelRowFactor()));

		Assert.assertTrue(this.ssaPanel.getSelDesignType().getValue()
				.equals(DesignType.RESOLVABLE_INCOMPLETE_BLOCK_DESIGN.getName()));

		if ((!this.ssaPanel.getSelReplicates().isEnabled() || this.ssaPanel.getSelReplicates().getItemIds().isEmpty())
				&& !this.ssaPanel.getSelBlocks().getItemIds().isEmpty()) {
			Assert.assertTrue(this.ssaPanel.getSelReplicates().isEnabled());
			for (final Object itemId : this.ssaPanel.getSelBlocks().getItemIds()) {
				Assert.assertTrue(SingleSiteAnalysisDetailsPanelTest.DEFAULT_REPLICATES
						.equals(this.ssaPanel.getSelReplicates().getItemCaption(itemId)));
			}
		}
	}

	@Test
	public void testDesignTypeIncompleteBlockDesignResolvableLatin() {
		Mockito.when(this.studyDataManager.getGeolocationPropValue(TermId.EXPERIMENT_DESIGN_FACTOR.getId(),
				this.input.getStudyId()))
				.thenReturn(Integer.toString(TermId.RESOLVABLE_INCOMPLETE_BLOCK_LATIN.getId()));

		this.ssaPanel.initializeComponents();

		final List<Component> components = this.getComponentsListFromGridLayout();

		Assert.assertTrue(components.contains(this.ssaPanel.getLblBlocks()));
		Assert.assertTrue(components.contains(this.ssaPanel.getSelBlocks()));
		Assert.assertTrue(components.contains(this.ssaPanel.getLblSpecifyGenotypesHeader()));
		Assert.assertTrue(components.contains(this.ssaPanel.getLblGenotypes()));
		Assert.assertTrue(components.contains(this.ssaPanel.getSelGenotypes()));

		Assert.assertFalse(components.contains(this.ssaPanel.getLblSpecifyColumnFactor()));
		Assert.assertFalse(components.contains(this.ssaPanel.getSelColumnFactor()));
		Assert.assertFalse(components.contains(this.ssaPanel.getLblSpecifyRowFactor()));
		Assert.assertFalse(components.contains(this.ssaPanel.getSelRowFactor()));

		Assert.assertTrue(this.ssaPanel.getSelDesignType().getValue()
				.equals(DesignType.RESOLVABLE_INCOMPLETE_BLOCK_DESIGN.getName()));

		if ((!this.ssaPanel.getSelReplicates().isEnabled() || this.ssaPanel.getSelReplicates().getItemIds().isEmpty())
				&& !this.ssaPanel.getSelBlocks().getItemIds().isEmpty()) {
			Assert.assertTrue(this.ssaPanel.getSelReplicates().isEnabled());
			for (final Object itemId : this.ssaPanel.getSelBlocks().getItemIds()) {
				Assert.assertTrue(SingleSiteAnalysisDetailsPanelTest.DEFAULT_REPLICATES
						.equals(this.ssaPanel.getSelReplicates().getItemCaption(itemId)));
			}
		}
	}

	@Test
	public void testDesignTypeRowColumnDesignLatin() {
		Mockito.when(this.studyDataManager.getGeolocationPropValue(TermId.EXPERIMENT_DESIGN_FACTOR.getId(),
				this.input.getStudyId()))
				.thenReturn(Integer.toString(TermId.RESOLVABLE_INCOMPLETE_ROW_COL_LATIN.getId()));

		this.ssaPanel.initializeComponents();

		final List<Component> components = this.getComponentsListFromGridLayout();

		Assert.assertTrue(components.contains(this.ssaPanel.getLblSpecifyColumnFactor()));
		Assert.assertTrue(components.contains(this.ssaPanel.getSelColumnFactor()));
		Assert.assertTrue(components.contains(this.ssaPanel.getLblSpecifyRowFactor()));
		Assert.assertTrue(components.contains(this.ssaPanel.getSelRowFactor()));
		Assert.assertTrue(components.contains(this.ssaPanel.getLblSpecifyGenotypesHeader()));
		Assert.assertTrue(components.contains(this.ssaPanel.getLblGenotypes()));
		Assert.assertTrue(components.contains(this.ssaPanel.getSelGenotypes()));

		Assert.assertFalse(components.contains(this.ssaPanel.getLblBlocks()));
		Assert.assertFalse(components.contains(this.ssaPanel.getSelBlocks()));

		Assert.assertTrue(
				this.ssaPanel.getSelDesignType().getValue().equals(DesignType.RESOLVABLE_ROW_COLUMN_DESIGN.getName()));

		if ((!this.ssaPanel.getSelReplicates().isEnabled() || this.ssaPanel.getSelReplicates().getItemIds().isEmpty())
				&& !this.ssaPanel.getSelBlocks().getItemIds().isEmpty()) {
			Assert.assertTrue(this.ssaPanel.getSelReplicates().isEnabled());
			for (final Object itemId : this.ssaPanel.getSelBlocks().getItemIds()) {
				Assert.assertTrue(SingleSiteAnalysisDetailsPanelTest.DEFAULT_REPLICATES
						.equals(this.ssaPanel.getSelReplicates().getItemCaption(itemId)));
			}
		}
	}

	@Test
	public void testDesignTypeRowColumnDesignNonLatin() {
		Mockito.when(this.studyDataManager.getGeolocationPropValue(TermId.EXPERIMENT_DESIGN_FACTOR.getId(),
				this.input.getStudyId())).thenReturn(Integer.toString(TermId.RESOLVABLE_INCOMPLETE_ROW_COL.getId()));

		this.ssaPanel.initializeComponents();

		final List<Component> components = this.getComponentsListFromGridLayout();

		Assert.assertTrue(components.contains(this.ssaPanel.getLblSpecifyColumnFactor()));
		Assert.assertTrue(components.contains(this.ssaPanel.getSelColumnFactor()));
		Assert.assertTrue(components.contains(this.ssaPanel.getLblSpecifyRowFactor()));
		Assert.assertTrue(components.contains(this.ssaPanel.getSelRowFactor()));
		Assert.assertTrue(components.contains(this.ssaPanel.getLblSpecifyGenotypesHeader()));
		Assert.assertTrue(components.contains(this.ssaPanel.getLblGenotypes()));
		Assert.assertTrue(components.contains(this.ssaPanel.getSelGenotypes()));

		Assert.assertFalse(components.contains(this.ssaPanel.getLblBlocks()));
		Assert.assertFalse(components.contains(this.ssaPanel.getSelBlocks()));

		Assert.assertTrue(
				this.ssaPanel.getSelDesignType().getValue().equals(DesignType.RESOLVABLE_ROW_COLUMN_DESIGN.getName()));

		if ((!this.ssaPanel.getSelReplicates().isEnabled() || this.ssaPanel.getSelReplicates().getItemIds().isEmpty())
				&& !this.ssaPanel.getSelBlocks().getItemIds().isEmpty()) {
			Assert.assertTrue(this.ssaPanel.getSelReplicates().isEnabled());
			for (final Object itemId : this.ssaPanel.getSelBlocks().getItemIds()) {
				Assert.assertTrue(SingleSiteAnalysisDetailsPanelTest.DEFAULT_REPLICATES
						.equals(this.ssaPanel.getSelReplicates().getItemCaption(itemId)));
			}
		}
	}

	@Test
	public void testDesignTypeRandomizedBlockDesign() {
		Mockito.when(this.studyDataManager.getGeolocationPropValue(TermId.EXPERIMENT_DESIGN_FACTOR.getId(),
				this.input.getStudyId())).thenReturn(Integer.toString(TermId.RANDOMIZED_COMPLETE_BLOCK.getId()));

		this.ssaPanel.initializeComponents();

		final List<Component> components = this.getComponentsListFromGridLayout();

		Assert.assertTrue(components.contains(this.ssaPanel.getLblSpecifyGenotypesHeader()));
		Assert.assertTrue(components.contains(this.ssaPanel.getLblGenotypes()));
		Assert.assertTrue(components.contains(this.ssaPanel.getSelGenotypes()));

		Assert.assertFalse(components.contains(this.ssaPanel.getLblSpecifyColumnFactor()));
		Assert.assertFalse(components.contains(this.ssaPanel.getSelColumnFactor()));
		Assert.assertFalse(components.contains(this.ssaPanel.getLblSpecifyRowFactor()));
		Assert.assertFalse(components.contains(this.ssaPanel.getSelRowFactor()));
		Assert.assertFalse(components.contains(this.ssaPanel.getLblBlocks()));
		Assert.assertFalse(components.contains(this.ssaPanel.getSelBlocks()));

		Assert.assertTrue(
				this.ssaPanel.getSelDesignType().getValue().equals(DesignType.RANDOMIZED_BLOCK_DESIGN.getName()));

		if ((!this.ssaPanel.getSelReplicates().isEnabled() || this.ssaPanel.getSelReplicates().getItemIds().isEmpty())
				&& !this.ssaPanel.getSelBlocks().getItemIds().isEmpty()) {
			Assert.assertTrue(this.ssaPanel.getSelReplicates().isEnabled());
			for (final Object itemId : this.ssaPanel.getSelBlocks().getItemIds()) {
				Assert.assertTrue(SingleSiteAnalysisDetailsPanelTest.DEFAULT_REPLICATES
						.equals(this.ssaPanel.getSelReplicates().getItemCaption(itemId)));
			}
		}
	}

	@Test
	public void testDesignTypeInvalid() {
		Mockito.when(this.studyDataManager.getGeolocationPropValue(TermId.EXPERIMENT_DESIGN_FACTOR.getId(),
				this.input.getStudyId())).thenReturn(null);

		this.ssaPanel.initializeComponents();

		Assert.assertNull(this.ssaPanel.getSelDesignType().getValue());
	}

	@Test
	public void testPopulateChoicesForGenotypes() {
		this.ssaPanel.setSelGenotypes(new Select());
		this.ssaPanel.populateChoicesForGenotypes();
		Assert.assertTrue("Genotypes dropdown should have 3 factors",
				this.ssaPanel.getSelGenotypes().getItemIds().size() == 3);
		for (final Object id : this.ssaPanel.getSelGenotypes().getItemIds()) {
			final String localName = (String) id;
			Assert.assertFalse("Entry Type factor should not be included in Genotypes dropdown",
					TermId.ENTRY_TYPE.name().equals(localName));
			Assert.assertFalse("Plot ID factor should not be included in Genotypes dropdown",
					TermId.PLOT_ID.name().equals(localName));
		}
	}

	@Test
	public void testPopulateChoicesForReplicates() {
		final SingleSiteAnalysisDetailsPanel ssaPanel = new SingleSiteAnalysisDetailsPanel(null,
				new BreedingViewInput(), this.factors, this.trialFactors, null, null);
		final SingleSiteAnalysisDetailsPanel mockSSAPanel = Mockito.spy(ssaPanel);
		final Select repSelect = new Select();
		Mockito.doReturn(repSelect).when(mockSSAPanel).getSelReplicates();

		mockSSAPanel.populateChoicesForReplicates();
		Assert.assertTrue("Dropdown should have 1 factor", repSelect.getItemIds().size() == 1);
		Assert.assertNotNull(repSelect.getItem("REP_NO"));
	}

	@Test
	public void testPopulateChoicesForBlocks() {
		final SingleSiteAnalysisDetailsPanel ssaPanel = new SingleSiteAnalysisDetailsPanel(null,
				new BreedingViewInput(), this.factors, this.trialFactors, null, null);
		final SingleSiteAnalysisDetailsPanel mockSSAPanel = Mockito.spy(ssaPanel);
		final Select blockSelect = new Select();
		Mockito.doReturn(blockSelect).when(mockSSAPanel).getSelBlocks();

		mockSSAPanel.populateChoicesForBlocks();
		Assert.assertTrue("Dropdown should have 1 factor", blockSelect.getItemIds().size() == 1);
		Assert.assertNotNull(blockSelect.getItem("BLOCK_NO"));
	}

	@Test
	public void testPopulateChoicesForRowFactor() {
		final SingleSiteAnalysisDetailsPanel ssaPanel = new SingleSiteAnalysisDetailsPanel(null,
				new BreedingViewInput(), this.factors, this.trialFactors, null, null);
		final SingleSiteAnalysisDetailsPanel mockSSAPanel = Mockito.spy(ssaPanel);
		final Select rowSelect = new Select();
		Mockito.doReturn(rowSelect).when(mockSSAPanel).getSelRowFactor();

		mockSSAPanel.populateChoicesForRowFactor();
		Assert.assertTrue("Dropdown should have 1 factor", rowSelect.getItemIds().size() == 1);
		Assert.assertNotNull(rowSelect.getItem("ROW_NO"));
	}

	@Test
	public void testPopulateChoicesForEnvForAnalysis() {
		this.ssaPanel.setTrialVariablesInDataset(DMSVariableTypeTestDataInitializer.createDMSVariableTypeList());
		this.ssaPanel.setFooterCheckBox(new CheckBox("Select All", false));
		this.ssaPanel.setEnvironmentsCheckboxState(new HashMap<String, Boolean>());

		this.ssaPanel.createEnvironmentSelectionTable();
		final TrialEnvironments trialEnvironments = new TrialEnvironments();
		final TrialEnvironment trialEnvironment = Mockito.mock(TrialEnvironment.class);
		Mockito.when(trialEnvironment.getId()).thenReturn(1);
		trialEnvironments.add(trialEnvironment);

		final VariableList variableList = Mockito.mock(VariableList.class);
		Mockito.when(trialEnvironment.getVariables()).thenReturn(variableList);
		final Variable trialInstance = new Variable();
		trialInstance.setValue("1");
		Mockito.when(variableList.findByLocalName(SingleSiteAnalysisDetailsPanelTest.TRIAL_INSTANCE))
				.thenReturn(trialInstance);
		Mockito.when(this.studyDataManager.getTrialEnvironmentsInDataset(Matchers.anyInt()))
				.thenReturn(trialEnvironments);
		Mockito.when(this.studyDataManager.getLocalNameByStandardVariableId(Matchers.anyInt(), Matchers.anyInt()))
				.thenReturn(SingleSiteAnalysisDetailsPanelTest.TRIAL_INSTANCE);

		this.ssaPanel.populateChoicesForEnvForAnalysis();
		Assert.assertFalse("The footer checkbox value should be false",
				this.ssaPanel.getFooterCheckBox().booleanValue());
		Assert.assertEquals("The environment check box state's size should be 0", 0,
				this.ssaPanel.getEnvironmentsCheckboxState().size());
		Assert.assertEquals("The trial instance name should be TRIAL_INSTANCE",
				SingleSiteAnalysisDetailsPanelTest.TRIAL_INSTANCE,
				this.ssaPanel.getBreedingViewInput().getTrialInstanceName());
	}

	@Test
	public void testPopulateEnvironmentSelectionTableWithTrialEnvironmets() {
		final Table table = new Table();
		table.addContainerProperty(SingleSiteAnalysisDetailsPanel.SELECT_COLUMN, Select.class, "");
		table.addContainerProperty(SingleSiteAnalysisDetailsPanel.TRIAL_NO_COLUMN, Integer.class, "");

		final TrialEnvironments trialEnvironments = new TrialEnvironments();
		final TrialEnvironment trialEnvironment = Mockito.mock(TrialEnvironment.class);
		Mockito.when(trialEnvironment.getId()).thenReturn(1);
		trialEnvironments.add(trialEnvironment);

		final VariableList variableList = Mockito.mock(VariableList.class);
		Mockito.when(trialEnvironment.getVariables()).thenReturn(variableList);
		final Variable trialInstance = new Variable();
		trialInstance.setValue("1");
		Mockito.when(variableList.findByLocalName(SingleSiteAnalysisDetailsPanelTest.TRIAL_INSTANCE))
				.thenReturn(trialInstance);
		final Variable locationVariable = new Variable();
		locationVariable.setValue("Africa Rice Center");
		Mockito.when(variableList.findByLocalName(LOCATION_NAME))
				.thenReturn(locationVariable);
		Mockito.when(this.studyDataManager.getTrialEnvironmentsInDataset(Matchers.anyInt()))
				.thenReturn(trialEnvironments);

		this.ssaPanel.populateEnvironmentSelectionTableWithTrialEnvironmets(table,
				SingleSiteAnalysisDetailsPanelTest.TRIAL_INSTANCE, LOCATION_NAME);
		final BeanItemContainer<SeaEnvironmentModel> container = (BeanItemContainer<SeaEnvironmentModel>) table
				.getContainerDataSource();
		final SeaEnvironmentModel bean = container.getIdByIndex(0);
		Assert.assertFalse("The active value should be false", bean.getActive());
		Assert.assertEquals("The environment name should be Africa Rice Center", "Africa Rice Center", bean.getEnvironmentName());
		Assert.assertEquals("The trial no should be 1", "1", bean.getTrialno());
		Assert.assertEquals("The location id should be 1", "1", bean.getLocationId().toString());
	}

	@Test
	public void testAdjustEnvironmentSelectionTableWhereTrialInstanceFactorNotSelectedEnvFactor() {
		final Table table = new Table();
		table.addContainerProperty(SingleSiteAnalysisDetailsPanel.SELECT_COLUMN, Select.class, "");
		table.addContainerProperty(SingleSiteAnalysisDetailsPanel.TRIAL_NO_COLUMN, Integer.class, "");
		table.addContainerProperty(SingleSiteAnalysisDetailsPanel.ENVIRONMENT_NAME, String.class, "");

		this.ssaPanel.adjustEnvironmentSelectionTable(table, SingleSiteAnalysisDetailsPanelTest.TRIAL_INSTANCE,
				LOCATION_NAME);
		Assert.assertEquals("There should be 3 visible columns", 3, table.getVisibleColumns().length);
		Assert.assertEquals("There should be 3 column headers", 3, table.getColumnHeaders().length);
		Assert.assertEquals("Select column's width should be 45.", 45,
				table.getColumnWidth(SingleSiteAnalysisDetailsPanel.SELECT_COLUMN));
		Assert.assertEquals("Trial No's width should be 60.", 60,
				table.getColumnWidth(SingleSiteAnalysisDetailsPanel.TRIAL_NO_COLUMN));
		Assert.assertEquals("Environment Names's width should be 500.", 500,
				table.getColumnWidth(SingleSiteAnalysisDetailsPanel.ENVIRONMENT_NAME));
		Assert.assertEquals("Table's width should be 90.0.", "90.0", String.valueOf(table.getWidth()));

	}

	@Test
	public void testAdjustEnvironmentSelectionTableWhereTrialInstanceFactorIsTheSelectedEnvFactor() {
		final Table table = new Table();
		table.addContainerProperty(SingleSiteAnalysisDetailsPanel.SELECT_COLUMN, Select.class, "");
		table.addContainerProperty(SingleSiteAnalysisDetailsPanel.TRIAL_NO_COLUMN, Integer.class, "");

		this.ssaPanel.adjustEnvironmentSelectionTable(table, SingleSiteAnalysisDetailsPanelTest.TRIAL_INSTANCE,
				SingleSiteAnalysisDetailsPanelTest.TRIAL_INSTANCE);
		Assert.assertEquals("There should be 2 visible columns", 2, table.getVisibleColumns().length);
		Assert.assertEquals("There should be 2 column headers", 2, table.getColumnHeaders().length);
		Assert.assertEquals("Select column's width should be 45.", 45,
				table.getColumnWidth(SingleSiteAnalysisDetailsPanel.SELECT_COLUMN));
		Assert.assertEquals("Trial No's width should be -1.", -1,
				table.getColumnWidth(SingleSiteAnalysisDetailsPanel.TRIAL_NO_COLUMN));
		Assert.assertEquals("Table's width should be 45.0.", "45.0", String.valueOf(table.getWidth()));

	}

	@Test
	public void testPopulateChoicesForColumnFactor() {
		final SingleSiteAnalysisDetailsPanel ssaPanel = new SingleSiteAnalysisDetailsPanel(null,
				new BreedingViewInput(), this.factors, this.trialFactors, null, null);
		final SingleSiteAnalysisDetailsPanel mockSSAPanel = Mockito.spy(ssaPanel);
		final Select columnSelect = new Select();
		Mockito.doReturn(columnSelect).when(mockSSAPanel).getSelColumnFactor();

		mockSSAPanel.populateChoicesForColumnFactor();
		Assert.assertTrue("Dropdown should have 1 factor", columnSelect.getItemIds().size() == 1);
		Assert.assertNotNull(columnSelect.getItem("COLUMN_NO"));
	}

	@Test
	public void testPopulateChoicesForEnvironmentFactor() {
		final SingleSiteAnalysisDetailsPanel ssaPanel = new SingleSiteAnalysisDetailsPanel(null,
				new BreedingViewInput(), this.factors, this.trialFactors, null, null);
		final SingleSiteAnalysisDetailsPanel mockSSAPanel = Mockito.spy(ssaPanel);
		mockSSAPanel.setMessageSource(this.messageSource);
		final String pleaseChooseOption = "Please Choose";
		Mockito.doReturn(pleaseChooseOption).when(this.messageSource).getMessage(Message.PLEASE_CHOOSE);

		final Select envSelect = new Select();
		Mockito.doReturn(envSelect).when(mockSSAPanel).getSelEnvFactor();

		mockSSAPanel.populateChoicesForEnvironmentFactor();
		// "Please Choose" was added as dropdown item
		Assert.assertTrue("Dropdown should return fixed # of env factors",
				envSelect.getItemIds().size() == SingleSiteAnalysisDetailsPanelTest.TRIAL_ENV_FACTORS.length + 1);
		for (final Object id : envSelect.getItemIds()) {
			final String localName = (String) id;
			Assert.assertTrue(ArrayUtils.contains(SingleSiteAnalysisDetailsPanelTest.TRIAL_ENV_FACTORS, localName)
					|| pleaseChooseOption.equals(localName));
			Assert.assertFalse(ArrayUtils.contains(SingleSiteAnalysisDetailsPanelTest.DATASET_FACTORS, localName));
		}
	}

	@Test
	public void testDisplayPRepDesignElements() {

		this.ssaPanel.initializeComponents();

		this.ssaPanel.displayPRepDesignElements();

		final List<Component> components = this.getComponentsListFromGridLayout();

		// The following components should be visible in Design Details' Grid
		// Layout
		Assert.assertTrue(components.contains(this.ssaPanel.getLblBlocks()));
		Assert.assertTrue(components.contains(this.ssaPanel.getSelBlocks()));
		Assert.assertTrue(components.contains(this.ssaPanel.getLblSpecifyColumnFactor()));
		Assert.assertTrue(components.contains(this.ssaPanel.getSelColumnFactor()));
		Assert.assertTrue(components.contains(this.ssaPanel.getLblSpecifyRowFactor()));
		Assert.assertTrue(components.contains(this.ssaPanel.getSelRowFactor()));
		Assert.assertTrue(components.contains(this.ssaPanel.getLblSpecifyGenotypesHeader()));
		Assert.assertTrue(components.contains(this.ssaPanel.getLblGenotypes()));
		Assert.assertTrue(components.contains(this.ssaPanel.getSelGenotypes()));

		// The following components should not be added in Design Details'
		// GridLayout
		Assert.assertFalse(components.contains(this.ssaPanel.getLblReplicates()));
		Assert.assertFalse(components.contains(this.ssaPanel.getSelReplicates()));

		Assert.assertNull("Replicates factor is not needed in P-rep design, so replicates should be unselected (null)",
				this.ssaPanel.getSelReplicates().getValue());

	}

	@Test
	public void testDisplayAugmentedDesignElements() {

		this.ssaPanel.initializeComponents();
		this.ssaPanel.displayAugmentedDesignElements();

		final List<Component> components = this.getComponentsListFromGridLayout();

		// The following components should be visible in Design Details' Grid
		// Layout
		Assert.assertTrue(components.contains(this.ssaPanel.getLblBlocks()));
		Assert.assertTrue(components.contains(this.ssaPanel.getSelBlocks()));
		Assert.assertTrue(components.contains(this.ssaPanel.getLblSpecifyGenotypesHeader()));
		Assert.assertTrue(components.contains(this.ssaPanel.getLblGenotypes()));
		Assert.assertTrue(components.contains(this.ssaPanel.getSelGenotypes()));

		// The following components should not be added in Design Details'
		// GridLayout
		Assert.assertFalse(components.contains(this.ssaPanel.getLblSpecifyColumnFactor()));
		Assert.assertFalse(components.contains(this.ssaPanel.getSelColumnFactor()));
		Assert.assertFalse(components.contains(this.ssaPanel.getLblSpecifyRowFactor()));
		Assert.assertFalse(components.contains(this.ssaPanel.getSelRowFactor()));
		Assert.assertFalse(components.contains(this.ssaPanel.getLblReplicates()));
		Assert.assertFalse(components.contains(this.ssaPanel.getSelReplicates()));

		Assert.assertNull(
				"Replicates factor is not needed in Augmented design, so replicates should be unselected (null)",
				this.ssaPanel.getSelReplicates().getValue());

	}

	@Test
	public void testSubstituteMissingReplicatesWithBlocksNoReplicatesFactor() {

		this.ssaPanel.initializeComponents();

		this.ssaPanel.getSelBlocks().addItem(SingleSiteAnalysisDetailsPanelTest.BLOCK_NO);
		this.ssaPanel.getSelReplicates().removeAllItems();

		this.ssaPanel.substituteMissingReplicatesWithBlocks();

		Assert.assertEquals("The value of Replicates Factor Select Field should be the same as the Block factor",
				SingleSiteAnalysisDetailsPanelTest.BLOCK_NO, this.ssaPanel.getSelReplicates().getValue());
		Assert.assertEquals(
				"If block factor is used as a substitute for replicates, then the item caption should be \""
						+ SingleSiteAnalysisDetailsPanel.REPLICATES + "\"",
				SingleSiteAnalysisDetailsPanel.REPLICATES,
				this.ssaPanel.getSelReplicates().getItemCaption(this.ssaPanel.getSelReplicates().getValue()));

	}

	@Test
	public void testChangeRowAndColumnLabelsBasedOnDesignTypePRepDesign() {

		this.ssaPanel.initializeComponents();

		this.ssaPanel.changeRowAndColumnLabelsBasedOnDesignType(DesignType.P_REP_DESIGN);

		// Row and Column factors are optional in P-rep Design, the labels
		// should not have required field indicator (red asterisk '*')
		Assert.assertEquals(SingleSiteAnalysisDetailsPanelTest.COLUMN_FACTOR_LABEL,
				this.ssaPanel.getLblSpecifyColumnFactor().getValue());
		Assert.assertEquals(SingleSiteAnalysisDetailsPanelTest.ROW_FACTOR_LABEL,
				this.ssaPanel.getLblSpecifyRowFactor().getValue());

	}

	@Test
	public void testChangeRowAndColumnLabelsBasedOnDesignTypeRowAndColumnDesign() {

		this.ssaPanel.initializeComponents();

		this.ssaPanel.changeRowAndColumnLabelsBasedOnDesignType(DesignType.RESOLVABLE_ROW_COLUMN_DESIGN);

		// Row and Column factors are required in Row-and-Column Design, the
		// labels should have a required field indicator (red asterisk
		// '*')
		Assert.assertEquals(
				SingleSiteAnalysisDetailsPanelTest.COLUMN_FACTOR_LABEL
						+ SingleSiteAnalysisDetailsPanel.REQUIRED_FIELD_INDICATOR,
				this.ssaPanel.getLblSpecifyColumnFactor().getValue());
		Assert.assertEquals(
				SingleSiteAnalysisDetailsPanelTest.ROW_FACTOR_LABEL
						+ SingleSiteAnalysisDetailsPanel.REQUIRED_FIELD_INDICATOR,
				this.ssaPanel.getLblSpecifyRowFactor().getValue());

	}

	@Test
	public void testChangeRowAndColumnLabelsBasedOnDesignTypeRowAndOtherDesign() {

		this.ssaPanel.initializeComponents();

		this.ssaPanel.changeRowAndColumnLabelsBasedOnDesignType(DesignType.RESOLVABLE_INCOMPLETE_BLOCK_DESIGN);

		Assert.assertEquals(SingleSiteAnalysisDetailsPanelTest.COLUMN_FACTOR_LABEL,
				this.ssaPanel.getLblSpecifyColumnFactor().getValue());
		Assert.assertEquals(SingleSiteAnalysisDetailsPanelTest.ROW_FACTOR_LABEL,
				this.ssaPanel.getLblSpecifyRowFactor().getValue());

	}

	private List<Component> getComponentsListFromGridLayout() {

		final GridLayout gLayout = (GridLayout) this.ssaPanel.getDesignDetailsContainer().getComponentIterator().next();
		final Iterator<Component> componentsIterator = gLayout.getComponentIterator();
		final List<Component> components = new ArrayList<>();
		while (componentsIterator.hasNext()) {
			final Component component = componentsIterator.next();
			components.add(component);
		}

		return components;
	}

	private List<DMSVariableType> createTestFactors() {
		final List<DMSVariableType> factors = new ArrayList<DMSVariableType>();

		int rank = 1;
		final StandardVariable entryNoVariable = new StandardVariable();
		entryNoVariable.setId(TermId.ENTRY_NO.getId());
		entryNoVariable.setPhenotypicType(PhenotypicType.GERMPLASM);
		entryNoVariable.setProperty(new Term(1, "GERMPLASM ENTRY", "GERMPLASM ENTRY"));
		factors.add(new DMSVariableType(TermId.ENTRY_NO.name(), TermId.ENTRY_NO.name(), entryNoVariable, rank++));

		final StandardVariable gidVariable = new StandardVariable();
		gidVariable.setId(TermId.GID.getId());
		gidVariable.setPhenotypicType(PhenotypicType.GERMPLASM);
		gidVariable.setProperty(new Term(1, "GERMPLASM ID", "GERMPLASM ID"));
		factors.add(new DMSVariableType(TermId.GID.name(), TermId.ENTRY_NO.name(), gidVariable, rank++));

		final StandardVariable desigVariable = new StandardVariable();
		desigVariable.setId(TermId.DESIG.getId());
		desigVariable.setPhenotypicType(PhenotypicType.GERMPLASM);
		desigVariable.setProperty(new Term(1, "GERMPLASM ID", "GERMPLASM ID"));
		factors.add(new DMSVariableType("DESIGNATION", "DESIGNATION", desigVariable, rank++));

		final StandardVariable entryTypeVariable = new StandardVariable();
		entryTypeVariable.setId(TermId.ENTRY_TYPE.getId());
		entryTypeVariable.setPhenotypicType(PhenotypicType.GERMPLASM);
		entryTypeVariable.setProperty(new Term(1, TermId.ENTRY_TYPE.name(), TermId.ENTRY_TYPE.name()));
		factors.add(new DMSVariableType(TermId.ENTRY_TYPE.name(), TermId.ENTRY_TYPE.name(), entryTypeVariable, rank++));

		final StandardVariable plotIdVariable = new StandardVariable();
		plotIdVariable.setId(TermId.PLOT_ID.getId());
		plotIdVariable.setPhenotypicType(PhenotypicType.GERMPLASM);
		plotIdVariable.setProperty(new Term(1, TermId.PLOT_ID.name(), TermId.PLOT_ID.name()));
		factors.add(new DMSVariableType(TermId.PLOT_ID.name(), TermId.PLOT_ID.name(), plotIdVariable, rank++));

		final StandardVariable repVariable = new StandardVariable();
		repVariable.setId(TermId.REP_NO.getId());
		repVariable.setPhenotypicType(PhenotypicType.TRIAL_DESIGN);
		repVariable.setProperty(new Term(1, SingleSiteAnalysisDetailsPanel.REPLICATION_FACTOR, "REP_NO"));
		factors.add(new DMSVariableType(TermId.REP_NO.name(), TermId.REP_NO.name(), repVariable, rank++));

		final StandardVariable blockVariable = new StandardVariable();
		blockVariable.setId(TermId.BLOCK_NO.getId());
		blockVariable.setPhenotypicType(PhenotypicType.TRIAL_DESIGN);
		blockVariable.setProperty(new Term(1, SingleSiteAnalysisDetailsPanel.BLOCKING_FACTOR, "BLOCK_NO"));
		factors.add(new DMSVariableType(TermId.BLOCK_NO.name(), TermId.BLOCK_NO.name(), blockVariable, rank++));

		final StandardVariable rowVariable = new StandardVariable();
		rowVariable.setId(TermId.ROW.getId());
		rowVariable.setPhenotypicType(PhenotypicType.TRIAL_DESIGN);
		rowVariable.setProperty(new Term(1, SingleSiteAnalysisDetailsPanel.ROW_FACTOR, "ROW_NO"));
		factors.add(new DMSVariableType("ROW_NO", "ROW_NO", rowVariable, rank++));

		final StandardVariable columnVariable = new StandardVariable();
		columnVariable.setId(TermId.COLUMN_NO.getId());
		columnVariable.setPhenotypicType(PhenotypicType.TRIAL_DESIGN);
		columnVariable.setProperty(new Term(1, SingleSiteAnalysisDetailsPanel.COLUMN_FACTOR, "COL_NO"));
		factors.add(new DMSVariableType(TermId.COLUMN_NO.name(), TermId.COLUMN_NO.name(), columnVariable, rank++));

		return factors;
	}

	private List<DMSVariableType> createTrialVariables() {
		final List<DMSVariableType> factors = new ArrayList<DMSVariableType>();

		final StandardVariable trialInstanceVar = new StandardVariable();
		trialInstanceVar.setId(TermId.TRIAL_INSTANCE_FACTOR.getId());
		trialInstanceVar.setPhenotypicType(PhenotypicType.TRIAL_ENVIRONMENT);
		trialInstanceVar.setProperty(new Term(1, SingleSiteAnalysisDetailsPanelTest.TRIAL_INSTANCE,
				SingleSiteAnalysisDetailsPanelTest.TRIAL_INSTANCE));
		factors.add(new DMSVariableType(SingleSiteAnalysisDetailsPanelTest.TRIAL_INSTANCE,
				SingleSiteAnalysisDetailsPanelTest.TRIAL_INSTANCE, trialInstanceVar, 1));

		final StandardVariable exptDesignVar = new StandardVariable();
		exptDesignVar.setId(TermId.EXPERIMENT_DESIGN_FACTOR.getId());
		exptDesignVar.setPhenotypicType(PhenotypicType.TRIAL_ENVIRONMENT);
		exptDesignVar.setProperty(new Term(1, "EXPERIMENTAL DESIGN", "EXPERIMENTAL DESIGN"));
		factors.add(new DMSVariableType(SingleSiteAnalysisDetailsPanelTest.EXPT_DESIGN,
				SingleSiteAnalysisDetailsPanelTest.EXPT_DESIGN, exptDesignVar, 2));

		final StandardVariable locNameVar = new StandardVariable();
		locNameVar.setId(TermId.SITE_NAME.getId());
		locNameVar.setPhenotypicType(PhenotypicType.TRIAL_ENVIRONMENT);
		locNameVar.setProperty(new Term(1, "LOCATION", "LOCATION"));
		factors.add(new DMSVariableType(SingleSiteAnalysisDetailsPanelTest.LOC_NAME,
				SingleSiteAnalysisDetailsPanelTest.LOC_NAME, locNameVar, 3));

		final StandardVariable locIDVar = new StandardVariable();
		locIDVar.setId(TermId.LOCATION_ID.getId());
		locIDVar.setPhenotypicType(PhenotypicType.TRIAL_ENVIRONMENT);
		locIDVar.setProperty(new Term(1, "LOCATION", "LOCATION"));
		factors.add(new DMSVariableType(SingleSiteAnalysisDetailsPanelTest.LOC_ID,
				SingleSiteAnalysisDetailsPanelTest.LOC_ID, locIDVar, 4));

		final StandardVariable datasetNameVar = new StandardVariable();
		datasetNameVar.setId(TermId.DATASET_NAME.getId());
		datasetNameVar.setPhenotypicType(PhenotypicType.DATASET);
		datasetNameVar.setProperty(new Term(1, "DATASET", "DATASET"));
		factors.add(new DMSVariableType(SingleSiteAnalysisDetailsPanelTest.DATASET_NAME,
				SingleSiteAnalysisDetailsPanelTest.DATASET_NAME, datasetNameVar, 5));

		final StandardVariable datasetTitleVar = new StandardVariable();
		datasetTitleVar.setId(TermId.DATASET_NAME.getId());
		datasetTitleVar.setPhenotypicType(PhenotypicType.DATASET);
		datasetTitleVar.setProperty(new Term(1, "DATASET TITLE", SingleSiteAnalysisDetailsPanelTest.DATASET_TITLE));
		factors.add(new DMSVariableType(SingleSiteAnalysisDetailsPanelTest.DATASET_TITLE,
				SingleSiteAnalysisDetailsPanelTest.DATASET_TITLE, datasetTitleVar, 6));

		final StandardVariable datasetTypeVar = new StandardVariable();
		datasetTypeVar.setId(TermId.DATASET_NAME.getId());
		datasetTypeVar.setPhenotypicType(PhenotypicType.DATASET);
		datasetTypeVar.setProperty(new Term(1, "DATASET", "DATASET"));
		factors.add(new DMSVariableType(SingleSiteAnalysisDetailsPanelTest.DATASET_TYPE,
				SingleSiteAnalysisDetailsPanelTest.DATASET_TYPE, datasetTypeVar, 7));

		return factors;
	}

}
