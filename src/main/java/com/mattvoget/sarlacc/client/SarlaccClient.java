package com.mattvoget.sarlacc.client;


import java.util.Arrays;
import java.util.List;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import com.mattvoget.sarlacc.models.AppRole;
import com.mattvoget.sarlacc.models.Token;
import com.mattvoget.sarlacc.models.User;

public class SarlaccClient {

    private Logger log = LoggerFactory.getLogger(SarlaccClient.class);

    private static final String TOKEN_ENDPOINT = "/oauth/token";
    private static final String USER_ENDPOINT = "/user-details";
    private static final String APPROLE_ENDPOINT = "/app-role/";
    
    private String encodedClientInfo;
    private String sarlaccUrl;
    
    public SarlaccClient(String clientId, String clientSecret, String sarlaccUrl) {
    	
    	if (StringUtils.isBlank(clientId))
    		throw new IllegalArgumentException("No client ID provided!");
    	
    	if (StringUtils.isBlank(clientSecret))
    		throw new IllegalArgumentException("No client secret provided!");

    	if (StringUtils.isBlank(sarlaccUrl))
    		throw new IllegalArgumentException("Sarlacc URL is not provided!");
    	
        this.encodedClientInfo = base64Encode(String.format("%s:%s",clientId,clientSecret));
        this.sarlaccUrl = sarlaccUrl;
    }

    public String getEncodedClientInfo() {
        return encodedClientInfo;
    }

    public void setEncodedClientInfo(String encodedClientInfo) {
        this.encodedClientInfo = encodedClientInfo;
    }

    public Token getUserToken(String username, String password, String grantType){
    	
    	String tokenUrl = this.sarlaccUrl + TOKEN_ENDPOINT;
    	
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();

        setAuthHeader(headers, "Basic", getEncodedClientInfo());
        setContentType(headers, MediaType.APPLICATION_FORM_URLENCODED_VALUE);

        String requestBody = String.format("username=%s&password=%s&grant_type=%s",username,password,grantType);

        HttpEntity<String> entity = new HttpEntity<String>(requestBody,headers);

        return (Token) sendRequest("Get Token", restTemplate, tokenUrl, HttpMethod.POST, entity, Token.class);
    }

    public User getUserDetails(Token token){
    	
    	String userUrl = this.sarlaccUrl + USER_ENDPOINT;
    	
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();

        setAuthHeader(headers, "Bearer", token.getAccessToken());

        HttpEntity<String> entity = new HttpEntity<String>("parameters", headers);

        User user = (User) sendRequest("Get User Details", restTemplate, userUrl,HttpMethod.GET, entity, User.class);
        user.setToken(token);
        
        List<AppRole> appRoles = getUserAppRoles(user);
        user.setAppRoles(appRoles);
        
        return user;
    }
    
	public List<AppRole> getUserAppRoles(User user){
    	
    	String appRoleUrl = this.sarlaccUrl + APPROLE_ENDPOINT + user.getUsername() + "/";
    	
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();

        setAuthHeader(headers, "Bearer", user.getToken().getAccessToken());

        HttpEntity<String> entity = new HttpEntity<String>("parameters", headers);
    	
        AppRole[] appRoles = (AppRole[]) sendRequest("Get App Roles", restTemplate, appRoleUrl,HttpMethod.GET, entity, AppRole[].class);
        
        return Arrays.asList(appRoles);
    }

    private String base64Encode(String strToEncode){
        return new String(Base64.encodeBase64(strToEncode.getBytes()));
    }

    private void setAuthHeader(HttpHeaders headers, String authType, String token){
        headers.set("Authorization", String.format("%s %s", authType, token));
    }

    private void setContentType(HttpHeaders headers, String contentType){
        headers.set("Content-Type", contentType);
    }

    private Object sendRequest(String action, RestTemplate restTemplate, String url, HttpMethod methodType, HttpEntity<String> entity, Class<?> clazz){
    	
    	if (log.isDebugEnabled())
    		log.debug(String.format("Sending request to Sarlacc: action=[%s] url=[%s] method=[%s] return=[%s]", action, url, methodType.toString(), clazz.getSimpleName()));
    	    	
        ResponseEntity<?> re = restTemplate.exchange(url, methodType, entity, clazz);
        return re.getBody();
    }
    
}
