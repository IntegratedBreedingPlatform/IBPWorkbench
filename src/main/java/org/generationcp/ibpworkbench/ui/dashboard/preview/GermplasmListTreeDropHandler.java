
package org.generationcp.ibpworkbench.ui.dashboard.preview;

import org.generationcp.commons.vaadin.spring.SimpleResourceBundleMessageSource;
import org.generationcp.commons.vaadin.util.MessageNotifier;
import org.generationcp.ibpworkbench.IBPWorkbenchApplication;
import org.generationcp.ibpworkbench.Message;
import org.generationcp.middleware.pojos.GermplasmList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import com.vaadin.data.util.HierarchicalContainer;
import com.vaadin.event.DataBoundTransferable;
import com.vaadin.event.Transferable;
import com.vaadin.event.dd.DragAndDropEvent;
import com.vaadin.event.dd.DropHandler;
import com.vaadin.event.dd.acceptcriteria.AcceptAll;
import com.vaadin.event.dd.acceptcriteria.AcceptCriterion;
import com.vaadin.terminal.gwt.client.ui.dd.VerticalDropLocation;
import com.vaadin.ui.Tree;

/**
 * Created with IntelliJ IDEA. User: cyrus Date: 12/26/13 Time: 10:06 PM To change this template use File | Settings | File Templates.
 */
@Configurable
public class GermplasmListTreeDropHandler implements DropHandler {

	/**
	 *
	 */
	private static final long serialVersionUID = -1719408013536885756L;
	private final Tree tree;
	private final GermplasmListPreviewPresenter presenter;
	private static final Logger LOG = LoggerFactory.getLogger(GermplasmListTreeDropHandler.class);

	@Autowired
	private SimpleResourceBundleMessageSource messageSource;

	public GermplasmListTreeDropHandler(Tree tree, GermplasmListPreviewPresenter presenter) {
		this.tree = tree;
		this.presenter = presenter;
	}

	@Override
	public void drop(DragAndDropEvent dropEvent) {
		// Called whenever a drop occurs on the component

		// Make sure the drag source is the same tree
		Transferable t = dropEvent.getTransferable();

		// see the comment in getAcceptCriterion()
		if (t.getSourceComponent() != this.tree || !(t instanceof DataBoundTransferable)) {
			return;
		}

		Tree.TreeTargetDetails dropData = (Tree.TreeTargetDetails) dropEvent.getTargetDetails();

		Object sourceItemId = ((DataBoundTransferable) t).getItemId();
		// FIXME: Why "over", should be "targetItemId" or just
		// "getItemId"
		Object targetItemId = dropData.getItemIdOver();

		// Location describes on which part of the node the drop took
		// place
		VerticalDropLocation location = dropData.getDropLocation();

		this.moveNode(sourceItemId, targetItemId, location);

		this.tree.requestRepaint();
	}

	@Override
	public AcceptCriterion getAcceptCriterion() {
		return AcceptAll.get();
	}

	/**
	 * Move a node within a tree onto, above or below another node depending on the drop location.
	 *
	 * @param sourceItemId id of the item to move
	 * @param targetItemId id of the item onto which the source node should be moved
	 * @param location VerticalDropLocation indicating where the source node was dropped relative to the target node
	 */
	private void moveNode(Object sourceItemId, Object targetItemId, VerticalDropLocation location) {

		if (location != VerticalDropLocation.MIDDLE || sourceItemId.equals(targetItemId)) {
			return;
		}

		HierarchicalContainer container = (HierarchicalContainer) this.tree.getContainerDataSource();

		if (sourceItemId.equals(presenter.getView().getListLabel())) {
			MessageNotifier.showError(IBPWorkbenchApplication.get().getMainWindow(),
					this.messageSource.getMessage(Message.INVALID_OPERATION),
					this.messageSource.getMessage(Message.UNABLE_TO_MOVE_ROOT_FOLDERS));
			return;
		}

		if (container.hasChildren(sourceItemId)) {
			MessageNotifier.showError(IBPWorkbenchApplication.get().getMainWindow(),
					this.messageSource.getMessage(Message.INVALID_OPERATION),
					this.messageSource.getMessage(Message.INVALID_CANNOT_MOVE_ITEM_WITH_CHILD, this.tree.getItemCaption(sourceItemId)));
			return;
		}

		try {

			if (targetItemId instanceof Integer && !this.presenter.isFolder((Integer) targetItemId)) {
				GermplasmList parentFolder = this.presenter.getGermplasmListParent((Integer) targetItemId);
				if (parentFolder != null) {
					targetItemId = parentFolder.getId();
				} else {
					targetItemId = presenter.getView().getListLabel();
				}
			}

			Object previousTargetItemId = container.getParent(sourceItemId);
			if (previousTargetItemId.equals(targetItemId)) {
				return;
			}

			if (targetItemId instanceof String) {
				this.presenter.dropGermplasmListToParent((Integer) sourceItemId, null);
			} else {
				this.presenter.dropGermplasmListToParent((Integer) sourceItemId, (Integer) targetItemId);
			}

			container.setChildrenAllowed(targetItemId, true);
			if (container.setParent(sourceItemId, targetItemId) && container.hasChildren(targetItemId)) {
				container.moveAfterSibling(sourceItemId, null);
			}

		} catch (Exception error) {
			GermplasmListTreeDropHandler.LOG.error(error.getLocalizedMessage(), error);
		}

	}

}
