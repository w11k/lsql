
-- create2
CREATE TABLE table2 (
  age     INT,
  content TEXT
);

--insert2
insert into table2 (age, content) values (10, 'test');

--columnAliasBehaviour
SELECT
  table2.age as a,
  table2.content as c
FROM table2;
