package com.ifactorinc.pushtester;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ifactorinc.pushtester.config.Config;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import okhttp3.*;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.security.KeyFactory;
import java.security.interfaces.ECPrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * Created by albertwold on 10/4/16.
 */
public class TokenPushTester {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    public static final String SANDBOX = "https://api.development.push.apple.com:443";
    public static final String PRODUCTION = "https://api.push.apple.com:443";

    public static void main(String[] args) throws Exception {
        if (args.length < 1) {
            System.out.println("Usage: token-push-tester <config.json>");
        } else {
            Config config = objectMapper.readValue(new File(args[0]), Config.class);

            byte[] pkcs8 = FileUtils.readFileToByteArray(new File(config.getKey().getFile()));
            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(pkcs8);
            KeyFactory keyFactory = KeyFactory.getInstance("EC");
            ECPrivateKey key = (ECPrivateKey) keyFactory.generatePrivate(keySpec);
            String compactJws = Jwts.builder()
                    .setIssuer(config.getTeamId())
                    .setIssuedAt(new Date())
                    .signWith(SignatureAlgorithm.ES256, key)
                    .setHeaderParam("kid", config.getKey().getKeyId())
                    .compact();
            System.out.println(compactJws);
            String payloadJson = objectMapper.writeValueAsString(config.getPayload());
            String server;
            if (config.getEnvironment().equals(Config.ENVIRONMENT_SANDBOX)) {
                server = SANDBOX;
            } else if (config.getEnvironment().equals(Config.ENVIRONMENT_PRODUCTION)) {
                server = PRODUCTION;
            } else {
                throw new Exception("Unknown environment: "+config.getEnvironment());
            }
            send(config.getDeviceTokens(), payloadJson, server, compactJws, config.getBundleId());
        }
    }

    private static void send(List<String> deviceTokens, String payload, String server, String jwt, String bundleId) throws IOException {
        OkHttpClient client = new OkHttpClient.Builder()
                .protocols(Arrays.asList(Protocol.HTTP_2, Protocol.HTTP_1_1))
                .build();

        RequestBody body = RequestBody.create(JSON, payload);
        for (String deviceToken : deviceTokens) {
            Request request = new Request.Builder()
                    .url(server + "/3/device/" + deviceToken)
                    .post(body)
                    .header("authorization", "Bearer " + jwt)
                    .header("apns-topic", bundleId)
                    .build();
            System.out.println("request: " + request);
            Response response = client.newCall(request).execute();
            System.out.println("response: " + response.body().string());
        }
        // TODO close connection?
    }
}
