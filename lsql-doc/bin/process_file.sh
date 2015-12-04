#! /bin/bash

cd `dirname $0`/..

SNIPPETS_DIR=target/snippets
FILE=$SNIPPETS_DIR/$1

mkdir -p `dirname $FILE`
echo "" > $FILE

EXTRACT_ON=0
INDENT=0
if [[ $1 == *".md" ]]; then
    EXTRACT_ON=1
fi

IFS=''
while read LINE; do

if [[ $EXTRACT_ON == 1 ]] \
    && [[ $LINE != *"*/"* ]] \
    && [[ $LINE != *"/*"* ]] \
    && [[ $LINE != *")))"* ]]
then
    echo ${LINE:$INDENT} >> $FILE
fi

if [[ $LINE == *"((("* ]]; then
    EXTRACT_ON=1
    INDENT=`echo $LINE | awk '{t=length($0);sub("^ *","");print t-length($0)}'`
fi

if [[ $LINE == *")))"* ]]; then
    EXTRACT_ON=0
    INDENT=0
fi

done < $1
