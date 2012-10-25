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

package org.generationcp.ibpworkbench.comp.project.create;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.generationcp.commons.exceptions.InternationalizableException;
import org.generationcp.commons.vaadin.spring.SimpleResourceBundleMessageSource;
import org.generationcp.commons.vaadin.util.MessageNotifier;
import org.generationcp.ibpworkbench.IBPWorkbenchApplication;
import org.generationcp.ibpworkbench.Message;
import org.generationcp.ibpworkbench.actions.HomeAction;
import org.generationcp.ibpworkbench.database.IBDBGenerator;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.manager.ManagerFactory;
import org.generationcp.middleware.manager.api.ManagerFactoryProvider;
import org.generationcp.middleware.manager.api.UserDataManager;
import org.generationcp.middleware.manager.api.WorkbenchDataManager;
import org.generationcp.middleware.pojos.Location;
import org.generationcp.middleware.pojos.Method;
import org.generationcp.middleware.pojos.Person;
import org.generationcp.middleware.pojos.User;
import org.generationcp.middleware.pojos.workbench.IbdbUserMap;
import org.generationcp.middleware.pojos.workbench.Project;
import org.generationcp.middleware.pojos.workbench.ProjectLocationMap;
import org.generationcp.middleware.pojos.workbench.ProjectMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;

@Configurable
public class SaveNewProjectAction implements ClickListener{

    private static final Logger LOG = LoggerFactory.getLogger(SaveNewProjectAction.class);
    private static final long serialVersionUID = 1L;

    private CreateProjectPanel createProjectPanel;
    private Project project;

    @Autowired
    private ManagerFactoryProvider managerFactoryProvider;

    @Autowired
    private WorkbenchDataManager workbenchDataManager;

    @Autowired
    private SimpleResourceBundleMessageSource messageSource;

    public SaveNewProjectAction(CreateProjectPanel createProjectPanel) {
        this.createProjectPanel = createProjectPanel;
    }

    @Override
    public void buttonClick(ClickEvent event) {

        boolean validProjectValues = createProjectPanel.validate();

        if (validProjectValues) {

            project = createProjectPanel.getProject();

            boolean isGenerationSuccess = false;

            IBPWorkbenchApplication app = IBPWorkbenchApplication.get();

            project.setUserId(app.getSessionData().getUserData().getUserid());

            //TODO: REMOVE Once template is no longer required in Project
            try {
                project.setTemplate(workbenchDataManager.getWorkflowTemplates().get(0));
            } catch (MiddlewareQueryException e) {
                LOG.error(e.getMessage());
            }

            //TODO: Verify the try-catch flow      
            try {
                project.setLastOpenDate(null);
                Project projectSaved = workbenchDataManager.saveOrUpdateProject(project);

                Set<Method> methods = project.getMethods();
                Set<Location> locations = project.getLocations();

                // FIXME: What happens when the user deletes all associated methods and locations?
                // Ideally, the methods and locations will be saved automatically when we save a project.
                // However, we need to fix the Project POJOs mapping in order to do that
                if ((methods != null) && (!methods.isEmpty())) {
                    saveProjectMethods(methods, projectSaved);
                }

                if ((locations != null) && (!locations.isEmpty())) {
                    saveProjectLocation(locations, projectSaved);
                }

            } catch (MiddlewareQueryException e) {
                LOG.error("Error encountered while trying to save the project.", e);
                MessageNotifier.showError(event.getComponent().getWindow(), messageSource.getMessage(Message.DATABASE_ERROR), "<br />"
                        + messageSource.getMessage(Message.SAVE_PROJECT_ERROR_DESC));
                return;
            }

            IBDBGenerator generator;

            try {
                generator = new IBDBGenerator(project.getCropType(), project.getProjectId());
                isGenerationSuccess = generator.generateDatabase();
            } catch (InternationalizableException e) {
                LOG.error(e.toString(), e);
                MessageNotifier.showError(event.getComponent().getWindow(), e.getCaption(), e.getDescription());
                return;
            }

            if (isGenerationSuccess) {
                generator.addCachedLocations(app.getSessionData().getProjectLocationData());

                User currentUser = app.getSessionData().getUserData();
                User user = currentUser.copy();

                try {
                    Person currentPerson = workbenchDataManager.getPersonById(currentUser.getPersonid());
                    Person person = currentPerson.copy();

                    // create the project's local person and user data
                    ManagerFactory managerFactory = managerFactoryProvider.getManagerFactoryForProject(project);
                    UserDataManager userDataManager = managerFactory.getUserDataManager();

                    // add the person to the project's local database
                    userDataManager.addPerson(person);

                    // add a user to project's local database
                    user.setPersonid(person.getId());
                    userDataManager.addUser(user);
                } catch (MiddlewareQueryException e) {
                    LOG.error(e.getMessage(), e);
                    MessageNotifier.showError(event.getComponent().getWindow(), messageSource.getMessage(Message.DATABASE_ERROR), "<br />"
                            + messageSource.getMessage(Message.SAVE_PROJECT_ERROR_DESC));
                    return;
                }

                // add a workbench user to ibdb user mapping
                IbdbUserMap ibdbUserMap = new IbdbUserMap();
                ibdbUserMap.setWorkbenchUserId(currentUser.getUserid());
                ibdbUserMap.setProjectId(project.getProjectId());
                ibdbUserMap.setIbdbUserId(user.getUserid());
                try {
                    workbenchDataManager.addIbdbUserMap(ibdbUserMap);
                } catch (MiddlewareQueryException e) {
                    LOG.error(e.getMessage(), e);
                    MessageNotifier.showError(event.getComponent().getWindow(), messageSource.getMessage(Message.DATABASE_ERROR), "<br />"
                            + messageSource.getMessage(Message.SAVE_PROJECT_ERROR_DESC));
                    return;
                }
            }

            app.getSessionData().getProjectLocationData().clear();

            app.getSessionData().getUniqueLocations().clear();

            LOG.info(project.getProjectId() + "  " + project.getProjectName() + " " + project.getStartDate() + " "
                    + project.getTemplate().getTemplateId());
            LOG.info("IBDB Local Generation Successful?: " + isGenerationSuccess);

            // go back to dashboard
            HomeAction home = new HomeAction();
            home.buttonClick(event);
        }

    }

    private void saveProjectMethods(Set<Method> methods, Project projectSaved) throws MiddlewareQueryException {

        ArrayList<Method> method = new ArrayList<Method>(methods);
        List<ProjectMethod> projectMethodList = new ArrayList<ProjectMethod>();

        for (Method m : method) {
            ProjectMethod projectMethod = new ProjectMethod();
            projectMethod.setMethodId(m.getMid());
            projectMethod.setProject(projectSaved);
            projectMethodList.add(projectMethod);
        }

        workbenchDataManager.addProjectMethod(projectMethodList);

    }

    private void saveProjectLocation(Set<Location> locations, Project projectSaved) throws MiddlewareQueryException {

        ArrayList<Location> loc = new ArrayList<Location>(locations);
        List<ProjectLocationMap> projectLocationMapList = new ArrayList<ProjectLocationMap>();

        for (Location l : loc) {
            ProjectLocationMap projectLocationMap = new ProjectLocationMap();
            projectLocationMap.setLocationId(new Long(l.getLocid()));
            projectLocationMap.setProject(projectSaved);
            projectLocationMapList.add(projectLocationMap);
        }

        workbenchDataManager.addProjectLocationMap(projectLocationMapList);

    }

}
