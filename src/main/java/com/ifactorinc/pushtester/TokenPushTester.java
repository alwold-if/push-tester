package com.ifactorinc.pushtester;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.ECPrivateKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Date;

/**
 * Created by albertwold on 10/4/16.
 */
public class TokenPushTester {
    public static void main(String[] args) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
        if (args.length < 3) {
            System.out.println("Usage: token-push-tester <key file> <team id> <device token>");
        } else {
            String keyFile = args[0];
            String teamId = args[1];
            String deviceToken = args[2];

            byte[] pkcs8 = FileUtils.readFileToByteArray(new File(keyFile));
            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(pkcs8);
            KeyFactory keyFactory = KeyFactory.getInstance("EC");
            ECPrivateKey key = (ECPrivateKey) keyFactory.generatePrivate(keySpec);
            String compactJws = Jwts.builder()
                    .setIssuer(teamId)
                    .setIssuedAt(new Date())
                    .signWith(SignatureAlgorithm.ES256, key)
                    .compact();
            System.out.println(compactJws);
        }
    }
}
