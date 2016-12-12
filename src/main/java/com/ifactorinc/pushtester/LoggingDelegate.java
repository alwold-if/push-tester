package com.ifactorinc.pushtester;

import com.notnoop.apns.ApnsDelegate;
import com.notnoop.apns.ApnsNotification;
import com.notnoop.apns.DeliveryError;
import org.apache.commons.codec.binary.Hex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by albertwold on 12/11/16.
 */
public class LoggingDelegate implements ApnsDelegate {
    private static final Logger logger = LoggerFactory.getLogger(LoggingDelegate.class);

    public void messageSent(ApnsNotification message, boolean resent) {
        logger.trace("message sent to "+ new String(Hex.encodeHex(message.getDeviceToken())));
    }

    public void messageSendFailed(ApnsNotification message, Throwable e) {
        logger.trace("message failed to "+new String(Hex.encodeHex(message.getDeviceToken())));
    }

    public void connectionClosed(DeliveryError e, int messageIdentifier) {
        logger.trace("connection closed: "+e.name());
    }

    public void cacheLengthExceeded(int newCacheLength) {
    }

    public void notificationsResent(int resendCount) {
        logger.trace("notifications resent");
    }
}
