package com.mattvoget.sarlacc.client;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.mattvoget.sarlacc.models.Token;
import com.mattvoget.sarlacc.models.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.client.HttpClientErrorException;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;


public class SarlaccUserService {

    private Logger log = LoggerFactory.getLogger(SarlaccUserService.class);

    public static final String TOKEN_NAME = "x-access-token";

    private String authUrlToken;
    private String authUrlUser;
    private String authClientId;
    private String authClientPassword;

    public SarlaccUserService(String authUrlToken, String authUrlUser, String authClientId, String authClientPassword){
        this.authUrlToken = authUrlToken;
        this.authUrlUser = authUrlUser;
        this.authClientId = authClientId;
        this.authClientPassword = authClientPassword;
    }

    private LoadingCache<String,User> userCache = CacheBuilder.newBuilder()
            .maximumSize(50)
            .expireAfterAccess(30, TimeUnit.MINUTES)
            .build(
                new CacheLoader<String, User>() {
                    public User load(String accessToken) throws Exception {

                        log.debug("Calling the Sarlacc to find user with access token: " + accessToken);

                        Token token = new Token();
                        token.setAccessToken(accessToken);

                        SarlaccClient client = new SarlaccClient(authClientId,authClientPassword,authUrlToken,authUrlUser);
                        User user = null;
                        try {
                            user = client.getUserDetails(token);
                        } catch (HttpClientErrorException hcee){
                            String message = "Unable to connect to the Sarlacc to get user information";
                            log.debug(message);
                            throw new SarlaccServerException(message, hcee);
                        } catch (Exception e){
                            String message = "Invalid x-access-token header provided in the request";
                            log.debug(message);
                            throw new SarlaccUserException(message);
                        }

                        log.debug(String.format("Found user %s from Sarlacc! Adding to cache.",user.getUsername()));

                        return user;
                    }
                }
            );

    public User getUser(String accessToken) {
        log.debug("Attempting to find user with access token: " + accessToken);
        User user = null;
        try {
            user = userCache.get(accessToken);
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        }
        log.debug("Returning user with username: " + user.getUsername());
        return user;
    }

    public long getUserCacheSize(){
        return userCache.size();
    }

}
