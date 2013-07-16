#! /bin/bash

cd `dirname $0`/..

mvn versions:display-dependency-updates
