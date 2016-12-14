package com.mattvoget.sarlacc.client.authentication;

import com.mattvoget.sarlacc.models.Role;
import com.mattvoget.sarlacc.models.User;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class SecurityHelper {
    private Logger log = LoggerFactory.getLogger(SecurityHelper.class);

    public void checkAccess(String accessToken){
        checkAccessToken(accessToken);
        checkUser();
    }

    public void checkAdmin(String accessToken) {
        checkAccessToken(accessToken);
        checkUser();
        if (getUser().getRole() != Role.ADMIN){
            log.warn(String.format("Rejected ADMIN access for user: %s",getUser().getUsername()));
            throw new IllegalAccessError("You do not have the correct role to perform this function!");
        }
    }

    public User getUser(){
        checkUser();
        return (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

    public void logout(){
        SecurityContextHolder.getContext().setAuthentication(null);
    }

    private void checkUser(){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null){
            String message = "Could not get user from the security context!";
            log.error(message);
            throw new IllegalStateException(message);
        }
    }

    private void checkAccessToken(String accessToken){
        if (!StringUtils.equals(getUser().getToken().getAccessToken(),accessToken)){
            log.warn(String.format("User %s provided a bad access token: %s",getUser().getUsername(),accessToken));
            throw new IllegalAccessError("You provided a bad access token!");
        }
    }
}
