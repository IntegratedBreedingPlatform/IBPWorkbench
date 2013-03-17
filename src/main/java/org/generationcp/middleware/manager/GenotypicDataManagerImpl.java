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
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.generationcp.middleware.dao.NameDAO;
import org.generationcp.middleware.dao.gdms.AccMetadataSetDAO;
import org.generationcp.middleware.dao.gdms.AlleleValuesDAO;
import org.generationcp.middleware.dao.gdms.CharValuesDAO;
import org.generationcp.middleware.dao.gdms.DartValuesDAO;
import org.generationcp.middleware.dao.gdms.DatasetDAO;
import org.generationcp.middleware.dao.gdms.DatasetUsersDAO;
import org.generationcp.middleware.dao.gdms.MapDAO;
import org.generationcp.middleware.dao.gdms.MappingDataDAO;
import org.generationcp.middleware.dao.gdms.MappingPopDAO;
import org.generationcp.middleware.dao.gdms.MappingPopValuesDAO;
import org.generationcp.middleware.dao.gdms.MarkerAliasDAO;
import org.generationcp.middleware.dao.gdms.MarkerDAO;
import org.generationcp.middleware.dao.gdms.MarkerDetailsDAO;
import org.generationcp.middleware.dao.gdms.MarkerInfoDAO;
import org.generationcp.middleware.dao.gdms.MarkerMetadataSetDAO;
import org.generationcp.middleware.dao.gdms.MarkerOnMapDAO;
import org.generationcp.middleware.dao.gdms.MarkerUserInfoDAO;
import org.generationcp.middleware.dao.gdms.QtlDAO;
import org.generationcp.middleware.dao.gdms.QtlDetailsDAO;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.manager.api.GenotypicDataManager;
import org.generationcp.middleware.pojos.Name;
import org.generationcp.middleware.pojos.gdms.AccMetadataSet;
import org.generationcp.middleware.pojos.gdms.AccMetadataSetPK;
import org.generationcp.middleware.pojos.gdms.AlleleValues;
import org.generationcp.middleware.pojos.gdms.AllelicValueElement;
import org.generationcp.middleware.pojos.gdms.AllelicValueWithMarkerIdElement;
import org.generationcp.middleware.pojos.gdms.CharValues;
import org.generationcp.middleware.pojos.gdms.DartValues;
import org.generationcp.middleware.pojos.gdms.Dataset;
import org.generationcp.middleware.pojos.gdms.DatasetElement;
import org.generationcp.middleware.pojos.gdms.DatasetUsers;
import org.generationcp.middleware.pojos.gdms.GermplasmMarkerElement;
import org.generationcp.middleware.pojos.gdms.Map;
import org.generationcp.middleware.pojos.gdms.MapDetailElement;
import org.generationcp.middleware.pojos.gdms.MapInfo;
import org.generationcp.middleware.pojos.gdms.MappingPop;
import org.generationcp.middleware.pojos.gdms.MappingPopValues;
import org.generationcp.middleware.pojos.gdms.MappingValueElement;
import org.generationcp.middleware.pojos.gdms.Marker;
import org.generationcp.middleware.pojos.gdms.MarkerAlias;
import org.generationcp.middleware.pojos.gdms.MarkerDetails;
import org.generationcp.middleware.pojos.gdms.MarkerIdMarkerNameElement;
import org.generationcp.middleware.pojos.gdms.MarkerInfo;
import org.generationcp.middleware.pojos.gdms.MarkerMetadataSet;
import org.generationcp.middleware.pojos.gdms.MarkerMetadataSetPK;
import org.generationcp.middleware.pojos.gdms.MarkerNameElement;
import org.generationcp.middleware.pojos.gdms.MarkerOnMap;
import org.generationcp.middleware.pojos.gdms.MarkerUserInfo;
import org.generationcp.middleware.pojos.gdms.ParentElement;
import org.generationcp.middleware.pojos.gdms.Qtl;
import org.generationcp.middleware.pojos.gdms.QtlDetailElement;
import org.generationcp.middleware.pojos.gdms.QtlDetails;
import org.generationcp.middleware.pojos.gdms.QtlDetailsPK;
import org.hibernate.Session;
import org.hibernate.Transaction;

/**
 * Implementation of GenotypicDataManager
 * 
 * @author Joyce Avestro
 */
public class GenotypicDataManagerImpl extends DataManager implements GenotypicDataManager{

    public GenotypicDataManagerImpl() {
    }
    
    public GenotypicDataManagerImpl(HibernateSessionProvider sessionProviderForLocal, HibernateSessionProvider sessionProviderForCentral) {
        super(sessionProviderForLocal, sessionProviderForCentral);
    }
    
    public GenotypicDataManagerImpl(Session sessionForLocal, Session sessionForCentral) {
        super(sessionForLocal, sessionForCentral);
    }
    
    
    @Override
    public List<Integer> getMapIDsByQTLName(String qtlName, int start, int numOfRows) throws MiddlewareQueryException {
        
        if ((qtlName == null) || (qtlName.isEmpty())){
            return new ArrayList<Integer>();
        }
        
        QtlDAO dao = new QtlDAO();
        
        long centralCount = 0;
        long localCount = 0;
        long relativeLimit = 0;

        Session sessionForCentral = getCurrentSessionForCentral();
        Session sessionForLocal = getCurrentSessionForLocal();
        
        List<Integer> qtl = new ArrayList<Integer>();

        if(sessionForCentral != null) {
            
            dao.setSession(sessionForCentral);
            centralCount = dao.countMapIDsByQTLName(qtlName);
            
            if(centralCount > start) {
                qtl.addAll(dao.getMapIDsByQTLName(qtlName, start, numOfRows));
                relativeLimit = numOfRows - (centralCount - start);
                if(relativeLimit > 0 && sessionForLocal != null) {
                    dao.setSession(sessionForLocal);
                    localCount = dao.countMapIDsByQTLName(qtlName);
                    if(localCount > 0) {
                        qtl.addAll(dao.getMapIDsByQTLName(qtlName, 0, (int) relativeLimit));
                    }
                }
            } else {
                relativeLimit = start - centralCount;
                if (sessionForLocal != null) {
                    dao.setSession(sessionForLocal);
                    localCount = dao.countMapIDsByQTLName(qtlName);
                    if (localCount > relativeLimit) {
                        qtl.addAll(dao.getMapIDsByQTLName(qtlName, (int) relativeLimit, numOfRows));
                    }
                }
            }
        } else if (sessionForLocal != null) {
            dao.setSession(sessionForLocal);
            localCount = dao.countMapIDsByQTLName(qtlName);
            if (localCount > start) {
                qtl.addAll(dao.getMapIDsByQTLName(qtlName, start, numOfRows));
            }
        }
        
        return qtl;    
    }   
    
    @Override
    public long countMapIDsByQTLName(String qtlName) throws MiddlewareQueryException {

        long count = 0;

        QtlDAO dao = new QtlDAO();

        Session sessionForCentral = getCurrentSessionForCentral();
        Session sessionForLocal = getCurrentSessionForLocal();

        if (sessionForLocal != null) {
            dao.setSession(sessionForLocal);
            count = count + dao.countMapIDsByQTLName(qtlName);
        }

        if (sessionForCentral != null) {
            dao.setSession(sessionForCentral);
            count = count + dao.countMapIDsByQTLName(qtlName);
        }

        return count;
    }

    @Override
    public List<Integer> getNameIdsByGermplasmIds(List<Integer> gIds) throws MiddlewareQueryException{
        AccMetadataSetDAO dao = new AccMetadataSetDAO();
        Session session = getSession(gIds.get(0));
        
        if (session != null) {
            dao.setSession(session);
        } else {
            return new ArrayList<Integer>();
        }

        return (List<Integer>) dao.getNameIdsByGermplasmIds(gIds);
    }

    @Override
    public List<Name> getNamesByNameIds(List<Integer> nIds) throws MiddlewareQueryException {
        NameDAO nameDao = new NameDAO();
        Session session = getSession(nIds.get(0));

        if (session != null) {
            nameDao.setSession(session);
        } else {
            return new ArrayList<Name>();
        }

        return (List<Name>) nameDao.getNamesByNameIds(nIds);
    }

    @Override
    public Name getNameByNameId(Integer nId) throws MiddlewareQueryException {
        NameDAO dao = new NameDAO();
        Session session = getSession(nId);
        
        if(session != null) {
            dao.setSession(session);
        } else {
            return null;
        }
        
        return dao.getNameByNameId(nId);
    }

    @Override
    public long countAllMaps(Database instance) throws MiddlewareQueryException {
        MapDAO dao = new MapDAO();
        Session session = getSession(instance);
        
        if(session != null) {
            dao.setSession(session);
        } else {
            return 0;
        }
        
        return dao.countAll();
    }

    @Override
    public List<Map> getAllMaps(int start, int numOfRows, Database instance) throws MiddlewareQueryException {
        MapDAO dao = new MapDAO();
        Session session = getSession(instance);
        
        if(session != null) {
            dao.setSession(session);
        } else {
            return new ArrayList<Map>();
        }
        
        return dao.getAll(start, numOfRows);
    }
    
    @Override
    public List<MapInfo> getMapInfoByMapName(String mapName, Database instance) throws MiddlewareQueryException{
        MappingDataDAO dao = new MappingDataDAO();
        Session session = getSession(instance);
        
        if (session != null) {
            dao.setSession(session);
        } else {
            return new ArrayList<MapInfo>();
        }
        return (List<MapInfo>) dao.getMapInfoByMapName(mapName);
    }
    
    @Override
    public long countDatasetNames(Database instance) throws MiddlewareQueryException{
        DatasetDAO dao = new DatasetDAO();
        Session session = getSession(instance);  
        
        if (session != null) {
            dao.setSession(session);
        } else {
            return 0;
        }
        return dao.countByName();
    }
    
    @Override
    public List<String> getDatasetNames(int start, int numOfRows, Database instance) throws MiddlewareQueryException{
        DatasetDAO dao = new DatasetDAO();
        Session session = getSession(instance);  
        
        if (session != null) {
            dao.setSession(session);
        } else {
            return new ArrayList<String>();
        }
        return (List<String>) dao.getDatasetNames(start, numOfRows);
    }
    
    
    /* (non-Javadoc)
     * @see org.generationcp.middleware.manager.api.GenotypicDataManager#getDatasetDetailsByDatasetName(java.lang.String)
     */
    @Override
    public List<DatasetElement> getDatasetDetailsByDatasetName(String datasetName, Database instance) throws MiddlewareQueryException{
        DatasetDAO dao = new DatasetDAO();
        Session session = getSession(instance);  
        
        if (session != null) {
            dao.setSession(session);
        } else {
            return new ArrayList<DatasetElement>();
        }
        return (List<DatasetElement>) dao.getDetailsByName(datasetName);
    }
    
    
    @Override
    public List<Integer> getMarkerIdsByMarkerNames(List<String> markerNames, int start, int numOfRows, Database instance) throws MiddlewareQueryException {
        MarkerDAO dao = new MarkerDAO();
        Session session = getSession(instance);
        
        if (session != null) {
            dao.setSession(session);
        } else {
            return new ArrayList<Integer> ();
        }
        
        List<Integer> markerIds = dao.getIdsByNames(markerNames, start, numOfRows);
        
        return markerIds;
    }
    
    @Override
    public Set<Integer> getMarkerIDsByMapIDAndLinkageBetweenStartPosition(int mapID, String linkageGroup, int startPos, int endPos, int start, int numOfRows) 
            throws MiddlewareQueryException {
        
        MarkerDAO dao = new MarkerDAO();
        Session session = getSession(mapID);
        
        if (session != null) {
            dao.setSession(session);
        } else {
            return new TreeSet<Integer> ();
        }
        
        Set<Integer> markerIds = dao.getMarkerIDsByMapIDAndLinkageBetweenStartPosition(mapID, linkageGroup, startPos, endPos, start, numOfRows);
        
        return markerIds;
    }
    
    @Override
    public long countMarkerIDsByMapIDAndLinkageBetweenStartPosition(int mapID, String linkageGroup, int startPos, int endPos) 
            throws MiddlewareQueryException {
        
        MarkerDAO dao = new MarkerDAO();
        Session session = getSession(mapID);
        
        if (session != null) {
            dao.setSession(session);
        } else {
            return 0l;
        }
        
        long count = dao.countMarkerIDsByMapIDAndLinkageBetweenStartPosition(mapID, linkageGroup, startPos, endPos);
        
        return count;
    }

    @Override
    public List<Integer> getMarkerIdsByDatasetId(Integer datasetId) throws MiddlewareQueryException{
        MarkerMetadataSetDAO dao = new MarkerMetadataSetDAO();
        Session session = getSession(datasetId);
        
        if (session != null) {
            dao.setSession(session);
        } else {
            return new ArrayList<Integer>();
        }
        return (List<Integer>) dao.getMarkerIdByDatasetId(datasetId);
    }
    
    
    @Override
    public List<ParentElement> getParentsByDatasetId(Integer datasetId) throws MiddlewareQueryException{
        MappingPopDAO dao = new MappingPopDAO();
        Session session = getSession(datasetId);  
        
        if (session != null) {
            dao.setSession(session);
        } else {
            return new ArrayList<ParentElement>();
        }
        return (List<ParentElement>) dao.getParentsByDatasetId(datasetId);
    }
    
    @Override
    public List<String> getMarkerTypesByMarkerIds(List<Integer> markerIds) throws MiddlewareQueryException{
        MarkerDAO dao = new MarkerDAO();
        Session session = getSession(markerIds.get(0));
        
        if (session != null) {
            dao.setSession(session);
        } else {
            return new ArrayList<String>();
        }
        return (List<String>) dao.getMarkerTypeByMarkerIds(markerIds);
    }
    
    @Override
    public List<MarkerNameElement> getMarkerNamesByGIds(List<Integer> gIds) throws MiddlewareQueryException{
        MarkerDAO dao = new MarkerDAO();
        Session session = getSession(gIds.get(0));  
        
        if (session != null) {
            dao.setSession(session);
        } else {
            return new ArrayList<MarkerNameElement>();
        }
        return (List<MarkerNameElement>) dao.getMarkerNamesByGIds(gIds);
    }
    
    @Override
    public List<GermplasmMarkerElement>  getGermplasmNamesByMarkerNames(List<String> markerNames, Database instance) throws MiddlewareQueryException{
        MarkerDAO dao = new MarkerDAO();
        Session session = getSession(instance);  
        
        if (session != null) {
            dao.setSession(session);
        } else {
            return new ArrayList<GermplasmMarkerElement>();
        }
        return (List<GermplasmMarkerElement>) dao.getGermplasmNamesByMarkerNames(markerNames);
    }
    

    @Override
    public List<MappingValueElement> getMappingValuesByGidsAndMarkerNames(
            List<Integer> gids, List<String> markerNames, int start, int numOfRows) throws MiddlewareQueryException {
        MarkerDAO markerDao = new MarkerDAO();
        MappingPopDAO mappingPopDao = new MappingPopDAO();
        
        //get db connection based on the GIDs provided
        Database instance;
        if (gids.get(0) < 0) {
            instance = Database.LOCAL;
        } else {
            instance = Database.CENTRAL;
        }
        Session session = getSession(instance);
        
        if (session != null) {
            markerDao.setSession(session);
            mappingPopDao.setSession(session);
        } else {
            return new ArrayList<MappingValueElement>();
        }
        
        List<Integer> markerIds = markerDao.getIdsByNames(markerNames, start, numOfRows);
        
        List<MappingValueElement> mappingValues = mappingPopDao.getMappingValuesByGidAndMarkerIds(gids, markerIds);
        
        return mappingValues;
    }
    
    @Override
    public List<AllelicValueElement> getAllelicValuesByGidsAndMarkerNames(
            List<Integer> gids, List<String> markerNames) throws MiddlewareQueryException {
        MarkerDAO markerDao = new MarkerDAO();
        
        //get db connection based on the GIDs provided
        Database instance;
        if (gids.get(0) < 0) {
            instance = Database.LOCAL;
        } else {
            instance = Database.CENTRAL;
        }
        Session session = getSession(instance);
        
        if (session != null) {
            markerDao.setSession(session);
        } else {
            return new ArrayList<AllelicValueElement>();
        }
        
        List<AllelicValueElement> allelicValues = markerDao.getAllelicValuesByGidsAndMarkerNames(gids, markerNames);
        
        return allelicValues;
    }

    @Override
    public List<AllelicValueWithMarkerIdElement> getAllelicValuesFromCharValuesByDatasetId(
            Integer datasetId, int start, int numOfRows) throws MiddlewareQueryException{
        CharValuesDAO dao = new CharValuesDAO();
        Session session = getSession(datasetId);
        if (session == null) {
            return new ArrayList<AllelicValueWithMarkerIdElement>();
        }
        dao.setSession(session);        
        return dao.getAllelicValuesByDatasetId(datasetId, start, numOfRows);
    }
    
    @Override
    public long countAllelicValuesFromCharValuesByDatasetId(Integer datasetId) throws MiddlewareQueryException{
        CharValuesDAO dao = new CharValuesDAO();
        Session session = getSession(datasetId);
        if (session == null) {
            return 0;
        }
        dao.setSession(session);
        return dao.countByDatasetId(datasetId);
    }

    @Override
    public List<AllelicValueWithMarkerIdElement> getAllelicValuesFromAlleleValuesByDatasetId(
            Integer datasetId, int start, int numOfRows) throws MiddlewareQueryException{
        AlleleValuesDAO dao = new AlleleValuesDAO();
        Session session = getSession(datasetId);

        if (session == null) {
            return new ArrayList<AllelicValueWithMarkerIdElement>();
        }
        dao.setSession(session);
        return dao.getAllelicValuesByDatasetId(datasetId, start, numOfRows);
    }
    
    
    @Override
    public long countAllelicValuesFromAlleleValuesByDatasetId(Integer datasetId) throws MiddlewareQueryException{
        AlleleValuesDAO dao = new AlleleValuesDAO();
        Session session = getSession(datasetId);
        if (session == null) {
            return 0;
        }
        dao.setSession(session);
        return dao.countByDatasetId(datasetId);
    }

    @Override
    public List<AllelicValueWithMarkerIdElement> getAllelicValuesFromMappingPopValuesByDatasetId(
            Integer datasetId, int start, int numOfRows) throws MiddlewareQueryException{
        MappingPopValuesDAO dao = new MappingPopValuesDAO();
        Session session = getSession(datasetId);
        if (session == null) {
            return  new ArrayList<AllelicValueWithMarkerIdElement>();
        }
        dao.setSession(session);
        return dao.getAllelicValuesByDatasetId(datasetId, start, numOfRows);
    }
    
    @Override
    public long countAllelicValuesFromMappingPopValuesByDatasetId(Integer datasetId) throws MiddlewareQueryException{
        MappingPopValuesDAO dao = new MappingPopValuesDAO();
        Session session = getSession(datasetId);
        if (session == null) {
            return 0;
        }
        dao.setSession(session);
        return dao.countByDatasetId(datasetId);
    }
    @Override
    public List<MarkerInfo> getMarkerInfoByMarkerName(String markerName, int start, int numOfRows) throws MiddlewareQueryException{

        MarkerInfoDAO dao = new MarkerInfoDAO();
        List<MarkerInfo> markerInfoList = new ArrayList<MarkerInfo>();
        
        long centralCount = 0;
        long localCount = 0;
        long relativeLimit = 0;
        
        Session sessionForCentral = getCurrentSessionForCentral();
        Session sessionForLocal = getCurrentSessionForLocal();
        
        if (sessionForCentral != null) {
                
            dao.setSession(sessionForCentral);
            centralCount = dao.countByMarkerName(markerName);
            
            if (centralCount > start) {  
                
                markerInfoList.addAll((List<MarkerInfo>) dao.getByMarkerName(markerName, start, numOfRows));
                relativeLimit = numOfRows - (centralCount - start);
                
                if (relativeLimit > 0) {
                        if (sessionForLocal != null) {                                
                            dao.setSession(sessionForLocal);
                            localCount = dao.countByMarkerName(markerName);
                            
                            if (localCount > 0) {
                                markerInfoList.addAll((List<MarkerInfo>) dao.getByMarkerName(markerName, 0, (int) relativeLimit));
                            }  
                        }
                }
                
            } else {
                
                relativeLimit = start - centralCount;
                
                if (sessionForLocal != null) {
                        
                    dao.setSession(sessionForLocal);
                    localCount = dao.countByMarkerName(markerName);
                    
                    if (localCount > relativeLimit) {
                        markerInfoList.addAll((List<MarkerInfo>) dao.getByMarkerName(markerName, (int) relativeLimit, numOfRows));
                    }  
                }
            }
            
        } else if (sessionForLocal != null) {
                
            dao.setSession(sessionForLocal);
            localCount = dao.countByMarkerName(markerName);

            if (localCount > start) {
                markerInfoList.addAll((List<MarkerInfo>) dao.getByMarkerName(markerName, start, numOfRows));
            }
        }
        
        return markerInfoList;
    }
    
    @Override
    public long countMarkerInfoByMarkerName(String markerName) throws MiddlewareQueryException{
        long count = 0;        
        MarkerInfoDAO dao = new MarkerInfoDAO();
        
        Session sessionForCentral = getCurrentSessionForCentral();
        Session sessionForLocal = getCurrentSessionForLocal();
        
        if (sessionForLocal != null) {
            dao.setSession(sessionForLocal);
            count = count + dao.countByMarkerName(markerName);
        }
        
        if (sessionForCentral != null) {
            dao.setSession(sessionForCentral);
            count = count + dao.countByMarkerName(markerName);
        }
        
        return count;
    }
    
    @Override
   public List<MarkerInfo> getMarkerInfoByGenotype(String genotype, int start, int numOfRows) throws MiddlewareQueryException{

        MarkerInfoDAO dao = new MarkerInfoDAO();
        List<MarkerInfo> markerInfoList = new ArrayList<MarkerInfo>();
        
        long centralCount = 0;
        long localCount = 0;
        long relativeLimit = 0;
        
        Session sessionForCentral = getCurrentSessionForCentral();
        Session sessionForLocal = getCurrentSessionForLocal();
        
        if (sessionForCentral != null) {
                
            dao.setSession(sessionForCentral);
            centralCount = dao.countByGenotype(genotype);
            
            if (centralCount > start) {  
                
                markerInfoList.addAll((List<MarkerInfo>) dao.getByGenotype(genotype, start, numOfRows));
                relativeLimit = numOfRows - (centralCount - start);
                
                if (relativeLimit > 0) {
                        if (sessionForLocal != null) {                                
                            dao.setSession(sessionForLocal);
                            localCount = dao.countByGenotype(genotype);
                            
                            if (localCount > 0) {
                                markerInfoList.addAll((List<MarkerInfo>) dao.getByGenotype(genotype, 0, (int) relativeLimit));
                            }  
                        }
                }
                
            } else {
                
                relativeLimit = start - centralCount;
                
                if (sessionForLocal != null) {
                        
                    dao.setSession(sessionForLocal);
                    localCount = dao.countByGenotype(genotype);
                    
                    if (localCount > relativeLimit) {
                        markerInfoList.addAll((List<MarkerInfo>) dao.getByGenotype(genotype, (int) relativeLimit, numOfRows));
                    }  
                }
            }
            
        } else if (sessionForLocal != null) {
                
            dao.setSession(sessionForLocal);
            localCount = dao.countByGenotype(genotype);

            if (localCount > start) {
                markerInfoList.addAll((List<MarkerInfo>) dao.getByGenotype(genotype, start, numOfRows));
            }
        }
        
        return markerInfoList;
    }
    
    @Override
    public long countMarkerInfoByGenotype(String genotype) throws MiddlewareQueryException{
        long count = 0;        
        MarkerInfoDAO dao = new MarkerInfoDAO();
        
        Session sessionForCentral = getCurrentSessionForCentral();
        Session sessionForLocal = getCurrentSessionForLocal();
        
        if (sessionForLocal != null) {
            dao.setSession(sessionForLocal);
            count = count + dao.countByGenotype(genotype);
        }
        
        if (sessionForCentral != null) {
            dao.setSession(sessionForCentral);
            count = count + dao.countByGenotype(genotype);
        }
        
        return count;
    }

    @Override
   public List<MarkerInfo> getMarkerInfoByDbAccessionId(String dbAccessionId, int start, int numOfRows) throws MiddlewareQueryException{

        MarkerInfoDAO dao = new MarkerInfoDAO();
        List<MarkerInfo> markerInfoList = new ArrayList<MarkerInfo>();
        
        long centralCount = 0;
        long localCount = 0;
        long relativeLimit = 0;
        
        Session sessionForCentral = getCurrentSessionForCentral();
        Session sessionForLocal = getCurrentSessionForLocal();
        
        if (sessionForCentral != null) {
                
            dao.setSession(sessionForCentral);
            centralCount = dao.countByDbAccessionId(dbAccessionId);
            
            if (centralCount > start) {  
                
                markerInfoList.addAll((List<MarkerInfo>) dao.getByDbAccessionId(dbAccessionId, start, numOfRows));
                relativeLimit = numOfRows - (centralCount - start);
                
                if (relativeLimit > 0) {
                        if (sessionForLocal != null) {                                
                            dao.setSession(sessionForLocal);
                            localCount = dao.countByDbAccessionId(dbAccessionId);
                            
                            if (localCount > 0) {
                                markerInfoList.addAll((List<MarkerInfo>) dao.getByDbAccessionId(dbAccessionId, 0, (int) relativeLimit));
                            }  
                        }
                }
                
            } else {
                
                relativeLimit = start - centralCount;
                
                if (sessionForLocal != null) {
                        
                    dao.setSession(sessionForLocal);
                    localCount = dao.countByDbAccessionId(dbAccessionId);
                    
                    if (localCount > relativeLimit) {
                        markerInfoList.addAll((List<MarkerInfo>) dao.getByDbAccessionId(dbAccessionId, (int) relativeLimit, numOfRows));
                    }  
                }
            }
            
        } else if (sessionForLocal != null) {
                
            dao.setSession(sessionForLocal);
            localCount = dao.countByDbAccessionId(dbAccessionId);

            if (localCount > start) {
                markerInfoList.addAll((List<MarkerInfo>) dao.getByDbAccessionId(dbAccessionId, start, numOfRows));
            }
        }
        
        return markerInfoList;
    }
    
    @Override
    public long countMarkerInfoByDbAccessionId(String dbAccessionId) throws MiddlewareQueryException{
        long count = 0;        
        MarkerInfoDAO dao = new MarkerInfoDAO();
        
        Session sessionForCentral = getCurrentSessionForCentral();
        Session sessionForLocal = getCurrentSessionForLocal();
        
        if (sessionForLocal != null) {
            dao.setSession(sessionForLocal);
            count = count + dao.countByDbAccessionId(dbAccessionId);
        }
        
        if (sessionForCentral != null) {
            dao.setSession(sessionForCentral);
            count = count + dao.countByDbAccessionId(dbAccessionId);
        }
        
        return count;
    }

    
    @Override
    public List<MarkerIdMarkerNameElement> getMarkerNamesByMarkerIds(List<Integer> markerIds)
            throws MiddlewareQueryException {
        MarkerDAO dao = new MarkerDAO();
        Session session = getSession(markerIds.get(0));
        
        if (session != null) {
            dao.setSession(session);
        } else {
            return new ArrayList<MarkerIdMarkerNameElement>();
        }
        
        List<MarkerIdMarkerNameElement> dataValues = dao.getNamesByIds(markerIds);
        return dataValues;
    }

    @Override
    public List<String> getAllMarkerTypes(int start, int numOfRows) throws MiddlewareQueryException {
        MarkerDAO dao = new MarkerDAO();
        List<String> markerTypes = new ArrayList<String>();
        long centralCount = 0;
        long localCount = 0;
        long relativeLimit = 0;
        
        Session sessionForCentral = getCurrentSessionForCentral();
        Session sessionForLocal = getCurrentSessionForLocal();
        
        if(sessionForCentral != null) {
            
            dao.setSession(sessionForCentral);
            centralCount = countAllMarkerTypes(Database.CENTRAL);
            
            if(centralCount > start) {
                markerTypes.addAll(dao.getAllMarkerTypes(start, numOfRows));
                relativeLimit = numOfRows - markerTypes.size();
                if(relativeLimit > 0 && sessionForLocal != null) {
                    dao.setSession(sessionForLocal);
                    localCount = countAllMarkerTypes(Database.LOCAL);
                    if(localCount > 0) {
                        markerTypes.addAll(dao.getAllMarkerTypes(0, (int) relativeLimit));
                    }
                }
            } else {
                relativeLimit = start - centralCount;
                if (sessionForLocal != null) {
                    dao.setSession(sessionForLocal);
                    localCount = countAllMarkerTypes(Database.LOCAL);
                    if (localCount > relativeLimit) {
                        markerTypes.addAll(dao.getAllMarkerTypes((int) relativeLimit, numOfRows));
                    }
                }
            }
        } else if (sessionForLocal != null) {
            dao.setSession(sessionForLocal);
            localCount = countAllMarkerTypes(Database.LOCAL);
            if (localCount > start) {
                markerTypes.addAll(dao.getAllMarkerTypes(start, numOfRows));
            }
        }
        
        return markerTypes;
    }

    @Override
    public long countAllMarkerTypes(Database instance) throws MiddlewareQueryException {
        MarkerDAO dao = new MarkerDAO();
        Session session = getSession(instance);
        long result = 0;
        
        if(session != null) {
            dao.setSession(session);
            result = dao.countAllMarkerTypes();
        }
        
        return result;
    }

    @Override
    public List<String> getMarkerNamesByMarkerType(String markerType, int start, int numOfRows) throws MiddlewareQueryException {
        MarkerDAO dao = new MarkerDAO();
        
        List<String> markerNames = new ArrayList<String>();
        
        long centralCount = 0;
        long localCount = 0;
        long relativeLimit = 0;
        
        Session sessionForCentral = getCurrentSessionForCentral();
        Session sessionForLocal = getCurrentSessionForLocal();
        
        if(sessionForCentral != null) {
            
            dao.setSession(sessionForCentral);
            centralCount = dao.countMarkerNamesByMarkerType(markerType);
            
            if(centralCount > start) {
                markerNames.addAll(dao.getMarkerNamesByMarkerType(markerType, start, numOfRows));
                relativeLimit = numOfRows - markerNames.size();
                if(relativeLimit > 0 && sessionForLocal != null) {
                    dao.setSession(sessionForLocal);
                    localCount = dao.countMarkerNamesByMarkerType(markerType);
                    if(localCount > 0) {
                        markerNames.addAll(dao.getMarkerNamesByMarkerType(markerType, 0, (int) relativeLimit));
                    }
                }
            } else {
                relativeLimit = start - centralCount;
                if (sessionForLocal != null) {
                    dao.setSession(sessionForLocal);
                    localCount = dao.countMarkerNamesByMarkerType(markerType);
                    if (localCount > relativeLimit) {
                        markerNames.addAll(dao.getMarkerNamesByMarkerType(markerType, (int) relativeLimit, numOfRows));
                    }
                }
            }
        } else if (sessionForLocal != null) {
            dao.setSession(sessionForLocal);
            localCount = dao.countMarkerNamesByMarkerType(markerType);
            if (localCount > start) {
                markerNames.addAll(dao.getMarkerNamesByMarkerType(markerType, start, numOfRows));
            }
        }
        
        return markerNames;
    }

    @Override
    public long countMarkerNamesByMarkerType(String markerType) 
        throws MiddlewareQueryException {

        MarkerDAO dao = new MarkerDAO();
        long result = 0;
        
        Session sessionForCentral = getCurrentSessionForCentral();
        Session sessionForLocal = getCurrentSessionForLocal();
        
        if(sessionForCentral != null) {
            dao.setSession(sessionForCentral);
            result = dao.countMarkerNamesByMarkerType(markerType);
        }
        
        if(sessionForLocal != null) {
            dao.setSession(sessionForLocal);
            result += dao.countMarkerNamesByMarkerType(markerType);
        }
        
        return result;
    }

    @Override
    public List<Integer> getGIDsFromCharValuesByMarkerId(Integer markerId, int start, int numOfRows)
        throws MiddlewareQueryException {
        
        CharValuesDAO dao = new CharValuesDAO();
        Session session = getSession(markerId);
        List<Integer> gids;
        
        if(session != null) {
            dao.setSession(session);
            gids = dao.getGIDsByMarkerId(markerId, start, numOfRows);
        } else {
            gids = new ArrayList<Integer>();
        }
        
        return gids;
    }

    @Override
    public long countGIDsFromCharValuesByMarkerId(Integer markerId) throws MiddlewareQueryException {
        CharValuesDAO dao = new CharValuesDAO();
        Session session = getSession(markerId);
        long result = 0;
        
        if(session != null) {
            dao.setSession(session);
            result = dao.countGIDsByMarkerId(markerId);
        } 
        
        return result;
    }

    @Override
    public List<Integer> getGIDsFromAlleleValuesByMarkerId(Integer markerId, int start, int numOfRows) throws MiddlewareQueryException {
        AlleleValuesDAO dao = new AlleleValuesDAO();
        Session session = getSession(markerId);
        List<Integer> gids;
        
        if(session != null) {
            dao.setSession(session);
            gids = dao.getGIDsByMarkerId(markerId, start, numOfRows);
        } else {
            gids = new ArrayList<Integer>();
        }
        
        return gids;
    }

    @Override
    public long countGIDsFromAlleleValuesByMarkerId(Integer markerId) throws MiddlewareQueryException {
        AlleleValuesDAO dao = new AlleleValuesDAO();
        Session session = getSession(markerId);
        long result = 0;
        
        if(session != null) {
            dao.setSession(session);
            result = dao.countGIDsByMarkerId(markerId);
        } 
        
        return result;
    }
    
    @Override
    public List<Integer> getGIDsFromMappingPopValuesByMarkerId(Integer markerId, int start, int numOfRows) throws MiddlewareQueryException {
        MappingPopValuesDAO dao = new MappingPopValuesDAO();
        Session session = getSession(markerId);
        List<Integer> gids;
        
        if(session != null) {
            dao.setSession(session);
            gids = dao.getGIDsByMarkerId(markerId, start, numOfRows);
        } else {
            gids = new ArrayList<Integer>();
        }
        
        return gids;
    }

    @Override
    public long countGIDsFromMappingPopValuesByMarkerId(Integer markerId) throws MiddlewareQueryException {
        MappingPopValuesDAO dao = new MappingPopValuesDAO();
        Session session = getSession(markerId);
        long result = 0;
        
        if(session != null) {
            dao.setSession(session);
            result = dao.countGIDsByMarkerId(markerId);
        } 
        
        return result;
    }

    @Override
    public List<String> getAllDbAccessionIdsFromMarker(int start, int numOfRows) throws MiddlewareQueryException {
        MarkerDAO dao = new MarkerDAO();
        List<String> dbAccessionIds = new ArrayList<String>();
        long centralCount = 0;
        long localCount = 0;
        long relativeLimit = 0;
        
        Session sessionForCentral = getCurrentSessionForCentral();
        Session sessionForLocal = getCurrentSessionForLocal();
        
        if(sessionForCentral != null) {
            
            dao.setSession(sessionForCentral);
            centralCount = dao.countAllDbAccessionIds();
            
            if(centralCount > start) {
                dbAccessionIds.addAll(dao.getAllDbAccessionIds(start, numOfRows));
                relativeLimit = numOfRows - dbAccessionIds.size();
                if(relativeLimit > 0 && sessionForLocal != null) {
                    dao.setSession(sessionForLocal);
                    localCount = dao.countAllDbAccessionIds();
                    if(localCount > 0) {
                        dbAccessionIds.addAll(dao.getAllDbAccessionIds(0, (int) relativeLimit));
                    }
                }
            } else {
                relativeLimit = start - centralCount;
                if (sessionForLocal != null) {
                    dao.setSession(sessionForLocal);
                    localCount = dao.countAllDbAccessionIds();
                    if (localCount > relativeLimit) {
                        dbAccessionIds.addAll(dao.getAllDbAccessionIds((int) relativeLimit, numOfRows));
                    }
                }
            }
        } else if (sessionForLocal != null) {
            dao.setSession(sessionForLocal);
            localCount = dao.countAllDbAccessionIds();
            if (localCount > start) {
                dbAccessionIds.addAll(dao.getAllDbAccessionIds(start, numOfRows));
            }
        }
        
        return dbAccessionIds;
    }

    @Override
    public long countAllDbAccessionIdsFromMarker() throws MiddlewareQueryException {
        MarkerDAO dao = new MarkerDAO();
        long result = 0;
        
        Session sessionForCentral = getCurrentSessionForCentral();
        Session sessionForLocal = getCurrentSessionForLocal();
        
        if(sessionForCentral != null) {
            dao.setSession(sessionForCentral);
            result = dao.countAllDbAccessionIds();
        }
        
        if(sessionForLocal != null) {
            dao.setSession(sessionForLocal);
            result += dao.countAllDbAccessionIds();
        }
        
        return result;
    }

    @Override
    public List<Integer> getNidsFromAccMetadatasetByDatasetIds(List<Integer> datasetIds, 
                                                               int start, int numOfRows) throws MiddlewareQueryException {
        return getNidsFromAccMetadatasetByDatasetIds(datasetIds, null, start, numOfRows);
    }

    @Override
    public List<Integer> getNidsFromAccMetadatasetByDatasetIds(List<Integer> datasetIds, 
                                                               List<Integer> gids, 
                                                               int start, int numOfRows) throws MiddlewareQueryException {
        
        AccMetadataSetDAO dao = new AccMetadataSetDAO();
        Session session = getSession(datasetIds.get(0));
        List<Integer> nids;
        
        if(session != null) {
            dao.setSession(session);
            nids = dao.getNIDsByDatasetIds(datasetIds, gids, start, numOfRows);
        } else {
            nids = new ArrayList<Integer>();
        }
        
        return nids;
    }
    
    @Override
    public List<Integer> getNIdsByMarkerIdsAndDatasetIdsAndNotGIds(List<Integer> datasetIds, 
        List<Integer> markerIds, List<Integer> gIds,
        int start, int numOfRows) throws MiddlewareQueryException {
        
        Set<Integer> nidSet = new TreeSet<Integer>();
        
        AccMetadataSetDAO dao = new AccMetadataSetDAO();

        Session sessionForCentral = getCurrentSessionForCentral();
        Session sessionForLocal = getCurrentSessionForLocal();
        
        if(sessionForCentral != null) {
            dao.setSession(sessionForCentral);
            nidSet.addAll(dao.getNIdsByMarkerIdsAndDatasetIdsAndNotGIds(datasetIds, markerIds, gIds));
        }
        
        if(sessionForLocal != null) {
            dao.setSession(sessionForLocal);
            nidSet.addAll(dao.getNIdsByMarkerIdsAndDatasetIdsAndNotGIds(datasetIds, markerIds, gIds));
        }
        
        List<Integer> nidList = new ArrayList<Integer>(((TreeSet<Integer>)nidSet).descendingSet());
        
        return nidList.subList(start, start+numOfRows);
    }
    
    @Override
    public int countNIdsByMarkerIdsAndDatasetIdsAndNotGIds(List<Integer> datasetIds, 
        List<Integer> markerIds, List<Integer> gIds) throws MiddlewareQueryException {
        
        Set<Integer> nidSet = new TreeSet<Integer>();
        
        AccMetadataSetDAO dao = new AccMetadataSetDAO();

        Session sessionForCentral = getCurrentSessionForCentral();
        Session sessionForLocal = getCurrentSessionForLocal();
        
        if(sessionForCentral != null) {
            dao.setSession(sessionForCentral);
            nidSet.addAll(dao.getNIdsByMarkerIdsAndDatasetIdsAndNotGIds(datasetIds, markerIds, gIds));
        }
        
        if(sessionForLocal != null) {
            dao.setSession(sessionForLocal);
            nidSet.addAll(dao.getNIdsByMarkerIdsAndDatasetIdsAndNotGIds(datasetIds, markerIds, gIds));
        }
        
        List<Integer> nidList = new ArrayList<Integer>(nidSet);
        
        return nidList.size();
    }
    
    @Override
    public int countNIdsByMarkerIdsAndDatasetIds(List<Integer> datasetIds, 
        List<Integer> markerIds) throws MiddlewareQueryException {
        
        Set<Integer> nidSet = new TreeSet<Integer>();
        
        AccMetadataSetDAO dao = new AccMetadataSetDAO();

        Session sessionForCentral = getCurrentSessionForCentral();
        Session sessionForLocal = getCurrentSessionForLocal();
        
        if(sessionForCentral != null) {
            dao.setSession(sessionForCentral);
            nidSet.addAll(dao.getNIdsByMarkerIdsAndDatasetIds(datasetIds, markerIds));
        }
        
        if(sessionForLocal != null) {
            dao.setSession(sessionForLocal);
            nidSet.addAll(dao.getNIdsByMarkerIdsAndDatasetIds(datasetIds, markerIds));
        }
        
        List<Integer> nidList = new ArrayList<Integer>(nidSet);
        
        return nidList.size();
    }
    
    @Override
    public List<Integer> getNIdsByMarkerIdsAndDatasetIds(List<Integer> datasetIds, 
        List<Integer> markerIds,
        int start, int numOfRows) throws MiddlewareQueryException {
        
        Set<Integer> nidSet = new TreeSet<Integer>();
        
        AccMetadataSetDAO dao = new AccMetadataSetDAO();

        Session sessionForCentral = getCurrentSessionForCentral();
        Session sessionForLocal = getCurrentSessionForLocal();
        
        if(sessionForCentral != null) {
            dao.setSession(sessionForCentral);
            nidSet.addAll(dao.getNIdsByMarkerIdsAndDatasetIds(datasetIds, markerIds));
        }
        
        if(sessionForLocal != null) {
            dao.setSession(sessionForLocal);
            nidSet.addAll(dao.getNIdsByMarkerIdsAndDatasetIds(datasetIds, markerIds));
        }
        
        List<Integer> nidList = new ArrayList<Integer>(((TreeSet<Integer>)nidSet).descendingSet());
        
        return nidList.subList(start, start+numOfRows);
    }

    @Override
    public List<Integer> getDatasetIdsForFingerPrinting(int start, int numOfRows) throws MiddlewareQueryException{
        DatasetDAO dao = new DatasetDAO();
        List<Integer> datasetIds = new ArrayList<Integer>();
        long centralCount = 0;
        long localCount = 0;
        long relativeLimit = 0;
        
        Session sessionForCentral = getCurrentSessionForCentral();
        Session sessionForLocal = getCurrentSessionForLocal();
        
        if(sessionForCentral != null) {
            
            dao.setSession(sessionForCentral);
            centralCount = dao.countDatasetIdsForFingerPrinting();
            
            if(centralCount > start) {
                datasetIds.addAll(dao.getDatasetIdsForFingerPrinting(start, numOfRows));
                relativeLimit = numOfRows - datasetIds.size();
                if(relativeLimit > 0 && sessionForLocal != null) {
                    dao.setSession(sessionForLocal);
                    localCount = dao.countDatasetIdsForFingerPrinting();
                    if(localCount > 0) {
                        datasetIds.addAll(dao.getDatasetIdsForFingerPrinting(0, (int) relativeLimit));
                    }
                }
            } else {
                relativeLimit = start - centralCount;
                if (sessionForLocal != null) {
                    dao.setSession(sessionForLocal);
                    localCount = dao.countDatasetIdsForFingerPrinting();
                    if (localCount > relativeLimit) {
                        datasetIds.addAll(dao.getDatasetIdsForFingerPrinting((int) relativeLimit, numOfRows));
                    }
                }
            }
        } else if (sessionForLocal != null) {
            dao.setSession(sessionForLocal);
            localCount = dao.countDatasetIdsForFingerPrinting();
            if (localCount > start) {
                datasetIds.addAll(dao.getDatasetIdsForFingerPrinting(start, numOfRows));
            }
        }
        
        return datasetIds;
        
    }
    
    @Override
    public long countDatasetIdsForFingerPrinting() throws MiddlewareQueryException{
        DatasetDAO dao = new DatasetDAO();
        long result = 0;
        
        Session sessionForCentral = getCurrentSessionForCentral();
        Session sessionForLocal = getCurrentSessionForLocal();
        
        if(sessionForCentral != null) {
            dao.setSession(sessionForCentral);
            result = dao.countDatasetIdsForFingerPrinting();
        }
        
        if(sessionForLocal != null) {
            dao.setSession(sessionForLocal);
            result += dao.countDatasetIdsForFingerPrinting();
        }
        
        return result;
    }

    @Override
    public List<Integer> getDatasetIdsForMapping(int start, int numOfRows) throws MiddlewareQueryException{
        DatasetDAO dao = new DatasetDAO();
        List<Integer> datasetIds = new ArrayList<Integer>();
        long centralCount = 0;
        long localCount = 0;
        long relativeLimit = 0;
        
        Session sessionForCentral = getCurrentSessionForCentral();
        Session sessionForLocal = getCurrentSessionForLocal();
        
        if(sessionForCentral != null) {
            
            dao.setSession(sessionForCentral);
            centralCount = dao.countDatasetIdsForMapping();
            
            if(centralCount > start) {
                datasetIds.addAll(dao.getDatasetIdsForMapping(start, numOfRows));
                relativeLimit = numOfRows - datasetIds.size();
                if(relativeLimit > 0 && sessionForLocal != null) {
                    dao.setSession(sessionForLocal);
                    localCount = dao.countDatasetIdsForMapping();
                    if(localCount > 0) {
                        datasetIds.addAll(dao.getDatasetIdsForMapping(0, (int) relativeLimit));
                    }
                }
            } else {
                relativeLimit = start - centralCount;
                if (sessionForLocal != null) {
                    dao.setSession(sessionForLocal);
                    localCount = dao.countDatasetIdsForMapping();
                    if (localCount > relativeLimit) {
                        datasetIds.addAll(dao.getDatasetIdsForMapping((int) relativeLimit, numOfRows));
                    }
                }
            }
        } else if (sessionForLocal != null) {
            dao.setSession(sessionForLocal);
            localCount = dao.countDatasetIdsForMapping();
            if (localCount > start) {
                datasetIds.addAll(dao.getDatasetIdsForMapping(start, numOfRows));
            }
        }
        
        return datasetIds;
        
    }
    
    @Override
    public long countDatasetIdsForMapping() throws MiddlewareQueryException{
        DatasetDAO dao = new DatasetDAO();
        long result = 0;
        
        Session sessionForCentral = getCurrentSessionForCentral();
        Session sessionForLocal = getCurrentSessionForLocal();
        
        if(sessionForCentral != null) {
            dao.setSession(sessionForCentral);
            result = dao.countDatasetIdsForMapping();
        }
        
        if(sessionForLocal != null) {
            dao.setSession(sessionForLocal);
            result += dao.countDatasetIdsForMapping();
        }
        
        return result;
    }

    @Override
    public List<AccMetadataSetPK> getGdmsAccMetadatasetByGid(List<Integer> gids, int start, int numOfRows) throws MiddlewareQueryException{
       
        AccMetadataSetDAO dao = new AccMetadataSetDAO();

        long centralCount = 0;
        long localCount = 0;
        long relativeLimit = 0;

        Session sessionForCentral = getCurrentSessionForCentral();
        Session sessionForLocal = getCurrentSessionForLocal();
        
        List<Integer> positiveGids = new ArrayList<Integer>();
        List<Integer> negativeGids = new ArrayList<Integer>();
        for (Integer gid : gids){
            if (gid < 0) {
                negativeGids.add(gid);
            } else {
                positiveGids.add(gid);
            }
        }

        List<AccMetadataSetPK> accMetadataSets = new ArrayList<AccMetadataSetPK>();

        if ((sessionForCentral != null) && (positiveGids != null) && (!positiveGids.isEmpty())) {
            
            dao.setSession(sessionForCentral);
            centralCount = dao.countAccMetadataSetByGids(positiveGids);
            
            if(centralCount > start) {
                accMetadataSets.addAll(dao.getAccMetadasetByGids(positiveGids, start, numOfRows));
                relativeLimit = numOfRows - accMetadataSets.size();
                if(relativeLimit > 0 && sessionForLocal != null) {
                    dao.setSession(sessionForLocal);
                    localCount = dao.countAccMetadataSetByGids(negativeGids);
                    if(localCount > 0) {
                        accMetadataSets.addAll(dao.getAccMetadasetByGids(negativeGids, 0, (int) relativeLimit));
                    }
                }
            } else {
                relativeLimit = start - centralCount;
                if ((sessionForLocal != null) && (negativeGids != null) && (!negativeGids.isEmpty())){
                    dao.setSession(sessionForLocal);
                    localCount = dao.countAccMetadataSetByGids(negativeGids);
                    if (localCount > relativeLimit) {
                        accMetadataSets.addAll(dao.getAccMetadasetByGids(negativeGids, (int) relativeLimit, numOfRows));
                    }
                }
            }
        } else if ((sessionForLocal != null) && (negativeGids != null) && (!negativeGids.isEmpty())) {
            dao.setSession(sessionForLocal);
            localCount = dao.countAccMetadataSetByGids(negativeGids);
            if (localCount > start) {
                accMetadataSets.addAll(dao.getAccMetadasetByGids(negativeGids, start, numOfRows));
            }
        }
        
        return accMetadataSets;

    }
    
    @Override
    public long countGdmsAccMetadatasetByGid(List<Integer> gids) throws MiddlewareQueryException{
        
        AccMetadataSetDAO dao = new AccMetadataSetDAO();

        Session sessionForCentral = getCurrentSessionForCentral();
        Session sessionForLocal = getCurrentSessionForLocal();

        List<Integer> positiveGids = new ArrayList<Integer>();
        List<Integer> negativeGids = new ArrayList<Integer>();
        for (Integer gid : gids){
            if (gid < 0) {
                negativeGids.add(gid);
            } else {
                positiveGids.add(gid);
            }
        }

        long result = 0;

        // Count from local
        if ((sessionForLocal != null) && (negativeGids != null) && (!negativeGids.isEmpty())){
            dao.setSession(sessionForLocal);
            result += dao.countAccMetadataSetByGids(negativeGids);
        }

        // Count from central
        if ((sessionForCentral != null) && (positiveGids != null) && (!positiveGids.isEmpty())) {
            dao.setSession(sessionForCentral);
            result += dao.countAccMetadataSetByGids(positiveGids);
        }

        return result;

    }

    @Override
    public List<Integer> getMarkersByGidAndDatasetIds(Integer gid, List<Integer> datasetIds, int start, int numOfRows) throws MiddlewareQueryException{
        MarkerMetadataSetDAO dao = new MarkerMetadataSetDAO();
        List<Integer> markerIds = new ArrayList<Integer>();
        long centralCount = 0;
        long localCount = 0;
        long relativeLimit = 0;
        
        Session sessionForCentral = getCurrentSessionForCentral();
        Session sessionForLocal = getCurrentSessionForLocal();
        
        if(sessionForCentral != null) {
            
            dao.setSession(sessionForCentral);
            centralCount = dao.countMarkersByGidAndDatasetIds(gid, datasetIds);
            
            if(centralCount > start) {
                markerIds.addAll(dao.getMarkersByGidAndDatasetIds(gid, datasetIds, start, numOfRows));
                relativeLimit = numOfRows - markerIds.size();
                if(relativeLimit > 0 && sessionForLocal != null) {
                    dao.setSession(sessionForLocal);
                    localCount = dao.countMarkersByGidAndDatasetIds(gid, datasetIds);
                    if(localCount > 0) {
                        markerIds.addAll(dao.getMarkersByGidAndDatasetIds(gid, datasetIds, 0, (int) relativeLimit));
                    }
                }
            } else {
                relativeLimit = start - centralCount;
                if (sessionForLocal != null) {
                    dao.setSession(sessionForLocal);
                    localCount = dao.countMarkersByGidAndDatasetIds(gid, datasetIds);
                    if (localCount > relativeLimit) {
                        markerIds.addAll(dao.getMarkersByGidAndDatasetIds(gid, datasetIds, (int) relativeLimit, numOfRows));
                    }
                }
            }
        } else if (sessionForLocal != null) {
            dao.setSession(sessionForLocal);
            localCount = dao.countMarkersByGidAndDatasetIds(gid, datasetIds);
            if (localCount > start) {
                markerIds.addAll(dao.getMarkersByGidAndDatasetIds(gid, datasetIds, start, numOfRows));
            }
        }
        
        return markerIds;
    }

    @Override
    public long countMarkersByGidAndDatasetIds(Integer gid, List<Integer> datasetIds) throws MiddlewareQueryException{
        MarkerMetadataSetDAO dao = new MarkerMetadataSetDAO();
        long result = 0;
        
        Session sessionForCentral = getCurrentSessionForCentral();
        Session sessionForLocal = getCurrentSessionForLocal();
        
        if(sessionForCentral != null) {
            dao.setSession(sessionForCentral);
            result = dao.countMarkersByGidAndDatasetIds(gid, datasetIds);
        }
        
        if(sessionForLocal != null) {
            dao.setSession(sessionForLocal);
            result += dao.countMarkersByGidAndDatasetIds(gid, datasetIds);
        }
        
        return result;
        
    }
    
    @Override
    public List<Marker> getMarkersByMarkerIDs(List<Integer> markerIDs, int start, int numOfRows) throws MiddlewareQueryException{
        MarkerDAO dao = new MarkerDAO();
        List<Marker> markerIds = new ArrayList<Marker>();
        long centralCount = 0;
        long localCount = 0;
        long relativeLimit = 0;
        
        Session sessionForCentral = getCurrentSessionForCentral();
        Session sessionForLocal = getCurrentSessionForLocal();
        
        if(sessionForCentral != null) {
            
            dao.setSession(sessionForCentral);
            centralCount = dao.countMarkersByIds(markerIDs);
            
            if(centralCount > start) {
                markerIds.addAll(dao.getMarkersByIds(markerIDs, start, numOfRows));
                relativeLimit = numOfRows - (centralCount -start);
                if(relativeLimit > 0 && sessionForLocal != null) {
                    dao.setSession(sessionForLocal);
                    localCount = dao.countMarkersByIds(markerIDs);
                    if(localCount > 0) {
                        markerIds.addAll(dao.getMarkersByIds(markerIDs, 0, (int) relativeLimit));
                    }
                }
            } else {
                relativeLimit = start - centralCount;
                if (sessionForLocal != null) {
                    dao.setSession(sessionForLocal);
                    localCount = dao.countMarkersByIds(markerIDs);
                    if (localCount > relativeLimit) {
                        markerIds.addAll(dao.getMarkersByIds(markerIDs, (int) relativeLimit, numOfRows));
                    }
                }
            }
        } else if (sessionForLocal != null) {
            dao.setSession(sessionForLocal);
            localCount = dao.countMarkersByIds(markerIDs);
            if (localCount > start) {
                markerIds.addAll(dao.getMarkersByIds(markerIDs, start, numOfRows));
            }
        }
        
        return markerIds;
    }

    @Override
    public long countMarkersByMarkerIDs(List<Integer> markerIDs) throws MiddlewareQueryException{
        MarkerDAO dao = new MarkerDAO();
        long result = 0;
        
        Session sessionForCentral = getCurrentSessionForCentral();
        Session sessionForLocal = getCurrentSessionForLocal();
        
        if(sessionForCentral != null) {
            dao.setSession(sessionForCentral);
            result = dao.countMarkersByIds(markerIDs);
        }
        
        if(sessionForLocal != null) {
            dao.setSession(sessionForLocal);
            result += dao.countMarkersByIds(markerIDs);
        }
        
        return result;
        
    }    
    
    @Override
    public long countAlleleValuesByGids(List<Integer> gids) throws MiddlewareQueryException{

        AlleleValuesDAO dao = new AlleleValuesDAO();

        Session sessionForCentral = getCurrentSessionForCentral();
        Session sessionForLocal = getCurrentSessionForLocal();

        long result = 0;

        // Count from local
        if (sessionForLocal != null) {
            dao.setSession(sessionForLocal);
            result += dao.countAlleleValuesByGids(gids);
        }

        // Count from central
        if (sessionForCentral != null) {
            dao.setSession(sessionForCentral);
            result += dao.countAlleleValuesByGids(gids);
        }

        return result;
    }


    @Override
    public long countCharValuesByGids(List<Integer> gids) throws MiddlewareQueryException{

        CharValuesDAO dao = new CharValuesDAO();

        Session sessionForCentral = getCurrentSessionForCentral();
        Session sessionForLocal = getCurrentSessionForLocal();

        long result = 0;

        // Count from local
        if (sessionForLocal != null) {
            dao.setSession(sessionForLocal);
            result += dao.countCharValuesByGids(gids);
        }

        // Count from central
        if (sessionForCentral != null) {
            dao.setSession(sessionForCentral);
            result += dao.countCharValuesByGids(gids);
        }

        return result;
    }


        

    @Override
    public List<AllelicValueElement> getIntAlleleValuesForPolymorphicMarkersRetrieval(List<Integer> gids, int start, int numOfRows) throws MiddlewareQueryException{
        AlleleValuesDAO dao = new AlleleValuesDAO();

        long centralCount = 0;
        long localCount = 0;
        long relativeLimit = 0;

        Session sessionForCentral = getCurrentSessionForCentral();
        Session sessionForLocal = getCurrentSessionForLocal();
        
        List<Integer> positiveGids = new ArrayList<Integer>();
        List<Integer> negativeGids = new ArrayList<Integer>();
        for (Integer gid : gids){
            if (gid < 0) {
                negativeGids.add(gid);
            } else {
                positiveGids.add(gid);
            }
        }

        List<AllelicValueElement> allelicValueElements = new ArrayList<AllelicValueElement>();

        if ((sessionForCentral != null) && (positiveGids != null) && (!positiveGids.isEmpty())) {
            
            dao.setSession(sessionForCentral);
            centralCount = dao.countIntAlleleValuesForPolymorphicMarkersRetrieval(positiveGids);
            
            if(centralCount > start) {
                allelicValueElements.addAll(dao.getIntAlleleValuesForPolymorphicMarkersRetrieval(positiveGids, start, numOfRows));
                relativeLimit = numOfRows - allelicValueElements.size();
                if(relativeLimit > 0 && sessionForLocal != null) {
                    dao.setSession(sessionForLocal);
                    localCount = dao.countIntAlleleValuesForPolymorphicMarkersRetrieval(negativeGids);
                    if(localCount > 0) {
                        allelicValueElements.addAll(dao.getIntAlleleValuesForPolymorphicMarkersRetrieval(negativeGids, 0, (int) relativeLimit));
                    }
                }
            } else {
                relativeLimit = start - centralCount;
                if ((sessionForLocal != null) && (negativeGids != null) && (!negativeGids.isEmpty())){
                    dao.setSession(sessionForLocal);
                    localCount = dao.countIntAlleleValuesForPolymorphicMarkersRetrieval(negativeGids);
                    if (localCount > relativeLimit) {
                        allelicValueElements.addAll(dao.getIntAlleleValuesForPolymorphicMarkersRetrieval(negativeGids, (int) relativeLimit, numOfRows));
                    }
                }
            }
        } else if ((sessionForLocal != null) && (negativeGids != null) && (!negativeGids.isEmpty())) {
            dao.setSession(sessionForLocal);
            localCount = dao.countIntAlleleValuesForPolymorphicMarkersRetrieval(negativeGids);
            if (localCount > start) {
                allelicValueElements.addAll(dao.getIntAlleleValuesForPolymorphicMarkersRetrieval(negativeGids, start, numOfRows));
            }
        }
        
        //Sort by gid, markerName
        Collections.sort(allelicValueElements, AllelicValueElement.AllelicValueElementComparator);

        return allelicValueElements;
    }

    
    @Override
    public long countIntAlleleValuesForPolymorphicMarkersRetrieval(List<Integer> gids) throws MiddlewareQueryException{
        AlleleValuesDAO dao = new AlleleValuesDAO();

        Session sessionForCentral = getCurrentSessionForCentral();
        Session sessionForLocal = getCurrentSessionForLocal();

        List<Integer> positiveGids = new ArrayList<Integer>();
        List<Integer> negativeGids = new ArrayList<Integer>();
        for (Integer gid : gids){
            if (gid < 0) {
                negativeGids.add(gid);
            } else {
                positiveGids.add(gid);
            }
        }

        long result = 0;

        // Count from local
        if ((sessionForLocal != null) && (negativeGids != null) && (!negativeGids.isEmpty())){
            dao.setSession(sessionForLocal);
            result += dao.countIntAlleleValuesForPolymorphicMarkersRetrieval(negativeGids);
        }

        // Count from central
        if ((sessionForCentral != null) && (positiveGids != null) && (!positiveGids.isEmpty())) {
            dao.setSession(sessionForCentral);
            result += dao.countIntAlleleValuesForPolymorphicMarkersRetrieval(positiveGids);
        }

        return result;
    }


    @Override
    public List<AllelicValueElement> getCharAlleleValuesForPolymorphicMarkersRetrieval(List<Integer> gids, int start, int numOfRows) throws MiddlewareQueryException{
        AlleleValuesDAO dao = new AlleleValuesDAO();

        long centralCount = 0;
        long localCount = 0;
        long relativeLimit = 0;

        Session sessionForCentral = getCurrentSessionForCentral();
        Session sessionForLocal = getCurrentSessionForLocal();
        
        List<Integer> positiveGids = new ArrayList<Integer>();
        List<Integer> negativeGids = new ArrayList<Integer>();
        for (Integer gid : gids){
            if (gid < 0) {
                negativeGids.add(gid);
            } else {
                positiveGids.add(gid);
            }
        }

        List<AllelicValueElement> allelicValueElements = new ArrayList<AllelicValueElement>();

        if ((sessionForCentral != null) && (positiveGids != null) && (!positiveGids.isEmpty())) {
            
            dao.setSession(sessionForCentral);
            centralCount = dao.countCharAlleleValuesForPolymorphicMarkersRetrieval(positiveGids);
            
            if(centralCount > start) {
                allelicValueElements.addAll(dao.getCharAlleleValuesForPolymorphicMarkersRetrieval(positiveGids, start, numOfRows));
                relativeLimit = numOfRows - allelicValueElements.size();
                if(relativeLimit > 0 && sessionForLocal != null) {
                    dao.setSession(sessionForLocal);
                    localCount = dao.countCharAlleleValuesForPolymorphicMarkersRetrieval(negativeGids);
                    if(localCount > 0) {
                        allelicValueElements.addAll(dao.getCharAlleleValuesForPolymorphicMarkersRetrieval(negativeGids, 0, (int) relativeLimit));
                    }
                }
            } else {
                relativeLimit = start - centralCount;
                if ((sessionForLocal != null) && (negativeGids != null) && (!negativeGids.isEmpty())){
                    dao.setSession(sessionForLocal);
                    localCount = dao.countCharAlleleValuesForPolymorphicMarkersRetrieval(negativeGids);
                    if (localCount > relativeLimit) {
                        allelicValueElements.addAll(dao.getCharAlleleValuesForPolymorphicMarkersRetrieval(negativeGids, (int) relativeLimit, numOfRows));
                    }
                }
            }
        } else if ((sessionForLocal != null) && (negativeGids != null) && (!negativeGids.isEmpty())){
            dao.setSession(sessionForLocal);
            localCount = dao.countCharAlleleValuesForPolymorphicMarkersRetrieval(negativeGids);
            if (localCount > start) {
                allelicValueElements.addAll(dao.getCharAlleleValuesForPolymorphicMarkersRetrieval(negativeGids, start, numOfRows));
            }
        }
        
        //Sort by gid, markerName
        Collections.sort(allelicValueElements, AllelicValueElement.AllelicValueElementComparator);

        return allelicValueElements;
    }

    @Override
    public long countCharAlleleValuesForPolymorphicMarkersRetrieval(List<Integer> gids) throws MiddlewareQueryException{
        AlleleValuesDAO dao = new AlleleValuesDAO();

        Session sessionForCentral = getCurrentSessionForCentral();
        Session sessionForLocal = getCurrentSessionForLocal();

        List<Integer> positiveGids = new ArrayList<Integer>();
        List<Integer> negativeGids = new ArrayList<Integer>();
        for (Integer gid : gids){
            if (gid < 0) {
                negativeGids.add(gid);
            } else {
                positiveGids.add(gid);
            }
        }

        long result = 0;

        // Count from local
        if ((sessionForLocal != null) && (negativeGids != null) && (!negativeGids.isEmpty())){
            dao.setSession(sessionForLocal);
            result += dao.countCharAlleleValuesForPolymorphicMarkersRetrieval(negativeGids);
        }

        // Count from central
        if ((sessionForCentral != null) && (positiveGids != null) && (!positiveGids.isEmpty())) {
            dao.setSession(sessionForCentral);
            result += dao.countCharAlleleValuesForPolymorphicMarkersRetrieval(positiveGids);
        }

        return result;
    }

    
    @Override
    public List<AllelicValueElement> getMappingAlleleValuesForPolymorphicMarkersRetrieval(List<Integer> gids, int start, int numOfRows) throws MiddlewareQueryException{
        AlleleValuesDAO dao = new AlleleValuesDAO();

        long centralCount = 0;
        long localCount = 0;
        long relativeLimit = 0;

        Session sessionForCentral = getCurrentSessionForCentral();
        Session sessionForLocal = getCurrentSessionForLocal();
        
        List<Integer> positiveGids = new ArrayList<Integer>();
        List<Integer> negativeGids = new ArrayList<Integer>();
        for (Integer gid : gids){
            if (gid < 0) {
                negativeGids.add(gid);
            } else {
                positiveGids.add(gid);
            }
        }

        List<AllelicValueElement> allelicValueElements = new ArrayList<AllelicValueElement>();

        if ((sessionForCentral != null) && (positiveGids != null) && (!positiveGids.isEmpty())) {
            
            dao.setSession(sessionForCentral);
            centralCount = dao.countMappingAlleleValuesForPolymorphicMarkersRetrieval(positiveGids);
            
            if(centralCount > start) {
                allelicValueElements.addAll(dao.getMappingAlleleValuesForPolymorphicMarkersRetrieval(positiveGids, start, numOfRows));
                relativeLimit = numOfRows - allelicValueElements.size();
                if(relativeLimit > 0 && sessionForLocal != null) {
                    dao.setSession(sessionForLocal);
                    localCount = dao.countMappingAlleleValuesForPolymorphicMarkersRetrieval(negativeGids);
                    if(localCount > 0) {
                        allelicValueElements.addAll(dao.getMappingAlleleValuesForPolymorphicMarkersRetrieval(negativeGids, 0, (int) relativeLimit));
                    }
                }
            } else {
                relativeLimit = start - centralCount;
                if ((sessionForLocal != null) && (negativeGids != null) && (!negativeGids.isEmpty())){
                    dao.setSession(sessionForLocal);
                    localCount = dao.countMappingAlleleValuesForPolymorphicMarkersRetrieval(negativeGids);
                    if (localCount > relativeLimit) {
                        allelicValueElements.addAll(dao.getMappingAlleleValuesForPolymorphicMarkersRetrieval(negativeGids, (int) relativeLimit, numOfRows));
                    }
                }
            }
        } else if ((sessionForLocal != null) && (negativeGids != null) && (!negativeGids.isEmpty())){
            dao.setSession(sessionForLocal);
            localCount = dao.countMappingAlleleValuesForPolymorphicMarkersRetrieval(negativeGids);
            if (localCount > start) {
                allelicValueElements.addAll(dao.getMappingAlleleValuesForPolymorphicMarkersRetrieval(negativeGids, start, numOfRows));
            }
        }
        
        //Sort by gid, markerName
        Collections.sort(allelicValueElements, AllelicValueElement.AllelicValueElementComparator);

        return allelicValueElements;
    }

    @Override
    public long countMappingAlleleValuesForPolymorphicMarkersRetrieval(List<Integer> gids) throws MiddlewareQueryException{
        AlleleValuesDAO dao = new AlleleValuesDAO();

        Session sessionForCentral = getCurrentSessionForCentral();
        Session sessionForLocal = getCurrentSessionForLocal();

        List<Integer> positiveGids = new ArrayList<Integer>();
        List<Integer> negativeGids = new ArrayList<Integer>();
        for (Integer gid : gids){
            if (gid < 0) {
                negativeGids.add(gid);
            } else {
                positiveGids.add(gid);
            }
        }

        long result = 0;

        // Count from local
        if ((sessionForLocal != null) && (negativeGids != null) && (!negativeGids.isEmpty())){
            dao.setSession(sessionForLocal);
            result += dao.countMappingAlleleValuesForPolymorphicMarkersRetrieval(negativeGids);
        }

        // Count from central
        if ((sessionForCentral != null) && (positiveGids != null) && (!positiveGids.isEmpty())) {
            dao.setSession(sessionForCentral);
            result += dao.countMappingAlleleValuesForPolymorphicMarkersRetrieval(positiveGids);
        }

        return result;
    }
    
    @Override
    public List<Qtl> getAllQtl(int start, int numOfRows) throws MiddlewareQueryException{
        QtlDAO dao = new QtlDAO();

        long centralCount = 0;
        long localCount = 0;
        long relativeLimit = 0;

        Session sessionForCentral = getCurrentSessionForCentral();
        Session sessionForLocal = getCurrentSessionForLocal();
        
        List<Qtl> qtl = new ArrayList<Qtl>();

        if(sessionForCentral != null) {
            
            dao.setSession(sessionForCentral);
            centralCount = dao.countAll();
            
            if(centralCount > start) {
                qtl.addAll(dao.getAll(start, numOfRows));
                relativeLimit = numOfRows - qtl.size();
                if(relativeLimit > 0 && sessionForLocal != null) {
                    dao.setSession(sessionForLocal);
                    localCount = dao.countAll();
                    if(localCount > 0) {
                        qtl.addAll(dao.getAll( 0, (int) relativeLimit));
                    }
                }
            } else {
                relativeLimit = start - centralCount;
                if (sessionForLocal != null) {
                    dao.setSession(sessionForLocal);
                    localCount = dao.countAll();
                    if (localCount > relativeLimit) {
                        qtl.addAll(dao.getAll((int) relativeLimit, numOfRows));
                    }
                }
            }
        } else if (sessionForLocal != null) {
            dao.setSession(sessionForLocal);
            localCount = dao.countAll();
            if (localCount > start) {
                qtl.addAll(dao.getAll(start, numOfRows));
            }
        }
        
        return qtl;
    }
    
    @Override
    public long countAllQtl() throws MiddlewareQueryException{
        QtlDAO dao = new QtlDAO();

        Session sessionForCentral = getCurrentSessionForCentral();
        Session sessionForLocal = getCurrentSessionForLocal();

        long result = 0;

        // Count from local
        if (sessionForLocal != null) {
            dao.setSession(sessionForLocal);
            result += dao.countAll();
        }

        // Count from central
        if (sessionForCentral != null) {
            dao.setSession(sessionForCentral);
            result += dao.countAll();
        }

        return result;

    }

    @Override
    public List<QtlDetailElement> getQtlByName(String name, int start, int numOfRows) throws MiddlewareQueryException{
        
        if ((name == null) || (name.isEmpty())){
            return new ArrayList<QtlDetailElement>();
        }
        
        QtlDAO dao = new QtlDAO();
        
        long centralCount = 0;
        long localCount = 0;
        long relativeLimit = 0;

        Session sessionForCentral = getCurrentSessionForCentral();
        Session sessionForLocal = getCurrentSessionForLocal();
        
        List<QtlDetailElement> qtl = new ArrayList<QtlDetailElement>();

        if(sessionForCentral != null) {
            
            dao.setSession(sessionForCentral);
            centralCount = dao.countQtlDetailsByName(name);
            
            if(centralCount > start) {
                qtl.addAll(dao.getQtlDetailsByName(name, start, numOfRows));
                relativeLimit = numOfRows - qtl.size();
                if(relativeLimit > 0 && sessionForLocal != null) {
                    dao.setSession(sessionForLocal);
                    localCount = dao.countQtlDetailsByName(name);
                    if(localCount > 0) {
                        qtl.addAll(dao.getQtlDetailsByName(name, 0, (int) relativeLimit));
                    }
                }
            } else {
                relativeLimit = start - centralCount;
                if (sessionForLocal != null) {
                    dao.setSession(sessionForLocal);
                    localCount = dao.countQtlDetailsByName(name);
                    if (localCount > relativeLimit) {
                        qtl.addAll(dao.getQtlDetailsByName(name, (int) relativeLimit, numOfRows));
                    }
                }
            }
        } else if (sessionForLocal != null) {
            dao.setSession(sessionForLocal);
            localCount = dao.countQtlDetailsByName(name);
            if (localCount > start) {
                qtl.addAll(dao.getQtlDetailsByName(name, start, numOfRows));
            }
        }
        
        return qtl;    
    }
    
    @Override
    public long countQtlByName(String name) throws MiddlewareQueryException{
        
        if ((name == null) || (name.isEmpty())){
            return 0;
        }
                
        QtlDAO dao = new QtlDAO();

        Session sessionForCentral = getCurrentSessionForCentral();
        Session sessionForLocal = getCurrentSessionForLocal();

        long result = 0;

        // Count from local
        if (sessionForLocal != null) {
            dao.setSession(sessionForLocal);
            result += dao.countQtlDetailsByName(name);
        }

        // Count from central
        if (sessionForCentral != null) {
            dao.setSession(sessionForCentral);
            result += dao.countQtlDetailsByName(name);
        }

        return result;
    }
    
    @Override
    public List<QtlDetailElement> getQTLByQTLIDs(List<Integer> qtlIDs, int start, int numOfRows) throws MiddlewareQueryException{
        
        if ((qtlIDs == null) || (qtlIDs.isEmpty())){
            return new ArrayList<QtlDetailElement>();
        }
        
        QtlDAO dao = new QtlDAO();
        
        long centralCount = 0;
        long localCount = 0;
        long relativeLimit = 0;

        Session sessionForCentral = getCurrentSessionForCentral();
        Session sessionForLocal = getCurrentSessionForLocal();
        
        List<QtlDetailElement> qtl = new ArrayList<QtlDetailElement>();

        if(sessionForCentral != null) {
            
            dao.setSession(sessionForCentral);
            centralCount = dao.countQtlDetailsByQTLIDs(qtlIDs);
            
            if(centralCount > start) {
                qtl.addAll(dao.getQtlDetailsByQTLIDs(qtlIDs, start, numOfRows));
                relativeLimit = numOfRows - qtl.size();
                if(relativeLimit > 0 && sessionForLocal != null) {
                    dao.setSession(sessionForLocal);
                    localCount = dao.countQtlDetailsByQTLIDs(qtlIDs);
                    if(localCount > 0) {
                        qtl.addAll(dao.getQtlDetailsByQTLIDs(qtlIDs, 0, (int) relativeLimit));
                    }
                }
            } else {
                relativeLimit = start - centralCount;
                if (sessionForLocal != null) {
                    dao.setSession(sessionForLocal);
                    localCount = dao.countQtlDetailsByQTLIDs(qtlIDs);
                    if (localCount > relativeLimit) {
                        qtl.addAll(dao.getQtlDetailsByQTLIDs(qtlIDs, (int) relativeLimit, numOfRows));
                    }
                }
            }
        } else if (sessionForLocal != null) {
            dao.setSession(sessionForLocal);
            localCount = dao.countQtlDetailsByQTLIDs(qtlIDs);
            if (localCount > start) {
                qtl.addAll(dao.getQtlDetailsByQTLIDs(qtlIDs, start, numOfRows));
            }
        }
        
        return qtl;    
    }
    
    @Override
    public long countQTLByQTLIDs(List<Integer> qtlIDs) throws MiddlewareQueryException{
        
        if ((qtlIDs == null) || (qtlIDs.isEmpty())){
            return 0;
        }
                
        QtlDAO dao = new QtlDAO();

        Session sessionForCentral = getCurrentSessionForCentral();
        Session sessionForLocal = getCurrentSessionForLocal();

        long result = 0;

        // Count from local
        if (sessionForLocal != null) {
            dao.setSession(sessionForLocal);
            result += dao.countQtlDetailsByQTLIDs(qtlIDs);
        }

        // Count from central
        if (sessionForCentral != null) {
            dao.setSession(sessionForCentral);
            result += dao.countQtlDetailsByQTLIDs(qtlIDs);
        }

        return result;
    }
    
    @Override
    public List<Integer> getQtlByTrait(String trait, int start, int numOfRows) throws MiddlewareQueryException{
        QtlDAO dao = new QtlDAO();

        long centralCount = 0;
        long localCount = 0;
        long relativeLimit = 0;

        Session sessionForCentral = getCurrentSessionForCentral();
        Session sessionForLocal = getCurrentSessionForLocal();
        
        List<Integer> qtl = new ArrayList<Integer>();

        if(sessionForCentral != null) {
            
            dao.setSession(sessionForCentral);
            centralCount = dao.countQtlByTrait(trait);
            
            if(centralCount > start) {
                qtl.addAll(dao.getQtlByTrait(trait, start, numOfRows));
                relativeLimit = numOfRows - qtl.size();
                if(relativeLimit > 0 && sessionForLocal != null) {
                    dao.setSession(sessionForLocal);
                    localCount = dao.countQtlByTrait(trait);
                    if(localCount > 0) {
                        qtl.addAll(dao.getQtlByTrait(trait, 0, (int) relativeLimit));
                    }
                }
            } else {
                relativeLimit = start - centralCount;
                if (sessionForLocal != null) {
                    dao.setSession(sessionForLocal);
                    localCount = dao.countQtlByTrait(trait);
                    if (localCount > relativeLimit) {
                        qtl.addAll(dao.getQtlByTrait(trait, (int) relativeLimit, numOfRows));
                    }
                }
            }
        } else if (sessionForLocal != null) {
            dao.setSession(sessionForLocal);
            localCount = dao.countQtlByTrait(trait);
            if (localCount > start) {
                qtl.addAll(dao.getQtlByTrait(trait, start, numOfRows));
            }
        }
        
        return qtl;    

    }
    
    @Override
    public long countQtlByTrait(String trait) throws MiddlewareQueryException{
        QtlDAO dao = new QtlDAO();
        
        Session sessionForCentral = getCurrentSessionForCentral();
        Session sessionForLocal = getCurrentSessionForLocal();

        long result = 0;

        // Count from local
        if (sessionForLocal != null) {
            dao.setSession(sessionForLocal);
            result += dao.countQtlByTrait(trait);
        }

        // Count from central
        if (sessionForCentral != null) {
            dao.setSession(sessionForCentral);
            result += dao.countQtlByTrait(trait);
        }

        return result;

    }
    @Override
    public List<ParentElement> getAllParentsFromMappingPopulation(
            int start, int numOfRows) throws MiddlewareQueryException {

        Long centralCount = Long.valueOf(0);
        Long localCount = Long.valueOf(0);
        int relativeLimit = 0;    	
    	
        MappingPopDAO mappingPopDao = new MappingPopDAO();
       
        List<ParentElement> allParentsFromMappingPopulation = new ArrayList<ParentElement>();
   
        Session sessionForCentral = getCurrentSessionForCentral();
        Session sessionForLocal = getCurrentSessionForLocal();

        if (sessionForCentral != null) {
            mappingPopDao.setSession(sessionForCentral);
            centralCount = mappingPopDao.countAllParentsFromMappingPopulation();
            
            if (centralCount > start) {
                allParentsFromMappingPopulation.addAll(mappingPopDao.getAllParentsFromMappingPopulation(start, numOfRows));
                relativeLimit = numOfRows - (centralCount.intValue() - start);

                if (relativeLimit > 0) {
                	
                    if (sessionForLocal != null) {
                        mappingPopDao.setSession(sessionForLocal);
                        localCount = mappingPopDao.countAllParentsFromMappingPopulation();
                        
                        if (localCount > 0) {
                            allParentsFromMappingPopulation.addAll(mappingPopDao.getAllParentsFromMappingPopulation(0, relativeLimit));
                        }
                    }
                }
            } else {
                relativeLimit = start - centralCount.intValue();
                if (sessionForLocal != null) {
                    mappingPopDao.setSession(sessionForLocal);
                    localCount = mappingPopDao.countAllParentsFromMappingPopulation();
                    if (localCount > relativeLimit) {
                        allParentsFromMappingPopulation.addAll(mappingPopDao.getAllParentsFromMappingPopulation(relativeLimit, numOfRows));
                    }
                }
            }
        } else if (sessionForLocal != null) {
            mappingPopDao.setSession(sessionForLocal);
            localCount = mappingPopDao.countAllParentsFromMappingPopulation();
            if (localCount > start) {
                allParentsFromMappingPopulation.addAll(mappingPopDao.getAllParentsFromMappingPopulation(start, numOfRows));
            }
        }
   
        return allParentsFromMappingPopulation;
    }

    @Override
    public Long countAllParentsFromMappingPopulation() throws MiddlewareQueryException {
    	
        MappingPopDAO mappingPopDao = new MappingPopDAO();
           
        Database centralInstance = Database.CENTRAL;
        Session centralSession = getSession(centralInstance);
        mappingPopDao.setSession(centralSession);
        Long centralCountParentsFromMappingPopulation = mappingPopDao.countAllParentsFromMappingPopulation();
        
        Database localInstance = Database.LOCAL;
        Session localSession = getSession(localInstance);
        mappingPopDao.setSession(localSession);
        Long localCountParentsFromMappingPopulation = mappingPopDao.countAllParentsFromMappingPopulation();
        
        Long totalCountParentsFromMappingPopulation = localCountParentsFromMappingPopulation + centralCountParentsFromMappingPopulation;
        
        return totalCountParentsFromMappingPopulation;
    }
    
    @Override
    public List<MapDetailElement> getMapDetailsByName(
            String nameLike, int start, int numOfRows) throws MiddlewareQueryException {

        Long centralCount = Long.valueOf(0);
        Long localCount = Long.valueOf(0);
        Long relativeLimit = Long.valueOf(0);    	
    	
        MapDAO mapDao = new MapDAO();
       
        List<MapDetailElement> maps = new ArrayList<MapDetailElement>();
   
        Session sessionForCentral = getCurrentSessionForCentral();
        Session sessionForLocal = getCurrentSessionForLocal();

        if (sessionForCentral != null) {
            mapDao.setSession(sessionForCentral);
            centralCount = mapDao.countMapDetailsByName(nameLike);
            
            if (centralCount > start) {
                maps.addAll(mapDao.getMapDetailsByName(nameLike, start, numOfRows));
                relativeLimit = numOfRows - (centralCount - start);

                if (relativeLimit > 0) {
                	
                    if (sessionForLocal != null) {
                        mapDao.setSession(sessionForLocal);
                        localCount = mapDao.countMapDetailsByName(nameLike);
                        
                        if (localCount > 0) {
                            maps.addAll(mapDao.getMapDetailsByName(nameLike, 0, relativeLimit.intValue()));
                        }
                    }
                }
            } else {
                relativeLimit = start - centralCount;
                if (sessionForLocal != null) {
                    mapDao.setSession(sessionForLocal);
                    localCount = mapDao.countMapDetailsByName(nameLike);
                    if (localCount > relativeLimit) {
                        maps.addAll(mapDao.getMapDetailsByName(nameLike, relativeLimit.intValue(), numOfRows));
                    }
                }
            }
        } else if (sessionForLocal != null) {
            mapDao.setSession(sessionForLocal);
            localCount = mapDao.countMapDetailsByName(nameLike);
            if (localCount > start) {
                maps.addAll(mapDao.getMapDetailsByName(nameLike, start, numOfRows));
            }
        }
     
        return maps;
    }

    @Override
    public Long countMapDetailsByName(String nameLike) throws MiddlewareQueryException {
    	
        MapDAO mapDao = new MapDAO();
           
        Database centralInstance = Database.CENTRAL;
        Session centralSession = getSession(centralInstance);
        mapDao.setSession(centralSession);
        Long centralCount = mapDao.countMapDetailsByName(nameLike);
        
        Database localInstance = Database.LOCAL;
        Session localSession = getSession(localInstance);
        mapDao.setSession(localSession);
        Long localCount = mapDao.countMapDetailsByName(nameLike);
        
        Long totalCount = centralCount + localCount;
        
        return totalCount;
    }


    public List<MapDetailElement> getAllMapDetails(int start, int numOfRows) throws MiddlewareQueryException{

        Long centralCount = Long.valueOf(0);
        Long localCount = Long.valueOf(0);
        Long relativeLimit = Long.valueOf(0);       
        
        MapDAO mapDao = new MapDAO();
       
        List<MapDetailElement> maps = new ArrayList<MapDetailElement>();
   
        Session sessionForCentral = getCurrentSessionForCentral();
        Session sessionForLocal = getCurrentSessionForLocal();

        if (sessionForCentral != null) {
            mapDao.setSession(sessionForCentral);
            centralCount = mapDao.countAllMapDetails();
            
            if (centralCount > start) {
                maps.addAll(mapDao.getAllMapDetails(start, numOfRows));
                relativeLimit = numOfRows - (centralCount - start);

                if (relativeLimit > 0) {
                    
                    if (sessionForLocal != null) {
                        mapDao.setSession(sessionForLocal);
                        localCount = mapDao.countAllMapDetails();
                        
                        if (localCount > 0) {
                            maps.addAll(mapDao.getAllMapDetails(0, relativeLimit.intValue()));
                        }
                    }
                }
            } else {
                relativeLimit = start - centralCount;
                if (sessionForLocal != null) {
                    mapDao.setSession(sessionForLocal);
                    localCount = mapDao.countAllMapDetails();
                    if (localCount > relativeLimit) {
                        maps.addAll(mapDao.getAllMapDetails(relativeLimit.intValue(), numOfRows));
                    }
                }
            }
        } else if (sessionForLocal != null) {
            mapDao.setSession(sessionForLocal);
            localCount = mapDao.countAllMapDetails();
            if (localCount > start) {
                maps.addAll(mapDao.getAllMapDetails(start, numOfRows));
            }
        }
     
        return maps;        
    }
    
    public long countAllMapDetails() throws MiddlewareQueryException{

        MapDAO mapDao = new MapDAO();

        Session sessionForCentral = getCurrentSessionForCentral();
        Session sessionForLocal = getCurrentSessionForLocal();

        long count = 0;        

        // Count from Central
        if (sessionForCentral != null) {
            mapDao.setSession(sessionForCentral);
            count += mapDao.countAllMapDetails();
        }

        // Count from Local
        if (sessionForLocal != null) {
            mapDao.setSession(sessionForLocal);
            count += mapDao.countAllMapDetails();
        }
        
        return count;
    }
    
    @Override
    public List<Integer> getMapIdsByQtlName(String qtlName, int start, int numOfRows) throws MiddlewareQueryException{
        QtlDetailsDAO dao = new QtlDetailsDAO();

        long centralCount = 0;
        long localCount = 0;
        long relativeLimit = 0;

        Session sessionForCentral = getCurrentSessionForCentral();
        Session sessionForLocal = getCurrentSessionForLocal();
        
        List<Integer> mapIds = new ArrayList<Integer>();

        if(sessionForCentral != null) {
            
            dao.setSession(sessionForCentral);
            centralCount = dao.countMapIdsByQtlName(qtlName);
            
            if(centralCount > start) {
                mapIds.addAll(dao.getMapIdsByQtlName(qtlName, start, numOfRows));
                relativeLimit = numOfRows - mapIds.size();
                if(relativeLimit > 0 && sessionForLocal != null) {
                    dao.setSession(sessionForLocal);
                    localCount = dao.countMapIdsByQtlName(qtlName);
                    if(localCount > 0) {
                        mapIds.addAll(dao.getMapIdsByQtlName(qtlName, 0, (int) relativeLimit));
                    }
                }
            } else {
                relativeLimit = start - centralCount;
                if (sessionForLocal != null) {
                    dao.setSession(sessionForLocal);
                    localCount = dao.countMapIdsByQtlName(qtlName);
                    if (localCount > relativeLimit) {
                        mapIds.addAll(dao.getMapIdsByQtlName(qtlName, (int) relativeLimit, numOfRows));
                    }
                }
            }
        } else if (sessionForLocal != null) {
            dao.setSession(sessionForLocal);
            localCount = dao.countMapIdsByQtlName(qtlName);
            if (localCount > start) {
                mapIds.addAll(dao.getMapIdsByQtlName(qtlName, start, numOfRows));
            }
        }
        
        return mapIds;    
    }



    @Override
    public long countMapIdsByQtlName(String qtlName) throws MiddlewareQueryException {
        QtlDetailsDAO qtlDetailsDao = new QtlDetailsDAO();

        Session sessionForCentral = getCurrentSessionForCentral();
        Session sessionForLocal = getCurrentSessionForLocal();

        long count = 0;        

        // Count from Central
        if (sessionForCentral != null) {
            qtlDetailsDao.setSession(sessionForLocal);
            count += qtlDetailsDao.countMapIdsByQtlName(qtlName);
        }

        // Count from Local
        if (sessionForLocal != null) {
            qtlDetailsDao.setSession(sessionForLocal);
            count += qtlDetailsDao.countMapIdsByQtlName(qtlName);
        }
        
        return count;
    }
    
    @Override
    public List<Integer> getMarkerIdsByQtl(String qtlName, String chromosome, int min, int max, int start, int numOfRows)
            throws MiddlewareQueryException {
        //TODO
        return null;

    }
    
    @Override
    public long countMarkerIdsByQtl(String qtlName, String chromosome, int min, int max) throws MiddlewareQueryException {
        QtlDetailsDAO qtlDetailsDao = new QtlDetailsDAO();

        Session sessionForCentral = getCurrentSessionForCentral();
        Session sessionForLocal = getCurrentSessionForLocal();

        long count = 0;        

        // Count from Central
        if (sessionForCentral != null) {
            qtlDetailsDao.setSession(sessionForLocal);
            count += qtlDetailsDao.countMarkerIdsByQtl(qtlName, chromosome, min, max);
        }

        // Count from Local
        if (sessionForLocal != null) {
            qtlDetailsDao.setSession(sessionForLocal);
            count += qtlDetailsDao.countMarkerIdsByQtl(qtlName, chromosome, min, max);
        }
        
        return count;
    }
    
    
    public List<Marker> getMarkersByIds(List<Integer> markerIds, int start, int numOfRows) throws MiddlewareQueryException{

        MarkerDAO dao = new MarkerDAO();
        Session sessionForCentral = getCurrentSessionForCentral();
        Session sessionForLocal = getCurrentSessionForLocal();

        List<Integer> positiveGids = new ArrayList<Integer>();
        List<Integer> negativeGids = new ArrayList<Integer>();
        for (Integer markerId : markerIds){
            if (markerId < 0) {
                negativeGids.add(markerId);
            } else {
                positiveGids.add(markerId);
            }
        }

        List<Marker> markers = new ArrayList<Marker>();
        
        // Count from local
        if ((sessionForLocal != null) && (negativeGids != null) && (!negativeGids.isEmpty())){
            dao.setSession(sessionForLocal);
            markers.addAll(dao.getMarkersByIds(negativeGids, start, numOfRows));
        }

        // Count from central
        if ((sessionForCentral != null) && (positiveGids != null) && (!positiveGids.isEmpty())) {
            dao.setSession(sessionForCentral);
            markers.addAll(dao.getMarkersByIds(positiveGids, start, numOfRows));
        }

        return markers;
    }


    
    @Override
    public QtlDetailsPK addQtlDetails(QtlDetails qtlDetails) throws MiddlewareQueryException {

        requireLocalDatabaseInstance();
        
        Session session = getCurrentSessionForLocal();
        Transaction trans = null;

        QtlDetailsPK savedId = new QtlDetailsPK();
        
        try {
            trans = session.beginTransaction();
            QtlDetailsDAO dao = new QtlDetailsDAO();
            dao.setSession(session);

            // No need to auto-assign negative IDs for new local DB records
            // qtlId and mapId are foreign keys
            
            QtlDetails recordSaved = dao.save(qtlDetails);
            savedId = recordSaved.getId();
            
            trans.commit();

        } catch (Exception e) {
            // Rollback transaction in case of errors
            if (trans != null) {
                trans.rollback();
            }
            throw new MiddlewareQueryException("Error encountered while saving Qtl Details: GenotypicDataManager.addQtlDetails(qtlDetails="
                    + qtlDetails + "): " + e.getMessage(), e);
        } finally {
            session.flush();
        }
        return savedId;

    }

    @Override
    public Integer addMarkerDetails(MarkerDetails markerDetails) throws MiddlewareQueryException {

        requireLocalDatabaseInstance();
        
        Session session = getCurrentSessionForLocal();
        Transaction trans = null;

        Integer savedId;
        
        try {
            trans = session.beginTransaction();
            MarkerDetailsDAO dao = new MarkerDetailsDAO();
            dao.setSession(session);

            // No need to auto-assign negative id. It should come from an existing entry in Marker.
            
            MarkerDetails recordSaved = dao.save(markerDetails);
            savedId = recordSaved.getMarkerId();
            
            trans.commit();

        } catch (Exception e) {
            // Rollback transaction in case of errors
            if (trans != null) {
                trans.rollback();
            }
            throw new MiddlewareQueryException("Error encountered while saving Marker Details: GenotypicDataManager.addMarkerDetails(markerDetails="
                    + markerDetails + "): " + e.getMessage(), e);
        } finally {
            session.flush();
        }
        return savedId;    
    }

    @Override
    public Integer addMarkerUserInfo(MarkerUserInfo markerUserInfo) throws MiddlewareQueryException {

        requireLocalDatabaseInstance();
        
        Session session = getCurrentSessionForLocal();
        Transaction trans = null;

        Integer savedId;
        
        try {
            trans = session.beginTransaction();
            MarkerUserInfoDAO dao = new MarkerUserInfoDAO();
            dao.setSession(session);

            // No need to auto-assign negative id. It should come from an existing entry in Marker.
            
            MarkerUserInfo recordSaved = dao.save(markerUserInfo);
            savedId = recordSaved.getMarkerId();
            
            trans.commit();

        } catch (Exception e) {
            // Rollback transaction in case of errors
            if (trans != null) {
                trans.rollback();
            }
            throw new MiddlewareQueryException("Error encountered while saving Marker Details: GenotypicDataManager.addMarkerUserInfo(markerUserInfo="
                    + markerUserInfo + "): " + e.getMessage(), e);
        } finally {
            session.flush();
        }
        return savedId;    
    }

    @Override
    public AccMetadataSetPK addAccMetadataSet(AccMetadataSet accMetadataSet) throws MiddlewareQueryException{
        
        requireLocalDatabaseInstance();
        
        Session session = getCurrentSessionForLocal();
        Transaction trans = null;

        AccMetadataSetPK savedId = new AccMetadataSetPK();
        
        try {
            trans = session.beginTransaction();
            AccMetadataSetDAO dao = new AccMetadataSetDAO();
            dao.setSession(session);

            // No need to auto-assign negative IDs for new local DB records
            // datasetId, germplasmId and nameId are foreign keys
            
            AccMetadataSet recordSaved = dao.save(accMetadataSet);
            savedId = recordSaved.getId();
            
            trans.commit();

        } catch (Exception e) {
            // Rollback transaction in case of errors
            if (trans != null) {
                trans.rollback();
            }
            throw new MiddlewareQueryException("Error encountered with addAccMetadataSet(accMetadataSet="
                    + accMetadataSet + "): " + e.getMessage(), e);
        } finally {
            session.flush();
        }
        return savedId;
    }
    
    @Override
    public MarkerMetadataSetPK addMarkerMetadataSet(MarkerMetadataSet markerMetadataSet) throws MiddlewareQueryException{

        requireLocalDatabaseInstance();
        
        Session session = getCurrentSessionForLocal();
        Transaction trans = null;

        MarkerMetadataSetPK savedId = new MarkerMetadataSetPK();
        
        try {
            trans = session.beginTransaction();
            MarkerMetadataSetDAO dao = new MarkerMetadataSetDAO();
            dao.setSession(session);

            // No need to auto-assign negative IDs for new local DB records
            // datasetId and markerId are foreign keys
            
            MarkerMetadataSet recordSaved = dao.save(markerMetadataSet);
            savedId = recordSaved.getId();
            
            trans.commit();

        } catch (Exception e) {
            // Rollback transaction in case of errors
            if (trans != null) {
                trans.rollback();
            }
            throw new MiddlewareQueryException("Error encountered with addMarkerMetadataSet(markerMetadataSet="
                    + markerMetadataSet + "): " + e.getMessage(), e);
        } finally {
            session.flush();
        }
        return savedId;
    }

    @Override
    public Integer addDataset(Dataset dataset) throws MiddlewareQueryException{

        requireLocalDatabaseInstance();
        
        Session session = getCurrentSessionForLocal();
        Transaction trans = null;

        Integer savedId;
        
        try {
            trans = session.beginTransaction();
            DatasetDAO dao = new DatasetDAO();
            dao.setSession(session);

            Integer generatedId = dao.getNegativeId("datasetId");
            dataset.setDatasetId(generatedId);
            
            Dataset recordSaved = dao.save(dataset);
            savedId = recordSaved.getDatasetId();
            
            trans.commit();

        } catch (Exception e) {
            // Rollback transaction in case of errors
            if (trans != null) {
                trans.rollback();
            }
            throw new MiddlewareQueryException("Error encountered with addDataset(dataset="
                    + dataset + "): " + e.getMessage(), e);
        } finally {
            session.flush();
        }
        return savedId;   
    }
   
    @Override
    public Integer addGDMSMarker(Marker marker) throws MiddlewareQueryException {
        requireLocalDatabaseInstance();

        Session session = getCurrentSessionForLocal();
        Transaction trans = null;

        Integer idGDMSMarkerSaved;
        try {
            // begin save transaction
            trans = session.beginTransaction();

            MarkerDAO dao = new MarkerDAO();
            dao.setSession(session);

            Integer markerId = dao.getNegativeId("markerId");
            marker.setMarkerId(markerId);

            Marker recordSaved = dao.saveOrUpdate(marker);
            idGDMSMarkerSaved = recordSaved.getMarkerId();

            trans.commit();
        } catch (Exception e) {
            // rollback transaction in case of errors
            if (trans != null) {
                trans.rollback();
            }
            throw new MiddlewareQueryException("Error encountered while saving Marker: addGDMSMarker(): "
                    + e.getMessage(), e);
        } finally {
            session.flush();
        }
        
        return idGDMSMarkerSaved;
    }
    
    @Override
    public Integer addGDMSMarkerAlias(MarkerAlias markerAlias) throws MiddlewareQueryException {
        requireLocalDatabaseInstance();

        Session session = getCurrentSessionForLocal();
        Transaction trans = null;

        Integer idGDMSMarkerAliasSaved;
        try {
            // begin save transaction
            trans = session.beginTransaction();

            MarkerAliasDAO dao = new MarkerAliasDAO();
            dao.setSession(session);

            //Integer markerAliasId = dao.getNegativeId("marker_id");
            //markerAlias.setMarkerId(markerAliasId);

            MarkerAlias recordSaved = dao.save(markerAlias);
            idGDMSMarkerAliasSaved = recordSaved.getMarkerId();

            trans.commit();
        } catch (Exception e) {
            // rollback transaction in case of errors
            if (trans != null) {
                trans.rollback();
            }
            throw new MiddlewareQueryException("Error encountered while saving Marker: addGDMSMarkerAlias(): "
                    + e.getMessage(), e);
        } finally {
            session.flush();
        }
        
        return idGDMSMarkerAliasSaved;
    }    
    
    
    @Override
    public Integer addDatasetUser(DatasetUsers datasetUser) throws MiddlewareQueryException {
        requireLocalDatabaseInstance();

        Session session = getCurrentSessionForLocal();
        Transaction trans = null;

        Integer idDatasetUserSaved;
        try {
            // begin save transaction
            trans = session.beginTransaction();

            DatasetUsersDAO dao = new DatasetUsersDAO();
            dao.setSession(session);

            DatasetUsers recordSaved = dao.save(datasetUser);
            idDatasetUserSaved = recordSaved.getUserId();

            trans.commit();
        } catch (Exception e) {
            // rollback transaction in case of errors
            if (trans != null) {
                trans.rollback();
            }
            throw new MiddlewareQueryException("Error encountered while saving Marker: addDatasetUser(): "
                    + e.getMessage(), e);
        } finally {
            session.flush();
        }
        
        return idDatasetUserSaved;
    }    

    @Override
    public Integer addAlleleValues(AlleleValues alleleValues) throws MiddlewareQueryException{

        requireLocalDatabaseInstance();
        
        Session session = getCurrentSessionForLocal();
        Transaction trans = null;

        Integer savedId;
        
        try {
            trans = session.beginTransaction();
            AlleleValuesDAO dao = new AlleleValuesDAO();
            dao.setSession(session);

            Integer generatedId = dao.getNegativeId("anId");
            alleleValues.setAnId(generatedId);
            
            AlleleValues recordSaved = dao.save(alleleValues);
            savedId = recordSaved.getAnId();
            
            trans.commit();

        } catch (Exception e) {
            // Rollback transaction in case of errors
            if (trans != null) {
                trans.rollback();
            }
            throw new MiddlewareQueryException("Error encountered with addAlleleValues(alleleValues="
                    + alleleValues + "): " + e.getMessage(), e);
        } finally {
            session.flush();
        }
        return savedId;   
    }
    

    @Override
    public Integer addCharValues(CharValues charValues) throws MiddlewareQueryException{

        requireLocalDatabaseInstance();
        
        Session session = getCurrentSessionForLocal();
        Transaction trans = null;

        Integer savedId;
        
        try {
            trans = session.beginTransaction();
            CharValuesDAO dao = new CharValuesDAO();
            dao.setSession(session);

            Integer generatedId = dao.getNegativeId("acId");
            charValues.setAcId(generatedId);
            
            CharValues recordSaved = dao.save(charValues);
            savedId = recordSaved.getAcId();
            
            trans.commit();

        } catch (Exception e) {
            // Rollback transaction in case of errors
            if (trans != null) {
                trans.rollback();
            }
            throw new MiddlewareQueryException("Error encountered with addCharValues(charValues="
                    + charValues + "): " + e.getMessage(), e);
        } finally {
            session.flush();
        }
        return savedId;   
    }
    
    
    @Override
    public Integer addMappingPop(MappingPop mappingPop) throws MiddlewareQueryException {
        requireLocalDatabaseInstance();

        Session session = getCurrentSessionForLocal();
        Transaction trans = null;

        Integer idSaved;
        try {
            // begin save transaction
            trans = session.beginTransaction();

            MappingPopDAO dao = new MappingPopDAO();
            dao.setSession(session);

            MappingPop recordSaved = dao.save(mappingPop);
            idSaved = recordSaved.getDatasetId();

            trans.commit();
        } catch (Exception e) {
            // rollback transaction in case of errors
            if (trans != null) {
                trans.rollback();
            }
            throw new MiddlewareQueryException("Error encountered while saving Marker: addMappingPop(): "
                    + e.getMessage(), e);
        } finally {
            session.flush();
        }
        
        return idSaved;
    }    

    @Override
    public Integer addMappingPopValue(MappingPopValues mappingPopValue) throws MiddlewareQueryException {
        requireLocalDatabaseInstance();

        Session session = getCurrentSessionForLocal();
        Transaction trans = null;

        Integer idSaved;
        try {
            // begin save transaction
            trans = session.beginTransaction();

            MappingPopValuesDAO dao = new MappingPopValuesDAO();
            dao.setSession(session);

            Integer mpId = dao.getNegativeId("mpId");
            mappingPopValue.setMpId(mpId);
            
            MappingPopValues recordSaved = dao.saveOrUpdate(mappingPopValue);
            idSaved = recordSaved.getMpId();

            trans.commit();
        } catch (Exception e) {
            // rollback transaction in case of errors
            if (trans != null) {
                trans.rollback();
            }
            throw new MiddlewareQueryException("Error encountered while saving Marker: addMappingPopValue(): "
                    + e.getMessage(), e);
        } finally {
            session.flush();
        }
        
        return idSaved;
    }    
    
    @Override
    public Integer addMarkerOnMap(MarkerOnMap markerOnMap) throws MiddlewareQueryException {
        requireLocalDatabaseInstance();

        Session session = getCurrentSessionForLocal();
        Transaction trans = null;

        Integer idSaved;
        try {
            // begin save transaction
            trans = session.beginTransaction();

            MarkerOnMapDAO dao = new MarkerOnMapDAO();
            dao.setSession(session);

            //Integer mapId = dao.getNegativeId("mapId");
            //mappingPopValue.setMpId(mpId);
            
            MarkerOnMap recordSaved = dao.save(markerOnMap);
            idSaved = recordSaved.getMapId();

            trans.commit();
        } catch (Exception e) {
            // rollback transaction in case of errors
            if (trans != null) {
                trans.rollback();
            }
            throw new MiddlewareQueryException("Error encountered while saving Marker: addMarkerOnMap(): "
                    + e.getMessage(), e);
        } finally {
            session.flush();
        }
        
        return idSaved;
    }
    
    @Override
    public Integer addDartValue(DartValues dartValue) throws MiddlewareQueryException {
        requireLocalDatabaseInstance();

        Session session = getCurrentSessionForLocal();
        Transaction trans = null;

        Integer idSaved;
        try {
            // begin save transaction
            trans = session.beginTransaction();

            DartValuesDAO dao = new DartValuesDAO();
            dao.setSession(session);

            Integer adId = dao.getNegativeId("adId");
            dartValue.setAdId(adId);
            
            DartValues recordSaved = dao.save(dartValue);
            idSaved = recordSaved.getAdId();

            trans.commit();
        } catch (Exception e) {
            // rollback transaction in case of errors
            if (trans != null) {
                trans.rollback();
            }
            throw new MiddlewareQueryException("Error encountered while saving Marker: addDartValue(): "
                    + e.getMessage(), e);
        } finally {
            session.flush();
        }
        
        return idSaved;
    }    
    
    

    @Override
    public Integer addQtl(Qtl qtl) throws MiddlewareQueryException {
        requireLocalDatabaseInstance();

        Session session = getCurrentSessionForLocal();
        Transaction trans = null;

        Integer idSaved;
        try {
            // begin save transaction
            trans = session.beginTransaction();

            QtlDAO dao = new QtlDAO();
            dao.setSession(session);

            Integer qtlId = dao.getNegativeId("qtlId");
            qtl.setQtlId(qtlId);
            
            Qtl recordSaved = dao.saveOrUpdate(qtl);
            idSaved = recordSaved.getQtlId();

            trans.commit();
        } catch (Exception e) {
            // rollback transaction in case of errors
            if (trans != null) {
                trans.rollback();
            }
            throw new MiddlewareQueryException("Error encountered while saving Qtl: addQtl(): "
                    + e.getMessage(), e);
        } finally {
            session.flush();
        }
        
        return idSaved;
    }    
    


    @Override
    public Integer addMap(Map map) throws MiddlewareQueryException {
        requireLocalDatabaseInstance();

        Session session = getCurrentSessionForLocal();
        Transaction trans = null;

        Integer idSaved;
        try {
            // begin save transaction
            trans = session.beginTransaction();

            MapDAO dao = new MapDAO();
            dao.setSession(session);

            Integer mapId = dao.getNegativeId("mapId");
            map.setMapId(mapId);
            
            Map recordSaved = dao.saveOrUpdate(map);
            idSaved = recordSaved.getMapId();

            trans.commit();
        } catch (Exception e) {
            // rollback transaction in case of errors
            if (trans != null) {
                trans.rollback();
            }
            throw new MiddlewareQueryException("Error encountered while saving Map: addMap(): "
                    + e.getMessage(), e);
        } finally {
            session.flush();
        }
        
        return idSaved;
    } 

    @Override
    public Boolean setSSRMarkers(Marker marker, MarkerAlias markerAlias, MarkerDetails markerDetails, MarkerUserInfo markerUserInfo) throws MiddlewareQueryException {
        requireLocalDatabaseInstance();

        Session session = getCurrentSessionForLocal();
        Transaction trans = null;
        Boolean transactionStatus = true;

        try {
            // begin save transaction
            trans = session.beginTransaction();

            // Add GDMS Marker
            MarkerDAO markerDao = new MarkerDAO();
            markerDao.setSession(session);

            Integer markerId = markerDao.getNegativeId("markerId");
            marker.setMarkerId(markerId);
            
            marker.setMarkerType("SSR");

            Marker markerRecordSaved = markerDao.saveOrUpdate(marker);
            Integer idGDMSMarkerSaved = markerRecordSaved.getMarkerId();
            if(idGDMSMarkerSaved == null)
            	transactionStatus = false;

            // Add GDMS Marker Alias
            MarkerAliasDAO markerAliasDao = new MarkerAliasDAO();
            markerAliasDao.setSession(session);
            markerAlias.setMarkerId(idGDMSMarkerSaved);

            MarkerAlias markerAliasRecordSaved = markerAliasDao.saveOrUpdate(markerAlias);
            Integer markerAliasRecordSavedMarkerId = markerAliasRecordSaved.getMarkerId();
            if(markerAliasRecordSavedMarkerId == null)
            	transactionStatus = false;
           
            // Add Marker Details
            MarkerDetailsDAO markerDetailsDao = new MarkerDetailsDAO();
            markerDetailsDao.setSession(session);
            markerDetails.setMarkerId(idGDMSMarkerSaved);

            MarkerDetails markerDetailsRecordSaved = markerDetailsDao.saveOrUpdate(markerDetails);
            Integer markerDetailsSavedMarkerId = markerDetailsRecordSaved.getMarkerId();
            if(markerDetailsSavedMarkerId == null)
            	transactionStatus = false;
            
            // Add marker user info
            MarkerUserInfoDAO dao = new MarkerUserInfoDAO();
            dao.setSession(session);
            markerUserInfo.setMarkerId(idGDMSMarkerSaved);
            
            MarkerUserInfo markerUserInfoRecordSaved = dao.saveOrUpdate(markerUserInfo);
            Integer markerUserInfoSavedId = markerUserInfoRecordSaved.getMarkerId();
            if(markerUserInfoSavedId == null)
            	transactionStatus = false;
            
            if(transactionStatus == true)
                trans.commit();
            else
            	trans.rollback();
            
        } catch (Exception e) {
            // rollback transaction in case of errors
        	transactionStatus = false;
            if (trans != null) {            	
                trans.rollback();
            }
            throw new MiddlewareQueryException("Error encountered while saving Marker: setSSRMarkers(): "
                    + e.getMessage(), e);
        } finally {
            session.flush();
        }
        
        return transactionStatus;
    }    
    
    @Override
    public Boolean setSNPMarkers(Marker marker, MarkerAlias markerAlias, MarkerDetails markerDetails, MarkerUserInfo markerUserInfo) throws MiddlewareQueryException {
        requireLocalDatabaseInstance();

        Session session = getCurrentSessionForLocal();
        Transaction trans = null;
        Boolean transactionStatus = true;

        try {
            // begin save transaction
            trans = session.beginTransaction();

            // Add GDMS Marker
            MarkerDAO markerDao = new MarkerDAO();
            markerDao.setSession(session);

            Integer markerId = markerDao.getNegativeId("markerId");
            marker.setMarkerId(markerId);
            
            marker.setMarkerType("SNP");

            Marker markerRecordSaved = markerDao.saveOrUpdate(marker);
            Integer idGDMSMarkerSaved = markerRecordSaved.getMarkerId();
            if(idGDMSMarkerSaved == null)
            	transactionStatus = false;

            // Add GDMS Marker Alias
            MarkerAliasDAO markerAliasDao = new MarkerAliasDAO();
            markerAliasDao.setSession(session);
            markerAlias.setMarkerId(idGDMSMarkerSaved);

            MarkerAlias markerAliasRecordSaved = markerAliasDao.save(markerAlias);
            Integer markerAliasRecordSavedMarkerId = markerAliasRecordSaved.getMarkerId();
            if(markerAliasRecordSavedMarkerId == null)
            	transactionStatus = false;
           
            // Add Marker Details
            MarkerDetailsDAO markerDetailsDao = new MarkerDetailsDAO();
            markerDetailsDao.setSession(session);
            markerDetails.setMarkerId(idGDMSMarkerSaved);

            MarkerDetails markerDetailsRecordSaved = markerDetailsDao.save(markerDetails);
            Integer markerDetailsSavedMarkerId = markerDetailsRecordSaved.getMarkerId();
            if(markerDetailsSavedMarkerId == null)
            	transactionStatus = false;
            
            // Add marker user info
            MarkerUserInfoDAO dao = new MarkerUserInfoDAO();
            dao.setSession(session);
            markerUserInfo.setMarkerId(idGDMSMarkerSaved);
            
            MarkerUserInfo markerUserInfoRecordSaved = dao.save(markerUserInfo);
            Integer markerUserInfoSavedId = markerUserInfoRecordSaved.getMarkerId();
            if(markerUserInfoSavedId == null)
            	transactionStatus = false;
            
            if(transactionStatus == true)
                trans.commit();
            else
            	trans.rollback();
            
        } catch (Exception e) {
            // rollback transaction in case of errors
        	transactionStatus = false;
            if (trans != null) {            	
                trans.rollback();
            }
            throw new MiddlewareQueryException("Error encountered while saving Marker: setSNPMarkers(): "
                    + e.getMessage(), e);
        } finally {
            session.flush();
        }
        
        return transactionStatus;
    }    

    @Override
    public Boolean setCAPMarkers(Marker marker, MarkerAlias markerAlias, MarkerDetails markerDetails, MarkerUserInfo markerUserInfo) throws MiddlewareQueryException {
        requireLocalDatabaseInstance();

        Session session = getCurrentSessionForLocal();
        Transaction trans = null;
        Boolean transactionStatus = true;

        try {
            // begin save transaction
            trans = session.beginTransaction();

            // Add GDMS Marker
            MarkerDAO markerDao = new MarkerDAO();
            markerDao.setSession(session);

            Integer markerId = markerDao.getNegativeId("markerId");
            marker.setMarkerId(markerId);
            
            marker.setMarkerType("CAP");

            Marker markerRecordSaved = markerDao.saveOrUpdate(marker);
            Integer idGDMSMarkerSaved = markerRecordSaved.getMarkerId();
            if(idGDMSMarkerSaved == null)
            	transactionStatus = false;

            // Add GDMS Marker Alias
            MarkerAliasDAO markerAliasDao = new MarkerAliasDAO();
            markerAliasDao.setSession(session);
            markerAlias.setMarkerId(idGDMSMarkerSaved);

            MarkerAlias markerAliasRecordSaved = markerAliasDao.save(markerAlias);
            Integer markerAliasRecordSavedMarkerId = markerAliasRecordSaved.getMarkerId();
            if(markerAliasRecordSavedMarkerId == null)
            	transactionStatus = false;
           
            // Add Marker Details
            MarkerDetailsDAO markerDetailsDao = new MarkerDetailsDAO();
            markerDetailsDao.setSession(session);
            markerDetails.setMarkerId(idGDMSMarkerSaved);

            MarkerDetails markerDetailsRecordSaved = markerDetailsDao.save(markerDetails);
            Integer markerDetailsSavedMarkerId = markerDetailsRecordSaved.getMarkerId();
            if(markerDetailsSavedMarkerId == null)
            	transactionStatus = false;
            
            // Add marker user info
            MarkerUserInfoDAO dao = new MarkerUserInfoDAO();
            dao.setSession(session);
            markerUserInfo.setMarkerId(idGDMSMarkerSaved);
            
            MarkerUserInfo markerUserInfoRecordSaved = dao.save(markerUserInfo);
            Integer markerUserInfoSavedId = markerUserInfoRecordSaved.getMarkerId();
            if(markerUserInfoSavedId == null)
            	transactionStatus = false;
            
            if(transactionStatus == true)
                trans.commit();
            else
            	trans.rollback();
            
        } catch (Exception e) {
            // rollback transaction in case of errors
        	transactionStatus = false;
            if (trans != null) {            	
                trans.rollback();
            }
            throw new MiddlewareQueryException("Error encountered while saving Marker: setSNPMarkers(): "
                    + e.getMessage(), e);
        } finally {
            session.flush();
        }
        
        return transactionStatus;
    }    

    @Override
    public Boolean setCISRMarkers(Marker marker, MarkerAlias markerAlias, MarkerDetails markerDetails, MarkerUserInfo markerUserInfo) throws MiddlewareQueryException {
        requireLocalDatabaseInstance();

        Session session = getCurrentSessionForLocal();
        Transaction trans = null;
        Boolean transactionStatus = true;

        try {
            // begin save transaction
            trans = session.beginTransaction();

            // Add GDMS Marker
            MarkerDAO markerDao = new MarkerDAO();
            markerDao.setSession(session);

            Integer markerId = markerDao.getNegativeId("markerId");
            marker.setMarkerId(markerId);
            
            marker.setMarkerType("CISR");

            Marker markerRecordSaved = markerDao.saveOrUpdate(marker);
            Integer idGDMSMarkerSaved = markerRecordSaved.getMarkerId();
            if(idGDMSMarkerSaved == null)
            	transactionStatus = false;

            // Add GDMS Marker Alias
            MarkerAliasDAO markerAliasDao = new MarkerAliasDAO();
            markerAliasDao.setSession(session);
            markerAlias.setMarkerId(idGDMSMarkerSaved);

            MarkerAlias markerAliasRecordSaved = markerAliasDao.save(markerAlias);
            Integer markerAliasRecordSavedMarkerId = markerAliasRecordSaved.getMarkerId();
            if(markerAliasRecordSavedMarkerId == null)
            	transactionStatus = false;
           
            // Add Marker Details
            MarkerDetailsDAO markerDetailsDao = new MarkerDetailsDAO();
            markerDetailsDao.setSession(session);
            markerDetails.setMarkerId(idGDMSMarkerSaved);


            MarkerDetails markerDetailsRecordSaved = markerDetailsDao.save(markerDetails);
            Integer markerDetailsSavedMarkerId = markerDetailsRecordSaved.getMarkerId();
            if(markerDetailsSavedMarkerId == null)
            	transactionStatus = false;
            
            // Add marker user info
            
            MarkerUserInfoDAO dao = new MarkerUserInfoDAO();
            dao.setSession(session);
            markerUserInfo.setMarkerId(idGDMSMarkerSaved);
            
            MarkerUserInfo markerUserInfoRecordSaved = dao.save(markerUserInfo);
            Integer markerUserInfoSavedId = markerUserInfoRecordSaved.getMarkerId();
            if(markerUserInfoSavedId == null)
            	transactionStatus = false;
            
            if(transactionStatus == true)
                trans.commit();
            else
            	trans.rollback();
            
        } catch (Exception e) {
            // rollback transaction in case of errors
        	transactionStatus = false;
            if (trans != null) {            	
                trans.rollback();
            }
            throw new MiddlewareQueryException("Error encountered while saving Marker: setSNPMarkers(): "
                    + e.getMessage(), e);
        } finally {
            session.flush();
        }
        
        return transactionStatus;
    }
    


    @Override
    public Boolean setQTL(DatasetUsers datasetUser, Dataset dataset, QtlDetails qtlDetails, Qtl qtl) throws MiddlewareQueryException {
        requireLocalDatabaseInstance();

        Session session = getCurrentSessionForLocal();
        Transaction trans = null;
        Boolean transactionStatus = true;

        try {
            // begin save transaction
            trans = session.beginTransaction();

            // Add Dataset
            DatasetDAO datasetDao = new DatasetDAO();
            datasetDao.setSession(session);

            Integer generatedId = datasetDao.getNegativeId("datasetId");
            dataset.setDatasetId(generatedId);
            
            dataset.setDatasetType("QTL");
            
            Dataset datasetRecordSaved = datasetDao.saveOrUpdate(dataset);
            Integer datasetSavedId = datasetRecordSaved.getDatasetId();            

            if(datasetSavedId == null)
            	transactionStatus = false;            
            
            // Add Dataset User
            DatasetUsersDAO datasetUserDao = new DatasetUsersDAO();
            datasetUserDao.setSession(session);
            datasetUser.setDatasetId(datasetSavedId);

            DatasetUsers recordSaved = datasetUserDao.saveOrUpdate(datasetUser);
            Integer idDatasetUserSaved = recordSaved.getUserId();    

            if(idDatasetUserSaved == null)
            	transactionStatus = false;            

            // Add Qtl            
            QtlDAO qtlDao = new QtlDAO();
            qtlDao.setSession(session);

            Integer qtlId = qtlDao.getNegativeId("qtlId");
            qtl.setQtlId(qtlId);
            qtl.setDatasetId(datasetSavedId);
            
            Qtl qtlRecordSaved = qtlDao.saveOrUpdate(qtl);
            Integer qtlIdSaved = qtlRecordSaved.getQtlId();            
            
            if(qtlIdSaved == null)
            	transactionStatus = false;            
            
            // Add QtlDetails
            QtlDetailsDAO qtlDetailsDao = new QtlDetailsDAO();
            qtlDetailsDao.setSession(session);
            
            QtlDetailsPK qtlDetailsPK = new QtlDetailsPK(qtlIdSaved, qtlDetails.getId().getMapId());
            
            qtlDetails.setQtlId(qtlDetailsPK);

            QtlDetails qtlDetailsRecordSaved = qtlDetailsDao.saveOrUpdate(qtlDetails);
            QtlDetailsPK qtlDetailsSavedId = qtlDetailsRecordSaved.getId();

            if(qtlDetailsSavedId == null)
            	transactionStatus = false;                        
  
            
            if(transactionStatus == true)
                trans.commit();
            else
            	trans.rollback();
            
        } catch (Exception e) {
            // rollback transaction in case of errors
        	transactionStatus = false;
            if (trans != null) {            	
                trans.rollback();
            }
            throw new MiddlewareQueryException("Error encountered while saving Marker: setQTL(): "
                    + e.getMessage(), e);
        } finally {
            session.flush();
        }
        
        return transactionStatus;
    }    

    public Boolean setDart(AccMetadataSet accMetadataSet, MarkerMetadataSet markerMetadataSet, DatasetUsers datasetUser, 
            AlleleValues alleleValues, Dataset dataset, DartValues dartValues) throws MiddlewareQueryException{
        
        requireLocalDatabaseInstance();

        Session session = getCurrentSessionForLocal();
        Transaction trans = null;
        Boolean transactionStatus = true;

        try {
            // Begin save transaction
            trans = session.beginTransaction();

            // Add Dataset
            DatasetDAO datasetDao = new DatasetDAO();
            datasetDao.setSession(session);

            Integer datasetGeneratedId = datasetDao.getNegativeId("datasetId");
            dataset.setDatasetId(datasetGeneratedId);
            
            dataset.setDatasetType("DArT");
            dataset.setDataType("int");

            Dataset datasetRecordSaved = datasetDao.saveOrUpdate(dataset);
            Integer datasetId = datasetRecordSaved.getDatasetId();            

            if(datasetId == null){
                throw new Exception();  // To immediately roll back and to avoid executing the other insert functions
            }

            // Add AccMetadataSet
            AccMetadataSetDAO accMetadataSetDao = new AccMetadataSetDAO();
            accMetadataSetDao.setSession(session);
            
            accMetadataSet.setDatasetId(datasetId);

            // No need to generate id, AccMetadataSetPK(datasetId, gId, nId) are foreign keys
            
            AccMetadataSet accMetadataSetRecordSaved = accMetadataSetDao.saveOrUpdate(accMetadataSet);
            AccMetadataSetPK accMetadatasetSavedId = accMetadataSetRecordSaved.getId();            

            if(accMetadatasetSavedId == null){
                throw new Exception(); 
            }
            
            // Add MarkerMetadataSet
            MarkerMetadataSetDAO markerMetadataSetDao = new MarkerMetadataSetDAO();
            markerMetadataSetDao.setSession(session);

            markerMetadataSet.setDatasetId(datasetId);

            // No need to generate id, MarkerMetadataSetPK(datasetId, markerId) are foreign keys

            MarkerMetadataSet markerMetadataSetRecordSaved = markerMetadataSetDao.saveOrUpdate(markerMetadataSet);
            MarkerMetadataSetPK markerMetadataSetSavedId = markerMetadataSetRecordSaved.getId();            

            if(markerMetadataSetSavedId == null){
                throw new Exception();
            }
            
            // Add DatasetUser
            
            DatasetUsersDAO datasetUserDao = new DatasetUsersDAO();
            datasetUserDao.setSession(session);
            
            datasetUser.setDatasetId(datasetId);

            DatasetUsers datasetUserSaved = datasetUserDao.saveOrUpdate(datasetUser);
            Integer datasetUserSavedId = datasetUserSaved.getUserId();    

            if(datasetUserSavedId == null){
                throw new Exception();
            }

            // Add AlleleValues
            AlleleValuesDAO alleleValuesDao = new AlleleValuesDAO();
            alleleValuesDao.setSession(session);
            
            alleleValues.setDatasetId(datasetId);

            Integer alleleValuesGeneratedId = alleleValuesDao.getNegativeId("anId");
            alleleValues.setAnId(alleleValuesGeneratedId);
            
            AlleleValues alleleValuesRecordSaved = alleleValuesDao.save(alleleValues);
            Integer alleleValuesSavedId = alleleValuesRecordSaved.getAnId();

            if(alleleValuesSavedId == null){
                throw new Exception();
            }

            // Add DArT Values
            
            DartValuesDAO dartValuesDao = new DartValuesDAO();
            dartValuesDao.setSession(session);
            
            dartValues.setDatasetId(datasetId);

            Integer adId = dartValuesDao.getNegativeId("adId");
            dartValues.setAdId(adId);
            
            DartValues dartValuesRecordSaved = dartValuesDao.save(dartValues);
            Integer dartValuesSavedId = dartValuesRecordSaved.getAdId();

            if(dartValuesSavedId == null){
                //transactionStatus = false;
                throw new Exception();
            }

            if(transactionStatus == true){
                trans.commit();
            } else {
                throw new Exception(); 
            }
            
        } catch (Exception e) {
            // rollback transaction in case of errors
            transactionStatus = false;
            if (trans != null) {                
                trans.rollback();
            }
            throw new MiddlewareQueryException("Error encountered while setting DArT: setDArT(): "
                    + e.getMessage(), e);
        } finally {
            session.flush();
        }
        
        return transactionStatus;
    }

	@Override
	public Boolean setSSR(AccMetadataSet accMetadataSet,MarkerMetadataSet markerMetadataSet, DatasetUsers datasetUser,
			AlleleValues alleleValues, Dataset dataset)throws MiddlewareQueryException {
		
		requireLocalDatabaseInstance();

        Session session = getCurrentSessionForLocal();
        Transaction trans = null;
        Boolean transactionStatus = true;

        try {
            // Begin save transaction
            trans = session.beginTransaction();

            // Add Dataset
            DatasetDAO datasetDao = new DatasetDAO();
            datasetDao.setSession(session);

            Integer datasetGeneratedId = datasetDao.getNegativeId("datasetId");
            dataset.setDatasetId(datasetGeneratedId);
            
            dataset.setDatasetType("SSR");
            dataset.setDataType("int");

            Dataset datasetRecordSaved = datasetDao.saveOrUpdate(dataset);
            Integer datasetId = datasetRecordSaved.getDatasetId();            

            if(datasetId == null){
                throw new Exception();  // To immediately roll back and to avoid executing the other insert functions
            }

            // Add AccMetadataSet
            AccMetadataSetDAO accMetadataSetDao = new AccMetadataSetDAO();
            accMetadataSetDao.setSession(session);
            
            accMetadataSet.setDatasetId(datasetId);

            // No need to generate id, AccMetadataSetPK(datasetId, gId, nId) are foreign keys
            
            AccMetadataSet accMetadataSetRecordSaved = accMetadataSetDao.saveOrUpdate(accMetadataSet);
            AccMetadataSetPK accMetadatasetSavedId = accMetadataSetRecordSaved.getId();            

            if(accMetadatasetSavedId == null){
                throw new Exception(); 
            }
            
            // Add MarkerMetadataSet
            MarkerMetadataSetDAO markerMetadataSetDao = new MarkerMetadataSetDAO();
            markerMetadataSetDao.setSession(session);

            markerMetadataSet.setDatasetId(datasetId);

            // No need to generate id, MarkerMetadataSetPK(datasetId, markerId) are foreign keys

            MarkerMetadataSet markerMetadataSetRecordSaved = markerMetadataSetDao.saveOrUpdate(markerMetadataSet);
            MarkerMetadataSetPK markerMetadataSetSavedId = markerMetadataSetRecordSaved.getId();            

            if(markerMetadataSetSavedId == null){
                throw new Exception();
            }
            
            // Add DatasetUser
            
            DatasetUsersDAO datasetUserDao = new DatasetUsersDAO();
            datasetUserDao.setSession(session);
            
            datasetUser.setDatasetId(datasetId);

            DatasetUsers datasetUserSaved = datasetUserDao.saveOrUpdate(datasetUser);
            Integer datasetUserSavedId = datasetUserSaved.getUserId();    

            if(datasetUserSavedId == null){
                throw new Exception();
            }

            // Add AlleleValues
            AlleleValuesDAO alleleValuesDao = new AlleleValuesDAO();
            alleleValuesDao.setSession(session);
            
            alleleValues.setDatasetId(datasetId);

            Integer alleleValuesGeneratedId = alleleValuesDao.getNegativeId("anId");
            alleleValues.setAnId(alleleValuesGeneratedId);
            
            AlleleValues alleleValuesRecordSaved = alleleValuesDao.save(alleleValues);
            Integer alleleValuesSavedId = alleleValuesRecordSaved.getAnId();

            if(alleleValuesSavedId == null){
                throw new Exception();
            }


            if(transactionStatus == true){
                trans.commit();
            } else {
                throw new Exception(); 
            }
            
        } catch (Exception e) {
            // rollback transaction in case of errors
            transactionStatus = false;
            if (trans != null) {                
                trans.rollback();
            }
            throw new MiddlewareQueryException("Error encountered while setting SSR: setSSR(): "
                    + e.getMessage(), e);
        } finally {
            session.flush();
        }
        
        return transactionStatus;
	}

	@Override
	public Boolean setSNP(AccMetadataSet accMetadataSet,MarkerMetadataSet markerMetadataSet, DatasetUsers datasetUser,
			CharValues charValues, Dataset dataset)throws MiddlewareQueryException {
		
		requireLocalDatabaseInstance();

        Session session = getCurrentSessionForLocal();
        Transaction trans = null;
        Boolean transactionStatus = true;

        try {
            // Begin save transaction
            trans = session.beginTransaction();

            // Add Dataset
            DatasetDAO datasetDao = new DatasetDAO();
            datasetDao.setSession(session);

            Integer datasetGeneratedId = datasetDao.getNegativeId("datasetId");
            dataset.setDatasetId(datasetGeneratedId);
            
            dataset.setDatasetType("SNP");
            dataset.setDataType("int");

            Dataset datasetRecordSaved = datasetDao.saveOrUpdate(dataset);
            Integer datasetId = datasetRecordSaved.getDatasetId();            

            if(datasetId == null){
                throw new Exception();  // To immediately roll back and to avoid executing the other insert functions
            }

            // Add AccMetadataSet
            AccMetadataSetDAO accMetadataSetDao = new AccMetadataSetDAO();
            accMetadataSetDao.setSession(session);
            
            accMetadataSet.setDatasetId(datasetId);

            // No need to generate id, AccMetadataSetPK(datasetId, gId, nId) are foreign keys
            
            AccMetadataSet accMetadataSetRecordSaved = accMetadataSetDao.saveOrUpdate(accMetadataSet);
            AccMetadataSetPK accMetadatasetSavedId = accMetadataSetRecordSaved.getId();            

            if(accMetadatasetSavedId == null){
                throw new Exception(); 
            }
            
            // Add MarkerMetadataSet
            MarkerMetadataSetDAO markerMetadataSetDao = new MarkerMetadataSetDAO();
            markerMetadataSetDao.setSession(session);

            markerMetadataSet.setDatasetId(datasetId);

            // No need to generate id, MarkerMetadataSetPK(datasetId, markerId) are foreign keys

            MarkerMetadataSet markerMetadataSetRecordSaved = markerMetadataSetDao.saveOrUpdate(markerMetadataSet);
            MarkerMetadataSetPK markerMetadataSetSavedId = markerMetadataSetRecordSaved.getId();            

            if(markerMetadataSetSavedId == null){
                throw new Exception();
            }
            
            // Add DatasetUser
            
            DatasetUsersDAO datasetUserDao = new DatasetUsersDAO();
            datasetUserDao.setSession(session);
            
            datasetUser.setDatasetId(datasetId);

            DatasetUsers datasetUserSaved = datasetUserDao.saveOrUpdate(datasetUser);
            Integer datasetUserSavedId = datasetUserSaved.getUserId();    

            if(datasetUserSavedId == null){
                throw new Exception();
            }


            //Add CharValues
            
            CharValuesDAO charValuesDao= new CharValuesDAO();
            charValuesDao.setSession(session);
            
            Integer generatedId = charValuesDao.getNegativeId("acId");
            charValues.setAcId(generatedId);
            
            CharValues charValuesRecordSaved = charValuesDao.saveOrUpdate(charValues);
            Integer charValuesSavedId = charValuesRecordSaved.getAcId();
            
            if(charValuesSavedId == null){
              throw new Exception();
          }

            if(transactionStatus == true){
                trans.commit();
            } else {
                throw new Exception(); 
            }
            
        } catch (Exception e) {
            // rollback transaction in case of errors
            transactionStatus = false;
            if (trans != null) {                
                trans.rollback();
            }
            throw new MiddlewareQueryException("Error encountered while setting SNP: setSNP(): "
                    + e.getMessage(), e);
        } finally {
            session.flush();
        }
        
        return transactionStatus;
	}

    
}