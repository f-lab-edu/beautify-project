//package com.beautify_project.bp_app_api;
//
//import com.beautify_project.bp_app_api.config.properties.KafkaProducerConfigProperties;
//import org.jasypt.encryption.StringEncryptor;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Qualifier;
//import org.springframework.boot.test.context.SpringBootTest;
//
//@SpringBootTest
//public class JasyptTest {
//
//    @Autowired
//    @Qualifier("encryptorBean")
//    private StringEncryptor stringEncryptor;
//
//    @Autowired
//    private KafkaProducerConfigProperties kafkaProducerConfigProperties;
//
//    @Test
//    void encryptSpringSecurityConfig() {
//        final String encryptedKakaoClientId = stringEncryptor.encrypt(
//            "3fc1928e41c31bfa3e85a8e7585f6d0a");
//        final String encryptedKakaoClientSecret = stringEncryptor.encrypt(
//            "iGXGQsjIXeF3o98TxxbFIBs4s1i1rEs1");
//        final String encryptedNaverClientId = stringEncryptor.encrypt("VxGtKuhMM9MFNJ5kNUz0");
//        final String encryptedNaverClientSecret = stringEncryptor.encrypt("hDEfkQcm3l");
//
//        System.out.println("encryptedKakaoClientId = " + encryptedKakaoClientId);
//        System.out.println("encryptedKakaoClientSecret = " + encryptedKakaoClientSecret);
//        System.out.println("encryptedNaverClientId = " + encryptedNaverClientId);
//        System.out.println("encryptedNaverClientSecret = " + encryptedNaverClientSecret);
//    }
//
//    @Test
//    void encryptMailConfig() {
//        final String encryptedMailPassword = stringEncryptor.encrypt("jddn nsit iiow pxir");
//        System.out.println("encryptedMailPassword = " + encryptedMailPassword);
//    }
//
//    @Test
//    void encryptJwtConfig() {
//        final String encryptedJwtSecretKey = stringEncryptor.encrypt("jwtSecretKeyForBeautifyProject");
//        System.out.println("encryptedJwtSecretKey = " + encryptedJwtSecretKey);
//    }
//
//    @Test
//    void encryptKafkaProducerConfig() {
//        final String encryptedBrokerUrl = stringEncryptor.encrypt(
//            kafkaProducerConfigProperties.getBroker());
//
//        System.out.println("encryptedBrokerUrl = " + encryptedBrokerUrl);
//    }
//
//    @Test
//    void encryptDatasourceConfig() {
//        final String decryptedDatasourceUrl = stringEncryptor.decrypt(
//            "WYQa+sbvAKaZjBGjG3eJYe0zs57sLmydUoHWy3+5K2uIzOX7L0s5Z+eqNqOUB10OkbVNmB7mANhO1LaL4Q1599kNNmzAiII0j93edJCVWErUtIc7hgEV+2L3CexhYg/FRcUBL1vMC4ejtLmgZ+P3NePq/v9EWTE7");
//
//        System.out.println("decryptedDatasourceUrl = " + decryptedDatasourceUrl);
//
//        final String plainDatasourceUrl = "jdbc:mysql://127.0.0.1:3306/beautify_project?serverTimezone=Asia/Seoul&useUniCode=true&characterEncoding=UTF-8&rewriteBatchedStatements=true";
//        final String encryptedDatasourceUrl = stringEncryptor.encrypt(plainDatasourceUrl);
//        System.out.println("encryptedDatasourceUrl = " + encryptedDatasourceUrl);
//
//    }
//
//}
