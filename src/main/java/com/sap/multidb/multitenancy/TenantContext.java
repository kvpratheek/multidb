package com.sap.multidb.multitenancy;

import org.springframework.stereotype.Component;

@Component
public class TenantContext {

    private static ThreadLocal<String> currentTenant = new ThreadLocal<>();

    public void setCurrentTenant(final String tenant) {
        currentTenant.set(tenant);
    }

    public String getCurrentTenant() {
        return currentTenant.get();
    }

}
