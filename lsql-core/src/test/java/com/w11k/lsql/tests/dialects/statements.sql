
-- create2
CREATE TABLE table2 (
  field_a int,
  field_b varchar (50)
);

--insert2
insert into table2 (field_a, field_b) values (10, 'test');

--columnAliasBehaviour
SELECT
  table2.field_a as a,
  table2.field_b as b
FROM table2;
