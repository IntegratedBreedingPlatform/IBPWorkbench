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
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.generationcp.commons.vaadin.util.MessageNotifier;
import org.generationcp.ibpworkbench.util.GxEUtility;
import org.generationcp.ibpworkbench.util.TableItems;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.manager.Database;
import org.generationcp.middleware.manager.ManagerFactory;
import org.generationcp.middleware.manager.api.ManagerFactoryProvider;
import org.generationcp.middleware.manager.api.WorkbenchDataManager;
import org.generationcp.middleware.pojos.workbench.Project;
import org.generationcp.middleware.v2.domain.DatasetReference;
import org.generationcp.middleware.v2.domain.FolderReference;
import org.generationcp.middleware.v2.domain.Reference;
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
import com.vaadin.data.util.IndexedContainer;
import com.vaadin.ui.AbstractSelect;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.CheckBox;
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
@SuppressWarnings("unchecked")
@Configurable
public class GxeAnalysisComponentPanel extends VerticalLayout implements InitializingBean{
    
    private static final Logger LOG = LoggerFactory.getLogger(GxeAnalysisComponentPanel.class);
    private static final long serialVersionUID = 1L;
    
   
 //   private TwinColSelect select;
    
    private Table tblDataSet;
    private Tree studiesTree;
    private Object[][] studies;
    private String[] stringList;
    private TabSheet studiesTabsheet;
    private int countme = 0;
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
    	//System.out.println("Project is " + project.getProjectName());
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

    protected Object[][] createStudies()
    {
    	studies = new Object[][]{
                new Object[]{"Mercury"}, 
                new Object[]{"Venus"},
                new Object[]{"Earth", "The Moon"},    
                new Object[]{"Mars", "Phobos", "Deimos"},
                new Object[]{"Jupiter", "Io", "Europa", "Ganymedes",
                                        "Callisto"},
                new Object[]{"Saturn",  "Titan", "Tethys", "Dione",
                                        "Rhea", "Iapetus"},
                new Object[]{"Uranus",  "Miranda", "Ariel", "Umbriel",
                                        "Titania", "Oberon"},
                new Object[]{"Neptune", "Triton", "Proteus", "Nereid",
                                        "Larissa"}};
    	
    	return studies;
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
    protected void generateChildren(FolderReference folderParent, Object folderParentItem) throws MiddlewareQueryException
    {
    	
    	List<Reference> children  = studyDataManager.getChildrenOfFolder(folderParent.getId());
    	
		if (children.size() == 0) {
            // The planet has no moons so make it a leaf.
        	//studiesTree.setChildrenAllowed(folderParent.getName(), false);
        } else {
            // Add children (moons) under the planets.
            for (Reference childStudy: children) {
            	 if(childStudy instanceof StudyReference)
            		 addCaptionedItem(childStudy.getName(), childStudy.getId(), folderParentItem);
            	 else if(childStudy instanceof FolderReference)
            	 {
            		 Object myfolderParentItem = addCaptionedItem(childStudy.getName(), childStudy.getId(), folderParentItem);
                 	 generateChildren((FolderReference)childStudy,myfolderParentItem);
            	 } 
               
            }

            // Expand the subtree.
           // studiesTree.expandItemsRecursively(studies);
        }
    }
    protected void refreshStudies() throws MiddlewareQueryException
    {
    	
        
    	List<FolderReference> listFolder = studyDataManager.getRootFolders(Database.CENTRAL);
		
    	studiesTree.addContainerProperty("caption", String.class, "");
    	studiesTree.addContainerProperty("id", String.class, "");
    	
    	studiesTree.setItemCaptionMode(AbstractSelect.ITEM_CAPTION_MODE_PROPERTY);
    	studiesTree.setItemCaptionPropertyId("caption");
    	
        
    	 /* Add planets as root items in the tree. */
    	for(FolderReference folderParent : listFolder)
		{
    		Object folderParentItem = addCaptionedItem(folderParent.getName(), folderParent.getId(), null);
        	generateChildren(folderParent,folderParentItem);
        	
        }
        
        
    }
    protected void repaintTab(Component comp)
    {
    	
    	if (comp != null) {
    		VerticalLayout container = (VerticalLayout)comp;
        	container.setSpacing(true);
        	container.setMargin(true,false,false,false);
        	container.removeAllComponents();
        	
        	Label tabTitle = new Label("&nbsp;&nbsp;" + "Adjusted means datasets",Label.CONTENT_XHTML);
        	tabTitle.setStyleName("gcp-content-title");
        	
        	container.addComponent(tabTitle);
        	container.addComponent(tblDataSet);
        	
        	
        	container.setExpandRatio(tblDataSet,1.0F);
        	
        	container.setSizeFull();
    	}
    }
    protected VerticalLayout generateTabContent(TabSheet tab, String caption)
    {
    	
    	/*
    	Accordion accord = new Accordion();
    	tblDataSet.setWidth("800px");
    	tblDataSet.setHeight("700px");
    	
    	accord.addTab(tblDataSet);
    	accord.getTab(tblDataSet).setCaption("Table");
    	accord.setCaption(caption);
    	
    	tab.addTab(accord);
    
    	tab.getTab(accord).setCaption(caption);
    	tab.addListener(new StudiesTabFocusListener(tab));
    	
    	return accord;
    	
    	*/
    	
    	VerticalLayout tabContainer = new VerticalLayout();
    	tabContainer.setStyleName("gcp-light-grey");
    	tabContainer.setSpacing(true);
    	tabContainer.setMargin(true,false,false,false);
    	
    	//TODO: unhardcode this!
    	Label tabTitle = new Label("&nbsp;&nbsp;" + "Adjusted means datasets",Label.CONTENT_XHTML);
    	tabTitle.setStyleName("gcp-content-title");
    	tabContainer.addComponent(tabTitle);
    	tabContainer.addComponent(tblDataSet);
    	
    	tabContainer.setExpandRatio(tblDataSet,1.0F);
    	tabContainer.setSizeFull();
    	
    	tab.addComponent(tabContainer);
    	
    	tab.getTab(tabContainer).setCaption(caption);
    	tab.addListener(new StudiesTabFocusListener(tab));
    	
    	return tabContainer;
    }
    
    protected void initializeComponents(){

       setSpacing(true);
       setMargin(true);
       createStudies();
       
       
       HorizontalLayout horizontal = new HorizontalLayout();
       
       
       ManagerFactory managerFactory = managerFactoryProvider.getManagerFactoryForProject(project);
   	   studyDataManager = managerFactory.getNewStudyDataManager();
       
       
       studiesTree = new Tree("Studies");
       studiesTree.setSizeFull();
       studiesTree.setImmediate(true);
       
       try {
			refreshStudies();
		} catch (MiddlewareQueryException e) {
			// TODO Auto-generated catch block
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
        horizontal.setExpandRatio(studiesTabsheet,1.0F);
        
        
        
        addComponent(horizontal);
        
        Button button = new Button("Export study dataset to Breeding View Excel input");
        Button gxebutton = new Button("Launch the Breeding View's GxE Analysis");
        
        button.addListener(new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				/*File csvFile =GxEUtility.generateGxEInputCSV(event.getComponent().getApplication().getMainWindow(),
						tblDataSet.getContainerDataSource(),
						project,
						new String[] {"environment","genotype","height","maturity","rust","height 1"});	// NOTE: the string array are the table headers
					*/
				
				File xlsFile = GxEUtility.exportGeneratedXlsFieldbook(
						tblDataSet.getContainerDataSource(),
						project,
						"xlsInput.xls");
				
				LOG.debug(xlsFile.getAbsolutePath());
				
				MessageNotifier.showMessage(event.getComponent().getWindow(),"GxE file saved","Successfully created GxE Excel input file for the breeding_view");
			}
		});
        
        this.setExpandRatio(horizontal,1.0F);
        
        HorizontalLayout btnLayout = new HorizontalLayout();
        btnLayout.setSizeUndefined();
        btnLayout.setSpacing(true);
        btnLayout.addComponent(button);
        btnLayout.addComponent(gxebutton);
        
        this.addComponent(btnLayout);
        
    }
    
  

   
    private void initializeTable()
    {
    	
    	tblDataSet = new Table();
    	tblDataSet.setImmediate(true);
        tblDataSet.setSizeFull();
    	
    	
        final List<Object> columnIds = new ArrayList<Object>();
        final List<String> columnHeaders = new ArrayList<String>();
       
        // prepare the container
        IndexedContainer container = new IndexedContainer();
        //container.addContainerProperty("userId", Integer.class, null);
        
        
        for (String role : stringList) {
        	
        	if(role.equalsIgnoreCase("environment") || role.equalsIgnoreCase("genotype"))
            {
	            columnIds.add(role);
	           // columnIds.add(role+"_value");
	            columnHeaders.add(role);
	            container.addContainerProperty(role, Label.class, null);
	           // container.addContainerProperty(role+"_value", CheckBox.class, null);
	            
            }else{
	            columnIds.add(role);
	            columnHeaders.add(role);
	           // container.addContainerProperty(role, Boolean.class, Boolean.TRUE);
	            container.addContainerProperty(role, CheckBox.class, new CheckBox());
            }
        }
        
        
        tblDataSet.setContainerDataSource(container);
        
        tblDataSet.setVisibleColumns(columnIds.toArray(new Object[0]));
        tblDataSet.setColumnHeaders(columnHeaders.toArray(new String[0]));
        
        tblDataSet.setEditable(true);
       
    }
    
    /*
     * CHANGE THIS FUNCTION IF YOU WANT A SEPARATE LIST OF HEADERS
     * */
    protected void initializeHeader() 
    {
    	 Container container = tblDataSet.getContainerDataSource();
    
         container.getItemIds();
         
         
         for (String role : stringList) {
         	
         	if(role.equalsIgnoreCase("environment") || role.equalsIgnoreCase("genotype"))
             {
 	            container.addContainerProperty(role, Label.class, null);
 	           // container.addContainerProperty(role+"_value", CheckBox.class, null);
 	            
             }else{
 	           container.addContainerProperty(role, CheckBox.class, new CheckBox());
             }
         }
         String obj = "FirstRow";
         
         //Item item = container.addItem(obj);
         
         int cnt = 0;
         
         TableItems[] headers = new TableItems[stringList.length];
         
        
         
         //Initialize the table items 
         for (String role : stringList) 
         {
        	 headers[cnt] = new TableItems(); 
        	 headers[cnt].setColumnId(role);
        	 if(role.equalsIgnoreCase(" "))
        	 {
        		 headers[cnt].setType("checkboxall");
        		 headers[cnt].setLabel("SELECT ALL");
        		 headers[cnt].setRowId(obj);
        		 headers[cnt].setValue(true);
        		 
        	 }else if(cnt < 3)
        	 {
        		 headers[cnt].setType("String");
        		 headers[cnt].setLabel(role);
        		 headers[cnt].setRowId(obj);
        		 headers[cnt].setValue(true);
        	 }else
        	 {
        		 headers[cnt].setType("checkbox_column");
        		 headers[cnt].setLabel(role);
        		 headers[cnt].setRowId(obj);
        		 headers[cnt].setValue(true);
        	 }
        	 cnt++;		 
         }
         // set the table columns
         
         createRow("FirstRow",headers,container);
         
         tblDataSet.getTableFieldFactory();
         
        
    }
    
    /*
     * @RowId = Id of the row to be inserted
     * @TableItems[] = list of table items object. One array of TableItems would be equal to one row
     */
    private void createRow(String RowId,TableItems[] tableItems, Container container)
    {
    	
    	Object[] obj = new Object[tableItems.length];
    	
    	for(Integer i = 0; i < tableItems.length; i++){
    	
    		
    			if(tableItems[i].getType().equalsIgnoreCase("checkbox"))
       	 		{
    				
       	 			CheckBox cb = new CheckBox();
       	 			if(!RowId.equalsIgnoreCase("firstrow"))
       	 			{
       	 				cb.addStyleName("hidecheckbox");
       	 			}
		       	 	cb.setCaption(tableItems[i].getLabel());
		       	 	cb.setValue(tableItems[i].getValue());
		       	 	//cb.setEnabled(false);
		       	 	
		       	 	obj[i] = cb;
       	 		}else if(tableItems[i].getType().equalsIgnoreCase("string"))
       	 		{
       	 			obj[i] = tableItems[i].getLabel();
       	 		}else if(tableItems[i].getType().equalsIgnoreCase("checkboxall"))
       	 		{
	       	 		CheckBox cb = new CheckBox();
		       	 	cb.setCaption(tableItems[i].getLabel());
		       	 	cb.setValue(tableItems[i].getValue());
		       	 	
		       		cb.setImmediate(true);
         		
		       		class CheckboxListener implements ValueChangeListener{
         			
					private static final long serialVersionUID = 1L;
					
         			public CheckboxListener ()
         			{
         				
         			}
                     @Override
                     public void valueChange(ValueChangeEvent event) {
                         // TODO Auto-generated method stub
                     	try{
                           
                             Container container = tblDataSet.getContainerDataSource();
                             Collection<?> items =  container.getItemIds();
                             
                             
                             for ( Iterator<?> myitems = items.iterator(); myitems.hasNext(); ) 
                          	 {
                            	
                            	 
                          	   Integer key = (Integer) myitems.next();
                          	   Item item = container.getItem(key);
                          	  
                          	   for(String column: stringList)
		  	                   	   {
		  	                   		   if(column.equalsIgnoreCase(" ") && key.intValue() == 0)
		  	                   			   continue;
		  	                   		   if(column.equalsIgnoreCase("environment") || column.equalsIgnoreCase("genotype"))
		  	                   			   continue;
		  	                   		   //added this since we dont have any checkboxes inside anymore
		  	                   		   if(!column.equalsIgnoreCase(" ") && key.intValue() > 0)
		  	                   			   continue;
		  	                   		   setAll = true;
		  	                   		   	CheckBox ba = (CheckBox) item.getItemProperty(column).getValue();
		  	                         	ba.setValue(event.getProperty().getValue());
		  	                   		    
		  	                   	   }
	                          	 
	                   		   
                          	   
                             }
                           requestRepaintAll();
                          
                      }catch(Exception e)
                      { e.printStackTrace();}
                      }
						
         		}
         		
         		cb.addListener(new CheckboxListener());
		       	 	
		       	 	obj[i] = cb;
       	 		}
    			
    		// column checkbox
       	 	else if(tableItems[i].getType().equalsIgnoreCase("checkbox_column"))
   	 		{
       	 		CheckBox cb = new CheckBox();
	       	 	cb.setCaption(tableItems[i].getLabel());
	       	 	cb.setValue(tableItems[i].getValue());
	       	 	
	       		cb.setImmediate(true);
     		
	       		class CheckboxListener implements ValueChangeListener{
     			
				private static final long serialVersionUID = 1L;
				public CheckboxListener (String propertyId)
     			{
     			}
                 @Override
                 public void valueChange(ValueChangeEvent event) {
                     // TODO Auto-generated method stub
                 	try{
                         
                         Container container = tblDataSet.getContainerDataSource();
                         container.getItemIds();
                         
                         container.getItem(0);
                         
                       requestRepaintAll();
                      
                  }catch(Exception e)
                  { e.printStackTrace();}
                  }
					
     		}
     		
     		cb.addListener(new CheckboxListener(tableItems[i].getLabel()));
	       	 	
	       	 	obj[i] = cb;
   	 		}
    			
    		// row checkbox
       	 	else if(tableItems[i].getType().equalsIgnoreCase("checkbox_row"))
	 		{
    	 		CheckBox cb = new CheckBox();
	       	 	cb.setCaption(tableItems[i].getLabel());
	       	 	cb.setValue(tableItems[i].getValue());
	       	 	
	       		cb.setImmediate(true);
  		
	       		class CheckboxListener implements ValueChangeListener{
  			
				private static final long serialVersionUID = 1L;
				public CheckboxListener (Integer propertyId)
	  			{
	  			}
	              @Override
	              public void valueChange(ValueChangeEvent event) {
	                  // TODO Auto-generated method stub
	              	try{
	                      System.out.println(event);
	                      Container container = tblDataSet.getContainerDataSource();
	                      container.getItemIds();
	                      
	                      
	                   	   container.getItem(0);
	                   	   
	                   	   	 //set the select all to false
	                      
	                       
	                    requestRepaintAll();
	                   
	               }catch(Exception e)
	               { e.printStackTrace();}
	               }
						
	  		}
  		
	  		cb.addListener(new CheckboxListener(countme));
		       	 	
		       	 	obj[i] = cb;
		 		}
	    	}
    	
    	
    	tblDataSet.addItem(obj, countme);
    
    	
    	

    	
    }
	private void addNewDataSetItem(String StudyName, String StudyTab, TableItems[] tableItems)
	{
		Container container = tblDataSet.getContainerDataSource();
		
		countme++;
        
	    container.getItemIds();
	    
	    for (String role : stringList) {
         	
         	if(!role.equalsIgnoreCase(" "))
             {
         		
 	           // container.addContainerProperty(role, Label.class, null);
 	           // container.addContainerProperty(role+"_value", CheckBox.class, null);
 	            
             }else{
 	         //  container.addContainerProperty(role, CheckBox.class, new CheckBox());
             }
         }
        String obj = ""+countme;
        
        createRow(obj,tableItems, container);
        
        
	}
	private void setDataSetHeaders(String[] list)
	{
		stringList = list;
		
	}
	
	
    private void createTableContents() {
    	
    	setDataSetHeaders(new String[] {" ","environment","genotype","height","maturity","rust","height 1"});
    	initializeTable();
    	initializeHeader();
    	for (Integer x = 0; x <= 1000; x++){
    		Integer y = 0;
    		TableItems[] row = new TableItems[stringList.length];
    		for (String col : stringList){
    			
    			TableItems item = new TableItems();
    			item.setColumnId(col+"_value");
    			item.setRowId(x);
    			if (col.equalsIgnoreCase("environment") || col.equalsIgnoreCase("genotype")){
    				item.setType("String");
    				item.setLabel("Data" + x);
    			}else if (col.equalsIgnoreCase(" ")){
    				item.setType("checkbox_row");
    				item.setLabel(" ");
    				item.setValue(true);
    			}else{
    				item.setType("CheckBox");
    				item.setLabel(GxEUtility.randomInRange(0, 100).toString());
    				item.setValue(true);
    			}
    			row[y] = item ; y++;
    		}
    		addNewDataSetItem("environment","height", row);
    	}
    	
    }

   

    protected void initializeLayout() {
        setSpacing(true);
        setMargin(true);
      
        setSizeFull();
    }
    	
    
    protected void initializeActions() {
      //  newMemberButton.addListener(new OpenNewProjectAddUserWindowAction(select));
       studiesTree.addListener(new StudiesTreeAction());
      // studiesTabsheet.addListener(new StudiesTabFocusListener()); 
       
        
    }
    
    private class StudiesTabFocusListener implements SelectedTabChangeListener
    {
    	public StudiesTabFocusListener(TabSheet tab)
    	{
    		
    	}
		@Override
		public void selectedTabChange(SelectedTabChangeEvent event) {
			// TODO Auto-generated method stub
			//System.out.println(event.getTabSheet().getSelectedTab().getCaption());
		//	System.out.println(event.getComponent().getCaption());
			
			//System.out.println("selectedtab caption "+studiesTabsheet.getSelectedTab().getCaption());
			repaintTab(studiesTabsheet.getSelectedTab());
			
			
		}
    	
    }
    private class StudiesTreeAction implements ValueChangeListener
    {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		@Override
		public void valueChange(ValueChangeEvent event) {
			// TODO Auto-generated method stub
			System.out.println(event);
			System.out.println(event.getProperty().getValue());
			studiesTree.getItemIds();
			Property p = event.getProperty();
			Container container = studiesTree.getContainerDataSource();
			
			container.getContainerProperty(new Integer(p.toString()), "caption");
            Property p2 = container.getContainerProperty(new Integer(p.toString()), "id");
            
                 
            try {
            	List<DatasetReference> datasets = studyDataManager.getDatasetReferences(new Integer(p2.toString()));
				p2.toString();
            	studiesTabsheet.removeAllComponents();
				studiesTabsheet.setImmediate(true);
			    
				for(DatasetReference data: datasets)
				{
					generateTabContent(studiesTabsheet, data.getName());
				    	
				}
				
				repaintTab(studiesTabsheet.getSelectedTab());
				
            } catch (NumberFormatException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
            } catch (MiddlewareQueryException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
			}
			
		}
    	
    }
    
    protected TabSheet generateTabSheet()
    {
    	TabSheet tab = new TabSheet();
    	
    	tab.setImmediate(true);
    	
    	createTableContents();
    	generateTabContent(tab, "AKMSHSSA");
    	generateTabContent(tab, "AKMSSSSA");
    	
        repaintTab(tab.getSelectedTab());
        
    	tab.setSizeFull();
    	
        tab.addListener(new StudiesTabFocusListener(tab)); 
        

        
    	return tab;
    }
  


    


  
        
       
        
        
}
