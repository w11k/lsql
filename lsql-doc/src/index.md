
### Introduction

LiterateSQL (LSql) is a pragmatic Java database access library on top of JDBC.

Out philosophy:

* LSql is *not* yet another object/relational mapper. We believe that functional application data
(in particular stored in a relational model) should not be mapped to a strict classes/objects model.
Every time a programmer creates a new class (e.g. POJO) it will be incompatible with existing API,
whereas using well-known classes like Maps, Lists, etc. enables access to amazing libraries like
[Google Guava](http://code.google.com/p/guava-libraries/wiki/CollectionUtilitiesExplained).

* All data gets mapped to `java.util.Map`, `java.util.List` and `Iterable`. However, LSql provides
subclasses to overcome the dynamic, less-types characteristics.

start

    if (true) {
        test();
    }


ende

### Maven Repository Artifacts

1

    <repositories>
        <repository>
            <id>lsql-github</id>
            <name>GitHub LSql Repository</name>
            <url>https://raw.github.com/weiglewilczek/lsql/mvn-repository</url>
        </repository>
    </repositories>

2

[latest version](https://raw.github.com/weiglewilczek/lsql/master/LATEST_RELEASED_VERSION)

    <dependency>
        <groupId>com.w11k.lsql</groupId>
        <artifactId>lsql-core</artifactId>
        <version> {{see link above for the latest version}} </version>
    </dependency>

3



#### Another section


