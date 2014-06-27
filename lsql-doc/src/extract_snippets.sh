#! /bin/bash

cd `dirname $0`
rm -rf snippets
mkdir snippets

find ../../lsql-example/src/test/java -type f -exec cat {} \; | python extract_snippets.py

