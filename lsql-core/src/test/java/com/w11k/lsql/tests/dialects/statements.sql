--join
SELECT
  *
FROM company
  JOIN customer ON customer.customer_company_fk = company.company_pk
  JOIN employee ON employee.employee_company_fk = company.company_pk
  JOIN contact c1
    ON c1.contact_pk = employee.contact1
  JOIN contact c2
    ON c2.contact_pk = employee.contact2
;

