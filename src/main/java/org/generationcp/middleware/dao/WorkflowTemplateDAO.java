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

import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.pojos.workbench.WorkflowTemplate;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.criterion.Restrictions;

import java.util.ArrayList;
import java.util.List;

/**
 * DAO class for {@link WorkflowTemplate}.
 * 
 */
public class WorkflowTemplateDAO extends GenericDAO<WorkflowTemplate, Long>{

    @SuppressWarnings("unchecked")
    public List<WorkflowTemplate> getByName(String name) throws MiddlewareQueryException{
        try{
			if (name != null) {
				Criteria criteria = getSession().createCriteria(WorkflowTemplate.class);
				criteria.add(Restrictions.eq("name", name));
				return (List<WorkflowTemplate>) criteria.list();
			}
        } catch (HibernateException e) {
            logAndThrowException("Error in getByName(name=" + name 
                + ") query from WorkflowTemplate: " + e.getMessage(), e);
        }
        return new ArrayList<WorkflowTemplate>();
    }
}
