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

import java.io.Serializable;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * POJO for workbench_project_method table.
 *  
 *  @author Joyce Avestro
 *  
 */
@Entity
@Table(name = "workbench_project_method")
public class ProjectMethod implements Serializable{

    private static final long serialVersionUID = 1L;
    
    /** Used by ProjectMethodDAO.getMethodsByProjectId() */
    public static final String GET_METHODS_BY_PROJECT_ID = 
            "SELECT method_id " + 
            "FROM workbench_project_method " +
            "WHERE project_id = :projectId";
    
    /** Used by ProjectMethodDAO.countMethodsByProjectId() */
    public static final String COUNT_METHODS_BY_PROJECT_ID = 
            "SELECT COUNT(method_id) " + 
            "FROM workbench_project_method " +
            "WHERE project_id = :projectId";
    
    /** Used by ProjectMethodDAO.getProjectMethodsByProjectId() */
    public static final String GET_PROJECT_METHODS_BY_PROJECT_ID = 
            "SELECT * " + 
            "FROM workbench_project_method " +
            "WHERE project_id = :projectId";
    
    @Id
    @Basic(optional = false)
    @GeneratedValue
    @Column(name = "project_method_id")
    private Integer projectMethodId;

    @OneToOne(optional = false)
    @JoinColumn(name = "project_id")
    private Project project;

    @Column(name = "method_id")
    private Integer methodId;

    public ProjectMethod() {
        super();
    }
    
    public ProjectMethod(Integer projectMethodId, Project project, Integer methodId) {
        super();
        this.projectMethodId = projectMethodId;
        this.project = project;
        this.methodId = methodId;
    }

    public Integer getProjectMethodId() {
        return projectMethodId;
    }
    
    public void setProjectMethodId(Integer projectMethodId) {
        this.projectMethodId = projectMethodId;
    }
    
    public Project getProject() {
        return project;
    }
    
    public void setProject(Project project) {
        this.project = project;
    }
    
    public Integer getMethodId() {
        return methodId;
    }

    public void setMethodId(Integer methodId) {
        this.methodId = methodId;
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(projectMethodId).hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj == this) {
            return true;
        }
        if (!ProjectMethod.class.isInstance(obj)) {
            return false;
        }

        ProjectMethod otherObj = (ProjectMethod) obj;

        return new EqualsBuilder().append(projectMethodId, otherObj.projectMethodId).isEquals();
    }
    
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("ProjectMethod [projectMethodId=");
        builder.append(projectMethodId);
        builder.append(", project=");
        builder.append(project);
        builder.append(", methodId=");
        builder.append(methodId);
        builder.append("]");
        return builder.toString();
    }


}
