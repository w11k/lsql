
# Literate SQL - A Java Database Library

LSql (Literate SQL) is a Java database library focusing on type-safety and the preservation of the relational data model.  

Key points:

* LSql is *not* yet another object/relational mapper. We believe that functional application data (in particular stored in a relational model) should not be mapped to a strict classes/objects model. The relational data model is elegant, well-thought-out and a reliable way to model the application data. Any abstraction between the relational model and the application logic makes things more complicated and should be avoided.

* Based on your *database schema* and *SQL statement files*, LSql generates Java classes (with an immutability API design) in order to interact with the database in a type-safe manner.   

* SQL is a superior language for data manipulation. The RDBMS should be choosen depending on the project requirements and database access libraries should not try to hide their characteristics.


## Example

### CRUD

Given the following DDL:

```sql
CREATE TABLE person (
    id SERIAL PRIMARY KEY,
    name VARCHAR(200),
    age INT
);
```

Java code:

```java
// insert
Person_Table personTable = new Person_Table(lSql);
Person_Row person = new Person_Row()
    .withName("John")
    .withAge(20);
Optional<Integer> pk = personTable.insert(person);

// load by ID
Optional<Person_Row> personRowOptional = personTable.load(pk.get());
personRowOptional.get().name == "John";
personRowOptional.get().age == 20;
```

## Query

Given the following SQL statement file `PersonStatements.sql`:

```sql
--loadPersonByAge
SELECT * FROM person WHERE age = /*=*/ 0 /**/;
```

Java code:

```java
PersonStatements statements = new PersonStatements(lSql);

List<LoadPersonByAge> list = personStatements.loadPersonByAge()
    .withAge(20)
    .toList();

list.get(0).name == "John";
list.get(0).age == 20;
```





# Download

## Maven Repository Artifacts

All released files are stored in a Maven repository:

```{.language-xml}
<repositories>
    <repository>
        <snapshots>
            <enabled>false</enabled>
        </snapshots>
        <id>bintray-w11k-lsql</id>
        <name>bintray</name>
        <url>https://dl.bintray.com/w11k/lsql</url>
    </repository>
</repositories>

<dependencies>
    <dependency>
        <groupId>com.w11k.lsql</groupId>
        <artifactId>lsql-core</artifactId>
        <version>...</version>
    </dependency>
    <dependency>
        <groupId>com.w11k.lsql</groupId>
        <artifactId>lsql-guice</artifactId>
        <version>...</version>
    </dependency>
    <dependency>
        <groupId>com.w11k.lsql</groupId>
        <artifactId>lsql-cli</artifactId>
        <version>...</version>
    </dependency>
</dependencies>
```
