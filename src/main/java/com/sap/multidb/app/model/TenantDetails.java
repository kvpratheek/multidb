package com.sap.multidb.app.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Id;

//@Entity
//@Table(name = "TNTDETAILS")
public class TenantDetails implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "TENANTID")
    private String tenantId;

    @Column(name = "PLAN")
    private String plan;

    public TenantDetails() {
        super();
        // TODO Auto-generated constructor stub
    }

    public TenantDetails(final String tenantId, final String plan) {
        super();
        this.tenantId = tenantId;
        this.plan = plan;
    }

    public String getTenantId() {
        return tenantId;
    }

    public void setTenantId(final String tenantId) {
        this.tenantId = tenantId;
    }

    public String getPlan() {
        return plan;
    }

    public void setPlan(final String plan) {
        this.plan = plan;
    }

    public static long getSerialversionuid() {
        return serialVersionUID;
    }

    @Override
    public String toString() {
        return "TenantDetails [tenantId=" + tenantId + ", plan=" + plan + "]";
    }

}
