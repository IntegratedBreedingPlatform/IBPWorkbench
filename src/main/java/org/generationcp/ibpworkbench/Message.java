package org.generationcp.ibpworkbench;

public enum Message {
     ACCOUNT
    ,ACTIONS
    ,ACTIVITY_NEXT
    ,ACTIVITY_RECENT
    ,CONTACT_CREATE
    ,DASHBOARD
    ,DATASETS
    ,ERROR_LOGIN_INVALID
    ,HELP
    ,HOME
    ,EMAIL
    ,LOGIN
    ,PASSWORD
    ,LOGIN_TITLE
    ,PROJECT_CREATE
    ,PROJECT_DASHBOARD_TITLE
    ,PROJECT_TITLE
    ,PROJECT_TABLE_CAPTION
    ,PROJECT
    ,ACTION
    ,DATE
    ,DATE_DUE
    ,OWNER
    ,STATUS
    ,RECENT
    ,SIGNOUT
    ,USER_GUIDE
    ,USER_GUIDE_1
    ,USERNAME
    ,WORKBENCH_TITLE
    
    // Workbench Dashboard
    ,ACTIVITIES
    ,PROJECT_DETAIL
    ,ROLES
    ,ROLE_TABLE_TITLE
    ,PROJECT_TABLE_TOOLTIP
    ,ROLE_TABLE_TOOLTIP
    ,START_DATE
    
    //General
    ,SAVE,
    CANCEL,
    
    //Register User Account
    REGISTER_USER_ACCOUNT,
    REGISTER_USER_ACCOUNT_FORM,
    REGISTER_SUCCESS,
    REGISTER_SUCCESS_DESCRIPTION,
    USER_ACC_POS_TITLE,
    USER_ACC_FNAME,
    USER_ACC_MIDNAME,
    USER_ACC_LNAME,
    USER_ACC_EMAIL,
    USER_ACC_USERNAME,
    USER_ACC_PASSWORD,
    USER_ACC_PASSWORD_CONFIRM,
    
    //Error Notification
    UPLOAD_ERROR,
    UPLOAD_ERROR_DESC,
    LAUNCH_TOOL_ERROR,
    LAUNCH_TOOL_ERROR_DESC,
    INVALID_TOOL_ERROR_DESC,
    LOGIN_ERROR,
    LOGIN_DB_ERROR_DESC,
    DATABASE_ERROR,
    SAVE_PROJECT_ERROR_DESC,
    SAVE_USER_ACCOUT_ERROR_DESC,
    ADD_CROP_TYPE_ERROR_DESC,
    FILE_NOT_FOUND_ERROR,
    FILE_NOT_FOUND_ERROR_DESC,
    FILE_ERROR,
    FILE_CANNOT_PROCESS_DESC,
    FILE_CANNOT_OPEN_DESC,
    PARSE_ERROR,
    WORKFLOW_DATE_PARSE_ERROR_DESC,
    CONFIG_ERROR,
    CONTACT_ADMIN_ERROR_DESC,
    INVALID_URI_ERROR,
    INVALID_URI_ERROR_DESC,
    INVALID_URI_SYNTAX_ERROR_DESC,
    INVALID_URL_PARAM_ERROR,
    INVALID_URL_PARAM_ERROR_DESC,
    
    //Tray Notification
    UPLOAD_SUCCESS,
    UPLOAD_SUCCESS_DESC,
    
    LOC_NAME,
    LOC_ABBR
    
    // Tool configuration update
    ,UPDATING
    ,UPDATING_TOOLS_CONFIGURATION
    
    // Create new project
    ,BASIC_DETAILS_LABEL
    ,USER_ROLES_LABEL
    ,PROJECT_MEMBERS_LABEL
    ,BREEDING_METHODS_LABEL
    ,LOCATIONS_LABEL
}
