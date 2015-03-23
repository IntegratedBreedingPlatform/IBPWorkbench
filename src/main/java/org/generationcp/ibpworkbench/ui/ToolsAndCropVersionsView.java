package org.generationcp.ibpworkbench.ui;

import com.vaadin.data.util.BeanContainer;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Table;
import com.vaadin.ui.VerticalLayout;
import org.generationcp.commons.vaadin.spring.InternationalizableComponent;
import org.generationcp.commons.vaadin.spring.SimpleResourceBundleMessageSource;
import org.generationcp.commons.vaadin.theme.Bootstrap;
import org.generationcp.ibpworkbench.Message;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.manager.api.WorkbenchDataManager;
import org.generationcp.middleware.pojos.workbench.CropType;
import org.generationcp.middleware.pojos.workbench.Tool;
import org.generationcp.middleware.pojos.workbench.ToolType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PropertiesLoaderUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

@Configurable
public class ToolsAndCropVersionsView extends VerticalLayout implements InitializingBean, InternationalizableComponent {
    private static final long serialVersionUID = 1L;
    
    private Label lblToolVersions;
    
    private Table tblTools;
    private Table tblCrops;

    @Autowired
    private WorkbenchDataManager workbenchDataManager;

    @Autowired
    private SimpleResourceBundleMessageSource messageSource;

    private static final Logger LOG = LoggerFactory.getLogger(ToolsAndCropVersionsView.class);
	private static final String CROP_NAME = "cropName";
	private static final String G_VERSION = "gVersion";
	private static final String TITLE = "title";
	private static final String TOOL_NAME_PREFIX = "tool_name.";
	private static final String TOOL_VERSION_PREFIX = "tool_version.";
	private static final String VERSION = "version";

    @Override
    public void afterPropertiesSet() throws Exception {
        assemble();
    }
    
    protected void initializeComponents() {
        lblToolVersions = new Label();
        lblToolVersions.setStyleName(Bootstrap.Typography.H1.styleName());
        
        initializeToolsTable();

        initializeCropsTable();
    }

    private void initializeCropsTable() {
        tblCrops = new Table();
        tblCrops.setImmediate(true);
        tblCrops.setColumnCollapsingAllowed(true);

        BeanContainer<Long,CropType> cropContainer = new BeanContainer<Long, CropType>(CropType.class);
        cropContainer.setBeanIdProperty(CROP_NAME);

        try {
            cropContainer.addAll(workbenchDataManager.getInstalledCentralCrops());

            tblCrops.setContainerDataSource(cropContainer);
            tblCrops.addGeneratedColumn(G_VERSION, new Table.ColumnGenerator() {
                @Override
                public Object generateCell(Table source, Object itemId, Object colId) {
                    final CropType beanItem = ((BeanContainer<Long,CropType>) source.getContainerDataSource()).getItem(itemId).getBean();

                    if (beanItem.getVersion() == null || beanItem.getVersion().trim().isEmpty()) {
                        return new Label("<em>Not Available</em>", Label.CONTENT_XHTML);
                    } else {
                        return beanItem.getVersion().trim();
                    }


                }
            });

            tblCrops.setVisibleColumns(new String[]{CROP_NAME, G_VERSION});
            tblCrops.setColumnHeaders(new String[]{"Crop Name", VERSION});
            tblCrops.setColumnExpandRatio(CROP_NAME,0.7F);
            tblCrops.setColumnExpandRatio(G_VERSION,0.3F);
        } catch (MiddlewareQueryException e) {
            LOG.error("Oops, something happened!",e);
        }

    }

    protected void initializeToolsTable() {
        tblTools = new Table();
        tblTools.setImmediate(true);
        tblTools.setColumnCollapsingAllowed(true);
        
        BeanContainer<Long, Tool> toolContainer = new BeanContainer<Long, Tool>(Tool.class);
        toolContainer.setBeanIdProperty("toolId");
        
        String[] propertyNames = new String[] {"mysql","flapjack","jre","tomcat","r"};
        
        try {
        	
        	Resource resource = new ClassPathResource("/workbench_tools.properties");
            Properties props = PropertiesLoaderUtils.loadProperties(resource);
        	Long toolId = 0L;
            List<Tool> tools = workbenchDataManager.getAllTools();
            List<String> addedToolNames = new ArrayList<String>();
            for (Tool tool : tools) {

            	if("gdms".equals(tool.getToolName())) {
            		//temporarily hide gdms
            		continue;
            	}
            	
                if (!(ToolType.ADMIN.equals(tool.getToolType()) || ToolType.WORKBENCH.equals(tool.getToolType()))
                    && !addedToolNames.contains(tool.getTitle())) {
                    addedToolNames.add(tool.getTitle());
                    
                    toolContainer.addBean(tool);
                    toolId++;
                }

            }
            
            for(String name: propertyNames) {
            	 toolId++;
            	 Tool t = new Tool();
                 t.setToolName(props.getProperty(TOOL_NAME_PREFIX+name));
                 t.setVersion(props.getProperty(TOOL_VERSION_PREFIX+name));
                 t.setParameter(props.getProperty(TOOL_NAME_PREFIX+name));
                 t.setPath(props.getProperty(TOOL_NAME_PREFIX+name));
                 t.setToolId(toolId);
                 t.setTitle(props.getProperty(TOOL_NAME_PREFIX+name));
                 toolContainer.addBean(t);
            }
           
        } catch (MiddlewareQueryException e) {
            LOG.error(e.getMessage(),e);
        } catch (IOException ioe) {
        	LOG.error(ioe.getMessage(),ioe);
        }

        toolContainer.sort(new String[]{TITLE},new boolean[] {true});

        tblTools.setContainerDataSource(toolContainer);
        
        String[] columns = new String[] {TITLE, VERSION};
        tblTools.setVisibleColumns(columns);
    }
    
    protected void initializeLayout() {
        setMargin(new MarginInfo(false,true,true,true));
        setSpacing(true);
        
        final HorizontalLayout root = new HorizontalLayout();
        root.setSpacing(true);
        root.setSizeFull();

        this.addComponent(lblToolVersions);

        final VerticalLayout cropsContainer = new VerticalLayout();
        cropsContainer.setSpacing(true);
        cropsContainer.addComponent(tblCrops);
        cropsContainer.addComponent(new Label("<em>Not available</em> means crop is installed prior to version BMS 3.0",Label.CONTENT_XHTML));

        root.addComponent(tblTools);
        root.addComponent(cropsContainer);
        this.addComponent(root);

        tblCrops.setWidth("100%");
        cropsContainer.setWidth("100%");
        tblTools.setWidth("100%");

        this.setWidth("100%");
    }
    
	protected void initializeActions() {
		//do nothing
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
        messageSource.setValue(lblToolVersions, Message.TOOL_VERSIONS);
        
        messageSource.setColumnHeader(tblTools, TITLE, Message.TOOL_NAME);
        messageSource.setColumnHeader(tblTools, VERSION, Message.VERSION);
    }
}
