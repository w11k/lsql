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
mvn clean test
exit_on_error

# Check for pending changes
#changes=$(git status --porcelain 2>/dev/null| egrep "^(AM|M| M|\?\?)" | wc -l)
#if [[ $changes != 0 ]]; then
#    echo "Project contains pending changes."
#    exit 1
#fi

# Set the current version
dev/get_current_version.sh > LATEST_RELEASED_VERSION
git tag "`dev/get_current_version.sh`"

# Commit and push
git add --all
git commit -m "`dev/get_current_version.sh`"
git push origin master

# Deploy
mvn -Dmaven.test.skip=true deploy

# Increment version in pom.xml
dev/increment_project_version.sh

