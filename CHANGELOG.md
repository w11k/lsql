
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
