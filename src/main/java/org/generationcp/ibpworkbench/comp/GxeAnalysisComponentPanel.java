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

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.generationcp.commons.breedingview.xml.Genotypes;
import org.generationcp.commons.vaadin.util.MessageNotifier;
import org.generationcp.ibpworkbench.comp.table.GxeTable;
import org.generationcp.ibpworkbench.util.GxeInput;
import org.generationcp.ibpworkbench.util.GxeUtility;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.manager.Database;
import org.generationcp.middleware.manager.ManagerFactory;
import org.generationcp.middleware.manager.api.ManagerFactoryProvider;
import org.generationcp.middleware.manager.api.WorkbenchDataManager;
import org.generationcp.middleware.pojos.workbench.Project;
import org.generationcp.middleware.v2.domain.DataSet;
import org.generationcp.middleware.v2.domain.DataSetType;
import org.generationcp.middleware.v2.domain.FolderReference;
import org.generationcp.middleware.v2.domain.Reference;
import org.generationcp.middleware.v2.domain.Study;
import org.generationcp.middleware.v2.domain.StudyReference;
import org.generationcp.middleware.v2.manager.api.StudyDataManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import com.vaadin.data.Container;
import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.ui.AbstractSelect;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.TabSheet.SelectedTabChangeEvent;
import com.vaadin.ui.TabSheet.SelectedTabChangeListener;
import com.vaadin.ui.Table;
import com.vaadin.ui.Tree;
import com.vaadin.ui.VerticalLayout;

/**
 * Multisite analysis component
 * 
 * @author Aldrich Abrogena
 */
@Configurable
public class GxeAnalysisComponentPanel extends VerticalLayout implements
		InitializingBean {

	private static final Logger LOG = LoggerFactory
			.getLogger(GxeAnalysisComponentPanel.class);
	private static final long serialVersionUID = 1L;

	// private TwinColSelect select;

	private Map<Integer, Table> studyTables = new HashMap<Integer, Table>();
	private Tree studiesTree;
	private TabSheet studiesTabsheet;
	
	protected Boolean setAll = true;
	protected Boolean fromOthers = true;

	private StudyDataManager studyDataManager;

	@Autowired
	private WorkbenchDataManager workbenchDataManager;

	@Autowired
	private ManagerFactoryProvider managerFactoryProvider;

	private Project project;

	public GxeAnalysisComponentPanel(Project project) {
		LOG.debug("Project is " + project.getProjectName());
		this.project = project;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		assemble();
	}

	protected void assemble() {

		initializeComponents();
		initializeLayout();
		initializeActions();

	}

	/**
	 * Helper to add an item with specified caption and (optional) parent.
	 * 
	 * @param caption
	 *            The item caption
	 * @param parent
	 *            The (optional) parent item id
	 * @return the created item's id
	 */
	private Object addCaptionedItem(String caption, Integer objid, Object parent) {
		// add item, let tree decide id
		final Object id = studiesTree.addItem();
		// get the created item
		final Item item = studiesTree.getItem(id);
		// set our "caption" property
		final Property p = item.getItemProperty("caption");
		p.setValue(caption);

		final Property idp = item.getItemProperty("id");
		idp.setValue(objid);

		if (parent != null) {
			studiesTree.setChildrenAllowed(parent, true);
			studiesTree.setParent(id, parent);
			studiesTree.setChildrenAllowed(id, false);
		}
		return id;
	}

	protected void generateChildren(FolderReference folderParent,
			Object folderParentItem) throws MiddlewareQueryException {

		List<Reference> children = studyDataManager
				.getChildrenOfFolder(folderParent.getId());

		if (children.size() == 0) {
			// The planet has no moons so make it a leaf.
			// studiesTree.setChildrenAllowed(folderParent.getName(), false);
		} else {
			// Add children (moons) under the planets.
			for (Reference childStudy : children) {
				if (childStudy instanceof StudyReference)
					addCaptionedItem(childStudy.getName(), childStudy.getId(),
							folderParentItem);
				else if (childStudy instanceof FolderReference) {
					Object myfolderParentItem = addCaptionedItem(
							childStudy.getName(), childStudy.getId(),
							folderParentItem);
					generateChildren((FolderReference) childStudy,
							myfolderParentItem);
				}

			}

			// Expand the subtree.
			// studiesTree.expandItemsRecursively(studies);
		}
	}

	protected void refreshStudies() throws MiddlewareQueryException {

		List<FolderReference> listFolder = studyDataManager
				.getRootFolders(Database.CENTRAL);

		studiesTree.addContainerProperty("caption", String.class, "");
		studiesTree.addContainerProperty("id", String.class, "");

		studiesTree
				.setItemCaptionMode(AbstractSelect.ITEM_CAPTION_MODE_PROPERTY);
		studiesTree.setItemCaptionPropertyId("caption");

		/* Add planets as root items in the tree. */
		for (FolderReference folderParent : listFolder) {
			Object folderParentItem = addCaptionedItem(folderParent.getName(),
					folderParent.getId(), null);
			generateChildren(folderParent, folderParentItem);

		}

	}

	protected void repaintTab(Component comp, Study study) {

		if (comp != null) {
			VerticalLayout container = (VerticalLayout) comp;
			container.setSpacing(true);
			container.setMargin(true, false, false, false);
			container.removeAllComponents();

			Label tabTitle = new Label("&nbsp;&nbsp;"
					+ "Adjusted means datasets", Label.CONTENT_XHTML);
			tabTitle.setStyleName("gcp-content-title");

			container.addComponent(tabTitle);
			
			container.addComponent(studyTables.get(study.getId()));
			container.setExpandRatio(studyTables.get(study.getId()), 1.0F);

			container.setSizeFull();
		}
	}

	protected VerticalLayout generateTabContent(TabSheet tab, Study study) {
	

		VerticalLayout tabContainer = new VerticalLayout();
		tabContainer.setStyleName("gcp-light-grey");
		tabContainer.setSpacing(true);
		tabContainer.setMargin(true, false, false, false);

		Label tabTitle = new Label("&nbsp;&nbsp;" + "Adjusted means datasets",
				Label.CONTENT_XHTML);
		tabTitle.setStyleName("gcp-content-title");
		tabContainer.addComponent(tabTitle);
		
		List<DataSet> ds = null;
		try {
			ds = studyDataManager.getDataSetsByType(study.getId(), DataSetType.MEANS_DATA);
		} catch (MiddlewareQueryException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		if (ds.size() > 0){
			studyTables.put(study.getId(), new GxeTable(studyDataManager, study.getId()));
			tabContainer.addComponent(studyTables.get(study.getId()));
			tabContainer.setExpandRatio(studyTables.get(study.getId()), 1.0F);
		}else{
			Label temp = new Label("&nbsp;&nbsp;No means dataset available for this study (" + study.getName().toString() + ")" );
			temp.setContentMode(Label.CONTENT_XHTML);
			tabContainer.addComponent(temp);
			tabContainer.setExpandRatio(temp, 1.0F);
		}
		
	
		tabContainer.setSizeFull();
		tabContainer.setCaption(study.getName());
		tabContainer.setData(study);
		
		tab.addComponent(tabContainer);
		tab.getTab(tabContainer).setClosable(true);
		tab.setCloseHandler(new StudiesTabCloseListener(studyTables));

		return tabContainer;
	}

	

	protected void initializeComponents() {

		setSpacing(true);
		setMargin(true);
	
		HorizontalLayout horizontal = new HorizontalLayout();

		ManagerFactory managerFactory = managerFactoryProvider
				.getManagerFactoryForProject(project);
		studyDataManager = managerFactory.getNewStudyDataManager();

		studiesTree = new Tree("Studies");
		studiesTree.setSizeFull();
		studiesTree.setImmediate(true);

		try {
			refreshStudies();
		} catch (MiddlewareQueryException e) {
			e.printStackTrace();
		}

		Panel studiesPanel = new Panel();
		studiesPanel.setWidth("200px");
		studiesPanel.setHeight("100%");
		studiesPanel.addComponent(studiesTree);

		horizontal.addComponent(studiesPanel);

		studiesTabsheet = generateTabSheet();

		horizontal.addComponent(studiesTabsheet);

		horizontal.setWidth("100%");
		horizontal.setHeight("530px");
		horizontal.setExpandRatio(studiesTabsheet, 1.0F);

		addComponent(horizontal);

		Button button = new Button(
				"Export study dataset to Breeding View Excel and XML input");
		Button gxebutton = new Button("Launch the Breeding View's GxE Analysis");

		button.addListener(new Button.ClickListener() {

			private static final long serialVersionUID = -7090745965019240566L;

			@Override
			public void buttonClick(ClickEvent event) {
				/*
				 * File csvFile
				 * =GxEUtility.generateGxEInputCSV(event.getComponent
				 * ().getApplication().getMainWindow(),
				 * tblDataSet.getContainerDataSource(), project, new String[]
				 * {"environment"
				 * ,"genotype","height","maturity","rust","height 1"}); // NOTE:
				 * the string array are the table headers
				 */
				
				Study study = (Study) ((VerticalLayout)studiesTabsheet.getSelectedTab()).getData();
				Table table = studyTables.get(study.getId());
				
				if (table != null){
					
					File xlsFile = GxeUtility.exportGeneratedXlsFieldbook(
							table.getContainerDataSource(), project,
							"xlsInput.xls");

					LOG.debug(xlsFile.getAbsolutePath());
					
					GxeInput gxeInput =  new GxeInput(project, "", 0, 0, "", "", "", "");
					
					gxeInput.setSourceXLSFilePath(xlsFile.getAbsolutePath());
					gxeInput.setDestXMLFilePath(xlsFile.getParent() + "\\xmlInput.xml");
					gxeInput.setTraits(((GxeTable)table).getSelectedTraits());
					gxeInput.setEnvironment(((GxeTable)table).getGxeEnvironment());
					Genotypes genotypes = new Genotypes();
					genotypes.setName("G!");
					gxeInput.setGenotypes(genotypes);
					gxeInput.setEnvironmentName("E!");
					gxeInput.setBreedingViewProjectName(project.getProjectName());
					
					GxeUtility.generateXmlFieldBook(gxeInput);
					
					
					MessageNotifier
							.showMessage(event.getComponent().getWindow(),
									"GxE file saved",
									"Successfully created GxE Excel and XML input file for the breeding_view");
					
				}
				
				
								
			}
			
		});

		this.setExpandRatio(horizontal, 1.0F);

		HorizontalLayout btnLayout = new HorizontalLayout();
		btnLayout.setSizeUndefined();
		btnLayout.setSpacing(true);
		btnLayout.addComponent(button);
		btnLayout.addComponent(gxebutton);

		this.addComponent(btnLayout);

	}


	protected void initializeLayout() {
		setSpacing(true);
		setMargin(true);
		setSizeFull();
	}

	protected void initializeActions() {
		// newMemberButton.addListener(new
		// OpenNewProjectAddUserWindowAction(select));
		studiesTree.addListener(new StudiesTreeAction());
		// studiesTabsheet.addListener(new StudiesTabFocusListener());

	}


	private class StudiesTreeAction implements ValueChangeListener {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		@Override
		public void valueChange(ValueChangeEvent event) {
			
			System.out.println(event);
			System.out.println(event.getProperty().getValue());

			Property p = event.getProperty();
			Container container = studiesTree.getContainerDataSource();
			Property p2 = container.getContainerProperty(new Integer(p.toString()), "id");
	
			try {
				Study study = studyDataManager.getStudy(Integer.parseInt(p2.toString()));
				
				if (study==null) return;
				System.out.println("selected from folder tree:" + study.toString());
				
				if (study.getName() != null){
					generateTabContent(studiesTabsheet, study);
					//repaintTab(studiesTabsheet.getSelectedTab(), study);
					studiesTabsheet.setImmediate(true);	
				}
				
				
			} catch (NumberFormatException e) {
				
				e.printStackTrace();
				
			} catch (MiddlewareQueryException e) {
				
				e.printStackTrace();
			}
			
			requestRepaintAll();
			

		}

	}

	protected TabSheet generateTabSheet() {
		TabSheet tab = new TabSheet();

		tab.setImmediate(true);

		//repaintTab(tab.getSelectedTab());

		tab.setSizeFull();

		return tab;
	}

}
