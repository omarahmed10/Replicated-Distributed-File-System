#!/bin/sh
cd bin
rmic replica.ReplicaServerImpl master.MasterServerClientImpl

rmiregistry 40000 &
rmiregistry 40001 &
rmiregistry 40002 &
rmiregistry 40003 &
rmiregistry 40004 &
rmiregistry 40005 &
rmiregistry 40006 &
rmiregistry 40007 &
rmiregistry 40008 &
