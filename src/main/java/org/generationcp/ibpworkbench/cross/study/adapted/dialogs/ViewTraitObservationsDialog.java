
package org.generationcp.ibpworkbench.cross.study.adapted.dialogs;

import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.Label;
import com.vaadin.ui.Table;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.BaseTheme;
import org.apache.commons.lang.StringUtils;
import org.generationcp.ibpworkbench.Message;
import org.generationcp.ibpworkbench.study.listeners.GidLinkButtonClickListener;
import org.generationcp.commons.vaadin.spring.InternationalizableComponent;
import org.generationcp.commons.vaadin.spring.SimpleResourceBundleMessageSource;
import org.generationcp.commons.vaadin.ui.BaseSubWindow;
import org.generationcp.commons.vaadin.util.MessageNotifier;
import org.generationcp.middleware.domain.h2h.TraitObservation;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.manager.api.CrossStudyDataManager;
import org.generationcp.middleware.manager.api.GermplasmDataManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Configurable
public class ViewTraitObservationsDialog extends BaseSubWindow implements InitializingBean, InternationalizableComponent {

	public static final String LINE_BY_TRAIT_WINDOW_NAME = "line-by-trait";
	private static final long serialVersionUID = 1L;

	private static final Logger LOG = LoggerFactory.getLogger(ViewTraitObservationsDialog.class);

	private static final String OBSERVATION_NO = "ViewTraitObservationsDialog Observation No";
	private static final String LINE_NO = "ViewTraitObservationsDialog Line No";
	private static final String LINE_GID = "ViewTraitObservationsDialog Line GID";
	private static final String LINE_DESIGNATION = "ViewTraitObservationsDialog Line Designation";

	@Autowired
	private SimpleResourceBundleMessageSource messageSource;

	@Autowired
	private CrossStudyDataManager crossStudyDataManager;

	@Autowired
	private GermplasmDataManager germplasmDataManager;

	private Label popUpLabel;

	private final int traitId;
	private final List<Integer> environmentIds;
	private final String traitName;
	private final String variateType;

	private Table locationTable;
	List<TraitObservation> traitObservations;
	List<Integer> gidList;
	List<String> locationList;
	Map<Integer, String> gidPreferredNameMap;

	Map<Integer, String> gidLocMap;

	public ViewTraitObservationsDialog(
		final Component source, final Window parentWindow, final String variateType, final int traitId, final String traitName,
			final List<Integer> environmentIds) {
		this.variateType = variateType;
		this.traitId = traitId;
		this.traitName = traitName;
		this.environmentIds = environmentIds;
	}

	@Override
	public void updateLabels() {
		// not implemented
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		// set as modal window, other components are disabled while window is open
		this.setModal(true);
		this.setName(ViewTraitObservationsDialog.LINE_BY_TRAIT_WINDOW_NAME);
		// define window size, set as not resizable
		this.setWidth("900px");
		this.setHeight("530px");
		this.setResizable(false);
		this.setCaption(this.messageSource.getMessage(Message.LINE_BY_LOCATION_TITLE) + " " + this.variateType);
		// center window within the browser
		this.center();

		this.initializeLocationTable();
		this.populateLocationTable();

		this.popUpLabel = new Label(this.messageSource.getMessage(Message.LINE_BY_LOCATION_FOR_TRAIT) + " " + this.traitName);
		this.popUpLabel.setDebugId("popUpLabel");

		final AbsoluteLayout mainLayout = new AbsoluteLayout();
		mainLayout.setDebugId("mainLayout");
		mainLayout.setWidth("900px");
		mainLayout.setHeight("420px");

		mainLayout.addComponent(this.popUpLabel, "top:10px;left:20px");
		mainLayout.addComponent(this.locationTable, "top:35px;left:20px");

		this.addComponent(mainLayout);
	}

	private void initializeLocationTable() throws MiddlewareQueryException {
		this.traitObservations = this.crossStudyDataManager.getObservationsForTrait(this.traitId, this.environmentIds);

		this.locationList = this.getLocations(this.traitObservations);

		this.locationTable = new Table();
		this.locationTable.setDebugId("locationTable");
		this.locationTable.setWidth("820px");
		this.locationTable.setHeight("380px");
		this.locationTable.setImmediate(true);
		this.locationTable.setSelectable(true);
		this.locationTable.setColumnCollapsingAllowed(true);
		this.locationTable.setColumnReorderingAllowed(true);

		this.locationTable.addContainerProperty(ViewTraitObservationsDialog.OBSERVATION_NO, Integer.class, null);
		this.locationTable.addContainerProperty(ViewTraitObservationsDialog.LINE_NO, Integer.class, null);
		this.locationTable.addContainerProperty(ViewTraitObservationsDialog.LINE_GID, Button.class, null);
		this.locationTable.addContainerProperty(ViewTraitObservationsDialog.LINE_DESIGNATION, String.class, null);

		this.locationTable.setColumnHeader(ViewTraitObservationsDialog.OBSERVATION_NO,
				this.messageSource.getMessage(Message.OBSERVATION_NO));
		this.locationTable.setColumnHeader(ViewTraitObservationsDialog.LINE_NO, this.messageSource.getMessage(Message.LINE_NO));
		this.locationTable.setColumnHeader(ViewTraitObservationsDialog.LINE_GID, this.messageSource.getMessage(Message.LINE_GID));
		this.locationTable.setColumnHeader(ViewTraitObservationsDialog.LINE_DESIGNATION,
				this.messageSource.getMessage(Message.LINE_DESIGNATION));

		for (final String locationName : this.locationList) {
			final String columnName = "ViewTraitObservationsDialog " + locationName;
			this.locationTable.addContainerProperty(columnName, String.class, null);
			this.locationTable.setColumnHeader(columnName, locationName);
		}

	}

	private void populateLocationTable() throws MiddlewareQueryException {
		this.gidList = this.getGIDs(this.traitObservations);
		this.gidPreferredNameMap = this.germplasmDataManager.getPreferredNamesByGids(this.gidList);

		int observationNo = 1;
		int lineNo = 0;
		int currentGid = 0;
		for (final TraitObservation traitObservation : this.traitObservations) {

			final int gid = traitObservation.getGid();
			final String gidName = this.gidPreferredNameMap.get(gid);
			final String location = traitObservation.getLocationName();
			final String traitVal = traitObservation.getTraitValue();

			if (gid != currentGid) {
				lineNo++;
				currentGid = gid;
			}

			final Object[] itemObj = this.getTableRow(observationNo, lineNo, gid, gidName, location, traitVal);
			this.locationTable.addItem(itemObj, observationNo);
			observationNo++;
		}

	}

	private Object[] getTableRow(final int observationNo, final int lineNo, final int gid, final String gidName, final String location, final String traitVal) {
		final int noOfCols = 4 + this.locationList.size();
		final Object[] row = new Object[noOfCols];

		row[0] = observationNo;
		row[1] = lineNo;

		// make GID as link
		final String gidString = String.valueOf(gid);
		final Button gidButton = new Button(gidString, new GidLinkButtonClickListener(gidString));
		gidButton.setDebugId("gidButton");
		gidButton.setStyleName(BaseTheme.BUTTON_LINK);
		gidButton.setDescription("Click to view Germplasm information");

		row[2] = gidButton;
		row[3] = gidName;

		if ("Numeric Variate".equals(this.variateType)) {
			String value = traitVal;
			if (StringUtils.isNumeric(traitVal)) {
				value = String.valueOf(Double.parseDouble(traitVal));
			}
			row[4 + this.locationList.indexOf(location)] = value;
		} else {
			row[4 + this.locationList.indexOf(location)] = traitVal;
		}

		return row;
	}

	private List<Integer> getGIDs(final List<TraitObservation> result) {
		final List<Integer> gids = new ArrayList<Integer>();

		for (final TraitObservation trait : result) {

			final int id = trait.getGid();

			if (!gids.contains(id)) {
				gids.add(id);
			}
		}

		return gids;
	}

	private List<String> getLocations(final List<TraitObservation> result) {
		final List<String> locList = new ArrayList<String>();

		for (final TraitObservation trait : result) {
			final String location = trait.getLocationName();

			if (!locList.contains(location)) {
				locList.add(location);
			}
		}

		return locList;
	}
}
