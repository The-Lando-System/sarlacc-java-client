package com.mattvoget.sarlacc.client;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.client.HttpClientErrorException;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.mattvoget.sarlacc.models.App;
import com.mattvoget.sarlacc.models.AppRole;
import com.mattvoget.sarlacc.models.Role;
import com.mattvoget.sarlacc.models.Token;
import com.mattvoget.sarlacc.models.User;

public class SarlaccUserServiceImpl implements SarlaccUserService {

    private Logger log = LoggerFactory.getLogger(SarlaccUserServiceImpl.class);

    private static final long CACHE_SIZE = 100;
    private static final long CACHE_TIME = 30;

    // Cache to hold users
    private LoadingCache<String,User> userCache = CacheBuilder.newBuilder()
        .maximumSize(CACHE_SIZE)
        .expireAfterAccess(CACHE_TIME, TimeUnit.MINUTES)
        .build(
            new CacheLoader<String, User>() {
                public User load(String accessToken) throws Exception {
                	return retrieveUserFromSarlacc(accessToken);
                }
            }
        );
    
    private SarlaccClient client;
    
    public SarlaccUserServiceImpl(String sarlaccUrl, String authClientId, String authClientPassword){
        this.client = new SarlaccClient(authClientId,authClientPassword,sarlaccUrl);
    }
    
    // Public Interface Methods ===================================================================
    
    @Override
	public Token authenticate(String username, String password) {

		if (StringUtils.isBlank(username))
			throw new IllegalArgumentException("No username provided!");

		if (StringUtils.isBlank(password))
			throw new IllegalArgumentException("No password provided!");
    	
		
    	if (log.isDebugEnabled())
    		log.debug(String.format("Attempting to authenticate user [%s]", username));

		
		Token token = authenticateWithSarlacc(username, password, "password");
		
        if (log.isDebugEnabled())
        	log.debug(String.format("Returning token [%s]", token.toString()));
		
		return token;
	}
    
    @Override
    public User getUser(String accessToken) {
    	
		if (StringUtils.isBlank(accessToken))
			throw new IllegalArgumentException("No access token provided!");
    	
    	if (log.isDebugEnabled())
    		log.debug(String.format("Attempting to find user with access token [%s]", accessToken));
        
    	User user = getUserFromCache(accessToken);
        
        if (log.isDebugEnabled())
        	log.debug(String.format("Returning user [%s]", user.toString()));
        
        return user;
    }

	@Override
	public List<AppRole> getUserAppRoles(String accessToken) {

		if (StringUtils.isBlank(accessToken))
			throw new IllegalArgumentException("No access token provided!");
		
		if (log.isDebugEnabled())
    		log.debug(String.format("Attempting to get user App Roles with access token [%s]", accessToken));

		User user = getUserFromCache(accessToken);	
		
		if (log.isDebugEnabled())
        	log.debug(String.format("Returning App Roles [%s]", StringUtils.join(user.getAppRoles(), ";")));
		
		return user.getAppRoles();
	}

	@Override
	public Role getUserRoleForApp(String accessToken, App app) {
		
		if (StringUtils.isBlank(accessToken))
			throw new IllegalArgumentException("No access token provided!");
		
		if (app == null)
			throw new IllegalArgumentException("No Application provided!");
		
		if (log.isDebugEnabled())
    		log.debug(String.format("Attempting to get user role for application [%s] with access token [%s]", app.getName(), accessToken));

		User user = getUserFromCache(accessToken);	
		
		for (AppRole appRole : user.getAppRoles()){
			if (StringUtils.equals(appRole.getAppName(),app.getName())){
				
				if (log.isDebugEnabled())
		        	log.debug(String.format("Returning role [%s] for application [%s] for user [%s]", appRole.getRole(), app.getName(), user.getUsername()));
				
				return appRole.getRole();
			}
				
		}
		
		if (log.isDebugEnabled())
			log.debug(String.format("Failed to find a role for user [%s] on application [%s]. Returning null.", user.getUsername(), app.getName()));
		
		return null;
	}

	@Override
	public boolean isUserAdminForApp(String accessToken, App app) {

		Role role = getUserRoleForApp(accessToken,app);
		
		if (role == Role.ADMIN){
			if (log.isDebugEnabled())
				log.debug(String.format("User is an ADMIN on application [%s]", app.getName()));
			
			return true;
			
		} else {
			
			if (log.isDebugEnabled())
				log.debug(String.format("User is NOT an ADMIN on application [%s]", app.getName()));
			
			return false;
		}
		
	}

    
    // Private helper methods ===========================================================
    
	private Token authenticateWithSarlacc(String username, String password, String grantType) {
				
		Token token = null;
        try {
            token = client.getUserToken(username, password, grantType);
        } catch (Exception e){	
        	handleSarlaccException(e);
        }        	

        if (token == null){
        	throw new SarlaccServerException("Got a null token when authenticating with the Sarlacc!");
        }
        
        return token;
	}
	
    private User retrieveUserFromSarlacc(String accessToken) {
    	
        Token token = new Token();
        token.setAccessToken(accessToken);

        User user = null;
        
        try {
            user = client.getUserDetails(token);
        } catch (Exception e){	
        	handleSarlaccException(e);
        }
        
        if (user == null){
        	throw new SarlaccServerException("Got a null user when getting user details from the Sarlacc!");
        }

        return user;

    }
    
    private void handleSarlaccException(Exception e) {
    	
    	if (e instanceof HttpClientErrorException){
    		
    		HttpClientErrorException hcee = (HttpClientErrorException) e;
    		
    		switch (hcee.getStatusCode()) {
			case UNAUTHORIZED:
				logAndThrow(new SarlaccUserException("Invalid x-access-token header provided in the request", hcee));
				
			case BAD_REQUEST:
				logAndThrow(new SarlaccUserException("Invalid x-access-token header provided in the request", hcee));
				
			default:
				logAndThrow(new SarlaccServerException("Unknown error from the Sarlacc Service", hcee));
			}
    		
    	} else {
    		logAndThrow(new SarlaccServerException("Unknown error from the Sarlacc Service", e));
    	}
    	
    }
    
    private void logAndThrow(RuntimeException e) {
		log.error(e.getMessage());
		throw e;
    }
    
    private User getUserFromCache(String accessToken) {
    	User user = null;
        try {
            user = userCache.get(accessToken);
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        }
        return user;
    }

 
}
