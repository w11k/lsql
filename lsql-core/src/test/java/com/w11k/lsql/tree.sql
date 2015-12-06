--tree
SELECT
  '!' || table1.id || '/',
  table1.*,
  '!' || table2.id || '/table2',
  table2.name2
FROM table1
  LEFT OUTER JOIN table2 ON table2.table1_id = table1.id;
