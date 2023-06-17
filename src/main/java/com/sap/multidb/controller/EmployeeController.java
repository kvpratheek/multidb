package com.sap.multidb.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.sap.multidb.multitenancy.TenantContext;
import com.sap.multidb.tenant.model.Employee;
import com.sap.multidb.tenant.repo.EmployeeRepo;

@RestController
public class EmployeeController {

    @Autowired
    EmployeeRepo employeeRepo;

    @Autowired
    TenantContext tenantContext;

    @PostMapping("/employee")
    public Employee createEmployee(@RequestParam final String tenantId, @RequestBody final Employee employee) {
        tenantContext.setCurrentTenant(tenantId);
        return employeeRepo.save(employee);
    }

    @GetMapping("/employee")
    public List<Employee> getAllEmployee(@RequestParam final String tenantId) {
        tenantContext.setCurrentTenant(tenantId);
        return (List<Employee>) employeeRepo.findAll();
    }

    @GetMapping("/employee/{id}")
    public Optional<Employee> getAllEmployee(@RequestParam final String tenantId, @PathVariable("id") final String employeeId) {
        tenantContext.setCurrentTenant(tenantId);
        return employeeRepo.findById(employeeId);
    }
}
