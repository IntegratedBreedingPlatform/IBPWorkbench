
package org.generationcp.ibpworkbench.cross.study.traitdonors.main.listeners;

import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Component;
import org.generationcp.ibpworkbench.cross.study.adapted.main.SetUpTraitFilter;
import org.generationcp.ibpworkbench.cross.study.commons.EnvironmentFilter;
import org.generationcp.ibpworkbench.cross.study.commons.trait.filter.CategoricalVariatesSection;
import org.generationcp.ibpworkbench.cross.study.commons.trait.filter.CharacterTraitsSection;
import org.generationcp.ibpworkbench.cross.study.commons.trait.filter.NumericTraitsSection;
import org.generationcp.ibpworkbench.cross.study.traitdonors.main.PreselectTraitFilter;
import org.generationcp.ibpworkbench.cross.study.traitdonors.main.SetUpTraitDonorFilter;
import org.generationcp.ibpworkbench.cross.study.traitdonors.main.TraitWelcomeScreen;
import org.generationcp.ibpworkbench.exception.GermplasmStudyBrowserException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class TraitDonorButtonClickListener implements ClickListener {

	private static final long serialVersionUID = -7609072413444730195L;

	private static final Logger log = LoggerFactory.getLogger(TraitDonorButtonClickListener.class);

	private final Component source;
	private Integer traitId;
	private String traitName;
	private String variateType;
	private List<Integer> envIds;

	public TraitDonorButtonClickListener(Component source) {
		super();
		this.source = source;
	}

	public TraitDonorButtonClickListener(Component source, Integer traitId, String traitName, String variateType, List<Integer> envIds) {
		super();
		this.source = source;
		this.traitId = traitId;
		this.traitName = traitName;
		this.variateType = variateType;
		this.envIds = envIds;
	}

	// TODO : Error handling
	@Override
	public void buttonClick(ClickEvent event) {
		Object data = event.getButton().getData();

		if (this.source instanceof TraitWelcomeScreen) {
			TraitWelcomeScreen screen = (TraitWelcomeScreen) this.source;
			if (TraitWelcomeScreen.NEXT_BUTTON_ID.equals(data)) {
				try {
					screen.nextButtonClickAction();
				} catch (GermplasmStudyBrowserException e) {
					TraitDonorButtonClickListener.log.error("Error occured while leaving the Welcome Screen", e);
				}
			}
		} else if (this.source instanceof PreselectTraitFilter) {
			PreselectTraitFilter screen = (PreselectTraitFilter) this.source;

			if (PreselectTraitFilter.NEXT_BUTTON_ID.equals(data)) {
				screen.nextButtonClickAction();
			}
		} else if (this.source instanceof NumericTraitsSection) {
			NumericTraitsSection screen = (NumericTraitsSection) this.source;

			if (NumericTraitsSection.TRAIT_BUTTON_ID.equals(data)) {
				screen.showNumericVariateClickAction(this.traitId, this.traitName, this.envIds);
			}
		} else if (this.source instanceof SetUpTraitDonorFilter) {
			SetUpTraitDonorFilter screen = (SetUpTraitDonorFilter) this.source;

			if (SetUpTraitFilter.NEXT_BUTTON_ID.equals(data)) {
				screen.nextButtonClickAction();
			}

		} else if (this.source instanceof EnvironmentFilter) {
			EnvironmentFilter screen = (EnvironmentFilter) this.source;
			if (EnvironmentFilter.NEXT_BUTTON_ID.equals(data)) {
				screen.nextButtonClickAction();
			}
		} else if (this.source instanceof CharacterTraitsSection) {
			CharacterTraitsSection screen = (CharacterTraitsSection) this.source;
			if (CharacterTraitsSection.TRAIT_BUTTON_ID.equals(data)) {
				screen.showTraitObservationClickAction(this.traitId, this.variateType, this.traitName, this.envIds);
			}
		} else if (this.source instanceof CategoricalVariatesSection) {
			CategoricalVariatesSection screen = (CategoricalVariatesSection) this.source;
			if (CategoricalVariatesSection.TRAIT_BUTTON_ID.equals(data)) {
				screen.showTraitObservationClickAction(this.traitId, this.variateType, this.traitName, this.envIds);
			}
		}
	}

}
