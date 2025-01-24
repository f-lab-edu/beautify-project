package com.bp.app.event.consumer.config;

import lombok.RequiredArgsConstructor;
import org.jasypt.encryption.StringEncryptor;
import org.jasypt.encryption.pbe.PooledPBEStringEncryptor;
import org.jasypt.encryption.pbe.config.SimpleStringPBEConfig;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
@RequiredArgsConstructor
public class JasyptConfigBean {

    @Primary
    @Bean(name = "encryptorBean")
    public StringEncryptor stringEncryptor(SimpleStringPBEConfig config) {
        PooledPBEStringEncryptor stringEncryptor = new PooledPBEStringEncryptor();
        stringEncryptor.setConfig(config);
        return stringEncryptor;
    }

    @Bean
    @ConfigurationProperties("config.encrypt")
    public SimpleStringPBEConfig simpleStringPBEConfig() {
        return new SimpleStringPBEConfig();
    }
}
