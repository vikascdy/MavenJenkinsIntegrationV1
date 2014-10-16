#!/bin/bash
echo "Starting Edifecs Agent...";
dir=$(cd -P -- "$(dirname -- "$0")" && pwd -P);
nohup $dir/start.sh $@ 1>console.log 2>&1 &
echo $$ > .pid

