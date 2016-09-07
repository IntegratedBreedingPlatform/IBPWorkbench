
package org.generationcp.ibpworkbench.security;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.generationcp.ibpworkbench.SessionData;
import org.generationcp.middleware.manager.Operation;
import org.generationcp.middleware.manager.api.WorkbenchDataManager;
import org.generationcp.middleware.pojos.Person;
import org.generationcp.middleware.pojos.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.WebAttributes;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

/**
 * Handler for setting up Workbench specific sutff e.g. {@link SessionData} before redirecting to the page requested on successful
 * authentication. Could also be used to redirect to different destinations based on role if needed.
 *
 * @author Naymesh Mistry
 *
 */
public class WorkbenchAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

	private static final Logger LOG = LoggerFactory.getLogger(WorkbenchAuthenticationSuccessHandler.class);

	@Autowired
	private SessionData sessionData;

	@Autowired
	private WorkbenchDataManager workbenchDataManager;

	private String defaultTargetUrl = "/main";

	private final RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();

	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication)
			throws IOException, ServletException {

		String targetUrl = this.defaultTargetUrl;
		if (response.isCommitted()) {
			this.LOG.debug("Response has already been committed. Unable to redirect to " + targetUrl);
			return;
		}
		this.populateWorkbenchSessionData(authentication);
		this.clearAuthenticationAttributes(request);
		this.redirectStrategy.sendRedirect(request, response, targetUrl);
	}

	/**
	 * Actions that the old org.generationcp.ibpworkbench.actions.LoginPresenter used to perform on successful login.
	 */
	private void populateWorkbenchSessionData(Authentication authentication) {
		// 1. Populate Session Data
		String username = authentication.getName();
		User user = this.workbenchDataManager.getUserByName(username, 0, 1, Operation.EQUAL).get(0);
		Person person = this.workbenchDataManager.getPersonById(user.getPersonid());
		user.setPerson(person);
		this.sessionData.setUserData(user);

		// 2. Remember Me. TODO under BMS-84.
		// See the cookie based scheme in org.generationcp.ibpworkbench.actions.LoginPresenter.doLogin(): line 97-111 for ref.
		// We want this replaced using Spring Security's "Remember Me services" options.

	}

	/**
	 * Removes temporary authentication-related data which may have been stored in the session during the authentication process.
	 */
	protected final void clearAuthenticationAttributes(HttpServletRequest request) {
		HttpSession session = request.getSession(false);
		if (session == null) {
			return;
		}
		session.removeAttribute(WebAttributes.AUTHENTICATION_EXCEPTION);
	}

	public void setDefaultTargetUrl(String defaultTargetUrl) {
		this.defaultTargetUrl = defaultTargetUrl;
	}

	public void setSessionData(SessionData sessionData) {
		this.sessionData = sessionData;
	}

	public void setWorkbenchDataManager(WorkbenchDataManager workbenchDataManager) {
		this.workbenchDataManager = workbenchDataManager;
	}
}
