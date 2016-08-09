#! /bin/bash

cd `dirname $0`/..

if [ $# -eq 0 ]
  then
    echo "Usage: set_project_version.sh <new version>"
    echo "Current version: `dev/get_current_version.sh`"
    exit 1
fi

version_new=$1

#cd lsql-example
#sed -i "s/<lsql-version>.*<\/lsql-version>/<lsql-version>$version_new<\/lsql-version>/g" pom.xml
#cd ..

mvn versions:set -DnewVersion=$version_new -DgenerateBackupPoms=false

dev/get_current_version.sh > VERSION
