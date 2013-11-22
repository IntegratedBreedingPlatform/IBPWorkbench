package org.generationcp.ibpworkbench.ui.dashboard.preview;

import java.util.List;

import com.vaadin.data.Item;
import com.vaadin.terminal.ThemeResource;
import com.vaadin.ui.Panel;
import com.vaadin.ui.Tree;
import com.vaadin.ui.themes.Reindeer;

import org.generationcp.commons.hibernate.ManagerFactoryProvider;
import org.generationcp.commons.vaadin.spring.SimpleResourceBundleMessageSource;
import org.generationcp.ibpworkbench.Message;
import org.generationcp.ibpworkbench.ui.dashboard.listener.DashboardMainTreeListener;

import org.generationcp.middleware.pojos.workbench.Project;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

/**
 * Created with IntelliJ IDEA.
 * User: cyrus
 * Date: 11/19/13
 * Time: 7:20 PM
 * To change this template use File | Settings | File Templates.
 */
@Configurable
public class NurseryListPreview extends Panel {

    private static final long serialVersionUID = 1L;
    
    private NurseryListPreviewPresenter presenter;
    
    private static final Logger LOG = LoggerFactory.getLogger(NurseryListPreview.class);
    
    private Tree treeView;

    private Project project;

    @Autowired 
    private ManagerFactoryProvider managerFactoryProvider;
    
    @Autowired
    private SimpleResourceBundleMessageSource messageSource;
    
    
    public NurseryListPreview(Project project) {
        
        this.project = project;
        presenter = new NurseryListPreviewPresenter(this, project);

        try {
            if (project != null){
                assemble();
            }
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
        }

    }

    
    public void setProject(Project project){
        this.project = project;
        presenter = new NurseryListPreviewPresenter(this, project);
        presenter.generateTreeNodes();
        this.addComponent(treeView);
    }
    
    protected void initializeComponents() {
        this.setHeight("400px");
    }
    
    
    public void generateTree(List<TreeNode> treeNodes){
        
        treeView = new Tree();
        
        ThemeResource folderResource =  new ThemeResource("images/folder.png");
        ThemeResource leafResource =  new ThemeResource("images/leaf_16.png");
        
        doCreateTree(treeNodes, treeView, null, folderResource, leafResource);
        
        treeView.addListener(new DashboardMainTreeListener(this, project));
        treeView.setImmediate(true);
        
    }
    
    private void doCreateTree(List<TreeNode> treeNodes, Tree treeView, Object parent, ThemeResource folder, ThemeResource leaf){
        for(TreeNode treeNode : treeNodes){
            treeView.addItem(treeNode.getId());
            treeView.setItemCaption(treeNode.getId(), treeNode.getName());
            
            ThemeResource resource = folder;
            if(treeNode.isLeaf()){
                resource = leaf;
                treeView.setChildrenAllowed(treeNode.getId(), false);
                //we add listener if its the leaf
                Item item = treeView.getItem(treeNode.getId());
                
                if (treeNode.getName().equals(messageSource.getMessage(Message.MY_STUDIES)) 
                		|| treeNode.getName().equals(messageSource.getMessage(Message.SHARED_STUDIES))){
                	resource = folder;
                }
            }
            
            treeView.setItemIcon(treeNode.getId(), resource);
            if(parent != null){
                treeView.setParent(treeNode.getId(), parent);
            }
            doCreateTree(treeNode.getTreeNodeList(), treeView, treeNode.getId(), folder, leaf);
        }
    }

    protected void initializeLayout() {
        this.setStyleName(Reindeer.PANEL_LIGHT);
        this.setSizeFull();
    }

    protected void initializeActions() {

    }

    protected void assemble() throws Exception {
        initializeComponents();
        initializeLayout();
        initializeActions();
    }
    

    public NurseryListPreviewPresenter getPresenter() {
        return presenter;
    }

    
    public void setPresenter(NurseryListPreviewPresenter presenter) {
        this.presenter = presenter;
    }


    public ManagerFactoryProvider getManagerFactoryProvider() {
        return managerFactoryProvider;
    }

    
    public void setManagerFactoryProvider(
            ManagerFactoryProvider managerFactoryProvider) {
        this.managerFactoryProvider = managerFactoryProvider;
    }

    
}
