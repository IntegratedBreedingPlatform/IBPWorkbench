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
package org.generationcp.middleware.manager.api;

import java.util.List;

import org.generationcp.middleware.domain.gms.GermplasmListNewColumnsInfo;
import org.generationcp.middleware.domain.gms.ListDataInfo;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.manager.Database;
import org.generationcp.middleware.manager.Operation;
import org.generationcp.middleware.pojos.GermplasmList;
import org.generationcp.middleware.pojos.GermplasmListData;
import org.generationcp.middleware.pojos.UserDefinedField;

/**
 * This is the API for retrieving information about Germplasm Lists.
 * 
 * @author Kevin Manansala, Mark Agarrado
 * 
 */
public interface GermplasmListManager{

    /**
     * Returns the GermplasmList identified by the given id.
     * 
     * @param id
     *          - the listid of the GermplasmList
     * @return Returns the GermplasmList POJO, null if no GermplasmList was retrieved. 
     * @throws MiddlewareQueryException 
     */
    public GermplasmList getGermplasmListById(Integer id) throws MiddlewareQueryException;

    /**
     * Returns all Germplasm list records.
     * 
     * @param start
     *            - the starting index of the sublist of results to be returned
     * @param numOfRows
     *            - the number of rows to be included in the sublist of results
     *            to be returned
     * @param instance
     *            - can either be Database.CENTRAL or Database.LOCAL
     * 
     * @return List of GermplasmList POJOs
     * @throws MiddlewareQueryException
     */
    public List<GermplasmList> getAllGermplasmLists(int start, int numOfRows, Database instance) throws MiddlewareQueryException;

    /**
     * Returns the total number of Germplasm Lists.
     * 
     * @return The count of all germplasm lists.
     * @throws MiddlewareQueryException 
     */
    public long countAllGermplasmLists() throws MiddlewareQueryException;

    /**
     * Returns all the Germplasm List records with names matching the given
     * parameter.
     * 
     * @param name
     * @param start
     *            - the starting index of the sublist of results to be returned
     * @param numOfRows
     *            - the number of rows to be included in the sublist of results
     *            to be returned
     * @param operation
     *            - can be equal or like
     * @param instance
     *            - can either be Database.CENTRAL or Database.LOCAL
     * @return List of GermplasmList POJOs
     * @throws MiddlewareQueryException
     */
    public List<GermplasmList> getGermplasmListByName(String name, int start, int numOfRows, Operation operation, Database instance)
            throws MiddlewareQueryException;

    /**
     * Returns the number of Germplasm List records with names matching the given parameter.
     * 
     * @param name
     * @param operation can be Operation.EQUAL or Operation.LIKE
     * @param instance
     *            - can either be Database.CENTRAL or Database.LOCAL
     * @return The count of Germplasm lists based on the given name and operation
     */
    public long countGermplasmListByName(String name, Operation operation, Database database) throws MiddlewareQueryException;

    /**
     * Returns all the Germplasm List records that have the given status.
     * 
     * @param status
     * @param start
     *            - the starting index of the sublist of results to be returned
     * @param numOfRows
     *            - the number of rows to be included in the sublist of results
     *            to be returned
     * @param instance
     *            - can either be Database.CENTRAL or Database.LOCAL
     * 
     * @return List of Germplasm POJOs
     * @throws MiddlewareQueryException
     */
    public List<GermplasmList> getGermplasmListByStatus(Integer status, int start, int numOfRows, Database instance) throws MiddlewareQueryException;

    /**
     * Returns the number of Germplasm List records that have the given status.
     * 
     * @param status
     * @param instance
     *            - can either be Database.CENTRAL or Database.LOCAL
     * @return The count of Germplasm lists based on the given status.
     */
    public long countGermplasmListByStatus(Integer status, Database instance)  throws MiddlewareQueryException;
    
    /**
     * Returns the germplasm lists that are associated with the specified GID.
     * 
     * @param gid
     * 	          - the Germplasm ID associated with the Germplasm Lists to be returned.
     * @param start
     *            - the starting index of the sublist of results to be returned
     * @param numOfRows
     *            - the number of rows to be included in the sublist of results
     *            to be returned
     * @return List of GermplasmList POJOs
     * @throws MiddlewareQueryException
     */
    public List<GermplasmList> getGermplasmListByGID(Integer gid, int start, int numOfRows) throws MiddlewareQueryException;
    
    /**
     * Returns the number of germplasm lists that are associated with the specified GID.
     * 
     * @param gid
     * 	          - the Germplasm ID associated with the Germplasm Lists to be returned.
     * @return The count of Germplasm Lists associated with the given Germplasm ID/
     * @throws MiddlewareQueryException
     */
    public long countGermplasmListByGID(Integer gid) throws MiddlewareQueryException;

    /**
     * Returns the germplasm list entries that belong to the list identified by
     * the given id.
     * 
     * @param id
     * @param start
     *            - the starting index of the sublist of results to be returned
     * @param numOfRows
     *            - the number of rows to be included in the sublist of results
     *            to be returned
     * 
     * @return List of GermplasmListData POJOs
     */
    public List<GermplasmListData> getGermplasmListDataByListId(Integer id, int start, int numOfRows) throws MiddlewareQueryException;

    /**
     * Returns the number of germplasm list entries that belong to the list
     * identified by the given id.
     * 
     * @param id
     * @return The count of Germplasm list data based on the given list ID
     */
    public long countGermplasmListDataByListId(Integer id) throws MiddlewareQueryException;

    /**
     * Returns the germplasm list entries that belong to the list identified by
     * the given id and have gids equal to the given parameter.
     * 
     * @param listId
     * @param gid
     * @return List of GermplasmListData POJOs
     */
    public List<GermplasmListData> getGermplasmListDataByListIdAndGID(Integer listId, Integer gid) throws MiddlewareQueryException;

    /**
     * Returns the germplasm list entry which is identified by the given
     * parameters.
     * 
     * @param listId
     * @param entryId
     * @return List of GermplasmListData POJOs
     */
    public GermplasmListData getGermplasmListDataByListIdAndEntryId(Integer listId, Integer entryId) throws MiddlewareQueryException;

    /**
     * Returns the germplasm list entries associated with the Germplasm
     * identified by the given gid.
     * 
     * @param gid
     * @param start
     *            - the starting index of the sublist of results to be returned
     * @param numOfRows
     *            - the number of rows to be included in the sublist of results
     *            to be returned
     * 
     * @return List of GermplasmListData POJOs
     * @throws MiddlewareQueryException
     */
    public List<GermplasmListData> getGermplasmListDataByGID(Integer gid, int start, int numOfRows) throws MiddlewareQueryException;

    /**
     * Returns the number of germplasm list entries associated with the
     * Germplasm identified by the given gid.
     * 
     * @param gid
     * @return The count of Germplasm List data based on the given GID
     */
    public long countGermplasmListDataByGID(Integer gid) throws MiddlewareQueryException;

    /**
     * Returns the Top Level Germplasm List Folders present in the specified database.
     * 
     * @param start
     *            - the starting index of the sublist of results to be returned
     * @param numOfRows
     *            - the number of rows to be included in the sublist of results
     *            to be returned
     * @param instance
     *            - can either be Database.CENTRAL or Database.LOCAL
     * @return - List of GermplasmList POJOs
     * @throws MiddlewareQueryException
     */
    public List<GermplasmList> getAllTopLevelLists(int start, int numOfRows, Database instance) throws MiddlewareQueryException;
    
    /**
     * Returns the Top Level Germplasm List Folders present in the specified database.
     * Retrieval from the database is done by batch (as specified in batchSize) to reduce the load
     * in instances where there is a large volume of top level folders to be retrieved. Though
     * retrieval is by batch, this method still returns all of the top level folders as a single list.
     * 
     * @param batchSize
     *            - the number of records to be retrieved per iteration
     * @param instance
     *            - can either be Database.CENTRAL or Database.LOCAL
     * @return - List of GermplasmList POJOs
     * @throws MiddlewareQueryException
     */
    public List<GermplasmList> getAllTopLevelListsBatched(int batchSize, Database instance) throws MiddlewareQueryException;
    
    /**
     * Returns the number of Top Level Germplasm List Folders in the specified database.
     * 
     * @param instance
     *            - can either be Database.CENTRAL or Database.LOCAL
     * @return The count of all top level lists on the specified instance.
     * @throws MiddlewareQueryException
     */
    public long countAllTopLevelLists(Database instance) throws MiddlewareQueryException;

    /**
     * Inserts a single {@code GermplasmList} object into the database.
     * 
     * @param germplasmList
     *            - The {@code GermplasmList} object to be persisted to the
     *            database. Must be a valid {@code GermplasmList} object.
     * @return Returns the GermplasmList id that was assigned to the new GermplasmLists
     * @throws MiddlewareQueryException
     */
    public Integer addGermplasmList(GermplasmList germplasmList) throws MiddlewareQueryException;

    /**
     * Inserts a list of multiple {@code GermplasmList} objects into the
     * database.
     * 
     * @param germplasmLists
     *            - A list of {@code GermplasmList} objects to be persisted to
     *            the database. {@code GermplasmList} objects must be valid.
     * @return Returns the list of GermplasmList ids that were assigned to the new GermplasmLists
     * @throws MiddlewareQueryException
     */
    public List<Integer> addGermplasmList(List<GermplasmList> germplasmLists) throws MiddlewareQueryException;

    /**
     * Updates the database with the {@code GermplasmList} object specified.
     * 
     * @param germplasmList
     *            - The {@code GermplasmList} object to be updated in the
     *            database. Must be a valid {@code GermplasmList} object.
     * @return Returns the list of GermplasmList ids that were updated in the database.
     * @throws MiddlewareQueryException
     */
    public Integer updateGermplasmList(GermplasmList germplasmList) throws MiddlewareQueryException;

    /**
     * Updates the database with multiple {@code GermplasmList} objects
     * specified.
     * 
     * @param germplasmLists
     *            - A list of {@code GermplasmList} objects to be updated in the
     *            database. {@code GermplasmList} objects must be valid.
     * @return Returns the list of GermplasmList ids that were updated in the database.
     * @throws MiddlewareQueryException
     */
    public List<Integer> updateGermplasmList(List<GermplasmList> germplasmLists) throws MiddlewareQueryException;

    /**
     * Removes the specified {@code GermplasmList} object from the database.
     * 
     * @param germplasmList
     *            - The {@code GermplasmList} object to be removed from the
     *            database. Must be a valid {@code GermplasmList} object.
     * @return Returns the number of {@code GermplasmList} objects deleted from
     *         the database.
     * @throws MiddlewareQueryException
     */
    public int deleteGermplasmList(GermplasmList germplasmList) throws MiddlewareQueryException;

    /**
     * Removes the specified {@code GermplasmList} objects from the database.
     * 
     * @param germplasmLists
     *            - A list of {@code GermplasmList} objects to be removed from
     *            the database. {@code GermplasmList} objects must be valid.
     * @return Returns the number of {@code GermplasmList} objects deleted from
     *         the database.
     * @throws MiddlewareQueryException
     */
    public int deleteGermplasmList(List<GermplasmList> germplasmLists) throws MiddlewareQueryException;

    /**
     * Removes the corresponding {@code GermplasmList} record and all related
     * Data from the database given the List ID.
     * 
     * @param listId
     *            - {@code GermplasmList} ID of the Germplasm List record to be
     *            deleted.
     * @return Returns the number of {@code GermplasmList} records deleted from
     *         the database.
     * @throws MiddlewareQueryException
     */
    public int deleteGermplasmListByListId(Integer listId) throws MiddlewareQueryException;

    /**
     * Inserts a {@code GermplasmListData} object into the database.
     * 
     * @param germplasmListData
     *            - The {@code GermplasmListData} object to be persisted to the
     *            database. Must be a valid {@code GermplasmListData} object.
     * @return Returns the id of {@code GermplasmListData} record inserted
     *         in the database.
     * @throws MiddlewareQueryException
     */
    public Integer addGermplasmListData(GermplasmListData germplasmListData) throws MiddlewareQueryException;

    /**
     * Inserts a list of multiple {@code GermplasmListData} objects into the
     * database.
     * 
     * @param germplasmListDatas
     *            - A list of {@code GermplasmListData} objects to be persisted
     *            to the database. {@code GermplasmListData} objects must be
     *            valid.
     * @return Returns the ids of the {@code GermplasmListData} records inserted
     *         in the database.
     * @throws MiddlewareQueryException
     */
    public List<Integer> addGermplasmListData(List<GermplasmListData> germplasmListDatas) throws MiddlewareQueryException;

    /**
     * Updates the database with the {@code GermplasmListData} object specified.
     * 
     * @param germplasmListData
     *            - The {@code GermplasmListData} object to be updated in the
     *            database. Must be a valid {@code GermplasmListData} object.
     * @return Returns the number of {@code GermplasmListData} records updated
     *         in the database.
     * @return Returns the id of the updated {@code GermplasmListData} record
     * @throws MiddlewareQueryException
     */
    public Integer updateGermplasmListData(GermplasmListData germplasmListData) throws MiddlewareQueryException;

    /**
     * Updates the database with the {@code GermplasmListData} objects
     * specified.
     * 
     * @param germplasmListDatas
     *            - A list of {@code GermplasmListData} objects to be updated in
     *            the database. Must be valid {@code GermplasmListData} objects.
     * @return Returns the ids of the updated {@code GermplasmListData} records
     * @throws MiddlewareQueryException
     */
    public List<Integer> updateGermplasmListData(List<GermplasmListData> germplasmListDatas) throws MiddlewareQueryException;

    /**
     * Removes the corresponding {@code GermplasmListData} record from the
     * database given its List ID and Entry ID.
     * 
     * @param listId
     *            - {@code GermplasmList} ID of the Germplasm List Data to be
     *            deleted.
     * @param entryId
     *            - {@code GermplasmListData} Entry ID of the Germplasm List
     *            Data to be deleted.
     * @return Returns the number of {@code GermplasmListData} records deleted
     *         from the database.
     * @throws MiddlewareQueryException
     */
    public int deleteGermplasmListDataByListIdEntryId(Integer listId, Integer entryId) throws MiddlewareQueryException;

    /**
     * Removes the corresponding {@code GermplasmListData} records from the
     * database given their List ID.
     * 
     * @param listId
     *            - {@code GermplasmList} ID of the Germplasm List Data records
     *            to be deleted.
     * @return Returns the number of {@code GermplasmListData} records deleted
     *         from the database.
     * @throws MiddlewareQueryException
     */
    public int deleteGermplasmListDataByListId(Integer listId) throws MiddlewareQueryException;

    /**
     * Removes the specified {@code GermplasmListData} object from the database.
     * 
     * @param germplasmListData
     *            - The {@code GermplasmListData} object to be removed from the
     *            database. Must be a valid {@code GermplasmListData} object.
     * @return Returns the number of {@code GermplasmListData} objects deleted
     *         from the database.
     * @throws MiddlewareQueryException
     */
    public int deleteGermplasmListData(GermplasmListData germplasmListData) throws MiddlewareQueryException;

    /**
     * Removes the specified {@code GermplasmListData} objects from the
     * database.
     * 
     * @param germplasmListDatas
     *            - A list of {@code GermplasmListData} objects to be removed
     *            from the database. {@code GermplasmListData} objects must be
     *            valid.
     * @return Returns the number of {@code GermplasmListData} records deleted
     *         from the database.
     * @throws MiddlewareQueryException
     */
    public int deleteGermplasmListData(List<GermplasmListData> germplasmListDatas) throws MiddlewareQueryException;

    /**
     * Returns a list of {@code GermplasmList} child records given a parent id.
     *
     * @param parentId - the ID of the parent to retrieve the child lists
     * @param start - the starting point to retrieve the results
     * @param numOfRows - the number of rows from the starting point to be retrieved
     * @return Returns a List of GermplasmList POJOs for the child lists
     * @throws MiddlewareQueryException
     */
    public List<GermplasmList> getGermplasmListByParentFolderId(Integer parentId, int start, int numOfRows) throws MiddlewareQueryException;
    
    /**
     * Returns a list of {@code GermplasmList} child records given a parent id.
     * Retrieval from the database is done by batch (as specified in batchSize) to reduce the load
     * in instances where there is a large volume of child folders to be retrieved. Though
     * retrieval is by batch, this method still returns all of the child folders as a single list.
     * 
     * @param parentId - the ID of the parent to retrieve the child lists
     * @param batchSize
     *            - the number of records to be retrieved per iteration
     * @return Returns a List of GermplasmList POJOs for the child lists
     * @throws MiddlewareQueryException
     */
    public List<GermplasmList> getGermplasmListByParentFolderIdBatched(Integer parentId, int batchSize) throws MiddlewareQueryException;
    
    /**
     * Returns the number of {@code GermplasmList} child records given a parent id.
     *
     * @param parentId the parent id
     * @return number of germplasm list child records of a parent record
     * @throws MiddlewareQueryException the MiddlewareQueryException
     */
    public long countGermplasmListByParentFolderId(Integer parentId) throws MiddlewareQueryException;
    
    
    /**
     * Return a List of UserDefinedField POJOs representing records from 
     * the udflds table of IBDB which are the types of germplasm lists.
     * 
     * @return
     * @throws MiddlewareQueryException
     */
    
    public List<UserDefinedField> getGermplasmListTypes() throws MiddlewareQueryException;    

    
    /**
     * Return a List of UserDefinedField POJOs representing records from 
     * the udflds table of IBDB which are the types of germplasm names.
     * 
     * @return
     * @throws MiddlewareQueryException
     */
    
    public List<UserDefinedField> getGermplasmNameTypes() throws MiddlewareQueryException;    
    
    /**
     * Search for germplasm lists given a search term Q
     * @param q
     * @param operation
     * @return - List of germplasm lists
     * @throws MiddlewareQueryException
     */
    public List<GermplasmList> searchForGermplasmList(String q, Operation o) throws MiddlewareQueryException;
    
    
    /**
     * Inserts or updates the ListDataProperty records (columns) 
     * corresponding to the ListDataColumn records
     * 
     * @param listDataCollection
     * @return the list of ListDataInfo objects with the listDataColumnId field of each 
     * 		ListDataColumn filled up if the corresponding ListDataProperty object was 
     * 		successfully inserted or updated
     * @throws MiddlewareQueryException
     */
    public List<ListDataInfo> saveListDataColumns(List<ListDataInfo> listDataCollection) 
    		throws MiddlewareQueryException;
    
    
    /**
     * Retrieves list of distinct column names from ListDataProperty for given list
     * Returns empty list if no related column found.
     * 
     * @param listId - id of list to retrieve columns for
     * @return
     * @throws MiddlewareQueryException
     */
    public GermplasmListNewColumnsInfo getAdditionalColumnsForList(Integer listId) throws MiddlewareQueryException;
    
}
