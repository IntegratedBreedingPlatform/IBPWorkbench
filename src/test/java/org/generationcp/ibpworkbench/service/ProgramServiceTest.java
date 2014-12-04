package org.generationcp.ibpworkbench.service;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import org.generationcp.commons.hibernate.ManagerFactoryProvider;
import org.generationcp.ibpworkbench.database.IBDBGeneratorCentralDb;
import org.generationcp.ibpworkbench.database.IBDBGeneratorLocalDb;
import org.generationcp.ibpworkbench.database.MysqlAccountGenerator;
import org.generationcp.ibpworkbench.util.ToolUtil;
import org.generationcp.middleware.dao.ProjectUserInfoDAO;
import org.generationcp.middleware.manager.ManagerFactory;
import org.generationcp.middleware.manager.api.UserDataManager;
import org.generationcp.middleware.manager.api.WorkbenchDataManager;
import org.generationcp.middleware.pojos.Person;
import org.generationcp.middleware.pojos.User;
import org.generationcp.middleware.pojos.workbench.CropType;
import org.generationcp.middleware.pojos.workbench.IbdbUserMap;
import org.generationcp.middleware.pojos.workbench.Project;
import org.generationcp.middleware.pojos.workbench.ProjectUserRole;
import org.generationcp.middleware.pojos.workbench.Role;
import org.generationcp.middleware.pojos.workbench.WorkflowTemplate;
import org.junit.Test;
import org.mockito.Mockito;


public class ProgramServiceTest {
	
	@Test
	public void testAddNewProgram() throws Exception {
		
		ProgramService programService = new ProgramService();
		
		Project project = new Project();
		project.setProjectId(1L);
		project.setProjectName("TestRiceProject");
		CropType cropType = new CropType(CropType.RICE);
		cropType.setCentralDbName("ibdbv2_rice_central");
		project.setCropType(cropType);
		
		User loggedInUser = new User();
		loggedInUser.setUserid(1);
		loggedInUser.setName("mrbreeder");
		
		User memberUser = new User();
		memberUser.setUserid(1);
		memberUser.setName("mrbreederfriend");
		
		Set<User> programMembers = new HashSet<User>();
		programMembers.add(memberUser);
		
		//WorkbenchDataManager mocks
		WorkbenchDataManager workbenchDataManager = mock(WorkbenchDataManager.class);
		when(workbenchDataManager.getCropTypeByName(Mockito.anyString())).thenReturn(cropType);
		ArrayList<WorkflowTemplate> workflowTemplates = new ArrayList<WorkflowTemplate>();
		workflowTemplates.add(new WorkflowTemplate());		
		when(workbenchDataManager.getWorkflowTemplates()).thenReturn(workflowTemplates);
		when(workbenchDataManager.getPersonById(Mockito.anyInt())).thenReturn(new Person());
		ProjectUserInfoDAO puiDao = mock(ProjectUserInfoDAO.class);
		when(workbenchDataManager.getProjectUserInfoDao()).thenReturn(puiDao);
		
		ArrayList<Role> allRolesList = new ArrayList<Role>();
		allRolesList.add(new Role(1, "CB Breeder", null));
		allRolesList.add(new Role(2, "MAS Breeder", null));
		allRolesList.add(new Role(3, "MABC Breeder", null));
		allRolesList.add(new Role(4, "MARS Breeder", null));
		allRolesList.add(new Role(5, "Manager", null));
		when(workbenchDataManager.getAllRoles()).thenReturn(allRolesList);
		
		ToolUtil toolUtil = mock(ToolUtil.class);
		
		IBDBGeneratorCentralDb centralDBGenerator = mock(IBDBGeneratorCentralDb.class);
		when(centralDBGenerator.generateDatabase()).thenReturn(true);
		
		IBDBGeneratorLocalDb localDBGenerator = mock(IBDBGeneratorLocalDb.class);
		when(localDBGenerator.generateDatabase()).thenReturn(true);
		
		MysqlAccountGenerator mySQLAccountGenerator = Mockito.mock(MysqlAccountGenerator.class);
		
		ManagerFactoryProvider managerFactoryProvider = mock(ManagerFactoryProvider.class);
		ManagerFactory managerFactory = mock(ManagerFactory.class);
		when(managerFactoryProvider.getManagerFactoryForProject(project)).thenReturn(managerFactory);
		
		UserDataManager userDataManager = mock(UserDataManager.class);
		when(managerFactory.getUserDataManager()).thenReturn(userDataManager);
		
		programService.setWorkbenchDataManager(workbenchDataManager);
		programService.setToolUtil(toolUtil);
		programService.setCentralDbGenerator(centralDBGenerator);
		programService.setLocalDbGenerator(localDBGenerator);
		programService.setMySQLAccountGenerator(mySQLAccountGenerator);
		programService.setManagerFactoryProvider(managerFactoryProvider);
		programService.setCurrentUser(loggedInUser);
		programService.setSelectedUsers(programMembers);
		
		programService.createNewProgram(project);
		
		//Verify that the key database operations for program creation are invoked.
		
		verify(workbenchDataManager).addProject(project);
		verify(workbenchDataManager).saveOrUpdateProject(project);
		verify(workbenchDataManager).getWorkflowTemplates();
		
		verify(toolUtil).createWorkspaceDirectoriesForProject(project);
		verify(centralDBGenerator).generateDatabase();
		verify(localDBGenerator).generateDatabase();
		
		verify(userDataManager).addPerson(Mockito.any(Person.class));
		verify(userDataManager).addUser(Mockito.any(User.class));
		
		verify(workbenchDataManager, Mockito.times(allRolesList.size())).addProjectUserRole(Mockito.any(ProjectUserRole.class));
		verify(mySQLAccountGenerator).generateMysqlAccounts();
		verify(workbenchDataManager).addIbdbUserMap(Mockito.any(IbdbUserMap.class));
	}

}