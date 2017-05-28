import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.mattvoget.sarlacc.client.SarlaccUserService;
import com.mattvoget.sarlacc.client.SarlaccUserServiceImpl;
import com.mattvoget.sarlacc.models.App;
import com.mattvoget.sarlacc.models.AppRole;
import com.mattvoget.sarlacc.models.Role;
import com.mattvoget.sarlacc.models.Token;
import com.mattvoget.sarlacc.models.User;

public class SarlaccUserServiceTest {

	private static final String SARLACC_URL = "http://sarlacc-svc.voget.io";
	private static final String SARLACC_CLIENT_ID = "sarlacc";
	private static final String SARLACC_CLIENT_PASSWORD = "deywannawanga";
	
	private SarlaccUserService sarlaccUserService;
	
	@Before
	public void setup() {
		sarlaccUserService = new SarlaccUserServiceImpl(SARLACC_URL, SARLACC_CLIENT_ID, SARLACC_CLIENT_PASSWORD);
	}
	
	@Test
	public void testAuthenticate() {
		assertNotNull(authenticate());
	}
	
	@Test
	public void testGetUser() {
		User user = sarlaccUserService.getUser(authenticate().getAccessToken());
		assertNotNull(user);
	}

	@Test
	public void testGetUserAppRoles() {
		List<AppRole> appRoles = sarlaccUserService.getUserAppRoles(authenticate().getAccessToken());
		assertNotNull(appRoles);
	}
	
	@Test
	public void testGetUserRoleForApp() {
		App app = new App();
		app.setName("sarlacc");
		Role role = sarlaccUserService.getUserRoleForApp(authenticate().getAccessToken(),app);
		assertNotNull(role);
	}
	
	@Test
	public void testIsUserAdminForApp() {
		App app = new App();
		app.setName("sarlacc");
		assertTrue(!sarlaccUserService.isUserAdminForApp(authenticate().getAccessToken(),app));
	}
	
	private Token authenticate() {
		return sarlaccUserService.authenticate("test", "test");
	}

}
