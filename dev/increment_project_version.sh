#! /bin/bash

cd `dirname $0`/..

version=`dev/get_current_version.sh`
version_number=$(echo $version | sed 's/\(.*\..*\..*\)-SNAPSHOT/\1/g')

version_segments=($(echo $version_number | tr "." " "))
vmajor=${version_segments[0]}
vminor=${version_segments[1]}
vmicro=`expr ${version_segments[2]} + 1`

version_new="$vmajor.$vminor.$vmicro"

version_snapshot=$(echo $version | grep "\-SNAPSHOT" | wc -m)
if [ $version_snapshot -ne 0 ]; then
    version_new="$version_new-SNAPSHOT"
fi

mvn versions:set -DnewVersion=$version_new -DgenerateBackupPoms=false
mvn -Dmaven.test.skip=true install
cd lsql-example
mvn versions:set -DnewVersion=$version_new -DgenerateBackupPoms=false
