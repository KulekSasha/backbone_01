package com.nix.config;

import com.nix.dao.RoleDao;
import com.nix.dao.UserDao;
import com.nix.dao.hiber.HRoleDao;
import com.nix.dao.hiber.HUserDao;
import com.nix.service.RoleService;
import com.nix.service.UserService;
import com.nix.service.impl.RoleServiceImpl;
import com.nix.service.impl.UserServiceImpl;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import org.springframework.orm.hibernate5.HibernateTransactionManager;
import org.springframework.orm.hibernate5.LocalSessionFactoryBuilder;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import javax.sql.DataSource;
import java.util.Properties;

@Configuration
@PropertySource(value = {"classpath:app-test.properties"})
@EnableTransactionManagement
@ComponentScan(basePackages = {
        "com.nix.api.rest",
})
public class UserResourceTestConfig {

    @Autowired
    private Environment env;

    @Bean
    public UserDao userDao() {
        return new HUserDao(sessionFactory());
    }

    @Bean
    public RoleDao roleDao() {
        return new HRoleDao(sessionFactory());
    }

    @Bean
    public DataSource dataSource() {
        return new EmbeddedDatabaseBuilder()
                .setType(EmbeddedDatabaseType.H2)
                .addScript("rest-sql/schema.sql")
                .addScript("rest-sql/data.sql")

                .ignoreFailedDrops(true)
                .build();
    }

    @Bean
    public SessionFactory sessionFactory() {
        return new LocalSessionFactoryBuilder(dataSource())
                .scanPackages("com.nix.model")
                .addProperties(hibernateProperties())
                .buildSessionFactory();
    }

    @Bean
    public HibernateTransactionManager txManager() {
        HibernateTransactionManager txManager = new HibernateTransactionManager();
        txManager.setSessionFactory(sessionFactory());
        return txManager;
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
                setProperty("hibernate.fetch_size", env.getProperty("hibernate.fetch_size"));
                setProperty("hibernate.batch_size", env.getProperty("hibernate.batch_size"));
                setProperty("javax.persistence.validation.mode", "none");

            }
        };
    }

    @Bean
    public UserService userService() {
        UserService service = new UserServiceImpl(userDao());
        return service;
    }

    @Bean
    public RoleService roleService() {
        RoleService service = new RoleServiceImpl(roleDao());
        return service;
    }

    @Bean
    public MessageSource messageSource() {
        ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
        messageSource.setBasename("messages");
        messageSource.setCacheSeconds(60);
        return messageSource;
    }

    @Bean
    public LocalValidatorFactoryBean validator() {
        LocalValidatorFactoryBean validatorFactoryBean = new LocalValidatorFactoryBean();
        validatorFactoryBean.setValidationMessageSource(messageSource());
        return validatorFactoryBean;
    }
}
