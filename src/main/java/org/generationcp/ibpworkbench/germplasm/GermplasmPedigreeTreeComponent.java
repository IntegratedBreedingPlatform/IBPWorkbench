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

package org.generationcp.ibpworkbench.germplasm;

import org.generationcp.ibpworkbench.germplasm.containers.GermplasmIndexContainer;
import org.generationcp.ibpworkbench.germplasm.listeners.GermplasmTreeExpandListener;
import org.generationcp.ibpworkbench.util.Util;
import org.generationcp.middleware.pojos.GermplasmPedigreeTree;
import org.generationcp.middleware.pojos.GermplasmPedigreeTreeNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Configurable;

import com.vaadin.event.ItemClickEvent;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.TabSheet.Tab;
import com.vaadin.ui.Tree;
import com.vaadin.ui.VerticalLayout;

@Configurable
public class GermplasmPedigreeTreeComponent extends Tree {

	private static final long serialVersionUID = 1L;
	private static final Logger LOG = LoggerFactory.getLogger(GermplasmPedigreeTreeComponent.class);
	private GermplasmPedigreeTree germplasmPedigreeTree;
	private GermplasmQueries qQuery;
	private VerticalLayout mainLayout;
	private TabSheet tabSheet;
	private GermplasmIndexContainer dataIndexContainer;
	private Boolean includeDerivativeLines;

	public GermplasmPedigreeTreeComponent(int gid, GermplasmQueries qQuery, GermplasmIndexContainer dataResultIndexContainer,
			VerticalLayout mainLayout, TabSheet tabSheet) {

		super();

		this.initializeTree(gid, qQuery, dataResultIndexContainer, mainLayout, tabSheet, false);

	}

	public GermplasmPedigreeTreeComponent(int gid, GermplasmQueries qQuery, GermplasmIndexContainer dataResultIndexContainer,
			VerticalLayout mainLayout, TabSheet tabSheet, Boolean includeDerivativeLines) {

		super();

		this.initializeTree(gid, qQuery, dataResultIndexContainer, mainLayout, tabSheet, includeDerivativeLines);
	}

	private void initializeTree(int gid, GermplasmQueries qQuery, GermplasmIndexContainer dataResultIndexContainer,
			VerticalLayout mainLayout, TabSheet tabSheet, Boolean includeDerivativeLines) {
		this.mainLayout = mainLayout;
		this.tabSheet = tabSheet;
		this.qQuery = qQuery;
		this.dataIndexContainer = dataResultIndexContainer;

		this.includeDerivativeLines = includeDerivativeLines;

		this.setSizeFull();
		this.germplasmPedigreeTree = qQuery.generatePedigreeTree(Integer.valueOf(gid), 1, includeDerivativeLines);
		this.addNode(this.germplasmPedigreeTree.getRoot(), 1);
		this.setImmediate(false);

		this.addListener(new ItemClickEvent.ItemClickListener() {

			private static final long serialVersionUID = -6626097251439208783L;

			@Override
			public void itemClick(ItemClickEvent event) {
				String item = event.getItemId().toString();
				if (GermplasmPedigreeTreeComponent.this.isExpanded(item)) {
					GermplasmPedigreeTreeComponent.this.collapseItem(item);
				} else {
					GermplasmPedigreeTreeComponent.this.expandItem(item);
					GermplasmPedigreeTreeComponent.this.pedigreeTreeExpandAction(item);
				}
			}
		});

		this.addListener(new GermplasmTreeExpandListener(this));

	}

	private void addNode(GermplasmPedigreeTreeNode node, int level) {
		if (level == 1) {
			String leafNodeId = node.getGermplasm().getGid().toString();
			this.addItem(leafNodeId);
			this.setItemCaption(leafNodeId, this.getNodeLabel(node));
			this.setParent(leafNodeId, leafNodeId);
			this.setChildrenAllowed(leafNodeId, true);

		}

		for (GermplasmPedigreeTreeNode parent : node.getLinkedNodes()) {
			String leafNodeId = node.getGermplasm().getGid().toString();
			final Integer gid = parent.getGermplasm().getGid();
			String parentNodeId = node.getGermplasm().getGid() + "@" + gid;
			this.addItem(parentNodeId);
			this.setItemCaption(parentNodeId,  this.getNodeLabel(parent));
			this.setParent(parentNodeId, leafNodeId);
			this.setChildrenAllowed(parentNodeId, true);

			this.addNode(parent, level + 1);
		}
	}
	
	String getNodeLabel(final GermplasmPedigreeTreeNode node) {
		String preferredName = "";
		final Integer gid = node.getGermplasm().getGid();
		try {
			preferredName = node.getGermplasm().getPreferredName().getNval();
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
			preferredName = String.valueOf(gid);
		}
		final StringBuilder sb = new StringBuilder(preferredName);
		if (gid != 0) {
			sb.append("(" + gid + ")");
			
		}
		return sb.toString();
	}

	private void addNode(GermplasmPedigreeTreeNode node, String itemIdOfParent) {
		for (GermplasmPedigreeTreeNode parent : node.getLinkedNodes()) {
			String parentNodeId = node.getGermplasm().getGid() + "@" + parent.getGermplasm().getGid();
			this.addItem(parentNodeId);
			this.setItemCaption(parentNodeId, this.getNodeLabel(parent));
			this.setParent(parentNodeId, itemIdOfParent);
			this.setChildrenAllowed(parentNodeId, true);
		}
	}

	public void pedigreeTreeExpandAction(String itemId) {
		if (itemId.contains("@")) {
			String gidString = itemId.substring(itemId.indexOf("@") + 1, itemId.length());
			this.germplasmPedigreeTree = this.qQuery.generatePedigreeTree(Integer.valueOf(gidString), 2, this.includeDerivativeLines);
			this.addNode(this.germplasmPedigreeTree.getRoot(), itemId);
		} else {
			this.germplasmPedigreeTree = this.qQuery.generatePedigreeTree(Integer.valueOf(itemId), 2, this.includeDerivativeLines);
			this.addNode(this.germplasmPedigreeTree.getRoot(), 2);
		}

	}

	public void displayNewGermplasmDetailTab(int gid) {
		if (this.mainLayout != null && this.tabSheet != null) {
			VerticalLayout detailLayout = new VerticalLayout();
			detailLayout.setSpacing(true);

			if (!Util.isTabExist(this.tabSheet, String.valueOf(gid))) {
				detailLayout.addComponent(new GermplasmDetail(gid, this.qQuery, this.dataIndexContainer, this.mainLayout, this.tabSheet,
						false));
				Tab tab = this.tabSheet.addTab(detailLayout, String.valueOf(gid), null);
				tab.setDescription(String.valueOf(gid));
				tab.setClosable(true);
				this.tabSheet.setSelectedTab(detailLayout);
				this.mainLayout.addComponent(this.tabSheet);

			} else {
				Tab tab = Util.getTabAlreadyExist(this.tabSheet, String.valueOf(gid));
				this.tabSheet.setSelectedTab(tab.getComponent());
			}
		}
	}

}
