#!/bin/bash

if [ $BUILD_TYPE == 'deploy' ]; then
    echo "Creating binary directories"
    mkdir -p binaries/view
    mkdir -p binaries/cloud
    mkdir -p binaries/services

    echo "Moving view binary"
    mv view/target/xcolab-view-1.0-SNAPSHOT.war binaries/view/

    echo "Moving cloud binaries"
    mv microservices/cloud/eureka-server/target/eureka-server-1.0-SNAPSHOT.jar binaries/cloud/
    mv microservices/cloud/service-proxy/target/service-proxy-1.0-SNAPSHOT.jar binaries/cloud/

    echo "Moving service binaries"
    mv microservices/services/*-service/target/*-1.0-SNAPSHOT.jar binaries/services/
else
    echo "Skipping deployment"
fi
