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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.pojos.Factor;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.criterion.Restrictions;

public class FactorDAO extends GenericDAO<Factor, Integer>{
    
    public static final String NUMERIC_DATATYPE = "N";
    public static final String CHARACTER_DATATYPE = "C";

    @SuppressWarnings("unchecked")
    public Set<Integer> getGIDSByObservationUnitIds(Set<Integer> ounitIds, int start, int numOfRows) throws MiddlewareQueryException {
        Set<Integer> results = new HashSet<Integer>();
        try {
            SQLQuery levelNQuery = getSession().createSQLQuery(Factor.GET_GID_FROM_NUMERIC_LEVELS_GIVEN_OBSERVATION_UNIT_IDS);
            levelNQuery.setParameterList("ounitids", ounitIds);
            levelNQuery.setFirstResult(start);
            levelNQuery.setMaxResults(numOfRows);

            List<Double> gids1 = levelNQuery.list();
            for (Double gid : gids1) {
                results.add(gid.intValue());
            }

            SQLQuery levelCQuery = getSession().createSQLQuery(Factor.GET_GID_FROM_CHARACTER_LEVELS_GIVEN_OBSERVATION_UNIT_IDS);
            levelCQuery.setParameterList("ounitids", ounitIds);
            levelCQuery.setFirstResult(start);
            levelCQuery.setMaxResults(numOfRows);

            List<String> gids2 = levelCQuery.list();
            for (String gid : gids2) {
                results.add(Integer.parseInt(gid));
            }

        } catch (HibernateException e) {
            logAndThrowException(
                    "Error with getGIDSByObservationUnitIds(ounitIds=" + ounitIds + ") query from Factor: " + e.getMessage(), e);
        }
        return results;
    }

    @SuppressWarnings("unchecked")
    public List<Factor> getByStudyID(Integer studyId) throws MiddlewareQueryException {
        try {
            Query query = getSession().getNamedQuery(Factor.GET_FACTORS_BY_STUDYID);
            query.setParameter("studyId", studyId);

            List<Factor> results = query.list();
            return results;
        } catch (HibernateException e) {
            logAndThrowException("Error with getByStudyID(studyId=" + studyId + ") query from Factor: "
                    + e.getMessage(), e);
        }
        return new ArrayList<Factor>();
    }

    @SuppressWarnings("unchecked")
    public List<Factor> getByRepresentationID(Integer representationId) throws MiddlewareQueryException {
        try {
            SQLQuery query = getSession().createSQLQuery(Factor.GET_BY_REPRESENTATION_ID);
            query.setParameter("representationId", representationId);
            query.addEntity("f", Factor.class);

            List<Factor> results = query.list();
            return results;
        } catch (HibernateException e) {
            logAndThrowException("Error with getByRepresentationID(representationId=" + representationId
                    + ") query from Factor: " + e.getMessage(), e);
        }
        return new ArrayList<Factor>();
    }

    @SuppressWarnings("unchecked")
    public String getMainLabel(Integer factorid) throws MiddlewareQueryException {
        try {
            Criteria criteria = getSession().createCriteria(getPersistentClass());
            criteria.add(Restrictions.eq("id", factorid));
            criteria.add(Restrictions.eq("factorId", factorid));

            List<Factor> results = criteria.list();
            if (!results.isEmpty()) {
                Factor factor = results.get(0);
                return factor.getName();
            }
        } catch (HibernateException e) {
            logAndThrowException("Error with getMainLabel(factorId=" + factorid + ") query from Factor: "
                    + e.getMessage(), e);
        }
        return null;
    }
    
    @SuppressWarnings("unchecked")
    public Factor getFactorOfDatasetGivenTraitid(Integer representationId, Integer traitid) throws MiddlewareQueryException {
        try {
            SQLQuery query = getSession().createSQLQuery(Factor.GET_FACTOR_OF_DATASET_GIVEN_TRAITID);
            query.setParameter("representationId", representationId);
            query.setParameter("traitid", traitid);
            query.addEntity("f", Factor.class);
            
            List<Factor> results = query.list();
            if(!results.isEmpty()){
                Factor factor = results.get(0);
                return factor;
            }
        } catch (HibernateException e) {
            logAndThrowException("Error with getFactorOfDatasetGivenTid(representationId=" + representationId
                    + ", traitid = " + traitid + ") query from Factor: " + e.getMessage(), e);
        }
        return null;
    }
    
    public boolean isLabelNumeric(int labelId) throws MiddlewareQueryException {
        try {
            Query query = getSession().createSQLQuery(Factor.GET_LABEL_ID_DATATYPE);
            query.setParameter("labelid", labelId);
            
            String result = "";
            
            if (!query.list().isEmpty()) 
                result = (String) query.list().get(0);
            else 
                throw new HibernateException("Database Error: No Datatype assigned on the label id: " + labelId);
            
            if (result.equals(NUMERIC_DATATYPE)) 
                return true;
            else if (result.equals(CHARACTER_DATATYPE))
               return false;
            else
                throw new HibernateException("Database Error: No Datatype assigned on the label id: " + labelId);
        } catch (HibernateException e) {
            logAndThrowException("Error with isLabelNumeric: " + e.getMessage(),
                    e);
        }
        return false;
    }
}
