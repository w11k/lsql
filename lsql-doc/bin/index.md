

# Introduction

LSql (Literate SQL) is a pragmatic Java database access library on top of JDBC.

Our philosophy:

* LSql is *not* yet another object/relational mapper. We believe that functional application data (in particular stored in a relational model) should not be mapped to a strict classes/objects model. Every time a programmer creates a new class (even a simple POJO), it will be incompatible with existing API, whereas using well-known classes like Maps, Lists, etc. enables access to amazing libraries like [Google Guava](http://code.google.com/p/guava-libraries/wiki/CollectionUtilitiesExplained).

* All data gets mapped to `com.w11k.lsql.Row` (which is a subclass of `java.util.Map`) and `java.util.List`. You can specify table-specific subclasses of `com.w11k.lsql.Row` to use your own domain classes, where useful.   

* It is not important to use SQL databases in a vendor-agnostic way. The RDBMS should be choosen (once) depending on the project requirements and database access libraries should not try to hide their characteristics.

* Software architectures currently shift towards rich browser and stand-alone application. The main responsibility for server code will be to query and return the stored data as quickly as possible and to serialize, maybe even intentionally denormalized, views as JSON string. Traditional Java persistence frameworks, based on the concept of handling all tasks within the server process (including HTML rendering), are less suitable for those environments.

## Quick Example

Assume the following DDL:

```{.language-sql}
CREATE TABLE person (
    id SERIAL PRIMARY KEY, -- auto increment primary key
    name TEXT,
    age INT
);
```

LSql Java code:

```{.language-java}
DataSource dataSource = ...;
LSql lsql = new LSql(new H2Dialect(), dataSource);

// Create a new person
Row john = new Row();
john.put("name", "John");
john.put("age", 20);

// Insert the new person
Table personTable = lsql.table("person");
personTable.insert(john);

// The generated ID is automatically put into the row object
Object newId = john.get("id");

// Use the ID to load the row, returns com.google.common.base.Optional
Optional<QueriedRow> queried = personTable.get(newId);
QueriedRow queriedJohn = queried.get();

assert queriedJohn.getString("name").equals("John");
assert queriedJohn.getInt("age") == 20;
```

# Download

## Maven Repository Artifacts

All released files are stored in a Maven repository:

```{.language-xml}
<repositories>
    <repository>
        <id>lsql-repo</id>
        <name>w11k Repository</name>
        <url>http://mvn.w11k.com/releases</url>
    </repository>
</repositories>
```

Add the following dependency to your POM. The latest version number can always
be found here:

[Latest Released Version](https://raw.github.com/weiglewilczek/lsql/master/LATEST_RELEASED_VERSION)

```{.language-xml}
<dependency>
    <groupId>com.w11k.lsql</groupId>
    <artifactId>lsql-core</artifactId>
    <version>{{see link above for the latest version}} </version>
</dependency>
```

# Documentation

## Configure LSql

## Row Class

## CRUD Operations

### Insert

### Read

### Update

### Delete

## Queries

### Simple Raw Queries

### Named Queries

###

