package org.generationcp.ibpworkbench.ui.dashboard.preview;

import java.util.List;

import org.generationcp.commons.hibernate.ManagerFactoryProvider;
import org.generationcp.commons.vaadin.spring.SimpleResourceBundleMessageSource;
import org.generationcp.commons.vaadin.theme.Bootstrap;
import org.generationcp.commons.vaadin.util.MessageNotifier;
import org.generationcp.ibpworkbench.IBPWorkbenchApplication;
import org.generationcp.ibpworkbench.Message;
import org.generationcp.ibpworkbench.actions.LaunchWorkbenchToolAction;
import org.generationcp.ibpworkbench.ui.WorkbenchMainView;
import org.generationcp.commons.vaadin.ui.ConfirmDialog;
import org.generationcp.ibpworkbench.ui.dashboard.listener.DashboardMainTreeListener;
import org.generationcp.ibpworkbench.ui.dashboard.listener.NurseryListTreeExpandListener;
import org.generationcp.ibpworkbench.ui.sidebar.WorkbenchSidebar;
import org.generationcp.middleware.domain.dms.FolderReference;
import org.generationcp.middleware.domain.dms.Reference;
import org.generationcp.middleware.pojos.dms.DmsProject;
import org.generationcp.middleware.pojos.workbench.Project;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import com.vaadin.data.util.HierarchicalContainer;
import com.vaadin.event.DataBoundTransferable;
import com.vaadin.event.Transferable;
import com.vaadin.event.dd.DragAndDropEvent;
import com.vaadin.event.dd.DropHandler;
import com.vaadin.event.dd.acceptcriteria.AcceptAll;
import com.vaadin.event.dd.acceptcriteria.AcceptCriterion;
import com.vaadin.terminal.ThemeResource;
import com.vaadin.terminal.gwt.client.ui.dd.VerticalDropLocation;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TextField;
import com.vaadin.ui.Tree;
import com.vaadin.ui.Tree.TreeDragMode;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.Reindeer;

/**
 * Created with IntelliJ IDEA.
 * User: cyrus
 * Date: 11/19/13
 * Time: 7:20 PM
 * <p/>
 * Revision done by mae
 * 1. Display hierarchy of studies from root to children per database instance (instead of categories like year, season and study type)
 */
@Configurable
public class NurseryListPreview extends VerticalLayout {

    private static final long serialVersionUID = 1L;

    private NurseryListPreviewPresenter presenter;

    private static final Logger LOG = LoggerFactory.getLogger(NurseryListPreview.class);

    private Tree treeView;

    private Project project;

    private Panel panel;

    private ThemeResource folderResource = new ThemeResource("images/folder.png");
    private ThemeResource leafResource = new ThemeResource("images/leaf_16.png");

    @Autowired
    private ManagerFactoryProvider managerFactoryProvider;

    @Autowired
    private SimpleResourceBundleMessageSource messageSource;
    private HorizontalLayout toolbar;
    private Button openStudyManagerBtn;
    private Button renameFolderBtn;
    private Button addFolderBtn;
    private Button deleteFolderBtn;

    public static String SHARED_STUDIES;
    public static String MY_STUDIES;

    public static final int ROOT_FOLDER = 1;

    public NurseryListPreview(Project project) {

        this.project = project;
        presenter = new NurseryListPreviewPresenter(this, project);

        try {
            if (project != null) {
                assemble();
            }
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
        }
    }


    public void setProject(Project project) {
        this.removeAllComponents();
        this.setSizeFull();

        panel = new Panel();
        panel.removeAllComponents();

        this.addComponent(buildToolbar());

        this.project = project;

        MY_STUDIES = messageSource.getMessage(Message.MY_STUDIES);
        SHARED_STUDIES = messageSource.getMessage(Message.SHARED_STUDIES);

        presenter = new NurseryListPreviewPresenter(this, project);
        //presenter.generateTreeNodes();
        presenter.generateInitialTreeNodes();

        CssLayout treeContainer = new CssLayout();
        treeContainer.setSizeUndefined();
        treeContainer.addComponent(treeView);

        panel.setContent(treeContainer);
        panel.setStyleName(Reindeer.PANEL_LIGHT);
        panel.setSizeFull();

        this.addComponent(panel);
        this.setExpandRatio(panel, 1.0F);
    }

    protected void initializeComponents() {
        //this.setHeight("400px");
    }

    public void generateTopListOfTree(List<FolderReference> centralFolders, List<FolderReference> localFolders) {

        treeView = new Tree();
        treeView.setContainerDataSource(new HierarchicalContainer());
        treeView.setDropHandler(new NurseryTreeDropHandler(treeView, presenter));
        treeView.setDragMode(TreeDragMode.NODE);

        addInstanceTree(treeView, localFolders, false);
        addInstanceTree(treeView, centralFolders, true);

        treeView.addListener(new NurseryListTreeExpandListener(this));
        treeView.addListener(new DashboardMainTreeListener(this, project));
        treeView.setImmediate(true);

    }


    private void addInstanceTree(Tree treeView, List<FolderReference> folders, boolean isCentral) {


        String folderName = null;
        if (isCentral) {
            folderName = SHARED_STUDIES;
        } else {
            folderName = MY_STUDIES;
        }

        treeView.addItem(folderName);
        treeView.setItemCaption(folderName, folderName);
        treeView.setItemIcon(folderName, folderResource);


        for (FolderReference folderReference : folders) {
            treeView.addItem(folderReference.getId());
            treeView.setItemCaption(folderReference.getId(), folderReference.getName());
            treeView.setParent(folderReference.getId(), folderName);
            boolean isFolder = getPresenter().isFolder(folderReference.getId());

            if (isFolder) {
                treeView.setChildrenAllowed(folderReference.getId(), true);
                treeView.setItemIcon(folderReference.getId(), folderResource);
            } else {
                treeView.setChildrenAllowed(folderReference.getId(), false);
                treeView.setItemIcon(folderReference.getId(), leafResource);
            }

            treeView.setSelectable(true);
        }
    }


//	public void generateTree(List<TreeNode> treeNodes){
//        
//        treeView = new Tree();
//        treeView.setDragMode(TreeDragMode.NODE);
//
//        doCreateTree(treeNodes, treeView, null, folderResource, leafResource);
//        
//        treeView.addListener(new DashboardMainTreeListener(this, project));
//        treeView.setImmediate(true);
//        
//    }

//    private void doCreateTree(List<TreeNode> treeNodes, Tree treeView, Object parent, ThemeResource folder, ThemeResource leaf){
//        for(TreeNode treeNode : treeNodes){
//        	
//            treeView.addItem(treeNode.getId());
//            treeView.setItemCaption(treeNode.getId(), treeNode.getName());
//
//            // Set resource icon
//            ThemeResource resource = folder;
//            if(treeNode.isLeaf()){
//                resource = leaf;
//                treeView.setChildrenAllowed(treeNode.getId(), false);
//                //we add listener if its the leaf
//                Item item = treeView.getItem(treeNode.getId());
//                
//                if (treeNode.getName().equals(messageSource.getMessage(Message.MY_STUDIES)) 
//                		|| treeNode.getName().equals(messageSource.getMessage(Message.SHARED_STUDIES))){
//                	resource = folder;
//                }
//            }
//            treeView.setItemIcon(treeNode.getId(), resource);
//
//            // Disable arrow of folders with no children
//            if (treeNode.getTreeNodeList().size() == 0){
//                treeView.setChildrenAllowed(treeNode.getId(), false);
//            }
//            
//            // Set parent
//            if(parent != null){
//                treeView.setParent(treeNode.getId(), parent);
//            }
//            
//            // Create children nodes
//            doCreateTree(treeNode.getTreeNodeList(), treeView, treeNode.getId(), folder, leaf);
//        }
//    }

    public void expandTree(Object itemId) {

        if (itemId == null) {
            return;
        }

        if (treeView.isExpanded(itemId)) {
            treeView.collapseItem(itemId);
            treeView.select(itemId);
        } else {
            treeView.expandItem(itemId);
            treeView.select(itemId);
        }
    }

    protected void initializeLayout() {
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


    private Component buildToolbar() {
        this.toolbar = new HorizontalLayout();
        this.toolbar.setSpacing(true);
        this.toolbar.setMargin(true);

        openStudyManagerBtn = new Button("<span class='glyphicon glyphicon-open' style='right: 4px'></span>Launch");
        openStudyManagerBtn.setHtmlContentAllowed(true);
        openStudyManagerBtn.setDescription("Open In Study Browser");
        openStudyManagerBtn.setEnabled(false);

        renameFolderBtn =new Button("<span class='glyphicon glyphicon-pencil' style='right: 2px'></span>");
        renameFolderBtn.setHtmlContentAllowed(true);
        renameFolderBtn.setDescription("Rename Folder");

        addFolderBtn = new Button("<span class='glyphicon glyphicon-plus' style='right: 2px'></span>");
        addFolderBtn.setHtmlContentAllowed(true);
        addFolderBtn.setDescription("Add New Folder");

        deleteFolderBtn = new Button("<span class='glyphicon glyphicon-trash' style='right: 2px'></span>");
        deleteFolderBtn.setHtmlContentAllowed(true);
        deleteFolderBtn.setDescription("Delete Selected Folder");

        openStudyManagerBtn.setStyleName(Bootstrap.Buttons.PRIMARY.styleName());
        renameFolderBtn.setStyleName(Bootstrap.Buttons.INFO.styleName());
        addFolderBtn.setStyleName(Bootstrap.Buttons.INFO.styleName());
        deleteFolderBtn.setStyleName(Bootstrap.Buttons.DANGER.styleName());

        openStudyManagerBtn.setWidth("100px");
        renameFolderBtn.setWidth("40px");
        addFolderBtn.setWidth("40px");
        deleteFolderBtn.setWidth("40px");

        this.toolbar.addComponent(openStudyManagerBtn);

        Label spacer = new Label("");
        this.toolbar.addComponent(spacer);
        this.toolbar.setExpandRatio(spacer, 1.0F);


        renameFolderBtn.setEnabled(false);
        addFolderBtn.setEnabled(false);
        deleteFolderBtn.setEnabled(false);

        this.toolbar.addComponent(addFolderBtn);
        this.toolbar.addComponent(renameFolderBtn);
        this.toolbar.addComponent(deleteFolderBtn);

        //this.toolbar.setSizeFull();
        this.toolbar.setWidth("100%");

        initializeToolbarActions();

        return this.toolbar;
    }

    private void initializeToolbarActions() {
        openStudyManagerBtn.addListener(new Button.ClickListener() {

            @Override
            public void buttonClick(Button.ClickEvent event) {
                if (treeView.getValue() == null || treeView.getValue() instanceof String) {
                    MessageNotifier.showError(event.getComponent().getWindow(), "Please select an item in the list", "");
                    return;
                }
                /*
                if (presenter.isFolder((Integer)lastItemId)) {
                    MessageNotifier.showError(event.getComponent().getWindow(),"Selected Item is a folder","");
                    return;
                }*/

                presenter.updateProjectLastOpenedDate();

                // page change to list manager, with parameter passed
                Project project = IBPWorkbenchApplication.get().getSessionData().getSelectedProject();
                Object value = treeView.getValue();

                new LaunchWorkbenchToolAction(LaunchWorkbenchToolAction.ToolEnum.STUDY_BROWSER_WITH_ID, project, ((Integer) value).intValue()).buttonClick(event);
            }
        });

        renameFolderBtn.addListener(new Button.ClickListener() {

            @Override
            public void buttonClick(Button.ClickEvent event) {
                if (treeView.getValue() == null) {
                    MessageNotifier.showError(event.getComponent().getWindow(), "Please select a folder to be renamed", "");
                    return;
                }

                if (treeView.getValue() instanceof String) {
                    MessageNotifier.showError(event.getComponent().getWindow(), (String) treeView.getValue() + " cannot be renamed", "");
                    return;
                }

                if (!presenter.isFolder((Integer) treeView.getValue())) {
                    MessageNotifier.showError(event.getComponent().getWindow(), "Please select a folder to be renamed", "");
                    return;
                }

                final Window w = new Window("Rename a folder");
                w.setWidth("300px");
                w.setHeight("150px");
                w.setModal(true);
                w.setResizable(false);
                w.setStyleName(Reindeer.WINDOW_LIGHT);

                VerticalLayout container = new VerticalLayout();
                container.setSpacing(true);
                container.setMargin(true);

                HorizontalLayout formContainer = new HorizontalLayout();
                formContainer.setSpacing(true);

                Label l = new Label("Folder Name");
                final TextField name = new TextField();
                name.setValue(treeView.getItemCaption(treeView.getValue()));

                formContainer.addComponent(l);
                formContainer.addComponent(name);

                HorizontalLayout btnContainer = new HorizontalLayout();
                btnContainer.setSpacing(true);
                btnContainer.setWidth("100%");

                Label spacer = new Label("");
                btnContainer.addComponent(spacer);
                btnContainer.setExpandRatio(spacer, 1.0F);

                Button ok = new Button("Ok");
                ok.setStyleName(Bootstrap.Buttons.PRIMARY.styleName());
                ok.addListener(new Button.ClickListener() {
                    @Override
                    public void buttonClick(Button.ClickEvent event) {
                        try {
                            presenter.renameNurseryListFolder(name.getValue().toString(), (Integer) treeView.getValue());
                        } catch (Error e) {
                            MessageNotifier.showError(event.getComponent().getWindow(), e.getMessage(), "");
                            return;
                        }

                        // update UI
                        treeView.setItemCaption(treeView.getValue(), name.getValue().toString());

                        // close popup
                        IBPWorkbenchApplication.get().getMainWindow().removeWindow(event.getComponent().getWindow());
                    }
                });

                Button cancel = new Button("Cancel");
                cancel.addListener(new Button.ClickListener() {
                    @Override
                    public void buttonClick(Button.ClickEvent event) {
                        IBPWorkbenchApplication.get().getMainWindow().removeWindow(w);
                    }
                });

                btnContainer.addComponent(ok);
                btnContainer.addComponent(cancel);

                container.addComponent(formContainer);
                container.addComponent(btnContainer);

                w.setContent(container);

                // show window
                IBPWorkbenchApplication.get().getMainWindow().addWindow(w);

            }
        });

        addFolderBtn.addListener(new Button.ClickListener() {

            @Override
            public void buttonClick(Button.ClickEvent event) {
                final Window w = new Window("Add new folder");
                w.setWidth("300px");
                w.setHeight("150px");
                w.setModal(true);
                w.setResizable(false);
                w.setStyleName(Reindeer.WINDOW_LIGHT);

                VerticalLayout container = new VerticalLayout();
                container.setSpacing(true);
                container.setMargin(true);

                HorizontalLayout formContainer = new HorizontalLayout();
                formContainer.setSpacing(true);

                Label l = new Label("Folder Name");
                final TextField name = new TextField();


                if (treeView.getValue() != null)
                    name.setValue(treeView.getItemCaption(treeView.getValue()));

                formContainer.addComponent(l);
                formContainer.addComponent(name);

                HorizontalLayout btnContainer = new HorizontalLayout();
                btnContainer.setSpacing(true);
                btnContainer.setWidth("100%");

                Label spacer = new Label("");
                btnContainer.addComponent(spacer);
                btnContainer.setExpandRatio(spacer, 1.0F);

                Button ok = new Button("Ok");
                ok.setStyleName(Bootstrap.Buttons.PRIMARY.styleName());
                ok.addListener(new Button.ClickListener() {
                    @Override
                    public void buttonClick(Button.ClickEvent event) {
                        Integer newItem = null;
                        try {
                            if (treeView.getValue() instanceof String)//top folder
                                newItem = presenter.addNurseryListFolder(name.getValue().toString(), ROOT_FOLDER);
                            else
                                newItem = presenter.addNurseryListFolder(name.getValue().toString(), (Integer) treeView.getValue());
                        } catch (Error e) {
                            MessageNotifier.showError(event.getComponent().getWindow(), e.getMessage(), "");
                            return;
                        }

                        //update UI
                        if (newItem != null) {
                            treeView.addItem(newItem);
                            treeView.setItemCaption(newItem, name.getValue().toString());
                            treeView.setChildrenAllowed(newItem, true);
                            treeView.setItemIcon(newItem, folderResource);

                            if (presenter.getStudyNodeParent(newItem) != null) {
                                treeView.setParent(newItem, treeView.getValue());
                            } else {
                                treeView.setParent(newItem, MY_STUDIES);
                            }

                            if (treeView.getValue() != null) {
                                if (!treeView.isExpanded(treeView.getValue()))
                                    expandTree(treeView.getValue());
                            } else
                                treeView.expandItem(MY_STUDIES);

                            treeView.select(newItem);
                        }

                        // close popup
                        IBPWorkbenchApplication.get().getMainWindow().removeWindow(event.getComponent().getWindow());
                    }
                });

                Button cancel = new Button("Cancel");
                cancel.addListener(new Button.ClickListener() {
                    @Override
                    public void buttonClick(Button.ClickEvent event) {
                        IBPWorkbenchApplication.get().getMainWindow().removeWindow(w);
                    }
                });

                btnContainer.addComponent(ok);
                btnContainer.addComponent(cancel);

                container.addComponent(formContainer);
                container.addComponent(btnContainer);

                w.setContent(container);

                // show window
                IBPWorkbenchApplication.get().getMainWindow().addWindow(w);
            }
        });

        deleteFolderBtn.addListener(new Button.ClickListener() {

            @Override
            public void buttonClick(final Button.ClickEvent event) {

                LOG.info(treeView.getValue() != null ? treeView.getValue().toString() : null);

                if (treeView.getValue() instanceof String) {
                    MessageNotifier.showError(event.getComponent().getWindow(), treeView.getValue().toString() + " cannot be deleted.", "");
                    return;
                }

                Integer id;

                try {
                    id = presenter.validateForDeleteNurseryList((Integer) treeView.getValue());
                } catch (Error e) {
                    MessageNotifier.showError(event.getComponent().getWindow(), e.getMessage(), "");
                    return;
                }

                final Integer finalId = id;
                ConfirmDialog.show(event.getComponent().getWindow(),
                        "Delete " + treeView.getItemCaption(treeView.getValue()),
                        "Are you sure you want to delete " + treeView.getItemCaption(treeView.getValue()),
                        "Yes", "No", new ConfirmDialog.Listener() {
                    @Override
                    public void onClose(ConfirmDialog dialog) {
                        if (dialog.isConfirmed()) {
                            try {
                                DmsProject parent = (DmsProject) presenter.getStudyNodeParent(finalId);
                                presenter.deleteNurseryListFolder(finalId);
                                treeView.removeItem(treeView.getValue());
                                if (parent.getProjectId().intValue() == ROOT_FOLDER) {
                                    treeView.select(MY_STUDIES);
                                } else {
                                    treeView.select(parent.getProjectId());
                                }
                            } catch (Error e) {
                                MessageNotifier.showError(event.getComponent().getWindow(), e.getMessage(), "");
                            }
                        }
                    }
                });
            }
        });
    }

    private static class NurseryTreeDropHandler implements DropHandler {
        private final Tree tree;
        private final NurseryListPreviewPresenter presenter;

        public NurseryTreeDropHandler(Tree tree, NurseryListPreviewPresenter presenter) {
            this.tree = tree;
            this.presenter = presenter;
        }


        @Override
        public void drop(DragAndDropEvent dropEvent) {
            // Called whenever a drop occurs on the component

            // Make sure the drag source is the same tree
            Transferable t = dropEvent.getTransferable();

            // see the comment in getAcceptCriterion()
            if (t.getSourceComponent() != tree
                    || !(t instanceof DataBoundTransferable)) {
                return;
            }

            Tree.TreeTargetDetails dropData = ((Tree.TreeTargetDetails) dropEvent
                    .getTargetDetails());

            Object sourceItemId = ((DataBoundTransferable) t).getItemId();
            // FIXME: Why "over", should be "targetItemId" or just
            // "getItemId"
            Object targetItemId = dropData.getItemIdOver();

            // Location describes on which part of the node the drop took
            // place
            VerticalDropLocation location = dropData.getDropLocation();

            moveNode(sourceItemId, targetItemId, location);

        }

        @Override
        public AcceptCriterion getAcceptCriterion() {
            return AcceptAll.get();
        }

        /**
         * Move a node within a tree onto, above or below another node depending
         * on the drop location.
         *
         * @param sourceItemId id of the item to move
         * @param targetItemId id of the item onto which the source node should be moved
         * @param location     VerticalDropLocation indicating where the source node was
         *                     dropped relative to the target node
         */
        private void moveNode(Object sourceItemId, Object targetItemId,
                              VerticalDropLocation location) {
            HierarchicalContainer container = (HierarchicalContainer) tree
                    .getContainerDataSource();

            if ((targetItemId instanceof String && ((String) targetItemId).equals(SHARED_STUDIES)) || (targetItemId instanceof Integer && ((Integer) targetItemId) > 0)) {
                MessageNotifier.showError(IBPWorkbenchApplication.get().getMainWindow(), "Error occurred", "Cannot move folder to Public Studies");
                return;
            }

            if (container.hasChildren(sourceItemId)) {
                MessageNotifier.showError(IBPWorkbenchApplication.get().getMainWindow(), "Error occurred", "Cannot move folder with child elements");
                return;
            }

            // Sorting goes as
            // - If dropped ON a node, we append it as a child
            // - If dropped on the TOP part of a node, we move/add it before
            // the node
            // - If dropped on the BOTTOM part of a node, we move/add it
            // after the node

            boolean success = true;
            try {
                int actualTargetId = 0;
                // switch to using the root folder id if target is the root of the local folder
                if (targetItemId instanceof String && ((String) targetItemId).equals(MY_STUDIES)) {
                    actualTargetId = ROOT_FOLDER;
                } else {
                    actualTargetId = (Integer)targetItemId;
                }

                success = presenter.moveNurseryListFolder((Integer) sourceItemId, actualTargetId);
            } catch (Error error) {
                MessageNotifier.showError(IBPWorkbenchApplication.get().getMainWindow(), error.getMessage(), "");
                success = false;
            }

            // only perform UI change if backend modification was successful
            if (success) {

                if (location == VerticalDropLocation.MIDDLE) {
                    if (container.setParent(sourceItemId, targetItemId)
                            && container.hasChildren(targetItemId)) {
                        // move first in the container
                        container.moveAfterSibling(sourceItemId, null);
                    }
                } else if (location == VerticalDropLocation.TOP) {
                    Object parentId = container.getParent(targetItemId);
                    if (container.setParent(sourceItemId, parentId)) {
                        // reorder only the two items, moving source above target
                        container.moveAfterSibling(sourceItemId, targetItemId);
                        container.moveAfterSibling(targetItemId, sourceItemId);
                    }
                } else if (location == VerticalDropLocation.BOTTOM) {
                    Object parentId = container.getParent(targetItemId);
                    if (container.setParent(sourceItemId, parentId)) {
                        container.moveAfterSibling(sourceItemId, targetItemId);
                    }
                }
            }
        }

    }

    public void addChildrenNode(int parentId, List<Reference> studyChildren) {
        for (Reference sc : studyChildren) {
            treeView.addItem(sc.getId());
            treeView.setItemCaption(sc.getId(), sc.getName());
            treeView.setParent(sc.getId(), parentId);
            // check if the study has sub study
            if (presenter.isFolder(sc.getId())) {
                treeView.setChildrenAllowed(sc.getId(), true);
                treeView.setItemIcon(sc.getId(), folderResource);
            } else {
                treeView.setChildrenAllowed(sc.getId(), false);
                treeView.setItemIcon(sc.getId(), leafResource);
            }
            treeView.setSelectable(true);
        }
        treeView.select(parentId);
        treeView.setImmediate(true);
    }


    public void setToolbarButtonsEnabled(boolean enabled) {
        addFolderBtn.setEnabled(enabled);
        renameFolderBtn.setEnabled(enabled);
        deleteFolderBtn.setEnabled(enabled);
    }

    public void setToolbarAddButtonEnabled(boolean enabled) {
        addFolderBtn.setEnabled(enabled);
    }

    public void setToolbarLaunchButtonEnabled(boolean enabled) {
        openStudyManagerBtn.setEnabled(enabled);
    }

}
