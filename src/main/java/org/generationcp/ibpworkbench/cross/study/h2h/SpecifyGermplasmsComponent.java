
package org.generationcp.ibpworkbench.cross.study.h2h;

import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.Button;
import com.vaadin.ui.Label;
import com.vaadin.ui.Window;
import org.generationcp.ibpworkbench.cross.study.h2h.listeners.H2HComparisonQueryButtonClickListener;
import org.generationcp.ibpworkbench.germplasm.dialogs.SelectAGermplasmDialog;
import org.generationcp.commons.vaadin.spring.InternationalizableComponent;
import org.generationcp.commons.vaadin.util.MessageNotifier;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Configurable;

@Configurable
public class SpecifyGermplasmsComponent extends AbsoluteLayout implements InitializingBean, InternationalizableComponent {

	private static final long serialVersionUID = -7925696669478799303L;

	public static final String NEXT_BUTTON_ID = "SpecifyGermplasmsComponent Next Button ID";
	public static final String SELECT_TEST_ENTRY_BUTTON_ID = "SpecifyGermplasmsComponent Select Test Entry Button ID";
	public static final String SELECT_STANDARD_ENTRY_BUTTON_ID = "SpecifyGermplasmsComponent Select Standard Entry Button ID";

	private Label specifyTestEntryLabel;
	private Label specifyStandardEntryLabel;
	private Label testEntryLabel;
	private Label standardEntryLabel;

	private Button selectTestEntryButton;
	private Button selectStandardEntryButton;
	private Button nextButton;

	private final HeadToHeadComparisonMain mainScreen;
	private final TraitsAvailableComponent nextScreen;
	private final ResultsComponent resultsScreen;

	private Integer lastTestEntryGID;
	private Integer lastStandardEntryGID;

	public SpecifyGermplasmsComponent(HeadToHeadComparisonMain mainScreen, TraitsAvailableComponent nextScreen,
			ResultsComponent resultScreen) {
		this.mainScreen = mainScreen;
		this.nextScreen = nextScreen;
		this.resultsScreen = resultScreen;
		this.lastTestEntryGID = null;
		this.lastStandardEntryGID = null;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		this.setHeight("200px");
		this.setWidth("1000px");

		this.specifyTestEntryLabel = new Label("Specify a test entry:");
		this.addComponent(this.specifyTestEntryLabel, "top:20px;left:30px");

		this.testEntryLabel = new Label();
		this.testEntryLabel.setWidth("200px");
		this.testEntryLabel.setImmediate(true);
		this.addComponent(this.testEntryLabel, "top:20px;left:150px");

		this.specifyStandardEntryLabel = new Label("Specify a standard entry:");
		this.addComponent(this.specifyStandardEntryLabel, "top:20px;left:450px");

		this.standardEntryLabel = new Label();
		this.standardEntryLabel.setWidth("200px");
		this.standardEntryLabel.setImmediate(true);
		this.addComponent(this.standardEntryLabel, "top:20px;left:600px");

		this.selectTestEntryButton = new Button("Select test entry");
		this.selectTestEntryButton.setData(SpecifyGermplasmsComponent.SELECT_TEST_ENTRY_BUTTON_ID);
		this.selectTestEntryButton.addListener(new H2HComparisonQueryButtonClickListener(this));
		this.addComponent(this.selectTestEntryButton, "top:70px;left:170px");

		this.selectStandardEntryButton = new Button("Select standard entry");
		this.selectStandardEntryButton.setData(SpecifyGermplasmsComponent.SELECT_STANDARD_ENTRY_BUTTON_ID);
		this.selectStandardEntryButton.addListener(new H2HComparisonQueryButtonClickListener(this));
		this.addComponent(this.selectStandardEntryButton, "top:70px;left:610px");

		this.nextButton = new Button("Next");
		this.nextButton.setData(SpecifyGermplasmsComponent.NEXT_BUTTON_ID);
		this.nextButton.addListener(new H2HComparisonQueryButtonClickListener(this));
		this.addComponent(this.nextButton, "top:150px;left:900px");
	}

	public void selectTestEntryButtonClickAction() {
		Window parentWindow = this.getWindow();
		SelectAGermplasmDialog selectAGermplasmDialog = new SelectAGermplasmDialog(this, parentWindow, this.testEntryLabel);
		parentWindow.addWindow(selectAGermplasmDialog);
	}

	public void selectStandardEntryButtonClickAction() {
		Window parentWindow = this.getWindow();
		SelectAGermplasmDialog selectAGermplasmDialog = new SelectAGermplasmDialog(this, parentWindow, this.standardEntryLabel);
		parentWindow.addWindow(selectAGermplasmDialog);
	}

	public void nextButtonClickAction() {
		if (this.testEntryLabel.getData() == null) {
			MessageNotifier.showWarning(this.getWindow(), "Warning!",
					"Need to specify a test entry. Please use the Select test entry button.");
			return;
		}

		if (this.standardEntryLabel.getData() == null) {
			MessageNotifier.showWarning(this.getWindow(), "Warning!",
					"Need to specify a standard entry. Please use the Select standard entry button.");
			return;
		}

		Integer testEntryGID = (Integer) this.testEntryLabel.getData();
		Integer standardEntryGID = (Integer) this.standardEntryLabel.getData();

		if (this.nextScreen != null) {
			if (this.areCurrentGIDsDifferentFromLast(testEntryGID, standardEntryGID)) {
				this.resultsScreen.setEntriesLabel((String) this.testEntryLabel.getValue(), (String) this.standardEntryLabel.getValue());
				this.nextScreen.populateTraitsAvailableTable(testEntryGID, standardEntryGID);
				this.lastTestEntryGID = testEntryGID;
				this.lastStandardEntryGID = standardEntryGID;
			}
			this.mainScreen.selectSecondTab();
		}
	}

	private boolean areCurrentGIDsDifferentFromLast(Integer currentTestEntryGID, Integer currentStandardEntryGID) {
		if (this.lastTestEntryGID != null && this.lastStandardEntryGID != null) {
			if (this.lastTestEntryGID == currentTestEntryGID && this.lastStandardEntryGID == currentStandardEntryGID) {
				return false;
			}
		}

		return true;
	}

	@Override
	public void updateLabels() {
		// TODO Auto-generated method stub

	}

}