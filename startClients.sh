#!/bin/sh

cd bin
java client.Reader localhost 40000 omar 5
java client.Writer localhost 40000 omar2 123 10
java client.Reader localhost 40000 omar2 12
java client.Writer localhost 40000 omar 1234 20
java client.Reader localhost 40000 omar 1
java client.Reader localhost 40000 omar 25
