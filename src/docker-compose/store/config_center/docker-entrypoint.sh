#!/bin/bash
source /etc/profile
cd /root/app/config_center
java -jar -Xms512m -Xmx512m holder-saas-config-center-0.0.1-SNAPSHOT.jar
