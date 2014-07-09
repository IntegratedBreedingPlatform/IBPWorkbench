package org.generationcp.ibpworkbench.ui.breedingview.singlesiteanalysis;

import com.vaadin.ui.Tree;
import com.vaadin.ui.Tree.ExpandEvent;
import com.vaadin.ui.TreeTable;
import org.generationcp.commons.exceptions.InternationalizableException;
import org.generationcp.commons.vaadin.util.MessageNotifier;
import org.generationcp.middleware.domain.dms.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StudyTreeExpandAction implements Tree.ExpandListener{
    
    private static final Logger LOG = LoggerFactory.getLogger(StudyTreeExpandAction.class);
    private static final long serialVersionUID = -5091664285613837786L;

    private SelectDatasetDialog source;
    private TreeTable tr;
    
    public StudyTreeExpandAction(SelectDatasetDialog source, TreeTable tr) {
        this.source = source;
        this.tr = tr;
    }

    @Override
    public void nodeExpand(ExpandEvent event) {
       
            try {
            	((SelectDatasetDialog) source).queryChildrenStudies((Reference)event.getItemId(), tr);
            	
            } catch (InternationalizableException e) {
                LOG.error(e.toString() + "\n" + e.getStackTrace());
                e.printStackTrace();
                MessageNotifier.showError(event.getComponent().getWindow(), e.getCaption(), e.getDescription());
            }
            
            try{
            	((SelectDatasetDialog) source).queryChildrenDatasets((Reference)event.getItemId(), tr);
            }catch(Exception e){
            	LOG.error(e.toString() + "\n" + e.getStackTrace());
                e.printStackTrace();
                
            }
    }
    
    

}