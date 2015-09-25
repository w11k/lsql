#! /bin/bash

cd `dirname $0`/..

SNIPPETS_DIR=target/snippets

rm -rf $SNIPPETS_DIR
mkdir $SNIPPETS_DIR

find src/test/java -type f -exec ./bin/process_file.sh {} \;
find $SNIPPETS_DIR -type f -exec cat {} \; > target/classes/main/index.md

