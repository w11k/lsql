--resolveTableAliasWhenReadingResultSet
SELECT
  t1.id,
  t1.yesno
FROM ta t1
WHERE t1.id > -1
ORDER BY t1.id;

--resolveTableAliasWhenReadingResultSetWithSpecialCharsInTableName
SELECT
  t1.id,
  t1.yesno
FROM t_b t1
WHERE t1.id = /*(*/ 1 /*)*/
ORDER BY t1.id;

--resolveTableAliasWithWildcardWhenReadingResultSet
SELECT
  t1.*
FROM ta t1
WHERE t1.id > -1
ORDER BY t1.id;

--resolveJoinedTableAliasWhenReadingResultSet
SELECT
  t1.id    AS t1_id,
  t1.yesno AS t1_yesno,
  t2.id    AS t2_id,
  t2.yesno AS t2_yesno
FROM ta t1
  LEFT OUTER JOIN ta t2 ON t2.id = t1.id
WHERE t1.id > -1
ORDER BY t1.id;

--resolveColumnAliasWhenReadingResultSet
SELECT
  ta.id    AS ta_id,
  ta.yesno AS ta_yesno
FROM ta
WHERE ta.id > -1
ORDER BY ta.id;


--resolveTableAndColumnAliasWhenReadingResultSet
SELECT
  t1.id    AS ta_id,
  t1.yesno AS ta_yesno
FROM ta t1
WHERE t1.id > -1
ORDER BY t1.id;


--resolveTableAliasWhenSettingParameter
SELECT
  t1.id,
  t1.yesno
FROM ta t1
WHERE t1.yesno = /*(*/ 'true' /*)*/
ORDER BY t1.id;

