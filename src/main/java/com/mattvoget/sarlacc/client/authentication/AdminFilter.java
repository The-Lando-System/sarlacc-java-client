package com.mattvoget.sarlacc.client.authentication;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mattvoget.sarlacc.models.Role;
import com.mattvoget.sarlacc.models.User;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

public class AdminFilter implements Filter{
    private Logger log = LoggerFactory.getLogger(AdminFilter.class);

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) servletRequest;
        Enumeration<String> headerNames = httpRequest.getHeaderNames();
        Map<String,String> headers = new HashMap<>();

        if (headerNames != null) {
            while (headerNames.hasMoreElements()) {
                String key = headerNames.nextElement();
                String value = httpRequest.getHeader(key);
                headers.put(key,value);
            }
        }

        try {
            User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            if ((user.getRole() != Role.ADMIN) || (!StringUtils.equals(user.getToken().getAccessToken(),headers.get("x-access-token")))){
                log.warn(String.format("User %s does not have permissions to access resource", user.getUsername()));

                AuthException ae = new AuthException("You do not have permissions to access this resource", IllegalAccessError.class.getSimpleName(), HttpStatus.UNAUTHORIZED);
                ObjectMapper mapper = new ObjectMapper();

                servletResponse.setContentType("application/json");
                servletResponse.setCharacterEncoding("UTF-8");

                String jsonResponse = mapper.writeValueAsString(ae);
                servletResponse.getOutputStream().print(jsonResponse);

            } else {
                filterChain.doFilter(servletRequest,servletResponse);
            }
        } catch (Exception e) {
            log.error("Failed to retrieve a user from the security context!");
            throw new RuntimeException(e);
        }

    }

    @Override
    public void destroy() {

    }
}
