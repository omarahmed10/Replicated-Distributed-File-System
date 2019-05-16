#!/bin/sh
cd bin
rmic replica.ReplicaServerImpl master.MasterServerClientImpl

rmiregistry 50000 &
rmiregistry 50001 &
rmiregistry 50002 &
rmiregistry 50003 &
rmiregistry 50004 &
rmiregistry 50005 &
rmiregistry 50006 &
rmiregistry 50007 &
rmiregistry 50008 &
