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

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.pojos.Method;
import org.generationcp.middleware.pojos.workbench.Project;
import org.generationcp.middleware.pojos.workbench.ProjectMethod;
import org.hibernate.HibernateException;
import org.hibernate.SQLQuery;

/**
 * The Class ProjectMethodDAO.
 * 
 * @author Joyce Avestro
 * 
 */
public class ProjectMethodDAO extends GenericDAO<ProjectMethod, Integer>{

    /**
     * Returns a list of {@link Method} records by project id.
     *
     * @param projectId the project id
     * @param start the first row to retrieve
     * @param numOfRows the number of rows to retrieve
     * @return the list of {@link Method}s
     * @throws MiddlewareQueryException the MiddlewareQueryException
     */
    @SuppressWarnings("unchecked")
    public List<Integer> getByProjectId(Long projectId, int start, int numOfRows) throws MiddlewareQueryException {

        if (projectId == null) {
            return new ArrayList<Integer>();
        }

        try {
            SQLQuery query = getSession().createSQLQuery(ProjectMethod.GET_METHODS_BY_PROJECT_ID);
            query.setParameter("projectId", projectId.intValue());
            query.setFirstResult(start);
            query.setMaxResults(numOfRows);
            return (List<Integer>) query.list();
        } catch (HibernateException e) {
            logAndThrowException("Error with getByProjectId(projectId=" + projectId + ") query from ProjectMethod: "
                    + e.getMessage(), e);
        }
        return new ArrayList<Integer>();
    }

    /**
     * Returns the number of {@link Method} records by project id.
     *
     * @param projectId the project id
     * @return the number of {@link Method} records
     * @throws MiddlewareQueryException the MiddlewareQueryException
     */
    public long countByProjectId(Long projectId) throws MiddlewareQueryException {
        try {
            SQLQuery query = getSession().createSQLQuery(ProjectMethod.COUNT_METHODS_BY_PROJECT_ID);
            query.setParameter("projectId", projectId.intValue());
            BigInteger result = (BigInteger) query.uniqueResult();
            return result.longValue();
        } catch (HibernateException e) {
            logAndThrowException("Error with countByProjectId(projectId=" + projectId + ") query from ProjectMethod: "
                    + e.getMessage(), e);
        }
        return 0;
    }

    @SuppressWarnings("rawtypes")
    public List<ProjectMethod> getProjectMethodByProject(Project project, int start, int numOfRows) throws MiddlewareQueryException {
        List<ProjectMethod> toReturn = new ArrayList<ProjectMethod>();
        if (project == null || project.getProjectId() == null) {
            return toReturn;
        }

        try {
            SQLQuery query = getSession().createSQLQuery(ProjectMethod.GET_PROJECT_METHODS_BY_PROJECT_ID);
            query.setParameter("projectId", project.getProjectId().intValue());
            query.setFirstResult(start);
            query.setMaxResults(numOfRows);
            List results = query.list();
            for (Object o : results) {
                Object[] result = (Object[]) o;
                if (result != null) {
                    Integer projectMethodId = (Integer) result[0];
                    Integer methodId = (Integer) result[2];
                    ProjectMethod projectMethod = new ProjectMethod(projectMethodId, project, methodId);
                    toReturn.add(projectMethod);
                }
            }
        } catch (HibernateException e) {
            logAndThrowException("Error with getProjectMethodByProjectId(project=" + project
                    + ") query from ProjectMethod: " + e.getMessage(), e);
        }
        return toReturn;
    }

}
