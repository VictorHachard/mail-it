package com.mailit;


import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;


@Component
public class CORSFilter implements Filter {

    private final Logger log = LoggerFactory.getLogger(CORSFilter.class);

    public CORSFilter() {
        log.info("CORSFilter init");
    }

    /**
     * Handles an incoming HTTP request by setting response headers for Cross-Origin Resource Sharing (CORS).
     *
     * The doFilter method first checks the run mode of the application (production or development)
     * and the request method (GET, POST, or OPTIONS) to determine the appropriate response headers to set.
     * If the run mode is production, it checks the origin of the request against a whitelist of allowed domains,
     * and sets the Access-Control-Allow-Origin header accordingly.
     * If the run mode is development, it sets the Access-Control-Allow-Origin header to * to allow all origins.
     * The method then sets several other response headers that are related to Cross-Origin Resource Sharing (CORS),
     * such as Access-Control-Allow-Methods and Access-Control-Max-Age. Finally, it passes the request and response
     * to the next filter in the chain using the chain.doFilter method.
     *
     * @param req The incoming request object.
     * @param res The outgoing response object.
     * @param chain The filter chain object, used to pass the request and response to the next filter in the chain.
     *
     * @throws IOException If an error occurs while reading or writing the request or response.
     * @throws ServletException If an error occurs while processing the request or response.
     */
    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {

        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;

        log.info("Request received from " + request.getRemoteAddr() + " for URI: " + request.getRequestURI());

        if (MailItApplication.runEnum.equals(RunEnum.PRODUCTION)) {
            if (request.getMethod().equals("GET")) {
                response.setHeader("Access-Control-Allow-Origin", request.getHeader("Origin"));
                log.info("Access-Control-Allow-Origin set for GET request: " + request.getHeader("Origin"));
            } else if (request.getMethod().equals("POST") && request.getRequestURI().equals("/logs")) {
                response.setHeader("Access-Control-Allow-Origin", request.getHeader("Origin"));
                log.info("Access-Control-Allow-Origin set for POST request: " + request.getHeader("Origin"));
            } else if (request.getMethod().equals("POST") || request.getMethod().equals("OPTIONS")) {
                String origin = request.getHeader("Origin");
                if (origin == null) {
                    response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                    log.error("Origin header not found in request");
                    return;
                } else if (MailItApplication.environment.ACCESS_CONTROL_ALLOW_ORIGIN_URL.contains(origin)) {
                    response.setHeader("Access-Control-Allow-Origin", origin);
                    log.info("Access-Control-Allow-Origin set for " + request.getMethod() + " request: " + origin);
                } else {
                    // Not used? -> a CORS policy error is throw
                    response.sendError(HttpServletResponse.SC_FORBIDDEN);
                    log.error("Access denied for " + request.getMethod() + " request from origin: " + origin);
                    return;
                }
            }
        } else {
            response.setHeader("Access-Control-Allow-Origin", "*");
            log.info("Access-Control-Allow-Origin set to * (wildcard)");
        }

        response.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS");
        response.setHeader("Access-Control-Max-Age", "3600");
        response.setHeader("Access-Control-Allow-Headers", "Content-Type, Accept, X-Requested-With");

        chain.doFilter(req, res);
    }

    @Override
    public void init(FilterConfig filterConfig) {
    }

    @Override
    public void destroy() {
    }

}
