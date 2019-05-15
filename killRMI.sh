#!/bin/sh

search_terms='rmiregistry'
kill $(ps aux | grep "$search_terms" | grep -v 'grep' | awk '{print $2}')
