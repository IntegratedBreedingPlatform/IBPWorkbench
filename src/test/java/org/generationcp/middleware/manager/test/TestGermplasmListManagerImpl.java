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

package org.generationcp.middleware.manager.test;

import java.util.ArrayList;
import java.util.List;

import org.generationcp.middleware.domain.gms.GermplasmListNewColumnsInfo;
import org.generationcp.middleware.domain.gms.ListDataColumn;
import org.generationcp.middleware.domain.gms.ListDataInfo;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.manager.DataManager;
import org.generationcp.middleware.manager.Database;
import org.generationcp.middleware.manager.DatabaseConnectionParameters;
import org.generationcp.middleware.manager.ManagerFactory;
import org.generationcp.middleware.manager.Operation;
import org.generationcp.middleware.manager.api.GermplasmListManager;
import org.generationcp.middleware.pojos.GermplasmList;
import org.generationcp.middleware.pojos.GermplasmListData;
import org.generationcp.middleware.pojos.UserDefinedField;
import org.generationcp.middleware.util.Debug;
import org.hibernate.Query;
import org.hibernate.Session;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;
import org.junit.runners.MethodSorters;

/* The add/update/delete tests are highly dependent on the tests before it 
   Therefore the order of execution is important.
   In the future, we will change this to make tests independent of each other.
   As a temporary solution, we will force the ordering in those methods using FixMethodOrder"
*/
@FixMethodOrder(MethodSorters.JVM)
public class TestGermplasmListManagerImpl{

    private static ManagerFactory factory;
    private static GermplasmListManager manager;
    private static List<Integer> testDataIds;
    private static final Integer STATUS_DELETED = 9;

    private long startTime;

    @Rule
    public TestName name = new TestName();


    @BeforeClass
    public static void setUp() throws Exception {
        DatabaseConnectionParameters local = new DatabaseConnectionParameters("testDatabaseConfig.properties", "local");
        DatabaseConnectionParameters central = new DatabaseConnectionParameters("testDatabaseConfig.properties", "central");
        factory = new ManagerFactory(local, central);
        manager = factory.getGermplasmListManager();
        testDataIds = new ArrayList<Integer>();
    }


    @Before
    public void beforeEachTest() {
        Debug.println(0, "#####" + name.getMethodName() + " Start: ");
        startTime = System.nanoTime();
    }
    
    @After
    public void afterEachTest() {
        long elapsedTime = System.nanoTime() - startTime;
        Debug.println(0, "#####" + name.getMethodName() + ": Elapsed Time = " + elapsedTime + " ns = " + ((double) elapsedTime/1000000000) + " s");
    }

    @Test
    public void testGetGermplasmListById() throws Exception {
        Integer id = Integer.valueOf(1);
        GermplasmList list = manager.getGermplasmListById(id);
        Debug.println(0, "testGetGermplasmListById(" + id + ") RESULTS:" + list);
    }

    @Test
    public void testGetAllGermplasmLists() throws Exception {
        int count = (int) manager.countAllGermplasmLists();
        List<GermplasmList> lists = manager.getAllGermplasmLists(0, count, Database.CENTRAL);
        Debug.println(0, "testGetAllGermplasmLists RESULTS: " + count);
        for (GermplasmList list : lists) {
            Debug.println(0, "  " + list);
        }
        // Verify using: select * from listnms where liststatus <> 9;

    }

    @Test
    public void testCountAllGermplasmLists() throws Exception {
        Debug.println(0, "testCountAllGermplasmLists() RESULTS: " + manager.countAllGermplasmLists());
        // Verify using: select count(*) from listnms where liststatus <> 9;
    }

    @Test
    public void testGetGermplasmListByName() throws Exception {
        String name = "2002%";     // Crop tested: Rice
        List<GermplasmList> lists = manager.getGermplasmListByName(name, 0, 5, Operation.LIKE, Database.CENTRAL);
        Debug.println(0, "testGetGermplasmListByName(" + name + ") RESULTS: ");
        for (GermplasmList list : lists) {
            Debug.println(0, "  " + list);
        }
        // Verify using: select * from listnms where liststatus <> 9 and listname like '2002%';
    }

    @Test
    public void testCountGermplasmListByName() throws Exception {
        String name = "2002%";     // Crop tested: Rice
        Debug.println(0, "testCountGermplasmListByName(" + name + ") RESULTS: " + manager.countGermplasmListByName(name, Operation.LIKE, Database.CENTRAL));
        // Verify using: select count(*) from listnms where liststatus <> 9 and listname like '2002%';
    }

    @Test
    public void testGetGermplasmListByStatus() throws Exception {
        Integer status = Integer.valueOf(1);
        List<GermplasmList> lists = manager.getGermplasmListByStatus(status, 0, 5, Database.CENTRAL);

        Debug.println(0, "testGetGermplasmListByStatus(status=" + status + ") RESULTS: ");
        for (GermplasmList list : lists) {
            Debug.println(0, "  " + list);
        }
        // Verify using: select * from listnms where liststatus <> 9 and liststatus = 1;
    }

    @Test
    public void testCountGermplasmListByStatus() throws Exception {
        Integer status = Integer.valueOf(1);
        Debug.println(0, "testCountGermplasmListByStatus(status=" + status + ") RESULTS: " + manager.countGermplasmListByStatus(status, Database.CENTRAL));
        // Verify using: select count(*) from listnms where liststatus <> 9 and liststatus = 1;
    }
    
    @Test
    public void testGetGermplasmListByGID() throws Exception {
	Integer gid = Integer.valueOf(2827287);
        List<GermplasmList> results = manager.getGermplasmListByGID(gid, 0, 200);

        Debug.println(0, "testGetGermplasmListByGID(" + gid + ") RESULTS:");
        for (GermplasmList list : results) {
            Debug.println(0, list.toString());
        }
    }
  
    @Test
    public void testCountGermplasmListByGID() throws Exception {
	Integer gid = Integer.valueOf(2827287);
        Debug.println(0, "testCountGermplasmListByGID(gid=" + gid + ") RESULTS: " + manager.countGermplasmListByGID(gid));
    }

    @Test
    public void testGetGermplasmListDataByListId() throws Exception {
        Integer listId = Integer.valueOf(28781);       // Crop tested: Rice
        List<GermplasmListData> results = manager.getGermplasmListDataByListId(listId, 0, 5);

        Debug.println(0, "testGetGermplasmListDataByListId(" + listId + ") RESULTS: ");
        for (GermplasmListData data : results) {
            Debug.println(0, "  " + data);
        }
    }

    @Test
    public void testCountGermplasmListDataByListId() throws Exception {
        Integer listId = Integer.valueOf(28781);       // Crop tested: Rice
        Debug.println(0, "testCountGermplasmListDataByListId(" + listId + ") RESULTS: " + manager.countGermplasmListDataByListId(listId));
    }

    @Test
    public void testGetGermplasmListDataByListIdAndGID() throws Exception {
        Integer listId = Integer.valueOf(1);
        Integer gid = Integer.valueOf(91959);
        List<GermplasmListData> results = manager.getGermplasmListDataByListIdAndGID(listId, gid);

        Debug.println(0, "testGetGermplasmListDataByListIdAndGID(" + listId + ", " + gid + ") RESULTS: ");
        for (GermplasmListData data : results) {
            Debug.println(0, "  " + data);
        }
    }

    @Test
    public void testGetGermplasmListDataByListIdAndEntryId() throws Exception {
        Integer listId = Integer.valueOf(1);
        Integer entryId = Integer.valueOf(1);
        GermplasmListData data = manager.getGermplasmListDataByListIdAndEntryId(listId, entryId);
        Debug.println(0, "testGetGermplasmListDataByListIdAndEntryId(" + listId + ", " + entryId + ") RESULTS: " + data);
    }

    @Test
    public void testGetGermplasmListDataByGID() throws Exception {
        Integer gid = Integer.valueOf(91959);
        List<GermplasmListData> results = manager.getGermplasmListDataByGID(gid, 0, 5);

        Debug.println(0, "testGetGermplasmListDataByGID(" + gid + ") RESULTS:");
        for (GermplasmListData data : results) {
            Debug.println(0, data.toString());
        }
    }

    @Test
    public void testCountGermplasmListDataByGID() throws Exception {
        Integer gid = Integer.valueOf(91959);
        Debug.println(0, "testCountGermplasmListDataByGID(" + gid + ") RESULTS: " + manager.countGermplasmListDataByGID(gid));
    }

    @Test
    public void testAddGermplasmList() throws Exception {
        GermplasmList germplasmList = new GermplasmList(null, "Test List #1", Long.valueOf(20120305), "LST", Integer.valueOf(1),
                "Test List #1 for GCP-92", null, 1);
        Integer id = manager.addGermplasmList(germplasmList);
        Debug.println(0, "testAddGermplasmList(germplasmList=" + germplasmList + ") RESULTS: \n  " + manager.getGermplasmListById(id));
  
        testDataIds.add(id);
        // No need to clean up, will need the record in subsequent tests
    }

    @Test
    public void testAddGermplasmLists() throws Exception {
        List<GermplasmList> germplasmLists = new ArrayList<GermplasmList>();
        GermplasmList germplasmList = new GermplasmList(null, "Test List #2", Long.valueOf(20120305), "LST", Integer.valueOf(1),
                "Test List #2 for GCP-92", null, 1);
        germplasmLists.add(germplasmList);

        germplasmList = new GermplasmList(null, "Test List #3", Long.valueOf(20120305), "LST", Integer.valueOf(1), "Test List #3 for GCP-92", null,
                1);
        germplasmLists.add(germplasmList);

        germplasmList = new GermplasmList(null, "Test List #4", Long.valueOf(20120305), "LST", Integer.valueOf(1), "Test List #4 for GCP-92", null,
                1);
        germplasmLists.add(germplasmList);

        List<Integer> ids = manager.addGermplasmList(germplasmLists);
        Debug.println(0, "testAddGermplasmLists() GermplasmLists added: " + ids.size());
        Debug.println(0, "testAddGermplasmLists() RESULTS: ");
        for (Integer id : ids) {
            GermplasmList listAdded = manager.getGermplasmListById(id);
            Debug.println(0, "  " + listAdded);
            // since we are not using logical delete, cleanup of test data will now be done at the AfterClass method instead
            // delete record
            //if (!listAdded.getName().equals("Test List #4")) { // delete except for Test List #4 which is used in testUpdateGermplasmList
            //    manager.deleteGermplasmList(listAdded);
            //}
        }
        
        testDataIds.addAll(ids);
    }

    @Test
    public void testUpdateGermplasmList() throws Exception {
        List<GermplasmList> germplasmLists = new ArrayList<GermplasmList>();
        List<String> germplasmListStrings = new ArrayList<String>();

        GermplasmList germplasmList1 = manager.getGermplasmListByName("Test List #1", 0, 1, Operation.EQUAL, Database.LOCAL).get(0);
        germplasmListStrings.add(germplasmList1.toString());
        germplasmList1.setDescription("Test List #1 for GCP-92, UPDATE");
        germplasmLists.add(germplasmList1);

        GermplasmList parent = manager.getGermplasmListById(-1);
        GermplasmList germplasmList2 = manager.getGermplasmListByName("Test List #4", 0, 1, Operation.EQUAL, Database.LOCAL).get(0);
        germplasmListStrings.add(germplasmList2.toString());
        germplasmList2.setDescription("Test List #4 for GCP-92 UPDATE");
        germplasmList2.setParent(parent);
        germplasmLists.add(germplasmList2);

        List<Integer> updatedIds = manager.updateGermplasmList(germplasmLists);

        Debug.println(0, "testUpdateGermplasmList() IDs updated: " + updatedIds);
        Debug.println(0, "testUpdateGermplasmList() RESULTS: ");
        for (int i = 0; i < updatedIds.size(); i++) {
            GermplasmList updatedGermplasmList = manager.getGermplasmListById(updatedIds.get(i));
            Debug.println(0, "  FROM " + germplasmListStrings.get(i));
            Debug.println(0, "  TO   " + updatedGermplasmList);
            
            // No need to clean up, will use the records in subsequent tests
        }

    }

    @Test
    public void testAddGermplasmListData() throws Exception {
        GermplasmList germList = manager.getGermplasmListByName("Test List #1", 0, 1, Operation.EQUAL, Database.LOCAL).get(0);
        GermplasmListData germplasmListData = new GermplasmListData(null, germList, Integer.valueOf(2), 1, "EntryCode", "SeedSource",
                "Germplasm Name 3", "GroupName", 0, 99992);

        Debug.println(0, "testAddGermplasmListData() records added: " + manager.addGermplasmListData(germplasmListData));
        Debug.println(0, "testAddGermplasmListData() RESULTS: ");
        if (germplasmListData.getId() != null) {
            Debug.println(0, "  " + germplasmListData);
        }

    }

    @Test
    public void testAddGermplasmListDatas() throws Exception {
        List<GermplasmListData> germplasmListDatas = new ArrayList<GermplasmListData>();

        GermplasmList germList = manager.getGermplasmListByName("Test List #4", 0, 1, Operation.EQUAL, Database.LOCAL).get(0);
        GermplasmListData germplasmListData = new GermplasmListData(null, germList, Integer.valueOf(2), /*entryId=*/ 2, "EntryCode", "SeedSource",
                "Germplasm Name 4", "GroupName", 0, 99993);
        germplasmListDatas.add(germplasmListData);
        germplasmListData = new GermplasmListData(null, germList, Integer.valueOf(2), /*entryId=*/ 1, "EntryCode", "SeedSource",
                "Germplasm Name 6", "GroupName", 0, 99996);
        germplasmListDatas.add(germplasmListData);

        germList = manager.getGermplasmListByName("Test List #1", 0, 1, Operation.EQUAL, Database.LOCAL).get(0);
        germplasmListData = new GermplasmListData(null, germList, Integer.valueOf(1), 2, "EntryCode", "SeedSource", "Germplasm Name 1",
                "GroupName", 0, 99990);
        germplasmListDatas.add(germplasmListData);

        germplasmListData = new GermplasmListData(null, germList, Integer.valueOf(1), 3, "EntryCode", "SeedSource", "Germplasm Name 2",
                "GroupName", 0, 99991);
        germplasmListDatas.add(germplasmListData);

        germList = manager.getGermplasmListByName("Test List #2", 0, 1, Operation.EQUAL, Database.LOCAL).get(0);
        germplasmListData = new GermplasmListData(null, germList, Integer.valueOf(3), 2, "EntryCode", "SeedSource",
                "Germplasm Name 5", "GroupName", 0, 99995);
        germplasmListDatas.add(germplasmListData);

        germList = manager.getGermplasmListByName("Test List #3", 0, 1, Operation.EQUAL, Database.LOCAL).get(0);
        germplasmListData = new GermplasmListData(null, germList, Integer.valueOf(4), 1, "EntryCode", "SeedSource",
                "Germplasm Name 7", "GroupName", 0, 99997);
        germplasmListDatas.add(germplasmListData);
        germplasmListData = new GermplasmListData(null, germList, Integer.valueOf(4), 2, "EntryCode", "SeedSource",
                "Germplasm Name 8", "GroupName", 0, 99998);
        germplasmListDatas.add(germplasmListData);
        germplasmListData = new GermplasmListData(null, germList, Integer.valueOf(4), 3, "EntryCode", "SeedSource",
                "Germplasm Name 9", "GroupName", 0, 99999);
        germplasmListDatas.add(germplasmListData);

       Debug.println(0, "testAddGermplasmListDatas() records added: " + manager.addGermplasmListData(germplasmListDatas));
        Debug.println(0, "testAddGermplasmListDatas() RESULTS: ");
        for (GermplasmListData listData : germplasmListDatas) {
            if (listData.getId() != null) {
                Debug.println(0, "  " + listData);
            }
        }
    }

    @Test
    public void testUpdateGermplasmListData() throws Exception {
        List<GermplasmListData> germplasmListDatas = new ArrayList<GermplasmListData>();
        
        //GermplasmListData prior to update
        List<String> germplasmListDataStrings = new ArrayList<String>();
        
        // Get germListId of GermplasmList with name Test List #1
        Integer germListId = manager.getGermplasmListByName("Test List #1", 0, 1, Operation.EQUAL, Database.LOCAL).get(0).getId();
        
        GermplasmListData germplasmListData = manager.getGermplasmListDataByListId(germListId, 0, 5).get(0); 
        germplasmListDataStrings.add(germplasmListData.toString());
        germplasmListData.setDesignation("Germplasm Name 3, UPDATE");
        germplasmListDatas.add(germplasmListData);

        germplasmListData  = manager.getGermplasmListDataByListId(germListId, 0, 5).get(1); 
        germplasmListDataStrings.add(germplasmListData.toString());
        germplasmListData.setDesignation("Germplasm Name 4, UPDATE");
        germplasmListDatas.add(germplasmListData);

        Debug.println(0, "testUpdateGermplasmListData() updated records: " + manager.updateGermplasmListData(germplasmListDatas));
        Debug.println(0, "testUpdateGermplasmListData() RESULTS: ");
        for (int i=0; i< germplasmListDatas.size(); i++){
            Debug.println(0, "  FROM " + germplasmListDataStrings.get(i));
            Debug.println(0, "  TO   " + germplasmListDatas.get(i));   
        }
    }

    @Test
    public void testDeleteGermplasmListData() throws Exception {
        List<GermplasmListData> listData = new ArrayList<GermplasmListData>();

        Debug.println(0, "Test Case #1: test deleteGermplasmListDataByListId");
        GermplasmList germplasmList = manager.getGermplasmListByName("Test List #1", 0, 1, Operation.EQUAL, Database.LOCAL).get(0);
        listData.addAll(manager.getGermplasmListDataByListId(germplasmList.getId(), 0, 10));
        manager.deleteGermplasmListDataByListId(germplasmList.getId());

        Debug.println(0, "Test Case #2: test deleteGermplasmListDataByListIdEntryId");
        germplasmList = manager.getGermplasmListByName("Test List #4", 0, 1, Operation.EQUAL, Database.LOCAL).get(0);
        listData.add(manager.getGermplasmListDataByListIdAndEntryId(germplasmList.getId(), /*entryId=*/ 2));
        manager.deleteGermplasmListDataByListIdEntryId(germplasmList.getId(), 2);
        
        Debug.println(0, "Test Case #3: test deleteGermplasmListData(data)");
        germplasmList = manager.getGermplasmListByName("Test List #4", 0, 1, Operation.EQUAL, Database.LOCAL).get(0);
        GermplasmListData data = manager.getGermplasmListDataByListIdAndEntryId(germplasmList.getId(), /*entryId=*/ 1);
        listData.add(data);
        manager.deleteGermplasmListData(data);
       
        Debug.println(0, "Test Case #4: test deleteGermplasmListData(list of data)");
        germplasmList = manager.getGermplasmListByName("Test List #3", 0, 1, Operation.EQUAL, Database.LOCAL).get(0);
        List<GermplasmListData> toBeDeleted = new ArrayList<GermplasmListData>();
        toBeDeleted.addAll(manager.getGermplasmListDataByListId(germplasmList.getId(), 0, 10));
        listData.addAll(toBeDeleted);
        manager.deleteGermplasmListData(toBeDeleted);
        
        
        Debug.println(0, "testDeleteGermplasmListData() records to delete: " + listData.size());        
        Debug.println(0, "testDeleteGermplasmListData() deleted records: " + listData);
        for (GermplasmListData listItem : listData) {
            Debug.println(0, "  " + listItem);
            //check if status in database was set to deleted
            Assert.assertEquals(STATUS_DELETED, getGermplasmListDataStatus(listItem.getId()));
       }
    }

    @Test
    public void testDeleteGermplasmList() throws Exception {
        List<GermplasmList> germplasmLists = new ArrayList<GermplasmList>();
        List<GermplasmListData> listDataList = new ArrayList<GermplasmListData>();

        Debug.println(0, "Test Case #1: test deleteGermplasmListByListId");
        GermplasmList germplasmList = manager.getGermplasmListByName("Test List #1", 0, 1, Operation.EQUAL, Database.LOCAL).get(0);
        germplasmLists.add(germplasmList);
        listDataList.addAll(germplasmList.getListData());
        Debug.println(0, "\tremoved " + manager.deleteGermplasmListByListId(germplasmList.getId()) + " record(s)");

        Debug.println(0, "Test Case #2: test deleteGermplasmList(data)");
        germplasmList = manager.getGermplasmListByName("Test List #4", 0, 1, Operation.EQUAL, Database.LOCAL).get(0);
        germplasmLists.add(germplasmList);
        listDataList.addAll(germplasmList.getListData());
        Debug.println(0, "\tremoved " + manager.deleteGermplasmList(germplasmList) + " record(s)");
		
        Debug.println(0, "Test Case #3: test deleteGermplasmList(list of data) - with cascade delete");
        List<GermplasmList> toBeDeleted = new ArrayList<GermplasmList>();
        /*germplasmList = manager.getGermplasmListByName("Test List #2", 0, 1, Operation.EQUAL, Database.LOCAL).get(0);
        toBeDeleted.add(germplasmList);
        listDataList.addAll(germplasmList.getListData());
        */
        germplasmList = manager.getGermplasmListByName("Test List #3", 0, 1, Operation.EQUAL, Database.LOCAL).get(0);
        toBeDeleted.add(germplasmList);
        listDataList.addAll(germplasmList.getListData());
        germplasmLists.addAll(toBeDeleted);
        Debug.println(0, "\tremoved " + manager.deleteGermplasmList(toBeDeleted));

        Debug.println(0, "testDeleteGermplasmList() records to delete: " + germplasmLists.size());
        Debug.println(0, "testDeleteGermplasmList() deleted list records: ");
        for (GermplasmList listItem : germplasmLists) {
            Debug.println(0, "  " + listItem);
            Assert.assertEquals(STATUS_DELETED, getGermplasmListStatus(listItem.getId()));
        }
        //checking cascade delete
        Debug.println(0, "testDeleteGermplasmList() deleted data records: ");
        for (GermplasmListData listData : listDataList) {
        	Debug.println(0, " " + listData);
            Assert.assertEquals(STATUS_DELETED, getGermplasmListDataStatus(listData.getId()));
        }
    }

    @Test
    public void testGetTopLevelLists() throws Exception {
        int count = (int) manager.countAllTopLevelLists(Database.CENTRAL);
        List<GermplasmList> topLevelFolders = manager.getAllTopLevelLists(0, count, Database.CENTRAL);
        Debug.println(0, "testGetTopLevelLists(0, 100, Database.CENTRAL) RESULTS: " + count);
        for (GermplasmList listItem : topLevelFolders) {
            Debug.println(0, "  " + listItem);
        }
        // Verify using: select * from listnms where liststatus <> 9 and lhierarchy = null or lhierarchy = 0
    }

    @Test
    public void testCountTopLevelLists() throws Exception {
        long count = manager.countAllTopLevelLists(Database.CENTRAL);
        Debug.println(0, "testCountTopLevelLists(Database.CENTRAL) RESULTS: " + count);
        // Verify using: select count(*) from listnms where liststatus <> 9 and lhierarchy = null or lhierarchy = 0
    }

    @Test
    public void testGermplasmListByParentFolderId() throws Exception {
        Integer parentFolderId = Integer.valueOf(56);   // Crop tested: Rice
        int count = (int) manager.countGermplasmListByParentFolderId(parentFolderId);
        List<GermplasmList> children = manager.getGermplasmListByParentFolderId(parentFolderId, 0, count);
        Debug.println(0, "testGermplasmListByParentFolderId(" + parentFolderId + ") RESULTS: " + count);
        for (GermplasmList child : children) {
            Debug.println(0, "  " + child);
        }
        // Verify using:  select * from listnms where liststatus <> 9 and lhierarchy = 56;
    }

    @Test
    public void testCountGermplasmListByParentFolderId() throws Exception {
        Integer parentFolderId = Integer.valueOf(56);   // Crop tested: Rice
        Long result = manager.countGermplasmListByParentFolderId(parentFolderId);
        Debug.println(0, "testCountGermplasmListByParentFolderId(" + parentFolderId + ") RESULTS: " + result);
        // Verify using:  select count(*) from listnms where liststatus <> 9 and lhierarchy = 56;
    }

    @Test
    public void testGetGermplasmListTypes() throws Exception {
    	List<UserDefinedField> userDefinedFields = new ArrayList<UserDefinedField>();
    	userDefinedFields = manager.getGermplasmListTypes();
        Debug.println(0, "testGetGermplasmListTypes() RESULTS:" + userDefinedFields);
    }

    @Test
    public void testGetGermplasmNameTypes() throws Exception {
    	List<UserDefinedField> userDefinedFields = new ArrayList<UserDefinedField>();
    	userDefinedFields = manager.getGermplasmNameTypes();
        Debug.println(0, "testGetGermplasmNameTypes() RESULTS:" + userDefinedFields);
    }   
    
   
    @Test
    public void testGetAllTopLevelListsBatched() throws Exception {
    	int batchSize = 1;
    	List<GermplasmList> results = manager.getAllTopLevelListsBatched(batchSize, Database.CENTRAL);
        Assert.assertNotNull(results);
        Assert.assertTrue(!results.isEmpty());
        Debug.println(0, "testGetAllTopLevelListsBatched("+batchSize+") Results: ");
        for (GermplasmList result : results) {
            Debug.println(0, "  " + result);
        }
        Debug.println(0, "Number of record/s: " + results.size());
    }
    
    @Test
    public void testGetGermplasmListByParentFolderId() throws Exception {
    	Integer parentId = Integer.valueOf(0);
    	List<GermplasmList> results = manager.getGermplasmListByParentFolderId(parentId, 0, 100);
        Assert.assertNotNull(results);
        Assert.assertTrue(!results.isEmpty());
        Debug.println(0, "testGetGermplasmListByParentFolderId("+parentId+") Results: ");
        for (GermplasmList result : results) {
            Debug.println(0, "  " + result);
        }
        Debug.println(0, "Number of record/s: " + results.size());
    }
    
    @Test
    public void testGetGermplasmListByParentFolderIdBatched() throws Exception {
    	Integer parentId = Integer.valueOf(0);
    	int batchSize = 1;
    	List<GermplasmList> results = new ArrayList<GermplasmList>();
    	results = manager.getGermplasmListByParentFolderIdBatched(parentId, batchSize);
        Assert.assertNotNull(results);
        Assert.assertTrue(!results.isEmpty());
        Debug.println(0, "testGetGermplasmListByParentFolderIdBatched() Results: ");
        for (GermplasmList result : results) {
            Debug.println(0, "  " + result);
        }
        Debug.println(0, "Number of record/s: " + results.size());
    }
    
    @Test
    public void testSearchGermplasmList() throws MiddlewareQueryException{
        //String q = "50533";
        //String q = "philippines";
    	//String q = "HB2009DS";
        String q = "dinurado";  
    	
        List<GermplasmList> results = manager.searchForGermplasmList(q, Operation.EQUAL);
        
        Debug.println(0, "###############################");
        Debug.println(0, " searchForGermplasmList("+q+")");
        Debug.println(0, "###############################");
        
        for(GermplasmList g : results){
          Debug.println(3, g.getId() + " - " + g.getName());
        }
    } 
    
    @Test
    public void testSaveListDataColumns() throws MiddlewareQueryException {
    	List<ListDataInfo> listDataCollection = new ArrayList<ListDataInfo>();
    	
    	//list Data ID 1
    	List<ListDataColumn> columns = new ArrayList<ListDataColumn>();
    	columns.add(new ListDataColumn("Preferred Name", "IRGC65"));
    	columns.add(new ListDataColumn("Germplasm Date", ""));
    	columns.add(new ListDataColumn("Location1", null));
    	columns.add(new ListDataColumn("Location2", "IRRI222"));
    	columns.add(new ListDataColumn("Location3", "IRRI333"));
    	listDataCollection.add( new ListDataInfo(-464, columns)); // Change the List Data ID applicable for local db
    	
    	//list Data ID 2
    	columns = new ArrayList<ListDataColumn>();
    	columns.add(new ListDataColumn("Location1", "IRRI1"));
    	columns.add(new ListDataColumn("Location2", "IRRI2"));
    	columns.add(new ListDataColumn("Location3", "IRRI3"));
    	listDataCollection.add( new ListDataInfo(-462, columns)); // Change the List Data ID applicable for local db
    	
    	
        List<ListDataInfo> results = manager.saveListDataColumns(listDataCollection);
        for (ListDataInfo data : results){
        	data.print(0);
        }
    } 
    
    @Test
    public void testGetAdditionalColumnsForList() throws MiddlewareQueryException{
    	GermplasmListNewColumnsInfo listInfo = manager.getAdditionalColumnsForList(-14);
    	listInfo.print(0);
    }
    
    
    @AfterClass
    public static void tearDown() throws Exception {
    	removeTestData();
        factory.close();
    }
    
    private static String getTestDataIds() {
        StringBuffer sqlString = new StringBuffer();
        for (int i = 0; i < testDataIds.size(); i++) {
        	if (i > 0) {
        		sqlString.append(",");
        	}
        	sqlString.append(testDataIds.get(i));
        }
    	return sqlString.toString();
    }
    
    private static void removeTestData() throws Exception {
    	if (testDataIds != null && testDataIds.size() > 0) {
	        Session session = ((DataManager) manager).getCurrentSessionForLocal();
	        
	        String idString = getTestDataIds();
	        
	        Query deleteQuery = session.createSQLQuery("DELETE FROM listdata WHERE listid IN (" + idString + ")");
	        deleteQuery.executeUpdate();
	        deleteQuery = session.createSQLQuery("DELETE FROM listnms WHERE listid IN (" + idString + ")");
	        deleteQuery.executeUpdate();
	        session.flush();
    	}
    }
    
    private Integer getGermplasmListStatus(Integer id) throws Exception {
    	Session session = ((DataManager) manager).getCurrentSessionForLocal();
    	Query query = session.createSQLQuery("SELECT liststatus FROM listnms WHERE listid = " + id);
    	return (Integer) query.uniqueResult();
    }
    
    private Integer getGermplasmListDataStatus(Integer id) throws Exception {
    	Session session = ((DataManager) manager).getCurrentSessionForLocal();
    	Query query = session.createSQLQuery("SELECT lrstatus FROM listdata WHERE lrecid = " + id);
    	return (Integer) query.uniqueResult();
    }

}