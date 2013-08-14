
-- create
create table table1 (
  id serial primary key,
  age int
);

--  getAll
select * from table1;

-- getSome
select * from table1 where
  age > /*age*/1
;
