package org.generationcp.ibpworkbench.actions;

import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import org.generationcp.ibpworkbench.ui.ibtools.breedingview.select.SelectDetailsForBreedingViewPanel;


public class BreedingViewEnvFactorValueChangeListener implements ValueChangeListener{

    private static final long serialVersionUID = -6425208753343322313L;

    SelectDetailsForBreedingViewPanel source;
    
    public BreedingViewEnvFactorValueChangeListener(SelectDetailsForBreedingViewPanel source){
        this.source = source;
    }
    
    @Override
    public void valueChange(ValueChangeEvent event) {
        source.populateChoicesForEnvForAnalysis();
    }

}
