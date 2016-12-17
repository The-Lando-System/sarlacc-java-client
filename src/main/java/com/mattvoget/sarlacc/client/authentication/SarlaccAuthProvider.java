package com.mattvoget.sarlacc.client.authentication;

import com.mattvoget.sarlacc.client.SarlaccClient;
import com.mattvoget.sarlacc.models.Token;
import com.mattvoget.sarlacc.models.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class SarlaccAuthProvider implements AuthenticationProvider {

    private Logger log = LoggerFactory.getLogger(SarlaccAuthProvider.class);

    @Value("${auth.url.token}")
    private String authUrlToken;

    @Value("${auth.url.user}")
    private String authUrlUser;

    @Value("${auth.client.id}")
    private String authClientId;

    @Value("${auth.client.password}")
    private String authClientPassword;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        if (authentication != null && !(authentication.getPrincipal() instanceof User)) {
            SecurityContextHolder.getContext().setAuthentication(null);
            return getAuthFromExternal(authentication);
        } else {
            return SecurityContextHolder.getContext().getAuthentication();
        }
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return true;
    }

    private Authentication getAuthFromExternal(Authentication authentication){
        String name = authentication.getName();
        String password = authentication.getCredentials().toString();

        log.info(String.format("Calling Sarlacc to authenticate user with creds: username=%s, password=****",name));

        SarlaccClient client = new SarlaccClient(authClientId,authClientPassword,authUrlToken,authUrlUser);

        Token token = client.getUserToken(name,password,"password");
        User user = client.getUserDetails(token);

        log.info("Successfully retrieved user from the Sarlacc: " + user.getUsername());

        user.setToken(token);

        return new UsernamePasswordAuthenticationToken(user, user.getAuthorities());
    }
}
