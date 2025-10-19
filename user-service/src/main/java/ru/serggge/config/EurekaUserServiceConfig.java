package ru.serggge.config;

import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration(proxyBeanMethods = false)
@EnableDiscoveryClient
@Profile("!test")
public class EurekaUserServiceConfig {
}