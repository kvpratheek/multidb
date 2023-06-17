package com.sap.multidb.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AppSchemaOnboardController {

//    @Autowired
//    ApplicationSchemaMigrationService applicationSchemaMigrationService;

    @PostMapping("/appschema")
    public void createAppSchema() {
//        applicationSchemaMigrationService.migrate();
    }

}
