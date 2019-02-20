#! /bin/bash

mvn clean flyway:clean flyway:migrate compile exec:java
