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

import org.generationcp.ibpworkbench.ui.common.TwinTableSelect;
import org.generationcp.ibpworkbench.ui.window.NewProjectAddUserWindow;
import org.generationcp.middleware.pojos.workbench.WorkbenchUser;

import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;

public class OpenNewProjectAddUserWindowAction implements ClickListener {

	private static final long serialVersionUID = 1L;

	private final TwinTableSelect<WorkbenchUser> membersSelect;

	public OpenNewProjectAddUserWindowAction(TwinTableSelect<WorkbenchUser> select) {
		this.membersSelect = select;
	}

	@Override
	public void buttonClick(ClickEvent event) {
		NewProjectAddUserWindow window = new NewProjectAddUserWindow(this.membersSelect);
		window.setDebugId("window");

		event.getComponent().getWindow().addWindow(window);
	}

}
