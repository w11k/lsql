#! /bin/bash

cd `dirname $0`/..

# TODO test example

../mvnrepository/check_repository_status.sh
mvn -Dmaven.test.skip=true -DaltDeploymentRepository=repo-snapshots::default::file:../mvnrepository/snapshots deploy
../mvnrepository/commit_and_push.sh
