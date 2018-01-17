
# Literate SQL - A Java Database Library

[![Join the chat at https://gitter.im/w11k/lsql](https://badges.gitter.im/Join%20Chat.svg)](https://gitter.im/w11k/lsql?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge&utm_content=badge)

LSql (Literate SQL) is a pragmatic Java database access library on top of JDBC.

Our philosophy:

* LSql is *not* yet another object/relational mapper. We believe that functional application data (in particular stored in a relational model) should not be mapped to a strict classes/objects model. Every time a programmer creates a new class (even a simple POJO), it will be incompatible with existing API, whereas using well-known classes like Maps, Lists, etc. enables access to amazing libraries like [Google Guava](http://code.google.com/p/guava-libraries/wiki/CollectionUtilitiesExplained).

* Database first

* It is not important to use SQL databases in a vendor-agnostic way. The RDBMS should be choosen (once) depending on the project requirements and database access libraries should not try to hide their characteristics.


## Quick Example

Assume the following DDL:

```{.language-sql}
CREATE TABLE person (
    id SERIAL PRIMARY KEY,
    name VARCHAR(200),
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
Optional<QueriedRow> queried = personTable.load(newId);
QueriedRow queriedJohn = queried.get();
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

Add the following dependency to your POM. The latest version number can always be found here:

[Latest Released Version](https://raw.github.com/w11k/lsql/master/LATEST_RELEASED_VERSION)

```{.language-xml}
<dependency>
    <groupId>com.w11k.lsql</groupId>
    <artifactId>lsql-core</artifactId>
    <version>{{see link above for the latest version}} </version>
</dependency>
```

# Documentation

