# push-tester
APNS command line tester

## building
```
$ mvn clean package appassembler:assemble
```

## running
* Put your certificate in cert.p12

```
$ target/appassembler/bin/push-tester cert.p12 password device_token
```

where password is the password for the PKCS#12 file, and device_token is the device token received in the app.
