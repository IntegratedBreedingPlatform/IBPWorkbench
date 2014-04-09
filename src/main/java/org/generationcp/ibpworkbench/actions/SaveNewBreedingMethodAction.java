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
package org.generationcp.ibpworkbench.actions;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.vaadin.data.Validator;
import com.vaadin.ui.Component;
import org.generationcp.commons.hibernate.ManagerFactoryProvider;
import org.generationcp.commons.vaadin.spring.SimpleResourceBundleMessageSource;
import org.generationcp.commons.vaadin.util.MessageNotifier;
import org.generationcp.ibpworkbench.IWorkbenchSession;
import org.generationcp.ibpworkbench.Message;
import org.generationcp.ibpworkbench.SessionData;
import org.generationcp.ibpworkbench.ui.ProjectBreedingMethodsPanel;
import org.generationcp.ibpworkbench.ui.form.AddBreedingMethodForm;
import org.generationcp.ibpworkbench.ui.programmethods.AddBreedingMethodsWindow;
import org.generationcp.ibpworkbench.model.BreedingMethodModel;
import org.generationcp.ibpworkbench.ui.programmethods.ProgramMethodsView;
import org.generationcp.middleware.manager.ManagerFactory;
import org.generationcp.middleware.manager.api.GermplasmDataManager;
import org.generationcp.middleware.pojos.Method;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import com.vaadin.data.util.BeanItem;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;

/**
 * 
 * @author Jeffrey Morales
 * 
 */

@Configurable
public class SaveNewBreedingMethodAction implements ClickListener {
    
    private static final Logger LOG = LoggerFactory.getLogger(SaveNewBreedingMethodAction.class);
    private static final long serialVersionUID = 1L;
   
    private AddBreedingMethodForm newBreedingMethodForm;
    
    private AddBreedingMethodsWindow window;
    
    private Component projectBreedingMethodsPanel;

    @Autowired
    private SimpleResourceBundleMessageSource messageSource;

    @Autowired
    private SessionData sessionData;

    @Autowired
    private ManagerFactoryProvider managerFactoryProvider;

    public SaveNewBreedingMethodAction(AddBreedingMethodForm newBreedingMethodForm, AddBreedingMethodsWindow window, Component projectBreedingMethodsPanel) {
        this.newBreedingMethodForm = newBreedingMethodForm;
        this.window = window;
        this.projectBreedingMethodsPanel = projectBreedingMethodsPanel;
        
    }
    
    @Override
    public void buttonClick(ClickEvent event) {

        try {
            newBreedingMethodForm.commit();
        } catch (Validator.EmptyValueException e) {
            MessageNotifier.showError(event.getComponent().getWindow(),messageSource.getMessage(Message.INVALID_OPERATION),e.getLocalizedMessage());
            return;
        }

        @SuppressWarnings("unchecked")
        BeanItem<BreedingMethodModel> breedingMethodBean = (BeanItem<BreedingMethodModel>) newBreedingMethodForm.getItemDataSource();
        BreedingMethodModel breedingMethod = breedingMethodBean.getBean();

        //IBPWorkbenchApplication app = IBPWorkbenchApplication.get();
        IWorkbenchSession appSession = (IWorkbenchSession) event.getComponent().getApplication();


        if (!appSession.getSessionData().getUniqueBreedingMethods().contains(breedingMethod.getMethodName())){

            appSession.getSessionData().getUniqueBreedingMethods().add(breedingMethod.getMethodName());
        
            Integer nextKey = appSession.getSessionData().getProjectBreedingMethodData().keySet().size() + 1;
            
            nextKey = nextKey*-1;
        
            BreedingMethodModel newBreedingMethod = new BreedingMethodModel();
        
            newBreedingMethod.setMethodName(breedingMethod.getMethodName());
            newBreedingMethod.setMethodDescription(breedingMethod.getMethodDescription());
            newBreedingMethod.setMethodCode(breedingMethod.getMethodCode());
            newBreedingMethod.setMethodGroup(breedingMethod.getMethodGroup());
            newBreedingMethod.setMethodType(breedingMethod.getMethodType());
        
            newBreedingMethod.setMethodId(nextKey);

            appSession.getSessionData().getProjectBreedingMethodData().put(nextKey, newBreedingMethod);
            
            LOG.info(appSession.getSessionData().getProjectBreedingMethodData().toString());
            
            //newBreedingMethodForm.commit();
            
            Method newMethod=new Method();
            newMethod.setMid(newBreedingMethod.getMethodId());
            newMethod.setMname(newBreedingMethod.getMethodName());
            newMethod.setMdesc(newBreedingMethod.getMethodDescription());
            newMethod.setMcode(newBreedingMethod.getMethodCode());
            newMethod.setMgrp(newBreedingMethod.getMethodGroup());
            newMethod.setMtype(newBreedingMethod.getMethodType());

            if (appSession.getSessionData().getUserData() != null)
                newMethod.setUser(appSession.getSessionData().getUserData().getUserid());

            newMethod.setLmid(newBreedingMethod.getMethodId());
            newMethod.setGeneq(newBreedingMethod.getMethodId());
            newMethod.setMattr(0);
            newMethod.setMprgn(0);
            newMethod.setReference(0);

            
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
            
            newMethod.setMdate(Integer.parseInt(sdf.format(new Date())));
            newMethod.setMfprg(0);

            //TODO: UPDATE THIS CODE
            GermplasmDataManager gdm = managerFactoryProvider.getManagerFactoryForProject(sessionData.getLastOpenedProject()).getGermplasmDataManager();

            try {
            	 Integer newMethodId =gdm.addMethod(newMethod);

                 newMethod.setMid(newMethodId);

			} catch (Exception e) { // we might have null exception, better be prepared
				e.printStackTrace();
			}
            
            newMethod.setIsnew(true);

            //TODO: compatibility to old UI, remove this if new UI is stable and final
            if (projectBreedingMethodsPanel instanceof  ProjectBreedingMethodsPanel)  {
                ((ProjectBreedingMethodsPanel)projectBreedingMethodsPanel).getSelect().addItem(newMethod);
                ((ProjectBreedingMethodsPanel)projectBreedingMethodsPanel).getSelect().setItemCaption(newMethod, newMethod.getMname());
                ((ProjectBreedingMethodsPanel)projectBreedingMethodsPanel).getSelect().select(newMethod);
                ((ProjectBreedingMethodsPanel)projectBreedingMethodsPanel).getSelect().setValue(newMethod);
            }

            if (projectBreedingMethodsPanel instanceof ProgramMethodsView) {
                ProgramMethodsView pv = (ProgramMethodsView)projectBreedingMethodsPanel;

                pv.addRow(newMethod,false,0);
            }

            /*
            User user = app.getSessionData().getUserData();
            Project currentProject = app.getSessionData().getLastOpenedProject();
            ProjectActivity projAct = new ProjectActivity(new Integer(currentProject.getProjectId().intValue()), currentProject, "Project Methods", "Added a new Breeding Method ("+ newMethod.getMname() + ")", user, new Date());
            try {
				workbenchDataManager.addProjectActivity(projAct);
			} catch (MiddlewareQueryException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} */

            newBreedingMethod = null;
            window.getParent().removeWindow(window);
        
        }
        
    }
}