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

import org.generationcp.ibpworkbench.ui.ContactBookPanel;
import org.generationcp.ibpworkbench.ui.window.IContentWindow;

import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Component.Event;
import com.vaadin.ui.Window;

/**
 * <b>Description</b>: Listener class for generating Create Contact view.
 *
 * <br>
 * <br>
 *
 * <b>Author</b>: Michael Blancaflor <br>
 * <b>File Created</b>: Jun 11, 2012.
 */
public class CreateContactAction implements ClickListener, ActionListener {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 2150231810445628892L;

	/**
	 * Button click.
	 *
	 * @param event the event
	 */
	@Override
	public void buttonClick(ClickEvent event) {
		this.doAction(event.getComponent().getWindow(), null, true);
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
		ContactBookPanel contactBookPanel = new ContactBookPanel();
		contactBookPanel.setDebugId("contactBookPanel");
		IContentWindow w = (IContentWindow) window;

		w.showContent(contactBookPanel);

	}

}
