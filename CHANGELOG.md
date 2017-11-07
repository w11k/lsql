
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
