package com.mattvoget.sarlacc.client;

import com.mattvoget.sarlacc.client.models.Token;
import com.mattvoget.sarlacc.client.models.User;
import org.apache.commons.codec.binary.Base64;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

public class SarlaccClient {

    private static final String CLIENT_ID = "acme";
    private static final String CLIENT_SECRET = "acmesecret";

    private static final String TOKEN_URL = "http://localhost:8080/oauth/token";
    private static final String USER_URL = "http://localhost:8080/user-details";

	public static void main(String[] args) {

        Token token = getUserToken("matt.voget","password");
        System.out.println(token.toString());

        User user = getUserDetails(token);
        System.out.println(user.toString());

        Token token2 = getUserToken("test","test");
        System.out.println(token2.toString());

        User user2 = getUserDetails(token2);
        System.out.println(user2.toString());
    }

    public static Token getUserToken(String username, String password){
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        String clientInfo = new String(Base64.encodeBase64(String.format("%s:%s",CLIENT_ID,CLIENT_SECRET).getBytes()));

        headers.set("Authorization",String.format("Basic %s", clientInfo));
        headers.set("Content-Type","application/x-www-form-urlencoded");

        String requestBody = String.format("username=%s&password=%s&grant_type=%s",username,password,"password");

        HttpEntity entity = new HttpEntity(requestBody,headers);

        ResponseEntity<Token> re = restTemplate.exchange(TOKEN_URL, HttpMethod.POST, entity, Token.class);
        return re.getBody();
    }

    public static User getUserDetails(Token token){
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization",String.format("Bearer %s",token.getAccessToken()));

        HttpEntity<String> entity = new HttpEntity<String>("parameters", headers);

        ResponseEntity<User> re = restTemplate.exchange(USER_URL, HttpMethod.GET, entity, User.class);
        return re.getBody();
    }

}
