#! /bin/bash

cd `dirname $0`/..

version=`cat pom.xml | grep -m 1 "<version>"`
version=$(echo $version | sed 's/<version>\(.*\)<\/version>/\1/g')
echo $version
