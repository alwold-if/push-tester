package com.ifactorinc.pushtester;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ifactorinc.pushtester.config.Config;
import com.notnoop.apns.APNS;
import com.notnoop.apns.ApnsService;
import com.notnoop.apns.ApnsServiceBuilder;
import com.notnoop.apns.EnhancedApnsNotification;

import java.io.File;
import java.util.Date;

/**
 * Created by albertwold on 2/26/16.
 */
public class PushTester {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static void main(String[] args) throws Exception {
        if (args.length < 1) {
            System.err.println("Usage: push-tester <config.json>");
            System.exit(1);
        } else {
            Config config = objectMapper.readValue(new File(args[0]), Config.class);
            ApnsServiceBuilder builder = APNS
                    .newService()
                    .withCert(config.getCertificate().getFile(), config.getCertificate().getPassword());
            if (config.getEnvironment().equals(Config.ENVIRONMENT_PRODUCTION)) {
                builder = builder.withProductionDestination();
            } else if (config.getEnvironment().equals(Config.ENVIRONMENT_SANDBOX)) {
                builder = builder.withSandboxDestination();
            } else {
                throw new Exception("Unknown environment in config: "+config.getEnvironment());
            }
            ApnsService apnsService = builder.build();
            int now =  (int)(new Date().getTime()/1000);
            String jsonPayload = objectMapper.writeValueAsString(config.getPayload());
            for (String deviceToken : config.getDeviceTokens()) {
                EnhancedApnsNotification notification = new EnhancedApnsNotification(EnhancedApnsNotification.INCREMENT_ID(), now + 60 * 60, deviceToken, jsonPayload);
                apnsService.push(notification);
            }
        }
    }
}
