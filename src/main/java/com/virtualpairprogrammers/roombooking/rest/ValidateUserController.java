package com.virtualpairprogrammers.roombooking.rest;

import com.virtualpairprogrammers.roombooking.services.JWTService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("api/basicAuth")
public class ValidateUserController { //the idea is that all methods in this controller are going to be secured by basic auth

    @Autowired
    JWTService jwtService;
    //we will configure security such that any api that starts with: api/basicAuth must be secured by basic authentication

    /**
     * what is nice is that we will not make any checking if the Authorization header
     * contains the correct details, (this will be done in security configuration) => if request is here => valid authentication
     * @return
     */
    @RequestMapping("/validate")
    public Map<String, String> userIsValid() { //instead of returning result string, we'll return a token
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) authentication.getPrincipal();
        String name = user.getUsername();
        String role = user.getAuthorities().toArray()[0].toString().substring(5);

        String token = this.jwtService.generateToken(name, role);

        Map<String, String> results = new HashMap<>();
        results.put("results", token);

      /*  Cookie cookie = new Cookie("token", token);
        cookie.setPath("/api");
        resp.addCookie(cookie);*/

        return  results;
//        return "{\"result\": \"ok\"}";
    }

    @GetMapping("/test")
    public String testing() {
        return "we passed security";
    }



}
