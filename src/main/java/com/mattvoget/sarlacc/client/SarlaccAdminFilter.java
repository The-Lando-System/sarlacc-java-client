package com.mattvoget.sarlacc.client;

import com.mattvoget.sarlacc.models.Role;
import com.mattvoget.sarlacc.models.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import java.io.IOException;

public class SarlaccAdminFilter implements Filter {

    private Logger log = LoggerFactory.getLogger(SarlaccAdminFilter.class);

    private SarlaccUserService sarlaccUserService;

    public SarlaccAdminFilter(SarlaccUserService sarlaccUserService){
        this.sarlaccUserService = sarlaccUserService;
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        log.debug("Entering the Sarlacc Admin Filter!");

        HttpServletResponseWrapper wrapper = new HttpServletResponseWrapper((HttpServletResponse) servletResponse);

        User user = null;
        try {
            String accessToken = ((HttpServletRequest) servletRequest).getHeader(SarlaccUserService.TOKEN_NAME);
            user = sarlaccUserService.getUser(accessToken);
        } catch (Exception e) {
            wrapper.sendError(HttpStatus.BAD_REQUEST.value(),"Invalid or no x-access-token header provided");
            return;
        }

        if (user.getRole() != Role.ADMIN){
            wrapper.sendError(HttpStatus.BAD_REQUEST.value(),"User does not have permission to access this resource");
            return;
        }

        filterChain.doFilter(servletRequest,servletResponse);
    }


    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }



    @Override
    public void destroy() {

    }
}
