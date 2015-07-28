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

package org.generationcp.ibpworkbench.actions;

import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;

import org.generationcp.commons.exceptions.InternationalizableException;
import org.generationcp.commons.vaadin.spring.SimpleResourceBundleMessageSource;
import org.generationcp.commons.vaadin.util.MessageNotifier;
import org.generationcp.commons.vaadin.validator.ValidationUtil;
import org.generationcp.ibpworkbench.Message;
import org.generationcp.ibpworkbench.SessionData;
import org.generationcp.ibpworkbench.model.UserAccountModel;
import org.generationcp.ibpworkbench.ui.common.TwinTableSelect;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.manager.api.WorkbenchDataManager;
import org.generationcp.middleware.pojos.Person;
import org.generationcp.middleware.pojos.User;
import org.generationcp.middleware.pojos.workbench.Project;
import org.generationcp.middleware.pojos.workbench.ProjectActivity;
import org.generationcp.middleware.pojos.workbench.UserInfo;
import org.generationcp.middleware.pojos.workbench.UserRole;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import com.vaadin.data.Validator.InvalidValueException;
import com.vaadin.data.util.BeanItem;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Form;

/**
 * <b>Description</b>: Listener responsible for saving new Users and Persons records created from the Create New Project screen.
 * 
 * <br>
 * <br>
 * 
 * <b>Author</b>: Mark Agarrado <br>
 * <b>File Created</b>: October 15, 2012
 */
@Configurable
public class SaveNewProjectAddUserAction implements ClickListener {

	private static final Logger LOG = LoggerFactory.getLogger(SaveNewProjectAddUserAction.class);

	private static final long serialVersionUID = 5386242653138617919L;

	private final Form userAccountForm;
	private final TwinTableSelect<User> membersSelect;

	@Autowired
	private WorkbenchDataManager workbenchDataManager;

	@Autowired
	private SimpleResourceBundleMessageSource messageSource;

	@Autowired
	private SessionData sessionData;

	public SaveNewProjectAddUserAction(Form userAccountForm, TwinTableSelect<User> membersSelect) {
		this.userAccountForm = userAccountForm;
		this.membersSelect = membersSelect;
	}

	// TODO: Code reviewed by Cyrus: Logic quite similar to SaveUserAccountAction,
	// this can be consolidated to avoid redundant code
	@Override
	public void buttonClick(ClickEvent event) {
		@SuppressWarnings("unchecked")
		BeanItem<UserAccountModel> bean = (BeanItem<UserAccountModel>) this.userAccountForm.getItemDataSource();
		UserAccountModel userAccount = bean.getBean();

		try {
			this.userAccountForm.commit();
		} catch (InternationalizableException e) {
			MessageNotifier.showRequiredFieldError(event.getComponent().getWindow(), e.getDescription());
			return;
		} catch (InvalidValueException e) {
			MessageNotifier.showRequiredFieldError(event.getComponent().getWindow(), ValidationUtil.getMessageFor(e));
			return;
		} catch (Exception e) {
			// handle error for unexpected cases
			return;
		}

		try {
			this.saveUserAccount(userAccount, this.membersSelect);
		} catch (MiddlewareQueryException e) {
			SaveNewProjectAddUserAction.LOG.error("Error encountered while trying to save user account details.", e);
			MessageNotifier.showError(event.getComponent().getWindow(), this.messageSource.getMessage(Message.DATABASE_ERROR),
					this.messageSource.getMessage(Message.SAVE_USER_ACCOUNT_ERROR_DESC));
			return;
		}

		try {
			User user = this.sessionData.getUserData();
			Project currentProject = this.sessionData.getLastOpenedProject();

			if (currentProject != null) {
				ProjectActivity projAct =
						new ProjectActivity(new Integer(currentProject.getProjectId().intValue()), currentProject, "Program Member",
								"Added a new user (" + userAccount.getUsername() + ") to " + currentProject.getProjectName(), user,
								new Date());
				this.workbenchDataManager.addProjectActivity(projAct);
			}
		} catch (MiddlewareQueryException e) {
			SaveNewProjectAddUserAction.LOG.error("Cannot log project activity", e);
		}

		CloseWindowAction action = new CloseWindowAction();
		action.buttonClick(event);

	}

	private void saveUserAccount(UserAccountModel userAccount, TwinTableSelect<User> membersSelect) throws MiddlewareQueryException {
		userAccount.trimAll();

		Person person = new Person();
		person.setFirstName(userAccount.getFirstName());
		person.setMiddleName(userAccount.getMiddleName());
		person.setLastName(userAccount.getLastName());
		person.setEmail(userAccount.getEmail());
		person.setTitle("-");
		person.setContact("-");
		person.setExtension("-");
		person.setFax("-");
		person.setInstituteId(0);
		person.setLanguage(0);
		person.setNotes("-");
		person.setPositionName("-");
		person.setPhone("-");
		this.workbenchDataManager.addPerson(person);

		User user = new User();
		user.setPersonid(person.getId());
		user.setPerson(person);
		user.setName(userAccount.getUsername());
		// set default password for the new user
		user.setPassword(userAccount.getUsername());
		user.setAccess(0);
		user.setAdate(0);
		user.setCdate(0);
		user.setInstalid(0);
		user.setStatus(0);
		user.setType(0);
		user.setIsNew(true);
		this.workbenchDataManager.addUser(user);


		UserInfo userInfo = new UserInfo();
		userInfo.setUserId(user.getUserid());
		userInfo.setLoginCount(0);
		this.workbenchDataManager.insertOrUpdateUserInfo(userInfo);

		// add new user to the TwinColumnSelect
		membersSelect.addItem(user);

		// get currently selected users and add the new user
		@SuppressWarnings("unchecked")
		HashSet<User> selectedMembers = new HashSet<User>(membersSelect.getValue());
		selectedMembers.add(user);
		membersSelect.setValue(selectedMembers);
	}

}
