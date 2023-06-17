package com.sap.multidb.tenant.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "EMPLOYEE")
public class Employee implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "EID")
    private String employeeId;

    @Column(name = "ENAME")
    private String employeeName;

    @Column(name = "EDEPT")
    private String employeeDepartment;

    public Employee() {
        super();
    }

    public Employee(final String employeeId, final String employeeName, final String employeeDepartment) {
        super();
        this.employeeId = employeeId;
        this.employeeName = employeeName;
        this.employeeDepartment = employeeDepartment;
    }

    public String getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(final String employeeId) {
        this.employeeId = employeeId;
    }

    public String getEmployeeName() {
        return employeeName;
    }

    public void setEmployeeName(final String employeeName) {
        this.employeeName = employeeName;
    }

    public String getEmployeeDepartment() {
        return employeeDepartment;
    }

    public void setEmployeeDepartment(final String employeeDepartment) {
        this.employeeDepartment = employeeDepartment;
    }

    public static long getSerialversionuid() {
        return serialVersionUID;
    }

}
