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
import java.util.List;

import org.generationcp.middleware.dao.GermplasmListDAO;
import org.generationcp.middleware.dao.GermplasmListDataDAO;
import org.generationcp.middleware.exceptions.QueryException;
import org.generationcp.middleware.manager.api.GermplasmListManager;
import org.generationcp.middleware.pojos.GermplasmList;
import org.generationcp.middleware.pojos.GermplasmListData;
import org.generationcp.middleware.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;

public class GermplasmListManagerImpl extends DataManager implements GermplasmListManager{

    public GermplasmListManagerImpl(HibernateUtil hibernateUtilForLocal, HibernateUtil hibernateUtilForCentral) {
        super(hibernateUtilForLocal, hibernateUtilForCentral);
    }

    @Override
    public GermplasmList getGermplasmListById(Integer id) {
        GermplasmListDAO dao = new GermplasmListDAO();
        HibernateUtil hibernateUtil = getHibernateUtil(id);

        if (hibernateUtil != null) {
            dao.setSession(hibernateUtil.getCurrentSession());
        } else {
            return null;
        }

        GermplasmList list = dao.findById(id, false);
        return list;
    }

    @Override
    public List<GermplasmList> getAllGermplasmLists(int start, int numOfRows, Database instance) throws QueryException {
        GermplasmListDAO dao = new GermplasmListDAO();
        HibernateUtil hibernateUtil = getHibernateUtil(instance);

        if (hibernateUtil != null) {
            dao.setSession(hibernateUtil.getCurrentSession());
        } else {
            return new ArrayList<GermplasmList>();
        }
        return dao.getAll(start, numOfRows);
    }

    @Override
    public int countAllGermplasmLists() {
        int count = 0;

        if (this.hibernateUtilForLocal != null) {
            GermplasmListDAO dao = new GermplasmListDAO();
            dao.setSession(hibernateUtilForLocal.getCurrentSession());
            count = count + dao.countAll().intValue();
        }

        if (this.hibernateUtilForCentral != null) {
            GermplasmListDAO centralDao = new GermplasmListDAO();
            centralDao.setSession(hibernateUtilForCentral.getCurrentSession());
            count = count + centralDao.countAll().intValue();
        }

        return count;
    }

    @Override
    public List<GermplasmList> findGermplasmListByName(String name, int start, int numOfRows, Operation operation, Database instance)
            throws QueryException {
        GermplasmListDAO dao = new GermplasmListDAO();
        HibernateUtil hibernateUtil = getHibernateUtil(instance);

        if (hibernateUtil != null) {
            dao.setSession(hibernateUtil.getCurrentSession());
        } else {
            return new ArrayList<GermplasmList>();
        }
        return dao.findByName(name, start, numOfRows, operation);

    }

    @Override
    public int countGermplasmListByName(String name, Operation operation) {
        int count = 0;

        if (this.hibernateUtilForLocal != null) {
            GermplasmListDAO dao = new GermplasmListDAO();
            dao.setSession(hibernateUtilForLocal.getCurrentSession());
            count = count + dao.countByName(name, operation).intValue();
        }

        if (this.hibernateUtilForCentral != null) {
            GermplasmListDAO centralDao = new GermplasmListDAO();
            centralDao.setSession(hibernateUtilForCentral.getCurrentSession());
            count = count + centralDao.countByName(name, operation).intValue();
        }

        return count;
    }

    @Override
    public List<GermplasmList> findGermplasmListByStatus(Integer status, int start, int numOfRows, Database instance) throws QueryException {
        GermplasmListDAO dao = new GermplasmListDAO();

        HibernateUtil hibernateUtil = getHibernateUtil(instance);

        if (hibernateUtil != null) {
            dao.setSession(hibernateUtil.getCurrentSession());
        } else {
            return new ArrayList<GermplasmList>();
        }
        return dao.findByStatus(status, start, numOfRows);

    }

    @Override
    public int countGermplasmListByStatus(Integer status) {
        int count = 0;

        if (this.hibernateUtilForLocal != null) {
            GermplasmListDAO dao = new GermplasmListDAO();
            dao.setSession(hibernateUtilForLocal.getCurrentSession());
            count = count + dao.countByStatus(status).intValue();
        }

        if (this.hibernateUtilForCentral != null) {
            GermplasmListDAO centralDao = new GermplasmListDAO();
            centralDao.setSession(hibernateUtilForCentral.getCurrentSession());
            count = count + centralDao.countByStatus(status).intValue();
        }

        return count;
    }

    @Override
    public List<GermplasmListData> getGermplasmListDataByListId(Integer id, int start, int numOfRows) {
        GermplasmListDataDAO dao = new GermplasmListDataDAO();
        HibernateUtil hibernateUtil = getHibernateUtil(id);

        if (hibernateUtil != null) {
            dao.setSession(hibernateUtil.getCurrentSession());
        } else {
            return new ArrayList<GermplasmListData>();
        }

        return dao.getByListId(id, start, numOfRows);
    }

    @Override
    public int countGermplasmListDataByListId(Integer id) {
        GermplasmListDataDAO dao = new GermplasmListDataDAO();
        HibernateUtil hibernateUtil = getHibernateUtil(id);

        if (hibernateUtil != null) {
            dao.setSession(hibernateUtil.getCurrentSession());
        } else {
            return 0;
        }

        return dao.countByListId(id).intValue();
    }

    @Override
    public List<GermplasmListData> getGermplasmListDataByListIdAndGID(Integer listId, Integer gid) {
        GermplasmListDataDAO dao = new GermplasmListDataDAO();
        HibernateUtil hibernateUtil = getHibernateUtil(listId);

        if (hibernateUtil != null) {
            dao.setSession(hibernateUtil.getCurrentSession());
        } else {
            return new ArrayList<GermplasmListData>();
        }

        return dao.getByListIdAndGID(listId, gid);
    }

    @Override
    public GermplasmListData getGermplasmListDataByListIdAndEntryId(Integer listId, Integer entryId) {
        GermplasmListDataDAO dao = new GermplasmListDataDAO();
        HibernateUtil hibernateUtil = getHibernateUtil(listId);

        if (hibernateUtil != null) {
            dao.setSession(hibernateUtil.getCurrentSession());
        } else {
            return null;
        }

        return dao.getByListIdAndEntryId(listId, entryId);
    }

    @Override
    public List<GermplasmListData> getGermplasmListDataByGID(Integer gid, int start, int numOfRows) throws QueryException {
        GermplasmListDataDAO dao = new GermplasmListDataDAO();

        List<GermplasmListData> toreturn = new ArrayList<GermplasmListData>();
        
        int centralCount = 0;
        int localCount = 0;
        int relativeLimit = 0;
        
        if (this.hibernateUtilForCentral != null) {
            dao.setSession(this.hibernateUtilForCentral.getCurrentSession());
            centralCount = dao.countByGID(gid).intValue();
            
            if (centralCount > start) {
                toreturn.addAll(dao.getByGID(gid, start, numOfRows));
                relativeLimit = numOfRows - (centralCount - start);
                
                if(relativeLimit > 0) {
                    if (this.hibernateUtilForLocal != null) {
                        dao.setSession(this.hibernateUtilForLocal.getCurrentSession());
                        localCount = dao.countByGID(gid).intValue();
                        
                        if (localCount > 0) {
                            toreturn.addAll(dao.getByGID(gid, 0, relativeLimit));
                        }
                    }
                }
            }
            else {
                relativeLimit = start - centralCount;
                
                if (this.hibernateUtilForLocal != null) {
                    dao.setSession(this.hibernateUtilForLocal.getCurrentSession());
                    localCount = dao.countByGID(gid).intValue();
                    
                    if (localCount > relativeLimit) {
                        toreturn.addAll(dao.getByGID(gid, relativeLimit, numOfRows));
                    }
                }
            }
        }
        else if (this.hibernateUtilForLocal != null) {
            dao.setSession(this.hibernateUtilForLocal.getCurrentSession());
            localCount = dao.countByGID(gid).intValue();
            
            if (localCount > start) {
                toreturn.addAll(dao.getByGID(gid, start, numOfRows));
            }
        }
                
        return toreturn;
    }

    @Override
    public int countGermplasmListDataByGID(Integer gid) {
        int count = 0;

        if (this.hibernateUtilForLocal != null) {
            GermplasmListDataDAO dao = new GermplasmListDataDAO();
            dao.setSession(hibernateUtilForLocal.getCurrentSession());
            count = count + dao.countByGID(gid).intValue();
        }

        if (this.hibernateUtilForCentral != null) {
            GermplasmListDataDAO centralDao = new GermplasmListDataDAO();
            centralDao.setSession(hibernateUtilForCentral.getCurrentSession());
            count = count + centralDao.countByGID(gid).intValue();
        }

        return count;
    }
    
    @Override
    public List<GermplasmList> getTopLevelFolders(int start, int numOfRows, Database instance) throws QueryException {
        GermplasmListDAO dao = new GermplasmListDAO();
        HibernateUtil hibernateUtil = getHibernateUtil(instance);

        List<GermplasmList> topLevelFolders = new ArrayList<GermplasmList>();
        
        if (hibernateUtil != null) {
            dao.setSession(hibernateUtil.getCurrentSession());
        } else {
            return new ArrayList<GermplasmList>();
        }
        
        topLevelFolders = dao.getTopLevelFolders(start, numOfRows);
        
        return topLevelFolders;
    }
    
    @Override
    public List<GermplasmList> getTopLevelFoldersBatched(int batchSize, Database instance) throws QueryException {
        List<GermplasmList> topLevelFolders = new ArrayList<GermplasmList>();
        HibernateUtil hibernateUtil = getHibernateUtil(instance);
        if (hibernateUtil == null) {
            return topLevelFolders;
        }
        
        // initialize session & transaction
        Session session = hibernateUtil.getCurrentSession();
        Transaction trans = null;
        
        try {
            // begin transaction
            trans = session.beginTransaction();
            
            GermplasmListDAO dao = new GermplasmListDAO();
            dao.setSession(session);
            
            int topLevelCount = dao.countTopLevelFolders().intValue();
            int start = 0;
            while (start < topLevelCount) {
                topLevelFolders.addAll(dao.getTopLevelFolders(start, batchSize));
                start += batchSize;
            }
        
            // end transaction, commit to database
            trans.commit();
        } catch (Exception ex) {
            // rollback transaction in case of errors
            if (trans != null) {
                trans.rollback();
            }
            throw new QueryException("Error encountered while retrieving top level folders: " + ex.getMessage(), ex);
        } finally {
            hibernateUtil.closeCurrentSession();
        }
        
        return topLevelFolders;
    }
    
    @Override
    public int countTopLevelFolders(Database instance) throws QueryException {
        int count = 0;
        
        GermplasmListDAO dao = new GermplasmListDAO();
        HibernateUtil hibernateUtil = getHibernateUtil(instance);
        dao.setSession(hibernateUtil.getCurrentSession());
        count = dao.countTopLevelFolders().intValue();
        
        return count;
    }

    @Override
    public Integer addGermplasmList(GermplasmList germplasmList) throws QueryException {
        List<GermplasmList> list = new ArrayList<GermplasmList>();
        list.add(germplasmList);
        List<Integer> idList = addGermplasmList(list);
        return idList.get(0);
    }

    @Override
    public List<Integer> addGermplasmList(List<GermplasmList> germplasmLists) throws QueryException {
        return addOrUpdateGermplasmList(germplasmLists, Operation.ADD);
    }

    @Override
    public Integer updateGermplasmList(GermplasmList germplasmList) throws QueryException {
        List<GermplasmList> list = new ArrayList<GermplasmList>();
        list.add(germplasmList);
        List<Integer> idList = updateGermplasmList(list);
        return idList.get(0);
    }

    @Override
    public List<Integer> updateGermplasmList(List<GermplasmList> germplasmLists) throws QueryException {
        return addOrUpdateGermplasmList(germplasmLists, Operation.UPDATE);
    }

    private List<Integer> addOrUpdateGermplasmList(List<GermplasmList> germplasmLists, Operation operation) throws QueryException {
        if (hibernateUtilForLocal == null) {
            throw new QueryException(NO_LOCAL_INSTANCE_MSG);
        }

        // initialize session & transaction
        Session session = hibernateUtilForLocal.getCurrentSession();
        Transaction trans = null;

        int germplasmListsSaved = 0;
        List<Integer> germplasmListIds = new ArrayList<Integer>();
        try {
            // begin save transaction
            trans = session.beginTransaction();

            GermplasmListDAO dao = new GermplasmListDAO();
            dao.setSession(session);

            for (GermplasmList germplasmList : germplasmLists) {
                if (operation == Operation.ADD) {
                    // Auto-assign negative IDs for new local DB records
                    Integer negativeId = dao.getNegativeId("id");
                    germplasmListIds.add(negativeId);
                    germplasmList.setId(negativeId);
                } else if (operation == Operation.UPDATE) {
                    // Check if GermplasmList is a local DB record. Throws
                    // exception if GermplasmList is a central DB record.
                    dao.validateId(germplasmList);
                    germplasmListIds.add(germplasmList.getId());
                }
                dao.saveOrUpdate(germplasmList);
                germplasmListsSaved++;
                if (germplasmListsSaved % JDBC_BATCH_SIZE == 0) {
                    // flush a batch of inserts and release memory
                    dao.flush();
                    dao.clear();
                }
            }
            // end transaction, commit to database
            trans.commit();
        } catch (Exception ex) {
            // rollback transaction in case of errors
            if (trans != null) {
                trans.rollback();
            }
            throw new QueryException("Error encountered while saving Germplasm List: " + ex.getMessage(), ex);
        } finally {
            hibernateUtilForLocal.closeCurrentSession();
        }
        
        return germplasmListIds;
    }

    @Override
    public int deleteGermplasmListByListId(Integer listId) throws QueryException {
        GermplasmList germplasmList = getGermplasmListById(listId);
        return deleteGermplasmList(germplasmList);
    }

    @Override
    public int deleteGermplasmList(GermplasmList germplasmList) throws QueryException {
        List<GermplasmList> list = new ArrayList<GermplasmList>();
        list.add(germplasmList);
        return deleteGermplasmList(list);
    }

    @Override
    public int deleteGermplasmList(List<GermplasmList> germplasmLists) throws QueryException {
        if (hibernateUtilForLocal == null) {
            throw new QueryException(NO_LOCAL_INSTANCE_MSG);
        }

        // initialize session & transaction
        Session session = hibernateUtilForLocal.getCurrentSession();
        Transaction trans = null;

        int germplasmListsDeleted = 0;
        try {
            // begin delete transaction
            trans = session.beginTransaction();

            GermplasmListDAO dao = new GermplasmListDAO();
            dao.setSession(session);

            for (GermplasmList germplasmList : germplasmLists) {
                dao.makeTransient(germplasmList);
                germplasmListsDeleted++;
            }
            // end transaction, commit to database
            trans.commit();
        } catch (Exception ex) {
            // rollback transaction in case of errors
            if (trans != null) {
                trans.rollback();
            }
            throw new QueryException("Error encountered while deleting Germplasm List: " + ex.getMessage(), ex);
        } finally {
            hibernateUtilForLocal.closeCurrentSession();
        }

        return germplasmListsDeleted;
    }

    @Override
    public int addGermplasmListData(GermplasmListData germplasmListData) throws QueryException {
        List<GermplasmListData> list = new ArrayList<GermplasmListData>();
        list.add(germplasmListData);
        return addGermplasmListData(list);
    }

    @Override
    public int addGermplasmListData(List<GermplasmListData> germplasmListDatas) throws QueryException {
        return addOrUpdateGermplasmListData(germplasmListDatas, Operation.ADD);
    }

    @Override
    public int updateGermplasmListData(GermplasmListData germplasmListData) throws QueryException {
        List<GermplasmListData> list = new ArrayList<GermplasmListData>();
        list.add(germplasmListData);
        return updateGermplasmListData(list);
    }

    @Override
    public int updateGermplasmListData(List<GermplasmListData> germplasmListDatas) throws QueryException {
        return addOrUpdateGermplasmListData(germplasmListDatas, Operation.UPDATE);
    }

    private int addOrUpdateGermplasmListData(List<GermplasmListData> germplasmListDatas, Operation operation) throws QueryException {
        if (hibernateUtilForLocal == null) {
            throw new QueryException(NO_LOCAL_INSTANCE_MSG);
        }

        // initialize session & transaction
        Session session = hibernateUtilForLocal.getCurrentSession();
        Transaction trans = null;

        int germplasmListDataSaved = 0;
        try {
            // begin save transaction
            trans = session.beginTransaction();

            GermplasmListDataDAO dao = new GermplasmListDataDAO();
            dao.setSession(session);

            for (GermplasmListData germplasmListData : germplasmListDatas) {
                if (operation == Operation.ADD) {
                    // Auto-assign negative IDs for new local DB records
                    Integer negativeListId = dao.getNegativeId("id");
                    germplasmListData.setId(negativeListId);
                } else if (operation == Operation.UPDATE) {
                    // Check if GermplasmList is a local DB record. Throws
                    // exception if GermplasmList is a central DB record.
                    dao.validateId(germplasmListData);
                }
                dao.saveOrUpdate(germplasmListData);
                germplasmListDataSaved++;
                if (germplasmListDataSaved % JDBC_BATCH_SIZE == 0) {
                    // flush a batch of inserts and release memory
                    dao.flush();
                    dao.clear();
                }
            }
            // end transaction, commit to database
            trans.commit();
        } catch (Exception ex) {
            // rollback transaction in case of errors
            if (trans != null) {
                trans.rollback();
            }
            throw new QueryException("Error encountered while saving Germplasm List Data: " + ex.getMessage(), ex);
        } finally {
            hibernateUtilForLocal.closeCurrentSession();
        }

        return germplasmListDataSaved;
    }

    @Override
    public int deleteGermplasmListDataByListId(Integer listId) throws QueryException {
        if (hibernateUtilForLocal == null) {
            throw new QueryException(NO_LOCAL_INSTANCE_MSG);
        }

        // initialize session & transaction
        Session session = hibernateUtilForLocal.getCurrentSession();
        Transaction trans = null;

        int germplasmListDataDeleted = 0;
        try {
            // begin delete transaction
            trans = session.beginTransaction();

            GermplasmListDataDAO dao = new GermplasmListDataDAO();
            dao.setSession(session);

            germplasmListDataDeleted = dao.deleteByListId(listId);
            // end transaction, commit to database
            trans.commit();
        } catch (Exception ex) {
            // rollback transaction in case of errors
            if (trans != null) {
                trans.rollback();
            }
            throw new QueryException("Error encountered while deleting Germplasm List Data: " + ex.getMessage(), ex);
        } finally {
            hibernateUtilForLocal.closeCurrentSession();
        }

        return germplasmListDataDeleted;
    }

    @Override
    public int deleteGermplasmListDataByListIdEntryId(Integer listId, Integer entryId) throws QueryException {
        GermplasmListData germplasmListData = getGermplasmListDataByListIdAndEntryId(listId, entryId);
        return deleteGermplasmListData(germplasmListData);
    }

    @Override
    public int deleteGermplasmListData(GermplasmListData germplasmListData) throws QueryException {
        List<GermplasmListData> list = new ArrayList<GermplasmListData>();
        list.add(germplasmListData);
        return deleteGermplasmListData(list);
    }

    @Override
    public int deleteGermplasmListData(List<GermplasmListData> germplasmListDatas) throws QueryException {
        if (hibernateUtilForLocal == null) {
            throw new QueryException(NO_LOCAL_INSTANCE_MSG);
        }

        // initialize session & transaction
        Session session = hibernateUtilForLocal.getCurrentSession();
        Transaction trans = null;

        int germplasmListDataDeleted = 0;
        try {
            // begin delete transaction
            trans = session.beginTransaction();

            GermplasmListDataDAO dao = new GermplasmListDataDAO();
            dao.setSession(session);

            for (GermplasmListData germplasmListData : germplasmListDatas) {
                dao.makeTransient(germplasmListData);
                germplasmListDataDeleted++;
            }
            // end transaction, commit to database
            trans.commit();
        } catch (Exception ex) {
            // rollback transaction in case of errors
            if (trans != null) {
                trans.rollback();
            }
            throw new QueryException("Error encountered while deleting Germplasm List Data: " + ex.getMessage(), ex);
        } finally {
            hibernateUtilForLocal.closeCurrentSession();
        }

        return germplasmListDataDeleted;
    }

    @Override
    public List<GermplasmList> getGermplasmListByParentFolderId(Integer parentId, int start, int numOfRows) 
        throws QueryException {

        GermplasmListDAO dao = new GermplasmListDAO();
        HibernateUtil util = getHibernateUtil(parentId);
        List<GermplasmList> childLists;
        
        if(util != null) {
            dao.setSession(util.getCurrentSession());
            childLists = dao.getByParentFolderId(parentId, start, numOfRows);
        } else {
            childLists = new ArrayList<GermplasmList>();
        }
        
        return childLists;
    }
    
    @Override
    public List<GermplasmList> getGermplasmListByParentFolderIdBatched(Integer parentId, int batchSize) 
        throws QueryException {
        List<GermplasmList> childLists = new ArrayList<GermplasmList>();
        HibernateUtil hibernateUtil = getHibernateUtil(parentId);
        if (hibernateUtil == null) {
            return childLists;
        }
        
        // initialize session & transaction
        Session session = hibernateUtil.getCurrentSession();
        Transaction trans = null;

        try {
            // begin transaction
            trans = session.beginTransaction();
            
            GermplasmListDAO dao = new GermplasmListDAO();
            dao.setSession(session);
            
            int start = 0;
            int childListCount = dao.countByParentFolderId(parentId).intValue();
            while (start < childListCount) {
                childLists.addAll(dao.getByParentFolderId(parentId, start, batchSize));
                start += batchSize;
            }
        } catch (Exception ex) {
            // rollback transaction in case of errors
            if (trans != null) {
                trans.rollback();
            }
            throw new QueryException("Error encountered while retrieving germplasm sub-lists: " + ex.getMessage(), ex);
        } finally {
            hibernateUtil.closeCurrentSession();
        }
        
        return childLists;
    }

    @Override
    public Long countGermplasmListByParentFolderId(Integer parentId) throws QueryException {
        GermplasmListDAO dao = new GermplasmListDAO();
        HibernateUtil util = getHibernateUtil(parentId);
        Long result = 0L;
        
        if(util != null) {
            dao.setSession(util.getCurrentSession());
            result = dao.countByParentFolderId(parentId);
        }
        
        return result;
    }

}
