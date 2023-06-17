package com.sap.multidb.jdbc.tenant;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.concurrent.ConcurrentHashMap;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.datasource.AbstractDataSource;

import com.sap.multidb.jdbc.exceptions.NoInstanceException;
import com.sap.multidb.jdbc.exceptions.PVSManagedHanaInstanceException;
import com.sap.multidb.jdbc.exceptions.PVSRuntimeException;
import com.sap.multidb.multitenancy.TenantContext;

public class TenantAwareDataSource extends AbstractDataSource {

    public static final String DUMMY_TENANT = "DUMMY_TENANT_PVS";

    private static ConcurrentHashMap<String, DataSource> dataSourceNamespaceVsDataSource = new ConcurrentHashMap<>();
    private final Logger tenantAwareDataSourcelogger = LoggerFactory.getLogger(TenantAwareDataSource.class);
    private final HanaInstanceManagerConnector hanaInstanceManagerConnector;
    private final TenantContext tenantContext;

    private static final String ERROR_DATA_SOURCE_CONNECTION = "Error while getting data source connection";

    public TenantAwareDataSource(final HanaInstanceManagerConnector hanaInstanceManagerConnector, final TenantContext tenantContext) {
        this.hanaInstanceManagerConnector = hanaInstanceManagerConnector;
        this.tenantContext = tenantContext;
        if (!hanaInstanceManagerConnector.doesInstanceExist(DUMMY_TENANT)) {
            tenantAwareDataSourcelogger.info("Create Instance for dummy tenant " + DUMMY_TENANT);
            try {
                hanaInstanceManagerConnector.createManagedInstance(DUMMY_TENANT);
            } catch (PVSManagedHanaInstanceException e) {
                tenantAwareDataSourcelogger.error("Instance for dummy tenant could not be created", e);
                throw new PVSRuntimeException("Instance for dummy tenant could not be created", e);
            }
        }
    }

    @Override
    public Connection getConnection() throws SQLException {
        try {
            Connection connection = getDataSource().getConnection(); // NOSONAR
            if (connection.isValid(10)) {
                return connection;
            }
            return getRefreshedTenantDataSource().getConnection();
        } catch (SQLException exception) {
            try {
                // if an entry is outdated because a tenant got new db connection data, the entry
                // must be rebuild ex: tenant deboard and onboard.
                return getRefreshedTenantDataSource().getConnection();
            } catch (SQLException | NoInstanceException ex) {
                tenantAwareDataSourcelogger.error(ERROR_DATA_SOURCE_CONNECTION, ex);
                throw new SQLException(ex.getMessage());
            }
        } catch (NoInstanceException ex) {
            tenantAwareDataSourcelogger.error(ERROR_DATA_SOURCE_CONNECTION, ex);
            throw new SQLException(ex.getMessage());
        }
    }

    @Override
    public Connection getConnection(final String username, final String password) throws SQLException {
        try {
            Connection connection = getDataSource().getConnection(username, password); // NOSONAR
            if (connection.isValid(10)) {
                return connection;
            }
            return getRefreshedTenantDataSource().getConnection(username, password);
        } catch (SQLException exception) {
            try {
                // if an entry is outdated because a tenant got new db connection data, the entry
                // must be rebuild ex: tenant deboard and onboard.
                return getRefreshedTenantDataSource().getConnection(username, password);
            } catch (SQLException | NoInstanceException ex) {
                tenantAwareDataSourcelogger.error(ERROR_DATA_SOURCE_CONNECTION, ex);
                throw new SQLException(ex.getMessage());
            }
        } catch (NoInstanceException ex) {
            tenantAwareDataSourcelogger.error(ERROR_DATA_SOURCE_CONNECTION, ex);
            throw new SQLException(ex.getMessage());
        }

    }

    protected DataSource getRefreshedTenantDataSource() throws NoInstanceException {
        String tenantId = tenantContext.getCurrentTenant();
        if (tenantId != null) {
            dataSourceNamespaceVsDataSource.remove(tenantId);
        } else {
            tenantId = DUMMY_TENANT;
        }
        return getDataSourceForTenant(tenantId);
    }

    private DataSource getDataSource() throws NoInstanceException {
        String tenantId = tenantContext.getCurrentTenant();
        String dataSourceNamespace = tenantId;
        if (dataSourceNamespace == null || dataSourceNamespace.isEmpty()) {
            dataSourceNamespace = tenantId;
        }
        if (tenantId == null || tenantId.isEmpty()) {
            tenantAwareDataSourcelogger.info(
                    "No tenant Id specified, take dummy tenant as replacement. TenantId: {}, collectionNamespace: {}", tenantId,
                    dataSourceNamespace);
            return getDataSourceForTenant(DUMMY_TENANT);
        }
        return getDataSourceForTenant(tenantId);
    }

    private DataSource getDataSourceForTenant(final String tenantId) throws NoInstanceException {
        DataSource dataSource = dataSourceNamespaceVsDataSource.get(tenantId);
        if (dataSource == null) {
            DataSource tenantDataSource = hanaInstanceManagerConnector.getDataSource(tenantId);
            if (tenantDataSource == null) {
                throw new NoInstanceException(String.format(
                        "No Managed Instance available for tenant: '%s' and data source namespace: '%s' ", tenantId));
            }
            dataSourceNamespaceVsDataSource.putIfAbsent(tenantId, tenantDataSource);
            return tenantDataSource;
        }
        return dataSource;
    }

    public ConcurrentHashMap<String, DataSource> getDataSourceInfos() {
        return dataSourceNamespaceVsDataSource;
    }

    public void removeTenantFromDSCache(final String dataSourceNamespace) {
        if (dataSourceNamespaceVsDataSource.containsKey(dataSourceNamespace)) {
            dataSourceNamespaceVsDataSource.remove(dataSourceNamespace);
        }
    }

}
