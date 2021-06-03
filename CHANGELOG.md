
**0.36.0**

- generate TypeScript typings in a dedicated file per table


**0.35.0**

*breaking change:* 

- All references to SQL identifiers (e.g. lsql.table("name")) must now be specified in the SQL format. The conversion only applies to `Row` keys.
- The IdentifierConverter is now a configuration property of `Config` instead of the `Dialect` and is called `RowKeyConverter`
- removed `Table.validate()`


**0.34.0**

- correctly handles unsupported compound primary keys
- lazily access lsql.table() in TypedTable


**0.33.5**

- append `Query` to generated query class names


**0.33.0**

- Core: Table#insert(): values for the primary columns are removed if they are `null`
- CLI: Added support for schemas


**0.32.10**

- fixed reading of table meta datas


**0.32.9**

- CLI: Print name of statement during execution
- `Converter#setValue/getValue` are now public to allow composition/wrapping of converters


**0.32.8**

- CLI: TypeScript interfaces are now named "Name_Row" and "Name_Map" instead of "NameRow" and "NameMap"  


**0.32.7**

- **BREAKING CHANGE**: Default IdentifierConverter is now JAVA_LOWER_UNDERSCORE_TO_SQL_LOWER_UNDERSCORE
- CLI: generate toMap and toInternalMap in DataClasses
- CLI: TypeScript generator now generates 2 interfaces per DataClass: typed and map
- CLI: generate TypeScript interfaces for DTOs
- CLI: DataClasses contain 2 static methods: fromMap / fromInternalMap


**0.32.4**

- CLI: improvement generated Java code names


**0.32.3**

- CLI: print help on wrong settings

**0.32.2**

- CLI: generated TypeScript files honors nullable attribute


**0.32.1**

- added several SuppressWarnings to generated classes


**0.32.0**

- updated ReactiveX version


**0.31.15**

- converters can now be targeted with type aliases
- improved code name generation


**0.31.14**

- CLI: detect nullable properties for statements


**0.31.12**

- CLI: added support for DTO generation
- CLI: generated statements contain their origin for logging
- CLI: bindings in generated Guice modules now use `.in(com.google.inject.Scopes.SINGLETON)` 
- CLI: fields in DataClasses now contain @Nonnull and @Nullable annotations

**0.31.11**

FIXED CLI: wrong table name embedded in generated table class

**0.31.10**

CLI: generate Guice module that include all table and statement files


**0.31.9**

FIXED: invalid import in generated statement files if all statements are void 


**0.31.8**

- CLI now generates equals()/hashCode()/toString() methods


**0.31.7**

- CaseFormat for generated code can now be different from the dialect's "to" case format


**0.31.5**

- infers Boolean from TRUE/FALSE in statements
- several CLI bugfixes

**0.30.0**

- in SQL statements:
    placeholder values like "/*=*/ 1.2 /**/" are not inferred with type double
    placeholder values like "/*=*/ 1 /**/" are not inferred with type number

- new converter: NumberConverter (can only be used to set parameters in prepared statements)


**0.29.6**

escape quotes in SQL statements during TypedStatement export


**0.29.5**

Row class fields are now `public final` instead of `private`

**0.29.3**

- added `statement: nogen` option


**0.29.0**

- made code generation more robust
- fixed missing parameter passing in TypedStatementCommands


**0.28.7**

Column names are now escaped in SQL statements to avoid conflicts with reserved keyword


**0.28.6**

Generated classes `TableRow` and `TypedTable` now contain the table and column names as static fields


**0.28.4**

Added convenience map method to TypedStatementQuery


**0.28.2**

TypeScript generation uses interfaces

include statement rows in TypeScript export


**0.28.1**

added TypeScript generation


**0.28.0**

add deleteById method to PojoTable


**0.27.7**

add deleteById method to generated TypedTable classes #25


**0.27.6**

fixed bug with ListLiteralQueryParameter and statement type parameter checks


**0.27.5**

- added support for statement return type void
- improved code generation


**0.27.4**

- added check for pk column in Table#delete
- implemented CLI statement generation


**0.27.3**

fixed statement parameter recognition


**0.27.2**

Removed logger warning "Unable to determine a Converter instance for column..."


**0.27.1**

Fixed LongConverter


**0.27.0**

Added method to specify the converter for statement parameters explicitly.

```
statement.setParameterConverter("paramName", converterInstance).query("paramName", value)
```

**0.26.6**

maintenance release
