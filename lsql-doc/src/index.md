
# Introduction

LiterateSQL (LSql) is a pragmatic Java database access library on top of JDBC.

Our philosophy:

* LSql is *not* yet another object/relational mapper. We believe that functional application data
(in particular stored in a relational model) should not be mapped to a strict classes/objects model.
Every time a programmer creates a new class (e.g. POJO), it will be incompatible with existing API,
whereas using well-known classes like Maps, Lists, etc. enables access to amazing libraries like
[Google Guava](http://code.google.com/p/guava-libraries/wiki/CollectionUtilitiesExplained).

* All data gets mapped to `java.util.Map`, `java.util.List` and `java.lang.Iterable`. However, LSql
uses subclasses to overcome the dynamic, less-types characteristics.

* It is not important to use SQL databases in a vendor-agnostic way. The RDBMS should be choosen (once)
depending on the project requirements and database access libraries should not try to hide their
characteristics.

* Use unit tests to verify your data model, not static typing. Obviously, using an untyped
datastructure like a Map imposes some challenges. E.g. a typo like `map.get("firstMame")` would
lead to an error which would have been catched with static typing. However, data structures like
`java.util.Map` provide ready to use methods like `equals(...)` which can be used to automatically
verify the data persistence logic and help to catch typos.

* Software architectures currently shift towards rich browser and stand-alone application. The main
responsibility for server code will be to query and return the stored data as quickly
as possible and to serialize, maybe even intentionally denormalized, views as JSON string. Traditional
Java persistence frameworks, based on the concept of handling all tasks within the server process (including
HTML rendering), are less suitable for those environments.

## Quick Example

Assume the following DDL:

    CREATE TABLE person (
        id SERIAL PRIMARY KEY, -- auto increment primary key
        name TEXT,
        age INT
    );

LSql Java code:

    DataSource dataSource = ...;
    LSql lsql = new LSql(new H2Dialect(), dataSource);

    // Create a new person row.
    Row newJohn = new Row();
    newJohn.put("name", "John");
    newJohn.put("age", 20);

    // Insert the new person
    Table tPerson = lsql.table("person");
    tPerson.insert(newJohn);

    // The generated ID is automatically put into the row object
    Object generatedId = newJohn.get("id");

    // Use the ID to load the row
    Optional<QueriedRow> queriedJohn = tPerson.get(generatedId);
    assert queriedJohn.get().getString("name").equals("John");
    assert queriedJohn.get().getInt("age") == 20;

# Download

## Maven Repository Artifacts

All released files are stored in a Maven repository:

    <repositories>
        <repository>
            <id>lsql-repo</id>
            <name>w11k Repository</name>
            <url>http://mvn.w11k.com/releases</url>
        </repository>
    </repositories>

Add the following dependency to your POM. The latest version number can always
be found here:

[Latest Released Version](https://raw.github.com/weiglewilczek/lsql/master/LATEST_RELEASED_VERSION)

    <dependency>
        <groupId>com.w11k.lsql</groupId>
        <artifactId>lsql-core</artifactId>
        <version> {{see link above for the latest version}} </version>
    </dependency>

# Documentation

## Configure LSql

## CRUD Operations

### Insert

### Read

### Update

### Delete

## Queries

### Simple Raw Queries

### Named Queries



