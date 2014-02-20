/**/

-- create1
CREATE TABLE table1 (
  id      SERIAL PRIMARY KEY,
  age     INT,
  content TEXT
);

-- create2
CREATE TABLE table2 (
  id     SERIAL PRIMARY KEY,
  number INT
);

-- deleteYoung
DELETE FROM table1
WHERE age < /*(*/ 5 /*)*/;

-- getAll
SELECT
  *
FROM table1;

-- queryRangeMarkers
SELECT
  *
FROM table1
WHERE
  age < /*(*/ 15 /*)*/;

-- queryFunctionCallback
SELECT
  *
FROM table1
WHERE
  table1.age IN (/*(*/ 10, 30, 60 /*)*/);

-- queryColumnConverter
SELECT
  *
FROM table2
WHERE
  table2.number < /*(*/ 1 /*)*/
;

--convertOperatorForNullValues
SELECT
  *
FROM table1
WHERE age = /*(*/ 2 /*)*/;


--executeQueryWithAlias
SELECT
  table1.id      AS i,
  table1.age     AS a,
  table1.content AS c
FROM table1
WHERE
  table1.age > /*(*/ 0 /*)*/;

--executeQueryWithTableAlias1
SELECT
  t1.id      AS i,
  t1.age     AS a,
  t1.content AS c
FROM table1 t1
WHERE t1.age > /*(*/ 0 /*)*/;

--executeQueryWithTableAlias2
SELECT
  t1.id      AS i,
  t1.age     AS a,
  t1.content AS c,
  table2.id  AS t2_id
FROM table2, table1 t1
WHERE t1.age > /*(*/ 0 /*)*/;
