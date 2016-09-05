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

import org.generationcp.commons.util.WorkbenchAppPathResolver;
import org.generationcp.ibpworkbench.SessionData;
import org.generationcp.ibpworkbench.ui.WorkbenchMainView;

import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import org.springframework.beans.factory.annotation.Configurable;

import javax.annotation.Resource;

/**
 * <b>Description</b>: Listener class for closing the application.
 *
 * <br>
 * <br>
 *
 * <b>Author</b>: Michael Blancaflor <br>
 * <b>File Created</b>: Jun 11, 2012.
 */
@Configurable
public class SignoutAction implements ClickListener {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1898892299981012511L;

	@Resource
	private SessionData sessionData;

	/**
	 * Button click.
	 *
	 * @param event the event
	 */
	@Override
	public void buttonClick(ClickEvent event) {
		WorkbenchMainView window = (WorkbenchMainView) event.getButton().getWindow();
		window.setUriFragment("", true);

		String fullLogoutUrl = WorkbenchAppPathResolver.getFullWebAddress("ibpworkbench/controller/logout",
				String.format("", this.sessionData.getLastOpenedProject().getProjectId(), this.sessionData.getUserData().getUserid()));

		window.showContent(fullLogoutUrl);
	}

}
