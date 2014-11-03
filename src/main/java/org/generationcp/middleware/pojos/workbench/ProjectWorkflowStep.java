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
package org.generationcp.middleware.pojos.workbench;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * POJO for workbench_project_workflow_step table.
 *  
 */
@Entity
@Table(name = "workbench_project_workflow_step")
public class ProjectWorkflowStep implements Serializable{

    private static final long serialVersionUID = 1L;

    @Id
    @Basic(optional = false)
    @GeneratedValue
    @Column(name = "project_workflow_step_id")
    private Long projectWorkflowStepId;

    @OneToOne(optional = false)
    @JoinColumn(name = "step_id")
    private WorkflowStep step;

    @OneToOne(optional = false)
    @JoinColumn(name = "contact_id")
    private Contact owner;

    @Basic(optional = false)
    @Column(name = "due_date")
    private Date dueDate;

    @Basic(optional = false)
    @Column(name = "status")
    private String status;

    public Long getProjectWorkflowStepId() {
        return projectWorkflowStepId;
    }

    public void setProjectWorkflowStepId(Long projectWorkflowStepId) {
        this.projectWorkflowStepId = projectWorkflowStepId;
    }

    public WorkflowStep getStep() {
        return step;
    }

    public void setStep(WorkflowStep step) {
        this.step = step;
    }

    public Contact getOwner() {
        return owner;
    }

    public void setOwner(Contact owner) {
        this.owner = owner;
    }

    public Date getDueDate() {
        return dueDate;
    }

    public void setDueDate(Date dueDate) {
        this.dueDate = dueDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(projectWorkflowStepId).hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj == this) {
            return true;
        }
        if (!ProjectWorkflowStep.class.isInstance(obj)) {
            return false;
        }

        ProjectWorkflowStep otherObj = (ProjectWorkflowStep) obj;

        return new EqualsBuilder().append(projectWorkflowStepId, otherObj.projectWorkflowStepId).isEquals();
    }
    
    @Override
    public String toString() {
        return "ProjectWorkflowStep [step=" + step +
                ", owner=" + owner +
                ", dueDate=" + dueDate +
                ", status=" + status + "]";
    }

}
