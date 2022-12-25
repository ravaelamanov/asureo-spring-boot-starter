package ru.relamanov.practiceSEM7.asureospringbootstarter.config;

import lombok.Value;
import org.hibernate.validator.constraints.URL;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import java.util.List;

/**
 * Конфигрурационные параметры клиента интеграционного сервиса АСУРЭО.
 */
@Value
@ConstructorBinding
@ConfigurationProperties(prefix = "sms-integration-client")
public class SmsIntegrationClientConfigurationProperties {
  /**
   * URL интеграционного сервиса.
   */
  @URL(protocol = "http", message = "Не валидный URL")
  String url;

  @Valid
  RepairsFetchConfiguration repairs;

  @Valid
  ClientCredentials credentials;

  /**
   * Конфигурация загрузки ремонтов.
   */
  @Value
  @ConstructorBinding
  public static class RepairsFetchConfiguration {
    /**
     * Идентификаторы категорий, ремонты по которым будут загружены.
     * Если не указано ни одной категории, будут загружены ремонты по всем категориям.
     */
    List<@NotBlank(message = "Индентификатор категории не должен быть пустым")
            String> categoriesIdents;
    /**
     * Набор атрибутов, которые должны быть включены интеграционным сервисом
     * в ответе на запрос загрузки ремонтов.
     * Если не указан ни один атрибут, ответ будет содержать все возможные атрибуты.
     */
    List<@NotBlank(message = "Название атрибута не должно быть пустым")
            String> responseAttributes;
  }

  /**
   * Учетные данные для аутентификации в сервисе интеграции.
   */
  @Value
  @ConstructorBinding
  public static class ClientCredentials {
    /**
     * Логин.
     */
    @NotBlank(message = "Имя пользователя не должно быть пустым")
    String username;
    /**
     * Пароль.
     */
    @NotBlank(message = "Пароль не должен быть пустым")
    String password;
  }
}
