#! /bin/bash

cd `dirname $0`/..

mvn clean install
cd lsql-example
mvn exec:java
