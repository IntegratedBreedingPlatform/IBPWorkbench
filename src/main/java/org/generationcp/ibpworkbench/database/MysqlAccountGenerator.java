/***************************************************************
 * Copyright (c) 2012, All Rights Reserved.
 * 
 * Generation Challenge Programme (GCP)
 * 
 * @author Kevin L. Manansala
 * 
 * This software is licensed for use under the terms of the 
 * GNU General Public License (http://bit.ly/8Ztv8M) and the 
 * provisions of Part F of the Generation Challenge Programme 
 * Amended Consortium Agreement (http://bit.ly/KQX1nL)
 * 
 **************************************************************/
package org.generationcp.ibpworkbench.database;

import org.generationcp.commons.exceptions.InternationalizableException;
import org.generationcp.commons.util.ResourceFinder;
import org.generationcp.ibpworkbench.Message;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.manager.api.WorkbenchDataManager;
import org.generationcp.middleware.pojos.User;
import org.generationcp.middleware.pojos.workbench.CropType;
import org.generationcp.middleware.pojos.workbench.Project;
import org.generationcp.middleware.pojos.workbench.ProjectUserMysqlAccount;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Configurable;

import javax.annotation.Resource;
import java.io.*;
import java.net.URISyntaxException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * Class which takes care of adding mysql user accounts for members of a workbench project.
 * Patterned after IBDBGeneratorLocalDb.
 * 
 * @author Kevin L. Manansala
 *
 */
@Configurable
public class MysqlAccountGenerator implements Serializable{

    private static final long serialVersionUID = -7078581017175422156L;
    
    private static final Logger LOG = LoggerFactory.getLogger(MysqlAccountGenerator.class);
    
    public static final String SQL_CREATE_USER = "CREATE USER";
    public static final String SQL_GRANT_SELECT_AND_EXECUTE = "GRANT SELECT, EXECUTE ON ";
    public static final String SQL_PERCENT_SIGN = "%";
    public static final String SPACE = " ";
    
    public static final String WORKBENCH_PROP_INSTALLER_MODE = "workbench.installer.mode";

        public static final String INSTALLER_MODE_LAN = "lan";
        public static final String INSTALLER_MODE_LOCAL = "local";

    private CropType cropType;
    private Long projectId;
    private Map<Integer, String> idAndNameOfProjectMembers;
    private WorkbenchDataManager dataManager;
    
    private String workbenchHost;
    private String workbenchPort;
    private String workbenchUsername;
    private String workbenchPassword;
    private String workbenchURL;
    
    private Connection connection;

    @Resource
    private Properties workbenchProperties;

    public MysqlAccountGenerator(CropType cropType, Long projectId, Map<Integer, String> idAndNameOfProjectMembers, WorkbenchDataManager dataManager){
        this.cropType = cropType;
        this.projectId = projectId;
        this.idAndNameOfProjectMembers = idAndNameOfProjectMembers;
        this.dataManager = dataManager;
    }
    
    public MysqlAccountGenerator() {
	
    }

	public boolean generateMysqlAccounts() throws InternationalizableException {
        boolean isGenerationSuccess = false;
        
        try{
            createLocalConnection();
            executeGrantStatements();
            storeWokrbenchUserToMysqlAccountMappings();
            
            isGenerationSuccess = true;
        } catch (InternationalizableException e) {
            isGenerationSuccess = false;            
            throw e;
        } 
        
        return isGenerationSuccess;
    }
    
    private void createLocalConnection() throws InternationalizableException {

        if (this.connection == null) {

            Properties prop = new Properties();

            InputStream in = null;
            try {
                try {
                    in = new FileInputStream(new File(ResourceFinder.locateFile(IBDBGenerator.WORKBENCH_PROP).toURI()));
                } catch (IllegalArgumentException ex) {
                    in = Thread.currentThread().getContextClassLoader().getResourceAsStream(IBDBGenerator.WORKBENCH_PROP);
                }
                prop.load(in);

                workbenchHost = prop.getProperty(IBDBGenerator.WORKBENCH_PROP_HOST);
                workbenchPort = prop.getProperty(IBDBGenerator.WORKBENCH_PROP_PORT);
                workbenchUsername = prop.getProperty(IBDBGenerator.WORKBENCH_PROP_USER);
                workbenchPassword = prop.getProperty(IBDBGenerator.WORKBENCH_PROP_PASSWORD);
                workbenchURL = "jdbc:mysql://" + workbenchHost + ":" + workbenchPort;
            } catch (URISyntaxException e) {
                IBDBGenerator.handleConfigurationError(e);
            } catch (IOException e) {
                IBDBGenerator.handleConfigurationError(e);
            }
            finally {
                if (in != null) {
                    try {
                        in.close();
                    }
                    catch (IOException e) {
                        // intentionally empty
                    }
                }
            }

            try {
                connection = DriverManager.getConnection(workbenchURL, workbenchUsername, workbenchPassword);
            } catch (SQLException e) {
                handleDatabaseError(e);
            }

        }
    }
    
    private void executeGrantStatements() throws InternationalizableException {
        //execute grant statements
        Statement statement = null;
        String cropDatabaseName = cropType.getDbName();

        try{
            statement = this.connection.createStatement();
            
            String grantFormat = "GRANT ALL ON %s.* TO %s@'%s' IDENTIFIED BY '%s'";

            for(Integer id : this.idAndNameOfProjectMembers.keySet()){
                String username = this.idAndNameOfProjectMembers.get(id);
                String password = username.substring(0, 11);

                if (isLanInstallerMode()) {
                    String grant = String
                            .format(grantFormat, cropDatabaseName, username, "%", password);
                    String localhostGrant = String
                            .format(grantFormat, cropDatabaseName, username, "localhost",
                                    password);

                    statement.addBatch(grant);
                    statement.addBatch(localhostGrant);
                } else {
                    processGrant(statement, cropDatabaseName, username,password);
                }

            }

            statement.addBatch("FLUSH PRIVILEGES");
            statement.executeBatch();

        } catch (SQLException e) {
            handleDatabaseError(e);
        } finally {
            if (statement != null) {
                try {
                    statement.close();
                } catch (SQLException e) {
                    handleDatabaseError(e);
                }
            }
        }
    }

    protected void processGrant(Statement statement, String cropDatabaseName, 
		String username, String password) throws SQLException {
        StringBuilder grantStatement = new StringBuilder();

        grantStatement.append(SQL_GRANT_SELECT_AND_EXECUTE);
        grantStatement.append(cropDatabaseName);
        grantStatement.append(IBDBGenerator.SQL_PERIOD);
        grantStatement.append(IBDBGenerator.DEFAULT_ALL);
        grantStatement.append(IBDBGenerator.SQL_TO);
        grantStatement.append(IBDBGenerator.SQL_SINGLE_QUOTE);
        grantStatement.append(username);
        grantStatement.append(IBDBGenerator.SQL_SINGLE_QUOTE);
        grantStatement.append(IBDBGenerator.SQL_AT_SIGN);
        grantStatement.append(IBDBGenerator.SQL_SINGLE_QUOTE);
        grantStatement.append(IBDBGenerator.DEFAULT_LOCAL_HOST);
        grantStatement.append(IBDBGenerator.SQL_SINGLE_QUOTE);
        grantStatement.append(IBDBGenerator.SQL_IDENTIFIED_BY);
        grantStatement.append(IBDBGenerator.SQL_SINGLE_QUOTE);
        grantStatement.append(password);
        grantStatement.append(IBDBGenerator.SQL_SINGLE_QUOTE);

        statement.addBatch(grantStatement.toString());
        statement.addBatch("FLUSH PRIVILEGES");
        statement.executeBatch();
    }
    
    private void storeWokrbenchUserToMysqlAccountMappings() throws InternationalizableException {
        List<ProjectUserMysqlAccount> mappingRecords = new ArrayList<ProjectUserMysqlAccount>();
        Project project = null;
        try{
            project = this.dataManager.getProjectById(this.projectId);
        } catch(MiddlewareQueryException ex) {
            LOG.error("Error with getting Project with id: " + this.projectId
                    + " in storing mappings of users to mysql accounts: " + ex.toString(), ex);
            throw new InternationalizableException(ex, 
                    Message.DATABASE_ERROR, Message.CONTACT_DEV_ERROR_DESC);
        }
        
        if(project != null){
            try{
                for(Integer userid : this.idAndNameOfProjectMembers.keySet()){
                    User userRecord = this.dataManager.getUserById(userid.intValue());
                    String username = this.idAndNameOfProjectMembers.get(userid);
                    String password = username.length() > 16 ? username.substring(0, 16) : username;
                    
                    if(userRecord != null){
                        ProjectUserMysqlAccount mappingRecord = new ProjectUserMysqlAccount();
                        mappingRecord.setProject(project);
                        mappingRecord.setUser(userRecord);
                        mappingRecord.setMysqlUsername(username);
                        mappingRecord.setMysqlPassword(password);
                        mappingRecords.add(mappingRecord);
                    }
                }
                
                this.dataManager.addProjectUserMysqlAccounts(mappingRecords);
                
            } catch(MiddlewareQueryException ex){
                LOG.error("Error with saving mappings of user to mysql accounts: " 
                        + ex.toString(), ex);
                throw new InternationalizableException(ex, 
                        Message.DATABASE_ERROR, Message.CONTACT_DEV_ERROR_DESC);
            }
        }             
    }
    
    private void handleDatabaseError(Exception e) throws InternationalizableException {
        LOG.error(e.toString(), e);
        throw new InternationalizableException(e, 
                Message.DATABASE_ERROR, Message.CONTACT_ADMIN_ERROR_DESC);
    }

	
	public void setCropType(CropType cropType) {
		this.cropType = cropType;
	}

	
	public void setProjectId(Long projectId) {
		this.projectId = projectId;
	}

	
	public void setIdAndNameOfProjectMembers(Map<Integer, String> idAndNameOfProjectMembers) {
		this.idAndNameOfProjectMembers = idAndNameOfProjectMembers;
	}

	
	public void setDataManager(WorkbenchDataManager dataManager) {
		this.dataManager = dataManager;
	}

    protected boolean isLanInstallerMode() {
        String installerMode = workbenchProperties
                .getProperty(WORKBENCH_PROP_INSTALLER_MODE, INSTALLER_MODE_LOCAL);
        return INSTALLER_MODE_LAN.equals(installerMode);
    }
}
