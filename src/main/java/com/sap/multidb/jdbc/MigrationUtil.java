package com.sap.multidb.jdbc;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sap.multidb.jdbc.exceptions.PVSManagedHanaInstanceException;
import com.sap.xs.env.VcapServices;
import com.sap.xsa.core.instancemanager.client.ImClientException;
import com.sap.xsa.core.instancemanager.client.InstanceManagerClient;
import com.sap.xsa.core.instancemanager.client.ServiceInstance;
import com.sap.xsa.core.instancemanager.client.ServiceManagerClient;
import com.sap.xsa.core.instancemanager.client.impl.InstanceManagerClientFactory;

public class MigrationUtil {

    private static final String SM_INSTANCE_NAME = "service-manager";

    private InstanceManagerClient instanceManagerClient;

    private final Logger logger = LoggerFactory.getLogger(MigrationUtil.class);


    public MigrationUtil() {
        instanceManagerClient = getServiceManagerClient();
    }

    private InstanceManagerClient getServiceManagerClient() {
        if (instanceManagerClient == null) {
            instanceManagerClient = initializeIMClient(SM_INSTANCE_NAME);
            try {
                ((ServiceManagerClient) instanceManagerClient).setDefaultManagedServicePlan("hana", "schema");
            } catch (ImClientException e) {
                throw new PVSManagedHanaInstanceException("Error initializing ServiceManagerClient", e);
            }
        }
        return instanceManagerClient;
    }

    private InstanceManagerClient initializeIMClient(final String serviceInstanceName) {
        logger.info("Initializing Instance Manager Client for '{}'", serviceInstanceName);
        try {
            List<ServiceInstance> services = InstanceManagerClientFactory.getServicesFromVCAP(System.getenv(VcapServices.VCAP_SERVICES));
            System.out.println("-----------------------------------------------------------------------------");
            System.out.println("-----------------------------------------------------------------------------");
            if (services == null || services.size() > 0) {
                System.out.println("No Services found");
            }
            services.forEach(ser -> {
                System.out.println(ser.getInstanceName());
            });
            System.out.println("-----------------------------------------------------------------------------");
            System.out.println("-----------------------------------------------------------------------------");
            Optional<ServiceInstance> instance = services.stream().filter(si -> serviceInstanceName.equals(si.getInstanceName())).findAny();
            if (instance.isPresent()) {
                return InstanceManagerClientFactory.getInstance(instance.get());
            } else {
                logger.debug("Service Instance : {} not bound to the application", serviceInstanceName);
                return null; // New space or deployments HANA IM will not be bound
            }
        } catch (ImClientException | ClassNotFoundException e) {
            throw new PVSManagedHanaInstanceException("Error initializing InstanceManagerClient", e);
        }
    }

    public InstanceManagerClient getSM() {
        return instanceManagerClient;
    }

}
