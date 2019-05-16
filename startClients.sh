#!/bin/sh

cd bin
java client.Writer W1 localhost 40000 omar2 123 5 &
java client.Reader R1 localhost 40000 omar2 12 &
java client.Writer W2 localhost 40000 omar 1234 20 &
java client.Writer W3 localhost 40000 omar2 abc 10 &
java client.Reader R2 localhost 40000 omar 1 &
java client.Reader R3 localhost 40000 omar 25 &
java client.Writer W4 localhost 40000 omar2 def 15 &
java client.Reader R5 localhost 40000 omar2 25 &
