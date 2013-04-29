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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.generationcp.commons.exceptions.InternationalizableException;
import org.generationcp.commons.vaadin.spring.SimpleResourceBundleMessageSource;
import org.generationcp.commons.vaadin.util.MessageNotifier;
import org.generationcp.ibpworkbench.IBPWorkbenchApplication;
import org.generationcp.ibpworkbench.Message;
import org.generationcp.ibpworkbench.actions.HomeAction;
import org.generationcp.ibpworkbench.database.IBDBGenerator;
import org.generationcp.ibpworkbench.database.MysqlAccountGenerator;
import org.generationcp.ibpworkbench.util.ToolUtil;
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
import org.generationcp.middleware.pojos.workbench.ProjectUserInfo;
import org.generationcp.middleware.pojos.workbench.ProjectUserRole;
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
    private static final int PROJECT_USER_ACCESS_NUMBER = 100;
    private static final int PROJECT_USER_TYPE = 422;
    private static final int PROJECT_USER_STATUS = 1;
    
    private int projectUserInstalId = -1; // instalid of installation inserted, default value is -1 
    
    private CreateProjectPanel createProjectPanel;
    private Project project;
    
    private Map<Integer, String> idAndNameOfProjectMembers;

    @Autowired
    private ManagerFactoryProvider managerFactoryProvider;

    @Autowired
    private WorkbenchDataManager workbenchDataManager;
    
    @Autowired
    private ToolUtil toolUtil;

    @Autowired
    private SimpleResourceBundleMessageSource messageSource;
    private Project projectSaved;
    
    public SaveNewProjectAction(CreateProjectPanel createProjectPanel) {
        this.createProjectPanel = createProjectPanel;
    }

    @Override
    public void buttonClick(ClickEvent event) {

        boolean validProjectValues = createProjectPanel.validate();

        if (validProjectValues) {
            
            this.idAndNameOfProjectMembers = new HashMap<Integer, String>();

            project = createProjectPanel.getProject();

            boolean isGenerationSuccess = false;
            boolean isMysqlAccountGenerationSuccess = false;

            IBPWorkbenchApplication app = IBPWorkbenchApplication.get();

            project.setUserId(app.getSessionData().getUserData().getUserid());

            try {
                //TODO: REMOVE Once template is no longer required in Project
                project.setTemplate(workbenchDataManager.getWorkflowTemplates().get(0));

                project.setLastOpenDate(null);
                projectSaved = workbenchDataManager.addProject(project);

                // set the project's local database name
                String cropName = project.getCropType().getCropName().toLowerCase();
                String localDatabaseName = String.format("%s_%d_local", cropName, project.getProjectId());
                String centralDatabaseName = String.format("ibdb_%s_central", cropName);
                project.setLocalDbName(localDatabaseName);
                project.setCentralDbName(centralDatabaseName);
                
                workbenchDataManager.saveOrUpdateProject(project);
                
                // create the project's workspace directories
                toolUtil.createWorkspaceDirectoriesForProject(projectSaved);
            } catch (MiddlewareQueryException e) {
                LOG.error(e.getMessage());
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
                //TODO cleanup of records already saved for the project needed
                return;
            }

            if (isGenerationSuccess) {
                //generator.addCachedLocations(app.getSessionData().getProjectLocationData());
                //generator.addCachedBreedingMethods(app.getSessionData().getProjectBreedingMethodData());

                User currentUser = app.getSessionData().getUserData();
                User user = currentUser.copy();

                try {
                    ManagerFactory managerFactory = managerFactoryProvider.getManagerFactoryForProject(project);
                    
                    // create the project's local person and user data
                    Person currentPerson = workbenchDataManager.getPersonById(currentUser.getUserid());
                    Person person = currentPerson.copy();

                    // add the person to the project's local database
                    managerFactory.getUserDataManager().addPerson(person);

                    // add a user to project's local database
                    String newUserName = person.getInitialsWithTimestamp();
                    //password should be 11 chars long only
                    String newPassword = newUserName.substring(0, 11);
                    
                    user.setName(newUserName);
                    user.setPassword(newPassword);
                    user.setPersonid(person.getId());
                    user.setAccess(PROJECT_USER_ACCESS_NUMBER);
                    user.setType(PROJECT_USER_TYPE);
                    user.setStatus(Integer.valueOf(PROJECT_USER_STATUS));
                    user.setAdate(getCurrentDate());
                    int localUserId = managerFactory.getUserDataManager().addUser(user);
                    //add to map of project members
                    this.idAndNameOfProjectMembers.put(currentUser.getUserid(), newUserName);
                    
                    // Add the installation record in the local db with the given project name and the newly added local user
                    projectUserInstalId = generator.addLocalInstallationRecord(project.getProjectName(), localUserId);

                    // Set the instalId of the local user
                    user.setInstalid(Integer.valueOf(projectUserInstalId));
                    managerFactory.getUserDataManager().updateUser(user);

                    List<ProjectUserRole> projectUserRoles = createProjectPanel.getProjectUserRoles();
                    if ((projectUserRoles != null) && (!projectUserRoles.isEmpty())) {
                        saveProjectUserRoles(projectUserRoles, projectSaved);
                    }

                    List<ProjectUserRole> projectMembers = createProjectPanel.getProjectMembers(); 
                    if ((projectMembers != null) && (!projectMembers.isEmpty())) {
                        saveProjectMembers(managerFactory, projectMembers, projectSaved);
                    }

                } catch (MiddlewareQueryException e) {
                    LOG.error(e.getMessage(), e);
                    MessageNotifier.showError(event.getComponent().getWindow(), messageSource.getMessage(Message.DATABASE_ERROR), "<br />"
                            + messageSource.getMessage(Message.SAVE_PROJECT_ERROR_DESC));
                    //TODO cleanup of records already saved for the project needed
                    return;
                }

                // create mysql user accounts for members of the project
                if(isGenerationSuccess){
                    Set<User> projectMembers = new HashSet<User>();
                    projectMembers.add(currentUser);
                    
                    List<ProjectUserRole> projectUserRoles = createProjectPanel.getProjectMembers();
                    for(ProjectUserRole projectUserRole : projectUserRoles){
                        try{
                            User member = this.workbenchDataManager.getUserById(projectUserRole.getUserId());
                            projectMembers.add(member);
                        } catch(MiddlewareQueryException ex) {
                            //do nothing because getting the User will not fail
                        }
                    }
                    
                    MysqlAccountGenerator mysqlAccountGenerator = new MysqlAccountGenerator(this.project.getCropType(), this.project.getProjectId(), 
                            this.idAndNameOfProjectMembers, this.workbenchDataManager);
                    
                    try {
                        isMysqlAccountGenerationSuccess = mysqlAccountGenerator.generateMysqlAccounts();
                    } catch (InternationalizableException e) {
                        LOG.error(e.toString(), e);
                        MessageNotifier.showError(event.getComponent().getWindow(), e.getCaption(), e.getDescription());
                        //TODO cleanup of records already saved for the project needed
                        return;
                    }
                }
                

                //Create records for workbench_project_user_info table
                    for (Map.Entry<Integer, String> e : idAndNameOfProjectMembers.entrySet()){
                 	   
                 	
                 		try {
							if (workbenchDataManager.getProjectUserInfoDao().getByProjectIdAndUserId(project.getProjectId().intValue(),  e.getKey())==null){
								ProjectUserInfo pUserInfo = new ProjectUserInfo(project.getProjectId().intValue(),  e.getKey());
								workbenchDataManager.saveOrUpdateProjectUserInfo(pUserInfo);
							}
						} catch (MiddlewareQueryException e1) {
							// do nothing
						}
        
                }
          
             
                
                // add a workbench user to ibdb user mapping
                IbdbUserMap ibdbUserMap = new IbdbUserMap();
                ibdbUserMap.setWorkbenchUserId(currentUser.getUserid());
                ibdbUserMap.setProjectId(project.getProjectId());
                ibdbUserMap.setIbdbUserId(user.getUserid());
                try {
                    workbenchDataManager.addIbdbUserMap(ibdbUserMap);

                    // FIXME: What happens when the user deletes all associated methods and locations?
                    // Ideally, the methods and locations will be saved automatically when we save a project.
                    // However, we need to fix the Project POJOs mapping in order to do that

                    // FOR FOURTH TAB: SELECT BREEDING METHODS
//                    Set<Method> methods = project.getMethods();
//                    if ((methods != null) && (!methods.isEmpty())) {
//                        saveProjectMethods(methods, projectSaved);
//                    }

                    // FOR FIFTH TAB: SELECT LOCATION
//                    //add a project location to workbench
//                    Set<Location> locations = project.getLocations();
//                    if ((locations != null) && (!locations.isEmpty())) {
//                        saveProjectLocation(locations, projectSaved);
//                    }

                } catch (MiddlewareQueryException e) {
                    LOG.error(e.getMessage(), e);
                    MessageNotifier.showError(event.getComponent().getWindow(), messageSource.getMessage(Message.DATABASE_ERROR), "<br />"
                            + messageSource.getMessage(Message.SAVE_PROJECT_ERROR_DESC));
                    //TODO cleanup of records already saved for the project needed
                    return;
                }
            }

//            app.getSessionData().getProjectLocationData().clear();
//            app.getSessionData().getUniqueLocations().clear();

            LOG.info(project.getProjectId() + "  " + project.getProjectName() + " " + project.getStartDate() + " "
                    + project.getTemplate().getTemplateId());
            LOG.info("IBDB Local Generation Successful?: " + isGenerationSuccess);
            LOG.info("Mysql Accounts Generation Successful?: " + isMysqlAccountGenerationSuccess);
            
            // go back to dashboard
            HomeAction home = new HomeAction();
            home.buttonClick(event);
        }

    }

    @SuppressWarnings("unused")
    private void saveProjectMethods(ManagerFactory managerFactory, Set<Method> methods, Project projectSaved) throws MiddlewareQueryException {

        List<ProjectMethod> projectMethodList = new ArrayList<ProjectMethod>();
        int mID = 0;
        for (Method m : methods) {
            ProjectMethod projectMethod = new ProjectMethod();
            if(m.getMid() < 1){
                //save the added  method to the local database created
                mID = managerFactory.getGermplasmDataManager().addMethod(new Method(m.getMid(), m.getMtype(), m.getMgrp(),
                        m.getMcode(), m.getMname(), m.getMdesc(),0, 0, 0,0, 0,0, 0, 0));
            }else{
                mID=m.getMid();
            }
            projectMethod.setMethodId(mID);
            projectMethod.setProject(projectSaved);
            projectMethodList.add(projectMethod);
        }
        workbenchDataManager.addProjectMethod(projectMethodList);

    }

    @SuppressWarnings("unused")
    private void saveProjectLocation(ManagerFactory managerFactory, Set<Location> locations, Project projectSaved) throws MiddlewareQueryException {

        List<ProjectLocationMap> projectLocationMapList = new ArrayList<ProjectLocationMap>();
        long locID=0;
        for (Location l : locations) {
            ProjectLocationMap projectLocationMap = new ProjectLocationMap();
            if(l.getLocid() < 1){
                //save the added new location to the local database created
                Location location = new Location();
                location.setLocid(l.getLocid());
                location.setCntryid(0);
                location.setLabbr(l.getLabbr());
                location.setLname(l.getLname());
                location.setLrplce(0);
                location.setLtype(0);
                location.setNllp(0);
                location.setSnl1id(0);
                location.setSnl2id(0);
                location.setSnl3id(0);
                
                locID= managerFactory.getGermplasmDataManager().addLocation(location);
            }else{
                locID=l.getLocid();
            }
            projectLocationMap.setLocationId(locID);
            projectLocationMap.setProject(projectSaved);
            projectLocationMapList.add(projectLocationMap);
        }

        workbenchDataManager.addProjectLocationMap(projectLocationMapList);

    }

    private void saveProjectUserRoles(List<ProjectUserRole> projectUserRoles, Project projectSaved) throws MiddlewareQueryException {

        IBPWorkbenchApplication app = IBPWorkbenchApplication.get();
        Integer userId = app.getSessionData().getUserData().getUserid();

        for (ProjectUserRole projectUserRole : projectUserRoles){
            projectUserRole.setProject(projectSaved);
            projectUserRole.setUserId(userId);
            workbenchDataManager.addProjectUserRole(projectUserRole);
        }

    }

    private void saveProjectMembers(ManagerFactory managerFactory, List<ProjectUserRole> projectUserRoles, Project projectSaved) throws MiddlewareQueryException {
        
        UserDataManager userDataManager = managerFactory.getUserDataManager();
        Map<Integer,String> usersAccountedFor = new HashMap<Integer, String>();
        
        for (ProjectUserRole projectUserRole : projectUserRoles){
            
            // Save role
            projectUserRole.setProject(projectSaved);
            workbenchDataManager.addProjectUserRole(projectUserRole);

            // Save User to local db
            //check if this user has already been accounted for, because each user may have many roles so this check is needed
            if(!usersAccountedFor.containsKey(projectUserRole.getUserId())){
                User workbenchUser = workbenchDataManager.getUserById(projectUserRole.getUserId());
                User localUser =  workbenchUser.copy();
                
                Person currentPerson = workbenchDataManager.getPersonById(workbenchUser.getPersonid());
                Person localPerson = currentPerson.copy();
                
                // Check if the Person record already exists
                if (!userDataManager.isPersonExists(localPerson.getFirstName().toUpperCase(), localPerson.getLastName().toUpperCase())){
                    userDataManager.addPerson(localPerson);
                } else {
                    // set localPerson to the existing person
                    List<Person> persons = userDataManager.getAllPersons();
                    for (Person person : persons){
                        if (person.getLastName().toUpperCase().equals(localPerson.getLastName().toUpperCase()) && 
                                person.getFirstName().toUpperCase().equals(localPerson.getFirstName().toUpperCase())){
                            localPerson = person;
                            break;
                        }
                    }
                }
                
                //append a timestamp to the username and password
                //and change the start of the username of be the initials of the user
                String newUserName = localPerson.getInitialsWithTimestamp();
                //password must be 11 chars long
                String newPassword = newUserName.substring(0, 11);
                
                localUser.setName(newUserName);
                localUser.setPassword(newPassword);
                
                // If the selected member does not exist yet in the local database, then add
                if (!userDataManager.isUsernameExists(localUser.getName())){
                    localUser.setPersonid(localPerson.getId());
                    localUser.setAccess(PROJECT_USER_ACCESS_NUMBER);
                    localUser.setType(PROJECT_USER_TYPE);
                    localUser.setInstalid(Integer.valueOf(projectUserInstalId));
                    localUser.setStatus(Integer.valueOf(PROJECT_USER_STATUS));
                    localUser.setAdate(getCurrentDate());
                    Integer userId = userDataManager.addUser(localUser);      
                    this.idAndNameOfProjectMembers.put(workbenchUser.getUserid(), newUserName);

                    // add a workbench user to ibdb user mapping
                    User ibdbUser = userDataManager.getUserById(userId);
                    IbdbUserMap ibdbUserMap = new IbdbUserMap();
                    ibdbUserMap.setWorkbenchUserId(workbenchUser.getUserid());
                    ibdbUserMap.setProjectId(project.getProjectId());
                    ibdbUserMap.setIbdbUserId(ibdbUser.getUserid());
                    workbenchDataManager.addIbdbUserMap(ibdbUserMap);

                }
                usersAccountedFor.put(projectUserRole.getUserId(), newUserName);
            }
        }
    }
    
    private Integer getCurrentDate(){
        Calendar now = Calendar.getInstance();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
        String dateNowStr = formatter.format(now.getTime());
        Integer dateNowInt = Integer.valueOf(dateNowStr);
        return dateNowInt;

    }
}
