package com.mattvoget.sarlacc.client;

import java.util.List;

import com.mattvoget.sarlacc.models.App;
import com.mattvoget.sarlacc.models.AppRole;
import com.mattvoget.sarlacc.models.Role;
import com.mattvoget.sarlacc.models.Token;
import com.mattvoget.sarlacc.models.User;

public interface SarlaccUserService {
	
	public static final String TOKEN_NAME = "x-access-token";

	public Token authenticate(String username, String password);
	
	public User getUser(String accessToken);
	
	public List<AppRole> getUserAppRoles(String accessToken);
	
	public Role getUserRoleForApp(String accessToken, App app);
	
	public boolean isUserAdminForApp(String accessToken, App app);
	
}
