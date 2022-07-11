package com.freelancerk;

import org.apache.tomcat.jdbc.pool.DataSource;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.autoconfigure.orm.jpa.JpaProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.boot.orm.jpa.hibernate.SpringImplicitNamingStrategy;
import org.springframework.boot.orm.jpa.hibernate.SpringPhysicalNamingStrategy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.persistenceunit.PersistenceUnitManager;
import org.springframework.orm.jpa.vendor.AbstractJpaVendorAdapter;
import org.springframework.orm.jpa.vendor.Database;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.persistence.EntityManagerFactory;
import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(
        entityManagerFactoryRef = "legacyEntityManager",
        transactionManagerRef = "legacyTransactionManager",
        basePackages = {"com.freelancerk.legacy.repository"})
public class LegacyDataSourceConfig {

    private final PersistenceUnitManager persistenceUnitManager;

    public LegacyDataSourceConfig(ObjectProvider<PersistenceUnitManager> persistenceUnitManager) {
        this.persistenceUnitManager = persistenceUnitManager.getIfAvailable();
    }

    @Bean(name = "legacyJpaProperties")
    @ConfigurationProperties("spring.jpa")
    public JpaProperties customerJpaProperties() {
        return new JpaProperties();
    }

    @Bean(name = "legacyDataSourceProperties")
    @ConfigurationProperties("legacy.datasource")
    public DataSourceProperties customerDataSourceProperties() {
        return new DataSourceProperties();
    }

    @Bean(name= "legacyDataSource")
    @ConfigurationProperties(prefix = "spring.jpa.properties")
    public DataSource customerDataSource() {
        return customerDataSourceProperties().initializeDataSourceBuilder()
                .type(DataSource.class).build();
    }

    @Bean(name = "legacyEntityManager")
    public LocalContainerEntityManagerFactoryBean customerEntityManager(
            @Qualifier("legacyJpaProperties") JpaProperties customerJpaProperties) {
        EntityManagerFactoryBuilder builder = createEntityManagerFactoryBuilder(
                customerJpaProperties);
        return builder
                .dataSource(customerDataSource())
                .packages("com.freelancerk.legacy.domain", "org.springframework.data.jpa.convert.threeten")
                .persistenceUnit("legacy")
                .properties(jpaProperties())
                .build();
    }

    @Bean(name= "legacyTransactionManager")
    public JpaTransactionManager customerTransactionManager(
            @Qualifier("legacyEntityManager") EntityManagerFactory customerEntityManager) {
        return new JpaTransactionManager(customerEntityManager);
    }

    private EntityManagerFactoryBuilder createEntityManagerFactoryBuilder(
            JpaProperties customerJpaProperties) {
        JpaVendorAdapter jpaVendorAdapter = createJpaVendorAdapter(customerJpaProperties);
        return new EntityManagerFactoryBuilder(jpaVendorAdapter,
                customerJpaProperties.getProperties(), this.persistenceUnitManager);
    }

    private JpaVendorAdapter createJpaVendorAdapter(JpaProperties jpaProperties) {
        AbstractJpaVendorAdapter adapter = new HibernateJpaVendorAdapter();
        adapter.setShowSql(jpaProperties.isShowSql());
        adapter.setDatabase(Database.MYSQL);
        adapter.setDatabasePlatform(jpaProperties.getDatabasePlatform());
        adapter.setGenerateDdl(jpaProperties.isGenerateDdl());
        return adapter;
    }

    protected Map<String, Object> jpaProperties() {
        Map<String, Object> props = new HashMap<>();
        props.put("hibernate.physical_naming_strategy", SpringPhysicalNamingStrategy.class.getName());
        props.put("hibernate.implicit_naming_strategy", SpringImplicitNamingStrategy.class.getName());
        props.put("hibernate.id.new_generator_mappings","false");
        props.put("hibernate.dialect","org.hibernate.dialect.MySQL5InnoDBDialect");
        return props;
    }
}
