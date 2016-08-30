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

package org.generationcp.ibpworkbench.ui.dashboard.listener;

import org.generationcp.commons.exceptions.InternationalizableException;
import org.generationcp.commons.vaadin.util.MessageNotifier;
import org.generationcp.ibpworkbench.ui.dashboard.preview.GermplasmListPreview;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.ui.Component;
import com.vaadin.ui.Tree;
import com.vaadin.ui.Tree.ExpandEvent;

public class GermplasmListTreeExpandListener implements Tree.ExpandListener {

	private static final Logger LOG = LoggerFactory.getLogger(GermplasmListTreeExpandListener.class);
	private static final long serialVersionUID = -5145904396164706110L;
	public static final String LISTS = "Lists";
	private final Component source;

	public GermplasmListTreeExpandListener(Component source) {
		this.source = source;
	}

	@Override
	public void nodeExpand(ExpandEvent event) {
		if (this.source instanceof GermplasmListPreview) {
			if (!event.getItemId().toString().equals(GermplasmListTreeExpandListener.LISTS)) {
				try {
					String id = event.getItemId().toString();
					int germplasmId = Integer.valueOf(id);

					((GermplasmListPreview) this.source).getPresenter().addGermplasmListNode(germplasmId, event.getItemId());
				} catch (NumberFormatException e) {
					GermplasmListTreeExpandListener.LOG.error("Click on the root",e);
				} catch (InternationalizableException e) {
					MessageNotifier.showError(event.getComponent().getWindow(), e.getCaption(), e.getDescription());
					GermplasmListTreeExpandListener.LOG.error(e.getMessage(),e);
				}
			}
		}
	}
}
