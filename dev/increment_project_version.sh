#! /bin/bash

cd `dirname $0`/..

CURRENT_VERSION=`cat pom.xml | grep -m 1 "<version>" | tr "-" "\n"`

for x in $CURRENT_VERSION
do
    echo "> [$x]"
done


echo $CURRENT_VERSION

