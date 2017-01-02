package com.mattvoget.sarlacc.client;

import com.mattvoget.sarlacc.models.Token;
import com.mattvoget.sarlacc.models.User;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.web.client.RestTemplate;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

public class SarlaccClientTest {

    private static final String CLIENT_ID = "testClientId";
    private static final String CLIENT_SECRET = "testClientSecret";
    private static final String ENCODED_CLIENT_INFO = "dGVzdENsaWVudElkOnRlc3RDbGllbnRTZWNyZXQ=";
    private static final String TOKEN_URL = "http://someaddr:8080/token/url";
    private static final String USER_URL = "http://someaddr:8080/user/url";

    @Test
    public void createGoodClient(){
        SarlaccClient client = new SarlaccClient(CLIENT_ID,CLIENT_SECRET,TOKEN_URL,USER_URL);

        assertEquals(CLIENT_ID,client.getClientId());
        assertEquals(CLIENT_SECRET,client.getClientSecret());
        assertEquals(TOKEN_URL,client.getTokenUrl());
        assertEquals(USER_URL,client.getUserUrl());
    }

    @Test (expected = IllegalArgumentException.class)
    public void createBadClientNoClientId(){
        SarlaccClient client = new SarlaccClient(null,CLIENT_SECRET,TOKEN_URL,USER_URL);
    }

    @Test (expected = IllegalArgumentException.class)
    public void createBadClientNoClientSecret(){
        SarlaccClient client = new SarlaccClient(CLIENT_ID,null,TOKEN_URL,USER_URL);
    }

    @Test (expected = IllegalArgumentException.class)
    public void createBadClientNoTokenUrl(){
        SarlaccClient client = new SarlaccClient(CLIENT_ID,CLIENT_SECRET,null,USER_URL);
    }

    @Test (expected = IllegalArgumentException.class)
    public void createBadClientNoUserUrl(){
        SarlaccClient client = new SarlaccClient(CLIENT_ID,CLIENT_SECRET,TOKEN_URL,null);
    }

    @Test
    public void testEncodedClientInfo(){
        SarlaccClient client = new SarlaccClient(CLIENT_ID,CLIENT_SECRET,TOKEN_URL,USER_URL);

        assertEquals(ENCODED_CLIENT_INFO,client.getEncodedClientInfo());
    }

    @Test
    public void testGetUserToken(){
        SarlaccClient client = spy(new SarlaccClient(CLIENT_ID,CLIENT_SECRET,TOKEN_URL,USER_URL));

        doReturn(new Token()).when(client).sendRequest(
                any(RestTemplate.class),anyString(),any(HttpMethod.class),any(HttpEntity.class),any(Class.class)
        );

        client.getUserToken("testusername","testpassword","password");

        verify(client).sendRequest(any(RestTemplate.class),eq(TOKEN_URL), eq(HttpMethod.POST), any(HttpEntity.class), eq(Token.class));
    }

    @Test
    public void testGetUserDetails(){
        SarlaccClient client = spy(new SarlaccClient(CLIENT_ID,CLIENT_SECRET,TOKEN_URL,USER_URL));

        Mockito.doReturn(new User()).when(client).sendRequest(
                any(RestTemplate.class),anyString(),any(HttpMethod.class),any(HttpEntity.class),any(Class.class)
        );

        client.getUserDetails(new Token());

        verify(client).sendRequest(any(RestTemplate.class),eq(USER_URL), eq(HttpMethod.GET), any(HttpEntity.class), eq(User.class));
    }
}
