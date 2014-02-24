-- create2
CREATE TABLE table2 (
  field_a INT,
  field_b VARCHAR(50)
);

--insert2
INSERT INTO table2 (field_a, field_b) VALUES (10, 'test');

--tableAlias
SELECT
  t1.age
FROM table1 t1
WHERE t1.age > 22;

--columnAliasBehaviour
SELECT
  table2.field_a AS a,
  table2.field_b AS b
FROM table2;
