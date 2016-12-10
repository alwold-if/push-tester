# push-tester
APNS command line tester

## building
```
$ mvn clean package appassembler:assemble
```

## running (using v1 apns)
* Put your certificate in cert.p12
* Add a config file
```json
{
  "certificate": {
    "file": "cert.p12",
    "password": "abc123"
  },
  "environment": "sandbox",
  "deviceTokens": [
    "xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx"
  ],
  "payload": {
    "aps": {
      "alert": "Hello world"
    }
  }
}
```

```
$ target/appassembler/bin/push-tester config.json
```

## running (using v2 apns)
* Download auth key from Apple
* Convert auth key
```
$ ./convert-authkey.sh APNSAuthKey_XXXXXXXX.p8 APNSAuthKey_XXXXXXXX.p8.der
```
* Create config file
```json
{
  "key": {
    "file": "APNSAuthKey_JN379KQMXU.p8.der",
    "keyId": "JN379KQMXU"
  }
  "teamId": "NZ8W5X5MA5",
  "bundleId": "com.ifactorinc.NotificationDemo",
  "environment": "sandbox",
  "deviceTokens": [
    "994a913e401498e1f334a04c5d08c61edcf12849a7c998a259339d88f10dcaa6"
  ],
  "payload": {
    "aps": {
      "alert": "Hello world again"
    }
  }
}
```
* Run
```
$ target/appassembler/bin/token-push-tester config.json
```