-- noinspection SqlResolveForFile

--tree1
SELECT
    table1.id AS "/",
    table1.*
FROM table1;

--tree2
SELECT
    table1.id AS "/",
    table1.*,
    table2.id AS "/table2",
    table2.*
FROM table1
    LEFT JOIN table2 ON table1.id = table2.table1_id;

--tree2and3
SELECT
    table1.id AS "/",
    table1.*,
    table2.id AS "/table2",
    table2.*,
    table3.id AS "/table3",
    table3.*
FROM table1
    LEFT JOIN table2 ON table1.id = table2.table1_id
    LEFT JOIN table3 ON table1.id = table3.table1_id;


--tree2Nested2bAnd3
SELECT
    table1.id  AS "/",
    table1.*,
    table2.id  AS "/table2",
    table2.*,
    table2b.id AS "/table2/table2b",
    table2b.*,
    table3.id  AS "/table3",
    table3.*
FROM table1
    LEFT JOIN table2 ON table1.id = table2.table1_id
    LEFT OUTER JOIN table2b ON table2.id = table2b.table2_id
    LEFT OUTER JOIN table3 ON table1.id = table3.table1_id;

