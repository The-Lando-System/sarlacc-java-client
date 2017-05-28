package com.mattvoget.sarlacc.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import java.io.IOException;

@Order(Ordered.HIGHEST_PRECEDENCE)
public class SarlaccUserFilter implements Filter{

    private Logger log = LoggerFactory.getLogger(SarlaccUserFilter.class);

    private SarlaccUserServiceImpl sarlaccUserService;

    public SarlaccUserFilter(SarlaccUserServiceImpl sarlaccUserService) {
        this.sarlaccUserService = sarlaccUserService;
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        log.debug("Entering the Sarlacc User Filter!");

        HttpServletResponseWrapper wrapper = new HttpServletResponseWrapper((HttpServletResponse) servletResponse);

        // Check for the x-access-token header
        String accessToken = ((HttpServletRequest) servletRequest).getHeader(SarlaccUserServiceImpl.TOKEN_NAME);
        if (accessToken == null) {
            wrapper.sendError(HttpStatus.BAD_REQUEST.value(),"No x-access-token header provided in the request");
            return;
        }

        // Check if the x-access-token is valid. Will load the user in the cache if successful
        try {
            sarlaccUserService.getUser(accessToken);
        } catch (Exception e) {
            HttpStatus errorStatus = HttpStatus.BAD_REQUEST;
            String message = e.getMessage();
            if (e.getCause() instanceof SarlaccServerException){
                errorStatus = HttpStatus.INTERNAL_SERVER_ERROR;
                message = e.getCause().getMessage();
                log.error(e.getCause().getMessage(),e.getCause());
            }
            wrapper.sendError(errorStatus.value(),message);
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
