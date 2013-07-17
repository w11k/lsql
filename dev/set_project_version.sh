#! /bin/bash

cd `dirname $0`/..

if [ $# -eq 0 ]
  then
    echo "Usage: set_project_version.sh <new version>"
    echo "Current version: `dev/get_current_version.sh`"
    exit 1
fi

version_new=$1

mvn versions:set -DnewVersion=$version_new -DgenerateBackupPoms=false
cd lsql-example
mvn versions:set -DnewVersion=$version_new -DgenerateBackupPoms=false

dev/get_current_version.sh >> VERSION
