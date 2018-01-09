
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

--keepUnderscoreForCamelCase
select
person1.id as "a_field" /*:int*/,
person1.first_name as "aField" /*:string*/
from person1;
