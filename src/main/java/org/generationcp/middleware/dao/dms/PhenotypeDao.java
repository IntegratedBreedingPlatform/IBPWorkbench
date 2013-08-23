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
package org.generationcp.middleware.dao.dms;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.generationcp.middleware.dao.GenericDAO;
import org.generationcp.middleware.domain.h2h.CategoricalTraitInfo;
import org.generationcp.middleware.domain.h2h.CategoricalValue;
import org.generationcp.middleware.domain.h2h.CharacterTraitInfo;
import org.generationcp.middleware.domain.h2h.NumericTraitInfo;
import org.generationcp.middleware.domain.h2h.TraitInfo;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.pojos.dms.Phenotype;
import org.hibernate.HibernateException;
import org.hibernate.SQLQuery;

/**
 * DAO class for {@link Phenotype}.
 * 
 */
@SuppressWarnings("unchecked")
public class PhenotypeDao extends GenericDAO<Phenotype, Integer> {
    
    public List<NumericTraitInfo>  getNumericTraitInfoList(List<Integer> environmentIds, List<Integer> numericVariableIds) throws MiddlewareQueryException{
        List<NumericTraitInfo> numericTraitInfoList = new ArrayList<NumericTraitInfo>();
        try {
            SQLQuery query = getSession().createSQLQuery(
                    "SELECT p.observable_id, "
                    + "COUNT(DISTINCT e.nd_geolocation_id) AS location_count, "
                    + "COUNT(DISTINCT es.stock_id) AS germplasm_count, "
                    + "COUNT(DISTINCT e.nd_experiment_id) AS observation_count , "
                    + "MIN(p.value * 1) AS min_value, "
                    + "MAX(p.value * 1) AS max_value "
                    + "FROM phenotype p "
                    + "    INNER JOIN nd_experiment_phenotype eph ON eph.phenotype_id = p.phenotype_id "
                    + "    INNER JOIN nd_experiment e ON e.nd_experiment_id = eph.nd_experiment_id "
                    + "    INNER JOIN nd_experiment_stock es ON es.nd_experiment_id = e.nd_experiment_id "
                    + "WHERE e.nd_geolocation_id IN (:environmentIds) "
                    + "    AND p.observable_id IN (:numericVariableIds) "
                    + "GROUP by p.observable_id ");
            query.setParameterList("environmentIds", environmentIds);
            query.setParameterList("numericVariableIds", numericVariableIds);
            
            List<Object[]> list =  query.list();
              
            for (Object[] row : list){
                Integer id = (Integer) row[0]; 
                Long locationCount = ((BigInteger) row [1]).longValue();
                Long germplasmCount =((BigInteger) row [2]).longValue();
                Long observationCount = ((BigInteger)  row [3]).longValue();
                Double minValue = (Double) row[4];
                Double maxValue = (Double) row[5];
                  
                numericTraitInfoList.add(new NumericTraitInfo(null, id, null, locationCount, germplasmCount, observationCount, minValue, maxValue, 0));
              }
            
        } catch(HibernateException e) {
            logAndThrowException("Error at getNumericTraitInfoList() query on PhenotypeDao: " + e.getMessage(), e);
        }

        return numericTraitInfoList;

    }
    
    
    public List<TraitInfo>  getTraitInfoCounts(List<Integer> environmentIds, List<Integer> variableIds) throws MiddlewareQueryException{
        List<TraitInfo> traitInfoList = new ArrayList<TraitInfo>();
        try {
            SQLQuery query = getSession().createSQLQuery(
                    "SELECT p.observable_id, "
                    + "COUNT(DISTINCT e.nd_geolocation_id) AS location_count, "
                    + "COUNT(DISTINCT es.stock_id) AS germplasm_count, "
                    + "COUNT(DISTINCT e.nd_experiment_id) AS observation_count "
                    + "FROM phenotype p "
                    + "    INNER JOIN nd_experiment_phenotype eph ON eph.phenotype_id = p.phenotype_id "
                    + "    INNER JOIN nd_experiment e ON e.nd_experiment_id = eph.nd_experiment_id "
                    + "    INNER JOIN nd_experiment_stock es ON es.nd_experiment_id = e.nd_experiment_id "
                    + "WHERE e.nd_geolocation_id IN (:environmentIds) "
                    + "    AND p.observable_id IN (:variableIds) "
                    + "GROUP by p.observable_id ");
            query.setParameterList("environmentIds", environmentIds);
            query.setParameterList("variableIds", variableIds);
            
            List<Object[]> list =  query.list();
              
            for (Object[] row : list){
                Integer id = (Integer) row[0]; 
                Long locationCount = ((BigInteger) row [1]).longValue();
                Long germplasmCount =((BigInteger) row [2]).longValue();
                Long observationCount = ((BigInteger)  row [3]).longValue();
                  
                traitInfoList.add(new TraitInfo(id, null, null, locationCount, germplasmCount, observationCount)); 
              }
            
        } catch(HibernateException e) {
            logAndThrowException("Error at getTraitInfoCounts() query on PhenotypeDao: " + e.getMessage(), e);
        }

        return traitInfoList;

    }
    

    public List<TraitInfo>  getTraitInfoCounts(List<Integer> environmentIds) throws MiddlewareQueryException{
        List<TraitInfo> traitInfoList = new ArrayList<TraitInfo>();
        try {
            SQLQuery query = getSession().createSQLQuery(
                    "SELECT p.observable_id, "
                    + "COUNT(DISTINCT e.nd_geolocation_id) AS location_count, "
                    + "COUNT(DISTINCT es.stock_id) AS germplasm_count, "
                    + "COUNT(DISTINCT e.nd_experiment_id) AS observation_count "
                    + "FROM phenotype p "
                    + "    INNER JOIN nd_experiment_phenotype eph ON eph.phenotype_id = p.phenotype_id "
                    + "    INNER JOIN nd_experiment e ON e.nd_experiment_id = eph.nd_experiment_id "
                    + "    INNER JOIN nd_experiment_stock es ON es.nd_experiment_id = e.nd_experiment_id "
                    + "WHERE e.nd_geolocation_id IN (:environmentIds) "
                    + "GROUP by p.observable_id ");
            query.setParameterList("environmentIds", environmentIds);
            
            List<Object[]> list =  query.list();
              
            for (Object[] row : list){
                Integer id = (Integer) row[0]; 
                Long locationCount = ((BigInteger) row [1]).longValue();
                Long germplasmCount =((BigInteger) row [2]).longValue();
                Long observationCount = ((BigInteger)  row [3]).longValue();
                  
                traitInfoList.add(new TraitInfo(id, null, null, locationCount, germplasmCount, observationCount)); 
              }
            
        } catch(HibernateException e) {
            logAndThrowException("Error at getTraitInfoCounts() query on PhenotypeDao: " + e.getMessage(), e);
        }

        return traitInfoList;

    }
    
    public  Map<Integer, List<Double>> getNumericTraitInfoValues(List<Integer> environmentIds, List<NumericTraitInfo> traitInfoList) throws MiddlewareQueryException{
        Map<Integer, List<Double>> traitValues = new HashMap<Integer, List<Double>>();
        
        // Get trait IDs
        List<Integer> traitIds = new ArrayList<Integer>();
        for (NumericTraitInfo trait : traitInfoList){
            traitIds.add(trait.getId());
        }

        try {
            SQLQuery query = getSession().createSQLQuery(
                    "SELECT p.observable_id, p.value * 1 "
                    + "FROM phenotype p "
                    + "    INNER JOIN nd_experiment_phenotype eph ON eph.phenotype_id = p.phenotype_id "
                    + "    INNER JOIN nd_experiment e ON e.nd_experiment_id = eph.nd_experiment_id "
                    + "WHERE e.nd_geolocation_id IN (:environmentIds) "
                    + "    AND p.observable_id IN (:traitIds) "
                    );
            query.setParameterList("environmentIds", environmentIds);
            query.setParameterList("traitIds", traitIds);

            List<Object[]> list =  query.list();

            for (Object[] row : list){
                Integer traitId = (Integer) row[0];
                Double value = (Double) row[1];

                List<Double> values = new ArrayList<Double>();
                values.add(value); 
                // If the trait exists in the map, add the value found. Else, just add the <trait, values> pair.
                if (traitValues.containsKey(traitId)){
                    values = traitValues.get(traitId);
                    values.add(value);
                    traitValues.remove(traitId);
                }
                traitValues.put(traitId, values);
                
            }
            
            
        } catch(HibernateException e) {
            logAndThrowException("Error at getNumericTraitInfoValues() query on PhenotypeDao: " + e.getMessage(), e);
        }

        return traitValues;

    }
    
    public Map<Integer, List<String>> getCharacterTraitInfoValues(List<Integer> environmentIds, List<CharacterTraitInfo> traitInfoList) throws MiddlewareQueryException{
        
        Map<Integer, List<String>> traitValues = new HashMap<Integer, List<String>>();
        
        // Get trait IDs
        List<Integer> traitIds = new ArrayList<Integer>();
        for (CharacterTraitInfo trait : traitInfoList){
            traitIds.add(trait.getId());
        }
        
        try {
            SQLQuery query = getSession().createSQLQuery(
                    "SELECT DISTINCT p.observable_id, p.value "
                    + "FROM phenotype p "
                    + "    INNER JOIN nd_experiment_phenotype eph ON eph.phenotype_id = p.phenotype_id "
                    + "    INNER JOIN nd_experiment e ON e.nd_experiment_id = eph.nd_experiment_id "
                    + "WHERE e.nd_geolocation_id IN (:environmentIds) "
                    + "    AND p.observable_id IN (:traitIds) " 
                    + "ORDER BY p.observable_id "
                    );
            query.setParameterList("environmentIds", environmentIds);
            query.setParameterList("traitIds", traitIds);

            List<Object[]> list =  query.list();
            
            for (Object[] row : list){
                Integer traitId = (Integer) row[0];
                String value = (String) row[1];
                
                List<String> values = new ArrayList<String>();
                values.add(value); 
                // If the trait exists in the map, add the value found. Else, just add the <trait, values> pair.
                if (traitValues.containsKey(traitId)){
                    values = traitValues.get(traitId);
                    values.add(value);
                    traitValues.remove(traitId);
                }
                traitValues.put(traitId, values);
            }
            
        } catch(HibernateException e) {
            logAndThrowException("Error at getCharacterraitInfoValues() query on PhenotypeDao: " + e.getMessage(), e);
        }
        return traitValues;

    }
    
    public void setCategoricalTraitInfoValues(List<CategoricalTraitInfo> traitInfoList) throws MiddlewareQueryException{
        
        // Get trait IDs
        List<Integer> traitIds = new ArrayList<Integer>();
        for (CategoricalTraitInfo trait : traitInfoList){
            traitIds.add(trait.getId());
        }

        try {
            SQLQuery query = getSession().createSQLQuery(
                    "SELECT p.observable_id, p.cvalue_id, COUNT(p.phenotype_id) AS valuesCount "
                    + "FROM phenotype p "
                    + "WHERE p.cvalue_id IS NOT NULL AND p.observable_id IN (:traitIds) "
                    + "GROUP BY p.observable_id, p.cvalue_id "
                    );
            query.setParameterList("traitIds", traitIds);

            List<Object[]> list =  query.list();
            
            for (Object[] row : list){
                Integer traitId = (Integer) row[0]; 
                Integer cValueId = (Integer) row [1];
                Long count = ((BigInteger) row[2]).longValue();
                
                for (CategoricalTraitInfo traitInfo: traitInfoList){
                    if(traitInfo.getId() == traitId){
                        traitInfo.addValueCount(new CategoricalValue(cValueId), count.longValue());
                        break;
                    }
                }
                
            }

        } catch(HibernateException e) {
            logAndThrowException("Error at getCategoricalTraitInfoValues() query on PhenotypeDao: " + e.getMessage(), e);
        }
        
    }
    
}
