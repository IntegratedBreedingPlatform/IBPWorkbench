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

import org.generationcp.commons.exceptions.InternationalizableException;
import org.generationcp.commons.vaadin.util.MessageNotifier;
import org.generationcp.ibpworkbench.IBPWorkbenchApplication;
import org.generationcp.ibpworkbench.SessionData;
import org.generationcp.ibpworkbench.ui.WorkbenchMainView;
import org.generationcp.ibpworkbench.ui.dashboard.WorkbenchDashboard;
import org.generationcp.ibpworkbench.ui.dashboard.listener.LaunchProgramAction;
import org.generationcp.middleware.pojos.workbench.Project;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Component.Event;
import com.vaadin.ui.Window;

/**
 * <b>Description</b>: Listener class for generating the home page view.
 *
 * <br>
 * <br>
 *
 * <b>Author</b>: Michael Blancaflor <br>
 * <b>File Created</b>: Jun 11, 2012.
 */
@Configurable
public class HomeAction implements ClickListener, ActionListener {

	@Autowired
	private SessionData sessionData;

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 5592156945270416052L;

	private static final Logger LOG = LoggerFactory.getLogger(HomeAction.class);

	public HomeAction() {
	}

	/**
	 * Button click.
	 *
	 * @param event the event
	 */
	@Override
	public void buttonClick(ClickEvent event) {
		Window window = event.getComponent().getWindow();
		this.doAction(window, "/Home", true);
	}

	/**
	 * Do action.
	 *
	 * @param event the event
	 */
	@Override
	public void doAction(Event event) {
		// does nothing
	}

	/**
	 * Do action.
	 *
	 * @param window the window
	 * @param uriFragment the uri fragment
	 */
	@Override
	public void doAction(Window window, String uriFragment, boolean isLinkAccessed) {
		// FIXME do not recreate workbench dashboard
		// we create a new WorkbenchDashboard object here
		// so that the UI is reset to its initial state
		// we can remove this if we want to present the last UI state.
		WorkbenchMainView w = (WorkbenchMainView) window;
		WorkbenchDashboard workbenchDashboard = null;
		try {
			workbenchDashboard = new WorkbenchDashboard();

			w.addTitle("");
			w.getSidebar().clearLinks();
			w.showContent(workbenchDashboard);

			// reinitialize dashboard with default values
			Project lastOpenedProgram = this.sessionData.getLastOpenedProject();
			if (lastOpenedProgram != null) {
				workbenchDashboard.initializeDashboardContents(lastOpenedProgram);
			} 

		} catch (Exception e) {
			HomeAction.LOG.error("Exception", e);
			if (e.getCause() instanceof InternationalizableException) {
				InternationalizableException i = (InternationalizableException) e.getCause();
				MessageNotifier.showError(window, i.getCaption(), i.getDescription());
			}
			return;
		}

	}
}
