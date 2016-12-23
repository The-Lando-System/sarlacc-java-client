package com.mattvoget.sarlacc.client.authentication;

import com.mattvoget.sarlacc.models.Token;
import com.mattvoget.sarlacc.models.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping(value="auth")
public class AuthenticationController {
    private Logger log = LoggerFactory.getLogger(AuthenticationController.class);

    @Autowired
    SecurityHelper securityHelper;

    @RequestMapping(value="/login", method= RequestMethod.POST)
    @ResponseBody
    public Token login() {
        User user = securityHelper.getUser();
        Token token = user.getToken();

        log.info(String.format("User %s has logged in!",user.getUsername()));
        log.info(String.format("Returning access token: %s",token.getAccessToken()));

        return token;
    }

    @RequestMapping(value="/logout", method= RequestMethod.POST)
    @ResponseBody
    public String logout() {
        log.info(String.format("User %s is logging out!",securityHelper.getUser().getUsername()));

        securityHelper.logout();
        return "The user has been logged out";
    }
}
