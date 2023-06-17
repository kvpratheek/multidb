package com.sap.multidb.jdbc;

import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import org.eclipse.persistence.config.PersistenceUnitProperties;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.EclipseLinkJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.sap.multidb.jdbc.tenant.HanaInstanceManagerConnector;
import com.sap.multidb.jdbc.tenant.TenantAwareDataSource;
import com.sap.multidb.multitenancy.TenantContext;
import com.sap.xs.env.VcapServices;

@Configuration
@EnableJpaRepositories(basePackages = "com.sap.multidb.tenant", entityManagerFactoryRef = "tenantEntityManagerFactory", transactionManagerRef = "tenantTransactionManager")
@EnableTransactionManagement
public class TenantHanaConfiguration {

    @Bean
    @Primary
    TenantAwareDataSource getTenantAwareDataSource(final HanaInstanceManagerConnector hanaInstanceManagerConnector,
            final TenantContext tenantContext) {
        return new TenantAwareDataSource(hanaInstanceManagerConnector, tenantContext);
    }

    @Primary
    @Bean(name = "tenantEntityManagerFactory")
    @Qualifier("tenantSchema")
    LocalContainerEntityManagerFactoryBean getEntityManagerFactoryBean(final DataSource dataSource, final JpaVendorAdapter adapter) {
        LocalContainerEntityManagerFactoryBean emf = new LocalContainerEntityManagerFactoryBean();
        emf.setPackagesToScan("com.sap.multidb.tenant");
        emf.setPersistenceUnitName("pvs");
        emf.setJpaPropertyMap(getJpaProperties());
        emf.setJpaVendorAdapter(adapter);
        emf.setDataSource(dataSource);
        return emf;
    }

    // please do not change the bean name, spring looks for transactionManager bean name by default
    @Primary
    @Bean(name = "tenantTransactionManager")
    @Qualifier("tenantSchema")
    PlatformTransactionManager transactionManager(@Qualifier("tenantSchema") final LocalContainerEntityManagerFactoryBean bean)
    {
        JpaTransactionManager txManager = new JpaTransactionManager();
        txManager.setEntityManagerFactory(bean.getObject());
        return txManager;
    }

    @Bean
    JpaVendorAdapter eclipselink()
    {
        return new EclipseLinkJpaVendorAdapter();
    }


    public VcapServices getVcapServices()
    {
        return VcapServices.fromEnvironment();
    }

    private Map<String, String> getJpaProperties()
    {
        Map<String, String> map = new HashMap<>();
        map.put("eclipselink.ddl−generation", "none");
        map.put(PersistenceUnitProperties.TARGET_DATABASE, "HANA");
        map.put("eclipselink.cache.shared.default", "false");
        return map;
    }
}
