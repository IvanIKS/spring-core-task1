package com.example.springcrm;

import com.example.springcrm.dao.HibernateUtil;
import org.hibernate.SessionFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackages = "com.example.springcrm")
public class AppConfig {

    @Bean
    public SessionFactory sessionFactory() {
        return HibernateUtil.getSessionFactory();
    }
}