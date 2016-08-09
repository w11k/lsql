#! /bin/bash

function exit_on_error {
	[[ "$?" != 0 ]] && exit 1
}

cd `dirname $0`/..

# Check if master branch is active
branch=`git rev-parse --abbrev-ref HEAD`
if [ $branch != "master" ]; then
    echo "Not in master branch."
    exit 1
fi

# Test build
cd lsql-core
mvn clean install
cd ..
exit_on_error

# Test Sample Application
#cd lsql-example
#mvn clean test
#cd ..
#exit_on_error

# Set the current version
git tag "`dev/get_current_version.sh`"
exit_on_error
dev/get_current_version.sh > LATEST_RELEASED_VERSION

# Commit and push
git add --all
git commit -m "new release `dev/get_current_version.sh`"
git push origin master

# Deploy
mvn -Dmaven.test.skip=true deploy

VERSION=`dev/get_current_version.sh`

# Increment version and push
dev/increment_project_version.sh
git add --all
git commit -m "started new version `dev/get_current_version.sh`"
git push origin master

echo --------------------------------------------------------------------
echo Released version $VERSION
echo --------------------------------------------------------------------
