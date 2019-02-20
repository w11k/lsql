#! /bin/bash

docker run --name lsql_benchmark -e POSTGRES_DB=lsql_benchmark -e POSTGRES_USER=lsql_benchmark -e POSTGRES_PASSWORD=lsql_benchmark -p 127.0.0.1:33333:5432 -d postgres:9.6
