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
import java.util.List;

import org.generationcp.commons.vaadin.spring.SimpleResourceBundleMessageSource;
import org.generationcp.commons.vaadin.util.MessageNotifier;
import org.generationcp.ibpworkbench.Message;
import org.generationcp.ibpworkbench.service.ProgramService;
import org.generationcp.ibpworkbench.ui.common.TwinTableSelect;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.manager.api.WorkbenchDataManager;
import org.generationcp.middleware.pojos.workbench.Project;
import org.generationcp.middleware.pojos.workbench.WorkbenchUser;
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

	private final TwinTableSelect<WorkbenchUser> select;

	private final Project project;
	
	@Autowired
	private ProgramService programService;

	@Autowired
	private WorkbenchDataManager workbenchDataManager;

	@Autowired
	private SimpleResourceBundleMessageSource messageSource;
	
	@Autowired
	private PlatformTransactionManager transactionManager;

	public SaveUsersInProjectAction(Project project, TwinTableSelect<WorkbenchUser> select) {
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
		
		final Collection<WorkbenchUser> userList = this.select.getValue();
		try {
			final TransactionTemplate transactionTemplate = new TransactionTemplate(transactionManager);
			transactionTemplate.execute(new TransactionCallbackWithoutResult() {
				@Override
				protected void doInTransactionWithoutResult(TransactionStatus status) {
					
					SaveUsersInProjectAction.this.programService.updateMembersProjectUserInfo(userList, project);
					SaveUsersInProjectAction.this.programService.updateMembersCropPerson(userList, project);
					
					MessageNotifier.showMessage(event.getComponent().getWindow(), "Success", "Successfully updated this project's members list.");
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

	
	public List<Integer> getRemovedUserIds(List<Integer> activeUserIds, Collection<WorkbenchUser> userList) {
		List<Integer> removedUserIds = new ArrayList<>();
		for(Integer activeUserId: activeUserIds) {
			boolean isProgramMember = false;
			for(WorkbenchUser user: userList) {
				if(user.getUserid().equals(activeUserId)) {
					isProgramMember = true;
					break;
				}
			}
			if(!isProgramMember) removedUserIds.add(activeUserId);
		}
		return removedUserIds;
	}

	public void setTransactionManager(PlatformTransactionManager transactionManager) {
		this.transactionManager = transactionManager;
	}

	
	public void setWorkbenchDataManager(WorkbenchDataManager workbenchDataManager) {
		this.workbenchDataManager = workbenchDataManager;
	}

	
	public void setProgramService(ProgramService programService) {
		this.programService = programService;
	}

}
