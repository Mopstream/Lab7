#!bin/bash
mvn install; rm lab-server/target/ServerRun.jar; mv lab-server/target/lab-server-1.0-SNAPSHOT-jar-with-dependencies.jar lab-server/target/ServerRun.jar;rm lab-client/target/ClientRun.jar; mv lab-client/target/lab-client-1.0-SNAPSHOT-jar-with-dependencies.jar lab-client/target/ClientRun.jar