/*******************************************************************************
 * Copyright (c) 2012, All Rights Reserved.
 *
 * Generation Challenge Programme (GCP)
 *
 *
 * This software is licensed for use under the terms of the GNU General Public License (http://bit.ly/8Ztv8M) and the provisions of Part F
 * of the Generation Challenge Programme Amended Consortium Agreement (http://bit.ly/KQX1nL)
 *
 *******************************************************************************/

package org.generationcp.ibpworkbench.ui.programmembers;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.generationcp.commons.hibernate.ManagerFactoryProvider;
import org.generationcp.commons.util.DateUtil;
import org.generationcp.commons.vaadin.spring.SimpleResourceBundleMessageSource;
import org.generationcp.commons.vaadin.util.MessageNotifier;
import org.generationcp.ibpworkbench.Message;
import org.generationcp.ibpworkbench.database.MysqlAccountGenerator;
import org.generationcp.ibpworkbench.ui.common.TwinTableSelect;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.manager.ManagerFactory;
import org.generationcp.middleware.manager.api.UserDataManager;
import org.generationcp.middleware.manager.api.WorkbenchDataManager;
import org.generationcp.middleware.pojos.Person;
import org.generationcp.middleware.pojos.User;
import org.generationcp.middleware.pojos.workbench.IbdbUserMap;
import org.generationcp.middleware.pojos.workbench.Project;
import org.generationcp.middleware.pojos.workbench.ProjectUserInfo;
import org.generationcp.middleware.pojos.workbench.ProjectUserMysqlAccount;
import org.generationcp.middleware.pojos.workbench.ProjectUserRole;
import org.generationcp.middleware.pojos.workbench.Role;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;

@Configurable
public class SaveUsersInProjectAction implements ClickListener {

	private static final long serialVersionUID = 1L;
	private static final Logger LOG = LoggerFactory.getLogger(SaveUsersInProjectAction.class);
	private static final int PROJECT_USER_ACCESS_NUMBER = 100;
	private static final int PROJECT_USER_TYPE = 422;
	private static final int PROJECT_USER_STATUS = 1;

	private final TwinTableSelect<User> select;

	// instalid of installation inserted, default value is -1
	private final int projectUserInstalId = -1;

	private final Project project;

	@Autowired
	private ManagerFactoryProvider managerFactoryProvider;

	@Autowired
	private WorkbenchDataManager workbenchDataManager;

	@Autowired
	private SimpleResourceBundleMessageSource messageSource;
	
	@Autowired
	private UserDataManager userDataManager;
	
	@Autowired
	private PlatformTransactionManager transactionManager;

	public SaveUsersInProjectAction(Project project, TwinTableSelect<User> select) {
		this.project = project;
		this.select = select;
	}

	public boolean validate() {
		return true;
	}

	@Override
	public void buttonClick(final ClickEvent event) {
		if (this.project == null) {
			return;
		}

		final Collection<User> userList = this.select.getValue();
		try {

			final TransactionTemplate transactionTemplate = new TransactionTemplate(transactionManager);
			transactionTemplate.execute(new TransactionCallbackWithoutResult() {
				@Override
				protected void doInTransactionWithoutResult(TransactionStatus status) {
					List<ProjectUserRole> projectUserRoleList = new ArrayList<ProjectUserRole>();

					// add project user roles to the list
					for (User u : userList) {
						for (Role role : SaveUsersInProjectAction.this.workbenchDataManager.getAllRoles()) {
							ProjectUserRole projUsrRole = new ProjectUserRole();
							projUsrRole.setUserId(u.getUserid());
							projUsrRole.setRole(role);

							projectUserRoleList.add(projUsrRole);
						}
					}

					MessageNotifier.showMessage(event.getComponent().getWindow(), "Success", "Successfully updated this project's members list.");
					
					for (User u : userList) {
						if (SaveUsersInProjectAction.this.workbenchDataManager.getProjectUserInfoDao().getByProjectIdAndUserId(SaveUsersInProjectAction.this.project.getProjectId().intValue(),
								u.getUserid()) == null) {
							ProjectUserInfo pUserInfo = new ProjectUserInfo(SaveUsersInProjectAction.this.project.getProjectId().intValue(), u.getUserid());
							SaveUsersInProjectAction.this.workbenchDataManager.saveOrUpdateProjectUserInfo(pUserInfo);

						}
					}
				}
			});



		} catch (MiddlewareQueryException ex) {
			SaveUsersInProjectAction.LOG.error(ex.getMessage(), ex);
			// do nothing because getting the User will not fail
			event.getComponent().getWindow().showNotification("");
			MessageNotifier.showError(event.getComponent().getWindow(), this.messageSource.getMessage(Message.ERROR_DATABASE),
					"A database problem occured while updating this project's members list. Please see error logs.");
		} catch (Exception e) {
			SaveUsersInProjectAction.LOG.error(e.getMessage(), e);
		}
	}

	/**
	 * Create a local database user for each workbench user.
	 * 
	 * @param managerFactory
	 * @param projectUserRoles
	 * @param projectSaved
	 * @throws MiddlewareQueryException
	 */
	private void createLocalDatabaseUsers(ManagerFactory managerFactory, List<ProjectUserRole> projectUserRoles, Project projectSaved)
			throws MiddlewareQueryException {
		userDataManager = managerFactory.getUserDataManager();
		Map<Integer, String> usersAccountedFor = new HashMap<Integer, String>();

		for (ProjectUserRole projectUserRole : projectUserRoles) {
			Integer workbenchUserId = projectUserRole.getUserId();

			// a user may have multiple roles
			// and we only need to create 1 local user per workbench user
			if (usersAccountedFor.containsKey(workbenchUserId)) {
				continue;
			}

			User workbenchUser = this.workbenchDataManager.getUserById(workbenchUserId);
			User localUser = workbenchUser.copy();

			Person currentPerson = this.workbenchDataManager.getPersonById(workbenchUser.getPersonid());
			Person localPerson = currentPerson.copy();

			// Check if the Person record already exists
			if (!userDataManager.isPersonExists(localPerson.getFirstName().toUpperCase(), localPerson.getLastName().toUpperCase())) {
				userDataManager.addPerson(localPerson);
			} else {
				// set localPerson to the existing person
				List<Person> persons = userDataManager.getAllPersons();
				for (Person person : persons) {
					if (person.getLastName().equalsIgnoreCase(localPerson.getLastName())
							&& person.getFirstName().equalsIgnoreCase(localPerson.getFirstName())) {
						localPerson = person;
						break;
					}
				}
			}

			ProjectUserMysqlAccount userMysqlAccount =
					this.workbenchDataManager.getProjectUserMysqlAccountByProjectIdAndUserId(this.project.getProjectId().intValue(),
							workbenchUser.getUserid());
			if (userMysqlAccount == null) {
				// this probably won't happen because we create MySQL accounts for each user
				continue;
			}

			// use the MySQL username and password as username/password for the new local database user
			localUser.setName(userMysqlAccount.getMysqlUsername());
			localUser.setPassword(userMysqlAccount.getMysqlPassword());

			// If the selected member does not exist yet in the local database, then add
			User localDatabaseUser = userDataManager.getUserByUserName(localUser.getName());
			Integer userId = localDatabaseUser == null ? null : localDatabaseUser.getUserid();

			if (userId != null) {
				continue;
			}

			localUser.setPersonid(localPerson.getId());
			localUser.setAccess(SaveUsersInProjectAction.PROJECT_USER_ACCESS_NUMBER);
			localUser.setType(SaveUsersInProjectAction.PROJECT_USER_TYPE);
			localUser.setInstalid(Integer.valueOf(this.projectUserInstalId));
			localUser.setStatus(Integer.valueOf(SaveUsersInProjectAction.PROJECT_USER_STATUS));
			localUser.setAdate(this.getCurrentDate());
			userId = userDataManager.addUser(localUser);

			// add or update a workbench user to ibdb user mapping
			User ibdbUser = userDataManager.getUserById(userId);

			IbdbUserMap ibdbUserMap;
			ibdbUserMap = this.workbenchDataManager.getIbdbUserMap(workbenchUser.getUserid(), this.project.getProjectId());

			if (ibdbUserMap != null) {
				ibdbUserMap.setIbdbUserId(ibdbUser.getUserid());
			} else {
				ibdbUserMap = new IbdbUserMap();
				ibdbUserMap.setWorkbenchUserId(workbenchUser.getUserid());
				ibdbUserMap.setProjectId(this.project.getProjectId());
				ibdbUserMap.setIbdbUserId(ibdbUser.getUserid());
			}
			this.workbenchDataManager.addIbdbUserMap(ibdbUserMap);

			usersAccountedFor.put(projectUserRole.getUserId(), localUser.getName());
		}
	}

	protected void createMySQLUsers(List<ProjectUserRole> projectUserRoles) {
		if (this.project.getProjectId() == null) {
			return;
		}

		Map<Integer, String> idAndNameOfProjectMembers = new HashMap<Integer, String>();

		for (ProjectUserRole projectUserRole : projectUserRoles) {
			try {
				User member = this.workbenchDataManager.getUserById(projectUserRole.getUserId());
				if (member == null) {
					continue;
				}
				Person person = this.workbenchDataManager.getPersonById(member.getPersonid());
				ProjectUserMysqlAccount userMysqlAccount =
						this.workbenchDataManager.getProjectUserMysqlAccountByProjectIdAndUserId(this.project.getProjectId().intValue(),
								member.getUserid());
				if (userMysqlAccount == null && person != null) {
					// we need to create a MySQL account for this user
					idAndNameOfProjectMembers.put(member.getUserid(), person.getInitialsWithTimestamp());
				}
			} catch (MiddlewareQueryException ex) {
				SaveUsersInProjectAction.LOG.error(ex.getMessage(), ex);
				// do nothing because getting the User will not fail
			}
		}

		MysqlAccountGenerator mysqlAccountGenerator =
				new MysqlAccountGenerator(this.project.getCropType(), this.project.getProjectId(), idAndNameOfProjectMembers,
						this.workbenchDataManager);

		mysqlAccountGenerator.generateMysqlAccounts();
	}

	private Integer getCurrentDate() {
		return DateUtil.getCurrentDateAsIntegerValue();
	}
}
