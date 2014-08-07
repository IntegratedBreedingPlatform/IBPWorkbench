/*******************************************************************************
 * Copyright (c) 2012, All Rights Reserved.
 * 
 * Generation Challenge Programme (GCP)
 * 
 * 
 * This software is licensed for use under the terms of the GNU General Public
 * License (http://bit.ly/8Ztv8M) and the provisions of Part F of the Generation
 * Challenge Programme Amended Consortium Agreement (http://bit.ly/KQX1nL)
 * 
 *******************************************************************************/

package org.generationcp.browser.study;

import org.generationcp.ibpworkbench.Message;
import org.generationcp.commons.exceptions.InternationalizableException;
import org.generationcp.commons.vaadin.spring.InternationalizableComponent;
import org.generationcp.commons.vaadin.spring.SimpleResourceBundleMessageSource;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.domain.dms.Study;
import org.generationcp.middleware.manager.ManagerFactory;
import org.generationcp.middleware.manager.api.OntologyDataManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import com.vaadin.ui.GridLayout;
import com.vaadin.ui.Label;

@Configurable
public class StudyDetailComponent extends GridLayout implements InitializingBean, InternationalizableComponent {

    @SuppressWarnings("unused")
    private static final Logger LOG = LoggerFactory.getLogger(StudyDetailComponent.class);
    private static final long serialVersionUID = 1738426765643928293L;

    private Label lblName;
    private Label lblTitle;
    private Label lblObjective;
    private Label lblType;
    private Label lblStartDate;
    private Label lblEndDate;
    
    private Label studyName;
    private Label studyTitle;
    private Label studyObjective;
    private Label studyType;
    private Label studyStartDate;
    private Label studyEndDate;
    
    private org.generationcp.middleware.manager.api.StudyDataManager studyDataManager;
    private int studyId;

    @Autowired
    private SimpleResourceBundleMessageSource messageSource;
    
    public StudyDetailComponent(org.generationcp.middleware.manager.api.StudyDataManager studyDataManager, int studyId){
        this.studyDataManager = studyDataManager;
        this.studyId = studyId;
    }
    
    @Override
    public void afterPropertiesSet() throws Exception{
        setRows(7);
        setColumns(3);
        setSpacing(true);
        setMargin(true);
        setHeight("100%");
        setHeight("330px");

        lblName = new Label(messageSource.getMessage(Message.NAME_LABEL)); // "Name"
        lblTitle = new Label(messageSource.getMessage(Message.TITLE_LABEL)); // "Title"
        lblObjective = new Label(messageSource.getMessage(Message.OBJECTIVE_LABEL)); // "Objective"
        lblType = new Label(messageSource.getMessage(Message.TYPE_LABEL)); // "Type"
        lblStartDate = new Label(messageSource.getMessage(Message.START_DATE_LABEL)); // "Start Date"
        lblEndDate = new Label(messageSource.getMessage(Message.END_DATE_LABEL)); // "End Date"
        
        // get Study
        Study study;

        try {
            study = studyDataManager.getStudy(Integer.valueOf(studyId));
            
            studyName = new Label(setStudyDetailValue(study.getName()));
            studyTitle = new Label(setStudyDetailValue(study.getTitle()));
            studyObjective = new Label(setStudyDetailValue(study.getObjective()));
            studyType = new Label(setStudyDetailValue(study.getType()));
            studyStartDate = new Label(setStudyDetailValue(String.valueOf(study.getStartDate())));
            studyEndDate = new Label(setStudyDetailValue(String.valueOf(study.getEndDate())));

        } catch (MiddlewareQueryException e) {
            throw new InternationalizableException(e, Message.ERROR_IN_GETTING_STUDY_DETAIL_BY_ID, Message.EMPTY_STRING);
        }
        
        addComponent(lblName, 1, 1);
        addComponent(lblTitle, 1, 2);
        addComponent(lblObjective, 1, 3);
        addComponent(lblType, 1, 4);
        addComponent(lblStartDate, 1, 5);
        addComponent(lblEndDate, 1, 6);
        
        addComponent(studyName, 2, 1);
        addComponent(studyTitle, 2, 2);
        addComponent(studyObjective, 2, 3);
        addComponent(studyType, 2, 4);
        addComponent(studyStartDate, 2, 5);
        addComponent(studyEndDate, 2, 6);
    }
    
    @Override
    public void attach() {
        super.attach();
        updateLabels();
    }

    @Override
    public void updateLabels() {
/*        messageSource.setCaption(lblName, Message.name_label);
        messageSource.setCaption(lblTitle, Message.title_label);
        messageSource.setCaption(lblObjective, Message.objective_label);
        messageSource.setCaption(lblType, Message.type_label);
        messageSource.setCaption(lblStartDate, Message.start_date_label);
        messageSource.setCaption(lblEndDate, Message.end_date_label);*/
    }
    
    private String setStudyDetailValue(String value){

	if(value==null || value.equals("null")){
	    return "";
	}
	return value;
    }

}