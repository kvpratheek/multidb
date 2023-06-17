package com.sap.multidb.app.repo;

import org.springframework.data.repository.CrudRepository;

import com.sap.multidb.app.model.TenantDetails;

public interface TenantDetailsRepo extends CrudRepository<TenantDetails, String> {

}
