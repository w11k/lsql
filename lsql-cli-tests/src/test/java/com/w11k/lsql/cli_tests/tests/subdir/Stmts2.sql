
--loadPersonsByAgeAndFirstName
select
*
from person2
where
age = /*: int  =*/ 100 /**/
and first_name = /*=*/ 'name' /**/
;

--deletePersonByFirstName: void
delete from person2 where first_name = /*=*/ 'name' /**/;
