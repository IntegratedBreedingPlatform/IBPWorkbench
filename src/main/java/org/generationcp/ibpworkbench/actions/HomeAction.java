/*******************************************************************************
 * Copyright (c) 2012, All Rights Reserved.
 * <p/>
 * Generation Challenge Programme (GCP)
 * <p/>
 * <p/>
 * This software is licensed for use under the terms of the GNU General Public License (http://bit.ly/8Ztv8M) and the provisions of Part F
 * of the Generation Challenge Programme Amended Consortium Agreement (http://bit.ly/KQX1nL)
 *******************************************************************************/

package org.generationcp.ibpworkbench.actions;

import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Component.Event;
import com.vaadin.ui.Window;
import org.generationcp.commons.exceptions.InternationalizableException;
import org.generationcp.commons.spring.util.ContextUtil;
import org.generationcp.commons.vaadin.util.MessageNotifier;
import org.generationcp.ibpworkbench.ui.WorkbenchMainView;
import org.generationcp.ibpworkbench.ui.dashboard.WorkbenchDashboard;
import org.generationcp.middleware.pojos.workbench.Project;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

@Configurable
public class HomeAction implements ClickListener, ActionListener {

	@Autowired
	private ContextUtil contextUtil;

	private static final long serialVersionUID = 5592156945270416052L;

	private static final Logger LOG = LoggerFactory.getLogger(HomeAction.class);

	public HomeAction() {
		// does nothing here
	}

	/**
	 * Button click.
	 *
	 * @param event the event
	 */
	@Override
	public void buttonClick(final ClickEvent event) {
		final Window window = event.getComponent().getWindow();
		this.doAction(window, "/Home", true);
	}

	/**
	 * Do action.
	 *
	 * @param event the event
	 */
	@Override
	public void doAction(final Event event) {
		// does nothing
	}

	/**
	 * Do action.
	 *
	 * @param window the window
	 * @param uriFragment the uri fragment
	 */
	@Override
	public void doAction(final Window window, final String uriFragment, final boolean isLinkAccessed) {
		// FIXME do not recreate workbench dashboard
		// we create a new WorkbenchDashboard object here
		// so that the UI is reset to its initial state
		// we can remove this if we want to present the last UI state.
		final WorkbenchMainView workbenchMainView = (WorkbenchMainView) window;
		WorkbenchDashboard workbenchDashboard;
		try {
			workbenchDashboard = new WorkbenchDashboard(window);

			workbenchMainView.showContent(workbenchDashboard);

			// reinitialize dashboard with default values
			final Project lastOpenedProgram = this.contextUtil.getProjectInContext();
			if (lastOpenedProgram != null) {
				workbenchDashboard.initializeDashboardContents(lastOpenedProgram);
			}

		} catch (final Exception e) {
			HomeAction.LOG.error("Exception", e);
			if (e.getCause() instanceof InternationalizableException) {
				final InternationalizableException i = (InternationalizableException) e.getCause();
				MessageNotifier.showError(window, i.getCaption(), i.getDescription());
			}
		}

	}
}
