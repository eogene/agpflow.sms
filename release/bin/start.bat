@echo off
cd .. 
start javaw -Dfile.encoding=utf-8 -jar agpflow.sms-0.0.1-SNAPSHOT.jar --server.port=8099
exit