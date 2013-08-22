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

-- getAll
SELECT
  *
FROM table1;

-- queryWithIntegerArg
SELECT
  *
FROM table1
WHERE
  age > 50 --age
;

-- queryWithStringArg
SELECT
  *
FROM table1
WHERE
  content = 'text1' --content
;

-- queryColumnConverter
SELECT
  *
FROM table2
WHERE
  number < 1 --table2.number
;
