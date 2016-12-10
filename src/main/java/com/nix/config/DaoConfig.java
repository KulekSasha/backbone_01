package com.nix.config;

import com.zaxxer.hikari.HikariDataSource;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.dao.annotation.PersistenceExceptionTranslationPostProcessor;
import org.springframework.orm.hibernate5.HibernateTransactionManager;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.util.Properties;

@Configuration
@PropertySource(value = {"classpath:app.properties"})
@EnableTransactionManagement
public class DaoConfig {

    @Autowired
    private Environment env;

    @Bean
    public DataSource dataSource() {
        HikariDataSource ds = new HikariDataSource();
        ds.setMaximumPoolSize(Integer.parseInt(env.getProperty("db.maxPoolSize")));
        ds.setDataSourceClassName(env.getProperty("db.dataSourceClassName"));
        ds.addDataSourceProperty("url", env.getProperty("db.url"));
        ds.addDataSourceProperty("user", env.getProperty("db.user"));
        ds.addDataSourceProperty("password", env.getProperty("db.password"));
        return ds;
    }

    @Bean
    public LocalSessionFactoryBean sessionFactory(){
        LocalSessionFactoryBean bean = new LocalSessionFactoryBean();
        bean.setDataSource(dataSource());
        bean.setPackagesToScan("com.nix.model");
        bean.setHibernateProperties(hibernateProperties());
        return bean;
    }

    @Bean
    @Autowired
    public HibernateTransactionManager txManager(SessionFactory sf) {
        HibernateTransactionManager txManager = new HibernateTransactionManager();
        txManager.setSessionFactory(sf);
        return txManager;
    }

    @Bean
    public PersistenceExceptionTranslationPostProcessor hibernateExceptionTranslator(){
        return new PersistenceExceptionTranslationPostProcessor();
    }

    private Properties hibernateProperties() {
        return new Properties() {
            {
                setProperty("hibernate.cache.use_query_cache",
                        env.getProperty("hibernate.cache.use_query_cache"));
                setProperty("hibernate.cache.use_second_level_cache",
                        env.getProperty("hibernate.cache.use_second_level_cache"));
                setProperty("hibernate.dialect", env.getProperty("hibernate.dialect"));
                setProperty("hibernate.max_fetch_depth",
                        env.getProperty("hibernate.max_fetch_depth"));
                setProperty("show_sql", env.getProperty("hibernate.show_sql"));
                setProperty("format_sql", env.getProperty("hibernate.format_sql"));
            }
        };
    }

}
