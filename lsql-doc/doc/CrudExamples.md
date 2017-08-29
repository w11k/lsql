
fdsfjdsfdsfdsfds fdsjfk jds lfdsf

- fsfdsfs
- fjdfdslfs

```
Table personTable = lSql.table("person");

// Option 1: java.util.Map
Map<String, Object> person1 = new HashMap<String, Object>();
person1.put("name", "John");
person1.put("birthday", DateTime.parse("1980-10-1"));
personTable.insert(new Row(person1));

// Option 2: Use a Row class
Row person2 = Row.fromKeyVals(
        "name", "Linus",
        "birthday", DateTime.parse("1970-10-1")
);
personTable.insert(person2);

// Option 3: LinkedRow
LinkedRow person3 = personTable.newLinkedRow(
        "name", "Linus",
        "birthday", DateTime.parse("1970-10-1")
);
person3.save();
```
