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

import java.util.ArrayList;
import java.util.List;

import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.pojos.Method;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.LogicalExpression;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

/**
 * DAO class for {@link Method}.
 * 
 */
public class MethodDAO extends GenericDAO<Method, Integer>{

    @SuppressWarnings("unchecked")
    public List<Method> getAllMethod() throws MiddlewareQueryException {
        try {
            Query query = getSession().getNamedQuery(Method.GET_ALL);
            return (List<Method>) query.list();
        } catch (HibernateException e) {
            logAndThrowException("Error with getAllMethod() query from Method: " + e.getMessage(), e);
        }
        return new ArrayList<Method>();
    }

    @SuppressWarnings("unchecked")
    public List<Method> getByType(String type) throws MiddlewareQueryException {
        try {
            Criteria criteria = getSession().createCriteria(Method.class);
            criteria.add(Restrictions.eq("mtype", type));
            criteria.addOrder(Order.asc("mname"));
            return criteria.list();
        } catch (HibernateException e) {
            logAndThrowException("Error with getMethodsByType(type=" + type + ") query from Method: " + e.getMessage(), e);
        }
        return new ArrayList<Method>();
    }


    @SuppressWarnings("unchecked")
    public List<Method> getByType(String type, int start, int numOfRows) throws MiddlewareQueryException {
        try {
            Criteria criteria = getSession().createCriteria(Method.class);
            criteria.add(Restrictions.eq("mtype", type));
            criteria.addOrder(Order.asc("mname"));
            criteria.setFirstResult(start);
            criteria.setMaxResults(numOfRows);            
            return criteria.list();
        } catch (HibernateException e) {
            logAndThrowException("Error with getMethodsByType(type=" + type + ") query from Method: " + e.getMessage(), e);
        }
        return new ArrayList<Method>();
    }

    public long countByType(String type) throws MiddlewareQueryException {
        try {
            Criteria criteria = getSession().createCriteria(Method.class);
            criteria.add(Restrictions.eq("mtype", type));
            criteria.setProjection(Projections.rowCount());
            return ((Long) criteria.uniqueResult()).longValue(); // count
        } catch (HibernateException e) {
            logAndThrowException("Error with countMethodsByType(type=" + type + ") query from Method: " + e.getMessage(), e);
        }
        return 0;
    }

    @SuppressWarnings("unchecked")
    public List<Method> getByGroup(String group) throws MiddlewareQueryException {
        try {
            Criteria criteria = getSession().createCriteria(Method.class);
            criteria.add(Restrictions.eq("mgrp", group));
            criteria.addOrder(Order.asc("mname"));
            return criteria.list();
        } catch (HibernateException e) {
            logAndThrowException("Error with getMethodsByGroup(group=" + group + ") query from Method: " + e.getMessage(), e);
        }
        return new ArrayList<Method>();
    }
    
    
    @SuppressWarnings("unchecked")
    public List<Method> getByGroupIncludesGgroup(String group) throws MiddlewareQueryException {
        try {
            Criteria criteria = getSession().createCriteria(Method.class);
            Criterion group1=Restrictions.eq("mgrp", group);
            Criterion group2=Restrictions.eq("mgrp", "G");
            LogicalExpression orExp=Restrictions.or(group1, group2);
            criteria.add(orExp);
            criteria.addOrder(Order.asc("mname"));
            return criteria.list();
        } catch (HibernateException e) {
            logAndThrowException("Error with getMethodsByGroup(group=" + group + ") query from Method: " + e.getMessage(), e);
        }
        return new ArrayList<Method>();
    }

    @SuppressWarnings("unchecked")
    public List<Method> getByGroup(String group, int start, int numOfRows) throws MiddlewareQueryException {
        try {
            Criteria criteria = getSession().createCriteria(Method.class);
            criteria.add(Restrictions.eq("mgrp", group));
            criteria.addOrder(Order.asc("mname"));
            criteria.setFirstResult(start);
            criteria.setMaxResults(numOfRows);            
            return criteria.list();
        } catch (HibernateException e) {
            logAndThrowException("Error with getMethodsByGroup(group=" + group + ") query from Method: " + e.getMessage(), e);
        }
        return new ArrayList<Method>();
    }
    
    
    @SuppressWarnings("unchecked")
    public List<Method> getByGroupAndType(String group,String type) throws MiddlewareQueryException {
        try {
           
            Criteria criteria = getSession().createCriteria(Method.class);
            Criterion group1=Restrictions.eq("mgrp", group);
            Criterion group2=Restrictions.eq("mgrp", "G");
            LogicalExpression orExp=Restrictions.or(group1, group2);
            Criterion filterType=Restrictions.eq("mtype", type);
            LogicalExpression andExp=Restrictions.and(orExp,filterType );
            
            criteria.add(andExp);
            criteria.addOrder(Order.asc("mname"));
            return criteria.list();
        } catch (HibernateException e) {
            logAndThrowException("Error with getMethodsByGroupAndType(group=" + group + " and "+type+") query from Method: " + e.getMessage(), e);
        }
        return new ArrayList<Method>();
    }
    
    @SuppressWarnings("unchecked")
    public List<Method> getByGroupAndTypeAndName(String group,String type, String name) throws MiddlewareQueryException {
        try {
           
            Criteria criteria = getSession().createCriteria(Method.class);
            Criterion group1=Restrictions.eq("mgrp", group);
            Criterion group2=Restrictions.eq("mgrp", "G");
            LogicalExpression orExp=Restrictions.or(group1, group2);
            criteria.add(Restrictions.eq("mtype", type));
            criteria.add(Restrictions.eq("mname", name));
            criteria.add(orExp);
            criteria.addOrder(Order.asc("mname"));
            return criteria.list();
        } catch (HibernateException e) {
            logAndThrowException("Error with getMethodsByGroupAndType(group=" + group + " and "+type+") query from Method: " + e.getMessage(), e);
        }
        return new ArrayList<Method>();
    }
    
    
    
    public long countByGroup(String group) throws MiddlewareQueryException {
        try {
            Criteria criteria = getSession().createCriteria(Method.class);
            criteria.add(Restrictions.eq("mgrp", group));
            criteria.setProjection(Projections.rowCount());
            return ((Long) criteria.uniqueResult()).longValue(); // count
        } catch (HibernateException e) {
            logAndThrowException("Error with countMethodsByGroup(group=" + group + ") query from Method: " + e.getMessage(), e);
        }
        return 0;
    }    
}
