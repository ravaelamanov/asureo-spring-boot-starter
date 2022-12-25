package ru.relamanov.practiceSEM7.asureospringbootstarter.config;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import ru.relamanov.practiceSEM7.asureospringbootstarter.client.SmsIntegrationServiceClient;

@Configuration
@EnableConfigurationProperties(SmsIntegrationClientConfigurationProperties.class)
@RequiredArgsConstructor
public class SmsIntegrationClientAutoconfigurationConfiguration {
  private final SmsIntegrationClientConfigurationProperties properties;

  @Bean
  @ConditionalOnMissingBean
  public Jaxb2Marshaller marshaller() {
    Jaxb2Marshaller marshaller = new Jaxb2Marshaller();
    marshaller.setContextPath("ru.relamanov.practiceSEM7.asureospringbootstarter.jaxb2");
    return marshaller;
  }

  @Bean
  @ConditionalOnMissingBean
  public SmsIntegrationServiceClient smsIntegrationServiceClient(Jaxb2Marshaller marshaller) {
    SmsIntegrationServiceClient client = new SmsIntegrationServiceClient();
    client.setCredentials(properties.getCredentials().getUsername(),
            properties.getCredentials().getPassword());
    client.setCategoriesIdents(properties.getRepairs().getCategoriesIdents());
    client.setAttributeArray(properties.getRepairs().getResponseAttributes());
    client.setDefaultUri(properties.getUrl());
    client.setMarshaller(marshaller);
    client.setUnmarshaller(marshaller);
    return client;
  }
}
