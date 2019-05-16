#!/bin/sh

cd bin
java client.Reader R1 localhost 50000 omar 5 &
java client.Writer W2 localhost 50000 omar2 123 2 &
java client.Reader R2 localhost 50000 omar2 12 &
java client.Writer W2 localhost 50000 omar 1234 20 &
java client.Reader R3 localhost 50000 omar 1 &
java client.Reader R4 localhost 50000 omar 25 &
java client.Writer W3 localhost 50000 omar2 abc 10 &
java client.Writer W4 localhost 50000 omar2 def 10 &
java client.Writer W5 localhost 50000 omar2 hig 10 &
java client.Reader R5 localhost 50000 omar2 30 &
