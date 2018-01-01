
--loadPersonsByAgeAndFirstName
select
*
from person2
where
age = /*=*/ 100 /**/
-- test comment embedded
and first_name = /*: string =*/ 'name' /**/
;

--deletePersonByFirstName: void
delete from person2 where first_name = /*=*/ 'name' /**/;

--testNoStatement
-- SELECT
-- *
-- from t
-- where
-- col = /*=*/ '12345' /**/
--                     ;
