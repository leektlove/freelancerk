package com.freelancerk;

import com.zaxxer.hikari.HikariDataSource;
import lombok.extern.slf4j.Slf4j;
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

@Slf4j
@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(entityManagerFactoryRef = "renewEntityManager", transactionManagerRef = "renewTransactionManager", basePackages = {
		"com.freelancerk.domain.repository" })
public class RenewDataSourceConfig {

	protected Map<String, Object> jpaProperties() {
		Map<String, Object> props = new HashMap<>();
		props.put("hibernate.physical_naming_strategy", SpringPhysicalNamingStrategy.class.getName());
		props.put("hibernate.implicit_naming_strategy", SpringImplicitNamingStrategy.class.getName());
		props.put("hibernate.id.new_generator_mappings", "none");
		props.put("hibernate.hbm2ddl.auto", "update");

		return props;
	}

	private final PersistenceUnitManager persistenceUnitManager;

	public RenewDataSourceConfig(ObjectProvider<PersistenceUnitManager> persistenceUnitManager) {
		this.persistenceUnitManager = persistenceUnitManager.getIfAvailable();
	}

	@Bean
	@ConfigurationProperties("spring.jpa")
	public JpaProperties orderJpaProperties() {
		return new JpaProperties();
	}

	@Bean
	@ConfigurationProperties("renew.datasource")
	public DataSourceProperties orderDataSourceProperties() {
		return new DataSourceProperties();
	}

	@Bean
	@ConfigurationProperties(prefix = "spring.jpa.properties")
	public HikariDataSource orderDataSource() {
		return orderDataSourceProperties().initializeDataSourceBuilder().type(HikariDataSource.class).build();
	}

	@Bean(name = "renewEntityManager")
	public LocalContainerEntityManagerFactoryBean orderEntityManager(JpaProperties orderJpaProperties) {
		EntityManagerFactoryBuilder builder = createEntityManagerFactoryBuilder(orderJpaProperties);
		return builder.dataSource(orderDataSource())
				.packages("com.freelancerk.domain", "com.freelancerk.Api",
						"org.springframework.data.jpa.convert.threeten")
				.persistenceUnit("renew").properties(jpaProperties()).build();
	}

	@Bean(name = "renewTransactionManager")
	public JpaTransactionManager renewTransactionManager(
			@Qualifier("renewEntityManager") EntityManagerFactory orderEntityManager) {
		return new JpaTransactionManager(orderEntityManager);
	}

	private EntityManagerFactoryBuilder createEntityManagerFactoryBuilder(JpaProperties orderJpaProperties) {
		JpaVendorAdapter jpaVendorAdapter = createJpaVendorAdapter(orderJpaProperties);
		return new EntityManagerFactoryBuilder(jpaVendorAdapter, orderJpaProperties.getProperties(),
				this.persistenceUnitManager);
	}

	private JpaVendorAdapter createJpaVendorAdapter(JpaProperties jpaProperties) {
		AbstractJpaVendorAdapter adapter = new HibernateJpaVendorAdapter();
		adapter.setShowSql(jpaProperties.isShowSql());
		adapter.setDatabase(Database.MYSQL);
		adapter.setDatabasePlatform(jpaProperties.getDatabasePlatform());
		adapter.setGenerateDdl(jpaProperties.isGenerateDdl());
		return adapter;
	}
}
