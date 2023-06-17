package com.sap.multidb.jdbc.tenant;

import static java.lang.String.format;

import java.util.Map;
import java.util.Optional;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sap.multidb.jdbc.MigrationUtil;
import com.sap.multidb.jdbc.exceptions.PVSManagedHanaInstanceException;
import com.sap.xsa.core.instancemanager.client.ImClientException;
import com.sap.xsa.core.instancemanager.client.InstanceCreationOptions;
import com.sap.xsa.core.instancemanager.client.InstanceManagerClient;
import com.sap.xsa.core.instancemanager.client.ManagedServiceInstance;

@Service
public class HanaInstanceManagerConnector {

    private final Logger logger = LoggerFactory.getLogger(HanaInstanceManagerConnector.class);

    private InstanceManagerClient instanceManagerClient;
    private static final String USER_KEY = "user";
    private static final String PASS_KEY = "password";
    private static final String DRIVER_KEY = "driver";
    private static final String URL_KEY = "url";

    private static final String ERROR_HANA_CREATION_FAILED = "Failed to create managed HANA instance for tenant ID '%s' and data source namespace '%s'.";
    private static final String ERROR_HANA_LOOKUP_FAILED = "Failed to look up managed HANA instance for tenant ID '%s' and data source namespace '%s'.";

    @Autowired
    private PoolingDataSourceCreator poolingDataSourceCreator;

    @Autowired
    private ObjectMapper objMapper;

    public ManagedServiceInstance createManagedInstance(final String tenantId) throws PVSManagedHanaInstanceException {

        ManagedServiceInstance newServiceInstance = null;
        Map<String, Object> provisioningParameters = new java.util.HashMap<>();
        provisioningParameters.put("database_id", "fcc60cae-2db1-4cf6-97ef-e876372326c0");
        provisioningParameters.put("enableTenant", true);

        Map<String, Object> dataEncryption = new java.util.HashMap<>();
        dataEncryption.put("mode", "DEDICATED_KEY");
        provisioningParameters.put("dataEncryption", dataEncryption);

        Map<String, Object> subscriptionContext = new java.util.HashMap<>();
        subscriptionContext.put("globalAccountID", "020a5b61-ba6c-437b-bfb6-66e0504b06d4");
        subscriptionContext.put("subAccountID", "03e10064-6472-4eff-8b79-b57e90f96756");
        subscriptionContext.put("applicationName", "multidbtest");
        subscriptionContext.put("keyConsumerName", "multedbtest1");
        provisioningParameters.put("subscriptionContext", subscriptionContext);



        InstanceCreationOptions creationOptions = new InstanceCreationOptions().withProvisioningParameters(provisioningParameters);

        // Step 4:Trigger Service Manager binding creation with the Tenant ID.
        try {
            newServiceInstance = getInstanceManagerClient().createManagedInstance(tenantId, creationOptions);
        } catch (ImClientException e) {
            throw new PVSManagedHanaInstanceException(String.format(ERROR_HANA_CREATION_FAILED, tenantId), e);
        }

        return newServiceInstance;
    }

    public boolean doesInstanceExist(final String tenantId) {
        try {
            return getInstanceManagerClient().getManagedInstance(tenantId) != null;
        } catch (ImClientException e) {
            logger.error(format("Error getting managed instance for tenant '%s'", tenantId), e);
            return false;
        }

    }

    public DataSource getDataSource(final String tenantId) {
        ManagedServiceInstance managedInstance = getManagedInstance(tenantId);
        return createDataSource(managedInstance);
    }

    private ManagedServiceInstance getManagedInstance(final String tenantId) {
        try {
            return getOptionalManagedInstance(tenantId).orElseThrow(() -> new ImClientException(tenantId));
        } catch (ImClientException e) {
            throw new PVSManagedHanaInstanceException(
                    format("Error getting managed instance for tenant '%s'", tenantId),
                    e);
        }
    }

    private Optional<ManagedServiceInstance> getOptionalManagedInstance(final String tenantId) {
        logger.info("Getting managed instance for tenant '{}' and data source namespace '{}'", tenantId);
        try {
            return Optional.ofNullable(getInstanceManagerClient().getManagedInstance(tenantId, true)); // true = refresh cache
        } catch (ImClientException e) {
            throw new PVSManagedHanaInstanceException(format(ERROR_HANA_LOOKUP_FAILED, tenantId), e);
        }
    }

    private DataSource createDataSource(final ManagedServiceInstance managedInstance) {
        Map<String, Object> credentials = managedInstance.getCredentials();
        String user = (String) credentials.get(USER_KEY);
        String password = (String) credentials.get(PASS_KEY);
        String driver = (String) credentials.get(DRIVER_KEY);
        String url = (String) credentials.get(URL_KEY);

        DataSource dataSource = poolingDataSourceCreator.createTransactionAwareDataSource(user, password, driver, url);
        return dataSource;
    }

    private InstanceManagerClient getInstanceManagerClient() throws ImClientException {
        if (instanceManagerClient == null) {
            instanceManagerClient = new MigrationUtil().getSM();
        }
        return instanceManagerClient;
    }

}
