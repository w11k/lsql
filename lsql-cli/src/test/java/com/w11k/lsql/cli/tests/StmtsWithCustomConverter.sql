
--load
select * from custom_converter
where field = /*: custom =*/ 1 /**/
;

--testQueryParamter
select
*
from
person1
WHERE id = /*=*/ 1 /**/;
