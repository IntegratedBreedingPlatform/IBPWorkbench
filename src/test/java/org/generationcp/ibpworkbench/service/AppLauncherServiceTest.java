package org.generationcp.ibpworkbench.service;

import org.generationcp.commons.context.ContextInfo;
import org.generationcp.commons.spring.util.ContextUtil;
import org.generationcp.ibpworkbench.exception.AppLaunchException;
import org.generationcp.ibpworkbench.util.ToolUtil;
import org.generationcp.middleware.manager.api.WorkbenchDataManager;
import org.generationcp.middleware.pojos.workbench.CropType;
import org.generationcp.middleware.pojos.workbench.Project;
import org.generationcp.middleware.pojos.workbench.Tool;
import org.generationcp.middleware.pojos.workbench.ToolName;
import org.generationcp.middleware.pojos.workbench.ToolType;
import org.generationcp.middleware.pojos.workbench.WorkbenchUser;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

@RunWith(MockitoJUnitRunner.class)
public class AppLauncherServiceTest {

	public static final String SCHEME = "http";
	public static final String HOST_NAME = "host-name";
	public static final int PORT = 18080;
	public static final String SAMPLE_BASE_URL = "somewhere/out/there";

	public static final String WORKBENCH_CONTEXT_PARAMS = "&loggedInUserId=5&selectedProjectId=1&authToken=VXNlck5hbWU&cropName=Maize&programUUID=862c4a31-a5fa-4a74-8165-80944301b9b9";
	public static final String RESTART_URL_STR = "?restartApplication";

	public static final int LOGGED_IN_USER_ID = 5;
	public static final Long PROJECT_ID = Long.valueOf(1);
	public static final String USER_NAME = "UserName";

	@Mock
	HttpServletRequest request;

	@Mock
	private WorkbenchDataManager workbenchDataManager;

	@Mock
	private ToolUtil toolUtil;

	@Mock
	private ContextUtil contextUtil;

	@Mock
	private SecurityContext securityContext;

	@InjectMocks
	private final AppLauncherService appLauncherService = Mockito.spy(new AppLauncherService());

	@Before
	public void setUp() throws Exception {

		final ContextInfo contextInfo = new ContextInfo(LOGGED_IN_USER_ID, PROJECT_ID);

		Mockito.when(this.contextUtil.getContextInfoFromSession()).thenReturn(contextInfo);

		RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(this.request));

		final Authentication authentication = Mockito.mock(Authentication.class);
		Mockito.when(this.securityContext.getAuthentication()).thenReturn(authentication);
		Mockito.when(authentication.getName()).thenReturn(USER_NAME);
		SecurityContextHolder.setContext(this.securityContext);

		Mockito.when(this.request.getScheme()).thenReturn(AppLauncherServiceTest.SCHEME);
		Mockito.when(this.request.getServerName()).thenReturn(AppLauncherServiceTest.HOST_NAME);
		Mockito.when(this.request.getServerPort()).thenReturn(AppLauncherServiceTest.PORT);
	}

	@Test
	public void testLaunchTool() throws Exception, AppLaunchException {

		final Project project = new Project();
		project.setCropType(new CropType("Maize"));
		Mockito.when(workbenchDataManager.getProjectById(Mockito.any(Long.class))).thenReturn(project);
		// case 1: web tool
		final Tool aWebTool = new Tool();
		aWebTool.setToolName(ToolName.BM_LIST_MANAGER_MAIN.getName());
		aWebTool.setToolType(ToolType.WEB);
		aWebTool.setPath(SAMPLE_BASE_URL);

		// case 2: gdms
		final Tool gdmsTool = new Tool();
		gdmsTool.setToolName(ToolName.GDMS.getName());
		gdmsTool.setToolType(ToolType.WEB_WITH_LOGIN);

		// case 3: NATIVE
		final Tool nativeTool = new Tool();
		nativeTool.setToolName(ToolName.BREEDING_VIEW.getName());
		nativeTool.setToolType(ToolType.NATIVE);

		Mockito.when(this.workbenchDataManager.getToolWithName(ToolName.BM_LIST_MANAGER_MAIN.getName())).thenReturn(aWebTool);
		Mockito.when(this.workbenchDataManager.getToolWithName(ToolName.GDMS.getName())).thenReturn(gdmsTool);
		Mockito.when(this.workbenchDataManager.getToolWithName(ToolName.BREEDING_VIEW.getName())).thenReturn(nativeTool);

		Mockito.doNothing().when(this.appLauncherService).launchNativeapp(Matchers.any(Tool.class));
		Mockito.doReturn("/result").when(this.appLauncherService).launchWebappWithLogin(Matchers.any(Tool.class));

		// the tests itself
		this.appLauncherService.launchTool(ToolName.BREEDING_VIEW.getName(), null);
		Mockito.verify(this.appLauncherService).launchNativeapp(nativeTool);
		Mockito.verify(this.contextUtil, Mockito.atLeastOnce())
				.logProgramActivity(nativeTool.getTitle(), AppLauncherService.LAUNCHED + nativeTool.getTitle());

		this.appLauncherService.launchTool(ToolName.BM_LIST_MANAGER_MAIN.getName(), null);
		Mockito.verify(this.appLauncherService).launchWebapp(aWebTool, null);
		Mockito.verify(this.contextUtil, Mockito.atLeastOnce())
				.logProgramActivity(aWebTool.getTitle(), AppLauncherService.LAUNCHED + aWebTool.getTitle());

		this.appLauncherService.launchTool(ToolName.GDMS.getName(), null);
		Mockito.verify(this.appLauncherService).launchWebappWithLogin(gdmsTool);
		Mockito.verify(this.contextUtil, Mockito.atLeastOnce())
				.logProgramActivity(gdmsTool.getTitle(), AppLauncherService.LAUNCHED + gdmsTool.getTitle());
	}

	@Test
	public void testLaunchNativeapp() throws Exception, AppLaunchException {
		final Tool aNativeTool = new Tool();

		// for vaadin type params with a dash
		aNativeTool.setToolName(ToolName.BREEDING_VIEW.getName());
		aNativeTool.setPath(AppLauncherServiceTest.SAMPLE_BASE_URL);
		aNativeTool.setToolType(ToolType.NATIVE);

		Mockito.doNothing().when(this.toolUtil).closeNativeTool(aNativeTool);

		// launch the native app!
		this.appLauncherService.launchNativeapp(aNativeTool);

		Mockito.verify(this.appLauncherService, Mockito.times(1)).launchNativeapp(aNativeTool);

	}

	@Test
	public void testLaunchWebapp() throws Exception {
		Tool aWebTool = new Tool();
		final Project project = new Project();
		project.setCropType(new CropType("Maize"));
		project.setUniqueID("862c4a31-a5fa-4a74-8165-80944301b9b9");
		Mockito.when(workbenchDataManager.getProjectById(Mockito.any(Long.class))).thenReturn(project);
		// for vaadin type params with a dash
		aWebTool.setToolName(ToolName.BM_LIST_MANAGER_MAIN.getName());
		aWebTool.setPath(AppLauncherServiceTest.SAMPLE_BASE_URL);
		aWebTool.setToolType(ToolType.WEB);

		String urlResult = this.appLauncherService.launchWebapp(aWebTool, AppLauncherServiceTest.LOGGED_IN_USER_ID);

		Assert.assertEquals("should return correct url for List manager app",
				String.format("%s://%s:%d/%s%d%s", AppLauncherServiceTest.SCHEME, AppLauncherServiceTest.HOST_NAME,
						AppLauncherServiceTest.PORT, AppLauncherServiceTest.SAMPLE_BASE_URL, AppLauncherServiceTest.LOGGED_IN_USER_ID,
						AppLauncherServiceTest.RESTART_URL_STR + AppLauncherServiceTest.WORKBENCH_CONTEXT_PARAMS), urlResult);

		aWebTool = new Tool();

		aWebTool.setToolName(ToolName.STUDY_MANAGER_FIELDBOOK_WEB.getName());
		aWebTool.setPath(AppLauncherServiceTest.SAMPLE_BASE_URL);
		aWebTool.setToolType(ToolType.WEB);
		urlResult = this.appLauncherService.launchWebapp(aWebTool, AppLauncherServiceTest.LOGGED_IN_USER_ID);

		Assert.assertEquals("should return correct url for fieldbook Study app",
				String.format("%s://%s:%d/%s/openTrial/%d%s", AppLauncherServiceTest.SCHEME, AppLauncherServiceTest.HOST_NAME,
						AppLauncherServiceTest.PORT, AppLauncherServiceTest.SAMPLE_BASE_URL, AppLauncherServiceTest.LOGGED_IN_USER_ID,
						AppLauncherServiceTest.RESTART_URL_STR + AppLauncherServiceTest.WORKBENCH_CONTEXT_PARAMS), urlResult);

	}

	@Test
	public void testLaunchMigratorWebapp() {
		final Tool migratorWebTool = new Tool();

		migratorWebTool.setToolName(ToolName.MIGRATOR.getName());
		migratorWebTool.setPath(AppLauncherServiceTest.SAMPLE_BASE_URL);
		migratorWebTool.setToolType(ToolType.WEB);
		final String urlResult = this.appLauncherService.launchMigratorWebapp(migratorWebTool, AppLauncherServiceTest.LOGGED_IN_USER_ID);

		Assert.assertEquals("should return correct url for List manager app",
				String.format("%s://%s:%d/%s%d", AppLauncherServiceTest.SCHEME, AppLauncherServiceTest.HOST_NAME,
						AppLauncherServiceTest.PORT, AppLauncherServiceTest.SAMPLE_BASE_URL, AppLauncherServiceTest.LOGGED_IN_USER_ID),
				urlResult);
	}

	@Test
	public void testLaunchWebappWithLogin() throws Exception {
		final Tool aWebTool = new Tool();

		// for vaadin type params with a dash
		aWebTool.setToolName(ToolName.GDMS.getName());
		aWebTool.setPath(AppLauncherServiceTest.SAMPLE_BASE_URL);
		aWebTool.setToolType(ToolType.WEB_WITH_LOGIN);

		final WorkbenchUser user = new WorkbenchUser();
		user.setUserid(LOGGED_IN_USER_ID);
		user.setName("a_username");
		user.setPassword("a_password");

		final String urlResult = this.appLauncherService.launchWebappWithLogin(aWebTool);

		Assert.assertEquals("should return correct url for gdms app",
				String.format("%s://%s:%d/%s?restartApplication&loggedInUserId=%s&selectedProjectId=%s", AppLauncherServiceTest.SCHEME,
						AppLauncherServiceTest.HOST_NAME, AppLauncherServiceTest.PORT, AppLauncherServiceTest.SAMPLE_BASE_URL,
						LOGGED_IN_USER_ID, AppLauncherServiceTest.PROJECT_ID), urlResult);

	}
}
