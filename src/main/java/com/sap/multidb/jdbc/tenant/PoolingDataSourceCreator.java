package com.sap.multidb.jdbc.tenant;
import org.apache.tomcat.jdbc.pool.DataSource;
import org.apache.tomcat.jdbc.pool.PoolProperties;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.jdbc.datasource.TransactionAwareDataSourceProxy;
import org.springframework.stereotype.Component;


@Component
@PropertySource("classpath:pvs-db-hana.properties")
public class PoolingDataSourceCreator {

    @Value("${pvs.db.hana.connection.pool.initialSize}")
    private int initialSize;

    @Value("${pvs.db.hana.connection.pool.minIdleConnections}")
    private int minIdleConnections;

    @Value("${pvs.db.hana.connection.pool.maxIdleConnections}")
    private int maxIdleConnections;

    @Value("${pvs.db.hana.connection.pool.minEvictableIdleTimeMillis}")
    private int minEvictableIdleTimeMillis;

    @Value("${pvs.db.hana.connection.pool.timeBetweenEvictionRunsMillis}")
    private int timeBetweenEvictionRunsMillis;

    @Value("${pvs.db.hana.connection.pool.maxWaitForBorrowMillis}")
    private int maxWaitForBorrowMillis;

    @Value("${pvs.db.hana.connection.pool.logStackTracesOfAbandonedConnections}")
    private boolean logStackTracesOfAbandonedConnections;

    @Value("${pvs.db.hana.connection.pool.testOnReturn}")
    private boolean testOnReturn;

    @Value("${pvs.db.hana.connection.pool.testOnBorrow}")
    private boolean testOnBorrow;

    @Value("${pvs.db.hana.connection.pool.validationQuery}")
    private String validationQuery;

    @Value("${pvs.db.hana.connection.pool.maxActive}")
    private int maxActiveConnectionsPerDataSource;

    public TransactionAwareDataSourceProxy createTransactionAwareDataSource(String userName, String password, String driverClassName,
            String url) {
        PoolProperties poolProperties = createPoolProperties(userName, password, driverClassName, url);
        DataSource rawDataSource = new DataSource(poolProperties);
        return new TransactionAwareDataSourceProxy(rawDataSource);
    }

    private PoolProperties createPoolProperties(String userName, String password, String driverClassName, String url) {
        PoolProperties properties = createCommonProperties();
        properties.setUsername(userName);
        properties.setPassword(password);
        properties.setDriverClassName(driverClassName);
        properties.setUrl(url);

        return properties;
    }

    private PoolProperties createCommonProperties() {
        PoolProperties properties = new PoolProperties();
        properties.setInitialSize(initialSize);
        properties.setMaxActive(getMaxActiveConnections());
        properties.setMinIdle(getMinIdleConnections());
        properties.setMaxIdle(getMaxIdleConnections());
        properties.setMinEvictableIdleTimeMillis(minEvictableIdleTimeMillis);
        properties.setTimeBetweenEvictionRunsMillis(timeBetweenEvictionRunsMillis);
        properties.setMaxWait(maxWaitForBorrowMillis);
        properties.setLogAbandoned(logStackTracesOfAbandonedConnections);
        properties.setTestOnReturn(testOnReturn);
        properties.setTestOnBorrow(testOnBorrow);
        properties.setValidationQuery(validationQuery);

        return properties;
    }

    private int getMaxActiveConnections() {
        return maxActiveConnectionsPerDataSource;
    }

    /**
     * number of connections which should be kept in the pool at all times, because it is likely that they are used within a short interval
     * again, saving reconnect times
     * 
     * @return
     */
    private int getMinIdleConnections() {
        return minIdleConnections;
    }

    /**
     * number of connections we can afford to leave in the pool even if not used for a little longer, saving reconnect times, when needed
     * again
     * 
     * @return
     */
    private int getMaxIdleConnections() {
        return maxIdleConnections;
    }
}