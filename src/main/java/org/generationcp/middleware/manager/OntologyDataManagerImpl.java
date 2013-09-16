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
package org.generationcp.middleware.manager;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.generationcp.middleware.domain.dms.StandardVariable;
import org.generationcp.middleware.domain.oms.CvId;
import org.generationcp.middleware.domain.oms.Term;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.manager.api.OntologyDataManager;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;

public class OntologyDataManagerImpl extends DataManager implements OntologyDataManager {
	
	public OntologyDataManagerImpl() { 		
	}

	public OntologyDataManagerImpl(HibernateSessionProvider sessionProviderForLocal,
			                    HibernateSessionProvider sessionProviderForCentral) {
		super(sessionProviderForLocal, sessionProviderForCentral);
	}

	public OntologyDataManagerImpl(Session sessionForLocal, Session sessionForCentral) {
		super(sessionForLocal, sessionForCentral);
	}

	@Override
	public Term getTermById(int termId) throws MiddlewareQueryException {
		return getTermBuilder().get(termId);
	}
	
	@Override
	public StandardVariable getStandardVariable(int stdVariableId) throws MiddlewareQueryException {
		return getStandardVariableBuilder().create(stdVariableId);
	}

	@Override
	public void addStandardVariable(StandardVariable stdVariable) throws MiddlewareQueryException {
		requireLocalDatabaseInstance();
		Session session = getCurrentSessionForLocal();
        Transaction trans = null;
 
        try {
            trans = session.beginTransaction();
            //check if scale, property and method exists first
            Term scale = findTermByName(stdVariable.getScale().getName(),CvId.SCALES);
            if(scale==null) {
            	stdVariable.setScale(getTermSaver().save(stdVariable.getScale().getName(), stdVariable.getScale().getDefinition(), CvId.SCALES));
            	System.out.println("new scale with id = " + stdVariable.getScale().getId());
            }
            Term property = findTermByName(stdVariable.getProperty().getName(),CvId.PROPERTIES);
            if(property==null) {
            	stdVariable.setProperty(getTermSaver().save(stdVariable.getProperty().getName(), stdVariable.getProperty().getDefinition(), CvId.PROPERTIES));
            	System.out.println("new property with id = " + stdVariable.getProperty().getId());
            }
            Term method = findTermByName(stdVariable.getMethod().getName(),CvId.METHODS);
            if(method==null) {
            	stdVariable.setMethod(getTermSaver().save(stdVariable.getMethod().getName(), stdVariable.getMethod().getDefinition(), CvId.METHODS));
            	System.out.println("new method with id = " + stdVariable.getMethod().getId());
            }
            if(findStandardVariableByTraitScaleMethodNames(stdVariable.getProperty().getName(),
            		stdVariable.getScale().getName(),stdVariable.getMethod().getName())==null) {
            	getStandardVariableSaver().save(stdVariable);
            }
			trans.commit();
			
        } catch (Exception e) {
	    	rollbackTransaction(trans);
	        throw new MiddlewareQueryException("error in addStandardVariable " + e.getMessage(), e);
	    }
	} 
	
	@Deprecated
	@Override
	public Term addMethod(String name, String definition) throws MiddlewareQueryException{
		return addTerm(name, definition, CvId.METHODS);
	}

	@Override
	public Set<StandardVariable> findStandardVariablesByNameOrSynonym(String nameOrSynonym) throws MiddlewareQueryException {
		Set<StandardVariable> standardVariables = new HashSet<StandardVariable>();
		if (setWorkingDatabase(Database.LOCAL)) {
			standardVariables.addAll(getStandardVariablesByNameOrSynonym(nameOrSynonym));
		}
		if (setWorkingDatabase(Database.CENTRAL)) {
			standardVariables.addAll(getStandardVariablesByNameOrSynonym(nameOrSynonym));
		}
		return standardVariables;
	}
	
	private Set<StandardVariable> getStandardVariablesByNameOrSynonym(String nameOrSynonym) throws MiddlewareQueryException {
		Set<StandardVariable> standardVariables = new HashSet<StandardVariable>();
		Set<Integer> stdVarIds = getCvTermDao().findStdVariablesByNameOrSynonym(nameOrSynonym);
		for (Integer stdVarId : stdVarIds) {
			standardVariables.add(getStandardVariable(stdVarId));
		}
		return standardVariables;
	}

	@Override
	public Term findMethodById(int id) throws MiddlewareQueryException {
		return getMethodBuilder().findMethodById(id);
	}

	@Override
	public Term findMethodByName(String name) throws MiddlewareQueryException {
		return getMethodBuilder().findMethodByName(name);
	}

	@Override
	public Integer getStandadardVariableIdByPropertyScaleMethod(
			Integer propertyId, Integer scaleId, Integer methodId)
			throws MiddlewareQueryException {
			requireLocalDatabaseInstance();
			Session session = getCurrentSessionForLocal();
		
			try {
			
			String sql = "select distinct cvr.subject_id "
					+ "from cvterm_relationship cvr " 
					+ "inner join cvterm_relationship cvrp on cvr.subject_id = cvrp.subject_id and cvrp.type_id = 1200 "
					+ "inner join cvterm_relationship cvrs on cvr.subject_id = cvrs.subject_id and cvrs.type_id = 1220 "
					+ "inner join cvterm_relationship cvrm on cvr.subject_id = cvrm.subject_id and cvrm.type_id = 1210 "
					+ "where cvrp.object_id = :propertyId and cvrs.object_id = :scaleId and cvrm.object_id = :methodId LIMIT 0,1";
			
			Query query = session.createSQLQuery(sql);
			query.setParameter("propertyId", propertyId);
			query.setParameter("scaleId", scaleId);
			query.setParameter("methodId", methodId);
								
			Integer id = (Integer) query.uniqueResult();
			
			return id;
						
		} catch(HibernateException e) {
			logAndThrowException("Error at getStandadardVariableIdByPropertyScaleMethod :" + e.getMessage(), e);
		}
		return null;
		
		
	
	}
	
	@Override
	public StandardVariable findStandardVariableByTraitScaleMethodNames(
			String property, String scale, String method) 
			throws MiddlewareQueryException {
		StandardVariable sv = null;
		Integer stdVariableId = null;
		
		if (setWorkingDatabase(Database.CENTRAL)) {
			//get standardVariableid from central first 
			stdVariableId = getCvTermDao().findStandardVariableIdByTraitScaleMethodNamesCentral(property, scale, method);
			
			//if not found in central, check if it exists in the local database
			if (stdVariableId == null) {
				if(setWorkingDatabase(Database.LOCAL)) {
					
					Integer[] cvTermIds = new Integer[3];
					cvTermIds = getCvTermDao().getCvTermIdsByTraitScaleMethodNamesLocal(property, scale, method);
					
					//if standard variable is in the local database
					if (cvTermIds != null) {
						//check its object ids, if positive, get standard variable from central, else local
						if (setWorkingDatabase(cvTermIds[0])) {
							stdVariableId = getCvTermDao().getStandadardVariableIdByPropertyScaleMethod(cvTermIds[0], cvTermIds[1], cvTermIds[2]);
						}
					}		
				}
			} 		
		}
		
		if (stdVariableId != null) {
			sv = getStandardVariable(stdVariableId);
		} 
		
		return sv;
	}

	@Override
	public List<Term> getAllTermsByCvId(CvId cvId) throws MiddlewareQueryException {
		return getTermBuilder().getTermsByCvId(cvId);
	}

	@Override
	public long countTermsByCvId(CvId cvId) throws MiddlewareQueryException {
		setWorkingDatabase(cvId.getId());
		return getCvTermDao().countTermsByCvId(cvId);
    }
    
	public List<Term> getMethodsForTrait(Integer traitId)
			throws MiddlewareQueryException {
		List<Term> methodTerms = new ArrayList<Term>();
		Set<Integer> methodIds = new HashSet<Integer>();
		if (setWorkingDatabase(Database.CENTRAL)) {
			List<Integer> centralMethodIds = getCvTermDao().findMethodTermIdsByTrait(traitId);
			if (centralMethodIds != null) {
				methodIds.addAll(centralMethodIds);
			}
		}
		if(setWorkingDatabase(Database.LOCAL)) {
			List<Integer> localMethodIds = getCvTermDao().findMethodTermIdsByTrait(traitId);
			if (localMethodIds != null) {
				methodIds.addAll(localMethodIds);
			}
		}
		//iterate list
		for (Integer termId : methodIds) {
			methodTerms.add(getTermBuilder().get(termId));
		}
		return methodTerms;
	}
	
	@Override
	public List<Term> getScalesForTrait(Integer traitId)
			throws MiddlewareQueryException {
		List<Term> scaleTerms = new ArrayList<Term>();
		Set<Integer> scaleIds = new HashSet<Integer>();
		if (setWorkingDatabase(Database.CENTRAL)) {
			List<Integer> centralMethodIds = getCvTermDao().findScaleTermIdsByTrait(traitId);
			if (centralMethodIds != null) {
				scaleIds.addAll(centralMethodIds);
			}
		}
		if(setWorkingDatabase(Database.LOCAL)) {
			List<Integer> localMethodIds = getCvTermDao().findScaleTermIdsByTrait(traitId);
			if (localMethodIds != null) {
				scaleIds.addAll(localMethodIds);
			}
		}
		//iterate list
		for (Integer termId : scaleIds) {
			scaleTerms.add(getTermBuilder().get(termId));
		}
		return scaleTerms;
	}
	
	@Override
	public Term findTermByName(String name, CvId cvId) throws MiddlewareQueryException {
		return getTermBuilder().findTermByName(name, cvId);
	}
	
	@Override
	public Term addTerm(String name, String definition, CvId cvId) throws MiddlewareQueryException{
		requireLocalDatabaseInstance();
		Session session = getCurrentSessionForLocal();
        Transaction trans = null;
        Term term = null;
        
	    try {
	    	if (CvId.VARIABLES.getId() != cvId.getId()) {
	            trans = session.beginTransaction();
				term = getTermSaver().save(name, definition, cvId);
				trans.commit();
	    	} else {
	    		throw new MiddlewareQueryException("variables cannot be used in this method");
	    	}
	    	return term;
	    } catch (Exception e) {
	    	rollbackTransaction(trans);
	        throw new MiddlewareQueryException("error in addTerm " + e.getMessage(), e);
	    }
	}
	
}
