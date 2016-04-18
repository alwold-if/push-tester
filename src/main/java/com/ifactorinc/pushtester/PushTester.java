package com.ifactorinc.pushtester;

import com.notnoop.apns.APNS;
import com.notnoop.apns.ApnsService;
import com.notnoop.apns.EnhancedApnsNotification;

import java.util.Date;

/**
 * Created by albertwold on 2/26/16.
 */
public class PushTester {
    public static void main(String[] args) {
        if (args.length < 3) {
            System.err.println("Usage: push-tester <certfile.p12> <cert password> <device token>");
            System.exit(1);
        } else {
            String certFile = args[0];
            String password = args[1];
            String deviceToken = args[2];
            ApnsService apnsService = APNS
                    .newService()
                    .withCert(certFile, password)
                    .withProductionDestination()
                    .build();
            int now =  (int)(new Date().getTime()/1000);
            String payload = APNS.newPayload()
                    .alertBody("Test notification")
                    .build();
            EnhancedApnsNotification notification = new EnhancedApnsNotification(EnhancedApnsNotification.INCREMENT_ID(), now + 60*60, deviceToken, payload);
            apnsService.push(notification);
        }
    }
}
