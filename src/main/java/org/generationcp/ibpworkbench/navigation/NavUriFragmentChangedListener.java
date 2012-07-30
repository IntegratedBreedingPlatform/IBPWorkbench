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
package org.generationcp.ibpworkbench.navigation;

import java.util.Map;

import org.generationcp.commons.exceptions.InternationalizableException;
import org.generationcp.commons.vaadin.util.MessageNotifier;
import org.generationcp.ibpworkbench.actions.ActionListener;
import org.generationcp.ibpworkbench.comp.window.WorkbenchDashboardWindow;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.ui.UriFragmentUtility;
import com.vaadin.ui.UriFragmentUtility.FragmentChangedEvent;
import com.vaadin.ui.UriFragmentUtility.FragmentChangedListener;

/**
 * <b>Description</b>: Handles URI fragment changes for the application and calls mapped 
 * Listeners for the view. Browser back button, bookmark access and manual user input on 
 * the address bar events are handled by this listener.
 * 
 * <br>
 * <br>
 * 
 * <b>Author</b>: Michael Blancaflor
 * <br>
 * <b>File Created</b>: Jun 13, 2012.
 *
 * @see NavUriFragmentChangedEvent
 */
public class NavUriFragmentChangedListener implements FragmentChangedListener {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = -504911617538850779L;
    private static final Logger LOG = LoggerFactory.getLogger(NavUriFragmentChangedListener.class);
    
    /** The nav xml parser. */
    private NavXmlParser navXmlParser;
    
    /**
     * Instantiates a new nav uri fragment changed listener.
     */
    public NavUriFragmentChangedListener() throws InternationalizableException {
        navXmlParser = new NavXmlParser();
    }

    /**
     * Handles URI change event.
     *
     * @param source the source
     */
    @Override
    public void fragmentChanged(FragmentChangedEvent source) {
        UriFragmentUtility u = source.getUriFragmentUtility();
        CrumbTrail crumbTrail = ((WorkbenchDashboardWindow) u.getWindow()).getCrumbTrail();
        
        if(crumbTrail.isLinkAccessed()) {
            crumbTrail.setLinkAccessed(false);
        } else {

            navXmlParser.setUriFragment(u.getFragment());
            
            //TODO test throws InternationalizableException
            try {
                if (navXmlParser.validateUriFragment()) {
                   Map<String, String> xPathDetails = navXmlParser.getXpathDetails();
                   ActionListener listener = (ActionListener) Class.forName(xPathDetails.get("className")).getConstructor().newInstance();
                   listener.doAction(u.getWindow(), u.getFragment(), false);
                }
            } catch (Exception e) {
                LOG.error("Exception", e);
                InternationalizableException i = (InternationalizableException) e;
                MessageNotifier.showError(u.getWindow(), i.getCaption(), i.getDescription());
            }
        }
    }
    
}
