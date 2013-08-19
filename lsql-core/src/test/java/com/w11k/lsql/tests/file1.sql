-- create
CREATE TABLE table1 (
  id      SERIAL PRIMARY KEY,
  age     INT,
  content TEXT
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
