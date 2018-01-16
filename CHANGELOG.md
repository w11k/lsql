
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
