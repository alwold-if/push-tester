package com.ifactorinc.pushtester;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import okhttp3.*;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.ECPrivateKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Arrays;
import java.util.Date;

/**
 * Created by albertwold on 10/4/16.
 */
public class TokenPushTester {
    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    public static final String SANDBOX = "https://api.development.push.apple.com:443";
    public static final String PRODUCTION = "https://api.push.apple.com:443";

    public static void main(String[] args) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
        if (args.length < 4) {
            System.out.println("Usage: token-push-tester <key file> <team id> <device token> <bundle id>");
        } else {
            String keyFile = args[0];
            String teamId = args[1];
            String deviceToken = args[2];
            String bundleId = args[3];

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
            send(deviceToken, compactJws, bundleId);
        }
    }

    private static void send(String deviceToken, String jwt, String bundleId) throws IOException {
        OkHttpClient client = new OkHttpClient.Builder()
                .protocols(Arrays.asList(Protocol.HTTP_2, Protocol.HTTP_1_1))
                .build();

        RequestBody body = RequestBody.create(JSON, "{ \"aps\": { \"alert\": \"Hello world\"} }");
        Request request = new Request.Builder()
                .url(SANDBOX+"/3/device/"+deviceToken)
                .post(body)
                .header("authorization", "Bearer "+jwt)
                .header("apns-topic", bundleId)
                .build();
        Response response = client.newCall(request).execute();
        System.out.println(response.body().string());
    }
}
