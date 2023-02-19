package com.virtualpairprogrammers.roombooking.services;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.Base64;
import java.util.Date;

/**
 * this service to generate and validate token, and we'll use this service any where in the application
 *
 */
@Service
public class JWTService {

    //part of the process of generating token is encoding using RSA256 algorithm which need 2 keys: private & public
    //keys and we can use java security package:
    RSAPrivateKey privateKey;
    RSAPublicKey publicKey;
    //i would like these keys to be generated ONCE when the application starts
    //=> every time we restart the application, those keys will be regenerated,
    private long expireationTime = 1800000L;
    @PostConstruct
    public void initKeys() throws NoSuchAlgorithmException {
        //that will run once when the service is constructed for the first time:
        //we will generate the keys using KeyPairGenerator:
        KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");

        generator.initialize(2048);
        KeyPair keyPair = generator.generateKeyPair();

        privateKey = (RSAPrivateKey) keyPair.getPrivate();
        publicKey = (RSAPublicKey) keyPair.getPublic();

    }
    public String generateToken(String name, String role) {
        //what data we are going to embed into the token:
        return JWT.create()
                .withClaim("user", name)
                .withClaim("role", role)
                .withExpiresAt(new Date(System.currentTimeMillis() + expireationTime)).sign(Algorithm.RSA256(publicKey, privateKey));
    }


    /**
     * validate token then return its decoded payload
     * @param token
     * @return
     * @throws JWTVerificationException
     */
    public String validateToken(String token) throws JWTVerificationException {
        String encodePayload = JWT.require(Algorithm.RSA256(publicKey, privateKey))
                .build()
                .verify(token).getPayload();

        System.out.println("token payload value: " +encodePayload );
        return new String(Base64.getDecoder().decode(encodePayload));
    }
}
