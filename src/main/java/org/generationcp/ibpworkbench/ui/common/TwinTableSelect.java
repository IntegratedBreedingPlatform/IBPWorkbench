package org.generationcp.ibpworkbench.ui.common;

import com.vaadin.data.Container;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.event.Action;
import com.vaadin.event.DataBoundTransferable;
import com.vaadin.event.dd.DragAndDropEvent;
import com.vaadin.event.dd.DropHandler;
import com.vaadin.event.dd.acceptcriteria.AcceptCriterion;
import com.vaadin.ui.AbstractSelect.AcceptItem;
import com.vaadin.ui.*;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Table.TableDragMode;
import org.generationcp.commons.vaadin.theme.Bootstrap;
import org.generationcp.middleware.pojos.BeanFormState;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class TwinTableSelect<T extends BeanFormState> extends GridLayout {
	
	private static final long serialVersionUID = 1L;
	
	private Object[] visibleColumns;
	private String[] columnHeaders;
	
	private Table tableLeft;
	private Table tableRight;
	
	private Label lblLeftColumnCaption;
	private Label lblRightColumnCaption;
	
	private Button btnLinkLeft;
	private Button btnLinkRight;
	
	private CheckBox chkSelectAllLeft;
	private CheckBox chkSelectAllRight;
	
	private Class<? super T> type;

	
	public TwinTableSelect(Class<? super T> class1) {
		
		super(2,3);
		
		this.type = class1;
		
		initializeComponents();
		initializeActions();
		initializeLayout();
	
		
	}
	
	private void initializeComponents(){
		
		this.setImmediate(true);
		this.setSizeFull();
		this.setSpacing(true);
		
		this.lblLeftColumnCaption = new Label();
		lblLeftColumnCaption.setSizeFull();
		this.lblRightColumnCaption = new Label();
		lblRightColumnCaption.setSizeFull();
		
		this.btnLinkLeft = new Button();
		btnLinkLeft.setStyleName("link");
		btnLinkLeft.setImmediate(true);
		this.btnLinkRight = new Button();
		btnLinkRight.setStyleName("link");
		btnLinkRight.setImmediate(true);
		
		this.chkSelectAllLeft = new CheckBox("Select All");
		this.chkSelectAllRight = new CheckBox("Select All");
		chkSelectAllLeft.setImmediate(true);
		chkSelectAllRight.setImmediate(true);
		
		
		this.tableLeft = (buildCustomTable());
		this.tableLeft.setData("left");
		this.tableRight = (buildCustomTable());
		this.tableRight.setData("right");
		
		this.setLeftContainerDataSource(new BeanItemContainer<T>(type));
		this.setRightContainerDataSource(new BeanItemContainer<T>(type));
	
	}
	
	private void initializeActions(){
		
		chkSelectAllLeft.addListener(new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				Table table = getTableLeft();
				
				Boolean val = (Boolean) ((CheckBox) event.getComponent()).getValue();
				for (Object itemId : getTableLeft().getItemIds()){
					((T)itemId).setActive(val);
					
					if (val && ((T)itemId).isEnabled())
						table.select(itemId);
					else
						table.unselect(itemId);
				}
				
				table.requestRepaint();
				table.refreshRowCache();
				
			}
		});
	
		
		chkSelectAllRight.addListener(new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				Table table = getTableRight();
				Boolean val = (Boolean) ((CheckBox) event.getComponent()).getValue();
				for (Object itemId : getTableRight().getItemIds()){
					((T)itemId).setActive(val);
					
					if (val && ((T)itemId).isEnabled())
						table.select(itemId);
					else
						table.unselect(itemId);
				}
				
				table.requestRepaint();
				table.refreshRowCache();
				
			}
		});
		
	}
	
	private void initializeLayout(){
		
		lblLeftColumnCaption.setStyleName(Bootstrap.Typography.H3.styleName());
		lblRightColumnCaption.setStyleName(Bootstrap.Typography.H3.styleName());
		
		this.addComponent(lblLeftColumnCaption, 0, 0);
		this.addComponent(lblRightColumnCaption, 1, 0);
		
		this.addComponent(getTableLeft(), 0, 1);
		this.addComponent(getTableRight(), 1, 1);
		
		
		HorizontalLayout hLayout1 = new HorizontalLayout();
		hLayout1.addComponent(chkSelectAllLeft);
		hLayout1.addComponent(btnLinkLeft);
		hLayout1.setComponentAlignment(btnLinkLeft, Alignment.TOP_RIGHT);
		hLayout1.setSizeFull();
		this.addComponent(hLayout1, 0, 2);
		
		HorizontalLayout hLayout2 = new HorizontalLayout();
		hLayout2.addComponent(chkSelectAllRight);
		hLayout2.addComponent(btnLinkRight);
		hLayout2.setComponentAlignment(btnLinkRight, Alignment.TOP_RIGHT);
		hLayout2.setSizeFull();
		this.addComponent(hLayout2, 1, 2);
		
		this.setColumnExpandRatio(0, 1.0f);
		this.setColumnExpandRatio(1, 1.0f);
		this.setRowExpandRatio(0, 1f);
		this.setRowExpandRatio(1, 10f);
		this.setRowExpandRatio(2, 1f);
		
		this.setStyleName("cell-style");
		
	}
	
	
	private void setLeftContainerDataSource(Container container){
		getTableLeft().setContainerDataSource(container);
		if (visibleColumns!=null)
			setVisibleColumns(this.visibleColumns);
		if (columnHeaders!=null)
			setColumnHeaders(this.columnHeaders);
	}
	
	private void setRightContainerDataSource(Container container){
		getTableRight().setContainerDataSource(container);
		if (visibleColumns!=null)
			setVisibleColumns(this.visibleColumns);
		if (columnHeaders!=null)
			setColumnHeaders(this.columnHeaders);
	}
	
	
	private Table buildCustomTable() {
		final Table table = new Table();
		final Property.ValueChangeListener tableValueChangeListener = new Property.ValueChangeListener() {

			private static final long serialVersionUID = 1L;

			@Override
			public void valueChange(ValueChangeEvent event) {
				
				Table source = ((Table) event.getProperty());
            	
				for (Object itemId : source.getItemIds()){
            		T bean = (T) itemId;
            		bean.setActive(false);
            	}
        
            	 Collection<T> sourceItemIds = (Collection<T>) source.getValue();
                 for (T itemId : sourceItemIds){
                	 if (itemId.isEnabled())
                		 itemId.setActive(true);
                 }
                
                 ((Table)event.getProperty()).requestRepaint();
                 ((Table)event.getProperty()).refreshRowCache();
                
				
			}
			
			
		};
		
		table.addListener(tableValueChangeListener);
		
		table.addGeneratedColumn("select", new Table.ColumnGenerator(){

			private static final long serialVersionUID = 1L;

			@SuppressWarnings("unchecked")
			@Override
			public Object generateCell(final Table source, final Object itemId,
					Object columnId) {
				
				BeanItemContainer<T> container = (BeanItemContainer<T>) source.getContainerDataSource();
				final T bean = container.getItem(itemId).getBean();
				
				final CheckBox checkBox = new CheckBox();
				checkBox.setImmediate(true);
				checkBox.setVisible(true);
				checkBox.addListener(new Button.ClickListener() {
					
					private static final long serialVersionUID = 1L;

					@Override
					public void buttonClick(ClickEvent event) {
						Boolean val = (Boolean) ((CheckBox) event.getComponent())
								.getValue();
						
						bean.setActive(val);
						if (val && bean.isEnabled())
							source.select(itemId);
						else {
							source.unselect(itemId);
							if (source.getData().equals("left")){
								chkSelectAllLeft.setValue(val);
							}else{
								chkSelectAllRight.setValue(val);
							}
						}
						
					}
				});
				
				checkBox.setEnabled(bean.isEnabled());

				if (bean.isActive() && bean.isEnabled()) {
					checkBox.setValue(true);
				} else {
					checkBox.setValue(false); 
					
				}
				
				return checkBox;
				
			}
        	
        });
		
		
		table.setDragMode(TableDragMode.MULTIROW);
		table.setDropHandler(new DropHandler() {
	        
			private static final long serialVersionUID = 1L;

			public void drop(DragAndDropEvent dropEvent) {
				
                DataBoundTransferable t = (DataBoundTransferable) dropEvent.getTransferable();
                
                if (t.getSourceComponent() == dropEvent.getTargetDetails().getTarget())
                    return;
                
                Table source =  ((Table)t.getSourceComponent());
                Table target =  ((Table)dropEvent.getTargetDetails().getTarget());
                
                Object itemIdOver = t.getItemId();
                
                //temporarily disable the value change listener to avoid conflict
                target.removeListener(tableValueChangeListener);
                
                
                Set<Object> sourceItemIds = (Set<Object>) source.getValue();
                for (Object itemId : sourceItemIds){
                	if (((T)itemId).isEnabled()){
                		source.removeItem(itemId);
                		target.addItem(itemId);
                	}
                }
                
                boolean selectedItemIsDisabled = false;
                if (sourceItemIds.size() == 1){
                	if (!((T)sourceItemIds.iterator().next()).isEnabled()){
                		selectedItemIsDisabled = true;
                	}
                }
                
                if (itemIdOver!=null && (sourceItemIds.size() <= 0 || selectedItemIsDisabled)) {
                	if (((T)itemIdOver).isEnabled()){
                	source.removeItem(itemIdOver);
                	target.addItem(itemIdOver); 
                	}
                }
                
                target.addListener(tableValueChangeListener);
                
        }
		

        public AcceptCriterion getAcceptCriterion() {
                return AcceptItem.ALL;
         }
		});
		
		final Action actionAddToProgramMembers = new Action("Add Selected Items");
		final Action actionRemoveFromProgramMembers = new Action("Remove Selected Items");
        final Action actionSelectAll = new Action("Select All");
        final Action actionDeSelectAll = new Action("De-select All");

        table.addActionHandler(new Action.Handler() {
            @Override
            public Action[] getActions(final Object target, final Object sender) {
            	
            	if (table.getData().toString().equals("left")){
            		return new Action[] {actionAddToProgramMembers, actionSelectAll, actionDeSelectAll};
            	}else{
            		return new Action[] {actionRemoveFromProgramMembers, actionSelectAll, actionDeSelectAll};
            	}
               
            }

            @Override
            public void handleAction(final Action action, final Object sender,
                    final Object target) {
                if (actionSelectAll == action) {
                	if (table.getData().toString().equals("left")){
                		chkSelectAllLeft.setValue(true);
                		chkSelectAllLeft.click();
                	}else{
                		chkSelectAllRight.setValue(true);
                		chkSelectAllRight.click();
                	}
                	
                } else if (actionDeSelectAll == action) {
                	if (table.getData().toString().equals("left")){
                		chkSelectAllLeft.setValue(false);
                		chkSelectAllLeft.click();
                	}else{
                		chkSelectAllRight.setValue(false);
                		chkSelectAllRight.click();
                	}
                } else if (actionAddToProgramMembers == action) {
                	addCheckedSelectedItems();
                } else if (actionRemoveFromProgramMembers == action) {
                	removeCheckedSelectedItems();
                }
               
            }

        });
		
	
		
		table.setColumnWidth("select", 20);
		table.setSelectable(true);
		table.setMultiSelect(true);
		table.setSizeFull();
		table.setNullSelectionAllowed(false);
		table.setImmediate(true);
		table.setReadOnly(false);
	
		
		return table;
	}
	
	public void setContainerDataSource(Container container){
		getTableLeft().setContainerDataSource(container);
		getTableRight().removeAllItems();
		if (visibleColumns!=null)
			setVisibleColumns(this.visibleColumns);
		if (columnHeaders!=null)
			setColumnHeaders(this.columnHeaders);
		chkSelectAllLeft.setValue(false);
		chkSelectAllRight.setValue(false);
	}
	
	public void setVisibleColumns(Object[] visibleColumns){
		 this.visibleColumns = visibleColumns;
		 getTableLeft().setVisibleColumns(visibleColumns);
    	 getTableRight().setVisibleColumns(visibleColumns);
	 }
	 
     public void setColumnHeaders(String[] columnHeaders){
    	 this.columnHeaders = columnHeaders;
    	 getTableLeft().setColumnHeaders(columnHeaders);
    	 getTableRight().setColumnHeaders(columnHeaders);
     }
     
     
     public void select(Object itemId){
    	 getTableLeft().removeItem(itemId);
    	 getTableRight().addItem(itemId);
     }
     
     public void unselect(Object itemId){
    	 getTableRight().removeItem(itemId);
    	 getTableLeft().addItem(itemId);
     }
     
     public void addItem(Object itemId){
    	 getTableLeft().addItem(itemId);
    	 select(itemId);
     }
     
     
     public Set<T> getValue(){
    	 Set ret = new HashSet<T>();
    	 for (Object itemId : getTableRight().getItemIds()){
    		 ret.add((T) itemId);
    	 }
    	 return (ret); 
     }
     
     public void setValue(Set<T> values){
    	 getTableRight().removeAllItems();
    	 for (T itemId : values){
    		 getTableRight().addItem(itemId);
    	 }
    	 
     }

	public Label getLeftColumnCaption() {
		return lblLeftColumnCaption;
	}

	public void setLeftColumnCaption(String leftColumnCaption) {
		lblLeftColumnCaption.setValue(leftColumnCaption);
	}
	

	public void setLeftLinkCaption(String caption) {
		btnLinkLeft.setCaption(caption);
	}
	
	public void addLeftLinkListener(Button.ClickListener listener) {
		btnLinkLeft.addListener(listener);
	}

	public Label getRightColumnCaption() {
		return lblRightColumnCaption;
	}

	public void setRightColumnCaption(String rightColumnCaption) {
		lblRightColumnCaption.setValue(rightColumnCaption);
	}
	
	public void setRightLinkCaption(String caption) {
		btnLinkRight.setCaption(caption);
	}
	
	public void addRightLinkListener(Button.ClickListener listener) {
		btnLinkRight.addListener(listener);
	}

	public Table getTableLeft() {
		return tableLeft;
	}

	public Table getTableRight() {
		return tableRight;
	}
	
	public void removeAllSelectedItems(){
		
		 for (Object itemId : getTableRight().getItemIds()){			 
	    	 getTableLeft().addItem(itemId);
    	 }
		 getTableRight().removeAllItems();
		
	}
	
	public void removeCheckedSelectedItems(){
		 
		 for (Object itemId : (Set<Object>) getTableRight().getValue()){	
			 if (((T)itemId).isActive() && ((T)itemId).isEnabled()){
				 ((T)itemId).setActive(false);
				 getTableLeft().addItem(itemId);
				 getTableRight().removeItem(itemId);
				
			 }
    	 }
		 
		 
	}
	
	public void addCheckedSelectedItems(){
		 
		 for (Object itemId : (Set<Object>) getTableLeft().getValue()){	
			 if (((T)itemId).isActive() && ((T)itemId).isEnabled()){
				 ((T)itemId).setActive(false);
				 getTableRight().addItem(itemId);
				 getTableLeft().removeItem(itemId);
				
			 }
   	 }
		 
		 
	}
	
	public void addAllToSelectedItems(){
		
		 for (Object itemId : getTableLeft().getItemIds()){
			 select(itemId);
		 }
		
	}

}