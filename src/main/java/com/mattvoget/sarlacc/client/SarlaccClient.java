package com.mattvoget.sarlacc.client;

import com.mattvoget.sarlacc.client.exceptions.SarlaccClientException;
import com.mattvoget.sarlacc.client.models.Token;
import com.mattvoget.sarlacc.client.models.User;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

public class SarlaccClient {

    private String clientId;
    private String clientSecret;
    private String encodedClientInfo;
    private String tokenUrl;
    private String userUrl;

    public SarlaccClient(String clientId, String clientSecret, String tokenUrl, String userUrl) {

        validateClientInput(clientId,"clientId");
        validateClientInput(clientSecret,"clientSecret");
        validateClientInput(tokenUrl,"tokenUrl");
        validateClientInput(userUrl,"userUrl");

        this.clientId = clientId;
        this.clientSecret = clientSecret;
        this.encodedClientInfo = base64Encode(String.format("%s:%s",clientId,clientSecret));
        this.tokenUrl = tokenUrl;
        this.userUrl = userUrl;
    }

    void validateClientInput(String str, String varName){
        String msg = "Failed to initialize a Sarlacc Client! Reason: %s";
        if (StringUtils.isBlank(str)){
            throw new SarlaccClientException(String.format(msg,String.format("Received no input for the following variable: %s", varName)));
        }
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getClientSecret() {
        return clientSecret;
    }

    public void setClientSecret(String clientSecret) {
        this.clientSecret = clientSecret;
    }

    public String getTokenUrl() {
        return tokenUrl;
    }

    public void setTokenUrl(String tokenUrl) {
        this.tokenUrl = tokenUrl;
    }

    public String getUserUrl() {
        return userUrl;
    }

    public void setUserUrl(String userUrl) {
        this.userUrl = userUrl;
    }

    public String getEncodedClientInfo() {
        return encodedClientInfo;
    }

    public void setEncodedClientInfo(String encodedClientInfo) {
        this.encodedClientInfo = encodedClientInfo;
    }

    public Token getUserToken(String username, String password, String grantType){
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();

        setAuthHeader(headers, "Basic", getEncodedClientInfo());
        setContentType(headers, MediaType.APPLICATION_FORM_URLENCODED_VALUE);

        String requestBody = String.format("username=%s&password=%s&grant_type=%s",username,password,grantType);

        HttpEntity<String> entity = new HttpEntity(requestBody,headers);

        return (Token) sendRequest(restTemplate,getTokenUrl(),HttpMethod.POST,entity,Token.class);
    }

    public User getUserDetails(Token token){
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();

        setAuthHeader(headers, "Bearer", token.getAccessToken());

        HttpEntity<String> entity = new HttpEntity("parameters", headers);

        return (User) sendRequest(restTemplate,getUserUrl(),HttpMethod.GET, entity, User.class);
    }

    String base64Encode(String strToEncode){
        return new String(Base64.encodeBase64(strToEncode.getBytes()));
    }

    void setAuthHeader(HttpHeaders headers, String authType, String token){
        headers.set("Authorization", String.format("%s %s", authType, token));
    }

    void setContentType(HttpHeaders headers, String contentType){
        headers.set("Content-Type", contentType);
    }

    Object sendRequest(RestTemplate restTemplate, String url, HttpMethod methodType, HttpEntity entity, Class clazz){
        ResponseEntity<?> re = restTemplate.exchange(url, methodType, entity, clazz);
        return re.getBody();
    }

}
