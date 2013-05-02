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
import org.generationcp.middleware.pojos.User;
import org.generationcp.middleware.pojos.workbench.Project;
import org.generationcp.middleware.pojos.workbench.ProjectUserRole;
import org.generationcp.middleware.pojos.workbench.Role;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.SQLQuery;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

/**
 * The Class ProjectUserDAO.
 * 
 * @author Joyce Avestro
 * 
 */
public class ProjectUserRoleDAO extends GenericDAO<ProjectUserRole, Integer>{

    /* (non-Javadoc)
     * @see org.generationcp.middleware.dao.GenericDAO#saveOrUpdate(java.lang.Object)
     */
    /**
     * Save or update.
     *
     * @param projectUser the project user
     * @return the project user
     * @throws MiddlewareQueryException 
     */
    public ProjectUserRole saveOrUpdate(ProjectUserRole projectUser) throws MiddlewareQueryException {

        if (projectUser.getProject() == null || projectUser.getProject().getProjectId() == null) {
            throw new IllegalArgumentException("Project cannot be null");
        }
        if (projectUser.getUserId() == null) {
            throw new IllegalArgumentException("User cannot be null");
        }

        return super.saveOrUpdate(projectUser);
    }

    /**
     * Gets the ProjectUser by id.
     *
     * @param id the ProjectUser id
     * @return the associated ProjectUser
     * @throws MiddlewareQueryException 
     */
    public ProjectUserRole getById(Integer id) throws MiddlewareQueryException {
        return super.getById(id, false);
    }

    /**
     * Gets the ProjectUser by project and user.
     *
     * @param project the project
     * @param user the user
     * @return the ProjectUser associated to the given project and user
     */
    @SuppressWarnings("rawtypes")
    public ProjectUserRole getByProjectAndUser(Project project, User user) throws MiddlewareQueryException {
        try {
            List<Criterion> criteria = new ArrayList<Criterion>();
            criteria.add(Restrictions.eq("project", project));
            criteria.add(Restrictions.eq("userId", user.getUserid()));
            List results = super.getByCriteria(criteria);
            return (ProjectUserRole) results.get(0);
        } catch (HibernateException e) {
            throw new MiddlewareQueryException("Error in getByProjectAndUser(project=" + project + ", user=" + user
                    + ") query from ProjectUser: " + e.getMessage(), e);
        }
    }

    /**
     * Gets the ProjectUser by project and user.
     *
     * @param project the project
     * @param user the user
     * @return the ProjectUser associated to the given project and user
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public List<ProjectUserRole> getByProject(Project project) throws MiddlewareQueryException {
        try {
            List<Criterion> criteria = new ArrayList<Criterion>();
            criteria.add(Restrictions.eq("project", project));
            List results = super.getByCriteria(criteria);
            return (List<ProjectUserRole>) results;
        } catch (HibernateException e) {
            throw new MiddlewareQueryException("Error in getByProject(project=" + project + ") query from ProjectUser: " + e.getMessage(),
                    e);
        }
    }

    /**
     * Return a List of {@link User} records associated with a {@link Project}
     *
     * @param projectId - the project id
     * @return the List of {@link User} records
     * @throws MiddlewareQueryException the MiddlewareQueryException
     */
    @SuppressWarnings("unchecked")
    public List<User> getUsersByProjectId(Long projectId) throws MiddlewareQueryException {
        try {

            SQLQuery query = getSession().createSQLQuery(ProjectUserRole.GET_USERS_BY_PROJECT_ID);
            query.setParameter("projectId", projectId);
            List<User> users = new ArrayList<User>();

            List<Object> results = query.list();
            for (Object o : results) {
                Object[] user = (Object[]) o;
                Integer userId = (Integer) user[0];
                Integer instalId = (Integer) user[1];
                Integer uStatus = (Integer) user[2];
                Integer uAccess = (Integer) user[3];
                Integer uType = (Integer) user[4];
                String uName = (String) user[5];
                String upswd = (String) user[6];
                Integer personId = (Integer) user[7];
                Integer aDate = (Integer) user[8];
                Integer cDate = (Integer) user[9];
                User u = new User(userId, instalId, uStatus, uAccess, uType, uName, upswd, personId, aDate, cDate);
                users.add(u);
            }

            return users;
        } catch (HibernateException e) {
            throw new MiddlewareQueryException("Error in getUsersByProjectId(projectId=" + projectId + ") query from ProjectUser: "
                    + e.getMessage(), e);
        }
    }

    /**
     * Returns the number of {@link User} records associated with a {@link Project}
     *
     * @param projectId - the project id
     * @return the number of {@link User} records
     * @throws MiddlewareQueryException the MiddlewareQueryException
     */
    public long countUsersByProjectId(Long projectId) throws MiddlewareQueryException {
        try {
            SQLQuery query = getSession().createSQLQuery(ProjectUserRole.COUNT_USERS_BY_PROJECT_ID);
            query.setParameter("projectId", projectId);
            return ((BigInteger) query.uniqueResult()).longValue();
        } catch (HibernateException e) {
            throw new MiddlewareQueryException("Error in countUsersByProjectId(projectId=" + projectId + ") query from ProjectUser: "
                    + e.getMessage(), e);
        }
    }
    
    @SuppressWarnings("unchecked")
    public List<Role> getRolesByProjectAndUser(Project project, User user) throws MiddlewareQueryException{
        try{
            Criteria criteria = getSession().createCriteria(ProjectUserRole.class);
            criteria.add(Restrictions.eq("project", project));
            criteria.add(Restrictions.eq("userId", user.getUserid()));
            List<ProjectUserRole> projectUsers = criteria.list();
            
            List<Role> roles = new ArrayList<Role>();
            for (ProjectUserRole projectUser : projectUsers){
                roles.add(projectUser.getRole());
            }
            return roles;
        } catch (HibernateException e) {
            throw new MiddlewareQueryException("Error in getByProjectAndUser(project=" + project + ", user=" + user
                + ") query from Role: " + e.getMessage(), e);
        }

    }
    
    @SuppressWarnings("unchecked")
    public List<Project> getProjectsByUser(User user) throws MiddlewareQueryException {
        try {
            Criteria criteria = getSession().createCriteria(ProjectUserRole.class);
            criteria.add(Restrictions.eq("userId", user.getUserid()));
            criteria.setProjection(Projections.distinct(Projections.property("project")));
            return criteria.list();
        } catch (HibernateException e) {
            throw new MiddlewareQueryException("Error in getProjectsByUser(user=" + user
                + ") query from Project: " + e.getMessage(), e);
        }
    }
}
