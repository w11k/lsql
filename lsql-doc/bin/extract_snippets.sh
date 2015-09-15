#! /bin/bash

cd `dirname $0`/..

SNIPPETS_DIR=target/snippets

rm -rf $SNIPPETS_DIR
mkdir $SNIPPETS_DIR

find src/test/java -type f
find src/test/java -type f -exec cat {} \; | python bin/extract_snippets.py

ls -l $SNIPPETS_DIR
