package org.generationcp.ibpworkbench.ui.gxe;

import com.vaadin.data.Property;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.HorizontalLayout;


public class GxeCheckBoxGroup extends HorizontalLayout {

	private Boolean value = true;
	private GxeTable table;
	private Property.ValueChangeListener listener;
	
	public GxeCheckBoxGroup(Integer cellType, Integer rowIndex, GxeTable table, Property.ValueChangeListener listener) {
		super();
		// TODO Auto-generated constructor stub
		this.table = table;
		this.listener = listener;
		
		if (rowIndex == 1){
			CheckBox cbRow = new CheckBox("All Rows");
			CheckBox cbCol = new CheckBox("All Columns");
			
			cbRow.setValue(true);
			cbCol.setValue(true);
			cbRow.addListener(new GxeCheckBoxGroupListener(null, table, cbRow));
			cbCol.addListener(new GxeCheckBoxGroupListener(null, table, cbCol));
			
			cbRow.setImmediate(true);
			cbCol.setImmediate(true);
			
			this.addComponent(cbRow);
			this.addComponent(cbCol);
			
		} else {
			CheckBox cb = new CheckBox(" ");
			cb.setValue(true);
			cb.addListener(new GxeCheckBoxGroupListener(null, table, cb));
			cb.addListener(listener);
			cb.setImmediate(true);
			value = true;
			this.addComponent(cb);
			
		}
		
		this.setImmediate(true);
			
	}
	
	public void refresh(){
		this.removeAllComponents();
		CheckBox cb = new CheckBox(" ");
		cb.setValue(value);
		cb.addListener(new GxeCheckBoxGroupListener(null, table, cb));
		cb.setImmediate(true);
		this.addComponent(cb);
		
	}
	
	public void setValue(Boolean val){
		this.value = val;
		this.refresh();
	}
	
	public Boolean getValue(){
		return this.value;
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 2449518272837130888L;

}