#!/bin/sh

search_terms='rmiregistry'
kill $(ps aux | grep "$search_terms" | grep -v 'grep' | awk '{print $2}')

cd bin
rm -r logs/ Readers/ Writers/ replica_*/
