package com.ds.ws.health.spring.config;

import javax.sql.DataSource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabase;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.Database;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(basePackages = "com.ds.ws.health.dao")
public class DBConfig {

    @Bean
    public DataSource dataSource() {
	EmbeddedDatabaseBuilder builder = new EmbeddedDatabaseBuilder();
	EmbeddedDatabase db = builder.setType(EmbeddedDatabaseType.HSQL).addScript("classpath:db/create-tables.sql")
		.addScript("classpath:db/insert-data.sql").build();
	return db;
    }

    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory() {
	LocalContainerEntityManagerFactoryBean entityManager = new LocalContainerEntityManagerFactoryBean();
	entityManager.setDataSource(dataSource());
	entityManager.setJpaVendorAdapter(hibernateJpaVendorAdapter());
	entityManager.setPackagesToScan("com.ds.ws.health.domain");
	return entityManager;
    }

    @Bean
    public HibernateJpaVendorAdapter hibernateJpaVendorAdapter() {
	HibernateJpaVendorAdapter hibernateJpaVendorAdapter = new HibernateJpaVendorAdapter();
	hibernateJpaVendorAdapter.setShowSql(true);
	hibernateJpaVendorAdapter.setDatabase(Database.HSQL);
	return hibernateJpaVendorAdapter;
    }

    @Bean
    public JpaTransactionManager transactionManager() {
	return new JpaTransactionManager(entityManagerFactory().getObject());
    }

}
