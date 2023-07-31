#! /bin/bash

scp backend/target/backend-0.0.1-SNAPSHOT.jar springboot:kob/backendcloud
scp BotRunningSystem/target/BotRunningSystem-0.0.1-SNAPSHOT.jar springboot:kob/backendcloud
scp matchingsystem/target/matchingsystem-0.0.1-SNAPSHOT.jar springboot:kob/backendcloud
