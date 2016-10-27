package com.mattvoget.sarlacc.client;

import com.mattvoget.sarlacc.client.models.Token;
import com.mattvoget.sarlacc.client.models.User;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

public class SarlaccClient {

	public static void main(String[] args) {

        Token token = getUserToken("matt.voget","password");
        System.out.println(token.toString());

        User user = getUserDetails(token);
        System.out.println(user.toString());
    }

    public static Token getUserToken(String username, String password){
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization","Basic YWNtZTphY21lc2VjcmV0");
        headers.set("Content-Type","application/x-www-form-urlencoded");

        String requestBody = String.format("username=%s&password=%s&grant_type=%s",username,password,"password");

        HttpEntity entity = new HttpEntity(requestBody,headers);

        ResponseEntity<Token> re = restTemplate.exchange("http://localhost:8080/oauth/token", HttpMethod.POST, entity, Token.class);
        return re.getBody();
    }

    public static User getUserDetails(Token token){
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization",String.format("Bearer %s",token.getAccessToken()));

        HttpEntity<String> entity = new HttpEntity<String>("parameters", headers);

        ResponseEntity<User> re = restTemplate.exchange("http://localhost:8080/user-details", HttpMethod.GET, entity, User.class);
        return re.getBody();
    }

}
