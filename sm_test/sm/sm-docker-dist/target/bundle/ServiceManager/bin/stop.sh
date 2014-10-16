#!/bin/bash
dir=$(cd -P -- "$(dirname -- "$0")" && pwd -P)
cd $dir
java -Djava.net.preferIPv4Stack=true -Djava.library.path="../platform/core/lib/native" -cp "./*:../platform/core/lib/sm-container/*:../platform/core/lib/common/*" com.edifecs.agent.stopper.AgentStopper $@
