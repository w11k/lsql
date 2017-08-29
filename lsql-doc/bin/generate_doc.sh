#! /bin/bash

cd `dirname $0`/..

mvn clean test
bin/extract_snippets.sh

rm -rf doc
cp -r target/doc/src/test/java/doc/ doc

#pandoc target/classes/main/index.md \
#--template=target/classes/main/template.html \
#--smart \
#--standalone \
#--toc \
#-o target/classes/main/index.html
