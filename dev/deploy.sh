#! /bin/bash

function exit_on_error {
	[[ "$?" != 0 ]] && exit 1
}

cd `dirname $0`/..

changes=$(git status --porcelain 2>/dev/null| egrep "^(AM|M| M|\?\?)" | wc -l)
if [[ $changes != 0 ]]; then
    echo "Repository contains pending changes."
    exit 1
fi

dev/test_example.sh
exit_on_error

../mvnrepository/check_repository_status.sh
exit_on_error

mvn -Dmaven.test.skip=true -DaltDeploymentRepository=repo-snapshots::default::file:../mvnrepository/snapshots deploy
exit_on_error

git tag "`dev/get_current_version.sh`"
exit_on_error

dev/increment_project_version.sh
exit_on_error

../mvnrepository/commit_and_push.sh
exit_on_error

