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

import org.generationcp.commons.vaadin.util.MessageNotifier;
import org.generationcp.ibpworkbench.IBPWorkbenchApplication;
import org.generationcp.ibpworkbench.comp.ibtools.breedingview.select.SelectDatasetForBreedingViewPanel;
import org.generationcp.ibpworkbench.comp.window.IContentWindow;
import org.generationcp.ibpworkbench.navigation.NavManager;
import org.generationcp.middleware.manager.Database;
import org.generationcp.middleware.pojos.workbench.Project;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Component.Event;
import com.vaadin.ui.Window;
import com.vaadin.ui.Window.Notification;

/**
 * 
 * @author Jeffrey Morales
 *
 */
public class OpenSelectProjectForStudyAndDatasetViewAction implements ClickListener, ActionListener {
    private static final long serialVersionUID = 1L;
    
    private Project currentProject;
    private Project lastOpenedProject;
    
    public OpenSelectProjectForStudyAndDatasetViewAction(Project currentProject) {
        
        this.currentProject = currentProject;
        
    }

    private static final Logger LOG = LoggerFactory.getLogger(OpenSelectProjectForStudyAndDatasetViewAction.class);
    
    @Override
    public void buttonClick(ClickEvent event) {
    	 doAction(event.getComponent().getWindow(), null, true);
    }

	@Override
	public void doAction(Window window, String uriFragment,
			boolean isLinkAccessed) {
		
		IContentWindow w = (IContentWindow) window;
        
        IBPWorkbenchApplication app = IBPWorkbenchApplication.get();
        
        lastOpenedProject = app.getSessionData().getLastOpenedProject();
        
        if (currentProject != null) {
            
            w.showContent(new SelectDatasetForBreedingViewPanel(currentProject, Database.LOCAL));
            
        } else if (lastOpenedProject != null) {
            
        	w.showContent(new SelectDatasetForBreedingViewPanel(lastOpenedProject, Database.LOCAL));
            
        } else {
        	MessageNotifier.showWarning(window, "Error", "Please select a Project first.");
            
        }
		
	}

	@Override
	public void doAction(Event event) {
		NavManager.breadCrumbClick(this, event);
	}
    
}
