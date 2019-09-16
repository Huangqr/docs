#!/bin/bash
source /etc/profile
cd /root/app/regiestered
java -jar -Xms512m -Xmx512m holder-saas-registered-0.0.1-SNAPSHOT.jar
