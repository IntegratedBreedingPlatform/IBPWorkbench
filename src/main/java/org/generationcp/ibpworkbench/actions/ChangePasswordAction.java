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

import org.generationcp.commons.vaadin.spring.SimpleResourceBundleMessageSource;
import org.generationcp.commons.vaadin.util.MessageNotifier;
import org.generationcp.ibpworkbench.Message;
import org.generationcp.ibpworkbench.service.WorkbenchUserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.PasswordField;
import com.vaadin.ui.Window;

@Configurable
public class ChangePasswordAction implements ClickListener {

	private static final long serialVersionUID = 1L;
	private static final Logger LOG = LoggerFactory.getLogger(ChangePasswordAction.class);

	private final String username;
	private final PasswordField confirm;
	private final PasswordField password;

	@Autowired
	private WorkbenchUserService workbenchUserService;

	@Autowired
	private SimpleResourceBundleMessageSource messageSource;

	public ChangePasswordAction(String username, PasswordField password, PasswordField confirm) {
		this.username = username;
		this.password = password;
		this.confirm = confirm;
	}

	@Override
	public void buttonClick(ClickEvent event) {
		try {
			if (!this.password.getValue().toString().equals(this.confirm.getValue().toString())) {
				MessageNotifier.showRequiredFieldError(event.getComponent().getWindow(), "Password must be the same as confirm password.");
				return;
			}

			if ("".equals(this.password.getValue().toString()) || "".equals(this.password.getValue().toString())) {
				MessageNotifier.showRequiredFieldError(event.getComponent().getWindow(), "Password cannot be blank.");
				return;
			}
			if (this.workbenchUserService.updateUserPassword(this.username, this.password.getValue().toString())) {
				Window popupWindow = event.getComponent().getWindow();
				Window parentWindow = popupWindow.getParent();
				parentWindow.removeWindow(popupWindow);

				MessageNotifier.showMessage(parentWindow, "Success", "Successfully changed password", 3000);
			} else {
				MessageNotifier.showRequiredFieldError(event.getComponent().getWindow(), "Password must be the same as confirm password.");
			}

		} catch (Exception e) {
			ChangePasswordAction.LOG.error("Error encountered while trying to login", e);
			MessageNotifier.showRequiredFieldError(event.getComponent().getWindow(),
					this.messageSource.getMessage(Message.LOGIN_DB_ERROR_DESC));
			return;
		}
	}
}
