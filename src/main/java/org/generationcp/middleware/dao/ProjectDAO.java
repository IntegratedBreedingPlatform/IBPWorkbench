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

package org.generationcp.middleware.dao;

import java.util.List;

import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.pojos.workbench.Project;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.SQLQuery;
import org.hibernate.criterion.Restrictions;

public class ProjectDAO extends GenericDAO<Project, Long>{

    public Project getById(Long projectId) throws MiddlewareQueryException {
        try {
            Criteria criteria = getSession().createCriteria(Project.class).add(Restrictions.eq("projectId", projectId)).setMaxResults(1);
            return (Project) criteria.uniqueResult();
        } catch (HibernateException e) {
            logAndThrowException("Error with getById(projectId=" + projectId + ") query from Project: " + e.getMessage(), e);
        }
        return null;
    }

    public Project getByName(String projectName) throws MiddlewareQueryException {
        try {
            Criteria criteria = getSession().createCriteria(Project.class).add(Restrictions.eq("projectName", projectName)).setMaxResults(1);
            return (Project) criteria.uniqueResult();
        } catch (HibernateException e) {
            logAndThrowException("Error with getByName(projectName=" + projectName + ") query from Project: " + e.getMessage(), e);
        }
        return null;
    }

    public Project getLastOpenedProject(Integer userId) throws MiddlewareQueryException {
        try {
            StringBuilder sb = new StringBuilder();
            sb.append("SELECT {w.*} FROM workbench_project w ").append("INNER JOIN workbench_project_user_info r ON w.project_id = r.project_id ")
                    .append("WHERE r.user_id = :userId AND r.last_open_date IS NOT NULL ORDER BY r.last_open_date DESC LIMIT 1 ;");

            SQLQuery query = getSession().createSQLQuery(sb.toString());
            query.addEntity("w", Project.class);
            query.setParameter("userId", userId);

            @SuppressWarnings("unchecked")
            List<Project> projectList = query.list();

            return projectList.size() > 0 ? projectList.get(0) : null;
        } catch (HibernateException e) {
            logAndThrowException("Error with getLastOpenedProject(userId=" + userId + ") query from Project "
                    + e.getMessage(), e);
        }
        return null;
    }
}
