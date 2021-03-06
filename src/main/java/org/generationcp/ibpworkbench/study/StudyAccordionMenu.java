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

package org.generationcp.ibpworkbench.study;

import org.generationcp.commons.vaadin.spring.InternationalizableComponent;
import org.generationcp.commons.vaadin.spring.SimpleResourceBundleMessageSource;
import org.generationcp.ibpworkbench.Message;
import org.generationcp.ibpworkbench.study.listeners.StudySelectedTabChangeListener;
import org.generationcp.middleware.manager.api.StudyDataManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import com.vaadin.ui.Accordion;
import com.vaadin.ui.Component;
import com.vaadin.ui.VerticalLayout;

@Configurable
public class StudyAccordionMenu extends Accordion implements InitializingBean, InternationalizableComponent {

	@SuppressWarnings("unused")
	private static final Logger LOG = LoggerFactory.getLogger(StudyAccordionMenu.class);
	private static final long serialVersionUID = -1409312205229461614L;

	private static final String STUDY_VARIATES = "Study Variates";
	private static final String STUDY_FACTORS = "Study Factors";
	private static final String STUDY_EFFECTS = "Study Effects";
	
	@Autowired
	private StudyDataManager studyDataManager;
	
	@Autowired
	private SimpleResourceBundleMessageSource messageSource;

	private final int studyId;
	private VerticalLayout layoutVariate;
	private VerticalLayout layoutFactor;
	private VerticalLayout layoutEffect;

	private final StudyDetailComponent studyDetailComponent;

	// this is true if this component is created by accessing the Study Details page directly from the URL
	private final boolean fromUrl;
	private final boolean h2hCall;

	public StudyAccordionMenu(final int studyId, final StudyDetailComponent studyDetailComponent, final boolean fromUrl,
			final boolean h2hCall) {
		this.studyId = studyId;
		this.studyDetailComponent = studyDetailComponent;
		this.fromUrl = fromUrl;
		this.h2hCall = h2hCall;
	}

	public void selectedTabChangeAction() {
		Component selected = this.getSelectedTab();
		Tab tab = this.getTab(selected);
		if (tab.getComponent() instanceof VerticalLayout) {
			// "Factors"
			if (((VerticalLayout) tab.getComponent()).getData().equals(StudyAccordionMenu.STUDY_FACTORS)) {
				if (this.layoutFactor.getComponentCount() == 0) {
					this.layoutFactor.addComponent(new StudyFactorComponent(this.studyDataManager, this.studyId));
					this.layoutFactor.setMargin(true);
					this.layoutFactor.setSpacing(true);
				}
				// "Variates"
			} else if (((VerticalLayout) tab.getComponent()).getData().equals(StudyAccordionMenu.STUDY_VARIATES)) {
				if (this.layoutVariate.getComponentCount() == 0) {
					this.layoutVariate.addComponent(new StudyVariateComponent(this.studyDataManager, this.studyId));
					this.layoutVariate.setMargin(true);
					this.layoutVariate.setSpacing(true);
				}
				// "Datasets"
			} else if (((VerticalLayout) tab.getComponent()).getData().equals(StudyAccordionMenu.STUDY_EFFECTS)) {
				if (this.layoutEffect.getComponentCount() == 0) {
					this.layoutEffect.addComponent(new StudyEffectComponent(this.studyDataManager, this.studyId, this, this.fromUrl,
							this.h2hCall));
				}
			}
		}
	}

	@Override
	public void afterPropertiesSet() {
		this.setSizeFull();

		this.layoutVariate = new VerticalLayout();
		this.layoutVariate.setData(StudyAccordionMenu.STUDY_VARIATES);

		this.layoutFactor = new VerticalLayout();
		this.layoutFactor.setData(StudyAccordionMenu.STUDY_FACTORS);

		this.layoutEffect = new VerticalLayout();
		this.layoutEffect.setData(StudyAccordionMenu.STUDY_EFFECTS);

		this.addTab(this.studyDetailComponent, this.messageSource.getMessage(Message.STUDY_DETAILS_TEXT)); // "Study Details"
		this.addTab(this.layoutFactor, this.messageSource.getMessage(Message.FACTORS_TEXT)); // "Factors"
		this.addTab(this.layoutVariate, this.messageSource.getMessage(Message.VARIATES_TEXT)); // "Variates"
		this.addTab(this.layoutEffect, this.messageSource.getMessage(Message.DATASETS_TEXT)); // "Datasets"

		this.addListener(new StudySelectedTabChangeListener(this));
	}

	@Override
	public void attach() {
		super.attach();
		this.updateLabels();
	}

	@Override
	public void updateLabels() {
		// currently does nothing
	}

	public void updateStudyName(String name) {
		this.studyDetailComponent.setStudyName(name);
	}

}
