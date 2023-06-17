package com.sap.multidb.tenant.repo;

import org.springframework.data.repository.CrudRepository;


public interface EmployeeRepo extends CrudRepository<com.sap.multidb.tenant.model.Employee, String> {

}
