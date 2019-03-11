
--loadAllPersons
select * from person1;

--loadAllPersonsColumnAlias
select
person1.id as "pid: int"
from person1;

--queryParamsWithDot
select
person1.*
from person1
WHERE
person1.id = /*=*/ 1 /**/;

--changeYesno: void
UPDATE checks
SET yesno = /*=*/ TRUE /**/
;
