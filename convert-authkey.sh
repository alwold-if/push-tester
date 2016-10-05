#!/bin/sh

# clean up the PEM formatting from apple
fold -w 64 $1 > $1.folded

openssl pkcs8 -nocrypt -in $1.folded -outform DER -out $2 -topk8
