
--loadAllPersons
select * from person1;

--loadAllPersonsEscaped1
select
person1.id as theId /*:int*/
from person1;

--loadAllPersonsEscaped2
select
person1.id as "theId" /*:int*/
from person1;

--queryParamsWithDot
select
person1.*
from person1
WHERE
person1.id = /*=*/ 1 /**/;

--keepUnderscoreForCamelCase
select
person1.id as "a_field" /*:int*/,
person1.first_name as "aField" /*:string*/
from person1;

--changeYesno: void
UPDATE checks
SET yesno = /*=*/ TRUE /**/
;
