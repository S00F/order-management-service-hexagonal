package com.orderms.infrastructure.config;


import com.orderms.domain.service.OrderDomainService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BeanConfig {

    @Bean
    public OrderDomainService orderDomainService() {
        return new OrderDomainService();  // Manually instantiate the domain service
    }
}
