
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
