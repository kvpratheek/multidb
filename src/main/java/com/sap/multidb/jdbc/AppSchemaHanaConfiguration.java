package com.sap.multidb.jdbc;

//@Configuration
//@EnableJpaRepositories(basePackages = "com.sap.multidb.app", entityManagerFactoryRef = "applicationSchemaEntityManagerFactory", transactionManagerRef = "applicationSchemaTransactionManager")
//@EnableTransactionManagement
public class AppSchemaHanaConfiguration {

//    @Bean(name = "applicationSchemaEntityManagerFactory")
//    LocalContainerEntityManagerFactoryBean getApplicationSchemaEntityManagerFactoryBean(
//            @Qualifier("applicationSchemaDatasource") final DataSource schemaDataSource, final JpaVendorAdapter adapter) {
//        LocalContainerEntityManagerFactoryBean emf = new LocalContainerEntityManagerFactoryBean();
//        emf.setPackagesToScan("com.sap.multidb.app");
//        emf.setPersistenceUnitName("pvs-application");
//        emf.setJpaPropertyMap(getJpaProperties());
//        emf.setJpaVendorAdapter(adapter);
//        emf.setDataSource(schemaDataSource);
//        return emf;
//    }
//
//    @Bean(name = "applicationSchemaTransactionManager")
//    PlatformTransactionManager applicationSchemaTransactionManager(
//            @Qualifier("applicationSchemaEntityManagerFactory") final LocalContainerEntityManagerFactoryBean applicationSchemaBean) {
//        JpaTransactionManager txManager = new JpaTransactionManager();
//        txManager.setEntityManagerFactory(applicationSchemaBean.getObject());
//        return txManager;
//    }
//
//    @Bean("applicationSchemaDatasource")
//    DataSource createDataSource() {
//        String appSchemaHDI = System.getenv("HANA_WITH_APP_SCHEMA");
//        String driver = (String) new VcapServiceReader().getAttribute(appSchemaHDI, "driver");
//        String user = (String) new VcapServiceReader().getAttribute(appSchemaHDI, "user");
//        String password = (String) new VcapServiceReader().getAttribute(appSchemaHDI, "password");
//        String url = (String) new VcapServiceReader().getAttribute(appSchemaHDI, "url");
//        final DataSource newDataSource = DataSourceBuilder.create().driverClassName(driver).url(url).username(user).password(password)
//                .build();
//        return newDataSource;
//    }
//
//
//    public VcapServices getVcapServices()
//    {
//        return VcapServices.fromEnvironment();
//    }
//
//    private Map<String, String> getJpaProperties()
//    {
//        Map<String, String> map = new HashMap<>();
//        map.put("eclipselink.ddl−generation", "none");
//        map.put(PersistenceUnitProperties.TARGET_DATABASE, "HANA");
//        map.put("eclipselink.cache.shared.default", "false");
//        return map;
//    }
}
