package com.mattvoget.sarlacc.client.authentication;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping(value="test")
public class TestController extends ErrorHandlingController {

    @Autowired
    SecurityHelper securityHelper;

    @RequestMapping(value="/user", method= RequestMethod.GET)
    @ResponseBody
    public String userTest(@RequestHeader(value="x-access-token") String accessToken) {
        securityHelper.checkAccess(accessToken);
        return "You have normal user access!";
    }

    @RequestMapping(value="/admin", method= RequestMethod.GET)
    @ResponseBody
    public String adminTest(@RequestHeader(value="x-access-token") String accessToken) {
        securityHelper.checkAdmin(accessToken);
        return "You are an administrator!";
    }
}
