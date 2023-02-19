package com.virtualpairprogrammers.roombooking.filters;

import com.virtualpairprogrammers.roombooking.services.JWTService;
import org.springframework.boot.json.JsonParser;
import org.springframework.boot.json.JsonParserFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.FilterChain;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class JWTAuthorizationFilter extends BasicAuthenticationFilter {

    public JWTAuthorizationFilter(AuthenticationManager am) {
        super(am);
    }


    /**
     *because this is a filter, => we can't use DI to get reference to our service,
     * instead we need to get a reference from spring
     */
    JWTService jwtService;

    @Override
    public void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
        //if no bearer in authorization header => continue filter chain
        //this filter is only interested in requests with Bearer in the Authorization header
        String header = request.getHeader("Authorization");
        if (header == null || !header.startsWith("Bearer")) {
            System.out.println("--------CONTINUE REQUEST------------------------");
            chain.doFilter(request, response);
            return;
        }

        //because we are in a filter we can't perform autowire services to be checked
        //preparation of jwtService
        if (jwtService == null) {
            ServletContext servletContext = request.getServletContext();
            WebApplicationContext wac  = WebApplicationContextUtils.getWebApplicationContext(servletContext);
            jwtService = wac.getBean(JWTService.class);
        }
        //core work of the filter:
        UsernamePasswordAuthenticationToken authentication = getAuthenticationObject(header);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        chain.doFilter(request, response);
    }

    //store information about our user:
    //once we have got it, put our token in the SecurityContextHolder and that's how we tell spring, having
    //gone through this process, this filter has set the UserDetails from analysing the token!
    private UsernamePasswordAuthenticationToken getAuthenticationObject(String header) {
        //the token is inside header: Authorization: Bearer token_value {starting from Bearer}
        String jwtToken = header.substring(7);
        try {
            String payload = jwtService.validateToken(jwtToken);
            JsonParser parser = JsonParserFactory.getJsonParser();
            Map<String, Object> payloadMap = parser.parseMap(payload);
            String user = payloadMap.get("user").toString();
            String role = payloadMap.get("role").toString();

            List<GrantedAuthority> roles = new ArrayList<>();
            GrantedAuthority ga = new GrantedAuthority() {

                @Override
                public String getAuthority() {
                    return "ROLE_" + role;
                }

            };

            roles.add(ga);

            return new UsernamePasswordAuthenticationToken(user, null, roles);
        }
        catch (Exception e) {
            //token is not valid
            return null;
        }
    }

}




