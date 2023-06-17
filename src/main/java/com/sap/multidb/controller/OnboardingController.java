package com.sap.multidb.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.sap.multidb.app.model.TenantDetails;
import com.sap.multidb.jdbc.tenant.TenantAwareDataSource;
import com.sap.multidb.multitenancy.TenantContext;
import com.sap.multidb.service.PVSOnBoardingService;

@RestController
public class OnboardingController {

    @Autowired
    TenantAwareDataSource dataSource;

    @Autowired
    PVSOnBoardingService onboardingService;

    @Autowired
    TenantContext tenantContext;

//    @Autowired
//    TenantDetailsRepo tenantRepo;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @PostMapping("/onboard")
    public String onBoardTenant(@RequestParam(name = "tenantId") final String tenantId, @RequestBody final TenantDetails tenantDetails) {

        logger.info("OnBoarding Started with " + tenantId);
        tenantContext.setCurrentTenant(tenantId);
        System.out.println(tenantDetails.toString());
//        saveTenantDetails(tenantDetails);
        return onboardingService.createManagedInstance(tenantId, dataSource, tenantContext);

    }

//    @Transactional("applicationSchemaTransactionManager")
//    public TenantDetails saveTenantDetails(final TenantDetails tenantDetail) {
//
//        return tenantRepo.save(tenantDetail);
//
//    }

}
