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

import java.util.Date;

import org.generationcp.commons.exceptions.InternationalizableException;
import org.generationcp.commons.vaadin.spring.SimpleResourceBundleMessageSource;
import org.generationcp.commons.vaadin.util.MessageNotifier;
import org.generationcp.ibpworkbench.Message;
import org.generationcp.ibpworkbench.SessionData;
import org.generationcp.ibpworkbench.ui.WorkflowConstants;
import org.generationcp.ibpworkbench.ui.programlocations.ProgramLocationsView;
import org.generationcp.ibpworkbench.ui.window.IContentWindow;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.manager.api.WorkbenchDataManager;
import org.generationcp.middleware.pojos.User;
import org.generationcp.middleware.pojos.workbench.Project;
import org.generationcp.middleware.pojos.workbench.ProjectActivity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Component.Event;
import com.vaadin.ui.Window;

/**
 * @author Joyce Avestro
 */
@Configurable
public class OpenProgramLocationsAction implements WorkflowConstants, ClickListener, ActionListener {

	private static final long serialVersionUID = 1L;

	private static final Logger LOG = LoggerFactory.getLogger(OpenProgramLocationsAction.class);

	private User user;
	private Project project;

	@Autowired
	private WorkbenchDataManager workbenchDataManager;

	@Autowired
	private SimpleResourceBundleMessageSource messageSource;

	@Autowired
	private SessionData sessionData;

	public OpenProgramLocationsAction() {
	}

	public OpenProgramLocationsAction(Project project) {
		this(project, null);
	}

	public OpenProgramLocationsAction(Project project, User user) {
		this.project = project;
		this.user = user;

	}

	@Override
	public void buttonClick(ClickEvent event) {
		this.doAction(event.getComponent().getWindow(), null, true);
	}

	@Override
	public void doAction(Event event) {
		// does nothing
	}

	@Override
	public void doAction(Window window, String uriFragment, boolean isLinkAccessed) {
		IContentWindow w = (IContentWindow) window;

		if (this.project == null) {
			this.project = this.sessionData.getLastOpenedProject();
		}

		if (this.user == null) {
			this.user = this.sessionData.getUserData();
		}

		try {

			if (this.user != null) {
				try {
					ProjectActivity projAct =
							new ProjectActivity(this.project.getProjectId().intValue(), this.project,
									this.messageSource.getMessage(Message.PROJECT_LOCATIONS_LINK), this.messageSource.getMessage(
											Message.LAUNCHED_APP, this.messageSource.getMessage(Message.PROJECT_LOCATIONS_LINK)),
									this.user, new Date());
					this.workbenchDataManager.addProjectActivity(projAct);

				} catch (MiddlewareQueryException e1) {
					MessageNotifier.showError(window, "Database Error", "<br />" + "Please see error logs");
				}
			}

			w.showContent(new ProgramLocationsView(this.project));

		} catch (Exception e) {
			OpenProgramLocationsAction.LOG.error("Exception", e);
			if (e.getCause() instanceof InternationalizableException) {
				InternationalizableException i = (InternationalizableException) e.getCause();
				MessageNotifier.showError(window, i.getCaption(), i.getDescription());
			}
			return;
		}
	}
}
