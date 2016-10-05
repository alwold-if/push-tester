#!/bin/sh

openssl pkcs8 -nocrypt -in $1 -outform DER -out $2 -topk8
