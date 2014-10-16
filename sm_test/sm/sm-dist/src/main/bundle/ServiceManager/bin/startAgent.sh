#!/bin/bash
dir=$(cd -P -- "$(dirname -- "$0")" && pwd -P)
cd $dir
java -Djava.net.preferIPv4Stack=true -cp "./system/*" com.edifecs.agent.launcher.AgentStarter -launchNodes=false %
