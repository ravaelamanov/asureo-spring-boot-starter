package ru.relamanov.practiceSEM7.asureospringbootstarter.client;

/**
 * Ошикба аутентификации в интеграционном сервисе.
 */
public class SmsIntegrationClientLoginException extends RuntimeException {
  public SmsIntegrationClientLoginException(String msg) {
    super(msg);
  }
}
