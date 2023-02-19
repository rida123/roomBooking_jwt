package com.virtualpairprogrammers.roombooking.config;

import com.virtualpairprogrammers.roombooking.filters.JWTAuthorizationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    public void ConfigureGlobal(AuthenticationManagerBuilder auth) throws  Exception {
        auth.inMemoryAuthentication().withUser("mat").password("{noop}secret").authorities("ROLE_ADMIN")
                .and().withUser("jane").password("{noop}12345").authorities("ROLE_USER");
    }


    @Override
    protected  void configure(HttpSecurity http) throws Exception {
        /* these rules work top to bottom
          we are saying that any request starting with /api/basicAuth/** must have role ADMIN
          and must be authenticated with basic authentication
         *///any preflight request (through Options http verb) is allowed to this pattern =>

        http.csrf().disable().authorizeRequests() //=>because options request will not pass the AUTHORIZATION HEADER
                // => WE CAN'T PERFORM B.A
                //we disabled cross-site_request forgery that our app is prone to be vulnerable at for post & put request
                .mvcMatchers(HttpMethod.OPTIONS, "/api/basicAuth/**").permitAll() //permit all preflight requests
                .mvcMatchers("/api/basicAuth/**").hasAnyRole("ADMIN", "USER")
                .and().httpBasic(); //we are specifying that we need authentication
        //that tells spring to use basic authentication. The built-in httpBasic() method tells spring to
        //to look for Authorization header, that header should contain value:
        //Basic base64(username:pwd). This method will decode the base64 encoded string and
        //checks if it maps to one of our users, if so => valid user =>
        //=> it creates an Authentication

        //now second set of requirements that has to do with the jwt part:
        http.csrf().disable()
                .authorizeRequests()
                //allow any request with options verb:
                .mvcMatchers(HttpMethod.OPTIONS, "/api/**").permitAll()
                .mvcMatchers(HttpMethod.GET, "/api/**").hasAnyRole("ADMIN", "USER")
                //when it reaches here => the request is othen than get: post, put, delete,...
                .mvcMatchers("/api/users/**").hasRole("ADMIN")
                .and().addFilter(new JWTAuthorizationFilter(authenticationManager()));
    }
}
