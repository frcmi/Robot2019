#!/bin/bash

pname=$1
pval=$2
server=$3
if [ -z "$server" ]; then
  server=http://tegra-ubuntu.local:5800
fi
curl --header "Content-Type: application/json" --request POST  --data "$pval" "$server/sv/api/v1.0/set-property?name=$pname"
